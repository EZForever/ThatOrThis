package io.github.ezforever.thatorthis.gui.future;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.rule.VisibleRule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class RuleButtonWidget extends ButtonWidget {
    private final VisibleRule rule;
    private Choice choice;

    public RuleButtonWidget(int x, int y, int width, int height, VisibleRule rule, RuleButtonListWidget.UpdateAction updateAction) {
        super(x, y, width, height, LiteralText.EMPTY, (ButtonWidget button) -> {
            RuleButtonWidget self = (RuleButtonWidget)button;
            SingleThreadFuture<Choice> newChoice = rule.updateChoice(self.choice)
                    .then(self::setChoice);
            updateAction.onUpdate(rule, newChoice);
        });

        this.rule = rule;
        rule.initButton(this);
    }

    public void setChoice(Choice choice) {
        this.choice = choice;
        setMessage(rule.getButtonCaption(choice));
    }

    public Text getTooltip() {
        return rule.getButtonTooltip(choice);
    }
}
