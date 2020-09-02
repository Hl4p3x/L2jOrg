// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import java.util.HashMap;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.StatsSet;
import java.util.function.Function;
import java.util.Map;

public final class EffectHandler
{
    private final Map<String, Function<StatsSet, AbstractEffect>> factories;
    
    private EffectHandler() {
        this.factories = new HashMap<String, Function<StatsSet, AbstractEffect>>();
    }
    
    public void registerFactory(final SkillEffectFactory factory) {
        final Map<String, Function<StatsSet, AbstractEffect>> factories = this.factories;
        final String effectName = factory.effectName();
        Objects.requireNonNull(factory);
        factories.put(effectName, factory::create);
    }
    
    public Function<StatsSet, AbstractEffect> getHandlerFactory(final String name) {
        return this.factories.get(name);
    }
    
    public int size() {
        return this.factories.size();
    }
    
    public static EffectHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static final class Singleton
    {
        protected static final EffectHandler INSTANCE;
        
        static {
            INSTANCE = new EffectHandler();
        }
    }
}
