package io.github.ezforever.thatorthis;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import io.github.ezforever.thatorthis.gui.ChoiceScreen;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ChoiceScreen::new;
    }
}
