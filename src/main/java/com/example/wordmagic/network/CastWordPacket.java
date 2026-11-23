package com.example.wordmagic.network;

import com.example.wordmagic.spell.SpellRegistry;
import com.example.wordmagic.spell.SpellState;
import com.example.wordmagic.spell.WordEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class CastWordPacket {
    public static void cast(ServerPlayerEntity p) {
        int id = SpellState.active(p.getUuid());
        if (id < 0) { p.sendMessage(Text.literal("§cВыберите Слово (R)."), true); return; }
        SpellRegistry.Spell s = SpellRegistry.byId(id);
        if (s == null) return;
        if (!SpellState.known(p.getUuid()).contains(id)) { p.sendMessage(Text.literal("§cСлово не изучено."), true); return; }
        if (SpellState.mana(p.getUuid()) < s.manaCost) { p.sendMessage(Text.literal("§cНедостаточно маны."), true); return; }
        SpellState.setMana(p.getUuid(), SpellState.mana(p.getUuid()) - s.manaCost);
        WordEffects.cast(p, s);
        SpellSync.send(p);
    }
}
