package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record WorldTransferSettings(
        int upperBound, Portal goUp,
        int lowerBound,  Portal goDown,
        String world, int portalFlavour
) {

    public static final ResourceKey<Level> LevelDDU = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("ddu", "ddu")
    );


    public static WorldTransferSettings getTransferSettings(Level level){
        var currentDim = level.dimension();

        if(currentDim == Level.END) return End;
        if(currentDim == Level.OVERWORLD) return Overworld;
        if(currentDim == LevelDDU) return DDU;
        if(currentDim == Level.NETHER) return Nether;
        return NULL;

    }

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
                BlockPos destPos = findValidSpace(player.getOnPos(), searchUp, maxSearchHeight, minSearchHeight, targ_level);
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

    static Portal defaultRescuePortal = new RescuePortal(64, Level.OVERWORLD);

    public static BlockPos findValidSpace(BlockPos pos, boolean searchUp, int maxSearchHeight, int minSearchHeight, ServerLevel level) {

        pos = pos.mutable().setY(searchUp? minSearchHeight:maxSearchHeight);

        WorldBorder worldborder = level.getWorldBorder();


        for (BlockPos.MutableBlockPos searchPos :
                BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {

            if(!worldborder.isWithinBounds(searchPos)) continue;

            boolean[] validity = {false,false,false};
            int startItt = 3;
            if(searchUp){ for (int y = minSearchHeight; y <= maxSearchHeight; ++y) {
                searchPos.setY(y);
                if(startItt >0){
                    startItt--;
                    validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                    continue;
                }
                validity[2] = validity[1];
                validity[1] = validity[0];

                boolean v = level.getBlockState(searchPos).canBeReplaced();
                validity[0] = v;

                if(validity[0] && validity[1] && !validity[2]){
                    return searchPos.move(0,-1,0).immutable();
                }
            }} else { for (int y = maxSearchHeight; y >= minSearchHeight; --y) {
                searchPos.setY(y);
                if(startItt >0){
                    startItt--;
                    validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                    continue;
                }
                validity[2] = validity[1];
                validity[1] = validity[0];

                validity[0] = level.getBlockState(searchPos).canBeReplaced();

                if(!validity[0] && validity[1] && validity[2]){
                    return searchPos.move(0,1,0).immutable();
                }
            }}


        }
        return searchUp ? pos.above():pos.below(2);
    }

    public static WorldTransferSettings End = new WorldTransferSettings(
            Integer.MAX_VALUE, new RescuePortal(512, Level.END),
            8, new RescuePortal(512, Level.OVERWORLD),
            "END", 0);

    public static WorldTransferSettings Overworld = new WorldTransferSettings(
            Integer.MAX_VALUE, new RescuePortal(512, Level.OVERWORLD),
            8, new TransferPortal(LevelDDU, 108, 90, false),
            "OVERWORLD", 0);

    public static WorldTransferSettings DDU = new WorldTransferSettings(
            110, new TransferPortal(Level.OVERWORLD, 28, 8, true),
            -58, new TransferPortal(Level.NETHER, 108, 90, false),
            "UNDERGROUND", 0);

    public static WorldTransferSettings Nether = new WorldTransferSettings(
            120,new TransferPortal(LevelDDU, -36, -56, true),
            -32, defaultRescuePortal,
            "NETHER", 0);

    public static WorldTransferSettings NULL = new WorldTransferSettings(
            Integer.MAX_VALUE, defaultRescuePortal,
            Integer.MIN_VALUE,defaultRescuePortal,
            "NULL", 0);


}
