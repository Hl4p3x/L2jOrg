// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Pet;

public class PetStats extends SummonStats
{
    public PetStats(final Pet activeChar) {
        super(activeChar);
    }
    
    public boolean addExp(final int value) {
        if (this.getCreature().isUncontrollable() || !super.addExp(value)) {
            return false;
        }
        this.getCreature().updateAndBroadcastStatus(1);
        return true;
    }
    
    public boolean addExpAndSp(final double addToExp, final double addToSp) {
        final long finalExp = Math.round(addToExp);
        if (this.getCreature().isUncontrollable() || !this.addExp(finalExp)) {
            return false;
        }
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_PET_GAINED_S1_XP);
        sm.addLong(finalExp);
        this.getCreature().updateAndBroadcastStatus(1);
        this.getCreature().sendPacket(sm);
        return true;
    }
    
    @Override
    public final boolean addLevel(final byte value) {
        if (this.getLevel() + value > this.getMaxLevel() - 1) {
            return false;
        }
        final boolean levelIncreased = super.addLevel(value);
        this.getCreature().broadcastStatusUpdate();
        if (levelIncreased) {
            this.getCreature().broadcastPacket(new SocialAction(this.getCreature().getObjectId(), 2122));
        }
        this.getCreature().updateAndBroadcastStatus(1);
        if (this.getCreature().getControlItem() != null) {
            this.getCreature().getControlItem().setEnchantLevel(this.getLevel());
        }
        return levelIncreased;
    }
    
    @Override
    public final long getExpForLevel(final int level) {
        try {
            return PetDataTable.getInstance().getPetLevelData(this.getCreature().getId(), level).getPetMaxExp();
        }
        catch (NullPointerException e) {
            if (this.getCreature() != null) {
                PetStats.LOGGER.warn(invokedynamic(makeConcatWithConstants:(III)Ljava/lang/String;, this.getCreature().getObjectId(), this.getCreature().getId(), level));
            }
            throw e;
        }
    }
    
    @Override
    public Pet getCreature() {
        return (Pet)super.getCreature();
    }
    
    public final int getFeedBattle() {
        return this.getCreature().getPetLevelData().getPetFeedBattle();
    }
    
    public final int getFeedNormal() {
        return this.getCreature().getPetLevelData().getPetFeedNormal();
    }
    
    @Override
    public void setLevel(final byte value) {
        this.getCreature().setPetData(PetDataTable.getInstance().getPetLevelData(this.getCreature().getTemplate().getId(), value));
        if (this.getCreature().getPetLevelData() == null) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(IB)Ljava/lang/String;, this.getCreature().getTemplate().getId(), value));
        }
        this.getCreature().stopFeed();
        super.setLevel(value);
        this.getCreature().startFeed();
        if (this.getCreature().getControlItem() != null) {
            this.getCreature().getControlItem().setEnchantLevel(this.getLevel());
        }
    }
    
    public final int getMaxFeed() {
        return this.getCreature().getPetLevelData().getPetMaxFeed();
    }
    
    @Override
    public int getPAtkSpd() {
        int val = super.getPAtkSpd();
        if (this.getCreature().isHungry()) {
            val /= 2;
        }
        return val;
    }
    
    @Override
    public int getMAtkSpd() {
        int val = super.getMAtkSpd();
        if (this.getCreature().isHungry()) {
            val /= 2;
        }
        return val;
    }
    
    @Override
    public int getMaxLevel() {
        return LevelData.getInstance().getMaxLevel();
    }
}
