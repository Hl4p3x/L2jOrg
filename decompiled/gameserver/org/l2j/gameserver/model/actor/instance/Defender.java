// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.engine.geo.GeoEngine;
import java.util.Comparator;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import java.util.Objects;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.actor.Attackable;

public class Defender extends Attackable
{
    private Castle _castle;
    
    public Defender(final NpcTemplate template) {
        super(template);
        this._castle = null;
        this.setInstanceType(InstanceType.L2DefenderInstance);
    }
    
    @Override
    public void addDamage(final Creature attacker, final int damage, final Skill skill) {
        super.addDamage(attacker, damage, skill);
        World.getInstance().forEachVisibleObjectInRange(this, Defender.class, 500, defender -> defender.addDamageHate(attacker, 0, 10));
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (!GameUtils.isPlayable(attacker)) {
            return false;
        }
        final Player player = attacker.getActingPlayer();
        if (Objects.nonNull(this._castle) && this._castle.getZone().isActive()) {
            final int activeSiegeId = this._castle.getId();
            return player != null && ((player.getSiegeState() == 2 && !player.isRegisteredOnThisSiegeField(activeSiegeId)) || player.getSiegeState() == 1 || player.getSiegeState() == 0);
        }
        return false;
    }
    
    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
    
    @Override
    public void returnHome() {
        if (this.getWalkSpeed() <= 0.0) {
            return;
        }
        if (this.getSpawn() == null) {
            return;
        }
        if (!MathUtil.isInsideRadius2D(this, this.getSpawn(), 40)) {
            this.setisReturningToSpawnPoint(true);
            this.clearAggroList();
            if (this.hasAI()) {
                this.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, this.getSpawn().getLocation());
            }
        }
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this._castle = CastleManager.getInstance().getCastle(this);
        if (this._castle == null) {
            Defender.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Defender;)Ljava/lang/String;, this));
        }
    }
    
    @Override
    public void onAction(final Player player, final boolean interact) {
        if (!this.canTarget(player)) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (this != player.getTarget()) {
            player.setTarget(this);
        }
        else if (interact) {
            if (this.isAutoAttackable(player) && !this.isAlikeDead() && Math.abs(player.getZ() - this.getZ()) < 600) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, this);
            }
            if (!this.isAutoAttackable(player) && !this.canInteract(player)) {
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
            }
        }
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    @Override
    public void useMagic(final Skill skill) {
        if (!skill.isBad()) {
            final Creature target = World.getInstance().findFirstVisibleObject(this, Creature.class, skill.getCastRange(), false, this::isSkillTargetable, Comparator.comparingDouble(Creature::getCurrentHp));
            this.setTarget(target);
        }
        super.useMagic(skill);
    }
    
    private boolean isSkillTargetable(final Creature creature) {
        if (creature.isDead() || !GeoEngine.getInstance().canSeeTarget(this, creature)) {
            return false;
        }
        if (creature instanceof Defender) {
            return true;
        }
        if (GameUtils.isPlayer(creature)) {
            final Player player = (Player)creature;
            return player.getSiegeState() == 2 && !player.isRegisteredOnThisSiegeField(this.getScriptValue());
        }
        return false;
    }
    
    @Override
    public void addDamageHate(final Creature attacker, final int damage, final int aggro) {
        if (attacker == null) {
            return;
        }
        if (!(attacker instanceof Defender)) {
            if (damage == 0 && aggro <= 1 && GameUtils.isPlayable(attacker)) {
                final Player player = attacker.getActingPlayer();
                if (Objects.nonNull(this._castle) && this._castle.getZone().isActive()) {
                    final int activeSiegeId = this._castle.getId();
                    if (player != null && ((player.getSiegeState() == 2 && player.isRegisteredOnThisSiegeField(activeSiegeId)) || player.getSiegeState() == 1)) {
                        return;
                    }
                }
            }
            super.addDamageHate(attacker, damage, aggro);
        }
    }
}
