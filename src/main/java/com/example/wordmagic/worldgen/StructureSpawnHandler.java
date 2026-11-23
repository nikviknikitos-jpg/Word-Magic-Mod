package com.example.wordmagic.worldgen;

import com.example.wordmagic.block.ModBlocks;
import com.example.wordmagic.entity.ModEntities;
import com.example.wordmagic.entity.RuneGuardianEntity;
import com.example.wordmagic.entity.ShadowCultistEntity;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class StructureSpawnHandler {
    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(StructureSpawnHandler::tick);
    }
    private static void tick(ServerWorld world) {
        if (world.getTime() % 400 != 0) return;
        for (var player : world.getPlayers()) {
            if (player.age < 100) continue;
            Random r = new Random();
            if (r.nextInt(20) != 0) continue;
            BlockPos base = player.getBlockPos().add((r.nextInt(41) - 20), 0, (r.nextInt(41) - 20));
            buildShrine(world, base);
            break;
        }
    }
    private static void buildShrine(ServerWorld world, BlockPos base) {
        BlockPos surface = world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, base);
        // columns
        for (int dx = -2; dx <= 2; dx += 2) for (int dz = -2; dz <= 2; dz += 2) {
            for (int dy = 0; dy < 4; dy++) {
                world.setBlockState(surface.add(dx, dy, dz), Blocks.CHISELED_STONE_BRICKS.getDefaultState());
            }
        }
        // altar
        world.setBlockState(surface, Blocks.CHISELED_STONE_BRICKS.getDefaultState());
        world.setBlockState(surface.up(), Blocks.CHISELED_STONE_BRICKS.getDefaultState());
        // chest
        world.setBlockState(surface.up(2), Blocks.CHEST.getDefaultState());
        // rune guardians
        for (int i = 0; i < 2; i++) {
            RuneGuardianEntity g = ModEntities.RUNE_GUARDIAN.create(world);
            if (g != null) {
                g.refreshPositionAndAngles(surface.getX() + i * 3 - 1.5, surface.getY() + 1, surface.getZ() + i * 2, 0, 0);
                world.spawnEntity(g);
            }
        }
        ShadowCultistEntity c = ModEntities.SHADOW_CULTIST.create(world);
        if (c != null) {
            c.refreshPositionAndAngles(surface.getX(), surface.getY() + 1, surface.getZ() - 3, 0, 0);
            world.spawnEntity(c);
        }
        // boss arena marker near
        BlockPos arena = surface.add(30, 0, 30);
        BlockPos arenaSurface = world.getTopPosition(net.minecraft.world.Heightmap.Type.WORLD_SURFACE, arena);
        for (int dx = -11; dx <= 11; dx++) for (int dz = -11; dz <= 11; dz++) {
            double dist = Math.sqrt(dx*dx + dz*dz);
            if (dist > 11) continue;
            world.setBlockState(arenaSurface.add(dx, 0, dz), Blocks.CRYING_OBSIDIAN.getDefaultState());
            if (dist > 9 && dist < 10) world.setBlockState(arenaSurface.add(dx, 1, dz), Blocks.CHISELED_DEEPSLATE.getDefaultState());
        }
        world.setBlockState(arenaSurface, ModBlocks.SEAL_STONE.getDefaultState());
    }
}
