// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.Objects;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class HeadquarterCreate extends AbstractEffect
{
    private static final int HQ_NPC_ID = 35062;
    private final boolean isAdvanced;
    
    private HeadquarterCreate(final StatsSet params) {
        this.isAdvanced = params.getBoolean("advanced", false);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        final Player player = effector.getActingPlayer();
        if (Objects.isNull(player.getClan()) || player.getClan().getLeaderId() != player.getObjectId()) {
            return;
        }
        final SiegeFlag flag = new SiegeFlag(player, NpcData.getInstance().getTemplate(35062), this.isAdvanced);
        flag.setTitle(player.getClan().getName());
        flag.setCurrentHpMp((double)flag.getMaxHp(), (double)flag.getMaxMp());
        flag.setHeading(player.getHeading());
        flag.spawnMe(player.getX(), player.getY(), player.getZ() + 50);
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new HeadquarterCreate(data);
        }
        
        public String effectName() {
            return "headquarter";
        }
    }
}
