// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.l2j.gameserver.network.serverpackets.MagicSkillLaunched;
import org.slf4j.LoggerFactory;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public final class Evolve
{
    protected static final Logger LOGGER;
    
    public static boolean doEvolve(final Player player, final Npc npc, final int itemIdtake, final int itemIdgive, final int petminlvl) {
        if (itemIdtake == 0 || itemIdgive == 0 || petminlvl == 0) {
            return false;
        }
        final Summon pet = player.getPet();
        if (pet == null) {
            return false;
        }
        final Pet currentPet = (Pet)pet;
        if (currentPet.isAlikeDead()) {
            GameUtils.handleIllegalPlayerAction(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, player.getName()));
            return false;
        }
        Item item = null;
        long petexp = currentPet.getStats().getExp();
        final String oldname = currentPet.getName();
        final int oldX = currentPet.getX();
        final int oldY = currentPet.getY();
        final int oldZ = currentPet.getZ();
        final PetData oldData = PetDataTable.getInstance().getPetDataByItemId(itemIdtake);
        if (oldData == null) {
            return false;
        }
        final int oldnpcID = oldData.getNpcId();
        if (currentPet.getStats().getLevel() < petminlvl || currentPet.getId() != oldnpcID) {
            return false;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(itemIdgive);
        if (petData == null) {
            return false;
        }
        final int npcID = petData.getNpcId();
        if (npcID == 0) {
            return false;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcID);
        currentPet.unSummon(player);
        currentPet.destroyControlItem(player, true);
        item = player.getInventory().addItem("Evolve", itemIdgive, 1L, player, npc);
        final Pet petSummon = Pet.spawnPet(npcTemplate, player, item);
        if (petSummon == null) {
            return false;
        }
        final long _minimumexp = petSummon.getStats().getExpForLevel(petminlvl);
        if (petexp < _minimumexp) {
            petexp = _minimumexp;
        }
        petSummon.getStats().addExp(petexp);
        petSummon.setCurrentHp(petSummon.getMaxHp());
        petSummon.setCurrentMp(petSummon.getMaxMp());
        petSummon.setCurrentFed(petSummon.getMaxFed());
        petSummon.setTitle(player.getName());
        petSummon.setName(oldname);
        petSummon.setRunning();
        petSummon.storeMe();
        player.setPet(petSummon);
        player.sendPacket(new MagicSkillUse(npc, 2046, 1, 1000, 600000));
        player.sendPacket(SystemMessageId.SUMMONING_YOUR_PET);
        petSummon.spawnMe(oldX, oldY, oldZ);
        petSummon.startFeed();
        item.setEnchantLevel(petSummon.getLevel());
        ThreadPool.schedule((Runnable)new EvolveFinalizer(player, petSummon), 900L);
        if (petSummon.getCurrentFed() <= 0) {
            ThreadPool.schedule((Runnable)new EvolveFeedWait(player, petSummon), 60000L);
        }
        else {
            petSummon.startFeed();
        }
        return true;
    }
    
    public static boolean doRestore(final Player player, final Npc npc, final int itemIdtake, final int itemIdgive, final int petminlvl) {
        if (itemIdtake == 0 || itemIdgive == 0 || petminlvl == 0) {
            return false;
        }
        final Item item = player.getInventory().getItemByItemId(itemIdtake);
        if (item == null) {
            return false;
        }
        int oldpetlvl = item.getEnchantLevel();
        if (oldpetlvl < petminlvl) {
            oldpetlvl = petminlvl;
        }
        final PetData oldData = PetDataTable.getInstance().getPetDataByItemId(itemIdtake);
        if (oldData == null) {
            return false;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(itemIdgive);
        if (petData == null) {
            return false;
        }
        final int npcId = petData.getNpcId();
        if (npcId == 0) {
            return false;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(npcId);
        final Item removedItem = player.getInventory().destroyItem("PetRestore", item, player, npc);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(removedItem);
        player.sendPacket(sm);
        final Item addedItem = player.getInventory().addItem("PetRestore", itemIdgive, 1L, player, npc);
        final Pet petSummon = Pet.spawnPet(npcTemplate, player, addedItem);
        if (petSummon == null) {
            return false;
        }
        final long _maxexp = petSummon.getStats().getExpForLevel(oldpetlvl);
        petSummon.getStats().addExp(_maxexp);
        petSummon.setCurrentHp(petSummon.getMaxHp());
        petSummon.setCurrentMp(petSummon.getMaxMp());
        petSummon.setCurrentFed(petSummon.getMaxFed());
        petSummon.setTitle(player.getName());
        petSummon.setRunning();
        petSummon.storeMe();
        player.setPet(petSummon);
        player.sendPacket(new MagicSkillUse(npc, 2046, 1, 1000, 600000));
        player.sendPacket(SystemMessageId.SUMMONING_YOUR_PET);
        petSummon.spawnMe(player.getX(), player.getY(), player.getZ());
        petSummon.startFeed();
        addedItem.setEnchantLevel(petSummon.getLevel());
        final InventoryUpdate iu = new InventoryUpdate();
        iu.addRemovedItem(removedItem);
        player.sendInventoryUpdate(iu);
        player.broadcastUserInfo();
        ThreadPool.schedule((Runnable)new EvolveFinalizer(player, petSummon), 900L);
        if (petSummon.getCurrentFed() <= 0) {
            ThreadPool.schedule((Runnable)new EvolveFeedWait(player, petSummon), 60000L);
        }
        else {
            petSummon.startFeed();
        }
        ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).deleteByItem(removedItem.getObjectId());
        return true;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Evolve.class);
    }
    
    static final class EvolveFeedWait implements Runnable
    {
        private final Player _activeChar;
        private final Pet _petSummon;
        
        EvolveFeedWait(final Player activeChar, final Pet petSummon) {
            this._activeChar = activeChar;
            this._petSummon = petSummon;
        }
        
        @Override
        public void run() {
            try {
                if (this._petSummon.getCurrentFed() <= 0) {
                    this._petSummon.unSummon(this._activeChar);
                }
                else {
                    this._petSummon.startFeed();
                }
            }
            catch (Exception e) {
                Evolve.LOGGER.warn("", (Throwable)e);
            }
        }
    }
    
    static final class EvolveFinalizer implements Runnable
    {
        private final Player _activeChar;
        private final Pet _petSummon;
        
        EvolveFinalizer(final Player activeChar, final Pet petSummon) {
            this._activeChar = activeChar;
            this._petSummon = petSummon;
        }
        
        @Override
        public void run() {
            try {
                this._activeChar.sendPacket(new MagicSkillLaunched(this._activeChar, 2046, 1));
                this._petSummon.setFollowStatus(true);
                this._petSummon.setShowSummonAnimation(false);
            }
            catch (Throwable e) {
                Evolve.LOGGER.warn("", e);
            }
        }
    }
}
