package com.example.wordmagic.entity;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {
    public static EntityType<RuneGuardianEntity> RUNE_GUARDIAN;
    public static EntityType<ShadowCultistEntity> SHADOW_CULTIST;
    public static EntityType<AncientColossusEntity> ANCIENT_COLOSSUS;
    public static EntityType<WordProjectileEntity> WORD_PROJECTILE;

    public static void register() {
        RUNE_GUARDIAN = Registry.register(Registries.ENTITY_TYPE,
                new Identifier("wordmagic", "rune_guardian"),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, RuneGuardianEntity::new)
                        .dimensions(EntityDimensions.fixed(1.0f, 1.8f)).build());
        SHADOW_CULTIST = Registry.register(Registries.ENTITY_TYPE,
                new Identifier("wordmagic", "shadow_cultist"),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, ShadowCultistEntity::new)
                        .dimensions(EntityDimensions.fixed(0.9f, 1.9f)).build());
        ANCIENT_COLOSSUS = Registry.register(Registries.ENTITY_TYPE,
                new Identifier("wordmagic", "ancient_colossus"),
                FabricEntityTypeBuilder.create(SpawnGroup.MONSTER, AncientColossusEntity::new)
                        .dimensions(EntityDimensions.fixed(3.0f, 4.5f)).build());
        WORD_PROJECTILE = Registry.register(Registries.ENTITY_TYPE,
                new Identifier("wordmagic", "word_projectile"),
                FabricEntityTypeBuilder.<WordProjectileEntity>create(SpawnGroup.MISC, WordProjectileEntity::new)
                        .dimensions(EntityDimensions.fixed(0.4f, 0.4f)).build());
    }
}
