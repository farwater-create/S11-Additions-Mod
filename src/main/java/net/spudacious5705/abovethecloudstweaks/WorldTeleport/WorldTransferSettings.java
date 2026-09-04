package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.Portals.*;

import static net.spudacious5705.abovethecloudstweaks.WorldTeleport.Portals.*;

public record WorldTransferSettings(
        int upperBound, Portal goUp,
        int lowerBound,  Portal goDown,
        String world, int portalFlavour
) {

    public static final ResourceKey<Level> LevelDDU = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("ddu", "ddu")
    );

    public static final ResourceKey<Level> LevelOverworld2 = ResourceKey.create(
            Registries.DIMENSION,
            ResourceLocation.fromNamespaceAndPath("farwater_s11", "overworld_2")
    );


    public static WorldTransferSettings getTransferSettings(Level level){
        var currentDim = level.dimension();

        if(currentDim == Level.END) return End;
        if(currentDim == Level.OVERWORLD) return Overworld;
        if(currentDim == LevelDDU) return DDU;
        if(currentDim == Level.NETHER) return Nether;
        if(currentDim == LevelOverworld2) return Overworld_2;
        return NULL;

    }

    public static WorldTransferSettings End = new WorldTransferSettings(
            Integer.MAX_VALUE, new Portals.RescuePortal(512, Level.END),
            8, EndFallPortal,
            "END", 0);

    public static WorldTransferSettings Overworld = new WorldTransferSettings(
            Integer.MAX_VALUE, new RescuePortal(512, Level.OVERWORLD),
            8, new TransferPortal(LevelDDU, 108, 90, false),
            "OVERWORLD", 0);

    public static WorldTransferSettings Overworld_2 = new WorldTransferSettings(
            Integer.MAX_VALUE, new RescuePortal(512, LevelOverworld2),
            8, new TransferPortal(LevelDDU, 108, 90, false),
            "OVERWORLD_2", 0);

    public static WorldTransferSettings DDU = new WorldTransferSettings(
            110, ToOverworldTransferPortal,
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


    public static void switchOverworlds(ServerPlayer player) {
        /*PortalProcessor pro = new PortalProcessor(SwitchOverworldPortal, BlockPos.ZERO);

        var transition = pro.getPortalDestination(player.serverLevel(), player);
        if(transition == null) return;

        player.changeDimension(transition);*/

        player.setAsInsidePortal(SwitchOverworldPortal, BlockPos.ZERO);
        if(player.portalProcess != null) {
            player.portalProcess.setAsInsidePortalThisTick(true);
        }

    }
}
