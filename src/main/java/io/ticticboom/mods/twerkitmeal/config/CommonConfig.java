package io.ticticboom.mods.twerkitmeal.config;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class CommonConfig {
    public final ModConfigSpec.BooleanValue showParticles;
    public final ModConfigSpec.BooleanValue useWhitelist;
    public final ModConfigSpec.ConfigValue<List<? extends String>> blackList;
    public final ModConfigSpec.ConfigValue<List<? extends String>> whitelist;
    public final ModConfigSpec.IntValue minCrouchesToApplyBonemeal;
    public final ModConfigSpec.IntValue effectRadius;
    public final ModConfigSpec.BooleanValue saplingsOnly;
    public final ModConfigSpec.DoubleValue sprintGrowChance;
    public final ModConfigSpec.DoubleValue crouchGrowChance;

    public static final List<String> defaultBlackList = List.of(
            "minecraft:netherrack",
            "minecraft:grass_block",
            "minecraft:warped_nylium",
            "minecraft:crimson_nylium",
            "minecraft:tall_grass",
            "minecraft:grass",
            "minecraft:short_grass",
            "botaniapots",
            "gaiadimension",
            "minecraft:peony",
            "minecraft:glow_lichen",
            "minecraft:fern",
            "minecraft:large_fern",
            "minecraft:rose_bush",
            "minecraft:lilac",
            "minecraft:sunflower",
            "minecraft:seagrass",
            "minecraft:tall_seagrass");
    public CommonConfig(ModConfigSpec.Builder builder) {

        showParticles = builder.comment("Whether to show particles or not when crouching to grow things")
                .translation("twerkitmeal.config.showParticles")
                .define("showParticles", true);
        useWhitelist = builder.comment("whether to enable the whitelist this does not disable the blacklist")
                .translation("twerkitmeal.config.useWhitelist")
                .define("useWhiteList", false);
        blackList = builder.comment("growables to disable crouching on")
                .translation("twerkitmeal.config.blacklist")
                .defineList("blacklist", defaultBlackList, () -> "minecraft:wheat", (x) -> validateResourceLocation(x.toString()));
        whitelist = builder.comment("growables to enable crouching on (only works if 'useWhiteList' is true and will give exclisivity to those growables) ")
                .translation("twerkitmeal.config.whitelist")
                .defineList("whitelist", List.of(), () -> "minecraft:wheat", (x) -> validateResourceLocation(x.toString()));
        minCrouchesToApplyBonemeal = builder.comment("the minimum number of crouches before the bonemeal is applied (bonemeal is applied randomly so this will not be exact)")
                .translation("twerkitmeal.config.minCrouchesToApplyBonemeal")
                .defineInRange("minCrouchesToApplyBonemeal", 5, 0, Integer.MAX_VALUE);
        effectRadius = builder.comment("The radius of effect in blocks of applying the growth effect. Not recommended to change due to performance.")
                .translation("twerkitmeal.config.effectRadius")
                .defineInRange("effectRadius", 5, 0, 20);
        saplingsOnly = builder.comment("When true only saplings are allowed to grow with twerking")
                .translation("twerkitmeal.config.saplingsOnly")
                .define("saplingsOnly", false);
        sprintGrowChance = builder.comment("The chance of growth effect being applied from sprinting")
                .translation("twerkitmeal.config.springGrowChance")
                .defineInRange("sprintGrowChance", 0.15, 0, 1);
        crouchGrowChance = builder.comment("The chance of growth effect being applied from any source")
                .translation("twerkitmeal.config.crouchGrowChance")
                .defineInRange("crouchGrowChance", 0.5, 0, 1);
    }

    private static boolean validateResourceLocation(String location) {
        return ResourceLocation.tryParse(location) != null;
    }
}
