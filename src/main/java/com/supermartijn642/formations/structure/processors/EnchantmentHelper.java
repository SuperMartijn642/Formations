package com.supermartijn642.formations.structure.processors;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.List;
import java.util.stream.Stream;

/**
 * Created 19/07/2025 by SuperMartijn642
 */
public class EnchantmentHelper {

    /**
     * @param levels        experience levels to enchant with
     * @param allowMultiple whether to allow multiple enchantments
     */
    public static ItemStack getRandomEnchantedBook(int levels, boolean allowMultiple, boolean allowCurses, boolean allowTreasure, RandomSource random, RegistryAccess registryAccess){
        return enchantItem(new ItemStack(Items.BOOK), levels, allowMultiple, allowCurses, allowTreasure, random, registryAccess);
    }

    /**
     * @param levels        experience levels to enchant with
     * @param allowMultiple whether to allow multiple enchantments
     */
    public static ItemStack enchantItem(ItemStack stack, int levels, boolean allowMultiple, boolean allowCurses, boolean allowTreasure, RandomSource random, RegistryAccess registryAccess){
        // Filter enchantment options
        Stream<Holder<Enchantment>> options = registryAccess.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(EnchantmentTags.ON_RANDOM_LOOT).stream().filter(Holder::isBound);
        if(!allowCurses)
            options = options.filter(holder -> !holder.is(EnchantmentTags.CURSE));
        if(!allowTreasure)
            options = options.filter(holder -> !holder.is(EnchantmentTags.TREASURE));

        // Enchant book
        List<EnchantmentInstance> enchantments = net.minecraft.world.item.enchantment.EnchantmentHelper.selectEnchantment(random, stack, levels, options);
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
