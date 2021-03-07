package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.EnumClassType;
import io.github.ezforever.thatorthis.config.choice.Choice;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

// Base class of all rules
public abstract class Rule {
    // Enum for serialization keys
    public enum Types implements EnumClassType<Rule> {
        NULL(NullRule.class),
        DISABLED(DisabledRule.class),
        DEFINED(DefinedRule.class),
        GENERATED(GeneratedRule.class),
        NESTED(NestedRule.class)
        ;

        // ---

        private final Class<? extends Rule> clazz;

        Types(Class<? extends Rule> clazz) {
            this.clazz = clazz;
        }

        // --- Implements EnumClassType<>

        @Override
        public Class<? extends Rule> getClazz() {
            return clazz;
        }
    }

    // ---

    // Unique identifier of this rule
    public final String id;

    public Rule(String id) {
        this.id = id;
    }

    // Default choice if user's choice is invalid, or Optional.empty() if not applicable
    public Optional<Choice> getDefaultChoice() {
        return Optional.empty();
    }

    // Resolve `choice` under the current rule and put directories and blacklists into `resultMap`
    // Return `true` on success, or `false` for loading defaults
    // XXX: Do automatic Choice type checking and conversion? (Req. two-way generic type?)
    public boolean resolve(Choice choice, Map<String, Set<String>> resultMap) {
        return false;
    }
}
