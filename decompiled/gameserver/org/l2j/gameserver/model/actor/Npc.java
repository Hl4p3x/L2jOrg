// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor;

import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.serverpackets.ExShowChannelingEffect;
import org.l2j.gameserver.enums.AISkillScope;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.NpcSay;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCanBeSeen;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcEventReceived;
import org.l2j.gameserver.model.events.EventType;
import java.util.Objects;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.network.serverpackets.ExChangeNpcState;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillFinished;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.network.serverpackets.NpcInfo;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcDespawn;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.instancemanager.WalkingManager;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSpawn;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleport;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.spawns.NpcSpawnTemplate;
import org.l2j.gameserver.instancemanager.DBSpawnManager;
import org.l2j.gameserver.enums.MpRewardAffectType;
import org.l2j.gameserver.model.MpRewardTask;
import org.l2j.gameserver.taskmanager.DecayTaskManager;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.model.actor.instance.Fisherman;
import org.l2j.gameserver.model.actor.instance.Warehouse;
import org.l2j.gameserver.model.actor.instance.Teleporter;
import org.l2j.gameserver.model.actor.instance.Merchant;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.handler.IBypassHandler;
import org.l2j.gameserver.handler.BypassHandler;
import org.l2j.gameserver.data.xml.impl.ClanHallManager;
import org.l2j.gameserver.model.entity.ClanHall;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.NpcInfoAbnormalVisualEffect;
import org.l2j.gameserver.network.serverpackets.ServerObjectInfo;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.NpcStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.NpcStats;
import org.l2j.gameserver.enums.AIType;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import java.util.Iterator;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.variables.NpcVariables;
import org.l2j.gameserver.world.zone.type.TaxZone;
import org.l2j.gameserver.instancemanager.RaidBossStatus;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.model.Spawn;

public class Npc extends Creature
{
    public static final int INTERACTION_DISTANCE = 250;
    public static final int RANDOM_ITEM_DROP_LIMIT = 70;
    private static final int MINIMUM_SOCIAL_INTERVAL = 6000;
    private static final String OLYMPIAD_HTML_PATH = "data/html/olympiad/";
    private final boolean _isQuestMonster;
    private Spawn _spawn;
    private boolean _isBusy;
    private volatile boolean _isDecayed;
    private boolean _isAutoAttackable;
    private long _lastSocialBroadcast;
    private boolean _isRandomAnimationEnabled;
    private boolean _isRandomWalkingEnabled;
    private boolean _isTalkable;
    private int _currentLHandId;
    private int _currentRHandId;
    private int _currentEnchant;
    private double _currentCollisionHeight;
    private double _currentCollisionRadius;
    private int _soulshotamount;
    private int _spiritshotamount;
    private int _displayEffect;
    private int _killingBlowWeaponId;
    private int _cloneObjId;
    private int _clanId;
    private NpcStringId _titleString;
    private NpcStringId _nameString;
    private StatsSet _params;
    private RaidBossStatus _raidStatus;
    private TaxZone _taxZone;
    private int scriptValue;
    private NpcVariables variables;
    
    public Npc(final NpcTemplate template) {
        super(template);
        this._isQuestMonster = this.getTemplate().isQuestMonster();
        this._isBusy = false;
        this._isDecayed = false;
        this._isAutoAttackable = false;
        this._lastSocialBroadcast = 0L;
        this._isRandomAnimationEnabled = true;
        this._isRandomWalkingEnabled = true;
        this._isTalkable = this.getTemplate().isTalkable();
        this._soulshotamount = 0;
        this._spiritshotamount = 0;
        this._displayEffect = 0;
        this._taxZone = null;
        this.scriptValue = 0;
        this.setInstanceType(InstanceType.L2Npc);
        this.initCharStatusUpdateValues();
        this.setTargetable(this.getTemplate().isTargetable());
        this._currentLHandId = this.getTemplate().getLHandId();
        this._currentRHandId = this.getTemplate().getRHandId();
        this._currentEnchant = (Config.ENABLE_RANDOM_ENCHANT_EFFECT ? Rnd.get(4, 21) : this.getTemplate().getWeaponEnchant());
        this._currentCollisionHeight = this.getTemplate().getfCollisionHeight();
        this._currentCollisionRadius = this.getTemplate().getfCollisionRadius();
        this.setIsFlying(template.isFlying());
        this.initStatusUpdateCache();
        for (final Skill skill : template.getSkills().values()) {
            this.addSkill(skill);
        }
    }
    
