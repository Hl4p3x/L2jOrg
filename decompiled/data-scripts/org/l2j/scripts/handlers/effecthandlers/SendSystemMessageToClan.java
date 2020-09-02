// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class SendSystemMessageToClan extends AbstractEffect
{
    private final SystemMessage message;
    
    private SendSystemMessageToClan(final StatsSet params) {
        final int id = params.getInt("id", 0);
        this.message = SystemMessage.getSystemMessage(id);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effected.getActingPlayer();
        if (Objects.isNull(player) || this.message == null) {
            return;
        }
        final Clan clan = player.getClan();
        if (Objects.nonNull(clan)) {
            clan.broadcastToOnlineMembers((ServerPacket)this.message);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SendSystemMessageToClan(data);
        }
        
        public String effectName() {
            return "clan-system-message";
        }
    }
}
