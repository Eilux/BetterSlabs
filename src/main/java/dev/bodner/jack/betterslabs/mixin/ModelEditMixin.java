package dev.bodner.jack.betterslabs.mixin;

import dev.bodner.jack.betterslabs.util.AutoModel;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.main.Main;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.io.IOException;

@Mixin(ModelLoader.class)
public class ModelEditMixin {

    @Shadow @Final private ResourceManager resourceManager;

    @Inject(at = @At("TAIL"), method = "<init>")
    public void ModelLoader(ResourceManager resourceManager, BlockColors blockColors, Profiler profiler, int i, CallbackInfo ci) {

    }
}
