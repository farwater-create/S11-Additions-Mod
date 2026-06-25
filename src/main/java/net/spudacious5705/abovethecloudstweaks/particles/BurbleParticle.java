package net.spudacious5705.abovethecloudstweaks.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BurbleParticle extends TextureSheetParticle {

    private final SpriteSet sprites;


    protected BurbleParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, 63.5, z);
        this.sprites = sprites;
        this.lifetime = 8 * 1;
        this.gravity = 0.00F;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.hasPhysics = false;
        this.setSpriteFromAge(this.sprites);
        this.quadSize = 0.1F;
        int scale = random.nextInt(8);
        this.scale(6.0f + scale);
        float add = scale*0.1f;
        this.setPos(x, 63.5+add, z);
        this.yo = this.y;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_Y;
    }


    public void tick() {
        if(removed)return;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.setSpriteFromAge(this.sprites);
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
            BurbleParticle bar = new BurbleParticle(cLevel, pX, pY, pZ, sprites);
            return bar;
        }
    }


}