package com.example.wordmagic.client;

import com.example.wordmagic.network.SpellSync;
import com.example.wordmagic.spell.SpellRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class SpellWheelScreen extends Screen {
    private int tome = 1;
    protected SpellWheelScreen() { super(Text.literal("Колесо Слов")); }

    @Override
    protected void init() {
        super.init();
        int cx = this.width / 2;
        int cy = this.height / 2;
        this.addDrawableChild(ButtonWidget.builder(Text.literal("<"), b -> { if (tome > 1) { tome--; refresh(); } })
                .dimensions(cx - 70, cy - 10, 24, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.literal(">"), b -> { if (tome < 10) { tome++; refresh(); } })
                .dimensions(cx + 46, cy - 10, 24, 20).build());

        List<SpellRegistry.Spell> spells = SpellRegistry.byTome(tome);
        for (int i = 0; i < spells.size(); i++) {
            final SpellRegistry.Spell s = spells.get(i);
            double a = Math.PI * 2 * i / 5.0 - Math.PI / 2;
            int bx = cx + (int)(Math.cos(a) * 85) - 40;
            int by = cy + (int)(Math.sin(a) * 85) - 10;
            boolean known = WordMagicClient.KNOWN.contains(s.id);
            String label = (known ? "★ " : "[Закрыто] ") + s.name;
            this.addDrawableChild(ButtonWidget.builder(Text.literal(label), b -> {
                if (known) {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeInt(s.id);
                    ClientPlayNetworking.send(new Identifier("wordmagic", "set_active"), buf);
                    WordMagicClient.ACTIVE = s.id;
                    this.close();
                }
            }).dimensions(bx, by, 80, 20).build());
        }
    }

    private void refresh() { this.clearChildren(); this.init(); }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        int cx = this.width / 2;
        int cy = this.height / 2;
        for (int i = 0; i < 80; i++) {
            double a = Math.PI * 2 * i / 80.0;
            int r = 85;
            int px = cx + (int)(Math.cos(a) * r);
            int py = cy + (int)(Math.sin(a) * r);
            context.fill(px - 1, py - 1, px + 1, py + 1, 0x88FFFFFF);
        }
        context.drawCenteredTextWithShadow(this.textRenderer, "Том " + tome + "/10", cx, cy, 0xFFFFFF);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override public boolean shouldPause() { return false; }
}
