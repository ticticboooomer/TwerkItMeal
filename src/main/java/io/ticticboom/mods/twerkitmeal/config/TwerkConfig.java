package io.ticticboom.mods.twerkitmeal.config;

import io.ticticboom.mods.twerkitmeal.TwerkItMeal;
import net.neoforged.fml.config.ModConfig;

import java.util.List;

public class TwerkConfig {
    public static boolean showParticles;
    public static boolean useWhitelist;
    public static List<String> blackList;
    public static List<String> whitelist;
    public static int minCrouchesToApplyBonemeal;
    public static int effectRadius;
    public static boolean saplingsOnly;
    public static int distanceSprintedToGrow;
    public static double sprintGrowChance;
    public static double crouchGrowChance;

    public static void bake(ModConfig config) {
        showParticles = TwerkItMeal.COMMON_CONFIG.showParticles.get();
        useWhitelist = TwerkItMeal.COMMON_CONFIG.useWhitelist.get();
        blackList = TwerkItMeal.COMMON_CONFIG.blackList.get().stream().map(x -> (String)x).toList();
        whitelist = TwerkItMeal.COMMON_CONFIG.whitelist.get().stream().map(x -> (String)x).toList();
        minCrouchesToApplyBonemeal = TwerkItMeal.COMMON_CONFIG.minCrouchesToApplyBonemeal.get();
        effectRadius = TwerkItMeal.COMMON_CONFIG.effectRadius.get();
        saplingsOnly = TwerkItMeal.COMMON_CONFIG.saplingsOnly.get();
        sprintGrowChance = TwerkItMeal.COMMON_CONFIG.sprintGrowChance.get();
        crouchGrowChance = TwerkItMeal.COMMON_CONFIG.crouchGrowChance.get();
    }
}
