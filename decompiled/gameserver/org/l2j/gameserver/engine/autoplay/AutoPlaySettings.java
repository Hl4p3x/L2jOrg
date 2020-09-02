// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.autoplay;

import java.util.concurrent.atomic.AtomicBoolean;

public class AutoPlaySettings
{
    private short size;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectFulHunt;
    private final AtomicBoolean autoPlaying;
    private int usableHpPetPotionPercent;
    
    public AutoPlaySettings(final short size, final boolean active, final boolean pickUp, final short nextTargetMode, final boolean isNearTarget, final int hpPotionPercent, final int usableHpPotionPercent, final boolean respectFulHunt) {
        this.autoPlaying = new AtomicBoolean(false);
        this.size = size;
        this.active = active;
        this.pickUp = pickUp;
        this.nextTargetMode = nextTargetMode;
        this.isNearTarget = isNearTarget;
        this.usableHpPotionPercent = usableHpPotionPercent;
        this.respectFulHunt = respectFulHunt;
    }
    
    public short getSize() {
        return this.size;
    }
    
    public void setSize(final short options) {
        this.size = options;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public boolean isAutoPickUpOn() {
        return this.pickUp;
    }
    
    public void setAutoPickUpOn(final boolean pickUp) {
        this.pickUp = pickUp;
    }
    
    public short getNextTargetMode() {
        return this.nextTargetMode;
    }
    
    public void setNextTargetMode(final short nextTargetMode) {
        this.nextTargetMode = nextTargetMode;
    }
    
    public boolean isNearTarget() {
        return this.isNearTarget;
    }
    
    public void setNearTarget(final boolean isNearTarget) {
        this.isNearTarget = isNearTarget;
    }
    
    public int getUsableHpPotionPercent() {
        return this.usableHpPotionPercent;
    }
    
    public void setUsableHpPotionPercent(final int usableHpPotionPercent) {
        this.usableHpPotionPercent = usableHpPotionPercent;
    }
    
    public boolean isRespectfulMode() {
        return this.respectFulHunt;
    }
    
    public void setRespectfulHunt(final boolean respectfulHunt) {
        this.respectFulHunt = respectfulHunt;
    }
    
    public boolean isAutoPlaying() {
        return this.autoPlaying.get();
    }
    
    public void setAutoPlaying(final boolean autoPlaying) {
        this.autoPlaying.set(autoPlaying);
    }
    
    public void setUsableHpPetPotionPercent(final int usableHpPetPotionPercent) {
        this.usableHpPetPotionPercent = usableHpPetPotionPercent;
    }
    
    public int getUsableHpPetPotionPercent() {
        return this.usableHpPetPotionPercent;
    }
}
