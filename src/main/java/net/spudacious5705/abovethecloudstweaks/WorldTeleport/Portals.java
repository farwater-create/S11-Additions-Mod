package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.abovethecloudstweaks.Abovethecloudstweaks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

import static net.spudacious5705.abovethecloudstweaks.WorldTeleport.WorldTransferSettings.LevelOverworld2;

class Portals {


    record TransferPortal(ResourceKey<Level> TARGET_LEVEL, int maxSearchHeight, int minSearchHeight,
                          boolean searchUp) implements Portal {

        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return entity instanceof Player ? 80 : 0;
        }

        @Override
        public @NotNull Transition getLocalTransition() {
            return Transition.CONFUSION;
        }

        @Override
        public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, @NotNull Entity player, @NotNull BlockPos DO_NOT_USE) {
            ServerLevel targ_level = serverLevel.getServer().getLevel(TARGET_LEVEL);
            if (targ_level == null) return null;
            BlockPos destPos = TeleportTargetFinders.findValidSpace(player.getOnPos(), searchUp, maxSearchHeight, minSearchHeight, targ_level);
            return new DimensionTransition(targ_level, destPos.getBottomCenter(), Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);//give some night vision?
        }
    }

    record RescuePortal(int y, ResourceKey<Level> TARGET_LEVEL) implements Portal{

        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return 0;
        }

        @Override
        public Portal.@NotNull Transition getLocalTransition() {
            return Transition.CONFUSION;
        }

        @Override
        public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, @NotNull Entity player, @NotNull BlockPos DO_NOT_USE) {
            ServerLevel targ_level = serverLevel.getServer().getLevel(TARGET_LEVEL);
            if(targ_level == null) return null;
            Vec3 rescue_pos = new Vec3(player.position().x, y, player.position().z);
            return new DimensionTransition(targ_level, rescue_pos, Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);
        }
    }

    static final Portal EndFallPortal = new Portal() {

        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return 0;
        }

        @Override
        public Portal.@NotNull Transition getLocalTransition() {
            return Transition.CONFUSION;
        }

        @Override
        public @Nullable DimensionTransition getPortalDestination(@NotNull ServerLevel serverLevel, @NotNull Entity entity, @NotNull BlockPos DO_NOT_USE) {
            if(!(entity instanceof ServerPlayer player)) return null;
            ServerLevel targ_level = Abovethecloudstweaks.getHomeOverworld(player);
            if(targ_level == null) return null;

            Vec3 rescue_pos = new Vec3(player.position().x, 512, player.position().z);
            return new DimensionTransition(targ_level, rescue_pos, Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);
        }
    };

    static final Portal SwitchOverworldPortal = new Portal() {
        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return 0;
        }

        @Override
        public @NotNull Transition getLocalTransition() {
            return Transition.NONE;
        }

        @Override
        public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, @NotNull Entity entity, @NotNull BlockPos DO_NOT_USE) {
            if(serverLevel.dimension() != Level.OVERWORLD && serverLevel.dimension() != LevelOverworld2) return null;
            if(!(entity instanceof ServerPlayer player)) return null;

            ServerLevel targ_level = Abovethecloudstweaks.getHomeOverworld(player);
            var d = targ_level.dimension();

            if (targ_level == null) return null;
            BlockPos destPos = TeleportTargetFinders.findValidSwitchSpace(player.getOnPos(), targ_level);

            return new DimensionTransition(targ_level, destPos.getBottomCenter(),
                    Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);
        }
    };

    static Portal defaultRescuePortal = new RescuePortal(64, Level.OVERWORLD);

    static final Portal ToOverworldTransferPortal = new Portal() {

        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return entity instanceof Player ? 80 : 0;
        }

        @Override
        public @NotNull Transition getLocalTransition() {
            return Transition.CONFUSION;
        }

        @Override
        public @Nullable DimensionTransition getPortalDestination(@NotNull ServerLevel serverLevel, @NotNull Entity entity, @NotNull BlockPos DO_NOT_USE) {
            if(!(entity instanceof ServerPlayer player)) return null;
            ServerLevel targ_level = Abovethecloudstweaks.getHomeOverworld(player);
            if (targ_level == null) return null;

            BlockPos destPos = TeleportTargetFinders.findValidSpace(player.getOnPos(), true, 28, 8, targ_level);

            return new DimensionTransition(targ_level, destPos.getBottomCenter(), Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.PLAY_PORTAL_SOUND);
        }
    };
}
