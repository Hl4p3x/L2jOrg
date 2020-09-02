// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritSetTalent;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritSetTalent extends ClientPacket
{
    private byte type;
    private byte attackPoints;
    private byte defensePoints;
    private byte critRate;
    private byte critDamage;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
        this.readByte();
        this.readByte();
        this.attackPoints = this.readByte();
        this.readByte();
        this.defensePoints = this.readByte();
        this.readByte();
        this.critRate = this.readByte();
        this.readByte();
        this.critDamage = this.readByte();
    }
    
    @Override
    protected void runImpl() {
        final ElementalSpirit spirit = ((GameClient)this.client).getPlayer().getElementalSpirit(ElementalType.of(this.type));
        boolean result = false;
        if (Objects.nonNull(spirit)) {
            if (this.attackPoints > 0 && spirit.getAvailableCharacteristicsPoints() >= this.attackPoints) {
                spirit.addAttackPoints(this.attackPoints);
                result = true;
            }
            if (this.defensePoints > 0 && spirit.getAvailableCharacteristicsPoints() >= this.defensePoints) {
                spirit.addDefensePoints(this.defensePoints);
                result = true;
            }
            if (this.critRate > 0 && spirit.getAvailableCharacteristicsPoints() >= this.critRate) {
                spirit.addCritRatePoints(this.critRate);
                result = true;
            }
            if (this.critDamage > 0 && spirit.getAvailableCharacteristicsPoints() >= this.critDamage) {
                spirit.addCritDamage(this.critDamage);
                result = true;
            }
        }
        if (result) {
            final UserInfo userInfo = new UserInfo(((GameClient)this.client).getPlayer());
            userInfo.addComponentType(UserInfoType.SPIRITS);
            ((GameClient)this.client).sendPacket(userInfo);
            ((GameClient)this.client).sendPacket(SystemMessage.getSystemMessage(SystemMessageId.CHARACTERISTICS_WERE_APPLIED_SUCCESSFULLY));
        }
        ((GameClient)this.client).sendPacket(new ElementalSpiritSetTalent(this.type, result));
    }
}
