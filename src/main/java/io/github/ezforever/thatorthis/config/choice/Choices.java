package io.github.ezforever.thatorthis.config.choice;

// Root node of `choices.json`
public class Choices {
    public final ChoiceHolder choices;

    public Choices(ChoiceHolder choices) {
        this.choices = choices.copy();
    }
}
