// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class GroupSpawn extends Spawn
{
    private final NpcTemplate _template;
    
    public GroupSpawn(final NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
        super(mobTemplate);
        this._template = mobTemplate;
        this.setAmount(1);
    }
    
    public Npc doGroupSpawn() {
        try {
            if (this._template.isType("Pet") || this._template.isType("Minion")) {
                return null;
            }
            int newlocx = 0;
            int newlocy = 0;
            int newlocz = 0;
            if (this.getX() != 0 || this.getY() != 0) {
                newlocx = this.getX();
                newlocy = this.getY();
                newlocz = this.getZ();
                final Npc mob = new ControllableMob(this._template);
                mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());
                mob.setHeading((this.getHeading() == -1) ? Rnd.get(61794) : this.getHeading());
                mob.setSpawn(this);
                mob.spawnMe(newlocx, newlocy, newlocz);
                return mob;
            }
            if (this.getLocationId() == 0) {
                return null;
            }
            return null;
        }
        catch (Exception e) {
            GroupSpawn.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            return null;
        }
    }
}
