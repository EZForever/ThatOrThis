### TODO

- Show the number of filtered out mods? / Show them in debug log?
- Replace "@xxx" hack with JSON (`Text net.minecraft.text.Text.Serializer.fromJson(String)`) (Sure?)
- Set default language file to `en_us.json` (How?)
- Mark each additional mod's parent as ThatOrThis for simplifying the mod menu (Set CustomData "modmenu:parent" to a fake mod; need more reflection hacks)
- Refactor `RuleHolder` to be like a container, similar to `ChoiceHolder`? (Good for consistency but otherwise unnecessary)

[modmenu_parent]: https://github.com/TerraformersMC/ModMenu/wiki/API#parents
[modmenu_read_routine]: https://github.com/TerraformersMC/ModMenu/blob/v1.16.8/src/main/java/com/terraformersmc/modmenu/util/mod/fabric/FabricMod.java#L45-L85

