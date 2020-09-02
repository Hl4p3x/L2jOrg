// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class PkCount extends AbstractEffect
{
    private final int power;
    
    private PkCount(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effected.getActingPlayer();
        if (Objects.isNull(player)) {
            return;
        }
        if (player.getPkKills() > 0) {
            final int newPkCount = Math.max(player.getPkKills() + this.power, 0);
            player.setPkKills(newPkCount);
            player.sendPacket(new ServerPacket[] { (ServerPacket)new UserInfo(player) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new PkCount(data);
        }
        
        public String effectName() {
            return "PkCount";
        }
    }
}
