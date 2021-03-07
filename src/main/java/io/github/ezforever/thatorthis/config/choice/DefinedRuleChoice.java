package io.github.ezforever.thatorthis.config.choice;

// Choice for DefinedRule
public class DefinedRuleChoice extends Choice {
    // User's choice as DefinedRule$Option.id
    public String choice;

    public DefinedRuleChoice(String choice) {
        this.choice = choice;
    }

    // --- Extends Choice

    @Override
    public Choice copy() {
        return new DefinedRuleChoice(choice);
    }
}
