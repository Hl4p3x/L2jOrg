// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.authcomm.gs2as;

import org.l2j.gameserver.network.authcomm.AuthServerClient;
import io.github.joealisson.primitive.HashIntIntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.l2j.gameserver.network.authcomm.SendablePacket;

public class ServerStatus extends SendablePacket
{
    public static final int SERVER_LIST_STATUS = 1;
    public static final int SERVER_LIST_CLOCK = 2;
    public static final int SERVER_LIST_SQUARE_BRACKET = 3;
    public static final int MAX_PLAYERS = 4;
    public static final int TEST_SERVER = 5;
    public static final int SERVER_LIST_TYPE = 6;
    private final IntIntMap status;
    
    public ServerStatus() {
        this.status = (IntIntMap)new HashIntIntMap();
    }
    
    public ServerStatus add(final int status, final int value) {
        this.status.put(status, value);
        return this;
    }
    
    @Override
    protected void writeImpl(final AuthServerClient client) {
        this.writeByte((byte)6);
        this.writeInt(this.status.size());
        this.status.entrySet().forEach(entry -> {
            this.writeInt(entry.getKey());
            this.writeInt(entry.getValue());
        });
    }
}
