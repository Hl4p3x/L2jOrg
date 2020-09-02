// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.as2gs;

import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.SetAccountInfo;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.network.authcomm.ReceivablePacket;

public class GetAccountInfo extends ReceivablePacket
{
    private String account;
    
    @Override
    protected void readImpl() {
        this.account = this.readString();
    }
    
    @Override
    protected void runImpl() {
        final int playerSize = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).playerCountByAccount(this.account);
        AuthServerCommunication.getInstance().sendPacket(new SetAccountInfo(this.account, playerSize));
    }
}
