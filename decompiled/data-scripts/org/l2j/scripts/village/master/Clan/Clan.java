// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.village.master.Clan;

import java.util.HashMap;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanLeft;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerClanJoin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import java.util.Map;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class Clan extends AbstractNpcAI
{
    private static final int[] NPCS;
    private static final Map<String, String> LEADER_REQUIRED;
    
    private Clan() {
        this.addStartNpc(Clan.NPCS);
        this.addTalkId(Clan.NPCS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        if (Clan.LEADER_REQUIRED.containsKey(event) && !player.isClanLeader()) {
            return Clan.LEADER_REQUIRED.get(event);
        }
        return event;
    }
    
    public String onTalk(final Npc npc, final Player talker) {
        return "9000-01.htm";
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerLogin(final OnPlayerLogin event) {
        final Player activeChar = event.getPlayer();
        if (activeChar.isClanLeader()) {
            final org.l2j.gameserver.model.Clan clan = event.getPlayer().getClan();
            clan.getMembers().forEach(member -> {
                if (member.isOnline()) {
                    CommonSkill.CLAN_ADVENT.getSkill().applyEffects((Creature)member.getPlayerInstance(), (Creature)member.getPlayerInstance());
                }
            });
        }
        else if (activeChar.getClan() != null && activeChar.getClan().getLeader().isOnline()) {
            CommonSkill.CLAN_ADVENT.getSkill().applyEffects((Creature)activeChar, (Creature)activeChar);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerLogout(final OnPlayerLogout event) {
        final Player activeChar = event.getActiveChar();
        if (activeChar.isClanLeader()) {
            final org.l2j.gameserver.model.Clan clan = activeChar.getClan();
            clan.getMembers().forEach(member -> {
                if (member.isOnline()) {
                    member.getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
                }
                return;
            });
        }
        if (activeChar.getClan() != null) {
            activeChar.getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_PROFESSION_CHANGE)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onProfessionChange(final OnPlayerProfessionChange event) {
        final Player activeChar = event.getActiveChar();
        if (activeChar.isClanLeader() || (activeChar.getClan() != null && activeChar.getClan().getLeader().isOnline())) {
            CommonSkill.CLAN_ADVENT.getSkill().applyEffects((Creature)activeChar, (Creature)activeChar);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_CLAN_JOIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerClanJoin(final OnPlayerClanJoin event) {
        final Player activeChar = event.getActiveChar().getPlayerInstance();
        if (activeChar.getClan().getLeader().isOnline()) {
            CommonSkill.CLAN_ADVENT.getSkill().applyEffects((Creature)activeChar, (Creature)activeChar);
        }
    }
    
    @RegisterEvent(EventType.ON_PLAYER_CLAN_LEFT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void onPlayerClanLeft(final OnPlayerClanLeft event) {
        event.getActiveChar().getPlayerInstance().getEffectList().stopSkillEffects(true, CommonSkill.CLAN_ADVENT.getId());
    }
    
    public static Clan provider() {
        return new Clan();
    }
    
    static {
        NPCS = new int[] { 30026, 30031, 30037, 30066, 30070, 30109, 30115, 30120, 30154, 30174, 30175, 30176, 30187, 30191, 30195, 30288, 30289, 30290, 30297, 30358, 30373, 30462, 30474, 30498, 30499, 30500, 30503, 30504, 30505, 30508, 30511, 30512, 30513, 30520, 30525, 30565, 30594, 30595, 30676, 30677, 30681, 30685, 30687, 30689, 30694, 30699, 30704, 30845, 30847, 30849, 30854, 30857, 30862, 30865, 30894, 30897, 30900, 30905, 30910, 30913 };
        (LEADER_REQUIRED = new HashMap<String, String>()).put("9000-03.htm", "9000-03-no.htm");
        Clan.LEADER_REQUIRED.put("9000-04.htm", "9000-04-no.htm");
        Clan.LEADER_REQUIRED.put("9000-05.htm", "9000-05-no.htm");
        Clan.LEADER_REQUIRED.put("9000-07.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-12a.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-12b.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-13a.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-13b.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-14a.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-14b.htm", "9000-07-no.htm");
        Clan.LEADER_REQUIRED.put("9000-15.htm", "9000-07-no.htm");
    }
}
