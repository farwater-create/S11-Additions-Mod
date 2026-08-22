package net.spudacious5705.abovethecloudstweaks.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class UpdraftParticle extends TextureSheetParticle {

    private final SpriteSet sprites;

    float spawnInFade = 1.0f;

    protected UpdraftParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        double spanmod = this.random.nextGaussian();
        spanmod = spanmod > 0 ? spanmod : -spanmod;
        this.lifetime = 12 + (int) (16 * spanmod);
        this.gravity = 0.00F;
        this.xd = 0;
        this.yd = 0.3;
        this.zd = 0;
        this.hasPhysics = false;
        this.setSprite(this.sprites.get(0, 1));
        double gx = this.random.nextGaussian() * 0.5D;
        double gz = this.random.nextGaussian() * 0.5D;
        this.xo += gx;
        this.zo += gz;
        this.x = xo;
        this.z = zo;
        this.setPos(xo, this.y, zo);
        this.yd *= calcCentreBoost(gx,gz);
        this.scale(2.0f);
        this.alpha = 0f;
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return SingleQuadParticle.FacingCameraMode.LOOKAT_Y;
    }

    static double calcCentreBoost(double gx, double gz) {
        return 3 - ((gx > 0 ? gx : -gx) + (gz > 0 ? gz : -gz));
    }

    @Override
    public Particle scale(float scale) {
        this.quadSize *= scale;
        this.setSize(0.2F * scale, 1.6F * scale);
        return this;
    }

    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            if(spawnInFade > 0f){
                spawnInFade -= 0.1f;
            } else {
                spawnInFade = 0;
            }
            float a = (this.lifetime - this.age);
            a = a/this.lifetime;
            this.alpha = a - spawnInFade + 0.2f;
            //this.yd += 0.05;
            this.move(this.xd, this.yd, this.zd);
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
            UpdraftParticle bar = new UpdraftParticle(cLevel, pX, pY, pZ, sprites);
            bar.pickSprite(this.sprites);
            bar.scale(1.0F);
            return bar;
        }
    }


    @Override
    public void render(VertexConsumer vCons, Camera camera, float d) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp((double)d, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp((double)d, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp((double)d, this.zo, this.z) - vec3.z());

        Quaternionf quat = new Quaternionf();
        this.getFacingCameraMode().setRotation(quat, camera, d);
        if (this.roll != 0.0F) {
            quat.rotateZ(Mth.lerp(d, this.oRoll, this.roll));
        }

        renderRotatedQuadCustom(vCons, quat, f,f1,f2,d);
    }

    protected void renderRotatedQuadCustom(VertexConsumer vCons, Quaternionf quat, float x, float y, float z, float d) {
        float f = this.getQuadSize(d);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(d);
        this.renderVertex(vCons, quat, x, y, z, 0.5F, -8.0F, f, f2, f4, i);
        this.renderVertex(vCons, quat, x, y, z, 0.5F, 8.0F, f, f2, f3, i);
        this.renderVertex(vCons, quat, x, y, z, -0.5F, 8.0F, f, f1, f3, i);
        this.renderVertex(vCons, quat, x, y, z, -0.5F, -8.0F, f, f1, f4, i);
    }

    private void renderVertex(VertexConsumer vCons, Quaternionf quat, float x, float y, float z, float offsetX, float offsetY, float scalar, float u, float v, int light) {
        Vector3f vector3f = (new Vector3f(offsetX, offsetY, 0.0F)).rotate(quat).mul(scalar).add(x, y, z);
        vCons.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
    }

}