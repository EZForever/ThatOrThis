package io.github.ezforever.thatorthis.config.rule;

import java.util.Collections;
import java.util.List;

// Root node of `rules.json`
public class Rules {
    public final List<Rule> rules;

    public Rules(List<Rule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    }
}
