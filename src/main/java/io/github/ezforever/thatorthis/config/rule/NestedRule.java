package io.github.ezforever.thatorthis.config.rule;

import java.util.Collections;
import java.util.List;

// Rule with type = "NESTED" - Leads to another screen filled with specified rules
public class NestedRule extends VisibleRule {
    // Rules to show on the new screen
    public final List<Rule> rules;

    public NestedRule(String id, String caption, String tooltip, List<Rule> rules) {
        super(id, caption, tooltip);
        this.rules = Collections.unmodifiableList(rules);
    }
}
