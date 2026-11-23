package com.example.wordmagic.client;

import com.example.wordmagic.entity.WordProjectileEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;

public class WordProjectileRenderer extends EntityRenderer<WordProjectileEntity> {
    private final ItemRenderer itemRenderer;
    public WordProjectileRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
        this.itemRenderer = ctx.getItemRenderer();
    }
    @Override public Identifier getTexture(WordProjectileEntity e) {
        return new Identifier("minecraft", "textures/item/blaze_powder.png");
    }
    @Override
    public void render(WordProjectileEntity e, float yaw, float tickDelta, MatrixStack m,
                       VertexConsumerProvider vcp, int light) {
        m.push();
        m.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(e.age * 20));
        ItemStack s = new ItemStack(Items.BLAZE_POWDER);
        itemRenderer.renderItem(s, ModelTransformationMode.GROUND, light,
                net.minecraft.client.render.OverlayTexture.DEFAULT_UV, m, vcp, e.getWorld(), e.getId());
        m.pop();
    }
}
