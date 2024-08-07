package com.kpabr.backrooms.block.entity;

import com.kpabr.backrooms.init.BackroomsBlocks;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.util.StringHelper;
import net.minecraft.util.math.BlockPos;

import com.google.common.collect.Iterables;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.UserCache;
import net.minecraft.util.Util;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class MaskBlockEntity extends BlockEntity {
    public static final String MASK_OWNER_KEY = "MaskOwner";
    @Nullable
    private static UserCache userCache;
    @Nullable
    private static MinecraftSessionService sessionService;
    @Nullable
    private static Executor executor;
    @Nullable
    private GameProfile owner;
    private int ticksPowered;
    private boolean powered;

    public MaskBlockEntity(BlockPos pos, BlockState state) {
        super(BackroomsBlocks.MASK, pos, state);
    }

    public static void setServices(UserCache userCache, MinecraftSessionService sessionService, Executor executor) {
        MaskBlockEntity.userCache = userCache;
        MaskBlockEntity.sessionService = sessionService;
        MaskBlockEntity.executor = executor;
    }

    public static void clearServices() {
        userCache = null;
        sessionService = null;
        executor = null;
    }

    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        if (this.owner != null) {
            NbtCompound nbtCompound = new NbtCompound();
            NbtHelper.writeGameProfile(nbtCompound, this.owner);
            nbt.put(MASK_OWNER_KEY, nbtCompound);
        }

    }

    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains(MASK_OWNER_KEY, 10)) {
            this.setOwner(NbtHelper.toGameProfile(nbt.getCompound(MASK_OWNER_KEY)));
        } else if (nbt.contains("ExtraType", 8)) {
            String string = nbt.getString("ExtraType");
            if (!StringHelper.isEmpty(string)) {
                this.setOwner(new GameProfile(null, string));
            }
        }

    }

    public static void tick(World world, BlockPos pos, BlockState state, MaskBlockEntity blockEntity) {
        if (world.isReceivingRedstonePower(pos)) {
            blockEntity.powered = true;
            ++blockEntity.ticksPowered;
        } else {
            blockEntity.powered = false;
        }
    }

    public float getTicksPowered(float tickDelta) {
        return this.powered ? (float) this.ticksPowered + tickDelta : (float) this.ticksPowered;
    }

    @Nullable
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public void setOwner(@Nullable GameProfile owner) {
        synchronized (this) {
            this.owner = owner;
        }

        this.loadOwnerProperties();
    }

    private void loadOwnerProperties() {
        loadProperties(this.owner, (owner) -> {
            this.owner = owner;
            this.markDirty();
        });
    }

    public static void loadProperties(@Nullable GameProfile owner, Consumer<GameProfile> callback) {
        if (owner != null && !StringHelper.isEmpty(owner.getName())
                && (!owner.isComplete() || !owner.getProperties().containsKey("textures")) && userCache != null
                && sessionService != null) {
            userCache.findByNameAsync(owner.getName(), (profile) -> {
                Util.getMainWorkerExecutor().execute(() -> {
                    Util.ifPresentOrElse(profile, (profilex) -> {
                        Property property = (Property) Iterables.getFirst(profilex.getProperties().get("textures"),
                                (Object) null);
                        if (property == null) {
                            profilex = sessionService.fillProfileProperties(profilex, true);
                        }

                        final GameProfile finalProfilex = profilex;
                        executor.execute(() -> {
                            userCache.add(finalProfilex);
                            callback.accept(finalProfilex);
                        });
                    }, () -> {
                        executor.execute(() -> {
                            callback.accept(owner);
                        });
                    });
                });
            });
        } else {
            callback.accept(owner);
        }
    }
}