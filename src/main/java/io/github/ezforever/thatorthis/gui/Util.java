package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.AbstractButtonWidget;

@Environment(EnvType.CLIENT)
public class Util {
    // Only needed on 1.16.5 since AbstractButtonWidget#hovered is not exposed
    public static boolean isHovered(AbstractButtonWidget button, double mouseX, double mouseY) {
        return mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();
    }
}
