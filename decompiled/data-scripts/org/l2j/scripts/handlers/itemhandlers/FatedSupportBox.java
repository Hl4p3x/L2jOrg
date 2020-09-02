// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class FatedSupportBox implements IItemHandler
{
    private static final int FATED_BOX_FIGHTER = 37315;
    private static final int FATED_BOX_WIZARD = 37316;
    private static final int FATED_BOX_WARRIOR = 37317;
    private static final int FATED_BOX_ROGUE = 37318;
    private static final int FATED_BOX_ORC_FIGHTER = 37320;
    private static final int FATED_BOX_ORC_WIZARD = 37321;
    
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        final Player player = playable.getActingPlayer();
        final Race race = player.getRace();
        final ClassId classId = player.getClassId();
        if (!player.isInventoryUnder80(false)) {
            player.sendPacket(SystemMessageId.UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return false;
        }
        if (player.getLevel() < 40 || player.isInCategory(CategoryType.FIRST_CLASS_GROUP) || player.isInCategory(CategoryType.SECOND_CLASS_GROUP)) {
            player.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item) });
            return false;
        }
        player.getInventory().destroyItem(this.getClass().getSimpleName(), item, 1L, player, (Object)null);
        player.sendPacket(new ServerPacket[] { (ServerPacket)new InventoryUpdate(item) });
        if (player.getLevel() > 84) {
            player.sendMessage("Fated Support Box was removed because your level has exceeded the maximum requirement.");
            return true;
        }
        switch (race) {
            case HUMAN:
            case ELF:
            case DARK_ELF:
            case DWARF: {
                if (player.isMageClass()) {
                    player.addItem(this.getClass().getSimpleName(), 37316, 1L, (WorldObject)player, true);
                    break;
                }
                if (CategoryManager.getInstance().isInCategory(CategoryType.SUB_GROUP_ROGUE, classId.getId())) {
                    player.addItem(this.getClass().getSimpleName(), 37318, 1L, (WorldObject)player, true);
                    break;
                }
                if (CategoryManager.getInstance().isInCategory(CategoryType.SUB_GROUP_KNIGHT, classId.getId())) {
                    player.addItem(this.getClass().getSimpleName(), 37315, 1L, (WorldObject)player, true);
                    break;
                }
                player.addItem(this.getClass().getSimpleName(), 37317, 1L, (WorldObject)player, true);
                break;
            }
            case ORC: {
                if (player.isMageClass()) {
                    player.addItem(this.getClass().getSimpleName(), 37321, 1L, (WorldObject)player, true);
                    break;
                }
                player.addItem(this.getClass().getSimpleName(), 37320, 1L, (WorldObject)player, true);
                break;
            }
        }
        return true;
    }
}
