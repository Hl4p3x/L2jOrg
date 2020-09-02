// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Arrays;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;

public class ExSendUIEvent extends ServerPacket
{
    public static int TYPE_COUNT_DOWN;
    public static int TYPE_REMOVE;
    public static int TYPE_ISTINA;
    public static int TYPE_COUNTER;
    public static int TYPE_GP_TIMER;
    public static int TYPE_NORNIL;
    public static int TYPE_DRACO_INCUBATION_1;
    public static int TYPE_DRACO_INCUBATION_2;
    public static int TYPE_CLAN_PROGRESS_BAR;
    private final int _objectId;
    private final int _type;
    private final int _countUp;
    private final int _startTime;
    private final int _startTime2;
    private final int _endTime;
    private final int _endTime2;
    private final int _npcstringId;
    private List<String> _params;
    
    public ExSendUIEvent(final Player player) {
        this(player, ExSendUIEvent.TYPE_REMOVE, 0, 0, 0, 0, 0, -1, new String[0]);
    }
    
    public ExSendUIEvent(final Player player, final int uiType, final int currentPoints, final int maxPoints, final NpcStringId npcString, final String... params) {
        this(player, uiType, -1, currentPoints, maxPoints, -1, -1, npcString.getId(), params);
    }
    
    public ExSendUIEvent(final Player player, final boolean hide, final boolean countUp, final int startTime, final int endTime, final String text) {
        this(player, hide ? 1 : 0, countUp ? 1 : 0, startTime / 60, startTime % 60, endTime / 60, endTime % 60, -1, new String[] { text });
    }
    
    public ExSendUIEvent(final Player player, final boolean hide, final boolean countUp, final int startTime, final int endTime, final NpcStringId npcString, final String... params) {
        this(player, hide ? 1 : 0, countUp ? 1 : 0, startTime / 60, startTime % 60, endTime / 60, endTime % 60, npcString.getId(), params);
    }
    
    public ExSendUIEvent(final Player player, final int type, final int countUp, final int startTime, final int startTime2, final int endTime, final int endTime2, final int npcstringId, final String... params) {
        this._params = null;
        this._objectId = player.getObjectId();
        this._type = type;
        this._countUp = countUp;
        this._startTime = startTime;
        this._startTime2 = startTime2;
        this._endTime = endTime;
        this._endTime2 = endTime2;
        this._npcstringId = npcstringId;
        this._params = Arrays.asList(params);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_SEND_UI_EVENT);
        this.writeInt(this._objectId);
        this.writeInt(this._type);
        this.writeInt(0);
        this.writeInt(0);
        this.writeString((CharSequence)String.valueOf(this._countUp));
        this.writeString((CharSequence)String.valueOf(this._startTime));
        this.writeString((CharSequence)String.valueOf(this._startTime2));
        this.writeString((CharSequence)String.valueOf(this._endTime));
        this.writeString((CharSequence)String.valueOf(this._endTime2));
        this.writeInt(this._npcstringId);
        if (this._params != null) {
            for (final String param : this._params) {
                this.writeString((CharSequence)param);
            }
        }
    }
    
    static {
        ExSendUIEvent.TYPE_COUNT_DOWN = 0;
        ExSendUIEvent.TYPE_REMOVE = 1;
        ExSendUIEvent.TYPE_ISTINA = 2;
        ExSendUIEvent.TYPE_COUNTER = 3;
        ExSendUIEvent.TYPE_GP_TIMER = 4;
        ExSendUIEvent.TYPE_NORNIL = 5;
        ExSendUIEvent.TYPE_DRACO_INCUBATION_1 = 6;
        ExSendUIEvent.TYPE_DRACO_INCUBATION_2 = 7;
        ExSendUIEvent.TYPE_CLAN_PROGRESS_BAR = 8;
    }
}
