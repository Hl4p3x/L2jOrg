// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.commons.util.Rnd;
import java.util.Iterator;
import org.l2j.gameserver.ai.ControllableMobAI;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import java.util.Set;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public final class MobGroup
{
    private final NpcTemplate _npcTemplate;
    private final int _groupId;
    private final int _maxMobCount;
    private Set<ControllableMob> _mobs;
    
    public MobGroup(final int groupId, final NpcTemplate npcTemplate, final int maxMobCount) {
        this._groupId = groupId;
        this._npcTemplate = npcTemplate;
        this._maxMobCount = maxMobCount;
    }
    
    public int getActiveMobCount() {
        return this.getMobs().size();
    }
    
    public int getGroupId() {
        return this._groupId;
    }
    
    public int getMaxMobCount() {
        return this._maxMobCount;
    }
    
    public Set<ControllableMob> getMobs() {
        if (this._mobs == null) {
            this._mobs = (Set<ControllableMob>)ConcurrentHashMap.newKeySet();
        }
        return this._mobs;
    }
    
    public String getStatus() {
        try {
            final ControllableMobAI mobGroupAI = (ControllableMobAI)this.getMobs().stream().findFirst().get().getAI();
            switch (mobGroupAI.getAlternateAI()) {
                case 2: {
                    return "Idle";
                }
                case 3: {
                    return "Force Attacking";
                }
                case 4: {
                    return "Following";
                }
                case 5: {
                    return "Casting";
                }
                case 6: {
                    return "Attacking Group";
                }
                default: {
                    return "Idle";
                }
            }
        }
        catch (Exception e) {
            return "Unspawned";
        }
    }
    
    public NpcTemplate getTemplate() {
        return this._npcTemplate;
    }
    
    public boolean isGroupMember(final ControllableMob mobInst) {
        for (final ControllableMob groupMember : this.getMobs()) {
            if (groupMember == null) {
                continue;
            }
            if (groupMember.getObjectId() == mobInst.getObjectId()) {
                return true;
            }
        }
        return false;
    }
    
    public void spawnGroup(final int x, final int y, final int z) {
        if (this.getMobs().size() > 0) {
            return;
        }
        try {
            for (int i = 0; i < this._maxMobCount; ++i) {
                final GroupSpawn spawn = new GroupSpawn(this._npcTemplate);
                final int signX = Rnd.nextBoolean() ? -1 : 1;
                final int signY = Rnd.nextBoolean() ? -1 : 1;
                final int randX = Rnd.get(300);
                final int randY = Rnd.get(300);
                spawn.setXYZ(x + signX * randX, y + signY * randY, z);
                spawn.stopRespawn();
                SpawnTable.getInstance().addNewSpawn(spawn, false);
                this.getMobs().add((ControllableMob)spawn.doGroupSpawn());
            }
        }
        catch (ClassNotFoundException ex) {}
        catch (NoSuchMethodException ex2) {}
    }
    
    public void spawnGroup(final Player activeChar) {
        this.spawnGroup(activeChar.getX(), activeChar.getY(), activeChar.getZ());
    }
    
    public void teleportGroup(final Player player) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            if (mobInst.isDead()) {
                continue;
            }
            final int x = player.getX() + Rnd.get(50);
            final int y = player.getY() + Rnd.get(50);
            mobInst.teleToLocation(new Location(x, y, player.getZ()), true);
            ((ControllableMobAI)mobInst.getAI()).follow(player);
        }
    }
    
    public ControllableMob getRandomMob() {
        this.removeDead();
        if (this.getMobs().size() == 0) {
            return null;
        }
        int choice = Rnd.get(this.getMobs().size());
        for (final ControllableMob mob : this.getMobs()) {
            if (--choice == 0) {
                return mob;
            }
        }
        return null;
    }
    
    public void unspawnGroup() {
        this.removeDead();
        if (this.getMobs().size() == 0) {
            return;
        }
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            if (!mobInst.isDead()) {
                mobInst.deleteMe();
            }
            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }
        this.getMobs().clear();
    }
    
    public void killGroup(final Player activeChar) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            if (!mobInst.isDead()) {
                mobInst.reduceCurrentHp(mobInst.getMaxHp() + 1, activeChar, null, DamageInfo.DamageType.OTHER);
            }
            SpawnTable.getInstance().deleteSpawn(mobInst.getSpawn(), false);
        }
        this.getMobs().clear();
    }
    
    public void setAttackRandom() {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            final ControllableMobAI ai = (ControllableMobAI)mobInst.getAI();
            ai.setAlternateAI(2);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
    
    public void setAttackTarget(final Creature target) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            ((ControllableMobAI)mobInst.getAI()).forceAttack(target);
        }
    }
    
    public void setIdleMode() {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            ((ControllableMobAI)mobInst.getAI()).stop();
        }
    }
    
    public void returnGroup(final Creature activeChar) {
        this.setIdleMode();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            final int signX = Rnd.nextBoolean() ? -1 : 1;
            final int signY = Rnd.nextBoolean() ? -1 : 1;
            final int randX = Rnd.get(300);
            final int randY = Rnd.get(300);
            final ControllableMobAI ai = (ControllableMobAI)mobInst.getAI();
            ai.move(activeChar.getX() + signX * randX, activeChar.getY() + signY * randY, activeChar.getZ());
        }
    }
    
    public void setFollowMode(final Creature character) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            ((ControllableMobAI)mobInst.getAI()).follow(character);
        }
    }
    
    public void setCastMode() {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            ((ControllableMobAI)mobInst.getAI()).setAlternateAI(5);
        }
    }
    
    public void setNoMoveMode(final boolean enabled) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            ((ControllableMobAI)mobInst.getAI()).setNotMoving(enabled);
        }
    }
    
    protected void removeDead() {
        this.getMobs().removeIf(Creature::isDead);
    }
    
    public void setInvul(final boolean invulState) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst != null) {
                mobInst.setInvul(invulState);
            }
        }
    }
    
    public void setAttackGroup(final MobGroup otherGrp) {
        this.removeDead();
        for (final ControllableMob mobInst : this.getMobs()) {
            if (mobInst == null) {
                continue;
            }
            final ControllableMobAI ai = (ControllableMobAI)mobInst.getAI();
            ai.forceAttackGroup(otherGrp);
            ai.setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }
}
