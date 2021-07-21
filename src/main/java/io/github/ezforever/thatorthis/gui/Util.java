package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Util {
    public static boolean isHovered(ClickableWidget button, double mouseX, double mouseY) {
        return button.visible && mouseX >= button.x && mouseY >= button.y && mouseX < button.x + button.getWidth() && mouseY < button.y + button.getHeight();
    }

    public static void renderWarpedTooltip(Screen screen, MatrixStack matrices, Text text, int mouseX, int mouseY) {
        screen.renderOrderedTooltip(
                matrices,
                MinecraftClient.getInstance().textRenderer.wrapLines(text, Math.max(screen.width / 2 - 43, 170)),
                mouseX, mouseY
        );
    }
}
