package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.gui.Texts;
import io.github.ezforever.thatorthis.gui.future.RuleButtonWidget;
import io.github.ezforever.thatorthis.gui.future.SingleThreadFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

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

    // Initialize a button (active, initial caption, etc,)
    @Environment(EnvType.CLIENT)
    public void initButton(RuleButtonWidget button) {
        // Empty
    }

    // Get the caption corresponding to the choice
    @Environment(EnvType.CLIENT)
    public Text getButtonCaption(Choice choice) {
        return Texts.getText(caption);
    }

    // Get the tooltip corresponding to the choice
    @Environment(EnvType.CLIENT)
    public Text getButtonTooltip(Choice choice) {
        return Texts.getText(tooltip);
    }

    // prevChoice + updateChoice() = newChoice
    // NOTE: Nested rules might "return later" on their results, thus the Future
    @Environment(EnvType.CLIENT)
    public SingleThreadFuture<Choice> updateChoice(Choice prevChoice) {
        throw new UnsupportedOperationException();
    }
}
