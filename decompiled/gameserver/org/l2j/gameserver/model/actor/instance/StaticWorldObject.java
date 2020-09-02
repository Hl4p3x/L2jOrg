// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.StaticObject;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.StaticObjStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.StaticObjStats;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.network.serverpackets.ShowTownMap;
import org.l2j.gameserver.model.actor.Creature;

public final class StaticWorldObject extends Creature
{
    public static final int INTERACTION_DISTANCE = 150;
    private final int _staticObjectId;
    private int _meshIndex;
    private int _type;
    private ShowTownMap _map;
    
    public StaticWorldObject(final CreatureTemplate template, final int staticId) {
        super(template);
        this._meshIndex = 0;
        this._type = -1;
        this.setInstanceType(InstanceType.L2StaticObjectInstance);
        this._staticObjectId = staticId;
    }
    
    @Override
    protected CreatureAI initAI() {
        return null;
    }
    
    @Override
    public int getId() {
        return this._staticObjectId;
    }
    
    @Override
    public final StaticObjStats getStats() {
        return (StaticObjStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new StaticObjStats(this));
    }
    
    @Override
    public final StaticObjStatus getStatus() {
        return (StaticObjStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new StaticObjStatus(this));
    }
    
    public int getType() {
        return this._type;
    }
    
    public void setType(final int type) {
        this._type = type;
    }
    
    public void setMap(final String texture, final int x, final int y) {
        this._map = new ShowTownMap(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, texture), x, y);
    }
    
    public ShowTownMap getMap() {
        return this._map;
    }
    
    @Override
    public final int getLevel() {
        return 1;
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
    public boolean isAutoAttackable(final Creature attacker) {
        return false;
    }
    
    public int getMeshIndex() {
        return this._meshIndex;
    }
    
    public void setMeshIndex(final int meshIndex) {
        this._meshIndex = meshIndex;
        this.broadcastPacket(new StaticObject(this));
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        activeChar.sendPacket(new StaticObject(this));
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
}
