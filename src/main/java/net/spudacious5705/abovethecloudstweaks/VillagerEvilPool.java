package net.spudacious5705.abovethecloudstweaks;

import fuzs.illagerinvasion.init.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

public class VillagerEvilPool {
    static final Random random = new Random(2026);
    public static EntityType<? extends Mob> getRandomPillager(){
        return options.get(random.nextInt(6)).get();
    }
    static final List<Supplier<EntityType<? extends Mob>>> options = List.of(
            () -> EntityType.PILLAGER,
            () -> EntityType.VINDICATOR,
            ModEntityTypes.NECROMANCER_ENTITY_TYPE::value,
            ModEntityTypes.ALCHEMIST_ENTITY_TYPE::value,
            ModEntityTypes.FIRECALLER_ENTITY_TYPE::value,
            ModEntityTypes.MARAUDER_ENTITY_TYPE::value,
            ModEntityTypes.BASHER_ENTITY_TYPE::value
    );
}
