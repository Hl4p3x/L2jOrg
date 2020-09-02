// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import java.util.Objects;
import org.l2j.gameserver.engine.skill.api.SkillConditionFactory;
import java.util.HashMap;
import org.l2j.gameserver.engine.skill.api.SkillCondition;
import org.w3c.dom.Node;
import java.util.function.Function;
import java.util.Map;

public final class SkillConditionHandler
{
    private final Map<String, Function<Node, SkillCondition>> skillConditionHandlerFactories;
    
    private SkillConditionHandler() {
        this.skillConditionHandlerFactories = new HashMap<String, Function<Node, SkillCondition>>();
    }
    
    public void registerFactory(final SkillConditionFactory skillConditionFactory) {
        final Map<String, Function<Node, SkillCondition>> skillConditionHandlerFactories = this.skillConditionHandlerFactories;
        final String conditionName = skillConditionFactory.conditionName();
        Objects.requireNonNull(skillConditionFactory);
        skillConditionHandlerFactories.put(conditionName, skillConditionFactory::create);
    }
    
    public Function<Node, SkillCondition> getHandlerFactory(final String name) {
        return this.skillConditionHandlerFactories.get(name);
    }
    
    public int size() {
        return this.skillConditionHandlerFactories.size();
    }
    
    public static SkillConditionHandler getInstance() {
        return Singleton.INSTANCE;
    }
    
    private static final class Singleton
    {
        private static final SkillConditionHandler INSTANCE;
        
        static {
            INSTANCE = new SkillConditionHandler();
        }
    }
}
