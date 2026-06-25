package net.spudacious5705.abovethecloudstweaks.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.spudacious5705.abovethecloudstweaks.Abovethecloudstweaks;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BigBubbleParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    float spawnInFade = 1.0f;

    protected BigBubbleParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        var depth = 63 - y;
        this.sprites = sprites;
        double spanmod = this.random.nextGaussian();
        spanmod = spanmod > 0 ? spanmod : -spanmod;
        this.lifetime = (int) (depth * (1f/0.3f));
        this.gravity = 0.00F;
        this.xd = 0;
        this.yd = 0.3;
        this.zd = 0;
        this.hasPhysics = true;
        this.setSpriteFromAge(sprites);
        double gx = this.random.nextGaussian() * 0.5D;
        double gz = this.random.nextGaussian() * 0.5D;
        this.xo += gx;
        this.zo += gz;
        this.x = xo;
        this.z = zo;
        this.setPos(xo, this.y, zo);
        this.scale(2.0f + random.nextInt(2));
        this.alpha = 0f;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_Y;
    }


    public void tick() {
        if(removed)return;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.yo >= 62.5) {
            this.level.addParticle(
                    Abovethecloudstweaks.BURBLE,
                    x,
                    63.875,
                    z,
                    0.0, 0.0, 0.0);
            this.remove();
        } else if (this.age++ >= this.lifetime) {
            this.level.addParticle(
                    ParticleTypes.BUBBLE_POP,
                    x,
                    y,
                    z,
                    0.0, 0.0, 0.0);
            this.remove();
        } else {
            if(spawnInFade > 0f){
                spawnInFade -= 0.1f;
            } else {
                spawnInFade = 0;
            }
            this.alpha = 1f - spawnInFade;
            //this.yd += 0.05;
            double gx = this.random.nextGaussian() * 0.2D;
            double gz = this.random.nextGaussian() * 0.2D;
            this.move(this.xd + gx, this.yd, this.zd + gz);
            this.setSprite(this.sprites.get(this.age % 8, 8));
            //this.setSpriteFromAge(this.sprites);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            super();
            this.sprites = sprites;
        }

        public Particle createParticle(
                SimpleParticleType particleType, ClientLevel cLevel,
                double pX, double pY, double pZ,
                double vX, double vY, double vZ
        ) {
            BigBubbleParticle bar = new BigBubbleParticle(cLevel, pX, pY, pZ, sprites);
            bar.pickSprite(this.sprites);
            bar.scale(1.0F);
            return bar;
        }
    }


}