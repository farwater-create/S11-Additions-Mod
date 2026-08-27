package net.spudacious5705.abovethecloudstweaks.mixin;

import com.github.alexthe666.alexsmobs.entity.EntityMurmur;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ServerLevelAccessor;
import net.spudacious5705.abovethecloudstweaks.MobSpawnEventEdits;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityMurmur.class)
public abstract class AlexMurmur {


    @Inject(
            method = "checkMurmurSpawnRules",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void spawnRules(EntityType<EntityMurmur> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason,
                                   BlockPos pos, RandomSource random, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
                MobSpawnEventEdits.checkMonsterSpawnRules(
                entityType, iServerWorld, reason,
                pos, random)
        );
        cir.cancel();
    }


}
