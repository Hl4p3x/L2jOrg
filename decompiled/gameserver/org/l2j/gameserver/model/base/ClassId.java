// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.base;

import io.github.joealisson.primitive.HashIntMap;
import java.util.HashSet;
import io.github.joealisson.primitive.IntMap;
import java.util.Set;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public enum ClassId implements IIdentifiable
{
    FIGHTER(0, false, Race.HUMAN, (ClassId)null), 
    WARRIOR(1, false, Race.HUMAN, ClassId.FIGHTER), 
    GLADIATOR(2, false, Race.HUMAN, ClassId.WARRIOR), 
    WARLORD(3, false, Race.HUMAN, ClassId.WARRIOR), 
    KNIGHT(4, false, Race.HUMAN, ClassId.FIGHTER), 
    PALADIN(5, false, Race.HUMAN, ClassId.KNIGHT), 
    DARK_AVENGER(6, false, Race.HUMAN, ClassId.KNIGHT), 
    ROGUE(7, false, Race.HUMAN, ClassId.FIGHTER), 
    TREASURE_HUNTER(8, false, Race.HUMAN, ClassId.ROGUE), 
    HAWKEYE(9, false, Race.HUMAN, ClassId.ROGUE), 
    MAGE(10, true, Race.HUMAN, (ClassId)null), 
    WIZARD(11, true, Race.HUMAN, ClassId.MAGE), 
    SORCERER(12, true, Race.HUMAN, ClassId.WIZARD), 
    NECROMANCER(13, true, Race.HUMAN, ClassId.WIZARD), 
    WARLOCK(14, true, true, Race.HUMAN, ClassId.WIZARD), 
    CLERIC(15, true, Race.HUMAN, ClassId.MAGE), 
    BISHOP(16, true, Race.HUMAN, ClassId.CLERIC), 
    PROPHET(17, true, Race.HUMAN, ClassId.CLERIC), 
    ELVEN_FIGHTER(18, false, Race.ELF, (ClassId)null), 
    ELVEN_KNIGHT(19, false, Race.ELF, ClassId.ELVEN_FIGHTER), 
    TEMPLE_KNIGHT(20, false, Race.ELF, ClassId.ELVEN_KNIGHT), 
    SWORDSINGER(21, false, Race.ELF, ClassId.ELVEN_KNIGHT), 
    ELVEN_SCOUT(22, false, Race.ELF, ClassId.ELVEN_FIGHTER), 
    PLAINS_WALKER(23, false, Race.ELF, ClassId.ELVEN_SCOUT), 
    SILVER_RANGER(24, false, Race.ELF, ClassId.ELVEN_SCOUT), 
    ELVEN_MAGE(25, true, Race.ELF, (ClassId)null), 
    ELVEN_WIZARD(26, true, Race.ELF, ClassId.ELVEN_MAGE), 
    SPELLSINGER(27, true, Race.ELF, ClassId.ELVEN_WIZARD), 
    ELEMENTAL_SUMMONER(28, true, true, Race.ELF, ClassId.ELVEN_WIZARD), 
    ORACLE(29, true, Race.ELF, ClassId.ELVEN_MAGE), 
    ELDER(30, true, Race.ELF, ClassId.ORACLE), 
    DARK_FIGHTER(31, false, Race.DARK_ELF, (ClassId)null), 
    PALUS_KNIGHT(32, false, Race.DARK_ELF, ClassId.DARK_FIGHTER), 
    SHILLIEN_KNIGHT(33, false, Race.DARK_ELF, ClassId.PALUS_KNIGHT), 
    BLADEDANCER(34, false, Race.DARK_ELF, ClassId.PALUS_KNIGHT), 
    ASSASSIN(35, false, Race.DARK_ELF, ClassId.DARK_FIGHTER), 
    ABYSS_WALKER(36, false, Race.DARK_ELF, ClassId.ASSASSIN), 
    PHANTOM_RANGER(37, false, Race.DARK_ELF, ClassId.ASSASSIN), 
    DARK_MAGE(38, true, Race.DARK_ELF, (ClassId)null), 
    DARK_WIZARD(39, true, Race.DARK_ELF, ClassId.DARK_MAGE), 
    SPELLHOWLER(40, true, Race.DARK_ELF, ClassId.DARK_WIZARD), 
    PHANTOM_SUMMONER(41, true, true, Race.DARK_ELF, ClassId.DARK_WIZARD), 
    SHILLIEN_ORACLE(42, true, Race.DARK_ELF, ClassId.DARK_MAGE), 
    SHILLIEN_ELDER(43, true, Race.DARK_ELF, ClassId.SHILLIEN_ORACLE), 
    ORC_FIGHTER(44, false, Race.ORC, (ClassId)null), 
    ORC_RAIDER(45, false, Race.ORC, ClassId.ORC_FIGHTER), 
    DESTROYER(46, false, Race.ORC, ClassId.ORC_RAIDER), 
    ORC_MONK(47, false, Race.ORC, ClassId.ORC_FIGHTER), 
    TYRANT(48, false, Race.ORC, ClassId.ORC_MONK), 
    ORC_MAGE(49, true, Race.ORC, (ClassId)null), 
    ORC_SHAMAN(50, true, Race.ORC, ClassId.ORC_MAGE), 
    OVERLORD(51, true, Race.ORC, ClassId.ORC_SHAMAN), 
    WARCRYER(52, true, Race.ORC, ClassId.ORC_SHAMAN), 
    DWARVEN_FIGHTER(53, false, Race.DWARF, (ClassId)null), 
    SCAVENGER(54, false, Race.DWARF, ClassId.DWARVEN_FIGHTER), 
    BOUNTY_HUNTER(55, false, Race.DWARF, ClassId.SCAVENGER), 
    ARTISAN(56, false, Race.DWARF, ClassId.DWARVEN_FIGHTER), 
    WARSMITH(57, false, Race.DWARF, ClassId.ARTISAN), 
    DUELIST(88, false, Race.HUMAN, ClassId.GLADIATOR), 
    DREADNOUGHT(89, false, Race.HUMAN, ClassId.WARLORD), 
    PHOENIX_KNIGHT(90, false, Race.HUMAN, ClassId.PALADIN), 
    HELL_KNIGHT(91, false, Race.HUMAN, ClassId.DARK_AVENGER), 
    SAGITTARIUS(92, false, Race.HUMAN, ClassId.HAWKEYE), 
    ADVENTURER(93, false, Race.HUMAN, ClassId.TREASURE_HUNTER), 
    ARCHMAGE(94, true, Race.HUMAN, ClassId.SORCERER), 
    SOULTAKER(95, true, Race.HUMAN, ClassId.NECROMANCER), 
    ARCANA_LORD(96, true, true, Race.HUMAN, ClassId.WARLOCK), 
    CARDINAL(97, true, Race.HUMAN, ClassId.BISHOP), 
    HIEROPHANT(98, true, Race.HUMAN, ClassId.PROPHET), 
    EVA_TEMPLAR(99, false, Race.ELF, ClassId.TEMPLE_KNIGHT), 
    SWORD_MUSE(100, false, Race.ELF, ClassId.SWORDSINGER), 
    WIND_RIDER(101, false, Race.ELF, ClassId.PLAINS_WALKER), 
    MOONLIGHT_SENTINEL(102, false, Race.ELF, ClassId.SILVER_RANGER), 
    MYSTIC_MUSE(103, true, Race.ELF, ClassId.SPELLSINGER), 
    ELEMENTAL_MASTER(104, true, true, Race.ELF, ClassId.ELEMENTAL_SUMMONER), 
    EVA_SAINT(105, true, Race.ELF, ClassId.ELDER), 
    SHILLIEN_TEMPLAR(106, false, Race.DARK_ELF, ClassId.SHILLIEN_KNIGHT), 
    SPECTRAL_DANCER(107, false, Race.DARK_ELF, ClassId.BLADEDANCER), 
    GHOST_HUNTER(108, false, Race.DARK_ELF, ClassId.ABYSS_WALKER), 
    GHOST_SENTINEL(109, false, Race.DARK_ELF, ClassId.PHANTOM_RANGER), 
    STORM_SCREAMER(110, true, Race.DARK_ELF, ClassId.SPELLHOWLER), 
    SPECTRAL_MASTER(111, true, true, Race.DARK_ELF, ClassId.PHANTOM_SUMMONER), 
    SHILLIEN_SAINT(112, true, Race.DARK_ELF, ClassId.SHILLIEN_ELDER), 
    TITAN(113, false, Race.ORC, ClassId.DESTROYER), 
    GRAND_KHAVATARI(114, false, Race.ORC, ClassId.TYRANT), 
    DOMINATOR(115, true, Race.ORC, ClassId.OVERLORD), 
    DOOMCRYER(116, true, Race.ORC, ClassId.WARCRYER), 
    FORTUNE_SEEKER(117, false, Race.DWARF, ClassId.BOUNTY_HUNTER), 
    MAESTRO(118, false, Race.DWARF, ClassId.WARSMITH), 
    JIN_KAMAEL_SOLDIER(192, false, Race.JIN_KAMAEL, (ClassId)null), 
    TROOPER(125, false, Race.JIN_KAMAEL, ClassId.JIN_KAMAEL_SOLDIER), 
    SOUL_FINDER(193, false, Race.JIN_KAMAEL, ClassId.JIN_KAMAEL_SOLDIER), 
    WARDER(126, false, Race.JIN_KAMAEL, ClassId.JIN_KAMAEL_SOLDIER), 
    BERSERKER(127, false, Race.JIN_KAMAEL, ClassId.TROOPER), 
    SOUL_BREAKER(194, false, Race.JIN_KAMAEL, ClassId.SOUL_FINDER), 
    SOUL_RANGER(130, false, Race.JIN_KAMAEL, ClassId.WARDER), 
    DOOMBRINGER(131, false, Race.JIN_KAMAEL, ClassId.BERSERKER), 
    SOUL_HOUND(195, false, Race.JIN_KAMAEL, ClassId.SOUL_BREAKER), 
    TRICKSTER(134, false, Race.JIN_KAMAEL, ClassId.SOUL_RANGER);
    
    private final int _id;
    private final boolean _isMage;
    private final boolean _isSummoner;
    private final Race _race;
    private final ClassId _parent;
    private final Set<ClassId> _nextClassIds;
    private static IntMap<ClassId> classIdMap;
    
    private ClassId(final int pId, final boolean pIsMage, final Race race, final ClassId pParent) {
        this._nextClassIds = new HashSet<ClassId>(1);
        this._id = pId;
        this._isMage = pIsMage;
        this._isSummoner = false;
        this._race = race;
        this._parent = pParent;
        if (this._parent != null) {
            this._parent.addNextClassId(this);
        }
    }
    
    private ClassId(final int pId, final boolean pIsMage, final boolean pIsSummoner, final Race race, final ClassId pParent) {
        this._nextClassIds = new HashSet<ClassId>(1);
        this._id = pId;
        this._isMage = pIsMage;
        this._isSummoner = pIsSummoner;
        this._race = race;
        this._parent = pParent;
        if (this._parent != null) {
            this._parent.addNextClassId(this);
        }
    }
    
    public static ClassId getClassId(final int cId) {
        return (ClassId)ClassId.classIdMap.get(cId);
    }
    
    @Override
    public final int getId() {
        return this._id;
    }
    
    public final boolean isMage() {
        return this._isMage;
    }
    
    public final boolean isSummoner() {
        return this._isSummoner;
    }
    
    public final Race getRace() {
        return this._race;
    }
    
    public final boolean childOf(final ClassId cid) {
        return this._parent != null && (this._parent == cid || this._parent.childOf(cid));
    }
    
    public final boolean equalsOrChildOf(final ClassId cid) {
        return this == cid || this.childOf(cid);
    }
    
    public final int level() {
        if (this._parent == null) {
            return 0;
        }
        return 1 + this._parent.level();
    }
    
    public final ClassId getParent() {
        return this._parent;
    }
    
    public final ClassId getRootClassId() {
        if (this._parent != null) {
            return this._parent.getRootClassId();
        }
        return this;
    }
    
    public Set<ClassId> getNextClassIds() {
        return this._nextClassIds;
    }
    
    private final void addNextClassId(final ClassId cId) {
        this._nextClassIds.add(cId);
    }
    
    static {
        ClassId.classIdMap = (IntMap<ClassId>)new HashIntMap();
        for (final ClassId classId : values()) {
            ClassId.classIdMap.put(classId.getId(), (Object)classId);
        }
    }
}
