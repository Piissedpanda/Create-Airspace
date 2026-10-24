# Create: Airspace

Aviation and airport accessories for [Create](https://github.com/Creators-of-Create/Create). Create: Airspace adds the things an airfield needs around your aircraft and fits right next to Create and Create Aeronautics: it builds on Create's own processes instead of duplicating them.

Minecraft 1.21.1 · NeoForge · Create 6.0.10 or newer

## Blocks

| Block | What it does |
| --- | --- |
| **Barrier Posts** | Connect posts with a wrench to stretch a belt between them, recolor belts with dye. Industrial iron, andesite, brass, copper and train casing variants. |
| **Traffic Cone** | Marks closed areas. Can be placed underwater. |
| **Terminal Seats** | Seats facing the same way join into rows. Sit down like on Create's seats, works on contraptions too. 16 colors, recolor with dye. |
| **Airfield Signs** | Location, direction and mandatory signs (switch with a wrench). Write on a whole row with a named name tag, a clipboard or a Display Link. |
| **Edge Lights** | Light up with redstone, redstone dust connects to them. White, blue, green, red and yellow. |
| **Windsock** | Turns into the wind and rises with its strength. Rain, thunderstorms and height make the wind stronger. |
| **Airport Beacon** | Rotational force turns the head, redstone from the sides controls brightness and range of the light. Land airport, water airport, heliport and military patterns (wrench). Looking into a beam dazzles you (can be turned off in the client config). |

Recipes show up in recipe viewers like JEI, and most blocks have a Ponder scene (hold `W` over the item).

## Recipes

Recipes use Create's processes where they make sense: crafting for the simple blocks, deploying and item application for seats and edge lights, sequenced assembly for airfield signs and mechanical crafting for the airport beacon.

## Building

```bash
./gradlew build
```

The jar ends up in `build/libs/`. Data generation (models, lang, recipes, tags) runs with `./gradlew runData`.

For shader testing in the dev client, `./gradlew installDevMods` copies Sodium and Iris into `run/mods` and `run2/mods`.

## License

[MIT](LICENSE)
