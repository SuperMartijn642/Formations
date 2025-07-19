package com.supermartijn642.formations.structure.processors;

import net.fabricmc.fabric.api.item.v1.EnchantingContext;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * Created 19/07/2025 by SuperMartijn642
 */
public class EnchantmentHelper {

    /**
     * @param levels        experience levels to enchant with
     * @param allowMultiple whether to allow multiple enchantments
     */
    public static ItemStack getRandomEnchantedBook(int levels, boolean allowMultiple, boolean allowCurses, boolean allowTreasure, RandomSource random, FeatureFlagSet featureFlags){
        return enchantItem(new ItemStack(Items.BOOK), levels, allowMultiple, allowCurses, allowTreasure, random, featureFlags);
    }

    /**
     * @param levels        experience levels to enchant with
     * @param allowMultiple whether to allow multiple enchantments
     */
    public static ItemStack enchantItem(ItemStack stack, int levels, boolean allowMultiple, boolean allowCurses, boolean allowTreasure, RandomSource random, FeatureFlagSet featureFlags){
        // Adjust level for item
        int enchantmentValue = stack.getItem().getEnchantmentValue();
        if(enchantmentValue <= 0)
            return stack;
        levels += 1 + random.nextInt(enchantmentValue / 4 + 1) + random.nextInt(enchantmentValue / 4 + 1);
        float multiplier = (random.nextFloat() + random.nextFloat() - 1) * 0.15f + 1;
        levels = Math.max(1, Math.round(levels * multiplier));

        // Find enchantments
        List<EnchantmentInstance> options = new ArrayList<>();
        boolean isBook = stack.is(Items.BOOK);
        for(Enchantment enchantment : BuiltInRegistries.ENCHANTMENT){
            if(enchantment.isDiscoverable()
                && (allowCurses || !enchantment.isCurse())
                && (allowTreasure || !enchantment.isTreasureOnly())
                && (isBook || stack.canBeEnchantedWith(enchantment, EnchantingContext.RANDOM_ENCHANTMENT))){
                for(int level = enchantment.getMaxLevel(); level >= enchantment.getMinLevel(); level--){
                    if(levels >= enchantment.getMinCost(level) && levels <= enchantment.getMaxCost(level)){
                        options.add(new EnchantmentInstance(enchantment, level));
                        break;
                    }
                }
            }
        }
        if(options.isEmpty())
            return stack;

        // Enchant item
        List<EnchantmentInstance> enchantments;
        if(allowMultiple){
            enchantments = new ArrayList<>();
            WeightedRandom.getRandomItem(random, options).ifPresent(enchantments::add);
            while(random.nextInt(50) <= levels){
                if(!enchantments.isEmpty())
                    net.minecraft.world.item.enchantment.EnchantmentHelper.filterCompatibleEnchantments(options, Util.lastOf(enchantments));
                if(options.isEmpty())
                    break;
                WeightedRandom.getRandomItem(random, options).ifPresent(enchantments::add);
                levels /= 2;
            }
        }else
            enchantments = WeightedRandom.getRandomItem(random, options).map(List::of).orElseGet(List::of);
        if(enchantments.isEmpty())
            return stack;

        // Enchant item
        if(stack.is(Items.BOOK))
            stack = new ItemStack(Items.ENCHANTED_BOOK);
        if(allowMultiple){
            for(EnchantmentInstance enchantment : enchantments)
                stack.enchant(enchantment.enchantment, enchantment.level);
        }else{
            EnchantmentInstance enchantment = enchantments.get(0);
            stack.enchant(enchantment.enchantment, enchantment.level);
        }
        return stack;
    }
}
