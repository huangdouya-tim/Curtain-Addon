package dev.dubhe.curtain.mixins;

import dev.dubhe.curtain.utils.TickManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements dev.dubhe.curtain.features.rules.fakes.TickManagerProvider {
    @Unique private static TickManager curtain_Addon$tickManager;
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        curtain_Addon$tickManager = new TickManager((MinecraftServer) (Object) this);
    }
    @Override
    public TickManager getTickManager() {
        return curtain_Addon$tickManager;
    }
    @Inject(method = "tickChildren", at = @At("HEAD"), cancellable = true)
    private void onTickChildren(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        if (!curtain_Addon$tickManager.shouldTickWorlds()) {
            ci.cancel();
        }
    }
    @Inject(method = "tickChildren", at = @At("RETURN"))
    private void onTickChildrenReturn(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        curtain_Addon$tickManager.onWorldTick();
    }
    @Inject(method = "waitUntilNextTick", at = @At("HEAD"), cancellable = true)
    private void onWaitForNextTick(CallbackInfo ci) {
        if (curtain_Addon$tickManager.shouldSkipSleep()) {
            ci.cancel();
        }
    }
}
