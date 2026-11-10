# Create: Airspace

Aviation and airport accessories for [Create](https://github.com/Creators-of-Create/Create). Create: Airspace adds the things an airfield needs around your aircraft and fits right next to Create and Create Aeronautics: it builds on Create's own processes instead of duplicating them.

Minecraft 1.21.1 · NeoForge · Create 6.0.10 or newer · Create Aeronautics 1.3.0 or newer · Curios and Opal Light optional

## Blocks

| Block | What it does |
| --- | --- |
| **Barrier Posts** | Connect posts with a wrench to stretch a belt between them, recolor belts with dye. Industrial iron, andesite, brass, copper and train casing variants. |
| **Traffic Cone** | Marks closed areas. Stacks up to four, can be placed underwater. |
| **Terminal Seats** | Seats facing the same way join into rows. Sit down like on Create's seats, works on contraptions too. 16 colors, recolor with dye. |
| **Airfield Signs** | Location, direction and mandatory signs (switch with a wrench). Write on a whole row with a named name tag, a clipboard or a Display Link. |
| **Edge Lights** | Light up with redstone, redstone dust connects to them. White, blue, green, red and yellow. |
| **Windsock** | Turns into the wind and rises with its strength. Rain, thunderstorms and height make the wind stronger. |
| **Paint Pail** | Paints runway markings and 2x3 runway numbers in white or yellow paint. Sneak-right-click switches the pattern. Fill it with a spout or from a tank, paint comes from the mixer (dye, clay ball, water). |
| **Suitcase Packager** | Like Create's packager, but packs suitcases, trolleys, duffel bags and backpacks. Luggage travels like packages and hangs from chain conveyors by its handle. |
| **Baggage Cart** | Luggage storage for vehicles, joins into carts of up to 3x3 with one inventory. |
| **Wheel Chocks, Marshalling Wands, Ear Defenders** | Ground crew gear. Wands wave while right-click is held, ear defenders muffle sounds (also in the Curios head slot). |
| **Light Panels, Light Strips, Floor Lights** | Terminal lighting. Panels join into lit surfaces, strips into lines. Redstone dims them, right-click keeps them always on, dye recolors them (13 colors). |
| **Gate Signs** | Hanging signs with the airfield sign styles and text, plus white on blue. |
| **Hangar Doors** | Corrugated panels and windows that join into one door, moved with gantries, pistons or pulleys. |
| **Hangar Lights** | Ceiling floodlights with a light cone, switched like the terminal lights. |
| **Check-in Desk, Luggage Scale** | Desks join into a counter. The scale weighs luggage by its contents, also inline on belts, and gives a comparator signal between a set minimum and maximum. |
| **Cargo Containers** | Build ULDs from blocks: 1x1x2 LD2, 2x2x2 LD3, 3x2x2 LD11, 3x3x3 AMA. Each block holds as much as an Item Vault block. Assembled with a Physics Assembler without glue. |
| **Container Lock** | Pulls in an assembled container like a magnet, lines it up and holds it until redstone lets go. Floor or wall. |
| **Container Port** | Like a Portable Storage Interface for a locked container, the only way to load and unload it. |
| **Roller Bed** | Physics objects slide over it, players keep their grip. Made with steel balls cut from iron ingots. |
| **Motorized Roller Floor** | Shafts on all four sides drive two independent networks, one per axis, shared by a whole floor area. Pushes physics objects with spinning steel discs, not slippery, and a Sequenced Gearshift moves them an exact distance. |
| **PAPI Lights** | Landing aid in rows of four: each pilot sees white above a light's angle and red below it, two red and two white is on the glide path. The angle is set like a Create value. |
| **Stop Bars, Runway Guard Lights** | Red inset lights across a taxiway that all go dark when one of them gets redstone, and yellow lamps that flash in turns while powered. Airfield and gate signs light up fully with redstone. |
| **Metal Detector, Baggage Scanner** | A walk-through frame and an X-ray tunnel on a belt check people and luggage for weapons and metal, contents included, and answer with a beep, a red lamp and redstone. What counts is the item tag `createairspace:security_alarm`. |
| **Airport Beacon** | Rotational force turns the head, redstone from the sides controls brightness and range of the light. Land airport, water airport, heliport and military patterns (wrench). Looking into a beam dazzles you (can be turned off in the client config). |

Recipes show up in recipe viewers like JEI, and most blocks have a Ponder scene (hold `W` over the item).

## Recipes

Recipes use Create's processes where they make sense: crafting for the simple blocks, deploying and item application for seats and edge lights, sequenced assembly for airfield signs and the suitcase packager, mixing for paint and mechanical crafting for the airport beacon.

## Building

```bash
./gradlew build
```

The jar ends up in `build/libs/`. Data generation (models, lang, recipes, tags) runs with `./gradlew runData`.

For shader testing in the dev client, `./gradlew installDevMods` copies Sodium and Iris into `run/mods` and `run2/mods`.

## License

[MIT](LICENSE). Parts of the kinetics code are adapted from Create Big Cannons, see [third-party notices](THIRD_PARTY_NOTICES.md).
