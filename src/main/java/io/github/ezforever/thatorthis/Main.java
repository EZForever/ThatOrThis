package io.github.ezforever.thatorthis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Main {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/main");

    static void run() {
        Configs configs;
        try {
            configs = Configs.getInstance();
        } catch(RuntimeException e) {
            LOGGER.error("Additional mods loading skipped due to config errors", e);
            return;
        }

        // Turn rules and options into maps, since orders are useless at load time
        // rule.id -> rule.options, option.id -> option.directories
        Map<String, Map<String, Set<String>>> rulesMap = new HashMap<>();
        for(Configs.Rules.Rule rule : configs.rules.rules) {
            Map<String, Set<String>> optionsMap = new HashMap<>();
            for(Configs.Rules.Rule.Option option : rule.options) {
                optionsMap.put(option.id, new HashSet<>(option.directories));
            }
            if(!optionsMap.isEmpty())
                rulesMap.put(rule.id, optionsMap);
        }

        Set<String> modDirs = new HashSet<>();
        for(String ruleId : rulesMap.keySet()) {
            String optionId = null;
            boolean optionIdValid;

            if(configs.choices.choices.containsKey(ruleId)) {
                optionId = configs.choices.choices.get(ruleId);
                optionIdValid = rulesMap.get(ruleId).containsKey(optionId);
            } else {
                optionIdValid = false;
            }

            if(!optionIdValid) {
                String defaultOptionId = configs.defaultChoices.choices.get(ruleId);
                LOGGER.info("Resetting invalid choice to default: {}({} -> {})",
                        ruleId, optionId, defaultOptionId
                );
                configs.choices.choices.put(ruleId, defaultOptionId);
                configs.save();

                optionId = defaultOptionId;
            }
            modDirs.addAll(rulesMap.get(ruleId).get(optionId));
        }

        String modDirText;
        switch (modDirs.size()) {
            case 0:
                modDirText = "Loading mods from {} additional directories";
                break;
            case 1:
                modDirText = "Loading mods from {} additional directory: {}";
                break;
            default:
                modDirText = "Loading mods from {} additional directories: {}";
                break;
        }

        LOGGER.info("[ThatOrThis] " + modDirText,
                modDirs.size(), String.join(", ", modDirs)
        );

        FabricInternals.hook(modDirs);
    }
}
