// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Creature;
import java.util.HashMap;
import org.l2j.gameserver.enums.Race;
import java.util.Map;

public class RespawnZone extends SpawnZone
{
    private final Map<Race, String> raceRespawnPoint;
    
    public RespawnZone(final int id) {
        super(id);
        this.raceRespawnPoint = new HashMap<Race, String>();
    }
    
    @Override
    protected void onEnter(final Creature creature) {
    }
    
    @Override
    protected void onExit(final Creature creature) {
    }
    
    public void addRaceRespawnPoint(final String race, final String point) {
        this.raceRespawnPoint.put(Race.valueOf(race), point);
    }
    
    public Map<Race, String> getAllRespawnPoints() {
        return this.raceRespawnPoint;
    }
    
    public String getRespawnPoint(final Player player) {
        return this.raceRespawnPoint.get(player.getRace());
    }
}
