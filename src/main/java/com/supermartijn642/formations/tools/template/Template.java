package com.supermartijn642.formations.tools.template;

import com.supermartijn642.core.TextComponents;
import com.supermartijn642.formations.Formations;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.AABB;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created 25/08/2023 by SuperMartijn642
 */
public class Template {

    public static final int MAX_NAME_LENGTH = 50;

    public static boolean isValidName(String name){
        name = name.trim();
        return name.length() <= MAX_NAME_LENGTH && name.matches("[a-zA-Z0-9_/-]+") && name.charAt(0) != '/' && name.charAt(name.length() - 1) != '/';
    }

    public static Template create(AABB area, String name){
        return new Template(area, name);
    }

    public static Template load(CompoundTag data){
        AABB area = new AABB(
            data.getDouble("areaMinX"), data.getDouble("areaMinY"), data.getDouble("areaMinZ"),
            data.getDouble("areaMaxX"), data.getDouble("areaMaxY"), data.getDouble("areaMaxZ")
        );
        String name = data.getString("name");
        return create(area, name);
    }

    public static Template load(FriendlyByteBuf buffer){
        AABB area = new AABB(
            buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
            buffer.readDouble(), buffer.readDouble(), buffer.readDouble()
        );
        String name = buffer.readUtf(MAX_NAME_LENGTH);
        return create(area, name);
    }

    private final AABB area;
    private final String name;

    private Template(AABB area, String name){
        this.area = area;
        this.name = name;
    }

    public AABB getArea(){
        return this.area;
    }

    public String getName(){
        return this.name;
    }

    public boolean recordAndExport(Level level, Path output){
        // Report empty containers without a loot table
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for(int x = (int)this.area.minX; x < this.area.maxX; x++){
            for(int y = (int)this.area.minY; y < this.area.maxY; y++){
                loop:
                for(int z = (int)this.area.minZ; z < this.area.maxZ; z++){
                    pos.set(x, y, z);
                    BlockEntity entity = level.getBlockEntity(pos);
                    if(entity instanceof RandomizableContainerBlockEntity container){
                        if(container.lootTable == null){
                            for(int i = 0; i < container.getContainerSize(); i++){
                                if(!container.getItem(i).isEmpty())
                                    continue loop;
                            }
                            Formations.LOGGER.warn("Template '" + this.name + "' has an empty '" + TextComponents.blockState(container.getBlockState()).format() + "' at {x=" + pos.getX() + ", y=" + pos.getY() + ", z=" + pos.getZ() + "}!");
                        }else if(level.getServer() != null){
                            LootTable lootTable = level.getServer().getLootTables().get(container.lootTable);
                            if(lootTable == LootTable.EMPTY)
                                Formations.LOGGER.warn("Template '" + this.name + "' has a '" + TextComponents.blockState(container.getBlockState()).format() + "' at {x=" + pos.getX() + ", y=" + pos.getY() + ", z=" + pos.getZ() + "} with missing loot table '" + container.lootTable + "'!");
                        }
                    }
                }
            }
        }

        // Create a structure template and record the blocks in the world
        StructureTemplate structure = new StructureTemplate();
        structure.fillFromWorld(level, new BlockPos((int)this.area.minX, (int)this.area.minY, (int)this.area.minZ), new Vec3i((int)this.area.getXsize(), (int)this.area.getYsize(), (int)this.area.getZsize()), true, Blocks.STRUCTURE_VOID);
        // Convert the structure template to nbt
        CompoundTag data = structure.save(new CompoundTag());
        // Write the nbt to the output file
        try{
            Files.createDirectories(output.getParent());
            NbtIo.writeCompressed(data, output.toFile());
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        return true;
    }

    public CompoundTag write(){
        CompoundTag data = new CompoundTag();
        data.putDouble("areaMinX", this.area.minX);
        data.putDouble("areaMinY", this.area.minY);
        data.putDouble("areaMinZ", this.area.minZ);
        data.putDouble("areaMaxX", this.area.maxX);
        data.putDouble("areaMaxY", this.area.maxY);
        data.putDouble("areaMaxZ", this.area.maxZ);
        data.putString("name", this.name);
        return data;
    }

    public void write(FriendlyByteBuf buffer){
        buffer.writeDouble(this.area.minX);
        buffer.writeDouble(this.area.minY);
        buffer.writeDouble(this.area.minZ);
        buffer.writeDouble(this.area.maxX);
        buffer.writeDouble(this.area.maxY);
        buffer.writeDouble(this.area.maxZ);
        buffer.writeUtf(this.name);
    }

    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if(o == null || this.getClass() != o.getClass()) return false;

        Template template = (Template)o;

        if(!this.area.equals(template.area)) return false;
        return this.name.equals(template.name);
    }

    @Override
    public int hashCode(){
        int result = this.area.hashCode();
        result = 31 * result + this.name.hashCode();
        return result;
    }
}
