// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import java.util.Collection;
import java.util.Map;

public class SummonEffectsTable
{
    private final Map<Integer, Map<Integer, Map<Integer, Collection<SummonEffect>>>> _servitorEffects;
    private final Map<Integer, Collection<SummonEffect>> _petEffects;
    
    private SummonEffectsTable() {
        this._servitorEffects = new HashMap<Integer, Map<Integer, Map<Integer, Collection<SummonEffect>>>>();
        this._petEffects = new HashMap<Integer, Collection<SummonEffect>>();
    }
    
    public Map<Integer, Map<Integer, Map<Integer, Collection<SummonEffect>>>> getServitorEffectsOwner() {
        return this._servitorEffects;
    }
    
    public Map<Integer, Collection<SummonEffect>> getServitorEffects(final Player owner) {
        final Map<Integer, Map<Integer, Collection<SummonEffect>>> servitorMap = this._servitorEffects.get(owner.getObjectId());
        if (servitorMap == null) {
            return null;
        }
        return servitorMap.get(owner.getClassIndex());
    }
    
    public Map<Integer, Collection<SummonEffect>> getPetEffects() {
        return this._petEffects;
    }
    
    public static SummonEffectsTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    public static class SummonEffect
    {
        private final Skill _skill;
        private final int _effectCurTime;
        
        public SummonEffect(final Skill skill, final int effectCurTime) {
            this._skill = skill;
            this._effectCurTime = effectCurTime;
        }
        
        public Skill getSkill() {
            return this._skill;
        }
        
        public int getEffectCurTime() {
            return this._effectCurTime;
        }
    }
    
    private static class Singleton
    {
        private static final SummonEffectsTable INSTANCE;
        
        static {
            INSTANCE = new SummonEffectsTable();
        }
    }
}
