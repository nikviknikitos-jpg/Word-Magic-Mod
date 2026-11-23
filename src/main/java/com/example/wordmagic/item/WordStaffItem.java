package com.example.wordmagic.item;

import com.example.wordmagic.network.CastWordPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class WordStaffItem extends Item {
    public WordStaffItem(Settings s) { super(s); }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity sp) {
            CastWordPacket.cast(sp);
            sp.getItemCooldownManager().set(this, 15);
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
