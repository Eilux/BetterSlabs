package dev.bodner.jack.betterslabs.enums;

public enum SlabPlaceMode {
    ALL,
    HORIZONTAL,
    VERTICAL;

    private static final SlabPlaceMode[] SLAB_PLACE_MODES = values();

    public SlabPlaceMode next(){
        return SLAB_PLACE_MODES[(this.ordinal()+1) % SLAB_PLACE_MODES.length];
    }

    public static String toString(SlabPlaceMode mode) {
        switch (mode){
            case HORIZONTAL:
                return "horizontal";
            case VERTICAL:
                return "vertical";
            default:
                return "all";
        }
    }

    public static SlabPlaceMode fromString(String string) throws Exception {
        switch (string){
            case "all":
                return SlabPlaceMode.ALL;
            case "horizontal":
                return SlabPlaceMode.HORIZONTAL;
            case "vertical":
                return SlabPlaceMode.VERTICAL;
            default:
                throw new Exception("Expected: all|horizontal|vertical, got: "+string);
        }
    }
}
