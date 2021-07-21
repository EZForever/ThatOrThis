package io.github.ezforever.thatorthis.gui;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.rule.Rule;
import io.github.ezforever.thatorthis.config.rule.RuleHolder;
import io.github.ezforever.thatorthis.config.rule.VisibleRule;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;

import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class RuleButtonListWidget extends ElementListWidget<RuleButtonListWidget.Entry> {
    @Environment(EnvType.CLIENT)
    static class Entry extends ElementListWidget.Entry<RuleButtonListWidget.Entry> {
        private final List<RuleButtonWidget> buttons;

        public Entry(List<RuleButtonWidget> buttons) {
            this.buttons = Collections.unmodifiableList(buttons);
        }

        // --- Extends ElementListWidget.Entry<>

        @Override
        public List<? extends Element> children() {
            return buttons;
        }

        @Override
        public List<? extends Selectable> selectableChildren() {
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

    @Environment(EnvType.CLIENT)
    @FunctionalInterface
    public interface UpdateAction {
        void onUpdate(Rule rule, SingleThreadFuture<Choice> choice);
    }

    // ---

    private static void addRule(List<RuleButtonWidget> buttons, Map<String, RuleButtonWidget> ruleMap, int x, Rule rule, UpdateAction updateAction) {
        if(!(rule instanceof VisibleRule))
            return;

        RuleButtonWidget button = new RuleButtonWidget(x, 0, 150, 20, (VisibleRule)rule, updateAction);
        buttons.add(button);
        ruleMap.put(rule.id, button);
    }

    // ---

    private final Map<String, RuleButtonWidget> ruleIdToButtonMap;
    private final Map<RuleButtonWidget, Boolean> buttonToStateMap;

    public RuleButtonListWidget(MinecraftClient minecraftClient,
                                int width, int height, int top, int bottom, int itemHeight,
                                RuleHolder ruleHolder,
                                UpdateAction updateAction) {
        super(minecraftClient, width, height, top, bottom, itemHeight);

        Map<String, RuleButtonWidget> ruleMap = new HashMap<>();
        int originX = getRowLeft();
        Iterator<Rule> ruleIterator = ruleHolder.getRules().iterator();
        while(ruleIterator.hasNext()) {
            List<RuleButtonWidget> buttons = new ArrayList<>();
            addRule(buttons, ruleMap, originX, ruleIterator.next(), updateAction);
            if(ruleIterator.hasNext())
                addRule(buttons, ruleMap, originX + 160, ruleIterator.next(), updateAction);
            addEntry(new Entry(buttons));
        }
        this.ruleIdToButtonMap = Collections.unmodifiableMap(ruleMap);
        this.buttonToStateMap = Collections.unmodifiableMap(ruleMap.values().stream()
                .collect(Collectors.toMap(
                        (RuleButtonWidget button) -> button,
                        (RuleButtonWidget button) -> button.active
                ))
        );
    }

    public Optional<RuleButtonWidget> getHoveredButton(double mouseX, double mouseY) {
        // isMouseOver() returns false for inactive buttons
        // isHovered() return true on keyboard focus
        return ruleIdToButtonMap.values().stream()
                .filter((RuleButtonWidget button) -> button.getType() == SelectionType.HOVERED).findAny();
    }

    public void setChoices(ChoiceHolder choices) {
        choices.forEach((String ruleId, Choice choice) -> {
            if(ruleIdToButtonMap.containsKey(ruleId))
                ruleIdToButtonMap.get(ruleId).setChoice(choice);
        });
    }

    public void setDisabled(boolean disabled) {
        buttonToStateMap.forEach((RuleButtonWidget button, Boolean state) -> button.active = !disabled && state);
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
