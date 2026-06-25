package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class Despawn {

    @Shadow
    protected abstract boolean shouldDespawnInPeaceful();

    @Inject(
            method = "checkDespawn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void modifiedDespawn(CallbackInfo ci) {
        Mob self = (Mob) (Object) this;
        if (net.neoforged.neoforge.event.EventHooks.checkMobDespawn(self)) return;
        if (self.level().getDifficulty() == Difficulty.PEACEFUL && this.shouldDespawnInPeaceful()) {
            self.discard();
        } else if (!self.isPersistenceRequired() && !self.requiresCustomPersistence()) {
            Player player = self.level().getNearestPlayer(self, -1.0);
            if (player != null) {
                int despawnDistSqr = self.getType().getCategory().getDespawnDistance();
                despawnDistSqr *= despawnDistSqr;
                double playerDistSqr = player.distanceToSqr(self);
                double distanceFraction = playerDistSqr/despawnDistSqr;
                if (distanceFraction >= 1 && self.removeWhenFarAway(playerDistSqr)) {
                    self.discard();
                }

                int noDespawnDist = self.getType().getCategory().getNoDespawnDistance();
                noDespawnDist *= noDespawnDist;
                if ((double)noDespawnDist >= playerDistSqr) {
                    self.setNoActionTime(0); // dont despawn when close
                } else if (
                        self.getNoActionTime() > 600 &&
                        self.removeWhenFarAway(playerDistSqr)
                ){
                    int despawnProbability = (int) (800 * Math.clamp(1-distanceFraction, 0.05, 1));
                    if(self.getRandom().nextInt(despawnProbability) == 0){
                        self.discard();
                    }
                }
            }
        } else {
            self.setNoActionTime(0);
        }
        ci.cancel();
    }
}
