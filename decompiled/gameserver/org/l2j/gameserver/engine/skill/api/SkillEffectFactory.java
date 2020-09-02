// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.engine.skill.api;

import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.StatsSet;

public interface SkillEffectFactory
{
    AbstractEffect create(final StatsSet data);
    
    String effectName();
}
