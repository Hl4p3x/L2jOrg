// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.Ex2ndPasswordAck;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.data.xml.SecondaryAuthManager;

public class RequestEx2ndPasswordReq extends ClientPacket
{
    private int changePass;
    private String password;
    private String newPassword;
    
    public void readImpl() {
        this.changePass = this.readByte();
        this.password = this.readString();
        if (this.changePass == 2) {
            this.newPassword = this.readString();
        }
    }
    
    public void runImpl() {
        if (!SecondaryAuthManager.getInstance().isEnabled()) {
            return;
        }
        boolean success = false;
        if (this.changePass == 0 && !((GameClient)this.client).hasSecondPassword()) {
            success = ((GameClient)this.client).saveSecondPassword(this.password);
        }
        else if (this.changePass == 2 && ((GameClient)this.client).hasSecondPassword()) {
            success = ((GameClient)this.client).changeSecondPassword(this.password, this.newPassword);
        }
        if (success) {
            ((GameClient)this.client).sendPacket(new Ex2ndPasswordAck(this.changePass, Ex2ndPasswordAck.SUCCESS));
        }
    }
}
