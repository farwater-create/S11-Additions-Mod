package net.spudacious5705.abovethecloudstweaks.mixin;

import TheAmirtini.coreprotectneo.container.ContainerChangeTracker;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.ChestMenu;
import noobanidus.mods.lootr.common.data.LootrInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ContainerChangeTracker.class)
public class CoreProtect_Lootr_StackOverflowFix {


    @Inject(
            method = "beginOnNextTick",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    private static void onTrackingRegister(ServerPlayer player, MenuProvider menuProvider, CallbackInfo ci) {
        if(menuProvider instanceof LootrInventory) ci.cancel();
    }

    @Inject(
            method = "endBeforeClose(Lnet/minecraft/server/level/ServerPlayer;)V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/level/ServerPlayer;containerMenu:Lnet/minecraft/world/inventory/AbstractContainerMenu;",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    private static void onBeforeClose(ServerPlayer player, CallbackInfo ci) {
        if(player.containerMenu instanceof ChestMenu chest){
            if(chest.getContainer() instanceof LootrInventory){
                ci.cancel();
            }
        }
    }



}
