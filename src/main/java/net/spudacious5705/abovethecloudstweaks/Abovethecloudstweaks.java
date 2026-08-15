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

    public static final DeferredBlock<Block> VENT_BLOCK = BLOCKS.register("vent_stone", VentBlock::new);
    public static final DeferredBlock<Block> VENT_SANDSTONE = BLOCKS.register("vent_sandstone", VentBlock::new);
    public static final DeferredBlock<Block> VENT_KAOLIN = BLOCKS.register("vent_kaolin", VentBlock::new);
    public static final DeferredBlock<Block> VENT_GRANITE = BLOCKS.register("vent_granite", VentBlock::new);
    public static final DeferredBlock<Block> VENT_CHERT = BLOCKS.register("vent_chert", VentBlock::new);
    public static final DeferredBlock<Block> VENT_ANDESITE = BLOCKS.register("vent_andesite", VentBlock::new);
    public static final DeferredBlock<Block> VENT_TERRACOTTA = BLOCKS.register("vent_terracotta", VentBlock::new);
    public static final DeferredBlock<Block> VENT_TRAVERTINE = BLOCKS.register("vent_travertine", VentBlock::new);
    public static final DeferredBlock<Block> VENT_PINK_SANDSTONE = BLOCKS.register("vent_pink_sandstone", VentBlock::new);
    public static final DeferredBlock<Block> VENT_RED_SANDSTONE = BLOCKS.register("vent_red_sandstone", VentBlock::new);
    public static final DeferredBlock<Block> VENT_CHALK = BLOCKS.register("vent_chalk", VentBlock::new);

    public static final Supplier<BlockEntityType<VentEntity>> VENT_ENTITY =
            BLOCK_ENTITIES.register("vent_be",
                    () -> BlockEntityType.Builder.of(VentEntity::new,
                            VENT_BLOCK.get(), VENT_SANDSTONE.get(), VENT_KAOLIN.get(), VENT_GRANITE.get(),
                            VENT_CHERT.get(), VENT_ANDESITE.get(), VENT_TERRACOTTA.get(), VENT_TRAVERTINE.get(),
                            VENT_PINK_SANDSTONE.get(), VENT_RED_SANDSTONE.get(), VENT_CHALK.get()
                    ).build(null)
            );

    public static final DeferredItem<BlockItem> VENT_STONE_ITEM = ITEMS.registerSimpleBlockItem("vent_stone", VENT_BLOCK);
    public static final DeferredItem<BlockItem> VENT_SANDSTONE_ITEM = ITEMS.registerSimpleBlockItem("vent_sandstone", VENT_SANDSTONE);
    public static final DeferredItem<BlockItem> VENT_KAOLIN_ITEM = ITEMS.registerSimpleBlockItem("vent_kaolin", VENT_KAOLIN);
    public static final DeferredItem<BlockItem> VENT_GRANITE_ITEM = ITEMS.registerSimpleBlockItem("vent_granite", VENT_GRANITE);
    public static final DeferredItem<BlockItem> VENT_CHERT_ITEM = ITEMS.registerSimpleBlockItem("vent_chert", VENT_CHERT);
    public static final DeferredItem<BlockItem> VENT_ANDESITE_ITEM = ITEMS.registerSimpleBlockItem("vent_andesite", VENT_ANDESITE);
    public static final DeferredItem<BlockItem> VENT_TERRACOTTA_ITEM = ITEMS.registerSimpleBlockItem("vent_terracotta", VENT_TERRACOTTA);
    public static final DeferredItem<BlockItem> VENT_TRAVERTINE_ITEM = ITEMS.registerSimpleBlockItem("vent_travertine", VENT_TRAVERTINE);
    public static final DeferredItem<BlockItem> VENT_PINK_SANDSTONE_ITEM = ITEMS.registerSimpleBlockItem("vent_pink_sandstone", VENT_PINK_SANDSTONE);
    public static final DeferredItem<BlockItem> VENT_RED_SANDSTONE_ITEM = ITEMS.registerSimpleBlockItem("vent_red_sandstone", VENT_RED_SANDSTONE);
    public static final DeferredItem<BlockItem> VENT_CHALK_ITEM = ITEMS.registerSimpleBlockItem("vent_chalk", VENT_CHALK);


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
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
            event.accept(VENT_STONE_ITEM);
            event.accept(VENT_SANDSTONE_ITEM);
            event.accept(VENT_KAOLIN_ITEM);
            event.accept(VENT_GRANITE_ITEM);
            event.accept(VENT_CHERT_ITEM);
            event.accept(VENT_ANDESITE_ITEM);
            event.accept(VENT_TERRACOTTA_ITEM);
            event.accept(VENT_TRAVERTINE_ITEM);
            event.accept(VENT_PINK_SANDSTONE_ITEM);
            event.accept(VENT_RED_SANDSTONE_ITEM);
            event.accept(VENT_CHALK_ITEM);

        }
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
