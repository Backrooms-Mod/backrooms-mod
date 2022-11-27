package com.kpabr.backrooms.component;

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
        // If wretched value less than 0, assign it to zero.
        this.wretched = Math.max(this.wretched, 0);
    }

    @Override
    public boolean increment() {
        return ++this.wretched == 100;
    }

    @Override
    public void decrement() {
        if(wretched != 0) --this.wretched;
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
