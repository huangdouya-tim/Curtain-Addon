package dev.dubhe.curtain.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import dev.dubhe.curtain.features.rules.fakes.TickManagerProvider;
import dev.dubhe.curtain.utils.TickManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import org.jetbrains.annotations.NotNull;

public class TickCommand {
    public static void register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("tick")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("rate")
                        .then(Commands.argument("tps", DoubleArgumentType.doubleArg(1, 500))
                                .executes(ctx -> setRate(ctx.getSource(), DoubleArgumentType.getDouble(ctx, "tps")))))
                .then(Commands.literal("warp")
                        .then(Commands.argument("ticks", IntegerArgumentType.integer(1))
                                .executes(ctx -> warp(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "ticks")))))
                .then(Commands.literal("freeze")
                        .executes(ctx -> freeze(ctx.getSource())))
                .then(Commands.literal("step")
                        .executes(ctx -> step(ctx.getSource(), 1))
                        .then(Commands.argument("ticks", IntegerArgumentType.integer(1))
                                .executes(ctx -> step(ctx.getSource(), IntegerArgumentType.getInteger(ctx, "ticks")))))
                .then(Commands.literal("query")
                        .executes(ctx -> query(ctx.getSource()))));
    }

    private static int setRate(CommandSourceStack source, double tps) {
        TickManager manager = getTickManager(source.getServer());
        manager.setTargetTps(tps);
        source.sendSuccess(new TextComponent("tick rate set to" + tps + "TPS"), true);
        return 1;
    }

    private static int step(CommandSourceStack source, int ticks) {
        TickManager manager = getTickManager(source.getServer());
        if (!manager.isFrozen()) {
            manager.setFrozen(true);
        }
        manager.setStepRemaining(ticks);
        source.sendSuccess(new TextComponent("Stepping" + ticks + "tick(s)"), true);
        return 1;
    }

    private static int warp(CommandSourceStack source, int ticks) {
        TickManager manager = getTickManager(source.getServer());
        manager.setWarpRemaining(ticks);
        source.sendSuccess(new TextComponent("Warping" + ticks + "ticks"), true);
        return 1;
    }

    private static int freeze(CommandSourceStack source) {
        TickManager manager = getTickManager(source.getServer());
        if (manager.isFrozen()) {
            source.sendSuccess(new TextComponent("Tick freezing"), true);
            manager.setFrozen(false);
        } else {
            source.sendSuccess(new TextComponent("Tick runs normally"), true);
            manager.setFrozen(true);
        }
        return 1;
    }
    private static int query(CommandSourceStack source) {
        MinecraftServer server = source.getServer();
        TickManager manager = getTickManager(server);
        double targetTps = manager.getTargetTps();
        Boolean frozen = manager.isFrozen();
        int warp = manager.getWarpRemaining();
        int step = manager.getStepRemaining();
        double actualTps = 1000.0/Math.max(server.getAverageTickTime(), 1);
        String msg = String.format("Target TPS: %.1f, Actual TPS: %.2f, Frozen: %s, warp: %d, Step: %d",
                targetTps, actualTps, frozen, warp, step);
        source.sendSuccess(new TextComponent(msg), false);
        return 1;
    }
    private static TickManager getTickManager(MinecraftServer server) {
        return ((TickManagerProvider) server).getTickManager();
    }
}
