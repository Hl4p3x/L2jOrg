// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import io.github.joealisson.primitive.pair.IntLong;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.maps.IntLongMap;

public class ExInzoneWaiting extends ServerPacket
{
    private final int _currentTemplateId;
    private final IntLongMap _instanceTimes;
    private final boolean _sendByClient;
    
    public ExInzoneWaiting(final Player activeChar, final boolean sendByClient) {
        final Instance instance = InstanceManager.getInstance().getPlayerInstance(activeChar, false);
        this._currentTemplateId = ((instance != null && instance.getTemplateId() >= 0) ? instance.getTemplateId() : -1);
        this._instanceTimes = InstanceManager.getInstance().getAllInstanceTimes(activeChar);
        this._sendByClient = sendByClient;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_INZONE_WAITING_INFO);
        this.writeByte((byte)(byte)(this._sendByClient ? 0 : 1));
        this.writeInt(this._currentTemplateId);
        this.writeInt(this._instanceTimes.size());
        for (final IntLong entry : this._instanceTimes.entrySet()) {
            final long instanceTime = TimeUnit.MILLISECONDS.toSeconds(entry.getValue() - System.currentTimeMillis());
            this.writeInt(entry.getKey());
            this.writeInt((int)instanceTime);
        }
    }
}
