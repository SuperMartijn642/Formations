package com.supermartijn642.formations;

import com.mojang.serialization.MapCodec;
import com.supermartijn642.formations.structure.FormationsSinglePoolElement;
import com.supermartijn642.formations.structure.PiecedStructure;
import com.supermartijn642.formations.structure.SimpleStructure;
import com.supermartijn642.formations.structure.processors.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;

import static com.supermartijn642.formations.Formations.location;

/**
 * Created 30/08/2023 by SuperMartijn642
 */
public class FormationsStructures {

    // Structures
    public static final StructureType<SimpleStructure> SIMPLE_STRUCTURE = StructureType.register("formations:simple", SimpleStructure.CODEC);
    public static final StructureType<PiecedStructure> PIECED_STRUCTURE = StructureType.register("formations:pieced", PiecedStructure.CODEC);

    // Pool elements
    public static final StructurePoolElementType<FormationsSinglePoolElement> SINGLE_POOL_ELEMENT = StructurePoolElementType.register("formations:single_pool_element", FormationsSinglePoolElement.CODEC);

    // Processors
    public static final MapCodec<BedColorProcessor> BED_COLOR_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("random_bed_color"), BedColorProcessor.CODEC);
    public static final MapCodec<BiomeReplacementProcessor> BIOME_REPLACEMENT_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("biome_replacements"), BiomeReplacementProcessor.CODEC);
    public static final MapCodec<BrewingStandProcessor> BREWING_STAND_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("fill_brewing_stands"), BrewingStandProcessor.CODEC);
    public static final MapCodec<ChiseledBookshelfProcessor> CHISELED_BOOKSHELF_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("fill_chiseled_bookshelves"), ChiseledBookshelfProcessor.CODEC);
    public static final MapCodec<FormationsBlockAgeProcessor> BLOCK_AGE_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("block_age"), FormationsBlockAgeProcessor.CODEC);
    public static final MapCodec<PlantGrowthProcessor> PLANT_GROWTH_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("random_plant_growth"), PlantGrowthProcessor.CODEC);
    public static final MapCodec<RespawnAnchorProcessor> RESPAWN_ANCHOR_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("respawn_anchor_charges"), RespawnAnchorProcessor.CODEC);
    public static final MapCodec<WaterloggingProcessor> WATERLOGGING_PROCESSOR = Registry.register(BuiltInRegistries.STRUCTURE_PROCESSOR, location("waterlogging"), WaterloggingProcessor.CODEC);

    public static void init(){
    }
}
