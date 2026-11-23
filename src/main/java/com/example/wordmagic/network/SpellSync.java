package com.example.wordmagic.network;

import com.example.wordmagic.spell.SpellState;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class SpellSync {
    public static final Identifier CHANNEL = new Identifier("wordmagic", "spell_sync");

    public static void registerCommon() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier("wordmagic", "set_active"),
                (server, player, handler, buf, sender) -> {
                    int id = buf.readInt();
                    server.execute(() -> SpellState.setActive(player.getUuid(), id));
                });
    }

    public static void send(ServerPlayerEntity p) {
        if (!ServerPlayNetworking.canSend(p, CHANNEL)) return;
        PacketByteBuf buf = PacketByteBufs.create();
        var known = SpellState.known(p.getUuid());
        buf.writeInt(known.size());
        for (int id : known) buf.writeInt(id);
        buf.writeInt(SpellState.mana(p.getUuid()));
        buf.writeInt(SpellState.active(p.getUuid()));
        ServerPlayNetworking.send(p, CHANNEL, buf);
    }
}
