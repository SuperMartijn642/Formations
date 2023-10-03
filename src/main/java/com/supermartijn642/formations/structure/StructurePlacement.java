package com.supermartijn642.formations.structure;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created 28/09/2023 by SuperMartijn642
 */
public enum StructurePlacement implements StringRepresentable {
    /**
     * At the highest solid blocks, but below oceans
     */
    SURFACE((context, box) -> findFromTop(context, box, Heightmap.Types.WORLD_SURFACE_WG, 10, 3, BlockBehaviour.BlockStateBase::isSolid)),
    /**
     * At the lowest solid blocks
     */
    CEILING((context, box) -> {
        List<BlockPos> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            for(int y = context.heightAccessor().getMinBuildHeight() + 1; y < highest; y++){
                if(!column.getBlock(y).isAir())
                    return pos.setY(y);
            }
            return null;
        }).collect(Collectors.toList());
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int[] heights = positions.stream().mapToInt(BlockPos::getY).toArray();
        // Find the average height
        int average = (int)Math.round(IntStream.of(heights).average().getAsDouble());
        // If the average height difference is greater than 4 blocks, fail
        if(IntStream.of(heights).map(y -> Math.abs(average - y)).average().getAsDouble() > 4)
            return null;

        return average;
    }),
    /**
     * At the highest water blocks
     */
    ON_WATER((context, box) -> findFromTop(context, box, Heightmap.Types.WORLD_SURFACE_WG, 1, 1, state -> state.is(Blocks.WATER))),
    /**
     * At the highest lava blocks
     */
    ON_LAVA((context, box) -> findFromTop(context, box, Heightmap.Types.WORLD_SURFACE_WG, 1, 1, state -> state.is(Blocks.LAVA))),
    /**
     * Somewhere between the highest and lowest solid blocks
     */
    UNDERGROUND((context, box) -> {
        List<Triple<NoiseColumn,Integer,Integer>> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            int lowest = context.heightAccessor().getMinBuildHeight();
            for(int y = lowest + 1; y < highest; y++){
                if(!column.getBlock(y).isAir()){
                    lowest = y;
                    break;
                }
            }
            if(lowest == context.heightAccessor().getMinBuildHeight())
                return null;
            return Triple.of(column, highest, lowest);
        }).toList();
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int min = positions.stream().mapToInt(Triple::getRight).max().getAsInt();
        int max = positions.stream().mapToInt(Triple::getMiddle).min().getAsInt();
        if(max - min - 2 < box.getYSpan())
            return null;

        return min + 1 + context.random().nextInt(max - min - box.getYSpan() - 2);
    }),
    /**
     * Somewhere between the highest and lowest solid blocks, but at a surface
     */
    UNDERGROUND_SURFACE((context, box) -> {
        List<Triple<NoiseColumn,Integer,Integer>> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            int lowest = context.heightAccessor().getMinBuildHeight();
            for(int y = lowest + 1; y < highest; y++){
                if(!column.getBlock(y).isAir()){
                    lowest = y;
                    break;
                }
            }
            if(lowest == context.heightAccessor().getMinBuildHeight())
                return null;
            return Triple.of(column, highest, lowest);
        }).toList();
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int min = positions.stream().mapToInt(Triple::getRight).max().getAsInt();
        int max = positions.stream().mapToInt(Triple::getMiddle).min().getAsInt();
        if(max - min - 2 < box.getYSpan())
            return null;

        int height = min + 1 + context.random().nextInt(max - min - box.getYSpan() - 2);
        Integer[] heights = positions.stream().map(t -> {
            NoiseColumn column = t.getLeft();
            int y = height;
            for(int i = 0; i < 10 && y > min; i++){
                if(column.getBlock(y).isAir())
                    break;
                y--;
            }
            if(!column.getBlock(y).isAir())
                return null;
            y--;
            for(int i = 0; i < 20; i++){
                if(!column.getBlock(y).isAir())
                    break;
                y--;
            }
            if(!column.getBlock(y).isSolid())
                return null;
            return y;
        }).toArray(Integer[]::new);
        if(Arrays.stream(heights).anyMatch(Objects::isNull))
            return null;

        // Find the average height
        int average = (int)Math.round(Stream.of(heights).mapToInt(Integer::intValue).average().getAsDouble());
        // If any height difference is greater than maxOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).max().getAsInt() > 5)
            return null;
        // If the average height difference is greater than maxAverageOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).average().getAsDouble() > 3)
            return null;

        return average;
    }),
    /**
     * Somewhere between the highest and lowest solid blocks, but at a ceiling
     */
    UNDERGROUND_CEILING((context, box) -> {
        List<Triple<NoiseColumn,Integer,Integer>> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            int lowest = context.heightAccessor().getMinBuildHeight();
            for(int y = lowest + 1; y < highest; y++){
                if(!column.getBlock(y).isAir()){
                    lowest = y;
                    break;
                }
            }
            if(lowest == context.heightAccessor().getMinBuildHeight())
                return null;
            return Triple.of(column, highest, lowest);
        }).toList();
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int min = positions.stream().mapToInt(Triple::getRight).max().getAsInt();
        int max = positions.stream().mapToInt(Triple::getMiddle).min().getAsInt();
        if(max - min - 2 < box.getYSpan())
            return null;

        int height = min + 1 + context.random().nextInt(max - min - box.getYSpan() - 2);
        Integer[] heights = positions.stream().map(t -> {
            NoiseColumn column = t.getLeft();
            int y = height;
            for(int i = 0; i < 10 && y > min; i++){
                if(column.getBlock(y).isAir())
                    break;
                y++;
            }
            if(!column.getBlock(y).isAir())
                return null;
            y++;
            for(int i = 0; i < 30; i++){
                if(!column.getBlock(y).isAir())
                    break;
                y++;
            }
            if(!column.getBlock(y).isSolid())
                return null;
            return y;
        }).toArray(Integer[]::new);
        if(Arrays.stream(heights).anyMatch(Objects::isNull))
            return null;

        // Find the average height
        int average = (int)Math.round(Stream.of(heights).mapToInt(Integer::intValue).average().getAsDouble());
        // If any height difference is greater than maxOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).max().getAsInt() > 5)
            return null;
        // If the average height difference is greater than maxAverageOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).average().getAsDouble() > 3)
            return null;

        return average;
    }),
    /**
     * Somewhere between the highest and lowest solid blocks, but inside of solid ground
     */
    UNDERGROUND_BURIED((context, box) -> {
        List<Triple<NoiseColumn,Integer,Integer>> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            int lowest = context.heightAccessor().getMinBuildHeight();
            for(int y = lowest + 1; y < highest; y++){
                if(!column.getBlock(y).isAir()){
                    lowest = y;
                    break;
                }
            }
            if(lowest == context.heightAccessor().getMinBuildHeight())
                return null;
            return Triple.of(column, highest, lowest);
        }).toList();
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int min = positions.stream().mapToInt(Triple::getRight).max().getAsInt();
        int max = positions.stream().mapToInt(Triple::getMiddle).min().getAsInt();
        if(max - min - 2 < box.getYSpan())
            return null;

        int ySpan = box.getYSpan();
        int height = min + ySpan + 1 + context.random().nextInt(max - min - ySpan - 2);
        Integer[] heights = positions.stream().map(t -> {
            NoiseColumn column = t.getLeft();
            int y = height;
            for(int i = 0; i < 10 && y > min; i++){
                if(column.getBlock(y).isSolid())
                    break;
                y--;
            }
            if(!column.getBlock(y).isSolid())
                return null;
            y -= 2;
            for(int i = 0; i < ySpan; i++){
                if(!column.getBlock(y).isSolid())
                    return null;
                y--;
            }
            if(!column.getBlock(y).isSolid() || !column.getBlock(y - 1).isSolid())
                return null;
            return y;
        }).toArray(Integer[]::new);
        if(Arrays.stream(heights).anyMatch(Objects::isNull))
            return null;

        // Find the average height
        int average = (int)Math.round(Stream.of(heights).mapToInt(Integer::intValue).average().getAsDouble());
        if(!positions.stream().allMatch(t -> {
            NoiseColumn column = t.getLeft();
            return column.getBlock(average).isSolid() && column.getBlock(average + ySpan).isSolid();
        }))
            return null;

        return average;
    }),
    /**
     * Somewhere between the highest and lowest solid blocks, but on top of lava
     */
    UNDERGROUND_ON_LAVA((context, box) -> {
        List<Triple<NoiseColumn,Integer,Integer>> positions = cornersAndCenter(box).map(pos -> {
            int highest = Math.min(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), Heightmap.Types.WORLD_SURFACE_WG, context.heightAccessor(), context.randomState()), context.heightAccessor().getMaxBuildHeight());
            NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
            int lowest = context.heightAccessor().getMinBuildHeight();
            for(int y = lowest + 1; y < highest; y++){
                if(!column.getBlock(y).isAir()){
                    lowest = y;
                    break;
                }
            }
            if(lowest == context.heightAccessor().getMinBuildHeight())
                return null;
            return Triple.of(column, highest, lowest);
        }).toList();
        // If a position was not found, fail
        if(positions.stream().anyMatch(Objects::isNull))
            return null;

        int min = positions.stream().mapToInt(Triple::getRight).max().getAsInt();
        int max = positions.stream().mapToInt(Triple::getMiddle).min().getAsInt();
        if(max - min - 2 < box.getYSpan())
            return null;

        int height = min + 1 + context.random().nextInt(max - min - box.getYSpan() - 2);
        Integer[] heights = positions.stream().map(t -> {
            NoiseColumn column = t.getLeft();
            int y = height;
            for(int i = 0; i < 10 && y > min; i++){
                if(column.getBlock(y).isAir())
                    break;
                y--;
            }
            if(!column.getBlock(y).isAir())
                return null;
            y--;
            for(int i = 0; i < 40; i++){
                if(!column.getBlock(y).isAir())
                    break;
                y--;
            }
            if(!column.getBlock(y).is(Blocks.LAVA))
                return null;
            return y;
        }).toArray(Integer[]::new);
        if(Arrays.stream(heights).anyMatch(Objects::isNull))
            return null;

        // Find the average height
        int average = (int)Math.round(Stream.of(heights).mapToInt(Integer::intValue).average().getAsDouble());
        // If any height difference is greater than maxOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).max().getAsInt() > 5)
            return null;
        // If the average height difference is greater than maxAverageOffset blocks, fail
        if(Stream.of(heights).mapToInt(y -> Math.abs(average - y)).average().getAsDouble() > 3)
            return null;

        return average;
    });

    public static final Codec<StructurePlacement> CODEC = StringRepresentable.fromEnum(StructurePlacement::values);

    final BiFunction<Structure.GenerationContext,BoundingBox,Integer> locator;

    StructurePlacement(BiFunction<Structure.GenerationContext,BoundingBox,Integer> locator){
        this.locator = locator;
    }

    public Optional<Integer> findHeight(Structure.GenerationContext context, BoundingBox boundingBox){
        return Optional.ofNullable(this.locator.apply(context, boundingBox));
    }

    @Override
    public String getSerializedName(){
        return this.name().toLowerCase(Locale.ROOT);
    }

    private static Stream<BlockPos.MutableBlockPos> cornersAndCenter(BoundingBox box){
        BlockPos center = box.getCenter();
        return Stream.of(
            new BlockPos.MutableBlockPos(box.minX(), 0, box.minZ()),
            new BlockPos.MutableBlockPos(box.minX(), 0, box.maxZ()),
            new BlockPos.MutableBlockPos(box.maxX(), 0, box.maxZ()),
            new BlockPos.MutableBlockPos(box.maxX(), 0, box.minZ()),
            new BlockPos.MutableBlockPos(center.getX(), 0, center.getZ())
        );
    }

    private static Integer findFromTop(Structure.GenerationContext context, BoundingBox box, Heightmap.Types heightmap, int maxOffset, double maxAverageOffset, Predicate<BlockState> target){
        List<BlockPos> positions = cornersAndCenter(box).map(pos -> pos.setY(context.chunkGenerator().getFirstOccupiedHeight(pos.getX(), pos.getZ(), heightmap, context.heightAccessor(), context.randomState()))).collect(Collectors.toList());
        int[] heights = positions.stream().mapToInt(BlockPos::getY).toArray();
        // If any pos did not find a block, fail
        if(IntStream.of(heights).anyMatch(y -> y <= context.heightAccessor().getMinBuildHeight()))
            return null;
        // Check if the found block is accepted
        if(positions.stream().anyMatch(pos -> !target.test(context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState()).getBlock(pos.getY()))))
            return null;

        // Find the average height
        int average = (int)Math.round(IntStream.of(heights).average().getAsDouble());
        // If any height difference is greater than maxOffset blocks, fail
        if(IntStream.of(heights).map(y -> Math.abs(average - y)).max().getAsInt() > maxOffset)
            return null;
        // If the average height difference is greater than maxAverageOffset blocks, fail
        if(IntStream.of(heights).map(y -> Math.abs(average - y)).average().getAsDouble() > maxAverageOffset)
            return null;

        return average;
    }
}
