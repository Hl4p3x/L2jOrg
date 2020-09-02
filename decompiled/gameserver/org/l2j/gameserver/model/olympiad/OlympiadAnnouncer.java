// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.model.actor.Npc;
import java.util.Iterator;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.network.NpcStringId;

public final class OlympiadAnnouncer implements Runnable
{
    private static final int OLY_MANAGER = 31688;
    private int _currentStadium;
    
    public OlympiadAnnouncer() {
        this._currentStadium = 0;
    }
    
    @Override
    public void run() {
        int i = OlympiadGameManager.getInstance().getNumberOfStadiums();
        while (--i >= 0) {
            if (this._currentStadium >= OlympiadGameManager.getInstance().getNumberOfStadiums()) {
                this._currentStadium = 0;
            }
            final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(this._currentStadium);
            Label_0207: {
                if (task != null && task.getGame() != null && task.needAnnounce()) {
                    final String arenaId = String.valueOf(task.getGame().getStadiumId() + 1);
                    NpcStringId npcString = null;
                    switch (task.getGame().getType()) {
                        case NON_CLASSED: {
                            npcString = NpcStringId.OLYMPIAD_CLASS_FREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                            break;
                        }
                        case CLASSED: {
                            npcString = NpcStringId.OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
                            break;
                        }
                        default: {
                            break Label_0207;
                        }
                    }
                    for (final Spawn spawn : SpawnTable.getInstance().getSpawns(31688)) {
                        final Npc manager = spawn.getLastSpawn();
                        if (manager != null) {
                            manager.broadcastSay(ChatType.NPC_SHOUT, npcString, arenaId);
                        }
                    }
                    break;
                }
            }
            ++this._currentStadium;
        }
    }
}
