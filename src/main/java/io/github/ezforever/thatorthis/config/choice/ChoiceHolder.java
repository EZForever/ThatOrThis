package io.github.ezforever.thatorthis.config.choice;

import java.util.HashMap;
import java.util.Map;

public class ChoiceHolder extends HashMap<String, Choice> {
    public ChoiceHolder() {
        super();
    }

    public ChoiceHolder(Map<String, Choice> choices) {
        super();
        choices.forEach((String ruleId, Choice choice) -> put(ruleId, choice.copy()));
    }

    public ChoiceHolder copy() {
        return new ChoiceHolder(this);
    }
}
