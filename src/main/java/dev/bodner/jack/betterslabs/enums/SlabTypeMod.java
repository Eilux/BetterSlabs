package dev.bodner.jack.betterslabs.enums;

import net.minecraft.util.StringIdentifiable;

public enum SlabTypeMod implements StringIdentifiable {
    TOP("top"),
    BOTTOM("bottom"),
    DOUBLE("double"),
    NORTH("north"),
    SOUTH("south"),
    EAST("east"),
    WEST("west"),
    DOUBLEX("doublex"),
    DOUBLEZ("doublez");

    private final String name;

    SlabTypeMod(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}
