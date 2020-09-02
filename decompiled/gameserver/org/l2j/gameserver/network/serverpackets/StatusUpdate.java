// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import java.util.Iterator;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.util.GameUtils;
import java.util.EnumMap;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.enums.StatusUpdateType;
import java.util.Map;

public final class StatusUpdate extends ServerPacket
{
    private final int objectId;
    private final boolean isPlayable;
    private final Map<StatusUpdateType, Integer> updates;
    private int casterObjectId;
    private boolean isVisible;
    
    public StatusUpdate(final WorldObject object) {
        this.updates = new EnumMap<StatusUpdateType, Integer>(StatusUpdateType.class);
        this.casterObjectId = 0;
        this.isVisible = false;
        this.objectId = object.getObjectId();
        this.isPlayable = GameUtils.isPlayable(object);
    }
    
    public StatusUpdate addUpdate(final StatusUpdateType type, final int level) {
        this.updates.put(type, level);
        if (this.isPlayable) {
            boolean isVisible = false;
            switch (type) {
                case CUR_HP:
                case CUR_MP:
                case CUR_CP: {
                    isVisible = true;
                    break;
                }
                default: {
                    isVisible = false;
                    break;
                }
            }
            this.isVisible = isVisible;
        }
        return this;
    }
    
    public void addCaster(final WorldObject object) {
        this.casterObjectId = object.getObjectId();
    }
    
    public boolean hasUpdates() {
        return !this.updates.isEmpty();
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerPacketId.STATUS_UPDATE);
        this.writeInt(this.objectId);
        this.writeInt(this.isVisible ? this.casterObjectId : 0);
        this.writeByte(this.isVisible);
        this.writeByte(this.updates.size());
        for (final Map.Entry<StatusUpdateType, Integer> entry : this.updates.entrySet()) {
            this.writeByte(entry.getKey().getClientId());
            this.writeInt((int)entry.getValue());
        }
    }
    
    public static StatusUpdate of(final WorldObject object, final StatusUpdateType type, final int value) {
        return new StatusUpdate(object).addUpdate(type, value);
    }
}
