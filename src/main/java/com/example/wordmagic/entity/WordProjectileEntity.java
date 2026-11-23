package com.example.wordmagic.entity;

import com.example.wordmagic.spell.WordEffects;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class WordProjectileEntity extends ThrownItemEntity {
    public String spellId = "ignis";

    public WordProjectileEntity(EntityType<? extends WordProjectileEntity> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }
    public void init(String spellId) { this.spellId = spellId; }

    @Override protected Item getDefaultItem() { return Items.BLAZE_POWDER; }

    @Override
    public void tick() {
        super.tick();
        if (getWorld().isClient) {
            getWorld().addParticle(ParticleTypes.FLAME, getX(), getY(), getZ(), 0, 0, 0);
            getWorld().addParticle(ParticleTypes.END_ROD, getX(), getY(), getZ(), 0, 0, 0);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!getWorld().isClient) {
            WordEffects.onProjectileHit(this, spellId);
            this.discard();
        }
    }

    @Override public void writeCustomDataToNbt(NbtCompound nbt) { super.writeCustomDataToNbt(nbt); nbt.putString("Spell", spellId); }
    @Override public void readCustomDataFromNbt(NbtCompound nbt) { super.readCustomDataFromNbt(nbt); spellId = nbt.getString("Spell"); }
}
