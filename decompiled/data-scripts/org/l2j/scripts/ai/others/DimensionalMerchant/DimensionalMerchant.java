// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.ai.others.DimensionalMerchant;

import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerBypass;
import java.util.Iterator;
import org.l2j.gameserver.model.item.container.PlayerFreight;
import org.l2j.gameserver.data.xml.impl.MultisellData;
import org.l2j.gameserver.network.serverpackets.attendance.ExVipAttendanceItemList;
import org.l2j.gameserver.network.serverpackets.WareHouseWithdrawalList;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.network.serverpackets.PackageToList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.scripts.ai.AbstractNpcAI;

public class DimensionalMerchant extends AbstractNpcAI
{
    private static final int MERCHANT = 32478;
    private static final int ATTENDANCE_REWARD_MULTISELL = 3247801;
    private static final int HAIR_MULTISELL = 4706;
    private static final int TICKET_MULTISELL = 4707;
    private static final int ENHANCEMENT_MULTISELL = 567;
    private static final int CLOAK_MULTISELL = 4669;
    private static final int HERO_MULTISELL = 4680;
    private static final int WEAPON_D_MULTISELL = 4037;
    private static final int WEAPON_C_MULTISELL = 4038;
    private static final String COMMAND_BYPASS = "Quest DimensionalMerchant ";
    
    private DimensionalMerchant() {
        this.addTalkId(32478);
    }
    
    public String onAdvEvent(final String event, final Npc npc, final Player player) {
        final String htmltext = null;
        switch (event) {
            case "package_deposit": {
                if (player.getAccountChars().size() < 1) {
                    player.sendPacket(SystemMessageId.THAT_CHARACTER_DOES_NOT_EXIST);
                    break;
                }
                player.sendPacket(new ServerPacket[] { (ServerPacket)new PackageToList(player.getAccountChars()) });
                break;
            }
            case "package_withdraw": {
                final PlayerFreight freight = player.getFreight();
                if (freight == null) {
                    break;
                }
                if (freight.getSize() > 0) {
                    player.setActiveWarehouse((Warehouse)freight);
                    for (final Item i : player.getActiveWarehouse().getItems()) {
                        if (i.isTimeLimitedItem() && i.getRemainingTime() <= 0L) {
                            player.getActiveWarehouse().destroyItem("ItemInstance", i, player, (Object)null);
                        }
                    }
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(1, player, 1) });
                    player.sendPacket(new ServerPacket[] { (ServerPacket)new WareHouseWithdrawalList(2, player, 1) });
                    break;
                }
                player.sendPacket(SystemMessageId.YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE);
                break;
            }
            case "attendance_rewards": {
                player.sendPacket(new ServerPacket[] { (ServerPacket)new ExVipAttendanceItemList(player) });
                break;
            }
            case "shop": {
                MultisellData.getInstance().separateAndSend(3247801, player, (Npc)null, false);
                break;
            }
            case "hair": {
                MultisellData.getInstance().separateAndSend(4706, player, (Npc)null, false);
                break;
            }
            case "ticket": {
                MultisellData.getInstance().separateAndSend(4707, player, (Npc)null, false);
                break;
            }
            case "enhancement": {
                MultisellData.getInstance().separateAndSend(567, player, (Npc)null, false);
                break;
            }
            case "kingdomcloak": {
                MultisellData.getInstance().separateAndSend(4669, player, (Npc)null, false);
                break;
            }
            case "herosweapon": {
                MultisellData.getInstance().separateAndSend(4680, player, (Npc)null, false);
                break;
            }
            case "timedweapond": {
                MultisellData.getInstance().separateAndSend(4037, player, (Npc)null, false);
                break;
            }
            case "timedweaponc": {
                MultisellData.getInstance().separateAndSend(4038, player, (Npc)null, false);
                break;
            }
        }
        return htmltext;
    }
    
    @RegisterEvent(EventType.ON_PLAYER_BYPASS)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerBypass(final OnPlayerBypass event) {
        final Player player = event.getPlayer();
        if (event.getCommand().startsWith("Quest DimensionalMerchant ")) {
            this.notifyEvent(event.getCommand().replace("Quest DimensionalMerchant ", ""), (Npc)null, player);
        }
    }
    
    public static DimensionalMerchant provider() {
        return new DimensionalMerchant();
    }
}
