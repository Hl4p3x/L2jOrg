// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.conditions;

import org.l2j.gameserver.engine.mission.MissionDataHolder;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;

public abstract class Condition
{
    private String _msg;
    private int _msgId;
    private boolean _addName;
    
    public Condition() {
        this._addName = false;
    }
    
    public final String getMessage() {
        return this._msg;
    }
    
    public final void setMessage(final String msg) {
        this._msg = msg;
    }
    
    public final int getMessageId() {
        return this._msgId;
    }
    
    public final void setMessageId(final int msgId) {
        this._msgId = msgId;
    }
    
    public final void addName() {
        this._addName = true;
    }
    
    public final boolean isAddName() {
        return this._addName;
    }
    
    public final boolean test(final Creature caster, final Creature target, final Skill skill) {
        return this.test(caster, target, skill, null);
    }
    
    public final boolean test(final Creature caster, final Creature target, final ItemTemplate item) {
        return this.test(caster, target, null, null);
    }
    
    public final boolean test(final Creature caster, final MissionDataHolder onewayreward) {
        return this.test(caster, null, null, null);
    }
    
    public final boolean test(final Creature caster, final Creature target, final Skill skill, final ItemTemplate item) {
        return this.testImpl(caster, target, skill, item);
    }
    
    public abstract boolean testImpl(final Creature effector, final Creature effected, final Skill skill, final ItemTemplate item);
}
