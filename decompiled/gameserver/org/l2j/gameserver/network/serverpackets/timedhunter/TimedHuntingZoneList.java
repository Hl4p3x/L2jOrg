// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets.timedhunter;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class TimedHuntingZoneList extends ServerPacket
{
    @Override
    protected void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_TIME_RESTRICT_FIELD_LIST);
        final List<TimeRestrictedFieldInfo> infos = new ArrayList<TimeRestrictedFieldInfo>();
        this.addField(infos);
        this.writeInt(infos.size());
        for (final TimeRestrictedFieldInfo info : infos) {
            this.writeInt(info.requiredItems.size());
            for (final FieldRequiredItem item : info.requiredItems) {
                this.writeInt(item.itemId);
                this.writeLong(item.count);
            }
            this.writeInt(info.resetCycle);
            this.writeInt(info.fieldId);
            this.writeInt(info.minLevel);
            this.writeInt(info.maxLevel);
            this.writeInt(info.remainTimeBase);
            this.writeInt(info.remainTime);
            this.writeInt(info.remainTimeMax);
            this.writeInt(info.remainRefillTime);
            this.writeInt(info.refillTimeMax);
            this.writeByte(info.fieldActivated);
        }
    }
    
    private void addField(final List<TimeRestrictedFieldInfo> infos) {
        final TimeRestrictedFieldInfo field = new TimeRestrictedFieldInfo();
        field.resetCycle = 1;
        field.fieldId = 2;
        field.minLevel = 78;
        field.maxLevel = 999;
        field.remainTimeBase = 3600;
        field.remainTime = 3600;
        field.remainTimeMax = 21600;
        field.remainRefillTime = 18000;
        field.refillTimeMax = 18000;
        field.fieldActivated = true;
        final FieldRequiredItem item = new FieldRequiredItem();
        item.itemId = 57;
        item.count = 10000L;
        field.requiredItems = List.of(item);
        infos.add(field);
    }
    
    static class TimeRestrictedFieldInfo
    {
        List<FieldRequiredItem> requiredItems;
        int resetCycle;
        int fieldId;
        int minLevel;
        int maxLevel;
        int remainTimeBase;
        int remainTime;
        int remainTimeMax;
        int remainRefillTime;
        int refillTimeMax;
        boolean fieldActivated;
    }
    
    static class FieldRequiredItem
    {
        int itemId;
        long count;
    }
}
