package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.NestedRuleChoice;

import java.util.*;

// Rule with type = "NESTED" - Leads to another screen filled with specified rules
public class NestedRule extends VisibleRule implements RuleHolder {
    // Rules to show on the new screen
    public final List<Rule> rules;

    public NestedRule(String id, String caption, String tooltip, List<Rule> rules) {
        super(id, caption, tooltip);
        this.rules = Collections.unmodifiableList(rules);
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        // RuleHolder.getDefaultChoices()
        return Optional.of(new NestedRuleChoice(getDefaultChoices()));
    }

    @Override
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        if(!(choice instanceof NestedRuleChoice))
            return false;

        // Call RuleHolder.resolve() to do the rest of the work
        resultMap.putAll(resolve(((NestedRuleChoice)choice).choices));
        return true;
    }

    // --- Implements RuleHolder

    @Override
    public List<Rule> getRules() {
        return rules;
    }
}