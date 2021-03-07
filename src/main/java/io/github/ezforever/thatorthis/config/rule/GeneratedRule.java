package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.choice.GeneratedRuleChoice;
import io.github.ezforever.thatorthis.gui.Texts;
import io.github.ezforever.thatorthis.gui.future.SingleThreadFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.Text;

import java.util.*;

// Rule with type = "GENERATED" - Leads to another screen filled with individual mods' options
public class GeneratedRule extends VisibleRule implements RuleHolder {
    // Directories to search mods from
    public final Set<String> directories;
    // Default blacklist
    public final Set<String> defaults;

    public GeneratedRule(String id, String caption, String tooltip, Set<String> directories, Set<String> defaults) {
        super(id, caption, tooltip);
        this.directories = Collections.unmodifiableSet(directories);
        this.defaults = Collections.unmodifiableSet(defaults);
    }

    // --- Extends VisibleRule

    @Override
    @Environment(EnvType.CLIENT)
    public SingleThreadFuture<Choice> updateChoice(Choice prevChoice) {
        // TODO: The nested screen
        return new SingleThreadFuture<>(prevChoice);
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        return Optional.of(new GeneratedRuleChoice(defaults));
    }

    @Override
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        if(!(choice instanceof GeneratedRuleChoice))
            return false;

        directories.forEach((String dir) -> resultMap.put(dir, ((GeneratedRuleChoice)choice).choices));
        return true;
    }

    // --- Implements RuleHolder

    @Override
    public List<Rule> getRules() {
        // TODO: Generate rule list with `transient` cache
        return null;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public Text getScreenTitle() {
        return Texts.getText(caption);
    }

    @Override
    public ChoiceHolder getDefaultChoices() {
        // TODO: Generate choices with `transient` cache
        // getDefaultChoice() does not do runtime check since it faces config structure,
        // while getDefaultChoices faces GUI
        return null;
    }

    // Resolving GeneratedRuleChoice does not involve nested rules
    @Override
    public Map<String, Set<String>> resolve(ChoiceHolder choices) {
        throw new UnsupportedOperationException();
    }
}