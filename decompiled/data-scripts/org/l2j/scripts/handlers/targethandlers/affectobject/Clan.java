// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.targethandlers.affectobject;

import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.handler.IAffectObjectHandler;

public class Clan implements IAffectObjectHandler
{
    public boolean checkAffectedObject(final Creature activeChar, final Creature target) {
        if (activeChar == target) {
            return true;
        }
        final Player player = activeChar.getActingPlayer();
        if (player != null) {
            final org.l2j.gameserver.model.Clan clan = player.getClan();
            if (clan != null) {
                return clan == target.getClan();
            }
        }
        else if (GameUtils.isNpc((WorldObject)activeChar) && GameUtils.isNpc((WorldObject)target)) {
            return ((Npc)activeChar).isInMyClan((Npc)target);
        }
        return false;
    }
    
    public Enum<AffectObject> getAffectObjectType() {
        return (Enum<AffectObject>)AffectObject.CLAN;
    }
}
