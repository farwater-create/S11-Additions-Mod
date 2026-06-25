package net.spudacious5705.abovethecloudstweaks.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class SurfaceRipplesParticle extends TextureSheetParticle {

    static final float halfPi = (float) (Math.PI / 2.0);

    private float RADIANS_PER_TICK = Mth.TWO_PI / 600.0f;
    private final SpriteSet sprites;
    float fade = 0f;
    float tickQuadSize = 1f;
    private float growthSpeed = 0.01f;

    Quaternionf baseQuat = new Quaternionf().rotateX(-halfPi);

    protected SurfaceRipplesParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y-0.6f, z);
        this.sprites = sprites;
        this.lifetime = 119;
        this.gravity = 0.00F;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.hasPhysics = false;
        this.setSprite(this.sprites.get(random.nextIntBetweenInclusive(0,4), 4));
        this.y = this.yo = this.y + random.nextFloat()*0.01f;
        this.x = this.xo;
        this.z = this.zo;
        this.x += (random.nextFloat()-0.5f)*8f;
        this.z += (random.nextFloat()-0.5f)*8f;
        this.setPos(this.x, this.y, this.z);
        this.scale(8f);
        this.alpha = 1f;
        this.tickQuadSize = quadSize;

        this.RADIANS_PER_TICK *= 1f + (random.nextFloat()-0.5f)*0.2f;
        this.growthSpeed *= 1f + (random.nextFloat()-0.5f)*0.3f;
    }

    static double calcCentreBoost(double gx, double gz) {
        return 3 - ((gx > 0 ? gx : -gx) + (gz > 0 ? gz : -gz));
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return (i, j, l) -> {};
    }

    @Override
    public Particle scale(float scale) {
        this.quadSize *= scale;
        this.setSize(1F * scale, 1F * scale);
        return this;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if (this.age > 100) {
                fade -= 0.035f;
            } else if (fade < 0.7f){
                fade += 0.05f;
            } else {
                fade = 0.7f;
            }
            this.tickQuadSize += growthSpeed;
            this.alpha = fade;
            this.baseQuat.rotateZ(RADIANS_PER_TICK);
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void render(VertexConsumer vCons, Camera camera, float delta) {

        if(age < 100){
            if(fade < 0.7f){
                this.alpha = fade + Mth.lerp(delta, 0f, 0.05f);
            }
        } else {
            this.alpha = fade - Mth.lerp(delta, 0f, 0.035f);
        }

        this.quadSize = Mth.lerp(delta, 0f, growthSpeed) + tickQuadSize;

        float r = Mth.lerp(delta, 0, RADIANS_PER_TICK);
        Quaternionf quat = new Quaternionf(baseQuat).rotateZ(r);

        this.renderRotatedQuad(vCons, camera, quat, delta);
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
            SurfaceRipplesParticle bar = new SurfaceRipplesParticle(cLevel, pX, pY, pZ, sprites);
            bar.pickSprite(this.sprites);
            bar.scale(1.0F);
            return bar;
        }
    }

}