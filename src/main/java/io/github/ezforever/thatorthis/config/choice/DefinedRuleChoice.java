package io.github.ezforever.thatorthis.config.choice;

import com.google.gson.annotations.Expose;

// Choice for DefinedRule
public class DefinedRuleChoice extends Choice {
    @Expose // User's choice as DefinedRule$Option.id
    public String choice;

    public DefinedRuleChoice(String choice) {
        this.choice = choice;
    }
}
