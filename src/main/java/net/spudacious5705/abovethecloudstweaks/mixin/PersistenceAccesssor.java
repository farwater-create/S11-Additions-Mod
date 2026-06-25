package net.spudacious5705.abovethecloudstweaks.mixin;

import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mob.class)
public interface PersistenceAccesssor {

    @Accessor("persistenceRequired")
    void updatePersistenceTo(boolean value);
}
