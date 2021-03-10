package io.github.ezforever.thatorthis.config.rule;

import io.github.ezforever.thatorthis.config.choice.Choice;
import io.github.ezforever.thatorthis.config.choice.ChoiceHolder;
import io.github.ezforever.thatorthis.config.choice.Choices;
import io.github.ezforever.thatorthis.gui.ChoiceScreen;
import io.github.ezforever.thatorthis.gui.SingleThreadFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

// Anything that can hold a list of `Rule`s
// Used for unifying Rules and NestedRule in many operations
// Also for GeneratedRule for generating fake rules
public interface RuleHolder {
    Logger LOGGER = LogManager.getLogger("thatorthis/config");

    // ---

    // Whether this list of `Rule`s can be disabled
    boolean canDisable();

    // Make sure we have a list of `Rule`s
    List<Rule> getRules();

    // The (secondary) title of the choice screen
    @Environment(EnvType.CLIENT)
    Text getScreenTitle();

    // ---

    default // "RuleID -> Choice" map
    ChoiceHolder getDefaultChoices() {
        // XXX: `transient` caching?
        ChoiceHolder defaultChoices = new ChoiceHolder();
        getRules().forEach((Rule rule) -> rule.getDefaultChoice()
                .ifPresent((Choice choice) -> defaultChoices.put(rule.id, choice)));
        return defaultChoices;
    }

    default // Resolve choices to "ModID -> Blacklist" map
    Map<String, Set<String>> resolve(ChoiceHolder choices) {
        Map<String, Set<String>> resultMap = new HashMap<>();
        for(Rule rule : getRules()) {
            Choice choice = choices.get(rule.id);
            if(choice == null || !rule.resolve(choice, resultMap)) {
                // Choice not found. Maybe a NULL rule (no default choice either), or a modpack update
                Optional<Choice> defaultChoice = rule.getDefaultChoice();
                if(defaultChoice.isPresent()) {
                    LOGGER.warn("Resetting invalid choice of rule {} to default", rule.id);
                    choice = defaultChoice.get();
                    if (rule.resolve(choice, resultMap))
                        choices.put(rule.id, choice);
                    else
                        LOGGER.error("Default choice of rule {} is invalid! Skipping", rule.id);
                }
            }
        }
        return Collections.unmodifiableMap(resultMap);
    }

    default // Show a nested ChoiceScreen and wait for result
    @Environment(EnvType.CLIENT)
    SingleThreadFuture<Choices> showNestedScreen(Choices initialChoices) {
        // NOTE: Screen will be patched by Mixin *after* loading ThatOrThis
        //  If RuleHolder imports Screen, Mixin will just fail
        //  So an inner class is used for delay-importing
        class ScreenDelayImporter {
            SingleThreadFuture<Choices> invoke() {
                SingleThreadFuture<Choices> future = new SingleThreadFuture<>();

                MinecraftClient minecraftClient = MinecraftClient.getInstance();
                minecraftClient.openScreen(new ChoiceScreen(minecraftClient.currentScreen,
                        RuleHolder.this, initialChoices,
                        (Choices choices, Screen parentScreen) -> {
                            future.resolve(choices);
                            return parentScreen;
                        }
                ));
                return future;
            }
        }
        return new ScreenDelayImporter().invoke();
    }
}
