package com.example.wordmagic.spell;

import com.example.wordmagic.entity.ModEntities;
import com.example.wordmagic.entity.WordProjectileEntity;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class WordEffects {
    public static void onProjectileHit(WordProjectileEntity p, String spellId) {
        if (!(p.getWorld() instanceof ServerWorld sw)) return;
        Vec3d pos = p.getPos();
        switch (spellId) {
            case "ignis" -> {
                sw.createExplosion(p, pos.x, pos.y, pos.z, 1.5f, World.ExplosionSourceType.NONE);
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-3, pos.y-3, pos.z-3, pos.x+3, pos.y+3, pos.z+3),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 6.0f);
                    e.setOnFireFor(4);
                }
            }
            case "ice_spear" -> {
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-3, pos.y-3, pos.z-3, pos.x+3, pos.y+3, pos.z+3),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 5.0f);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
                }
            }
            case "stone_shot" -> {
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-3, pos.y-3, pos.z-3, pos.x+3, pos.y+3, pos.z+3),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 8.0f);
                    e.setVelocity(e.getVelocity().add(0, 0.8, 0));
                }
            }
            case "shadow_bolt" -> {
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-3, pos.y-3, pos.z-3, pos.x+3, pos.y+3, pos.z+3),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 6.0f);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 1));
                }
            }
            case "poison_thorn" -> {
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-3, pos.y-3, pos.z-3, pos.x+3, pos.y+3, pos.z+3),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 5.0f);
                    e.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 100, 1));
                }
            }
            default -> {}
        }
    }

    public static void cast(ServerPlayerEntity p, SpellRegistry.Spell s) {
        ServerWorld sw = (ServerWorld) p.getWorld();
        Vec3d eye = p.getEyePos();
        Vec3d dir = p.getRotationVec(1.0f).normalize();

        // rotating runic circle under feet
        for (int i = 0; i < 24; i++) {
            double a = Math.PI * 2 * i / 24.0;
            sw.spawnParticles(ParticleTypes.END_ROD, p.getX() + Math.cos(a) * 1.5, p.getY() + 0.1, p.getZ() + Math.sin(a) * 1.5, 1, 0, 0.02, 0, 0.01);
        }
        sw.playSound(null, p.getBlockPos(), net.minecraft.sound.SoundEvents.ENTITY_EVOKER_CAST_SPELL,
                net.minecraft.sound.SoundCategory.PLAYERS, 1.2f, 1.0f);

        int id = s.id;
        // TOME I - FIRE
        if (id == 1) { // Ignis
            WordProjectileEntity proj = ModEntities.WORD_PROJECTILE.create(sw);
            if (proj != null) {
                proj.init("ignis");
                proj.setOwner(p);
                proj.refreshPositionAndAngles(eye.x, eye.y, eye.z, 0, 0);
                proj.setVelocity(dir.multiply(1.6));
                sw.spawnEntity(proj);
            }
        } else if (id == 2) { // Pyro ring
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-4, p.getY()-2, p.getZ()-4, p.getX()+4, p.getY()+2, p.getZ()+4),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 8.0f);
                e.setOnFireFor(3);
                var d = e.getPos().subtract(p.getPos()).normalize().multiply(1.2);
                e.setVelocity(d.x, 0.5, d.z);
            }
            for (int i = 0; i < 60; i++) {
                double a = Math.PI * 2 * i / 60.0;
                sw.spawnParticles(ParticleTypes.FLAME, p.getX() + Math.cos(a) * 4, p.getY() + 0.5, p.getZ() + Math.sin(a) * 4, 1, 0, 0.02, 0, 0.01);
            }
        } else if (id == 3) { // Flame dash
            p.setVelocity(dir.multiply(2.0).x, 0.3, dir.multiply(2.0).z);
            p.velocityModified = true;
            for (int i = 1; i <= 8; i++) {
                Vec3d pos = p.getPos().add(dir.multiply(i));
                sw.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 5, 0.3, 0.3, 0.3, 0.05);
            }
        } else if (id == 4) { // Incinerating ray
            for (int i = 1; i <= 15; i++) {
                Vec3d pos = eye.add(dir.multiply(i));
                sw.spawnParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 3, 0.1, 0.1, 0.1, 0.02);
                sw.spawnParticles(ParticleTypes.LAVA, pos.x, pos.y, pos.z, 1, 0, 0, 0, 0);
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-1, pos.y-1, pos.z-1, pos.x+1, pos.y+1, pos.z+1),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 3.0f);
                    e.setOnFireFor(2);
                }
            }
        } else if (id == 5) { // Meteor
            Vec3d target = eye.add(dir.multiply(10));
            sw.createExplosion(p, target.x, target.y, target.z, 4.0f, World.ExplosionSourceType.NONE);
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(target.x-6, target.y-6, target.z-6, target.x+6, target.y+6, target.z+6),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 20.0f);
                e.setOnFireFor(6);
            }
            for (int i = 0; i < 80; i++) {
                sw.spawnParticles(ParticleTypes.FLAME,
                        target.x + (sw.random.nextDouble()-0.5) * 6,
                        target.y + sw.random.nextDouble() * 3,
                        target.z + (sw.random.nextDouble()-0.5) * 6,
                        1, 0, 0.02, 0, 0.02);
            }
        }
        // TOME II - WIND
        else if (id == 6) { // Gust
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-5, p.getY()-3, p.getZ()-5, p.getX()+5, p.getY()+3, p.getZ()+5),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                var d = e.getPos().subtract(p.getPos()).normalize().multiply(1.5);
                e.setVelocity(d.x, 0.6, d.z);
            }
            for (int i = 0; i < 40; i++) {
                sw.spawnParticles(ParticleTypes.CLOUD, p.getX() + (sw.random.nextDouble()-0.5) * 6, p.getY() + sw.random.nextDouble()*2, p.getZ() + (sw.random.nextDouble()-0.5) * 6, 1, 0, 0.05, 0, 0.02);
            }
        } else if (id == 7) { // Ether wings
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 100, 1));
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0));
        } else if (id == 8) { // Air shield
            for (int i = 0; i < 30; i++) {
                double a = Math.PI * 2 * i / 30.0;
                sw.spawnParticles(ParticleTypes.CLOUD, p.getX() + Math.cos(a) * 2, p.getY() + 1, p.getZ() + Math.sin(a) * 2, 1, 0, 0.02, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-3, p.getY()-2, p.getZ()-3, p.getX()+3, p.getY()+2, p.getZ()+3),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                var d = e.getPos().subtract(p.getPos()).normalize().multiply(1.5);
                e.setVelocity(d.x, 0.5, d.z);
            }
        } else if (id == 9) { // Vortex
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-4, p.getY()-2, p.getZ()-4, p.getX()+4, p.getY()+4, p.getZ()+4),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.setVelocity(0, 1.2, 0);
            }
            for (int i = 0; i < 80; i++) {
                double a = (i * 0.3) % (Math.PI * 2);
                double r = 0.5 + (i / 80.0) * 4;
                sw.spawnParticles(ParticleTypes.CLOUD, p.getX() + Math.cos(a) * r, p.getY() + i * 0.05, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.01);
            }
        } else if (id == 10) { // Storm tornado
            for (int i = 0; i < 200; i++) {
                double a = (i * 0.2) % (Math.PI * 2);
                double r = 0.5 + (i / 200.0) * 8;
                sw.spawnParticles(ParticleTypes.CLOUD, p.getX() + Math.cos(a) * r, p.getY() + i * 0.03, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.01);
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, p.getX() + Math.cos(a) * r, p.getY() + i * 0.03, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-8, p.getY()-4, p.getZ()-8, p.getX()+8, p.getY()+8, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.setVelocity(0, 1.5, 0);
                e.damage(sw.getDamageSources().magic(), 12.0f);
            }
        }
        // TOME III - ICE
        else if (id == 11) { // Ice spear
            WordProjectileEntity proj = ModEntities.WORD_PROJECTILE.create(sw);
            if (proj != null) { proj.init("ice_spear"); proj.setOwner(p);
                proj.refreshPositionAndAngles(eye.x, eye.y, eye.z, 0, 0);
                proj.setVelocity(dir.multiply(1.8)); sw.spawnEntity(proj); }
        } else if (id == 12) { // Winter step
            BlockPos bp = BlockPos.ofFloored(p.getX(), p.getY(), p.getZ());
            for (int dx = -2; dx <= 2; dx++) for (int dz = -2; dz <= 2; dz++) {
                BlockPos t = bp.add(dx, -1, dz);
                if (sw.getBlockState(t).isOf(Blocks.WATER)) sw.setBlockState(t, Blocks.ICE.getDefaultState());
            }
        } else if (id == 13) { // Snowstorm
            for (int i = 0; i < 100; i++) {
                double a = (i * 0.4) % (Math.PI * 2);
                double r = 0.5 + (i / 100.0) * 4;
                sw.spawnParticles(ParticleTypes.SNOWFLAKE, p.getX() + Math.cos(a) * r, p.getY() + i * 0.03, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-4, p.getY()-2, p.getZ()-4, p.getX()+4, p.getY()+3, p.getZ()+4),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 8.0f);
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 100, 2));
            }
        } else if (id == 14) { // Ice bastion
            BlockPos base = BlockPos.ofFloored(p.getX() + dir.x * 3, p.getY(), p.getZ() + dir.z * 3);
            for (int dx = -2; dx <= 2; dx++) for (int dy = 0; dy < 3; dy++) {
                BlockPos t = base.add(dx, dy, 0);
                sw.setBlockState(t, Blocks.PACKED_ICE.getDefaultState());
            }
        } else if (id == 15) { // Absolute zero
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-8, p.getY()-4, p.getZ()-8, p.getX()+8, p.getY()+4, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 120, 4));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 120, 4));
                e.damage(sw.getDamageSources().magic(), 6.0f);
            }
            for (int i = 0; i < 120; i++) {
                double a = Math.PI * 2 * i / 120.0;
                sw.spawnParticles(ParticleTypes.SNOWFLAKE, p.getX() + Math.cos(a) * 8, p.getY() + 0.5, p.getZ() + Math.sin(a) * 8, 1, 0, 0.02, 0, 0.01);
            }
        }
        // TOME IV - EARTH
        else if (id == 16) { // Stone shot
            WordProjectileEntity proj = ModEntities.WORD_PROJECTILE.create(sw);
            if (proj != null) { proj.init("stone_shot"); proj.setOwner(p);
                proj.refreshPositionAndAngles(eye.x, eye.y, eye.z, 0, 0);
                proj.setVelocity(dir.multiply(1.4)); sw.spawnEntity(proj); }
        } else if (id == 17) { // Dome
            BlockPos center = p.getBlockPos();
            for (int dx = -3; dx <= 3; dx++) for (int dy = 0; dy <= 4; dy++) for (int dz = -3; dz <= 3; dz++) {
                double d = Math.sqrt(dx*dx + dy*dy + dz*dz);
                if (d >= 3 && d <= 3.6) {
                    BlockPos t = center.add(dx, dy, dz);
                    if (sw.getBlockState(t).isAir()) sw.setBlockState(t, Blocks.COBBLESTONE.getDefaultState());
                }
            }
        } else if (id == 18) { // Earthquake
            for (int i = 0; i < 60; i++) {
                double a = Math.PI * 2 * i / 60.0;
                sw.spawnParticles(ParticleTypes.SPORE_BLOSSOM_AIR, p.getX() + Math.cos(a) * 6, p.getY() + 0.2, p.getZ() + Math.sin(a) * 6, 1, 0, 0.05, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-6, p.getY()-2, p.getZ()-6, p.getX()+6, p.getY()+3, p.getZ()+6),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.setVelocity(e.getVelocity().x, 1.2, e.getVelocity().z);
                e.damage(sw.getDamageSources().magic(), 10.0f);
            }
        } else if (id == 19) { // Call of depths
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, 200, 1));
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 200, 0));
        } else if (id == 20) { // Tectonic wrath
            Vec3d target = eye.add(dir.multiply(8));
            sw.createExplosion(p, target.x, target.y, target.z, 5.0f, World.ExplosionSourceType.NONE);
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(target.x-7, target.y-7, target.z-7, target.x+7, target.y+7, target.z+7),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 20.0f);
                e.setOnFireFor(3);
            }
            for (int i = 0; i < 100; i++) {
                sw.spawnParticles(ParticleTypes.LAVA, target.x + (sw.random.nextDouble()-0.5) * 6, target.y, target.z + (sw.random.nextDouble()-0.5) * 6, 1, 0, 0.1, 0, 0.02);
            }
        }
        // TOME V - LIGHT
        else if (id == 21) { // Healing
            p.heal(6.0f);
            for (int i = 0; i < 40; i++) {
                double a = Math.PI * 2 * i / 40.0;
                sw.spawnParticles(ParticleTypes.END_ROD, p.getX() + Math.cos(a) * 1.5, p.getY() + 0.2, p.getZ() + Math.sin(a) * 1.5, 1, 0, 0.02, 0, 0.01);
            }
        } else if (id == 22) { // True sight
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 400, 0));
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-32, p.getY()-16, p.getZ()-32, p.getX()+32, p.getY()+16, p.getZ()+32),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 200, 0));
            }
        } else if (id == 23) { // Blinding flash
            sw.spawnParticles(ParticleTypes.FLASH, p.getX(), p.getY() + 1, p.getZ(), 3, 0, 0, 0, 0);
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-8, p.getY()-4, p.getZ()-8, p.getX()+8, p.getY()+4, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 100, 1));
            }
        } else if (id == 24) { // Purification
            for (var eff : new net.minecraft.entity.effect.StatusEffect[]{
                    StatusEffects.POISON, StatusEffects.WITHER, StatusEffects.SLOWNESS,
                    StatusEffects.WEAKNESS, StatusEffects.BLINDNESS, StatusEffects.NAUSEA,
                    StatusEffects.DARKNESS, StatusEffects.MINING_FATIGUE}) {
                p.removeStatusEffect(eff);
            }
            for (int i = 0; i < 30; i++) {
                sw.spawnParticles(ParticleTypes.END_ROD, p.getX() + (sw.random.nextDouble()-0.5), p.getY() + sw.random.nextDouble()*2, p.getZ() + (sw.random.nextDouble()-0.5), 1, 0, 0.02, 0, 0.01);
            }
        } else if (id == 25) { // Archangel radiance
            p.heal(p.getMaxHealth());
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 2));
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 200, 2));
        }
        // TOME VI - LIGHTNING
        else if (id == 26) { // Discharge
            Vec3d target = eye.add(dir.multiply(10));
            var l = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(sw);
            if (l != null) { l.refreshPositionAfterTeleport(target); sw.spawnEntity(l); }
        } else if (id == 27) { // Chain spark
            List<LivingEntity> mobs = sw.getEntitiesByClass(LivingEntity.class,
                    new Box(p.getX()-8, p.getY()-4, p.getZ()-8, p.getX()+8, p.getY()+4, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity));
            int n = 0;
            for (LivingEntity m : mobs) {
                if (n++ >= 4) break;
                m.damage(sw.getDamageSources().magic(), 8.0f);
                for (int i = 0; i < 20; i++) {
                    double t = i / 20.0;
                    sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK,
                            p.getX() + (m.getX() - p.getX()) * t,
                            p.getY() + 1 + (m.getY() - p.getY()) * t,
                            p.getZ() + (m.getZ() - p.getZ()) * t,
                            1, 0, 0, 0, 0);
                }
            }
        } else if (id == 28) { // Lightning step
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 160, 2));
        } else if (id == 29) { // Electro sphere
            for (int i = 0; i < 60; i++) {
                double a = Math.PI * 2 * i / 60.0;
                sw.spawnParticles(ParticleTypes.ELECTRIC_SPARK, p.getX() + Math.cos(a) * 2, p.getY() + 1, p.getZ() + Math.sin(a) * 2, 1, 0, 0.02, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-2, p.getY()-2, p.getZ()-2, p.getX()+2, p.getY()+2, p.getZ()+2),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 6.0f);
            }
        } else if (id == 30) { // Zeus wrath
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-10, p.getY()-5, p.getZ()-10, p.getX()+10, p.getY()+5, p.getZ()+10),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                var l = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(sw);
                if (l != null) { l.refreshPositionAfterTeleport(e.getPos()); sw.spawnEntity(l); }
                e.damage(sw.getDamageSources().magic(), 10.0f);
            }
        }
        // TOME VII - SHADOW
        else if (id == 31) { // Shadow bolt
            WordProjectileEntity proj = ModEntities.WORD_PROJECTILE.create(sw);
            if (proj != null) { proj.init("shadow_bolt"); proj.setOwner(p);
                proj.refreshPositionAndAngles(eye.x, eye.y, eye.z, 0, 0);
                proj.setVelocity(dir.multiply(1.4)); sw.spawnEntity(proj); }
        } else if (id == 32) { // Shadow step
            Vec3d target = p.getPos().add(dir.multiply(10));
            p.requestTeleport(target.x, target.y, target.z);
            for (int i = 0; i < 30; i++) {
                sw.spawnParticles(ParticleTypes.SQUID_INK, p.getX() + (sw.random.nextDouble()-0.5), p.getY() + sw.random.nextDouble()*2, p.getZ() + (sw.random.nextDouble()-0.5), 1, 0, 0.02, 0, 0.02);
            }
        } else if (id == 33) { // Invisibility
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, 200, 0));
        } else if (id == 34) { // Life drain
            Vec3d target = eye.add(dir.multiply(6));
            List<LivingEntity> mobs = sw.getEntitiesByClass(LivingEntity.class,
                    new Box(target.x-3, target.y-3, target.z-3, target.x+3, target.y+3, target.z+3),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity));
            if (!mobs.isEmpty()) {
                LivingEntity m = mobs.get(0);
                m.damage(sw.getDamageSources().magic(), 8.0f);
                p.heal(4.0f);
            }
        } else if (id == 35) { // Night veil
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-8, p.getY()-4, p.getZ()-8, p.getX()+8, p.getY()+4, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0));
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 200, 2));
            }
        }
        // TOME VIII - NATURE
        else if (id == 36) { // Poison thorn
            WordProjectileEntity proj = ModEntities.WORD_PROJECTILE.create(sw);
            if (proj != null) { proj.init("poison_thorn"); proj.setOwner(p);
                proj.refreshPositionAndAngles(eye.x, eye.y, eye.z, 0, 0);
                proj.setVelocity(dir.multiply(1.5)); sw.spawnEntity(proj); }
        } else if (id == 37) { // Blossom
            BlockPos bp = BlockPos.ofFloored(p.getPos());
            for (int dx = -4; dx <= 4; dx++) for (int dz = -4; dz <= 4; dz++) for (int dy = 0; dy <= 2; dy++) {
                BlockPos t = bp.add(dx, dy, dz);
                if (sw.getBlockState(t).isOf(Blocks.GRASS_BLOCK) || sw.getBlockState(t).isOf(Blocks.DIRT)) {
                    if (sw.getBlockState(t.up()).isAir() && sw.random.nextInt(4) == 0) {
                        sw.setBlockState(t.up(), Blocks.GRASS.getDefaultState());
                    }
                }
            }
        } else if (id == 38) { // Emerald cloud
            for (int i = 0; i < 60; i++) {
                sw.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                        p.getX() + (sw.random.nextDouble()-0.5) * 6,
                        p.getY() + 1 + sw.random.nextDouble() * 2,
                        p.getZ() + (sw.random.nextDouble()-0.5) * 6, 1, 0, 0.02, 0, 0.01);
            }
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-6, p.getY()-3, p.getZ()-6, p.getX()+6, p.getY()+3, p.getZ()+6),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, 200, 1));
            }
        } else if (id == 39) { // Forest gift
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1));
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, 100, 0));
        } else if (id == 40) { // Ancient forest call
            for (int i = 0; i < 3; i++) {
                var wolf = net.minecraft.entity.EntityType.WOLF.create(sw);
                if (wolf != null) {
                    wolf.refreshPositionAndAngles(p.getX() + (i-1), p.getY(), p.getZ() + i, 0, 0);
                    wolf.setTamed(true);
                    wolf.setOwner(p);
                    sw.spawnEntity(wolf);
                }
            }
        }
        // TOME IX - SPACE
        else if (id == 41) { // Blink
            Vec3d target = p.getPos().add(dir.multiply(14));
            p.requestTeleport(target.x, target.y, target.z);
            for (int i = 0; i < 30; i++) {
                sw.spawnParticles(ParticleTypes.PORTAL, p.getX() + (sw.random.nextDouble()-0.5), p.getY() + sw.random.nextDouble()*2, p.getZ() + (sw.random.nextDouble()-0.5), 1, 0, 0.02, 0, 0.02);
            }
        } else if (id == 42) { // Gravity pull
            Vec3d target = eye.add(dir.multiply(8));
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(target.x-10, target.y-10, target.z-10, target.x+10, target.y+10, target.z+10),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                var d = target.subtract(e.getPos()).normalize().multiply(0.6);
                e.setVelocity(d.x, d.y, d.z);
                e.velocityModified = true;
            }
        } else if (id == 43) { // Repulse
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-6, p.getY()-3, p.getZ()-6, p.getX()+6, p.getY()+3, p.getZ()+6),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                var d = e.getPos().subtract(p.getPos()).normalize().multiply(2.0);
                e.setVelocity(d.x, 0.8, d.z);
            }
        } else if (id == 44) { // Sky jump
            p.setVelocity(p.getVelocity().x, 1.5, p.getVelocity().z);
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 200, 0));
        } else if (id == 45) { // Dimension rift
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-8, p.getY()-8, p.getZ()-8, p.getX()+8, p.getY()+8, p.getZ()+8),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 18.0f);
            }
            for (int i = 0; i < 120; i++) {
                double a = Math.PI * 2 * i / 120.0;
                sw.spawnParticles(ParticleTypes.PORTAL, p.getX() + Math.cos(a) * 8, p.getY() + 1, p.getZ() + Math.sin(a) * 8, 1, 0, 0.02, 0, 0.02);
            }
        }
        // TOME X - VOID
        else if (id == 46) { // DNA disintegration
            for (int i = 1; i <= 20; i++) {
                Vec3d pos = eye.add(dir.multiply(i));
                double offset = Math.sin(i * 0.5) * 0.4;
                Vec3d perp = new Vec3d(-dir.z, 0, dir.x).normalize().multiply(offset);
                sw.spawnParticles(ParticleTypes.DRAGON_BREATH, pos.x + perp.x, pos.y, pos.z + perp.z, 1, 0, 0, 0, 0);
                sw.spawnParticles(ParticleTypes.END_ROD, pos.x - perp.x, pos.y, pos.z - perp.z, 1, 0, 0, 0, 0);
                for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(pos.x-1, pos.y-1, pos.z-1, pos.x+1, pos.y+1, pos.z+1),
                        e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                    e.damage(sw.getDamageSources().magic(), 2.5f);
                }
            }
        } else if (id == 47) { // Black hole
            Vec3d target = eye.add(dir.multiply(10));
            for (int t = 0; t < 20; t++) {
                for (int i = 0; i < 30; i++) {
                    double a = Math.PI * 2 * i / 30.0;
                    double r = 2.0 * (1 - t / 20.0);
                    sw.spawnParticles(ParticleTypes.PORTAL, target.x + Math.cos(a) * r, target.y, target.z + Math.sin(a) * r, 1, 0, 0, 0, 0);
                }
            }
            sw.createExplosion(p, target.x, target.y, target.z, 4.0f, World.ExplosionSourceType.NONE);
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(target.x-6, target.y-6, target.z-6, target.x+6, target.y+6, target.z+6),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 20.0f);
            }
        } else if (id == 48) { // Archmage storm
            for (int i = 0; i < 7; i++) {
                Vec3d target = eye.add(dir.multiply(5 + i * 2));
                var l = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(sw);
                if (l != null) { l.refreshPositionAfterTeleport(target); sw.spawnEntity(l); }
            }
        } else if (id == 49) { // Archmage aegis
            p.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 120, 4));
            for (int i = 0; i < 60; i++) {
                double a = Math.PI * 2 * i / 60.0;
                sw.spawnParticles(ParticleTypes.END_ROD, p.getX() + Math.cos(a) * 2, p.getY() + 1, p.getZ() + Math.sin(a) * 2, 1, 0, 0.02, 0, 0.01);
            }
        } else if (id == 50) { // End of world
            for (var e : sw.getEntitiesByClass(LivingEntity.class, new Box(p.getX()-15, p.getY()-15, p.getZ()-15, p.getX()+15, p.getY()+15, p.getZ()+15),
                    e -> e.isAlive() && !((Object) e instanceof ServerPlayerEntity))) {
                e.damage(sw.getDamageSources().magic(), 40.0f);
                e.setVelocity(0, 1.5, 0);
            }
            for (int i = 0; i < 400; i++) {
                double a = Math.PI * 2 * i / 400.0;
                double r = (i / 400.0) * 15;
                sw.spawnParticles(ParticleTypes.FLASH, p.getX() + Math.cos(a) * r, p.getY() + 1, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.02);
                sw.spawnParticles(ParticleTypes.SOUL_FIRE_FLAME, p.getX() + Math.cos(a) * r, p.getY() + 1, p.getZ() + Math.sin(a) * r, 1, 0, 0.02, 0, 0.02);
            }
        }
    }
}

