package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.world.item.ElytraItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraItem.class)
public class Elytra {

    @Inject(
            method = "isFlyEnabled",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void alwaysNoFly(
            ItemStack p_41141_, CallbackInfoReturnable<Boolean> ci
    ) {
        ci.setReturnValue(false);
        ci.cancel();
    }
}
