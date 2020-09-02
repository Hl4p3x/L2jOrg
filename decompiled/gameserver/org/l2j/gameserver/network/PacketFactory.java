// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import io.github.joealisson.mmocore.ReadableBuffer;
import java.util.EnumSet;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import java.util.function.Supplier;

interface PacketFactory
{
    public static final Supplier<ClientPacket> NULL_PACKET_SUPLIER = () -> null;
    public static final PacketFactory NULLABLE_PACKET_FACTORY = () -> null;
    public static final DiscardPacket DISCARD_PACKET = new DiscardPacket();
    public static final Supplier<ClientPacket> DISCARD = () -> PacketFactory.DISCARD_PACKET;
    
    default int getPacketId() {
        return -1;
    }
    
    ClientPacket newIncomingPacket();
    
    default EnumSet<ConnectionState> getConnectionStates() {
        return ConnectionState.EMPTY;
    }
    
    default boolean canHandleState(final ConnectionState state) {
        return false;
    }
    
    default boolean hasExtension() {
        return false;
    }
    
    default PacketFactory handleExtension(final ReadableBuffer buffer) {
        return PacketFactory.NULLABLE_PACKET_FACTORY;
    }
}
