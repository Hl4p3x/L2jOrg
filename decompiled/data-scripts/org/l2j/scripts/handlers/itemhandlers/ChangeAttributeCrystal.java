// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.attributechange.ExChangeAttributeItemList;
import org.l2j.gameserver.model.ItemInfo;
import java.util.ArrayList;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.enums.ItemGrade;
import java.util.Map;
import org.l2j.gameserver.handler.IItemHandler;

public class ChangeAttributeCrystal implements IItemHandler
{
    private static final Map<Integer, ItemGrade> ITEM_GRADES;
    
    public ChangeAttributeCrystal() {
        ChangeAttributeCrystal.ITEM_GRADES.put(33502, ItemGrade.S);
    }
    
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player player = playable.getActingPlayer();
        if (player.getPrivateStoreType() != PrivateStoreType.NONE) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.YOU_CANNOT_CHANGE_AN_ATTRIBUTE_WHILE_USING_A_PRIVATE_STORE_OR_WORKSHOP) });
            return false;
        }
        if (ChangeAttributeCrystal.ITEM_GRADES.get(item.getId()) == null) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.CHANGING_ATTRIBUTES_HAS_BEEN_FAILED) });
            return false;
        }
        final List<ItemInfo> itemList = new ArrayList<ItemInfo>();
        for (final Item i : player.getInventory().getItems()) {
            if (i.isWeapon() && i.hasAttributes() && i.getTemplate().getItemGrade() == ChangeAttributeCrystal.ITEM_GRADES.get(item.getId())) {
                itemList.add(new ItemInfo(i));
            }
        }
        if (itemList.isEmpty()) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.THE_ITEM_FOR_CHANGING_AN_ATTRIBUTE_DOES_NOT_EXIST) });
            return false;
        }
        player.sendPacket(new ServerPacket[] { (ServerPacket)new ExChangeAttributeItemList(item.getId(), (ItemInfo[])itemList.toArray(new ItemInfo[itemList.size()])) });
        return true;
    }
    
    static {
        ITEM_GRADES = new HashMap<Integer, ItemGrade>();
    }
}
