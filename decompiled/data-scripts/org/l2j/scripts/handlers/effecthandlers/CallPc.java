// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Objects;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.holders.SummonRequest;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;

public final class CallPc extends AbstractEffect
{
    private final int itemId;
    private final int itemCount;
    
    private CallPc(final StatsSet params) {
        this.itemId = params.getInt("item", 0);
        this.itemCount = params.getInt("item-count", 0);
    }
    
    public boolean isInstant() {
        return true;
    }
    
    public void instant(final Creature effector, final Creature effected, final Skill skill, final Item item) {
        if (effector == effected) {
            return;
        }
        final Player target = effected.getActingPlayer();
        final Player player = effector.getActingPlayer();
        if (checkSummonTargetStatus(target, (Creature)player)) {
            if (this.itemId != 0 && this.itemCount != 0) {
                if (target.getInventory().getInventoryItemCount(this.itemId, 0) < this.itemCount) {
                    target.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_IS_REQUIRED_FOR_SUMMONING).addItemName(this.itemId) });
                    return;
                }
                target.getInventory().destroyItemByItemId("Consume", this.itemId, (long)this.itemCount, player, (Object)target);
                target.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED).addItemName(this.itemId) });
            }
            target.addRequest((AbstractRequest)new SummonRequest(player, skill));
            target.sendPacket(new ServerPacket[] { (ServerPacket)((ConfirmDlg)((ConfirmDlg)new ConfirmDlg(SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT).addString(player.getName())).addZoneName(player.getX(), player.getY(), player.getZ())).addTime(30000).addRequesterId(player.getObjectId()) });
        }
    }
    
    public static boolean checkSummonTargetStatus(final Player target, final Creature creature) {
        if (target == creature) {
            return false;
        }
        if (target.isAlikeDead()) {
            creature.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target) });
            return false;
        }
        if (target.isInStoreMode()) {
            creature.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target) });
            return false;
        }
        if (target.isRooted() || target.isInCombat()) {
            creature.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED).addPcName(target) });
            return false;
        }
        if (target.isInOlympiadMode()) {
            creature.sendPacket(SystemMessageId.A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING);
            return false;
        }
        if (target.isFlyingMounted() || target.isInTimedHuntingZone()) {
            creature.sendPacket(SystemMessageId.YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA);
            return false;
        }
        if (target.inObserverMode() || OlympiadManager.getInstance().isRegisteredInComp(target) || target.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || target.isInsideZone(ZoneType.JAIL)) {
            creature.sendPacket(new ServerPacket[] { (ServerPacket)SystemMessage.getSystemMessage(SystemMessageId.C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING).addString(target.getName()) });
            return false;
        }
        final Instance instance = creature.getInstanceWorld();
        if (Objects.nonNull(instance) && !instance.isPlayerSummonAllowed()) {
            creature.sendPacket(SystemMessageId.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION);
            return false;
        }
        return true;
    }
    
    public static class Factory implements SkillEffectFactory
    {
        public AbstractEffect create(final StatsSet data) {
            return new CallPc(data);
        }
        
        public String effectName() {
            return "call-pc";
        }
    }
}
