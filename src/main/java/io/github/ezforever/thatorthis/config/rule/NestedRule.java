package io.github.ezforever.thatorthis.config.rule;

import com.google.gson.annotations.Expose;
import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.NestedRuleChoice;

import java.util.*;

// Rule with type = "NESTED" - Leads to another screen filled with specified rules
public class NestedRule extends VisibleRule {
    @Expose // Rules to show on the new screen
    public final List<Rule> rules;

    public NestedRule(String id, String caption, String tooltip, List<Rule> rules) {
        super(id, caption, tooltip);
        this.rules = Collections.unmodifiableList(rules);
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        Map<String, Choice> defaultChoicesMap = new HashMap<>();
        for(Rule rule : rules)
            rule.getDefaultChoice().ifPresent((Choice choice) -> defaultChoicesMap.put(rule.id, choice));
        return Optional.of(new NestedRuleChoice(defaultChoicesMap));
    }
}