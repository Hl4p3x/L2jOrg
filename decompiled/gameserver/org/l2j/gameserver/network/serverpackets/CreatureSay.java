// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.ArrayList;
import org.l2j.gameserver.network.NpcStringId;
import java.util.Objects;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.List;
import org.l2j.gameserver.enums.ChatType;

public final class CreatureSay extends ServerPacket
{
    private final int objectId;
    private final ChatType type;
    private String senderName;
    private String message;
    private int npcString;
    private int _mask;
    private int charLevel;
    private List<String> npcStringParameters;
    private int rank;
    private int castleId;
    
    public CreatureSay(final Player sender, final Player receiver, final String name, final ChatType type, final String text) {
        this(sender, type, text);
        this.senderName = name;
        this.charLevel = sender.getLevel();
        this.rank = sender.getRank();
        this.castleId = Util.zeroIfNullOrElse((Object)sender.getClan(), Clan::getCastleId);
        if (Objects.nonNull(receiver)) {
            if (receiver.getFriendList().contains(sender.getObjectId())) {
                this._mask |= 0x1;
            }
            if (receiver.getClanId() > 0 && receiver.getClanId() == sender.getClanId()) {
                this._mask |= 0x2;
            }
            if (receiver.getAllyId() > 0 && receiver.getAllyId() == sender.getAllyId()) {
                this._mask |= 0x8;
            }
        }
        if (sender.isGM()) {
            this._mask |= 0x10;
        }
    }
    
    public CreatureSay(final Player player, final ChatType type, final String message) {
        this(player.getObjectId(), type, player.getAppearance().getVisibleName(), message);
        this.rank = player.getRank();
    }
    
    public CreatureSay(final int objectId, final ChatType type, final String senderName, final String text) {
        this.message = null;
        this.npcString = -1;
        this.charLevel = -1;
        this.objectId = objectId;
        this.type = type;
        this.senderName = senderName;
        this.message = text;
    }
    
    public CreatureSay(final int objectId, final ChatType messageType, final String charName, final NpcStringId npcString) {
        this.message = null;
        this.npcString = -1;
        this.charLevel = -1;
        this.objectId = objectId;
        this.type = messageType;
        this.senderName = charName;
        this.npcString = npcString.getId();
    }
    
    public void addStringParameter(final String text) {
        if (this.npcStringParameters == null) {
            this.npcStringParameters = new ArrayList<String>();
        }
        this.npcStringParameters.add(text);
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.SAY2);
        this.writeInt(this.objectId);
        this.writeInt(this.type.getClientId());
        this.writeString((CharSequence)this.senderName);
        this.writeInt(this.npcString);
        if (Objects.nonNull(this.message)) {
            this.writeString((CharSequence)this.message);
            if (this.charLevel > 0 && this.type == ChatType.WHISPER) {
                this.writeByte(this._mask);
                if ((this._mask & 0x10) == 0x0) {
                    this.writeByte(this.charLevel);
                }
            }
        }
        else if (Objects.nonNull(this.npcStringParameters)) {
            for (final String s : this.npcStringParameters) {
                this.writeString((CharSequence)s);
            }
        }
        this.writeByte(this.rank);
        this.writeByte(this.castleId);
        this.writeInt(0);
    }
    
    @Override
    public final void runImpl(final Player player) {
        if (player != null) {
            player.broadcastSnoop(this.type, this.senderName, this.message);
        }
    }
}
