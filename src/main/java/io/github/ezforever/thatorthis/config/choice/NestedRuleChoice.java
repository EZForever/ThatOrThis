package io.github.ezforever.thatorthis.config.choice;

// Choice for NestedRule
public class NestedRuleChoice extends Choice {
    // Choices of all nested rules
    public final ChoiceHolder choices;
    // Whether this list of `Rule`s is disabled
    public final Boolean disabled;

    public NestedRuleChoice(ChoiceHolder choices, Boolean disabled) {
        this.choices = choices.copy();
        this.disabled = disabled;
    }

    // --- Extends Choice

    @Override
    public Choice copy() {
        return new NestedRuleChoice(choices, disabled);
    }
}
