// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.network.serverpackets.AbstractMessagePacket;
import org.l2j.gameserver.model.actor.stat.PlayableStats;
import org.l2j.gameserver.model.actor.status.PlayableStatus;
import java.util.concurrent.TimeUnit;
import org.l2j.gameserver.world.MapRegionManager;
import org.l2j.gameserver.network.serverpackets.sessionzones.TimedHuntingZoneExit;
import org.l2j.gameserver.data.xml.impl.AttendanceRewardData;
import org.l2j.gameserver.settings.AttendanceSettings;
import java.util.Calendar;
import org.l2j.gameserver.model.holders.AttendanceInfoHolder;
import org.l2j.gameserver.model.holders.TrainingHolder;
import org.l2j.gameserver.enums.GroupType;
import org.l2j.gameserver.model.stats.MoveType;
import org.l2j.gameserver.network.serverpackets.ExQuestItemList;
import org.l2j.gameserver.network.serverpackets.item.ItemList;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.ExBloodyCoinCount;
import org.l2j.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2j.gameserver.enums.CastleSide;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.model.actor.tasks.player.RecoGiveTask;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.settings.ChatSettings;
import java.util.stream.IntStream;
import java.util.function.IntFunction;
import org.l2j.gameserver.network.serverpackets.friend.FriendStatus;
import java.time.temporal.Temporal;
import java.time.temporal.ChronoUnit;
import org.l2j.gameserver.network.serverpackets.ExStopScenePlayer;
import org.l2j.gameserver.network.serverpackets.RecipeShopMsg;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgBuy;
import org.l2j.gameserver.network.serverpackets.ExPrivateStoreSetWholeMsg;
import org.l2j.gameserver.network.serverpackets.PrivateStoreMsgSell;
import org.l2j.gameserver.network.serverpackets.GetOnVehicle;
import org.l2j.gameserver.model.actor.tasks.player.ResetChargesTask;
import org.l2j.gameserver.model.actor.transform.Transform;
import org.l2j.gameserver.util.FloodProtectors;
import org.l2j.gameserver.model.actor.tasks.player.DismountTask;
import org.l2j.gameserver.data.xml.impl.PetDataTable;
import org.l2j.gameserver.model.actor.tasks.player.PetFeedTask;
import org.l2j.gameserver.network.serverpackets.ExMagicAttackInfo;
import org.l2j.gameserver.model.actor.tasks.player.ResetSoulsTask;
import org.l2j.gameserver.model.actor.tasks.player.FameTask;
import org.l2j.gameserver.model.punishment.PunishmentType;
import org.l2j.gameserver.model.punishment.PunishmentAffect;
import org.l2j.gameserver.instancemanager.PunishmentManager;
import org.l2j.gameserver.model.ClanMember;
import org.l2j.gameserver.taskmanager.SaveTaskManager;
import org.l2j.gameserver.network.serverpackets.ExPledgeCount;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.instancemanager.MatchingRoomManager;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.network.serverpackets.Snoop;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.model.actor.tasks.player.TeleportWatchdogTask;
import org.l2j.gameserver.network.serverpackets.ExStartScenePlayer;
import org.l2j.gameserver.ai.SummonAI;
import org.l2j.gameserver.data.sql.impl.PlayerSummonTable;
import org.l2j.gameserver.settings.CharacterSettings;
import org.l2j.gameserver.network.serverpackets.ConfirmDlg;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMentorStatus;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerMenteeStatus;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.network.serverpackets.Die;
import org.l2j.gameserver.model.actor.tasks.player.WaterTask;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.tasks.player.WarnUserTakeBreakTask;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSubChange;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;
import org.l2j.gameserver.network.serverpackets.SocialAction;
import org.l2j.gameserver.network.serverpackets.ShortCutInit;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionCancel;
import io.github.joealisson.primitive.LongMap;
import java.util.HashMap;
import org.l2j.gameserver.network.serverpackets.AcquireSkillList;
import org.l2j.gameserver.network.serverpackets.SkillList;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.network.serverpackets.ObservationReturn;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMode;
import org.l2j.gameserver.network.serverpackets.ObservationMode;
import org.l2j.gameserver.network.serverpackets.ExGetBookMarkInfoPacket;
import org.l2j.gameserver.model.actor.tasks.player.InventoryEnableTask;
import org.l2j.gameserver.network.serverpackets.ExUserInfoAbnormalVisualEffect;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.enums.NextActionType;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.entity.Siege;
import org.l2j.gameserver.enums.ClanWarState;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import java.util.function.BiFunction;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHennaAdd;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerHennaRemove;
import org.l2j.gameserver.network.serverpackets.HennaInfo;
import org.l2j.gameserver.model.actor.tasks.player.HennaDurationTask;
import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.network.serverpackets.ExUseSharedGroupItem;
import org.l2j.gameserver.enums.IllegalActionPunishmentType;
import org.l2j.gameserver.data.xml.impl.ClassListData;
import org.l2j.gameserver.model.TimeStamp;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.BuffInfo;
import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.variables.AccountVariables;
import java.sql.ResultSet;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.network.authcomm.SendablePacket;
import org.l2j.gameserver.network.authcomm.gs2as.ChangeAccessLevel;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.model.CommandChannel;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.world.zone.type.WaterZone;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.network.serverpackets.Ride;
import org.l2j.gameserver.network.Disconnection;
import java.util.LinkedHashMap;
import org.l2j.gameserver.network.serverpackets.TradeDone;
import org.l2j.gameserver.network.serverpackets.TradeOtherDone;
import org.l2j.gameserver.network.serverpackets.TradeStart;
import java.util.stream.Stream;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.model.stats.Stat;
import java.util.Collections;
import java.time.Instant;
import org.l2j.gameserver.network.serverpackets.pvpbook.ExNewPk;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPvPKill;
import org.l2j.gameserver.engine.autoplay.AutoPlayEngine;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.network.serverpackets.ExDieInfo;
import org.l2j.gameserver.model.item.Armor;
import org.l2j.gameserver.network.serverpackets.TargetUnselected;
import org.l2j.gameserver.network.serverpackets.ExAbnormalStatusUpdateFromTarget;
import org.l2j.gameserver.util.Broadcast;
import org.l2j.gameserver.network.serverpackets.TargetSelected;
import org.l2j.gameserver.network.serverpackets.MyTargetSelected;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;
import org.l2j.gameserver.network.serverpackets.PrivateStoreManageListBuy;
import org.l2j.gameserver.model.item.type.EtcItemType;
import org.l2j.gameserver.model.item.type.WeaponType;
import org.l2j.gameserver.model.item.type.ArmorType;
import org.l2j.gameserver.instancemanager.ItemsOnGroundManager;
import org.l2j.gameserver.enums.PartyDistributionType;
import org.l2j.gameserver.network.serverpackets.StopMove;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.network.serverpackets.RecipeShopSellList;
import org.l2j.gameserver.network.serverpackets.PrivateStoreListBuy;
import org.l2j.gameserver.network.serverpackets.PrivateStoreListSell;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.NicknameChanged;
import org.l2j.gameserver.network.serverpackets.ExCharInfo;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.network.serverpackets.ExDuelUpdateUserInfo;
import org.l2j.gameserver.instancemanager.DuelManager;
import org.l2j.gameserver.model.olympiad.OlympiadGameManager;
import org.l2j.gameserver.enums.PartySmallWindowUpdateType;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.ItemsAutoDestroy;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.item.container.ItemContainer;
import org.l2j.gameserver.model.item.container.Inventory;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.cache.WarehouseCacheManager;
import org.l2j.gameserver.model.actor.tasks.player.StandUpTask;
import org.l2j.gameserver.model.entity.Event;
import org.l2j.gameserver.model.actor.tasks.player.SitDownTask;
import org.l2j.gameserver.network.serverpackets.ChangeWaitType;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.enums.Race;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.SiegeManager;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.network.serverpackets.ExSubjobInfo;
import org.l2j.gameserver.enums.SubclassInfoType;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListUpdate;
import org.l2j.gameserver.network.serverpackets.PartySmallWindowUpdate;
import org.l2j.gameserver.network.serverpackets.MagicSkillUse;
import org.l2j.gameserver.network.serverpackets.PledgeShowMemberListDelete;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.data.xml.CategoryManager;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerFameChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPvPChanged;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerEquipItem;
import org.l2j.gameserver.network.serverpackets.ExStorageMaxCount;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.item.BodyPart;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerReputationChanged;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.ListenersContainer;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPKChanged;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.serverpackets.ExSetCompassZoneCode;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.enums.StatusUpdateType;
import org.l2j.gameserver.network.serverpackets.StatusUpdate;
import org.l2j.gameserver.model.Macro;
import org.l2j.gameserver.network.serverpackets.ShortCutRegister;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.instancemanager.QuestManager;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import org.l2j.gameserver.model.quest.Quest;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.ai.PlayerAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.model.actor.status.CreatureStatus;
import org.l2j.gameserver.model.actor.status.PlayerStatus;
import org.l2j.gameserver.model.actor.stat.CreatureStats;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import java.util.ArrayList;
import org.l2j.gameserver.model.actor.tasks.player.PvPFlagTask;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.network.serverpackets.SetupGauge;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.actor.tasks.character.NotifyAITask;
import org.l2j.gameserver.ai.CtrlEvent;
import java.util.Collection;
import org.l2j.gameserver.model.item.container.WarehouseType;
import org.l2j.gameserver.model.WorldObject;
import java.util.function.BiConsumer;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.ExAutoSoulShot;
import org.l2j.gameserver.data.xml.impl.LevelData;
import java.time.LocalDate;
import org.l2j.gameserver.data.database.dao.PlayerVariablesDAO;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.taskmanager.AttackStanceTaskManager;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.network.serverpackets.UserInfo;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.xml.impl.AdminData;
import java.util.Iterator;
import org.l2j.gameserver.data.database.data.ElementalSpiritData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.ElementalSpiritDAO;
import java.util.Objects;
import java.util.function.Consumer;
import org.l2j.gameserver.data.database.data.Shortcut;
import java.util.function.Predicate;
import java.util.Arrays;
import org.l2j.gameserver.enums.InstanceType;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.enums.HtmlActionScope;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import io.github.joealisson.primitive.HashIntMap;
import io.github.joealisson.primitive.Containers;
import java.util.EnumMap;
import org.l2j.gameserver.model.actor.templates.CreatureTemplate;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.model.holders.SellBuffHolder;
import org.l2j.gameserver.model.holders.MovieHolder;
import org.l2j.gameserver.model.holders.SkillUseHolder;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.holders.PlayerEventHolder;
import org.l2j.gameserver.model.actor.request.AbstractRequest;
import org.l2j.gameserver.model.item.Weapon;
import org.l2j.gameserver.model.eventengine.AbstractEvent;
import org.l2j.gameserver.network.serverpackets.commission.ExResponseCommissionInfo;
import org.l2j.gameserver.model.Party;
import java.util.List;
import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.util.EnumIntBitmask;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.holders.PreparedMultisellListHolder;
import org.l2j.gameserver.model.ManufactureItem;
import org.l2j.gameserver.model.item.container.Warehouse;
import org.l2j.gameserver.model.TradeList;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.item.container.PlayerRefund;
import org.l2j.gameserver.model.item.container.PlayerWarehouse;
import org.l2j.gameserver.enums.AdminTeleportType;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.actor.Vehicle;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.model.base.SubClass;
import org.l2j.gameserver.model.PetLevelData;
import org.l2j.gameserver.model.PetData;
import org.l2j.gameserver.network.GameClient;
import java.util.concurrent.Future;
import org.l2j.gameserver.model.Fishing;
import java.util.LinkedList;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.Request;
import java.util.concurrent.atomic.AtomicInteger;
import org.l2j.gameserver.model.stats.BaseStats;
import org.l2j.gameserver.model.item.Henna;
import java.util.Set;
import org.l2j.gameserver.model.MacroList;
import org.l2j.gameserver.model.Shortcuts;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.model.item.container.PlayerFreight;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PremiumItem;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.model.TeleportBookmark;
import org.l2j.gameserver.model.ContactList;
import java.util.concurrent.locks.ReentrantLock;
import org.l2j.gameserver.model.Radar;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.database.data.CostumeCollectionData;
import java.util.concurrent.ScheduledFuture;
import org.l2j.gameserver.data.database.data.CostumeData;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.data.database.data.PlayerStatsData;
import org.l2j.gameserver.data.database.data.PlayerVariableData;
import org.l2j.gameserver.engine.autoplay.AutoPlaySettings;
import org.l2j.gameserver.api.elemental.ElementalType;
import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.DamageInfo;
import org.l2j.commons.util.collection.LimitedQueue;
import org.l2j.gameserver.enums.ShotType;
import java.util.Map;
import org.l2j.gameserver.model.actor.appearance.PlayerAppearance;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.model.actor.Playable;

public final class Player extends Playable
{
    private final PlayerData data;
    private final PlayerAppearance appearance;
    private final Map<ShotType, Integer> activeSoulShots;
    private final LimitedQueue<DamageInfo> lastDamages;
    private ElementalSpirit[] spirits;
    private ElementalType activeElementalSpiritType;
    private AutoPlaySettings autoPlaySettings;
    private PlayerVariableData variables;
    private PlayerStatsData statsData;
    private IntMap<CostumeData> costumes;
    private ScheduledFuture<?> _timedHuntingZoneFinishTask;
    private IntMap<CostumeCollectionData> costumesCollections;
    private CostumeCollectionData activeCostumesCollection;
    private IntSet teleportFavorites;
    private IntMap<String> accountPlayers;
    private byte vipTier;
    private int rank;
    private int rankRace;
    private byte shineSouls;
    private byte shadowSouls;
    private int additionalSoulshot;
    private final Radar radar;
    public static final int ID_NONE = -1;
    public static final int REQUEST_TIMEOUT = 15;
    private static final String RESTORE_SKILLS_FOR_CHAR = "SELECT skill_id,skill_level,skill_sub_level FROM character_skills WHERE charId=? AND class_index=?";
    private static final String UPDATE_CHARACTER_SKILL_LEVEL = "UPDATE character_skills SET skill_level=?, skill_sub_level=?  WHERE skill_id=? AND charId=? AND class_index=?";
    private static final String ADD_NEW_SKILLS = "REPLACE INTO character_skills (charId,skill_id,skill_level,skill_sub_level,class_index) VALUES (?,?,?,?,?)";
    private static final String DELETE_SKILL_FROM_CHAR = "DELETE FROM character_skills WHERE skill_id=? AND charId=? AND class_index=?";
    private static final String DELETE_CHAR_SKILLS = "DELETE FROM character_skills WHERE charId=? AND class_index=?";
    private static final String ADD_SKILL_SAVE = "INSERT INTO character_skills_save (charId,skill_id,skill_level,skill_sub_level,remaining_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)";
    private static final String RESTORE_SKILL_SAVE = "SELECT skill_id,skill_level,skill_sub_level,remaining_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE charId=? AND class_index=? ORDER BY buff_index ASC";
    private static final String DELETE_SKILL_SAVE = "DELETE FROM character_skills_save WHERE charId=? AND class_index=?";
    private static final String ADD_ITEM_REUSE_SAVE = "INSERT INTO character_item_reuse_save (charId,itemId,itemObjId,reuseDelay,systime) VALUES (?,?,?,?,?)";
    private static final String RESTORE_ITEM_REUSE_SAVE = "SELECT charId,itemId,itemObjId,reuseDelay,systime FROM character_item_reuse_save WHERE charId=?";
    private static final String DELETE_ITEM_REUSE_SAVE = "DELETE FROM character_item_reuse_save WHERE charId=?";
    private static final String INSERT_CHARACTER = "INSERT INTO characters (account_name,charId,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,reputation,fame,raidbossPoints,pvpkills,pkkills,clanid,race,classid,cancraft,title,title_color,online,clan_privs,wantspeace,base_class,nobless,power_grade,vitality_points,createDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CHARACTER = "UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,reputation=?,fame=?,raidbossPoints=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,title=?,title_color=?,online=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,nobless=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,bookmarkslot=?,vitality_points=?,language=?,pccafe_points=? WHERE charId=?";
    private static final String INSERT_TP_BOOKMARK = "INSERT INTO character_tpbookmark (charId,Id,x,y,z,icon,tag,name) values (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_TP_BOOKMARK = "UPDATE character_tpbookmark SET icon=?,tag=?,name=? where charId=? AND Id=?";
    private static final String RESTORE_TP_BOOKMARK = "SELECT Id,x,y,z,icon,tag,name FROM character_tpbookmark WHERE charId=?";
    private static final String DELETE_TP_BOOKMARK = "DELETE FROM character_tpbookmark WHERE charId=? AND Id=?";
    private static final String RESTORE_CHAR_SUBCLASSES = "SELECT class_id,exp,sp,level,vitality_points,class_index,dual_class FROM character_subclasses WHERE charId=? ORDER BY class_index ASC";
    private static final String ADD_CHAR_SUBCLASS = "INSERT INTO character_subclasses (charId,class_id,exp,sp,level,vitality_points,class_index,dual_class) VALUES (?,?,?,?,?,?,?,?)";
    private static final String UPDATE_CHAR_SUBCLASS = "UPDATE character_subclasses SET exp=?,sp=?,level=?,vitality_points=?,class_id=?,dual_class=? WHERE charId=? AND class_index =?";
    private static final String DELETE_CHAR_SUBCLASS = "DELETE FROM character_subclasses WHERE charId=? AND class_index=?";
    private static final String RESTORE_CHAR_HENNAS = "SELECT slot,symbol_id FROM character_hennas WHERE charId=? AND class_index=?";
    private static final String ADD_CHAR_HENNA = "INSERT INTO character_hennas (charId,symbol_id,slot,class_index) VALUES (?,?,?,?)";
    private static final String DELETE_CHAR_HENNA = "DELETE FROM character_hennas WHERE charId=? AND slot=? AND class_index=?";
    private static final String DELETE_CHAR_HENNAS = "DELETE FROM character_hennas WHERE charId=? AND class_index=?";
    private static final String DELETE_CHAR_RECIPE_SHOP = "DELETE FROM character_recipeshoplist WHERE charId=?";
    private static final String INSERT_CHAR_RECIPE_SHOP = "REPLACE INTO character_recipeshoplist (`charId`, `recipeId`, `price`, `index`) VALUES (?, ?, ?, ?)";
    private static final String RESTORE_CHAR_RECIPE_SHOP = "SELECT * FROM character_recipeshoplist WHERE charId=? ORDER BY `index`";
    private static final int FALLING_VALIDATION_DELAY = 1000;
    private static final String TRAINING_CAMP_VAR = "TRAINING_CAMP";
    private static final String TRAINING_CAMP_DURATION = "TRAINING_CAMP_DURATION";
    private static final String ATTENDANCE_DATE_VAR = "ATTENDANCE_DATE";
    private static final String ATTENDANCE_INDEX_VAR = "ATTENDANCE_INDEX";
    private final ReentrantLock _subclassLock;
    private final ContactList _contactList;
    private final Map<Integer, TeleportBookmark> _tpbookmarks;
    private final Map<Integer, RecipeList> _dwarvenRecipeBook;
    private final Map<Integer, RecipeList> _commonRecipeBook;
    private final Map<Integer, PremiumItem> _premiumItems;
    private final Location _lastServerPosition;
    private final PlayerInventory inventory;
    private final PlayerFreight _freight;
    private final Map<String, QuestState> _quests;
    private final Shortcuts shortcuts;
    private final MacroList macros;
    private final Set<Player> _snoopListener;
    private final Set<Player> _snoopedPlayer;
    private final Henna[] _henna;
    private final Map<BaseStats, Integer> _hennaBaseStats;
    private final Map<Integer, ScheduledFuture<?>> _hennaRemoveSchedules;
    private final AtomicInteger _charges;
    private final Request _request;
    private final Map<Integer, CubicInstance> _cubics;
    private final int[] _race;
    private final BlockList _blockList;
    private final int[] _htmlActionOriginObjectIds;
    private final LinkedList<String>[] htmlActionCaches;
    private final Fishing _fishing;
    private final Set<Integer> _whisperers;
    private final IntSet friends;
    protected int _activeClass;
    protected int _classIndex;
    protected Future<?> _mountFeedTask;
    protected boolean _recoTwoHoursGiven;
    protected boolean _inventoryDisable;
    private GameClient _client;
    private String _ip;
    private String _lang;
    private String _htmlPrefix;
    private volatile boolean _isOnline;
    private long _onlineTime;
    private long _onlineBeginTime;
    private long _uptime;
    private int _controlItemId;
    private PetData _data;
    private PetLevelData _leveldata;
    private int _curFeed;
    private ScheduledFuture<?> _dismountTask;
    private boolean _petItems;
    private final IntMap<SubClass> _subClasses;
    private int _pvpKills;
    private int _pkKills;
    private byte _pvpFlag;
    private int _fame;
    private ScheduledFuture<?> _fameTask;
    private volatile ScheduledFuture<?> _teleportWatchdog;
    private byte _siegeState;
    private int _siegeSide;
    private int _curWeightPenalty;
    private int _lastCompassZone;
    private int _bookmarkslot;
    private boolean _canFeed;
    private boolean _isInSiege;
    private boolean _isInHideoutSiege;
    private boolean _inOlympiadMode;
    private boolean _OlympiadStart;
    private int _olympiadGameId;
    private int _olympiadSide;
    private boolean _isInDuel;
    private boolean _startingDuel;
    private int _duelState;
    private int _duelId;
    private SystemMessageId _noDuelReason;
    private Vehicle _vehicle;
    private Location _inVehiclePosition;
    private MountType _mountType;
    private int _mountNpcId;
    private int _mountLevel;
    private int mountObjectID;
    private AdminTeleportType _teleportType;
    private boolean _inCrystallize;
    private boolean _isCrafting;
    private long _offlineShopStart;
    private boolean _waitTypeSitting;
    private Location _lastLoc;
    private boolean _observerMode;
    private int _recomHave;
    private int _recomLeft;
    private ScheduledFuture<?> _recoGiveTask;
    private ScheduledFuture<?> _onlineTimeUpdateTask;
    private PlayerWarehouse _warehouse;
    private PlayerRefund _refund;
    private PrivateStoreType privateStoreType;
    private TradeList activeTradeList;
    private Warehouse activeWarehouse;
    private volatile Map<Integer, ManufactureItem> _manufactureItems;
    private String _storeName;
    private TradeList _sellList;
    private TradeList _buyList;
    private PreparedMultisellListHolder _currentMultiSell;
    private boolean _noble;
    private boolean _hero;
    private Npc _lastFolkNpc;
    private int _questNpcObject;
    private boolean _simulatedTalking;
    private Pet pet;
    private volatile Map<Integer, Summon> _servitors;
    private int _agathionId;
    private volatile Set<TamedBeast> tamedBeast;
    private MatchingRoom _matchingRoom;
    private int clanId;
    private Clan _clan;
    private volatile EnumIntBitmask<ClanPrivilege> _clanPrivileges;
    private int _pledgeClass;
    private ScheduledFuture<?> _chargeTask;
    private int _souls;
    private ScheduledFuture<?> _soulTask;
    private Location _currentSkillWorldPosition;
    private AccessLevel accessLevel;
    private boolean messageRefusing;
    private boolean _silenceMode;
    private List<Integer> _silenceModeExcluded;
    private boolean _dietMode;
    private boolean tradeRefusing;
    private Party _party;
    private Player activeRequester;
    private long requestExpireTime;
    private long _spawnProtectEndTime;
    private long _teleportProtectEndTime;
    private volatile Map<Integer, ExResponseCommissionInfo> _lastCommissionInfos;
    private volatile Map<Class<? extends AbstractEvent>, AbstractEvent<?>> _events;
    private boolean _isOnCustomEvent;
    private long _recentFakeDeathEndTime;
    private Weapon _fistsWeaponItem;
    private volatile Map<Class<? extends AbstractRequest>, AbstractRequest> requests;
    private PlayerEventHolder eventStatus;
    private byte _handysBlockCheckerEventArena;
    private volatile Map<Integer, Skill> _transformSkills;
    private ScheduledFuture<?> _taskRentPet;
    private ScheduledFuture<?> _taskWater;
    private ScheduledFuture<?> _skillListRefreshTask;
    private int _lastHtmlActionOriginObjId;
    private SkillUseHolder _queuedSkill;
    private boolean _canRevive;
    private int _reviveRequested;
    private double _revivePower;
    private boolean _revivePet;
    private double _cpUpdateIncCheck;
    private double _cpUpdateDecCheck;
    private double _cpUpdateInterval;
    private double _mpUpdateIncCheck;
    private double _mpUpdateDecCheck;
    private double _mpUpdateInterval;
    private double _originalCp;
    private double _originalHp;
    private double _originalMp;
    private int _clientX;
    private int _clientY;
    private int _clientZ;
    private int _clientHeading;
    private volatile long _fallingTimestamp;
    private volatile int _fallingDamage;
    private Future<?> _fallingDamageTask;
    private int _multiSocialTarget;
    private int _multiSociaAction;
    private MovieHolder _movieHolder;
    private String _adminConfirmCmd;
    private volatile long _lastItemAuctionInfoRequest;
    private Future<?> _PvPRegTask;
    private long _pvpFlagLasts;
    private long _notMoveUntil;
    private Map<Integer, Skill> _customSkills;
    private volatile int _actionMask;
    private int _questZoneId;
    private String _lastPetitionGmName;
    private boolean hasCharmOfCourage;
    private boolean _isSellingBuffs;
    private List<SellBuffHolder> _sellingBuffs;
    private volatile Set<QuestState> _notifyQuestOfDeathList;
    private ScheduledFuture<?> _taskWarnUserTakeBreak;
    
