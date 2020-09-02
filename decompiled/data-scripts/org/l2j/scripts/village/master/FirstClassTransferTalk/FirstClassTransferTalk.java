// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.FirstClassTransferTalk;

import io.github.joealisson.primitive.HashIntMap;
import org.l2j.gameserver.model.actor.instance.VillageMasterFighter;
import org.l2j.gameserver.model.actor.instance.VillageMasterPriest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.enums.Race;
import io.github.joealisson.primitive.IntMap;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class FirstClassTransferTalk extends AbstractNpcAI
{
    private static final IntMap<Race> MASTERS;
    
    private FirstClassTransferTalk() {
        this.addStartNpc((IntCollection)FirstClassTransferTalk.MASTERS.keySet());
        this.addTalkId((IntCollection)FirstClassTransferTalk.MASTERS.keySet());
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        return event;
    }
    
    public String onTalk(final Npc npc, final Player player) {
        String htmltext = invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npc.getId());
        if (FirstClassTransferTalk.MASTERS.get(npc.getId()) != player.getRace()) {
            return htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
        }
        switch ((Race)FirstClassTransferTalk.MASTERS.get(npc.getId())) {
            case HUMAN: {
                if (player.getClassId().level() == 0) {
                    if (player.isMageClass()) {
                        if (npc instanceof VillageMasterPriest) {
                            htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                            break;
                        }
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                        break;
                    }
                    else {
                        if (npc instanceof VillageMasterFighter) {
                            htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                            break;
                        }
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                        break;
                    }
                }
                else {
                    if (player.getClassId().level() == 1) {
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                        break;
                    }
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                    break;
                }
                break;
            }
            case ELF:
            case DARK_ELF:
            case ORC: {
                if (player.getClassId().level() == 0) {
                    if (player.isMageClass()) {
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                        break;
                    }
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                    break;
                }
                else {
                    if (player.getClassId().level() == 1) {
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                        break;
                    }
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                    break;
                }
                break;
            }
            case DWARF: {
                if (player.getClassId().level() == 0) {
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                    break;
                }
                if (player.getClassId().level() == 1) {
                    htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                    break;
                }
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                break;
            }
            default: {
                htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, htmltext);
                break;
            }
        }
        return htmltext;
    }
    
    public static FirstClassTransferTalk provider() {
        return new FirstClassTransferTalk();
    }
    
    static {
        (MASTERS = (IntMap)new HashIntMap()).put(30026, (Object)Race.HUMAN);
        FirstClassTransferTalk.MASTERS.put(30031, (Object)Race.HUMAN);
        FirstClassTransferTalk.MASTERS.put(30154, (Object)Race.ELF);
        FirstClassTransferTalk.MASTERS.put(30358, (Object)Race.DARK_ELF);
        FirstClassTransferTalk.MASTERS.put(30565, (Object)Race.ORC);
        FirstClassTransferTalk.MASTERS.put(30520, (Object)Race.DWARF);
        FirstClassTransferTalk.MASTERS.put(30525, (Object)Race.DWARF);
    }
}
