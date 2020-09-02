// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.templates;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.engine.vip.VipEngine;
import org.l2j.commons.util.Rnd;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.Collection;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.enums.DropType;
import java.util.Iterator;
import org.l2j.gameserver.data.xml.impl.NpcData;
import java.util.Collections;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.Race;
import java.util.ArrayList;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.enums.MpRewardAffectType;
import org.l2j.gameserver.enums.MpRewardType;
import java.util.Set;
import org.l2j.gameserver.enums.AISkillScope;
import org.l2j.gameserver.engine.skill.api.Skill;
import java.util.Map;
import org.l2j.gameserver.enums.AIType;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.DropHolder;
import java.util.List;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

public final class NpcTemplate extends CreatureTemplate implements IIdentifiable
{
    private final List<DropHolder> _dropListDeath;
    private final List<DropHolder> _dropListSpoil;
    private int _id;
    private int _displayId;
    private byte _level;
    private String _type;
    private String _name;
    private boolean _usingServerSideName;
    private String _title;
    private boolean _usingServerSideTitle;
    private StatsSet _parameters;
    private Sex _sex;
    private int _chestId;
    private int _rhandId;
    private int _lhandId;
    private int _weaponEnchant;
    private double _exp;
    private double _sp;
    private double _raidPoints;
    private boolean _unique;
    private boolean _attackable;
    private boolean _targetable;
    private boolean _talkable;
    private boolean _isQuestMonster;
    private boolean _undying;
    private boolean _showName;
    private boolean _randomWalk;
    private boolean _randomAnimation;
    private boolean _flying;
    private boolean _canMove;
    private boolean _noSleepMode;
    private boolean _passableDoor;
    private boolean _hasSummoner;
    private boolean _canBeSown;
    private boolean _canBeCrt;
    private boolean _isDeathPenalty;
    private int _corpseTime;
    private AIType _aiType;
    private int _aggroRange;
    private int _clanHelpRange;
    private int _dodge;
    private boolean _isChaos;
    private boolean _isAggressive;
    private int _soulShot;
    private int _spiritShot;
    private int _soulShotChance;
    private int _spiritShotChance;
    private int _minSkillChance;
    private int _maxSkillChance;
    private double _hitTimeFactor;
    private double _hitTimeFactorSkill;
    private Map<Integer, Skill> _skills;
    private Map<AISkillScope, List<Skill>> _aiSkillLists;
    private Set<Integer> _clans;
    private Set<Integer> _ignoreClanNpcIds;
    private double _collisionRadiusGrown;
    private double _collisionHeightGrown;
    private int _mpRewardValue;
    private MpRewardType _mpRewardType;
    private int _mpRewardTicks;
    private MpRewardAffectType _mpRewardAffectType;
    private List<Integer> _extendDrop;
    private ElementalType elementalType;
    private long attributeExp;
    
    public NpcTemplate(final StatsSet set) {
        super(set);
        this._dropListDeath = new ArrayList<DropHolder>();
        this._dropListSpoil = new ArrayList<DropHolder>();
    }
    
