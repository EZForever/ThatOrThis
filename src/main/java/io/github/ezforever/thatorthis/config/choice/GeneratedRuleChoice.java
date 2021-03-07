package io.github.ezforever.thatorthis.config.choice;

import java.util.HashSet;
import java.util.Set;

// Choice for GeneratedRule
public class GeneratedRuleChoice extends Choice {
    // The blacklisted modids
    public final Set<String> choices;

    public GeneratedRuleChoice(Set<String> choices) {
        this.choices = new HashSet<>(choices);
    }

    // --- Extends Choice

    @Override
    public Choice copy() {
        return new GeneratedRuleChoice(choices);
    }
}
