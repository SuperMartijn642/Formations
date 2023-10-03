package com.supermartijn642.formations.structure;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.Optional;
import java.util.function.Function;

/**
 * Created 25/09/2023 by SuperMartijn642
 */
public class SimpleStructure extends Structure {

    public static final Codec<SimpleStructure> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            StructureSettings.CODEC.forGetter(s -> s.modifiableStructureInfo().getOriginalStructureInfo().structureSettings()),
            Codec.either(StructurePoolElement.CODEC, StructureTemplatePool.CODEC).fieldOf("template").forGetter(s -> s.structure),
            StructurePlacement.CODEC.fieldOf("placement").forGetter(s -> s.placement)
        ).apply(instance, SimpleStructure::new)
    );

    private final Either<StructurePoolElement,Holder<StructureTemplatePool>> structure;
    private final StructurePlacement placement;

    public SimpleStructure(StructureSettings settings, Either<StructurePoolElement,Holder<StructureTemplatePool>> structure, StructurePlacement placement){
        super(settings);
        this.structure = structure;
        this.placement = placement;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        // Pick a random structure element
        StructurePoolElement element = this.structure.map(
            Function.identity(),
            holder -> holder.value().getRandomTemplate(context.random())
        );
        if(element == EmptyPoolElement.INSTANCE)
            return Optional.empty();

        // Determine a position in the chunk
        int x = context.chunkPos().getBlockX(context.random().nextInt(16)), z = context.chunkPos().getBlockZ(context.random().nextInt(16));
        Rotation rotation = Rotation.getRandom(context.random());
        BoundingBox boundingBox = element.getBoundingBox(context.structureTemplateManager(), new BlockPos(x, 0, z), rotation);

        // Try to find a place to generate the structure
        Optional<Integer> height = this.placement.findHeight(context, boundingBox);
        // Define a random piece to be placed
        return height.map(y -> y - element.getGroundLevelDelta() + 1)
            .filter(y -> y >= context.heightAccessor().getMinBuildHeight())
            .map(y -> new GenerationStub(new BlockPos(x, y, z), piecesBuilder ->
                piecesBuilder.addPiece(new PoolElementStructurePiece(
                    context.structureTemplateManager(),
                    element,
                    new BlockPos(x, y, z),
                    element.getGroundLevelDelta(),
                    rotation,
                    boundingBox.move(0, y, 0)
                ))
            ));
    }

    @Override
    public StructureType<?> type(){
        return FormationsStructures.SIMPLE_STRUCTURE.get();
    }
}
