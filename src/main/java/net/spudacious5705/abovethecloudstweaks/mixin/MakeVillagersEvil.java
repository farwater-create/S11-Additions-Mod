package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.spudacious5705.abovethecloudstweaks.VillagerEvilPool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class MakeVillagersEvil {


    @Shadow
    protected abstract void releaseAllPois();


    @Inject(
            method = "tick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void removeWhenFar(CallbackInfo ci) {
        Villager villager = (Villager) (Object) this;

        this.releaseAllPois();

        if (villager.level() instanceof ServerLevel server) {
            Mob mob = VillagerEvilPool.getRandomPillager().create(server);

            if(mob != null){
                ((PersistenceAccesssor) mob).updatePersistenceTo(true);
                mob.moveTo(villager.getX(), villager.getY(), villager.getZ(), villager.getYRot(), villager.getXRot());
                server.addFreshEntity(mob);
            }
        }

        villager.discard();
        ci.cancel();
    }

    @Inject(
            method = "<init>*", // This is the keyword that tells Mixin to target the constructor
            at = @At("TAIL")   // Runs at the very end of the constructor, after they set persistence
    )
    private void removePersistenceOnCreation(CallbackInfo ci) {
        ((PersistenceAccesssor) this).updatePersistenceTo(false);
    }

}
