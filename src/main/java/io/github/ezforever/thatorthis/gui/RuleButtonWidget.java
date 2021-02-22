package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.LiteralText;

import java.util.stream.IntStream;

import io.github.ezforever.thatorthis.Configs;

@Environment(EnvType.CLIENT)
public class RuleButtonWidget extends ButtonWidget {
    @Environment(EnvType.CLIENT)
    public interface PressAction {
        void onPress(RuleButtonWidget button, Configs.Rules.Rule.Option option);
    }

    // ---

    public static RuleButtonWidget create(int x, int y, int width, int height, Configs.Rules.Rule rule, PressAction pressAction) {
        return new RuleButtonWidget(x, y, width, height, rule, (ButtonWidget button) -> {
            RuleButtonWidget ruleButton = (RuleButtonWidget) button;
            ruleButton.cycle();
            pressAction.onPress(ruleButton, ruleButton.getChoice());
        });
    }

    // ---

    public final Configs.Rules.Rule rule;
    private int optionIndex;

    private RuleButtonWidget(int x, int y, int width, int height, Configs.Rules.Rule rule, ButtonWidget.PressAction pressAction) {
        super(x, y, width, height, LiteralText.EMPTY, pressAction);
        this.rule = rule;

        // Rules with exactly one option are enforced
        active = rule.options.size() > 1;
        // Rules with no options are placeholders
        if(rule.options.isEmpty()) {
            visible = false;
            optionIndex = -1;
        } else {
            optionIndex = 0;
            setFormattedMessage();
        }
    }

    public void setChoice(String optionId) {
        if(optionIndex < 0)
            return;

        optionIndex = IntStream.range(0, rule.options.size())
                .filter((int idx) -> rule.options.get(idx).id.equals(optionId))
                .findFirst().orElse(0);
        setFormattedMessage();
    }

    private void cycle() {
        if(++optionIndex >= rule.options.size())
            optionIndex = 0;
        setFormattedMessage();
    }

    private Configs.Rules.Rule.Option getChoice() {
        return rule.options.get(optionIndex);
    }

    private void setFormattedMessage() {
        setMessage(Texts.getText(rule.caption, Texts.getText(getChoice().caption).asString()));
    }
}
