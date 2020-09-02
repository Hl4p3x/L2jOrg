// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.actor.Attackable;

public class FriendlyMob extends Attackable
{
    public FriendlyMob(final NpcTemplate template) {
        super(template);
        this.setInstanceType(InstanceType.L2FriendlyMobInstance);
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (GameUtils.isPlayer(attacker)) {
            return attacker.getReputation() < 0;
        }
        return super.isAutoAttackable(attacker);
    }
    
    @Override
    public boolean isAggressive() {
        return true;
    }
}
