package io.github.ezforever.thatorthis.gui;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Texts {
    TITLE("thatorthis.choice.title"),
    SUBTITLE("thatorthis.choice.subtitle"),
    DISCARD("thatorthis.choice.discard"),
    DEFAULT("thatorthis.choice.default"),
    DONE("thatorthis.choice.done"),
    DISABLED_TITLE("thatorthis.choice.disabled.title"),
    DISABLED_MESSAGE("thatorthis.choice.disabled.message"),
    CONFIRM_TITLE("thatorthis.confirm.title"),
    CONFIRM_MESSAGE("thatorthis.confirm.message"),
    GENERATED_FORMAT("thatorthis.generated.format"),
    GENERATED_ON("thatorthis.generated.on"),
    GENERATED_OFF("thatorthis.generated.off"),
    RULE_USAGE_CAPTION("thatorthis.rule.usage.caption"),
    RULE_USAGE_TOOLTIP("thatorthis.rule.usage.caption"),
    RULE_DEFAULT_CAPTION("thatorthis.rule.default.caption"),
    RULE_DEFAULT_TOOLTIP("thatorthis.rule.default.caption")
    ;

    // "abc.def" -> new LiteralText("abc.def")
    // "@abc.def" -> new TranslatableText("abc.def")
    // "@@abc.def" -> new LiteralText("@abc.def")
    // "@@@abc.def" -> new LiteralText("@@abc.def")
    // XXX: Will there be any translation key that starts with "@"?
    public static Text getText(String keyOrLiteral, Object... params) {
        boolean isKey;
        if(keyOrLiteral.startsWith("@")) {
            keyOrLiteral = keyOrLiteral.substring(1);
            isKey = !keyOrLiteral.startsWith("@");
        } else {
            isKey = false;
        }
        return isKey
                ? new TranslatableText(keyOrLiteral, params)
                : new LiteralText(String.format(keyOrLiteral, params));
    }

    // ---

    private final String key;
    private TranslatableText text;

    public Text get() {
        if(text == null)
            text = new TranslatableText(key);
        return text;
    }

    public Text get(Object... args) {
        return new TranslatableText(key, args);
    }

    Texts(String key) {
        this.key = key;
    }
}
