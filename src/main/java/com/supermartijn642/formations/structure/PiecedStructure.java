package com.supermartijn642.formations.structure;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.Optional;

/**
 * Created 01/10/2023 by SuperMartijn642
 */
public class PiecedStructure extends Structure {

    public static final MapCodec<PiecedStructure> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            StructureSettings.CODEC.forGetter(structure -> structure.settings),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            Codec.intRange(0, 7).fieldOf("max_depth").forGetter(structure -> structure.maxDepth),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
            StructurePlacement.CODEC.fieldOf("placement").forGetter(s -> s.placement)
        ).apply(instance, PiecedStructure::new)
    );

    private final Holder<StructureTemplatePool> startPool;
    private final int maxDepth;
    private final int maxDistanceFromCenter;
    private final StructurePlacement placement;

    public PiecedStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int maxDepth, int maxDistanceFromCenter, StructurePlacement placement){
        super(settings);
        this.startPool = startPool;
        this.maxDepth = maxDepth;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.placement = placement;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context){
        // Pick a random structure element
        StructurePoolElement element = this.startPool.value().getRandomTemplate(context.random());
        if(element == EmptyPoolElement.INSTANCE)
            return Optional.empty();

        // Determine a position in the chunk
        int x = context.chunkPos().getBlockX(context.random().nextInt(16)), z = context.chunkPos().getBlockZ(context.random().nextInt(16));
        Rotation rotation = Rotation.getRandom(context.random());
        BoundingBox boundingBox = element.getBoundingBox(context.structureTemplateManager(), new BlockPos(x, 0, z), rotation);

        // Try to find a place to generate the structure
        Optional<Integer> height = this.placement.findHeight(context, boundingBox);
        if(height.isEmpty())
            return Optional.empty();

        BlockPos pos = new BlockPos(x, height.get() + 1, z);
        return addPieces(context, element, rotation, this.maxDepth, pos, this.maxDistanceFromCenter);
    }

    @Override
    public StructureType<?> type(){
        return FormationsStructures.PIECED_STRUCTURE;
    }

    private static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext context, StructurePoolElement startElement, Rotation rotation, int maxDepth, BlockPos pos, int maxDistanceFromCenter){
        if(startElement == EmptyPoolElement.INSTANCE)
            return Optional.empty();

        ChunkGenerator chunkGenerator = context.chunkGenerator();
        StructureTemplateManager templateManager = context.structureTemplateManager();
        LevelHeightAccessor levelHeightAccessor = context.heightAccessor();
        WorldgenRandom random = context.random();
        Registry<StructureTemplatePool> registry = context.registryAccess().registryOrThrow(Registries.TEMPLATE_POOL);

        PoolElementStructurePiece structurePiece = new PoolElementStructurePiece(templateManager, startElement, pos, startElement.getGroundLevelDelta(), rotation, startElement.getBoundingBox(templateManager, pos, rotation));
        BoundingBox boundingBox = structurePiece.getBoundingBox();
        int centeredX = (boundingBox.maxX() + boundingBox.minX()) / 2;
        int centeredZ = (boundingBox.maxZ() + boundingBox.minZ()) / 2;
        int heightOffset = boundingBox.minY() + structurePiece.getGroundLevelDelta();
        structurePiece.move(0, pos.getY() - heightOffset, 0);
        BlockPos centeredPos = new BlockPos(centeredX, pos.getY(), centeredZ);

        return Optional.of(new Structure.GenerationStub(centeredPos, piecesBuilder -> {
            piecesBuilder.addPiece(structurePiece);
            if(maxDepth <= 0)
                return;

            VoxelShape allowedSpace = Shapes.join(Shapes.create(new AABB(centeredPos).inflate(maxDistanceFromCenter)), Shapes.create(AABB.of(boundingBox)), BooleanOp.ONLY_FIRST);
            ArrayList<PoolElementStructurePiece> pieces = Lists.newArrayList();
            JigsawPlacement.addPieces(context.randomState(), maxDepth, false, chunkGenerator, templateManager, levelHeightAccessor, random, registry, structurePiece, pieces, allowedSpace, PoolAliasLookup.EMPTY);
            pieces.forEach(piecesBuilder::addPiece);
        }));
    }
}
