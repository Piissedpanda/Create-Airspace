# Changelog

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
