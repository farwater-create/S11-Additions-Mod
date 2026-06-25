package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(BaseFireBlock.class)
public class Fire {

    @Inject(
            method = "onPlace",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cutPortalLogic(
            BlockState state, Level level, BlockPos pos,
            BlockState oldState, boolean isMoving,
            CallbackInfo ci
    ) {
        if (
                !oldState.is(state.getBlock())
                &&
                !state.canSurvive(level, pos)
        ) {
            level.removeBlock(pos, false);
        }

        ci.cancel();
    }

}
