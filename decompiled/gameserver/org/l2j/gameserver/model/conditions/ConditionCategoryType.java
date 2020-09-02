// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import java.util.Iterator;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.CategoryType;
import java.util.Set;

public class ConditionCategoryType extends Condition
{
    private final Set<CategoryType> _categoryTypes;
    
    public ConditionCategoryType(final Set<CategoryType> categoryTypes) {
        this._categoryTypes = categoryTypes;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        for (final CategoryType type : this._categoryTypes) {
            if (effector.isInCategory(type)) {
                return true;
            }
        }
        return false;
    }
}
