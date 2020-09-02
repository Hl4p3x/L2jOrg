// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.itemhandlers;

import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.actor.instance.Block;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.handler.IItemHandler;

public class EventItem implements IItemHandler
{
    public boolean useItem(final Playable playable, final Item item, final boolean forceUse) {
        if (!GameUtils.isPlayer((WorldObject)playable)) {
            playable.sendPacket(SystemMessageId.YOUR_PET_CANNOT_CARRY_THIS_ITEM);
            return false;
        }
        boolean used = false;
        final Player activeChar = playable.getActingPlayer();
        final int itemId = item.getId();
        switch (itemId) {
            case 13787: {
                used = this.useBlockCheckerItem(activeChar, item);
                break;
            }
            case 13788: {
                used = this.useBlockCheckerItem(activeChar, item);
                break;
            }
            default: {
                EventItem.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, itemId));
                break;
            }
        }
        return used;
    }
    
    private final boolean useBlockCheckerItem(final Player castor, final Item item) {
        final int blockCheckerArena = castor.getBlockCheckerArena();
        if (blockCheckerArena == -1) {
            final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS);
            msg.addItemName(item);
            castor.sendPacket(new ServerPacket[] { (ServerPacket)msg });
            return false;
        }
        final Skill sk = item.getSkills(ItemSkillType.NORMAL).get(0).getSkill();
        if (sk == null) {
            return false;
        }
        if (!castor.destroyItem("Consume", item, 1L, (WorldObject)castor, true)) {
            return false;
        }
        final Block block = (Block)castor.getTarget();
        final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(blockCheckerArena);
        if (holder != null) {
            final int team = holder.getPlayerTeam(castor);
            final ArenaParticipantsHolder arenaParticipantsHolder;
            final int enemyTeam;
            final int n;
            final Skill skill;
            World.getInstance().forEachVisibleObjectInRange((WorldObject)block, (Class)Player.class, sk.getEffectRange(), pc -> {
                enemyTeam = arenaParticipantsHolder.getPlayerTeam(pc);
                if (enemyTeam != -1 && enemyTeam != n) {
                    skill.applyEffects((Creature)castor, (Creature)pc);
                }
                return;
            });
            return true;
        }
        EventItem.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, castor.getName(), castor.getObjectId()));
        return false;
    }
}
