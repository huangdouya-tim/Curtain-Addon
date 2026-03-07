package dev.dubhe.curtain.mixins;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MinecraftServer.class)
public interface TickRateAccessor {
    @Accessor("MS_PER_TICK")
    @Mutable
    void setMS_PER_TICK(int MS_PER_TICK);

}
