package io.github.ezforever.thatorthis.config.rule;

// Rule with type = "DISABLED" - Visible but read-only, best for as hints
public class DisabledRule extends VisibleRule {
    public DisabledRule(String id, String caption, String tooltip) {
        super(id, caption, tooltip);
    }
}
