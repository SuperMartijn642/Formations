package com.supermartijn642.formations.generators.properties;

import com.google.gson.JsonObject;
import com.supermartijn642.formations.structure.StructurePlacement;
import net.minecraft.resources.ResourceLocation;

/**
 * Created 01/10/2023 by SuperMartijn642
 */
public class SimpleStructureProperties implements StructureProperties {

    private final String namespace;
    private ResourceLocation template;
    private ResourceLocation templatePool;
    private StructurePlacement placement = StructurePlacement.SURFACE;

    public SimpleStructureProperties(String namespace){
        this.namespace = namespace;
    }

    public SimpleStructureProperties template(String template){
        if(this.templatePool != null)
            throw new IllegalStateException("Cannot have both a template and a template pool!");

        this.template = new ResourceLocation(this.namespace, template);
        return this;
    }

    public SimpleStructureProperties templatePool(String template){
        if(this.template != null)
            throw new IllegalStateException("Cannot have both a template and a template pool!");

        this.templatePool = new ResourceLocation(this.namespace, template);
        return this;
    }

    public SimpleStructureProperties placement(StructurePlacement placement){
        this.placement = placement;
        return this;
    }

    @Override
    public void toJson(JsonObject json){
        if(this.template == null && this.templatePool == null)
            throw new IllegalStateException("Missing starting template!");

        json.addProperty("type", "formations:simple");
        json.addProperty("template", this.template == null ? this.templatePool.toString() : this.template.toString());
        json.addProperty("placement", this.placement.getSerializedName());
    }
}
