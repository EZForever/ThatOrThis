package io.github.ezforever.thatorthis.config.choice;

import java.util.ArrayList;
import java.util.List;

// Choice for GeneratedRule
public class GeneratedRuleChoice extends Choice {
    // The blacklisted modids
    public final List<String> choices;

    public GeneratedRuleChoice(List<String> choices) {
        this.choices = new ArrayList<>(choices);
    }
}
