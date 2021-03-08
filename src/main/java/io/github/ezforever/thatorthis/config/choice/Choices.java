package io.github.ezforever.thatorthis.config.choice;

// Root node of `choices.json`
// Or, Choices = ChoiceHolder + `disabled` flag
public class Choices {
    public final ChoiceHolder choices;
    public final Boolean disabled;

    public Choices(ChoiceHolder choices, Boolean disabled) {
        this.choices = choices.copy();
        this.disabled = disabled;
    }

    public Choices copy() {
        return new Choices(choices, disabled);
    }
}
