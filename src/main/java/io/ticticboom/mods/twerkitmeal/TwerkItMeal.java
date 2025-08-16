package io.ticticboom.mods.twerkitmeal;

import io.ticticboom.mods.twerkitmeal.config.CommonConfig;
import io.ticticboom.mods.twerkitmeal.config.TwerkConfig;
import io.ticticboom.mods.twerkitmeal.helper.FilterListHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.*;


@Mod(TwerkItMeal.MOD_ID)
public class TwerkItMeal {
    public static final String MOD_ID = "twerkitmeal";

    static final ModConfigSpec commonSpec;
    public static final CommonConfig COMMON_CONFIG;

    static {
        final Pair<CommonConfig, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(CommonConfig::new);
        commonSpec = specPair.getRight();
        COMMON_CONFIG = specPair.getLeft();
    }

    public TwerkItMeal(ModContainer container) {
        container.registerConfig(ModConfig.Type.COMMON, commonSpec, "twerk-config.toml");
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


    @EventBusSubscriber(modid = TwerkItMeal.MOD_ID)
    public static class RegistryEvents {
        private static final Map<UUID, Integer> crouchCount = new HashMap<>();
        private static final Map<UUID, Boolean> prevSneaking = new HashMap<>();
        private static final Map<UUID, Integer> playerDistance = new HashMap<>();

        @SubscribeEvent
        public static void onTwerk(PlayerTickEvent.Pre event) {
            var player = event.getEntity();
            Level level = player.level();
            if (level.isClientSide) {
                return;
            }

            ServerLevel world = (ServerLevel) level;
            ServerPlayer serverPlayer = (ServerPlayer) player;

            UUID uuid = player.getUUID();
            if (!crouchCount.containsKey(uuid)) {
                crouchCount.put(uuid, 0);
                prevSneaking.put(uuid, player.isCrouching());
                playerDistance.put(uuid, 0);
            }

            if (player.isSprinting() && world.getRandom().nextDouble() <= TwerkConfig.sprintGrowChance) {
                triggerGrowth(event, uuid, serverPlayer, world);
            }

            boolean wasPlayerSneaking = prevSneaking.get(uuid);
            int playerCrouchCount = crouchCount.get(uuid);
            if (!player.isCrouching()) {
                prevSneaking.put(uuid, false);
                return;
            }
            if (wasPlayerSneaking && player.isCrouching()) {
                return;
            } else if (!wasPlayerSneaking && player.isCrouching()) {
                prevSneaking.put(uuid, true);
                crouchCount.put(uuid, ++playerCrouchCount);
            }

            if (playerCrouchCount >= TwerkConfig.minCrouchesToApplyBonemeal && world.getRandom().nextDouble() <= TwerkConfig.crouchGrowChance) {
                triggerGrowth(event, uuid, serverPlayer, world);
            }

        }

        private static void triggerGrowth(PlayerTickEvent event, UUID uuid, ServerPlayer player, ServerLevel level) {

            crouchCount.put(uuid, 0);
            List<BlockPos> growables = getNearestBlocks(level, player.getOnPos());
            Set<BlockPos> grownDT = new HashSet<>();
            for (BlockPos growablePos : growables) {
                BlockState blockState = level.getBlockState(growablePos);
                String regId = BuiltInRegistries.BLOCK.getKey(blockState.getBlock()).toString();
                if (!FilterListHelper.shouldAllow(regId)) {
                    continue;
                }

                if (TwerkConfig.saplingsOnly) {
                    if (!blockState.is(BlockTags.SAPLINGS)) {
                        continue;
                    }
                }
                if (blockState.hasProperty(CropBlock.AGE)) {
                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, growablePos, player);
                } else if (blockState.getBlock() instanceof BonemealableBlock) {
                    BoneMealItem.applyBonemeal(new ItemStack(Items.BONE_MEAL), level, growablePos, player);
                }

                level.sendParticles(
                        player,
                        ParticleTypes.HAPPY_VILLAGER,
                        false,
                        growablePos.getX() + level.random.nextDouble(),
                        growablePos.getY() + level.random.nextDouble(),
                        growablePos.getZ() + level.random.nextDouble(),
                        10, 0, 0, 0, 3);
            }
        }

        private static List<BlockPos> getNearestBlocks(Level world, BlockPos pos) {
            List<BlockPos> list = new ArrayList<>();
            for (int x = -TwerkConfig.effectRadius; x <= TwerkConfig.effectRadius; x++)
                for (int y = -2; y <= 2; y++)
                    for (int z = -TwerkConfig.effectRadius; z <= TwerkConfig.effectRadius; z++) {
                        Block block = world.getBlockState(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ())).getBlock();
                        String regId = BuiltInRegistries.BLOCK.getKey(block).toString();
                        if (block instanceof BonemealableBlock) {
                            if (FilterListHelper.shouldAllow(regId)) {
                                list.add(new BlockPos(x + pos.getX(), y + pos.getY(), z + pos.getZ()));
                            }
                        }
                    }
            return list;
        }
    }
}
