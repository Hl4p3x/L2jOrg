// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.SymbolMaker;

import org.l2j.gameserver.network.serverpackets.HennaRemoveList;
import org.l2j.gameserver.network.serverpackets.HennaEquipList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class SymbolMaker extends AbstractNpcAI
{
    private static final int[] NPCS;
    
    private SymbolMaker() {
        this.addFirstTalkId(SymbolMaker.NPCS);
        this.addStartNpc(SymbolMaker.NPCS);
        this.addTalkId(SymbolMaker.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        String htmltext = null;
        switch (event) {
            case "symbol_maker.htm":
            case "symbol_maker-1.htm":
            case "symbol_maker-2.htm":
            case "symbol_maker-3.htm": {
                htmltext = event;
                break;
            }
            case "Draw": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new HennaEquipList(player) });
                break;
            }
            case "Remove": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new HennaRemoveList(player) });
                break;
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        return "symbol_maker.htm";
    }
    
    public static AbstractNpcAI provider() {
        return new SymbolMaker();
    }
    
    static {
        NPCS = new int[] { 31046, 31047, 31048, 31049, 31050, 31051, 31052, 31053, 31264 };
    }
}
