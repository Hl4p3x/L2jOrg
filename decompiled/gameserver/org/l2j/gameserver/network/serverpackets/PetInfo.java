// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Summon;

public class PetInfo extends ServerPacket
{
    private final Summon summon;
    private final int animationType;
    private final int runSpd;
    private final int walkSpd;
    private final int swimRunSpd;
    private final int swimWalkSpd;
    private final int flyRunSpd;
    private final int flyWalkSpd;
    private final double moveMultiplier;
    private int maxFed;
    private int currentFed;
    private int statusMask;
    
    public PetInfo(final Summon summon, final int val) {
        this.statusMask = 0;
        this.summon = summon;
        this.moveMultiplier = summon.getMovementSpeedMultiplier();
        this.runSpd = (int)Math.round(summon.getRunSpeed() / this.moveMultiplier);
        this.walkSpd = (int)Math.round(summon.getWalkSpeed() / this.moveMultiplier);
        this.swimRunSpd = (int)Math.round(summon.getSwimRunSpeed() / this.moveMultiplier);
        this.swimWalkSpd = (int)Math.round(summon.getSwimWalkSpeed() / this.moveMultiplier);
        this.flyRunSpd = (summon.isFlying() ? this.runSpd : 0);
        this.flyWalkSpd = (summon.isFlying() ? this.walkSpd : 0);
        this.animationType = val;
        if (GameUtils.isPet(summon)) {
            final Pet pet = (Pet)this.summon;
            this.currentFed = pet.getCurrentFed();
            this.maxFed = pet.getMaxFed();
        }
        else if (summon.isServitor()) {
            final Servitor sum = (Servitor)this.summon;
            this.currentFed = sum.getLifeTimeRemaining();
            this.maxFed = sum.getLifeTime();
        }
        if (summon.isBetrayed()) {
            this.statusMask |= 0x1;
        }
        this.statusMask |= 0x2;
        if (summon.isRunning()) {
            this.statusMask |= 0x4;
        }
        if (AttackStanceTaskManager.getInstance().hasAttackStanceTask(summon)) {
            this.statusMask |= 0x8;
        }
        if (summon.isDead()) {
            this.statusMask |= 0x10;
        }
        if (summon.isMountable()) {
            this.statusMask |= 0x20;
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.PET_INFO);
        this.writeByte(this.summon.getSummonType());
        this.writeInt(this.summon.getObjectId());
        this.writeInt(this.summon.getTemplate().getDisplayId() + 1000000);
        this.writeInt(this.summon.getX());
        this.writeInt(this.summon.getY());
        this.writeInt(this.summon.getZ());
        this.writeInt(this.summon.getHeading());
        this.writeInt(this.summon.getStats().getMAtkSpd());
        this.writeInt(this.summon.getStats().getPAtkSpd());
        this.writeShort(this.runSpd);
        this.writeShort(this.walkSpd);
        this.writeShort(this.swimRunSpd);
        this.writeShort(this.swimWalkSpd);
        this.writeShort(this.runSpd);
        this.writeShort(this.walkSpd);
        this.writeShort(this.flyRunSpd);
        this.writeShort(this.flyWalkSpd);
        this.writeDouble(this.moveMultiplier);
        this.writeDouble(this.summon.getAttackSpeedMultiplier());
        this.writeDouble(this.summon.getTemplate().getfCollisionRadius());
        this.writeDouble(this.summon.getTemplate().getfCollisionHeight());
        this.writeInt(this.summon.getWeapon());
        this.writeInt(this.summon.getArmor());
        this.writeInt(0);
        this.writeByte(this.summon.isShowSummonAnimation() ? 2 : this.animationType);
        this.writeInt(-1);
        if (GameUtils.isPet(this.summon)) {
            this.writeString((CharSequence)this.summon.getName());
        }
        else {
            this.writeString((CharSequence)(this.summon.getTemplate().isUsingServerSideName() ? this.summon.getName() : ""));
        }
        this.writeInt(-1);
        this.writeString((CharSequence)this.summon.getTitle());
        this.writeByte(this.summon.getPvpFlag());
        this.writeInt(this.summon.getReputation());
        this.writeInt(this.currentFed);
        this.writeInt(this.maxFed);
        this.writeInt((int)this.summon.getCurrentHp());
        this.writeInt(this.summon.getMaxHp());
        this.writeInt((int)this.summon.getCurrentMp());
        this.writeInt(this.summon.getMaxMp());
        this.writeLong(this.summon.getStats().getSp());
        this.writeShort(this.summon.getLevel());
        this.writeLong(this.summon.getStats().getExp());
        this.writeLong(this.summon.getExpForThisLevel());
        this.writeLong(this.summon.getExpForNextLevel());
        this.writeInt(GameUtils.isPet(this.summon) ? this.summon.getInventory().getTotalWeight() : 0);
        this.writeInt(this.summon.getMaxLoad());
        this.writeInt(this.summon.getPAtk());
        this.writeInt(this.summon.getPDef());
        this.writeInt(this.summon.getAccuracy());
        this.writeInt(this.summon.getEvasionRate());
        this.writeInt(this.summon.getCriticalHit());
        this.writeInt(this.summon.getMAtk());
        this.writeInt(this.summon.getMDef());
        this.writeInt(this.summon.getMagicAccuracy());
        this.writeInt(this.summon.getMagicEvasionRate());
        this.writeInt(this.summon.getMCriticalHit());
        this.writeInt((int)this.summon.getStats().getMoveSpeed());
        this.writeInt(this.summon.getPAtkSpd());
        this.writeInt(this.summon.getMAtkSpd());
        this.writeByte(0);
        this.writeByte(this.summon.getTeam().getId());
        this.writeByte((int)this.summon.getSoulShotsPerHit());
        this.writeByte((int)this.summon.getSpiritShotsPerHit());
        this.writeInt(0);
        this.writeInt(this.summon.getFormId());
        this.writeByte(this.summon.getOwner().getSummonPoints());
        this.writeByte(this.summon.getOwner().getMaxSummonPoints());
        final Set<AbnormalVisualEffect> aves = this.summon.getEffectList().getCurrentAbnormalVisualEffects();
        this.writeShort(aves.size());
        for (final AbnormalVisualEffect ave : aves) {
            this.writeShort(ave.getClientId());
        }
        this.writeByte(this.statusMask);
        this.writeInt(0);
        this.writeInt(0);
        this.writeInt(0);
    }
}
