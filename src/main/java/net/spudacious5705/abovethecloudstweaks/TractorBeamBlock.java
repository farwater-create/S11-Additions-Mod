package net.spudacious5705.abovethecloudstweaks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;


public class TractorBeamBlock extends Block {

    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        entity.onInsideBubbleColumn(false);
    }

    protected TractorBeamBlock() {
        super(
                Properties.of()
                        .mapColor(MapColor.COLOR_LIGHT_BLUE)
                        .noCollission()
                        .forceSolidOn()
                        .noOcclusion()
                        .pushReaction(PushReaction.BLOCK)
                        .strength(0f)
        );
        this.registerDefaultState(this.stateDefinition.any());
    }

    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(100) == 0) {

            level.addParticle(ParticleTypes.END_ROD,
                    (double) pos.getX() + random.nextDouble(),
                    (double) pos.getY() + random.nextDouble(),
                    (double) pos.getZ() + random.nextDouble(), 0.0, 1.0, 0.0);


        }

    }
}

