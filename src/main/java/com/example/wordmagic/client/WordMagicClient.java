package com.example.wordmagic.client;

import com.example.wordmagic.entity.AncientColossusEntity;
import com.example.wordmagic.entity.ModEntities;
import com.example.wordmagic.entity.RuneGuardianEntity;
import com.example.wordmagic.entity.ShadowCultistEntity;
import com.example.wordmagic.network.SpellSync;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BlazeEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.util.InputUtil;
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

        EntityRendererRegistry.register(ModEntities.RUNE_GUARDIAN,
                ctx -> new BipedEntityRenderer<RuneGuardianEntity, BipedEntityModel<RuneGuardianEntity>>(
                        ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 1.0f) {
                    @Override
                    public Identifier getTexture(RuneGuardianEntity entity) {
                        return new Identifier("minecraft", "textures/entity/zombie/zombie.png");
                    }
                });

        EntityRendererRegistry.register(ModEntities.SHADOW_CULTIST,
                ctx -> new MobEntityRenderer<ShadowCultistEntity, BlazeEntityModel<ShadowCultistEntity>>(
                        ctx, new BlazeEntityModel<>(ctx.getPart(EntityModelLayers.BLAZE)), 0.9f) {
                    @Override
                    public Identifier getTexture(ShadowCultistEntity e) {
                        return new Identifier("minecraft", "textures/entity/blaze.png");
                    }
                });

        EntityRendererRegistry.register(ModEntities.ANCIENT_COLOSSUS,
                ctx -> new BipedEntityRenderer<AncientColossusEntity, BipedEntityModel<AncientColossusEntity>>(
                        ctx, new BipedEntityModel<>(ctx.getPart(EntityModelLayers.PLAYER)), 3.0f) {
                    @Override
                    public Identifier getTexture(AncientColossusEntity e) {
                        return new Identifier("minecraft", "textures/entity/zombie/husk.png");
                    }
                });

        EntityRendererRegistry.register(ModEntities.WORD_PROJECTILE,
                ctx -> new WordProjectileRenderer(ctx));

        ClientPlayNetworking.registerGlobalReceiver(SpellSync.CHANNEL, (client, handler, buf, sender) -> {
            int n = buf.readInt();
            Set<Integer> set = new HashSet<>();
            for (int i = 0; i < n; i++) set.add(buf.readInt());
            int mana = buf.readInt();
            int active = buf.readInt();
            client.execute(() -> {
                KNOWN.clear();
                KNOWN.addAll(set);
                MANA = mana;
                ACTIVE = active;
            });
        });
    }
}
