// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class FocusMomentum extends AbstractEffect
{
    private final int maxCharges;
    
    private FocusMomentum(final StatsSet params) {
        this.maxCharges = params.getInt("power", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effected)) {
            return;
        }
        final Player player = effected.getActingPlayer();
        final int currentCharges = player.getCharges();
        final int maxCharges = Math.min(this.maxCharges, (int)effected.getStats().getValue(Stat.MAX_MOMENTUM, 1.0));
        if (currentCharges >= maxCharges) {
            if (!skill.isTriggeredSkill()) {
                player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
            }
            return;
        }
        final int newCharge = Math.min(currentCharges + 1, maxCharges);
        player.setCharges(newCharge);
        if (newCharge == maxCharges) {
            player.sendPacket(SystemMessageId.YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY);
        }
        else {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1).addInt(newCharge) });
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new EtcStatusUpdate(player) });
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new FocusMomentum(data);
        }
        
        public String effectName() {
            return "FocusMomentum";
        }
    }
}
