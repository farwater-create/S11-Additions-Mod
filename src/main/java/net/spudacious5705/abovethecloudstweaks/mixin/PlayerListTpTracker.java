package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.IPlayerWorldTp;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public abstract class PlayerListTpTracker {

    @Inject(
            method = "placeNewPlayer",
            at = @At("RETURN")
    )
    private void playerPlaced(Connection connection, ServerPlayer player, CommonListenerCookie cookie, CallbackInfo ci) {
        ((IPlayerWorldTp) player).updateWorldTpConf();
    }

    @Inject(
            method = "respawn",
            at = @At("RETURN")
    )
    private void playerRespawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        ServerPlayer newPlayer = cir.getReturnValue();
        ((IPlayerWorldTp) newPlayer).updateWorldTpConf();
    }

}
