// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.function.Consumer;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class TriggerSkillByChargeShot extends AbstractEffect
{
    private final SkillHolder skill;
    private final ShotType type;
    private final boolean forBeast;
    private final boolean blessed;
    
    private TriggerSkillByChargeShot(final StatsSet data) {
        this.skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
        this.type = (ShotType)data.getEnum("type", (Class)ShotType.class);
        this.forBeast = data.getBoolean("for-beast");
        this.blessed = data.getBoolean("blessed");
    }
    
    private void onChargeShotEvent(final OnPlayeableChargeShots event) {
        if (event.getShotType() != this.type || this.forBeast != GameUtils.isSummon((WorldObject)event.getPlayable()) || this.blessed != event.isBlessed()) {
            return;
        }
        final Skill triggerSkill = this.skill.getSkill();
        final Playable playable = event.getPlayable();
        final WorldObject target = triggerSkill.getTarget((Creature)playable, false, false, false);
        SkillCaster.triggerCast((Creature)playable, (Creature)target, triggerSkill);
    }
    
    public void onExit(final Creature effector, final Creature effected, final Skill skill) {
        effected.removeListenerIf(EventType.ON_PLAYER_CHARGE_SHOTS, listener -> listener.getOwner() == this);
    }
    
    public void onStart(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        effected.addListener((AbstractEventListener)new ConsumerEventListener((ListenersContainer)effected, EventType.ON_PLAYER_CHARGE_SHOTS, (Consumer)this::onChargeShotEvent, (Object)this));
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new TriggerSkillByChargeShot(data);
        }
        
        public String effectName() {
            return "trigger-skill-by-charge-shot";
        }
    }
}
