package com.example.wordmagic.item;

import com.example.wordmagic.block.ModBlocks;
import com.example.wordmagic.entity.AncientColossusEntity;
import com.example.wordmagic.entity.ModEntities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RitualDaggerItem extends Item {
    public RitualDaggerItem(Settings s) { super(s); }
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity sp && world instanceof ServerWorld sw) {
            BlockHitResult hit = (BlockHitResult) sp.raycast(6.0, 0.0f, false);
            BlockPos bp = hit.getBlockPos();
            if (sw.getBlockState(bp).isOf(ModBlocks.SEAL_STONE)) {
                // summon boss
                AncientColossusEntity boss = ModEntities.ANCIENT_COLOSSUS.create(sw);
                if (boss != null) {
                    boss.refreshPositionAndAngles(bp.getX() + 0.5, bp.getY() + 1, bp.getZ() + 0.5, 0, 0);
                    sw.spawnEntity(boss);
                    sw.playSound(null, bp, net.minecraft.sound.SoundEvents.ENTITY_WITHER_SPAWN,
                            net.minecraft.sound.SoundCategory.PLAYERS, 2.0f, 0.7f);
                    for (int i = 0; i < 120; i++) {
                        double a = Math.PI * 2 * i / 120.0;
                        sw.spawnParticles(net.minecraft.particle.ParticleTypes.SOUL_FIRE_FLAME,
                                bp.getX() + Math.cos(a) * 3, bp.getY() + 0.2, bp.getZ() + Math.sin(a) * 3,
                                1, 0, 0.1, 0, 0.01);
                    }
                }
                sp.getStackInHand(hand).decrement(1);
            }
        }
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
