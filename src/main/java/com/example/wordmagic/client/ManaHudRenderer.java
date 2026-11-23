package com.example.wordmagic.client;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ManaHudRenderer implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext ctx, float tickDelta) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        int cx = ctx.getScaledWindowWidth() / 2;
        int y = ctx.getScaledWindowHeight() - 60;
        int w = 100, h = 6;
        ctx.fill(cx - w/2, y, cx + w/2, y + h, 0xAA000000);
        int fill = (int) (((float) WordMagicClient.MANA / 100f) * w);
        ctx.fill(cx - w/2, y, cx - w/2 + fill, y + h, 0xFFFFC030);
        ctx.drawText(mc.textRenderer, "Mana " + WordMagicClient.MANA + "/100", cx - 40, y - 12, 0xFFFFFF, true);
    }
}
