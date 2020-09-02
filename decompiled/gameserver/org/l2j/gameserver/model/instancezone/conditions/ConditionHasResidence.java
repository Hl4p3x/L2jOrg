// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.instancezone.conditions;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.enums.ResidenceType;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;

public final class ConditionHasResidence extends Condition
{
    public ConditionHasResidence(final InstanceTemplate template, final StatsSet parameters, final boolean onlyLeader, final boolean showMessageAndHtml) {
        super(template, parameters, onlyLeader, showMessageAndHtml);
    }
    
    @Override
    protected boolean test(final Player player, final Npc npc) {
        final Clan clan = player.getClan();
        if (clan == null) {
            return false;
        }
        final StatsSet params = this.getParameters();
        final int id = params.getInt("id");
        boolean test = false;
        switch (params.getEnum("type", ResidenceType.class)) {
            case CASTLE: {
                test = (clan.getCastleId() == id);
                break;
            }
            case CLANHALL: {
                test = (clan.getHideoutId() == id);
                break;
            }
        }
        return test;
    }
}
