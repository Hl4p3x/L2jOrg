// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.PetItemList;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import java.util.Objects;
import org.l2j.gameserver.model.holders.PetItemRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class SummonPet extends AbstractEffect
{
    private SummonPet() {
    }
    
    public EffectType getEffectType() {
        return EffectType.SUMMON_PET;
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (!GameUtils.isPlayer((WorldObject)effector) || !GameUtils.isPlayer((WorldObject)effected) || effected.isAlikeDead()) {
            return;
        }
        final Player player = effector.getActingPlayer();
        if (player.hasPet() || player.isMounted()) {
            player.sendPacket(SystemMessageId.YOU_ALREADY_HAVE_A_PET);
            return;
        }
        final PetItemRequest request = (PetItemRequest)player.getRequest((Class)PetItemRequest.class);
        if (Objects.isNull(request)) {
            SummonPet.LOGGER.warn("Summoning pet without attaching PetItemHandler!", new Throwable());
            return;
        }
        player.removeRequest((Class)PetItemRequest.class);
        final Item collar = request.getItem();
        if (player.getInventory().getItemByObjectId(collar.getObjectId()) != collar) {
            SummonPet.LOGGER.warn("Player: {} is trying to summon pet from item that he doesn't owns.", (Object)player);
            return;
        }
        final PetData petData = PetDataTable.getInstance().getPetDataByItemId(collar.getId());
        if (Objects.isNull(petData) || petData.getNpcId() == -1) {
            return;
        }
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(petData.getNpcId());
        final Pet pet = Pet.spawnPet(npcTemplate, player, collar);
        pet.setShowSummonAnimation(true);
        if (!pet.isRespawned()) {
            pet.setCurrentHp((double)pet.getMaxHp());
            pet.setCurrentMp((double)pet.getMaxMp());
            pet.getStats().setExp(pet.getExpForThisLevel());
            pet.setCurrentFed(pet.getMaxFed());
        }
        pet.setRunning();
        if (!pet.isRespawned()) {
            pet.storeMe();
        }
        collar.setEnchantLevel(pet.getLevel());
        player.setPet(pet);
        pet.spawnMe(player.getX() + 50, player.getY() + 100, player.getZ());
        pet.startFeed();
        pet.setFollowStatus(true);
        pet.getOwner().sendPacket(new ServerPacket[] { (ServerPacket)new PetItemList(pet.getInventory().getItems()) });
        pet.broadcastStatusUpdate();
    }
    
    public static class Factory implements SkillEffectFactory
    {
        private static final SummonPet INSTANCE;
        
        public AbstractEffect create(final StatsSet data) {
            return Factory.INSTANCE;
        }
        
        public String effectName() {
            return "SummonPet";
        }
        
        static {
            INSTANCE = new SummonPet();
        }
    }
}
