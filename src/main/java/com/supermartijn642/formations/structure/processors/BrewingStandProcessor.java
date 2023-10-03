package com.supermartijn642.formations.structure.processors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.supermartijn642.formations.FormationsStructures;
import com.supermartijn642.formations.structure.BlockInstance;
import com.supermartijn642.formations.structure.FormationsStructureProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrewingStandBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Created 01/09/2023 by SuperMartijn642
 */
public class BrewingStandProcessor extends StructureProcessor implements FormationsStructureProcessor {

    public static final Codec<BrewingStandProcessor> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.floatRange(0, 1).optionalFieldOf("slotFillChance", 0.5f).forGetter(p -> p.slotFillChance), Codec.intRange(0, 64).optionalFieldOf("maxBlazePowder", 16).forGetter(p -> p.maxBlazePowder)).apply(instance, BrewingStandProcessor::new));

    private final float slotFillChance;
    private final int maxBlazePowder;

    public BrewingStandProcessor(float slotFillChance, int maxBlazePowder){
        this.slotFillChance = slotFillChance;
        this.maxBlazePowder = maxBlazePowder;
    }

    @Override
    public @NotNull BlockInstance processBlock(BlockInstance block, BlockPos pos, LevelReader level, BlockPos piecePosition, BlockPos structurePosition, StructurePlaceSettings placeSettings, Map<BlockPos,BlockInstance> pieceBlocks){
        if(block.state() != null && block.state().is(Blocks.BREWING_STAND)){
            BlockState state = block.state();
            // Load the potions from the brewing stand's nbt
            NonNullList<ItemStack> potions = NonNullList.withSize(5, ItemStack.EMPTY);
            if(block.nbt() != null)
                ContainerHelper.loadAllItems(block.nbt(), potions);
            // Randomly add potions
            RandomSource random = placeSettings.getRandom(pos);
            for(int i = 0; i < 3; i++){
                if(potions.get(i).isEmpty() && random.nextFloat() < this.slotFillChance){
                    Potion potion = BuiltInRegistries.POTION.getRandom(random).get().value();
                    ItemStack bottle = PotionUtils.setPotion((random.nextFloat() < 0.4f ? Items.POTION : random.nextFloat() < 0.66f ? Items.SPLASH_POTION : Items.LINGERING_POTION).getDefaultInstance(), potion);
                    potions.set(i, bottle);
                    state = state.setValue(BrewingStandBlock.HAS_BOTTLE[i], true);
                }
            }
            // Convert the potions back to nbt
            CompoundTag nbt = block.nbt() == null ? new CompoundTag() : block.nbt().copy();
            ContainerHelper.saveAllItems(nbt, potions, true);
            return new BlockInstance(state, nbt);
        }
        return block;
    }

    @Override
    protected StructureProcessorType<?> getType(){
        return FormationsStructures.BREWING_STAND_PROCESSOR.get();
    }
}
