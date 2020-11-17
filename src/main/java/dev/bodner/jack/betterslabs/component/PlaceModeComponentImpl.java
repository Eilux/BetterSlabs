package dev.bodner.jack.betterslabs.component;

import dev.bodner.jack.betterslabs.enums.SlabPlaceMode;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import net.minecraft.nbt.CompoundTag;

interface PlaceModeComponent extends Component {
    void incrementPlaceMode();
    SlabPlaceMode getPlaceMode();
}

public class PlaceModeComponentImpl implements PlaceModeComponent, AutoSyncedComponent {
    private SlabPlaceMode mode;

    public PlaceModeComponentImpl(){
        mode = SlabPlaceMode.ALL;
    }

    @Override
    public void incrementPlaceMode() {
        this.mode = this.mode.next();
        Components.MODE_KEY.sync(this.mode);
    }

    @Override
    public SlabPlaceMode getPlaceMode() {
        return this.mode;
    }

    @Override
    public void readFromNbt(CompoundTag compoundTag) {
        try{
            this.mode = SlabPlaceMode.fromString(compoundTag.getString("placemode"));
            Components.MODE_KEY.sync(this.mode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToNbt(CompoundTag compoundTag) {
        compoundTag.putString("placemode", SlabPlaceMode.toString(this.mode));
        Components.MODE_KEY.sync(this.mode);
    }
}