    public void onRandomAnimation(final int animationId) {
        final long now = System.currentTimeMillis();
        if (now - this._lastSocialBroadcast > 6000L) {
            this._lastSocialBroadcast = now;
            this.broadcastPacket(new SocialAction(this.getObjectId(), animationId));
        }
    }
    
    public boolean hasRandomAnimation() {
        return Config.MAX_NPC_ANIMATION > 0 && this._isRandomAnimationEnabled && this.getAiType() != AIType.CORPSE;
    }
    
    public void setRandomAnimation(final boolean val) {
        this._isRandomAnimationEnabled = val;
    }
    
    public boolean isRandomAnimationEnabled() {
        return this._isRandomAnimationEnabled;
    }
    
    public void setRandomWalking(final boolean enabled) {
        this._isRandomWalkingEnabled = enabled;
    }
    
    public boolean isRandomWalkingEnabled() {
        return this._isRandomWalkingEnabled;
    }
    
    @Override
    public NpcStats getStats() {
        return (NpcStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new NpcStats(this));
    }
    
    @Override
    public NpcStatus getStatus() {
        return (NpcStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new NpcStatus(this));
    }
    
    @Override
    public final NpcTemplate getTemplate() {
        return (NpcTemplate)super.getTemplate();
    }
    
    @Override
    public int getId() {
        return this.getTemplate().getId();
    }
    
    @Override
    public boolean canBeAttacked() {
        return Config.ALT_ATTACKABLE_NPCS;
    }
    
    @Override
    public final int getLevel() {
        return this.getTemplate().getLevel();
    }
    
    public boolean isAggressive() {
        return false;
    }
    
    public int getAggroRange() {
        return this.getTemplate().getAggroRange();
    }
    
    public boolean isInMyClan(final Npc npc) {
        return this.getTemplate().isClan(npc.getTemplate().getClans());
    }
    
    @Override
    public boolean isUndead() {
        return this.getTemplate().getRace() == Race.UNDEAD;
    }
    
