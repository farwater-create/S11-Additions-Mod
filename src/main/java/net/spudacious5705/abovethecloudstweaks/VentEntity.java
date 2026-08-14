package net.spudacious5705.abovethecloudstweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import tictim.paraglider.api.movement.Movement;

import java.util.List;
import java.util.function.Predicate;

import static net.spudacious5705.abovethecloudstweaks.Config.*;

public class VentEntity extends BlockEntity {

    protected final RandomSource random;

    private static final Predicate<Entity> EFFECT_PREDICATE =
            EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE);

    static final float targetVelocityGliding = 2.5f;
    static final float targetVelocityNormal = 0.6f;


    int lazyCounter = 8;
    int lazyCooldown = 120;

    public static BlockEntityTicker<VentEntity> SERVER_TICKER = (level, pos, state, entity) -> {

        if(entity.lazyCounter > 0){
            entity.lazyCounter--;
            return;
        }

        int heightThis = pos.above().getY();


        AABB aabb = new AABB(pos.above()).expandTowards(0.0, heightThis > 62? ventDraughtHeight : 62-heightThis, 0.0);
        List<Entity> entities = level.getEntitiesOfClass(Entity.class, aabb, EFFECT_PREDICATE);

        if(entities.isEmpty()){
            if(entity.lazyCooldown < 1)
                 entity.lazyCounter = 8;
            else entity.lazyCooldown--;
            return;
        }
        entity.lazyCooldown = 120;


        if( heightThis > 62 ){
            launch_air(entities, heightThis);
        } else {
            launch_water(entities, heightThis);
        }

    };

    public static void launch_air(List<Entity> entities, int heightThis){

        for (Entity entityToBeLaunched : entities) {
            Vec3 entityVelocity = entityToBeLaunched.getDeltaMovement();

            float targetVelocity = targetVelocityNormal;
            if (entityToBeLaunched instanceof Player player) {
                Movement paragliderMovement = Movement.get(player);
                if (paragliderMovement.state().paragliding())
                    targetVelocity = targetVelocityGliding;
            }

            double v = (targetVelocity - Math.clamp(entityVelocity.y, 0f, targetVelocity))*0.1f;


            float aboveBlockHeight = (float) (entityToBeLaunched.position().y() - heightThis);
            if(aboveBlockHeight < 1.5f){
                if(aboveBlockHeight < 0.4f){
                    v += 0.04f;
                }
                v *= 1.5;
            }

            entityToBeLaunched.addDeltaMovement(new Vec3(0.0, v, 0.0));
            entityToBeLaunched.hurtMarked = true;
            entityToBeLaunched.hasImpulse = true;
            entityToBeLaunched.checkSlowFallDistance();
            entityToBeLaunched.fallDistance = Math.min(entityToBeLaunched.fallDistance, aboveBlockHeight+2);
        }
    }

    public static void launch_water(List<Entity> entities, int heightThis){

        for (Entity entityToBeLaunched : entities) {
            Vec3 entityVelocity = entityToBeLaunched.getDeltaMovement();

            float targetVelocity = targetVelocityNormal*WATER_MAX_LAUNCH_SPEED;

            double v = (targetVelocity - Math.clamp(entityVelocity.y, 0f, targetVelocity))*WATER_SPEED_INCREASE_FRACTION;


            float aboveBlockHeight = (float) (entityToBeLaunched.position().y() - heightThis);
            if(aboveBlockHeight < 1.5f){
                if(aboveBlockHeight < 0.4f){
                    v += 0.04f;
                }
                v *= 1.5;
            }

            if (entityToBeLaunched instanceof LivingEntity entity) {
                MobEffectInstance effectInstance = new MobEffectInstance(
                        Abovethecloudstweaks.LAUNCH_EFFECT,
                        80, // Duration (200 ticks = 10 seconds)
                        0,   // Amplifier (0 is level 1, 1 is level 2)
                        false, // Ambient effect
                        true,  // Show particles
                        true   // Show icon
                );
                entity.addEffect(effectInstance);
            }

            entityToBeLaunched.addDeltaMovement(new Vec3(0.0, v, 0.0));
            entityToBeLaunched.hurtMarked = true;
            entityToBeLaunched.hasImpulse = true;
            entityToBeLaunched.checkSlowFallDistance();
            entityToBeLaunched.fallDistance = Math.min(entityToBeLaunched.fallDistance, aboveBlockHeight+2);
        }
    }

    private long lastVisualTick = -1L;
    private long lastSoundTick = -1L;
    public static BlockEntityTicker<VentEntity> CLIENT_TICKER = (level, pos, state, entity) -> {

        int heightThis = pos.above().getY();
        if( heightThis > 62 ){
            // effects air

            long gameTime = level.getGameTime();
            long particleTime = gameTime - entity.lastVisualTick;
            long soundTime = gameTime - entity.lastSoundTick;
            if (particleTime % 20L > 1L) {
                entity.lastVisualTick = gameTime;
                entity.spawnParticleAIR(level, pos.above(1));
            }
            if (soundTime > 10L) {
                entity.lastSoundTick = gameTime + entity.random.nextIntBetweenInclusive(5, 12);
                float pitch = 0.5F + 0.4F * entity.random.nextFloat();
                float volume = 0.3F + 0.4F * entity.random.nextFloat();
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BREEZE_IDLE_GROUND, SoundSource.BLOCKS,
                        volume, pitch, false);
            }

        } else {
            // effects water

            long gameTime = level.getGameTime();
            long particleTime = gameTime - entity.lastVisualTick;
            long soundTime = gameTime - entity.lastSoundTick;
            if (particleTime % 20L > 1L) {
                entity.lastVisualTick = gameTime;
                entity.spawnParticleWATER(level, pos.above(1));
                //entity.spawnParticleWATER(level, pos.mutable().setY(63));
            }
            if (soundTime % 40L > 1L) {
                entity.lastSoundTick = gameTime;
                float pitch = 0.7F + 0.4F * entity.random.nextFloat();
                float volume = 0.4F + 0.2F * entity.random.nextFloat();
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS,
                        volume, pitch, false);

                if(entity.random.nextFloat() > 0.7f) return;
                pitch = 0.1F + 0.4F * entity.random.nextFloat();
                volume = 0.4F + 0.2F * entity.random.nextFloat();
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        62,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BUBBLE_COLUMN_WHIRLPOOL_AMBIENT, SoundSource.BLOCKS,
                        volume, pitch, false);
            }

        }
    };


    private void spawnParticleAIR(Level level, BlockPos sourcePos) {


        level.addParticle(
                ParticleTypes.SMALL_GUST,
                (double)sourcePos.getX() + 0.5 + random.nextGaussian() * 0.4D,
                sourcePos.getY() + 2 + random.nextGaussian(),
                (double)sourcePos.getZ() + 0.5 + random.nextGaussian() * 0.4D,
                0.0, 0.0, 0.0);

        if (random.nextFloat() < 0.5f) return;

        level.addParticle(
                Abovethecloudstweaks.UPDRAFT_PARTICLE,
                (double)sourcePos.getX() + 0.5,
                sourcePos.getY(),
                (double)sourcePos.getZ() + 0.5,
                0.0, 1.0, 0.0);

        if (random.nextFloat() < 0.5f) return;
        level.addParticle(
                ParticleTypes.CLOUD,
                (double)sourcePos.getX() + 0.5,
                sourcePos.getY(),
                (double)sourcePos.getZ() + 0.5,
                0.0, 0.4, 0.0);

    }

    private int whirl_release = 0;
    private void spawnParticleWATER(Level level, BlockPos sourcePos) {


        level.addParticle(
                Abovethecloudstweaks.BIG_BUBBLE,
                (double)sourcePos.getX() + 0.5,
                sourcePos.getY(),
                (double)sourcePos.getZ() + 0.5,
                0.0, 0.4, 0.0);

        if (random.nextFloat() < 0.2f) {
            level.addParticle(
                    Abovethecloudstweaks.SURFACE_RIPPLES_PARTICLE,
                    true,
                    (double)sourcePos.getX() + 0.5,
                    63.5,
                    (double)sourcePos.getZ() + 0.5,
                    0.0, 0.0, 0.0);
        }


        if(whirl_release > 0){
            whirl_release--;

            level.addParticle(
                    Abovethecloudstweaks.BREEZE_WHIRL_PARTICLE,
                    (double)sourcePos.getX() + 0.5,
                    sourcePos.getY(),
                    (double)sourcePos.getZ() + 0.5,
                    0.0, 0.4, 0.0);

            return;
        }
        if (random.nextFloat() < 0.8f) return;

        whirl_release = random.nextIntBetweenInclusive(2,4);


    }

    public VentEntity(BlockPos pos, BlockState blockState) {
        super(Abovethecloudstweaks.VENT_ENTITY.get(), pos, blockState);
        this.random = RandomSource.create();
    }
}
