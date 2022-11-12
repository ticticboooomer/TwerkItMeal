package com.ticticboooom.twerkitmeal.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class Config {

  public static final ForgeConfigSpec SPEC;
  public static final Config COMMON;

  static {
    final Pair<Config, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Config::new);
    SPEC = specPair.getRight();
    COMMON = specPair.getLeft();
  }
  public final ForgeConfigSpec.BooleanValue showParticles;
  public final ForgeConfigSpec.BooleanValue useWhitelist;
  public final ForgeConfigSpec.ConfigValue<List<String>> blackList;
  public final ForgeConfigSpec.ConfigValue<List<String>> whitelist;
  public final ForgeConfigSpec.IntValue minCrouchesToApplyBonemeal;
  public final ForgeConfigSpec.IntValue effectRadius;
  public final ForgeConfigSpec.BooleanValue saplingsOnly;
  public final ForgeConfigSpec.DoubleValue sprintGrowChance;
  public final ForgeConfigSpec.DoubleValue crouchGrowChance;

  public Config(ForgeConfigSpec.Builder builder) {
    final List<String> defaultBlackList = List.of("minecraft:netherrack", "minecraft:grass_block", "minecraft:warped_nylium", "minecraft:crimson_nylium", "minecraft:tall_grass", "minecraft:grass", "botanypots", "gaiadimension");
    showParticles = builder.comment("Whether to show particles or not when crouching to grow things")
        .define("showParticles", true);
    useWhitelist = builder.comment("whether to enable the whitelist this does not disable the blacklist")
        .define("useWhiteList", false);
    blackList = builder.comment("growables to disable crouching on")
        .define("blacklist", defaultBlackList);
    whitelist = builder.comment("growables to enable crouching on (only works if 'useWhiteList' is true and will give exclisivity to those growables) ")
        .define("whitelist", new ArrayList<>());
    minCrouchesToApplyBonemeal = builder.comment("the minimum number of crouches before the bonemeal is applied (bonemeal is applied randomly so this will not be exact)")
        .defineInRange("minCrouchesToApplyBonemeal", 5, 0, Integer.MAX_VALUE);
    effectRadius = builder.comment("The radius of effect in blocks of applying the growth effect. Not recommended to change due to performance.")
        .defineInRange("effectRadius", 5, 0, 20);
    saplingsOnly = builder.comment("When true only saplings are allowed to grow with twerking")
        .define("saplingsOnly", false);
    sprintGrowChance = builder.comment("The chance of growth effect being applied from sprinting")
        .defineInRange("sprintGrowChance", 0.15, 0, 1);
    crouchGrowChance = builder.comment("The chance of growth effect being applied from any source")
        .defineInRange("crouchGrowChance", 0.5, 0, 1);
  }

  public boolean showParticles() {
    return showParticles.get();
  }

  public boolean useWhitelist() {
    return useWhitelist.get();
  }

  public List<String> getBlacklist() {
    return blackList.get();
  }

  public List<String> getWhitelist() {
    return whitelist.get();
  }

  public int minCrouchesToApplyBonemeal() {
    return minCrouchesToApplyBonemeal.get();
  }

  public int effectRadius() {
    return effectRadius.get();
  }

  public boolean saplingsOnly() {
    return saplingsOnly.get();
  }

  public double sprintGrowChance() {
    return sprintGrowChance.get();
  }

  public double crouchGrowChance() {
    return crouchGrowChance.get();
  }
}
