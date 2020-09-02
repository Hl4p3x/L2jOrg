// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;

public class ExSetCompassZoneCode extends ServerPacket
{
    public static final int ALTEREDZONE = 8;
    public static final int SIEGEWARZONE1 = 10;
    public static final int SIEGEWARZONE2 = 11;
    public static final int PEACEZONE = 12;
    public static final int SEVENSIGNSZONE = 13;
    public static final int PVPZONE = 14;
    public static final int GENERALZONE = 15;
    private final int _zoneType;
    
    public ExSetCompassZoneCode(final int val) {
        this._zoneType = val;
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SET_COMPASS_ZONE_CODE);
        this.writeInt(this._zoneType);
    }
}
