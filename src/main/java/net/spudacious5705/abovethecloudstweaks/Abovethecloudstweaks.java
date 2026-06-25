package net.spudacious5705.abovethecloudstweaks;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.spudacious5705.abovethecloudstweaks.particles.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.function.Supplier;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Abovethecloudstweaks.MODID)
public class Abovethecloudstweaks {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "abovethecloudstweaks";

    public static ResourceLocation id(@NotNull String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "abovethecloudstweaks" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);


    // Create a Deferred Register to hold Items which will all be registered under the "abovethecloudstweaks" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "abovethecloudstweaks" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "abovethecloudstweaks:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.register("vent_block", VentBlock::new);
            //BlockBehaviour.Properties.of().mapColor(MapColor.STONE));

    public static final Supplier<BlockEntityType<VentEntity>> VENT_ENTITY =
            BLOCK_ENTITIES.register("vent_be",
                    () -> BlockEntityType.Builder.of(VentEntity::new,
                            EXAMPLE_BLOCK.get()
                    ).build(null)
            );

    // Creates a new BlockItem with the id "abovethecloudstweaks:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "abovethecloudstweaks:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "abovethecloudstweaks:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.abovethecloudstweaks")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
    }).build());


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


    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public Abovethecloudstweaks(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        //todo addmeback! MobSpawnEventEdits.otherReg(modEventBus);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);

        BLOCK_ENTITIES.register(modEventBus);

        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        MOB_EFFECTS.register(modEventBus);

        PARTICLE_TYPES.register(modEventBus);

        modEventBus.addListener(this::loadComplete);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (Abovethecloudstweaks) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock) LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));


        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    private void loadComplete(final FMLLoadCompleteEvent event){
        UPDRAFT_PARTICLE = UPDRAFT_PARTICLE_HOLDER.get();
        BIG_BUBBLE = BIG_BUBBLE_PARTICLE_HOLDER.get();
        BREEZE_WHIRL_PARTICLE = BREEZE_WHIRL_PARTICLE_HOLDER.get();
        BURBLE = BURBLE_PARTICLE_HOLDER.get();
        SURFACE_RIPPLES_PARTICLE = SURFACE_RIPPLES_PARTICLE_HOLDER.get();
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) event.accept(EXAMPLE_BLOCK_ITEM);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    @EventBusSubscriber(modid = MODID, value = net.neoforged.api.distmarker.Dist.CLIENT)
    public static class ClientParticleEvents {
        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(UPDRAFT_PARTICLE_HOLDER.get(), UpdraftParticle.Provider::new);
            event.registerSpriteSet(BIG_BUBBLE_PARTICLE_HOLDER.get(), BigBubbleParticle.Provider::new);
            event.registerSpriteSet(BREEZE_WHIRL_PARTICLE_HOLDER.get(), BreezeWhirlParticle.Provider::new);
            event.registerSpriteSet(BURBLE_PARTICLE_HOLDER.get(), BurbleParticle.Provider::new);
            event.registerSpriteSet(SURFACE_RIPPLES_PARTICLE_HOLDER.get(), SurfaceRipplesParticle.Provider::new);
            // Alternatively, replace with a custom TextureSheetParticle.Provider if you have one
        }
    }
}
