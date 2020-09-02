// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

abstract class AbstractElementalSpiritPacket extends ServerPacket
{
    void writeSpiritInfo(final ElementalSpirit spirit) {
        this.writeByte(spirit.getStage());
        this.writeInt(spirit.getNpcId());
        this.writeLong(spirit.getExperience());
        this.writeLong(spirit.getExperienceToNextLevel());
        this.writeLong(spirit.getExperienceToNextLevel());
        this.writeInt((int)spirit.getLevel());
        this.writeInt(spirit.getMaxLevel());
        this.writeInt(spirit.getAvailableCharacteristicsPoints());
        this.writeInt(spirit.getAttackPoints());
        this.writeInt(spirit.getDefensePoints());
        this.writeInt(spirit.getCriticalRatePoints());
        this.writeInt(spirit.getCriticalDamagePoints());
        this.writeInt(spirit.getMaxCharacteristics());
        this.writeInt(spirit.getMaxCharacteristics());
        this.writeInt(spirit.getMaxCharacteristics());
        this.writeInt(spirit.getMaxCharacteristics());
        this.writeByte(1);
        for (int j = 0; j < 1; ++j) {
            this.writeShort(2);
            this.writeLong(100L);
        }
    }
}
