// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import io.github.joealisson.mmocore.Client;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.enums.InventorySlot;
import org.slf4j.Logger;
import org.l2j.gameserver.network.GameClient;
import io.github.joealisson.mmocore.WritablePacket;

public abstract class ServerPacket extends WritablePacket<GameClient>
{
    private static final Logger LOGGER;
    private final InventorySlot[] PAPERDOLL_ORDER_AUGMENT;
    
    public ServerPacket() {
        this.PAPERDOLL_ORDER_AUGMENT = new InventorySlot[] { InventorySlot.RIGHT_HAND, InventorySlot.LEFT_HAND, InventorySlot.TWO_HAND };
    }
    
    public InventorySlot[] getPaperdollOrder() {
        return InventorySlot.cachedValues();
    }
    
    public InventorySlot[] getPaperdollOrderAugument() {
        return this.PAPERDOLL_ORDER_AUGMENT;
    }
    
    public void sendTo(final Player player) {
        player.sendPacket(this);
    }
    
    protected boolean write(final GameClient client) {
        try {
            this.writeImpl(client);
            return true;
        }
        catch (Exception e) {
            ServerPacket.LOGGER.error("[{}] Error writing packet {} to client {}", new Object[] { GameServer.fullVersion, this, client });
            ServerPacket.LOGGER.error(e.getLocalizedMessage(), (Throwable)e);
            return false;
        }
    }
    
    public void runImpl(final Player player) {
    }
    
    public void writeOptionalD(final int value) {
        if (value >= 32767) {
            this.writeShort((short)32767);
            this.writeInt(value);
        }
        else {
            this.writeShort(value);
        }
    }
    
    protected void writeId(final ServerPacketId packet) {
        this.writeByte(packet.getId());
    }
    
    protected void writeId(final ServerExPacketId exPacket) {
        this.writeByte(254);
        this.writeShort(exPacket.getId());
    }
    
    protected abstract void writeImpl(final GameClient client) throws Exception;
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ServerPacket.class);
    }
}
