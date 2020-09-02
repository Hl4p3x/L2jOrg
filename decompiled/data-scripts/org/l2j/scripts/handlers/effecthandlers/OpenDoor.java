// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class OpenDoor extends AbstractEffect
{
    private final int power;
    
    private OpenDoor(final StatsSet params) {
        this.power = params.getInt("power", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isDoor((WorldObject)effected) || effector.getInstanceWorld() != effected.getInstanceWorld()) {
            return;
        }
        final Door door = (Door)effected;
        if (!door.isOpenableBySkill()) {
            effector.sendPacket(SystemMessageId.THIS_DOOR_CANNOT_BE_UNLOCKED);
            return;
        }
        if (!door.isOpen() && Rnd.chance(this.power)) {
            door.openMe();
        }
        else {
            effector.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
        }
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new OpenDoor(data);
        }
        
        public String effectName() {
            return "OpenDoor";
        }
    }
}
