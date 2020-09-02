// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.ForestOfTheMirrors;

import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;
import java.util.Iterator;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.interfaces.IPositionable;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Mirrors extends AbstractNpcAI
{
    private static final Logger LOGGER;
    private static final int MIRROR_NPC_ID = 20639;
    private static final int DESPAWN_TIME = 600000;
    private static final int MIRROR_COUNT = 4;
    private static Map<Integer, Integer> _Leaders_Stages;
    private static Map<Integer, List<Integer>> _Leader_Minions;
    private static Map<Integer, Boolean> _Minions_State;
    
    public Mirrors() {
        this.addKillId(20639);
    }
    
    public String onKill(final Npc npc, final Player killer, final boolean isSummon) {
        if (npc.getId() == 20639) {
            int leaderObjectId = this.getLeader(npc.getObjectId());
            if (leaderObjectId == -1) {
                leaderObjectId = npc.getObjectId();
                Mirrors._Leaders_Stages.put(leaderObjectId, 0);
                Mirrors._Leader_Minions.put(leaderObjectId, new ArrayList<Integer>());
            }
            else {
                Mirrors._Minions_State.put(npc.getObjectId(), false);
            }
            if (this.getAliveMinions(leaderObjectId) % 4 == 0) {
                switch (Mirrors._Leaders_Stages.get(leaderObjectId)) {
                    case 0:
                    case 1:
                    case 2:
                    case 3: {
                        for (int i = 0; i < 4; ++i) {
                            final Npc mirror = addSpawn(20639, (IPositionable)npc, true, 600000L);
                            Mirrors._Leader_Minions.get(leaderObjectId).add(mirror.getObjectId());
                            Mirrors._Minions_State.put(mirror.getObjectId(), true);
                            this.addAttackPlayerDesire(mirror, (Playable)killer);
                        }
                        Mirrors._Leaders_Stages.replace(leaderObjectId, Mirrors._Leaders_Stages.get(leaderObjectId) + 1);
                        break;
                    }
                    case 4: {
                        for (final int minion : Mirrors._Leader_Minions.get(leaderObjectId)) {
                            Mirrors._Minions_State.remove(minion);
                        }
                        Mirrors._Leader_Minions.remove(leaderObjectId);
                        Mirrors._Leaders_Stages.remove(leaderObjectId);
                        break;
                    }
                }
            }
        }
        return super.onKill(npc, killer, isSummon);
    }
    
    private int getLeader(final int npcObjectId) {
        for (final Map.Entry<Integer, List<Integer>> leader : Mirrors._Leader_Minions.entrySet()) {
            if (leader.getValue().contains(npcObjectId)) {
                return leader.getKey();
            }
        }
        return -1;
    }
    
    private int getAliveMinions(final int leaderObjectId) {
        int count = 0;
        for (final int minion : Mirrors._Leader_Minions.get(leaderObjectId)) {
            if (Mirrors._Minions_State.get(minion)) {
                ++count;
            }
        }
        return count;
    }
    
    public static AbstractNpcAI provider() {
        return new Mirrors();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Mirrors.class);
        Mirrors._Leaders_Stages = new ConcurrentHashMap<Integer, Integer>();
        Mirrors._Leader_Minions = new ConcurrentHashMap<Integer, List<Integer>>();
        Mirrors._Minions_State = new ConcurrentHashMap<Integer, Boolean>();
    }
}
