// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonAgathion;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SummonAgathion extends AbstractEffect
{
    private final int npcId;
    
    private SummonAgathion(final StatsSet params) {
        if (params.isEmpty()) {
            SummonAgathion.LOGGER.warn("must have parameters.");
        }
        this.npcId = params.getInt("id", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            return;
        }
        final Player player = effected.getActingPlayer();
        player.setAgathionId(this.npcId);
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoCubic(player) });
        player.broadcastCharInfo();
        EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnPlayerSummonAgathion(player, this.npcId), new ListenersContainer[0]);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new SummonAgathion(data);
        }
        
        public String effectName() {
            return "summon-agathion";
        }
    }
}
