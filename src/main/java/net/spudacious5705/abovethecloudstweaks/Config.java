package net.spudacious5705.abovethecloudstweaks;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
@EventBusSubscriber(modid = Abovethecloudstweaks.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue LOG_DIRT_BLOCK = BUILDER.comment("Whether to log the dirt block on common setup").define("logDirtBlock", true);

    private static final ModConfigSpec.DoubleValue WATER_MAX_LAUNCH_SPEED_CFG = BUILDER.comment("The max velocity an underwater vent can impart onto an entity")
            .defineInRange("water_max_launch_speed", 8.0, 4.0, 100.0);

    private static final ModConfigSpec.DoubleValue WATER_SPEED_INCREASE_FRACTION_CFG = BUILDER.comment("Essentially, the rate at which an entity is brought up to max velocity")
            .defineInRange("water_speed_increase_fraction", 0.4, 0.001, 1.0);

    public static final ModConfigSpec.ConfigValue<String> MAGIC_NUMBER_INTRODUCTION = BUILDER.comment("What you want the introduction message to be for the magic number").define("magicNumberIntroduction", "The magic number is... ");

    // a list of strings that are treated as resource locations for items
    private static final ModConfigSpec.ConfigValue<List<? extends String>> ITEM_STRINGS = BUILDER.comment("A list of items to log on common setup.").defineListAllowEmpty("items", List.of("minecraft:iron_ingot"), Config::validateItemName);

    static final ModConfigSpec SPEC = BUILDER.build();

    public static boolean logDirtBlock;
    public static String magicNumberIntroduction;
    public static Set<Item> items;

    public static float WATER_MAX_LAUNCH_SPEED;
    public static float WATER_SPEED_INCREASE_FRACTION;


    private static boolean validateItemName(final Object obj) {
        return obj instanceof String itemName && BuiltInRegistries.ITEM.containsKey(ResourceLocation.parse(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        logDirtBlock = LOG_DIRT_BLOCK.get();
        magicNumberIntroduction = MAGIC_NUMBER_INTRODUCTION.get();

        double foo = WATER_MAX_LAUNCH_SPEED_CFG.get();
        WATER_MAX_LAUNCH_SPEED = (float) foo;
        foo = WATER_SPEED_INCREASE_FRACTION_CFG.get();
        WATER_SPEED_INCREASE_FRACTION = (float) foo;

        // convert the list of strings into a set of items
        items = ITEM_STRINGS.get().stream().map(itemName -> BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemName))).collect(Collectors.toSet());
    }
}
