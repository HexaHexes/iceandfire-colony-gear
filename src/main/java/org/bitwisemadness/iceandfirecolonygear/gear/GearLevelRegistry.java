package org.bitwisemadness.iceandfirecolonygear.gear;

import com.minecolonies.api.compatibility.Compatibility;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Registers the approved Ice and Fire CE gear ladder into MineColonies'
 * equipment-level system through its public API ({@code Compatibility
 * .registerItemTier}), which backs {@code getItemLevel} and therefore the
 * level window every tool/weapon/bow equipment type checks ({@code 0 <= level
 * <= maxEquipmentLevel}, with {@code maxEquipmentLevel = buildingLevel - 1}
 * below the building's max level and unbounded at max).
 *
 * <p>Armor deliberately registers nothing of its own: MineColonies derives
 * armor levels from each piece's armor points against the vanilla per-slot
 * distributions, and there is no public override. Ice and Fire's sets grade
 * sensibly that way (copper/silver around iron, deathworm/troll mid, dragon
 * scale above, dragonsteel pinning the top grade). Name-fallback matches on
 * armor paths are inert for the same reason - the armor-points derivation
 * takes precedence in MineColonies' lookup.
 *
 * <p>No weapon recognizer is needed: every Ice and Fire weapon extends a
 * standard vanilla class ({@code SwordItem} / {@code AxeItem} /
 * {@code BowItem}), which MineColonies' own equipment-type checks already
 * recognize.
 *
 * <p>Ice and Fire is never referenced at compile time. Tool tiers are keyed by
 * the {@code IafToolMaterials} enum constant names, everything else by
 * material fragments in the registry path. This mod is namespace-pure: only
 * {@code iceandfire:} items are registered, the vanilla compact ladder stays
 * owned by tensura-colony-gear.
 */
public final class GearLevelRegistry {

    private static final Logger LOG = LogManager.getLogger("iceandfire_colony_gear");

    public static final String IAF_NAMESPACE = "iceandfire";

    /**
     * Ice and Fire tool-tier enum constant ({@code IafToolMaterials}) to
     * colony level. The approved ladder:
     *
     * <pre>
     * copper                        1  (vanilla-copper parity)
     * silver, troll, boss-loot      2  (early/mid, iron-par)
     * dragonbone (+blooded), ghost  3  (above iron, ~netherite-par)
     * dread, dread knight           3
     * dread queen, dragonsteel      4  (endgame)
     * </pre>
     */
    private static final Map<String, Integer> TIER_LEVELS = Map.ofEntries(
            Map.entry("COPPER_TOOL_MATERIAL", 1),
            Map.entry("SILVER_TOOL_MATERIAL", 2),
            Map.entry("TROLL_WEAPON_TOOL_MATERIAL", 2),
            Map.entry("HIPPOGRYPH_SWORD_TOOL_MATERIAL", 2),
            Map.entry("STYMHALIAN_SWORD_TOOL_MATERIAL", 2),
            Map.entry("AMPHITHERE_SWORD_TOOL_MATERIAL", 2),
            Map.entry("HIPPOCAMPUS_SWORD_TOOL_MATERIAL", 2),
            Map.entry("DRAGONBONE_TOOL_MATERIAL", 3),
            Map.entry("BLOODED_DRAGONBONE_TOOL_MATERIAL", 3),
            Map.entry("GHOST_SWORD_TOOL_MATERIAL", 3),
            Map.entry("DREAD_SWORD_TOOL_MATERIAL", 3),
            Map.entry("DREAD_KNIGHT_TOOL_MATERIAL", 3),
            Map.entry("DREAD_QUEEN", 4),
            Map.entry("DRAGONSTEEL_FIRE", 4),
            Map.entry("DRAGONSTEEL_ICE", 4),
            Map.entry("DRAGONSTEEL_LIGHTNING", 4));

    /**
     * Fallback for Ice and Fire gear without a readable tier (the dragonbone
     * bow): most specific fragments first so "dread_queen" never falls through
     * to plain "dread", and "troll_weapon" never matches the troll-leather
     * armor (armor is auto-derived anyway).
     */
    private static final String[][] NAME_LEVELS = {
        {"dragonsteel", "4"},
        {"dread_queen", "4"},
        {"dragonbone", "3"},
        {"ghost_sword", "3"},
        {"dread", "3"},
        {"hippogryph", "2"},
        {"stymphalian", "2"},
        {"amphithere", "2"},
        {"hippocampus", "2"},
        {"troll_weapon", "2"},
        {"silver", "2"},
        {"copper", "1"},
    };

    private GearLevelRegistry() {}

    /** FMLCommonSetupEvent enqueueWork entry point; idempotent per game launch. */
    public static void init() {
        int registered = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            int level = levelFor(item);
            if (level >= 0) {
                Compatibility.registerItemTier(item, level);
                registered++;
            }
        }
        LOG.info(
                "Registered {} Ice and Fire gear levels into MineColonies (ladder: copper=1"
                        + " silver/troll/boss-loot=2 dragonbone/blooded/ghost/dread=3"
                        + " dread-queen/dragonsteel=4; armor auto-derived from armor points)",
                registered);
    }

    /** Colony level for the item, or -1 when the item is not part of the ladder. */
    public static int levelFor(Item item) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) {
            return -1;
        }
        if (!id.getNamespace().equals(IAF_NAMESPACE)) {
            // Namespace-pure: the vanilla compact ladder is owned by
            // tensura-colony-gear, other mods' gear is not ours to place.
            return -1;
        }
        if (item instanceof TieredItem tiered) {
            Integer fromTier = tierLevelOrNull(tiered.getTier());
            if (fromTier != null) {
                return fromTier;
            }
        }
        if (!isGearShaped(item)) {
            return -1;
        }
        return levelFromName(id.getPath());
    }

    private static Integer tierLevelOrNull(Tier tier) {
        if (tier == null) {
            return null;
        }
        String name = tier instanceof Enum<?> e ? e.name() : tier.toString();
        return TIER_LEVELS.get(name);
    }

    private static int levelFromName(String path) {
        for (String[] entry : NAME_LEVELS) {
            if (path.contains(entry[0])) {
                return Integer.parseInt(entry[1]);
            }
        }
        return -1;
    }

    /**
     * Only registry entries that could plausibly be equipment take the
     * name-based fallback, so materials/monsters sharing a material word (e.g.
     * stymphalian feathers, dragon bones and scales) are never registered as
     * gear. The fishing spear ({@code TridentItem}) is deliberately not
     * equipment for citizens.
     */
    private static boolean isGearShaped(Item item) {
        return item instanceof TieredItem
                || item instanceof Equipable
                || item instanceof BowItem
                || item instanceof CrossbowItem
                || item instanceof ProjectileWeaponItem;
    }
}
