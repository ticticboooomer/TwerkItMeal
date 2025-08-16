package io.ticticboom.mods.twerkitmeal.config;

import io.ticticboom.mods.twerkitmeal.TwerkItMeal;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;

@EventBusSubscriber(modid = TwerkItMeal.MOD_ID)
public class ConfigSubscriber {
    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event) {
        TwerkConfig.bake(event.getConfig());
    }
}
