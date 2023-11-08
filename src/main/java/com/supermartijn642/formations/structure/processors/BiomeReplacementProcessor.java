package com.supermartijn642.formations.structure.processors;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Swaps out certain blocks based on the biome a structure is placed in.
 * <p>
 * Created 31/08/2023 by SuperMartijn642
 */
public class BiomeReplacementProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final Codec<BiomeReplacementProcessor> CODEC = Codec.unit(BiomeReplacementProcessor::new);

    private static final Map<ResourceKey<Biome>,Map<Block,Block>> BIOME_REPLACEMENT_MAP = new HashMap<>();
    private static final Map<ResourceKey<Biome>,Map<Block,Block>> BIOME_SOIL_REPLACEMENT_MAP = new HashMap<>();
    private static final Set<Block> REPLACEABLE_BLOCKS = new HashSet<>();

    static{
        // ------ Wood ------
        // Planks
        addReplacements(
            Pair.of(Blocks.OAK_PLANKS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_PLANKS, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_PLANKS, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_PLANKS, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_PLANKS, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_PLANKS, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_PLANKS, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_PLANKS, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_PLANKS, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Plank stairs
        addReplacements(
            Pair.of(Blocks.OAK_STAIRS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_STAIRS, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_STAIRS, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_STAIRS, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_STAIRS, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_STAIRS, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_STAIRS, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_STAIRS, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_STAIRS, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Plank slabs
        addReplacements(
            Pair.of(Blocks.OAK_SLAB, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_SLAB, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_SLAB, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_SLAB, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_SLAB, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_SLAB, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_SLAB, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_SLAB, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_SLAB, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Logs
        addReplacements(
            Pair.of(Blocks.OAK_LOG, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_LOG, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_LOG, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_LOG, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_LOG, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_LOG, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_LOG, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_STEM, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_STEM, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Woods
        addReplacements(
            Pair.of(Blocks.OAK_WOOD, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_WOOD, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_WOOD, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_WOOD, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_WOOD, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_WOOD, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_WOOD, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_HYPHAE, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_HYPHAE, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Stripped logs
        addReplacements(
            Pair.of(Blocks.STRIPPED_OAK_LOG, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.STRIPPED_SPRUCE_LOG, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.STRIPPED_MANGROVE_LOG, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.STRIPPED_BIRCH_LOG, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.STRIPPED_DARK_OAK_LOG, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.STRIPPED_ACACIA_LOG, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.STRIPPED_JUNGLE_LOG, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.STRIPPED_CRIMSON_STEM, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.STRIPPED_WARPED_STEM, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Stripped woods
        addReplacements(
            Pair.of(Blocks.STRIPPED_OAK_WOOD, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.STRIPPED_SPRUCE_WOOD, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.STRIPPED_MANGROVE_WOOD, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.STRIPPED_BIRCH_WOOD, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.STRIPPED_DARK_OAK_WOOD, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.STRIPPED_ACACIA_WOOD, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.STRIPPED_JUNGLE_WOOD, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.STRIPPED_CRIMSON_HYPHAE, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.STRIPPED_WARPED_HYPHAE, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Leaves
        addReplacements(
            Pair.of(Blocks.OAK_LEAVES, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_LEAVES, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_LEAVES, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_LEAVES, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_LEAVES, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_LEAVES, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_LEAVES, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.NETHER_WART_BLOCK, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_WART_BLOCK, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Doors
        addReplacements(
            Pair.of(Blocks.OAK_DOOR, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_DOOR, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_DOOR, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_DOOR, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_DOOR, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_DOOR, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_DOOR, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_DOOR, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_DOOR, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Trapdoors
        addReplacements(
            Pair.of(Blocks.OAK_TRAPDOOR, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_TRAPDOOR, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_TRAPDOOR, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_TRAPDOOR, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_TRAPDOOR, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_TRAPDOOR, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_TRAPDOOR, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_TRAPDOOR, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_TRAPDOOR, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Signs
        addReplacements(
            Pair.of(Blocks.OAK_SIGN, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_SIGN, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_SIGN, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_SIGN, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_SIGN, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_SIGN, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_SIGN, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_SIGN, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_SIGN, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Wall signs
        addReplacements(
            Pair.of(Blocks.OAK_WALL_SIGN, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_WALL_SIGN, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_WALL_SIGN, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_WALL_SIGN, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_WALL_SIGN, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_WALL_SIGN, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_WALL_SIGN, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_WALL_SIGN, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_WALL_SIGN, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Fences
        addReplacements(
            Pair.of(Blocks.OAK_FENCE, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_FENCE, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_FENCE, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_FENCE, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_FENCE, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_FENCE, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_FENCE, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_FENCE, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FENCE, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Fence gates
        addReplacements(
            Pair.of(Blocks.OAK_FENCE_GATE, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_FENCE_GATE, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_FENCE_GATE, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_FENCE_GATE, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_FENCE_GATE, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_FENCE_GATE, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_FENCE_GATE, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_FENCE_GATE, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FENCE_GATE, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Pressure plates
        addReplacements(
            Pair.of(Blocks.OAK_PRESSURE_PLATE, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_PRESSURE_PLATE, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_PRESSURE_PLATE, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_PRESSURE_PLATE, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_PRESSURE_PLATE, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_PRESSURE_PLATE, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_PRESSURE_PLATE, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_PRESSURE_PLATE, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_PRESSURE_PLATE, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Buttons
        addReplacements(
            Pair.of(Blocks.OAK_BUTTON, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_BUTTON, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_BUTTON, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_BUTTON, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_BUTTON, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_BUTTON, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_BUTTON, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.CRIMSON_BUTTON, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_BUTTON, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Saplings
        addReplacements(
            Pair.of(Blocks.OAK_SAPLING, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.WOODED_BADLANDS, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SPRUCE_SAPLING, List.of(Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN)),
            Pair.of(Blocks.MANGROVE_PROPAGULE, List.of(Biomes.SWAMP, Biomes.MANGROVE_SWAMP)),
            Pair.of(Blocks.BIRCH_SAPLING, List.of(Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER)),
            Pair.of(Blocks.DARK_OAK_SAPLING, List.of(Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN)),
            Pair.of(Blocks.ACACIA_SAPLING, List.of(Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.DEAD_BUSH, List.of(Biomes.DESERT)),
            Pair.of(Blocks.JUNGLE_SAPLING, List.of(Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES)),
            Pair.of(Blocks.BAMBOO_SAPLING, List.of(Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST, Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );

        // ------ Stone ------
        // Chiseled bricks
        addReplacements(
            Pair.of(Blocks.CHISELED_STONE_BRICKS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.CHISELED_SANDSTONE, List.of(Biomes.DESERT)),
            Pair.of(Blocks.CHISELED_RED_SANDSTONE, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.CHISELED_DEEPSLATE, List.of(Biomes.DEEP_DARK))
        );
        // Cracked bricks
        addReplacements(
            Pair.of(Blocks.CRACKED_STONE_BRICKS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.SMOOTH_SANDSTONE, List.of(Biomes.DESERT)),
            Pair.of(Blocks.SMOOTH_RED_SANDSTONE, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.CRACKED_DEEPSLATE_BRICKS, List.of(Biomes.DEEP_DARK))
        );
        // Bricks
        addReplacements(
            Pair.of(Blocks.STONE_BRICKS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.SANDSTONE, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_SANDSTONE, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.DEEPSLATE_BRICKS, List.of(Biomes.DEEP_DARK))
        );
        // Brick stairs
        addReplacements(
            Pair.of(Blocks.STONE_BRICK_STAIRS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.SANDSTONE_STAIRS, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_SANDSTONE_STAIRS, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.DEEPSLATE_BRICK_STAIRS, List.of(Biomes.DEEP_DARK))
        );
        // Brick slabs
        addReplacements(
            Pair.of(Blocks.STONE_BRICK_SLAB, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.SANDSTONE_SLAB, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_SANDSTONE_SLAB, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.DEEPSLATE_BRICK_SLAB, List.of(Biomes.DEEP_DARK))
        );
        // Walls
        addReplacements(
            Pair.of(Blocks.STONE_BRICK_WALL, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.SANDSTONE_WALL, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_SANDSTONE_WALL, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.DEEPSLATE_BRICK_WALL, List.of(Biomes.DEEP_DARK))
        );
        // Polished/smooth
        addReplacements(
            Pair.of(Blocks.SMOOTH_STONE, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.CUT_SANDSTONE, List.of(Biomes.DESERT)),
            Pair.of(Blocks.CUT_RED_SANDSTONE, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.POLISHED_DEEPSLATE, List.of(Biomes.DEEP_DARK))
        );
        // Polished/smooth slabs
        addReplacements(
            Pair.of(Blocks.SMOOTH_STONE_SLAB, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE)),
            Pair.of(Blocks.CUT_SANDSTONE_SLAB, List.of(Biomes.DESERT)),
            Pair.of(Blocks.CUT_RED_SANDSTONE_SLAB, List.of(Biomes.WOODED_BADLANDS, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.POLISHED_DEEPSLATE_SLAB, List.of(Biomes.DEEP_DARK))
        );

        // ------ Soil ------
        // Grass block
        addSoilReplacements(
            Pair.of(Blocks.GRASS_BLOCK, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SAND, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_SAND, List.of(Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.BASALT, List.of(Biomes.BASALT_DELTAS)),
            Pair.of(Blocks.SOUL_SOIL, List.of(Biomes.SOUL_SAND_VALLEY)),
            Pair.of(Blocks.NETHERRACK, List.of(Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_NYLIUM, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_NYLIUM, List.of(Biomes.WARPED_FOREST)),
            Pair.of(Blocks.END_STONE, List.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Path
        addReplacements(
            Pair.of(Blocks.DIRT_PATH, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.DESERT, Biomes.BADLANDS, Biomes.ERODED_BADLANDS)),
            Pair.of(Blocks.SOUL_SAND, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST)),
            Pair.of(Blocks.END_STONE, List.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Dirt
        addReplacements(
            Pair.of(Blocks.DIRT, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.DEEP_DARK)),
            Pair.of(Blocks.SAND, List.of(Biomes.DESERT)),
            Pair.of(Blocks.TERRACOTTA, List.of(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS)),
            Pair.of(Blocks.BASALT, List.of(Biomes.BASALT_DELTAS)),
            Pair.of(Blocks.SOUL_SOIL, List.of(Biomes.SOUL_SAND_VALLEY)),
            Pair.of(Blocks.NETHERRACK, List.of(Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST)),
            Pair.of(Blocks.END_STONE, List.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );
        // Stone
        addReplacements(
            Pair.of(Blocks.STONE, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.DEEP_DARK, Biomes.DESERT)),
            Pair.of(Blocks.TERRACOTTA, List.of(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS)),
            Pair.of(Blocks.BASALT, List.of(Biomes.BASALT_DELTAS)),
            Pair.of(Blocks.NETHERRACK, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST)),
            Pair.of(Blocks.END_STONE, List.of(Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS))
        );

        // ------ Plants ------
        // Grass
        addReplacements(
            Pair.of(Blocks.GRASS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT))
        );
        // Roots
        addReplacements(
            Pair.of(Blocks.AIR, List.of(Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES, Biomes.SOUL_SAND_VALLEY)),
            Pair.of(Blocks.CRIMSON_ROOTS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_ROOTS, List.of(Biomes.WARPED_FOREST))
        );
        // Tall grass
        addReplacements(
            Pair.of(Blocks.GRASS, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.DEAD_BUSH, List.of(Biomes.DESERT)),
            Pair.of(Blocks.AIR, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES, Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.TWISTING_VINES_PLANT, List.of(Biomes.WARPED_FOREST))
        );
        // Dandelion
        addReplacements(
            Pair.of(Blocks.DANDELION, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Poppy
        addReplacements(
            Pair.of(Blocks.POPPY, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.BROWN_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Blue orchid
        addReplacements(
            Pair.of(Blocks.BLUE_ORCHID, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Allium
        addReplacements(
            Pair.of(Blocks.ALLIUM, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Azure Bluet
        addReplacements(
            Pair.of(Blocks.AZURE_BLUET, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Red tulip
        addReplacements(
            Pair.of(Blocks.RED_TULIP, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Orange tulip
        addReplacements(
            Pair.of(Blocks.ORANGE_TULIP, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT, Biomes.CRIMSON_FOREST, Biomes.WARPED_FOREST)),
            Pair.of(Blocks.BROWN_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES))
        );
        // White tulip
        addReplacements(
            Pair.of(Blocks.WHITE_TULIP, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Pink tulip
        addReplacements(
            Pair.of(Blocks.PINK_TULIP, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Oxeye daisy
        addReplacements(
            Pair.of(Blocks.OXEYE_DAISY, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Cornflower
        addReplacements(
            Pair.of(Blocks.CORNFLOWER, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
        // Lily of the valley
        addReplacements(
            Pair.of(Blocks.LILY_OF_THE_VALLEY, List.of(Biomes.PLAINS, Biomes.SUNFLOWER_PLAINS, Biomes.FOREST, Biomes.FLOWER_FOREST, Biomes.WINDSWEPT_HILLS, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_FOREST, Biomes.MEADOW, Biomes.BEACH, Biomes.STONY_SHORE, Biomes.DRIPSTONE_CAVES, Biomes.SNOWY_PLAINS, Biomes.ICE_SPIKES, Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA, Biomes.TAIGA, Biomes.SNOWY_TAIGA, Biomes.GROVE, Biomes.SNOWY_SLOPES, Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS, Biomes.STONY_PEAKS, Biomes.FROZEN_RIVER, Biomes.SNOWY_BEACH, Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN, Biomes.SWAMP, Biomes.MANGROVE_SWAMP, Biomes.BIRCH_FOREST, Biomes.OLD_GROWTH_BIRCH_FOREST, Biomes.RIVER, Biomes.DARK_FOREST, Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN, Biomes.OCEAN, Biomes.DEEP_OCEAN, Biomes.COLD_OCEAN, Biomes.DEEP_COLD_OCEAN, Biomes.SAVANNA, Biomes.SAVANNA_PLATEAU, Biomes.WINDSWEPT_SAVANNA, Biomes.JUNGLE, Biomes.SPARSE_JUNGLE, Biomes.LUSH_CAVES, Biomes.BAMBOO_JUNGLE, Biomes.WOODED_BADLANDS, Biomes.DEEP_DARK, Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.THE_END, Biomes.END_HIGHLANDS, Biomes.END_MIDLANDS, Biomes.SMALL_END_ISLANDS, Biomes.END_BARRENS)),
            Pair.of(Blocks.AIR, List.of(Biomes.DESERT)),
            Pair.of(Blocks.RED_MUSHROOM, List.of(Biomes.SOUL_SAND_VALLEY, Biomes.BASALT_DELTAS, Biomes.NETHER_WASTES)),
            Pair.of(Blocks.CRIMSON_FUNGUS, List.of(Biomes.CRIMSON_FOREST)),
            Pair.of(Blocks.WARPED_FUNGUS, List.of(Biomes.WARPED_FOREST))
        );
    }

    @SafeVarargs
    private static void addReplacements(Pair<Block,List<ResourceKey<Biome>>>... entries){
        for(int i = 0; i < entries.length; i++){
            Pair<Block,List<ResourceKey<Biome>>> entry = entries[i];
            for(ResourceKey<Biome> biome : entry.getSecond()){
                BIOME_REPLACEMENT_MAP.computeIfAbsent(biome, o -> new HashMap<>());
                REPLACEABLE_BLOCKS.add(entry.getFirst());
                for(int j = 0; j < entries.length; j++){
                    if(i != j && entries[j].getFirst() != entry.getFirst() && entries[j].getFirst() != null)
                        BIOME_REPLACEMENT_MAP.get(biome).put(entries[j].getFirst(), entry.getFirst());
                }
            }
        }
    }

    @SafeVarargs
    private static void addSoilReplacements(Pair<Block,List<ResourceKey<Biome>>>... entries){
        for(int i = 0; i < entries.length; i++){
            Pair<Block,List<ResourceKey<Biome>>> entry = entries[i];
            for(ResourceKey<Biome> biome : entry.getSecond()){
                BIOME_SOIL_REPLACEMENT_MAP.computeIfAbsent(biome, o -> new HashMap<>());
                REPLACEABLE_BLOCKS.add(entry.getFirst());
                for(int j = 0; j < entries.length; j++){
                    if(i != j && entries[j].getFirst() != entry.getFirst() && entries[j].getFirst() != null)
                        BIOME_SOIL_REPLACEMENT_MAP.get(biome).put(entries[j].getFirst(), entry.getFirst());
                }
            }
        }
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        // Check if the block has replacement entries
        BlockState originalState = block.state();
        if(originalState == null || originalState.is(Blocks.AIR) || !REPLACEABLE_BLOCKS.contains(originalState.getBlock()))
            return block;

        // Get the biome the structure is in
        BoundingBox boundingBox = placeSettings.getBoundingBox();
        if(boundingBox == null)
            return block;
        Holder<Biome> biomeHolder = level.getBiome(structurePosition);
        if(!biomeHolder.isBound())
            return block;
        ResourceKey<Biome> biome = biomeHolder.unwrapKey().get();

        // Check if the biome has replacement entries
        Map<Block,Block> replacements = BIOME_SOIL_REPLACEMENT_MAP.get(biome);
        BlockInstance blockAbove;
        if(replacements == null || !replacements.containsKey(originalState.getBlock())
            || ((blockAbove = pieceBlocks.get(pos.above())) != null && blockAbove.state() != null ? blockAbove.state() : level.getBlockState(pos.above())).isFaceSturdy(level, pos.above(), Direction.DOWN)){
            replacements = BIOME_REPLACEMENT_MAP.get(biome);
            if(replacements == null || !replacements.containsKey(originalState.getBlock()))
                return block;
        }

        // Get the new state
        BlockState newState = replacements.get(originalState.getBlock()).withPropertiesOf(originalState);
        return new BlockInstance(newState, newState.is(block.state().getBlock()) ? block.nbt() : null);
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.BIOME_REPLACEMENT_PROCESSOR.get();
    }
}
