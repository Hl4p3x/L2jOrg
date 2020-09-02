// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.Objects;
import org.l2j.gameserver.model.conditions.ConditionFactory;
import java.util.HashMap;
import org.l2j.gameserver.model.conditions.ICondition;
import org.l2j.gameserver.model.StatsSet;
import java.util.function.Function;
import java.util.Map;

public final class ConditionHandler
{
    private final Map<String, Function<StatsSet, ICondition>> factories;
    
    private ConditionHandler() {
        this.factories = new HashMap<String, Function<StatsSet, ICondition>>();
    }
    
    public void registerFactory(final ConditionFactory handler) {
        final Map<String, Function<StatsSet, ICondition>> factories = this.factories;
        final String conditionName = handler.conditionName();
        Objects.requireNonNull(handler);
        factories.put(conditionName, handler::create);
    }
    
    public Function<StatsSet, ICondition> getHandlerFactory(final String name) {
        return this.factories.get(name);
    }
    
    public int size() {
        return this.factories.size();
    }
    
    public static ConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static final class Singleton
    {
        private static final ConditionHandler INSTANCE;
        
        static {
            INSTANCE = new ConditionHandler();
        }
    }
}
