package io.github.ezforever.thatorthis.gui;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.rule.Rule;
import io.github.ezforever.thatorthis.config.rule.RuleHolder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.LiteralText;

import java.util.Objects;
import java.util.function.BiFunction;

@Environment(EnvType.CLIENT)
public class ChoiceScreen extends Screen {
    private final Screen parent;
    private final RuleHolder ruleHolder;
    private final ChoiceHolder initialChoices;
    private final BiFunction<ChoiceHolder, Screen, Screen> callback;
    private ChoiceHolder shownChoices;
    private RuleButtonListWidget ruleButtons;
    private ButtonWidget discardOrDefaultButton;
    private ButtonWidget doneButton;
    private boolean dirty = false;

    public ChoiceScreen(Screen parent,
                        RuleHolder ruleHolder, ChoiceHolder initialChoices,
                        BiFunction<ChoiceHolder, Screen, Screen> callback) {
        super(ruleHolder.getScreenTitle());

        this.parent = parent;
        this.ruleHolder = ruleHolder;
        this.initialChoices = initialChoices.copy();
        this.callback = callback;

        this.shownChoices = initialChoices.copy();
    }

    private void setDirty(boolean value) {
        dirty = value;
        discardOrDefaultButton.setMessage((dirty ? Texts.DISCARD : Texts.DEFAULT).get());
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
                width / 2 - 155, height - 27, 150, 20,
                LiteralText.EMPTY,
                (ButtonWidget button) -> {
                    shownChoices = (dirty ? initialChoices : ruleHolder.getDefaultChoices()).copy();
                    ruleButtons.setChoices(shownChoices);
                    setDirty(!dirty);
                }
        );
        setDirty(dirty); // Reset button caption
        addButton(discardOrDefaultButton);

        doneButton = new ButtonWidget(width / 2 - 155 + 160, height - 27, 150, 20, Texts.DONE.get(), (ButtonWidget button) -> onClose());
        addButton(doneButton);
    }

    @Override
    public void onClose() {
        Screen nextScreen;
        if(dirty) {
            setDirty(false);
            nextScreen = callback.apply(shownChoices, parent);
        } else {
            nextScreen = parent;
        }
        Objects.requireNonNull(client).openScreen(nextScreen);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        ruleButtons.render(matrices, mouseX, mouseY, delta);
        drawCenteredText(matrices, textRenderer, Texts.TITLE.get(), width / 2, 7, 0xffffff);
        drawCenteredText(matrices, textRenderer, title, width / 2, 20, 0xffffff);
        super.render(matrices, mouseX, mouseY, delta);
        ruleButtons.getHoveredButton(mouseX, mouseY)
                .ifPresent((RuleButtonWidget button) -> renderOrderedTooltip(
                        matrices,
                        Objects.requireNonNull(client).textRenderer
                                .wrapLines(button.getTooltip(), Math.max(width / 2 - 43, 170)),
                        mouseX, mouseY
                ));
    }
}
