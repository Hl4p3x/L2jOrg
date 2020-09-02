// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.events.ThePowerOfLove;

import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.quest.LongTimeEvent;

public final class ThePowerOfLove extends LongTimeEvent
{
    private static final int COCO = 33893;
    private static final int COCOGIFBOX = 36081;
    private static final int AMULETLOVE = 70232;
    private static final SkillHolder COCO_M;
    
    private ThePowerOfLove() {
        this.addStartNpc(33893);
        this.addFirstTalkId(33893);
        this.addTalkId(33893);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "33893-1.htm":
            case "33893-2.htm":
            case "33893-3.htm": {
                htmltext = event;
                break;
            }
            case "coco_giveItem": {
                if (!hasQuestItems(player, 36081)) {
                    giveItems(player, 36081, 1L);
                    htmltext = "33893-5.htm";
                    break;
                }
                htmltext = "33893-9.htm";
                break;
            }
            case "coco_takeAmulet": {
                if (hasQuestItems(player, 70232)) {
                    SkillCaster.triggerCast((Creature)npc, (Creature)player, ThePowerOfLove.COCO_M.getSkill());
                    htmltext = "33893-4.htm";
                    takeItems(player, 70232, 1L);
                    break;
                }
                htmltext = "33893-9.htm";
                break;
            }
        }
        return htmltext;
    }
    
    public String onFirstTalk(final Npc npc, final Player player) {
        return invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
    }
    
    public static AbstractScript provider() {
        return (AbstractScript)new ThePowerOfLove();
    }
    
    static {
        COCO_M = new SkillHolder(55327, 1);
    }
}
