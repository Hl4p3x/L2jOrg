// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.data.CrestData;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.InvalidDataPacketException;

public final class RequestExSetPledgeCrestLarge extends ClientPacket
{
    private int _length;
    private byte[] _data;
    
    public RequestExSetPledgeCrestLarge() {
        this._data = null;
    }
    
    public void readImpl() throws InvalidDataPacketException {
        this._length = this.readInt();
        if (this._length > 2176) {
            throw new InvalidDataPacketException();
        }
        this.readBytes(this._data = new byte[this._length]);
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        final Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }
        if (this._length < 0 || this._length > 2176) {
            ((GameClient)this.client).sendPacket(SystemMessageId.THE_SIZE_OF_THE_UPLOADED_SYMBOL_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS);
            return;
        }
        if (clan.getDissolvingExpiryTime() > System.currentTimeMillis()) {
            ((GameClient)this.client).sendPacket(SystemMessageId.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
            return;
        }
        if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_REGISTER_CREST)) {
            ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }
        if (this._length == 0) {
            if (clan.getCrestLargeId() != 0) {
                clan.changeLargeCrest(0);
                ((GameClient)this.client).sendPacket(SystemMessageId.THE_CLAN_MARK_HAS_BEEN_DELETED);
            }
        }
        else {
            if (clan.getLevel() < 3) {
                ((GameClient)this.client).sendPacket(SystemMessageId.A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLAN_S_SKILL_LEVEL_IS_3_OR_ABOVE);
                return;
            }
            final CrestData crest = CrestTable.getInstance().createCrest(this._data, CrestData.CrestType.PLEDGE_LARGE);
            if (crest != null) {
                clan.changeLargeCrest(crest.getId());
                ((GameClient)this.client).sendPacket(SystemMessageId.THE_CLAN_MARK_WAS_SUCCESSFULLY_REGISTERED_THE_SYMBOL_WILL_APPEAR_ON_THE_CLAN_FLAG_AND_THE_INSIGNIA_IS_ONLY_DISPLAYED_ON_ITEMS_PERTAINING_TO_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_CASTLE);
            }
        }
    }
}
