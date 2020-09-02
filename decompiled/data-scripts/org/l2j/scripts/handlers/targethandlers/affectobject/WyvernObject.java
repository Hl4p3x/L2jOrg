// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectobject;

import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectObjectHandler;

public class WyvernObject implements IAffectObjectHandler
{
    public boolean checkAffectedObject(final Creature activeChar, final Creature target) {
        return CategoryManager.getInstance().isInCategory(CategoryType.WYVERN_GROUP, target.getId());
    }
    
    public Enum<AffectObject> getAffectObjectType() {
        return (Enum<AffectObject>)AffectObject.WYVERN_OBJECT;
    }
}
