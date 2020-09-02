// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.conditions;

import org.l2j.gameserver.model.conditions.ConditionFactory;
import java.util.stream.Stream;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.CategoryType;
import java.util.List;
import org.l2j.gameserver.model.conditions.ICondition;

public class CategoryTypeCondition implements ICondition
{
    private final List<CategoryType> _categoryTypes;
    
    private CategoryTypeCondition(final StatsSet params) {
        this._categoryTypes = (List<CategoryType>)params.getEnumList("category", (Class)CategoryType.class);
    }
    
    public boolean test(final Creature creature, final WorldObject target) {
        final Stream<Object> stream = this._categoryTypes.stream();
        Objects.requireNonNull(creature);
        return stream.anyMatch((Predicate<? super Object>)creature::isInCategory);
    }
    
    public static class Factory implements ConditionFactory
    {
        public ICondition create(final StatsSet data) {
            return (ICondition)new CategoryTypeCondition(data);
        }
        
        public String conditionName() {
            return "CategoryType";
        }
    }
}
