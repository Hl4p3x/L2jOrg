// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ReuseSkillById extends AbstractEffect
{
    private final int skillId;
    
    private ReuseSkillById(final StatsSet params) {
        this.skillId = params.getInt("id", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effector.getActingPlayer();
        if (Objects.nonNull(player)) {
            final Skill s = player.getKnownSkill(this.skillId);
            if (Objects.nonNull(s)) {
                player.removeTimeStamp(s);
                player.enableSkill(s);
                player.sendPacket(new ServerPacket[] { (ServerPacket)new SkillCoolTime(player) });
            }
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new ReuseSkillById(data);
        }
        
        public String effectName() {
            return "reuse-skill";
        }
    }
}
