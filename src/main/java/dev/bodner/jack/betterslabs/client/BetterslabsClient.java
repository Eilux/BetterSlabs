package dev.bodner.jack.betterslabs.client;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.StringResource;
import dev.bodner.jack.betterslabs.enums.SlabPlaceMode;
import dev.bodner.jack.betterslabs.json.JSONMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BetterslabsClient implements ClientModInitializer {

    public static List<Identifier> slabList = new ArrayList<>();
    public static SlabPlaceMode mode = SlabPlaceMode.ALL;

    public static void createPack(){
        ArtificeResourcePack overwrite = Artifice.registerAssets("betterslabs:overwrite", pack -> {
            pack.setDisplayName("Betterslabs Overwriter");
            pack.setDescription("overwrites all slab blockstates and supplies models");
            pack.shouldOverwrite();

            for (int i = 0; i<=slabList.size()-1; i++){
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();

                Identifier blockstateLocation = new Identifier(slabList.get(i).getNamespace(),"blockstates/" + slabList.get(i).getPath() + ".json");

                Identifier sideLocation = new Identifier(slabList.get(i).getNamespace(), "models/block/" + slabList.get(i).getPath() + "_side.json");
                Identifier doublesideLocation = new Identifier(slabList.get(i).getNamespace(), "models/block/" + slabList.get(i).getPath() + "_double_horizontal.json");

                InputStream stream = BetterslabsClient.class.getClassLoader().getResourceAsStream("assets/" + slabList.get(i).getNamespace() + "/blockstates/" + slabList.get(i).getPath() + ".json");
                JsonObject blockstateObject = (JsonObject)jsonParser.parse(new InputStreamReader(stream));
                JSONMap jsonBlockstate = gson.fromJson(blockstateObject, JSONMap.class);

                String modelpathBottom = jsonBlockstate.getModel("type=bottom");
                String modelpathTop = jsonBlockstate.getModel("type=top");
                String modelpathDouble = jsonBlockstate.getModel("type=double");

                String[] modelarray = modelpathBottom.split(":");
                InputStream stream1 = BetterslabsClient.class.getClassLoader().getResourceAsStream("assets/" + modelarray[0] + "/models/" + modelarray[1] + ".json");
                JsonObject modelObject = (JsonObject)jsonParser.parse(new InputStreamReader(stream1));
                JSONMap jsonModel = gson.fromJson(modelObject, JSONMap.class);

                String bottomTexture = jsonModel.getTexture("bottom");
                String topTexture = jsonModel.getTexture("top");
                String sideTexture = jsonModel.getTexture("side");


                pack.add(sideLocation, new StringResource(
                        "{\n" +
                                "  \"parent\": \"betterslabs:block/side_slab\",\n" +
                                "  \"textures\": {\n" +
                                "    \"bottom\": \"" + bottomTexture + "\",\n" +
                                "    \"top\": \"" + topTexture + "\",\n" +
                                "    \"side\": \"" + sideTexture + "\"\n" +
                                "  }\n" +
                                "}"
                ));
                pack.add(doublesideLocation, new StringResource(
                        "{\n" +
                                "  \"parent\": \"minecraft:block/cube_column_horizontal\",\n" +
                                "  \"textures\": {\n" +
                                "    \"end\": \"" + topTexture + "\",\n" +
                                "    \"side\": \"" + sideTexture + "\"\n" +
                                "  }\n" +
                                "}"
                ));
                pack.add(blockstateLocation, new StringResource(
                        "{\n" +
                                "  \"variants\": {\n" +
                                "    \"new_type=bottom\": {\n" +
                                "      \"model\": \"" + modelpathBottom + "\"\n" +
                                "    },\n" +
                                "    \"new_type=north\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/" + slabList.get(i).getPath() + "_side\"\n" +
                                "    },\n" +
                                "    \"new_type=south\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/" + slabList.get(i).getPath() + "_side\",\n" +
                                "      \"y\": 180\n" +
                                "    },\n" +
                                "    \"new_type=east\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/"  + slabList.get(i).getPath() + "_side\",\n" +
                                "      \"y\": 90\n" +
                                "    },\n" +
                                "    \"new_type=west\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/" + slabList.get(i).getPath() + "_side\",\n" +
                                "      \"y\": 270\n" +
                                "    },\n" +
                                "    \"new_type=double\": {\n" +
                                "      \"model\": \"" + modelpathDouble + "\"\n" +
                                "    },\n" +
                                "    \"new_type=doublex\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/" + slabList.get(i).getPath() + "_double_horizontal\",\n" +
                                "      \"x\": 90,\n" +
                                "      \"y\": 90\n" +
                                "    },\n" +
                                "    \"new_type=doublez\": {\n" +
                                "      \"model\": \"" + slabList.get(i).getNamespace() + ":block/" + slabList.get(i).getPath() + "_double_horizontal\",\n" +
                                "      \"x\": 90\n" +
                                "    },\n" +
                                "    \"new_type=top\": {\n" +
                                "      \"model\": \"" + modelpathTop + "\"\n" +
                                "    }\n" +
                                "  }\n" +
                                "}"
                ));
            }
        });
    }

    @Override
    public void onInitializeClient() {
        KeyBinding placemodeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.betterslabs.placemode", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.categories.misc"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (placemodeKey.wasPressed()){
                mode = mode.next();
                assert client.player != null;
                switch (mode){
                    case VERTICAL:
                        client.player.sendMessage(Text.of("Slab placement mode set to: vertical only"),true);
                        break;
                    case HORIZONTAL:
                        client.player.sendMessage(Text.of("Slab placement mode set to: horizontal only"),true);
                        break;
                    default:
                        client.player.sendMessage(Text.of("Slab placement mode set to: all"),true);
                        break;
                }
            }
        });
    }
}