// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Collection;
import java.util.concurrent.Future;

public final class TamedBeast extends FeedableBeast
{
    private static final int MAX_DISTANCE_FROM_HOME = 30000;
    private static final int MAX_DISTANCE_FROM_OWNER = 2000;
    private static final int MAX_DURATION = 1200000;
    private static final int DURATION_CHECK_INTERVAL = 60000;
    private static final int DURATION_INCREASE_INTERVAL = 20000;
    private static final int BUFF_INTERVAL = 5000;
    protected Player _owner;
    protected boolean _isFreyaBeast;
    private int _foodSkillId;
    private int _remainingTime;
    private int _homeX;
    private int _homeY;
    private int _homeZ;
    private Future<?> _buffTask;
    private Future<?> _durationCheckTask;
    private Collection<Skill> _beastSkills;
    
    public TamedBeast(final int npcTemplateId) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        this._remainingTime = 1200000;
        this._buffTask = null;
        this._durationCheckTask = null;
        this._beastSkills = null;
        this.setInstanceType(InstanceType.L2TamedBeastInstance);
        this.setHome(this);
    }
    
    public TamedBeast(final int npcTemplateId, final Player owner, final int foodSkillId, final int x, final int y, final int z) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        this._remainingTime = 1200000;
        this._buffTask = null;
        this._durationCheckTask = null;
        this._beastSkills = null;
        this._isFreyaBeast = false;
        this.setInstanceType(InstanceType.L2TamedBeastInstance);
        this.setCurrentHp(this.getMaxHp());
        this.setCurrentMp(this.getMaxMp());
        this.setOwner(owner);
        this.setFoodType(foodSkillId);
        this.setHome(x, y, z);
        this.spawnMe(x, y, z);
    }
    
    public TamedBeast(final int npcTemplateId, final Player owner, final int food, final int x, final int y, final int z, final boolean isFreyaBeast) {
        super(NpcData.getInstance().getTemplate(npcTemplateId));
        this._remainingTime = 1200000;
        this._buffTask = null;
        this._durationCheckTask = null;
        this._beastSkills = null;
        this._isFreyaBeast = isFreyaBeast;
        this.setInstanceType(InstanceType.L2TamedBeastInstance);
        this.setCurrentHp(this.getMaxHp());
        this.setCurrentMp(this.getMaxMp());
        this.setFoodType(food);
        this.setHome(x, y, z);
        this.spawnMe(x, y, z);
        this.setOwner(owner);
        if (isFreyaBeast) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this._owner);
        }
    }
    
    public void onReceiveFood() {
        this._remainingTime += 20000;
        if (this._remainingTime > 1200000) {
            this._remainingTime = 1200000;
        }
    }
    
    public Location getHome() {
        return new Location(this._homeX, this._homeY, this._homeZ);
    }
    
    public void setHome(final Creature c) {
        this.setHome(c.getX(), c.getY(), c.getZ());
    }
    
    public void setHome(final int x, final int y, final int z) {
        this._homeX = x;
        this._homeY = y;
        this._homeZ = z;
    }
    
    public int getRemainingTime() {
        return this._remainingTime;
    }
    
    public void setRemainingTime(final int duration) {
        this._remainingTime = duration;
    }
    
    public int getFoodType() {
        return this._foodSkillId;
    }
    
    public void setFoodType(final int foodItemId) {
        if (foodItemId > 0) {
            this._foodSkillId = foodItemId;
            if (this._durationCheckTask != null) {
                this._durationCheckTask.cancel(true);
            }
            this._durationCheckTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new CheckDuration(this), 60000L, 60000L);
        }
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        this.getAI().stopFollow();
        if (this._buffTask != null) {
            this._buffTask.cancel(true);
        }
        if (this._durationCheckTask != null) {
            this._durationCheckTask.cancel(true);
        }
        if (this._owner != null && this._owner.getTrainedBeasts() != null) {
            this._owner.getTrainedBeasts().remove(this);
        }
        this._buffTask = null;
        this._durationCheckTask = null;
        this._owner = null;
        this._foodSkillId = 0;
        this._remainingTime = 0;
        return true;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return !this._isFreyaBeast;
    }
    
    public boolean isFreyaBeast() {
        return this._isFreyaBeast;
    }
    
    public void addBeastSkill(final Skill skill) {
        if (this._beastSkills == null) {
            this._beastSkills = (Collection<Skill>)ConcurrentHashMap.newKeySet();
        }
        this._beastSkills.add(skill);
    }
    
    public void castBeastSkills() {
        if (this._owner == null || this._beastSkills == null) {
            return;
        }
        int delay = 100;
        for (final Skill skill : this._beastSkills) {
            ThreadPool.schedule((Runnable)new buffCast(skill), (long)delay);
            delay += 100 + skill.getHitTime();
        }
        ThreadPool.schedule((Runnable)new buffCast(null), (long)delay);
    }
    
    public Player getOwner() {
        return this._owner;
    }
    
    public void setOwner(final Player owner) {
        if (owner != null) {
            this._owner = owner;
            this.setTitle(owner.getName());
            this.setShowSummonAnimation(true);
            this.broadcastPacket(new NpcInfo(this));
            owner.addTrainedBeast(this);
            this.getAI().startFollow(this._owner, 100);
            if (!this._isFreyaBeast) {
                int totalBuffsAvailable = 0;
                for (final Skill skill : this.getTemplate().getSkills().values()) {
                    if (skill.isContinuous() && !skill.isDebuff()) {
                        ++totalBuffsAvailable;
                    }
                }
                if (this._buffTask != null) {
                    this._buffTask.cancel(true);
                }
                this._buffTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new CheckOwnerBuffs(this, totalBuffsAvailable), 5000L, 5000L);
            }
        }
        else {
            this.deleteMe();
        }
    }
    
    public boolean isTooFarFromHome() {
        return !MathUtil.isInsideRadius3D(this, this._homeX, this._homeY, this._homeZ, 30000);
    }
    
    @Override
    public boolean deleteMe() {
        if (this._buffTask != null) {
            this._buffTask.cancel(true);
        }
        this._durationCheckTask.cancel(true);
        this.stopHpMpRegeneration();
        if (this._owner != null && this._owner.getTrainedBeasts() != null) {
            this._owner.getTrainedBeasts().remove(this);
        }
        this.setTarget(null);
        this._buffTask = null;
        this._durationCheckTask = null;
        this._owner = null;
        this._foodSkillId = 0;
        this._remainingTime = 0;
        return super.deleteMe();
    }
    
    public void onOwnerGotAttacked(final Creature attacker) {
        if (this._owner == null || !this._owner.isOnline()) {
            this.deleteMe();
            return;
        }
        if (!MathUtil.isInsideRadius3D(this._owner, this, 2000)) {
            this.getAI().startFollow(this._owner);
            return;
        }
        if (this._owner.isDead() || this._isFreyaBeast) {
            return;
        }
        if (this.isCastingNow(SkillCaster::isAnyNormalType)) {
            return;
        }
        final float HPRatio = (float)this._owner.getCurrentHp() / this._owner.getMaxHp();
        if (HPRatio >= 0.8) {
            for (final Skill skill : this.getTemplate().getSkills().values()) {
                if (skill.isDebuff() && Rnd.get(3) < 1 && attacker != null && attacker.isAffectedBySkill(skill.getId())) {
                    this.sitCastAndFollow(skill, attacker);
                }
            }
        }
        else if (HPRatio < 0.5) {
            int chance = 1;
            if (HPRatio < 0.25) {
                chance = 2;
            }
            for (final Skill skill2 : this.getTemplate().getSkills().values()) {
                if (Rnd.get(5) < chance && skill2.hasAnyEffectType(EffectType.CPHEAL, EffectType.HEAL, EffectType.MANAHEAL_BY_LEVEL, EffectType.MANAHEAL_PERCENT)) {
                    this.sitCastAndFollow(skill2, this._owner);
                }
            }
        }
    }
    
    protected void sitCastAndFollow(final Skill skill, final Creature target) {
        this.stopMove(null);
        this.broadcastPacket(new StopMove(this));
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        this.setTarget(target);
        this.doCast(skill);
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this._owner);
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (player == null || !this.canTarget(player)) {
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
        }
        else if (interact) {
            if (this.isAutoAttackable(player) && Math.abs(player.getZ() - this.getZ()) < 100) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            }
            else {
                player.sendPacket(ActionFailed.STATIC_PACKET);
            }
        }
    }
    
    private static class CheckDuration implements Runnable
    {
        private final TamedBeast _tamedBeast;
        
        CheckDuration(final TamedBeast tamedBeast) {
            this._tamedBeast = tamedBeast;
        }
        
        @Override
        public void run() {
            final int foodTypeSkillId = this._tamedBeast.getFoodType();
            final Player owner = this._tamedBeast.getOwner();
            Item item = null;
            if (this._tamedBeast._isFreyaBeast) {
                item = owner.getInventory().getItemByItemId(foodTypeSkillId);
                if (item != null && item.getCount() >= 1L) {
                    owner.destroyItem("BeastMob", item, 1L, this._tamedBeast, true);
                    this._tamedBeast.broadcastPacket(new SocialAction(this._tamedBeast.getObjectId(), 3));
                }
                else {
                    this._tamedBeast.deleteMe();
                }
            }
            else {
                this._tamedBeast.setRemainingTime(this._tamedBeast.getRemainingTime() - 60000);
                if (foodTypeSkillId == 2188) {
                    item = owner.getInventory().getItemByItemId(6643);
                }
                else if (foodTypeSkillId == 2189) {
                    item = owner.getInventory().getItemByItemId(6644);
                }
                if (item != null && item.getCount() >= 1L) {
                    final WorldObject oldTarget = owner.getTarget();
                    owner.setTarget(this._tamedBeast);
                    SkillCaster.triggerCast(owner, this._tamedBeast, SkillEngine.getInstance().getSkill(foodTypeSkillId, 1));
                    owner.setTarget(oldTarget);
                }
                else if (this._tamedBeast.getRemainingTime() < 900000) {
                    this._tamedBeast.setRemainingTime(-1);
                }
                if (this._tamedBeast.getRemainingTime() <= 0) {
                    this._tamedBeast.deleteMe();
                }
            }
        }
    }
    
    private class buffCast implements Runnable
    {
        private final Skill _skill;
        
        public buffCast(final Skill skill) {
            this._skill = skill;
        }
        
        @Override
        public void run() {
            if (this._skill == null) {
                TamedBeast.this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, TamedBeast.this._owner);
            }
            else {
                TamedBeast.this.sitCastAndFollow(this._skill, TamedBeast.this._owner);
            }
        }
    }
    
    private class CheckOwnerBuffs implements Runnable
    {
        private final TamedBeast _tamedBeast;
        private final int _numBuffs;
        
        CheckOwnerBuffs(final TamedBeast tamedBeast, final int numBuffs) {
            this._tamedBeast = tamedBeast;
            this._numBuffs = numBuffs;
        }
        
        @Override
        public void run() {
            final Player owner = this._tamedBeast.getOwner();
            if (owner == null || !owner.isOnline()) {
                TamedBeast.this.deleteMe();
                return;
            }
            if (!MathUtil.isInsideRadius3D(TamedBeast.this, owner, 2000)) {
                TamedBeast.this.getAI().startFollow(owner);
                return;
            }
            if (owner.isDead()) {
                return;
            }
            if (TamedBeast.this.isCastingNow(SkillCaster::isAnyNormalType)) {
                return;
            }
            int totalBuffsOnOwner = 0;
            int i = 0;
            final int rand = Rnd.get(this._numBuffs);
            Skill buffToGive = null;
            for (final Skill skill : this._tamedBeast.getTemplate().getSkills().values()) {
                if (skill.isContinuous() && !skill.isDebuff()) {
                    if (i++ == rand) {
                        buffToGive = skill;
                    }
                    if (!owner.isAffectedBySkill(skill.getId())) {
                        continue;
                    }
                    ++totalBuffsOnOwner;
                }
            }
            if (this._numBuffs * 2 / 3 > totalBuffsOnOwner) {
                this._tamedBeast.sitCastAndFollow(buffToGive, owner);
            }
            TamedBeast.this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this._tamedBeast.getOwner());
        }
    }
}