    Player(final PlayerData playerData, final PlayerTemplate template) {
        super(playerData.getCharId(), template);
        this.activeSoulShots = new EnumMap<ShotType, Integer>(ShotType.class);
        this.lastDamages = (LimitedQueue<DamageInfo>)new LimitedQueue(30);
        this.costumes = (IntMap<CostumeData>)Containers.emptyIntMap();
        this.costumesCollections = (IntMap<CostumeCollectionData>)Containers.emptyIntMap();
        this.accountPlayers = (IntMap<String>)new HashIntMap();
        this._subclassLock = new ReentrantLock();
        this._contactList = new ContactList(this);
        this._tpbookmarks = new ConcurrentSkipListMap<Integer, TeleportBookmark>();
        this._dwarvenRecipeBook = new ConcurrentSkipListMap<Integer, RecipeList>();
        this._commonRecipeBook = new ConcurrentSkipListMap<Integer, RecipeList>();
        this._premiumItems = new ConcurrentSkipListMap<Integer, PremiumItem>();
        this._lastServerPosition = new Location(0, 0, 0);
        this.inventory = new PlayerInventory(this);
        this._freight = new PlayerFreight(this);
        this._quests = new ConcurrentHashMap<String, QuestState>();
        this.shortcuts = new Shortcuts(this);
        this.macros = new MacroList(this);
        this._snoopListener = (Set<Player>)ConcurrentHashMap.newKeySet();
        this._snoopedPlayer = (Set<Player>)ConcurrentHashMap.newKeySet();
        this._henna = new Henna[3];
        this._hennaBaseStats = new ConcurrentHashMap<BaseStats, Integer>();
        this._hennaRemoveSchedules = new ConcurrentHashMap<Integer, ScheduledFuture<?>>(3);
        this._charges = new AtomicInteger();
        this._request = new Request(this);
        this._cubics = new ConcurrentSkipListMap<Integer, CubicInstance>();
        this._race = new int[2];
        this._blockList = new BlockList(this);
        this._htmlActionOriginObjectIds = new int[HtmlActionScope.values().length];
        this.htmlActionCaches = (LinkedList<String>[])new LinkedList[HtmlActionScope.values().length];
        this._fishing = new Fishing(this);
        this._whisperers = (Set<Integer>)ConcurrentHashMap.newKeySet();
        this.friends = (IntSet)CHashIntMap.newKeySet();
        this._classIndex = 0;
        this._recoTwoHoursGiven = false;
        this._inventoryDisable = false;
        this._ip = "N/A";
        this._lang = null;
        this._htmlPrefix = null;
        this._isOnline = false;
        this._petItems = false;
        this._subClasses = (IntMap<SubClass>)new CHashIntMap();
        this._siegeState = 0;
        this._siegeSide = 0;
        this._curWeightPenalty = 0;
        this._bookmarkslot = 0;
        this._isInHideoutSiege = false;
        this._inOlympiadMode = false;
        this._OlympiadStart = false;
        this._olympiadGameId = -1;
        this._olympiadSide = -1;
        this._isInDuel = false;
        this._startingDuel = false;
        this._duelState = 0;
        this._duelId = 0;
        this._noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
        this._vehicle = null;
        this._mountType = MountType.NONE;
        this.mountObjectID = 0;
        this._teleportType = AdminTeleportType.NORMAL;
        this._offlineShopStart = 0L;
        this._waitTypeSitting = false;
        this._observerMode = false;
        this.privateStoreType = PrivateStoreType.NONE;
        this._storeName = "";
        this._currentMultiSell = null;
        this._noble = false;
        this._hero = false;
        this._lastFolkNpc = null;
        this._questNpcObject = 0;
        this._simulatedTalking = false;
        this.pet = null;
        this._servitors = null;
        this._agathionId = 0;
        this.tamedBeast = null;
        this._clanPrivileges = new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, false);
        this._pledgeClass = 0;
        this._chargeTask = null;
        this._souls = 0;
        this._soulTask = null;
        this.messageRefusing = false;
        this._silenceMode = false;
        this._dietMode = false;
        this.tradeRefusing = false;
        this.requestExpireTime = 0L;
        this._spawnProtectEndTime = 0L;
        this._teleportProtectEndTime = 0L;
        this._isOnCustomEvent = false;
        this._recentFakeDeathEndTime = 0L;
        this.eventStatus = null;
        this._handysBlockCheckerEventArena = -1;
        this._canRevive = true;
        this._reviveRequested = 0;
        this._revivePower = 0.0;
        this._revivePet = false;
        this._cpUpdateIncCheck = 0.0;
        this._cpUpdateDecCheck = 0.0;
        this._cpUpdateInterval = 0.0;
        this._mpUpdateIncCheck = 0.0;
        this._mpUpdateDecCheck = 0.0;
        this._mpUpdateInterval = 0.0;
        this._originalCp = 0.0;
        this._originalHp = 0.0;
        this._originalMp = 0.0;
        this._fallingTimestamp = 0L;
        this._fallingDamage = 0;
        this._fallingDamageTask = null;
        this._multiSocialTarget = 0;
        this._multiSociaAction = 0;
        this._movieHolder = null;
        this._adminConfirmCmd = null;
        this._lastItemAuctionInfoRequest = 0L;
        this._notMoveUntil = 0L;
        this._customSkills = null;
        this._questZoneId = -1;
        this._lastPetitionGmName = null;
        this.hasCharmOfCourage = false;
        this._isSellingBuffs = false;
        this._sellingBuffs = null;
        this.data = playerData;
        this.setName(playerData.getName());
        this.setInstanceType(InstanceType.L2PcInstance);
        this.initCharStatusUpdateValues();
        this.appearance = new PlayerAppearance(this, playerData.getFace(), playerData.getHairColor(), playerData.getHairStyle(), playerData.isFemale());
        this.getAI();
        this.radar = new Radar(this);
        Arrays.fill(this.htmlActionCaches, new LinkedList());
        this.running = true;
        this.setAccessLevel(playerData.getAccessLevel(), false, false);
    }
    
    public void deleteShortcuts(final Predicate<Shortcut> filter) {
        this.shortcuts.deleteShortcuts(filter);
    }
    
    public void forEachShortcut(final Consumer<Shortcut> action) {
        this.shortcuts.forEachShortcut(action);
    }
    
    public void initElementalSpirits() {
        if (Objects.nonNull(this.spirits)) {
            return;
        }
        this.tryLoadSpirits();
        if (Objects.isNull(this.spirits)) {
            final ElementalType[] types = ElementalType.values();
            this.spirits = new ElementalSpirit[types.length - 1];
            for (final ElementalType type : types) {
                if (ElementalType.NONE != type) {
                    final ElementalSpirit spirit = new ElementalSpirit(type, this);
                    (this.spirits[type.getId() - 1] = spirit).save();
                }
            }
        }
        if (Objects.isNull(this.activeElementalSpiritType)) {
            this.activeElementalSpiritType = ElementalType.FIRE;
        }
    }
    
    private void tryLoadSpirits() {
        final List<ElementalSpiritData> spiritsData = ((ElementalSpiritDAO)DatabaseAccess.getDAO((Class)ElementalSpiritDAO.class)).findByPlayerId(this.getObjectId());
        if (!spiritsData.isEmpty()) {
            this.spirits = new ElementalSpirit[ElementalType.values().length - 1];
            for (final ElementalSpiritData spiritData : spiritsData) {
                this.spirits[spiritData.getType() - 1] = new ElementalSpirit(spiritData, this);
                if (spiritData.isInUse()) {
                    this.activeElementalSpiritType = ElementalType.of(spiritData.getType());
                }
            }
        }
    }
    
    public void setAccessLevel(final int level, final boolean broadcast, final boolean updateInDb) {
        this.accessLevel = AdminData.getInstance().getAccessLevelOrDefault(level);
        this.appearance.setNameColor(this.accessLevel.getNameColor());
        this.appearance.setTitleColor(this.accessLevel.getTitleColor());
        if (broadcast) {
            this.broadcastUserInfo();
        }
        if (updateInDb) {
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updateAccessLevel(this.objectId, this.accessLevel.getLevel());
        }
        PlayerNameTable.getInstance().addName(this);
        if (level > 0) {
            Player.LOGGER.warn("{} access level set for player {} ! Just a warning to be careful ;)", (Object)this.accessLevel.getName(), (Object)this);
        }
    }
    
    public ElementalSpirit getElementalSpirit(final ElementalType type) {
        if (Objects.isNull(this.spirits) || Objects.isNull(type) || type == ElementalType.NONE) {
            return null;
        }
        return this.spirits[type.getId() - 1];
    }
    
    public void changeElementalSpirit(final byte element) {
        this.activeElementalSpiritType = ElementalType.of(element);
        final UserInfo userInfo = new UserInfo(this, false);
        userInfo.addComponentType(UserInfoType.SPIRITS);
        this.sendPacket(userInfo);
    }
    
    public double getActiveElementalSpiritAttack() {
        return this.getStats().getElementalSpiritPower(this.activeElementalSpiritType, Util.zeroIfNullOrElse((Object)this.getElementalSpirit(this.activeElementalSpiritType), ElementalSpirit::getAttack));
    }
    
    public double getFireSpiritDefense() {
        return this.getElementalSpiritDefenseOf(ElementalType.FIRE);
    }
    
    public double getWaterSpiritDefense() {
        return this.getElementalSpiritDefenseOf(ElementalType.WATER);
    }
    
    public double getWindSpiritDefense() {
        return this.getElementalSpiritDefenseOf(ElementalType.WIND);
    }
    
    public double getEarthSpiritDefense() {
        return this.getElementalSpiritDefenseOf(ElementalType.EARTH);
    }
    
    @Override
    public double getElementalSpiritDefenseOf(final ElementalType type) {
        return this.getStats().getElementalSpiritDefense(type, Util.zeroIfNullOrElse((Object)this.getElementalSpirit(type), ElementalSpirit::getDefense));
    }
    
    public double getElementalSpiritCritRate() {
        return this.getStats().getElementalSpiritCriticalRate(Util.zeroIfNullOrElse((Object)this.getElementalSpirit(this.activeElementalSpiritType), ElementalSpirit::getCriticalRate));
    }
    
    public double getElementalSpiritCritDamage() {
        return this.getStats().getElementalSpiritCriticalDamage(Util.zeroIfNullOrElse((Object)this.getElementalSpirit(this.activeElementalSpiritType), ElementalSpirit::getCriticalDamage));
    }
    
    public double getElementalSpiritXpBonus() {
        return this.getStats().getElementalSpiritXpBonus();
    }
    
    public byte getActiveElementalSpiritType() {
        return (byte)Util.zeroIfNullOrElse((Object)this.activeElementalSpiritType, ElementalType::getId);
    }
    
    public ElementalSpirit[] getSpirits() {
        return this.spirits;
    }
    
    public byte getVipTier() {
        return this.vipTier;
    }
    
    public void setVipTier(final byte vipTier) {
        this.vipTier = vipTier;
    }
    
    public long getVipPoints() {
        return this.getClient().getVipPoints();
    }
    
    public void updateVipPoints(final long points) {
        this.getClient().updateVipPoints(points);
    }
    
    public void setNCoins(final int coins) {
        this.getClient().setCoin(coins);
    }
    
    public int getNCoins() {
        return this.getClient().getCoin();
    }
    
    public void updateNCoins(final int coins) {
        this.getClient().updateCoin(coins);
    }
    
    public long getRustyCoin() {
        return this.inventory.getRustyCoin();
    }
    
    public long getSilverCoin() {
        return this.inventory.getSilverCoin();
    }
    
    public long getVipTierExpiration() {
        return this.getClient().getVipTierExpiration();
    }
    
    public void setVipTierExpiration(final long expiration) {
        this.getClient().setVipTierExpiration(expiration);
    }
    
    public long getLCoins() {
        return this.inventory.getLCoin();
    }
    
    public void addLCoins(final long count) {
        this.inventory.addLCoin(count);
    }
    
    public boolean isInBattle() {
        return AttackStanceTaskManager.getInstance().hasAttackStanceTask(this);
    }
    
    public void setAutoPlaySettings(final AutoPlaySettings autoPlaySettings) {
        this.autoPlaySettings = autoPlaySettings;
    }
    
    public AutoPlaySettings getAutoPlaySettings() {
        return this.autoPlaySettings;
    }
    
    public int getShortcutAmount() {
        return this.shortcuts.getAmount();
    }
    
    public void setActiveAutoShortcut(final int room, final boolean active) {
        this.shortcuts.setActive(room, active);
    }
    
    public Shortcut nextAutoShortcut() {
        return this.shortcuts.nextAutoShortcut();
    }
    
    public void resetNextAutoShortcut() {
        this.shortcuts.resetNextAutoShortcut();
    }
    
    public void setRank(final int rank) {
        this.rank = rank;
    }
    
    public int getRank() {
        return this.rank;
    }
    
    public void setRankRace(final int rankRace) {
        this.rankRace = rankRace;
    }
    
    public int getRankRace() {
        return this.rankRace;
    }
    
    public boolean isHairAccessoryEnabled() {
        return this.variables.isHairAccessoryEnabled();
    }
    
    public int getWorldChatUsed() {
        return this.variables.getWorldChatUsed();
    }
    
    public int getVitalityItemsUsed() {
        return this.variables.getVitalityItemsUsed();
    }
    
    public int getAbilityPointsMainClassUsed() {
        return this.variables.getAbilityPointsMainClassUsed();
    }
    
    public int getAbilityPointsDualClassUsed() {
        return this.variables.getAbilityPointsDualClassUsed();
    }
    
    public int getRevelationSkillMainClass1() {
        return this.variables.getRevelationSkillMainClass1();
    }
    
    public int getRevelationSkillMainClass2() {
        return this.variables.getRevelationSkillMainClass2();
    }
    
    public int getRevelationSkillDualClass1() {
        return this.variables.getRevelationSkillDualClass1();
    }
    
    public int getRevelationSkillDualClass2() {
        return this.variables.getRevelationSkillDualClass2();
    }
    
    public String getExtendDrop() {
        return this.variables.getExtendDrop();
    }
    
    public int getFortuneTelling() {
        return this.variables.getFortuneTelling();
    }
    
    public boolean isFortuneTellingBlackCat() {
        return this.variables.isFortuneTellingBlackCat();
    }
    
    public long getHuntingZoneResetTime(final int zoneId) {
        final String[] timeZones = this.variables.getHuntingZoneResetTime().split(";");
        for (int i = 0; i < timeZones.length; i += 2) {
            if (timeZones[i].equalsIgnoreCase(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, zoneId))) {
                return Long.parseLong(timeZones[i + 1]);
            }
        }
        return 0L;
    }
    
    public int getAutoCp() {
        return this.variables.getAutoCp();
    }
    
    public int getAutoHp() {
        return this.variables.getAutoHp();
    }
    
    public int getAutoMp() {
        return this.variables.getAutoMp();
    }
    
    public boolean getExpOff() {
        return this.variables.getExpOff();
    }
    
    public boolean isItemsRewarded() {
        return this.variables.isItemsRewarded();
    }
    
    public long getHennaDuration(final int slot) {
        long n = 0L;
        switch (slot) {
            case 1: {
                n = this.variables.getHenna1Duration();
                break;
            }
            case 2: {
                n = this.variables.getHenna2Duration();
                break;
            }
            case 3: {
                n = this.variables.getHenna3Duration();
                break;
            }
            default: {
                n = 0L;
                break;
            }
        }
        return n;
    }
    
    public int getVisualHair() {
        return this.variables.getVisualHairId();
    }
    
    public int getVisualHairColor() {
        return this.variables.getVisualHairColorId();
    }
    
    public int getVisualFace() {
        return this.variables.getVisualFaceId();
    }
    
    public int[] getInstanceOrigin() {
        return this.variables.getInstanceOrigin();
    }
    
    public int getInstanceRestore() {
        return this.variables.getInstanceRestore();
    }
    
    public int getMentorPenaltyId() {
        return this.variables.getMentorPenaltyId();
    }
    
    public long getMentorPenaltyTime() {
        return this.variables.getMentorPenaltyTime();
    }
    
    public int getClaimedClanRewards(final int defaultValue) {
        return (this.variables.getClaimedClanRewards() != 0) ? this.variables.getClaimedClanRewards() : defaultValue;
    }
    
    public String getCondOverrideKey() {
        return this.isGM() ? (this.variables.getCondOverrideKey().trim().equalsIgnoreCase("") ? Long.toString(PcCondOverride.getAllExceptionsMask()) : this.variables.getCondOverrideKey()) : this.variables.getCondOverrideKey();
    }
    
    public long getAttendanceDate() {
        return this.variables.getAttendanceDate();
    }
    
    public int getAttendanceIndex() {
        return this.variables.getAttendanceIndex();
    }
    
    public int getUnclaimedOlympiadPoints() {
        return this.variables.getUnclaimedOlympiadPoints();
    }
    
    public int getMonsterReturn() {
        return this.variables.getMonsterReturn();
    }
    
    public String getUiKeyMapping() {
        return this.variables.getUiKeyMapping();
    }
    
    public void setHairAccessoryEnabled(final boolean hairAccessory_Enabled) {
        this.variables.setHairAccessoryEnabled(hairAccessory_Enabled);
    }
    
    public void setWorldChatUsed(final int timesUsed) {
        this.variables.setWorldChatUsed(timesUsed);
    }
    
    public void setVitalityItemsUsed(final int vitalityItemsUsed) {
        this.variables.setVitalityItemsUsed(vitalityItemsUsed);
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).save((Object)this.variables);
    }
    
    public void setAbilityPointsMainClassUsed(final int abilityPointsMainClassUsed) {
        this.variables.setAbilityPointsMainClassUsed(abilityPointsMainClassUsed);
    }
    
    public void setAbilityPointsDualClassUsed(final int abilityPointsDualClassUsed) {
        this.variables.setAbilityPointsDualClassUsed(abilityPointsDualClassUsed);
    }
    
    public void setRevelationSkillMainClass1(final int revelationSkillMainClass1) {
        this.variables.setRevelationSkillMainClass1(revelationSkillMainClass1);
    }
    
    public void setRevelationSkillMainClass2(final int revelationSkillMainClass2) {
        this.variables.setRevelationSkillMainClass2(revelationSkillMainClass2);
    }
    
    public void setRevelationSkillDualClass1(final int revelationSkillDualClass1) {
        this.variables.setRevelationSkillDualClass1(revelationSkillDualClass1);
    }
    
    public void setRevelationSkillDualClass2(final int revelationSkillDualClass2) {
        this.variables.setRevelationSkillMainClass2(revelationSkillDualClass2);
    }
    
    public void setExtendDrop(final String extendDrop) {
        this.variables.setExtendDrop(extendDrop);
    }
    
    public void setFortuneTelling(final int fortuneTelling) {
        this.variables.setFortuneTelling(fortuneTelling);
    }
    
    public void setFortuneTellingBlackCat(final boolean fortuneTellingBlackCat) {
        this.variables.setFortuneTellingBlackCat(fortuneTellingBlackCat);
    }
    
    public void setHuntingZoneResetTime(final int zoneId, final long huntingZoneResetTime) {
        if (this.variables.getHuntingZoneResetTime().equalsIgnoreCase("")) {
            this.variables.setHuntingZoneResetTime(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, zoneId, huntingZoneResetTime));
        }
        else {
            this.variables.setHuntingZoneResetTime(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;IJ)Ljava/lang/String;, this.variables.getHuntingZoneResetTime(), zoneId, huntingZoneResetTime));
        }
    }
    
    public void setAutoCp(final int autoCp) {
        this.variables.setAutoCp(autoCp);
    }
    
    public void setAutoHp(final int autoHp) {
        this.variables.setAutoHp(autoHp);
    }
    
    public void setAutoMp(final int autoMp) {
        this.variables.setAutoMp(autoMp);
    }
    
    public void setExpOff(final boolean expOff) {
        this.variables.setExpOff(expOff);
    }
    
    public void setItemsRewarded(final boolean itemsRewarded) {
        this.variables.setItemsRewarded(itemsRewarded);
    }
    
    public void setHennaDuration(final long hennaDuration, final int slot) {
        switch (slot) {
            case 1: {
                this.variables.setHenna1Duration(hennaDuration);
                break;
            }
            case 2: {
                this.variables.setHenna2Duration(hennaDuration);
                break;
            }
            case 3: {
                this.variables.setHenna3Duration(hennaDuration);
                break;
            }
        }
    }
    
    public void setVisualHair(final int visualHairId) {
        this.variables.setVisualHairId(visualHairId);
    }
    
    public void setVisualHairColor(final int visualHairColorId) {
        this.variables.setVisualHairColorId(visualHairColorId);
    }
    
    public void setVisualFace(final int visualFaceId) {
        this.variables.setVisualFaceId(visualFaceId);
    }
    
    public void setInstanceOrigin(final String instanceOrigin) {
        this.variables.setInstanceOrigin(instanceOrigin);
    }
    
    public void setInstanceRestore(final int instanceRestore) {
        this.variables.setInstanceRestore(instanceRestore);
    }
    
    public void setMentorPenaltyId(final int mentorPenaltyId) {
        this.variables.setMentorPenaltyId(mentorPenaltyId);
    }
    
    public void setMentorPenaltyTime(final long mentorPenaltyTime) {
        this.variables.setMentorPenaltyTime(mentorPenaltyTime);
    }
    
    public void setClaimedClanRewards(final int claimedClanRewards) {
        this.variables.setClaimedClanRewards(claimedClanRewards);
    }
    
    public void setCondOverrideKey(final String condOverrideKey) {
        this.variables.setCondOverrideKey(condOverrideKey);
    }
    
    public void setAttendanceDate(final long attendanceDate) {
        this.variables.setAttendanceDate(attendanceDate);
    }
    
    public void setAttendanceIndex(final int attendanceIndex) {
        this.variables.setAttendanceIndex(attendanceIndex);
    }
    
    public void setUnclaimedOlympiadPoints(final int unclaimedOlympiadPoints) {
        this.variables.setUnclaimedOlympiadPoints(unclaimedOlympiadPoints);
    }
    
    public void setMonsterReturn(final int monsterReturn) {
        this.variables.setMonsterReturn(monsterReturn);
    }
    
    public void setUiKeyMapping(final String uiKeyMapping) {
        this.variables.setUiKeyMapping(uiKeyMapping);
    }
    
    public int getRevengeUsableLocation() {
        return this.variables.getRevengeLocations();
    }
    
    public int getRevengeUsableTeleport() {
        return this.variables.getRevengeTeleports();
    }
    
    public void useRevengeLocation() {
        this.variables.useRevengeLocation();
    }
    
    public void useRevengeTeleport() {
        this.variables.useRevengeTeleport();
    }
    
    public void resetRevengeData() {
        this.variables.resetRevengeData();
    }
    
    public void updateExtendDrop(final int id, final long count) {
        StringBuilder result = new StringBuilder();
        final String data = this.getExtendDrop();
        if (data.isEmpty()) {
            result = new StringBuilder(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, id, count));
        }
        else if (data.contains(";")) {
            for (final String s : data.split(";")) {
                final String[] drop = s.split(",");
                if (!drop[0].equals(Integer.toString(id))) {
                    result.append(";").append(s);
                }
            }
            result = new StringBuilder(result.substring(1));
        }
        else {
            result = new StringBuilder(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, id, count));
        }
        this.variables.setExtendDrop(result.toString());
    }
    
    public long getExtendDropCount(final int id) {
        final String data = this.getExtendDrop();
        for (final String s : data.split(";")) {
            final String[] drop = s.split(",");
            if (drop[0].equals(Integer.toString(id))) {
                return Long.parseLong(drop[1]);
            }
        }
        return 0L;
    }
    
    public LocalDate getCreateDate() {
        return this.data.getCreateDate();
    }
    
    public PlayerStatsData getStatsData() {
        return this.statsData;
    }
    
    public void updateCharacteristicPoints() {
        this.statsData.setPoints(LevelData.getInstance().getCharacteristicPoints(this.getLevel()));
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).save(this.statsData);
    }
    
    public void enableAutoSoulShot(final ShotType type, final int itemId) {
        this.activeSoulShots.put(type, itemId);
        this.sendPacket(new ExAutoSoulShot(itemId, true, type.getClientType()), ((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED)).addItemName(itemId));
        this.rechargeShot(type);
    }
    
    public boolean rechargeShot(final ShotType type) {
        final Integer itemId = this.activeSoulShots.get(type);
        if (!Objects.nonNull(itemId)) {
            return false;
        }
        final Item item = this.inventory.getItemByItemId(itemId);
        if (Objects.isNull(item)) {
            this.disableAutoShot(type);
            return false;
        }
        return Util.falseIfNullOrElse((Object)ItemHandler.getInstance().getHandler(item.getEtcItem()), handler -> handler.useItem(this, item, false));
    }
    
    public void disableSummonAutoShot() {
        this.disableAutoShot(ShotType.BEAST_SOULSHOTS);
        this.disableAutoShot(ShotType.BEAST_SPIRITSHOTS);
    }
    
    public boolean isAutoShotEnabled(final ShotType type) {
        return this.activeSoulShots.containsKey(type);
    }
    
    public void disableAutoShot(final ShotType type) {
        Util.doIfNonNull((Object)this.activeSoulShots.remove(type), itemId -> this.sendDisableShotPackets(type, itemId));
        switch (type) {
            case SOULSHOTS:
            case SPIRITSHOTS: {
                this.unchargeShot(type);
                break;
            }
            case BEAST_SOULSHOTS:
            case BEAST_SPIRITSHOTS: {
                final ShotType shotType = (type == ShotType.BEAST_SOULSHOTS) ? ShotType.SOULSHOTS : ShotType.SPIRITSHOTS;
                Util.doIfNonNull((Object)this.getPet(), pet -> pet.unchargeShot(shotType));
                this.getServitors().values().forEach(s -> s.unchargeShot(shotType));
                break;
            }
        }
    }
    
    public void disableAutoShots() {
        this.activeSoulShots.forEach(this::sendDisableShotPackets);
        this.activeSoulShots.clear();
        this.unchargeAllShots();
        Util.doIfNonNull((Object)this.getPet(), Creature::unchargeAllShots);
        this.getServitors().values().forEach(Creature::unchargeAllShots);
    }
    
    private void sendDisableShotPackets(final ShotType type, final int itemId) {
        this.sendPacket(new ExAutoSoulShot(itemId, false, type.getClientType()), ((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED)).addItemName(itemId));
    }
    
    @Override
    public void consumeAndRechargeShots(final ShotType type, final int targets) {
        if (!this.activeSoulShots.containsKey(type) || Objects.isNull(this.getActiveWeaponInstance())) {
            return;
        }
        int shotsCount = this.getActiveWeaponItem().getConsumeShotsCount();
        if (targets >= 4 && targets <= 8) {
            shotsCount <<= 1;
        }
        else if (targets >= 9 && targets <= 14) {
            shotsCount *= 3;
        }
        else if (targets >= 15) {
            shotsCount <<= 2;
        }
        if (!this.consumeAndRechargeShotCount(type, shotsCount)) {
            this.unchargeShot(type);
        }
    }
    
    public boolean consumeAndRechargeShotCount(final ShotType type, final int count) {
        if (count < 1) {
            return this.rechargeShot(type);
        }
        final Integer itemId = this.activeSoulShots.get(type);
        final Item item;
        if (!Objects.nonNull(itemId) || !Objects.nonNull(item = this.inventory.getItemByItemId(itemId))) {
            return false;
        }
        final long consume = Math.min(count, item.getCount());
        this.destroyItemWithoutTrace("Consume", item.getObjectId(), consume, this, false);
        if (consume < count) {
            this.disableAutoShot(type);
            this.sendNotEnoughShotMessage(type);
            return false;
        }
        return this.rechargeShot(type);
    }
    
    private void sendNotEnoughShotMessage(final ShotType type) {
        SystemMessageId systemMessageId = null;
        switch (type) {
            case SPIRITSHOTS: {
                systemMessageId = SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT;
                break;
            }
            case SOULSHOTS: {
                systemMessageId = SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT;
                break;
            }
            case BEAST_SOULSHOTS: {
                systemMessageId = SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR;
                break;
            }
            case BEAST_SPIRITSHOTS: {
                systemMessageId = SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
                break;
            }
            default: {
                throw new IncompatibleClassChangeError();
            }
        }
        final SystemMessageId message = systemMessageId;
        this.sendPacket(message);
    }
    
    public void setAdditionalSoulshot(final int jewel) {
        this.additionalSoulshot = jewel;
    }
    
    public int getAdditionalSoulshot() {
        return this.additionalSoulshot;
    }
    
    public void setShineSouls(final byte souls) {
        this.shineSouls = souls;
    }
    
    public byte getShineSouls() {
        return this.shineSouls;
    }
    
    public void setShadowSouls(final byte shadowSouls) {
        this.shadowSouls = shadowSouls;
    }
    
    public byte getShadowSouls() {
        return this.shadowSouls;
    }
    
    public void setCostumes(final IntMap<CostumeData> costumes) {
        this.costumes = costumes;
    }
    
    public void setActiveCostumeCollection(final CostumeCollectionData collection) {
        this.activeCostumesCollection = collection;
    }
    
    public CostumeData addCostume(final int costumeId) {
        if (this.costumes.equals((Object)Containers.emptyIntMap())) {
            this.costumes = (IntMap<CostumeData>)new HashIntMap();
        }
        final CostumeData costume = (CostumeData)this.costumes.computeIfAbsent(costumeId, id -> CostumeData.of(id, this));
        costume.increaseAmount();
        return costume;
    }
    
    public void forEachCostume(final Consumer<CostumeData> action) {
        this.costumes.values().forEach(action);
    }
    
    public int getCostumeAmount() {
        return this.costumes.size();
    }
    
    public CostumeData getCostume(final int id) {
        return (CostumeData)this.costumes.get(id);
    }
    
    public void removeCostume(final int id) {
        this.costumes.remove(id);
        ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).removeCostume(this.objectId, id);
    }
    
    public boolean setActiveCostumesCollection(final int collectionId) {
        final CostumeCollectionData collection = (CostumeCollectionData)this.costumesCollections.get(collectionId);
        if (Objects.nonNull(collection)) {
            (this.activeCostumesCollection = collection).updateReuseTime();
            return true;
        }
        return false;
    }
    
    public CostumeCollectionData getActiveCostumeCollection() {
        return this.activeCostumesCollection;
    }
    
    public void addCostumeCollection(final int collectionId) {
        if (this.costumesCollections.equals((Object)Containers.emptyIntMap())) {
            this.costumesCollections = (IntMap<CostumeCollectionData>)new HashIntMap();
        }
        this.costumesCollections.computeIfAbsent(collectionId, id -> CostumeCollectionData.of(this, id));
    }
    
    public void removeCostumeCollection(final int collectionId) {
        if (this.costumesCollections.isEmpty()) {
            return;
        }
        final CostumeCollectionData collection = (CostumeCollectionData)this.costumesCollections.remove(collectionId);
        if (this.activeCostumesCollection.equals(collection)) {
            this.activeCostumesCollection = CostumeCollectionData.DEFAULT;
        }
    }
    
    public int getCostumeCollectionAmount() {
        return this.costumesCollections.size();
    }
    
    public void addTeleportFavorite(final int teleportId) {
        this.teleportFavorites.add(teleportId);
    }
    
    public void removeTeleportFavorite(final int teleportId) {
        this.teleportFavorites.remove(teleportId);
    }
    
    public IntSet getTeleportFavorites() {
        return this.teleportFavorites;
    }
    
    public Collection<Item> getDepositableItems(final WarehouseType type) {
        return this.inventory.getDepositableItems(type);
    }
    
    @Override
    protected void onReceiveDamage(final Creature attacker, final Skill skill, final double value, final DamageInfo.DamageType damageType) {
        if (Objects.nonNull(this.tamedBeast)) {
            for (final TamedBeast tamedBeast : this.tamedBeast) {
                tamedBeast.onOwnerGotAttacked(attacker);
            }
        }
        this.lastDamages.add((Object)DamageInfo.of((attacker == this) ? null : attacker, skill, value, damageType));
        super.onReceiveDamage(attacker, skill, value, damageType);
    }
    
    @Override
    protected boolean checkRangedAttackCondition(final Weapon weapon, final Creature target) {
        if (!super.checkRangedAttackCondition(weapon, target)) {
            ThreadPool.schedule((Runnable)new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), 1000L);
            return false;
        }
        if (!this.inventory.findAmmunitionForCurrentWeapon()) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            this.sendPacket(SystemMessageId.YOU_HAVE_RUN_OUT_OF_ARROWS);
            return false;
        }
        if (target.isInsidePeaceZone(this)) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            this.sendPacket(SystemMessageId.YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE);
            return false;
        }
        final int mpConsume = this.isAffected(EffectFlag.CHEAPSHOT) ? 0 : weapon.getMpConsume();
        if (this.getCurrentMp() < mpConsume) {
            ThreadPool.schedule((Runnable)new NotifyAITask(this, CtrlEvent.EVT_READY_TO_ACT), 1000L);
            this.sendPacket(SystemMessageId.NOT_ENOUGH_MP);
            return false;
        }
        if (mpConsume > 0) {
            this.reduceCurrentMp(mpConsume);
        }
        return true;
    }
    
    @Override
    protected void onStartRangedAttack(final boolean isCrossBow, final int reuse) {
        this.inventory.reduceAmmunitionCount();
        if (isCrossBow) {
            this.sendPacket(SystemMessageId.YOUR_CROSSBOW_IS_PREPARING_TO_FIRE);
        }
        this.sendPacket(new SetupGauge(this.getObjectId(), 1, reuse));
        super.onStartRangedAttack(isCrossBow, reuse);
    }
    
    void setVariables(final PlayerVariableData variables) {
        this.variables = variables;
    }
    
    void setStatsData(final PlayerStatsData statsData) {
        this.statsData = statsData;
    }
    
    void setTeleportFavorites(final IntSet teleports) {
        this.teleportFavorites = teleports;
    }
    
    public static Player create(final PlayerData playerData, final PlayerTemplate template) {
        final Player player = new Player(playerData, template);
        player.setRecomLeft(20);
        if (player.createDb()) {
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
                PlayerNameTable.getInstance().addName(player);
            }
            player.variables = PlayerVariableData.init(player.getObjectId());
            ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).save((Object)player.variables);
            player.statsData = PlayerStatsData.init(player.getObjectId());
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).save(player.statsData);
            return player;
        }
        return null;
    }
    
    @Override
    protected void initCharStatusUpdateValues() {
        super.initCharStatusUpdateValues();
        this._cpUpdateInterval = this.getMaxCp() / 352.0;
        this._cpUpdateIncCheck = this.getMaxCp();
        this._cpUpdateDecCheck = this.getMaxCp() - this._cpUpdateInterval;
        this._mpUpdateInterval = this.getMaxMp() / 352.0;
        this._mpUpdateIncCheck = this.getMaxMp();
        this._mpUpdateDecCheck = this.getMaxMp() - this._mpUpdateInterval;
    }
    
    public long getPvpFlagLasts() {
        return this._pvpFlagLasts;
    }
    
    public void setPvpFlagLasts(final long time) {
        this._pvpFlagLasts = time;
    }
    
    public void startPvPFlag() {
        this.updatePvPFlag(1);
        if (this._PvPRegTask == null) {
            this._PvPRegTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new PvPFlagTask(this), 1000L, 1000L);
        }
    }
    
    public void stopPvpRegTask() {
        if (this._PvPRegTask != null) {
            this._PvPRegTask.cancel(true);
            this._PvPRegTask = null;
        }
    }
    
    public void stopPvPFlag() {
        this.stopPvpRegTask();
        this.updatePvPFlag(0);
        this._PvPRegTask = null;
    }
    
    public boolean isSellingBuffs() {
        return this._isSellingBuffs;
    }
    
    public void setIsSellingBuffs(final boolean val) {
        this._isSellingBuffs = val;
    }
    
    public List<SellBuffHolder> getSellingBuffs() {
        if (this._sellingBuffs == null) {
            this._sellingBuffs = new ArrayList<SellBuffHolder>();
        }
        return this._sellingBuffs;
    }
    
    public String getAccountName() {
        return (this._client == null) ? this.data.getAccountName() : this._client.getAccountName();
    }
    
    public String getAccountNamePlayer() {
        return this.data.getAccountName();
    }
    
    public IntMap<String> getAccountChars() {
        return this.accountPlayers;
    }
    
    public int getRelation(final Player target) {
        final Clan clan = this.getClan();
        final Party party = this.getParty();
        final Clan targetClan = target.getClan();
        int result = 0;
        if (clan != null) {
            result |= 0x40;
            if (clan == target.getClan()) {
                result |= 0x100;
            }
            if (this.getAllyId() != 0) {
                result |= 0x10000;
            }
        }
        if (this.isClanLeader()) {
            result |= 0x80;
        }
        if (party != null && party == target.getParty()) {
            result |= 0x20;
            for (int i = 0; i < party.getMembers().size(); ++i) {
                if (party.getMembers().get(i) == this) {
                    switch (i) {
                        case 0: {
                            result |= 0x10;
                            break;
                        }
                        case 1: {
                            result |= 0x8;
                            break;
                        }
                        case 2: {
                            result |= 0x7;
                            break;
                        }
                        case 3: {
                            result |= 0x6;
                            break;
                        }
                        case 4: {
                            result |= 0x5;
                            break;
                        }
                        case 5: {
                            result |= 0x4;
                            break;
                        }
                        case 6: {
                            result |= 0x3;
                            break;
                        }
                        case 7: {
                            result |= 0x2;
                            break;
                        }
                        case 8: {
                            result |= 0x1;
                            break;
                        }
                    }
                }
            }
        }
        if (this._siegeState != 0) {
            result |= 0x200;
            if (this.getSiegeState() != target.getSiegeState()) {
                result |= 0x1000;
            }
            else {
                result |= 0x800;
            }
            if (this._siegeState == 1) {
                result |= 0x400;
            }
        }
        if (clan != null && targetClan != null && target.getPledgeType() != -1 && this.getPledgeType() != -1) {
            final ClanWar war = clan.getWarWith(target.getClan().getId());
            if (war != null) {
                switch (war.getState()) {
                    case DECLARATION:
                    case BLOOD_DECLARATION: {
                        result |= 0x4000;
                        break;
                    }
                    case MUTUAL: {
                        result |= 0x4000;
                        result |= 0x8000;
                        break;
                    }
                }
            }
        }
        if (this._handysBlockCheckerEventArena != -1) {
            result |= 0x200;
            final ArenaParticipantsHolder holder = HandysBlockCheckerManager.getInstance().getHolder(this.getBlockCheckerArena());
            if (holder.getPlayerTeam(this) == 0) {
                result |= 0x1000;
            }
            else {
                result |= 0x800;
            }
            result |= 0x400;
        }
        return result;
    }
    
    @Override
    public final PlayerStats getStats() {
        return (PlayerStats)super.getStats();
    }
    
    @Override
    public void initCharStat() {
        this.setStat(new PlayerStats(this));
    }
    
    @Override
    public final PlayerStatus getStatus() {
        return (PlayerStatus)super.getStatus();
    }
    
    @Override
    public void initCharStatus() {
        this.setStatus(new PlayerStatus(this));
    }
    
    public final PlayerAppearance getAppearance() {
        return this.appearance;
    }
    
    public final PlayerTemplate getBaseTemplate() {
        return PlayerTemplateData.getInstance().getTemplate(this.data.getBaseClass());
    }
    
    @Override
    public final PlayerTemplate getTemplate() {
        return (PlayerTemplate)super.getTemplate();
    }
    
    public void setTemplate(final ClassId newclass) {
        super.setTemplate(PlayerTemplateData.getInstance().getTemplate(newclass));
    }
    
    @Override
    protected CreatureAI initAI() {
        return new PlayerAI(this);
    }
    
    @Override
    public final int getLevel() {
        return this.getStats().getLevel();
    }
    
    public boolean isInStoreMode() {
        return this.privateStoreType != PrivateStoreType.NONE;
    }
    
    public boolean isCrafting() {
        return this._isCrafting;
    }
    
    public void setIsCrafting(final boolean isCrafting) {
        this._isCrafting = isCrafting;
    }
    
    public RecipeList[] getCommonRecipeBook() {
        return this._commonRecipeBook.values().toArray(RecipeList[]::new);
    }
    
    public RecipeList[] getDwarvenRecipeBook() {
        return this._dwarvenRecipeBook.values().toArray(RecipeList[]::new);
    }
    
    public void registerCommonRecipeList(final RecipeList recipe, final boolean saveToDb) {
        this._commonRecipeBook.put(recipe.getId(), recipe);
        if (saveToDb) {
            this.insertNewRecipeData(recipe.getId(), false);
        }
    }
    
    public void registerDwarvenRecipeList(final RecipeList recipe, final boolean saveToDb) {
        this._dwarvenRecipeBook.put(recipe.getId(), recipe);
        if (saveToDb) {
            this.insertNewRecipeData(recipe.getId(), true);
        }
    }
    
    public boolean hasRecipeList(final int recipeId) {
        return this._dwarvenRecipeBook.containsKey(recipeId) || this._commonRecipeBook.containsKey(recipeId);
    }
    
    public void unregisterRecipeList(final int recipeId) {
        if (this._dwarvenRecipeBook.remove(recipeId) != null) {
            this.deleteRecipeData(recipeId, true);
        }
        else if (this._commonRecipeBook.remove(recipeId) != null) {
            this.deleteRecipeData(recipeId, false);
        }
        else {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, recipeId));
        }
        this.shortcuts.deleteShortcuts(s -> s.getShortcutId() == recipeId && s.getType() == ShortcutType.RECIPE);
    }
    
    private void insertNewRecipeData(final int recipeId, final boolean isDwarf) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO character_recipebook (charId, id, classIndex, type) values(?,?,?,?)");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, recipeId);
                    statement.setInt(3, isDwarf ? this._classIndex : 0);
                    statement.setInt(4, isDwarf ? 1 : 0);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, recipeId, this.getObjectId()), (Throwable)e);
        }
    }
    
    private void deleteRecipeData(final int recipeId, final boolean isDwarf) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_recipebook WHERE charId=? AND id=? AND classIndex=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, recipeId);
                    statement.setInt(3, isDwarf ? this._classIndex : 0);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(II)Ljava/lang/String;, recipeId, this.getObjectId()), (Throwable)e);
        }
    }
    
    public int getLastQuestNpcObject() {
        return this._questNpcObject;
    }
    
    public void setLastQuestNpcObject(final int npcId) {
        this._questNpcObject = npcId;
    }
    
    public boolean isSimulatingTalking() {
        return this._simulatedTalking;
    }
    
    public void setSimulatedTalking(final boolean value) {
        this._simulatedTalking = value;
    }
    
    public QuestState getQuestState(final String quest) {
        return this._quests.get(quest);
    }
    
    public void setQuestState(final QuestState qs) {
        this._quests.put(qs.getQuestName(), qs);
    }
    
    public boolean hasQuestState(final String quest) {
        return this._quests.containsKey(quest);
    }
    
    public void delQuestState(final String quest) {
        this._quests.remove(quest);
    }
    
    public Collection<QuestState> getAllQuestStates() {
        return this._quests.values();
    }
    
    public List<Quest> getAllActiveQuests() {
        return this._quests.values().stream().filter(QuestState::isStarted).map((Function<? super QuestState, ?>)QuestState::getQuest).filter(Objects::nonNull).filter(q -> q.getId() > 1).collect((Collector<? super Object, ?, List<Quest>>)Collectors.toList());
    }
    
    public void processQuestEvent(final String questName, final String event) {
        final Quest quest = QuestManager.getInstance().getQuest(questName);
        if (quest == null || event == null || event.isEmpty()) {
            return;
        }
        final Npc target = this._lastFolkNpc;
        if (target != null && MathUtil.isInsideRadius2D(this, target, 250)) {
            quest.notifyEvent(event, target, this);
        }
        else if (this._questNpcObject > 0) {
            final WorldObject object = World.getInstance().findObject(this.getLastQuestNpcObject());
            if (GameUtils.isNpc(object) && MathUtil.isInsideRadius2D(this, object, 250)) {
                final Npc npc = (Npc)object;
                quest.notifyEvent(event, npc, this);
            }
        }
    }
    
    public void addNotifyQuestOfDeath(final QuestState qs) {
        if (qs == null) {
            return;
        }
        if (!this.getNotifyQuestOfDeath().contains(qs)) {
            this.getNotifyQuestOfDeath().add(qs);
        }
    }
    
    public void removeNotifyQuestOfDeath(final QuestState qs) {
        if (qs == null || this._notifyQuestOfDeathList == null) {
            return;
        }
        this._notifyQuestOfDeathList.remove(qs);
    }
    
    public final Set<QuestState> getNotifyQuestOfDeath() {
        if (this._notifyQuestOfDeathList == null) {
            synchronized (this) {
                if (this._notifyQuestOfDeathList == null) {
                    this._notifyQuestOfDeathList = (Set<QuestState>)ConcurrentHashMap.newKeySet();
                }
            }
        }
        return this._notifyQuestOfDeathList;
    }
    
    public final boolean isNotifyQuestOfDeathEmpty() {
        return this._notifyQuestOfDeathList == null || this._notifyQuestOfDeathList.isEmpty();
    }
    
    public Shortcut getShortcut(final int room) {
        return this.shortcuts.getShortcut(room);
    }
    
    public void registerShortCut(final Shortcut shortcut) {
        this.shortcuts.registerShortCut(shortcut);
        this.sendPacket(new ShortCutRegister(shortcut));
    }
    
    public void updateShortCuts(final int skillId, final int skillLevel, final int skillSubLevel) {
        this.shortcuts.updateShortCuts(skillId, skillLevel, skillSubLevel);
    }
    
    public void deleteShortcut(final int room) {
        this.shortcuts.deleteShortcut(room);
    }
    
    public void registerMacro(final Macro macro) {
        this.macros.registerMacro(macro);
    }
    
    public void deleteMacro(final int id) {
        this.macros.deleteMacro(id);
    }
    
    public MacroList getMacros() {
        return this.macros;
    }
    
    public byte getSiegeState() {
        return this._siegeState;
    }
    
    public void setSiegeState(final byte siegeState) {
        this._siegeState = siegeState;
    }
    
    public boolean isRegisteredOnThisSiegeField(final int val) {
        return this._siegeSide == val || (this._siegeSide >= 81 && this._siegeSide <= 89);
    }
    
    public int getSiegeSide() {
        return this._siegeSide;
    }
    
    public void setSiegeSide(final int val) {
        this._siegeSide = val;
    }
    
    @Override
    public byte getPvpFlag() {
        return this._pvpFlag;
    }
    
    public void setPvpFlag(final int pvpFlag) {
        this._pvpFlag = (byte)pvpFlag;
    }
    
    @Override
    public void updatePvPFlag(final int value) {
        if (this._pvpFlag == value) {
            return;
        }
        this.setPvpFlag(value);
        final StatusUpdate su = new StatusUpdate(this);
        this.computeStatusUpdate(su, StatusUpdateType.PVP_FLAG);
        if (su.hasUpdates()) {
            this.broadcastPacket(su);
            this.sendPacket(su);
        }
        if (this.hasSummon()) {
            final RelationChanged rc = new RelationChanged();
            final Summon pet = this.pet;
            if (pet != null) {
                rc.addRelation(pet, this.getRelation(this), false);
            }
            if (this.hasServitors()) {
                this.getServitors().values().forEach(s -> rc.addRelation(s, this.getRelation(this), false));
            }
            this.sendPacket(rc);
        }
        int relation;
        Integer oldrelation;
        RelationChanged rc2;
        Summon pet2;
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (!(!this.isVisibleFor(player))) {
                relation = this.getRelation(player);
                oldrelation = this.getKnownRelations().get(player.getObjectId());
                if (oldrelation == null || oldrelation != relation) {
                    rc2 = new RelationChanged();
                    rc2.addRelation(this, relation, this.isAutoAttackable(player));
                    if (this.hasSummon()) {
                        pet2 = this.pet;
                        if (pet2 != null) {
                            rc2.addRelation(pet2, relation, this.isAutoAttackable(player));
                        }
                        if (this.hasServitors()) {
                            this.getServitors().values().forEach(s -> rc2.addRelation(s, relation, this.isAutoAttackable(player)));
                        }
                    }
                    player.sendPacket(rc2);
                    this.getKnownRelations().put(player.getObjectId(), relation);
                }
            }
        });
    }
    
    @Override
    public void revalidateZone(final boolean force) {
        if (this.getWorldRegion() == null) {
            return;
        }
        if (force) {
            this._zoneValidateCounter = 4;
        }
        else {
            --this._zoneValidateCounter;
            if (this._zoneValidateCounter >= 0) {
                return;
            }
            this._zoneValidateCounter = 4;
        }
        ZoneManager.getInstance().getRegion(this).revalidateZones(this);
        if (Config.ALLOW_WATER) {
            this.checkWaterState();
        }
        if (this.isInsideZone(ZoneType.ALTERED)) {
            if (this._lastCompassZone == 8) {
                return;
            }
            this._lastCompassZone = 8;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(8);
            this.sendPacket(cz);
        }
        else if (this.isInsideZone(ZoneType.SIEGE)) {
            if (this._lastCompassZone == 11) {
                return;
            }
            this._lastCompassZone = 11;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(11);
            this.sendPacket(cz);
        }
        else if (this.isInsideZone(ZoneType.PVP)) {
            if (this._lastCompassZone == 14) {
                return;
            }
            this._lastCompassZone = 14;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(14);
            this.sendPacket(cz);
        }
        else if (this.isInsideZone(ZoneType.PEACE)) {
            if (this._lastCompassZone == 12) {
                return;
            }
            this._lastCompassZone = 12;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(12);
            this.sendPacket(cz);
        }
        else {
            if (this._lastCompassZone == 15) {
                return;
            }
            if (this._lastCompassZone == 11) {
                this.updatePvPStatus();
            }
            this._lastCompassZone = 15;
            final ExSetCompassZoneCode cz = new ExSetCompassZoneCode(15);
            this.sendPacket(cz);
        }
    }
    
    public boolean hasDwarvenCraft() {
        return this.getSkillLevel(CommonSkill.CREATE_DWARVEN.getId()) >= 1;
    }
    
    public int getDwarvenCraft() {
        return this.getSkillLevel(CommonSkill.CREATE_DWARVEN.getId());
    }
    
    public boolean hasCommonCraft() {
        return this.getSkillLevel(CommonSkill.CREATE_COMMON.getId()) >= 1;
    }
    
    public int getCommonCraft() {
        return this.getSkillLevel(CommonSkill.CREATE_COMMON.getId());
    }
    
    public int getPkKills() {
        return this._pkKills;
    }
    
    public void setPkKills(final int pkKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPKChanged(this, this._pkKills, pkKills), this);
        this._pkKills = pkKills;
    }
    
    public int getRecomHave() {
        return this._recomHave;
    }
    
    public void setRecomHave(final int value) {
        this._recomHave = Math.min(Math.max(value, 0), 255);
    }
    
    protected void incRecomHave() {
        if (this._recomHave < 255) {
            ++this._recomHave;
        }
    }
    
    public int getRecomLeft() {
        return this._recomLeft;
    }
    
    public void setRecomLeft(final int value) {
        this._recomLeft = Math.min(Math.max(value, 0), 255);
    }
    
    protected void decRecomLeft() {
        if (this._recomLeft > 0) {
            --this._recomLeft;
        }
    }
    
    public void giveRecom(final Player target) {
        target.incRecomHave();
        this.decRecomLeft();
    }
    
    public void setInitialReputation(final int reputation) {
        super.setReputation(reputation);
    }
    
    @Override
    public void setReputation(int reputation) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerReputationChanged(this, this.getReputation(), reputation), this);
        if (reputation > Config.MAX_REPUTATION) {
            reputation = Config.MAX_REPUTATION;
        }
        if (this.getReputation() == reputation) {
            return;
        }
        if (this.getReputation() >= 0 && reputation < 0) {
            World.getInstance().forEachVisibleObject(this, Guard.class, object -> {
                if (object.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE) {
                    object.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                }
                return;
            });
        }
        super.setReputation(reputation);
        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1)).addInt(this.getReputation()));
        this.broadcastReputation();
    }
    
    public int getWeightPenalty() {
        if (this._dietMode) {
            return 0;
        }
        return this._curWeightPenalty;
    }
    
    public void refreshOverloaded(final boolean broadcast) {
        final int maxLoad = this.getMaxLoad();
        if (maxLoad > 0) {
            final long weightproc = (this.getCurrentLoad() - this.getBonusWeightPenalty()) * 1000 / this.getMaxLoad();
            int newWeightPenalty;
            if (weightproc < 500L || this._dietMode) {
                newWeightPenalty = 0;
            }
            else if (weightproc < 666L) {
                newWeightPenalty = 1;
            }
            else if (weightproc < 800L) {
                newWeightPenalty = 2;
            }
            else if (weightproc < 1000L) {
                newWeightPenalty = 3;
            }
            else {
                newWeightPenalty = 4;
            }
            if (this._curWeightPenalty != newWeightPenalty) {
                this._curWeightPenalty = newWeightPenalty;
                if (newWeightPenalty > 0 && !this._dietMode) {
                    this.addSkill(SkillEngine.getInstance().getSkill(CommonSkill.WEIGHT_PENALTY.getId(), newWeightPenalty));
                    this.setIsOverloaded(this.getCurrentLoad() > maxLoad);
                }
                else {
                    this.removeSkill(this.getKnownSkill(4270), false, true);
                    this.setIsOverloaded(false);
                }
                if (broadcast) {
                    this.sendPacket(new EtcStatusUpdate(this));
                    this.broadcastUserInfo();
                }
            }
        }
    }
    
    public void useEquippableItem(final Item item, final boolean abortAttack) {
        final int oldInvLimit = this.getInventoryLimit();
        Set<Item> modifiedItems;
        if (item.isEquipped()) {
            final BodyPart bodyPart = BodyPart.fromEquippedPaperdoll(item);
            if (bodyPart.isAnyOf(BodyPart.TALISMAN, BodyPart.BROOCH_JEWEL, BodyPart.AGATHION, BodyPart.ARTIFACT)) {
                modifiedItems = this.inventory.unEquipItemInSlotAndRecord(InventorySlot.fromId(item.getLocationSlot()));
            }
            else {
                modifiedItems = this.inventory.unEquipItemInBodySlotAndRecord(bodyPart);
            }
            SystemMessage sm;
            if (item.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(item.getEnchantLevel()).addItemName(item);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(item);
            }
            this.sendPacket(sm);
        }
        else {
            modifiedItems = this.inventory.equipItemAndRecord(item);
            if (item.isEquipped()) {
                SystemMessage sm;
                if (item.getEnchantLevel() > 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.EQUIPPED_S1_S2).addInt(item.getEnchantLevel());
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EQUIPPED_YOUR_S1);
                }
                sm.addItemName(item);
                this.sendPacket(sm);
                if (item.getBodyPart().isAnyOf(BodyPart.RIGHT_HAND, BodyPart.TWO_HAND)) {
                    this.rechargeShot(ShotType.SOULSHOTS);
                    this.rechargeShot(ShotType.SPIRITSHOTS);
                }
            }
            else {
                this.sendPacket(SystemMessageId.YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM);
            }
        }
        this.broadcastUserInfo();
        this.sendInventoryUpdate(new InventoryUpdate(modifiedItems));
        if (abortAttack) {
            this.abortAttack();
        }
        if (this.getInventoryLimit() != oldInvLimit) {
            this.sendPacket(new ExStorageMaxCount(this));
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerEquipItem(this, item), this);
    }
    
    public int getPvpKills() {
        return this._pvpKills;
    }
    
    public void setPvpKills(final int pvpKills) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPChanged(this, this._pvpKills, pvpKills), this);
        this._pvpKills = pvpKills;
    }
    
    public int getFame() {
        return this._fame;
    }
    
    public void setFame(final int fame) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerFameChanged(this, this._fame, fame), this);
        this._fame = Math.min(fame, Config.MAX_PERSONAL_FAME_POINTS);
    }
    
    public int getRaidbossPoints() {
        return this.data.getRaidBossPoints();
    }
    
    public void setRaidbossPoints(final int points) {
        this.data.setRaidbossPoints(points);
    }
    
    public void increaseRaidbossPoints(final int increasePoints) {
        this.setRaidbossPoints(this.data.getRaidBossPoints() + increasePoints);
    }
    
    public ClassId getClassId() {
        return this.getTemplate().getClassId();
    }
    
    public void setClassId(final int Id) {
        if (!this._subclassLock.tryLock()) {
            return;
        }
        try {
            if (this.getLvlJoinedAcademy() != 0 && this._clan != null && CategoryManager.getInstance().isInCategory(CategoryType.THIRD_CLASS_GROUP, Id)) {
                if (this.getLvlJoinedAcademy() <= 16) {
                    this._clan.addReputationScore(Config.JOIN_ACADEMY_MAX_REP_SCORE, true);
                }
                else if (this.getLvlJoinedAcademy() >= 39) {
                    this._clan.addReputationScore(Config.JOIN_ACADEMY_MIN_REP_SCORE, true);
                }
                else {
                    this._clan.addReputationScore(Config.JOIN_ACADEMY_MAX_REP_SCORE - (this.getLvlJoinedAcademy() - 16) * 20, true);
                }
                this.setLvlJoinedAcademy(0);
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.CLAN_MEMBER_S1_HAS_BEEN_EXPELLED);
                msg.addPcName(this);
                this._clan.broadcastToOnlineMembers(msg);
                this._clan.broadcastToOnlineMembers(new PledgeShowMemberListDelete(this.getName()));
                this._clan.removeClanMember(this.getObjectId(), 0L);
                this.sendPacket(SystemMessageId.CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES);
                this.inventory.addItem("Gift", 8181, 1L, this, null);
            }
            if (this.isSubClassActive()) {
                ((SubClass)this.getSubClasses().get(this._classIndex)).setClassId(Id);
            }
            this.setTarget(this);
            this.broadcastPacket(new MagicSkillUse(this, 5103, 1, 1000, 0));
            this.setClassTemplate(Id);
            if (this.getClassId().level() == 3) {
                this.sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_YOUR_THIRD_CLASS_TRANSFER_QUEST);
            }
            else {
                this.sendPacket(SystemMessageId.CONGRATULATIONS_YOU_VE_COMPLETED_A_CLASS_TRANSFER);
            }
            for (int slot = 1; slot < 4; ++slot) {
                final Henna henna = this.getHenna(slot);
                if (henna != null && !henna.isAllowedClass(this.getClassId())) {
                    this.removeHenna(slot);
                }
            }
            if (this.isInParty()) {
                this._party.broadcastPacket(new PartySmallWindowUpdate(this, true));
            }
            if (this._clan != null) {
                this._clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdate(this));
            }
            this.sendPacket(new ExSubjobInfo(this, SubclassInfoType.CLASS_CHANGED));
            this.rewardSkills();
            if (!this.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
                this.checkPlayerSkills();
            }
            this.notifyFriends(3);
        }
        finally {
            this._subclassLock.unlock();
        }
    }
    
    public long getExp() {
        return this.getStats().getExp();
    }
    
    public void setExp(long exp) {
        if (exp < 0L) {
            exp = 0L;
        }
        this.getStats().setExp(exp);
    }
    
    public Weapon getFistsWeaponItem() {
        return this._fistsWeaponItem;
    }
    
    public void setFistsWeaponItem(final Weapon weaponItem) {
        this._fistsWeaponItem = weaponItem;
    }
    
    public Weapon findFistsWeaponItem() {
        final ClassId classId = this.getClassId();
        int n = 0;
        switch (classId.getRace()) {
            case HUMAN: {
                n = (classId.isMage() ? 251 : 246);
                break;
            }
            case ELF: {
                n = (classId.isMage() ? 249 : 244);
                break;
            }
            case DARK_ELF: {
                n = (classId.isMage() ? 250 : 245);
                break;
            }
            case ORC: {
                n = (classId.isMage() ? 252 : 248);
                break;
            }
            case DWARF: {
                n = 247;
                break;
            }
            default: {
                n = 246;
                break;
            }
        }
        final int fistWeaponId = n;
        return (Weapon)ItemEngine.getInstance().getTemplate(fistWeaponId);
    }
    
    public void rewardSkills() {
        if (Config.AUTO_LEARN_SKILLS) {
            this.giveAvailableSkills(Config.AUTO_LEARN_FS_SKILLS, true);
        }
        else {
            this.giveAvailableAutoGetSkills();
        }
        if (!this.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            this.checkPlayerSkills();
        }
        this.checkItemRestriction();
        this.sendSkillList();
    }
    
    public void regiveTemporarySkills() {
        if (this.isNoble()) {
            this.setNoble(true);
        }
        if (this._hero) {
            this.setHero(true);
        }
        if (this._clan != null) {
            this._clan.addSkillEffects(this);
            if (this._clan.getLevel() >= SiegeManager.getInstance().getSiegeClanMinLevel() && this.isClanLeader()) {
                SiegeManager.getInstance().addSiegeSkills(this);
            }
            if (this._clan.getCastleId() > 0) {
                CastleManager.getInstance().getCastleByOwner(this.getClan()).giveResidentialSkills(this);
            }
        }
        this.inventory.reloadEquippedItems();
    }
    
    public int giveAvailableSkills(final boolean includedByFs, final boolean includeAutoGet) {
        int skillCounter = 0;
        final Collection<Skill> skills = SkillTreesData.getInstance().getAllAvailableSkills(this, this.getTemplate().getClassId(), includedByFs, includeAutoGet);
        final List<Skill> skillsForStore = new ArrayList<Skill>();
        for (Skill skill : skills) {
            final Skill oldSkill = this.getKnownSkill(skill.getId());
            if (oldSkill == skill) {
                continue;
            }
            if (this.getSkillLevel(skill.getId()) == 0) {
                ++skillCounter;
            }
            if (skill.isToggle() && this.isAffectedBySkill(skill.getId())) {
                this.stopSkillEffects(true, skill.getId());
            }
            if (oldSkill != null && oldSkill.getSubLevel() > 0 && skill.getSubLevel() == 0 && oldSkill.getLevel() < skill.getLevel()) {
                skill = SkillEngine.getInstance().getSkill(skill.getId(), skill.getLevel());
            }
            this.addSkill(skill, false);
            skillsForStore.add(skill);
        }
        this.storeSkills(skillsForStore, -1);
        if (Config.AUTO_LEARN_SKILLS && skillCounter > 0) {
            this.sendMessage(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, skillCounter));
        }
        return skillCounter;
    }
    
    public void giveAvailableAutoGetSkills() {
        final List<SkillLearn> autoGetSkills = SkillTreesData.getInstance().getAvailableAutoGetSkills(this);
        final SkillEngine st = SkillEngine.getInstance();
        for (final SkillLearn s : autoGetSkills) {
            final Skill skill = st.getSkill(s.getSkillId(), s.getSkillLevel());
            if (skill != null) {
                this.addSkill(skill, true);
            }
            else {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this.toString()));
            }
        }
    }
    
    @Override
    public Race getRace() {
        if (!this.isSubClassActive()) {
            return this.getTemplate().getRace();
        }
        return PlayerTemplateData.getInstance().getTemplate(this.data.getBaseClass()).getRace();
    }
    
    public Radar getRadar() {
        return this.radar;
    }
    
    public long getSp() {
        return this.getStats().getSp();
    }
    
    public void setSp(long sp) {
        if (sp < 0L) {
            sp = 0L;
        }
        super.getStats().setSp(sp);
    }
    
    public boolean isCastleLord(final int castleId) {
        if (this._clan != null && this._clan.getLeader().getPlayerInstance() == this) {
            final Castle castle = CastleManager.getInstance().getCastleByOwner(this._clan);
            if (castle != null && castle == CastleManager.getInstance().getCastleById(castleId)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int getClanId() {
        return this.clanId;
    }
    
    public int getClanCrestId() {
        if (this._clan != null) {
            return this._clan.getCrestId();
        }
        return 0;
    }
    
    public int getClanCrestLargeId() {
        if (this._clan != null && (this._clan.getCastleId() != 0 || this._clan.getHideoutId() != 0)) {
            return this._clan.getCrestLargeId();
        }
        return 0;
    }
    
    public long getClanJoinExpiryTime() {
        return this.data.getClanJoinExpiryTime();
    }
    
    public void setClanJoinExpiryTime(final long time) {
        this.data.setClanJoinExpiryTime(time);
    }
    
    public long getClanCreateExpiryTime() {
        return this.data.getClanCreateExpiryTime();
    }
    
    public void setClanCreateExpiryTime(final long time) {
        this.data.setClanCreateExpiryTime(time);
    }
    
    public void setOnlineTime(final long time) {
        this._onlineTime = time;
        this._onlineBeginTime = System.currentTimeMillis();
    }
    
    @Override
    public PlayerInventory getInventory() {
        return this.inventory;
    }
    
    public void removeItemFromShortCut(final int objectId) {
        this.shortcuts.deleteShortCutByObjectId(objectId);
    }
    
    public boolean isSitting() {
        return this._waitTypeSitting;
    }
    
    public void setIsSitting(final boolean state) {
        this._waitTypeSitting = state;
    }
    
    public void sitDown() {
        this.sitDown(true);
    }
    
    public void sitDown(final boolean checkCast) {
        if (checkCast && this.isCastingNow()) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_MOVE_WHILE_CASTING);
            return;
        }
        if (!this._waitTypeSitting && !this.isAttackingDisabled() && !this.isControlBlocked() && !this.isImmobilized() && !this.isFishing()) {
            this.breakAttack();
            this.setIsSitting(true);
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_REST);
            this.broadcastPacket(new ChangeWaitType(this, 0));
            ThreadPool.schedule((Runnable)new SitDownTask(this), 2500L);
            this.setBlockActions(true);
        }
    }
    
    public void standUp() {
        if (Event.isParticipant(this) && this.eventStatus.isSitForced()) {
            this.sendMessage("A dark force beyond your mortal understanding makes your knees to shake when you try to stand up...");
        }
        else if (this._waitTypeSitting && !this.isInStoreMode() && !this.isAlikeDead()) {
            if (this.getEffectList().isAffected(EffectFlag.RELAXING)) {
                this.stopEffects(EffectFlag.RELAXING);
            }
            this.broadcastPacket(new ChangeWaitType(this, 1));
            ThreadPool.schedule((Runnable)new StandUpTask(this), 2500L);
        }
    }
    
    public PlayerWarehouse getWarehouse() {
        if (this._warehouse == null) {
            (this._warehouse = new PlayerWarehouse(this)).restore();
        }
        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().addCacheTask(this);
        }
        return this._warehouse;
    }
    
    public void clearWarehouse() {
        if (this._warehouse != null) {
            this._warehouse.deleteMe();
        }
        this._warehouse = null;
    }
    
    public PlayerFreight getFreight() {
        return this._freight;
    }
    
    public boolean hasRefund() {
        return this._refund != null && this._refund.getSize() > 0 && Config.ALLOW_REFUND;
    }
    
    public PlayerRefund getRefund() {
        if (this._refund == null) {
            this._refund = new PlayerRefund(this);
        }
        return this._refund;
    }
    
    public void clearRefund() {
        if (this._refund != null) {
            this._refund.deleteMe();
        }
        this._refund = null;
    }
    
    public long getAdena() {
        return this.inventory.getAdena();
    }
    
    public long getAncientAdena() {
        return this.inventory.getAncientAdena();
    }
    
    public long getBeautyTickets() {
        return this.inventory.getBeautyTickets();
    }
    
    public void addAdena(final String process, final long count, final WorldObject reference, final boolean sendMessage) {
        if (sendMessage) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
            sm.addLong(count);
            this.sendPacket(sm);
        }
        if (count > 0L) {
            this.inventory.addAdena(process, count, this, reference);
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(this.inventory.getAdenaInstance());
                this.sendInventoryUpdate(iu);
            }
            else {
                this.sendItemList();
            }
        }
    }
    
    public boolean reduceAdena(final String process, final long count, final WorldObject reference, final boolean sendMessage) {
        if (count > this.inventory.getAdena()) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            }
            return false;
        }
        if (count > 0L) {
            final Item adenaItem = this.inventory.getAdenaInstance();
            if (!this.inventory.reduceAdena(process, count, this, reference)) {
                return false;
            }
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(adenaItem);
                this.sendInventoryUpdate(iu);
            }
            else {
                this.sendItemList();
            }
            if (sendMessage) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_ADENA_DISAPPEARED);
                sm.addLong(count);
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    public boolean reduceBeautyTickets(final String process, final long count, final WorldObject reference, final boolean sendMessage) {
        if (count > this.inventory.getBeautyTickets()) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        if (count > 0L) {
            final Item beautyTickets = this.inventory.getBeautyTicketsInstance();
            if (!this.inventory.reduceBeautyTickets(process, count, this, reference)) {
                return false;
            }
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(beautyTickets);
                this.sendInventoryUpdate(iu);
            }
            else {
                this.sendItemList();
            }
            if (sendMessage) {
                if (count > 1L) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(36308);
                    sm.addLong(count);
                    this.sendPacket(sm);
                }
                else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(36308);
                    this.sendPacket(sm);
                }
            }
        }
        return true;
    }
    
    public void addAncientAdena(final String process, final long count, final WorldObject reference, final boolean sendMessage) {
        if (sendMessage) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
            sm.addItemName(5575);
            sm.addLong(count);
            this.sendPacket(sm);
        }
        if (count > 0L) {
            this.inventory.addAncientAdena(process, count, this, reference);
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(this.inventory.getAncientAdenaInstance());
                this.sendInventoryUpdate(iu);
            }
            else {
                this.sendItemList();
            }
        }
    }
    
    public boolean reduceAncientAdena(final String process, final long count, final WorldObject reference, final boolean sendMessage) {
        if (count > this.inventory.getAncientAdena()) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            }
            return false;
        }
        if (count > 0L) {
            final Item ancientAdenaItem = this.inventory.getAncientAdenaInstance();
            if (!this.inventory.reduceAncientAdena(process, count, this, reference)) {
                return false;
            }
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addItem(ancientAdenaItem);
                this.sendInventoryUpdate(iu);
            }
            else {
                this.sendItemList();
            }
            if (sendMessage) {
                if (count > 1L) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                    sm.addItemName(5575);
                    sm.addLong(count);
                    this.sendPacket(sm);
                }
                else {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                    sm.addItemName(5575);
                    this.sendPacket(sm);
                }
            }
        }
        return true;
    }
    
    public void addItem(final String process, final Item item, final WorldObject reference, final boolean sendMessage) {
        if (item.getCount() > 0L) {
            if (sendMessage) {
                if (item.getCount() > 1L) {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1).addItemName(item)).addLong(item.getCount()));
                }
                else if (item.getEnchantLevel() > 0) {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2).addInt(item.getEnchantLevel())).addItemName(item));
                }
                else {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1)).addItemName(item));
                }
            }
            final Item newitem = this.inventory.addItem(process, item, this, reference);
            if (!this.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !this.inventory.validateCapacity(0L, item.isQuestItem()) && newitem.isDropable() && (!newitem.isStackable() || newitem.getLastChange() != 2)) {
                this.dropItem("InvDrop", newitem, null, true, true);
            }
        }
    }
    
    public Item addItem(final String process, final int itemId, final long count, final WorldObject reference, final boolean sendMessage) {
        return this.addItem(process, itemId, count, 0, reference, sendMessage);
    }
    
    public Item addItem(final String process, final int itemId, final long count, final int enchant, final WorldObject reference, final boolean sendMessage) {
        Item item = null;
        if (count > 0L) {
            final ItemTemplate template = ItemEngine.getInstance().getTemplate(itemId);
            if (Objects.isNull(template)) {
                Player.LOGGER.error("Item doesn't exist so cannot be added. Item ID: {}", (Object)itemId);
                return null;
            }
            if (template.hasExImmediateEffect()) {
                final ItemHandler instance = ItemHandler.getInstance();
                final ItemTemplate itemTemplate = template;
                final EtcItem etcItem;
                final IItemHandler handler = instance.getHandler((itemTemplate instanceof EtcItem && (etcItem = (EtcItem)itemTemplate) == itemTemplate) ? etcItem : null);
                if (handler == null) {
                    Player.LOGGER.warn("No item handler registered for immediate item id {}!", (Object)template.getId());
                }
                else {
                    handler.useItem(this, new Item(itemId), false);
                }
            }
            else {
                item = this.inventory.addItem(process, itemId, count, this, reference);
                if (enchant > 0) {
                    item.setEnchantLevel(enchant);
                }
                if (!this.canOverrideCond(PcCondOverride.ITEM_CONDITIONS) && !this.inventory.validateCapacity(0L, template.isQuestItem()) && item.isDropable() && (!item.isStackable() || item.getLastChange() != 2)) {
                    this.dropItem("InvDrop", item, null, true);
                }
            }
            if (sendMessage) {
                if (count > 1L) {
                    if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
                        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S).addItemName(template)).addLong(count));
                    }
                    else {
                        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S2_S1).addItemName(template)).addLong(count));
                    }
                }
                else if (process.equalsIgnoreCase("Sweeper") || process.equalsIgnoreCase("Quest")) {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1)).addItemName(template));
                }
                else if (enchant > 0) {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_A_S1_S2).addItemName(template)).addInt(enchant));
                }
                else {
                    this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_OBTAINED_S1)).addItemName(template));
                }
            }
        }
        return item;
    }
    
    public Item addItem(final String process, final ItemHolder item, final WorldObject reference, final boolean sendMessage) {
        return this.addItem(process, item.getId(), item.getCount(), item.getEnchantment(), reference, sendMessage);
    }
    
    public boolean destroyItem(final String process, final Item item, final WorldObject reference, final boolean sendMessage) {
        return this.destroyItem(process, item, item.getCount(), reference, sendMessage);
    }
    
    public boolean destroyItem(final String process, Item item, final long count, final WorldObject reference, final boolean sendMessage) {
        item = this.inventory.destroyItem(process, item, count, this, reference);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        final InventoryUpdate playerIU = new InventoryUpdate();
        playerIU.addItem(item);
        this.sendInventoryUpdate(playerIU);
        if (sendMessage) {
            if (count > 1L) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(item);
                sm.addLong(count);
                this.sendPacket(sm);
            }
            else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(item);
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    @Override
    public boolean destroyItem(final String process, final int objectId, final long count, final WorldObject reference, final boolean sendMessage) {
        final Item item = this.inventory.getItemByObjectId(objectId);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        return this.destroyItem(process, item, count, reference, sendMessage);
    }
    
    public boolean destroyItemWithoutTrace(final String process, final int objectId, final long count, final WorldObject reference, final boolean sendMessage) {
        final Item item = this.inventory.getItemByObjectId(objectId);
        if (item == null || item.getCount() < count) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        return this.destroyItem(null, item, count, reference, sendMessage);
    }
    
    @Override
    public boolean destroyItemByItemId(final String process, final int itemId, final long count, final WorldObject reference, final boolean sendMessage) {
        if (itemId == 57) {
            return this.reduceAdena(process, count, reference, sendMessage);
        }
        final Item item = this.inventory.getItemByItemId(itemId);
        if (item == null || item.getCount() < count || this.inventory.destroyItemByItemId(process, itemId, count, this, reference) == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(item);
            this.sendInventoryUpdate(playerIU);
        }
        else {
            this.sendItemList();
        }
        if (sendMessage) {
            if (count > 1L) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S2_S1_S_DISAPPEARED);
                sm.addItemName(itemId);
                sm.addLong(count);
                this.sendPacket(sm);
            }
            else {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
                sm.addItemName(itemId);
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    public Item transferItem(final String process, final int objectId, final long count, final Inventory target, final WorldObject reference) {
        final Item oldItem = this.checkItemManipulation(objectId, count, "transfer");
        if (oldItem == null) {
            return null;
        }
        final Item newItem = this.inventory.transferItem(process, objectId, count, target, this, reference);
        if (newItem == null) {
            return null;
        }
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            if (oldItem.getCount() > 0L && oldItem != newItem) {
                playerIU.addModifiedItem(oldItem);
            }
            else {
                playerIU.addRemovedItem(oldItem);
            }
            this.sendInventoryUpdate(playerIU);
        }
        else {
            this.sendItemList();
        }
        if (target instanceof PlayerInventory) {
            final Player targetPlayer = ((PlayerInventory)target).getOwner();
            if (!Config.FORCE_INVENTORY_UPDATE) {
                final InventoryUpdate playerIU2 = new InventoryUpdate();
                if (newItem.getCount() > count) {
                    playerIU2.addModifiedItem(newItem);
                }
                else {
                    playerIU2.addNewItem(newItem);
                }
                targetPlayer.sendPacket(playerIU2);
            }
            else {
                targetPlayer.sendItemList();
            }
        }
        return newItem;
    }
    
    public boolean exchangeItemsById(final String process, final WorldObject reference, final int coinId, final long cost, final int rewardId, final long count, final boolean sendMessage) {
        final PlayerInventory inv = this.inventory;
        if (!inv.validateCapacityByItemId(rewardId, count)) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
            }
            return false;
        }
        if (!inv.validateWeightByItemId(rewardId, count)) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT);
            }
            return false;
        }
        if (this.destroyItemByItemId(process, coinId, cost, reference, sendMessage)) {
            this.addItem(process, rewardId, count, reference, sendMessage);
            return true;
        }
        return false;
    }
    
    public boolean dropItem(final String process, Item item, final WorldObject reference, final boolean sendMessage, final boolean protectItem) {
        item = this.inventory.dropItem(process, item, this, reference);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return false;
        }
        item.dropMe(this, this.getX() + Rnd.get(50) - 25, this.getY() + Rnd.get(50) - 25, this.getZ() + 20);
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.autoDestroyItemTime() > 0 && generalSettings.destroyPlayerDroppedItem() && !generalSettings.isProtectedItem(item.getId()) && (!item.isEquipable() || generalSettings.destroyEquipableItem())) {
            ItemsAutoDestroy.getInstance().addItem(item);
        }
        if (generalSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !generalSettings.destroyEquipableItem());
        }
        else {
            item.setProtected(true);
        }
        if (protectItem) {
            item.getDropProtection().protect(this);
        }
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(item);
            this.sendInventoryUpdate(playerIU);
        }
        else {
            this.sendItemList();
        }
        if (sendMessage) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
            sm.addItemName(item);
            this.sendPacket(sm);
        }
        return true;
    }
    
    public boolean dropItem(final String process, final Item item, final WorldObject reference, final boolean sendMessage) {
        return this.dropItem(process, item, reference, sendMessage, false);
    }
    
    public Item dropItem(final String process, final int objectId, final long count, final int x, final int y, final int z, final WorldObject reference, final boolean sendMessage, final boolean protectItem) {
        final Item invitem = this.inventory.getItemByObjectId(objectId);
        final Item item = this.inventory.dropItem(process, objectId, count, this, reference);
        if (item == null) {
            if (sendMessage) {
                this.sendPacket(SystemMessageId.INCORRECT_ITEM_COUNT);
            }
            return null;
        }
        item.dropMe(this, x, y, z);
        final GeneralSettings generalSettings = (GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class);
        if (generalSettings.autoDestroyItemTime() > 0 && generalSettings.destroyPlayerDroppedItem() && !generalSettings.isProtectedItem(item.getId()) && (!item.isEquipable() || generalSettings.destroyEquipableItem())) {
            ItemsAutoDestroy.getInstance().addItem(item);
        }
        if (generalSettings.destroyPlayerDroppedItem()) {
            item.setProtected(item.isEquipable() && !generalSettings.destroyEquipableItem());
        }
        else {
            item.setProtected(true);
        }
        if (protectItem) {
            item.getDropProtection().protect(this);
        }
        if (!Config.FORCE_INVENTORY_UPDATE) {
            final InventoryUpdate playerIU = new InventoryUpdate();
            playerIU.addItem(invitem);
            this.sendInventoryUpdate(playerIU);
        }
        else {
            this.sendItemList();
        }
        if (sendMessage) {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_DROPPED_S1);
            sm.addItemName(item);
            this.sendPacket(sm);
        }
        return item;
    }
    
    public Item checkItemManipulation(final int objectId, final long count, final String action) {
        if (World.getInstance().findObject(objectId) == null) {
            Player.LOGGER.debug("player {} tried to {} item not available in World", (Object)this, (Object)action);
            return null;
        }
        final Item item = this.inventory.getItemByObjectId(objectId);
        if (item == null || item.getOwnerId() != this.getObjectId()) {
            Player.LOGGER.debug(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.getObjectId(), action));
            return null;
        }
        if (count < 0L || (count > 1L && !item.isStackable())) {
            Player.LOGGER.debug(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;J)Ljava/lang/String;, this.getObjectId(), action, count));
            return null;
        }
        if (count > item.getCount()) {
            Player.LOGGER.debug(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.getObjectId(), action));
            return null;
        }
        if ((this.pet != null && this.pet.getControlObjectId() == objectId) || this.mountObjectID == objectId) {
            return null;
        }
        if (this.isProcessingItem(objectId)) {
            return null;
        }
        if (item.isAugmented() && this.isCastingNow()) {
            return null;
        }
        return item;
    }
    
    public boolean isSpawnProtected() {
        return this._spawnProtectEndTime > 0L && this._spawnProtectEndTime > System.currentTimeMillis();
    }
    
    public boolean isTeleportProtected() {
        return this._teleportProtectEndTime > 0L && this._teleportProtectEndTime > System.currentTimeMillis();
    }
    
    public void setSpawnProtection(final boolean protect) {
        this._spawnProtectEndTime = (protect ? (System.currentTimeMillis() + Config.PLAYER_SPAWN_PROTECTION * 1000) : 0L);
    }
    
    public void setTeleportProtection(final boolean protect) {
        this._teleportProtectEndTime = (protect ? (System.currentTimeMillis() + Config.PLAYER_TELEPORT_PROTECTION * 1000) : 0L);
    }
    
    public boolean isRecentFakeDeath() {
        return this._recentFakeDeathEndTime > WorldTimeController.getInstance().getGameTicks();
    }
    
    public void setRecentFakeDeath(final boolean protect) {
        this._recentFakeDeathEndTime = (protect ? (WorldTimeController.getInstance().getGameTicks() + Config.PLAYER_FAKEDEATH_UP_PROTECTION * 10) : 0L);
    }
    
    public final boolean isFakeDeath() {
        return this.isAffected(EffectFlag.FAKE_DEATH);
    }
    
    @Override
    public final boolean isAlikeDead() {
        return super.isAlikeDead() || this.isFakeDeath();
    }
    
    public GameClient getClient() {
        return this._client;
    }
    
    public void setClient(final GameClient client) {
        this._client = client;
        if (this._client != null && this._client.getHostAddress() != null) {
            this._ip = this._client.getHostAddress();
        }
    }
    
    public String getIPAddress() {
        return this._ip;
    }
    
    public Location getCurrentSkillWorldPosition() {
        return this._currentSkillWorldPosition;
    }
    
    public void setCurrentSkillWorldPosition(final Location worldPosition) {
        this._currentSkillWorldPosition = worldPosition;
    }
    
    @Override
    public void enableSkill(final Skill skill) {
        super.enableSkill(skill);
        this.removeTimeStamp(skill);
    }
    
    private boolean needCpUpdate() {
        final double currentCp = this.getCurrentCp();
        if (currentCp <= 1.0 || this.getMaxCp() < 352.0) {
            return true;
        }
        if (currentCp <= this._cpUpdateDecCheck || currentCp >= this._cpUpdateIncCheck) {
            if (currentCp == this.getMaxCp()) {
                this._cpUpdateIncCheck = currentCp + 1.0;
                this._cpUpdateDecCheck = currentCp - this._cpUpdateInterval;
            }
            else {
                final double doubleMulti = currentCp / this._cpUpdateInterval;
                int intMulti = (int)doubleMulti;
                this._cpUpdateDecCheck = this._cpUpdateInterval * ((doubleMulti < intMulti) ? intMulti-- : intMulti);
                this._cpUpdateIncCheck = this._cpUpdateDecCheck + this._cpUpdateInterval;
            }
            return true;
        }
        return false;
    }
    
    private boolean needMpUpdate() {
        final double currentMp = this.getCurrentMp();
        if (currentMp <= 1.0 || this.getMaxMp() < 352.0) {
            return true;
        }
        if (currentMp <= this._mpUpdateDecCheck || currentMp >= this._mpUpdateIncCheck) {
            if (currentMp == this.getMaxMp()) {
                this._mpUpdateIncCheck = currentMp + 1.0;
                this._mpUpdateDecCheck = currentMp - this._mpUpdateInterval;
            }
            else {
                final double doubleMulti = currentMp / this._mpUpdateInterval;
                int intMulti = (int)doubleMulti;
                this._mpUpdateDecCheck = this._mpUpdateInterval * ((doubleMulti < intMulti) ? intMulti-- : intMulti);
                this._mpUpdateIncCheck = this._mpUpdateDecCheck + this._mpUpdateInterval;
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void broadcastStatusUpdate(final Creature caster) {
        final StatusUpdate su = new StatusUpdate(this);
        if (caster != null) {
            su.addCaster(caster);
        }
        this.computeStatusUpdate(su, StatusUpdateType.LEVEL);
        this.computeStatusUpdate(su, StatusUpdateType.MAX_HP);
        this.computeStatusUpdate(su, StatusUpdateType.CUR_HP);
        this.computeStatusUpdate(su, StatusUpdateType.MAX_MP);
        this.computeStatusUpdate(su, StatusUpdateType.CUR_MP);
        this.computeStatusUpdate(su, StatusUpdateType.MAX_CP);
        this.computeStatusUpdate(su, StatusUpdateType.CUR_CP);
        if (su.hasUpdates()) {
            this.broadcastPacket(su);
        }
        final boolean needCpUpdate = this.needCpUpdate();
        final boolean needHpUpdate = this.needHpUpdate();
        final boolean needMpUpdate = this.needMpUpdate();
        if (this._party != null && (needCpUpdate || needHpUpdate || needMpUpdate)) {
            final PartySmallWindowUpdate partyWindow = new PartySmallWindowUpdate(this, false);
            if (needCpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_CP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_CP);
            }
            if (needHpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_HP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_HP);
            }
            if (needMpUpdate) {
                partyWindow.addComponentType(PartySmallWindowUpdateType.CURRENT_MP);
                partyWindow.addComponentType(PartySmallWindowUpdateType.MAX_MP);
            }
            this._party.broadcastToPartyMembers(this, partyWindow);
        }
        if (this._inOlympiadMode && this._OlympiadStart && (needCpUpdate || needHpUpdate)) {
            final OlympiadGameTask game = OlympiadGameManager.getInstance().getOlympiadTask(this.getOlympiadGameId());
            if (game != null && game.isBattleStarted()) {
                game.getStadium().broadcastStatusUpdate(this);
            }
        }
        if (this._isInDuel && (needCpUpdate || needHpUpdate)) {
            DuelManager.getInstance().broadcastToOppositTeam(this, new ExDuelUpdateUserInfo(this));
        }
    }
    
    public final void broadcastUserInfo() {
        this.sendPacket(new UserInfo(this));
        this.broadcastCharInfo();
    }
    
    public final void broadcastUserInfo(final UserInfoType... types) {
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(types);
        this.sendPacket(ui);
        this.broadcastCharInfo();
    }
    
    public final void broadcastCharInfo() {
        final ExCharInfo charInfo = new ExCharInfo(this);
        final ServerPacket serverPacket;
        int relation;
        Integer oldRelation;
        RelationChanged rc;
        Summon pet;
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (this.isVisibleFor(player)) {
                if (!this.isInvisible() || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)) {
                    player.sendPacket(serverPacket);
                }
                relation = this.getRelation(player);
                oldRelation = this.getKnownRelations().get(player.getObjectId());
                if (oldRelation == null || oldRelation != relation) {
                    rc = new RelationChanged();
                    rc.addRelation(this, relation, !this.isInsideZone(ZoneType.PEACE));
                    if (this.hasSummon()) {
                        pet = this.getPet();
                        if (pet != null) {
                            rc.addRelation(pet, relation, !this.isInsideZone(ZoneType.PEACE));
                        }
                        if (this.hasServitors()) {
                            this.getServitors().values().forEach(s -> rc.addRelation(s, relation, !this.isInsideZone(ZoneType.PEACE)));
                        }
                    }
                    player.sendPacket(rc);
                    this.getKnownRelations().put(player.getObjectId(), relation);
                }
            }
        });
    }
    
    public final void broadcastTitleInfo() {
        this.broadcastUserInfo(UserInfoType.CLAN);
        this.broadcastPacket(new NicknameChanged(this));
    }
    
    @Override
    public final void broadcastPacket(final ServerPacket mov) {
        if (mov instanceof ExCharInfo) {
            throw new IllegalArgumentException("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }
        this.sendPacket(mov);
        final World instance = World.getInstance();
        final Class<Player> clazz = Player.class;
        Objects.requireNonNull(mov);
        instance.forEachVisibleObject(this, clazz, mov::sendTo, this::isVisibleFor);
    }
    
    @Override
    public void broadcastPacket(final ServerPacket mov, final int radiusInKnownlist) {
        if (mov instanceof ExCharInfo) {
            Player.LOGGER.warn("ExCharInfo is being send via broadcastPacket. Do NOT do that! Use broadcastCharInfo() instead.");
        }
        this.sendPacket(mov);
        final World instance = World.getInstance();
        final Class<Player> clazz = Player.class;
        Objects.requireNonNull(mov);
        instance.forEachVisibleObjectInRange(this, clazz, radiusInKnownlist, mov::sendTo, this::isVisibleFor);
    }
    
    @Override
    public int getAllyId() {
        return Util.zeroIfNullOrElse((Object)this._clan, Clan::getAllyId);
    }
    
    public int getAllyCrestId() {
        return Util.zeroIfNullOrElse((Object)this._clan, Clan::getAllyCrestId);
    }
    
    @Override
    public void sendPacket(final ServerPacket... packets) {
        if (this._client != null) {
            for (final ServerPacket packet : packets) {
                this._client.sendPacket(packet);
            }
        }
    }
    
    @Override
    public void sendPacket(final SystemMessageId id) {
        this.sendPacket(SystemMessage.getSystemMessage(id));
    }
    
    public void doInteract(final Creature target) {
        if (target == null) {
            return;
        }
        if (GameUtils.isPlayer(target)) {
            final Player targetPlayer = (Player)target;
            this.sendPacket(ActionFailed.STATIC_PACKET);
            if (targetPlayer.getPrivateStoreType() == PrivateStoreType.SELL || targetPlayer.getPrivateStoreType() == PrivateStoreType.PACKAGE_SELL) {
                this.sendPacket(new PrivateStoreListSell(this, targetPlayer));
            }
            else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.BUY) {
                this.sendPacket(new PrivateStoreListBuy(this, targetPlayer));
            }
            else if (targetPlayer.getPrivateStoreType() == PrivateStoreType.MANUFACTURE) {
                this.sendPacket(new RecipeShopSellList(this, targetPlayer));
            }
        }
        else {
            target.onAction(this);
        }
    }
    
    public void doAutoLoot(final Attackable target, final int itemId, final long itemCount) {
        if (this.isInParty() && !ItemEngine.getInstance().getTemplate(itemId).hasExImmediateEffect()) {
            this._party.distributeItem(this, itemId, itemCount, false, target);
        }
        else if (itemId == 57) {
            this.addAdena("Loot", itemCount, target, true);
        }
        else {
            this.addItem("Loot", itemId, itemCount, target, true);
        }
    }
    
    public void doAutoLoot(final Attackable target, final ItemHolder item) {
        this.doAutoLoot(target, item.getId(), item.getCount());
    }
    
    @Override
    public void doPickupItem(final WorldObject object) {
        if (this.isAlikeDead() || this.isFakeDeath()) {
            return;
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        if (!GameUtils.isItem(object)) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Lorg/l2j/gameserver/model/WorldObject;)Ljava/lang/String;, this, this.getTarget()));
            return;
        }
        final Item target = (Item)object;
        this.sendPacket(ActionFailed.STATIC_PACKET);
        final StopMove sm = new StopMove(this);
        this.sendPacket(sm);
        SystemMessage smsg = null;
        synchronized (target) {
            if (!target.isSpawned()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return;
            }
            if (!target.getDropProtection().tryPickUp(this)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                this.sendPacket(smsg);
                return;
            }
            if ((!this.isInParty() || this._party.getDistributionType() == PartyDistributionType.FINDERS_KEEPERS) && !this.inventory.validateCapacity(target)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.YOUR_INVENTORY_IS_FULL);
                return;
            }
            if (this.isInvul() && !this.canOverrideCond(PcCondOverride.ITEM_CONDITIONS)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                smsg.addItemName(target);
                this.sendPacket(smsg);
                return;
            }
            if (target.getOwnerId() != 0 && target.getOwnerId() != this.getObjectId() && !this.isInLooterParty(target.getOwnerId())) {
                if (target.getId() == 57) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    smsg.addLong(target.getCount());
                }
                else if (target.getCount() > 1L) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S);
                    smsg.addItemName(target);
                    smsg.addLong(target.getCount());
                }
                else {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    smsg.addItemName(target);
                }
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(smsg);
                return;
            }
            if (target.getItemLootShedule() != null && (target.getOwnerId() == this.getObjectId() || this.isInLooterParty(target.getOwnerId()))) {
                target.resetOwnerTimer();
            }
            target.pickupMe(this);
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).saveDroppedItems()) {
                ItemsOnGroundManager.getInstance().removeObject(target);
            }
        }
        if (target.getTemplate().hasExImmediateEffect()) {
            final IItemHandler handler = ItemHandler.getInstance().getHandler(target.getEtcItem());
            if (handler == null) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, target.getId()));
            }
            else {
                handler.useItem(this, target, false);
            }
            ItemEngine.getInstance().destroyItem("Consume", target, this, null);
        }
        else {
            if (target.getItemType() instanceof ArmorType || target.getItemType() instanceof WeaponType) {
                if (target.getEnchantLevel() > 0) {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2_S3);
                    smsg.addPcName(this);
                    smsg.addInt(target.getEnchantLevel());
                    smsg.addItemName(target.getId());
                    this.broadcastPacket(smsg, 1400);
                }
                else {
                    smsg = SystemMessage.getSystemMessage(SystemMessageId.ATTENTION_C1_HAS_PICKED_UP_S2);
                    smsg.addPcName(this);
                    smsg.addItemName(target.getId());
                    this.broadcastPacket(smsg, 1400);
                }
            }
            if (this.isInParty()) {
                this._party.distributeItem(this, target);
            }
            else if (target.getId() == 57 && this.inventory.getAdenaInstance() != null) {
                this.addAdena("Pickup", target.getCount(), null, true);
                ItemEngine.getInstance().destroyItem("Pickup", target, this, null);
            }
            else {
                this.addItem("Pickup", target, null, true);
                final Item weapon = this.inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
                if (weapon != null) {
                    final EtcItem etcItem = target.getEtcItem();
                    if (etcItem != null) {
                        final EtcItemType itemType = etcItem.getItemType();
                        if ((weapon.getItemType() == WeaponType.BOW && itemType == EtcItemType.ARROW) || ((weapon.getItemType() == WeaponType.CROSSBOW || weapon.getItemType() == WeaponType.TWO_HAND_CROSSBOW) && itemType == EtcItemType.BOLT)) {
                            this.inventory.findAmmunitionForCurrentWeapon();
                        }
                    }
                }
            }
        }
    }
    
    @Override
    public void doAutoAttack(final Creature target) {
        super.doAutoAttack(target);
        this.setRecentFakeDeath(false);
    }
    
    @Override
    public void doCast(final Skill skill) {
        super.doCast(skill);
        this.setRecentFakeDeath(false);
    }
    
    public boolean canOpenPrivateStore() {
        return !this._isSellingBuffs && !this.isAlikeDead() && !this._inOlympiadMode && !this.isMounted() && !this.isInsideZone(ZoneType.NO_STORE) && !this.isCastingNow();
    }
    
    public void tryOpenPrivateBuyStore() {
        if (this.canOpenPrivateStore()) {
            if (this.privateStoreType == PrivateStoreType.BUY || this.privateStoreType == PrivateStoreType.BUY_MANAGE) {
                this.setPrivateStoreType(PrivateStoreType.NONE);
            }
            if (this.privateStoreType == PrivateStoreType.NONE) {
                if (this._waitTypeSitting) {
                    this.standUp();
                }
                this.setPrivateStoreType(PrivateStoreType.BUY_MANAGE);
                this.sendPacket(new PrivateStoreManageListBuy(1, this));
                this.sendPacket(new PrivateStoreManageListBuy(2, this));
            }
        }
        else {
            if (this.isInsideZone(ZoneType.NO_STORE)) {
                this.sendPacket(SystemMessageId.YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE);
            }
            this.sendPacket(ActionFailed.STATIC_PACKET);
        }
    }
    
    public final PreparedMultisellListHolder getMultiSell() {
        return this._currentMultiSell;
    }
    
    public final void setMultiSell(final PreparedMultisellListHolder list) {
        this._currentMultiSell = list;
    }
    
    @Override
    public void setTarget(WorldObject newTarget) {
        if (newTarget != null) {
            final boolean isInParty = GameUtils.isPlayer(newTarget) && this.isInParty() && this._party.containsPlayer(newTarget.getActingPlayer());
            if (!isInParty && Math.abs(newTarget.getZ() - this.getZ()) > 1000) {
                newTarget = null;
            }
            if (newTarget != null && !isInParty && !newTarget.isSpawned()) {
                newTarget = null;
            }
            if (!this.isGM() && newTarget instanceof Vehicle) {
                newTarget = null;
            }
        }
        final WorldObject oldTarget = this.getTarget();
        if (oldTarget != null) {
            if (oldTarget.equals(newTarget)) {
                if (newTarget.getObjectId() != this.getObjectId()) {
                    this.sendPacket(new ValidateLocation(newTarget));
                }
                return;
            }
            oldTarget.removeStatusListener(this);
        }
        if (GameUtils.isCreature(newTarget)) {
            final Creature target = (Creature)newTarget;
            if (newTarget.getObjectId() != this.getObjectId()) {
                this.sendPacket(new ValidateLocation(target));
            }
            this.sendPacket(new MyTargetSelected(this, target));
            target.addStatusListener(this);
            final StatusUpdate su = new StatusUpdate(target);
            su.addUpdate(StatusUpdateType.MAX_HP, target.getMaxHp());
            su.addUpdate(StatusUpdateType.CUR_HP, (int)target.getCurrentHp());
            this.sendPacket(su);
            Broadcast.toKnownPlayers(this, new TargetSelected(this.getObjectId(), newTarget.getObjectId(), this.getX(), this.getY(), this.getZ()));
            this.sendPacket(new ExAbnormalStatusUpdateFromTarget(target));
        }
        if (newTarget == null && this.getTarget() != null) {
            this.broadcastPacket(new TargetUnselected(this));
        }
        super.setTarget(newTarget);
    }
    
    @Override
    public Item getActiveWeaponInstance() {
        return this.inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
    }
    
    @Override
    public Weapon getActiveWeaponItem() {
        final Item weapon = this.getActiveWeaponInstance();
        if (Objects.isNull(weapon)) {
            return this._fistsWeaponItem;
        }
        return (Weapon)weapon.getTemplate();
    }
    
    public Item getChestArmorInstance() {
        return this.inventory.getPaperdollItem(InventorySlot.CHEST);
    }
    
    public Item getLegsArmorInstance() {
        return this.inventory.getPaperdollItem(InventorySlot.LEGS);
    }
    
    public Armor getActiveChestArmorItem() {
        final Item armor = this.getChestArmorInstance();
        if (armor == null) {
            return null;
        }
        return (Armor)armor.getTemplate();
    }
    
    public Armor getActiveLegsArmorItem() {
        final Item legs = this.getLegsArmorInstance();
        if (legs == null) {
            return null;
        }
        return (Armor)legs.getTemplate();
    }
    
    public boolean isWearingHeavyArmor() {
        final Item legs = this.getLegsArmorInstance();
        final Item armor = this.getChestArmorInstance();
        return (armor != null && legs != null && legs.getItemType() == ArmorType.HEAVY && armor.getItemType() == ArmorType.HEAVY) || (armor != null && this.inventory.getPaperdollItem(BodyPart.CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR && armor.getItemType() == ArmorType.HEAVY);
    }
    
    public boolean isWearingLightArmor() {
        final Item legs = this.getLegsArmorInstance();
        final Item armor = this.getChestArmorInstance();
        return (armor != null && legs != null && legs.getItemType() == ArmorType.LIGHT && armor.getItemType() == ArmorType.LIGHT) || (armor != null && this.inventory.getPaperdollItem(BodyPart.CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR && armor.getItemType() == ArmorType.LIGHT);
    }
    
    public boolean isWearingMagicArmor() {
        final Item legs = this.getLegsArmorInstance();
        final Item armor = this.getChestArmorInstance();
        return (armor != null && legs != null && legs.getItemType() == ArmorType.MAGIC && armor.getItemType() == ArmorType.MAGIC) || (armor != null && this.inventory.getPaperdollItem(BodyPart.CHEST.slot()).getBodyPart() == BodyPart.FULL_ARMOR && armor.getItemType() == ArmorType.MAGIC);
    }
    
    @Override
    public Item getSecondaryWeaponInstance() {
        return this.inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
    }
    
    @Override
    public ItemTemplate getSecondaryWeaponItem() {
        final Item item = this.inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
        if (item != null) {
            return item.getTemplate();
        }
        return null;
    }
    
    @Override
    public boolean doDie(final Creature killer) {
        if (Objects.nonNull(killer)) {
            if (!super.doDie(killer)) {
                return false;
            }
            final Player pk = killer.getActingPlayer();
            if (Objects.nonNull(pk)) {
                this.onKilledByPlayer(pk);
            }
            this.data.setExpBeforeDeath(0L);
            final Collection<Item> droppedItems = this.onDieDropItem(killer);
            this.sendPacket(new ExDieInfo((Collection<DamageInfo>)this.lastDamages, droppedItems));
            final boolean insidePvpZone = this.isInsideZone(ZoneType.PVP) || this.isInsideZone(ZoneType.SIEGE);
            if (!insidePvpZone && pk != null) {
                final Clan pkClan = pk.getClan();
                if (pkClan != null && this._clan != null && !this.isAcademyMember() && !pk.isAcademyMember()) {
                    final ClanWar clanWar = this._clan.getWarWith(pkClan.getId());
                    if (clanWar != null && AntiFeedManager.getInstance().check(killer, this)) {
                        clanWar.onKill(pk, this);
                    }
                }
            }
            if (!this.isLucky() && !insidePvpZone) {
                this.calculateDeathExpPenalty(killer);
            }
        }
        if (this.isMounted()) {
            this.stopFeed();
        }
        synchronized (this) {
            if (this.isFakeDeath()) {
                this.stopFakeDeath(true);
            }
        }
        if (!this._cubics.isEmpty()) {
            this._cubics.values().forEach(CubicInstance::deactivate);
            this._cubics.clear();
        }
        if (this._agathionId != 0) {
            this.setAgathionId(0);
        }
        this.stopRentPet();
        this.stopWaterTask();
        if (this.hasCharmOfCourage) {
            if (this.isInSiege()) {
                this.reviveRequest(this, null, false, 0);
            }
            this.hasCharmOfCourage = false;
            this.sendPacket(new EtcStatusUpdate(this));
        }
        Util.doIfNonNull((Object)this.getInstanceWorld(), instance -> instance.onDeath(this));
        AntiFeedManager.getInstance().setLastDeathTime(this.getObjectId());
        if (this.getReputation() < 0) {
            final int newRep = this.getReputation() - this.getReputation() / 4;
            this.setReputation((newRep < -20) ? newRep : 0);
        }
        if (this.autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
        return true;
    }
    
    private void onKilledByPlayer(final Player killer) {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPvPKill(killer, this), this);
        if (Event.isParticipant(killer)) {
            killer.getEventStatus().addKill(this);
        }
        else {
            this.sendPacket(new ExNewPk(killer));
            ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).updatePlayerKiller(this.objectId, killer.objectId, Instant.now().getEpochSecond());
        }
        if ((!Config.DISABLE_REWARDS_IN_INSTANCES || this.getInstanceId() == 0) && (!Config.DISABLE_REWARDS_IN_PVP_ZONES || !this.isInsideZone(ZoneType.PVP))) {
            if (Config.REWARD_PVP_ITEM && this._pvpFlag != 0) {
                killer.addItem("PvP Item Reward", Config.REWARD_PVP_ITEM_ID, Config.REWARD_PVP_ITEM_AMOUNT, this, Config.REWARD_PVP_ITEM_MESSAGE);
            }
            if (Config.REWARD_PK_ITEM && this._pvpFlag == 0) {
                killer.addItem("PK Item Reward", Config.REWARD_PK_ITEM_ID, Config.REWARD_PK_ITEM_AMOUNT, this, Config.REWARD_PK_ITEM_MESSAGE);
            }
        }
        if (Config.ANNOUNCE_PK_PVP && !killer.isGM()) {
            String msg = "";
            if (this._pvpFlag == 0) {
                msg = Config.ANNOUNCE_PK_MSG.replace("$killer", killer.getName()).replace("$target", this.getName());
                if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE) {
                    Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1)).addString(msg));
                }
                else {
                    Broadcast.toAllOnlinePlayers(msg, false);
                }
            }
            else {
                msg = Config.ANNOUNCE_PVP_MSG.replace("$killer", killer.getName()).replace("$target", this.getName());
                if (Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE) {
                    Broadcast.toAllOnlinePlayers(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.S1)).addString(msg));
                }
                else {
                    Broadcast.toAllOnlinePlayers(msg, false);
                }
            }
        }
    }
    
    private Collection<Item> onDieDropItem(final Creature killer) {
        if (Event.isParticipant(this) || killer == null) {
            return (Collection<Item>)Collections.emptyList();
        }
        final Player pk = killer.getActingPlayer();
        if (this.getReputation() >= 0 && Objects.nonNull(pk) && Util.falseIfNullOrElse((Object)pk.getClan(), c -> c.isAtWarWith(this._clan))) {
            return (Collection<Item>)Collections.emptyList();
        }
        if ((!this.isInsideZone(ZoneType.PVP) || Objects.isNull(pk)) && (!this.isGM() || Config.KARMA_DROP_GM)) {
            boolean isKarmaDrop = false;
            int dropEquip = 0;
            int dropEquipWeapon = 0;
            int rateDropItem = 0;
            int dropLimit = 0;
            double dropPercent = 0.0;
            if (GameUtils.isPlayable(killer) && this.getReputation() < 0 && this._pkKills >= Config.KARMA_PK_LIMIT) {
                isKarmaDrop = true;
                dropPercent = Config.KARMA_RATE_DROP * this.getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_PVP, 1.0);
                dropEquip = Config.KARMA_RATE_DROP_EQUIP;
                dropEquipWeapon = Config.KARMA_RATE_DROP_EQUIP_WEAPON;
                rateDropItem = Config.KARMA_RATE_DROP_ITEM;
                dropLimit = Config.KARMA_DROP_LIMIT;
            }
            else if (GameUtils.isNpc(killer)) {
                dropPercent = Config.PLAYER_RATE_DROP * (killer.isRaid() ? this.getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_RAID, 1.0) : this.getStats().getValue(Stat.REDUCE_DEATH_PENALTY_BY_MOB, 1.0));
                dropEquip = Config.PLAYER_RATE_DROP_EQUIP;
                dropEquipWeapon = Config.PLAYER_RATE_DROP_EQUIP_WEAPON;
                rateDropItem = Config.PLAYER_RATE_DROP_ITEM;
                dropLimit = Config.PLAYER_DROP_LIMIT;
            }
            if (Rnd.chance(dropPercent)) {
                int dropCount = 0;
                final List<Item> droppedItems = new ArrayList<Item>(dropLimit);
                for (final Item itemDrop : this.inventory.getItems()) {
                    if (!itemDrop.isTimeLimitedItem() && itemDrop.isDropable() && itemDrop.getId() != 57 && itemDrop.getTemplate().getType2() != 3 && (this.pet == null || this.pet.getControlObjectId() != itemDrop.getId()) && Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_ITEMS, itemDrop.getId()) < 0) {
                        if (Arrays.binarySearch(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS, itemDrop.getId()) >= 0) {
                            continue;
                        }
                        int itemDropPercent;
                        if (itemDrop.isEquipped()) {
                            itemDropPercent = ((itemDrop.getTemplate().getType2() == 0) ? dropEquipWeapon : dropEquip);
                            this.inventory.unEquipItemInSlot(InventorySlot.fromId(itemDrop.getLocationSlot()));
                        }
                        else {
                            itemDropPercent = rateDropItem;
                        }
                        if (!Rnd.chance(itemDropPercent)) {
                            continue;
                        }
                        this.dropItem("DieDrop", itemDrop, killer, true);
                        droppedItems.add(itemDrop);
                        if (isKarmaDrop) {
                            Player.LOGGER.warn("{} has karma and dropped {} {}", new Object[] { this, itemDrop.getCount(), itemDrop });
                        }
                        else {
                            Player.LOGGER.warn("{} dropped {} {}", new Object[] { this, itemDrop.getCount(), itemDrop });
                        }
                        if (++dropCount >= dropLimit) {
                            break;
                        }
                        continue;
                    }
                }
            }
        }
        return (Collection<Item>)Collections.emptyList();
    }
    
    public void onPlayeableKill(final Playable killedPlayable) {
        final Player killedPlayer = killedPlayable.getActingPlayer();
        if (killedPlayer == null || this == killedPlayer) {
            return;
        }
        if (this.isInDuel() && killedPlayer.isInDuel()) {
            return;
        }
        if (this.isInsideZone(ZoneType.PVP) && killedPlayer.isInsideZone(ZoneType.PVP)) {
            return;
        }
        if (this.isInsideZone(ZoneType.SIEGE) && killedPlayer.isInsideZone(ZoneType.SIEGE)) {
            if (!this.isSiegeFriend(killedPlayable)) {
                final Clan targetClan = killedPlayer.getClan();
                if (this._clan != null && targetClan != null) {
                    this._clan.addSiegeKill();
                    targetClan.addSiegeDeath();
                }
            }
            return;
        }
        if (this.checkIfPvP(killedPlayer)) {
            if (killedPlayer.getReputation() < 0) {
                final int levelDiff = killedPlayer.getLevel() - this.getLevel();
                if (this.getReputation() >= 0 && levelDiff < 11 && levelDiff > -11) {
                    this.setReputation(this.getReputation() + Config.REPUTATION_INCREASE);
                }
            }
            this.setPvpKills(this._pvpKills + 1);
            this.updatePvpTitleAndColor(true);
        }
        else if (this.getReputation() > 0 && this._pkKills == 0) {
            this.setReputation(0);
            this.setPkKills(1);
        }
        else {
            this.setReputation(this.getReputation() - Formulas.calculateKarmaGain(this.getPkKills(), GameUtils.isSummon(killedPlayable)));
            this.setPkKills(this.getPkKills() + 1);
        }
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(UserInfoType.SOCIAL);
        this.sendPacket(ui);
        this.checkItemRestriction();
    }
    
    public void updatePvpTitleAndColor(final boolean broadcastInfo) {
        if (Config.PVP_COLOR_SYSTEM_ENABLED) {
            if (this._pvpKills >= Config.PVP_AMOUNT1 && this._pvpKills < Config.PVP_AMOUNT2) {
                this.setTitle(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.TITLE_FOR_PVP_AMOUNT1));
                this.appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT1);
            }
            else if (this._pvpKills >= Config.PVP_AMOUNT2 && this._pvpKills < Config.PVP_AMOUNT3) {
                this.setTitle(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.TITLE_FOR_PVP_AMOUNT2));
                this.appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT2);
            }
            else if (this._pvpKills >= Config.PVP_AMOUNT3 && this._pvpKills < Config.PVP_AMOUNT4) {
                this.setTitle(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.TITLE_FOR_PVP_AMOUNT3));
                this.appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT3);
            }
            else if (this._pvpKills >= Config.PVP_AMOUNT4 && this._pvpKills < Config.PVP_AMOUNT5) {
                this.setTitle(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.TITLE_FOR_PVP_AMOUNT4));
                this.appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT4);
            }
            else if (this._pvpKills >= Config.PVP_AMOUNT5) {
                this.setTitle(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.TITLE_FOR_PVP_AMOUNT5));
                this.appearance.setTitleColor(Config.NAME_COLOR_FOR_PVP_AMOUNT5);
            }
            if (broadcastInfo) {
                this.broadcastTitleInfo();
            }
        }
    }
    
    public void updatePvPStatus() {
        if (this.isInsideZone(ZoneType.PVP)) {
            return;
        }
        this.setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
        if (this._pvpFlag == 0) {
            this.startPvPFlag();
        }
    }
    
    public void updatePvPStatus(final Creature target) {
        final Player player_target = target.getActingPlayer();
        if (player_target == null) {
            return;
        }
        if (this == player_target) {
            return;
        }
        if (this._isInDuel && player_target.getDuelId() == this.getDuelId()) {
            return;
        }
        if ((!this.isInsideZone(ZoneType.PVP) || !player_target.isInsideZone(ZoneType.PVP)) && player_target.getReputation() >= 0) {
            if (this.checkIfPvP(player_target)) {
                this.setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_PVP_TIME);
            }
            else {
                this.setPvpFlagLasts(System.currentTimeMillis() + Config.PVP_NORMAL_TIME);
            }
            if (this._pvpFlag == 0) {
                this.startPvPFlag();
            }
        }
    }
    
    public boolean isLucky() {
        return this.getLevel() <= 9 && this.isAffectedBySkill(CommonSkill.LUCKY.getId());
    }
    
    public void restoreExp(final double restorePercent) {
        if (this.data.getExpBeforeDeath() > 0L) {
            this.getStats().addExp(Math.round((this.data.getExpBeforeDeath() - this.getExp()) * restorePercent / 100.0));
            this.data.setExpBeforeDeath(0L);
        }
    }
    
    public void calculateDeathExpPenalty(final Creature killer) {
        final int lvl = this.getLevel();
        final LevelData levelData = LevelData.getInstance();
        float percentLost = levelData.getXpPercentLost(this.getLevel());
        if (killer != null) {
            if (killer.isRaid()) {
                percentLost *= (float)this.getStats().getValue(Stat.REDUCE_EXP_LOST_BY_RAID, 1.0);
            }
            else if (GameUtils.isMonster(killer)) {
                percentLost *= (float)this.getStats().getValue(Stat.REDUCE_EXP_LOST_BY_MOB, 1.0);
            }
            else if (GameUtils.isPlayable(killer)) {
                percentLost *= (float)this.getStats().getValue(Stat.REDUCE_EXP_LOST_BY_PVP, 1.0);
            }
        }
        if (this.getReputation() < 0) {
            percentLost *= Config.RATE_KARMA_EXP_LOST;
        }
        long lostExp = 0L;
        if (!Event.isParticipant(this)) {
            if (lvl < LevelData.getInstance().getMaxLevel()) {
                lostExp = Math.round((this.getStats().getExpForLevel(lvl + 1) - this.getStats().getExpForLevel(lvl)) * percentLost / 100.0f);
            }
            else {
                lostExp = Math.round((this.getStats().getExpForLevel(LevelData.getInstance().getMaxLevel()) - this.getStats().getExpForLevel(LevelData.getInstance().getMaxLevel() - 1)) * percentLost / 100.0f);
            }
        }
        if (GameUtils.isPlayable(killer) && this.atWarWith(killer.getActingPlayer())) {
            lostExp /= (long)4.0;
        }
        this.data.setExpBeforeDeath(this.getExp());
        this.getStats().removeExp(lostExp);
    }
    
    public void stopAllTimers() {
        this.stopHpMpRegeneration();
        this.stopWarnUserTakeBreak();
        this.stopWaterTask();
        this.stopFeed();
        this.clearPetData();
        this.storePetFood(this._mountNpcId);
        this.stopRentPet();
        this.stopPvpRegTask();
        this.stopSoulTask();
        this.stopChargeTask();
        this.stopFameTask();
        this.stopRecoGiveTask();
        this.stopOnlineTimeUpdateTask();
    }
    
    @Override
    public Pet getPet() {
        return this.pet;
    }
    
    public void setPet(final Pet pet) {
        this.pet = pet;
    }
    
    @Override
    public Map<Integer, Summon> getServitors() {
        return (this._servitors == null) ? Collections.emptyMap() : this._servitors;
    }
    
    public Summon getAnyServitor() {
        return this.getServitors().values().stream().findAny().orElse(null);
    }
    
    public Summon getFirstServitor() {
        return this.getServitors().values().stream().findFirst().orElse(null);
    }
    
    @Override
    public Summon getServitor(final int objectId) {
        return this.getServitors().get(objectId);
    }
    
    public List<Summon> getServitorsAndPets() {
        final List<Summon> summons = new ArrayList<Summon>();
        summons.addAll(this.getServitors().values());
        if (this.pet != null) {
            summons.add(this.pet);
        }
        return summons;
    }
    
    public Trap getTrap() {
        final Stream<Npc> filter = this.getSummonedNpcs().stream().filter(GameUtils::isTrap);
        final Class<Trap> obj = Trap.class;
        Objects.requireNonNull(obj);
        return filter.map((Function<? super Npc, ?>)obj::cast).findAny().orElse(null);
    }
    
    public void addServitor(final Summon servitor) {
        if (this._servitors == null) {
            synchronized (this) {
                if (this._servitors == null) {
                    this._servitors = new ConcurrentHashMap<Integer, Summon>(1);
                }
            }
        }
        this._servitors.put(servitor.getObjectId(), servitor);
    }
    
    public Set<TamedBeast> getTrainedBeasts() {
        return this.tamedBeast;
    }
    
    public void addTrainedBeast(final TamedBeast tamedBeast) {
        if (this.tamedBeast == null) {
            synchronized (this) {
                if (this.tamedBeast == null) {
                    this.tamedBeast = (Set<TamedBeast>)ConcurrentHashMap.newKeySet();
                }
            }
        }
        this.tamedBeast.add(tamedBeast);
    }
    
    public Request getRequest() {
        return this._request;
    }
    
    public Player getActiveRequester() {
        final Player requester = this.activeRequester;
        if (Objects.nonNull(requester) && requester.isRequestExpired() && Objects.isNull(this.activeTradeList)) {
            this.activeRequester = null;
        }
        return this.activeRequester;
    }
    
    public void setActiveRequester(final Player requester) {
        this.activeRequester = requester;
    }
    
    public boolean isProcessingRequest() {
        return Objects.nonNull(this.getActiveRequester()) || this.requestExpireTime > WorldTimeController.getInstance().getGameTicks();
    }
    
    public boolean isProcessingTransaction() {
        return Objects.nonNull(this.getActiveRequester()) || Objects.nonNull(this.activeTradeList) || this.requestExpireTime > WorldTimeController.getInstance().getGameTicks();
    }
    
    public void onTransactionRequest(final Player partner) {
        this.requestExpireTime = WorldTimeController.getInstance().getGameTicks() + 150;
        partner.setActiveRequester(this);
    }
    
    public boolean isRequestExpired() {
        return this.requestExpireTime <= WorldTimeController.getInstance().getGameTicks();
    }
    
    public void onTransactionResponse() {
        this.requestExpireTime = 0L;
    }
    
    public Warehouse getActiveWarehouse() {
        return this.activeWarehouse;
    }
    
    public void setActiveWarehouse(final Warehouse warehouse) {
        this.activeWarehouse = warehouse;
    }
    
    public TradeList getActiveTradeList() {
        return this.activeTradeList;
    }
    
    private void onTradeStart(final Player partner) {
        this.activeTradeList = new TradeList(this, partner);
        this.sendPacket(TradeStart.partnerInfo(this, partner));
        this.sendPacket(TradeStart.itemsInfo(this));
        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_BEGIN_TRADING_WITH_C1)).addPcName(partner));
    }
    
    public void onTradeConfirm(final Player partner) {
        this.sendPacket(TradeOtherDone.STATIC_PACKET);
        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_CONFIRMED_THE_TRADE)).addPcName(partner));
    }
    
    private void onTradeCancel(final Player partner) {
        if (Objects.isNull(this.activeTradeList)) {
            return;
        }
        this.activeTradeList.lock();
        this.activeTradeList = null;
        this.sendPacket(TradeDone.CANCELLED);
        this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_CANCELLED_THE_TRADE)).addPcName(partner));
    }
    
    public void onTradeFinish(final boolean successfull) {
        this.activeTradeList = null;
        this.sendPacket(TradeDone.COMPLETED);
        if (successfull) {
            this.sendPacket(SystemMessageId.YOUR_TRADE_WAS_SUCCESSFUL);
        }
    }
    
    public void startTrade(final Player partner) {
        this.onTradeStart(partner);
        partner.onTradeStart(this);
    }
    
    public void cancelActiveTrade() {
        if (Objects.isNull(this.activeTradeList)) {
            return;
        }
        final Player partner = this.activeTradeList.getPartner();
        if (Objects.nonNull(partner)) {
            partner.onTradeCancel(this);
        }
        this.onTradeCancel(this);
    }
    
    public boolean hasManufactureShop() {
        return this._manufactureItems != null && !this._manufactureItems.isEmpty();
    }
    
    public Map<Integer, ManufactureItem> getManufactureItems() {
        if (this._manufactureItems == null) {
            synchronized (this) {
                if (this._manufactureItems == null) {
                    this._manufactureItems = Collections.synchronizedMap(new LinkedHashMap<Integer, ManufactureItem>());
                }
            }
        }
        return this._manufactureItems;
    }
    
    public String getStoreName() {
        return this._storeName;
    }
    
    public void setStoreName(final String name) {
        this._storeName = ((name == null) ? "" : name);
    }
    
    public TradeList getSellList() {
        if (this._sellList == null) {
            this._sellList = new TradeList(this);
        }
        return this._sellList;
    }
    
    public TradeList getBuyList() {
        if (this._buyList == null) {
            this._buyList = new TradeList(this);
        }
        return this._buyList;
    }
    
    public PrivateStoreType getPrivateStoreType() {
        return this.privateStoreType;
    }
    
    public void setPrivateStoreType(final PrivateStoreType privateStoreType) {
        this.privateStoreType = privateStoreType;
        if (Config.OFFLINE_DISCONNECT_FINISHED && privateStoreType == PrivateStoreType.NONE && (this._client == null || this._client.isDetached())) {
            Disconnection.of(this).storeMe().deleteMe();
        }
    }
    
    @Override
    public Clan getClan() {
        return this._clan;
    }
    
    public void setClan(final Clan clan) {
        this._clan = clan;
        if (clan == null) {
            this.setTitle("");
            this.clanId = 0;
            this._clanPrivileges = new EnumIntBitmask<ClanPrivilege>(ClanPrivilege.class, false);
            this.setPledgeType(0);
            this.setPowerGrade(0);
            this.setLvlJoinedAcademy(0);
            this.setApprentice(0);
            this.setSponsor(0);
            this.activeWarehouse = null;
            return;
        }
        if (!clan.isMember(this.getObjectId())) {
            this.setClan(null);
            return;
        }
        this.clanId = clan.getId();
    }
    
    public boolean isClanLeader() {
        return this._clan != null && this.getObjectId() == this._clan.getLeaderId();
    }
    
    public boolean disarmWeapons() {
        final Item wpn = this.inventory.getPaperdollItem(InventorySlot.RIGHT_HAND);
        if (wpn == null) {
            return true;
        }
        final Set<Item> modified = this.inventory.unEquipItemInBodySlotAndRecord(wpn.getBodyPart());
        final InventoryUpdate iu = new InventoryUpdate();
        for (final Item itm : modified) {
            iu.addModifiedItem(itm);
        }
        this.sendInventoryUpdate(iu);
        this.abortAttack();
        this.broadcastUserInfo();
        if (modified.size() > 0) {
            final Item unequipped = modified.iterator().next();
            SystemMessage sm;
            if (unequipped.getEnchantLevel() > 0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
            }
            sm.addItemName(unequipped);
            this.sendPacket(sm);
        }
        return true;
    }
    
    public boolean disarmShield() {
        final Item sld = this.inventory.getPaperdollItem(InventorySlot.LEFT_HAND);
        if (sld != null) {
            final Set<Item> modified = this.inventory.unEquipItemInBodySlotAndRecord(sld.getBodyPart());
            final InventoryUpdate iu = new InventoryUpdate();
            for (final Item itm : modified) {
                iu.addModifiedItem(itm);
            }
            this.sendInventoryUpdate(iu);
            this.abortAttack();
            this.broadcastUserInfo();
            final Iterator<Item> iterator = modified.iterator();
            if (iterator.hasNext()) {
                final Item unequipped = iterator.next();
                SystemMessage sm;
                if (unequipped.getEnchantLevel() > 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(unequipped.getEnchantLevel());
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED);
                }
                sm.addItemName(unequipped);
                this.sendPacket(sm);
            }
        }
        return true;
    }
    
    public boolean mount(final Summon pet) {
        if (!Config.ALLOW_MOUNTS_DURING_SIEGE && this.isInsideZone(ZoneType.SIEGE)) {
            return false;
        }
        if (!this.disarmWeapons() || !this.disarmShield() || this.isTransformed()) {
            return false;
        }
        this.getEffectList().stopAllToggles();
        this.setMount(pet.getId(), pet.getLevel());
        this.setMountObjectID(pet.getControlObjectId());
        this.clearPetData();
        this.startFeed(pet.getId());
        this.broadcastPacket(new Ride(this));
        this.broadcastUserInfo();
        pet.unSummon(this);
        return true;
    }
    
    public boolean mount(final int npcId, final int controlItemObjId, final boolean useFood) {
        if (!this.disarmWeapons() || !this.disarmShield() || this.isTransformed()) {
            return false;
        }
        this.getEffectList().stopAllToggles();
        this.setMount(npcId, this.getLevel());
        this.clearPetData();
        this.setMountObjectID(controlItemObjId);
        this.broadcastPacket(new Ride(this));
        this.broadcastUserInfo();
        if (useFood) {
            this.startFeed(npcId);
        }
        return true;
    }
    
    public boolean mountPlayer(final Summon pet) {
        if (pet != null && pet.isMountable() && !this.isMounted() && !this.isBetrayed()) {
            if (this.isDead()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHEN_DEAD);
                return false;
            }
            if (pet.isDead()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_DEAD_MOUNT_CANNOT_BE_RIDDEN);
                return false;
            }
            if (pet.isInCombat() || pet.isRooted()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_MOUNT_IN_BATTLE_CANNOT_BE_RIDDEN);
                return false;
            }
            if (this.isInCombat()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_MOUNT_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE);
                return false;
            }
            if (this._waitTypeSitting) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_MOUNT_CAN_BE_RIDDEN_ONLY_WHEN_STANDING);
                return false;
            }
            if (this.isFishing()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN);
                return false;
            }
            if (this.isTransformed()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
            if (this.inventory.getItemByItemId(9819) != null) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendMessage("You cannot mount a steed while holding a flag.");
                return false;
            }
            if (pet.isHungry()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
                return false;
            }
            if (!GameUtils.checkIfInRange(200, this, pet, true)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE);
                return false;
            }
            if (!pet.isDead() && !this.isMounted()) {
                this.mount(pet);
            }
        }
        else if (this.isRentedPet()) {
            this.stopRentPet();
        }
        else if (this.isMounted()) {
            if (this._mountType == MountType.WYVERN && this.isInsideZone(ZoneType.NO_LANDING)) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
                return false;
            }
            if (this.isHungry()) {
                this.sendPacket(ActionFailed.STATIC_PACKET);
                this.sendPacket(SystemMessageId.A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED);
                return false;
            }
            this.dismount();
        }
        return true;
    }
    
    public boolean dismount() {
        WaterZone water = null;
        for (final Zone zone : ZoneManager.getInstance().getZones(this.getX(), this.getY(), this.getZ() - 300)) {
            if (zone instanceof WaterZone) {
                water = (WaterZone)zone;
            }
        }
        if (water == null) {
            if (!this.isInWater() && this.getZ() > 10000) {
                this.sendPacket(SystemMessageId.YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION);
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
            if (GeoEngine.getInstance().getHeight(this.getX(), this.getY(), this.getZ()) + 300 < this.getZ()) {
                this.sendPacket(SystemMessageId.YOU_CANNOT_DISMOUNT_FROM_THIS_ELEVATION);
                this.sendPacket(ActionFailed.STATIC_PACKET);
                return false;
            }
        }
        else {
            ThreadPool.schedule(() -> {
                if (this.isInWater()) {
                    this.broadcastUserInfo();
                }
                return;
            }, 1500L);
        }
        final boolean wasFlying = this.isFlying();
        this.sendPacket(new SetupGauge(3, 0, 0));
        final int petId = this._mountNpcId;
        this.setMount(0, 0);
        this.stopFeed();
        this.clearPetData();
        if (wasFlying) {
            this.removeSkill(CommonSkill.WYVERN_BREATH.getSkill());
        }
        this.broadcastPacket(new Ride(this));
        this.setMountObjectID(0);
        this.storePetFood(petId);
        this.broadcastUserInfo();
        return true;
    }
    
    public long getUptime() {
        return System.currentTimeMillis() - this._uptime;
    }
    
    public void setUptime(final long time) {
        this._uptime = time;
    }
    
    @Override
    public boolean isInvul() {
        return super.isInvul() || this.isTeleportProtected();
    }
    
    @Override
    public boolean isInParty() {
        return this._party != null;
    }
    
    public void joinParty(final Party party) {
        if (party != null) {
            (this._party = party).addPartyMember(this);
        }
    }
    
    public void leaveParty() {
        if (this.isInParty()) {
            this._party.removePartyMember(this, Party.MessageType.DISCONNECTED);
            this._party = null;
        }
    }
    
    @Override
    public Party getParty() {
        return this._party;
    }
    
    public void setParty(final Party party) {
        this._party = party;
    }
    
    public boolean isInCommandChannel() {
        return this.isInParty() && this._party.isInCommandChannel();
    }
    
    public CommandChannel getCommandChannel() {
        return this.isInCommandChannel() ? this._party.getCommandChannel() : null;
    }
    
    @Override
    public boolean isGM() {
        return this.accessLevel.isGm();
    }
    
    public void setAccountAccesslevel(final int level) {
        AuthServerCommunication.getInstance().sendPacket(new ChangeAccessLevel(this.getAccountName(), level, 0));
    }
    
    @Override
    public AccessLevel getAccessLevel() {
        return this.accessLevel;
    }
    
    public void updateAndBroadcastStatus(final int broadcastType) {
        this.refreshOverloaded(true);
        if (broadcastType == 1) {
            this.sendPacket(new UserInfo(this));
        }
        if (broadcastType == 2) {
            this.broadcastUserInfo();
        }
    }
    
    public void broadcastReputation() {
        this.broadcastUserInfo(UserInfoType.SOCIAL);
        int relation;
        Integer oldrelation;
        RelationChanged rc;
        World.getInstance().forEachVisibleObject(this, Player.class, player -> {
            if (!(!this.isVisibleFor(player))) {
                relation = this.getRelation(player);
                oldrelation = this.getKnownRelations().get(player.getObjectId());
                if (oldrelation == null || oldrelation != relation) {
                    rc = new RelationChanged();
                    rc.addRelation(this, relation, !this.isInsideZone(ZoneType.PEACE));
                    if (this.hasSummon()) {
                        if (this.pet != null) {
                            rc.addRelation(this.pet, relation, !this.isInsideZone(ZoneType.PEACE));
                        }
                        if (this.hasServitors()) {
                            this.getServitors().values().forEach(s -> rc.addRelation(s, relation, !this.isInsideZone(ZoneType.PEACE)));
                        }
                    }
                    player.sendPacket(rc);
                    this.getKnownRelations().put(player.getObjectId(), relation);
                }
            }
        });
    }
    
    public void setOnlineStatus(final boolean isOnline, final boolean updateInDb) {
        if (this._isOnline != isOnline) {
            this._isOnline = isOnline;
        }
        if (updateInDb) {
            this.updateOnlineStatus();
        }
    }
    
    public void updateOnlineStatus() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE charId=?");
                try {
                    statement.setInt(1, this.isOnlineInt());
                    statement.setLong(2, System.currentTimeMillis());
                    statement.setInt(3, this.getObjectId());
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("Failed updating character online status.", (Throwable)e);
        }
    }
    
    private boolean createDb() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO characters (account_name,charId,char_name,level,maxHp,curHp,maxCp,curCp,maxMp,curMp,face,hairStyle,hairColor,sex,exp,sp,reputation,fame,raidbossPoints,pvpkills,pkkills,clanid,race,classid,cancraft,title,title_color,online,clan_privs,wantspeace,base_class,nobless,power_grade,vitality_points,createDate) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                try {
                    statement.setString(1, this.data.getAccountName());
                    statement.setInt(2, this.getObjectId());
                    statement.setString(3, this.getName());
                    statement.setInt(4, this.getLevel());
                    statement.setInt(5, this.getMaxHp());
                    statement.setDouble(6, this.getCurrentHp());
                    statement.setInt(7, this.getMaxCp());
                    statement.setDouble(8, this.getCurrentCp());
                    statement.setInt(9, this.getMaxMp());
                    statement.setDouble(10, this.getCurrentMp());
                    statement.setInt(11, this.appearance.getFace());
                    statement.setInt(12, this.appearance.getHairStyle());
                    statement.setInt(13, this.appearance.getHairColor());
                    statement.setInt(14, this.appearance.isFemale() ? 1 : 0);
                    statement.setLong(15, this.getExp());
                    statement.setLong(16, this.getSp());
                    statement.setInt(17, this.getReputation());
                    statement.setInt(18, this._fame);
                    statement.setInt(19, this.getRaidbossPoints());
                    statement.setInt(20, this._pvpKills);
                    statement.setInt(21, this._pkKills);
                    statement.setInt(22, this.clanId);
                    statement.setInt(23, this.getRace().ordinal());
                    statement.setInt(24, this.data.getClassId());
                    statement.setInt(25, this.hasDwarvenCraft() ? 1 : 0);
                    statement.setString(26, this.getTitle());
                    statement.setInt(27, this.appearance.getTitleColor());
                    statement.setInt(28, this.isOnlineInt());
                    statement.setInt(29, this._clanPrivileges.getBitmask());
                    statement.setBoolean(30, this.wantsPeace());
                    statement.setInt(31, this.data.getBaseClass());
                    statement.setInt(32, this.isNoble() ? 1 : 0);
                    statement.setLong(33, 0L);
                    statement.setInt(34, 0);
                    statement.setObject(35, this.data.getCreateDate());
                    statement.executeUpdate();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            return false;
        }
        return true;
    }
    
    void restoreCharData() {
        this.restoreSkills();
        this.macros.restoreMe();
        this.restoreHenna();
        this.restoreTeleportBookmark();
        this.restoreRecipeBook(true);
        if (Config.STORE_RECIPE_SHOPLIST) {
            this.restoreRecipeShopList();
        }
        this.loadPremiumItemList();
        this.restorePetInventoryItems();
    }
    
    void restoreShortCuts() {
        this.shortcuts.restoreMe();
    }
    
    private void restoreRecipeBook(final boolean loadCommon) {
        final String sql = loadCommon ? "SELECT id, type, classIndex FROM character_recipebook WHERE charId=?" : "SELECT id FROM character_recipebook WHERE charId=? AND classIndex=? AND type = 1";
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement(sql);
                try {
                    statement.setInt(1, this.getObjectId());
                    if (!loadCommon) {
                        statement.setInt(2, this._classIndex);
                    }
                    final ResultSet rset = statement.executeQuery();
                    try {
                        this._dwarvenRecipeBook.clear();
                        final RecipeData rd = RecipeData.getInstance();
                        while (rset.next()) {
                            final RecipeList recipe = rd.getRecipeList(rset.getInt("id"));
                            if (loadCommon) {
                                if (rset.getInt(2) == 1) {
                                    if (rset.getInt(3) != this._classIndex) {
                                        continue;
                                    }
                                    this.registerDwarvenRecipeList(recipe, false);
                                }
                                else {
                                    this.registerCommonRecipeList(recipe, false);
                                }
                            }
                            else {
                                this.registerDwarvenRecipeList(recipe, false);
                            }
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public Map<Integer, PremiumItem> getPremiumItemList() {
        return this._premiumItems;
    }
    
    private void loadPremiumItemList() {
        final String sql = "SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?";
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT itemNum, itemId, itemCount, itemSender FROM character_premium_items WHERE charId=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            final int itemNum = rset.getInt("itemNum");
                            final int itemId = rset.getInt("itemId");
                            final long itemCount = rset.getLong("itemCount");
                            final String itemSender = rset.getString("itemSender");
                            this._premiumItems.put(itemNum, new PremiumItem(itemId, itemCount, itemSender));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void updatePremiumItem(final int itemNum, final long newcount) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE character_premium_items SET itemCount=? WHERE charId=? AND itemNum=? ");
                try {
                    statement.setLong(1, newcount);
                    statement.setInt(2, this.getObjectId());
                    statement.setInt(3, itemNum);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    public void deletePremiumItem(final int itemNum) {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_premium_items WHERE charId=? AND itemNum=? ");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, itemNum);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/Exception;)Ljava/lang/String;, e));
        }
    }
    
    public synchronized void store(final boolean storeActiveEffects) {
        this.storeCharBase();
        this.storeCharSub();
        this.storeEffect(storeActiveEffects);
        this.storeItemReuseDelay();
        if (Config.STORE_RECIPE_SHOPLIST) {
            this.storeRecipeShopList();
        }
        final AccountVariables aVars = this.getScript(AccountVariables.class);
        if (aVars != null) {
            aVars.storeMe();
        }
        if (Objects.nonNull(this.spirits)) {
            for (final ElementalSpirit spirit : this.spirits) {
                if (Objects.nonNull(spirit)) {
                    spirit.save();
                }
            }
            if (Objects.nonNull(this.activeElementalSpiritType)) {
                ((ElementalSpiritDAO)DatabaseAccess.getDAO((Class)ElementalSpiritDAO.class)).updateActiveSpirit(this.getObjectId(), this.activeElementalSpiritType.getId());
            }
        }
        this.shortcuts.storeMe();
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).save((Object)this.variables);
        final PlayerDAO playerDAO = (PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class);
        playerDAO.save(this.statsData);
        if (!this.costumes.isEmpty()) {
            playerDAO.save(this.costumes.values());
        }
        if (CostumeCollectionData.DEFAULT.equals(this.activeCostumesCollection)) {
            playerDAO.deleteCostumeCollection(this.objectId);
        }
        else {
            playerDAO.save(this.activeCostumesCollection);
        }
        playerDAO.removeTeleportFavorites(this.objectId);
        if (Util.isNotEmpty((IntCollection)this.teleportFavorites)) {
            playerDAO.saveTeleportFavorites(this.objectId, this.teleportFavorites);
        }
        this.storeRecommendations();
        if (Config.UPDATE_ITEMS_ON_CHAR_STORE) {
            this.inventory.updateDatabase();
            this.getWarehouse().updateDatabase();
        }
    }
    
    @Override
    public void storeMe() {
        this.store(true);
    }
    
    private void storeCharBase() {
        final long exp = this.getStats().getBaseExp();
        final int level = this.getStats().getBaseLevel();
        final long sp = this.getStats().getBaseSp();
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE characters SET level=?,maxHp=?,curHp=?,maxCp=?,curCp=?,maxMp=?,curMp=?,face=?,hairStyle=?,hairColor=?,sex=?,heading=?,x=?,y=?,z=?,exp=?,expBeforeDeath=?,sp=?,reputation=?,fame=?,raidbossPoints=?,pvpkills=?,pkkills=?,clanid=?,race=?,classid=?,title=?,title_color=?,online=?,clan_privs=?,wantspeace=?,base_class=?,onlinetime=?,nobless=?,power_grade=?,subpledge=?,lvl_joined_academy=?,apprentice=?,sponsor=?,clan_join_expiry_time=?,clan_create_expiry_time=?,char_name=?,bookmarkslot=?,vitality_points=?,language=?,pccafe_points=? WHERE charId=?");
                try {
                    statement.setInt(1, level);
                    statement.setInt(2, this.getMaxHp());
                    statement.setDouble(3, this.getCurrentHp());
                    statement.setInt(4, this.getMaxCp());
                    statement.setDouble(5, this.getCurrentCp());
                    statement.setInt(6, this.getMaxMp());
                    statement.setDouble(7, this.getCurrentMp());
                    statement.setInt(8, this.appearance.getFace());
                    statement.setInt(9, this.appearance.getHairStyle());
                    statement.setInt(10, this.appearance.getHairColor());
                    statement.setInt(11, this.appearance.isFemale() ? 1 : 0);
                    statement.setInt(12, this.getHeading());
                    statement.setInt(13, (this._lastLoc != null) ? this._lastLoc.getX() : this.getX());
                    statement.setInt(14, (this._lastLoc != null) ? this._lastLoc.getY() : this.getY());
                    statement.setInt(15, (this._lastLoc != null) ? this._lastLoc.getZ() : this.getZ());
                    statement.setLong(16, exp);
                    statement.setLong(17, this.data.getExpBeforeDeath());
                    statement.setLong(18, sp);
                    statement.setInt(19, this.getReputation());
                    statement.setInt(20, this._fame);
                    statement.setInt(21, this.getRaidbossPoints());
                    statement.setInt(22, this._pvpKills);
                    statement.setInt(23, this._pkKills);
                    statement.setInt(24, this.clanId);
                    statement.setInt(25, this.getRace().ordinal());
                    statement.setInt(26, this.getClassId().getId());
                    statement.setString(27, this.getTitle());
                    statement.setInt(28, this.appearance.getTitleColor());
                    statement.setInt(29, this.isOnlineInt());
                    statement.setInt(30, this._clanPrivileges.getBitmask());
                    statement.setBoolean(31, this.wantsPeace());
                    statement.setInt(32, this.data.getBaseClass());
                    long totalOnlineTime = this._onlineTime;
                    if (this._onlineBeginTime > 0L) {
                        totalOnlineTime += (System.currentTimeMillis() - this._onlineBeginTime) / 1000L;
                    }
                    statement.setLong(33, totalOnlineTime);
                    statement.setInt(34, this.isNoble() ? 1 : 0);
                    statement.setInt(35, this.getPowerGrade());
                    statement.setInt(36, this.getPledgeType());
                    statement.setInt(37, this.getLvlJoinedAcademy());
                    statement.setLong(38, this.getApprentice());
                    statement.setLong(39, this.getSponsor());
                    statement.setLong(40, this.getClanJoinExpiryTime());
                    statement.setLong(41, this.getClanJoinExpiryTime());
                    statement.setString(42, this.getName());
                    statement.setInt(43, this._bookmarkslot);
                    statement.setInt(44, this.getStats().getBaseVitalityPoints());
                    statement.setString(45, this._lang);
                    statement.setInt(46, this.getPcCafePoints());
                    statement.setInt(47, this.getObjectId());
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    private void storeCharSub() {
        if (this.getTotalSubClasses() <= 0) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("UPDATE character_subclasses SET exp=?,sp=?,level=?,vitality_points=?,class_id=?,dual_class=? WHERE charId=? AND class_index =?");
                try {
                    for (final SubClass subClass : this.getSubClasses().values()) {
                        statement.setLong(1, subClass.getExp());
                        statement.setLong(2, subClass.getSp());
                        statement.setInt(3, subClass.getLevel());
                        statement.setInt(4, subClass.getVitalityPoints());
                        statement.setInt(5, subClass.getClassId());
                        statement.setBoolean(6, subClass.isDualClass());
                        statement.setInt(7, this.getObjectId());
                        statement.setInt(8, subClass.getClassIndex());
                        statement.addBatch();
                    }
                    statement.executeBatch();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getName(), e.getMessage()), (Throwable)e);
        }
    }
    
    @Override
    public void storeEffect(final boolean storeEffects) {
        if (!Config.STORE_SKILL_COOLTIME) {
            return;
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement delete = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=? AND class_index=?");
                try {
                    delete.setInt(1, this.getObjectId());
                    delete.setInt(2, this._classIndex);
                    delete.execute();
                    if (delete != null) {
                        delete.close();
                    }
                }
                catch (Throwable t3) {
                    if (delete != null) {
                        try {
                            delete.close();
                        }
                        catch (Throwable exception) {
                            t3.addSuppressed(exception);
                        }
                    }
                    throw t3;
                }
                int buff_index = 0;
                final List<Long> storedSkills = new ArrayList<Long>();
                final long currentTime = System.currentTimeMillis();
                final PreparedStatement statement = con.prepareStatement("INSERT INTO character_skills_save (charId,skill_id,skill_level,skill_sub_level,remaining_time,reuse_delay,systime,restore_type,class_index,buff_index) VALUES (?,?,?,?,?,?,?,?,?,?)");
                try {
                    if (storeEffects) {
                        for (final BuffInfo info : this.getEffectList().getEffects()) {
                            if (info == null) {
                                continue;
                            }
                            final Skill skill = info.getSkill();
                            if (skill.isDeleteAbnormalOnLeave()) {
                                continue;
                            }
                            if (skill.getAbnormalType() == AbnormalType.LIFE_FORCE_OTHERS) {
                                continue;
                            }
                            if (skill.isToggle()) {
                                continue;
                            }
                            if (skill.isDance() && !Config.ALT_STORE_DANCES) {
                                continue;
                            }
                            if (storedSkills.contains(skill.getReuseHashCode())) {
                                continue;
                            }
                            storedSkills.add(skill.getReuseHashCode());
                            statement.setInt(1, this.getObjectId());
                            statement.setInt(2, skill.getId());
                            statement.setInt(3, skill.getLevel());
                            statement.setInt(4, skill.getSubLevel());
                            statement.setInt(5, info.getTime());
                            final TimeStamp t = this.getSkillReuseTimeStamp(skill.getReuseHashCode());
                            statement.setLong(6, (t != null && currentTime < t.getStamp()) ? t.getReuse() : 0L);
                            statement.setDouble(7, (t != null && currentTime < t.getStamp()) ? ((double)t.getStamp()) : 0.0);
                            statement.setInt(8, 0);
                            statement.setInt(9, this._classIndex);
                            statement.setInt(10, ++buff_index);
                            statement.addBatch();
                        }
                        statement.executeBatch();
                    }
                    for (final Map.Entry<Long, TimeStamp> ts : this.getSkillReuseTimeStamps().entrySet()) {
                        final long hash = ts.getKey();
                        if (storedSkills.contains(hash)) {
                            continue;
                        }
                        final TimeStamp t2 = ts.getValue();
                        if (t2 == null || currentTime >= t2.getStamp()) {
                            continue;
                        }
                        storedSkills.add(hash);
                        statement.setInt(1, this.getObjectId());
                        statement.setInt(2, t2.getSkillId());
                        statement.setInt(3, t2.getSkillLvl());
                        statement.setInt(4, t2.getSkillSubLvl());
                        statement.setInt(5, -1);
                        statement.setLong(6, t2.getReuse());
                        statement.setDouble(7, (double)t2.getStamp());
                        statement.setInt(8, 1);
                        statement.setInt(9, this._classIndex);
                        statement.setInt(10, ++buff_index);
                        statement.addBatch();
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t4) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t4.addSuppressed(exception2);
                        }
                    }
                    throw t4;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t5) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t5.addSuppressed(exception3);
                    }
                }
                throw t5;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn("Could not store char effect data: ", (Throwable)e);
        }
    }
    
    private void storeItemReuseDelay() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps1 = con.prepareStatement("DELETE FROM character_item_reuse_save WHERE charId=?");
                try {
                    final PreparedStatement ps2 = con.prepareStatement("INSERT INTO character_item_reuse_save (charId,itemId,itemObjId,reuseDelay,systime) VALUES (?,?,?,?,?)");
                    try {
                        ps1.setInt(1, this.getObjectId());
                        ps1.execute();
                        final long currentTime = System.currentTimeMillis();
                        for (final TimeStamp ts : this.getItemReuseTimeStamps().values()) {
                            if (ts != null && currentTime < ts.getStamp()) {
                                ps2.setInt(1, this.getObjectId());
                                ps2.setInt(2, ts.getItemId());
                                ps2.setInt(3, ts.getItemObjectId());
                                ps2.setLong(4, ts.getReuse());
                                ps2.setDouble(5, (double)ts.getStamp());
                                ps2.addBatch();
                            }
                        }
                        ps2.executeBatch();
                        if (ps2 != null) {
                            ps2.close();
                        }
                    }
                    catch (Throwable t) {
                        if (ps2 != null) {
                            try {
                                ps2.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (ps1 != null) {
                        ps1.close();
                    }
                }
                catch (Throwable t2) {
                    if (ps1 != null) {
                        try {
                            ps1.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn("Could not store char item reuse data: ", (Throwable)e);
        }
    }
    
    public boolean isOnline() {
        return this._isOnline;
    }
    
    public int isOnlineInt() {
        if (this._isOnline && this._client != null) {
            return this._client.isDetached() ? 2 : 1;
        }
        return 0;
    }
    
    public boolean isInOfflineMode() {
        return this._client == null || this._client.isDetached();
    }
    
    @Override
    public Skill addSkill(final Skill newSkill) {
        this.addCustomSkill(newSkill);
        if (Objects.nonNull(newSkill)) {
            this.sendSkillList();
        }
        return super.addSkill(newSkill);
    }
    
    public Skill addSkill(final Skill newSkill, final boolean store) {
        final Skill oldSkill = this.addSkill(newSkill);
        if (store) {
            this.storeSkill(newSkill, oldSkill, -1);
        }
        return oldSkill;
    }
    
    @Override
    public Skill removeSkill(final Skill skill, final boolean store) {
        this.removeCustomSkill(skill);
        if (Objects.nonNull(skill)) {
            this.sendSkillList();
        }
        return store ? this.removeSkill(skill) : super.removeSkill(skill, true);
    }
    
    public Skill removeSkill(final Skill skill, final boolean store, final boolean cancelEffect) {
        this.removeCustomSkill(skill);
        if (Objects.nonNull(skill)) {
            this.sendSkillList();
        }
        return store ? this.removeSkill(skill) : super.removeSkill(skill, cancelEffect);
    }
    
    public Skill removeSkill(final Skill skill) {
        this.removeCustomSkill(skill);
        final Skill oldSkill = super.removeSkill(skill, true);
        if (oldSkill != null) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND charId=? AND class_index=?");
                    try {
                        statement.setInt(1, oldSkill.getId());
                        statement.setInt(2, this.getObjectId());
                        statement.setInt(3, this._classIndex);
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
        }
        if (this.getTransformationId() > 0) {
            return oldSkill;
        }
        if (Objects.nonNull(skill) && (skill.getId() < 3080 || skill.getId() > 3259)) {
            this.deleteShortcuts(s -> s.getShortcutId() == skill.getId() && s.getType() == ShortcutType.SKILL);
        }
        return oldSkill;
    }
    
    private void storeSkill(final Skill newSkill, final Skill oldSkill, final int newClassIndex) {
        final int classIndex = (newClassIndex > -1) ? newClassIndex : this._classIndex;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                if (oldSkill != null && newSkill != null) {
                    final PreparedStatement ps = con.prepareStatement("UPDATE character_skills SET skill_level=?, skill_sub_level=?  WHERE skill_id=? AND charId=? AND class_index=?");
                    try {
                        ps.setInt(1, newSkill.getLevel());
                        ps.setInt(2, newSkill.getSubLevel());
                        ps.setInt(3, oldSkill.getId());
                        ps.setInt(4, this.getObjectId());
                        ps.setInt(5, classIndex);
                        ps.execute();
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (Throwable t) {
                        if (ps != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                }
                else if (newSkill != null) {
                    final PreparedStatement ps = con.prepareStatement("REPLACE INTO character_skills (charId,skill_id,skill_level,skill_sub_level,class_index) VALUES (?,?,?,?,?)");
                    try {
                        ps.setInt(1, this.getObjectId());
                        ps.setInt(2, newSkill.getId());
                        ps.setInt(3, newSkill.getLevel());
                        ps.setInt(4, newSkill.getSubLevel());
                        ps.setInt(5, classIndex);
                        ps.execute();
                        if (ps != null) {
                            ps.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (ps != null) {
                            try {
                                ps.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    private void storeSkills(final List<Skill> newSkills, final int newClassIndex) {
        if (newSkills.isEmpty()) {
            return;
        }
        final int classIndex = (newClassIndex > -1) ? newClassIndex : this._classIndex;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("REPLACE INTO character_skills (charId,skill_id,skill_level,skill_sub_level,class_index) VALUES (?,?,?,?,?)");
                try {
                    for (final Skill addSkill : newSkills) {
                        ps.setInt(1, this.getObjectId());
                        ps.setInt(2, addSkill.getId());
                        ps.setInt(3, addSkill.getLevel());
                        ps.setInt(4, addSkill.getSubLevel());
                        ps.setInt(5, classIndex);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (SQLException e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
    }
    
    private void restoreSkills() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level,skill_sub_level FROM character_skills WHERE charId=? AND class_index=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, this._classIndex);
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            final int id = rset.getInt("skill_id");
                            final int level = rset.getInt("skill_level");
                            final Skill skill = SkillEngine.getInstance().getSkill(id, level);
                            if (skill == null) {
                                Player.LOGGER.warn("Skipped null skill id: {} level: {} while restoring player skills for player: {}", new Object[] { id, level, this });
                            }
                            else {
                                this.addSkill(skill);
                                if (!Config.SKILL_CHECK_ENABLE || (this.canOverrideCond(PcCondOverride.SKILL_CONDITIONS) && !Config.SKILL_CHECK_GM) || SkillTreesData.getInstance().isSkillAllowed(this, skill)) {
                                    continue;
                                }
                                GameUtils.handleIllegalPlayerAction(this, invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;)Ljava/lang/String;, this.getName(), skill.getName(), skill.getId(), skill.getLevel(), ClassListData.getInstance().getClass(this.getClassId()).getClassName()), IllegalActionPunishmentType.BROADCAST);
                                if (!Config.SKILL_CHECK_REMOVE) {
                                    continue;
                                }
                                this.removeSkill(skill);
                            }
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    @Override
    public void restoreEffects() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT skill_id,skill_level,skill_sub_level,remaining_time, reuse_delay, systime, restore_type FROM character_skills_save WHERE charId=? AND class_index=? ORDER BY buff_index ASC");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, this._classIndex);
                    final ResultSet rset = statement.executeQuery();
                    try {
                        final long currentTime = System.currentTimeMillis();
                        while (rset.next()) {
                            final int remainingTime = rset.getInt("remaining_time");
                            final long reuseDelay = rset.getLong("reuse_delay");
                            final long systime = rset.getLong("systime");
                            final int restoreType = rset.getInt("restore_type");
                            final Skill skill = SkillEngine.getInstance().getSkill(rset.getInt("skill_id"), rset.getInt("skill_level"));
                            if (skill == null) {
                                continue;
                            }
                            final long time = systime - currentTime;
                            if (time > 10L) {
                                this.disableSkill(skill, time);
                                this.addTimeStamp(skill, reuseDelay, systime);
                            }
                            if (restoreType > 0) {
                                continue;
                            }
                            skill.applyEffects(this, this, false, remainingTime);
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    final PreparedStatement delete = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=? AND class_index=?");
                    try {
                        delete.setInt(1, this.getObjectId());
                        delete.setInt(2, this._classIndex);
                        delete.executeUpdate();
                        if (delete != null) {
                            delete.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (delete != null) {
                            try {
                                delete.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t3) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t4) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception4) {
                        t4.addSuppressed(exception4);
                    }
                }
                throw t4;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    void restoreItemReuse() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT charId,itemId,itemObjId,reuseDelay,systime FROM character_item_reuse_save WHERE charId=?");
                try {
                    final PreparedStatement delete = con.prepareStatement("DELETE FROM character_item_reuse_save WHERE charId=?");
                    try {
                        statement.setInt(1, this.getObjectId());
                        final ResultSet rset = statement.executeQuery();
                        try {
                            final long currentTime = System.currentTimeMillis();
                            while (rset.next()) {
                                final int itemId = rset.getInt("itemId");
                                final long reuseDelay = rset.getLong("reuseDelay");
                                final long systime = rset.getLong("systime");
                                boolean isInInventory = true;
                                Item item = this.inventory.getItemByItemId(itemId);
                                if (item == null) {
                                    item = this.getWarehouse().getItemByItemId(itemId);
                                    isInInventory = false;
                                }
                                if (item != null && item.getId() == itemId && item.getReuseDelay() > 0) {
                                    final long remainingTime = systime - currentTime;
                                    if (remainingTime <= 10L) {
                                        continue;
                                    }
                                    this.addTimeStampItem(item, reuseDelay, systime);
                                    if (!isInInventory || !item.isEtcItem()) {
                                        continue;
                                    }
                                    final int group = item.getSharedReuseGroup();
                                    if (group <= 0) {
                                        continue;
                                    }
                                    this.sendPacket(new ExUseSharedGroupItem(itemId, group, (int)remainingTime, (int)reuseDelay));
                                }
                            }
                            if (rset != null) {
                                rset.close();
                            }
                        }
                        catch (Throwable t) {
                            if (rset != null) {
                                try {
                                    rset.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                        delete.setInt(1, this.getObjectId());
                        delete.executeUpdate();
                        if (delete != null) {
                            delete.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (delete != null) {
                            try {
                                delete.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t3) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t4) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception4) {
                        t4.addSuppressed(exception4);
                    }
                }
                throw t4;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;Ljava/lang/String;)Ljava/lang/String;, this, e.getMessage()), (Throwable)e);
        }
    }
    
    private void restoreHenna() {
        for (int i = 1; i < 4; ++i) {
            this._henna[i - 1] = null;
        }
        for (final Map.Entry<Integer, ScheduledFuture<?>> entry : this._hennaRemoveSchedules.entrySet()) {
            final ScheduledFuture<?> task = entry.getValue();
            if (task != null && !task.isCancelled() && !task.isDone()) {
                task.cancel(true);
            }
            this._hennaRemoveSchedules.remove(entry.getKey());
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT slot,symbol_id FROM character_hennas WHERE charId=? AND class_index=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, this._classIndex);
                    final ResultSet rset = statement.executeQuery();
                    try {
                        final long currentTime = System.currentTimeMillis();
                        while (rset.next()) {
                            final int slot = rset.getInt("slot");
                            if (slot >= 1) {
                                if (slot > 3) {
                                    continue;
                                }
                                final int symbolId = rset.getInt("symbol_id");
                                if (symbolId == 0) {
                                    continue;
                                }
                                final Henna henna = HennaData.getInstance().getHenna(symbolId);
                                if (henna.getDuration() > 0) {
                                    final long remainingTime = this.getHennaDuration(slot) - currentTime;
                                    if (remainingTime < 0L) {
                                        this.removeHenna(slot);
                                        continue;
                                    }
                                    this._hennaRemoveSchedules.put(slot, ThreadPool.schedule((Runnable)new HennaDurationTask(this, slot), currentTime + remainingTime));
                                }
                                this._henna[slot - 1] = henna;
                                for (final Skill skill : henna.getSkills()) {
                                    this.addSkill(skill, false);
                                }
                            }
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this), (Throwable)e);
        }
        this.recalcHennaStats();
    }
    
    public int getHennaEmptySlots() {
        int totalSlots = 0;
        if (this.getClassId().level() == 1) {
            totalSlots = 2;
        }
        else if (this.getClassId().level() > 1) {
            totalSlots = 3;
        }
        for (int i = 0; i < 3; ++i) {
            if (this._henna[i] != null) {
                --totalSlots;
            }
        }
        if (totalSlots <= 0) {
            return 0;
        }
        return totalSlots;
    }
    
    public boolean removeHenna(final int slot) {
        if (slot < 1 || slot > 3) {
            return false;
        }
        final Henna henna = this._henna[slot - 1];
        if (henna == null) {
            return false;
        }
        this._henna[slot - 1] = null;
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("DELETE FROM character_hennas WHERE charId=? AND slot=? AND class_index=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, slot);
                    statement.setInt(3, this._classIndex);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("Failed removing character henna.", (Throwable)e);
        }
        this.recalcHennaStats();
        this.sendPacket(new HennaInfo(this));
        final UserInfo ui = new UserInfo(this, false);
        ui.addComponentType(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
        this.sendPacket(ui);
        final long remainingTime = this.getHennaDuration(slot) - System.currentTimeMillis();
        if (henna.getDuration() < 0 || remainingTime > 0L) {
            if (henna.getCancelFee() > 0) {
                this.reduceAdena("Henna", henna.getCancelFee(), this, false);
            }
            if (henna.getCancelCount() > 0) {
                this.inventory.addItem("Henna", henna.getDyeItemId(), henna.getCancelCount(), this, null);
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
                sm.addItemName(henna.getDyeItemId());
                sm.addLong(henna.getCancelCount());
                this.sendPacket(sm);
            }
        }
        this.sendPacket(SystemMessageId.THE_SYMBOL_HAS_BEEN_DELETED);
        if (henna.getDuration() > 0) {
            this.setHennaDuration(0L, slot);
            if (this._hennaRemoveSchedules.get(slot) != null) {
                this._hennaRemoveSchedules.get(slot).cancel(false);
                this._hennaRemoveSchedules.remove(slot);
            }
        }
        for (final Skill skill : henna.getSkills()) {
            this.removeSkill(skill, false);
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaRemove(this, henna), this);
        return true;
    }
    
    public boolean addHenna(final Henna henna) {
        for (int i = 1; i < 4; ++i) {
            if (this._henna[i - 1] == null) {
                this._henna[i - 1] = henna;
                this.recalcHennaStats();
                try {
                    final Connection con = DatabaseFactory.getInstance().getConnection();
                    try {
                        final PreparedStatement statement = con.prepareStatement("INSERT INTO character_hennas (charId,symbol_id,slot,class_index) VALUES (?,?,?,?)");
                        try {
                            statement.setInt(1, this.getObjectId());
                            statement.setInt(2, henna.getDyeId());
                            statement.setInt(3, i);
                            statement.setInt(4, this._classIndex);
                            statement.execute();
                            if (statement != null) {
                                statement.close();
                            }
                        }
                        catch (Throwable t) {
                            if (statement != null) {
                                try {
                                    statement.close();
                                }
                                catch (Throwable exception) {
                                    t.addSuppressed(exception);
                                }
                            }
                            throw t;
                        }
                        if (con != null) {
                            con.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (con != null) {
                            try {
                                con.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                }
                catch (Exception e) {
                    Player.LOGGER.error("Failed saving character henna.", (Throwable)e);
                }
                if (henna.getDuration() > 0) {
                    this.setHennaDuration(System.currentTimeMillis() + henna.getDuration() * 60000, i);
                    this._hennaRemoveSchedules.put(i, ThreadPool.schedule((Runnable)new HennaDurationTask(this, i), System.currentTimeMillis() + henna.getDuration() * 60000));
                }
                for (final Skill skill : henna.getSkills()) {
                    this.addSkill(skill, false);
                }
                this.sendPacket(new HennaInfo(this));
                final UserInfo ui = new UserInfo(this, false);
                ui.addComponentType(UserInfoType.BASE_STATS, UserInfoType.MAX_HPCPMP, UserInfoType.STATS, UserInfoType.SPEED);
                this.sendPacket(ui);
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerHennaAdd(this, henna), this);
                return true;
            }
        }
        return false;
    }
    
    private void recalcHennaStats() {
        this._hennaBaseStats.clear();
        for (final Henna henna : this._henna) {
            if (henna != null) {
                for (final Map.Entry<BaseStats, Integer> entry : henna.getBaseStats().entrySet()) {
                    this._hennaBaseStats.merge(entry.getKey(), entry.getValue(), Integer::sum);
                }
            }
        }
    }
    
    public Henna getHenna(final int slot) {
        if (slot < 1 || slot > 3) {
            return null;
        }
        return this._henna[slot - 1];
    }
    
    public boolean hasHennas() {
        for (final Henna henna : this._henna) {
            if (henna != null) {
                return true;
            }
        }
        return false;
    }
    
    public Henna[] getHennaList() {
        return this._henna;
    }
    
    public int getHennaValue(final BaseStats stat) {
        return this._hennaBaseStats.getOrDefault(stat, 0);
    }
    
    public Map<BaseStats, Integer> getHennaBaseStats() {
        return this._hennaBaseStats;
    }
    
    @Override
    public boolean hasBasicPropertyResist() {
        return false;
    }
    
    public boolean canLogout() {
        if (this.hasItemRequest() || this.hasRequest(CaptchaRequest.class, (Class<? extends AbstractRequest>[])new Class[0])) {
            return false;
        }
        if (this.isSubClassLocked()) {
            Player.LOGGER.warn("Player {} tried to restart/logout during class change.", (Object)this.getName());
            return false;
        }
        return (!AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) || (this.isGM() && Config.GM_RESTART_FIGHTING)) && !this.isBlockedFromExit();
    }
    
    @Override
    public boolean isAutoAttackable(final Creature attacker) {
        if (Objects.isNull(attacker)) {
            return false;
        }
        if (attacker == this || attacker == this.pet || attacker.hasServitor(attacker.getObjectId())) {
            return false;
        }
        if (attacker instanceof FriendlyMob) {
            return false;
        }
        if (GameUtils.isMonster(attacker)) {
            return true;
        }
        if (GameUtils.isPlayable(attacker) && this._duelState == 1 && this.getDuelId() == attacker.getActingPlayer().getDuelId()) {
            return true;
        }
        if (this.isInParty() && this._party.getMembers().contains(attacker)) {
            return false;
        }
        if (GameUtils.isPlayer(attacker) && attacker.getActingPlayer().isInOlympiadMode()) {
            return this._inOlympiadMode && this._OlympiadStart && ((Player)attacker).getOlympiadGameId() == this.getOlympiadGameId();
        }
        if (this._isOnCustomEvent && this.getTeam() == attacker.getTeam()) {
            return false;
        }
        if (this.isOnEvent()) {
            return true;
        }
        if (GameUtils.isPlayable(attacker)) {
            if (this.isInsideZone(ZoneType.PEACE)) {
                return false;
            }
            final Player attackerPlayer = attacker.getActingPlayer();
            final Clan clan = this.getClan();
            final Clan attackerClan = attackerPlayer.getClan();
            if (clan != null) {
                final Siege siege = SiegeManager.getInstance().getSiege(this);
                if (siege != null) {
                    if (siege.checkIsDefender(attackerClan) && siege.checkIsDefender(clan)) {
                        return false;
                    }
                    if (siege.checkIsAttacker(attackerClan) && siege.checkIsAttacker(clan)) {
                        return false;
                    }
                }
                if (attackerClan != null && !this.wantsPeace() && !attackerPlayer.wantsPeace() && !this.isAcademyMember()) {
                    final ClanWar war = attackerClan.getWarWith(this.getClanId());
                    if (war != null && war.getState() == ClanWarState.MUTUAL) {
                        return true;
                    }
                }
            }
            if (this.isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP) && (!this.isInsideZone(ZoneType.SIEGE) || !attackerPlayer.isInsideZone(ZoneType.SIEGE))) {
                return true;
            }
            if (clan != null && clan.isMember(attacker.getObjectId())) {
                return false;
            }
            if (GameUtils.isPlayer(attacker) && this.getAllyId() != 0 && this.getAllyId() == attackerPlayer.getAllyId()) {
                return false;
            }
            if (this.isInsideZone(ZoneType.PVP) && attackerPlayer.isInsideZone(ZoneType.PVP) && this.isInsideZone(ZoneType.SIEGE) && attackerPlayer.isInsideZone(ZoneType.SIEGE)) {
                return true;
            }
            if (this.getPvpFlag() > 0) {
                return true;
            }
        }
        if (attacker instanceof Defender && this._clan != null) {
            final Siege siege2 = SiegeManager.getInstance().getSiege(this);
            return siege2 != null && siege2.checkIsAttacker(this._clan);
        }
        if (attacker instanceof Guard) {
            return this.getReputation() < 0;
        }
        return this.getReputation() < 0 || this._pvpFlag > 0;
    }
    
    @Override
    public boolean useMagic(final Skill skill, final Item item, final boolean forceUse, final boolean dontMove) {
        if (skill.isPassive()) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (!Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT && this.getReputation() < 0 && skill.hasAnyEffectType(EffectType.TELEPORT)) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (skill.isToggle() && this.isMounted()) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (!skill.canCastWhileDisabled() && (this.isControlBlocked() || this.hasBlockActions())) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isDead()) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isFishing() && !skill.hasAnyEffectType(EffectType.FISHING, EffectType.FISHING_START)) {
            this.sendPacket(SystemMessageId.ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME);
            return false;
        }
        if (this._observerMode) {
            this.sendPacket(SystemMessageId.OBSERVERS_CANNOT_PARTICIPATE);
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isSkillDisabled(skill)) {
            SystemMessage sm;
            if (this.hasSkillReuse(skill.getReuseHashCode())) {
                final int remainingTime = (int)(this.getSkillRemainingReuseTime(skill.getReuseHashCode()) / 1000L);
                final int hours = remainingTime / 3600;
                final int minutes = remainingTime % 3600 / 60;
                final int seconds = remainingTime % 60;
                if (hours > 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                    sm.addInt(hours);
                    sm.addInt(minutes);
                }
                else if (minutes > 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                    sm.addInt(minutes);
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME);
                    sm.addSkillName(skill);
                }
                sm.addInt(seconds);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE);
                sm.addSkillName(skill);
            }
            this.sendPacket(sm);
            return false;
        }
        if (this._waitTypeSitting) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING);
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (skill.isToggle() && this.isAffectedBySkill(skill.getId())) {
            this.stopSkillEffects(true, skill.getId());
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this.isFakeDeath()) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        final Location worldPosition = this._currentSkillWorldPosition;
        if (skill.getTargetType() == TargetType.GROUND && worldPosition == null) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        final WorldObject target = skill.getTarget(this, forceUse, dontMove, true);
        if (target == null) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (!skill.checkCondition(this, target)) {
            this.sendPacket(ActionFailed.STATIC_PACKET);
            if (skill.getNextAction() != NextActionType.NONE && target != this && target.isAutoAttackable(this) && (this.getAI().getNextIntention() == null || this.getAI().getNextIntention().getCtrlIntention() != CtrlIntention.AI_INTENTION_MOVE_TO)) {
                if (skill.getNextAction() == NextActionType.ATTACK) {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
                }
                else if (skill.getNextAction() == NextActionType.CAST) {
                    this.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, false, false);
                }
            }
            return false;
        }
        if (this.isCastingNow(SkillCaster::isAnyNormalType) || (this.isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL) && this.isCastingNow(s -> s.getCastingType() == SkillCastingType.NORMAL_SECOND))) {
            if (item == null) {
                this.setQueuedSkill(skill, item, forceUse, dontMove);
            }
            this.sendPacket(ActionFailed.STATIC_PACKET);
            return false;
        }
        if (this._queuedSkill != null) {
            this.setQueuedSkill(null, null, false, false);
        }
        this.getAI().setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target, item, forceUse, dontMove);
        return true;
    }
    
    public boolean isInLooterParty(final int LooterId) {
        final Player looter = World.getInstance().findPlayer(LooterId);
        if (this.isInParty() && this._party.isInCommandChannel() && looter != null) {
            return this._party.getCommandChannel().getMembers().contains(looter);
        }
        return this.isInParty() && looter != null && this._party.getMembers().contains(looter);
    }
    
    public boolean isMageClass() {
        return this.getClassId().isMage();
    }
    
    public boolean isMounted() {
        return this._mountType != MountType.NONE;
    }
    
    public boolean checkLandingState() {
        return this.isInsideZone(ZoneType.NO_LANDING) || (this.isInsideZone(ZoneType.SIEGE) && (this.getClan() == null || CastleManager.getInstance().getCastle(this) != CastleManager.getInstance().getCastleByOwner(this.getClan()) || this != this.getClan().getLeader().getPlayerInstance()));
    }
    
    public void setMount(final int npcId, final int npcLevel) {
        final MountType type = MountType.findByNpcId(npcId);
        switch (type) {
            case NONE: {
                this.setIsFlying(false);
                break;
            }
            case STRIDER: {
                if (this.isNoble()) {
                    this.addSkill(CommonSkill.STRIDER_SIEGE_ASSAULT.getSkill(), false);
                    break;
                }
                break;
            }
            case WYVERN: {
                this.setIsFlying(true);
                break;
            }
        }
        this._mountType = type;
        this._mountNpcId = npcId;
        this._mountLevel = npcLevel;
    }
    
    public MountType getMountType() {
        return this._mountType;
    }
    
    @Override
    public final void stopAllEffects() {
        super.stopAllEffects();
        this.updateAndBroadcastStatus(2);
    }
    
    @Override
    public final void stopAllEffectsExceptThoseThatLastThroughDeath() {
        super.stopAllEffectsExceptThoseThatLastThroughDeath();
        this.updateAndBroadcastStatus(2);
    }
    
    public final void stopCubics() {
        if (!this._cubics.isEmpty()) {
            this._cubics.values().forEach(CubicInstance::deactivate);
            this._cubics.clear();
        }
    }
    
    public final void stopCubicsByOthers() {
        if (!this._cubics.isEmpty()) {
            boolean broadcast = false;
            for (final CubicInstance cubic : this._cubics.values()) {
                if (cubic.isGivenByOther()) {
                    cubic.deactivate();
                    this._cubics.remove(cubic.getTemplate().getId());
                    broadcast = true;
                }
            }
            if (broadcast) {
                this.sendPacket(new ExUserInfoCubic(this));
                this.broadcastUserInfo();
            }
        }
    }
    
    @Override
    public void updateAbnormalVisualEffects() {
        this.sendPacket(new ExUserInfoAbnormalVisualEffect(this));
        this.broadcastCharInfo();
    }
    
    public void setInventoryBlockingStatus(final boolean val) {
        this._inventoryDisable = val;
        if (val) {
            ThreadPool.schedule((Runnable)new InventoryEnableTask(this), 1500L);
        }
    }
    
    public boolean isInventoryDisabled() {
        return this._inventoryDisable;
    }
    
    public CubicInstance addCubic(final CubicInstance cubic) {
        return this._cubics.put(cubic.getTemplate().getId(), cubic);
    }
    
    public Map<Integer, CubicInstance> getCubics() {
        return this._cubics;
    }
    
    public CubicInstance getCubicById(final int cubicId) {
        return this._cubics.get(cubicId);
    }
    
    public int getEnchantEffect() {
        final Item wpn = this.getActiveWeaponInstance();
        if (wpn == null) {
            return 0;
        }
        return Math.min(127, wpn.getEnchantLevel());
    }
    
    public Npc getLastFolkNPC() {
        return this._lastFolkNpc;
    }
    
    public void setLastFolkNPC(final Npc folkNpc) {
        this._lastFolkNpc = folkNpc;
    }
    
    public EnumIntBitmask<ClanPrivilege> getClanPrivileges() {
        return this._clanPrivileges;
    }
    
    public void setClanPrivileges(final EnumIntBitmask<ClanPrivilege> clanPrivileges) {
        this._clanPrivileges = clanPrivileges.clone();
    }
    
    public boolean hasClanPrivilege(final ClanPrivilege privilege) {
        return this._clanPrivileges.has(privilege, new ClanPrivilege[0]);
    }
    
    public int getPledgeClass() {
        return this._pledgeClass;
    }
    
    public void setPledgeClass(final int classId) {
        this._pledgeClass = classId;
        this.checkItemRestriction();
    }
    
    @Override
    public int getPledgeType() {
        return this.data.getSubPledge();
    }
    
    public void setPledgeType(final int typeId) {
        this.data.setSubPledge(typeId);
    }
    
    public int getApprentice() {
        return this.data.getApprentice();
    }
    
    public void setApprentice(final int apprenticeId) {
        this.data.setApprentice(apprenticeId);
    }
    
    public int getSponsor() {
        return this.data.getSponsor();
    }
    
    public void setSponsor(final int sponsorId) {
        this.data.setSponsor(sponsorId);
    }
    
    public int getBookMarkSlot() {
        return this._bookmarkslot;
    }
    
    public void setBookMarkSlot(final int slot) {
        this._bookmarkslot = slot;
        this.sendPacket(new ExGetBookMarkInfoPacket(this));
    }
    
    @Override
    public void sendMessage(final String message) {
        this.sendPacket(SystemMessage.sendString(message));
    }
    
    public void setObserving(final boolean state) {
        this._observerMode = state;
        this.setTarget(null);
        this.setBlockActions(state);
        this.setIsInvul(state);
        this.setInvisible(state);
        if (this.hasAI() && !state) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
    }
    
    public void enterObserverMode(final Location loc) {
        this.setLastLocation();
        this.getEffectList().stopEffects(AbnormalType.HIDE);
        this.setObserving(true);
        this.sendPacket(new ObservationMode(loc));
        this.teleToLocation(loc, false);
        this.broadcastUserInfo();
    }
    
    public void setLastLocation() {
        this._lastLoc = new Location(this.getX(), this.getY(), this.getZ());
    }
    
    public void unsetLastLocation() {
        this._lastLoc = null;
    }
    
    public void enterOlympiadObserverMode(final Location loc, final int id) {
        if (this.pet != null) {
            this.pet.unSummon(this);
        }
        if (this.hasServitors()) {
            this.getServitors().values().forEach(s -> s.unSummon(this));
        }
        this.getEffectList().stopEffects(AbnormalType.HIDE);
        if (!this._cubics.isEmpty()) {
            this._cubics.values().forEach(CubicInstance::deactivate);
            this._cubics.clear();
            this.sendPacket(new ExUserInfoCubic(this));
        }
        if (this._party != null) {
            this._party.removePartyMember(this, Party.MessageType.EXPELLED);
        }
        this._olympiadGameId = id;
        if (this._waitTypeSitting) {
            this.standUp();
        }
        if (!this._observerMode) {
            this.setLastLocation();
        }
        this._observerMode = true;
        this.setTarget(null);
        this.setIsInvul(true);
        this.setInvisible(true);
        this.setInstance(OlympiadGameManager.getInstance().getOlympiadTask(id).getStadium().getInstance());
        this.teleToLocation(loc, false);
        this.sendPacket(new ExOlympiadMode(3));
        this.broadcastUserInfo();
    }
    
    public void leaveObserverMode() {
        this.setTarget(null);
        this.setInstance(null);
        this.teleToLocation(this._lastLoc, false);
        this.unsetLastLocation();
        this.sendPacket(new ObservationReturn(this.getLocation()));
        this.setBlockActions(false);
        if (!this.isGM()) {
            this.setInvisible(false);
            this.setIsInvul(false);
        }
        if (this.hasAI()) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
        this.setFalling();
        this._observerMode = false;
        this.broadcastUserInfo();
    }
    
    public void leaveOlympiadObserverMode() {
        if (this._olympiadGameId == -1) {
            return;
        }
        this._olympiadGameId = -1;
        this._observerMode = false;
        this.setTarget(null);
        this.sendPacket(new ExOlympiadMode(0));
        this.setInstance(null);
        this.teleToLocation(this._lastLoc, true);
        if (!this.isGM()) {
            this.setInvisible(false);
            this.setIsInvul(false);
        }
        if (this.hasAI()) {
            this.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        }
        this.unsetLastLocation();
        this.broadcastUserInfo();
    }
    
    public int getOlympiadSide() {
        return this._olympiadSide;
    }
    
    public void setOlympiadSide(final int i) {
        this._olympiadSide = i;
    }
    
    public int getOlympiadGameId() {
        return this._olympiadGameId;
    }
    
    public void setOlympiadGameId(final int id) {
        this._olympiadGameId = id;
    }
    
    public Location getLastLocation() {
        return this._lastLoc;
    }
    
    public boolean inObserverMode() {
        return this._observerMode;
    }
    
    public AdminTeleportType getTeleMode() {
        return this._teleportType;
    }
    
    public void setTeleMode(final AdminTeleportType type) {
        this._teleportType = type;
    }
    
    public int getRace(final int i) {
        return this._race[i];
    }
    
    public boolean isMessageRefusing() {
        return this.messageRefusing;
    }
    
    public void setMessageRefusing(final boolean mode) {
        this.messageRefusing = mode;
        this.sendPacket(new EtcStatusUpdate(this));
    }
    
    public boolean getDietMode() {
        return this._dietMode;
    }
    
    public void setDietMode(final boolean mode) {
        this._dietMode = mode;
    }
    
    public boolean isTradeRefusing() {
        return this.tradeRefusing;
    }
    
    public void setTradeRefusing(final boolean mode) {
        this.tradeRefusing = mode;
    }
    
    public BlockList getBlockList() {
        return this._blockList;
    }
    
    public boolean isBlocked(final Player player) {
        return player.getBlockList().isBlockAll() || player.getBlockList().isInBlockList(this);
    }
    
    public boolean isNotBlocked(final Player player) {
        return !player.getBlockList().isBlockAll() && !player.getBlockList().isInBlockList(this);
    }
    
    public void setIsInOlympiadMode(final boolean b) {
        this._inOlympiadMode = b;
    }
    
    public void setIsOlympiadStart(final boolean b) {
        this._OlympiadStart = b;
    }
    
    public boolean isOlympiadStart() {
        return this._OlympiadStart;
    }
    
    public boolean isHero() {
        return this._hero;
    }
    
    public void setHero(final boolean hero) {
        if (hero && this.data.getBaseClass() == this._activeClass) {
            for (final Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                this.addSkill(skill, false);
            }
        }
        else {
            for (final Skill skill : SkillTreesData.getInstance().getHeroSkillTree()) {
                this.removeSkill(skill, false, true);
            }
        }
        this._hero = hero;
        this.sendSkillList();
    }
    
    public boolean isInOlympiadMode() {
        return this._inOlympiadMode;
    }
    
    public boolean isInDuel() {
        return this._isInDuel;
    }
    
    public void setStartingDuel() {
        this._startingDuel = true;
    }
    
    public int getDuelId() {
        return this._duelId;
    }
    
    public int getDuelState() {
        return this._duelState;
    }
    
    public void setDuelState(final int mode) {
        this._duelState = mode;
    }
    
    public void setIsInDuel(final int duelId) {
        if (duelId > 0) {
            this._isInDuel = true;
            this._duelState = 1;
            this._duelId = duelId;
        }
        else {
            if (this._duelState == 2) {
                this.enableAllSkills();
                this.getStatus().startHpMpRegeneration();
            }
            this._isInDuel = false;
            this._duelState = 0;
            this._duelId = 0;
        }
        this._startingDuel = false;
    }
    
    public SystemMessage getNoDuelReason() {
        final SystemMessage sm = SystemMessage.getSystemMessage(this._noDuelReason);
        sm.addPcName(this);
        this._noDuelReason = SystemMessageId.THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
        return sm;
    }
    
    public boolean canDuel() {
        if (this.isInCombat() || this.isJailed()) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }
        if (this.isDead() || this.isAlikeDead() || this.getCurrentHp() < this.getMaxHp() / 2.0 || this.getCurrentMp() < this.getMaxMp() / 2.0) {
            this._noDuelReason = SystemMessageId.C1_S_HP_OR_MP_IS_BELOW_50_AND_CANNOT_DUEL;
            return false;
        }
        if (this._isInDuel || this._startingDuel) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL;
            return false;
        }
        if (this._inOlympiadMode) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS;
            return false;
        }
        if (this.isOnEvent()) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
            return false;
        }
        if (this.privateStoreType != PrivateStoreType.NONE) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
            return false;
        }
        if (this.isMounted() || this.isInBoat()) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_FENRIR_OR_STRIDER;
            return false;
        }
        if (this.isFishing()) {
            this._noDuelReason = SystemMessageId.C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING;
            return false;
        }
        if (this.isInsideZone(ZoneType.PVP) || this.isInsideZone(ZoneType.PEACE) || this.isInsideZone(ZoneType.SIEGE)) {
            this._noDuelReason = SystemMessageId.C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL;
            return false;
        }
        return true;
    }
    
    public boolean isNoble() {
        return this._noble;
    }
    
    public void setNoble(final boolean val) {
        if (val) {
            SkillTreesData.getInstance().getNobleSkillAutoGetTree().forEach(skill -> this.addSkill(skill, false));
        }
        else {
            SkillTreesData.getInstance().getNobleSkillTree().forEach(skill -> this.removeSkill(skill, false, true));
        }
        this._noble = val;
        this.sendSkillList();
    }
    
    public int getLvlJoinedAcademy() {
        return this.data.getLevelJoinedAcademy();
    }
    
    public void setLvlJoinedAcademy(final int lvl) {
        this.data.setLevelJoinedAcademy(lvl);
    }
    
    @Override
    public boolean isAcademyMember() {
        return this.getLvlJoinedAcademy() > 0;
    }
    
    @Override
    public void setTeam(final Team team) {
        super.setTeam(team);
        this.broadcastUserInfo();
        if (this.pet != null) {
            this.pet.broadcastStatusUpdate();
        }
        if (this.hasServitors()) {
            this.getServitors().values().forEach(Creature::broadcastStatusUpdate);
        }
    }
    
    public boolean wantsPeace() {
        return this.data.wantsPeace();
    }
    
    public void sendSkillList() {
        if (this._skillListRefreshTask == null) {
            this._skillListRefreshTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> {
                this.sendSkillList(0);
                this._skillListRefreshTask = null;
            }, 1000L);
        }
    }
    
    public void sendSkillList(final int lastLearnedSkillId) {
        boolean isDisabled = false;
        final SkillList sl = new SkillList();
        for (final Skill s : this.getSkillList()) {
            if (this._clan != null) {
                isDisabled = (s.isClanSkill() && this._clan.getReputationScore() < 0);
            }
            sl.addSkill(s.getDisplayId(), s.getReuseDelayGroup(), s.getDisplayLevel(), s.getSubLevel(), s.isPassive(), isDisabled, s.isEnchantable());
        }
        if (lastLearnedSkillId > 0) {
            sl.setLastLearnedSkillId(lastLearnedSkillId);
        }
        this.sendPacket(sl);
        this.sendPacket(new AcquireSkillList(this));
    }
    
    public boolean addSubClass(final int classId, final int classIndex, final boolean isDualClass) {
        if (!this._subclassLock.tryLock()) {
            return false;
        }
        try {
            if (this.getTotalSubClasses() == Config.MAX_SUBCLASS || classIndex == 0) {
                return false;
            }
            if (this.getSubClasses().containsKey(classIndex)) {
                return false;
            }
            final SubClass newClass = new SubClass();
            newClass.setClassId(classId);
            newClass.setClassIndex(classIndex);
            newClass.setVitalityPoints(140000);
            if (isDualClass) {
                newClass.setIsDualClass(true);
                newClass.setExp(LevelData.getInstance().getExpForLevel(Config.BASE_DUALCLASS_LEVEL));
                newClass.setLevel(Config.BASE_DUALCLASS_LEVEL);
            }
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("INSERT INTO character_subclasses (charId,class_id,exp,sp,level,vitality_points,class_index,dual_class) VALUES (?,?,?,?,?,?,?,?)");
                    try {
                        statement.setInt(1, this.getObjectId());
                        statement.setInt(2, newClass.getClassId());
                        statement.setLong(3, newClass.getExp());
                        statement.setLong(4, newClass.getSp());
                        statement.setInt(5, newClass.getLevel());
                        statement.setInt(6, newClass.getVitalityPoints());
                        statement.setInt(7, newClass.getClassIndex());
                        statement.setBoolean(8, newClass.isDualClass());
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, this.getName(), e.getMessage()), (Throwable)e);
                return false;
            }
            this.getSubClasses().put(newClass.getClassIndex(), (Object)newClass);
            final ClassId subTemplate = ClassId.getClassId(classId);
            final LongMap<SkillLearn> skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(subTemplate);
            final Map<Integer, Skill> prevSkillList = new HashMap<Integer, Skill>();
            for (final SkillLearn skillInfo : skillTree.values()) {
                if (skillInfo.getGetLevel() <= newClass.getLevel()) {
                    final Skill prevSkill = prevSkillList.get(skillInfo.getSkillId());
                    final Skill newSkill = SkillEngine.getInstance().getSkill(skillInfo.getSkillId(), skillInfo.getSkillLevel());
                    if (prevSkill != null && prevSkill.getLevel() > newSkill.getLevel()) {
                        continue;
                    }
                    if (SkillTreesData.getInstance().isRemoveSkill(subTemplate, skillInfo.getSkillId())) {
                        continue;
                    }
                    prevSkillList.put(newSkill.getId(), newSkill);
                    this.storeSkill(newSkill, prevSkill, classIndex);
                }
            }
            return true;
        }
        finally {
            this._subclassLock.unlock();
        }
    }
    
    public boolean modifySubClass(final int classIndex, final int newClassId, final boolean isDualClass) {
        if (!this._subclassLock.tryLock()) {
            return false;
        }
        try {
            if (!this.getSubClasses().isEmpty()) {
                final int classId = ((SubClass)this.getSubClasses().get(classIndex)).getClassId();
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionCancel(this, classId), this);
            }
            final SubClass subClass = (SubClass)this.getSubClasses().get(classIndex);
            if (subClass == null) {
                return false;
            }
            if (subClass.isDualClass()) {
                this.setAbilityPointsDualClassUsed(1000);
                int revelationSkill = this.getRevelationSkillDualClass1();
                if (revelationSkill != 0) {
                    this.removeSkill(revelationSkill);
                }
                revelationSkill = this.getRevelationSkillDualClass2();
                if (revelationSkill != 0) {
                    this.removeSkill(revelationSkill);
                }
            }
            this.getSubClasses().remove(classIndex);
            this.shortcuts.deleteShortcuts();
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement deleteHennas = con.prepareStatement("DELETE FROM character_hennas WHERE charId=? AND class_index=?");
                    try {
                        final PreparedStatement deleteSkillReuse = con.prepareStatement("DELETE FROM character_skills_save WHERE charId=? AND class_index=?");
                        try {
                            final PreparedStatement deleteSkills = con.prepareStatement("DELETE FROM character_skills WHERE charId=? AND class_index=?");
                            try {
                                final PreparedStatement deleteSubclass = con.prepareStatement("DELETE FROM character_subclasses WHERE charId=? AND class_index=?");
                                try {
                                    deleteHennas.setInt(1, this.getObjectId());
                                    deleteHennas.setInt(2, classIndex);
                                    deleteHennas.execute();
                                    deleteSkillReuse.setInt(1, this.getObjectId());
                                    deleteSkillReuse.setInt(2, classIndex);
                                    deleteSkillReuse.execute();
                                    deleteSkills.setInt(1, this.getObjectId());
                                    deleteSkills.setInt(2, classIndex);
                                    deleteSkills.execute();
                                    deleteSubclass.setInt(1, this.getObjectId());
                                    deleteSubclass.setInt(2, classIndex);
                                    deleteSubclass.execute();
                                    if (deleteSubclass != null) {
                                        deleteSubclass.close();
                                    }
                                }
                                catch (Throwable t) {
                                    if (deleteSubclass != null) {
                                        try {
                                            deleteSubclass.close();
                                        }
                                        catch (Throwable exception) {
                                            t.addSuppressed(exception);
                                        }
                                    }
                                    throw t;
                                }
                                if (deleteSkills != null) {
                                    deleteSkills.close();
                                }
                            }
                            catch (Throwable t2) {
                                if (deleteSkills != null) {
                                    try {
                                        deleteSkills.close();
                                    }
                                    catch (Throwable exception2) {
                                        t2.addSuppressed(exception2);
                                    }
                                }
                                throw t2;
                            }
                            if (deleteSkillReuse != null) {
                                deleteSkillReuse.close();
                            }
                        }
                        catch (Throwable t3) {
                            if (deleteSkillReuse != null) {
                                try {
                                    deleteSkillReuse.close();
                                }
                                catch (Throwable exception3) {
                                    t3.addSuppressed(exception3);
                                }
                            }
                            throw t3;
                        }
                        if (deleteHennas != null) {
                            deleteHennas.close();
                        }
                    }
                    catch (Throwable t4) {
                        if (deleteHennas != null) {
                            try {
                                deleteHennas.close();
                            }
                            catch (Throwable exception4) {
                                t4.addSuppressed(exception4);
                            }
                        }
                        throw t4;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t5) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception5) {
                            t5.addSuppressed(exception5);
                        }
                    }
                    throw t5;
                }
            }
            catch (Exception e) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getName(), classIndex, e.getMessage()), (Throwable)e);
                return false;
            }
        }
        finally {
            this._subclassLock.unlock();
        }
        return this.addSubClass(newClassId, classIndex, isDualClass);
    }
    
    public boolean isSubClassActive() {
        return this._classIndex > 0;
    }
    
    public boolean isDualClassActive() {
        if (!this.isSubClassActive() || this._subClasses.isEmpty()) {
            return false;
        }
        final SubClass subClass = (SubClass)this._subClasses.get(this._classIndex);
        return Objects.nonNull(subClass) && subClass.isDualClass();
    }
    
    public boolean hasDualClass() {
        return this.getSubClasses().values().stream().anyMatch(SubClass::isDualClass);
    }
    
    public SubClass getDualClass() {
        return this.getSubClasses().values().stream().filter(SubClass::isDualClass).findFirst().orElse(null);
    }
    
    public void setDualClass(final int classIndex) {
        if (this.isSubClassActive()) {
            ((SubClass)this.getSubClasses().get(this._classIndex)).setIsDualClass(true);
        }
    }
    
    public IntMap<SubClass> getSubClasses() {
        return this._subClasses;
    }
    
    public int getTotalSubClasses() {
        return this.getSubClasses().size();
    }
    
    public int getBaseClass() {
        return this.data.getBaseClass();
    }
    
    public void setBaseClass(final int baseClass) {
        this.data.setBaseClass(baseClass);
    }
    
    public int getActiveClass() {
        return this._activeClass;
    }
    
    public int getClassIndex() {
        return this._classIndex;
    }
    
    protected void setClassIndex(final int classIndex) {
        this._classIndex = classIndex;
    }
    
    private void setClassTemplate(final int classId) {
        this._activeClass = classId;
        final PlayerTemplate pcTemplate = PlayerTemplateData.getInstance().getTemplate(classId);
        if (pcTemplate == null) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, classId));
            throw new Error();
        }
        this.setTemplate(pcTemplate);
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerProfessionChange(this, pcTemplate, this.isSubClassActive()), this);
    }
    
    public void setActiveClass(final int classIndex) {
        if (!this._subclassLock.tryLock()) {
            return;
        }
        try {
            if (this.isTransformed()) {
                return;
            }
            this.inventory.forEachEquippedItem(item -> item.getAugmentation().removeBonus(this), Item::isAugmented);
            this.abortCast();
            if (this.isChannelized()) {
                this.getSkillChannelized().abortChannelization();
            }
            this.store(Config.SUBCLASS_STORE_SKILL_COOLTIME);
            if (this._sellingBuffs != null) {
                this._sellingBuffs.clear();
            }
            this.resetTimeStamps();
            this._charges.set(0);
            this.stopChargeTask();
            if (this.hasServitors()) {
                this.getServitors().values().forEach(s -> s.unSummon(this));
            }
            if (classIndex == 0) {
                this.setClassTemplate(this.data.getBaseClass());
            }
            else {
                try {
                    this.setClassTemplate(((SubClass)this.getSubClasses().get(classIndex)).getClassId());
                }
                catch (Exception e) {
                    Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, this.getName(), classIndex, e.getMessage()), (Throwable)e);
                    return;
                }
            }
            this._classIndex = classIndex;
            if (this.isInParty()) {
                this._party.recalculatePartyLevel();
            }
            for (final Skill oldSkill : this.getAllSkills()) {
                this.removeSkill(oldSkill, false, true);
            }
            this.stopAllEffectsExceptThoseThatLastThroughDeath();
            this.stopAllEffects();
            this.stopCubics();
            this.restoreRecipeBook(false);
            this.restoreSkills();
            this.rewardSkills();
            this.regiveTemporarySkills();
            this.resetDisabledSkills();
            this.restoreEffects();
            this.sendPacket(new EtcStatusUpdate(this));
            for (int i = 0; i < 4; ++i) {
                this._henna[i] = null;
            }
            this.restoreHenna();
            this.sendPacket(new HennaInfo(this));
            if (this.getCurrentHp() > this.getMaxHp()) {
                this.setCurrentHp(this.getMaxHp());
            }
            if (this.getCurrentMp() > this.getMaxMp()) {
                this.setCurrentMp(this.getMaxMp());
            }
            if (this.getCurrentCp() > this.getMaxCp()) {
                this.setCurrentCp(this.getMaxCp());
            }
            this.refreshOverloaded(true);
            this.broadcastUserInfo();
            this.data.setExpBeforeDeath(0L);
            this.shortcuts.restoreMe();
            this.sendPacket(new ShortCutInit());
            this.broadcastPacket(new SocialAction(this.getObjectId(), 2122));
            this.sendPacket(new SkillCoolTime(this));
            this.sendPacket(new ExStorageMaxCount(this));
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSubChange(this), this);
        }
        finally {
            this._subclassLock.unlock();
        }
    }
    
    public boolean isSubClassLocked() {
        return this._subclassLock.isLocked();
    }
    
    public void stopWarnUserTakeBreak() {
        if (this._taskWarnUserTakeBreak != null) {
            this._taskWarnUserTakeBreak.cancel(true);
            this._taskWarnUserTakeBreak = null;
        }
    }
    
    public void startWarnUserTakeBreak() {
        if (this._taskWarnUserTakeBreak == null) {
            this._taskWarnUserTakeBreak = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new WarnUserTakeBreakTask(this), 3600000L, 3600000L);
        }
    }
    
    public void stopRentPet() {
        if (this._taskRentPet != null) {
            if (this.checkLandingState() && this._mountType == MountType.WYVERN) {
                this.teleToLocation(TeleportWhereType.TOWN);
            }
            if (this.dismount()) {
                this._taskRentPet.cancel(true);
                this._taskRentPet = null;
            }
        }
    }
    
    public boolean isRentedPet() {
        return this._taskRentPet != null;
    }
    
    public void stopWaterTask() {
        if (this._taskWater != null) {
            this._taskWater.cancel(false);
            this._taskWater = null;
            this.sendPacket(new SetupGauge(this.getObjectId(), 2, 0));
        }
    }
    
    public void startWaterTask() {
        if (!this.isDead() && this._taskWater == null) {
            final int timeinwater = (int)this.getStats().getValue(Stat.BREATH, 60000.0);
            this.sendPacket(new SetupGauge(this.getObjectId(), 2, timeinwater));
            this._taskWater = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new WaterTask(this), (long)timeinwater, 1000L);
        }
    }
    
    public boolean isInWater() {
        return this._taskWater != null;
    }
    
    public void checkWaterState() {
        if (this.isInsideZone(ZoneType.WATER)) {
            this.startWaterTask();
        }
        else {
            this.stopWaterTask();
        }
    }
    
    public void onEnter() {
        this.startWarnUserTakeBreak();
        if (this.isGM() && !Config.GM_STARTUP_BUILDER_HIDE) {
            if (this.isInvul()) {
                this.sendMessage("Entering world in Invulnerable mode.");
            }
            if (this.isInvisible()) {
                this.sendMessage("Entering world in Invisible mode.");
            }
            if (this._silenceMode) {
                this.sendMessage("Entering world in Silence mode.");
            }
        }
        this.inventory.applyItemSkills();
        if (Config.STORE_SKILL_COOLTIME) {
            this.restoreEffects();
        }
        if (!this.isDead()) {
            this.setCurrentCp(this._originalCp);
            this.setCurrentHp(this._originalHp);
            this.setCurrentMp(this._originalMp);
        }
        if (this.isAlikeDead()) {
            this.sendPacket(new Die(this));
        }
        this.revalidateZone(true);
        this.notifyFriends(1);
        if (!this.canOverrideCond(PcCondOverride.SKILL_CONDITIONS)) {
            this.checkPlayerSkills();
        }
        try {
            for (final Zone zone : ZoneManager.getInstance().getZones(this)) {
                zone.onPlayerLoginInside(this);
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("", (Throwable)e);
        }
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogin(this), this);
        if (this.isMentee()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, true), this);
        }
        else if (this.isMentor()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, true), this);
        }
    }
    
    public long getLastAccess() {
        return this.data.getLastAccess();
    }
    
    @Override
    public void doRevive() {
        super.doRevive();
        this.sendPacket(new EtcStatusUpdate(this));
        this._revivePet = false;
        this._reviveRequested = 0;
        this._revivePower = 0.0;
        if (this.isMounted()) {
            this.startFeed(this._mountNpcId);
        }
        final Instance instance = this.getInstanceWorld();
        if (instance != null) {
            instance.doRevive(this);
        }
        this.lastDamages.clear();
    }
    
    @Override
    public void doRevive(final double revivePower) {
        this.doRevive();
        this.restoreExp(revivePower);
    }
    
    public void reviveRequest(final Player reviver, final Skill skill, final boolean isPet, final int power) {
        if (this.isResurrectionBlocked()) {
            return;
        }
        if (this._reviveRequested == 1) {
            if (this._revivePet == isPet) {
                reviver.sendPacket(SystemMessageId.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
            }
            else if (isPet) {
                reviver.sendPacket(SystemMessageId.A_PET_CANNOT_BE_RESURRECTED_WHILE_IT_S_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
            }
            else {
                reviver.sendPacket(SystemMessageId.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
            }
            return;
        }
        if ((isPet && this.pet != null && this.pet.isDead()) || (!isPet && this.isDead())) {
            this._reviveRequested = 1;
            this._revivePower = Formulas.calculateSkillResurrectRestorePercent(power, reviver);
            this._revivePet = isPet;
            if (this.hasCharmOfCourage()) {
                final ConfirmDlg dlg = new ConfirmDlg(SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW);
                dlg.addTime(60000);
                this.sendPacket(dlg);
                return;
            }
            final long restoreExp = Math.round((this.data.getExpBeforeDeath() - this.getExp()) * this._revivePower / 100.0);
            final ConfirmDlg dlg2 = new ConfirmDlg(SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT);
            dlg2.addPcName(reviver);
            dlg2.addLong(restoreExp);
            dlg2.addInt(power);
            this.sendPacket(dlg2);
        }
    }
    
    public void reviveAnswer(final int answer) {
        if (this._reviveRequested != 1 || (!this.isDead() && !this._revivePet) || (this._revivePet && this.pet != null && !this.pet.isDead())) {
            return;
        }
        if (answer == 1) {
            if (!this._revivePet) {
                if (this._revivePower != 0.0) {
                    this.doRevive(this._revivePower);
                }
                else {
                    this.doRevive();
                }
            }
            else if (this.pet != null) {
                if (this._revivePower != 0.0) {
                    this.pet.doRevive(this._revivePower);
                }
                else {
                    this.pet.doRevive();
                }
            }
        }
        this._reviveRequested = 0;
        this._revivePower = 0.0;
    }
    
    public boolean isReviveRequested() {
        return this._reviveRequested == 1;
    }
    
    public boolean isRevivingPet() {
        return this._revivePet;
    }
    
    public void removeReviving() {
        this._reviveRequested = 0;
        this._revivePower = 0.0;
    }
    
    public void onActionRequest() {
        if (this.isSpawnProtected()) {
            this.setSpawnProtection(false);
            if (!this.isInsideZone(ZoneType.PEACE)) {
                this.sendPacket(SystemMessageId.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);
            }
            if (((CharacterSettings)Configurator.getSettings((Class)CharacterSettings.class)).restoreSummonOnReconnect() && !this.hasSummon()) {
                if (PlayerSummonTable.getInstance().getServitors().containsKey(this.getObjectId())) {
                    PlayerSummonTable.getInstance().restoreServitor(this);
                }
                else if (PlayerSummonTable.getInstance().getPets().containsKey(this.getObjectId())) {
                    PlayerSummonTable.getInstance().restorePet(this);
                }
            }
        }
        if (this.isTeleportProtected()) {
            this.setTeleportProtection(false);
            if (!this.isInsideZone(ZoneType.PEACE)) {
                this.sendMessage("Teleport spawn protection ended.");
            }
        }
    }
    
    @Override
    public void teleToLocation(final ILocational loc, final int randomOffset) {
        if (this._vehicle != null && !this._vehicle.isTeleporting()) {
            this.setVehicle(null);
        }
        if (this.isFlyingMounted() && loc.getZ() < -1005) {
            super.teleToLocation(loc.getX(), loc.getY(), -1005, loc.getHeading());
        }
        super.teleToLocation(loc, randomOffset);
    }
    
    @Override
    public void teleToLocation(final ILocational loc, final boolean allowRandomOffset) {
        if (this._vehicle != null && !this._vehicle.isTeleporting()) {
            this.setVehicle(null);
        }
        if (this.isFlyingMounted() && loc.getZ() < -1005) {
            super.teleToLocation(loc.getX(), loc.getY(), -1005, loc.getHeading());
        }
        super.teleToLocation(loc, allowRandomOffset);
    }
    
    @Override
    public final void onTeleported() {
        super.onTeleported();
        this.setLastServerPosition(this.getX(), this.getY(), this.getZ());
        this.revalidateZone(true);
        this.checkItemRestriction();
        if (Config.PLAYER_TELEPORT_PROTECTION > 0 && !this._inOlympiadMode) {
            this.setTeleportProtection(true);
        }
        if (this.tamedBeast != null) {
            for (final TamedBeast tamedBeast : this.tamedBeast) {
                tamedBeast.deleteMe();
            }
            this.tamedBeast.clear();
        }
        if (this.pet != null) {
            this.pet.setFollowStatus(false);
            this.pet.teleToLocation(this.getLocation(), false);
            ((SummonAI)this.pet.getAI()).setStartFollowController(true);
            this.pet.setFollowStatus(true);
            this.pet.setInstance(this.getInstanceWorld());
            this.pet.updateAndBroadcastStatus(0);
        }
        this.getServitors().values().forEach(s -> {
            s.setFollowStatus(false);
            s.teleToLocation(this.getLocation(), false);
            ((SummonAI)s.getAI()).setStartFollowController(true);
            s.setFollowStatus(true);
            s.setInstance(this.getInstanceWorld());
            s.updateAndBroadcastStatus(0);
            return;
        });
        if (!this.isInTimedHuntingZone()) {
            this.stopTimedHuntingZoneTask();
        }
        if (this._movieHolder != null) {
            this.sendPacket(new ExStartScenePlayer(this._movieHolder.getMovie()));
        }
        if (Objects.nonNull(this.autoPlaySettings) && this.autoPlaySettings.isActive()) {
            AutoPlayEngine.getInstance().stopAutoPlay(this);
        }
    }
    
    @Override
    public void setIsTeleporting(final boolean teleport) {
        this.setIsTeleporting(teleport, true);
    }
    
    public void setIsTeleporting(final boolean teleport, final boolean useWatchDog) {
        super.setIsTeleporting(teleport);
        if (!useWatchDog) {
            return;
        }
        if (teleport) {
            if (this._teleportWatchdog == null && Config.TELEPORT_WATCHDOG_TIMEOUT > 0) {
                synchronized (this) {
                    if (this._teleportWatchdog == null) {
                        this._teleportWatchdog = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new TeleportWatchdogTask(this), (long)(Config.TELEPORT_WATCHDOG_TIMEOUT * 1000));
                    }
                }
            }
        }
        else if (this._teleportWatchdog != null) {
            this._teleportWatchdog.cancel(false);
            this._teleportWatchdog = null;
        }
    }
    
    public void setLastServerPosition(final int x, final int y, final int z) {
        this._lastServerPosition.setXYZ(x, y, z);
    }
    
    public Location getLastServerPosition() {
        return this._lastServerPosition;
    }
    
    @Override
    public void addExpAndSp(final double addToExp, final double addToSp) {
        this.getStats().addExpAndSp(addToExp, addToSp, false);
    }
    
    public void addExpAndSp(final double addToExp, final double addToSp, final boolean useVitality) {
        this.getStats().addExpAndSp(addToExp, addToSp, useVitality);
    }
    
    public void removeExpAndSp(final long removeExp, final long removeSp) {
        this.getStats().removeExpAndSp(removeExp, removeSp, true);
    }
    
    public void broadcastSnoop(final ChatType type, final String name, final String _text) {
        if (!this._snoopListener.isEmpty()) {
            final Snoop sn = new Snoop(this.getObjectId(), this.getName(), type, name, _text);
            for (final Player pci : this._snoopListener) {
                if (pci != null) {
                    pci.sendPacket(sn);
                }
            }
        }
    }
    
    public void addSnooper(final Player pci) {
        if (!this._snoopListener.contains(pci)) {
            this._snoopListener.add(pci);
        }
    }
    
    public void removeSnooper(final Player pci) {
        this._snoopListener.remove(pci);
    }
    
    public void addSnooped(final Player pci) {
        if (!this._snoopedPlayer.contains(pci)) {
            this._snoopedPlayer.add(pci);
        }
    }
    
    public void removeSnooped(final Player pci) {
        this._snoopedPlayer.remove(pci);
    }
    
    public void addHtmlAction(final HtmlActionScope scope, final String action) {
        this.htmlActionCaches[scope.ordinal()].add(action);
    }
    
    public void clearHtmlActions(final HtmlActionScope scope) {
        this.htmlActionCaches[scope.ordinal()].clear();
    }
    
    public void setHtmlActionOriginObjectId(final HtmlActionScope scope, final int npcObjId) {
        if (npcObjId < 0) {
            throw new IllegalArgumentException();
        }
        this._htmlActionOriginObjectIds[scope.ordinal()] = npcObjId;
    }
    
    public int getLastHtmlActionOriginId() {
        return this._lastHtmlActionOriginObjId;
    }
    
    public void setLastHtmlActionOriginId(final int objId) {
        this._lastHtmlActionOriginObjId = objId;
    }
    
    private boolean validateHtmlAction(final Iterable<String> actionIter, final String action) {
        for (final String cachedAction : actionIter) {
            if (cachedAction.charAt(cachedAction.length() - 1) == '$') {
                if (action.startsWith(cachedAction.substring(0, cachedAction.length() - 1).trim())) {
                    return true;
                }
                continue;
            }
            else {
                if (cachedAction.equals(action)) {
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public int validateHtmlAction(final String action) {
        for (int i = 0; i < this.htmlActionCaches.length; ++i) {
            if (this.validateHtmlAction(this.htmlActionCaches[i], action)) {
                return this._lastHtmlActionOriginObjId = this._htmlActionOriginObjectIds[i];
            }
        }
        return -1;
    }
    
    public boolean validateItemManipulation(final int objectId, final String action) {
        final Item item = this.inventory.getItemByObjectId(objectId);
        if (Objects.isNull(item) || item.getOwnerId() != this.getObjectId()) {
            Player.LOGGER.debug("player {} tried to {} item he is not owner of", (Object)this, (Object)action);
            return false;
        }
        return (!Objects.nonNull(this.pet) || this.pet.getControlObjectId() != objectId) && this.mountObjectID != objectId && !this.isProcessingItem(objectId);
    }
    
    public boolean isInBoat() {
        return this._vehicle != null && this._vehicle.isBoat();
    }
    
    public Boat getBoat() {
        return (Boat)this._vehicle;
    }
    
    public boolean isInShuttle() {
        return this._vehicle instanceof Shuttle;
    }
    
    public Shuttle getShuttle() {
        return (Shuttle)this._vehicle;
    }
    
    public Vehicle getVehicle() {
        return this._vehicle;
    }
    
    public void setVehicle(final Vehicle v) {
        if (v == null && this._vehicle != null) {
            this._vehicle.removePassenger(this);
        }
        this._vehicle = v;
    }
    
    public boolean isInVehicle() {
        return this._vehicle != null;
    }
    
    public boolean isInCrystallize() {
        return this._inCrystallize;
    }
    
    public void setInCrystallize(final boolean inCrystallize) {
        this._inCrystallize = inCrystallize;
    }
    
    public Location getInVehiclePosition() {
        return this._inVehiclePosition;
    }
    
    public void setInVehiclePosition(final Location pt) {
        this._inVehiclePosition = pt;
    }
    
    @Override
    public boolean deleteMe() {
        EventDispatcher.getInstance().notifyEventAsync(new OnPlayerLogout(this), this);
        AutoPlayEngine.getInstance().stopTasks(this);
        try {
            for (final Zone zone : ZoneManager.getInstance().getZones(this)) {
                zone.onPlayerLogoutInside(this);
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            if (!this._isOnline) {
                Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this), (Throwable)new RuntimeException());
            }
            this.setOnlineStatus(false, true);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            if (Config.ENABLE_BLOCK_CHECKER_EVENT && this._handysBlockCheckerEventArena != -1) {
                HandysBlockCheckerManager.getInstance().onDisconnect(this);
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            this._isOnline = false;
            this.abortAttack();
            this.abortCast();
            this.stopMove(null);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            if (this._matchingRoom != null) {
                this._matchingRoom.deleteMember(this, false);
            }
            MatchingRoomManager.getInstance().removeFromWaitingList(this);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            if (this.isFlying()) {
                this.removeSkill(SkillEngine.getInstance().getSkill(CommonSkill.WYVERN_BREATH.getId(), 1));
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            this.storeRecommendations();
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            this.stopAllTimers();
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            this.setIsTeleporting(false);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            RecipeController.getInstance().requestMakeItemAbort(this);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        try {
            this.setTarget(null);
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        if (this.isChannelized()) {
            this.getSkillChannelized().abortChannelization();
        }
        this.getEffectList().stopAllToggles();
        ZoneManager.getInstance().getRegion(this).removeFromZones(this);
        try {
            this.decayMe();
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        if (this.isInParty()) {
            try {
                this.leaveParty();
            }
            catch (Exception e) {
                Player.LOGGER.error("deleteMe()", (Throwable)e);
            }
        }
        if (OlympiadManager.getInstance().isRegistered(this) || this.getOlympiadGameId() != -1) {
            OlympiadManager.getInstance().removeDisconnectedCompetitor(this);
        }
        if (this.hasSummon()) {
            try {
                Summon pet = this.pet;
                if (pet != null) {
                    pet.setRestoreSummon(true);
                    pet.unSummon(this);
                    pet = this.pet;
                    if (pet != null) {
                        pet.broadcastNpcInfo(0);
                    }
                }
                this.getServitors().values().forEach(s -> {
                    s.setRestoreSummon(true);
                    s.unSummon(this);
                    return;
                });
            }
            catch (Exception e) {
                Player.LOGGER.error("deleteMe()", (Throwable)e);
            }
        }
        if (this._clan != null) {
            try {
                final ClanMember clanMember = this._clan.getClanMember(this.getObjectId());
                if (clanMember != null) {
                    clanMember.setPlayerInstance(null);
                }
            }
            catch (Exception e) {
                Player.LOGGER.error("deleteMe()", (Throwable)e);
            }
        }
        if (this.getActiveRequester() != null) {
            this.setActiveRequester(null);
            this.cancelActiveTrade();
        }
        if (this.isGM()) {
            try {
                AdminData.getInstance().deleteGm(this);
            }
            catch (Exception e) {
                Player.LOGGER.error("deleteMe()", (Throwable)e);
            }
        }
        try {
            if (this._observerMode) {
                this.setLocationInvisible(this._lastLoc);
            }
            if (this._vehicle != null) {
                this._vehicle.oustPlayer(this);
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("deleteMe()", (Throwable)e);
        }
        final Instance inst = this.getInstanceWorld();
        if (inst != null) {
            try {
                inst.onPlayerLogout(this);
            }
            catch (Exception e2) {
                Player.LOGGER.error("deleteMe()", (Throwable)e2);
            }
        }
        try {
            this.stopCubics();
        }
        catch (Exception e2) {
            Player.LOGGER.error("deleteMe()", (Throwable)e2);
        }
        try {
            this.inventory.deleteMe();
        }
        catch (Exception e2) {
            Player.LOGGER.error("deleteMe()", (Throwable)e2);
        }
        try {
            this.clearWarehouse();
        }
        catch (Exception e2) {
            Player.LOGGER.error("deleteMe()", (Throwable)e2);
        }
        if (Config.WAREHOUSE_CACHE) {
            WarehouseCacheManager.getInstance().remCacheTask(this);
        }
        try {
            this._freight.deleteMe();
        }
        catch (Exception e2) {
            Player.LOGGER.error("deleteMe()", (Throwable)e2);
        }
        try {
            this.clearRefund();
        }
        catch (Exception e2) {
            Player.LOGGER.error("deleteMe()", (Throwable)e2);
        }
        if (this.clanId > 0) {
            this._clan.broadcastToOtherOnlineMembers(new PledgeShowMemberListUpdate(this), this);
            this._clan.broadcastToOnlineMembers(new ExPledgeCount(this._clan));
        }
        for (final Player player : this._snoopedPlayer) {
            player.removeSnooper(this);
        }
        for (final Player player : this._snoopListener) {
            player.removeSnooped(this);
        }
        if (this.isMentee()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMenteeStatus(this, false), this);
        }
        else if (this.isMentor()) {
            EventDispatcher.getInstance().notifyEventAsync(new OnPlayerMentorStatus(this, false), this);
        }
        if (Event.isParticipant(this)) {
            Event.savePlayerEventStatus(this);
        }
        try {
            this.notifyFriends(0);
            this._blockList.playerLogout();
        }
        catch (Exception e2) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e2.getMessage()), (Throwable)e2);
        }
        this.getEffectList().stopAllPassives(false, false);
        this.getEffectList().stopAllOptions(false, false);
        SaveTaskManager.getInstance().remove(this);
        return super.deleteMe();
    }
    
    public int getInventoryLimit() {
        int ivlim = Config.INVENTORY_MAXIMUM_NO_DWARF;
        if (this.isGM()) {
            ivlim = Config.INVENTORY_MAXIMUM_GM;
        }
        else if (this.getRace() == Race.DWARF) {
            ivlim = Config.INVENTORY_MAXIMUM_DWARF;
        }
        return ivlim + (int)this.getStats().getValue(Stat.INVENTORY_NORMAL, 0.0);
    }
    
    public int getWareHouseLimit() {
        int whlim;
        if (this.getRace() == Race.DWARF) {
            whlim = Config.WAREHOUSE_SLOTS_DWARF;
        }
        else {
            whlim = Config.WAREHOUSE_SLOTS_NO_DWARF;
        }
        whlim += (int)this.getStats().getValue(Stat.STORAGE_PRIVATE, 0.0);
        return whlim;
    }
    
    public int getPrivateSellStoreLimit() {
        int pslim;
        if (this.getRace() == Race.DWARF) {
            pslim = Config.MAX_PVTSTORESELL_SLOTS_DWARF;
        }
        else {
            pslim = Config.MAX_PVTSTORESELL_SLOTS_OTHER;
        }
        pslim += (int)this.getStats().getValue(Stat.TRADE_SELL, 0.0);
        return pslim;
    }
    
    public int getPrivateBuyStoreLimit() {
        int pblim;
        if (this.getRace() == Race.DWARF) {
            pblim = Config.MAX_PVTSTOREBUY_SLOTS_DWARF;
        }
        else {
            pblim = Config.MAX_PVTSTOREBUY_SLOTS_OTHER;
        }
        pblim += (int)this.getStats().getValue(Stat.TRADE_BUY, 0.0);
        return pblim;
    }
    
    public int getDwarfRecipeLimit() {
        int recdlim = Config.DWARF_RECIPE_LIMIT;
        recdlim += (int)this.getStats().getValue(Stat.RECIPE_DWARVEN, 0.0);
        return recdlim;
    }
    
    public int getCommonRecipeLimit() {
        int recclim = Config.COMMON_RECIPE_LIMIT;
        recclim += (int)this.getStats().getValue(Stat.RECIPE_COMMON, 0.0);
        return recclim;
    }
    
    public int getMountNpcId() {
        return this._mountNpcId;
    }
    
    public int getMountLevel() {
        return this._mountLevel;
    }
    
    public int getMountObjectID() {
        return this.mountObjectID;
    }
    
    public void setMountObjectID(final int newID) {
        this.mountObjectID = newID;
    }
    
    public SkillUseHolder getQueuedSkill() {
        return this._queuedSkill;
    }
    
    public void setQueuedSkill(final Skill queuedSkill, final Item item, final boolean ctrlPressed, final boolean shiftPressed) {
        if (queuedSkill == null) {
            this._queuedSkill = null;
            return;
        }
        this._queuedSkill = new SkillUseHolder(queuedSkill, item, ctrlPressed, shiftPressed);
    }
    
    public boolean isJailed() {
        return PunishmentManager.getInstance().hasPunishment(this.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(this.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.JAIL) || PunishmentManager.getInstance().hasPunishment(this.getIPAddress(), PunishmentAffect.IP, PunishmentType.JAIL);
    }
    
    public boolean isChatBanned() {
        return PunishmentManager.getInstance().hasPunishment(this.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(this.getAccountName(), PunishmentAffect.ACCOUNT, PunishmentType.CHAT_BAN) || PunishmentManager.getInstance().hasPunishment(this.getIPAddress(), PunishmentAffect.IP, PunishmentType.CHAT_BAN);
    }
    
    public void startFameTask(final long delay, final int fameFixRate) {
        if (this.getLevel() < 40 || this.getClassId().level() < 2) {
            return;
        }
        if (this._fameTask == null) {
            this._fameTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new FameTask(this, fameFixRate), delay, delay);
        }
    }
    
    public void stopFameTask() {
        if (this._fameTask != null) {
            this._fameTask.cancel(false);
            this._fameTask = null;
        }
    }
    
    public int getPowerGrade() {
        return this.data.getPowerGrade();
    }
    
    public void setPowerGrade(final int power) {
        this.data.setPowerGrade(power);
    }
    
    public int getChargedSouls() {
        return this._souls;
    }
    
    public void increaseSouls(final int count) {
        this._souls += count;
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_IT_IS_NOW_AT_S2);
        sm.addInt(count);
        sm.addInt(this._souls);
        this.sendPacket(sm);
        this.restartSoulTask();
        this.sendPacket(new EtcStatusUpdate(this));
    }
    
    public boolean decreaseSouls(final int count, final Skill skill) {
        this._souls -= count;
        if (this._souls < 0) {
            this._souls = 0;
        }
        if (this._souls == 0) {
            this.stopSoulTask();
        }
        else {
            this.restartSoulTask();
        }
        this.sendPacket(new EtcStatusUpdate(this));
        return true;
    }
    
    public void clearSouls() {
        this._souls = 0;
        this.stopSoulTask();
        this.sendPacket(new EtcStatusUpdate(this));
    }
    
    private void restartSoulTask() {
        if (this._soulTask != null) {
            this._soulTask.cancel(false);
            this._soulTask = null;
        }
        this._soulTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ResetSoulsTask(this), 600000L);
    }
    
    public void stopSoulTask() {
        if (this._soulTask != null) {
            this._soulTask.cancel(false);
            this._soulTask = null;
        }
    }
    
    @Override
    public Player getActingPlayer() {
        return this;
    }
    
    @Override
    public void sendDamageMessage(final Creature target, final Skill skill, final int damage, final double elementalDamage, final boolean crit, final boolean miss) {
        if (miss) {
            if (skill == null) {
                if (GameUtils.isPlayer(target)) {
                    final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_EVADED_C2_S_ATTACK);
                    sm.addPcName(target.getActingPlayer());
                    sm.addString(this.getName());
                    target.sendPacket(sm);
                }
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_S_ATTACK_WENT_ASTRAY);
                sm.addPcName(this);
                this.sendPacket(sm);
            }
            else {
                this.sendPacket(new ExMagicAttackInfo(this.getObjectId(), target.getObjectId(), 4));
            }
            return;
        }
        if (crit) {
            if (skill == null || !skill.isMagic()) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_LANDED_A_CRITICAL_HIT);
                sm.addPcName(this);
                this.sendPacket(sm);
            }
            else {
                this.sendPacket(SystemMessageId.M_CRITICAL);
            }
            if (skill != null) {
                this.sendPacket(new ExMagicAttackInfo(this.getObjectId(), target.getObjectId(), 1));
            }
        }
        if (this.isInOlympiadMode() && GameUtils.isPlayer(target) && target.getActingPlayer().isInOlympiadMode() && target.getActingPlayer().getOlympiadGameId() == this.getOlympiadGameId()) {
            OlympiadGameManager.getInstance().notifyCompetitorDamage(this, damage);
        }
        SystemMessage sm = null;
        if ((target.isHpBlocked() && !GameUtils.isNpc(target)) || (GameUtils.isPlayer(target) && target.isAffected(EffectFlag.DUELIST_FURY) && !this.isAffected(EffectFlag.FACEOFF))) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THE_ATTACK_HAS_BEEN_BLOCKED);
        }
        else if (GameUtils.isDoor(target) || target instanceof ControlTower) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HIT_FOR_S1_DAMAGE);
            sm.addInt(damage);
        }
        else if (this != target) {
            if (elementalDamage != 0.0) {
                sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_INFLICTED_S3_S4_ATTRIBUTE_DAMGE_DAMAGE_TO_S2);
            }
            else {
                sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_INFLICTED_S3_DAMAGE_ON_C2);
            }
            sm.addPcName(this);
            sm.addString(target.getName());
            sm.addInt(damage);
            if (elementalDamage != 0.0) {
                sm.addInt((int)elementalDamage);
            }
            sm.addPopup(target.getObjectId(), this.getObjectId(), -damage);
        }
        if (sm != null) {
            this.sendPacket(sm);
        }
    }
    
    public int getAgathionId() {
        return this._agathionId;
    }
    
    public void setAgathionId(final int npcId) {
        this._agathionId = npcId;
    }
    
    public int getVitalityPoints() {
        return this.getStats().getVitalityPoints();
    }
    
    public void setVitalityPoints(final int points, final boolean quiet) {
        this.getStats().setVitalityPoints(points, quiet);
    }
    
    public void updateVitalityPoints(final int points, final boolean useRates, final boolean quiet) {
        this.getStats().updateVitalityPoints(points, useRates, quiet);
    }
    
    public void checkItemRestriction() {
        for (final InventorySlot slot : InventorySlot.values()) {
            final Item item = this.inventory.getPaperdollItem(slot);
            if (Objects.nonNull(item) && !item.getTemplate().checkCondition(this, this, false)) {
                this.inventory.unEquipItemInSlot(slot);
                final InventoryUpdate iu = new InventoryUpdate();
                iu.addModifiedItem(item);
                this.sendInventoryUpdate(iu);
                if (item.getBodyPart() == BodyPart.BACK) {
                    this.sendPacket(SystemMessageId.YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE);
                    return;
                }
                SystemMessage sm;
                if (item.getEnchantLevel() > 0) {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED).addInt(item.getEnchantLevel()).addItemName(item);
                }
                else {
                    sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_UNEQUIPPED).addItemName(item);
                }
                this.sendPacket(sm);
            }
        }
    }
    
    public void addTransformSkill(final Skill skill) {
        if (this._transformSkills == null) {
            synchronized (this) {
                if (this._transformSkills == null) {
                    this._transformSkills = new HashMap<Integer, Skill>();
                }
            }
        }
        this._transformSkills.put(skill.getId(), skill);
    }
    
    public boolean hasTransformSkill(final Skill skill) {
        return this._transformSkills != null && this._transformSkills.get(skill.getId()) == skill;
    }
    
    public boolean hasTransformSkills() {
        return this._transformSkills != null;
    }
    
    public Collection<Skill> getAllTransformSkills() {
        final Map<Integer, Skill> transformSkills = this._transformSkills;
        return (Collection<Skill>)((transformSkills != null) ? transformSkills.values() : Collections.emptyList());
    }
    
    public synchronized void removeAllTransformSkills() {
        this._transformSkills = null;
    }
    
    @Override
    public final Skill getKnownSkill(final int skillId) {
        final Map<Integer, Skill> transformSkills = this._transformSkills;
        return (transformSkills != null) ? transformSkills.getOrDefault(skillId, super.getKnownSkill(skillId)) : super.getKnownSkill(skillId);
    }
    
    public Collection<Skill> getSkillList() {
        Collection<Skill> currentSkills = this.getAllSkills();
        if (this.isTransformed()) {
            final Map<Integer, Skill> transformSkills = this._transformSkills;
            if (transformSkills != null) {
                currentSkills = currentSkills.stream().filter(Skill::allowOnTransform).collect((Collector<? super Skill, ?, Collection<Skill>>)Collectors.toList());
                if (this.isDualClassActive()) {
                    int revelationSkill = this.getRevelationSkillDualClass1();
                    if (revelationSkill != 0) {
                        this.addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                    revelationSkill = this.getRevelationSkillDualClass2();
                    if (revelationSkill != 0) {
                        this.addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                }
                else if (!this.isSubClassActive()) {
                    int revelationSkill = this.getRevelationSkillMainClass1();
                    if (revelationSkill != 0) {
                        this.addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                    revelationSkill = this.getRevelationSkillMainClass2();
                    if (revelationSkill != 0) {
                        this.addSkill(SkillEngine.getInstance().getSkill(revelationSkill, 1), false);
                    }
                }
                currentSkills.addAll(transformSkills.values());
            }
        }
        return currentSkills.stream().filter(Objects::nonNull).filter(s -> !s.isBlockActionUseSkill()).collect((Collector<? super Skill, ?, Collection<Skill>>)Collectors.toList());
    }
    
    protected void startFeed(final int npcId) {
        this._canFeed = (npcId > 0);
        if (!this.isMounted()) {
            return;
        }
        if (this.hasPet()) {
            this.setCurrentFeed(this.pet.getCurrentFed());
            this._controlItemId = this.pet.getControlObjectId();
            this.sendPacket(new SetupGauge(3, this._curFeed * 10000 / this.getFeedConsume(), this.getMaxFeed() * 10000 / this.getFeedConsume()));
            if (!this.isDead()) {
                this._mountFeedTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new PetFeedTask(this), 10000L, 10000L);
            }
        }
        else if (this._canFeed) {
            this.setCurrentFeed(this.getMaxFeed());
            final SetupGauge sg = new SetupGauge(3, this._curFeed * 10000 / this.getFeedConsume(), this.getMaxFeed() * 10000 / this.getFeedConsume());
            this.sendPacket(sg);
            if (!this.isDead()) {
                this._mountFeedTask = (Future<?>)ThreadPool.scheduleAtFixedRate((Runnable)new PetFeedTask(this), 10000L, 10000L);
            }
        }
    }
    
    public void stopFeed() {
        if (this._mountFeedTask != null) {
            this._mountFeedTask.cancel(false);
            this._mountFeedTask = null;
        }
    }
    
    private void clearPetData() {
        this._data = null;
    }
    
    public final PetData getPetData(final int npcId) {
        if (this._data == null) {
            this._data = PetDataTable.getInstance().getPetData(npcId);
        }
        return this._data;
    }
    
    private PetLevelData getPetLevelData(final int npcId) {
        if (this._leveldata == null) {
            this._leveldata = PetDataTable.getInstance().getPetData(npcId).getPetLevelData(this.getMountLevel());
        }
        return this._leveldata;
    }
    
    public int getCurrentFeed() {
        return this._curFeed;
    }
    
    public void setCurrentFeed(final int num) {
        final boolean lastHungryState = this.isHungry();
        this._curFeed = ((num > this.getMaxFeed()) ? this.getMaxFeed() : num);
        final SetupGauge sg = new SetupGauge(3, this._curFeed * 10000 / this.getFeedConsume(), this.getMaxFeed() * 10000 / this.getFeedConsume());
        this.sendPacket(sg);
        if (lastHungryState != this.isHungry()) {
            this.broadcastUserInfo();
        }
    }
    
    public int getFeedConsume() {
        if (this.isAttackingNow()) {
            return this.getPetLevelData(this._mountNpcId).getPetFeedBattle();
        }
        return this.getPetLevelData(this._mountNpcId).getPetFeedNormal();
    }
    
    private int getMaxFeed() {
        return this.getPetLevelData(this._mountNpcId).getPetMaxFeed();
    }
    
    public boolean isHungry() {
        return this.hasPet() && this._canFeed && this._curFeed < this.getPetData(this._mountNpcId).getHungryLimit() / 100.0f * this.getPetLevelData(this._mountNpcId).getPetMaxFeed();
    }
    
    public void enteredNoLanding(final int delay) {
        this._dismountTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new DismountTask(this), (long)(delay * 1000));
    }
    
    public void exitedNoLanding() {
        if (this._dismountTask != null) {
            this._dismountTask.cancel(true);
            this._dismountTask = null;
        }
    }
    
    public void storePetFood(final int petId) {
        if (this._controlItemId != 0 && petId != 0) {
            final String req = "UPDATE pets SET fed=? WHERE item_obj_id = ?";
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement(req);
                    try {
                        statement.setInt(1, this._curFeed);
                        statement.setInt(2, this._controlItemId);
                        statement.executeUpdate();
                        this._controlItemId = 0;
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, petId), (Throwable)e);
            }
        }
    }
    
    public void setIsInSiege(final boolean b) {
        this._isInSiege = b;
    }
    
    public boolean isInSiege() {
        return this._isInSiege;
    }
    
    public void setIsInHideoutSiege(final boolean isInHideoutSiege) {
        this._isInHideoutSiege = isInHideoutSiege;
    }
    
    public boolean isInHideoutSiege() {
        return this._isInHideoutSiege;
    }
    
    public FloodProtectors getFloodProtectors() {
        return this._client.getFloodProtectors();
    }
    
    public boolean isFlyingMounted() {
        return this.checkTransformed(Transform::isFlying);
    }
    
    public int getCharges() {
        return this._charges.get();
    }
    
    public void setCharges(final int count) {
        this.restartChargeTask();
        this._charges.set(count);
    }
    
    public boolean decreaseCharges(final int count) {
        if (this._charges.get() < count) {
            return false;
        }
        if (this._charges.addAndGet(-count) == 0) {
            this.stopChargeTask();
        }
        else {
            this.restartChargeTask();
        }
        this.sendPacket(new EtcStatusUpdate(this));
        return true;
    }
    
    public void clearCharges() {
        this._charges.set(0);
        this.sendPacket(new EtcStatusUpdate(this));
    }
    
    private void restartChargeTask() {
        if (this._chargeTask != null) {
            this._chargeTask.cancel(false);
            this._chargeTask = null;
        }
        this._chargeTask = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)new ResetChargesTask(this), 600000L);
    }
    
    public void stopChargeTask() {
        if (this._chargeTask != null) {
            this._chargeTask.cancel(false);
            this._chargeTask = null;
        }
    }
    
    public void teleportBookmarkModify(final int id, final int icon, final String tag, final String name) {
        final TeleportBookmark bookmark = this._tpbookmarks.get(id);
        if (bookmark != null) {
            bookmark.setIcon(icon);
            bookmark.setTag(tag);
            bookmark.setName(name);
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("UPDATE character_tpbookmark SET icon=?,tag=?,name=? where charId=? AND Id=?");
                    try {
                        statement.setInt(1, icon);
                        statement.setString(2, tag);
                        statement.setString(3, name);
                        statement.setInt(4, this.getObjectId());
                        statement.setInt(5, id);
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
        }
        this.sendPacket(new ExGetBookMarkInfoPacket(this));
    }
    
    public void teleportBookmarkDelete(final int id) {
        if (this._tpbookmarks.remove(id) != null) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    final PreparedStatement statement = con.prepareStatement("DELETE FROM character_tpbookmark WHERE charId=? AND Id=?");
                    try {
                        statement.setInt(1, this.getObjectId());
                        statement.setInt(2, id);
                        statement.execute();
                        if (statement != null) {
                            statement.close();
                        }
                    }
                    catch (Throwable t) {
                        if (statement != null) {
                            try {
                                statement.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t2) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
            }
            catch (Exception e) {
                Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
            }
            this.sendPacket(new ExGetBookMarkInfoPacket(this));
        }
    }
    
    public void teleportBookmarkGo(final int id) {
        if (!this.teleportBookmarkCondition(0)) {
            return;
        }
        if (this.inventory.getInventoryItemCount(13016, 0) == 0L) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM);
            return;
        }
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(13016);
        this.sendPacket(sm);
        final TeleportBookmark bookmark = this._tpbookmarks.get(id);
        if (bookmark != null) {
            this.destroyItem("Consume", this.inventory.getItemByItemId(13016).getObjectId(), 1L, null, false);
            this.teleToLocation(bookmark, false);
        }
        this.sendPacket(new ExGetBookMarkInfoPacket(this));
    }
    
    public boolean teleportBookmarkCondition(final int type) {
        if (this.isInCombat()) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE);
            return false;
        }
        if (this._isInSiege || this._siegeState != 0) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_CLAN_HALL_SIEGE);
            return false;
        }
        if (this._isInDuel || this._startingDuel) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL);
            return false;
        }
        if (this.isFlying()) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING);
            return false;
        }
        if (this._inOlympiadMode) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH);
            return false;
        }
        if (this.hasBlockActions() && this.hasAbnormalType(AbnormalType.PARALYZE)) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE);
            return false;
        }
        if (this.isDead()) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD);
            return false;
        }
        if (this.isInWater()) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER);
            return false;
        }
        if (type == 1 && (this.isInsideZone(ZoneType.SIEGE) || this.isInsideZone(ZoneType.CLAN_HALL) || this.isInsideZone(ZoneType.JAIL) || this.isInsideZone(ZoneType.CASTLE) || this.isInsideZone(ZoneType.NO_SUMMON_FRIEND) || this.isInsideZone(ZoneType.FORT))) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            return false;
        }
        if (this.isInsideZone(ZoneType.NO_BOOKMARK) || this.isInBoat()) {
            if (type == 0) {
                this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA);
            }
            else if (type == 1) {
                this.sendPacket(SystemMessageId.YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA);
            }
            return false;
        }
        return true;
    }
    
    public void teleportBookmarkAdd(final int x, final int y, final int z, final int icon, final String tag, final String name) {
        if (!this.teleportBookmarkCondition(1)) {
            return;
        }
        if (this._tpbookmarks.size() >= this._bookmarkslot) {
            this.sendPacket(SystemMessageId.YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION);
            return;
        }
        if (this.inventory.getInventoryItemCount(20033, 0) == 0L) {
            this.sendPacket(SystemMessageId.YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG);
            return;
        }
        int id;
        for (id = 1; id <= this._bookmarkslot && this._tpbookmarks.containsKey(id); ++id) {}
        this._tpbookmarks.put(id, new TeleportBookmark(id, x, y, z, icon, tag, name));
        this.destroyItem("Consume", this.inventory.getItemByItemId(20033).getObjectId(), 1L, null, false);
        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DISAPPEARED);
        sm.addItemName(20033);
        this.sendPacket(sm);
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("INSERT INTO character_tpbookmark (charId,Id,x,y,z,icon,tag,name) values (?,?,?,?,?,?,?,?)");
                try {
                    statement.setInt(1, this.getObjectId());
                    statement.setInt(2, id);
                    statement.setInt(3, x);
                    statement.setInt(4, y);
                    statement.setInt(5, z);
                    statement.setInt(6, icon);
                    statement.setString(7, tag);
                    statement.setString(8, name);
                    statement.execute();
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, e.getMessage()), (Throwable)e);
        }
        this.sendPacket(new ExGetBookMarkInfoPacket(this));
    }
    
    public void restoreTeleportBookmark() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT Id,x,y,z,icon,tag,name FROM character_tpbookmark WHERE charId=?");
                try {
                    statement.setInt(1, this.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            this._tpbookmarks.put(rset.getInt("Id"), new TeleportBookmark(rset.getInt("Id"), rset.getInt("x"), rset.getInt("y"), rset.getInt("z"), rset.getInt("icon"), rset.getString("tag"), rset.getString("name")));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error("Failed restoring character teleport bookmark.", (Throwable)e);
        }
    }
    
    @Override
    public void sendInfo(final Player player) {
        if (!this.isInvisible() || player.canOverrideCond(PcCondOverride.SEE_ALL_PLAYERS)) {
            player.sendPacket(new ExCharInfo(this));
        }
        if (this.isInBoat() && this.isInvisible()) {
            this.setXYZ(this.getBoat().getLocation());
            player.sendPacket(new GetOnVehicle(this.getObjectId(), this.getBoat().getObjectId(), this._inVehiclePosition));
        }
        final int relation1 = this.getRelation(player);
        final RelationChanged rc1 = new RelationChanged();
        rc1.addRelation(this, relation1, !this.isInsideZone(ZoneType.PEACE));
        if (this.hasSummon()) {
            if (this.pet != null) {
                rc1.addRelation(this.pet, relation1, !this.isInsideZone(ZoneType.PEACE));
            }
            if (this.hasServitors()) {
                this.getServitors().values().forEach(s -> rc1.addRelation(s, relation1, !this.isInsideZone(ZoneType.PEACE)));
            }
        }
        player.sendPacket(rc1);
        final int relation2 = player.getRelation(this);
        final RelationChanged rc2 = new RelationChanged();
        rc2.addRelation(player, relation2, !player.isInsideZone(ZoneType.PEACE));
        if (player.hasSummon()) {
            if (this.pet != null) {
                rc2.addRelation(this.pet, relation2, !player.isInsideZone(ZoneType.PEACE));
            }
            if (this.hasServitors()) {
                this.getServitors().values().forEach(s -> rc2.addRelation(s, relation2, !player.isInsideZone(ZoneType.PEACE)));
            }
        }
        this.sendPacket(rc2);
        switch (this.privateStoreType) {
            case SELL: {
                player.sendPacket(new PrivateStoreMsgSell(this));
                break;
            }
            case PACKAGE_SELL: {
                player.sendPacket(new ExPrivateStoreSetWholeMsg(this));
                break;
            }
            case BUY: {
                player.sendPacket(new PrivateStoreMsgBuy(this));
                break;
            }
            case MANUFACTURE: {
                player.sendPacket(new RecipeShopMsg(this));
                break;
            }
        }
    }
    
    public void playMovie(final MovieHolder holder) {
        if (this._movieHolder != null) {
            return;
        }
        this.abortAttack();
        this.stopMove(null);
        this.setMovieHolder(holder);
        if (!this.isTeleporting()) {
            this.sendPacket(new ExStartScenePlayer(holder.getMovie()));
        }
    }
    
    public void stopMovie() {
        this.sendPacket(new ExStopScenePlayer(this._movieHolder.getMovie()));
        this.setMovieHolder(null);
    }
    
    public boolean isAllowedToEnchantSkills() {
        return !this.isSubClassLocked() && !this.isTransformed() && !AttackStanceTaskManager.getInstance().hasAttackStanceTask(this) && !this.isCastingNow() && !this.isInBoat();
    }
    
    public int getBirthdays() {
        return (int)ChronoUnit.YEARS.between(this.data.getCreateDate(), LocalDate.now());
    }
    
    public IntSet getFriendList() {
        return this.friends;
    }
    
    public void restoreFriendList() {
        this.friends.clear();
        this.friends.addAll((IntCollection)((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findFriendsById(this.getObjectId()));
    }
    
    public void notifyFriends(final int type) {
        final FriendStatus pkt = new FriendStatus(this, type);
        final World word = World.getInstance();
        final IntStream stream = this.friends.stream();
        final World obj = word;
        Objects.requireNonNull(obj);
        final Stream<Object> filter = stream.mapToObj((IntFunction<?>)obj::findPlayer).filter(Objects::nonNull);
        final FriendStatus obj2 = pkt;
        Objects.requireNonNull(obj2);
        filter.forEach((Consumer<? super Object>)obj2::sendTo);
    }
    
    public boolean isSilenceMode() {
        return this._silenceMode;
    }
    
    public void setSilenceMode(final boolean mode) {
        this._silenceMode = mode;
        if (this._silenceModeExcluded != null) {
            this._silenceModeExcluded.clear();
        }
        this.sendPacket(new EtcStatusUpdate(this));
    }
    
    public boolean isSilenceMode(final int playerObjId) {
        if (((ChatSettings)Configurator.getSettings((Class)ChatSettings.class)).silenceModeExclude() && this._silenceMode && Objects.nonNull(this._silenceModeExcluded)) {
            return !this._silenceModeExcluded.contains(playerObjId);
        }
        return this._silenceMode;
    }
    
    public void addSilenceModeExcluded(final int playerObjId) {
        if (this._silenceModeExcluded == null) {
            this._silenceModeExcluded = new ArrayList<Integer>(1);
        }
        this._silenceModeExcluded.add(playerObjId);
    }
    
    private void storeRecipeShopList() {
        if (this.hasManufactureShop()) {
            try {
                final Connection con = DatabaseFactory.getInstance().getConnection();
                try {
                    PreparedStatement st = con.prepareStatement("DELETE FROM character_recipeshoplist WHERE charId=?");
                    try {
                        st.setInt(1, this.getObjectId());
                        st.execute();
                        if (st != null) {
                            st.close();
                        }
                    }
                    catch (Throwable t) {
                        if (st != null) {
                            try {
                                st.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    st = con.prepareStatement("REPLACE INTO character_recipeshoplist (`charId`, `recipeId`, `price`, `index`) VALUES (?, ?, ?, ?)");
                    try {
                        final AtomicInteger slot = new AtomicInteger(1);
                        con.setAutoCommit(false);
                        for (final ManufactureItem item : this._manufactureItems.values()) {
                            st.setInt(1, this.getObjectId());
                            st.setInt(2, item.getRecipeId());
                            st.setLong(3, item.getCost());
                            st.setInt(4, slot.getAndIncrement());
                            st.addBatch();
                        }
                        st.executeBatch();
                        con.commit();
                        if (st != null) {
                            st.close();
                        }
                    }
                    catch (Throwable t2) {
                        if (st != null) {
                            try {
                                st.close();
                            }
                            catch (Throwable exception2) {
                                t2.addSuppressed(exception2);
                            }
                        }
                        throw t2;
                    }
                    if (con != null) {
                        con.close();
                    }
                }
                catch (Throwable t3) {
                    if (con != null) {
                        try {
                            con.close();
                        }
                        catch (Throwable exception3) {
                            t3.addSuppressed(exception3);
                        }
                    }
                    throw t3;
                }
            }
            catch (Exception e) {
                Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
            }
        }
    }
    
    private void restoreRecipeShopList() {
        if (this._manufactureItems != null) {
            this._manufactureItems.clear();
        }
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT * FROM character_recipeshoplist WHERE charId=? ORDER BY `index`");
                try {
                    statement.setInt(1, this.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        while (rset.next()) {
                            this.getManufactureItems().put(rset.getInt("recipeId"), new ManufactureItem(rset.getInt("recipeId"), rset.getLong("price")));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
    }
    
    @Override
    public double getCollisionRadius() {
        if (this.isMounted() && this._mountNpcId > 0) {
            return NpcData.getInstance().getTemplate(this.getMountNpcId()).getfCollisionRadius();
        }
        final double defaultCollisionRadius = this.appearance.isFemale() ? this.getBaseTemplate().getFCollisionRadiusFemale() : this.getBaseTemplate().getfCollisionRadius();
        return this.getTransformation().map(transform -> transform.getCollisionRadius(this, defaultCollisionRadius)).orElse(defaultCollisionRadius);
    }
    
    @Override
    public double getCollisionHeight() {
        if (this.isMounted() && this._mountNpcId > 0) {
            return NpcData.getInstance().getTemplate(this.getMountNpcId()).getfCollisionHeight();
        }
        final double defaultCollisionHeight = this.appearance.isFemale() ? this.getBaseTemplate().getFCollisionHeightFemale() : this.getBaseTemplate().getfCollisionHeight();
        return this.getTransformation().map(transform -> transform.getCollisionHeight(this, defaultCollisionHeight)).orElse(defaultCollisionHeight);
    }
    
    public final void setClientX(final int val) {
        this._clientX = val;
    }
    
    public final void setClientY(final int val) {
        this._clientY = val;
    }
    
    public final int getClientZ() {
        return this._clientZ;
    }
    
    public final void setClientZ(final int val) {
        this._clientZ = val;
    }
    
    public final void setClientHeading(final int val) {
        this._clientHeading = val;
    }
    
    public final boolean isFalling(final int z) {
        if (this.isDead() || this.isFlying() || this.isFlyingMounted() || this.isInsideZone(ZoneType.WATER)) {
            return false;
        }
        if (this._fallingTimestamp > 0L && System.currentTimeMillis() < this._fallingTimestamp) {
            return true;
        }
        final int deltaZ = this.getZ() - z;
        if (deltaZ <= this.getBaseTemplate().getSafeFallHeight()) {
            this._fallingTimestamp = 0L;
            return false;
        }
        if (!GeoEngine.getInstance().hasGeo(this.getX(), this.getY())) {
            this._fallingTimestamp = 0L;
            return false;
        }
        if (this._fallingDamage == 0) {
            this._fallingDamage = (int)Formulas.calcFallDam(this, deltaZ);
        }
        if (this._fallingDamageTask != null) {
            this._fallingDamageTask.cancel(true);
        }
        this._fallingDamageTask = (Future<?>)ThreadPool.schedule(() -> {
            if (this._fallingDamage > 0 && !this.isInvul()) {
                this.reduceCurrentHp(Math.min(this._fallingDamage, this.getCurrentHp() - 1.0), this, null, false, true, false, false, DamageInfo.DamageType.FALL);
                this.sendPacket(((AbstractMessagePacket<ServerPacket>)SystemMessage.getSystemMessage(SystemMessageId.YOU_RECEIVED_S1_FALLING_DAMAGE)).addInt(this._fallingDamage));
            }
            this._fallingDamage = 0;
            this._fallingDamageTask = null;
            return;
        }, 1500L);
        this.sendPacket(new ValidateLocation(this));
        this.setFalling();
        return false;
    }
    
    public final void setFalling() {
        this._fallingTimestamp = System.currentTimeMillis() + 1000L;
    }
    
    public MovieHolder getMovieHolder() {
        return this._movieHolder;
    }
    
    public void setMovieHolder(final MovieHolder movie) {
        this._movieHolder = movie;
    }
    
    public void updateLastItemAuctionRequest() {
        this._lastItemAuctionInfoRequest = System.currentTimeMillis();
    }
    
    public boolean isItemAuctionPolling() {
        return System.currentTimeMillis() - this._lastItemAuctionInfoRequest < 2000L;
    }
    
    @Override
    public boolean isMovementDisabled() {
        return super.isMovementDisabled() || this._movieHolder != null || this._fishing.isFishing();
    }
    
    public String getHtmlPrefix() {
        if (!Config.MULTILANG_ENABLE) {
            return null;
        }
        return this._htmlPrefix;
    }
    
    public String getLang() {
        return this._lang;
    }
    
    public boolean setLang(final String lang) {
        boolean result = false;
        if (Config.MULTILANG_ENABLE) {
            if (Config.MULTILANG_ALLOWED.contains(lang)) {
                this._lang = lang;
                result = true;
            }
            else {
                this._lang = Config.MULTILANG_DEFAULT;
            }
            this._htmlPrefix = (this._lang.equals("en") ? "" : invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, this._lang));
        }
        else {
            this._lang = null;
            this._htmlPrefix = null;
        }
        return result;
    }
    
    public long getOfflineStartTime() {
        return this._offlineShopStart;
    }
    
    public void setOfflineStartTime(final long time) {
        this._offlineShopStart = time;
    }
    
    public int getPcCafePoints() {
        return this.data.getPcCafePoints();
    }
    
    public void setPcCafePoints(final int count) {
        this.data.setPcCafePoints(Math.min(count, Config.PC_CAFE_MAX_POINTS));
    }
    
    public void checkPlayerSkills() {
        for (final Map.Entry<Integer, Skill> e : this.getSkills().entrySet()) {
            final SkillLearn learn = SkillTreesData.getInstance().getClassSkill(e.getKey(), e.getValue().getLevel() % 100, this.getClassId());
            if (learn != null) {
                final int lvlDiff = (e.getKey() == CommonSkill.EXPERTISE.getId()) ? 0 : 9;
                if (this.getLevel() >= learn.getGetLevel() - lvlDiff) {
                    continue;
                }
                this.deacreaseSkillLevel(e.getValue(), lvlDiff);
            }
        }
    }
    
    private void deacreaseSkillLevel(final Skill skill, final int lvlDiff) {
        int nextLevel = -1;
        final LongMap<SkillLearn> skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(this.getClassId());
        for (final SkillLearn sl : skillTree.values()) {
            if (sl.getSkillId() == skill.getId() && nextLevel < sl.getSkillLevel() && this.getLevel() >= sl.getGetLevel() - lvlDiff) {
                nextLevel = sl.getSkillLevel();
            }
        }
        if (nextLevel == -1) {
            Player.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/engine/skill/api/Skill;Ljava/lang/String;)Ljava/lang/String;, skill, this.toString()));
            this.removeSkill(skill, true);
        }
        else {
            Player.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/engine/skill/api/Skill;ILjava/lang/String;)Ljava/lang/String;, skill, nextLevel, this.toString()));
            this.addSkill(SkillEngine.getInstance().getSkill(skill.getId(), nextLevel), true);
        }
    }
    
    public boolean canMakeSocialAction() {
        return this.privateStoreType == PrivateStoreType.NONE && this.getActiveRequester() == null && !this.isAlikeDead() && !this.isAllSkillsDisabled() && !this.isCastingNow() && this.getAI().getIntention() == CtrlIntention.AI_INTENTION_IDLE;
    }
    
    public void setMultiSocialAction(final int id, final int targetId) {
        this._multiSociaAction = id;
        this._multiSocialTarget = targetId;
    }
    
    public int getMultiSociaAction() {
        return this._multiSociaAction;
    }
    
    public int getMultiSocialTarget() {
        return this._multiSocialTarget;
    }
    
    public Collection<TeleportBookmark> getTeleportBookmarks() {
        return this._tpbookmarks.values();
    }
    
    public int getBookmarkslot() {
        return this._bookmarkslot;
    }
    
    public int getQuestInventoryLimit() {
        return Config.INVENTORY_MAXIMUM_QUEST_ITEMS;
    }
    
    public boolean canAttackCharacter(final Creature cha) {
        if (GameUtils.isAttackable(cha)) {
            return true;
        }
        if (GameUtils.isPlayable(cha)) {
            if (cha.isInsideZone(ZoneType.PVP) && !cha.isInsideZone(ZoneType.SIEGE)) {
                return true;
            }
            final Player target = (Player)(GameUtils.isSummon(cha) ? ((Summon)cha).getOwner() : cha);
            if (this.isInDuel() && target.isInDuel() && target.getDuelId() == this.getDuelId()) {
                return true;
            }
            if (this.isInParty() && target.isInParty()) {
                if (this.getParty() == target.getParty()) {
                    return false;
                }
                if ((this.getParty().getCommandChannel() != null || target.getParty().getCommandChannel() != null) && this.getParty().getCommandChannel() == target.getParty().getCommandChannel()) {
                    return false;
                }
            }
            else if (this.getClan() != null && target.getClan() != null) {
                if (this.getClanId() == target.getClanId()) {
                    return false;
                }
                if ((this.getAllyId() > 0 || target.getAllyId() > 0) && this.getAllyId() == target.getAllyId()) {
                    return false;
                }
                if (this.getClan().isAtWarWith(target.getClan().getId()) && target.getClan().isAtWarWith(this.getClan().getId())) {
                    return true;
                }
            }
            else if ((this.getClan() == null || target.getClan() == null) && target.getPvpFlag() == 0 && target.getReputation() >= 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isInventoryUnder90(final boolean includeQuestInv) {
        return this.inventory.getSize(item -> !item.isQuestItem() || includeQuestInv, (Predicate<Item>[])new Predicate[0]) <= this.getInventoryLimit() * 0.9;
    }
    
    public boolean isInventoryUnder80(final boolean includeQuestInv) {
        return this.inventory.getSize(item -> !item.isQuestItem() || includeQuestInv, (Predicate<Item>[])new Predicate[0]) <= this.getInventoryLimit() * 0.8;
    }
    
    public boolean havePetInvItems() {
        return this._petItems;
    }
    
    public void setPetInvItems(final boolean haveit) {
        this._petItems = haveit;
    }
    
    private void restorePetInventoryItems() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT object_id FROM `items` WHERE `owner_id`=? AND (`loc`='PET' OR `loc`='PET_EQUIP') LIMIT 1;");
                try {
                    statement.setInt(1, this.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        this.setPetInvItems(rset.next() && rset.getInt("object_id") > 0);
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
    }
    
    public String getAdminConfirmCmd() {
        return this._adminConfirmCmd;
    }
    
    public void setAdminConfirmCmd(final String adminConfirmCmd) {
        this._adminConfirmCmd = adminConfirmCmd;
    }
    
    public int getBlockCheckerArena() {
        return this._handysBlockCheckerEventArena;
    }
    
    public void setBlockCheckerArena(final byte arena) {
        this._handysBlockCheckerEventArena = arena;
    }
    
    void loadRecommendations() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement statement = con.prepareStatement("SELECT rec_have, rec_left FROM character_reco_bonus WHERE charId = ?");
                try {
                    statement.setInt(1, this.getObjectId());
                    final ResultSet rset = statement.executeQuery();
                    try {
                        if (rset.next()) {
                            this.setRecomHave(rset.getInt("rec_have"));
                            this.setRecomLeft(rset.getInt("rec_left"));
                        }
                        if (rset != null) {
                            rset.close();
                        }
                    }
                    catch (Throwable t) {
                        if (rset != null) {
                            try {
                                rset.close();
                            }
                            catch (Throwable exception) {
                                t.addSuppressed(exception);
                            }
                        }
                        throw t;
                    }
                    if (statement != null) {
                        statement.close();
                    }
                }
                catch (Throwable t2) {
                    if (statement != null) {
                        try {
                            statement.close();
                        }
                        catch (Throwable exception2) {
                            t2.addSuppressed(exception2);
                        }
                    }
                    throw t2;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t3) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception3) {
                        t3.addSuppressed(exception3);
                    }
                }
                throw t3;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
    }
    
    public void storeRecommendations() {
        try {
            final Connection con = DatabaseFactory.getInstance().getConnection();
            try {
                final PreparedStatement ps = con.prepareStatement("REPLACE INTO character_reco_bonus (charId,rec_have,rec_left,time_left) VALUES (?,?,?,?)");
                try {
                    ps.setInt(1, this.getObjectId());
                    ps.setInt(2, this._recomHave);
                    ps.setInt(3, this._recomLeft);
                    ps.setLong(4, 0L);
                    ps.execute();
                    if (ps != null) {
                        ps.close();
                    }
                }
                catch (Throwable t) {
                    if (ps != null) {
                        try {
                            ps.close();
                        }
                        catch (Throwable exception) {
                            t.addSuppressed(exception);
                        }
                    }
                    throw t;
                }
                if (con != null) {
                    con.close();
                }
            }
            catch (Throwable t2) {
                if (con != null) {
                    try {
                        con.close();
                    }
                    catch (Throwable exception2) {
                        t2.addSuppressed(exception2);
                    }
                }
                throw t2;
            }
        }
        catch (Exception e) {
            Player.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, this.getObjectId()), (Throwable)e);
        }
    }
    
    public void startRecoGiveTask() {
        this._recoGiveTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)new RecoGiveTask(this), 7200000L, 3600000L);
        this.storeRecommendations();
    }
    
    public void stopRecoGiveTask() {
        if (this._recoGiveTask != null) {
            this._recoGiveTask.cancel(false);
            this._recoGiveTask = null;
        }
    }
    
    public boolean isRecoTwoHoursGiven() {
        return this._recoTwoHoursGiven;
    }
    
    public void setRecoTwoHoursGiven(final boolean val) {
        this._recoTwoHoursGiven = val;
    }
    
    public String getLastPetitionGmName() {
        return this._lastPetitionGmName;
    }
    
    public void setLastPetitionGmName(final String gmName) {
        this._lastPetitionGmName = gmName;
    }
    
    public ContactList getContactList() {
        return this._contactList;
    }
    
    public void setEventStatus() {
        this.eventStatus = new PlayerEventHolder(this);
    }
    
    public PlayerEventHolder getEventStatus() {
        return this.eventStatus;
    }
    
    public void setEventStatus(final PlayerEventHolder pes) {
        this.eventStatus = pes;
    }
    
    public long getNotMoveUntil() {
        return this._notMoveUntil;
    }
    
    public void updateNotMoveUntil() {
        this._notMoveUntil = System.currentTimeMillis() + Config.PLAYER_MOVEMENT_BLOCK_TIME;
    }
    
    public final Skill getCustomSkill(final int skillId) {
        return (this._customSkills != null) ? this._customSkills.get(skillId) : null;
    }
    
    private void addCustomSkill(final Skill skill) {
        if (skill != null && skill.getDisplayId() != skill.getId()) {
            if (this._customSkills == null) {
                this._customSkills = new ConcurrentSkipListMap<Integer, Skill>();
            }
            this._customSkills.put(skill.getDisplayId(), skill);
        }
    }
    
    private void removeCustomSkill(final Skill skill) {
        if (skill != null && this._customSkills != null && skill.getDisplayId() != skill.getId()) {
            this._customSkills.remove(skill.getDisplayId());
        }
    }
    
    @Override
    public boolean canRevive() {
        if (this._events != null) {
            for (final AbstractEvent<?> listener : this._events.values()) {
                if (listener.isOnEvent(this) && !listener.canRevive(this)) {
                    return false;
                }
            }
        }
        return this._canRevive;
    }
    
    @Override
    public void setCanRevive(final boolean val) {
        this._canRevive = val;
    }
    
    public boolean isOnCustomEvent() {
        return this._isOnCustomEvent;
    }
    
    public void setOnCustomEvent(final boolean value) {
        this._isOnCustomEvent = value;
    }
    
    @Override
    public boolean isOnEvent() {
        if (this._isOnCustomEvent) {
            return true;
        }
        if (this._events != null) {
            for (final AbstractEvent<?> listener : this._events.values()) {
                if (listener.isOnEvent(this)) {
                    return true;
                }
            }
        }
        return super.isOnEvent();
    }
    
    public boolean isBlockedFromExit() {
        if (this._isOnCustomEvent) {
            return true;
        }
        if (this._events != null) {
            for (final AbstractEvent<?> listener : this._events.values()) {
                if (listener.isOnEvent(this) && listener.isBlockingExit(this)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isBlockedFromDeathPenalty() {
        if (this._isOnCustomEvent) {
            return true;
        }
        if (this._events != null) {
            for (final AbstractEvent<?> listener : this._events.values()) {
                if (listener.isOnEvent(this) && listener.isBlockingDeathPenalty(this)) {
                    return true;
                }
            }
        }
        return this.isAffected(EffectFlag.PROTECT_DEATH_PENALTY);
    }
    
    public void setOriginalCpHpMp(final double cp, final double hp, final double mp) {
        this._originalCp = cp;
        this._originalHp = hp;
        this._originalMp = mp;
    }
    
    @Override
    public void addOverrideCond(final PcCondOverride... excs) {
        super.addOverrideCond(excs);
        this.setCondOverrideKey(Long.toString(this._exceptions));
    }
    
    @Override
    public void removeOverridedCond(final PcCondOverride... excs) {
        super.removeOverridedCond(excs);
        this.setCondOverrideKey(Long.toString(this._exceptions));
    }
    
    public void storeVariables() {
        ((PlayerVariablesDAO)DatabaseAccess.getDAO((Class)PlayerVariablesDAO.class)).save((Object)this.variables);
    }
    
    public boolean hasAccountVariables() {
        return this.getScript(AccountVariables.class) != null;
    }
    
    public AccountVariables getAccountVariables() {
        final AccountVariables vars = this.getScript(AccountVariables.class);
        return (vars != null) ? vars : this.addScript(new AccountVariables(this.getAccountName()));
    }
    
    @Override
    public int getId() {
        return this.objectId;
    }
    
    public boolean isPartyBanned() {
        return PunishmentManager.getInstance().hasPunishment(this.getObjectId(), PunishmentAffect.CHARACTER, PunishmentType.PARTY_BAN);
    }
    
    public boolean addAction(final PlayerAction act) {
        if (!this.hasAction(act)) {
            this._actionMask |= act.getMask();
            return true;
        }
        return false;
    }
    
    public boolean removeAction(final PlayerAction act) {
        if (this.hasAction(act)) {
            this._actionMask &= ~act.getMask();
            return true;
        }
        return false;
    }
    
    public boolean hasAction(final PlayerAction act) {
        return (this._actionMask & act.getMask()) == act.getMask();
    }
    
    public void setCharmOfCourage(final boolean val) {
        this.hasCharmOfCourage = val;
    }
    
    public boolean hasCharmOfCourage() {
        return this.hasCharmOfCourage;
    }
    
    public boolean atWarWith(final Playable target) {
        return target != null && (this._clan != null && !this.isAcademyMember() && target.getClan() != null && !target.isAcademyMember()) && this._clan.isAtWarWith(target.getClan());
    }
    
    public boolean isMentor() {
        return MentorManager.getInstance().isMentor(this.getObjectId());
    }
    
    public boolean isMentee() {
        return MentorManager.getInstance().isMentee(this.getObjectId());
    }
    
    public int getAbilityPointsUsed() {
        return this.isDualClassActive() ? this.getAbilityPointsDualClassUsed() : this.getAbilityPointsMainClassUsed();
    }
    
    public int getWorldChatPoints() {
        return (int)this.getStats().getValue(Stat.WORLD_CHAT_POINTS, Config.WORLD_CHAT_POINTS_PER_DAY);
    }
    
    public CastleSide getPlayerSide() {
        if (this._clan == null || this._clan.getCastleId() == 0) {
            return CastleSide.NEUTRAL;
        }
        return CastleManager.getInstance().getCastleById(this.getClan().getCastleId()).getSide();
    }
    
    public boolean isOnDarkSide() {
        return this.getPlayerSide() == CastleSide.DARK;
    }
    
    public boolean isOnLightSide() {
        return this.getPlayerSide() == CastleSide.LIGHT;
    }
    
    public int getMaxSummonPoints() {
        return (int)this.getStats().getValue(Stat.MAX_SUMMON_POINTS, 0.0);
    }
    
    public int getSummonPoints() {
        return this.getServitors().values().stream().mapToInt(Summon::getSummonPoints).sum();
    }
    
    public boolean addRequest(final AbstractRequest request) {
        if (this.requests == null) {
            synchronized (this) {
                if (this.requests == null) {
                    this.requests = new ConcurrentHashMap<Class<? extends AbstractRequest>, AbstractRequest>();
                }
            }
        }
        return this.canRequest(request) && this.requests.putIfAbsent(request.getClass(), request) == null;
    }
    
    public boolean canRequest(final AbstractRequest request) {
        if (this.requests != null) {
            final Stream<AbstractRequest> stream = this.requests.values().stream();
            Objects.requireNonNull(request);
            if (stream.allMatch(request::canWorkWith)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean removeRequest(final Class<? extends AbstractRequest> clazz) {
        return this.requests != null && this.requests.remove(clazz) != null;
    }
    
    public <T extends AbstractRequest> T getRequest(final Class<T> requestClass) {
        return (T)((this.requests != null) ? ((T)requestClass.cast(this.requests.get(requestClass))) : null);
    }
    
    public boolean hasRequests() {
        return this.requests != null && !this.requests.isEmpty();
    }
    
    public boolean hasItemRequest() {
        return Objects.nonNull(this.requests) && this.requests.values().stream().anyMatch(AbstractRequest::isItemRequest);
    }
    
    @SafeVarargs
    public final boolean hasRequest(final Class<? extends AbstractRequest> requestClass, final Class<? extends AbstractRequest>... classes) {
        if (this.requests != null) {
            for (final Class<? extends AbstractRequest> clazz : classes) {
                if (this.requests.containsKey(clazz)) {
                    return true;
                }
            }
            return this.requests.containsKey(requestClass);
        }
        return false;
    }
    
    public boolean isProcessingItem(final int objectId) {
        return Objects.nonNull(this.requests) && this.requests.values().stream().anyMatch(req -> req.isUsing(objectId));
    }
    
    public void removeRequestsThatProcessesItem(final int objectId) {
        if (this.requests != null) {
            this.requests.values().removeIf(req -> req.isUsing(objectId));
        }
    }
    
    public Map<Integer, ExResponseCommissionInfo> getLastCommissionInfos() {
        if (this._lastCommissionInfos == null) {
            synchronized (this) {
                if (this._lastCommissionInfos == null) {
                    this._lastCommissionInfos = new ConcurrentHashMap<Integer, ExResponseCommissionInfo>();
                }
            }
        }
        return this._lastCommissionInfos;
    }
    
    public Set<Integer> getWhisperers() {
        return this._whisperers;
    }
    
    public MatchingRoom getMatchingRoom() {
        return this._matchingRoom;
    }
    
    public void setMatchingRoom(final MatchingRoom matchingRoom) {
        this._matchingRoom = matchingRoom;
    }
    
    public boolean isInMatchingRoom() {
        return this._matchingRoom != null;
    }
    
    @Override
    public boolean isVisibleFor(final Player player) {
        return super.isVisibleFor(player) || (player.getParty() != null && player.getParty() == this.getParty());
    }
    
    public int getQuestZoneId() {
        return this._questZoneId;
    }
    
    public void setQuestZoneId(final int id) {
        this._questZoneId = id;
    }
    
    public void sendInventoryUpdate(final InventoryUpdate iu) {
        this.sendPacket(iu, new ExAdenaInvenCount(this), new ExBloodyCoinCount(), new ExUserInfoInvenWeight(this));
    }
    
    public void sendItemList() {
        ItemList.sendList(this);
        this.sendPacket(new ExQuestItemList(1, this));
        this.sendPacket(new ExQuestItemList(2, this));
        this.sendPacket(new ExAdenaInvenCount(this));
        this.sendPacket(new ExUserInfoInvenWeight(this));
        this.sendPacket(new ExBloodyCoinCount());
    }
    
    public boolean registerOnEvent(final AbstractEvent<?> event) {
        if (this._events == null) {
            synchronized (this) {
                if (this._events == null) {
                    this._events = new ConcurrentHashMap<Class<? extends AbstractEvent>, AbstractEvent<?>>();
                }
            }
        }
        return this._events.putIfAbsent(event.getClass(), event) == null;
    }
    
    public boolean removeFromEvent(final AbstractEvent<?> event) {
        return this._events != null && this._events.remove(event.getClass()) != null;
    }
    
    public <T extends AbstractEvent<?>> T getEvent(final Class<T> clazz) {
        if (this._events == null) {
            return null;
        }
        final Stream<AbstractEvent<?>> filter = this._events.values().stream().filter(event -> clazz.isAssignableFrom(event.getClass()));
        Objects.requireNonNull(clazz);
        return filter.map((Function<? super AbstractEvent<?>, ?>)clazz::cast).findFirst().orElse(null);
    }
    
    public AbstractEvent<?> getEvent() {
        if (this._events == null) {
            return null;
        }
        return this._events.values().stream().findFirst().orElse(null);
    }
    
    public boolean isOnEvent(final Class<? extends AbstractEvent<?>> clazz) {
        return this._events != null && this._events.containsKey(clazz);
    }
    
    public Fishing getFishing() {
        return this._fishing;
    }
    
    public boolean isFishing() {
        return this._fishing.isFishing();
    }
    
    @Override
    public MoveType getMoveType() {
        if (this._waitTypeSitting) {
            return MoveType.SITTING;
        }
        return super.getMoveType();
    }
    
    void startOnlineTimeUpdateTask() {
        if (this._onlineTimeUpdateTask != null) {
            this.stopOnlineTimeUpdateTask();
        }
        this._onlineTimeUpdateTask = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate(this::updateOnlineTime, 60000L, 60000L);
    }
    
    private void updateOnlineTime() {
        if (this._clan != null) {
            this._clan.addMemberOnlineTime(this);
        }
    }
    
    private void stopOnlineTimeUpdateTask() {
        if (this._onlineTimeUpdateTask != null) {
            this._onlineTimeUpdateTask.cancel(true);
            this._onlineTimeUpdateTask = null;
        }
    }
    
    public GroupType getGroupType() {
        return this.isInParty() ? (this._party.isInCommandChannel() ? GroupType.COMMAND_CHANNEL : GroupType.PARTY) : GroupType.NONE;
    }
    
    @Override
    protected void initStatusUpdateCache() {
        super.initStatusUpdateCache();
        this.addStatusUpdateValue(StatusUpdateType.LEVEL);
        this.addStatusUpdateValue(StatusUpdateType.MAX_CP);
        this.addStatusUpdateValue(StatusUpdateType.CUR_CP);
    }
    
    public TrainingHolder getTraingCampInfo() {
        final String info = this.getAccountVariables().getString("TRAINING_CAMP", null);
        if (info == null) {
            return null;
        }
        return new TrainingHolder(Integer.parseInt(info.split(";")[0]), Integer.parseInt(info.split(";")[1]), Integer.parseInt(info.split(";")[2]), Long.parseLong(info.split(";")[3]), Long.parseLong(info.split(";")[4]));
    }
    
    public void setTraingCampInfo(final TrainingHolder holder) {
        this.getAccountVariables().set("TRAINING_CAMP", invokedynamic(makeConcatWithConstants:(IIIJJ)Ljava/lang/String;, holder.getObjectId(), holder.getClassIndex(), holder.getLevel(), holder.getStartTime(), holder.getEndTime()));
    }
    
    public void removeTraingCampInfo() {
        this.getAccountVariables().remove("TRAINING_CAMP");
    }
    
    public long getTraingCampDuration() {
        return this.getAccountVariables().getLong("TRAINING_CAMP_DURATION", 0L);
    }
    
    public void setTraingCampDuration(final long duration) {
        this.getAccountVariables().set("TRAINING_CAMP_DURATION", duration);
    }
    
    public void resetTraingCampDuration() {
        this.getAccountVariables().remove("TRAINING_CAMP_DURATION");
    }
    
    public boolean isInTraingCamp() {
        return Util.falseIfNullOrElse((Object)this.getTraingCampInfo(), t -> t.getEndTime() > System.currentTimeMillis());
    }
    
    public AttendanceInfoHolder getAttendanceInfo() {
        final Calendar calendar = Calendar.getInstance();
        if (calendar.get(11) < 6 && calendar.get(12) < 30) {
            calendar.add(5, -1);
        }
        calendar.set(11, 6);
        calendar.set(12, 30);
        calendar.set(13, 0);
        calendar.set(14, 0);
        long receiveDate;
        int rewardIndex;
        if (((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).shareAccount()) {
            receiveDate = this.getAccountVariables().getLong("ATTENDANCE_DATE", 0L);
            rewardIndex = this.getAccountVariables().getInt("ATTENDANCE_INDEX", 0);
        }
        else {
            receiveDate = this.getAttendanceDate();
            rewardIndex = this.getAttendanceIndex();
        }
        boolean canBeRewarded = false;
        if (calendar.getTimeInMillis() > receiveDate) {
            canBeRewarded = true;
            if (rewardIndex >= AttendanceRewardData.getInstance().getRewardsCount() - 1) {
                rewardIndex = 0;
            }
        }
        return new AttendanceInfoHolder(rewardIndex, canBeRewarded);
    }
    
    public void setAttendanceInfo(final int rewardIndex) {
        final Calendar nextReward = Calendar.getInstance();
        nextReward.set(12, 30);
        if (nextReward.get(11) >= 6) {
            nextReward.add(5, 1);
        }
        nextReward.set(11, 6);
        if (((AttendanceSettings)Configurator.getSettings((Class)AttendanceSettings.class)).shareAccount()) {
            this.getAccountVariables().set("ATTENDANCE_DATE", nextReward.getTimeInMillis());
            this.getAccountVariables().set("ATTENDANCE_INDEX", rewardIndex);
        }
        else {
            this.setAttendanceDate(nextReward.getTimeInMillis());
            this.setAttendanceIndex(rewardIndex);
        }
    }
    
    public boolean isFriend(final Player player) {
        return this.friends.contains(player.getObjectId());
    }
    
    public boolean isInSameClan(final Player player) {
        return this.clanId > 0 && this.clanId == player.getClanId();
    }
    
    public boolean isInSameAlly(final Player player) {
        final int ally = this.getAllyId();
        return ally > 0 && player.getAllyId() == ally;
    }
    
    public boolean hasMentorRelationship(final Player player) {
        return Objects.nonNull(MentorManager.getInstance().getMentee(this.objectId, player.getObjectId())) || Objects.nonNull(MentorManager.getInstance().getMentee(player.getObjectId(), this.objectId));
    }
    
    public boolean isSiegeFriend(final WorldObject target) {
        if (this._siegeState == 0 || !this.isInsideZone(ZoneType.SIEGE)) {
            return false;
        }
        final Player targetPlayer = target.getActingPlayer();
        if (targetPlayer == null || targetPlayer == this || targetPlayer.getSiegeSide() != this._siegeSide || this._siegeState != targetPlayer.getSiegeState()) {
            return false;
        }
        if (this._siegeState == 1) {
            final Castle castle = CastleManager.getInstance().getCastleById(this._siegeSide);
            return castle != null && castle.getOwner() == null;
        }
        return true;
    }
    
    public boolean isInTimedHuntingZone() {
        return this.isInTimedHuntingZone(2);
    }
    
    public boolean isInTimedHuntingZone(final int zoneId) {
        final int x = (this.getX() + 294912 >> 15) + 11;
        final int y = (this.getY() + 262144 >> 15) + 10;
        switch (zoneId) {
            case 2: {
                return x == 20 && y == 15;
            }
            default: {
                return false;
            }
        }
    }
    
    public void startTimedHuntingZone(final int zoneId, final long delay) {
        this.stopTimedHuntingZoneTask();
        this.sendMessage(invokedynamic(makeConcatWithConstants:(J)Ljava/lang/String;, delay / 60L / 1000L));
        this._timedHuntingZoneFinishTask = (ScheduledFuture<?>)ThreadPool.schedule(() -> {
            if (this.isOnlineInt() > 0 && this.isInTimedHuntingZone(zoneId)) {
                this.sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
                this.abortCast();
                this.stopMove(null);
                this.teleToLocation(MapRegionManager.getInstance().getTeleToLocation(this, TeleportWhereType.TOWN));
            }
        }, delay);
    }
    
    public void stopTimedHuntingZoneTask() {
        if (this._timedHuntingZoneFinishTask != null && !this._timedHuntingZoneFinishTask.isCancelled() && !this._timedHuntingZoneFinishTask.isDone()) {
            this._timedHuntingZoneFinishTask.cancel(true);
            this._timedHuntingZoneFinishTask = null;
        }
        this.sendPacket(TimedHuntingZoneExit.STATIC_PACKET);
    }
    
    public long getTimedHuntingZoneRemainingTime() {
        if (this._timedHuntingZoneFinishTask != null && !this._timedHuntingZoneFinishTask.isCancelled() && !this._timedHuntingZoneFinishTask.isDone()) {
            return this._timedHuntingZoneFinishTask.getDelay(TimeUnit.MILLISECONDS);
        }
        return 0L;
    }
}
