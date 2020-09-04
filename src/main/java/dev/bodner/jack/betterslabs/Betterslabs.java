package dev.bodner.jack.betterslabs;

import dev.bodner.jack.betterslabs.block.SlabBlockMod;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Betterslabs implements ModInitializer {
    public static final SlabBlockMod SMOOTH_STONE_SLAB = new SlabBlockMod(FabricBlockSettings.of(Material.STONE, MaterialColor.STONE).requiresTool().strength(2.0F, 6.0F));


    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("betterslabs","smooth_stone_slab"),SMOOTH_STONE_SLAB);
        Registry.register(Registry.ITEM,new Identifier("betterslabs","smooth_stone_slab"),new BlockItem(SMOOTH_STONE_SLAB, new Item.Settings().group(ItemGroup.BUILDING_BLOCKS)));
    }


}
