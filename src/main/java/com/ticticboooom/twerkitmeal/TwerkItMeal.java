package com.ticticboooom.twerkitmeal;

import com.ticticboooom.twerkitmeal.config.Config;
import com.ticticboooom.twerkitmeal.helper.FilterListHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;


@Mod(TwerkItMeal.MOD_ID)
public class TwerkItMeal {

  public static final String MOD_ID = "twerkitmeal";

  public TwerkItMeal() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC, "twerk-config.toml");
    MinecraftForge.EVENT_BUS.register(new RegistryEvents());
  }


  public static class RegistryEvents {

    private final Map<UUID, Integer> crouchCount = new HashMap<>();
    private final Map<UUID, Boolean> prevSneaking = new HashMap<>();

    @SubscribeEvent
    public void onSprint(TickEvent.PlayerTickEvent event) {
      if (event.player.level.isClientSide || event.player.isSpectator()) {
        return;
      }

      UUID uuid = event.player.getUUID();

      ServerLevel world = (ServerLevel) event.player.level;
      if (event.player.isSprinting() && world.getRandom().nextDouble() <= Config.COMMON.sprintGrowChance()) {
        triggerGrowth(event, uuid);
      }
    }

    @SubscribeEvent
    public void onTwerk(TickEvent.PlayerTickEvent event) {
      if (event.player.level.isClientSide || event.player.isSpectator()) {
        return;
      }

      UUID uuid = event.player.getUUID();
      boolean playerCurrentlySneaking = event.player.isCrouching();
      if (!crouchCount.containsKey(uuid)) {
        crouchCount.put(uuid, 0);
        prevSneaking.put(uuid, playerCurrentlySneaking);
      }

      boolean wasPlayerSneaking = prevSneaking.get(uuid);
      int playerCrouchCount = crouchCount.get(uuid);

      if (!playerCurrentlySneaking) {
        prevSneaking.put(uuid, false);
        return;
      }

      if (wasPlayerSneaking) {
        return;
      }

      prevSneaking.put(uuid, true);
      crouchCount.put(uuid, ++playerCrouchCount);

      ServerLevel world = (ServerLevel) event.player.level;
      boolean hasCrouchedEnough = playerCrouchCount >= Config.COMMON.minCrouchesToApplyBonemeal();
      boolean hasPassedDiceRoll = world.getRandom().nextDouble() <= Config.COMMON.crouchGrowChance();
      if (hasCrouchedEnough && hasPassedDiceRoll) {
        triggerGrowth(event, uuid);
      }
    }

    private void triggerGrowth(TickEvent.PlayerTickEvent event, UUID uuid) {
      crouchCount.put(uuid, 0);
      Level level = event.player.level;

      for (BlockPos growablePos : getNearestBlocks(level, event.player.getOnPos())) {
        BlockState blockState = level.getBlockState(growablePos);
        if (!FilterListHelper.shouldAllow(blockState.getBlock())) {
          continue;
        }

        boolean isSapling = ForgeRegistries.BLOCKS.tags().getTag(BlockTags.SAPLINGS).contains(blockState.getBlock());
        if (Config.COMMON.saplingsOnly() && !isSapling) {
          continue;
        }

        boolean isCrop = blockState.hasProperty(CropBlock.AGE);
        boolean isBonemealable = blockState.getBlock() instanceof BonemealableBlock;
        if (isCrop || isBonemealable) {
          BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, growablePos, event.player);
        }

        if (!Config.COMMON.showParticles()) {
          return;
        }

        double xPos = growablePos.getX() + level.random.nextDouble();
        double yPos = growablePos.getY() + level.random.nextDouble();
        double zPos = growablePos.getZ() + level.random.nextDouble();
        ((ServerLevel) level).sendParticles((ServerPlayer) event.player, ParticleTypes.HAPPY_VILLAGER, false, xPos, yPos, zPos, 10, 0, 0, 0, 3);
      }
    }

    private List<BlockPos> getNearestBlocks(Level world, BlockPos pos) {
      List<BlockPos> list = new ArrayList<>();
      int radius = Config.COMMON.effectRadius();
      for (int x = -radius; x <= radius; x++) {
        for (int y = -2; y <= 2; y++) {
          for (int z = -radius; z <= radius; z++) {
            BlockPos targetPos = pos.offset(x, y, z);
            BlockState blockState = world.getBlockState(targetPos);
            boolean isCrop = blockState.hasProperty(CropBlock.AGE);
            boolean isBonemealable = blockState.getBlock() instanceof BonemealableBlock;
            boolean isAllowed = FilterListHelper.shouldAllow(blockState.getBlock());
            if ((isCrop || isBonemealable) && isAllowed) {
              list.add(targetPos);
            }
          }
        }
      }
      return list;
    }
  }
}
