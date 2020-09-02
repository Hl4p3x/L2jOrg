// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class EventMonster extends Monster
{
    public boolean block_skill_attack;
    public boolean drop_on_ground;
    
    public EventMonster(final NpcTemplate template) {
        super(template);
        this.block_skill_attack = false;
        this.drop_on_ground = false;
        this.setInstanceType(InstanceType.L2EventMobInstance);
    }
    
    public void eventSetBlockOffensiveSkills(final boolean value) {
        this.block_skill_attack = value;
    }
    
    public void eventSetDropOnGround(final boolean value) {
        this.drop_on_ground = value;
    }
    
    public boolean eventDropOnGround() {
        return this.drop_on_ground;
    }
    
    public boolean eventSkillAttackBlocked() {
        return this.block_skill_attack;
    }
}
