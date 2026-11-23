package com.example.wordmagic.tick;

import com.example.wordmagic.spell.SpellState;
import net.minecraft.server.world.ServerWorld;

public class WordMagicTickHandler {
    public static void tick(ServerWorld world) {
        for (var p : world.getPlayers()) {
            if (p.age % 20 == 0) {
                int m = SpellState.mana(p.getUuid());
                if (m < 100) SpellState.setMana(p.getUuid(), m + 2);
            }
        }
    }
}
