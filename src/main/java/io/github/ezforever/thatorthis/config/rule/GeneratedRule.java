package io.github.ezforever.thatorthis.config.rule;

import com.google.gson.annotations.Expose;
import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.GeneratedRuleChoice;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

// Rule with type = "GENERATED" - Leads to another screen filled with individual mods' options
public class GeneratedRule extends VisibleRule {
    @Expose // Directories to search mods from
    public final List<String> directories;
    @Expose // Default blacklist
    public final List<String> defaults;

    public GeneratedRule(String id, String caption, String tooltip, List<String> directories, List<String> defaults) {
        super(id, caption, tooltip);
        this.directories = Collections.unmodifiableList(directories);
        this.defaults = Collections.unmodifiableList(defaults);
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        return Optional.of(new GeneratedRuleChoice(defaults));
    }
}