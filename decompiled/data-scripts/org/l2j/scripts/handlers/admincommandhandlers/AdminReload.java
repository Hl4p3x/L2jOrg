// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.admincommandhandlers;

import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.data.xml.impl.FishingData;
import org.l2j.gameserver.data.xml.impl.AugmentationEngine;
import org.l2j.gameserver.data.xml.impl.ArmorSetsData;
import org.l2j.gameserver.data.xml.impl.PrimeShopData;
import org.l2j.gameserver.data.xml.impl.ItemCrystallizationData;
import org.l2j.gameserver.data.xml.impl.TransformData;
import org.l2j.gameserver.engine.item.EnchantItemEngine;
import org.l2j.gameserver.data.sql.impl.CrestTable;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.data.xml.impl.BuyListData;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.util.BuilderUtil;
import org.l2j.gameserver.instancemanager.QuestManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.AdminData;
import org.l2j.gameserver.Config;
import java.util.StringTokenizer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IAdminCommandHandler;

public class AdminReload implements IAdminCommandHandler
{
    private static final String[] ADMIN_COMMANDS;
    private static final String RELOAD_USAGE = "Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|enchant|options|fishing>";
    
    public boolean useAdminCommand(final String command, final Player activeChar) {
        final StringTokenizer st = new StringTokenizer(command, " ");
        final String actualCommand = st.nextToken();
        if (actualCommand.equalsIgnoreCase("admin_reload")) {
            if (!st.hasMoreTokens()) {
                AdminHtml.showAdminHtml(activeChar, "reload.htm");
                activeChar.sendMessage("Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|enchant|options|fishing>");
                return true;
            }
            final String type = st.nextToken();
            final String lowerCase = type.toLowerCase();
            switch (lowerCase) {
                case "config": {
                    Config.load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "access": {
                    AdminData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "npc": {
                    NpcData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "quest": {
                    if (st.hasMoreElements()) {
                        final String value = st.nextToken();
                        if (!Util.isDigit(value)) {
                            QuestManager.getInstance().reload(value);
                            AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, activeChar.getName(), value));
                        }
                        else {
                            final int questId = Integer.parseInt(value);
                            QuestManager.getInstance().reload(questId);
                            AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, activeChar.getName(), questId));
                        }
                        break;
                    }
                    QuestManager.getInstance().reloadAllScripts();
                    BuilderUtil.sendSysMessage(activeChar, "All scripts have been reloaded.");
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "walker": {
                    WalkingManager.getInstance().load();
                    BuilderUtil.sendSysMessage(activeChar, "All walkers have been reloaded");
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "htm":
                case "html": {
                    if (st.hasMoreElements()) {
                        final String path = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, st.nextToken());
                        if (HtmCache.getInstance().purge(path)) {
                            AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, activeChar.getName(), path));
                        }
                        else {
                            BuilderUtil.sendSysMessage(activeChar, "Html Cache doesn't contains File or Directory.");
                        }
                        break;
                    }
                    HtmCache.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "multisell": {
                    MultisellData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "buylist": {
                    BuyListData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "teleport": {
                    TeleportersData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "skill": {
                    SkillEngine.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "item": {
                    ItemEngine.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "door": {
                    DoorDataManager.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "zone": {
                    ZoneManager.getInstance().reload();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "crest": {
                    CrestTable.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "enchant": {
                    EnchantItemEngine.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "transform": {
                    TransformData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "crystalizable": {
                    ItemCrystallizationData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "primeshop": {
                    PrimeShopData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "sets": {
                    ArmorSetsData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "options": {
                    AugmentationEngine.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "fishing": {
                    FishingData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "attendance": {
                    AttendanceRewardData.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                case "instance": {
                    InstanceManager.getInstance().load();
                    AdminData.getInstance().broadcastMessageToGMs(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, activeChar.getName()));
                    break;
                }
                default: {
                    activeChar.sendMessage("Usage: //reload <config|access|npc|quest [quest_id|quest_name]|walker|htm[l] [file|directory]|multisell|buylist|teleport|skill|item|door|enchant|options|fishing>");
                    return true;
                }
            }
            BuilderUtil.sendSysMessage(activeChar, "WARNING: There are several known issues regarding this feature. Reloading server data during runtime is STRONGLY NOT RECOMMENDED for live servers, just for developing environments.");
        }
        return true;
    }
    
    public String[] getAdminCommandList() {
        return AdminReload.ADMIN_COMMANDS;
    }
    
    static {
        ADMIN_COMMANDS = new String[] { "admin_reload" };
    }
}
