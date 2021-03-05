package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.NestedRuleChoice;

import java.util.*;

// Rule with type = "NESTED" - Leads to another screen filled with specified rules
public class NestedRule extends VisibleRule {
    // Rules to show on the new screen
    public final List<Rule> rules;

    public NestedRule(String id, String caption, String tooltip, List<Rule> rules) {
        super(id, caption, tooltip);
        this.rules = Collections.unmodifiableList(rules);
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        Map<String, Choice> defaultChoicesMap = new HashMap<>();
        rules.forEach((Rule rule) -> rule.getDefaultChoice()
                .ifPresent((Choice choice) -> defaultChoicesMap.put(rule.id, choice)));
        return Optional.of(new NestedRuleChoice(defaultChoicesMap));
    }

    @Override
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        if(!(choice instanceof NestedRuleChoice))
            return false;

        // For not polluting resultMap if resolve() fails halfway
        Map<String, Set<String>> nestedResultMap = new HashMap<>();
        for(Rule rule : rules) {
            // This logic is much simpler (and buggier) than Config.resolve():
            //  If choice is not found, assume it's a NULL rule and continue silently
            //  Any invalid choice will trigger a full reset on all choices
            // FIXME: Ideally code from Config.resolve() can be copied here but maybe a RuleHolder interface will be better?
            Choice nestedChoice = ((NestedRuleChoice) choice).choices.get(rule.id);
            if(nestedChoice != null && !rule.resolve(nestedChoice, nestedResultMap))
                return false;
        }
        resultMap.putAll(nestedResultMap);
        return true;
    }
}