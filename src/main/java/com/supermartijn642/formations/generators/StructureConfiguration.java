package com.supermartijn642.formations.generators;

import com.supermartijn642.formations.generators.properties.PiecedStructureProperties;
import com.supermartijn642.formations.generators.properties.SimpleStructureProperties;
import com.supermartijn642.formations.generators.properties.StructureProperties;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.structure.TerrainAdjustment;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Created 03/09/2023 by SuperMartijn642
 */
public class StructureConfiguration {

    public static StructureConfiguration create(String namespace, String identifier){
        return new StructureConfiguration(namespace, identifier);
    }

    final String namespace, identifier;
    StructureProperties typeProperties;
    Set<ResourceLocation> biomes = new LinkedHashSet<>();
    Set<ResourceLocation> biomeTags = new LinkedHashSet<>();
    GenerationStep.Decoration generationStep = GenerationStep.Decoration.SURFACE_STRUCTURES;
    TerrainAdjustment terrainAdjustment = TerrainAdjustment.BEARD_THIN;
    StructureSetKey structureSet;
    int weight = 1;

    private StructureConfiguration(String namespace, String identifier){
        this.namespace = namespace;
        this.identifier = identifier;
    }

    private <T extends StructureProperties> StructureConfiguration type(T properties, Consumer<T> propertiesConsumer){
        if(this.typeProperties != null)
            throw new IllegalStateException("Type has already been set!");
        propertiesConsumer.accept(properties);
        this.typeProperties = properties;
        return this;
    }

    public StructureConfiguration simpleType(Consumer<SimpleStructureProperties> propertiesConsumer){
        return this.type(new SimpleStructureProperties(this.namespace), propertiesConsumer);
    }

    public StructureConfiguration piecedType(Consumer<PiecedStructureProperties> propertiesConsumer){
        return this.type(new PiecedStructureProperties(this.namespace), propertiesConsumer);
    }

    public StructureConfiguration biomes(ResourceLocation... biomes){
        this.biomes.addAll(Arrays.asList(biomes));
        return this;
    }

    public StructureConfiguration biomes(ResourceKey<Biome>... biomes){
        Arrays.stream(biomes).map(ResourceKey::location).forEach(this.biomes::add);
        return this;
    }

    public StructureConfiguration biomeTags(ResourceLocation... tags){
        this.biomeTags.addAll(Arrays.asList(tags));
        return this;
    }

    public StructureConfiguration generationStep(GenerationStep.Decoration step){
        this.generationStep = step;
        return this;
    }

    public StructureConfiguration terrainAdjustment(TerrainAdjustment adjustment){
        this.terrainAdjustment = adjustment;
        return this;
    }

    public StructureConfiguration set(StructureSetKey key){
        this.structureSet = key;
        return this;
    }

    public StructureConfiguration weight(int weight){
        this.weight = weight;
        return this;
    }
}
