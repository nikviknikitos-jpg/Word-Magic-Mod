package com.example.wordmagic.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class AncientColossusEntity extends HostileEntity {
    private ServerBossBar bossBar;
    private int tick = 0;

    public AncientColossusEntity(EntityType<? extends HostileEntity> type, World world) {
        super(type, world);
        this.setPersistent();
        this.experiencePoints = 500;
        if (!world.isClient) {
            bossBar = new ServerBossBar(Text.literal("Древний Колосс Печати"), ServerBossBar.Color.PURPLE, ServerBossBar.Style.PROGRESS);
        }
    }

    public static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 350.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 22.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_ARMOR, 12.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new SwimGoal(this));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0, true));
        this.goalSelector.add(5, new LookAtEntityGoal(this, PlayerEntity.class, 32));
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) return;
        tick++;
        if (bossBar != null) {
            bossBar.setPercent(getHealth() / getMaxHealth());
            for (ServerPlayerEntity p : ((ServerWorld) getWorld()).getPlayers()) {
                if (p.squaredDistanceTo(this) < 4096) bossBar.addPlayer(p);
                else bossBar.removePlayer(p);
            }
        }
        if (tick % 100 == 0) {
            ServerWorld sw = (ServerWorld) getWorld();
            Box box = new Box(getX() - 8, getY() - 4, getZ() - 8, getX() + 8, getY() + 4, getZ() + 8);
            for (LivingEntity le : sw.getEntitiesByClass(LivingEntity.class, box, e -> e.isAlive() && !((Object) e instanceof AncientColossusEntity))) {
                le.damage(sw.getDamageSources().magic(), 6.0f);
                var dir = le.getPos().subtract(getPos()).normalize().multiply(1.2);
                le.setVelocity(dir.x, 1.0, dir.z);
                le.velocityModified = true;
            }
            for (int i = 0; i < 20; i++) {
                double a = Math.PI * 2 * i / 20.0;
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, getX() + Math.cos(a) * 3, getY() + 0.5, getZ() + Math.sin(a) * 3, 1, 0, 0.05, 0, 0.01);
            }
        }
        if (tick % 60 == 0) {
            ServerWorld sw = (ServerWorld) getWorld();
            if (getTarget() instanceof PlayerEntity p) {
                var l = EntityType.LIGHTNING_BOLT.create(sw);
                if (l != null) { l.refreshPositionAfterTeleport(p.getX(), p.getY(), p.getZ()); sw.spawnEntity(l); }
            }
        }
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!getWorld().isClient && getWorld() instanceof ServerWorld sw) {
            net.minecraft.entity.ItemEntity t10 = new net.minecraft.entity.ItemEntity(sw, getX(), getY() + 1, getZ(),
                    new net.minecraft.item.ItemStack(com.example.wordmagic.item.ModItems.TOMES.get(9)));
            sw.spawnEntity(t10);
            for (int i = 0; i < 2; i++) {
                sw.spawnEntity(new net.minecraft.entity.ItemEntity(sw, getX() + i, getY() + 1, getZ(),
                        new net.minecraft.item.ItemStack(com.example.wordmagic.item.ModItems.VOID_DUST)));
                sw.spawnEntity(new net.minecraft.entity.ItemEntity(sw, getX(), getY() + 1, getZ() + i,
                        new net.minecraft.item.ItemStack(com.example.wordmagic.item.ModItems.STORM_CORE)));
            }
            if (bossBar != null) bossBar.clearPlayers();
        }
    }
}
