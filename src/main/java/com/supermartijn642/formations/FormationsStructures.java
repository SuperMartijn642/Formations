package com.supermartijn642.formations;

import com.supermartijn642.formations.structure.FormationsSinglePoolElement;
import com.supermartijn642.formations.structure.PiecedStructure;
import com.supermartijn642.formations.structure.SimpleStructure;
import com.supermartijn642.formations.structure.processors.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * Created 30/08/2023 by SuperMartijn642
 */
public class FormationsStructures {


    // Structures
    private static final DeferredRegister<StructureType<?>> STRUCTURE_TYPES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Formations.MODID);
    public static final DeferredHolder<StructureType<?>,StructureType<SimpleStructure>> SIMPLE_STRUCTURE = STRUCTURE_TYPES.register("simple", () -> () -> SimpleStructure.CODEC);
    public static final DeferredHolder<StructureType<?>,StructureType<PiecedStructure>> PIECED_STRUCTURE = STRUCTURE_TYPES.register("pieced", () -> () -> PiecedStructure.CODEC);

    // Pool elements
    private static final DeferredRegister<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENTS = DeferredRegister.create(Registries.STRUCTURE_POOL_ELEMENT, Formations.MODID);
    public static final DeferredHolder<StructurePoolElementType<?>,StructurePoolElementType<FormationsSinglePoolElement>> SINGLE_POOL_ELEMENT = STRUCTURE_POOL_ELEMENTS.register("single_pool_element", () -> () -> FormationsSinglePoolElement.CODEC);

    // Processors
    private static final DeferredRegister<StructureProcessorType<?>> STRUCTURE_PROCESSOR_TYPES = DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, Formations.MODID);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<BedColorProcessor>> BED_COLOR_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("random_bed_color", () -> () -> BedColorProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<BiomeReplacementProcessor>> BIOME_REPLACEMENT_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("biome_replacements", () -> () -> BiomeReplacementProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<BrewingStandProcessor>> BREWING_STAND_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("fill_brewing_stands", () -> () -> BrewingStandProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<ChiseledBookshelfProcessor>> CHISELED_BOOKSHELF_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("fill_chiseled_bookshelves", () -> () -> ChiseledBookshelfProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<FormationsBlockAgeProcessor>> BLOCK_AGE_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("block_age", () -> () -> FormationsBlockAgeProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<PlantGrowthProcessor>> PLANT_GROWTH_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("random_plant_growth", () -> () -> PlantGrowthProcessor.CODEC);
    public static final DeferredHolder<StructureProcessorType<?>,StructureProcessorType<RespawnAnchorProcessor>> RESPAWN_ANCHOR_PROCESSOR = STRUCTURE_PROCESSOR_TYPES.register("respawn_anchor_charges", () -> () -> RespawnAnchorProcessor.CODEC);

    public static void init(){
        IEventBus eventBus = ModLoadingContext.get().getActiveContainer().getEventBus();
        //noinspection DataFlowIssue
        STRUCTURE_TYPES.register(eventBus);
        STRUCTURE_POOL_ELEMENTS.register(eventBus);
        STRUCTURE_PROCESSOR_TYPES.register(eventBus);
    }
}
