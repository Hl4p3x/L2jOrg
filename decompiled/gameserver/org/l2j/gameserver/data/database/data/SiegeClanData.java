// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.gameserver.model.actor.instance.SiegeFlag;
import java.util.function.Consumer;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.l2j.gameserver.enums.SiegeClanType;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Set;
import org.l2j.commons.database.annotation.Table;

@Table("siege_clans")
public class SiegeClanData
{
    @NonUpdatable
    private final Set<Npc> flags;
    @Column("castle_id")
    private int castleId;
    @Column("clan_id")
    private int clanId;
    private SiegeClanType type;
    
    public SiegeClanData() {
        this.flags = (Set<Npc>)ConcurrentHashMap.newKeySet();
    }
    
    public SiegeClanData(final int id, final SiegeClanType type, final int castleId) {
        this.flags = (Set<Npc>)ConcurrentHashMap.newKeySet();
        this.clanId = id;
        this.type = type;
        this.castleId = castleId;
    }
    
    public int getCastleId() {
        return this.castleId;
    }
    
    public int getClanId() {
        return this.clanId;
    }
    
    public SiegeClanType getType() {
        return this.type;
    }
    
    public void setType(final SiegeClanType type) {
        this.type = type;
    }
    
    public Set<Npc> getFlags() {
        return this.flags;
    }
    
    public boolean removeFlag(final Npc flag) {
        if (Objects.isNull(flag)) {
            return false;
        }
        flag.deleteMe();
        return this.flags.remove(flag);
    }
    
    public void removeFlags() {
        this.flags.forEach(this::removeFlag);
    }
    
    public void addFlag(final SiegeFlag siegeFlag) {
        this.flags.add(siegeFlag);
    }
    
    public int getNumFlags() {
        return this.flags.size();
    }
}
