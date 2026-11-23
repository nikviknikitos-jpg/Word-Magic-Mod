package com.example.wordmagic.command;

import com.example.wordmagic.block.ModBlocks;
import com.example.wordmagic.item.ModItems;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class WordMagicCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("wordmagic_give").executes(ctx -> {
            ServerPlayerEntity p = ctx.getSource().getPlayer();
            if (p != null) {
                p.getInventory().insertStack(new ItemStack(ModItems.WORD_STAFF));
                p.getInventory().insertStack(new ItemStack(ModItems.RITUAL_DAGGER));
                p.getInventory().insertStack(new ItemStack(ModBlocks.SEAL_STONE, 4));
                p.getInventory().insertStack(new ItemStack(ModItems.FORGOTTEN_SCROLL, 8));
                p.getInventory().insertStack(new ItemStack(ModItems.FIRE_RUNE, 8));
                p.getInventory().insertStack(new ItemStack(ModItems.AETHER_CRYSTAL, 8));
                p.getInventory().insertStack(new ItemStack(ModItems.VOID_DUST, 8));
                p.getInventory().insertStack(new ItemStack(ModItems.STORM_CORE, 8));
                for (var t : ModItems.TOMES) p.getInventory().insertStack(new ItemStack(t));
                ctx.getSource().sendFeedback(() -> Text.literal("WordMagic kit given"), false);
                return 1;
            }
            return 0;
        }));
    }
}
