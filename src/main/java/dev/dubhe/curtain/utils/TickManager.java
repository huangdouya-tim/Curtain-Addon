package dev.dubhe.curtain.utils;

import dev.dubhe.curtain.mixins.TickRateAccessor;
import net.minecraft.server.MinecraftServer;

public class TickManager {
    private final MinecraftServer server;
    private double targetTps = 20.0;
    private double oldtargetTps = 20.0;
    private boolean frozen = false;
    private int warpRemaining = 0;
    private int stepRemaining = 0;
    private boolean wasfrozen = false;
    private boolean warped = false;
    private boolean stepped = false;
    public TickManager (MinecraftServer server) {
        this.server = server;
    }
    public void setTargetTps(double tps) {
        if (tps < 1.0) tps = 1.0;
        if (tps > 500.0) tps = 500.0;
        this.targetTps = tps;
        int newTickRate = (int)(1000.0/tps);
        ((TickRateAccessor) server).setMS_PER_TICK(newTickRate);
    }
    public double getTargetTps() {return targetTps;}
    public boolean isFrozen() {return frozen;}
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
        if (frozen) {
            warpRemaining = 0;
            stepRemaining = 0;
        }
    }
    public int getWarpRemaining() {return warpRemaining;}
    public void setWarpRemaining(int ticks) {
        if (ticks > 0) {
            if (this.frozen = true) this.wasfrozen = true;
            this.warpRemaining = ticks;
            this.warped = true;
            this.oldtargetTps = this.targetTps;
            this.targetTps = 500;
            this.frozen = false;
        }
    }
    public int getStepRemaining() {return stepRemaining;}
    public void setStepRemaining(int ticks) {
        if (ticks > 0 && this.frozen) {
            this.stepRemaining = ticks;
            this.stepped = true;
            this.oldtargetTps = this.targetTps;
            this.targetTps = 20;
            this.frozen = false;
        }
    }
    public boolean shouldTickWorlds() {
        if (warpRemaining > 0) return true;
        if (frozen) return stepRemaining > 0;
        return true;
    }
    public boolean shouldSkipSleep() {return warpRemaining > 0;}
    public void onWorldTick() {
        if (warpRemaining > 0) warpRemaining--;
        if (stepRemaining > 0) stepRemaining--;
        if (warpRemaining == 0 && warped) {
            if (this.wasfrozen) {
                this.frozen = true;
                this.wasfrozen = false;
            }
            this.targetTps = this.oldtargetTps;
            this.warped = false;
        }
        if (stepRemaining == 0 && stepped) {
            this.frozen = true;
            this.targetTps = this.oldtargetTps;
            this.stepped = false;
        }
    }
}
