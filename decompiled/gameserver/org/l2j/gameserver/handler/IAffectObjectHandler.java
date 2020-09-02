// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.actor.Creature;

public interface IAffectObjectHandler
{
    boolean checkAffectedObject(final Creature activeChar, final Creature target);
    
    Enum<AffectObject> getAffectObjectType();
}
