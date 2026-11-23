package com.example.wordmagic.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> TOMES = new ArrayList<>();
    public static Item WORD_STAFF, RITUAL_DAGGER, FORGOTTEN_SCROLL, FIRE_RUNE, AETHER_CRYSTAL, VOID_DUST, STORM_CORE;
    public static ItemGroup TAB;

    public static void register() {
        for (int i = 1; i <= 10; i++) {
            TOMES.add(reg("tome_" + i, new Item(new Item.Settings().maxCount(1))));
        }
        WORD_STAFF = reg("word_staff", new WordStaffItem(new Item.Settings().maxCount(1)));
        RITUAL_DAGGER = reg("ritual_dagger", new RitualDaggerItem(new Item.Settings().maxCount(1)));
        FORGOTTEN_SCROLL = reg("forgotten_scroll", new Item(new Item.Settings()));
        FIRE_RUNE = reg("fire_rune", new Item(new Item.Settings()));
        AETHER_CRYSTAL = reg("aether_crystal", new Item(new Item.Settings()));
        VOID_DUST = reg("void_dust", new Item(new Item.Settings()));
        STORM_CORE = reg("storm_core", new Item(new Item.Settings()));

        TAB = Registry.register(Registries.ITEM_GROUP, new Identifier("wordmagic", "wordmagic_tab"),
                FabricItemGroup.builder().displayName(Text.translatable("itemGroup.wordmagic.wordmagic_tab"))
                        .icon(() -> new ItemStack(WORD_STAFF))
                        .entries((ctx, entries) -> {
                            entries.add(WORD_STAFF);
                            entries.add(RITUAL_DAGGER);
                            entries.add(FORGOTTEN_SCROLL);
                            entries.add(FIRE_RUNE);
                            entries.add(AETHER_CRYSTAL);
                            entries.add(VOID_DUST);
                            entries.add(STORM_CORE);
                            for (Item t : TOMES) entries.add(t);
                            entries.add(com.example.wordmagic.block.ModBlocks.SEAL_STONE.asItem());
                        }).build());
    }
    private static Item reg(String name, Item item) {
        return Registry.register(Registries.ITEM, new Identifier("wordmagic", name), item);
    }
}
