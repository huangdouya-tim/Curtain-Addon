package dev.dubhe.curtain.features.rules.fakes;

import dev.dubhe.curtain.utils.TickManager;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

public interface TickManagerProvider {
    TickManager getTickManager();
}
