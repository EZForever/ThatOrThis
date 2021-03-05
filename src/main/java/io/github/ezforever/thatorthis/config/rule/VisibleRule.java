package io.github.ezforever.thatorthis.config.rule;

// Base class of all "visible" rules (which has a button on the choices screen)
public abstract class VisibleRule extends Rule {
    // Button caption
    public final String caption;
    // Button tooltip
    public final String tooltip;

    public VisibleRule(String id, String caption, String tooltip) {
        super(id);
        this.caption = caption;
        this.tooltip = tooltip;
    }
}
