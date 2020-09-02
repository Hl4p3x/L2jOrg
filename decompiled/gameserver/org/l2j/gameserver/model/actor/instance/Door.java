// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.DamageInfo;
import java.util.PrimitiveIterator;
import java.util.Objects;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.OnEventTrigger;
import org.l2j.gameserver.network.serverpackets.DoorStatusUpdate;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.enums.DoorOpenType;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.DoorStats;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.DoorStatus;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.DoorAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.templates.DoorTemplate;
import java.util.concurrent.Future;
import org.l2j.gameserver.model.actor.Creature;

public final class Door extends Creature
{
    boolean open;
    private boolean _isAttackableDoor;
    private boolean inverted;
    private int _meshindex;
    private Future<?> _autoCloseTask;
    
    public Door(final DoorTemplate template) {
        super(template);
        this._meshindex = 1;
        this.setInstanceType(InstanceType.L2DoorInstance);
        this.setIsInvul(false);
        this.setLethalable(false);
        this.open = template.isOpenByDefault();
        this._isAttackableDoor = template.isAttackable();
        this.inverted = template.isInverted();
        super.setTargetable(template.isTargetable());
        if (this.isOpenableByTime()) {
            this.startTimerOpen();
        }
    }
    
    @Override
    protected CreatureAI initAI() {
        return new DoorAI(this);
    }
    
    @Override
    public void moveToLocation(final int x, final int y, final int z, final int offset) {
    }
    
    @Override
    public void stopMove(final Location loc) {
    }
    
    @Override
    public void doAutoAttack(final Creature target) {
    }
    
    @Override
    public void doCast(final Skill skill) {
    }
    
    private void startTimerOpen() {
        int delay = this.open ? this.getTemplate().getOpenTime() : this.getTemplate().getCloseTime();
        if (this.getTemplate().getRandomTime() > 0) {
            delay += Rnd.get(this.getTemplate().getRandomTime());
        }
        ThreadPool.schedule((Runnable)new TimerOpen(), (long)(delay * 1000));
    }
    
    @Override
    public DoorTemplate getTemplate() {
        return (DoorTemplate)super.getTemplate();
    }
    
