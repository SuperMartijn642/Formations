package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChiseledBookShelfBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
public class ChiseledBookshelfProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final MapCodec<ChiseledBookshelfProcessor> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(Codec.floatRange(0, 1).optionalFieldOf("slotFillChance", 0.4f).forGetter(p -> p.slotFillChance)).apply(instance, ChiseledBookshelfProcessor::new));

    private final float slotFillChance;

    public ChiseledBookshelfProcessor(float slotFillChance){
        this.slotFillChance = slotFillChance;
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        BlockState state = block.state();
        if(state != null && state.is(Blocks.CHISELED_BOOKSHELF)){
            // Load the books from the bookshelf's nbt
            NonNullList<ItemStack> books = NonNullList.withSize(6, ItemStack.EMPTY);
            if(block.nbt() != null)
                ContainerHelper.loadAllItems(block.nbt(), books, level.registryAccess());
            // Randomly add books
            RandomSource random = placeSettings.getRandom(pos);
            for(int i = 0; i < 6; i++){ // This isn't very efficient, but since there's only 6 slots, it should be fine
                if(books.get(i).isEmpty() && random.nextFloat() < this.slotFillChance){
                    //noinspection OptionalGetWithoutIsPresent
                    Holder<Enchantment> enchantment = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getRandom(random).get();
                    ItemStack book = EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, random.nextInt(enchantment.value().getMaxLevel()) + 1));
                    books.set(i, book);
                    state = state.setValue(ChiseledBookShelfBlock.SLOT_OCCUPIED_PROPERTIES.get(i), true);
                }
            }
            // Convert the books back to nbt
            CompoundTag nbt = block.nbt() == null ? new CompoundTag() : block.nbt().copy();
            ContainerHelper.saveAllItems(nbt, books, level.registryAccess());
            return new BlockInstance(state, nbt);
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.CHISELED_BOOKSHELF_PROCESSOR.get();
    }
}
