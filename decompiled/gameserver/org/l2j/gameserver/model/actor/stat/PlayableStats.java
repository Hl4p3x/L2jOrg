// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.stat;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExNewSkillToLearnByLevelUp;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.slf4j.Logger;

public class PlayableStats extends CreatureStats
{
    protected static final Logger LOGGER;
    
    public PlayableStats(final Playable activeChar) {
        super(activeChar);
    }
    
    public boolean addExp(long value) {
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayableExpChanged(this.getCreature(), this.getExp(), this.getExp() + value), this.getCreature(), TerminateReturn.class);
        if (term != null && term.terminate()) {
            return false;
        }
        final long maxExp = LevelData.getInstance().getMaxExp();
        if (this.getExp() + value < 0L || (value > 0L && this.getExp() == maxExp)) {
            return true;
        }
        if (this.getExp() + value >= maxExp) {
            value = maxExp - 1L - this.getExp();
        }
        final int oldLevel = this.getLevel();
        this.setExp(this.getExp() + value);
        byte minimumLevel = 1;
        if (GameUtils.isPet(this.getCreature())) {
            minimumLevel = (byte)PetDataTable.getInstance().getPetMinLevel(((Pet)this.getCreature()).getTemplate().getId());
        }
        byte tmp;
        byte level;
        for (level = (tmp = minimumLevel); tmp <= this.getMaxLevel() + 1; ++tmp) {
            if (this.getExp() < this.getExpForLevel(tmp)) {
                tmp = (level = (byte)(tmp - 1));
                break;
            }
        }
        if (level != this.getLevel() && level >= minimumLevel) {
            this.addLevel((byte)(level - this.getLevel()));
        }
        if (this.getLevel() > oldLevel && GameUtils.isPlayer(this.getCreature())) {
            final Player activeChar = this.getCreature().getActingPlayer();
            if (SkillTreesData.getInstance().hasAvailableSkills(activeChar, activeChar.getClassId())) {
                this.getCreature().sendPacket(ExNewSkillToLearnByLevelUp.STATIC_PACKET);
            }
        }
        return true;
    }
    
    public boolean removeExp(long value) {
        if (this.getExp() - value < this.getExpForLevel(this.getLevel()) && !((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).delevel()) {
            value = this.getExp() - this.getExpForLevel(this.getLevel());
        }
        if (this.getExp() - value < 0L) {
            value = this.getExp() - 1L;
        }
        this.setExp(this.getExp() - value);
        byte minimumLevel = 1;
        if (GameUtils.isPet(this.getCreature())) {
            minimumLevel = (byte)PetDataTable.getInstance().getPetMinLevel(((Pet)this.getCreature()).getTemplate().getId());
        }
        byte tmp;
        byte level;
        for (level = (tmp = minimumLevel); tmp <= this.getMaxLevel(); ++tmp) {
            if (this.getExp() < this.getExpForLevel(tmp)) {
                tmp = (level = (byte)(tmp - 1));
                break;
            }
        }
        if (level != this.getLevel() && level >= minimumLevel) {
            this.addLevel((byte)(level - this.getLevel()));
        }
        return true;
    }
    
    public boolean removeExpAndSp(final long removeExp, final long removeSp) {
        boolean expRemoved = false;
        boolean spRemoved = false;
        if (removeExp > 0L) {
            expRemoved = this.removeExp(removeExp);
        }
        if (removeSp > 0L) {
            spRemoved = this.removeSp(removeSp);
        }
        return expRemoved || spRemoved;
    }
    
    public boolean addLevel(byte value) {
        if (this.getLevel() + value > this.getMaxLevel()) {
            if (this.getLevel() >= this.getMaxLevel() - 1) {
                return false;
            }
            value = (byte)(this.getMaxLevel() - 1 - this.getLevel());
        }
        final boolean levelIncreased = this.getLevel() + value > this.getLevel();
        value += this.getLevel();
        this.setLevel(value);
        if (this.getExp() >= this.getExpForLevel(this.getLevel() + 1) || this.getExpForLevel(this.getLevel()) > this.getExp()) {
            this.setExp(this.getExpForLevel(this.getLevel()));
        }
        if (!levelIncreased && GameUtils.isPlayer(this.getCreature()) && !this.getCreature().isGM()) {
            ((Player)this.getCreature()).checkPlayerSkills();
        }
        if (!levelIncreased) {
            return false;
        }
        this.getCreature().getStatus().setCurrentHp(this.getCreature().getStats().getMaxHp());
        this.getCreature().getStatus().setCurrentMp(this.getCreature().getStats().getMaxMp());
        return true;
    }
    
    public boolean addSp(long value) {
        if (value < 0L) {
            PlayableStats.LOGGER.warn("wrong usage");
            return false;
        }
        final long currentSp = this.getSp();
        if (currentSp >= Config.MAX_SP) {
            return false;
        }
        if (currentSp > Config.MAX_SP - value) {
            value = Config.MAX_SP - currentSp;
        }
        this.setSp(currentSp + value);
        return true;
    }
    
    public boolean removeSp(long value) {
        final long currentSp = this.getSp();
        if (currentSp < value) {
            value = currentSp;
        }
        this.setSp(this.getSp() - value);
        return true;
    }
    
    public long getExpForLevel(final int level) {
        return LevelData.getInstance().getExpForLevel(level);
    }
    
    @Override
    public Playable getCreature() {
        return (Playable)super.getCreature();
    }
    
    public int getMaxLevel() {
        return LevelData.getInstance().getMaxLevel();
    }
    
    @Override
    public int getPhysicalAttackRadius() {
        final Weapon weapon = this.getCreature().getActiveWeaponItem();
        return (weapon != null) ? weapon.getBaseAttackRadius() : super.getPhysicalAttackRadius();
    }
    
    @Override
    public int getPhysicalAttackAngle() {
        final Weapon weapon = this.getCreature().getActiveWeaponItem();
        return (weapon != null) ? weapon.getBaseAttackAngle() : super.getPhysicalAttackAngle();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PlayableStats.class);
    }
}
