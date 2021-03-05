package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.GeneratedRuleChoice;

import java.util.*;

// Rule with type = "GENERATED" - Leads to another screen filled with individual mods' options
public class GeneratedRule extends VisibleRule {
    // Directories to search mods from
    public final Set<String> directories;
    // Default blacklist
    public final Set<String> defaults;

    public GeneratedRule(String id, String caption, String tooltip, Set<String> directories, Set<String> defaults) {
        super(id, caption, tooltip);
        this.directories = Collections.unmodifiableSet(directories);
        this.defaults = Collections.unmodifiableSet(defaults);
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
}