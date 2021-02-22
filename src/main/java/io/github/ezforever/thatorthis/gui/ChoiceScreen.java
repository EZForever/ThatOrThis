package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

import io.github.ezforever.thatorthis.Configs;

@Environment(EnvType.CLIENT)
public class ChoiceScreen extends Screen {
    private static final Logger LOGGER = LogManager.getFormatterLogger("thatorthis/gui");
    private static final Configs CONFIGS;

    static {
        Configs configs;
        try {
            configs = Configs.getInstance();
        } catch(RuntimeException e) {
            LOGGER.error("Choice screen disabled due to config errors", e);
            configs = null;
        }
        CONFIGS = configs;
    }

    // ---

    private final Screen parentScreen;
    private final Screen confirmScreen;
    private ButtonWidget discardOrDefaultButton;
    private ButtonWidget doneButton;
    private RuleButtonListWidget optionButtons;
    private Configs.Choices shownChoices;
    private boolean dirty = false;

    public ChoiceScreen(Screen parentScreen) {
        super(Texts.TITLE);
        this.parentScreen = parentScreen;
        this.confirmScreen = new ConfirmScreen(
                (boolean result) -> {
                    CONFIGS.choices = shownChoices;
                    CONFIGS.save();
                    setDirty(false);

                    if(result)
                        Objects.requireNonNull(client).scheduleStop();
                    else
                        Objects.requireNonNull(client).openScreen(this.parentScreen);
                },
                Texts.CONFIRM_TITLE,
                Texts.CONFIRM_MESSAGE
        );
    }

    private void setDirty(boolean value) {
        dirty = value;
        discardOrDefaultButton.setMessage(dirty ? Texts.DISCARD : Texts.DEFAULT);
    }

    @Override
    protected void init() {
        super.init();

        optionButtons = new RuleButtonListWidget(this.client, width, height, 32, height - 32, 25, CONFIGS, (RuleButtonWidget button, Configs.Rules.Rule.Option option) -> {
            shownChoices.choices.put(button.rule.id, option.id);
            setDirty(true);
        });
        addChild(optionButtons);

        discardOrDefaultButton = new ButtonWidget(width / 2 - 155, height - 27, 150, 20, Texts.DEFAULT, (ButtonWidget button) -> {
            optionButtons.setChoices(dirty ? CONFIGS.choices : CONFIGS.defaultChoices);
            setDirty(!dirty);
        });
        addButton(discardOrDefaultButton);

        doneButton = new ButtonWidget(width / 2 - 155 + 160, height - 27, 150, 20, Texts.DONE, (ButtonWidget button) -> onClose());
        addButton(doneButton);

        if(CONFIGS == null)
            discardOrDefaultButton.active = false;
        else
            shownChoices = new Configs.Choices(CONFIGS.choices);
    }

    @Override
    public void onClose() {
        Objects.requireNonNull(client).openScreen(dirty ? confirmScreen : parentScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        optionButtons.render(matrices, mouseX, mouseY, delta);
        if(CONFIGS == null)
            drawCenteredText(matrices, textRenderer, Texts.DISABLED, width / 2, height / 2, 0xaaaaaa);
        drawCenteredText(matrices, textRenderer, title, width / 2, 20, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
        optionButtons.getHoveredButton(mouseX, mouseY).ifPresent((RuleButtonWidget button) -> renderOrderedTooltip(matrices, Objects.requireNonNull(client).textRenderer.wrapLines(Texts.getText(button.rule.tooltip), Math.max(width / 2 - 43, 170)), mouseX, mouseY));
    }
}
