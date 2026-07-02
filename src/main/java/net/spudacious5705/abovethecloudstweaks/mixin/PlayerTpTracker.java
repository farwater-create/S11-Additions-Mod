package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.IPlayerWorldTp;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.WorldTransferSettings;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.network.OverlayNetworking;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerTpTracker implements IPlayerWorldTp {

    @Unique @NotNull
    private WorldTransferSettings aboveTheCloudsTweaks$worldTransferSettings = WorldTransferSettings.NULL;

    @Shadow
    public abstract ServerLevel serverLevel();

    /*@Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    private void init(CallbackInfo ci) {
        //aboveTheCloudsTweaks$updateWorldTpConf();
    }*/

    @Inject(
            method = "tick",
            at = @At("TAIL")
    )
    private void tick(CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        if(player.getY() > aboveTheCloudsTweaks$worldTransferSettings.upperBound()) {
            player.setAsInsidePortal(aboveTheCloudsTweaks$worldTransferSettings.goUp(), BlockPos.ZERO); //todo check blockpos safety
            OverlayNetworking.sendPacket(player, aboveTheCloudsTweaks$worldTransferSettings.portalFlavour());
        } else if(player.getY() < aboveTheCloudsTweaks$worldTransferSettings.lowerBound()) {
            player.setAsInsidePortal(aboveTheCloudsTweaks$worldTransferSettings.goDown(), BlockPos.ZERO); //todo check blockpos safety
            OverlayNetworking.sendPacket(player, aboveTheCloudsTweaks$worldTransferSettings.portalFlavour());
        }
    }

    @Inject(
            method = "hasChangedDimension",
            at = @At("TAIL")
    )
    private void dimChanged(CallbackInfo ci) {
        updateWorldTpConf();
        ServerPlayer player = (ServerPlayer) (Object) this;
        OverlayNetworking.sendPacket(player, -1);
    }


    @Override
    public void updateWorldTpConf() {
        aboveTheCloudsTweaks$worldTransferSettings = WorldTransferSettings.getTransferSettings(serverLevel());
    }
}
