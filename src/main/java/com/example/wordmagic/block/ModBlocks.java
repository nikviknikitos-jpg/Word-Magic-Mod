package com.example.wordmagic.block;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static Block SEAL_STONE;
    public static void register() {
        SEAL_STONE = Registry.register(Registries.BLOCK,
                new Identifier("wordmagic", "seal_stone"),
                new Block(Block.Settings.create().strength(-1.0f, 3600000.0f).luminance(s -> 4)));
        Registry.register(Registries.ITEM, new Identifier("wordmagic", "seal_stone"),
                new BlockItem(SEAL_STONE, new Item.Settings()));
    }
}
