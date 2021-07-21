package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Util {
    public static void renderWarpedTooltip(Screen screen, MatrixStack matrices, Text text, int mouseX, int mouseY) {
        screen.renderOrderedTooltip(
                matrices,
                MinecraftClient.getInstance().textRenderer.wrapLines(text, Math.max(screen.width / 2 - 43, 170)),
                mouseX, mouseY
        );
    }
}
