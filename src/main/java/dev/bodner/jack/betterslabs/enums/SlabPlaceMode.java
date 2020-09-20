package dev.bodner.jack.betterslabs.enums;

import java.util.Arrays;

public enum SlabPlaceMode {
    ALL,
    HORIZONTAL,
    VERTICAL;

    private static final SlabPlaceMode[] SLAB_PLACE_MODES = values();

    public SlabPlaceMode next(){
        return SLAB_PLACE_MODES[(this.ordinal()+1) % SLAB_PLACE_MODES.length];
    }
}
