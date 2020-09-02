// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets.elementalspirits;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.engine.elemental.AbsorbItem;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.network.serverpackets.elementalspirits.ElementalSpiritAbsorb;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import java.util.Objects;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

public class ExElementalSpiritAbsorb extends ClientPacket
{
    private byte type;
    private int itemId;
    private int amount;
    
    @Override
    protected void readImpl() throws Exception {
        this.type = this.readByte();
        this.readInt();
        this.itemId = this.readInt();
        this.amount = this.readInt();
    }
    
    @Override
    protected void runImpl() {
        final Player player = ((GameClient)this.client).getPlayer();
        final ElementalSpirit spirit = player.getElementalSpirit(ElementalType.of(this.type));
        if (Objects.isNull(spirit)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.NO_SPIRITS_ARE_AVAILABLE);
            return;
        }
        final AbsorbItem absorbItem = spirit.getAbsorbItem(this.itemId);
        if (Objects.isNull(absorbItem)) {
            player.sendPacket(new ElementalSpiritAbsorb(this.type, false));
            return;
        }
        final boolean canAbsorb = this.checkConditions(player, spirit);
        if (canAbsorb) {
            ((GameClient)this.client).sendPacket(SystemMessageId.DRAIN_SUCCESSFUL);
            spirit.addExperience(absorbItem.getExperience() * this.amount);
            final UserInfo userInfo = new UserInfo(player);
            userInfo.addComponentType(UserInfoType.SPIRITS);
            ((GameClient)this.client).sendPacket(userInfo);
        }
        ((GameClient)this.client).sendPacket(new ElementalSpiritAbsorb(this.type, canAbsorb));
    }
    
    private boolean checkConditions(final Player player, final ElementalSpirit spirit) {
        boolean noMeetConditions = false;
        if (noMeetConditions = (player.getPrivateStoreType() != PrivateStoreType.NONE)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP);
        }
        else if (noMeetConditions = player.isInBattle()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.CANNOT_DRAIN_DURING_BATTLE);
        }
        else if (noMeetConditions = (spirit.getLevel() == spirit.getMaxLevel() && spirit.getExperience() == spirit.getExperienceToNextLevel())) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_HAVE_REACHED_THE_HIGHEST_LEVEL_AND_CANNOT_ABSORB_ANY_FURTHER);
        }
        else if (noMeetConditions = (this.amount < 1 || !player.destroyItemByItemId("Absorb", this.itemId, this.amount, player, true))) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_ABSORB);
        }
        return !noMeetConditions;
    }
}
