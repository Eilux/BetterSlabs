package dev.bodner.jack.betterslabs;

import com.swordglowsblue.artifice.api.Artifice;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import com.swordglowsblue.artifice.api.resource.StringResource;
import dev.bodner.jack.betterslabs.block.SlabBlockMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.ArrayList;
import java.util.List;

public class Betterslabs implements ModInitializer {

    public static final SlabBlockMod TESTSLAB = new SlabBlockMod(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F));


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("betterslabs","test_slab"),TESTSLAB);
        Registry.register(Registry.ITEM,new Identifier("betterslabs","test_slab"),new BlockItem(TESTSLAB, new Item.Settings()));
    }
}
