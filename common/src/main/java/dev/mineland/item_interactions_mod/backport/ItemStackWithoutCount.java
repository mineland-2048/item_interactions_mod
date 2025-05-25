package dev.mineland.item_interactions_mod.backport;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class ItemStackWithoutCount {
    public static final Codec<ItemStack> CODEC = RecordCodecBuilder.create((instance) -> {
        return instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("id").forGetter(ItemStack::getItem),
                ExtraCodecs.intRange(1, 64).fieldOf("Count").orElse(1).forGetter(ItemStack::getCount),
                CompoundTag.CODEC.optionalFieldOf("tag").forGetter((itemStack) -> {
            return Optional.ofNullable(itemStack.getTag());
        })).apply(instance, (item, integer, compoundTag) -> new ItemStack(item, integer));
    });

}
