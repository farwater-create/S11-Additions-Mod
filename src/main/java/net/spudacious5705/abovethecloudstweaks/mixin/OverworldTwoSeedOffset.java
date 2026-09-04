package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.server.level.ServerLevel;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.WorldTransferSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class OverworldTwoSeedOffset {

    @Inject(
            method = "getSeed",
            at = @At("RETURN"),
            cancellable = true
    )
    private void offsetSeed(CallbackInfoReturnable<Long> cir) {
        if (((ServerLevel)(Object) this).dimension() == WorldTransferSettings.LevelOverworld2) {
            cir.setReturnValue(
                    1009 - cir.getReturnValue()
            );
        }
    }
}
