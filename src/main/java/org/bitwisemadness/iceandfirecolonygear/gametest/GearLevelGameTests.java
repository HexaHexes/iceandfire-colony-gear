package org.bitwisemadness.iceandfirecolonygear.gametest;

import com.minecolonies.api.compatibility.Compatibility;
import org.bitwisemadness.iceandfirecolonygear.gear.GearLevelRegistry;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestAssertException;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

/**
 * Verifies the registered gear ladder end to end through MineColonies' own
 * lookup ({@code Compatibility.getItemLevel}) - the exact value the equipment
 * windows check in-game. Ice and Fire items are discovered from the registry
 * by tier enum name / material fragment, so the tests do not depend on
 * individual item ids and stay valid across Ice and Fire CE updates.
 */
@GameTestHolder("iceandfire_colony_gear")
@PrefixGameTestTemplate(false)
public class GearLevelGameTests {

    /** The approved ladder, mirrored here independently of the registry code. */
    private static final Map<String, Integer> EXPECTED_TIER_LEVELS = Map.ofEntries(
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

    private static Item item(String id) {
        return BuiltInRegistries.ITEM
                .getOptional(ResourceLocation.parse(id))
                .orElseThrow(() -> new GameTestAssertException("missing item " + id));
    }

    /** Every Ice and Fire tool tier lands on its approved rung (via tier enum name). */
    @GameTest(template = "empty")
    public static void iafToolTiersMapToLadder(GameTestHelper helper) {
        Map<String, Integer> found = new LinkedHashMap<>();
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null
                    || !id.getNamespace().equals(GearLevelRegistry.IAF_NAMESPACE)
                    || !(item instanceof TieredItem tiered)) {
                continue;
            }
            String tierName = tiered.getTier() instanceof Enum<?> e ? e.name() : tiered.getTier().toString();
            Integer expected = EXPECTED_TIER_LEVELS.get(tierName);
            if (expected == null) {
                continue;
            }
            int actual = Compatibility.getItemLevel(new ItemStack(item));
            if (actual != expected) {
                throw new GameTestAssertException(
                        id + " (tier " + tierName + ") has colony level " + actual + " expected " + expected);
            }
            found.putIfAbsent(tierName, actual);
        }
        for (Map.Entry<String, Integer> expected : EXPECTED_TIER_LEVELS.entrySet()) {
            if (!found.containsKey(expected.getKey())) {
                throw new GameTestAssertException("no Ice and Fire TieredItem found for tier " + expected.getKey());
            }
        }
        helper.succeed();
    }

    /**
     * Name-based fallback covers gear without a readable tier (the dragonbone
     * bow), every item we place on the ladder keeps its level at runtime (i.e.
     * MineColonies never overwrites our registration with its own defaults),
     * the ladder stays inside 1..5, and non-gear materials are untouched.
     */
    @GameTest(template = "empty")
    public static void nameFallbackBoundsAndNegatives(GameTestHelper helper) {
        int fallbackMatches = 0;
        int ours = 0;
        for (Item item : BuiltInRegistries.ITEM) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
            if (id == null || !id.getNamespace().equals(GearLevelRegistry.IAF_NAMESPACE)) {
                continue;
            }
            int expected = GearLevelRegistry.levelFor(item);
            if (expected < 0) {
                continue;
            }
            if (expected < 1 || expected > 5) {
                throw new GameTestAssertException(id + " placed outside the 1..5 ladder (" + expected + ")");
            }
            int actual = Compatibility.getItemLevel(new ItemStack(item));
            if (actual != expected) {
                throw new GameTestAssertException(
                        id + " has runtime colony level " + actual + " but was registered at " + expected);
            }
            ours++;
            if (!(item instanceof TieredItem) && id.getPath().contains("dragonbone")) {
                fallbackMatches++; // dragonbone_bow: BowItem, no tier, matched by fragment
            }
        }
        if (ours < 50) {
            throw new GameTestAssertException("expected a full Ice and Fire gear ladder, only " + ours + " items");
        }
        if (fallbackMatches == 0) {
            throw new GameTestAssertException("no Ice and Fire bow matched the name-based fallback");
        }
        // Non-gear materials sharing our fragments stay off the ladder
        // (both contain ladder fragments but are not gear-shaped).
        if (GearLevelRegistry.levelFor(item("iceandfire:dragonbone")) != -1) {
            throw new GameTestAssertException("dragonbone material was registered as gear");
        }
        if (GearLevelRegistry.levelFor(item("iceandfire:raw_silver")) != -1) {
            throw new GameTestAssertException("raw_silver material was registered as gear");
        }
        helper.succeed();
    }

    /**
     * Armor needs no registration: MineColonies derives its level from the
     * armor points. Pin the relative ordering (dragonsteel endgame above the
     * copper starter set) and that neither set collides with our tool ladder
     * intent.
     */
    @GameTest(template = "empty")
    public static void armorAutoDerived(GameTestHelper helper) {
        int copper = Compatibility.getItemLevel(new ItemStack(item("iceandfire:armor_copper_metal_helmet")));
        int dragonsteelFire = Compatibility.getItemLevel(new ItemStack(item("iceandfire:dragonsteel_fire_helmet")));
        int dragonsteelIce = Compatibility.getItemLevel(new ItemStack(item("iceandfire:dragonsteel_ice_helmet")));
        if (copper < 1) {
            throw new GameTestAssertException("copper helmet derived level " + copper + " below the ladder");
        }
        if (dragonsteelFire <= copper || dragonsteelIce <= copper) {
            throw new GameTestAssertException("dragonsteel armor (" + dragonsteelFire + "/" + dragonsteelIce
                    + ") does not outgrade copper armor (" + copper + ")");
        }
        if (Math.abs(dragonsteelFire - dragonsteelIce) > 1) {
            throw new GameTestAssertException(
                    "fire/ice dragonsteel helmets diverge (" + dragonsteelFire + " vs " + dragonsteelIce + ")");
        }
        helper.succeed();
    }
}
