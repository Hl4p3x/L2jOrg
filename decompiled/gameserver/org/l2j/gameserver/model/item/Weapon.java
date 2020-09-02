// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.item.type.CrystalType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.item.type.WeaponType;

public final class Weapon extends ItemTemplate implements EquipableItem
{
    private final WeaponType type;
    private boolean magic;
    private int soulShot;
    private int spiritShot;
    private int manaConsume;
    private int damageRadius;
    private int attackAngle;
    private int changeWeapon;
    private boolean isAttackWeapon;
    private boolean useWeaponSkillsOnly;
    
    public Weapon(final int id, final String name, final WeaponType type, final BodyPart bodyPart) {
        super(id, name);
        this.type = type;
        this.bodyPart = bodyPart;
        this.type1 = 0;
        this.type2 = 0;
    }
    
    @Override
    public WeaponType getItemType() {
        return this.type;
    }
    
    @Override
    public int getItemMask() {
        return this.type.mask();
    }
    
    @Override
    public boolean isMagicWeapon() {
        return this.magic;
    }
    
    public int getMpConsume() {
        return this.manaConsume;
    }
    
    public int getBaseAttackRadius() {
        return this.damageRadius;
    }
    
    public int getBaseAttackAngle() {
        return this.attackAngle;
    }
    
    public int getChangeWeaponId() {
        return this.changeWeapon;
    }
    
    public boolean isAttackWeapon() {
        return this.isAttackWeapon;
    }
    
    public boolean useWeaponSkillsOnly() {
        return this.useWeaponSkillsOnly;
    }
    
    public void applyConditionalSkills(final Creature caster, final Creature target, final Skill trigger, final ItemSkillType type) {
        final Skill skill;
        final OnNpcSkillSee event;
        final Skill skill2;
        final EventDispatcher eventDispatcher;
        SystemMessage sm;
        this.forEachSkill(type, holder -> {
            skill = holder.getSkill();
            if (Rnd.get(100) < holder.getChance()) {
                if (type == ItemSkillType.ON_MAGIC_SKILL) {
                    if (trigger.isBad() != skill.isBad()) {
                        return;
                    }
                    else if (trigger.isMagic() != skill.isMagic()) {
                        return;
                    }
                    else if (trigger.isToggle()) {
                        return;
                    }
                    else if (skill.isBad() && Formulas.calcShldUse(caster, target) == 2) {
                        return;
                    }
                }
                if (!(!skill.checkCondition(caster, target))) {
                    skill.activateSkill(caster, target);
                    if (type == ItemSkillType.ON_MAGIC_SKILL) {
                        if (GameUtils.isPlayer(caster)) {
                            World.getInstance().forEachVisibleObjectInRange(caster, Npc.class, 1000, npc -> {
                                EventDispatcher.getInstance();
                                new OnNpcSkillSee(npc, caster.getActingPlayer(), skill2, false, new WorldObject[] { target });
                                eventDispatcher.notifyEventAsync(event, npc);
                                return;
                            });
                        }
                        if (GameUtils.isPlayer(caster)) {
                            sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ACTIVATED);
                            sm.addSkillName(skill);
                            caster.sendPacket(sm);
                        }
                    }
                }
            }
        });
    }
    
    public void setMagic(final boolean magic) {
        this.magic = magic;
    }
    
    public void setSoulshots(final int soulshots) {
        this.soulShot = soulshots;
    }
    
    public void setSpiritshots(final int spiritshots) {
        this.spiritShot = spiritshots;
    }
    
    public void setManaConsume(final int mana) {
        this.manaConsume = mana;
    }
    
    public void setDamageRadius(final int radius) {
        this.damageRadius = radius;
    }
    
    public void setDamageAngle(final int angle) {
        this.attackAngle = angle;
    }
    
    public void setEnchantable(final Boolean enchantable) {
        this.enchantable = enchantable;
    }
    
    public void setChangeWeapon(final int changeWeapon) {
        this.changeWeapon = changeWeapon;
    }
    
    public void setCanAttack(final Boolean canAttack) {
        this.isAttackWeapon = canAttack;
    }
    
    public void setRestrictSkills(final Boolean restrictSkills) {
        this.useWeaponSkillsOnly = restrictSkills;
    }
    
    public void setEquipReuseDelay(final int equipReuseDelay) {
        this.equipReuseDelay = equipReuseDelay;
    }
    
    @Override
    public void setCrystalType(final CrystalType crystalType) {
        this.crystalType = crystalType;
    }
    
    @Override
    public void setCrystalCount(final int count) {
        this.crystalCount = count;
    }
    
    public int getConsumeShotsCount() {
        int n = 0;
        switch (this.crystalType) {
            case S: {
                n = 4;
                break;
            }
            case A: {
                n = 3;
                break;
            }
            case B: {
                n = 2;
                break;
            }
            default: {
                n = 1;
                break;
            }
        }
        int count = n;
        if (this.type == WeaponType.BOW) {
            ++count;
        }
        return count;
    }
}
