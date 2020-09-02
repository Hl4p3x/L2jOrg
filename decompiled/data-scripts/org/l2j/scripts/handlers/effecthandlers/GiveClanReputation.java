// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class GiveClanReputation extends AbstractEffect
{
    private final int power;
    
    private GiveClanReputation(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Clan clan;
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead() || Objects.isNull(clan = effector.getClan())) {
            return;
        }
        clan.addReputationScore(this.power, true);
        clan.broadcastToOnlineMembers((ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION).addInt(this.power));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new GiveClanReputation(data);
        }
        
        public String effectName() {
            return "GiveClanReputation";
        }
    }
}
