package com.ticticboooom.twerkitmeal.helper;

import static com.ticticboooom.twerkitmeal.config.Config.COMMON;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

public class FilterListHelper {

  public static boolean shouldAllow(Block block) {
    return shouldAllow(ForgeRegistries.BLOCKS.getKey(block).toString());
  }

  public static boolean shouldAllow(String key) {
    if (COMMON.getBlacklist().isEmpty()) {
      return true;
    }

    List<String> variations = new ArrayList<>();
    // entire block RL
    variations.add(key);
    // mod id from RL of block
    variations.add(key.substring(0, key.indexOf(":")));
    for (String listed : COMMON.getBlacklist()) {
      if (variations.contains(listed)) {
        return false;
      }
    }

    if (!COMMON.useWhitelist()) {
      return true;
    }

    for (String listed : COMMON.getWhitelist()) {
      if (variations.contains(listed)) {
        return true;
      }
    }
    return true;
  }
}
