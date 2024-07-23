package com.kpabr.backrooms.util;

import com.mojang.datafixers.types.Type;
import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import com.kpabr.backrooms.BackroomsMod;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RegistryHelper {

    public static <T extends Codec<? extends ChunkGenerator>> T get(String id, T chunkGenerator) {
        return Registry.register(Registries.CHUNK_GENERATOR, BackroomsMod.id(id), chunkGenerator);
    }

    public static RegistryKey<Biome> getBiome(String id) {
        RegistryKey<Biome> key = RegistryKey.of(RegistryKeys.BIOME, BackroomsMod.id(id));
        return key;
    }

    public static <T extends BlockEntity> BlockEntityType<T> get(String id, FabricBlockEntityTypeBuilder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, BackroomsMod.id(id), builder.build(type));
    }

    public static <T extends PaintingVariant> T get(String id, T painting) {
        return Registry.register(Registries.PAINTING_VARIANT, BackroomsMod.id(id), painting);
    }

    public static SoundEvent get(String id) {
        return get(id, SoundEvent.of(BackroomsMod.id(id)));
    }

    public static <T extends SoundEvent> T get(String id, T sound) {
        return Registry.register(Registries.SOUND_EVENT, BackroomsMod.id(id), sound);
    }

    @Deprecated
    public static <T extends Block> T get(Identifier id, T block) {
        return Registry.register(Registries.BLOCK, id, block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block) {
        return Registry.register(Registries.BLOCK, BackroomsMod.id(id), block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block, ItemGroup group) {
        get(id, new BlockItem(block, new FabricItemSettings()));
        return get(id, block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block, FabricItemSettings settings) {
        get(id, new BlockItem(block, settings));
        return get(id, block);
    }

    @Deprecated
    public static <T extends Item> T get(String id, T item) {
        return Registry.register(Registries.ITEM, BackroomsMod.id(id), item);
    }

    @Deprecated
    public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
        return Registry.register(Registries.ENTITY_TYPE, BackroomsMod.id(id), entity);
    }
}