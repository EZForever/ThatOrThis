package io.github.ezforever.thatorthis.gui;

import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public enum Texts {
    TITLE("thatorthis.gui.choice.title"),
    SUBTITLE("thatorthis.gui.choice.subtitle"),
    DISCARD("thatorthis.gui.choice.discard"),
    DEFAULT("thatorthis.gui.choice.default"),
    DONE("thatorthis.gui.choice.done"),
    DISABLED_TITLE("thatorthis.gui.choice.disabled.title"),
    DISABLED_MESSAGE("thatorthis.gui.choice.disabled.message"),
    CONFIRM_TITLE("thatorthis.gui.confirm.title"),
    CONFIRM_MESSAGE("thatorthis.gui.confirm.message"),
    GENERATED_FORMAT("thatorthis.gui.generated.format"),
    GENERATED_ON("thatorthis.gui.generated.on"),
    GENERATED_OFF("thatorthis.gui.generated.off")
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
