package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.DefinedRuleChoice;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Rule with type = "DEFINED" - options set by modpack developer
public class DefinedRule extends VisibleRule {
    // Option of a DefinedRule
    public static class Option {
        // Unique identifier
        public final String id;
        // Format argument for button's caption
        public final String caption;
        // Directories this option corresponds to
        public final Set<String> directories;
        // Is this option the default option?
        public final Boolean isDefault;

        public Option(String id, String caption, Set<String> directories, Boolean isDefault) {
            this.id = id;
            this.caption = caption;
            this.directories = Collections.unmodifiableSet(directories);
            this.isDefault = isDefault;
        }
    }

    // ---

    // The list of options
    public final List<Option> options;

    private transient final Map<String, Option> optionMap;

    public DefinedRule(String id, String caption, String tooltip, List<Option> options) {
        super(id, caption, tooltip);
        this.options = Collections.unmodifiableList(options);
        this.optionMap = Collections.unmodifiableMap(options.stream()
                .collect(Collectors.toMap((Option option) -> option.id, (Option option) -> option)));
    }

    // --- Extends VisibleRule -> Rule

    @Override
    public Optional<Choice> getDefaultChoice() {
        Optional<Option> defaultOption = Stream.concat(
                options.stream().filter((Option option) -> option.isDefault != null && option.isDefault),
                options.stream()
        ).findFirst();
        return defaultOption.map((Option option) -> new DefinedRuleChoice(option.id));
    }

    @Override
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        if(!(choice instanceof DefinedRuleChoice))
            return false;

        String optionId = ((DefinedRuleChoice)choice).choice;
        if(optionMap.containsKey(optionId)) {
            optionMap.get(optionId).directories.forEach((String dir) -> resultMap.put(dir, null));
            return true;
        } else {
            return false;
        }
    }
}
