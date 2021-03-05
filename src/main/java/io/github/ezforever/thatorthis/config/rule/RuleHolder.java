package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

// Anything that can hold a list of `Rule`s
// Used for unifying Rules and NestedRule in many operations
// Also for GeneratedRule for generating fake rules
public interface RuleHolder {
    Logger LOGGER = LogManager.getLogger("thatorthis/config");

    // Make sure we have a list of `Rule`s
    List<Rule> getRules();

    default // "RuleID -> Choice" map
    Map<String, Choice> getDefaultChoices() {
        // XXX: `transient` caching?
        Map<String, Choice> defaultChoicesMap = new HashMap<>();
        getRules().forEach((Rule rule) -> rule.getDefaultChoice()
                .ifPresent((Choice choice) -> defaultChoicesMap.put(rule.id, choice)));
        return defaultChoicesMap;
    }

    default // Resolve choices to "ModID -> Blacklist" map
    Map<String, Set<String>> resolve(Map<String, Choice> choices) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        for(Rule rule : getRules()) {
            Choice choice = choices.get(rule.id);
            if(choice == null || !rule.resolve(choice, resultMap)) {
                // Choice not found. Maybe a NULL rule (no default choice either), or a modpack update
                Optional<Choice> defaultChoice = rule.getDefaultChoice();
                if(defaultChoice.isPresent()) {
                    LOGGER.warn("Resetting invalid choice of rule {} to default", rule.id);
                    choice = defaultChoice.get();
                    if (rule.resolve(choice, resultMap))
                        choices.put(rule.id, choice);
                    else
                        LOGGER.error("Default choice of rule {} is invalid! Skipping", rule.id);
                }
            }
        }
        return Collections.unmodifiableMap(resultMap);
    }

    // TODO: Screen preparation
}
