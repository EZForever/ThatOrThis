package io.github.ezforever.thatorthis.config.choice;

// Choice for NestedRule
public class NestedRuleChoice extends Choice {
    // Choices of all nested rules
    public final ChoiceHolder choices;

    public NestedRuleChoice(ChoiceHolder choices) {
        this.choices = choices.copy();
    }

    // --- Extends Choice

    @Override
    public Choice copy() {
        return new NestedRuleChoice(choices);
    }
}
