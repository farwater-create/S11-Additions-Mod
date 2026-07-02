package net.spudacious5705.abovethecloudstweaks;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;


@EventBusSubscriber(modid = Abovethecloudstweaks.MODID)
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.DoubleValue WATER_MAX_LAUNCH_SPEED_CFG = BUILDER.comment("The max velocity an underwater vent can impart onto an entity")
            .defineInRange("water_max_launch_speed", 8.0, 4.0, 100.0);

    private static final ModConfigSpec.DoubleValue WATER_SPEED_INCREASE_FRACTION_CFG = BUILDER.comment("Essentially, the rate at which an entity is brought up to max velocity")
            .defineInRange("water_speed_increase_fraction", 0.4, 0.001, 1.0);


    static final ModConfigSpec SPEC = BUILDER.build();

    public static float WATER_MAX_LAUNCH_SPEED;
    public static float WATER_SPEED_INCREASE_FRACTION;



    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        double foo = WATER_MAX_LAUNCH_SPEED_CFG.get();
        WATER_MAX_LAUNCH_SPEED = (float) foo;
        foo = WATER_SPEED_INCREASE_FRACTION_CFG.get();
        WATER_SPEED_INCREASE_FRACTION = (float) foo;
    }
}
