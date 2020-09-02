// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.sql.impl;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.data.database.data.SummonData;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Servitor;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.data.database.dao.PetDAO;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.SummonDAO;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.CHashIntIntMap;
import io.github.joealisson.primitive.IntSet;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntIntMap;
import org.slf4j.Logger;

public class PlayerSummonTable
{
    private static final Logger LOGGER;
    private final IntIntMap pets;
    private final IntMap<IntSet> servitors;
    
    private PlayerSummonTable() {
        this.pets = (IntIntMap)new CHashIntIntMap();
        this.servitors = (IntMap<IntSet>)new CHashIntMap();
    }
    
    public IntIntMap getPets() {
        return this.pets;
    }
    
    public IntMap<IntSet> getServitors() {
        return this.servitors;
    }
    
    public void init() {
        if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).restoreSummonOnReconnect()) {
            ((SummonDAO)DatabaseAccess.getDAO((Class)SummonDAO.class)).findAllSummonOwners().forEach(data -> ((IntSet)this.servitors.computeIfAbsent(data.getOwnerId(), id -> CHashIntMap.newKeySet())).add(data.getSummonId()));
            ((PetDAO)DatabaseAccess.getDAO((Class)PetDAO.class)).findAllPetOwnersByRestore().forEach(data -> this.pets.put(data.getOwnerId(), data.getItemObjectId()));
        }
    }
    
    public void removeServitor(final Player player, final int summonObjectId) {
        if (this.servitors.containsKey(player.getObjectId())) {
            ((IntSet)this.servitors.get(player.getObjectId())).remove(summonObjectId);
            ((SummonDAO)DatabaseAccess.getDAO((Class)SummonDAO.class)).deleteByIdAndOwner(summonObjectId, player.getObjectId());
        }
    }
    
    public void restorePet(final Player player) {
        final Item item = player.getInventory().getItemByObjectId(this.pets.get(player.getObjectId()));
        if (Objects.isNull(item)) {
            PlayerSummonTable.LOGGER.warn("Null pet summoning item for {}", (Object)player);
            return;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(item.getId());
        if (Objects.isNull(petData)) {
            PlayerSummonTable.LOGGER.warn("Null pet data for: {} and summoning item: {}", (Object)player, (Object)item);
            return;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
        if (Objects.isNull(npcTemplate)) {
            PlayerSummonTable.LOGGER.warn("Null pet NPC template for: {} and pet Id: {}", (Object)player, (Object)petData.getNpcId());
            return;
        }
        final Pet pet = Pet.spawnPet(npcTemplate, player, item);
        if (Objects.isNull(pet)) {
            PlayerSummonTable.LOGGER.warn("Null pet instance for: {} and pet NPC template: {}", (Object)player, (Object)npcTemplate);
            return;
        }
        pet.setShowSummonAnimation(true);
        pet.setTitle(player.getName());
        if (!pet.isRespawned()) {
            pet.setCurrentHp(pet.getMaxHp());
            pet.setCurrentMp(pet.getMaxMp());
            pet.getStats().setExp(pet.getExpForThisLevel());
            pet.setCurrentFed(pet.getMaxFed());
        }
        pet.setRunning();
        if (!pet.isRespawned()) {
            pet.storeMe();
        }
        item.setEnchantLevel(pet.getLevel());
        player.setPet(pet);
        pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
        pet.startFeed();
        pet.setFollowStatus(true);
        pet.getOwner().sendPacket(new PetItemList(pet.getInventory().getItems()));
        pet.broadcastStatusUpdate();
    }
    
    public void restoreServitor(final Player player) {
        final Skill skill;
        Servitor summon;
        ((SummonDAO)DatabaseAccess.getDAO((Class)SummonDAO.class)).findSummonsByOwner(player.getObjectId()).forEach(data -> {
            skill = SkillEngine.getInstance().getSkill(data.getSummonSkillId(), player.getSkillLevel(data.getSummonSkillId()));
            if (Objects.isNull(skill)) {
                this.removeServitor(player, data.getSummonId());
            }
            else {
                skill.applyEffects(player, player);
                if (player.hasServitors()) {
                    summon = player.getServitors().values().stream().map(s -> (Servitor)s).filter(s -> s.getReferenceSkill() == data.getSummonSkillId()).findAny().orElse(null);
                    if (Objects.nonNull(summon)) {
                        summon.setCurrentHp(data.getCurHp());
                        summon.setCurrentMp(data.getCurMp());
                        summon.setLifeTimeRemaining(data.getTime());
                    }
                }
            }
        });
    }
    
    public void saveSummon(final Servitor summon) {
        if (Objects.isNull(summon) || summon.getLifeTimeRemaining() <= 0) {
            return;
        }
        ((IntSet)this.servitors.computeIfAbsent(summon.getOwner().getObjectId(), k -> CHashIntMap.newKeySet())).add(summon.getObjectId());
        ((SummonDAO)DatabaseAccess.getDAO((Class)SummonDAO.class)).save(summon.getOwner().getObjectId(), summon.getObjectId(), summon.getReferenceSkill(), (int)summon.getCurrentHp(), (int)summon.getCurrentMp(), summon.getLifeTime());
    }
    
    public static PlayerSummonTable getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayerSummonTable.class);
    }
    
    private static class Singleton
    {
        private static final PlayerSummonTable INSTANCE;
        
        static {
            INSTANCE = new PlayerSummonTable();
        }
    }
}
