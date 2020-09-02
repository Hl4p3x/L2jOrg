// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.ai;

import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.model.actor.instance.Defender;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.Location;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;

public class DoorAI extends CreatureAI
{
    public DoorAI(final Door door) {
        super(door);
    }
    
    @Override
    protected void onIntentionIdle() {
    }
    
    @Override
    protected void onIntentionActive() {
    }
    
    @Override
    protected void onIntentionRest() {
    }
    
    @Override
    protected void onIntentionAttack(final Creature target) {
    }
    
    @Override
    protected void onIntentionCast(final Skill skill, final WorldObject target, final Item item, final boolean forceUse, final boolean dontMove) {
    }
    
    @Override
    protected void onIntentionMoveTo(final ILocational destination) {
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
    public void onEvtThink() {
    }
    
    @Override
    protected void onEvtAttacked(final Creature attacker) {
        ThreadPool.execute((Runnable)new onEventAttackedDoorTask((Door)this.actor, attacker));
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
    protected void onEvtReadyToAct() {
    }
    
    @Override
    protected void onEvtArrived() {
    }
    
    @Override
    protected void onEvtArrivedRevalidate() {
    }
    
    @Override
    protected void onEvtArrivedBlocked(final Location blocked_at_loc) {
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
    
    private class onEventAttackedDoorTask implements Runnable
    {
        private final Door _door;
        private final Creature _attacker;
        
        public onEventAttackedDoorTask(final Door door, final Creature attacker) {
            this._door = door;
            this._attacker = attacker;
        }
        
        @Override
        public void run() {
            World.getInstance().forEachVisibleObject(this._door, Defender.class, guard -> {
                if (MathUtil.isInsideRadius3D(DoorAI.this.actor, guard, guard.getTemplate().getClanHelpRange())) {
                    guard.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, this._attacker, 15);
                }
            });
        }
    }
}
