package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public record WorldTransferSettings(
        int lowerBound, int upperBound,
        Portal goUp, Portal goDown, String world,
        int portalFlavour
) {

    WorldTransferSettings(int lowerBound, int upperBound, String world){
        this(lowerBound, upperBound, toOverworld, toOverworld, world, 0);
    }

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

    abstract static class TransferPortal implements Portal {

        @Override
        public int getPortalTransitionTime(@NotNull ServerLevel level, @NotNull Entity entity) {
            return entity instanceof Player ? 80 : 0;
        }

        @Override
        public Portal.@NotNull Transition getLocalTransition() {
            return Transition.CONFUSION;
        }
    }

    public static Portal testPortal = new TransferPortal(){
        @Override
        public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, @NotNull Entity player, @NotNull BlockPos DO_NOT_USE) {
            ServerLevel targ_level = serverLevel.getServer().getLevel(LevelDDU);
            if(targ_level == null) return null;
            return new DimensionTransition(targ_level, player.position(), Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.DO_NOTHING);//give some night vision?
        }
    };

    public static Portal toOverworld = new TransferPortal(){
        @Override
        public @Nullable DimensionTransition getPortalDestination(ServerLevel serverLevel, @NotNull Entity player, @NotNull BlockPos DO_NOT_USE) {
            ServerLevel targ_level = serverLevel.getServer().getLevel(Level.OVERWORLD);
            if(targ_level == null) return null;
            return new DimensionTransition(targ_level, player.position(), Vec3.ZERO, player.getYRot(), player.getXRot(),
                    DimensionTransition.DO_NOTHING);//give some night vision?
        }
    };

    public static WorldTransferSettings End = new WorldTransferSettings(
            8, Integer.MAX_VALUE, "END");

    public static WorldTransferSettings Overworld = new WorldTransferSettings(
            8, Integer.MAX_VALUE,
            toOverworld, testPortal, "OVERWORLD", 0);

    public static WorldTransferSettings DDU = new WorldTransferSettings(
            -58, 110, "UNDERGROUND");

    public static WorldTransferSettings Nether = new WorldTransferSettings(
            Integer.MIN_VALUE, 120, "NETHER");

    public static WorldTransferSettings NULL = new WorldTransferSettings(
            Integer.MIN_VALUE, Integer.MAX_VALUE, "NULL");

}
