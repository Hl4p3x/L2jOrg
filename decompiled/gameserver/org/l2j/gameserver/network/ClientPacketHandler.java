// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import org.slf4j.LoggerFactory;
import io.github.joealisson.mmocore.Client;
import org.l2j.commons.util.CommonUtil;
import java.util.Objects;
import io.github.joealisson.mmocore.ReadablePacket;
import io.github.joealisson.mmocore.ReadableBuffer;
import org.slf4j.Logger;
import io.github.joealisson.mmocore.PacketHandler;

public class ClientPacketHandler implements PacketHandler<GameClient>
{
    private static final Logger LOGGER;
    private static final Logger ST_PACKET;
    
    public ReadablePacket<GameClient> handlePacket(final ReadableBuffer buffer, final GameClient client) {
        ClientPacketHandler.ST_PACKET.debug("{}", (Object)buffer.remaining());
        final int opcode = Byte.toUnsignedInt(buffer.readByte());
        if (opcode >= IncomingPackets.PACKET_ARRAY.length) {
            this.unknownPacket(buffer, opcode, null);
            return null;
        }
        final PacketFactory packetFactory = this.getPacketFactory(opcode, buffer);
        return this.makePacketWithFactory(buffer, client, opcode, packetFactory);
    }
    
    private ReadablePacket<GameClient> makePacketWithFactory(final ReadableBuffer buffer, final GameClient client, final int opcode, final PacketFactory packetFactory) {
        final ReadablePacket<GameClient> packet;
        if (Objects.isNull(packetFactory) || Objects.isNull(packet = packetFactory.newIncomingPacket())) {
            this.unknownPacket(buffer, opcode, packetFactory);
            return null;
        }
        if (packet instanceof DiscardPacket) {
            return null;
        }
        final ConnectionState connectionState = client.getConnectionState();
        if (!packetFactory.canHandleState(client.getConnectionState())) {
            final byte[] data = new byte[buffer.remaining()];
            buffer.readBytes(data);
            ClientPacketHandler.LOGGER.warn("Client {} sent packet {} at invalid state {} Required States: {} - [{}]: {}", new Object[] { client, packetFactory, connectionState, packetFactory.getConnectionStates(), Integer.toHexString(opcode), CommonUtil.printData(data) });
            return null;
        }
        if (ConnectionState.JOINING_GAME_AND_IN_GAME.contains(connectionState) && Objects.isNull(client.getPlayer())) {
            ClientPacketHandler.LOGGER.warn("Client {} sent IN_GAME packet {} without a player", (Object)client, (Object)packetFactory);
            return null;
        }
        return packet;
    }
    
    private void unknownPacket(final ReadableBuffer buffer, final int opcode, final PacketFactory packetFactory) {
        final byte[] data = new byte[buffer.remaining()];
        buffer.readBytes(data);
        ClientPacketHandler.LOGGER.debug("Unknown Packet ({}) : {} - {}", new Object[] { packetFactory, Integer.toHexString(opcode), CommonUtil.printData(data) });
    }
    
    private PacketFactory getPacketFactory(final int opcode, final ReadableBuffer buffer) {
        final IncomingPackets packetFactory = IncomingPackets.PACKET_ARRAY[opcode];
        if (Objects.nonNull(packetFactory) && packetFactory.hasExtension()) {
            return packetFactory.handleExtension(buffer);
        }
        return packetFactory;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)ClientPacketHandler.class);
        ST_PACKET = LoggerFactory.getLogger("ST_PACKET");
    }
}
