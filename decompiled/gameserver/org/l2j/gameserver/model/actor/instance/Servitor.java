// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.network.serverpackets.SetSummonRemainTime;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.WorldObject;
import java.sql.ResultSet;
import org.l2j.gameserver.model.skills.EffectScope;
import java.util.List;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import java.util.ArrayList;
import org.l2j.commons.database.DatabaseFactory;
import java.util.Collections;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import java.util.Iterator;
import java.util.Map;
import java.util.Collection;
import org.l2j.gameserver.data.sql.impl.SummonEffectsTable;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.l2j.gameserver.model.actor.Summon;

public class Servitor extends Summon implements Runnable
{
    protected static final Logger log;
    private static final String ADD_SKILL_SAVE = "REPLACE INTO character_summon_skills_save (ownerId,ownerClassIndex,summonSkillId,skill_id,skill_level,remaining_time,buff_index) VALUES (?,?,?,?,?,?,?)";
    private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,remaining_time,buff_index FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=? ORDER BY buff_index ASC";
    private static final String DELETE_SKILL_SAVE = "DELETE FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=?";
    protected Future<?> _summonLifeTask;
    private float _expMultiplier;
    private ItemHolder _itemConsume;
    private int _lifeTime;
    private int _lifeTimeRemaining;
    private int _consumeItemInterval;
    private int _consumeItemIntervalRemaining;
    private int _referenceSkill;
    
