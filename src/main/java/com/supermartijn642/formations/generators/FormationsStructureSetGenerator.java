package com.supermartijn642.formations.generators;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.supermartijn642.core.generator.ResourceCache;
import com.supermartijn642.core.generator.ResourceGenerator;
import com.supermartijn642.core.generator.ResourceType;
import com.supermartijn642.core.generator.aggregator.ResourceAggregator;
import com.supermartijn642.core.util.Pair;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;

/**
 * Created 04/09/2023 by SuperMartijn642
 */
public abstract class FormationsStructureSetGenerator extends ResourceGenerator {

    private static final ResourceAggregator<Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>>,Pair<Pair<StructureSetKey,String>,Pair<ResourceLocation,Integer>>> AGGREGATOR = new ResourceAggregator<Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>>,Pair<Pair<StructureSetKey,String>,Pair<ResourceLocation,Integer>>>() {
        static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        @Override
        public Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>> initialData(){
            return null;
        }

        @Override
        public Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>> combine(Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>> data, Pair<Pair<StructureSetKey,String>,Pair<ResourceLocation,Integer>> newData){
            if(data == null)
                return Pair.of(newData.left(), new LinkedHashSet<>(Collections.singleton(newData.right())));

            if(!Objects.equals(data.left(), newData.left()))
                throw new IllegalStateException();
            data.right().add(newData.right());
            return data;
        }

        @Override
        public void write(OutputStream stream, Pair<Pair<StructureSetKey,String>,Set<Pair<ResourceLocation,Integer>>> data) throws IOException{
            // Placement
            JsonObject placement = new JsonObject();
            placement.addProperty("type", "minecraft:random_spread");
            StructureSetKey key = data.left().left();
            int salt = key.name().hashCode() ^ data.left().right().hashCode(); // Mix the constant salt with the hash code of the owning modid
            if(salt < 0)
                salt = -salt;
            placement.addProperty("salt", salt);
            placement.addProperty("spacing", key.spacing());
            placement.addProperty("separation", key.separation());

            // Structures
            JsonArray structures = new JsonArray(data.right().size());
            for(Pair<ResourceLocation,Integer> structure : data.right()){
                JsonObject object = new JsonObject();
                object.addProperty("structure", structure.left().toString());
                object.addProperty("weight", structure.right());
                structures.add(object);
            }

            // Combine the data
            JsonObject json = new JsonObject();
            json.add("placement", placement);
            json.add("structures", structures);

            // Write the json
            try(Writer writer = new OutputStreamWriter(stream)){
                this.gson.toJson(json, writer);
            }
        }
    };

    private final List<Pair<StructureSetKey,Pair<ResourceLocation,Integer>>> structures = new ArrayList<>();

    public FormationsStructureSetGenerator(String modid, ResourceCache cache){
        super(modid, cache);
    }

    @Override
    public void save(){
        this.structures.forEach(pair -> this.cache.saveResource(ResourceType.DATA, AGGREGATOR, Pair.of(Pair.of(pair.left(), this.modid), pair.right()), this.modid, "worldgen/structure_set", pair.left().name(), ".json"));
    }

    protected void addStructure(StructureSetKey key, ResourceLocation location, int weight){
        this.structures.add(Pair.of(key, Pair.of(location, weight)));
        this.cache.trackToBeGeneratedResource(ResourceType.DATA, this.modid, "worldgen/structure_set", key.name(), ".json");
    }
}
