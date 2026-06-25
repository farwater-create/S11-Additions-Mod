package net.spudacious5705.abovethecloudstweaks.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class BreezeWhirlParticle extends TextureSheetParticle {

    private static final float RADIANS_PER_TICK = Mth.TWO_PI / 40.0f;
    private final SpriteSet sprites;
    float spawnInFade = 0f;
    float rotation = 0f;
    float rotationOld = 0f;
    static final float halfPi = (float) (Math.PI / 2.0);

    protected BreezeWhirlParticle(ClientLevel level, double x, double y, double z, SpriteSet sprites) {
        super(level, x, y, z);
        this.sprites = sprites;
        this.gravity = 0.00F;
        this.xd = 0;
        this.yd = 0.3;
        this.zd = 0;
        this.hasPhysics = false;
        this.setSprite(this.sprites.get(0, 1));
        this.x = xo;
        this.z = zo;
        this.setPos(xo, this.y, zo);
        this.scale(3.5f);
        this.alpha = 0f;
    }

    static double calcCentreBoost(double gx, double gz) {
        return 3 - ((gx > 0 ? gx : -gx) + (gz > 0 ? gz : -gz));
    }

    @Override
    public FacingCameraMode getFacingCameraMode() {
        return FacingCameraMode.LOOKAT_Y;
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
        if (this.yo >= 62.5) {
            this.scale(0.8f);
        }
        if (this.yo >= 62.5 +3) {
            this.remove();
        } else {
            if (spawnInFade < 1f) {
                spawnInFade += 0.05f;
            } else {
                spawnInFade = 1f;
            }
            this.alpha = spawnInFade;
            this.move(this.xd, this.yd, this.zd);
            tickRotation();
        }
    }

    public void tickRotation() {
        // 1. Save the current rotation as the old rotation
        this.rotationOld = this.rotation;

        // 2. Advance the rotation in radians for the new tick
        this.rotation += RADIANS_PER_TICK;

        // 3. Keep the value within the 0 to 2*PI range to prevent float overflow
        if (this.rotation >= Mth.TWO_PI) {
            this.rotation -= Mth.TWO_PI;
            this.rotationOld -= Mth.TWO_PI;
        }
    }

    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void render(VertexConsumer vCons, Camera camera, float delta) {

        float r = Mth.lerp(delta, rotationOld, rotation);

        Vec3 vec3 = camera.getPosition();
        float xv = (float)(Mth.lerp(delta, this.xo, this.x) - vec3.x());
        float yv = (float)(Mth.lerp(delta, this.yo, this.y) - vec3.y());
        float zv = (float)(Mth.lerp(delta, this.zo, this.z) - vec3.z());

        Quaternionf baseQuat = new Quaternionf().rotateY(r);

        for (int faceIndex = 0; faceIndex < 4; faceIndex++) {
            Quaternionf faceQuat = new Quaternionf(baseQuat);

            faceQuat.rotateY(faceIndex * halfPi);

            float flip = 1f;
            if(!ParticleUtils.isQuadFacingAwayFromCamera(this.getPos().toVector3f(), faceQuat, camera)){
                faceQuat.rotateY((float) Math.PI);
                flip = -1f;
            }

            this.renderRotatedQuad(vCons, faceQuat, xv,yv,zv, delta, flip);
        }
    }

    protected void renderRotatedQuad(VertexConsumer vCons, Quaternionf quat, float x, float y, float z, float d, float flip) {
        float f = this.getQuadSize(d);
        float f1 = this.getU0();
        float f2 = this.getU1();
        float f3 = this.getV0();
        float f4 = this.getV1();
        int i = this.getLightColor(d);
        this.renderVert(vCons, quat, x, y, z, 1F, -0.25F, f, f2, f4, i, flip);
        this.renderVert(vCons, quat, x, y, z, 1F, 0.25F, f, f2, f3, i, flip);
        this.renderVert(vCons, quat, x, y, z, -1F, 0.25F, f, f1, f3, i, flip);
        this.renderVert(vCons, quat, x, y, z, -1F, -0.25F, f, f1, f4, i, flip);
    }

    private void renderVert(VertexConsumer vCons, Quaternionf quat, float x, float y, float z, float offsetX, float offsetY, float scalar, float u, float v, int light, float flip) {
        Vector3f vector3f = (new Vector3f(offsetX, offsetY, flip))//offset added here
                .rotate(quat).mul(scalar).add(x, y, z);
        vCons.addVertex(vector3f.x(), vector3f.y(), vector3f.z()).setUv(u, v).setColor(this.rCol, this.gCol, this.bCol, this.alpha).setLight(light);
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
            BreezeWhirlParticle bar = new BreezeWhirlParticle(cLevel, pX, pY, pZ, sprites);
            bar.pickSprite(this.sprites);
            bar.scale(1.0F);
            return bar;
        }
    }

}