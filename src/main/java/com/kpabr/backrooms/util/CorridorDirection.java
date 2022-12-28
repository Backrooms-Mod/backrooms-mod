package com.kpabr.backrooms.util;

public enum CorridorDirection {
    EAST_WEST(0),
    NORTH_SOUTH(1),
    NORTH_EAST_WEST(2),
    SOUTH_EAST_WEST(3);


    private final int directionValue;
    CorridorDirection(int directionValue/*, DirectionPredicate predicate*/) {
        this.directionValue = directionValue;
    }
    public static CorridorDirection of(int direction) {
        return switch (direction) {
            case 0 -> EAST_WEST;
            case 1 -> NORTH_SOUTH;
            case 2 -> NORTH_EAST_WEST;
            case 3 -> SOUTH_EAST_WEST;
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        };
    }
    public int asInt(CorridorDirection direction) {
        return direction.directionValue;
    }
    public interface DirectionPredicate {
        boolean is(int x, int y);
    }
}
