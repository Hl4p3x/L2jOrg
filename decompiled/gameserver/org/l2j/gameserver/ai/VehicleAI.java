// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Vehicle;

public abstract class VehicleAI extends CreatureAI
{
    public VehicleAI(final Vehicle vehicle) {
        super(vehicle);
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
    }
    
    @Override
    protected void onIntentionFollow(final Creature target) {
    }
    
    @Override
    protected void onIntentionPickUp(final WorldObject item) {
    }
    
    @Override
    protected void onIntentionInteract(final WorldObject object) {
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
    }
    
    @Override
    protected void onEvtAggression(final Creature target, final int aggro) {
    }
    
    @Override
    protected void onEvtActionBlocked(final Creature attacker) {
    }
    
    @Override
    protected void onEvtRooted(final Creature attacker) {
    }
    
    @Override
    protected void onEvtForgetObject(final WorldObject object) {
    }
    
    @Override
    protected void onEvtCancel() {
    }
    
    @Override
    protected void onEvtDead() {
    }
    
    @Override
    protected void onEvtFakeDeath() {
    }
    
    @Override
    protected void onEvtFinishCasting() {
    }
    
    @Override
    protected void clientActionFailed() {
    }
    
    @Override
    public void moveToPawn(final WorldObject pawn, final int offset) {
    }
    
    @Override
    protected void clientStoppedMoving() {
    }
}
