# Ice and Fire Colony Gear

A NeoForge mod for the **Tensura RPG** pack (MC 1.21.1 / NeoForge 21.1.259) that folds **Ice and Fire CE** gear into MineColonies' citizen weapon/tool/armor ladder — using only MineColonies' public registration API (no mixins), mirroring [tensura-colony-gear](https://github.com/HexaHexes/tensura-colony-gear).

MineColonies ships no Ice and Fire integration, so colonists could not equip any of its tools or blades. This mod registers the approved ladder via `Compatibility.registerItemTier`, which backs `Compatibility.getItemLevel` — the exact lookup the equipment windows check in-game.

## The ladder

Tools and weapons (keyed by Ice and Fire's `IafToolMaterials` tier enum, with a registry-path fragment fallback for gear without a readable tier — the dragonbone bow):

| Material | Level | Examples |
|---|---|---|
| copper | 1 | copper tools/sword (vanilla-copper parity) |
| silver, troll weapon, boss-loot swords | 2 | silver tools, troll axe, hippogryph/stymphalian/amphithere/hippocampus swords |
| dragonbone (+ blooded elemental swords), ghost, dread, dread knight | 3 | dragonbone tools/bow, ghost sword, dread sword (above iron, ~netherite-par) |
| dread queen, dragonsteel (fire/ice/lightning) | 4 | dread queen sword, all dragonsteel tools/weapons (endgame) |

Armor needs no registration: MineColonies derives each piece's level from its armor points against the vanilla per-slot distributions, which grades Ice and Fire's sets sensibly on its own (copper/silver around iron, deathworm/troll mid, dragon scale above, dragonsteel pinning the top grade). The gametests pin the relative ordering.

Notes:

- **Namespace-pure**: only `iceandfire:` items are registered — the compact vanilla ladder (wood/gold/stone=1, iron/diamond=2, netherite=3) stays owned by tensura-colony-gear. Non-gear materials sharing a material word (`dragonbone`, `raw_silver`, `dragonsteel_*_ingot`) are excluded by a gear-shape gate (TieredItem/Equipable/Bow/Crossbow/ProjectileWeaponItem).
- **No weapon recognizer needed**: every Ice and Fire weapon extends a standard vanilla class (`SwordItem`/`AxeItem`/`BowItem`), which MineColonies' own equipment-type checks already recognize. The fishing spear (`TridentItem`) is deliberately not citizen gear.
- Ice and Fire is never referenced at compile time; the ladder keys off tier enum names and registry paths, so it stays valid across Ice and Fire CE updates.

## Dependencies (all required)

| Mod | Version range | Why |
|---|---|---|
| NeoForge | `[21.1,)` | Platform |
| Minecraft | `[1.21.1,1.21.2)` | Platform |
| MineColonies | `[1.1,)` | Public gear-level API |
| Ice and Fire CE (`iceandfire`) | `[2.0-beta.1,)` | The gear (2.0 is still in beta; the floor includes the beta line) |

## Build & test

Requires JDK 21. Compile-only mod jars go in `libs/`, gametest-run mod jars in `testmods/` (both gitignored).

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21"
gradle build          # jar -> build\libs\iceandfire-colony-gear-1.0.0.jar
gradle runGametest    # headless GameTestServer in run/, graded by the build
```

The gametests (3 required) verify the ladder end to end through MineColonies' own `Compatibility.getItemLevel`: every tier enum constant lands on its approved rung (exhaustive registry walk), the name-fallback bow and runtime persistence hold inside the 1..5 ladder, non-gear materials stay unregistered, and armor auto-grades with dragonsteel above copper. Pure registration mod — the gametests cover everything; no in-game-only caveats.

## Install

Copy the jar into the instance's `mods\` folder (requires tensura-colony-gear for the shared vanilla compaction, though the mod works standalone — vanilla simply keeps MineColonies' defaults then).
