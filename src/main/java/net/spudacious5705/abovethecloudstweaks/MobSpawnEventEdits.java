package net.spudacious5705.abovethecloudstweaks;

import com.github.L_Ender.cataclysm.init.ModEntities;
import fuzs.illagerinvasion.init.ModEntityTypes;
import net.mcreator.brazillegends.init.BrazilLegendsModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent.Operation;

import static net.minecraft.world.entity.Mob.checkMobSpawnRules;


public class MobSpawnEventEdits {

    public static void otherReg(IEventBus modEventBus) {
            mod("cataclysm").register(new CataclysmEdit());
            mod("brazil_legends").register(new LegendsEdit());
            mod("illagerinvasion").register(new IllagerEdit());
    }

    static IEventBus mod(String id){
        return ModList.get().getModContainerById(id).get().getEventBus();
    }

    public static boolean checkMonsterSpawnRules(
            EntityType<? extends Monster> m, ServerLevelAccessor level, MobSpawnType spawnType,
            BlockPos pos, RandomSource rand
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && (
                lightLevelCheck(level, pos, rand, spawnType)
                ) &&
                checkMobSpawnRules(m, level, spawnType, pos, rand);
    }

    public static boolean lightLevelCheck(ServerLevelAccessor level, BlockPos pos, RandomSource random, MobSpawnType spawnType) {
        if(level.dimensionType().hasSkyLight()) {
            if (level.getBrightness(LightLayer.SKY, pos) < 8) {
                return false;
            } else {
                DimensionType dimensiontype = level.dimensionType();
                int i = dimensiontype.monsterSpawnBlockLightLimit();
                if (i < 15 && level.getBrightness(LightLayer.BLOCK, pos) > i) {
                    return false;
                } else {
                    int j = level.getLevel().isThundering() ? level.getMaxLocalRawBrightness(pos, 10) : level.getMaxLocalRawBrightness(pos);
                    return j <= dimensiontype.monsterSpawnLightTest().sample(random);
                }
            }
        } else {
            return MobSpawnType.ignoresLightRequirements(spawnType) || Monster.isDarkEnoughToSpawn(level,pos,random);
        }
    }

    public static boolean checkIllagerSpawnRules(
            EntityType<? extends Monster> m, ServerLevelAccessor level, MobSpawnType spawnType,
            BlockPos pos, RandomSource rand
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                &&
                lightLevelCheckIllager(level, pos, rand)
         &&
                checkMobSpawnRules(m, level, spawnType, pos, rand);
    }

    public static boolean lightLevelCheckIllager(ServerLevelAccessor level, BlockPos pos, RandomSource random) {
        return (level.dimensionType().hasSkyLight() ? level.getBrightness(LightLayer.SKY, pos) >= 8 : true)
                && level.getBrightness(LightLayer.BLOCK, pos) < 9;
    }


    static class CataclysmEdit {
        @SubscribeEvent
        public void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register((EntityType) ModEntities.DRAUGR.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
            event.register((EntityType) ModEntities.ELITE_DRAUGR.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
            event.register((EntityType) ModEntities.ROYAL_DRAUGR.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
        }
    }

    static class LegendsEdit {
        @SubscribeEvent
        public void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register((EntityType) BrazilLegendsModEntities.CUCA.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
            event.register((EntityType) BrazilLegendsModEntities.CAPELOBO.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
            event.register((EntityType) BrazilLegendsModEntities.MAPINGUARI.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
            event.register((EntityType) BrazilLegendsModEntities.MAPINGUARY.get(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkMonsterSpawnRules, Operation.REPLACE);
        }
    }

    static class IllagerEdit {
        @SubscribeEvent
        public void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
            event.register((EntityType) ModEntityTypes.NECROMANCER_ENTITY_TYPE.value(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkIllagerSpawnRules, Operation.REPLACE);
            event.register((EntityType) ModEntityTypes.FIRECALLER_ENTITY_TYPE.value(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkIllagerSpawnRules, Operation.REPLACE);
            event.register((EntityType) ModEntityTypes.ALCHEMIST_ENTITY_TYPE.value(), SpawnPlacementTypes.ON_GROUND,
                    Types.MOTION_BLOCKING_NO_LEAVES, MobSpawnEventEdits::checkIllagerSpawnRules, Operation.REPLACE);
        }
    }

}
