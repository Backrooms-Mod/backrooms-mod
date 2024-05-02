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
import net.minecraft.entity.decoration.painting.PaintingMotive;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class RegistryHelper {

    public static <T extends Codec<? extends ChunkGenerator>> T get(String id, T chunkGenerator) {
        return Registry.register(Registry.CHUNK_GENERATOR, BackroomsMod.id(id), chunkGenerator);
    }

    public static RegistryKey<Biome> get(String id, Biome biome) {
        Registry.register(BuiltinRegistries.BIOME, BackroomsMod.id(id), biome);
        return RegistryKey.of(Registry.BIOME_KEY, BackroomsMod.id(id));
    }

    public static <T extends BlockEntity> BlockEntityType<T> get(String id, FabricBlockEntityTypeBuilder<T> builder) {
        Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id);
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, BackroomsMod.id(id), builder.build(type));
    }

    public static <T extends PaintingMotive> T get(String id, T painting) {
        return Registry.register(Registry.PAINTING_MOTIVE, BackroomsMod.id(id), painting);
    }

    public static SoundEvent get(String id) {
        return get(id, new SoundEvent(BackroomsMod.id(id)));
    }

    public static <T extends SoundEvent> T get(String id, T sound) {
        return Registry.register(Registry.SOUND_EVENT, BackroomsMod.id(id), sound);
    }

    @Deprecated
    public static <T extends Block> T get(Identifier id, T block) {
        return Registry.register(Registry.BLOCK, id, block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block) {
        return Registry.register(Registry.BLOCK, BackroomsMod.id(id), block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block, ItemGroup group) {
        get(id, new BlockItem(block, new FabricItemSettings().group(group)));
        return get(id, block);
    }

    @Deprecated
    public static <T extends Block> T get(String id, T block, FabricItemSettings settings) {
        get(id, new BlockItem(block, settings));
        return get(id, block);
    }

    @Deprecated
    public static <T extends Item> T get(String id, T item) {
        return Registry.register(Registry.ITEM, BackroomsMod.id(id), item);
    }

    @Deprecated
    public static <E extends Entity, T extends EntityType<E>> T get(String id, T entity) {
        return Registry.register(Registry.ENTITY_TYPE, BackroomsMod.id(id), entity);
    }
}