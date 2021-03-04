package io.github.ezforever.thatorthis.config.choice;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

// Choice for GeneratedRule
public class GeneratedRuleChoice extends Choice {
    @Expose // The blacklisted modids
    public final List<String> choices;

    public GeneratedRuleChoice(List<String> choices) {
        this.choices = new ArrayList<>(choices);
    }
}
