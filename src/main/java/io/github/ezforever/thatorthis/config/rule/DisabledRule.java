package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.gui.RuleButtonWidget;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

// Rule with type = "DISABLED" - Visible but read-only, best for as hints
public class DisabledRule extends VisibleRule {
    public DisabledRule(String id, String caption, String tooltip) {
        super(id, caption, tooltip);
    }

    // --- Extends VisibleRule

    @Override
    @Environment(EnvType.CLIENT)
    public void initButton(RuleButtonWidget button) {
        super.initButton(button);
        button.active = false;
        button.setMessage(getButtonCaption(null));
    }
}
