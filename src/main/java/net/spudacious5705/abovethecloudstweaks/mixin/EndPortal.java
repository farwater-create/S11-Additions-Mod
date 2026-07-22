package net.spudacious5705.abovethecloudstweaks.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EndDragonFight.class)
public class EndPortal {

    @Inject(
            method = "spawnNewGateway()V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void noPortal(
            CallbackInfo ci
    ) {
        ci.cancel();
    }

    @Inject(
            method = "spawnNewGateway(Lnet/minecraft/core/BlockPos;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void stillNoPortal(
            BlockPos pos,
            CallbackInfo ci
    ) {
        ci.cancel();
    }
}
