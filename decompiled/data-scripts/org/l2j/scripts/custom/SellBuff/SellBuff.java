// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.custom.SellBuff;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.events.AbstractScript;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.instancemanager.SellBuffsManager;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.VoicedCommandHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.handler.IVoicedCommandHandler;

public class SellBuff implements IVoicedCommandHandler, IBypassHandler
{
    private static final String[] VOICED_COMMANDS;
    private static final String[] BYPASS_COMMANDS;
    
    public SellBuff() {
        if (Config.SELLBUFF_ENABLED) {
            BypassHandler.getInstance().registerHandler((IBypassHandler)this);
            VoicedCommandHandler.getInstance().registerHandler((IVoicedCommandHandler)this);
        }
    }
    
    public boolean useBypass(final String command, final Player player, final Creature target) {
        String cmd = "";
        String params = "";
        final StringTokenizer st = new StringTokenizer(command, " ");
        if (st.hasMoreTokens()) {
            cmd = st.nextToken();
        }
        while (st.hasMoreTokens()) {
            params = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, params, st.nextToken(), st.hasMoreTokens() ? " " : "");
        }
        return !cmd.isEmpty() && this.useBypass(cmd, player, params);
    }
    
    public boolean useVoicedCommand(final String command, final Player activeChar, final String params) {
        switch (command) {
            case "sellbuff":
            case "sellbuffs": {
                SellBuffsManager.getInstance().sendSellMenu(activeChar);
                break;
            }
        }
        return true;
    }
    
    public boolean useBypass(final String command, final Player activeChar, final String params) {
        if (!Config.SELLBUFF_ENABLED) {
            return false;
        }
        switch (command) {
            case "sellbuffstart": {
                if (activeChar.isSellingBuffs() || params == null || params.isEmpty()) {
                    return false;
                }
                if (activeChar.getSellingBuffs().isEmpty()) {
                    activeChar.sendMessage("Your list of buffs is empty, please add some buffs first!");
                    return false;
                }
                String title = "BUFF SELL: ";
                final StringTokenizer st = new StringTokenizer(params, " ");
                while (st.hasMoreTokens()) {
                    title = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, title, st.nextToken());
                }
                if (title.length() > 40) {
                    activeChar.sendMessage("Your title cannot exceed 29 characters in length. Please try again.");
                    return false;
                }
                SellBuffsManager.getInstance().startSellBuffs(activeChar, title);
                break;
            }
            case "sellbuffstop": {
                if (activeChar.isSellingBuffs()) {
                    SellBuffsManager.getInstance().stopSellBuffs(activeChar);
                    break;
                }
                break;
            }
            case "sellbuffadd": {
                if (!activeChar.isSellingBuffs()) {
                    int index = 0;
                    if (params != null && !params.isEmpty() && Util.isInteger(params)) {
                        index = Integer.parseInt(params);
                    }
                    SellBuffsManager.getInstance().sendBuffChoiceMenu(activeChar, index);
                    break;
                }
                break;
            }
            case "sellbuffedit": {
                if (!activeChar.isSellingBuffs()) {
                    SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
                    break;
                }
                break;
            }
            case "sellbuffchangeprice": {
                if (activeChar.isSellingBuffs() || params == null || params.isEmpty()) {
                    break;
                }
                final StringTokenizer st2 = new StringTokenizer(params, " ");
                int skillId = -1;
                int price = -1;
                if (st2.hasMoreTokens()) {
                    skillId = Integer.parseInt(st2.nextToken());
                }
                if (st2.hasMoreTokens()) {
                    try {
                        price = Integer.parseInt(st2.nextToken());
                    }
                    catch (NumberFormatException e) {
                        activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Config.SELLBUFF_MAX_PRICE));
                        SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
                    }
                }
                if (skillId == -1 || price == -1) {
                    return false;
                }
                final Skill skillToChange = activeChar.getKnownSkill(skillId);
                if (skillToChange == null) {
                    return false;
                }
                final SellBuffHolder holder = (SellBuffHolder)activeChar.getSellingBuffs().stream().filter(h -> h.getSkillId() == skillToChange.getId()).findFirst().orElse(null);
                if (holder != null) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getKnownSkill(holder.getSkillId()).getName(), price));
                    holder.setPrice(price);
                    SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
                }
                break;
            }
            case "sellbuffremove": {
                if (activeChar.isSellingBuffs() || params == null || params.isEmpty()) {
                    break;
                }
                final StringTokenizer st2 = new StringTokenizer(params, " ");
                int skillId = -1;
                if (st2.hasMoreTokens()) {
                    skillId = Integer.parseInt(st2.nextToken());
                }
                if (skillId == -1) {
                    return false;
                }
                final Skill skillToRemove = activeChar.getKnownSkill(skillId);
                if (skillToRemove == null) {
                    return false;
                }
                final SellBuffHolder holder2 = (SellBuffHolder)activeChar.getSellingBuffs().stream().filter(h -> h.getSkillId() == skillToRemove.getId()).findFirst().orElse(null);
                if (holder2 != null && activeChar.getSellingBuffs().remove(holder2)) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getKnownSkill(holder2.getSkillId()).getName()));
                    SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
                }
                break;
            }
            case "sellbuffaddskill": {
                if (activeChar.isSellingBuffs() || params == null || params.isEmpty()) {
                    break;
                }
                final StringTokenizer st2 = new StringTokenizer(params, " ");
                int skillId = -1;
                long price2 = -1L;
                if (st2.hasMoreTokens()) {
                    skillId = Integer.parseInt(st2.nextToken());
                }
                if (st2.hasMoreTokens()) {
                    try {
                        price2 = Integer.parseInt(st2.nextToken());
                    }
                    catch (NumberFormatException e2) {
                        activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Config.SELLBUFF_MIN_PRICE));
                        SellBuffsManager.getInstance().sendBuffEditMenu(activeChar);
                    }
                }
                if (skillId == -1 || price2 == -1L) {
                    return false;
                }
                final Skill skillToAdd = activeChar.getKnownSkill(skillId);
                if (skillToAdd == null) {
                    return false;
                }
                if (price2 < Config.SELLBUFF_MIN_PRICE) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Config.SELLBUFF_MIN_PRICE));
                    return false;
                }
                if (price2 > Config.SELLBUFF_MAX_PRICE) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, Config.SELLBUFF_MAX_PRICE));
                    return false;
                }
                if (activeChar.getSellingBuffs().size() >= Config.SELLBUFF_MAX_BUFFS) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Config.SELLBUFF_MAX_BUFFS));
                    return false;
                }
                if (!SellBuffsManager.getInstance().isInSellList(activeChar, skillToAdd)) {
                    activeChar.getSellingBuffs().add(new SellBuffHolder(skillToAdd.getId(), price2));
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skillToAdd.getName()));
                    SellBuffsManager.getInstance().sendBuffChoiceMenu(activeChar, 0);
                }
                break;
            }
            case "sellbuffbuymenu": {
                if (params != null && !params.isEmpty()) {
                    final StringTokenizer st2 = new StringTokenizer(params, " ");
                    int objId = -1;
                    int index2 = 0;
                    if (st2.hasMoreTokens()) {
                        objId = Integer.parseInt(st2.nextToken());
                    }
                    if (st2.hasMoreTokens()) {
                        index2 = Integer.parseInt(st2.nextToken());
                    }
                    final Player seller = World.getInstance().findPlayer(objId);
                    if (seller != null) {
                        if (!seller.isSellingBuffs() || !MathUtil.isInsideRadius3D((ILocational)activeChar, (ILocational)seller, 250)) {
                            return false;
                        }
                        SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller, index2);
                    }
                    break;
                }
                break;
            }
            case "sellbuffbuyskill": {
                if (params == null || params.isEmpty()) {
                    break;
                }
                final StringTokenizer st2 = new StringTokenizer(params, " ");
                int objId = -1;
                int skillId2 = -1;
                int index3 = 0;
                if (st2.hasMoreTokens()) {
                    objId = Integer.parseInt(st2.nextToken());
                }
                if (st2.hasMoreTokens()) {
                    skillId2 = Integer.parseInt(st2.nextToken());
                }
                if (st2.hasMoreTokens()) {
                    index3 = Integer.parseInt(st2.nextToken());
                }
                if (skillId2 == -1 || objId == -1) {
                    return false;
                }
                final Player seller2 = World.getInstance().findPlayer(objId);
                if (seller2 == null) {
                    return false;
                }
                final Skill skillToBuy = seller2.getKnownSkill(skillId2);
                if (!seller2.isSellingBuffs() || !GameUtils.checkIfInRange(250, (WorldObject)activeChar, (WorldObject)seller2, true) || skillToBuy == null) {
                    return false;
                }
                if (seller2.getCurrentMp() < skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER) {
                    activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, seller2.getName(), skillToBuy.getName()));
                    SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller2, index3);
                    return false;
                }
                final SellBuffHolder holder3 = (SellBuffHolder)seller2.getSellingBuffs().stream().filter(h -> h.getSkillId() == skillToBuy.getId()).findFirst().orElse(null);
                if (holder3 != null) {
                    if (AbstractScript.getQuestItemsCount(activeChar, Config.SELLBUFF_PAYMENT_ID) >= holder3.getPrice()) {
                        AbstractScript.takeItems(activeChar, Config.SELLBUFF_PAYMENT_ID, holder3.getPrice());
                        AbstractScript.giveItems(seller2, Config.SELLBUFF_PAYMENT_ID, holder3.getPrice());
                        seller2.reduceCurrentMp((double)(skillToBuy.getMpConsume() * Config.SELLBUFF_MP_MULTIPLER));
                        skillToBuy.activateSkill((Creature)seller2, new WorldObject[] { (WorldObject)activeChar });
                    }
                    else {
                        final ItemTemplate item = ItemEngine.getInstance().getTemplate(Config.SELLBUFF_PAYMENT_ID);
                        if (item != null) {
                            activeChar.sendMessage(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, item.getName()));
                        }
                        else {
                            activeChar.sendMessage("Not enough items!");
                        }
                    }
                }
                SellBuffsManager.getInstance().sendBuffMenu(activeChar, seller2, index3);
                break;
            }
        }
        return true;
    }
    
    public String[] getVoicedCommandList() {
        return SellBuff.VOICED_COMMANDS;
    }
    
    public String[] getBypassList() {
        return SellBuff.BYPASS_COMMANDS;
    }
    
    static {
        VOICED_COMMANDS = new String[] { "sellbuff", "sellbuffs" };
        BYPASS_COMMANDS = new String[] { "sellbuffadd", "sellbuffaddskill", "sellbuffedit", "sellbuffchangeprice", "sellbuffremove", "sellbuffbuymenu", "sellbuffbuyskill", "sellbuffstart", "sellbuffstop" };
    }
}
