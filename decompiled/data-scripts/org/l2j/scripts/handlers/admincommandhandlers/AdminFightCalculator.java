// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminFightCalculator implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        try {
            if (command.startsWith("admin_fight_calculator_show")) {
                this.handleShow(command.substring("admin_fight_calculator_show".length()), activeChar);
            }
            else if (command.startsWith("admin_fcs")) {
                this.handleShow(command.substring("admin_fcs".length()), activeChar);
            }
            else if (command.startsWith("admin_fight_calculator")) {
                this.handleStart(command.substring("admin_fight_calculator".length()), activeChar);
            }
        }
        catch (StringIndexOutOfBoundsException ex) {}
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminFightCalculator.ADMIN_COMMANDS;
    }
    
    private void handleStart(final String params, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(params);
        int lvl1 = 0;
        int lvl2 = 0;
        int mid1 = 0;
        int mid2 = 0;
        while (st.hasMoreTokens()) {
            final String s = st.nextToken();
            if (s.equals("lvl1")) {
                lvl1 = Integer.parseInt(st.nextToken());
            }
            else if (s.equals("lvl2")) {
                lvl2 = Integer.parseInt(st.nextToken());
            }
            else if (s.equals("mid1")) {
                mid1 = Integer.parseInt(st.nextToken());
            }
            else {
                if (!s.equals("mid2")) {
                    continue;
                }
                mid2 = Integer.parseInt(st.nextToken());
            }
        }
        NpcTemplate npc1 = null;
        if (mid1 != 0) {
            npc1 = NpcData.getInstance().getTemplate(mid1);
        }
        NpcTemplate npc2 = null;
        if (mid2 != 0) {
            npc2 = NpcData.getInstance().getTemplate(mid2);
        }
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        String replyMSG;
        if (npc1 != null && npc2 != null) {
            replyMSG = invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, lvl1, lvl2, npc1.getId(), npc2.getId(), npc1.getName(), npc2.getName(), npc1.getId(), npc2.getId());
        }
        else if (lvl1 != 0 && npc1 == null) {
            final List<NpcTemplate> npcs = (List<NpcTemplate>)NpcData.getInstance().getAllOfLevel(new int[] { lvl1 });
            final StringBuilder sb = new StringBuilder(50 + npcs.size() * 200);
            sb.append("<html><title>Select first mob to fight</title><body><table>");
            for (final NpcTemplate n : npcs) {
                sb.append(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, lvl1, lvl2, n.getId(), mid2, n.getName()));
            }
            sb.append("</table></body></html>");
            replyMSG = sb.toString();
        }
        else if (lvl2 != 0 && npc2 == null) {
            final List<NpcTemplate> npcs = (List<NpcTemplate>)NpcData.getInstance().getAllOfLevel(new int[] { lvl2 });
            final StringBuilder sb = new StringBuilder(50 + npcs.size() * 200);
            sb.append("<html><title>Select second mob to fight</title><body><table>");
            for (final NpcTemplate n : npcs) {
                sb.append(invokedynamic(makeConcatWithConstants:(IIIILjava/lang/String;)Ljava/lang/String;, lvl1, lvl2, mid1, n.getId(), n.getName()));
            }
            sb.append("</table></body></html>");
            replyMSG = sb.toString();
        }
        else {
            replyMSG = "<html><title>Select mobs to fight</title><body><table><tr><td>First</td><td>Second</td></tr><tr><td><edit var=\"lvl1\" width=80></td><td><edit var=\"lvl2\" width=80></td></tr></table><center><br><br><br><button value=\"OK\" action=\"bypass -h admin_fight_calculator lvl1 $lvl1 lvl2 $lvl2\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></center></body></html>";
        }
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
    }
    
    private void handleShow(String params, final Player activeChar) {
        params = params.trim();
        Creature npc1 = null;
        Creature npc2 = null;
        if (params.isEmpty()) {
            npc1 = (Creature)activeChar;
            npc2 = (Creature)activeChar.getTarget();
            if (npc2 == null) {
                activeChar.sendPacket(SystemMessageId.INVALID_TARGET);
                return;
            }
        }
        else {
            int mid1 = 0;
            int mid2 = 0;
            final StringTokenizer st = new StringTokenizer(params);
            mid1 = Integer.parseInt(st.nextToken());
            mid2 = Integer.parseInt(st.nextToken());
            npc1 = (Creature)new Monster(NpcData.getInstance().getTemplate(mid1));
            npc2 = (Creature)new Monster(NpcData.getInstance().getTemplate(mid2));
        }
        int miss1 = 0;
        int miss2 = 0;
        int shld1 = 0;
        int shld2 = 0;
        int crit1 = 0;
        int crit2 = 0;
        double patk1 = 0.0;
        double patk2 = 0.0;
        double pdef1 = 0.0;
        double pdef2 = 0.0;
        double dmg1 = 0.0;
        double dmg2 = 0.0;
        int sAtk1 = Formulas.calculateTimeBetweenAttacks(npc1.getPAtkSpd());
        int sAtk2 = Formulas.calculateTimeBetweenAttacks(npc2.getPAtkSpd());
        sAtk1 = 100000 / sAtk1;
        sAtk2 = 100000 / sAtk2;
        for (int i = 0; i < 10000; ++i) {
            final boolean _miss1 = Formulas.calcHitMiss(npc1, npc2);
            if (_miss1) {
                ++miss1;
            }
            final byte _shld1 = Formulas.calcShldUse(npc1, npc2, false);
            if (_shld1 > 0) {
                ++shld1;
            }
            final boolean _crit1 = Formulas.calcCrit((double)npc1.getCriticalHit(), npc1, npc2, (Skill)null);
            if (_crit1) {
                ++crit1;
            }
            double _patk1 = npc1.getPAtk();
            _patk1 += npc1.getRandomDamageMultiplier();
            patk1 += _patk1;
            final double _pdef1 = npc1.getPDef();
            pdef1 += _pdef1;
            if (!_miss1) {
                final double _dmg1 = Formulas.calcAutoAttackDamage(npc1, npc2, _shld1, _crit1, false);
                dmg1 += _dmg1;
                npc1.abortAttack();
            }
        }
        for (int i = 0; i < 10000; ++i) {
            final boolean _miss2 = Formulas.calcHitMiss(npc2, npc1);
            if (_miss2) {
                ++miss2;
            }
            final byte _shld2 = Formulas.calcShldUse(npc2, npc1, false);
            if (_shld2 > 0) {
                ++shld2;
            }
            final boolean _crit2 = Formulas.calcCrit((double)npc2.getCriticalHit(), npc2, npc1, (Skill)null);
            if (_crit2) {
                ++crit2;
            }
            double _patk2 = npc2.getPAtk();
            _patk2 *= npc2.getRandomDamageMultiplier();
            patk2 += _patk2;
            final double _pdef2 = npc2.getPDef();
            pdef2 += _pdef2;
            if (!_miss2) {
                final double _dmg2 = Formulas.calcAutoAttackDamage(npc2, npc1, _shld2, _crit2, false);
                dmg2 += _dmg2;
                npc2.abortAttack();
            }
        }
        miss1 /= 100;
        miss2 /= 100;
        shld1 /= 100;
        shld2 /= 100;
        crit1 /= 100;
        crit2 /= 100;
        patk1 /= 10000.0;
        patk2 /= 10000.0;
        pdef1 /= 10000.0;
        pdef2 /= 10000.0;
        dmg1 /= 10000.0;
        dmg2 /= 10000.0;
        final int tdmg1 = (int)(sAtk1 * dmg1);
        final int tdmg2 = (int)(sAtk2 * dmg2);
        final double maxHp1 = npc1.getMaxHp();
        final int hp1 = (int)(npc1.getStats().getValue(Stat.REGENERATE_HP_RATE) * 100000.0 / Formulas.getRegeneratePeriod(npc1));
        final double maxHp2 = npc2.getMaxHp();
        final int hp2 = (int)(npc2.getStats().getValue(Stat.REGENERATE_HP_RATE) * 100000.0 / Formulas.getRegeneratePeriod(npc2));
        final NpcHtmlMessage adminReply = new NpcHtmlMessage(0, 1);
        final StringBuilder replyMSG = new StringBuilder(1000);
        replyMSG.append("<html><title>Selected mobs to fight</title><body><table>");
        if (params.isEmpty()) {
            replyMSG.append("<tr><td width=140>Parameter</td><td width=70>me</td><td width=70>target</td></tr>");
        }
        else {
            replyMSG.append(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, ((NpcTemplate)npc1.getTemplate()).getName(), ((NpcTemplate)npc2.getTemplate()).getName()));
        }
        replyMSG.append(invokedynamic(makeConcatWithConstants:(IIIIIIIIIIIIIIIIIIII)Ljava/lang/String;, miss1, miss2, shld2, shld1, crit1, crit2, (int)patk1, (int)pdef1, (int)patk2, (int)pdef2, sAtk1, sAtk2, (int)dmg1, (int)dmg2, tdmg2, tdmg1, hp1, hp2, (int)maxHp1, (int)maxHp2));
        if (tdmg2 - hp1 > 1) {
            replyMSG.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (int)(100.0 * maxHp1 / (tdmg2 - hp1))));
        }
        else {
            replyMSG.append("<td>never</td>");
        }
        if (tdmg1 - hp2 > 1) {
            replyMSG.append(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, (int)(100.0 * maxHp2 / (tdmg1 - hp2))));
        }
        else {
            replyMSG.append("<td>never</td>");
        }
        replyMSG.append("</tr></table><center><br>");
        if (params.isEmpty()) {
            replyMSG.append("<button value=\"Retry\" action=\"bypass -h admin_fight_calculator_show\"  width=100 height=15 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">");
        }
        else {
            replyMSG.append(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, ((NpcTemplate)npc1.getTemplate()).getId(), ((NpcTemplate)npc2.getTemplate()).getId()));
        }
        replyMSG.append("</center></body></html>");
        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(new ServerPacket[] { (ServerPacket)adminReply });
        if (params.length() != 0) {
            npc1.deleteMe();
            npc2.deleteMe();
        }
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_fight_calculator", "admin_fight_calculator_show", "admin_fcs" };
    }
}
