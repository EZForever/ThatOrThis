package io.github.ezforever.thatorthis.config.rule;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;

// Root node of `rules.json`
public class Rules implements RuleHolder {
    public final List<Rule> rules;

    public Rules(List<Rule> rules) {
        this.rules = Collections.unmodifiableList(rules);
    }

    // --- Implements RuleHolder

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getScreenTitle() {
        // TODO: Texts.TITLE_SECONDARY = "thatorthis.gui.choice.title.secondary"
        return LiteralText.EMPTY;
    }
}
