// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public final class ConditionPlayerHasCastle extends Condition
{
    public final int _castle;
    
    public ConditionPlayerHasCastle(final int castle) {
        this._castle = castle;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        final Clan clan = effector.getActingPlayer().getClan();
        if (clan == null) {
            return this._castle == 0;
        }
        if (this._castle == -1) {
            return clan.getCastleId() > 0;
        }
        return clan.getCastleId() == this._castle;
    }
}
