// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ChangePassword extends SendablePacket
{
    public String _account;
    public String _oldPass;
    public String _newPass;
    public String _hwid;
    
    public ChangePassword(final String account, final String oldPass, final String newPass, final String hwid) {
        this._account = account;
        this._oldPass = oldPass;
        this._newPass = newPass;
        this._hwid = hwid;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)8);
        this.writeString((CharSequence)this._account);
        this.writeString((CharSequence)this._oldPass);
        this.writeString((CharSequence)this._newPass);
        this.writeString((CharSequence)this._hwid);
    }
}
