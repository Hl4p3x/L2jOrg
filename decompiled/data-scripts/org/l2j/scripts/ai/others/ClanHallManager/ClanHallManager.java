// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.ClanHallManager;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.network.serverpackets.AgitDecoInfo;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.data.database.data.ResidenceFunctionData;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.enums.ClanHallGrade;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.commons.util.Util;
import org.l2j.commons.util.CommonUtil;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.model.ClanPrivilege;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public final class ClanHallManager extends AbstractNpcAI
{
    private static final int[] CLANHALL_MANAGERS;
    private static final int[] ALLOWED_BUFFS;
    
    private ClanHallManager() {
        this.addStartNpc(ClanHallManager.CLANHALL_MANAGERS);
        this.addTalkId(ClanHallManager.CLANHALL_MANAGERS);
        this.addFirstTalkId(ClanHallManager.CLANHALL_MANAGERS);
        this.addSeeCreatureId(ClanHallManager.CLANHALL_MANAGERS);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final StringTokenizer st = new StringTokenizer(event, " ");
        final String action = st.nextToken();
        final ClanHall clanHall = npc.getClanHall();
        String htmltext = null;
        if (clanHall != null && this.isOwningClan(player, npc)) {
            final String s = action;
            switch (s) {
                case "index": {
                    htmltext = (this.isOwningClan(player, npc) ? "ClanHallManager-01.html" : "ClanHallManager-03.html");
                    break;
                }
                case "manageDoors": {
                    if (!player.hasClanPrivilege(ClanPrivilege.CH_OPEN_DOOR)) {
                        htmltext = "ClanHallManager-noAuthority.html";
                        break;
                    }
                    if (st.hasMoreTokens()) {
                        final boolean open = st.nextToken().equals("1");
                        clanHall.openCloseDoors(open);
                        htmltext = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, open ? "5" : "6");
                        break;
                    }
                    htmltext = "ClanHallManager-04.html";
                    break;
                }
                case "expel": {
                    if (!player.hasClanPrivilege(ClanPrivilege.CH_DISMISS)) {
                        htmltext = "ClanHallManager-noAuthority.html";
                        break;
                    }
                    if (st.hasMoreTokens()) {
                        clanHall.banishOthers();
                        htmltext = "ClanHallManager-08.html";
                        break;
                    }
                    htmltext = "ClanHallManager-07.html";
                    break;
                }
                case "useFunctions": {
                    if (!player.hasClanPrivilege(ClanPrivilege.CH_OTHER_RIGHTS)) {
                        htmltext = "ClanHallManager-noAuthority.html";
                        break;
                    }
                    if (!st.hasMoreTokens()) {
                        final ResidenceFunctionData hpFunc = clanHall.getFunction(ResidenceFunctionType.HP_REGEN);
                        final ResidenceFunctionData mpFunc = clanHall.getFunction(ResidenceFunctionType.MP_REGEN);
                        final ResidenceFunctionData xpFunc = clanHall.getFunction(ResidenceFunctionType.EXP_RESTORE);
                        htmltext = this.getHtml(player, "ClanHallManager-09.html");
                        htmltext = htmltext.replaceAll("%hpFunction%", (hpFunc != null) ? String.valueOf((int)hpFunc.getValue()) : "0");
                        htmltext = htmltext.replaceAll("%mpFunction%", (mpFunc != null) ? String.valueOf((int)mpFunc.getValue()) : "0");
                        htmltext = htmltext.replaceAll("%resFunction%", (xpFunc != null) ? String.valueOf((int)xpFunc.getValue()) : "0");
                        break;
                    }
                    final String nextToken = st.nextToken();
                    int n2 = -1;
                    switch (nextToken.hashCode()) {
                        case -1360201941: {
                            if (nextToken.equals("teleport")) {
                                n2 = 0;
                                break;
                            }
                            break;
                        }
                        case 94091904: {
                            if (nextToken.equals("buffs")) {
                                n2 = 1;
                                break;
                            }
                            break;
                        }
                        case 100526016: {
                            if (nextToken.equals("items")) {
                                n2 = 2;
                                break;
                            }
                            break;
                        }
                    }
                    Label_1074: {
                        switch (n2) {
                            case 0: {
                                final int teleportLevel = clanHall.getFunctionLevel(ResidenceFunctionType.TELEPORT);
                                if (teleportLevel > 0) {
                                    final TeleportHolder holder = TeleportersData.getInstance().getHolder(npc.getId(), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, teleportLevel));
                                    if (holder != null) {
                                        if (!st.hasMoreTokens()) {
                                            holder.showTeleportList(player, npc, "Quest ClanHallManager useFunctions teleport");
                                        }
                                        else if (st.countTokens() >= 2) {
                                            final String listName = st.nextToken();
                                            final int funcLvl = (listName.length() >= 4) ? CommonUtil.parseInt(listName.substring(3), -1) : -1;
                                            if (teleportLevel == funcLvl) {
                                                holder.doTeleport(player, npc, Util.parseNextInt(st, -1));
                                            }
                                        }
                                    }
                                    break;
                                }
                                htmltext = "ClanHallManager-noFunction.html";
                                break;
                            }
                            case 1: {
                                final int buffLevel = clanHall.getFunctionLevel(ResidenceFunctionType.BUFF);
                                if (buffLevel <= 0) {
                                    htmltext = "ClanHallManager-noFunction.html";
                                    break;
                                }
                                if (!st.hasMoreTokens()) {
                                    htmltext = this.getHtml(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, buffLevel));
                                    htmltext = htmltext.replaceAll("%manaLeft%", Integer.toString((int)npc.getCurrentMp()));
                                    break;
                                }
                                final String[] skillData = st.nextToken().split("_");
                                final SkillHolder skill = new SkillHolder(Integer.parseInt(skillData[0]), Integer.parseInt(skillData[1]));
                                if (Util.contains(ClanHallManager.ALLOWED_BUFFS, skill.getSkillId())) {
                                    if (npc.getCurrentMp() < npc.getStats().getMpConsume(skill.getSkill()) + npc.getStats().getMpInitialConsume(skill.getSkill())) {
                                        htmltext = this.getHtml(player, "ClanHallManager-funcBuffsNoMp.html");
                                    }
                                    else if (npc.isSkillDisabled(skill.getSkill())) {
                                        htmltext = this.getHtml(player, "ClanHallManager-funcBuffsNoReuse.html");
                                    }
                                    else {
                                        this.castSkill(npc, (Playable)player, skill);
                                        htmltext = this.getHtml(player, "ClanHallManager-funcBuffsDone.html");
                                    }
                                    htmltext = htmltext.replaceAll("%manaLeft%", Integer.toString((int)npc.getCurrentMp()));
                                }
                                break;
                            }
                            case 2: {
                                final int itemLevel = clanHall.getFunctionLevel(ResidenceFunctionType.ITEM);
                                switch (itemLevel) {
                                    case 1:
                                    case 2:
                                    case 3: {
                                        ((Merchant)npc).showBuyWindow(player, Integer.parseInt(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npc.getId(), itemLevel - 1)));
                                        break Label_1074;
                                    }
                                    default: {
                                        htmltext = "ClanHallManager-noFunction.html";
                                        break Label_1074;
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                case "warehouse": {
                    htmltext = this.getHtml(player, "ClanHallManager-10.html");
                    htmltext = htmltext.replaceAll("%lease%", String.valueOf(clanHall.getLease()));
                    htmltext = htmltext.replaceAll("%payDate%", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(clanHall.getNextPayment())));
                    break;
                }
                case "manageFunctions": {
                    if (!player.hasClanPrivilege(ClanPrivilege.CH_SET_FUNCTIONS)) {
                        htmltext = "ClanHallManager-noAuthority.html";
                        break;
                    }
                    if (!st.hasMoreTokens()) {
                        htmltext = "ClanHallManager-11.html";
                        break;
                    }
                    final String nextToken2 = st.nextToken();
                    switch (nextToken2) {
                        case "recovery": {
                            htmltext = this.getHtml(player, (clanHall.getGrade() == ClanHallGrade.S) ? "ClanHallManager-manageFuncRecoverySGrade.html" : "ClanHallManager-manageFuncRecoveryBGrade.html");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.HP_REGEN), htmltext, "HP");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.MP_REGEN), htmltext, "MP");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.EXP_RESTORE), htmltext, "XP");
                            break;
                        }
                        case "other": {
                            htmltext = this.getHtml(player, "ClanHallManager-manageFuncOther.html");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.TELEPORT), htmltext, "TP");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.BUFF), htmltext, "BUFF");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.ITEM), htmltext, "ITEM");
                            break;
                        }
                        case "decor": {
                            htmltext = this.getHtml(player, "ClanHallManager-manageFuncDecor.html");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.CURTAIN), htmltext, "CURTAIN");
                            htmltext = this.getFunctionInfo(clanHall.getFunction(ResidenceFunctionType.PLATFORM), htmltext, "PODIUM");
                            break;
                        }
                        case "selectFunction": {
                            if (st.countTokens() == 2) {
                                final int funcId = Integer.parseInt(st.nextToken());
                                final int funcLv = Integer.parseInt(st.nextToken());
                                final ResidenceFunctionData oldFunc = clanHall.getFunction(funcId, funcLv);
                                if (oldFunc != null) {
                                    final int funcVal = (int)oldFunc.getTemplate().getValue();
                                    htmltext = this.getHtml(player, "ClanHallManager-manageFuncAlreadySet.html");
                                    htmltext = htmltext.replaceAll("%funcEffect%", invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, (funcVal > 0) ? funcVal : oldFunc.getLevel(), (funcVal > 0) ? NpcStringId.S1.getId() : NpcStringId.STAGE_S1.getId()));
                                }
                                else if (funcId >= 1 && funcId <= 8) {
                                    final ResidenceFunctionTemplate template = ResidenceFunctionsData.getInstance().getFunction(funcId, funcLv);
                                    if (template != null) {
                                        htmltext = this.getHtml(player, invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, funcId));
                                        htmltext = htmltext.replaceAll("%funcId%", String.valueOf(funcId));
                                        htmltext = htmltext.replaceAll("%funcLv%", String.valueOf(funcLv));
                                        htmltext = htmltext.replaceAll("%funcCost%", invokedynamic(makeConcatWithConstants:(JJI)Ljava/lang/String;, template.getCost().getCount(), template.getDurationAsDays(), NpcStringId.FONT_COLOR_FFAABB_S1_FONT_ADENA_S2_DAY_S.getId()));
                                    }
                                }
                                break;
                            }
                            break;
                        }
                        case "setFunction": {
                            if (st.countTokens() == 2) {
                                final int funcId = Integer.parseInt(st.nextToken());
                                final int funcLv = Integer.parseInt(st.nextToken());
                                final ResidenceFunctionTemplate template2 = ResidenceFunctionsData.getInstance().getFunction(funcId, funcLv);
                                if (template2 != null && getQuestItemsCount(player, template2.getCost().getId()) >= template2.getCost().getCount()) {
                                    if (clanHall.getFunction(funcId, funcLv) != null) {
                                        return null;
                                    }
                                    takeItems(player, template2.getCost().getId(), template2.getCost().getCount());
                                    clanHall.addFunction(funcId, funcLv);
                                    this.updateVisualEffects(clanHall, npc);
                                    htmltext = "ClanHallManager-manageFuncDone.html";
                                }
                                else {
                                    htmltext = "ClanHallManager-noAdena.html";
                                }
                                break;
                            }
                            break;
                        }
                        case "removeFunction": {
                            if (st.countTokens() == 2) {
                                final String act = st.nextToken();
                                final ResidenceFunctionType funcType = ResidenceFunctionType.valueOf(st.nextToken());
                                if (funcType != null) {
                                    if (act.equals("confirm")) {
                                        htmltext = this.getHtml(player, "ClanHallManager-removeFunctionConfirm.html");
                                        htmltext = htmltext.replaceAll("%FUNC_TYPE%", funcType.toString());
                                    }
                                    else if (act.equals("remove")) {
                                        final ResidenceFunctionData func = clanHall.getFunction(funcType);
                                        if (func != null) {
                                            clanHall.removeFunction(func);
                                            this.updateVisualEffects(clanHall, npc);
                                            htmltext = "ClanHallManager-removeFunctionDone.html";
                                        }
                                        else {
                                            htmltext = "ClanHallManager-removeFunctionFail.html";
                                        }
                                    }
                                    else {
                                        htmltext = "ClanHallManager-removeFunctionFail.html";
                                    }
                                }
                                else {
                                    htmltext = "ClanHallManager-removeFunctionFail.html";
                                }
                                break;
                            }
                            htmltext = "ClanHallManager-removeFunctionFail.html";
                            break;
                        }
                    }
                    break;
                }
            }
        }
        return htmltext;
    }
    
    @Override
    public String onFirstTalk(final Npc npc, final Player player) {
        String htmltext = null;
        final ClanHall clanHall = npc.getClanHall();
        if (this.isOwningClan(player, npc)) {
            if (clanHall.getCostFailDay() == 0) {
                htmltext = "ClanHallManager-01.html";
            }
            else {
                htmltext = this.getHtml(player, "ClanHallManager-02.html");
                htmltext = htmltext.replaceAll("%costFailDayLeft%", Integer.toString(8 - clanHall.getCostFailDay()));
            }
        }
        else {
            htmltext = "ClanHallManager-03.html";
        }
        return htmltext;
    }
    
    public String onSeeCreature(final Npc npc, final Creature creature, final boolean isSummon) {
        if (GameUtils.isPlayer((WorldObject)creature)) {
            final ClanHall clanHall = npc.getClanHall();
            if (clanHall != null) {
                creature.getActingPlayer().sendPacket(new ServerPacket[] { (ServerPacket)new AgitDecoInfo((AbstractResidence)clanHall) });
            }
        }
        return super.onSeeCreature(npc, creature, isSummon);
    }
    
    private void updateVisualEffects(final ClanHall clanHall, final Npc npc) {
        World.getInstance().forEachVisibleObject((WorldObject)npc, (Class)Player.class, player -> player.sendPacket(new ServerPacket[] { (ServerPacket)new AgitDecoInfo((AbstractResidence)clanHall) }));
    }
    
    private String getFunctionInfo(final ResidenceFunctionData func, String htmltext, final String name) {
        if (func != null) {
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, String.valueOf((int)func.getTemplate().getValue())));
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), invokedynamic(makeConcatWithConstants:(JJI)Ljava/lang/String;, func.getTemplate().getCost().getCount(), func.getTemplate().getDurationAsDays(), NpcStringId.FONT_COLOR_FFAABB_S1_FONT_ADENA_S2_DAY_S.getId()));
            htmltext = htmltext.replace(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(func.getExpiration()))));
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, func.getType().toString()));
        }
        else {
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, NpcStringId.NONE_2.getId()));
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), "");
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), "");
            htmltext = htmltext.replaceAll(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, name), "");
        }
        return htmltext;
    }
    
    private boolean isOwningClan(final Player player, final Npc npc) {
        return npc.getClanHall().getOwnerId() == player.getClanId() && player.getClanId() != 0;
    }
    
    public static AbstractNpcAI provider() {
        return new ClanHallManager();
    }
    
    static {
        CLANHALL_MANAGERS = new int[] { 35384, 35386, 35388, 35390, 35400, 35392, 35394, 35396, 35398, 35403, 35405, 35407, 35439, 35441, 35443, 35445, 35447, 35449, 35451, 35453, 35455, 35457, 35459, 35461, 35463, 35465, 35467, 35566, 35568, 35570, 35572, 35574, 35576, 35578, 35580, 35582, 35584, 35586, 36721, 36723, 36725, 36727, 36729, 36731, 36733, 36735, 36737, 36739 };
        ALLOWED_BUFFS = new int[] { 4342, 4343, 4344, 4346, 4345, 4347, 4349, 4350, 4348, 4351, 4352, 4353, 4358, 4354, 4355, 4356, 4357, 4359, 4360 };
    }
}
