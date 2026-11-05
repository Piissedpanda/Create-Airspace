# Changelog

## 1.4.0 – Airfield Systems

### Added
- **PAPI Lights**, a real landing aid: set four in a row next to the runway. Each pilot sees white above a light's angle and red below it, two red and two white means on the glide path. Every player sees the colors of their own approach. The angle is set on the back like other Create values; a light placed next to a row takes the next lower angle (3.5, 3.2, 2.8 and 2.5 degrees).
- **Stop Bars**: red inset lights across a taxiway. A redstone signal into any of them switches off the whole bar.
- **Runway Guard Lights**: two yellow lamps that flash in turns while powered.
- **Metal Detector**: a walk-through frame, tall enough to walk through upright, that checks players and mobs for weapons and metal, including what is packed in luggage, packages, shulker boxes and bundles. It beeps, turns its lamp red and gives off a redstone signal.
- **Baggage Scanner**: an X-ray tunnel placed like a Create tunnel on the middle of a straight belt. It encases the belt in industrial iron, has rubber curtains that swing as luggage passes and screens everything the same way, without holding it up. An andesite or brass belt casing comes off and drops.
- What the scanners react to is the item tag `createairspace:security_alarm`, datapacks can extend it.
- Ponder scenes for PAPI lights, stop bars and the scanners.
- Recipes: PAPI lights from iron sheets, an electron tube and iron bars; stop bars and runway guard lights from edge lights; both scanners with electron tubes, the baggage scanner on industrial iron blocks.

### Changed
- Airfield and gate signs light up fully while powered by redstone.

## 1.3.0 – Cargo

### Added
- **Cargo Containers** built from blocks in real ULD shapes: 1x1x2 is an LD2, 2x2x2 an LD3, 3x2x2 an LD11 and 3x3x3 an AMA. Each block holds as much as an Item Vault block. A Physics Assembler takes the whole container as one piece, no glue needed. The dock side (facing you when you place the last block, a wrench turns it) shows a lock point and port points.
- **Container Lock** for the floor or the wall: it pulls in an assembled container like a magnet, lines it up, grips it with claws and bolts and holds it until a redstone signal lets go. A comparator shows whether a container is locked.
- **Container Port**: works like a Portable Storage Interface. Once a locked container shows it a port point, its bolts slide forward and funnels, chutes and other logistics at the port fill or empty the container. Containers can only be loaded and unloaded through ports.
- **Roller Bed** with steel balls: containers and other physics objects slide over it, players keep their grip. The balls rattle while something rolls across.
- **Steel Balls**, cut from iron ingots in a stonecutter or with Create's saw.
- Ponder scene for containers, locks, ports and roller beds.

### Changed
- **Create Aeronautics** (with Simulated and Sable) is now required.

## 1.2.0 – Terminal & Hangar

### Added
- **Light Panels**, **Light Strips** and **Floor Lights** for terminals. Panels join into lit surfaces, strips into lines. Off without a signal, redstone dims them up to full brightness; right-click or a wrench keeps them always on. Dye recolors them without being used up (13 colors, white resets).
- **Gate Signs**: hanging signs with the same styles and text as airfield signs (name tags, clipboards, Display Links), plus white on blue.
- **Hangar Door Panels** and **Hangar Door Windows** in corrugated metal. They join into one door and can be moved with Create's gantries, pistons and pulleys.
- **Hangar Lights**: ceiling floodlights with a soft light cone down to the floor, switched like the terminal lights.
- **Check-in Desks** that join into one counter, and the **Luggage Scale**: put luggage on by hand, with funnels and chutes or let it roll through on belts. The weight comes from what is packed inside (block mass from Create Aeronautics when it is installed) and turns red above 23 kg. Comparators read it between a minimum and maximum weight set in the scale's window.
- Ponder scene for the luggage scale, and Create-style item descriptions (hold Shift) for the terminal lights.
- Colored light under shaders with **Opal Light** (optional): edge lights, terminal lights, hangar lights and signs glow in their own color.

### Changed
- Airfield signs give off a little light, black location signs only light up their text.

## 1.1.0 – Ground Handling

### Added
- **Paint Pail** with **White Paint** and **Yellow Paint**: paint runway markings (center line, edge line, double line, bar, full, holding position, curve, diagonal) and 2x3 runway numbers (0-9, L, R, C). Sneak-right-click switches the pattern, a wrench changes the character of a number. Fill the pail with a spout or from a tank; paint is mixed from dye, a clay ball and water.
- **Suitcase Packager**: a packager that wraps items into suitcases, trolleys, duffel bags and backpacks instead of cardboard boxes. Luggage works like Create's packages and hangs from chain conveyors by its handle. Dye fixes the luggage color.
- **Baggage Cart**: luggage storage for vehicles. Carts join into groups of up to 3x3 with one shared inventory; right-click to load, empty hand to take the top piece, funnels and chutes work too.
- **Wheel Chocks**, **Marshalling Wands** (hold right-click to wave) and **Ear Defenders** (muffle loud sounds while worn, also in the Curios head slot; volume in the client config).
- Traffic cones stack up to four per block. Sneak-right-click with an empty hand takes one off.

### Changed
- Texts use American English.
- Optional: Curios for the ear defenders.

### Fixed
- 1.0.0 did not start together with Create ("no jar was provided which matched the range" for Registrate). Registrate is no longer embedded, Create already ships it.

## 1.0.0 – Airport Accessories

First release.

### Added
- **Barrier Posts** in industrial iron, andesite, brass, copper and train casing. Connect them with a wrench, recolor the belts with dye.
- **Traffic Cone**.
- **Terminal Seats** in 16 colors. They join into rows and work like Create's seats, including on contraptions.
- **Airfield Signs** with location, direction and mandatory styles. Text from name tags, clipboards or Display Links spans the whole row.
- **Edge Lights** in five colors, switched by redstone.
- **Windsock** that follows the wind direction and strength.
- **Airport Beacon** driven by rotational force, light controlled by redstone, four beacon patterns and a glare when you look into the beam.
- Ponder scenes for barrier posts, airfield signs, edge lights, windsock and airport beacon.
