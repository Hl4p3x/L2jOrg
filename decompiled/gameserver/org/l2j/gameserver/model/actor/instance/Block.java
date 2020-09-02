// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.network.serverpackets.ExCubeGameExtendedChangePoints;
import org.l2j.gameserver.network.serverpackets.ExCubeGameChangePoints;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.entity.BlockCheckerEngine;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

public class Block extends Monster
{
    private int _colorEffect;
    
    public Block(final NpcTemplate template) {
        super(template);
    }
    
    public void changeColor(final Player attacker, final ArenaParticipantsHolder holder, final int team) {
        synchronized (this) {
            final BlockCheckerEngine event = holder.getEvent();
            if (this._colorEffect == 83) {
                this._colorEffect = 0;
                this.broadcastPacket(new NpcInfo(this));
                this.increaseTeamPointsAndSend(attacker, team, event);
            }
            else {
                this._colorEffect = 83;
                this.broadcastPacket(new NpcInfo(this));
                this.increaseTeamPointsAndSend(attacker, team, event);
            }
            final int random = Rnd.get(100);
            if (random > 69 && random <= 84) {
                this.dropItem(13787, event, attacker);
            }
            else if (random > 84) {
                this.dropItem(13788, event, attacker);
            }
        }
    }
    
    public void setRed(final boolean isRed) {
        this._colorEffect = (isRed ? 83 : 0);
    }
    
    @Override
    public int getColorEffect() {
        return this._colorEffect;
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        return !GameUtils.isPlayer(attacker) || (attacker.getActingPlayer() != null && attacker.getActingPlayer().getBlockCheckerArena() > -1);
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        return false;
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (!this.canTarget(player)) {
            return;
        }
        player.setLastFolkNPC(this);
        if (player.getTarget() != this) {
            player.setTarget(this);
            this.getAI();
        }
        else if (interact) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    private void increaseTeamPointsAndSend(final Player player, final int team, final BlockCheckerEngine eng) {
        eng.increasePlayerPoints(player, team);
        final int timeLeft = (int)((eng.getStarterTime() - System.currentTimeMillis()) / 1000L);
        final boolean isRed = eng.getHolder().getRedPlayers().contains(player);
        final ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints());
        final ExCubeGameExtendedChangePoints secretPoints = new ExCubeGameExtendedChangePoints(timeLeft, eng.getBluePoints(), eng.getRedPoints(), isRed, player, eng.getPlayerPoints(player, isRed));
        eng.getHolder().broadCastPacketToTeam(changePoints);
        eng.getHolder().broadCastPacketToTeam(secretPoints);
    }
    
    private void dropItem(final int id, final BlockCheckerEngine eng, final Player player) {
        final Item drop = ItemEngine.getInstance().createItem("Loot", id, 1L, player, this);
        final int x = this.getX() + Rnd.get(50);
        final int y = this.getY() + Rnd.get(50);
        final int z = this.getZ();
        drop.dropMe(this, x, y, z);
        eng.addNewDrop(drop);
    }
}
