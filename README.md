# ThatOrThis

*A Fabric mod for choosing between sets of Fabric mods.*

This branch (`master`) is the active development branch for latest Minecraft (currently 1.17).

**NOTE: v0.2.0 introduces breaking changes regarding config file schemas; see [migration guide][migration-guide] for details.**

## What is this

*ThatOrThis* is designed mainly for modpack developers. It hacks into Fabric Loader to allow loading mods from additional user-selectable directories. This allows the end user to choose which mod(s) to load into their modpack, or workaround incompatible mod sets.

Technically speaking, ThatOrThis take its inspiration from [Modsmod][modsmod] and [GrossFabricHacks][grossfabrichacks], which abuses the `LanguageAdapter` feature of the Fabric Loader. This allows part of this mod's code to run *even before Fabric Loader starts to initialize most other mods*. It then read configs, resolve for additional mods to load, and inject them into the loader via Java reflection wizardry.

## Features

- Load mods from directories other than the default `.minecraft/mods`
- Let the end user to choose which feature(s) they want (e.g. OptiFabric for shaders or Sodium for optimization)
- Runtime check for dependencies and conflicts of additional mods
- Configurable settings screen accessible via ModMenu integration
- Option names and tooltips support formatting codes and translation keys

## Usage

### For ordinary users (who installed this mod manually)

Install this mod into your mods folder, along with [ModMenu][modmenu] and [Fabric API][fabric-api].

Launch Minecraft and open the settings screen via ModMenu. Follow the instructions.

ThatOrThis will be running under the default (limited) rule set if configured this way. If you need features more than toggling individual mods, please refer to the "modpack developers" section.

### For modpack users (who downloaded a modpack containing this mod)

Open the settings screen via ModMenu. Change the settings as you desire. Done.

Note that the new settings won't come into effect until you re-launch the game.

### For modpack developers (who wish to integrate this mod into a modpack)

Install this mod into your mods folder, along with [ModMenu][modmenu] and [Fabric API][fabric-api].

Then, create `thatorthis` folder in your config folder, and file `rules.json` in that. If done correctly, the path for it should be `.minecraft/config/thatorthis/rules.json`.

`rules.json` defines all the available settings and their options. Specs for it can be found [here][rules_example_json5].

Finally, put the mods into subdirectories of the mods folder, as defined in the rule file.

This mod is open-sourced and [MIT licensed][mit], meaning that you can include it into your modpack and redistribute it without asking for permission, as long as you credit me (and of course the authors of all other mods) in your modpack.

## Compatibility

- ThatOrThis **requires** a recent version of Minecraft and Fabric Loader to work. 
	- **Forge support is never planned; use [ModDirector][moddirector] instead.**
- ModMenu and Fabric API are **optional**, meaning that ThatOrThis will work correctly without them given valid `rules.json` and `choices.json`. 
	- However they are required for the in-game settings screen. You can load them via ThatOrThis, but it is not recommended.
- Loading programming language support mods (e.g. for [Kotlin][fabric-language-kotlin], [Scala][fabric-language-scala] and [Grovvy][fabric-language-groovy]) via ThatOrThis is **not recommended**.
	- They might work, but this feature has not been tested; place them right into the mods directory whenever possible.
- Modsmod does the *exact same thing* as ThatOrThis to the Fabric Loader, thus **incompatible** with each other.

NOTE: ThatOrThis is only **intended for small, client-side mods**. It may cause crashes and/or server/world incompatibilities if rules and options are not designed carefully. Test your modpack throughly before and after installing this mod.

[migration-guide]: https://github.com/EZForever/ThatOrThis/releases/tag/v0.2.0#user-content-v0.2.0-migration-guide
[modsmod]: https://www.modrinth.com/mod/modsmod
[grossfabrichacks]: https://github.com/Devan-Kerman/GrossFabricHacks
[modmenu]: https://modrinth.com/mod/modmenu
[fabric-api]: https://modrinth.com/mod/fabric-api
[rules_example_json5]: https://github.com/EZForever/ThatOrThis/blob/master/rules.example.json5
[mit]: https://github.com/EZForever/ThatOrThis/blob/master/LICENSE.txt
[moddirector]: https://www.curseforge.com/minecraft/mc-mods/moddirector
[fabric-language-kotlin]: https://www.curseforge.com/minecraft/mc-mods/fabric-language-kotlin
[fabric-language-scala]: https://www.curseforge.com/minecraft/mc-mods/fabric-language-scala
[fabric-language-groovy]: https://www.curseforge.com/minecraft/mc-mods/fabric-language-groovy

