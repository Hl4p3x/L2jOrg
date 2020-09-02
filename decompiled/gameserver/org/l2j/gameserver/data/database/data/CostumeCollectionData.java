// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import java.time.temporal.TemporalAmount;
import java.time.Duration;
import java.time.Instant;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("player_costume_collection")
public class CostumeCollectionData
{
    public static CostumeCollectionData DEFAULT;
    @Column("player_id")
    private int playerId;
    private int id;
    private int reuse;
    
    public static CostumeCollectionData of(final Player player, final int id) {
        final CostumeCollectionData collection = new CostumeCollectionData();
        collection.playerId = player.getObjectId();
        collection.id = id;
        return collection;
    }
    
    public int getId() {
        return this.id;
    }
    
    public void updateReuseTime() {
        this.reuse = (int)Instant.now().plus((TemporalAmount)Duration.ofMinutes(10L)).getEpochSecond();
    }
    
    public int getReuseTime() {
        return (int)Math.max(0L, this.reuse - Instant.now().getEpochSecond());
    }
    
    static {
        CostumeCollectionData.DEFAULT = new CostumeCollectionData();
    }
}