    public Servitor(final NpcTemplate template, final Player owner) {
        super(template, owner);
        this._expMultiplier = 0.0f;
        this.setInstanceType(InstanceType.L2ServitorInstance);
        this.setShowSummonAnimation(true);
        this.running = true;
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        if (this._lifeTime > 0 && this._summonLifeTask == null) {
            this._summonLifeTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)this, 0L, 5000L);
        }
    }
    
    @Override
    public final int getLevel() {
        return (this.getTemplate() != null) ? this.getTemplate().getLevel() : 0;
    }
    
    @Override
    public int getSummonType() {
        return 1;
    }
    
    public float getExpMultiplier() {
        return this._expMultiplier;
    }
    
    public void setExpMultiplier(final float expMultiplier) {
        this._expMultiplier = expMultiplier;
    }
    
    public ItemHolder getItemConsume() {
        return this._itemConsume;
    }
    
    public void setItemConsume(final ItemHolder item) {
        this._itemConsume = item;
    }
    
    public int getItemConsumeInterval() {
        return this._consumeItemInterval;
    }
    
    public void setItemConsumeInterval(final int interval) {
        this._consumeItemInterval = interval;
        this._consumeItemIntervalRemaining = interval;
    }
    
    public int getLifeTime() {
        return this._lifeTime;
    }
    
    public void setLifeTime(final int lifeTime) {
        this._lifeTime = lifeTime;
        this._lifeTimeRemaining = lifeTime;
    }
    
    public int getLifeTimeRemaining() {
        return this._lifeTimeRemaining;
    }
    
    public void setLifeTimeRemaining(final int time) {
        this._lifeTimeRemaining = time;
    }
    
    public int getReferenceSkill() {
        return this._referenceSkill;
    }
    
    public void setReferenceSkill(final int skillId) {
        this._referenceSkill = skillId;
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        if (this._summonLifeTask != null) {
            this._summonLifeTask.cancel(false);
        }
        return true;
    }
    
    @Override
    public void doCast(final Skill skill) {
        final int petLevel = this.getLevel();
        int skillLevel = petLevel / 10;
        if (petLevel >= 70) {
            skillLevel += (petLevel - 65) / 10;
        }
        if (skillLevel < 1) {
            skillLevel = 1;
        }
        final Skill skillToCast = SkillEngine.getInstance().getSkill(skill.getId(), skillLevel);
        if (skillToCast != null) {
            super.doCast(skillToCast);
        }
        else {
            super.doCast(skill);
        }
    }
    
    @Override
    public void setRestoreSummon(final boolean val) {
        this._restoreSummon = val;
    }
    
    @Override
    public final void stopSkillEffects(final boolean removed, final int skillId) {
        super.stopSkillEffects(removed, skillId);
        final Map<Integer, Collection<SummonEffectsTable.SummonEffect>> servitorEffects = SummonEffectsTable.getInstance().getServitorEffects(this.getOwner());
        if (servitorEffects != null) {
            final Collection<SummonEffectsTable.SummonEffect> effects = servitorEffects.get(this._referenceSkill);
            if (effects != null && !effects.isEmpty()) {
                for (final SummonEffectsTable.SummonEffect effect : effects) {
                    final Skill skill = effect.getSkill();
                    if (skill != null && skill.getId() == skillId) {
                        effects.remove(effect);
                    }
                }
            }
        }
    }
    
    @Override
    public void storeMe() {
        if (this._referenceSkill == 0) {
            return;
        }
        if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).restoreSummonOnReconnect()) {
            if (this.isDead()) {
                PlayerSummonTable.getInstance().removeServitor(this.getOwner(), this.getObjectId());
            }
            else {
                PlayerSummonTable.getInstance().saveSummon(this);
            }
        }
    }
    
    @Override
    public void storeEffect(final boolean storeEffects) {
        if (!Config.SUMMON_STORE_SKILL_COOLTIME) {
            return;
        }
        if (this.getOwner() == null || this.getOwner().isInOlympiadMode()) {
            return;
        }
        if (SummonEffectsTable.getInstance().getServitorEffectsOwner().getOrDefault(this.getOwner().getObjectId(), Collections.emptyMap()).containsKey(this.getOwner().getClassIndex())) {
            SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).getOrDefault(this.getReferenceSkill(), (Collection<SummonEffectsTable.SummonEffect>)Collections.emptyList()).clear();
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=?");
                try {
                    statement.setInt(1, this.getOwner().getObjectId());
                    statement.setInt(2, this.getOwner().getClassIndex());
                    statement.setInt(3, this._referenceSkill);
                    statement.execute();
                    int buff_index = 0;
                    final List<Long> storedSkills = new ArrayList<Long>();
                    if (storeEffects) {
                        final PreparedStatement ps2 = con.prepareStatement("REPLACE INTO character_summon_skills_save (ownerId,ownerClassIndex,summonSkillId,skill_id,skill_level,remaining_time,buff_index) VALUES (?,?,?,?,?,?,?)");
                        try {
                            for (final BuffInfo info : this.getEffectList().getEffects()) {
                                if (info == null) {
                                    continue;
                                }
                                final Skill skill = info.getSkill();
                                if (skill.isDeleteAbnormalOnLeave()) {
                                    continue;
                                }
                                if (skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                                    continue;
                                }
                                if (skill.isToggle()) {
                                    continue;
                                }
                                if (skill.isDance() && !Config.ALT_STORE_DANCES) {
                                    continue;
                                }
                                if (storedSkills.contains(skill.getReuseHashCode())) {
                                    continue;
                                }
                                storedSkills.add(skill.getReuseHashCode());
                                ps2.setInt(1, this.getOwner().getObjectId());
                                ps2.setInt(2, this.getOwner().getClassIndex());
                                ps2.setInt(3, this._referenceSkill);
                                ps2.setInt(4, skill.getId());
                                ps2.setInt(5, skill.getLevel());
                                ps2.setInt(6, info.getTime());
                                ps2.setInt(7, ++buff_index);
                                ps2.addBatch();
                                if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().containsKey(this.getOwner().getObjectId())) {
                                    SummonEffectsTable.getInstance().getServitorEffectsOwner().put(this.getOwner().getObjectId(), new HashMap<Integer, Map<Integer, Collection<SummonEffectsTable.SummonEffect>>>());
                                }
                                if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).containsKey(this.getOwner().getClassIndex())) {
                                    SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).put(this.getOwner().getClassIndex(), new HashMap<Integer, Collection<SummonEffectsTable.SummonEffect>>());
                                }
                                if (!SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).containsKey(this.getReferenceSkill())) {
                                    SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).put(this.getReferenceSkill(), (Collection<SummonEffectsTable.SummonEffect>)ConcurrentHashMap.newKeySet());
                                }
                                SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).get(this.getReferenceSkill()).add(new SummonEffectsTable.SummonEffect(skill, info.getTime()));
                            }
                            ps2.executeBatch();
                            if (ps2 != null) {
                                ps2.close();
                            }
                        }
                        catch (Throwable t) {
                            if (ps2 != null) {
                                try {
                                    ps2.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Servitor.LOGGER.warn("Could not store summon effect data: ", (Throwable)e);
        }
    }
    
    @Override
    public void restoreEffects() {
        if (this.getOwner().isInOlympiadMode()) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().containsKey(this.getOwner().getObjectId()) || !SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).containsKey(this.getOwner().getClassIndex()) || !SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).containsKey(this.getReferenceSkill())) {
                    final PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level,remaining_time,buff_index FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=? ORDER BY buff_index ASC");
                    try {
                        statement.setInt(1, this.getOwner().getObjectId());
                        statement.setInt(2, this.getOwner().getClassIndex());
                        statement.setInt(3, this._referenceSkill);
                        final ResultSet rset = statement.executeQuery();
                        try {
                            while (rset.next()) {
                                final int effectCurTime = rset.getInt("remaining_time");
                                final Skill skill = SkillEngine.getInstance().getSkill(rset.getInt("skill_id"), rset.getInt("skill_level"));
                                if (skill == null) {
                                    continue;
                                }
                                if (!skill.hasEffects(EffectScope.GENERAL)) {
                                    continue;
                                }
                                if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().containsKey(this.getOwner().getObjectId())) {
                                    SummonEffectsTable.getInstance().getServitorEffectsOwner().put(this.getOwner().getObjectId(), new HashMap<Integer, Map<Integer, Collection<SummonEffectsTable.SummonEffect>>>());
                                }
                                if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).containsKey(this.getOwner().getClassIndex())) {
                                    SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).put(this.getOwner().getClassIndex(), new HashMap<Integer, Collection<SummonEffectsTable.SummonEffect>>());
                                }
                                if (!SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).containsKey(this.getReferenceSkill())) {
                                    SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).put(this.getReferenceSkill(), (Collection<SummonEffectsTable.SummonEffect>)ConcurrentHashMap.newKeySet());
                                }
                                SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).get(this.getReferenceSkill()).add(new SummonEffectsTable.SummonEffect(skill, effectCurTime));
                            }
                            if (rset != null) {
                                rset.close();
                            }
                        }
                        catch (Throwable t) {
                            if (rset != null) {
                                try {
                                    rset.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                }
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_summon_skills_save WHERE ownerId=? AND ownerClassIndex=? AND summonSkillId=?");
                try {
                    statement.setInt(1, this.getOwner().getObjectId());
                    statement.setInt(2, this.getOwner().getClassIndex());
                    statement.setInt(3, this._referenceSkill);
                    statement.executeUpdate();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t3) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t4) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception4) {
                        t4.addSuppressed(exception4);
                    }
                }
                throw t4;
            }
        }
        catch (Exception e) {
            Servitor.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Servitor;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
        finally {
            if (!SummonEffectsTable.getInstance().getServitorEffectsOwner().containsKey(this.getOwner().getObjectId()) || !SummonEffectsTable.getInstance().getServitorEffectsOwner().get(this.getOwner().getObjectId()).containsKey(this.getOwner().getClassIndex()) || !SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).containsKey(this.getReferenceSkill())) {
                return;
            }
            for (final SummonEffectsTable.SummonEffect se : SummonEffectsTable.getInstance().getServitorEffects(this.getOwner()).get(this.getReferenceSkill())) {
                if (se != null) {
                    se.getSkill().applyEffects(this, this, false, se.getEffectCurTime());
                }
            }
        }
    }
    
    @Override
    public void unSummon(final Player owner) {
        if (this._summonLifeTask != null) {
            this._summonLifeTask.cancel(false);
        }
        super.unSummon(owner);
        if (!this._restoreSummon) {
            PlayerSummonTable.getInstance().removeServitor(owner, this.getObjectId());
        }
    }
    
    @Override
    public boolean destroyItem(final String process, final int objectId, final long count, final WorldObject reference, final boolean sendMessage) {
        return this.getOwner().destroyItem(process, objectId, count, reference, sendMessage);
    }
    
    @Override
    public boolean destroyItemByItemId(final String process, final int itemId, final long count, final WorldObject reference, final boolean sendMessage) {
        return this.getOwner().destroyItemByItemId(process, itemId, count, reference, sendMessage);
    }
    
    @Override
    public AttributeType getAttackElement() {
        if (this.getOwner() != null) {
            return this.getOwner().getAttackElement();
        }
        return super.getAttackElement();
    }
    
    @Override
    public int getAttackElementValue(final AttributeType attackAttribute) {
        if (this.getOwner() != null) {
            return this.getOwner().getAttackElementValue(attackAttribute);
        }
        return super.getAttackElementValue(attackAttribute);
    }
    
    @Override
    public int getDefenseElementValue(final AttributeType defenseAttribute) {
        if (this.getOwner() != null) {
            return this.getOwner().getDefenseElementValue(defenseAttribute);
        }
        return super.getDefenseElementValue(defenseAttribute);
    }
    
    @Override
    public boolean isServitor() {
        return true;
    }
    
    @Override
    public void run() {
        final int usedtime = 5000;
        this._lifeTimeRemaining -= 5000;
        if (this.isDead() || !this.isSpawned()) {
            if (this._summonLifeTask != null) {
                this._summonLifeTask.cancel(false);
            }
            return;
        }
        if (this._lifeTimeRemaining < 0) {
            this.sendPacket(SystemMessageId.YOUR_SERVITOR_PASSED_AWAY);
            this.unSummon(this.getOwner());
            return;
        }
        if (this._consumeItemInterval > 0) {
            this._consumeItemIntervalRemaining -= 5000;
            if (this._consumeItemIntervalRemaining <= 0 && this._itemConsume.getCount() > 0L && this._itemConsume.getId() > 0 && !this.isDead()) {
                if (this.destroyItemByItemId("Consume", this._itemConsume.getId(), this._itemConsume.getCount(), this, false)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.A_SUMMONED_MONSTER_USES_S1);
                    msg.addItemName(this._itemConsume.getId());
                    this.sendPacket(msg);
                    this._consumeItemIntervalRemaining = this._consumeItemInterval;
                }
                else {
                    this.sendPacket(SystemMessageId.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITOR_S_STAY_THE_SERVITOR_HAS_DISAPPEARED);
                    this.unSummon(this.getOwner());
                }
            }
        }
        this.sendPacket(new SetSummonRemainTime(this._lifeTime, this._lifeTimeRemaining));
        if (!MathUtil.isInsideRadius3D(this, this.getOwner(), 2000)) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this.getOwner());
        }
    }
    
    @Override
    public void doPickupItem(final WorldObject object) {
    }
    
    static {
        log = LoggerFactory.getLogger((Class)Servitor.class);
    }
}
