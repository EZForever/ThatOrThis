package io.github.ezforever.thatorthis.config.rule;

import java.util.Collections;
import java.util.List;

// Rule with type = "GENERATED" - Leads to another screen filled with individual mods' options
public class GeneratedRule extends VisibleRule {
    // Directories to search mods from
    public final List<String> directories;
    // Default blacklist
    public final List<String> defaults;

    public GeneratedRule(String id, String caption, String tooltip, List<String> directories, List<String> defaults) {
        super(id, caption, tooltip);
        this.directories = Collections.unmodifiableList(directories);
        this.defaults = Collections.unmodifiableList(defaults);
    }
}
