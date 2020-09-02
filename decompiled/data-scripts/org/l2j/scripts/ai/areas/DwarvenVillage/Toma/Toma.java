// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.DwarvenVillage.Toma;

import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.Location;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Toma extends AbstractNpcAI
{
    private static final int TOMA = 30556;
    private static final Location[] LOCATIONS;
    private static final int TELEPORT_DELAY = 1800000;
    
    private Toma() {
        this.addFirstTalkId(30556);
        this.onAdvEvent("RESPAWN_TOMA", null, null);
        this.startQuestTimer("RESPAWN_TOMA", 1800000L, (Npc)null, (Player)null, true);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (event.equals("RESPAWN_TOMA")) {
            addSpawn(30556, (IPositionable)getRandomEntry((Object[])Toma.LOCATIONS), false, 1800000L);
        }
        return null;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "30556.htm";
    }
    
    public static AbstractNpcAI provider() {
        return new Toma();
    }
    
    static {
        LOCATIONS = new Location[] { new Location(151680, -174891, -1782), new Location(154153, -220105, -3402), new Location(178834, -184336, -355, 41400) };
    }
}
