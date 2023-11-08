package com.supermartijn642.formations;

import com.supermartijn642.formations.structure.FormationsSinglePoolElement;
import com.supermartijn642.formations.structure.PiecedStructure;
import com.supermartijn642.formations.structure.SimpleStructure;
import com.supermartijn642.formations.structure.processors.*;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

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
    public static final StructureProcessorType<BedColorProcessor> BED_COLOR_PROCESSOR = StructureProcessorType.register("formations:random_bed_color", BedColorProcessor.CODEC);
    public static final StructureProcessorType<BiomeReplacementProcessor> BIOME_REPLACEMENT_PROCESSOR = StructureProcessorType.register("formations:biome_replacements", BiomeReplacementProcessor.CODEC);
    public static final StructureProcessorType<BrewingStandProcessor> BREWING_STAND_PROCESSOR = StructureProcessorType.register("formations:fill_brewing_stands", BrewingStandProcessor.CODEC);
    public static final StructureProcessorType<FormationsBlockAgeProcessor> BLOCK_AGE_PROCESSOR = StructureProcessorType.register("formations:block_age", FormationsBlockAgeProcessor.CODEC);
    public static final StructureProcessorType<PlantGrowthProcessor> PLANT_GROWTH_PROCESSOR = StructureProcessorType.register("formations:random_plant_growth", PlantGrowthProcessor.CODEC);
    public static final StructureProcessorType<RespawnAnchorProcessor> RESPAWN_ANCHOR_PROCESSOR = StructureProcessorType.register("formations:respawn_anchor_charges", RespawnAnchorProcessor.CODEC);

    public static void init(){
    }
}
