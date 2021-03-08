package io.github.ezforever.thatorthis.config.choice;

import java.util.HashSet;
import java.util.Set;

// Choice for GeneratedRule
public class GeneratedRuleChoice extends Choice {
    // The blacklisted modids
    public final Set<String> choices;
    // Whether this list of `Rule`s is disabled
    public final Boolean disabled;

    public GeneratedRuleChoice(Set<String> choices, Boolean disabled) {
        this.choices = new HashSet<>(choices);
        this.disabled = disabled;
    }

    // --- Extends Choice

    @Override
    public Choice copy() {
        return new GeneratedRuleChoice(choices, disabled);
    }
}
