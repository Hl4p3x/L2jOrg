// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.effects;

import org.l2j.gameserver.model.skills.BuffInfo;

public class EffectTickTask implements Runnable
{
    private final BuffInfo _info;
    private final AbstractEffect _effect;
    
    public EffectTickTask(final BuffInfo info, final AbstractEffect effect) {
        this._info = info;
        this._effect = effect;
    }
    
    public BuffInfo getBuffInfo() {
        return this._info;
    }
    
    public AbstractEffect getEffect() {
        return this._effect;
    }
    
    @Override
    public void run() {
        this._info.onTick(this._effect);
    }
}
