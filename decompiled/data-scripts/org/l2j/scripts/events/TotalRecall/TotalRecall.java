// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.TotalRecall;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public final class TotalRecall extends LongTimeEvent
{
    private static final int FROG = 9013;
    private static final SkillHolder FROG_KISS;
    
    private TotalRecall() {
        this.addStartNpc(9013);
        this.addFirstTalkId(9013);
        this.addTalkId(9013);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "9013-1.htm":
            case "9013-2.htm":
            case "9013-3.htm": {
                htmltext = event;
                break;
            }
            case "frog_buff": {
                SkillCaster.triggerCast((Creature)npc, (Creature)player, TotalRecall.FROG_KISS.getSkill());
                htmltext = "9013-4.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return "9013-1.htm";
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new TotalRecall();
    }
    
    static {
        FROG_KISS = new SkillHolder(55314, 1);
    }
}
