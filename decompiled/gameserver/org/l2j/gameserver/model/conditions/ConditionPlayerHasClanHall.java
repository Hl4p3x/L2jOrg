// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import java.util.ArrayList;

public final class ConditionPlayerHasClanHall extends Condition
{
    private final ArrayList<Integer> _clanHall;
    
    public ConditionPlayerHasClanHall(final ArrayList<Integer> clanHall) {
        this._clanHall = clanHall;
    }
    
    @Override
    public boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item) {
        if (effector.getActingPlayer() == null) {
            return false;
        }
        final Clan clan = effector.getActingPlayer().getClan();
        if (clan == null) {
            return this._clanHall.size() == 1 && this._clanHall.get(0) == 0;
        }
        if (this._clanHall.size() == 1 && this._clanHall.get(0) == -1) {
            return clan.getHideoutId() > 0;
        }
        return this._clanHall.contains(clan.getHideoutId());
    }
}
