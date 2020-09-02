// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.entity;

import java.time.LocalDateTime;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Set;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.data.database.data.SiegeClanData;

public interface Siegable
{
    void startSiege();
    
    void endSiege();
    
    SiegeClanData getAttackerClan(final int clanId);
    
    SiegeClanData getAttackerClan(final Clan clan);
    
    IntMap<SiegeClanData> getAttackerClans();
    
    boolean checkIsAttacker(final Clan clan);
    
    SiegeClanData getDefenderClan(final int clanId);
    
    SiegeClanData getDefenderClan(final Clan clan);
    
    IntMap<SiegeClanData> getDefenderClans();
    
    boolean checkIsDefender(final Clan clan);
    
    Set<Npc> getFlag(final Clan clan);
    
    LocalDateTime getSiegeDate();
    
    boolean giveFame();
    
    int getFameFrequency();
    
    int getFameAmount();
    
    void updateSiege();
}
