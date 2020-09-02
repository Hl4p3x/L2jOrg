// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.OlyBuffer;

import org.l2j.gameserver.model.events.timers.TimerHolder;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class OlyBuffer extends AbstractNpcAI
{
    private static final int OLYMPIAD_BUFFER = 36402;
    private static final SkillHolder[] ALLOWED_BUFFS;
    
    private OlyBuffer() {
        this.addStartNpc(36402);
        this.addFirstTalkId(36402);
        this.addTalkId(36402);
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        if (npc.getScriptValue() < 5) {
            htmltext = "OlyBuffer-index.html";
        }
        return htmltext;
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        if (event.startsWith("giveBuff;") && npc.getScriptValue() < 5) {
            final int buffId = Integer.parseInt(event.replace("giveBuff;", ""));
            if (OlyBuffer.ALLOWED_BUFFS[buffId] != null) {
                npc.setScriptValue(npc.getScriptValue() + 1);
                SkillCaster.triggerCast((Creature)npc, (Creature)player, OlyBuffer.ALLOWED_BUFFS[buffId].getSkill());
                htmltext = "OlyBuffer-afterBuff.html";
            }
            if (npc.getScriptValue() >= 5) {
                htmltext = "OlyBuffer-noMore.html";
                this.getTimers().addTimer((Object)"DELETE_ME", 5000L, evnt -> npc.deleteMe());
            }
        }
        return htmltext;
    }
    
    public static AbstractNpcAI provider() {
        return new OlyBuffer();
    }
    
    static {
        ALLOWED_BUFFS = new SkillHolder[] { new SkillHolder(1086, 1), new SkillHolder(1085, 1), new SkillHolder(1204, 1), new SkillHolder(1068, 1), new SkillHolder(1040, 1), new SkillHolder(1036, 1), new SkillHolder(1045, 1), new SkillHolder(1048, 1), new SkillHolder(1062, 1) };
    }
}
