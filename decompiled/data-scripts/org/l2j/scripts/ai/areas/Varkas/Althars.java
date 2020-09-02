// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.areas.Varkas;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.data.xml.impl.SpawnsData;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import java.util.Arrays;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.scripts.ai.AbstractNpcAI;

public class Althars extends AbstractNpcAI
{
    private static Logger LOGGER;
    private final int _DELAY = 30000;
    private final int altharsID = 18926;
    private boolean[] althars_state;
    private Map<String, Npc> althars;
    private int[] ALTHARS_1_LOCATION;
    private int[] ALTHARS_2_LOCATION;
    private int[] ALTHARS_3_LOCATION;
    private int[] ALTHARS_4_LOCATION;
    private int[] ALTHARS_5_LOCATION;
    private int[] ALTHARS_6_LOCATION;
    private int[] ALTHARS_7_LOCATION;
    private int[] ALTHARS_8_LOCATION;
    private int[] ALTHARS_9_LOCATION;
    private int[] ALTHARS_10_LOCATION;
    private int[] ALTHARS_11_LOCATION;
    private int[] ALTHARS_12_LOCATION;
    
    private Althars() {
        this.althars_state = new boolean[12];
        this.althars = new HashMap<String, Npc>();
        this.ALTHARS_1_LOCATION = new int[] { 124809, -43595, 3221, 0 };
        this.ALTHARS_2_LOCATION = new int[] { 119008, -42127, -3213, 52282 };
        this.ALTHARS_3_LOCATION = new int[] { 123355, -47015, -2867, 11547 };
        this.ALTHARS_4_LOCATION = new int[] { 119906, -45557, -2805, 53685 };
        this.ALTHARS_5_LOCATION = new int[] { 124543, -52061, -2456, 47971 };
        this.ALTHARS_6_LOCATION = new int[] { 121611, -57169, -2174, 30437 };
        this.ALTHARS_7_LOCATION = new int[] { 114120, -46272, -2582, 2425 };
        this.ALTHARS_8_LOCATION = new int[] { 106936, -47288, -1888, 0 };
        this.ALTHARS_9_LOCATION = new int[] { 112536, -55496, -2832, 0 };
        this.ALTHARS_10_LOCATION = new int[] { 115256, -39144, -2488, 43325 };
        this.ALTHARS_11_LOCATION = new int[] { 105736, -41768, -1776, 0 };
        this.ALTHARS_12_LOCATION = new int[] { 109176, -36024, -896, 0 };
        for (int i = 0; i < this.althars_state.length; ++i) {
            this.althars_state[i] = false;
        }
        this.althars.put("althar_1", addSpawn(18926, this.ALTHARS_1_LOCATION[0], this.ALTHARS_1_LOCATION[1], this.ALTHARS_1_LOCATION[2], this.ALTHARS_1_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_2", addSpawn(18926, this.ALTHARS_2_LOCATION[0], this.ALTHARS_2_LOCATION[1], this.ALTHARS_2_LOCATION[2], this.ALTHARS_2_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_3", addSpawn(18926, this.ALTHARS_3_LOCATION[0], this.ALTHARS_3_LOCATION[1], this.ALTHARS_3_LOCATION[2], this.ALTHARS_3_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_4", addSpawn(18926, this.ALTHARS_4_LOCATION[0], this.ALTHARS_4_LOCATION[1], this.ALTHARS_4_LOCATION[2], this.ALTHARS_4_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_5", addSpawn(18926, this.ALTHARS_5_LOCATION[0], this.ALTHARS_5_LOCATION[1], this.ALTHARS_5_LOCATION[2], this.ALTHARS_5_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_6", addSpawn(18926, this.ALTHARS_6_LOCATION[0], this.ALTHARS_6_LOCATION[1], this.ALTHARS_6_LOCATION[2], this.ALTHARS_6_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_7", addSpawn(18926, this.ALTHARS_7_LOCATION[0], this.ALTHARS_7_LOCATION[1], this.ALTHARS_7_LOCATION[2], this.ALTHARS_7_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_8", addSpawn(18926, this.ALTHARS_8_LOCATION[0], this.ALTHARS_8_LOCATION[1], this.ALTHARS_8_LOCATION[2], this.ALTHARS_8_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_9", addSpawn(18926, this.ALTHARS_9_LOCATION[0], this.ALTHARS_9_LOCATION[1], this.ALTHARS_9_LOCATION[2], this.ALTHARS_9_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_10", addSpawn(18926, this.ALTHARS_10_LOCATION[0], this.ALTHARS_10_LOCATION[1], this.ALTHARS_10_LOCATION[2], this.ALTHARS_10_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_11", addSpawn(18926, this.ALTHARS_11_LOCATION[0], this.ALTHARS_11_LOCATION[1], this.ALTHARS_11_LOCATION[2], this.ALTHARS_11_LOCATION[3], false, 0L, false, 0));
        this.althars.put("althar_12", addSpawn(18926, this.ALTHARS_12_LOCATION[0], this.ALTHARS_12_LOCATION[1], this.ALTHARS_12_LOCATION[2], this.ALTHARS_12_LOCATION[3], false, 0L, false, 0));
        this.startQuestTimer("ALTHARS_TIMER", 30000L, (Npc)null, (Player)null);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if ("ALTHARS_TIMER".equals(event)) {
            for (int i = 0; i < Config.ALTHARS_MAX_ACTIVE - this.getAltharsActiveCount(); ++i) {
                if (Rnd.get(100) < Config.ALTHARS_ACTIVATE_CHANCE_RATE) {
                    final int altharsRndIndex = Rnd.get(12 - this.getAltharsActiveCount());
                    final int altharsIndex = this.getAltharsIndex(altharsRndIndex);
                    this.spawnMonsters(altharsIndex);
                    final int AltharsDurationCycle = Rnd.get(Config.ALTHARS_MIN_DURATION_CYCLE, Config.ALTHARS_MAX_DURATION_CYCLE);
                    this.startQuestTimer(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, altharsIndex + 1), (long)AltharsDurationCycle, (Npc)null, (Player)null);
                }
            }
            this.startQuestTimer("ALTHARS_TIMER", 30000L, (Npc)null, (Player)null);
        }
        else if (event.startsWith("STOP_ALTHARS_")) {
            final String[] token = event.split("_");
            this.unSpawnMonsters(Integer.parseInt(token[2]) - 1);
        }
        return super.onAdvEvent(event, npc, player);
    }
    
    private int getAltharsActiveCount() {
        int count = 0;
        for (int i = 0; i < this.althars_state.length; ++i) {
            if (this.althars_state[i]) {
                ++count;
            }
        }
        return count;
    }
    
    private int getAltharsIndex(final int rndIndex) {
        int virtualIndex = -1;
        for (int realIndex = 0; realIndex < this.althars_state.length; ++realIndex) {
            if (!this.althars_state[realIndex]) {
                ++virtualIndex;
                if (rndIndex == virtualIndex) {
                    return realIndex;
                }
            }
        }
        Althars.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Arrays.toString(this.althars_state)));
        return -1;
    }
    
    private void spawnMonsters(final int altharIndex) {
        this.althars.get(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, altharIndex + 1)).getEffectList().startAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.INVINCIBILITY });
        SpawnsData.getInstance().spawnByName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, altharIndex + 1));
        this.althars_state[altharIndex] = true;
    }
    
    private void unSpawnMonsters(final int altharIndex) {
        this.althars.get(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, altharIndex + 1)).getEffectList().stopAbnormalVisualEffect(new AbnormalVisualEffect[] { AbnormalVisualEffect.INVINCIBILITY });
        SpawnsData.getInstance().deSpawnByName(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, altharIndex + 1));
        this.althars_state[altharIndex] = false;
    }
    
    public static AbstractNpcAI provider() {
        return new Althars();
    }
    
    static {
        Althars.LOGGER = LoggerFactory.getLogger((Class)Althars.class);
    }
}
