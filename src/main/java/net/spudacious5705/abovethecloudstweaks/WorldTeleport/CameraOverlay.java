package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;

public class CameraOverlay {
    //private static final ResourceLocation CAMERA_OVERLAY = ResourceLocation.fromNamespaceAndPath(MODID, "textures/gui/camera_overlay.png");

    //private static TextureAtlasSprite textureatlassprite;
    //public static void clientLoaded() {}

    public static void renderOverlay(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if(!effectEnabled) return;

        float strLerp = Mth.lerp(
                deltaTracker.getGameTimeDeltaPartialTick(false),
                strengthOld, strength );

        TextureAtlasSprite textureatlassprite = Minecraft.getInstance()
                .getBlockRenderer()
                .getBlockModelShaper()
                .getParticleIcon(Blocks.NETHER_PORTAL.defaultBlockState());

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, strLerp*0.7f);
        guiGraphics.blit(0, 0, -90, guiGraphics.guiWidth(), guiGraphics.guiHeight(), textureatlassprite);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean effectEnabled = true;

    private static float strength = 0f;
    private static float strengthOld = 0f;
    private static int decayBuffer = 0;

    public static void update(int i) {
        if(i < 0){
            effectEnabled = false;
            decayBuffer = 0;
            strength = 0f;
            strengthOld = 0f;
        }
        decayBuffer = 10;
        effectEnabled = true;
    }

    public static void tick() {
        strengthOld = strength;
        if(decayBuffer < 1){
            if(strengthOld <= 0f){
                if(effectEnabled) {
                    strength = 0f;
                    effectEnabled = false;
                }
                return;
            }
            strength -= 0.025f;
        } else {
            decayBuffer--;
            strength += 0.0125f;
        }
    }

}
