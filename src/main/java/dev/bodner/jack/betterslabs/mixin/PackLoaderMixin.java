package dev.bodner.jack.betterslabs.mixin;

import dev.bodner.jack.betterslabs.client.BetterslabsClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public abstract class PackLoaderMixin {

    @Shadow public abstract CompletableFuture<Void> reloadResources();

    @Inject(at=@At("TAIL"), method = "<init>")
    public void MinecraftClient(RunArgs args, CallbackInfo ci) {
        BetterslabsClient.createPack();
        reloadResources();
    }

}
