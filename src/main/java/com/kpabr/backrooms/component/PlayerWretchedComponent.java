package com.kpabr.backrooms.component;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.NbtCompound;

public class PlayerWretchedComponent implements WretchedComponent {
    private int wretched = 0;

    @Override
    public int getValue() {
        return wretched;
    }

    @Override
    public void setValue(int value) {
        this.wretched = value;
    }

    @Override
    public void remove(int amount) {
        this.wretched -= amount;
    }

    @Override
    public void increment() {
        this.wretched++;
    }

    @Override
    public void decrement() {
        this.wretched--;
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        this.wretched = tag.getInt("wretched");
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        tag.putInt("wretched", this.wretched);
    }
}
