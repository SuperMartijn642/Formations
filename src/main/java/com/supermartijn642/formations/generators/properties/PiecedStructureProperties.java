package com.supermartijn642.formations.generators.properties;

import com.google.gson.JsonObject;
import com.supermartijn642.formations.structure.StructurePlacement;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 03/09/2023 by SuperMartijn642
 */
public class PiecedStructureProperties implements StructureProperties {

    private final String namespace;
    private ResourceLocation startPool;
    private int maxDepth = 1;
    private int maxDistance = 116;
    private StructurePlacement placement = StructurePlacement.SURFACE;

    public PiecedStructureProperties(String namespace){
        this.namespace = namespace;
    }

    public PiecedStructureProperties startPool(String pool){
        this.startPool = new ResourceLocation(this.namespace, pool);
        return this;
    }

    public PiecedStructureProperties maxDepth(int depth){
        if(depth < 0 || depth > 7)
            throw new IllegalArgumentException("Max depth must be between 0 and 7, not '" + depth + "'!");
        this.maxDepth = depth;
        return this;
    }

    public PiecedStructureProperties maxDistance(int distance){
        if(distance < 1 || distance > 128)
            throw new IllegalArgumentException("Max distance must be between 1 and 128, not '" + distance + "'!");
        this.maxDistance = distance;
        return this;
    }

    public PiecedStructureProperties placement(StructurePlacement placement){
        this.placement = placement;
        return this;
    }

    @Override
    public void toJson(JsonObject json){
        if(this.startPool == null)
            throw new IllegalStateException("Missing starting pool!");

        json.addProperty("type", "formations:pieced");
        json.addProperty("start_pool", this.startPool.toString());
        json.addProperty("max_depth", this.maxDepth);
        json.addProperty("max_distance_from_center", this.maxDistance);
        json.addProperty("placement", this.placement.getSerializedName());
    }
}
