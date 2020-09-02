// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class GiveExpAndSp extends AbstractEffect
{
    private final int xp;
    private final int sp;
    
    private GiveExpAndSp(final StatsSet params) {
        this.xp = params.getInt("xp", 0);
        this.sp = params.getInt("sp", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        if (this.sp != 0 && this.xp != 0) {
            effector.getActingPlayer().getStats().addExp((long)this.xp);
            effector.getActingPlayer().getStats().addSp((long)this.sp);
            effector.sendPacket(new ServerPacket[] { (ServerPacket)((SystemMessage)((SystemMessage)((SystemMessage)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong((long)this.xp)).addLong(0L)).addLong((long)this.sp)).addLong(0L) });
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new GiveExpAndSp(data);
        }
        
        public String effectName() {
            return "acquire-xp-sp";
        }
    }
}
