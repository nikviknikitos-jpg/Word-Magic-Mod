package com.example.wordmagic;

import com.example.wordmagic.block.ModBlocks;
import com.example.wordmagic.command.WordMagicCommand;
import com.example.wordmagic.entity.ModEntities;
import com.example.wordmagic.item.ModItems;
import com.example.wordmagic.network.SpellSync;
import com.example.wordmagic.spell.SpellRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordMagicMod implements ModInitializer {
    public static final String MODID = "wordmagic";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        ModEntities.register();
        ModItems.register();
        ModBlocks.register();
        SpellRegistry.init();
        SpellSync.registerCommon();
        FabricDefaultAttributeRegistry.register(ModEntities.RUNE_GUARDIAN, com.example.wordmagic.entity.RuneGuardianEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.SHADOW_CULTIST, com.example.wordmagic.entity.ShadowCultistEntity.createAttributes());
        FabricDefaultAttributeRegistry.register(ModEntities.ANCIENT_COLOSSUS, com.example.wordmagic.entity.AncientColossusEntity.createAttributes());
        CommandRegistrationCallback.EVENT.register((d, ra, e) -> WordMagicCommand.register(d));
        ServerTickEvents.END_WORLD_TICK.register(com.example.wordmagic.tick.WordMagicTickHandler::tick);
        ServerPlayConnectionEvents.JOIN.register((h, s, srv) -> {
            SpellSync.send(h.getPlayer());
            com.example.wordmagic.spell.SpellState.known(h.getPlayer().getUuid()).addAll(java.util.Set.of(1,2,3,4,5));
            SpellSync.send(h.getPlayer());
        });
        LOGGER.info("WordMagicMod initialized");
    }
}
