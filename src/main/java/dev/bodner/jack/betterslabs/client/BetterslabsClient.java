package dev.bodner.jack.betterslabs.client;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.StringResource;
import dev.bodner.jack.betterslabs.Betterslabs;
import dev.bodner.jack.betterslabs.enums.SlabPlaceMode;
import dev.bodner.jack.betterslabs.json.BlockstateMap;
import dev.bodner.jack.betterslabs.json.ModelMap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.CallbackI;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class BetterslabsClient implements ClientModInitializer {

    public static List<Identifier> slabList = new ArrayList<>();
    public static SlabPlaceMode mode = SlabPlaceMode.ALL;

    public static void createPack(){
        //Creates Artifice pack
        ArtificeResourcePack overwrite = Artifice.registerAssets("betterslabs:overwrite", pack -> {
            //Creates settings for Artifice
            pack.setDisplayName("Betterslabs Overwriter");
            pack.setDescription("overwrites all slab blockstates and supplies models");
            pack.shouldOverwrite();

            //Iterates through all slab blocks, previously gotten through SlabGetterMixin
            for (int i = 0; i<=slabList.size()-1; i++){
                Gson gson = new Gson();
                JsonParser jsonParser = new JsonParser();

                //Gets Identifier for the side slab parent
                Identifier sideSlabParent = new Identifier("betterslabs","block/side_slab");

                //Gets Identifier for the double slab parent
                Identifier sideSlabDoubleParent = new Identifier("minecraft","block/cube_column_horizontal");

                //Creates input stream from blockstate file, creates JsonObject from it, then maps it
                InputStream stream = BetterslabsClient.class.getClassLoader().getResourceAsStream("assets/" + slabList.get(i).getNamespace() + "/blockstates/" + slabList.get(i).getPath() + ".json");
                JsonObject blockstateObject = (JsonObject)jsonParser.parse(new InputStreamReader(stream));
                BlockstateMap mappedBlockstate = gson.fromJson(blockstateObject, BlockstateMap.class);

                List<String> topSlabList = new ArrayList<>(mappedBlockstate.getTop());
                List<String> bottomSlabList = new ArrayList<>(mappedBlockstate.getBottom());
                List<String> doubleSlabList = new ArrayList<>(mappedBlockstate.getDouble());
                List<String> sideSlabList = new ArrayList<>();
                List<String> sideSlabDoubleList = new ArrayList<>();

                //Loops through and creates side slab block models
                for(int j = 0; j<=mappedBlockstate.getTop().size()-1; j++){
                    //maps the model so texture can be gotten
                    String[] modelArray = mappedBlockstate.getTop().get(j).split(":");
                    InputStream stream1 = BetterslabsClient.class.getClassLoader().getResourceAsStream("assets/" + modelArray[0] + "/models/" + modelArray[1] + ".json");
                    JsonObject modelObject = (JsonObject)jsonParser.parse(new InputStreamReader(stream1));
                    ModelMap modelMap = gson.fromJson(modelObject,ModelMap.class);

                    //splits textures into arrays
                    String[] textureArrayTop = modelMap.getTexture("top").split(":");
                    String[] textureArrayBottom = modelMap.getTexture("bottom").split(":");
                    String[] textureArraySide = modelMap.getTexture("side").split(":");

                    //get model identifiers
                    Identifier sideLocation = new Identifier(slabList.get(i).getNamespace(), slabList.get(i).getPath() + "_side_" + j);
                    Identifier doubleSideLocation = new Identifier(slabList.get(i).getNamespace(), slabList.get(i).getPath() + "_double_horizontal_" + j);

                    sideSlabList.add(sideLocation.getNamespace()+":"+"block/"+sideLocation.getPath());
                    sideSlabDoubleList.add(doubleSideLocation.getNamespace()+":"+"block/"+doubleSideLocation.getPath());

                    //creates models
                    pack.addBlockModel(sideLocation, modelBuilder -> modelBuilder
                        .parent(sideSlabParent)
                        .texture("top", new Identifier(textureArrayTop[0],textureArrayTop[1]))
                        .texture("bottom", new Identifier(textureArrayBottom[0],textureArrayBottom[1]))
                        .texture("side", new Identifier(textureArraySide[0],textureArraySide[1]))
                    );
                    pack.addBlockModel(doubleSideLocation, modelBuilder -> modelBuilder
                        .parent(sideSlabDoubleParent)
                        .texture("end", new Identifier(textureArrayTop[0],textureArrayTop[1]))
                        .texture("side", new Identifier(textureArraySide[0],textureArraySide[1]))
                    );
                }

                //Creates the blockstate file
                Identifier blockstateLocation = new Identifier(slabList.get(i).getNamespace(),"blockstates/" + slabList.get(i).getPath() + ".json");
                pack.add(blockstateLocation, new StringResource(
                        "{\n" +
                                "  \"variants\": {\n" +
                                "    \"axis=x,type=bottom\": ["+createModel(sideSlabList,0,270,0)+"],\n" +
                                "    \"axis=x,type=top\": ["+createModel(sideSlabList,0,90,0)+"],\n" +
                                "    \"axis=x,type=double\": ["+createModel(sideSlabDoubleList,90,90,0)+"],\n" +
                                "    \"axis=y,type=bottom\": ["+createModel(bottomSlabList,0,0,0)+"],\n" +
                                "    \"axis=y,type=top\": ["+createModel(topSlabList,0,0,0)+"],\n" +
                                "    \"axis=y,type=double\": ["+createModel(doubleSlabList,0,0,0)+"],\n" +
                                "    \"axis=z,type=bottom\": ["+createModel(sideSlabList,0,0,0)+"],\n" +
                                "    \"axis=z,type=top\": ["+createModel(sideSlabList,0,180,0)+"],\n" +
                                "    \"axis=z,type=double\": ["+createModel(sideSlabDoubleList,90,0,0)+"]\n" +
                                "  }\n" +
                                "}"
                ));
            }
        });
    }

    private static String createModel(List<String> models, int xRotation, int yRotation, int zRotation){
        String result = "";
        for(int k = 0; k<=models.size()-1; k++){
            result = result+"{\"model\": \""+models.get(k)+"\",\"x\": "+xRotation+",\"y\": "+yRotation+",\"z\": "+zRotation+"}";
            if(k<models.size()-1){
                result = result+",";
            }
        }
        return result;
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