    public static boolean isAssignableTo(Class<?> sub, final Class<?> clazz) {
        if (!clazz.isInterface()) {
            while (!sub.getName().equals(clazz.getName())) {
                sub = sub.getSuperclass();
                if (sub == null) {
                    return false;
                }
            }
            return true;
        }
        for (final Class<?> interface1 : sub.getInterfaces()) {
            if (clazz.getName().equals(interface1.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isAssignableTo(final Object obj, final Class<?> clazz) {
        return isAssignableTo(obj.getClass(), clazz);
    }
    
    @Override
    public void set(final StatsSet set) {
        super.set(set);
        this._id = set.getInt("id");
        this._displayId = set.getInt("displayId", this._id);
        this._level = set.getByte("level", (byte)70);
        this._type = set.getString("type", "Npc");
        this._name = set.getString("name", "");
        this._usingServerSideName = set.getBoolean("usingServerSideName", false);
        this._title = set.getString("title", "");
        this._usingServerSideTitle = set.getBoolean("usingServerSideTitle", false);
        this.setRace(set.getEnum("race", Race.class, Race.NONE));
        this._sex = set.getEnum("sex", Sex.class, Sex.ETC);
        this.elementalType = set.getEnum("elementalType", ElementalType.class);
        this._chestId = set.getInt("chestId", 0);
        this._rhandId = set.getInt("rhandId", 0);
        this._lhandId = set.getInt("lhandId", 0);
        this._weaponEnchant = set.getInt("weaponEnchant", 0);
        this._exp = set.getDouble("exp", 0.0);
        this._sp = set.getDouble("sp", 0.0);
        this._raidPoints = set.getDouble("raidPoints", 0.0);
        this.attributeExp = set.getLong("attribute_exp", 0L);
        this._unique = set.getBoolean("unique", !this._type.equals("Monster") && !this._type.equals("RaidBoss") && !this._type.equals("GrandBoss"));
        this._attackable = set.getBoolean("attackable", true);
        this._targetable = set.getBoolean("targetable", true);
        this._talkable = set.getBoolean("talkable", true);
        this._isQuestMonster = this._title.contains("Quest");
        this._undying = set.getBoolean("undying", true);
        this._showName = set.getBoolean("showName", true);
        this._randomWalk = set.getBoolean("randomWalk", !this._type.equals("Guard"));
        this._randomAnimation = set.getBoolean("randomAnimation", true);
        this._flying = set.getBoolean("flying", false);
        this._canMove = set.getBoolean("canMove", true);
        this._noSleepMode = set.getBoolean("noSleepMode", false);
        this._passableDoor = set.getBoolean("passableDoor", false);
        this._hasSummoner = set.getBoolean("hasSummoner", false);
        this._canBeSown = set.getBoolean("canBeSown", false);
        this._canBeCrt = set.getBoolean("exCrtEffect", true);
        this._isDeathPenalty = set.getBoolean("isDeathPenalty", false);
        this._corpseTime = set.getInt("corpseTime", Config.DEFAULT_CORPSE_TIME);
        this._aiType = set.getEnum("aiType", AIType.class, AIType.FIGHTER);
        this._aggroRange = set.getInt("aggroRange", 0);
        this._clanHelpRange = set.getInt("clanHelpRange", 0);
        this._dodge = set.getInt("dodge", 0);
        this._isChaos = set.getBoolean("isChaos", false);
        this._isAggressive = set.getBoolean("isAggressive", false);
        this._soulShot = set.getInt("soulShot", 0);
        this._spiritShot = set.getInt("spiritShot", 0);
        this._soulShotChance = set.getInt("shotShotChance", 0);
        this._spiritShotChance = set.getInt("spiritShotChance", 0);
        this._minSkillChance = set.getInt("minSkillChance", 7);
        this._maxSkillChance = set.getInt("maxSkillChance", 15);
        this._hitTimeFactor = set.getInt("hitTime", 100) / 100.0;
        this._hitTimeFactorSkill = set.getInt("hitTimeSkill", 100) / 100.0;
        this._collisionRadiusGrown = set.getDouble("collisionRadiusGrown", 0.0);
        this._collisionHeightGrown = set.getDouble("collisionHeightGrown", 0.0);
        this._mpRewardValue = set.getInt("mpRewardValue", 0);
        this._mpRewardType = set.getEnum("mpRewardType", MpRewardType.class, MpRewardType.DIFF);
        this._mpRewardTicks = set.getInt("mpRewardTicks", 0);
        this._mpRewardAffectType = set.getEnum("mpRewardAffectType", MpRewardAffectType.class, MpRewardAffectType.SOLO);
        this._extendDrop = set.getList("extendDrop", Integer.class);
        if (Config.ENABLE_NPC_STAT_MULTIPIERS) {
            final String type = this._type;
            switch (type) {
                case "Monster": {
                    this._baseValues.put(Stat.MAX_HP, this.getBaseHpMax() * Config.MONSTER_HP_MULTIPLIER);
                    this._baseValues.put(Stat.MAX_MP, this.getBaseMpMax() * Config.MONSTER_MP_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_ATTACK, this.getBasePAtk() * Config.MONSTER_PATK_MULTIPLIER);
                    this._baseValues.put(Stat.MAGIC_ATTACK, this.getBaseMAtk() * Config.MONSTER_MATK_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_DEFENCE, this.getBasePDef() * Config.MONSTER_PDEF_MULTIPLIER);
                    this._baseValues.put(Stat.MAGICAL_DEFENCE, this.getBaseMDef() * Config.MONSTER_MDEF_MULTIPLIER);
                    this._aggroRange *= (int)Config.MONSTER_AGRRO_RANGE_MULTIPLIER;
                    this._clanHelpRange *= (int)Config.MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "RaidBoss":
                case "GrandBoss": {
                    this._baseValues.put(Stat.MAX_HP, this.getBaseHpMax() * Config.RAIDBOSS_HP_MULTIPLIER);
                    this._baseValues.put(Stat.MAX_MP, this.getBaseMpMax() * Config.RAIDBOSS_MP_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_ATTACK, this.getBasePAtk() * Config.RAIDBOSS_PATK_MULTIPLIER);
                    this._baseValues.put(Stat.MAGIC_ATTACK, this.getBaseMAtk() * Config.RAIDBOSS_MATK_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_DEFENCE, this.getBasePDef() * Config.RAIDBOSS_PDEF_MULTIPLIER);
                    this._baseValues.put(Stat.MAGICAL_DEFENCE, this.getBaseMDef() * Config.RAIDBOSS_MDEF_MULTIPLIER);
                    this._aggroRange *= (int)Config.RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
                    this._clanHelpRange *= (int)Config.RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "Guard": {
                    this._baseValues.put(Stat.MAX_HP, this.getBaseHpMax() * Config.GUARD_HP_MULTIPLIER);
                    this._baseValues.put(Stat.MAX_MP, this.getBaseMpMax() * Config.GUARD_MP_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_ATTACK, this.getBasePAtk() * Config.GUARD_PATK_MULTIPLIER);
                    this._baseValues.put(Stat.MAGIC_ATTACK, this.getBaseMAtk() * Config.GUARD_MATK_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_DEFENCE, this.getBasePDef() * Config.GUARD_PDEF_MULTIPLIER);
                    this._baseValues.put(Stat.MAGICAL_DEFENCE, this.getBaseMDef() * Config.GUARD_MDEF_MULTIPLIER);
                    this._aggroRange *= (int)Config.GUARD_AGRRO_RANGE_MULTIPLIER;
                    this._clanHelpRange *= (int)Config.GUARD_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
                case "Defender": {
                    this._baseValues.put(Stat.MAX_HP, this.getBaseHpMax() * Config.DEFENDER_HP_MULTIPLIER);
                    this._baseValues.put(Stat.MAX_MP, this.getBaseMpMax() * Config.DEFENDER_MP_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_ATTACK, this.getBasePAtk() * Config.DEFENDER_PATK_MULTIPLIER);
                    this._baseValues.put(Stat.MAGIC_ATTACK, this.getBaseMAtk() * Config.DEFENDER_MATK_MULTIPLIER);
                    this._baseValues.put(Stat.PHYSICAL_DEFENCE, this.getBasePDef() * Config.DEFENDER_PDEF_MULTIPLIER);
                    this._baseValues.put(Stat.MAGICAL_DEFENCE, this.getBaseMDef() * Config.DEFENDER_MDEF_MULTIPLIER);
                    this._aggroRange *= (int)Config.DEFENDER_AGRRO_RANGE_MULTIPLIER;
                    this._clanHelpRange *= (int)Config.DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;
                    break;
                }
            }
        }
    }
    
    @Override
    public int getId() {
        return this._id;
    }
    
    public int getDisplayId() {
        return this._displayId;
    }
    
    public byte getLevel() {
        return this._level;
    }
    
    public String getType() {
        return this._type;
    }
    
    public boolean isType(final String type) {
        return this._type.equalsIgnoreCase(type);
    }
    
    public String getName() {
        return this._name;
    }
    
    public boolean isUsingServerSideName() {
        return this._usingServerSideName;
    }
    
    public String getTitle() {
        return this._title;
    }
    
    public boolean isUsingServerSideTitle() {
        return this._usingServerSideTitle;
    }
    
    public StatsSet getParameters() {
        return this._parameters;
    }
    
    public void setParameters(final StatsSet set) {
        this._parameters = set;
    }
    
    public Sex getSex() {
        return this._sex;
    }
    
    public int getChestId() {
        return this._chestId;
    }
    
    public int getRHandId() {
        return this._rhandId;
    }
    
    public int getLHandId() {
        return this._lhandId;
    }
    
    public int getWeaponEnchant() {
        return this._weaponEnchant;
    }
    
    public double getExp() {
        return this._exp;
    }
    
    public double getSP() {
        return this._sp;
    }
    
    public long getAttributeExp() {
        return this.attributeExp;
    }
    
    public ElementalType getElementalType() {
        return this.elementalType;
    }
    
    public double getRaidPoints() {
        return this._raidPoints;
    }
    
    public boolean isUnique() {
        return this._unique;
    }
    
    public boolean isAttackable() {
        return this._attackable;
    }
    
    public boolean isTargetable() {
        return this._targetable;
    }
    
    public boolean isTalkable() {
        return this._talkable;
    }
    
    public boolean isQuestMonster() {
        return this._isQuestMonster;
    }
    
    public boolean isUndying() {
        return this._undying;
    }
    
    public boolean isShowName() {
        return this._showName;
    }
    
    public boolean isRandomWalkEnabled() {
        return this._randomWalk;
    }
    
    public boolean isRandomAnimationEnabled() {
        return this._randomAnimation;
    }
    
    public boolean isFlying() {
        return this._flying;
    }
    
    public boolean canMove() {
        return this._canMove;
    }
    
    public boolean isNoSleepMode() {
        return this._noSleepMode;
    }
    
    public boolean isPassableDoor() {
        return this._passableDoor;
    }
    
    public boolean hasSummoner() {
        return this._hasSummoner;
    }
    
    public boolean canBeSown() {
        return this._canBeSown;
    }
    
    public boolean canBeCrt() {
        return this._canBeCrt;
    }
    
    public boolean isDeathPenalty() {
        return this._isDeathPenalty;
    }
    
    public int getCorpseTime() {
        return this._corpseTime;
    }
    
    public AIType getAIType() {
        return this._aiType;
    }
    
    public int getAggroRange() {
        return this._aggroRange;
    }
    
    public int getClanHelpRange() {
        return this._clanHelpRange;
    }
    
    public int getDodge() {
        return this._dodge;
    }
    
    public boolean isChaos() {
        return this._isChaos;
    }
    
    public boolean isAggressive() {
        return this._isAggressive;
    }
    
    public int getSoulShot() {
        return this._soulShot;
    }
    
    public int getSpiritShot() {
        return this._spiritShot;
    }
    
    public int getSoulShotChance() {
        return this._soulShotChance;
    }
    
    public int getSpiritShotChance() {
        return this._spiritShotChance;
    }
    
    public int getMinSkillChance() {
        return this._minSkillChance;
    }
    
    public int getMaxSkillChance() {
        return this._maxSkillChance;
    }
    
    public double getHitTimeFactor() {
        return this._hitTimeFactor;
    }
    
    public double getHitTimeFactorSkill() {
        return this._hitTimeFactorSkill;
    }
    
    @Override
    public Map<Integer, Skill> getSkills() {
        return this._skills;
    }
    
    public void setSkills(final Map<Integer, Skill> skills) {
        this._skills = ((skills != null) ? Collections.unmodifiableMap((Map<? extends Integer, ? extends Skill>)skills) : Collections.emptyMap());
    }
    
    public List<Skill> getAISkills(final AISkillScope aiSkillScope) {
        return this._aiSkillLists.getOrDefault(aiSkillScope, Collections.emptyList());
    }
    
    public void setAISkillLists(final Map<AISkillScope, List<Skill>> aiSkillLists) {
        this._aiSkillLists = ((aiSkillLists != null) ? Collections.unmodifiableMap((Map<? extends AISkillScope, ? extends List<Skill>>)aiSkillLists) : Collections.emptyMap());
    }
    
    public Set<Integer> getClans() {
        return this._clans;
    }
    
    public void setClans(final Set<Integer> clans) {
        this._clans = ((clans != null) ? Collections.unmodifiableSet((Set<? extends Integer>)clans) : null);
    }
    
    public int getMpRewardValue() {
        return this._mpRewardValue;
    }
    
    public MpRewardType getMpRewardType() {
        return this._mpRewardType;
    }
    
    public int getMpRewardTicks() {
        return this._mpRewardTicks;
    }
    
    public MpRewardAffectType getMpRewardAffectType() {
        return this._mpRewardAffectType;
    }
    
    public boolean isClan(final String clanName, final String... clanNames) {
        final Set<Integer> clans = this._clans;
        if (clans == null) {
            return false;
        }
        int clanId = NpcData.getInstance().getClanId("ALL");
        if (clans.contains(clanId)) {
            return true;
        }
        clanId = NpcData.getInstance().getClanId(clanName);
        if (clans.contains(clanId)) {
            return true;
        }
        for (final String name : clanNames) {
            clanId = NpcData.getInstance().getClanId(name);
            if (clans.contains(clanId)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean isClan(final Set<Integer> clans) {
        final Set<Integer> clanSet = this._clans;
        if (clanSet == null || clans == null) {
            return false;
        }
        final int clanId = NpcData.getInstance().getClanId("ALL");
        if (clanSet.contains(clanId)) {
            return true;
        }
        for (final Integer id : clans) {
            if (clanSet.contains(id)) {
                return true;
            }
        }
        return false;
    }
    
    public Set<Integer> getIgnoreClanNpcIds() {
        return this._ignoreClanNpcIds;
    }
    
    public void setIgnoreClanNpcIds(final Set<Integer> ignoreClanNpcIds) {
        this._ignoreClanNpcIds = ((ignoreClanNpcIds != null) ? Collections.unmodifiableSet((Set<? extends Integer>)ignoreClanNpcIds) : null);
    }
    
    public void addDrop(final DropHolder dropHolder) {
        this._dropListDeath.add(dropHolder);
    }
    
    public void addSpoil(final DropHolder dropHolder) {
        this._dropListSpoil.add(dropHolder);
    }
    
    public List<DropHolder> getDropList(final DropType dropType) {
        List<DropHolder> list = null;
        switch (dropType) {
            case DROP:
            case LUCKY: {
                list = this._dropListDeath;
                break;
            }
            case SPOIL: {
                list = this._dropListSpoil;
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        return list;
    }
    
    private double calculateLevelGapChanceToDrop(final DropHolder dropItem, final int levelDifference) {
        double levelGapChanceToDrop;
        if (dropItem.getItemId() == 57) {
            levelGapChanceToDrop = GameUtils.map(levelDifference, -Config.DROP_ADENA_MAX_LEVEL_DIFFERENCE, -Config.DROP_ADENA_MIN_LEVEL_DIFFERENCE, Config.DROP_ADENA_MIN_LEVEL_GAP_CHANCE, 100.0);
        }
        else {
            levelGapChanceToDrop = GameUtils.map(levelDifference, -Config.DROP_ITEM_MAX_LEVEL_DIFFERENCE, -Config.DROP_ITEM_MIN_LEVEL_DIFFERENCE, Config.DROP_ITEM_MIN_LEVEL_GAP_CHANCE, 100.0);
        }
        return levelGapChanceToDrop;
    }
    
    private void processDropList(final DropType dropType, final Collection<ItemHolder> itemsToDrop, final Creature victim, final Creature killer) {
        final List<DropHolder> list = this.getDropList(dropType);
        if (Objects.isNull(list)) {
            return;
        }
        Collections.shuffle(list);
        int dropOccurrenceCounter = victim.isRaid() ? Config.DROP_MAX_OCCURRENCES_RAIDBOSS : Config.DROP_MAX_OCCURRENCES_NORMAL;
        for (final DropHolder dropItem : list) {
            if (dropOccurrenceCounter == 0 && dropItem.getChance() < 100.0) {
                continue;
            }
            final ItemHolder drop = this.calculateDropWithLevelGap(dropItem, victim, killer);
            if (!Objects.nonNull(drop)) {
                continue;
            }
            if (dropItem.getChance() < 100.0) {
                --dropOccurrenceCounter;
            }
            itemsToDrop.add(drop);
        }
        if (victim.isChampion()) {
            if (victim.getLevel() < killer.getLevel() && Rnd.get(100) < Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE) {
                return;
            }
            if (victim.getLevel() > killer.getLevel() && Rnd.get(100) < Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE) {
                return;
            }
            itemsToDrop.add(new ItemHolder(Config.CHAMPION_REWARD_ID, Config.CHAMPION_REWARD_QTY));
        }
    }
    
    private void proccessEtcDrop(final Collection<ItemHolder> items, final Creature victim, final Creature killer) {
        final List<DropHolder> dropList = new ArrayList<DropHolder>();
        if (Objects.nonNull(killer.getActingPlayer())) {
            final float silverCoinChance = VipEngine.getInstance().getSilverCoinDropChance(killer.getActingPlayer());
            final float rustyCoinChance = VipEngine.getInstance().getRustyCoinDropChance(killer.getActingPlayer());
            final float l2CoinDropChance = (float)killer.getStats().getValue(Stat.BONUS_L2COIN_DROP_RATE) + Config.L2_COIN_DROP_RATE;
            if (silverCoinChance > 0.0f) {
                dropList.add(new DropHolder(DropType.DROP, 29983, 2L, 5L, silverCoinChance));
            }
            if (rustyCoinChance > 0.0f) {
                dropList.add(new DropHolder(DropType.DROP, 29984, 2L, 5L, rustyCoinChance));
            }
            if (l2CoinDropChance > 0.0f) {
                dropList.add(new DropHolder(DropType.DROP, 91663, 1L, 1L, l2CoinDropChance));
            }
        }
        for (final DropHolder dropItem : dropList) {
            final ItemHolder drop = this.calculateDropWithLevelGap(dropItem, victim, killer);
            if (drop == null) {
                continue;
            }
            items.add(drop);
        }
    }
    
    private ItemHolder calculateDropWithLevelGap(final DropHolder dropItem, final Creature victim, final Creature killer) {
        final int levelDifference = victim.getLevel() - killer.getLevel();
        final double levelGapChanceToDrop = this.calculateLevelGapChanceToDrop(dropItem, levelDifference);
        if (!Rnd.chance(levelGapChanceToDrop)) {
            return null;
        }
        return this.calculateDrop(dropItem, victim, killer);
    }
    
    public Collection<ItemHolder> calculateDrops(final DropType dropType, final Creature victim, final Creature killer) {
        final Collection<ItemHolder> calculatedDrops = new ArrayList<ItemHolder>();
        if (dropType == DropType.DROP) {
            this.proccessEtcDrop(calculatedDrops, victim, killer);
        }
        this.processDropList(dropType, calculatedDrops, victim, killer);
        return calculatedDrops;
    }
    
    private ItemHolder calculateDrop(final DropHolder dropItem, final Creature victim, final Creature killer) {
        switch (dropItem.getDropType()) {
            case DROP:
            case LUCKY: {
                final int itemId = dropItem.getItemId();
                final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
                final boolean champion = victim.isChampion();
                double rateChance = 1.0;
                if (Config.RATE_DROP_CHANCE_BY_ID.get(itemId) != null) {
                    rateChance *= Config.RATE_DROP_CHANCE_BY_ID.get(dropItem.getItemId());
                    if (champion && itemId == 57) {
                        rateChance *= Config.CHAMPION_ADENAS_REWARDS_CHANCE;
                    }
                }
                else if (item.hasExImmediateEffect()) {
                    rateChance *= Config.RATE_HERB_DROP_CHANCE_MULTIPLIER;
                }
                else if (victim.isRaid()) {
                    rateChance *= Config.RATE_RAID_DROP_CHANCE_MULTIPLIER;
                }
                else {
                    rateChance *= Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_CHANCE : 1.0f);
                }
                rateChance *= killer.getStats().getValue(Stat.BONUS_DROP_RATE, 1.0);
                if (Rnd.chance(dropItem.getChance() * rateChance)) {
                    double rateAmount = 1.0;
                    if (Config.RATE_DROP_AMOUNT_BY_ID.get(itemId) != null) {
                        rateAmount *= Config.RATE_DROP_AMOUNT_BY_ID.get(itemId);
                        if (champion && itemId == 57) {
                            rateAmount *= Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
                        }
                    }
                    else if (item.hasExImmediateEffect()) {
                        rateAmount *= Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER;
                    }
                    else if (victim.isRaid()) {
                        rateAmount *= Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER;
                    }
                    else {
                        rateAmount *= Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER * (champion ? Config.CHAMPION_REWARDS_AMOUNT : 1.0f);
                    }
                    rateAmount *= killer.getStats().getValue(Stat.BONUS_DROP_AMOUNT, 1.0);
                    return new ItemHolder(itemId, (long)(Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount));
                }
                break;
            }
            case SPOIL: {
                double rateChance2 = Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
                rateChance2 *= killer.getStats().getValue(Stat.BONUS_SPOIL_RATE, 1.0);
                if (Rnd.chance(dropItem.getChance() * rateChance2)) {
                    final double rateAmount2 = Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
                    return new ItemHolder(dropItem.getItemId(), (long)(Rnd.get(dropItem.getMin(), dropItem.getMax()) * rateAmount2));
                }
                break;
            }
        }
        return null;
    }
    
    public double getCollisionRadiusGrown() {
        return this._collisionRadiusGrown;
    }
    
    public double getCollisionHeightGrown() {
        return this._collisionHeightGrown;
    }
    
    public List<Integer> getExtendDrop() {
        return (this._extendDrop == null) ? Collections.emptyList() : this._extendDrop;
    }
}
