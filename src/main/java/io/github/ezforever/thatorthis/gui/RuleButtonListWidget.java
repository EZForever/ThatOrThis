package io.github.ezforever.thatorthis.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.AbstractButtonWidget;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import io.github.ezforever.thatorthis.Configs;

@Environment(EnvType.CLIENT)
public class RuleButtonListWidget extends ElementListWidget<RuleButtonListWidget.ButtonEntry> {
    @Environment(EnvType.CLIENT)
    static class ButtonEntry extends ElementListWidget.Entry<RuleButtonListWidget.ButtonEntry> {
        private final List<RuleButtonWidget> buttons;

        public ButtonEntry(List<RuleButtonWidget> buttons) {
            this.buttons = buttons;
        }

        // --- Extends ElementListWidget.Entry<>

        @Override
        public List<RuleButtonWidget> children() {
            return buttons;
        }

        @Override
        public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            this.buttons.forEach((button) -> {
                button.y = y;
                button.render(matrices, mouseX, mouseY, tickDelta);
            });
        }
    }

    // ---

    public RuleButtonListWidget(MinecraftClient minecraftClient, int width, int height, int top, int bottom, int itemHeight, Configs configs, RuleButtonWidget.PressAction pressAction) {
        super(minecraftClient, width, height, top, bottom, itemHeight);

        if(configs != null) {
            Iterator<Configs.Rules.Rule> ruleIterator = configs.rules.rules.iterator();
            int originX = getRowLeft();
            while(ruleIterator.hasNext()) {
                List<RuleButtonWidget> buttons = new ArrayList<>();
                buttons.add(RuleButtonWidget.create(originX + 0, 0, 150, 20, ruleIterator.next(), pressAction));
                if(ruleIterator.hasNext())
                    buttons.add(RuleButtonWidget.create(originX + 160, 0, 150, 20, ruleIterator.next(), pressAction));
                addEntry(new ButtonEntry(buttons));
            }
            setChoices(configs.choices);
        }
    }

    public Optional<RuleButtonWidget> getHoveredButton(double mouseX, double mouseY) {
        return children().stream()
                .filter((ButtonEntry entry) -> entry.isMouseOver(mouseX, mouseY))
                .findFirst()
                .flatMap((ButtonEntry entry) -> entry.children().stream()
                    .filter(AbstractButtonWidget::isHovered) // isMouseOver() returns false for inactive buttons
                    .findFirst());
    }

    public void setChoices(Configs.Choices choices) {
        Map<String, RuleButtonWidget> buttons = children().stream()
                .map(ButtonEntry::children)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap((RuleButtonWidget button) -> button.rule.id, (RuleButtonWidget button) -> button));
        for(String ruleId : choices.choices.keySet()) {
            if(buttons.containsKey(ruleId))
                buttons.get(ruleId).setChoice(choices.choices.get(ruleId));
        }
    }

    @Override
    public int getRowWidth() {
        return 310;
    }

    @Override
    protected int getScrollbarPositionX() {
        return super.getScrollbarPositionX() + 32;
    }
}
