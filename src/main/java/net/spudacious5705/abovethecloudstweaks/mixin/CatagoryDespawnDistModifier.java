package net.spudacious5705.abovethecloudstweaks.mixin;


import net.minecraft.world.entity.MobCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MobCategory.class)
public abstract class CatagoryDespawnDistModifier {

    @Inject(method = "getDespawnDistance", at = @At("RETURN"), cancellable = true)
    private void customDistance(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(
                (cir.getReturnValue() /4) *3
        );
    }
}
