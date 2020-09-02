// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.network.serverpackets.BlockListPacket;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.model.actor.instance.Player;
import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.IntMap;

public class BlockList
{
    private static final IntMap<IntSet> OFFLINE_LIST;
    private final Player owner;
    private IntSet list;
    
    public BlockList(final Player owner) {
        this.owner = owner;
        this.list = (IntSet)BlockList.OFFLINE_LIST.get(owner.getObjectId());
        if (this.list == null) {
            this.list = loadList(this.owner.getObjectId());
        }
    }
    
    private void addToBlockList(final int target) {
        this.list.add(target);
        this.updateInDB(target, true);
    }
    
    private void removeFromBlockList(final int target) {
        this.list.remove(target);
        this.updateInDB(target, false);
    }
    
    public void playerLogout() {
        BlockList.OFFLINE_LIST.put(this.owner.getObjectId(), (Object)this.list);
    }
    
    private void updateInDB(final int targetId, final boolean add) {
        if (add) {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).saveBlockedPlayer(this.owner.getObjectId(), targetId);
        }
        else {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).deleteBlockedPlayer(this.owner.getObjectId(), targetId);
        }
    }
    
    public boolean isInBlockList(final Player target) {
        return this.list.contains(target.getObjectId());
    }
    
    public boolean isInBlockList(final int targetId) {
        return this.list.contains(targetId);
    }
    
    public boolean isBlockAll() {
        return this.owner.isMessageRefusing();
    }
    
    private void setBlockAll(final boolean state) {
        this.owner.setMessageRefusing(state);
    }
    
    private IntSet getBlockList() {
        return this.list;
    }
    
    private static IntSet loadList(final int objId) {
        return ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findBlockListById(objId);
    }
    
    public static boolean isBlocked(final Player listOwner, final Player target) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(target);
    }
    
    public static boolean isBlocked(final Player listOwner, final int targetId) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(targetId);
    }
    
    public static void addToBlockList(final Player listOwner, final int targetId) {
        if (listOwner == null) {
            return;
        }
        final String charName = PlayerNameTable.getInstance().getNameById(targetId);
        if (listOwner.getFriendList().contains(targetId)) {
            listOwner.sendPacket(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
            return;
        }
        if (listOwner.getBlockList().getBlockList().contains(targetId)) {
            listOwner.sendMessage("Already in ignore list.");
            return;
        }
        listOwner.getBlockList().addToBlockList(targetId);
        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST);
        sm.addString(charName);
        listOwner.sendPacket(sm);
        final Player player = World.getInstance().findPlayer(targetId);
        if (player != null) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
            sm.addString(listOwner.getName());
            player.sendPacket(sm);
        }
    }
    
    public static void removeFromBlockList(final Player listOwner, final int targetId) {
        if (listOwner == null) {
            return;
        }
        final String charName = PlayerNameTable.getInstance().getNameById(targetId);
        if (!listOwner.getBlockList().getBlockList().contains(targetId)) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            listOwner.sendPacket(sm);
            return;
        }
        listOwner.getBlockList().removeFromBlockList(targetId);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST);
        sm.addString(charName);
        listOwner.sendPacket(sm);
    }
    
    public static void setBlockAll(final Player listOwner, final boolean newValue) {
        listOwner.getBlockList().setBlockAll(newValue);
    }
    
    public static void sendListToOwner(final Player listOwner) {
        listOwner.sendPacket(new BlockListPacket(listOwner.getBlockList().getBlockList()));
    }
    
    public static boolean isInBlockList(final int ownerId, final int targetId) {
        final Player player = World.getInstance().findPlayer(ownerId);
        if (player != null) {
            return isBlocked(player, targetId);
        }
        if (!BlockList.OFFLINE_LIST.containsKey(ownerId)) {
            BlockList.OFFLINE_LIST.put(ownerId, (Object)loadList(ownerId));
        }
        return ((IntSet)BlockList.OFFLINE_LIST.get(ownerId)).contains(targetId);
    }
    
    static {
        OFFLINE_LIST = (IntMap)new CHashIntMap();
    }
}
