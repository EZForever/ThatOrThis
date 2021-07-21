package io.github.ezforever.thatorthis.gui;

import io.github.ezforever.thatorthis.config.Config;
import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.choice.Choices;
import io.github.ezforever.thatorthis.config.rule.Rule;
import io.github.ezforever.thatorthis.config.rule.RuleHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.LockButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Objects;
import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public class ChoiceScreen extends Screen {
    private static final Logger LOGGER = LogManager.getLogger("thatorthis/gui");

    public static Screen create(Screen parent) {
        Config config;
        try {
            config = Config.getInstance();
        } catch(RuntimeException e) {
            LOGGER.error("Choice screen disabled due to config errors", e);
            return new ConfirmScreen(
                    (boolean result) -> {
                        if(result) {
                            File logsDir = FabricLoader.getInstance()
                                    .getGameDir().resolve("logs")
                                    .toFile();
                            net.minecraft.util.Util.getOperatingSystem().open(logsDir);
                        }
                        MinecraftClient.getInstance().openScreen(parent);
                    },
                    Texts.DISABLED_TITLE.get(),
                    Texts.DISABLED_MESSAGE.get()
            );
        }

        return new ChoiceScreen(parent, config.rules, config.choices, (Choices choices, Screen parentScreen) -> {
            config.choices = choices; //choices.copy();
            config.save();

            return new ConfirmScreen(
                    (boolean result) -> {
                        MinecraftClient minecraftClient = MinecraftClient.getInstance();
                        if(result)
                            minecraftClient.scheduleStop();
                        else
                            minecraftClient.openScreen(parentScreen);
                    },
                    Texts.CONFIRM_TITLE.get(),
                    Texts.CONFIRM_MESSAGE.get()
            );
        });
    }

    // ---

    private final Screen parent;
    private final RuleHolder ruleHolder;
    private final Choices initialChoices;
    private final BiFunction<Choices, Screen, Screen> callback;
    private ChoiceHolder shownChoices;
    private RuleButtonListWidget ruleButtons;
    private ButtonWidget discardOrDefaultButton;
    private LockButtonWidget disableButton;
    private ButtonWidget doneButton;
    private boolean dirty = false;

    public ChoiceScreen(Screen parent,
                        RuleHolder ruleHolder, Choices initialChoices,
                        BiFunction<Choices, Screen, Screen> callback) {
        super(ruleHolder.getScreenTitle());

        this.parent = parent;
        this.ruleHolder = ruleHolder;
        this.initialChoices = initialChoices.copy();
        this.callback = callback;

        this.shownChoices = initialChoices.choices.copy();
    }

    private void setDirty(boolean value) {
        dirty = value;
        discardOrDefaultButton.setMessage((dirty ? Texts.DISCARD : Texts.DEFAULT).get());
    }

    private void renderWarpedTooltip(MatrixStack matrices, Text text, int mouseX, int mouseY) {
        renderOrderedTooltip(
                matrices,
                Objects.requireNonNull(client).textRenderer
                        .wrapLines(text, Math.max(width / 2 - 43, 170)),
                mouseX, mouseY
        );
    }

    @Override
    protected void init() {
        super.init();

        ruleButtons = new RuleButtonListWidget(this.client,
                width, height, 32, height - 32, 25,
                ruleHolder,
                (Rule rule, SingleThreadFuture<Choice> choice) ->
                        choice.then((Choice result) -> {
                            shownChoices.put(rule.id, result);
                            if(!dirty)
                                setDirty(true);
                        })
        );
        ruleButtons.setChoices(shownChoices);
        addChild(ruleButtons);

        discardOrDefaultButton = new ButtonWidget(
                width / 2 - 155, height - 27, 150 - 20, 20,
                LiteralText.EMPTY,
                (ButtonWidget button) -> {
                    shownChoices = (dirty ? initialChoices.choices : ruleHolder.getDefaultChoices()).copy();
                    ruleButtons.setChoices(shownChoices);
                    disableButton.setLocked(dirty && ruleHolder.canDisable() && initialChoices.disabled != null && initialChoices.disabled);
                    setDirty(!dirty);
                }
        );
        setDirty(dirty); // Reset button caption
        addButton(discardOrDefaultButton);

        disableButton = new LockButtonWidget(
                width / 2 - 155 + 150 - 20, height - 27,
                (ButtonWidget button) -> {
                    LockButtonWidget self = (LockButtonWidget)button;
                    self.setLocked(!self.isLocked());
                    if(!dirty)
                        setDirty(true);
                }
        ) {
            @Override // Erase difficulty info in narration message
            protected MutableText getNarrationMessage() {
                return new TranslatableText("gui.narrate.button", getMessage());
            }

            @Override // Sync "locked"/"disabled" status to the buttons list
            public void setLocked(boolean locked) {
                super.setLocked(locked);
                ruleButtons.setDisabled(locked);
            }
        };
        disableButton.setMessage(Texts.LOCK.get());
        disableButton.setLocked(ruleHolder.canDisable() && initialChoices.disabled != null && initialChoices.disabled);
        disableButton.active = ruleHolder.canDisable();
        addButton(disableButton);

        doneButton = new ButtonWidget(width / 2 - 155 + 160, height - 27, 150, 20, Texts.DONE.get(), (ButtonWidget button) -> onClose());
        addButton(doneButton);
    }

    @Override
    public void onClose() {
        Screen nextScreen;
        if(dirty) {
            setDirty(false);
            nextScreen = callback.apply(new Choices(shownChoices, disableButton.isLocked()), parent);
        } else {
            nextScreen = parent;
        }
        Objects.requireNonNull(client).openScreen(nextScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ruleButtons.render(matrices, mouseX, mouseY, delta);
        super.render(matrices, mouseX, mouseY, delta);

        drawCenteredText(matrices, textRenderer, Texts.TITLE.get(), width / 2, 7, 0xffffff);
        drawCenteredText(matrices, textRenderer, title, width / 2, 20, 0xffffff);

        ruleButtons.getHoveredButton(mouseX, mouseY)
                .ifPresent((RuleButtonWidget button) -> Util.renderWarpedTooltip(this, matrices, button.getTooltip(), mouseX, mouseY));

        if(Util.isHovered(disableButton, mouseX, mouseY) && ruleHolder.canDisable()) {
            Text tooltip = (disableButton.isLocked() ? Texts.LOCK_ON : Texts.LOCK_OFF).get();
            Util.renderWarpedTooltip(this, matrices, tooltip, mouseX, mouseY);
        }
    }
}
