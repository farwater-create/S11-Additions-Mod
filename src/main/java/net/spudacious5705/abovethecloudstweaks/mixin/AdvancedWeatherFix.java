package net.spudacious5705.abovethecloudstweaks.mixin;

import com.tterrag.registrate.builders.FluidBuilder;
import net.antopfr.advancedweather.AdvancedWeather;
import net.antopfr.advancedweather.content.fluid.AWFluids;
import net.antopfr.advancedweather.util.AWRegistrate;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.tterrag.registrate.util.entry.FluidEntry;

@Mixin(AWFluids.class)
public class AdvancedWeatherFix {


    @Redirect(
            method = "<clinit>",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/tterrag/registrate/builders/FluidBuilder;register()Lcom/tterrag/registrate/util/entry/FluidEntry;"
            )
    )
    private static FluidEntry<?> overrideMercuryInitialization(FluidBuilder instance) {
        return AWRegistrate.get()
                .fluid("mercury",
                        ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "block/mercury_still"),
                        ResourceLocation.fromNamespaceAndPath(AdvancedWeather.MOD_ID, "block/mercury_flow"))
                .properties(p -> p
                        .density(13500)
                        .viscosity(1500)
                        .canSwim(false)
                        .canDrown(false)
                        .canConvertToSource(false))
                // removed .noBlock() and .noBucket()
                .register();
    }

}
