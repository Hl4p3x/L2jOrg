// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerUnsummonAgathion;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class UnsummonAgathion extends AbstractEffect
{
    private UnsummonAgathion() {
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effector.getActingPlayer();
        if (Objects.nonNull(player)) {
            final int agathionId = player.getAgathionId();
            if (agathionId > 0) {
                player.setAgathionId(0);
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExUserInfoCubic(player) });
                player.broadcastCharInfo();
                EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnPlayerUnsummonAgathion(player, agathionId), new ListenersContainer[0]);
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final UnsummonAgathion INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "UnsummonAgathion";
        }
        
        static {
            INSTANCE = new UnsummonAgathion();
        }
    }
}
