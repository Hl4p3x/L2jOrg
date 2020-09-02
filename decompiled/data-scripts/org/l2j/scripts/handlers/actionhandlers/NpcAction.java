// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.scripts.handlers.actionhandlers;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.Location;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.MoveToPawn;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.handler.IActionHandler;

public class NpcAction implements IActionHandler
{
    public boolean action(final Player player, final WorldObject target, final boolean interact) {
        if (!((Npc)target).canTarget(player)) {
            return false;
        }
        player.setLastFolkNPC((Npc)target);
        if (target != player.getTarget()) {
            player.setTarget(target);
            if (target.isAutoAttackable((Creature)player)) {
                ((Npc)target).getAI();
            }
        }
        else if (interact) {
            if (target.isAutoAttackable((Creature)player) && !((Creature)target).isAlikeDead()) {
                if (GeoEngine.getInstance().canSeeTarget((WorldObject)player, target)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, new Object[] { target });
                }
                else {
                    player.sendPacket(new ServerPacket[] { (ServerPacket)ActionFailed.STATIC_PACKET });
                }
            }
            else if (!target.isAutoAttackable((Creature)player)) {
                if (!((Npc)target).canInteract(player)) {
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, new Object[] { target });
                }
                else {
                    final Npc npc = (Npc)target;
                    if (!player.isSitting()) {
                        player.sendPacket(new ServerPacket[] { (ServerPacket)new MoveToPawn((Creature)player, (WorldObject)npc, 100) });
                        if (npc.hasRandomAnimation()) {
                            npc.onRandomAnimation(Rnd.get(8));
                        }
                    }
                    if (npc.isMoving()) {
                        player.stopMove((Location)null);
                    }
                    if (npc.hasVariables() && npc.getVariables().getBoolean("eventmob", false)) {
                        Event.showEventHtml(player, String.valueOf(target.getObjectId()));
                    }
                    else {
                        if (npc.hasListener(EventType.ON_NPC_QUEST_START)) {
                            player.setLastQuestNpcObject(target.getObjectId());
                        }
                        if (npc.hasListener(EventType.ON_NPC_FIRST_TALK)) {
                            EventDispatcher.getInstance().notifyEventAsync((IBaseEvent)new OnNpcFirstTalk(npc, player), new ListenersContainer[] { (ListenersContainer)npc });
                        }
                        else {
                            npc.showChatWindow(player);
                        }
                    }
                    if (Config.PLAYER_MOVEMENT_BLOCK_TIME > 0) {
                        player.updateNotMoveUntil();
                    }
                }
            }
        }
        return true;
    }
    
    public InstanceType getInstanceType() {
        return InstanceType.L2Npc;
    }
}
