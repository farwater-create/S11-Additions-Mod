package net.spudacious5705.abovethecloudstweaks.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherPortalBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NetherPortalBlock.class)
public class Portal {

    @Inject(
            method = "randomTick",
            at = @At("HEAD"),
            cancellable = true
    )
    private void destroyPortalOnTick(
            BlockState state, ServerLevel level, BlockPos pos,
            RandomSource rand,
            CallbackInfo ci
    ) {
        level.removeBlock(pos, false);
        ci.cancel();
    }

    @Inject(
            method = "updateShape",
            at = @At("HEAD"),
            cancellable = true
    )
    private void destroyPortalOnUpdate(
            BlockState p_54928_, Direction p_54929_,
            BlockState p_54930_, LevelAccessor p_54931_,
            BlockPos p_54932_, BlockPos p_54933_,
            CallbackInfoReturnable<BlockState> ci
    ) {
        ci.setReturnValue(Blocks.AIR.defaultBlockState());
        ci.cancel();
    }

    @Inject(
            method = "entityInside",
            at = @At("HEAD"),
            cancellable = true
    )
    private void destroyPortalEntityCollide(
            BlockState state, Level level, BlockPos pos,
            Entity theMfTrynaCrashTheServer, CallbackInfo ci
    ) {
        level.removeBlock(pos, false);
        ci.cancel();
    }

}
