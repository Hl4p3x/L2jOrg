// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.sql.impl.PetNameTable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.network.GameClient;

public final class RequestChangePetName extends ClientPacket
{
    private String _name;
    
    public void readImpl() {
        this._name = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Summon pet = activeChar.getPet();
        if (!GameUtils.isPet(pet)) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
            return;
        }
        if (pet.getName() != null) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SET_THE_NAME_OF_THE_PET);
            return;
        }
        if (PetNameTable.getInstance().doesPetNameExist(this._name, pet.getTemplate().getId())) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET);
            return;
        }
        if (this._name.length() < 3 || this._name.length() > 16) {
            activeChar.sendMessage("Your pet's name can be up to 16 characters in length.");
            return;
        }
        if (!PetNameTable.getInstance().isValidPetName(this._name)) {
            activeChar.sendPacket(SystemMessageId.AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PET_S_NAME);
            return;
        }
        pet.setName(this._name);
        pet.updateAndBroadcastStatus(1);
    }
}
