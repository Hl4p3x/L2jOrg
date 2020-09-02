// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;

public class CharCreateFail extends ServerPacket
{
    private final int _error;
    
    public CharCreateFail(final CharacterCreateFailReason reason) {
        this._error = reason.getCode();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.CHARACTER_CREATE_FAIL);
        this.writeInt(this._error);
    }
    
    public enum CharacterCreateFailReason
    {
        REASON_CREATION_FAILED(0), 
        REASON_TOO_MANY_CHARACTERS(1), 
        REASON_NAME_ALREADY_EXISTS(2), 
        REASON_16_ENG_CHARS(3), 
        REASON_INCORRECT_NAME(4), 
        REASON_CREATE_NOT_ALLOWED(5), 
        REASON_CHOOSE_ANOTHER_SVR(6);
        
        private final int _code;
        
        private CharacterCreateFailReason(final int code) {
            this._code = code;
        }
        
        public final int getCode() {
            return this._code;
        }
    }
}
