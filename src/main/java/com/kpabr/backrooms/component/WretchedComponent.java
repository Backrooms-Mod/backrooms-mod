package com.kpabr.backrooms.component;

import dev.onyxstudios.cca.api.v3.component.Component;

public interface WretchedComponent extends Component {
    int getValue();

    void setValue(int value);

    void remove(int amount);

    boolean increment();

    void decrement();
}
