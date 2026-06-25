package net.spudacious5705.abovethecloudstweaks;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class LaunchEffect extends MobEffect {

    public LaunchEffect() {
        super(MobEffectCategory.BENEFICIAL, 0xF0F0FF);
    }

    public static MobEffect sup(ResourceLocation resourceLocation) {
        return new LaunchEffect()
                .addAttributeModifier(
                        Attributes.GRAVITY,
                        resourceLocation,
                        -0.9,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );
    }
}

