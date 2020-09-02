// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class LoginFail extends ServerPacket
{
    public static final int NO_TEXT = 0;
    public static final int SYSTEM_ERROR_LOGIN_LATER = 1;
    public static final int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT = 2;
    public static final int PASSWORD_DOES_NOT_MATCH_THIS_ACCOUNT2 = 3;
    public static final int ACCESS_FAILED_TRY_LATER = 4;
    public static final int INCORRECT_ACCOUNT_INFO_CONTACT_CUSTOMER_SUPPORT = 5;
    public static final int ACCESS_FAILED_TRY_LATER2 = 6;
    public static final int ACOUNT_ALREADY_IN_USE = 7;
    public static final int ACCESS_FAILED_TRY_LATER3 = 8;
    public static final int ACCESS_FAILED_TRY_LATER4 = 9;
    public static final int ACCESS_FAILED_TRY_LATER5 = 10;
    public static final LoginFail LOGIN_SUCCESS;
    private final int _reason;
    private final int _success;
    
    public LoginFail(final int reason) {
        this._success = 0;
        this._reason = reason;
    }
    
    public LoginFail(final int success, final int reason) {
        this._success = success;
        this._reason = reason;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.LOGIN_RESULT);
        this.writeInt(this._success);
        this.writeInt(this._reason);
    }
    
    static {
        LOGIN_SUCCESS = new LoginFail(-1, 0);
    }
}
