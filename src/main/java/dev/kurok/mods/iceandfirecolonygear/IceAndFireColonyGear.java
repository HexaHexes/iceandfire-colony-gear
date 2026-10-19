package dev.kurok.mods.iceandfirecolonygear;

import dev.kurok.mods.iceandfirecolonygear.gear.GearLevelRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Ice and Fire Colony Gear - folds Ice and Fire CE gear into MineColonies'
 * citizen weapon/tool/armor ladder through MineColonies' public registration
 * API only (no mixins).
 */
@Mod(IceAndFireColonyGear.MOD_ID)
public class IceAndFireColonyGear {

    public static final String MOD_ID = "iceandfire_colony_gear";

    public IceAndFireColonyGear(IEventBus modBus) {
        modBus.addListener((FMLCommonSetupEvent event) -> event.enqueueWork(GearLevelRegistry::init));
    }
}
