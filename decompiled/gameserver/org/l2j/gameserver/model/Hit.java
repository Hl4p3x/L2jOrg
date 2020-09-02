// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.AttackType;
import java.lang.ref.WeakReference;

public class Hit
{
    private final WeakReference<WorldObject> target;
    private final int targetId;
    private final int damage;
    private final int grade;
    private int flags;
    
    public Hit(final WorldObject target, final int damage, final boolean miss, final boolean crit, final byte shld, final int soulshotGrade) {
        this.flags = 0;
        this.target = new WeakReference<WorldObject>(target);
        this.targetId = target.getObjectId();
        this.damage = damage;
        this.grade = soulshotGrade;
        if (miss) {
            this.addMask(AttackType.MISSED);
            return;
        }
        if (crit) {
            this.addMask(AttackType.CRITICAL);
        }
        if (soulshotGrade >= 0) {
            this.addMask(AttackType.SHOT_USED);
        }
        if ((GameUtils.isCreature(target) && ((Creature)target).isHpBlocked()) || shld > 0) {
            this.addMask(AttackType.BLOCKED);
        }
    }
    
    private void addMask(final AttackType type) {
        this.flags |= type.getMask();
    }
    
    public WorldObject getTarget() {
        return this.target.get();
    }
    
    public int getTargetId() {
        return this.targetId;
    }
    
    public int getDamage() {
        return this.damage;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    public int getGrade() {
        return this.isMiss() ? -1 : this.grade;
    }
    
    public boolean isMiss() {
        return (AttackType.MISSED.getMask() & this.flags) != 0x0;
    }
    
    public boolean isCritical() {
        return (AttackType.CRITICAL.getMask() & this.flags) != 0x0;
    }
    
    public boolean isShotUsed() {
        return (AttackType.SHOT_USED.getMask() & this.flags) != 0x0;
    }
    
    public boolean isBlocked() {
        return (AttackType.BLOCKED.getMask() & this.flags) != 0x0;
    }
}
