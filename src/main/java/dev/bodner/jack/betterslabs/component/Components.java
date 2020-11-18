package dev.bodner.jack.betterslabs.component;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import nerdhub.cardinal.components.api.util.RespawnCopyStrategy;
import net.minecraft.util.Identifier;

public class Components implements EntityComponentInitializer {
    public static final ComponentKey<PlaceModeComponentImpl> MODE_KEY =
            ComponentRegistry.getOrCreate(new Identifier("betterslabs", "placemode"), PlaceModeComponentImpl.class);

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        registry.registerForPlayers(MODE_KEY, playerEntity -> new PlaceModeComponentImpl(), RespawnCopyStrategy.ALWAYS_COPY);
    }
}