    @Override
    public void updateAbnormalVisualEffects() {
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (!(!this.isVisibleFor(player))) {
                if (this.getRunSpeed() == 0.0) {
                    player.sendPacket(new ServerObjectInfo(this, player));
                }
                else {
                    player.sendPacket(new NpcInfoAbnormalVisualEffect(this));
                }
            }
        });
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (attacker == null) {
            return false;
        }
        if (!this.isTargetable()) {
            return false;
        }
        if (GameUtils.isAttackable(attacker)) {
            return !this.isInMyClan((Npc)attacker) && (((NpcTemplate)attacker.getTemplate()).isChaos() || ((Attackable)attacker).getHating(this) > 0);
        }
        return this._isAutoAttackable;
    }
    
    public void setAutoAttackable(final boolean flag) {
        this._isAutoAttackable = flag;
    }
    
    public int getLeftHandItem() {
        return this._currentLHandId;
    }
    
    public int getRightHandItem() {
        return this._currentRHandId;
    }
    
    public int getEnchantEffect() {
        return this._currentEnchant;
    }
    
    public final boolean isBusy() {
        return this._isBusy;
    }
    
    public void setBusy(final boolean isBusy) {
        this._isBusy = isBusy;
    }
    
    public boolean isWarehouse() {
        return false;
    }
    
    public boolean canTarget(final Player player) {
        if (player.isControlBlocked()) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (player.isLockedTarget() && player.getLockedTarget() != this) {
            player.sendPacket(SystemMessageId.FAILED_TO_CHANGE_ENMITY);
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        return true;
    }
    
    public boolean canInteract(final Player player) {
        return !player.isCastingNow() && !player.isDead() && !player.isFakeDeath() && !player.isSitting() && player.getPrivateStoreType() == PrivateStoreType.NONE && MathUtil.isInsideRadius3D(this, player, 250) && player.getInstanceWorld() == this.getInstanceWorld() && !this._isBusy;
    }
    
    public final void setTaxZone(final TaxZone zone) {
        this._taxZone = ((zone != null && !this.isInInstance()) ? zone : null);
    }
    
    public final Castle getTaxCastle() {
        return (this._taxZone != null) ? this._taxZone.getCastle() : null;
    }
    
    public final double getCastleTaxRate(final TaxType type) {
        final Castle castle = this.getTaxCastle();
        return (castle != null) ? (castle.getTaxPercent(type) / 100.0) : 0.0;
    }
    
    public final void handleTaxPayment(final long amount) {
        final Castle taxCastle = this.getTaxCastle();
        if (taxCastle != null) {
            taxCastle.addToTreasury(amount);
        }
    }
    
    public final Castle getCastle() {
        return CastleManager.getInstance().findNearestCastle(this);
    }
    
    public final ClanHall getClanHall() {
        return ClanHallManager.getInstance().getClanHallByNpcId(this.getId());
    }
    
    public final Castle getCastle(final long maxDistance) {
        return CastleManager.getInstance().findNearestCastle(this, maxDistance);
    }
    
    public void onBypassFeedback(final Player player, final String command) {
        if (this.canInteract(player)) {
            final IBypassHandler handler = BypassHandler.getInstance().getHandler(command);
            if (handler != null) {
                handler.useBypass(command, player, this);
            }
            else {
                Npc.LOGGER.warn("Unknown NPC {} bypass {}", (Object)this.getId(), (Object)command);
            }
        }
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
    
    public String getHtmlPath(final int npcId, final int val) {
        String pom = "";
        if (val == 0) {
            pom = Integer.toString(npcId);
        }
        else {
            pom = invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, npcId, val);
        }
        final String temp = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, pom);
        if (HtmCache.getInstance().isLoadable(temp)) {
            return temp;
        }
        return "data/html/npcdefault.htm";
    }
    
    public void showChatWindow(final Player player) {
        this.showChatWindow(player, 0);
    }
    
    private boolean showPkDenyChatWindow(final Player player, final String type) {
        String html = HtmCache.getInstance().getHtm(player, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, type, this.getId()));
        if (html != null) {
            html = html.replaceAll("%objectId%", String.valueOf(this.getObjectId()));
            player.sendPacket(new NpcHtmlMessage(this.getObjectId(), html));
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return true;
        }
        return false;
    }
    
    public void showChatWindow(final Player player, final int val) {
        if (!this._isTalkable) {
            player.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        if (player.getReputation() < 0) {
            if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof Merchant) {
                if (this.showPkDenyChatWindow(player, "merchant")) {
                    return;
                }
            }
            else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && this instanceof Teleporter) {
                if (this.showPkDenyChatWindow(player, "teleporter")) {
                    return;
                }
            }
            else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE && this instanceof Warehouse) {
                if (this.showPkDenyChatWindow(player, "warehouse")) {
                    return;
                }
            }
            else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP && this instanceof Fisherman && this.showPkDenyChatWindow(player, "fisherman")) {
                return;
            }
        }
        if (this.getTemplate().isType("Auctioneer") && val == 0) {
            return;
        }
        final int npcId = this.getTemplate().getId();
        String filename = null;
        switch (npcId) {
            case 31688: {
                if (player.isNoble()) {
                    filename = "data/html/olympiad/noble_main.htm";
                    break;
                }
                filename = this.getHtmlPath(npcId, val);
                break;
            }
            case 31690:
            case 31769:
            case 31770:
            case 31771:
            case 31772: {
                if (player.isHero() || player.isNoble()) {
                    filename = "data/html/olympiad/hero_main.htm";
                    break;
                }
                filename = this.getHtmlPath(npcId, val);
                break;
            }
            case 30298: {
                if (player.isAcademyMember()) {
                    filename = this.getHtmlPath(npcId, 1);
                    break;
                }
                filename = this.getHtmlPath(npcId, val);
                break;
            }
            default: {
                if ((npcId >= 31093 && npcId <= 31094) || (npcId >= 31172 && npcId <= 31201) || (npcId >= 31239 && npcId <= 31254)) {
                    return;
                }
                filename = this.getHtmlPath(npcId, val);
                break;
            }
        }
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, filename);
        html.replace("%npcname%", this.getName());
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        player.sendPacket(html);
    }
    
    public void showChatWindow(final Player player, final String filename) {
        final NpcHtmlMessage html = new NpcHtmlMessage(this.getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(this.getObjectId()));
        player.sendPacket(html);
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }
    
    public double getExpReward() {
        final Instance instance = this.getInstanceWorld();
        final float rateMul = (instance != null) ? instance.getExpRate() : ((RateSettings)Configurator.getSettings((Class)RateSettings.class)).xp();
        return this.getTemplate().getExp() * rateMul;
    }
    
    public double getSpReward() {
        final Instance instance = this.getInstanceWorld();
        final float rateMul = (instance != null) ? instance.getSPRate() : Config.RATE_SP;
        return this.getTemplate().getSP() * rateMul;
    }
    
    public long getAttributeExp() {
        return this.getTemplate().getAttributeExp();
    }
    
    @Override
    public ElementalType getElementalSpiritType() {
        return this.getTemplate().getElementalType();
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }
        this._currentLHandId = this.getTemplate().getLHandId();
        this._currentRHandId = this.getTemplate().getRHandId();
        this._currentCollisionHeight = this.getTemplate().getfCollisionHeight();
        this._currentCollisionRadius = this.getTemplate().getfCollisionRadius();
        final Weapon weapon = (killer != null) ? killer.getActiveWeaponItem() : null;
        this._killingBlowWeaponId = ((weapon != null) ? weapon.getId() : 0);
        DecayTaskManager.getInstance().add(this);
        if (this._spawn != null) {
            final NpcSpawnTemplate npcTemplate = this._spawn.getNpcSpawnTemplate();
            if (npcTemplate != null) {
                npcTemplate.notifyNpcDeath(this, killer);
            }
        }
        if (this.getTemplate().getMpRewardValue() > 0 && GameUtils.isPlayable(killer)) {
            final Player killerPlayer = killer.getActingPlayer();
            new MpRewardTask(killerPlayer, this);
            for (final Summon summon : killerPlayer.getServitors().values()) {
                new MpRewardTask(summon, this);
            }
            if (this.getTemplate().getMpRewardAffectType() == MpRewardAffectType.PARTY) {
                final Party party = killerPlayer.getParty();
                if (party != null) {
                    for (final Player member : party.getMembers()) {
                        if (member != killerPlayer && MathUtil.isInsideRadius3D(member, this.getX(), this.getY(), this.getZ(), Config.ALT_PARTY_RANGE)) {
                            new MpRewardTask(member, this);
                            for (final Summon summon2 : member.getServitors().values()) {
                                new MpRewardTask(summon2, this);
                            }
                        }
                    }
                }
            }
        }
        DBSpawnManager.getInstance().updateStatus(this, true);
        return true;
    }
    
    @Override
    public void onSpawn() {
        super.onSpawn();
        this._soulshotamount = this.getTemplate().getSoulShot();
        this._spiritshotamount = this.getTemplate().getSpiritShot();
        this._killingBlowWeaponId = 0;
        this._isRandomAnimationEnabled = this.getTemplate().isRandomAnimationEnabled();
        this._isRandomWalkingEnabled = this.getTemplate().isRandomWalkEnabled();
        if (this.isTeleporting()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcTeleport(this), this);
        }
        else {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcSpawn(this), this);
        }
        if (!this.isTeleporting()) {
            WalkingManager.getInstance().onSpawn(this);
        }
        if (this.isInsideZone(ZoneType.TAX) && this.getCastle() != null && (Config.SHOW_CREST_WITHOUT_QUEST || this.getCastle().isShowNpcCrest()) && this.getCastle().getOwnerId() != 0) {
            this.setClanId(this.getCastle().getOwnerId());
        }
    }
    
    public void onRespawn() {
        this.setIsDead(false);
        this.getEffectList().stopAllEffects(false);
        this.setDecayed(false);
        this.setCurrentHp(this.getMaxHp(), false);
        this.setCurrentMp(this.getMaxMp(), false);
        if (this.hasVariables()) {
            this.getVariables().getSet().clear();
        }
        this.setTargetable(this.getTemplate().isTargetable());
        this.setSummoner(null);
        this.resetSummonedNpcs();
        this._nameString = null;
        this._titleString = null;
        this._params = null;
    }
    
    @Override
    public void onDecay() {
        if (this._isDecayed) {
            return;
        }
        this.setDecayed(true);
        super.onDecay();
        if (this._spawn != null) {
            this._spawn.decreaseCount(this);
        }
        WalkingManager.getInstance().onDeath(this);
        EventDispatcher.getInstance().notifyEventAsync(new OnNpcDespawn(this), this);
        final Instance instance = this.getInstanceWorld();
        if (instance != null) {
            instance.removeNpc(this);
        }
        this.scriptValue = 0;
    }
    
    @Override
    public boolean deleteMe() {
        try {
            this.onDecay();
        }
        catch (Exception e) {
            Npc.LOGGER.error("Failed decayMe().", (Throwable)e);
        }
        if (this.isChannelized()) {
            this.getSkillChannelized().abortChannelization();
        }
        ZoneManager.getInstance().getRegion(this).removeFromZones(this);
        return super.deleteMe();
    }
    
    public Spawn getSpawn() {
        return this._spawn;
    }
    
    public void setSpawn(final Spawn spawn) {
        this._spawn = spawn;
    }
    
    @Override
    public String toString() {
        return invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), this.getName(), this.getId(), this.getObjectId());
    }
    
    public boolean isDecayed() {
        return this._isDecayed;
    }
    
    public void setDecayed(final boolean decayed) {
        this._isDecayed = decayed;
    }
    
    public void endDecayTask() {
        if (!this._isDecayed) {
            DecayTaskManager.getInstance().cancel(this);
            this.onDecay();
        }
    }
    
    public void setLHandId(final int newWeaponId) {
        this._currentLHandId = newWeaponId;
        this.broadcastInfo();
    }
    
    public void setRHandId(final int newWeaponId) {
        this._currentRHandId = newWeaponId;
        this.broadcastInfo();
    }
    
    public void setLRHandId(final int newLWeaponId, final int newRWeaponId) {
        this._currentRHandId = newRWeaponId;
        this._currentLHandId = newLWeaponId;
        this.broadcastInfo();
    }
    
    public void setEnchant(final int newEnchantValue) {
        this._currentEnchant = newEnchantValue;
        this.broadcastInfo();
    }
    
    public boolean isShowName() {
        return this.getTemplate().isShowName();
    }
    
    @Override
    public double getCollisionHeight() {
        return this._currentCollisionHeight;
    }
    
    public void setCollisionHeight(final double height) {
        this._currentCollisionHeight = height;
    }
    
    @Override
    public double getCollisionRadius() {
        return this._currentCollisionRadius;
    }
    
    public void setCollisionRadius(final double radius) {
        this._currentCollisionRadius = radius;
    }
    
    @Override
    public void sendInfo(final Player activeChar) {
        if (this.isVisibleFor(activeChar)) {
            if (this.getRunSpeed() == 0.0) {
                activeChar.sendPacket(new ServerObjectInfo(this, activeChar));
            }
            else {
                activeChar.sendPacket(new NpcInfo(this));
            }
        }
    }
    
    public void scheduleDespawn(final long delay) {
        ThreadPool.schedule(() -> {
            if (!this._isDecayed) {
                this.deleteMe();
            }
        }, delay);
    }
    
    @Override
    public final void notifyQuestEventSkillFinished(final Skill skill, final WorldObject target) {
        if (target != null) {
            EventDispatcher.getInstance().notifyEventAsync(new OnNpcSkillFinished(this, target.getActingPlayer(), skill), this);
        }
    }
    
    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || !this.getTemplate().canMove() || this.getAiType() == AIType.CORPSE;
    }
    
    public AIType getAiType() {
        return this.getTemplate().getAIType();
    }
    
    public boolean hasDisplayEffect(final int val) {
        return this._displayEffect == val;
    }
    
    public int getDisplayEffect() {
        return this._displayEffect;
    }
    
    public void setDisplayEffect(final int val) {
        if (val != this._displayEffect) {
            this._displayEffect = val;
            this.broadcastPacket(new ExChangeNpcState(this.getObjectId(), val));
        }
    }
    
    public int getColorEffect() {
        return 0;
    }
    
    @Override
    public void setTeam(final Team team) {
        super.setTeam(team);
        this.broadcastInfo();
    }
    
    @Override
    public void consumeAndRechargeShots(final ShotType shotType, final int targets) {
        if (shotType == ShotType.SOULSHOTS) {
            this.consumeAndRechargeSoulShots();
        }
        else if (shotType == ShotType.SPIRITSHOTS) {
            this.consumeAndRechargeSpiritShots();
        }
    }
    
    private void consumeAndRechargeSpiritShots() {
        if (this._spiritshotamount > 0 && Rnd.chance(this.getTemplate().getSpiritShotChance())) {
            --this._spiritshotamount;
            Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2061, 1, 0, 0), 600);
            this.chargeShot(ShotType.SPIRITSHOTS, 4.0);
        }
        else {
            this.unchargeShot(ShotType.SPIRITSHOTS);
        }
    }
    
    private void consumeAndRechargeSoulShots() {
        if (this._soulshotamount > 0 && Rnd.chance(this.getTemplate().getSoulShotChance())) {
            --this._soulshotamount;
            Broadcast.toSelfAndKnownPlayersInRadius(this, new MagicSkillUse(this, this, 2154, 1, 0, 0), 600);
            this.chargeShot(ShotType.SOULSHOTS, 4.0);
        }
        else {
            this.unchargeShot(ShotType.SOULSHOTS);
        }
    }
    
    public int getScriptValue() {
        return this.scriptValue;
    }
    
    public void setScriptValue(final int value) {
        this.scriptValue = value;
    }
    
    public boolean isScriptValue(final int value) {
        return this.scriptValue == value;
    }
    
    public boolean isInMySpawnGroup(final Npc npc) {
        return this.getSpawn() != null && npc.getSpawn() != null && this.getSpawn().getName() != null && this.getSpawn().getName().equals(npc.getSpawn().getName());
    }
    
    public boolean staysInSpawnLoc() {
        return this._spawn != null && this._spawn.getX() == this.getX() && this._spawn.getY() == this.getY();
    }
    
    public boolean hasVariables() {
        return Objects.nonNull(this.variables);
    }
    
    public NpcVariables getVariables() {
        if (Objects.isNull(this.variables)) {
            this.variables = new NpcVariables();
        }
        return this.variables;
    }
    
    public void broadcastEvent(final String eventName, final int radius, final WorldObject reference) {
        World.getInstance().forEachVisibleObjectInRange(this, Npc.class, radius, obj -> {
            if (obj.hasListener(EventType.ON_NPC_EVENT_RECEIVED)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnNpcEventReceived(eventName, this, obj, reference), obj);
            }
        });
    }
    
    public void sendScriptEvent(final String eventName, final WorldObject receiver, final WorldObject reference) {
        EventDispatcher.getInstance().notifyEventAsync(new OnNpcEventReceived(eventName, this, (Npc)receiver, reference), receiver);
    }
    
    public Location getPointInRange(final int radiusMin, final int radiusMax) {
        if (radiusMax == 0 || radiusMax < radiusMin) {
            return new Location(this.getX(), this.getY(), this.getZ());
        }
        final int radius = Rnd.get(radiusMin, radiusMax);
        final double angle = Rnd.nextDouble() * 2.0 * 3.141592653589793;
        return new Location((int)(this.getX() + radius * Math.cos(angle)), (int)(this.getY() + radius * Math.sin(angle)), this.getZ());
    }
    
    public Item dropItem(final Creature character, final int itemId, final long itemCount) {
        Item item = null;
        for (int i = 0; i < itemCount; ++i) {
            final int newX = this.getX() + Rnd.get(141) - 70;
            final int newY = this.getY() + Rnd.get(141) - 70;
            final int newZ = this.getZ() + 20;
            if (ItemEngine.getInstance().getTemplate(itemId) == null) {
                Npc.LOGGER.error(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, itemId, this.getName()));
                return null;
            }
            item = ItemEngine.getInstance().createItem("Loot", itemId, itemCount, character, this);
            if (item == null) {
                return null;
            }
            if (character != null) {
                item.getDropProtection().protect(character);
            }
            item.dropMe(this, newX, newY, newZ);
            final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
            if (!generalSettings.isProtectedItem(itemId) && ((generalSettings.autoDestroyItemTime() > 0 && !item.getTemplate().hasExImmediateEffect()) || (generalSettings.autoDestroyHerbTime() > 0 && item.getTemplate().hasExImmediateEffect()))) {
                ItemsAutoDestroy.getInstance().addItem(item);
            }
            item.setProtected(false);
            if (item.isStackable()) {
                break;
            }
            if (!Config.MULTIPLE_ITEM_DROP) {
                break;
            }
        }
        return item;
    }
    
    public Item dropItem(final Creature character, final ItemHolder item) {
        return this.dropItem(character, item.getId(), item.getCount());
    }
    
    @Override
    public final String getName() {
        return this.getTemplate().getName();
    }
    
    @Override
    public boolean isVisibleFor(final Player player) {
        if (this.hasListener(EventType.ON_NPC_CAN_BE_SEEN)) {
            final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnNpcCanBeSeen(this, player), this, TerminateReturn.class);
            if (term != null) {
                return term.terminate();
            }
        }
        return super.isVisibleFor(player);
    }
    
    public void setIsTalkable(final boolean val) {
        this._isTalkable = val;
    }
    
    public boolean isTalkable() {
        return this._isTalkable;
    }
    
    public boolean isQuestMonster() {
        return this._isQuestMonster;
    }
    
    public int getKillingBlowWeapon() {
        return this._killingBlowWeaponId;
    }
    
    public void setKillingBlowWeapon(final int weaponId) {
        this._killingBlowWeaponId = weaponId;
    }
    
    public int getCloneObjId() {
        return this._cloneObjId;
    }
    
    public void setCloneObjId(final int cloneObjId) {
        this._cloneObjId = cloneObjId;
    }
    
    @Override
    public int getClanId() {
        return this._clanId;
    }
    
    public void setClanId(final int clanObjId) {
        this._clanId = clanObjId;
    }
    
    public void broadcastSay(final ChatType chatType, final String text) {
        Broadcast.toKnownPlayers(this, new NpcSay(this, chatType, text));
    }
    
    public void broadcastSay(final ChatType chatType, final NpcStringId npcStringId, final String... parameters) {
        final NpcSay npcSay = new NpcSay(this, chatType, npcStringId);
        if (parameters != null) {
            for (final String parameter : parameters) {
                if (parameter != null) {
                    npcSay.addStringParameter(parameter);
                }
            }
        }
        if (chatType == ChatType.NPC_GENERAL) {
            Broadcast.toKnownPlayersInRadius(this, npcSay, 1250);
        }
        else {
            Broadcast.toKnownPlayers(this, npcSay);
        }
    }
    
    public void broadcastSay(final ChatType chatType, final String text, final int radius) {
        Broadcast.toKnownPlayersInRadius(this, new NpcSay(this, chatType, text), radius);
    }
    
    public void broadcastSay(final ChatType chatType, final NpcStringId npcStringId, final int radius) {
        Broadcast.toKnownPlayersInRadius(this, new NpcSay(this, chatType, npcStringId), radius);
    }
    
    public StatsSet getParameters() {
        if (this._params != null) {
            return this._params;
        }
        if (this._spawn != null) {
            final NpcSpawnTemplate npcSpawnTemplate = this._spawn.getNpcSpawnTemplate();
            if (npcSpawnTemplate != null && npcSpawnTemplate.getParameters() != null && !npcSpawnTemplate.getParameters().isEmpty()) {
                final StatsSet params = this.getTemplate().getParameters();
                if (params != null && !params.getSet().isEmpty()) {
                    final StatsSet set = new StatsSet();
                    set.merge(params);
                    set.merge(npcSpawnTemplate.getParameters());
                    return this._params = set;
                }
                return this._params = npcSpawnTemplate.getParameters();
            }
        }
        return this._params = this.getTemplate().getParameters();
    }
    
    public List<Skill> getLongRangeSkills() {
        return this.getTemplate().getAISkills(AISkillScope.LONG_RANGE);
    }
    
    public List<Skill> getShortRangeSkills() {
        return this.getTemplate().getAISkills(AISkillScope.SHORT_RANGE);
    }
    
    public boolean hasSkillChance() {
        return Rnd.get(100) < Rnd.get(this.getTemplate().getMinSkillChance(), this.getTemplate().getMaxSkillChance());
    }
    
    public void initSeenCreatures() {
        this.initSeenCreatures(this.getTemplate().getAggroRange());
    }
    
    public NpcStringId getNameString() {
        return this._nameString;
    }
    
    public void setNameString(final NpcStringId nameString) {
        this._nameString = nameString;
    }
    
    public NpcStringId getTitleString() {
        return this._titleString;
    }
    
    public void setTitleString(final NpcStringId titleString) {
        this._titleString = titleString;
    }
    
    public void sendChannelingEffect(final Creature target, final int state) {
        this.broadcastPacket(new ExShowChannelingEffect(this, target, state));
    }
    
    public RaidBossStatus getRaidBossStatus() {
        return this._raidStatus;
    }
    
    public void setRaidBossStatus(final RaidBossStatus status) {
        this._raidStatus = status;
    }
}
