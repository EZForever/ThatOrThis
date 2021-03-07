package io.github.ezforever.thatorthis.config.choice;

import io.github.ezforever.thatorthis.config.EnumClassType;

// Base class of all choices
public abstract class Choice {
    // Enum for serialization keys
    public enum Types implements EnumClassType<Choice> {
        DEFINED(DefinedRuleChoice.class),
        GENERATED(GeneratedRuleChoice.class),
        NESTED(NestedRuleChoice.class)
        ;

        // ---

        private final Class<? extends Choice> clazz;

        Types(Class<? extends Choice> clazz) {
            this.clazz = clazz;
        }

        // --- Implements EnumClassType<>

        @Override
        public Class<? extends Choice> getClazz() {
            return clazz;
        }
    }

    // ---

    public Choice() {
        // Nothing
    }

    // Deep copy the choice
    public abstract Choice copy();
}
