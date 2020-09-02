// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DungeonOfAbyss.Tores;

import java.util.HashMap;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import java.util.Map;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Tores extends AbstractNpcAI
{
    private static final int TORES = 31778;
    private static final Map<String, Location> LOCATIONS;
    
    private Tores() {
        this.addStartNpc(31778);
        this.addTalkId(31778);
        this.addFirstTalkId(31778);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "31778.htm";
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        switch (event) {
            case "1": {
                final Location loc = Tores.LOCATIONS.get(event);
                if (player.getLevel() > 39 && player.getLevel() < 45) {
                    player.teleToLocation((ILocational)loc, true);
                    break;
                }
                return "31778-no_level.htm";
            }
            case "2": {
                final Location loc = Tores.LOCATIONS.get(event);
                if (player.getLevel() > 44 && player.getLevel() < 50) {
                    player.teleToLocation((ILocational)loc, true);
                    break;
                }
                return "31778-no_level01.htm";
            }
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    public static AbstractNpcAI provider() {
        return new Tores();
    }
    
    static {
        (LOCATIONS = new HashMap<String, Location>()).put("1", new Location(-120325, -182444, -6752));
        Tores.LOCATIONS.put("2", new Location(-109202, -180546, -6751));
    }
}
