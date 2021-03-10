package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.Choices;
import io.github.ezforever.thatorthis.config.choice.NestedRuleChoice;
import io.github.ezforever.thatorthis.gui.SingleThreadFuture;
import io.github.ezforever.thatorthis.gui.Texts;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.*;

// Rule with type = "NESTED" - Leads to another screen filled with specified rules
public class NestedRule extends VisibleRule implements RuleHolder {
    // Rules to show on the new screen
    public final List<Rule> rules;
    // Whether this list of `Rule`s can be disabled
    public final Boolean canDisable;

    public NestedRule(String id, String caption, String tooltip, List<Rule> rules, Boolean canDisable) {
        super(id, caption, tooltip);
        this.rules = Collections.unmodifiableList(rules);
        this.canDisable = canDisable;
    }

    // --- Extends VisibleRule

    @Override
    @Environment(EnvType.CLIENT)
    public SingleThreadFuture<Choice> updateChoice(Choice prevChoice) {
        NestedRuleChoice realPrevChoice = (NestedRuleChoice)prevChoice;
        return showNestedScreen(new Choices(realPrevChoice.choices, realPrevChoice.disabled))
                .then((Choices choices) -> new NestedRuleChoice(choices.choices, choices.disabled));
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        // RuleHolder.getDefaultChoices()
        return Optional.of(new NestedRuleChoice(getDefaultChoices(), false));
    }

    @Override
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        if(!(choice instanceof NestedRuleChoice))
            return false;

        // Call RuleHolder.resolve() to do the rest of the work
        NestedRuleChoice realChoice = (NestedRuleChoice)choice;
        if(canDisable() && realChoice.disabled != null && realChoice.disabled)
            LOGGER.debug("Mods under rule {} are skipped as per user request", id);
        else
            resultMap.putAll(resolve(realChoice.choices));
        return true;
    }

    // --- Implements RuleHolder

    @Override
    public boolean canDisable() {
        // Nested rules needs opt-in to disable
        return canDisable != null && canDisable;
    }

    @Override
    public List<Rule> getRules() {
        return rules;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getScreenTitle() {
        return Texts.getText(caption);
    }
}