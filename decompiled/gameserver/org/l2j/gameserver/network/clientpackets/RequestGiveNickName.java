// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.GameClient;

public class RequestGiveNickName extends ClientPacket
{
    private String _target;
    private String _title;
    
    public void readImpl() {
        this._target = this.readString();
        this._title = this.readString();
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        if (activeChar == null) {
            return;
        }
        if (activeChar.isNoble() && this._target.equalsIgnoreCase(activeChar.getName())) {
            activeChar.setTitle(this._title);
            ((GameClient)this.client).sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
            activeChar.broadcastTitleInfo();
        }
        else {
            if (!activeChar.hasClanPrivilege(ClanPrivilege.CL_GIVE_TITLE)) {
                ((GameClient)this.client).sendPacket(SystemMessageId.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (activeChar.getClan().getLevel() < 3) {
                ((GameClient)this.client).sendPacket(SystemMessageId.A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE);
                return;
            }
            final ClanMember member1 = activeChar.getClan().getClanMember(this._target);
            if (member1 != null) {
                final Player member2 = member1.getPlayerInstance();
                if (member2 != null) {
                    member2.setTitle(this._title);
                    member2.sendPacket(SystemMessageId.YOUR_TITLE_HAS_BEEN_CHANGED);
                    member2.broadcastTitleInfo();
                }
                else {
                    ((GameClient)this.client).sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
                }
            }
            else {
                ((GameClient)this.client).sendPacket(SystemMessageId.THE_TARGET_MUST_BE_A_CLAN_MEMBER);
            }
        }
    }
}
