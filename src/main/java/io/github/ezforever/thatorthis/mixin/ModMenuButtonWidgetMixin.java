package io.github.ezforever.thatorthis.mixin;

import com.terraformersmc.modmenu.gui.widget.ModMenuButtonWidget;
import io.github.ezforever.thatorthis.gui.ChoiceScreen;
import io.github.ezforever.thatorthis.gui.Texts;
import io.github.ezforever.thatorthis.gui.Util;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;

import java.util.Optional;

@Environment(EnvType.CLIENT)
@Mixin(ModMenuButtonWidget.class)
public abstract class ModMenuButtonWidgetMixin extends ButtonWidget {
    public ModMenuButtonWidgetMixin(int x, int y, int width, int height, Text message, PressAction onPress) {
        super(x, y, width, height, message, onPress);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button))
            return true;

        if(clicked(mouseX, mouseY) && button == 1) {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            playDownSound(minecraftClient.getSoundManager());
            minecraftClient.setScreen(ChoiceScreen.create(minecraftClient.currentScreen));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void renderTooltip(MatrixStack matrices, int mouseX, int mouseY) {
        if(!active)
            return;
        Optional.ofNullable(MinecraftClient.getInstance().currentScreen)
                .ifPresent((Screen currentScreen) -> Util.renderWarpedTooltip(currentScreen, matrices, Texts.MODMENU_TOOLTIP.get(), mouseX, mouseY));
    }
}
