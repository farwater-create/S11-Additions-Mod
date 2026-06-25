package net.spudacious5705.abovethecloudstweaks.mixin;

import net.mcreator.brazillegends.entity.CucaEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CucaEntity.class)
public abstract class LegendsCuca {


    @Inject(
            method = "removeWhenFarAway",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeWhenFar(double distanceToClosestPlayer, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Inject(
            method = "<init>", // This is the keyword that tells Mixin to target the constructor
            at = @At("TAIL")   // Runs at the very end of the constructor, after they set persistence
    )
    private void removePersistenceOnCreation(CallbackInfo ci) {
        ((PersistenceAccesssor) this).updatePersistenceTo(false);
    }
}
