package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

@Environment(EnvType.CLIENT)
public class Texts {
    public static final Text TITLE = new TranslatableText("thatorthis.gui.choice.title");
    public static final Text DISCARD = new TranslatableText("thatorthis.gui.choice.discard");
    public static final Text DEFAULT = new TranslatableText("thatorthis.gui.choice.default");
    public static final Text DONE = new TranslatableText("thatorthis.gui.choice.done");
    public static final Text DISABLED = new TranslatableText("thatorthis.gui.choice.disabled");
    public static final Text CONFIRM_TITLE = new TranslatableText("thatorthis.gui.confirm.title");
    public static final Text CONFIRM_MESSAGE = new TranslatableText("thatorthis.gui.confirm.message");

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
}
