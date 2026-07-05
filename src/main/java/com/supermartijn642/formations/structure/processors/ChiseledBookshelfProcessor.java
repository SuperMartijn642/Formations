package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.Formations;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Optional;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
public class ChiseledBookshelfProcessor implements StructureProcessor, FormationsStructureProcessor {

    public static final MapCodec<ChiseledBookshelfProcessor> CODEC = RecordCodecBuilder.mapCodec(instance ->
        instance.group(
            Codec.floatRange(0, 1).optionalFieldOf("slotFillChance", 0.4f).forGetter(p -> p.slotFillChance),
            Codec.intRange(1, 1000).optionalFieldOf("levels").forGetter(p -> p.minLevels != p.maxLevels || p.minLevels == 10 ? Optional.empty() : Optional.of(p.maxLevels)),
            Codec.intRange(1, 1000).optionalFieldOf("min_levels").forGetter(p -> p.minLevels == p.maxLevels ? Optional.empty() : Optional.of(p.minLevels)),
            Codec.intRange(1, 1000).optionalFieldOf("max_levels").forGetter(p -> p.minLevels == p.maxLevels ? Optional.empty() : Optional.of(p.maxLevels)),
            Codec.BOOL.optionalFieldOf("allow_curses", true).forGetter(p -> p.allowCurses)
        ).apply(instance, (slotFillChance, levels, minLevels, maxLevels, allowCurses) -> {
            if(minLevels.isPresent() && maxLevels.isPresent())
                return new ChiseledBookshelfProcessor(slotFillChance, minLevels.get(), maxLevels.get(), allowCurses);
            int l = levels.orElse(10);
            return new ChiseledBookshelfProcessor(slotFillChance, l, l, allowCurses);
        }));

    private final float slotFillChance;
    private final int minLevels, maxLevels;
    private final boolean allowCurses;

    public ChiseledBookshelfProcessor(float slotFillChance, int minLevels, int maxLevels, boolean allowCurses){
        this.slotFillChance = slotFillChance;
        this.minLevels = minLevels;
        this.maxLevels = maxLevels;
        this.allowCurses = allowCurses;
    }

    public ChiseledBookshelfProcessor(float slotFillChance){
        this(slotFillChance, 10, 10, true);
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        BlockState state = block.state();
        if(state != null && state.is(Blocks.CHISELED_BOOKSHELF)){
            // Load the books from the bookshelf's nbt
            NonNullList<ItemStack> books = NonNullList.withSize(6, ItemStack.EMPTY);
            if(block.nbt() != null){
                Identifier name = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_PROCESSOR).getKey(this.codec());
                try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(name::toString, Formations.LOGGER)){
                    ContainerHelper.loadAllItems(TagValueInput.create(reporter, level.registryAccess(), block.nbt()), books);
                }
            }
            // Randomly add books
            RandomSource random = placeSettings.getRandom(pos);
            for(int i = 0; i < 6; i++){ // This isn't very efficient, but since there's only 6 slots, it should be fine
                if(books.get(i).isEmpty() && random.nextFloat() < this.slotFillChance){
                    int levels = random.nextInt(this.maxLevels - this.minLevels + 1) + this.minLevels;
                    ItemStack book = EnchantmentHelper.getRandomEnchantedBook(levels, true, this.allowCurses, true, random, level.registryAccess());
                    books.set(i, book);
                    state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i), true);
                }
            }
            // Convert the books back to nbt
            CompoundTag nbt = block.nbt() == null ? new CompoundTag() : block.nbt().copy();
            Identifier name = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_PROCESSOR).getKey(this.codec());
            try(ProblemReporter.ScopedCollector reporter = new ProblemReporter.ScopedCollector(name::toString, Formations.LOGGER)){
                TagValueOutput output = TagValueOutput.createWithContext(reporter, level.registryAccess());
                ContainerHelper.saveAllItems(output, books);
                nbt.merge(output.buildResult());
            }
            return new BlockInstance(state, nbt);
        }
        return block;
    }

    @Override
    public MapCodec<? extends StructureProcessor> codec(){
        return CODEC;
    }
}
