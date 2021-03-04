package io.github.ezforever.thatorthis.config.rule;

import com.google.gson.annotations.Expose;
import io.github.ezforever.thatorthis.config.EnumClassType;
import io.github.ezforever.thatorthis.config.choice.Choice;

import java.util.Optional;

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

    @Expose // Unique identifier of this rule
    public final String id;

    public Rule(String id) {
        this.id = id;
    }

    public Optional<Choice> getDefaultChoice() {
        return Optional.empty();
    }
}
