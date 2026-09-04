package net.spudacious5705.abovethecloudstweaks.WorldTeleport;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.Heightmap;

class TeleportTargetFinders {

    static BlockPos findValidSpace(BlockPos pos, boolean searchUp, int maxSearchHeight, int minSearchHeight, ServerLevel level) {

        pos = pos.mutable().setY(searchUp? minSearchHeight:maxSearchHeight);

        WorldBorder worldborder = level.getWorldBorder();


        for (BlockPos.MutableBlockPos searchPos :
                BlockPos.spiralAround(pos, 16, Direction.EAST, Direction.SOUTH)) {

            if(!worldborder.isWithinBounds(searchPos)) continue;

            boolean[] validity = {false,false,false};
            int startItt = 3;
            if(searchUp){ for (int y = minSearchHeight; y <= maxSearchHeight; ++y) {
                searchPos.setY(y);
                if(startItt >0){
                    startItt--;
                    validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                    continue;
                }
                validity[2] = validity[1];
                validity[1] = validity[0];

                boolean v = level.getBlockState(searchPos).canBeReplaced();
                validity[0] = v;

                if(validity[0] && validity[1] && !validity[2]){
                    return searchPos.move(0,-1,0).immutable();
                }
            }} else { for (int y = maxSearchHeight; y >= minSearchHeight; --y) {
                searchPos.setY(y);
                if(startItt >0){
                    startItt--;
                    validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                    continue;
                }
                validity[2] = validity[1];
                validity[1] = validity[0];

                validity[0] = level.getBlockState(searchPos).canBeReplaced();

                if(!validity[0] && validity[1] && validity[2]){
                    return searchPos.move(0,1,0).immutable();
                }
            }}


        }
        return searchUp ? pos.above():pos.below(2);
    }

    static BlockPos findValidSwitchSpace(BlockPos posIn, ServerLevel level) {

        var d = level.dimension();

        int maxSearchHeight = 320;
        int minSearchHeight = 10;

        BlockPos.MutableBlockPos pos = posIn.mutable();

        boolean isInSolid =
                !level.getBlockState(pos).canBeReplaced() || !level.getBlockState(pos.above()).canBeReplaced();

        boolean seeSky = level.canSeeSky(pos);
        boolean seeSkyWater = level.canSeeSkyFromBelowWater(pos);

        if(!seeSky & seeSkyWater) return pos; // in water. Player is safe.

        if(seeSky) {// floating in air. Lower to valid floor.
            var p =level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, pos);
            return p;
        }

        boolean[] validity = {false,false,false};
        int startItt = 3;

        BlockPos.MutableBlockPos searchPos = pos.mutable();//creates copy

        if(isInSolid){ for (int y = pos.getY(); y <= maxSearchHeight; ++y) {
            searchPos.setY(y);
            if(startItt >0){
                startItt--;
                validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                continue;
            }
            validity[2] = validity[1];
            validity[1] = validity[0];

            boolean v = level.getBlockState(searchPos).canBeReplaced();
            validity[0] = v;

            if(validity[0] && validity[1] && !validity[2]){
                return searchPos.move(0,-1,0).immutable();
            }
        }} else { for (int y = pos.getY(); y >= minSearchHeight; --y) {
            searchPos.setY(y);
            if(startItt >0){
                startItt--;
                validity[startItt] = level.getBlockState(searchPos).canBeReplaced();
                continue;
            }
            validity[2] = validity[1];
            validity[1] = validity[0];

            validity[0] = level.getBlockState(searchPos).canBeReplaced();

            if(!validity[0] && validity[1] && validity[2]){
                return searchPos.move(0,1,0).immutable();
            }
        }}

        return pos.below(2).immutable();

    }
}
