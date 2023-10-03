package com.supermartijn642.formations.generators;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supermartijn642.core.generator.ResourceGenerator;
import com.supermartijn642.core.generator.ResourceType;
import com.supermartijn642.core.registry.GeneratorRegistrationHandler;
import com.supermartijn642.core.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created 04/09/2023 by SuperMartijn642
 */
public final class StructureResourceGenerators {

    private final String modid;
    private final Map<String,StructureConfigurator> structures = new LinkedHashMap<>();
    private boolean registered = false;

    public StructureResourceGenerators(String modid){
        this.modid = modid;
    }

    public void register(){
        if(this.registered)
            throw new IllegalStateException("Registration has already completed!");
        this.registered = true;

        GeneratorRegistrationHandler handler = GeneratorRegistrationHandler.get(this.modid);

        // Create the structure properties
        List<StructureConfiguration> properties = this.structures.values().stream()
            .map(structure -> {
                StructureConfiguration structureConfiguration = StructureConfiguration.create(this.modid, structure.name);
                structure.configureStructure(structureConfiguration);
                return structureConfiguration;
            })
            .toList();

        // Template pools
        handler.addGenerator(cache -> new TemplatePoolGenerator(this.modid, cache) {
            @Override
            public void generate(){
                StructureResourceGenerators.this.structures.values().forEach(structure -> structure.createTemplatePools(this));
            }
        });
        // Loot tables
        handler.addGenerator(cache -> new FormationsLootTableGenerator(this.modid, cache) {
            @Override
            public void generate(){
                StructureResourceGenerators.this.structures.values().forEach(structure -> structure.createLootTables(this));
            }
        });
        // Biome tags
        handler.addGenerator(cache -> new ResourceGenerator(this.modid, cache) {
            final List<Pair<String,JsonObject>> data = new ArrayList<>();

            @Override
            public void generate(){
                properties.forEach(structure -> {
                    JsonArray entries = new JsonArray();
                    structure.biomeTags.forEach(tag -> {
                        if(tag.getNamespace().equals("minecraft"))
                            entries.add("#" + tag);
                        else{
                            JsonObject object = new JsonObject();
                            object.addProperty("id", "#" + tag);
                            object.addProperty("required", false);
                            entries.add(object);
                        }
                    });
                    structure.biomes.forEach(biome -> {
                        if(biome.getNamespace().equals("minecraft"))
                            entries.add(biome.toString());
                        else{
                            JsonObject object = new JsonObject();
                            object.addProperty("id", biome.toString());
                            object.addProperty("required", false);
                            entries.add(object);
                        }
                    });

                    JsonObject json = new JsonObject();
                    json.add("values", entries);
                    this.data.add(Pair.of(structure.identifier, json));
                    this.cache.trackToBeGeneratedResource(ResourceType.DATA, this.modid, "tags/worldgen/biome/has_structure", structure.identifier, ".json");
                });
            }

            @Override
            public void save(){
                this.data.forEach(pair -> this.cache.saveJsonResource(ResourceType.DATA, pair.right(), this.modid, "tags/worldgen/biome/has_structure", pair.left()));
            }

            @Override
            public String getName(){
                return this.modName + " Biome Tag Generator";
            }
        });
        // Structure
        handler.addGenerator(cache -> new ResourceGenerator(this.modid, cache) {
            final List<Pair<String,JsonObject>> data = new ArrayList<>();

            @Override
            public void generate(){
                properties.forEach(structure -> {
                    JsonObject json = new JsonObject();
                    structure.typeProperties.toJson(json);
                    json.addProperty("biomes", "#" + this.modid + ":has_structure/" + structure.identifier);
                    json.addProperty("step", structure.generationStep.getSerializedName());
                    json.addProperty("terrain_adaptation", structure.terrainAdjustment.getSerializedName());
                    json.add("spawn_overrides", new JsonObject());
                    this.data.add(Pair.of(structure.identifier, json));
                });
            }

            @Override
            public void save(){
                this.data.forEach(pair -> this.cache.saveJsonResource(ResourceType.DATA, pair.right(), this.modid, "worldgen/structure", pair.left()));
            }

            @Override
            public String getName(){
                return this.modName + " Structure Generator";
            }
        });

        // Rarity structure sets
        handler.addGenerator(cache -> new FormationsStructureSetGenerator(this.modid, cache) {
            @Override
            public void generate(){
                properties.forEach(structure -> this.addStructure(structure.structureSet, new ResourceLocation(this.modid, structure.identifier), structure.weight));
            }
        });
    }

    public void addStructure(StructureConfigurator configurer){
        if(this.registered)
            throw new IllegalStateException("Cannot add structures after registration has completed!");

        StructureConfigurator previous = this.structures.put(configurer.identifier, configurer);
        if(previous != null)
            throw new IllegalStateException("Duplicate structure with name '" + configurer.name + "'!");
    }
}
