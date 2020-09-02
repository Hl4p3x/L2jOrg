// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.SystemMessageId;

public final class SystemMessage extends AbstractMessagePacket<SystemMessage>
{
    private SystemMessage(final SystemMessageId smId) {
        super(smId);
    }
    
    public static SystemMessage sendString(final String text) {
        if (text == null) {
            throw new NullPointerException();
        }
        final SystemMessage sm = getSystemMessage(SystemMessageId.S1);
        sm.addString(text);
        return sm;
    }
    
    public static SystemMessage getSystemMessage(final SystemMessageId smId) {
        SystemMessage sm = smId.getStaticSystemMessage();
        if (sm != null) {
            return sm;
        }
        sm = new SystemMessage(smId);
        if (smId.getParamCount() == 0) {
            smId.setStaticSystemMessage(sm);
        }
        return sm;
    }
    
    public static SystemMessage getSystemMessage(final int id) {
        return getSystemMessage(SystemMessageId.getSystemMessageId(id));
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SYSTEM_MSG);
        this.writeShort(this.getId());
        this.writeMe();
    }
}