    @Override
    public final DoorStatus getStatus() {
        return (DoorStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new DoorStatus(this));
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new DoorStats(this));
    }
    
    @Override
    public DoorStats getStats() {
        return (DoorStats)super.getStats();
    }
    
    public final boolean isOpenableBySkill() {
        return this.getTemplate().getOpenType() == DoorOpenType.BY_SKILL;
    }
    
    public final boolean isOpenableByItem() {
        return this.getTemplate().getOpenType() == DoorOpenType.BY_ITEM;
    }
    
    public final boolean isOpenableByClick() {
        return this.getTemplate().getOpenType() == DoorOpenType.BY_CLICK;
    }
    
    public final boolean isOpenableByTime() {
        return this.getTemplate().getOpenType() == DoorOpenType.BY_TIME;
    }
    
    public final boolean isOpenableByCycle() {
        return this.getTemplate().getOpenType() == DoorOpenType.BY_CYCLE;
    }
    
    @Override
    public final int getLevel() {
        return this.getTemplate().getLevel();
    }
    
    @Override
    public int getId() {
        return this.getTemplate().getId();
    }
    
    public boolean isOpen() {
        return this.open;
    }
    
    public void setOpen(final boolean open) {
        this.open = open;
        if (this.getChildId() > 0) {
            final Door sibling = this.getSiblingDoor(this.getChildId());
            if (sibling != null) {
                sibling.notifyChildEvent(open);
            }
            else {
                Door.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getChildId()));
            }
        }
    }
    
    public boolean getIsAttackableDoor() {
        return this._isAttackableDoor;
    }
    
    public void setIsAttackableDoor(final boolean val) {
        this._isAttackableDoor = val;
    }
    
    public boolean isInverted() {
        return this.inverted;
    }
    
    public boolean getIsShowHp() {
        return this.getTemplate().isShowHp();
    }
    
    public int getDamage() {
        if (this.getCastle() == null) {
            return 0;
        }
        final int dmg = 6 - (int)Math.ceil(this.getCurrentHp() / this.getMaxHp() * 6.0);
        if (dmg > 6) {
            return 6;
        }
        if (dmg < 0) {
            return 0;
        }
        return dmg;
    }
    
    public final Castle getCastle() {
        return CastleManager.getInstance().getCastle(this);
    }
    
    public boolean isEnemy() {
        return this.getCastle() != null && this.getCastle().getId() > 0 && this.getCastle().getZone().isActive() && this.getIsShowHp();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (!GameUtils.isPlayable(attacker)) {
            return false;
        }
        if (this._isAttackableDoor) {
            return true;
        }
        if (!this.getIsShowHp()) {
            return false;
        }
        final Player actingPlayer = attacker.getActingPlayer();
        final boolean isCastle = this.getCastle() != null && this.getCastle().getId() > 0 && this.getCastle().getZone().isActive();
        if (isCastle) {
            final Clan clan = actingPlayer.getClan();
            if (clan != null && clan.getId() == this.getCastle().getOwnerId()) {
                return false;
            }
        }
        return isCastle;
    }
    
    @Override
    public Item getActiveWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        return null;
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return null;
    }
    
    @Override
    public Weapon getSecondaryWeaponItem() {
        return null;
    }
    
    @Override
    public void broadcastStatusUpdate(final Creature caster) {
        final StaticObject su = new StaticObject(this, false);
        final StaticObject targetableSu = new StaticObject(this, true);
        final DoorStatusUpdate dsu = new DoorStatusUpdate(this);
        final OnEventTrigger oe = (this.getEmitter() <= 0) ? null : new OnEventTrigger(this.getEmitter(), this.inverted ^ this.open);
        World.getInstance().forAnyVisibleObject(this, Player.class, player -> this.sendUpdateToPlayer(player, su, targetableSu, dsu, oe), this::isVisibleFor);
    }
    
    private void sendUpdateToPlayer(final Player player, final StaticObject su, final StaticObject targetableSu, final DoorStatusUpdate dsu, final OnEventTrigger oe) {
        if (player.isGM() || Util.falseIfNullOrElse((Object)this.getCastle(), c -> c.getId() > 0)) {
            player.sendPacket(targetableSu);
        }
        else {
            player.sendPacket(su);
        }
        player.sendPacket(dsu);
        if (oe != null) {
            player.sendPacket(oe);
        }
    }
    
    public final void openCloseMe(final boolean open) {
        if (open) {
            this.openMe();
        }
        else {
            this.closeMe();
        }
    }
    
    public final void openMe() {
        if (this.getGroupName() != null) {
            this.manageGroupOpen(true, this.getGroupName());
            return;
        }
        this.setOpen(true);
        this.broadcastStatusUpdate();
        this.startAutoCloseTask();
    }
    
    public final void closeMe() {
        final Future<?> oldTask = this._autoCloseTask;
        if (oldTask != null) {
            this._autoCloseTask = null;
            oldTask.cancel(false);
        }
        if (this.getGroupName() != null) {
            this.manageGroupOpen(false, this.getGroupName());
            return;
        }
        this.setOpen(false);
        this.broadcastStatusUpdate();
    }
    
    private void manageGroupOpen(final boolean open, final String groupName) {
        final PrimitiveIterator.OfInt doorsId = DoorDataManager.getInstance().getDoorsByGroup(groupName).iterator();
        Door first = null;
        while (doorsId.hasNext()) {
            final Door door = this.getSiblingDoor(doorsId.nextInt());
            if (Objects.isNull(first)) {
                first = door;
            }
            if (door.isOpen() != open) {
                door.setOpen(open);
                door.broadcastStatusUpdate();
            }
        }
        if (Objects.nonNull(first) && open) {
            first.startAutoCloseTask();
        }
    }
    
    private void notifyChildEvent(final boolean open) {
        final byte openThis = open ? this.getTemplate().getMasterDoorOpen() : this.getTemplate().getMasterDoorClose();
        if (openThis == 1) {
            this.openMe();
        }
        else if (openThis == -1) {
            this.closeMe();
        }
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this.getTemplate().getId(), this.getObjectId());
    }
    
    @Override
    public String getName() {
        return this.getTemplate().getName();
    }
    
    public int getX(final int i) {
        return this.getTemplate().getNodeX()[i];
    }
    
    public int getY(final int i) {
        return this.getTemplate().getNodeY()[i];
    }
    
    public int getZMin() {
        return this.getTemplate().getNodeZ();
    }
    
    public int getZMax() {
        return this.getTemplate().getNodeZ() + this.getTemplate().getHeight();
    }
    
    public int getMeshIndex() {
        return this._meshindex;
    }
    
    public void setMeshIndex(final int mesh) {
        this._meshindex = mesh;
    }
    
    public int getEmitter() {
        return this.getTemplate().getEmmiter();
    }
    
    public boolean isWall() {
        return this.getTemplate().isWall();
    }
    
    public String getGroupName() {
        return this.getTemplate().getGroupName();
    }
    
    public int getChildId() {
        return this.getTemplate().getChildDoorId();
    }
    
    @Override
    public void reduceCurrentHp(final double value, final Creature attacker, final Skill skill, final boolean isDOT, final boolean directlyToHp, final boolean critical, final boolean reflect, final DamageInfo.DamageType damageType) {
        if (this.isWall() && !this.isInInstance()) {
            if (!attacker.isServitor()) {
                return;
            }
            final Servitor servitor = (Servitor)attacker;
            if (servitor.getTemplate().getRace() != Race.SIEGE_WEAPON) {
                return;
            }
        }
        super.reduceCurrentHp(value, attacker, skill, isDOT, directlyToHp, critical, reflect, damageType);
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        final boolean isCastle = this.getCastle() != null && this.getCastle().getId() > 0 && this.getCastle().getSiege().isInProgress();
        if (isCastle) {
            this.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_CASTLE_GATE_HAS_BEEN_DESTROYED));
        }
        else {
            this.openMe();
        }
        return true;
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        if (this.isVisibleFor(activeChar)) {
            if (this.getEmitter() > 0) {
                if (this.inverted) {
                    activeChar.sendPacket(new OnEventTrigger(this.getEmitter(), !this.open));
                }
                else {
                    activeChar.sendPacket(new OnEventTrigger(this.getEmitter(), this.open));
                }
            }
            activeChar.sendPacket(new StaticObject(this, activeChar.isGM()));
        }
    }
    
    @Override
    public void setTargetable(final boolean targetable) {
        super.setTargetable(targetable);
        this.broadcastStatusUpdate();
    }
    
    public boolean checkCollision() {
        return this.getTemplate().isCheckCollision();
    }
    
    private Door getSiblingDoor(final int doorId) {
        final Instance inst = this.getInstanceWorld();
        return (inst != null) ? inst.getDoor(doorId) : DoorDataManager.getInstance().getDoor(doorId);
    }
    
    private void startAutoCloseTask() {
        if (this.getTemplate().getCloseTime() < 0 || this.isOpenableByTime()) {
            return;
        }
        final Future<?> oldTask = this._autoCloseTask;
        if (oldTask != null) {
            this._autoCloseTask = null;
            oldTask.cancel(false);
        }
        this._autoCloseTask = (Future<?>)ThreadPool.schedule((Runnable)new AutoClose(), (long)(this.getTemplate().getCloseTime() * 1000));
    }
    
    class AutoClose implements Runnable
    {
        @Override
        public void run() {
            if (Door.this.open) {
                Door.this.closeMe();
            }
        }
    }
    
    class TimerOpen implements Runnable
    {
        @Override
        public void run() {
            if (Door.this.open) {
                Door.this.closeMe();
            }
            else {
                Door.this.openMe();
            }
            int delay = Door.this.open ? Door.this.getTemplate().getCloseTime() : Door.this.getTemplate().getOpenTime();
            if (Door.this.getTemplate().getRandomTime() > 0) {
                delay += Rnd.get(Door.this.getTemplate().getRandomTime());
            }
            ThreadPool.schedule((Runnable)this, (long)(delay * 1000));
        }
    }
}
