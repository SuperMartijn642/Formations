package com.supermartijn642.formations.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.serialization.JsonOps;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.ResourceGenerator;
import com.supermartijn642.core.generator.ResourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created 03/09/2023 by SuperMartijn642
 */
public abstract class TemplatePoolGenerator extends ResourceGenerator {

    private final Map<ResourceLocation,TemplatePoolBuilder> builders = new HashMap<>();

    public TemplatePoolGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public void save(){
        for(TemplatePoolBuilder pool : this.builders.values()){
            // Validate all templates actually exist
            List<ResourceLocation> missingStructures = pool.entries.stream()
                .map(entry -> entry.location)
                .collect(Collectors.toSet())
                .stream()
                .filter(structure -> !this.cache.doesResourceExist(ResourceType.DATA, structure.getNamespace(), "structures", structure.getPath(), ".nbt"))
                .toList();
            if(!missingStructures.isEmpty())
                throw new RuntimeException("Template pool '" + pool.identifier + "' has missing structure nbt files: " + missingStructures);

            JsonObject json = new JsonObject();
            // Fallback
            json.addProperty("fallback", pool.fallbackPool.toString());
            // Entries
            JsonArray entries = new JsonArray(pool.entries.size());
            for(TemplatePoolEntryBuilder entry : pool.entries){
                JsonObject entryJson = new JsonObject();
                // Weight
                entryJson.addProperty("weight", entry.weight);
                // Properties
                JsonObject propertyJson = new JsonObject();
                // Type
                propertyJson.addProperty("element_type", entry.type.toString());
                // Location
                propertyJson.addProperty("location", entry.location.toString());
                // Projection
                propertyJson.addProperty("projection", entry.projection.getSerializedName());
                // Ground level
                if(entry.groundLevel != null)
                    propertyJson.addProperty("ground_level", entry.groundLevel);
                // Processors
                if(entry.processors.isEmpty())
                    propertyJson.addProperty("processors", "minecraft:empty");
                else{
                    JsonArray processors = new JsonArray(entry.processors.size());
                    entry.processors.stream()
                        .map(processor -> StructureProcessorType.SINGLE_CODEC.encodeStart(JsonOps.INSTANCE, processor).getOrThrow())
                        .forEach(processors::add);
                    propertyJson.add("processors", processors);
                }
                entryJson.add("element", propertyJson);
                entries.add(entryJson);
            }
            json.add("elements", entries);

            // Save the json data
            this.cache.saveJsonResource(ResourceType.DATA, json, pool.identifier.getNamespace(), "worldgen/template_pool", pool.identifier.getPath());
        }
    }

    public TemplatePoolBuilder pool(String namespace, String identifier){
        return this.builders.computeIfAbsent(ResourceLocation.fromNamespaceAndPath(namespace, identifier), i -> new TemplatePoolBuilder(this.modid, i));
    }

    public TemplatePoolBuilder pool(String identifier){
        return this.pool(this.modid, identifier);
    }

    @Override
    public String getName(){
        return this.modName + " Template Pool Generator";
    }

    public static class TemplatePoolBuilder {

        private final String owningModid;
        private final ResourceLocation identifier;
        private ResourceLocation fallbackPool = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");
        private final List<TemplatePoolEntryBuilder> entries = new ArrayList<>();

        private TemplatePoolBuilder(String owningModid, ResourceLocation identifier){
            this.owningModid = owningModid;
            this.identifier = identifier;
        }

        public TemplatePoolBuilder fallback(String namespace, String identifier){
            this.fallbackPool = ResourceLocation.fromNamespaceAndPath(namespace, identifier);
            return this;
        }

        public TemplatePoolBuilder entry(String template, Consumer<TemplatePoolEntryBuilder> entryBuilder){
            TemplatePoolEntryBuilder entry = new TemplatePoolEntryBuilder(ResourceLocation.fromNamespaceAndPath(this.owningModid, template));
            entryBuilder.accept(entry);
            this.entries.add(entry);
            return this;
        }

        public TemplatePoolBuilder commonEntries(BiConsumer<String,TemplatePoolEntryBuilder> entryBuilder, String... templates){
            Arrays.stream(templates).forEach(t -> this.entry(t, o -> entryBuilder.accept(t, o)));
            return this;
        }

        public TemplatePoolBuilder commonEntries(Consumer<TemplatePoolEntryBuilder> entryBuilder, String... templates){
            return this.commonEntries((template, builder) -> entryBuilder.accept(builder), templates);
        }
    }

    public static class TemplatePoolEntryBuilder {

        private final ResourceLocation location;
        private final List<StructureProcessor> processors = new ArrayList<>();
        private StructureTemplatePool.Projection projection = StructureTemplatePool.Projection.RIGID;
        private Integer groundLevel;
        private ResourceLocation type = ResourceLocation.fromNamespaceAndPath("formations", "single_pool_element");
        private int weight = 1;

        private TemplatePoolEntryBuilder(ResourceLocation location){
            this.location = location;
        }

        public TemplatePoolEntryBuilder processors(StructureProcessor... processors){
            this.processors.addAll(Arrays.asList(processors));
            return this;
        }

        public TemplatePoolEntryBuilder rigidProjection(){
            this.projection = StructureTemplatePool.Projection.RIGID;
            return this;
        }

        public TemplatePoolEntryBuilder terrainMatchingProjection(){
            this.projection = StructureTemplatePool.Projection.TERRAIN_MATCHING;
            return this;
        }

        public TemplatePoolEntryBuilder groundLevel(int level){
            this.groundLevel = level;
            return this;
        }

        public TemplatePoolEntryBuilder weight(int weight){
            this.weight = weight;
            return this;
        }
    }
}
