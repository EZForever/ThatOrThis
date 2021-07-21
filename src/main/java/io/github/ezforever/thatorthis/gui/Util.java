package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ClickableWidget;

@Environment(EnvType.CLIENT)
public class Util {
    public static boolean isHovered(ClickableWidget button, double mouseX, double mouseY) {
        return mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();
    }
}
