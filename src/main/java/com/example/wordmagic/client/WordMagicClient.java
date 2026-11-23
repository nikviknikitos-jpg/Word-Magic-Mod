package com.example.wordmagic.client;

import com.example.wordmagic.network.SpellSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.IronGolemEntityModel;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

public class WordMagicClient implements ClientModInitializer {
    public static final Set<Integer> KNOWN = new HashSet<>();
    public static int MANA = 100;
    public static int ACTIVE = -1;
    private static KeyBinding wheelKey;

    @Override
    public void onInitializeClient() {
        wheelKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.wordmagic.wheel", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.wordmagic"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (wheelKey.wasPressed()) {
                if (client.player != null) client.setScreen(new SpellWheelScreen());
            }
        });
        HudRenderCallback.EVENT.register(new ManaHudRenderer());
        EntityRendererRegistry.register(com.example.wordmagic.entity.ModEntities.RUNE_GUARDIAN,
                ctx -> new MobEntityRenderer<com.example.wordmagic.entity.RuneGuardianEntity, IronGolemEntityModel<com.example.wordmagic.entity.RuneGuardianEntity>>(
                        ctx, new IronGolemEntityModel<>(ctx.getPart(EntityModelLayers.IRON_GOLEM)), 1.0f) {
                    @Override public Identifier getTexture(com.example.wordmagic.entity.RuneGuardianEntity e) {
                        return new Identifier("minecraft", "textures/entity/iron_golem/iron_golem.png");
                    }
                });
        EntityRendererRegistry.register(com.example.wordmagic.entity.ModEntities.SHADOW_CULTIST,
                ctx -> new MobEntityRenderer<com.example.wordmagic.entity.ShadowCultistEntity, BlazeEntityModel<com.example.wordmagic.entity.ShadowCultistEntity>>(
                        ctx, new BlazeEntityModel<>(ctx.getPart(EntityModelLayers.BLAZE)), 0.9f) {
                    @Override public Identifier getTexture(com.example.wordmagic.entity.ShadowCultistEntity e) {
                        return new Identifier("minecraft", "textures/entity/blaze.png");
                    }
                });
        EntityRendererRegistry.register(com.example.wordmagic.entity.ModEntities.ANCIENT_COLOSSUS,
                ctx -> new MobEntityRenderer<com.example.wordmagic.entity.AncientColossusEntity, IronGolemEntityModel<com.example.wordmagic.entity.AncientColossusEntity>>(
                        ctx, new IronGolemEntityModel<>(ctx.getPart(EntityModelLayers.IRON_GOLEM)), 3.0f) {
                    @Override public Identifier getTexture(com.example.wordmagic.entity.AncientColossusEntity e) {
                        return new Identifier("minecraft", "textures/entity/iron_golem/iron_golem.png");
                    }
                });
        EntityRendererRegistry.register(com.example.wordmagic.entity.ModEntities.WORD_PROJECTILE,
                ctx -> new WordProjectileRenderer(ctx));

        ClientPlayNetworking.registerGlobalReceiver(SpellSync.CHANNEL, (client, handler, buf, sender) -> {
            int n = buf.readInt();
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < n; i++) set.add(buf.readInt());
            int mana = buf.readInt();
            int active = buf.readInt();
            client.execute(() -> {
                KNOWN.clear(); KNOWN.addAll(set);
                MANA = mana; ACTIVE = active;
            });
        });
    }
}

