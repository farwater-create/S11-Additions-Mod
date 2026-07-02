package net.spudacious5705.abovethecloudstweaks;


import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.abovethecloudstweaks.WorldTeleport.CameraOverlay;
import net.spudacious5705.abovethecloudstweaks.particles.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static net.neoforged.neoforge.client.gui.VanillaGuiLayers.CAMERA_OVERLAYS;

@Mod(Abovethecloudstweaks.MODID)
public class Abovethecloudstweaks {

    public static final String MODID = "abovethecloudstweaks";

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    //private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredBlock<Block> VENT_BLOCK = BLOCKS.register("vent_block", VentBlock::new);

    public static final Supplier<BlockEntityType<VentEntity>> VENT_ENTITY =
            BLOCK_ENTITIES.register("vent_be",
                    () -> BlockEntityType.Builder.of(VentEntity::new,
                            VENT_BLOCK.get()
                    ).build(null)
            );

    public static final DeferredItem<BlockItem> VENT_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("vent_block", VENT_BLOCK);


    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, MODID);
    public static final DeferredHolder<MobEffect, MobEffect> LAUNCH_EFFECT =
            MOB_EFFECTS.register("launched", LaunchEffect::sup);

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, MODID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> UPDRAFT_PARTICLE_HOLDER =
        PARTICLE_TYPES.register("updraft_particle", () -> new SimpleParticleType(false));

    public static SimpleParticleType UPDRAFT_PARTICLE;

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BIG_BUBBLE_PARTICLE_HOLDER =
            PARTICLE_TYPES.register("big_bubble", () -> new SimpleParticleType(false));
    public static SimpleParticleType BIG_BUBBLE;

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BURBLE_PARTICLE_HOLDER =
            PARTICLE_TYPES.register("burble", () -> new SimpleParticleType(false));
    public static SimpleParticleType BURBLE;

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> BREEZE_WHIRL_PARTICLE_HOLDER =
            PARTICLE_TYPES.register("breeze_whirl", () -> new SimpleParticleType(false));
    public static SimpleParticleType BREEZE_WHIRL_PARTICLE;

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SURFACE_RIPPLES_PARTICLE_HOLDER =
            PARTICLE_TYPES.register("surface_ripples", () -> new SimpleParticleType(false));
    public static SimpleParticleType SURFACE_RIPPLES_PARTICLE;


    public Abovethecloudstweaks(IEventBus modEventBus, ModContainer modContainer) {

        MobSpawnEventEdits.otherReg(modEventBus);

        BLOCKS.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);

        ITEMS.register(modEventBus);

        MOB_EFFECTS.register(modEventBus);

        PARTICLE_TYPES.register(modEventBus);

        modEventBus.addListener(this::loadComplete);

        //NeoForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void loadComplete(final FMLLoadCompleteEvent event){
        UPDRAFT_PARTICLE = UPDRAFT_PARTICLE_HOLDER.get();
        BIG_BUBBLE = BIG_BUBBLE_PARTICLE_HOLDER.get();
        BREEZE_WHIRL_PARTICLE = BREEZE_WHIRL_PARTICLE_HOLDER.get();
        BURBLE = BURBLE_PARTICLE_HOLDER.get();
        SURFACE_RIPPLES_PARTICLE = SURFACE_RIPPLES_PARTICLE_HOLDER.get();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) event.accept(VENT_BLOCK_ITEM);
    }

    /*@EventBusSubscriber(modid = MODID)
    public static class CommonEvents {
        @SubscribeEvent
        public static void addCreative(BuildCreativeModeTabContentsEvent event) {
            if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) event.accept(VENT_BLOCK_ITEM);
        }
    }*/


    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(UPDRAFT_PARTICLE_HOLDER.get(), UpdraftParticle.Provider::new);
            event.registerSpriteSet(BIG_BUBBLE_PARTICLE_HOLDER.get(), BigBubbleParticle.Provider::new);
            event.registerSpriteSet(BREEZE_WHIRL_PARTICLE_HOLDER.get(), BreezeWhirlParticle.Provider::new);
            event.registerSpriteSet(BURBLE_PARTICLE_HOLDER.get(), BurbleParticle.Provider::new);
            event.registerSpriteSet(SURFACE_RIPPLES_PARTICLE_HOLDER.get(), SurfaceRipplesParticle.Provider::new);
        }
        @SubscribeEvent
        public static void registerGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(CAMERA_OVERLAYS,
                    ResourceLocation.fromNamespaceAndPath(MODID, "portal_overlay"),
                CameraOverlay::renderOverlay
        );
        }
        @SubscribeEvent
        public static void clientTickers(ClientTickEvent.Post event) {
            CameraOverlay.tick();
        }
        /*@SubscribeEvent
        public static void onServerStarting(FMLLoadCompleteEvent event) {
            CameraOverlay.clientLoaded();
        }*/
    }
}
