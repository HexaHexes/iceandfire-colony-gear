# Ice and Fire Colony Gear

Lets your **MineColonies** citizens equip **Ice and Fire CE** weapons, tools and armor,
gated by building level the same way MineColonies gates its own gear. MineColonies ships
no Ice and Fire support on its own, so colonists couldn't use any of it before.

## How it works

Citizens can use gear up to one level below their building's level. This mod registers
Ice and Fire's gear onto that ladder:

| Material | Level | Examples |
|---|---|---|
| copper | 1 | copper tools and sword |
| silver, troll weapons, boss-loot swords | 2 | silver tools, troll axe, hippogryph/stymphalian/amphithere/hippocampus swords |
| dragonbone, ghost, dread, dread knight | 3 | dragonbone tools and bow, ghost sword, dread sword (above iron, near-netherite) |
| dread queen, dragonsteel (fire/ice/lightning) | 4 | dread queen sword, all dragonsteel gear (endgame) |

- **Armor needs no setup** — MineColonies grades it from each piece's defense value
  (copper/silver near iron, deathworm and troll mid, dragon scale above, dragonsteel at
  the very top).
- Every Ice and Fire weapon is recognised automatically as a citizen weapon.

This mod is deliberately **Ice and Fire only** — the shared vanilla ladder
(wood/gold/stone, iron/diamond, netherite) stays owned by
[Tensura Colony Gear](https://github.com/HexaHexes/tensura-colony-gear), so the two
never double up.

## Getting it in-game

Nothing to configure — Ice and Fire gear appears in colonists' equipment screens once
the building level allows it.

## Dependencies

| | Mod | Why |
|---|---|---|
| **Required** | [MineColonies](https://www.curseforge.com/minecraft/mc-mods/minecolonies) | The citizen equipment system |
| **Required** | [Ice and Fire CE](https://www.curseforge.com/minecraft/mc-mods/iceandfire-ce) | The gear |
| Recommended | [Tensura Colony Gear](https://github.com/HexaHexes/tensura-colony-gear) | Owns the shared vanilla ladder (works without it, keeping MineColonies' defaults) |

Made for the **Tensura RPG** pack (Minecraft 1.21.1, NeoForge).

## License

GNU Lesser General Public License v2.1 — see [`LICENSE`](LICENSE).
Copyright (c) 2026 Bitwise Madness.
