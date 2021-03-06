// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver;

import java.util.Collections;
import java.util.Iterator;
import java.util.Enumeration;
import java.net.SocketException;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.net.Inet6Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import java.nio.file.Path;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.LoggerFactory;
import org.l2j.commons.util.Util;
import java.util.stream.Stream;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.RateSettings;
import java.util.Arrays;
import org.l2j.commons.util.StringUtil;
import java.util.HashMap;
import java.io.IOException;
import org.l2j.commons.util.PropertiesParser;
import org.l2j.gameserver.model.Location;
import java.io.File;
import org.l2j.gameserver.util.FloodProtectorConfig;
import org.l2j.gameserver.model.holders.ItemHolder;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

public final class Config
{
    private static final Logger LOGGER;
    public static final String OLYMPIAD_CONFIG_FILE = "./config/Olympiad.ini";
    public static final String SIEGE_CONFIG_FILE = "./config/Siege.ini";
    private static final String CHARACTER_CONFIG_FILE = "config/character.properties";
    private static final String FEATURE_CONFIG_FILE = "config/feature.properties";
    private static final String FLOOD_PROTECTOR_CONFIG_FILE = "./config/FloodProtector.ini";
    private static final String GENERAL_CONFIG_FILE = "config/general.properties";
    private static final String GRACIASEEDS_CONFIG_FILE = "./config/GraciaSeeds.ini";
    private static final String GRANDBOSS_CONFIG_FILE = "./config/GrandBoss.ini";
    private static final String NPC_CONFIG_FILE = "./config/NPC.ini";
    private static final String PVP_CONFIG_FILE = "./config/PVP.ini";
    private static final String RATES_CONFIG_FILE = "config/rates.properties";
    private static final String ALTHARS_CONFIG_FILE = "config/althars.ini";
    private static final String SERVER_CONFIG_FILE = "config/server.properties";
    private static final String CHAT_FILTER_FILE = "./config/chatfilter.txt";
    private static final String IPCONFIG_FILE = "./config/ipconfig.xml";
    private static final String CUSTOM_BANKING_CONFIG_FILE = "./config/Custom/Banking.ini";
    private static final String CUSTOM_CHAMPION_MONSTERS_CONFIG_FILE = "./config/Custom/ChampionMonsters.ini";
    private static final String CUSTOM_COMMUNITY_BOARD_CONFIG_FILE = "./config/Custom/CommunityBoard.ini";
    private static final String CUSTOM_DUALBOX_CHECK_CONFIG_FILE = "./config/Custom/DualboxCheck.ini";
    private static final String CUSTOM_MULTILANGUAL_SUPPORT_CONFIG_FILE = "./config/Custom/MultilingualSupport.ini";
    private static final String CUSTOM_NPC_STAT_MULTIPIERS_CONFIG_FILE = "./config/Custom/NpcStatMultipliers.ini";
    private static final String CUSTOM_PC_CAFE_CONFIG_FILE = "./config/Custom/PcCafe.ini";
    private static final String CUSTOM_PVP_ANNOUNCE_CONFIG_FILE = "./config/Custom/PvpAnnounce.ini";
    private static final String CUSTOM_PVP_REWARD_ITEM_CONFIG_FILE = "./config/Custom/PvpRewardItem.ini";
    private static final String CUSTOM_PVP_TITLE_CONFIG_FILE = "./config/Custom/PvpTitleColor.ini";
    private static final String CUSTOM_RANDOM_SPAWNS_CONFIG_FILE = "./config/Custom/RandomSpawns.ini";
    private static final String CUSTOM_SCREEN_WELCOME_MESSAGE_CONFIG_FILE = "./config/Custom/ScreenWelcomeMessage.ini";
    private static final String CUSTOM_SELL_BUFFS_CONFIG_FILE = "./config/Custom/SellBuffs.ini";
    private static final String CUSTOM_SERVER_TIME_CONFIG_FILE = "./config/Custom/ServerTime.ini";
    private static final String CUSTOM_SCHEME_BUFFER_CONFIG_FILE = "./config/Custom/ShemeBuffer.ini";
    private static final String CUSTOM_STARTING_LOCATION_CONFIG_FILE = "./config/Custom/StartingLocation.ini";
    private static final String CUSTOM_VOTE_REWARD_CONFIG_FILE = "./config/Custom/VoteReward.ini";
    private static final String TIME_LIMITED_ZONE_CONFIG_FILE = "./config/time-limited-zones.properties";
    public static double RESPAWN_RESTORE_CP;
    public static double RESPAWN_RESTORE_HP;
    public static double RESPAWN_RESTORE_MP;
    public static boolean ENABLE_MODIFY_SKILL_DURATION;
    public static Map<Integer, Integer> SKILL_DURATION_LIST;
    public static boolean ENABLE_MODIFY_SKILL_REUSE;
    public static Map<Integer, Integer> SKILL_REUSE_LIST;
    public static boolean AUTO_LEARN_SKILLS;
    public static boolean AUTO_LEARN_FS_SKILLS;
    public static boolean AUTO_LOOT_HERBS;
    public static byte BUFFS_MAX_AMOUNT;
    public static byte TRIGGERED_BUFFS_MAX_AMOUNT;
    public static byte DANCES_MAX_AMOUNT;
    public static boolean DANCE_CANCEL_BUFF;
    public static boolean DANCE_CONSUME_ADDITIONAL_MP;
    public static boolean ALT_STORE_DANCES;
    public static boolean ALT_GAME_CANCEL_BOW;
    public static boolean ALT_GAME_CANCEL_CAST;
    public static boolean ALT_GAME_MAGICFAILURES;
    public static boolean ALT_GAME_STUN_BREAK;
    public static int PLAYER_FAKEDEATH_UP_PROTECTION;
    public static boolean STORE_SKILL_COOLTIME;
    public static boolean SUBCLASS_STORE_SKILL_COOLTIME;
    public static boolean SUMMON_STORE_SKILL_COOLTIME;
    public static long EFFECT_TICK_RATIO;
    public static boolean LIFE_CRYSTAL_NEEDED;
    public static boolean DIVINE_SP_BOOK_NEEDED;
    public static boolean ALT_GAME_SUBCLASS_WITHOUT_QUESTS;
    public static boolean ALT_GAME_SUBCLASS_EVERYWHERE;
    public static boolean ALLOW_TRANSFORM_WITHOUT_QUEST;
    public static double MAX_BONUS_EXP;
    public static double MAX_BONUS_SP;
    public static int MAX_RUN_SPEED;
    public static int MAX_PATK;
    public static int MAX_MATK;
    public static int MAX_PCRIT_RATE;
    public static int MAX_MCRIT_RATE;
    public static int MAX_PATK_SPEED;
    public static int MAX_MATK_SPEED;
    public static int MAX_EVASION;
    public static int MIN_ABNORMAL_STATE_SUCCESS_RATE;
    public static int MAX_ABNORMAL_STATE_SUCCESS_RATE;
    public static long MAX_SP;
    public static byte MAX_SUBCLASS;
    public static byte BASE_SUBCLASS_LEVEL;
    public static byte BASE_DUALCLASS_LEVEL;
    public static byte MAX_SUBCLASS_LEVEL;
    public static int MAX_PVTSTORESELL_SLOTS_DWARF;
    public static int MAX_PVTSTORESELL_SLOTS_OTHER;
    public static int MAX_PVTSTOREBUY_SLOTS_DWARF;
    public static int MAX_PVTSTOREBUY_SLOTS_OTHER;
    public static int INVENTORY_MAXIMUM_NO_DWARF;
    public static int INVENTORY_MAXIMUM_DWARF;
    public static int INVENTORY_MAXIMUM_GM;
    public static int INVENTORY_MAXIMUM_QUEST_ITEMS;
    public static int WAREHOUSE_SLOTS_DWARF;
    public static int WAREHOUSE_SLOTS_NO_DWARF;
    public static int WAREHOUSE_SLOTS_CLAN;
    public static int ALT_FREIGHT_SLOTS;
    public static int ALT_FREIGHT_PRICE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_SHOP;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TELEPORT;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_GK;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_TRADE;
    public static boolean ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE;
    public static int MAX_PERSONAL_FAME_POINTS;
    public static int CASTLE_ZONE_FAME_TASK_FREQUENCY;
    public static int CASTLE_ZONE_FAME_AQUIRE_POINTS;
    public static boolean FAME_FOR_DEAD_PLAYERS;
    public static boolean IS_CRAFTING_ENABLED;
    public static boolean CRAFT_MASTERWORK;
    public static int BASE_CRITICAL_CRAFT_RATE;
    public static int DWARF_RECIPE_LIMIT;
    public static int COMMON_RECIPE_LIMIT;
    public static boolean ALT_GAME_CREATION;
    public static double ALT_GAME_CREATION_SPEED;
    public static double ALT_GAME_CREATION_XP_RATE;
    public static double ALT_GAME_CREATION_RARE_XPSP_RATE;
    public static double ALT_GAME_CREATION_SP_RATE;
    public static boolean ALT_CLAN_LEADER_INSTANT_ACTIVATION;
    public static int ALT_CLAN_JOIN_DAYS;
    public static int ALT_CLAN_CREATE_DAYS;
    public static int ALT_CLAN_DISSOLVE_DAYS;
    public static int ALT_ALLY_JOIN_DAYS_WHEN_LEAVED;
    public static int ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED;
    public static int ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED;
    public static int ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED;
    public static int ALT_MAX_NUM_OF_CLANS_IN_ALLY;
    public static int ALT_CLAN_MEMBERS_FOR_WAR;
    public static boolean ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH;
    public static long ALT_CLAN_MEMBERS_TIME_FOR_BONUS;
    public static boolean REMOVE_CASTLE_CIRCLETS;
    public static int ALT_PARTY_MAX_MEMBERS;
    public static int ALT_PARTY_RANGE;
    public static boolean ALT_LEAVE_PARTY_LEADER;
    public static long STARTING_ADENA;
    public static byte STARTING_LEVEL;
    public static int STARTING_SP;
    public static long MAX_ADENA;
    public static boolean AUTO_LOOT_SLOT_LIMIT;
    public static int LOOT_RAIDS_PRIVILEGE_CC_SIZE;
    public static boolean ENABLE_KEYBOARD_MOVEMENT;
    public static int UNSTUCK_INTERVAL;
    public static int TELEPORT_WATCHDOG_TIMEOUT;
    public static int PLAYER_SPAWN_PROTECTION;
    public static int PLAYER_TELEPORT_PROTECTION;
    public static boolean RANDOM_RESPAWN_IN_TOWN_ENABLED;
    public static boolean OFFSET_ON_TELEPORT_ENABLED;
    public static int MAX_OFFSET_ON_TELEPORT;
    public static boolean PETITIONING_ALLOWED;
    public static int MAX_PETITIONS_PER_PLAYER;
    public static int MAX_PETITIONS_PENDING;
    public static int MAX_FREE_TELEPORT_LEVEL;
    public static int MAX_NEWBIE_BUFF_LEVEL;
    public static int DELETE_DAYS;
    public static String PARTY_XP_CUTOFF_METHOD;
    public static double PARTY_XP_CUTOFF_PERCENT;
    public static int PARTY_XP_CUTOFF_LEVEL;
    public static int[][] PARTY_XP_CUTOFF_GAPS;
    public static int[] PARTY_XP_CUTOFF_GAP_PERCENTS;
    public static boolean DISABLE_TUTORIAL;
    public static boolean STORE_RECIPE_SHOPLIST;
    public static boolean STORE_UI_SETTINGS;
    public static long CS_TELE_FEE_RATIO;
    public static int CS_TELE1_FEE;
    public static int CS_TELE2_FEE;
    public static long CS_MPREG_FEE_RATIO;
    public static int CS_MPREG1_FEE;
    public static int CS_MPREG2_FEE;
    public static long CS_HPREG_FEE_RATIO;
    public static int CS_HPREG1_FEE;
    public static int CS_HPREG2_FEE;
    public static long CS_EXPREG_FEE_RATIO;
    public static int CS_EXPREG1_FEE;
    public static int CS_EXPREG2_FEE;
    public static long CS_SUPPORT_FEE_RATIO;
    public static int CS_SUPPORT1_FEE;
    public static int CS_SUPPORT2_FEE;
    public static int CASTLE_BUY_TAX_NEUTRAL;
    public static int CASTLE_BUY_TAX_LIGHT;
    public static int CASTLE_BUY_TAX_DARK;
    public static int CASTLE_SELL_TAX_NEUTRAL;
    public static int CASTLE_SELL_TAX_LIGHT;
    public static int CASTLE_SELL_TAX_DARK;
    public static int OUTER_DOOR_UPGRADE_PRICE2;
    public static int OUTER_DOOR_UPGRADE_PRICE3;
    public static int OUTER_DOOR_UPGRADE_PRICE5;
    public static int INNER_DOOR_UPGRADE_PRICE2;
    public static int INNER_DOOR_UPGRADE_PRICE3;
    public static int INNER_DOOR_UPGRADE_PRICE5;
    public static int WALL_UPGRADE_PRICE2;
    public static int WALL_UPGRADE_PRICE3;
    public static int WALL_UPGRADE_PRICE5;
    public static int TRAP_UPGRADE_PRICE1;
    public static int TRAP_UPGRADE_PRICE2;
    public static int TRAP_UPGRADE_PRICE3;
    public static int TRAP_UPGRADE_PRICE4;
    public static int TAKE_CASTLE_POINTS;
    public static int LOOSE_CASTLE_POINTS;
    public static int CASTLE_DEFENDED_POINTS;
    public static int HERO_POINTS;
    public static int ROYAL_GUARD_COST;
    public static int KNIGHT_UNIT_COST;
    public static int REPUTATION_SCORE_PER_KILL;
    public static int JOIN_ACADEMY_MIN_REP_SCORE;
    public static int JOIN_ACADEMY_MAX_REP_SCORE;
    public static boolean ALLOW_WYVERN_ALWAYS;
    public static boolean ALLOW_WYVERN_DURING_SIEGE;
    public static boolean ALLOW_MOUNTS_DURING_SIEGE;
    public static boolean SERVER_GMONLY;
    public static boolean GM_HERO_AURA;
    public static boolean GM_STARTUP_BUILDER_HIDE;
    public static boolean GM_STARTUP_INVULNERABLE;
    public static boolean GM_STARTUP_INVISIBLE;
    public static boolean GM_STARTUP_SILENCE;
    public static boolean GM_STARTUP_AUTO_LIST;
    public static boolean GM_STARTUP_DIET_MODE;
    public static boolean GM_ITEM_RESTRICTION;
    public static boolean GM_SKILL_RESTRICTION;
    public static boolean GM_TRADE_RESTRICTED_ITEMS;
    public static boolean GM_RESTART_FIGHTING;
    public static boolean GM_ANNOUNCER_NAME;
    public static boolean GM_GIVE_SPECIAL_SKILLS;
    public static boolean GM_GIVE_SPECIAL_AURA_SKILLS;
    public static boolean GM_DEBUG_HTML_PATHS;
    public static boolean USE_SUPER_HASTE_AS_GM_SPEED;
    public static boolean LOG_ITEM_ENCHANTS;
    public static boolean LOG_SKILL_ENCHANTS;
    public static boolean SKILL_CHECK_ENABLE;
    public static boolean SKILL_CHECK_REMOVE;
    public static boolean SKILL_CHECK_GM;
    public static boolean HTML_ACTION_CACHE_DEBUG;
    public static boolean DEVELOPER;
    public static boolean ALT_DEV_NO_QUESTS;
    public static boolean ALT_DEV_NO_SPAWNS;
    public static boolean ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS;
    public static boolean ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS;
    public static boolean ALLOW_DISCARDITEM;
    public static boolean LAZY_ITEMS_UPDATE;
    public static boolean UPDATE_ITEMS_ON_CHAR_STORE;
    public static boolean DESTROY_ALL_ITEMS;
    public static boolean AUTODELETE_INVALID_QUEST_DATA;
    public static boolean ENABLE_STORY_QUEST_BUFF_REWARD;
    public static boolean MULTIPLE_ITEM_DROP;
    public static boolean FORCE_INVENTORY_UPDATE;
    public static int MIN_NPC_ANIMATION;
    public static int MAX_NPC_ANIMATION;
    public static int MIN_MONSTER_ANIMATION;
    public static int MAX_MONSTER_ANIMATION;
    public static boolean ENABLE_FALLING_DAMAGE;
    public static boolean GRIDS_ALWAYS_ON;
    public static int GRID_NEIGHBOR_TURNON_TIME;
    public static int GRID_NEIGHBOR_TURNOFF_TIME;
    public static int PEACE_ZONE_MODE;
    public static boolean WAREHOUSE_CACHE;
    public static int WAREHOUSE_CACHE_TIME;
    public static boolean ALLOW_REFUND;
    public static boolean ALLOW_ATTACHMENTS;
    public static boolean ALLOW_WEAR;
    public static int WEAR_DELAY;
    public static int WEAR_PRICE;
    public static int INSTANCE_FINISH_TIME;
    public static boolean RESTORE_PLAYER_INSTANCE;
    public static int EJECT_DEAD_PLAYER_TIME;
    public static boolean ALLOW_WATER;
    public static boolean ALLOW_FISHING;
    public static boolean ALLOW_BOAT;
    public static int BOAT_BROADCAST_RADIUS;
    public static boolean ALLOW_MANOR;
    public static boolean SERVER_NEWS;
    public static boolean ENABLE_COMMUNITY_BOARD;
    public static String BBS_DEFAULT;
    public static int WORLD_CHAT_POINTS_PER_DAY;
    public static long ALT_OLY_BATTLE;
    public static int ALT_OLY_CLASSED;
    public static int ALT_OLY_NONCLASSED;
    public static List<ItemHolder> ALT_OLY_WINNER_REWARD;
    public static List<ItemHolder> ALT_OLY_LOSER_REWARD;
    public static int ALT_OLY_COMP_RITEM;
    public static int ALT_OLY_MIN_MATCHES;
    public static int ALT_OLY_MARK_PER_POINT;
    public static int ALT_OLY_MAX_POINTS;
    public static int ALT_OLY_DIVIDER_CLASSED;
    public static int ALT_OLY_DIVIDER_NON_CLASSED;
    public static boolean ALT_OLY_LOG_FIGHTS;
    public static boolean ALT_OLY_SHOW_MONTHLY_WINNERS;
    public static List<Integer> LIST_OLY_RESTRICTED_ITEMS;
    public static int ALT_OLY_ENCHANT_LIMIT;
    public static int ALT_OLY_WAIT_TIME;
    public static int ALT_MANOR_REFRESH_TIME;
    public static int ALT_MANOR_REFRESH_MIN;
    public static int ALT_MANOR_APPROVE_TIME;
    public static int ALT_MANOR_APPROVE_MIN;
    public static int ALT_MANOR_MAINTENANCE_MIN;
    public static int ALT_MANOR_SAVE_PERIOD_RATE;
    public static boolean ALT_ITEM_AUCTION_ENABLED;
    public static int ALT_ITEM_AUCTION_EXPIRED_AFTER;
    public static long ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID;
    public static int DEFAULT_PUNISH_PARAM;
    public static boolean ONLY_GM_ITEMS_FREE;
    public static boolean JAIL_IS_PVP;
    public static boolean JAIL_DISABLE_TRANSACTION;
    public static boolean CUSTOM_NPC_DATA;
    public static boolean CUSTOM_ITEMS_LOAD;
    public static int ALT_BIRTHDAY_GIFT;
    public static String ALT_BIRTHDAY_MAIL_SUBJECT;
    public static String ALT_BIRTHDAY_MAIL_TEXT;
    public static boolean ENABLE_BLOCK_CHECKER_EVENT;
    public static boolean HBCE_FAIR_PLAY;
    public static int PLAYER_MOVEMENT_BLOCK_TIME;
    public static boolean BOTREPORT_ENABLE;
    public static String[] BOTREPORT_RESETPOINT_HOUR;
    public static long BOTREPORT_REPORT_DELAY;
    public static boolean BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_USE_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ROLL_DICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_FIREWORK;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_PET_SUMMON;
    public static FloodProtectorConfig FLOOD_PROTECTOR_HERO_VOICE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_GLOBAL_CHAT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SUBCLASS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_DROP_ITEM;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SERVER_BYPASS;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MULTISELL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_TRANSACTION;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MANUFACTURE;
    public static FloodProtectorConfig FLOOD_PROTECTOR_MANOR;
    public static FloodProtectorConfig FLOOD_PROTECTOR_SENDMAIL;
    public static FloodProtectorConfig FLOOD_PROTECTOR_CHARACTER_SELECT;
    public static FloodProtectorConfig FLOOD_PROTECTOR_ITEM_AUCTION;
    public static boolean ALT_MOB_AGRO_IN_PEACEZONE;
    public static boolean ALT_ATTACKABLE_NPCS;
    public static boolean ALT_GAME_VIEWNPC;
    public static int MAX_DRIFT_RANGE;
    public static boolean SHOW_NPC_LVL;
    public static boolean SHOW_CREST_WITHOUT_QUEST;
    public static boolean ENABLE_RANDOM_ENCHANT_EFFECT;
    public static int MIN_NPC_LVL_DMG_PENALTY;
    public static Map<Integer, Float> NPC_DMG_PENALTY;
    public static Map<Integer, Float> NPC_CRIT_DMG_PENALTY;
    public static Map<Integer, Float> NPC_SKILL_DMG_PENALTY;
    public static int MIN_NPC_LVL_MAGIC_PENALTY;
    public static Map<Integer, Float> NPC_SKILL_CHANCE_PENALTY;
    public static int DEFAULT_CORPSE_TIME;
    public static int SPOILED_CORPSE_EXTEND_TIME;
    public static int CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY;
    public static boolean AGGRO_DISTANCE_CHECK_ENABLED;
    public static int AGGRO_DISTANCE_CHECK_RANGE;
    public static boolean AGGRO_DISTANCE_CHECK_RAIDS;
    public static boolean AGGRO_DISTANCE_CHECK_INSTANCES;
    public static boolean AGGRO_DISTANCE_CHECK_RESTORE_LIFE;
    public static boolean GUARD_ATTACK_AGGRO_MOB;
    public static double RAID_HP_REGEN_MULTIPLIER;
    public static double RAID_MP_REGEN_MULTIPLIER;
    public static double RAID_PDEFENCE_MULTIPLIER;
    public static double RAID_MDEFENCE_MULTIPLIER;
    public static double RAID_PATTACK_MULTIPLIER;
    public static double RAID_MATTACK_MULTIPLIER;
    public static double RAID_MINION_RESPAWN_TIMER;
    public static Map<Integer, Integer> MINIONS_RESPAWN_TIME;
    public static float RAID_MIN_RESPAWN_MULTIPLIER;
    public static float RAID_MAX_RESPAWN_MULTIPLIER;
    public static boolean RAID_DISABLE_CURSE;
    public static boolean FORCE_DELETE_MINIONS;
    public static long DESPAWN_MINION_DELAY;
    public static int RAID_CHAOS_TIME;
    public static int GRAND_CHAOS_TIME;
    public static int MINION_CHAOS_TIME;
    public static int INVENTORY_MAXIMUM_PET;
    public static double PET_HP_REGEN_MULTIPLIER;
    public static double PET_MP_REGEN_MULTIPLIER;
    public static int VITALITY_CONSUME_BY_MOB;
    public static int VITALITY_CONSUME_BY_BOSS;
    public static boolean KARMA_DROP_GM;
    public static int KARMA_PK_LIMIT;
    public static String KARMA_NONDROPPABLE_PET_ITEMS;
    public static String KARMA_NONDROPPABLE_ITEMS;
    public static int[] KARMA_LIST_NONDROPPABLE_PET_ITEMS;
    public static int[] KARMA_LIST_NONDROPPABLE_ITEMS;
    public static boolean ANTIFEED_ENABLE;
    public static boolean ANTIFEED_DUALBOX;
    public static boolean ANTIFEED_DISCONNECTED_AS_DUALBOX;
    public static int ANTIFEED_INTERVAL;
    public static float RATE_SP;
    public static float RATE_PARTY_XP;
    public static float RATE_PARTY_SP;
    public static float RATE_INSTANCE_XP;
    public static float RATE_INSTANCE_SP;
    public static float RATE_INSTANCE_PARTY_XP;
    public static float RATE_INSTANCE_PARTY_SP;
    public static float RATE_RAIDBOSS_POINTS;
    public static float RATE_EXTRACTABLE;
    public static int RATE_DROP_MANOR;
    public static float RATE_QUEST_DROP;
    public static float RATE_QUEST_REWARD;
    public static float RATE_QUEST_REWARD_XP;
    public static float RATE_QUEST_REWARD_SP;
    public static float RATE_QUEST_REWARD_ADENA;
    public static boolean RATE_QUEST_REWARD_USE_MULTIPLIERS;
    public static float RATE_QUEST_REWARD_POTION;
    public static float RATE_QUEST_REWARD_SCROLL;
    public static float RATE_QUEST_REWARD_RECIPE;
    public static float RATE_QUEST_REWARD_MATERIAL;
    public static float RATE_DEATH_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_SPOIL_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_HERB_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_RAID_DROP_AMOUNT_MULTIPLIER;
    public static float RATE_DEATH_DROP_CHANCE_MULTIPLIER;
    public static float RATE_SPOIL_DROP_CHANCE_MULTIPLIER;
    public static float RATE_HERB_DROP_CHANCE_MULTIPLIER;
    public static float RATE_RAID_DROP_CHANCE_MULTIPLIER;
    public static Map<Integer, Float> RATE_DROP_AMOUNT_BY_ID;
    public static Map<Integer, Float> RATE_DROP_CHANCE_BY_ID;
    public static int DROP_MAX_OCCURRENCES_NORMAL;
    public static int DROP_MAX_OCCURRENCES_RAIDBOSS;
    public static int DROP_ADENA_MIN_LEVEL_DIFFERENCE;
    public static int DROP_ADENA_MAX_LEVEL_DIFFERENCE;
    public static double DROP_ADENA_MIN_LEVEL_GAP_CHANCE;
    public static int DROP_ITEM_MIN_LEVEL_DIFFERENCE;
    public static int DROP_ITEM_MAX_LEVEL_DIFFERENCE;
    public static double DROP_ITEM_MIN_LEVEL_GAP_CHANCE;
    public static float RATE_KARMA_LOST;
    public static float RATE_KARMA_EXP_LOST;
    public static float RATE_SIEGE_GUARDS_PRICE;
    public static int PLAYER_DROP_LIMIT;
    public static int PLAYER_RATE_DROP;
    public static int PLAYER_RATE_DROP_ITEM;
    public static int PLAYER_RATE_DROP_EQUIP;
    public static int PLAYER_RATE_DROP_EQUIP_WEAPON;
    public static float PET_XP_RATE;
    public static int PET_FOOD_RATE;
    public static float SINEATER_XP_RATE;
    public static int KARMA_DROP_LIMIT;
    public static int KARMA_RATE_DROP;
    public static int KARMA_RATE_DROP_ITEM;
    public static int KARMA_RATE_DROP_EQUIP;
    public static int KARMA_RATE_DROP_EQUIP_WEAPON;
    public static boolean HARDWARE_INFO_ENABLED;
    public static int MAX_PLAYERS_PER_HWID;
    public static String PET_NAME_TEMPLATE;
    public static String CLAN_NAME_TEMPLATE;
    public static int MAX_CHARACTERS_NUMBER_PER_ACCOUNT;
    public static File DATAPACK_ROOT;
    public static int SERVER_LIST_AGE;
    public static boolean SERVER_LIST_BRACKET;
    public static boolean SERVER_RESTART_SCHEDULE_MESSAGE;
    public static int SERVER_RESTART_SCHEDULE_COUNTDOWN;
    public static String[] SERVER_RESTART_SCHEDULE;
    public static boolean ENABLE_VITALITY;
    public static int STARTING_VITALITY_POINTS;
    public static boolean RAIDBOSS_USE_VITALITY;
    public static float RATE_VITALITY_EXP_MULTIPLIER;
    public static int VITALITY_MAX_ITEMS_ALLOWED;
    public static float RATE_VITALITY_LOST;
    public static float RATE_VITALITY_GAIN;
    public static int MAX_ITEM_IN_PACKET;
    public static List<String> GAME_SERVER_SUBNETS;
    public static List<String> GAME_SERVER_HOSTS;
    public static int PVP_NORMAL_TIME;
    public static int PVP_PVP_TIME;
    public static int MAX_REPUTATION;
    public static int REPUTATION_INCREASE;
    public static double ENCHANT_CHANCE_ELEMENT_STONE;
    public static double ENCHANT_CHANCE_ELEMENT_CRYSTAL;
    public static double ENCHANT_CHANCE_ELEMENT_JEWEL;
    public static double ENCHANT_CHANCE_ELEMENT_ENERGY;
    public static int[] ENCHANT_BLACKLIST;
    public static boolean DISABLE_OVER_ENCHANTING;
    public static int[] AUGMENTATION_BLACKLIST;
    public static boolean ALT_ALLOW_AUGMENT_PVP_ITEMS;
    public static int ANTHARAS_WAIT_TIME;
    public static int ANTHARAS_SPAWN_INTERVAL;
    public static int ANTHARAS_SPAWN_RANDOM;
    public static int BAIUM_SPAWN_INTERVAL;
    public static int CORE_SPAWN_INTERVAL;
    public static int CORE_SPAWN_RANDOM;
    public static int ORFEN_SPAWN_INTERVAL;
    public static int ORFEN_SPAWN_RANDOM;
    public static int QUEEN_ANT_SPAWN_INTERVAL;
    public static int QUEEN_ANT_SPAWN_RANDOM;
    public static int ZAKEN_SPAWN_INTERVAL;
    public static int ZAKEN_SPAWN_RANDOM;
    public static int SOD_TIAT_KILL_COUNT;
    public static long SOD_STAGE_2_LENGTH;
    public static List<String> FILTER_LIST;
    public static boolean CHAMPION_ENABLE;
    public static boolean CHAMPION_PASSIVE;
    public static int CHAMPION_FREQUENCY;
    public static String CHAMP_TITLE;
    public static boolean SHOW_CHAMPION_AURA;
    public static int CHAMP_MIN_LVL;
    public static int CHAMP_MAX_LVL;
    public static int CHAMPION_HP;
    public static float CHAMPION_REWARDS_EXP_SP;
    public static float CHAMPION_REWARDS_CHANCE;
    public static float CHAMPION_REWARDS_AMOUNT;
    public static float CHAMPION_ADENAS_REWARDS_CHANCE;
    public static float CHAMPION_ADENAS_REWARDS_AMOUNT;
    public static float CHAMPION_HP_REGEN;
    public static float CHAMPION_ATK;
    public static float CHAMPION_SPD_ATK;
    public static int CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE;
    public static int CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE;
    public static int CHAMPION_REWARD_ID;
    public static int CHAMPION_REWARD_QTY;
    public static boolean CHAMPION_ENABLE_VITALITY;
    public static boolean CHAMPION_ENABLE_IN_INSTANCES;
    public static int BANKING_SYSTEM_GOLDBARS;
    public static int BANKING_SYSTEM_ADENA;
    public static boolean ENABLE_NPC_STAT_MULTIPIERS;
    public static double MONSTER_HP_MULTIPLIER;
    public static double MONSTER_MP_MULTIPLIER;
    public static double MONSTER_PATK_MULTIPLIER;
    public static double MONSTER_MATK_MULTIPLIER;
    public static double MONSTER_PDEF_MULTIPLIER;
    public static double MONSTER_MDEF_MULTIPLIER;
    public static double MONSTER_AGRRO_RANGE_MULTIPLIER;
    public static double MONSTER_CLAN_HELP_RANGE_MULTIPLIER;
    public static double RAIDBOSS_HP_MULTIPLIER;
    public static double RAIDBOSS_MP_MULTIPLIER;
    public static double RAIDBOSS_PATK_MULTIPLIER;
    public static double RAIDBOSS_MATK_MULTIPLIER;
    public static double RAIDBOSS_PDEF_MULTIPLIER;
    public static double RAIDBOSS_MDEF_MULTIPLIER;
    public static double RAIDBOSS_AGRRO_RANGE_MULTIPLIER;
    public static double RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER;
    public static int RAIDBOSS_LIMIT_BARRIER;
    public static double GUARD_HP_MULTIPLIER;
    public static double GUARD_MP_MULTIPLIER;
    public static double GUARD_PATK_MULTIPLIER;
    public static double GUARD_MATK_MULTIPLIER;
    public static double GUARD_PDEF_MULTIPLIER;
    public static double GUARD_MDEF_MULTIPLIER;
    public static double GUARD_AGRRO_RANGE_MULTIPLIER;
    public static double GUARD_CLAN_HELP_RANGE_MULTIPLIER;
    public static double DEFENDER_HP_MULTIPLIER;
    public static double DEFENDER_MP_MULTIPLIER;
    public static double DEFENDER_PATK_MULTIPLIER;
    public static double DEFENDER_MATK_MULTIPLIER;
    public static double DEFENDER_PDEF_MULTIPLIER;
    public static double DEFENDER_MDEF_MULTIPLIER;
    public static double DEFENDER_AGRRO_RANGE_MULTIPLIER;
    public static double DEFENDER_CLAN_HELP_RANGE_MULTIPLIER;
    public static boolean DISPLAY_SERVER_TIME;
    public static int BUFFER_MAX_SCHEMES;
    public static int BUFFER_STATIC_BUFF_COST;
    public static boolean WELCOME_MESSAGE_ENABLED;
    public static String WELCOME_MESSAGE_TEXT;
    public static int WELCOME_MESSAGE_TIME;
    public static boolean ANNOUNCE_PK_PVP;
    public static boolean ANNOUNCE_PK_PVP_NORMAL_MESSAGE;
    public static String ANNOUNCE_PK_MSG;
    public static String ANNOUNCE_PVP_MSG;
    public static boolean REWARD_PVP_ITEM;
    public static int REWARD_PVP_ITEM_ID;
    public static int REWARD_PVP_ITEM_AMOUNT;
    public static boolean REWARD_PVP_ITEM_MESSAGE;
    public static boolean REWARD_PK_ITEM;
    public static int REWARD_PK_ITEM_ID;
    public static int REWARD_PK_ITEM_AMOUNT;
    public static boolean REWARD_PK_ITEM_MESSAGE;
    public static boolean DISABLE_REWARDS_IN_INSTANCES;
    public static boolean DISABLE_REWARDS_IN_PVP_ZONES;
    public static boolean PVP_COLOR_SYSTEM_ENABLED;
    public static int PVP_AMOUNT1;
    public static int PVP_AMOUNT2;
    public static int PVP_AMOUNT3;
    public static int PVP_AMOUNT4;
    public static int PVP_AMOUNT5;
    public static int NAME_COLOR_FOR_PVP_AMOUNT1;
    public static int NAME_COLOR_FOR_PVP_AMOUNT2;
    public static int NAME_COLOR_FOR_PVP_AMOUNT3;
    public static int NAME_COLOR_FOR_PVP_AMOUNT4;
    public static int NAME_COLOR_FOR_PVP_AMOUNT5;
    public static String TITLE_FOR_PVP_AMOUNT1;
    public static String TITLE_FOR_PVP_AMOUNT2;
    public static String TITLE_FOR_PVP_AMOUNT3;
    public static String TITLE_FOR_PVP_AMOUNT4;
    public static String TITLE_FOR_PVP_AMOUNT5;
    public static boolean MULTILANG_ENABLE;
    public static List<String> MULTILANG_ALLOWED;
    public static String MULTILANG_DEFAULT;
    public static boolean MULTILANG_VOICED_ALLOW;
    public static int DUALBOX_CHECK_MAX_PLAYERS_PER_IP;
    public static int DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP;
    public static int DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP;
    public static boolean DUALBOX_COUNT_OFFLINE_TRADERS;
    public static Map<Integer, Integer> DUALBOX_CHECK_WHITELIST;
    public static boolean CUSTOM_STARTING_LOC;
    public static int CUSTOM_STARTING_LOC_X;
    public static int CUSTOM_STARTING_LOC_Y;
    public static int CUSTOM_STARTING_LOC_Z;
    public static boolean ENABLE_RANDOM_MONSTER_SPAWNS;
    public static int MOB_MIN_SPAWN_RANGE;
    public static int MOB_MAX_SPAWN_RANGE;
    public static List<Integer> MOBS_LIST_NOT_RANDOM;
    public static boolean CUSTOM_CB_ENABLED;
    public static int COMMUNITYBOARD_CURRENCY;
    public static boolean COMMUNITYBOARD_ENABLE_MULTISELLS;
    public static boolean COMMUNITYBOARD_ENABLE_TELEPORTS;
    public static boolean COMMUNITYBOARD_ENABLE_BUFFS;
    public static boolean COMMUNITYBOARD_ENABLE_HEAL;
    public static boolean COMMUNITYBOARD_ENABLE_PREMIUM;
    public static boolean COMMUNITYBOARD_ENABLE_AUTO_HP_MP_CP;
    public static int COMMUNITYBOARD_TELEPORT_PRICE;
    public static int COMMUNITYBOARD_BUFF_PRICE;
    public static int COMMUNITYBOARD_HEAL_PRICE;
    public static boolean COMMUNITYBOARD_KARMA_DISABLED;
    public static boolean COMMUNITYBOARD_CAST_ANIMATIONS;
    public static List<Integer> COMMUNITY_AVAILABLE_BUFFS;
    public static Map<String, Location> COMMUNITY_AVAILABLE_TELEPORTS;
    public static boolean PC_CAFE_ENABLED;
    public static boolean PC_CAFE_ONLY_VIP;
    public static int PC_CAFE_MAX_POINTS;
    public static boolean PC_CAFE_ENABLE_DOUBLE_POINTS;
    public static int PC_CAFE_DOUBLE_POINTS_CHANCE;
    public static double PC_CAFE_POINT_RATE;
    public static boolean PC_CAFE_RANDOM_POINT;
    public static boolean PC_CAFE_REWARD_LOW_EXP_KILLS;
    public static int PC_CAFE_LOW_EXP_KILLS_CHANCE;
    public static boolean SELLBUFF_ENABLED;
    public static int SELLBUFF_MP_MULTIPLER;
    public static int SELLBUFF_PAYMENT_ID;
    public static long SELLBUFF_MIN_PRICE;
    public static long SELLBUFF_MAX_PRICE;
    public static int SELLBUFF_MAX_BUFFS;
    public static boolean ALLOW_NETWORK_VOTE_REWARD;
    public static String NETWORK_SERVER_LINK;
    public static int NETWORK_VOTES_DIFFERENCE;
    public static int NETWORK_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> NETWORK_REWARD;
    public static int NETWORK_DUALBOXES_ALLOWED;
    public static boolean ALLOW_NETWORK_GAME_SERVER_REPORT;
    public static boolean ALLOW_TOPZONE_VOTE_REWARD;
    public static String TOPZONE_SERVER_LINK;
    public static int TOPZONE_VOTES_DIFFERENCE;
    public static int TOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> TOPZONE_REWARD;
    public static int TOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_TOPZONE_GAME_SERVER_REPORT;
    public static boolean ALLOW_HOPZONE_VOTE_REWARD;
    public static String HOPZONE_SERVER_LINK;
    public static int HOPZONE_VOTES_DIFFERENCE;
    public static int HOPZONE_REWARD_CHECK_TIME;
    public static Map<Integer, Integer> HOPZONE_REWARD;
    public static int HOPZONE_DUALBOXES_ALLOWED;
    public static boolean ALLOW_HOPZONE_GAME_SERVER_REPORT;
    public static long TIME_LIMITED_ZONE_INITIAL_TIME;
    public static long TIME_LIMITED_MAX_ADDED_TIME;
    public static long TIME_LIMITED_ZONE_RESET_DELAY;
    public static long TIME_LIMITED_ZONE_TELEPORT_FEE;
    public static float L2_COIN_DROP_RATE;
    public static int ALTHARS_ACTIVATE_CHANCE_RATE;
    public static int ALTHARS_MAX_ACTIVE;
    public static int ALTHARS_MIN_DURATION_CYCLE;
    public static int ALTHARS_MAX_DURATION_CYCLE;
    
    public static void load() {
        Config.FLOOD_PROTECTOR_USE_ITEM = new FloodProtectorConfig("UseItemFloodProtector");
        Config.FLOOD_PROTECTOR_ROLL_DICE = new FloodProtectorConfig("RollDiceFloodProtector");
        Config.FLOOD_PROTECTOR_FIREWORK = new FloodProtectorConfig("FireworkFloodProtector");
        Config.FLOOD_PROTECTOR_ITEM_PET_SUMMON = new FloodProtectorConfig("ItemPetSummonFloodProtector");
        Config.FLOOD_PROTECTOR_HERO_VOICE = new FloodProtectorConfig("HeroVoiceFloodProtector");
        Config.FLOOD_PROTECTOR_GLOBAL_CHAT = new FloodProtectorConfig("GlobalChatFloodProtector");
        Config.FLOOD_PROTECTOR_SUBCLASS = new FloodProtectorConfig("SubclassFloodProtector");
        Config.FLOOD_PROTECTOR_DROP_ITEM = new FloodProtectorConfig("DropItemFloodProtector");
        Config.FLOOD_PROTECTOR_SERVER_BYPASS = new FloodProtectorConfig("ServerBypassFloodProtector");
        Config.FLOOD_PROTECTOR_MULTISELL = new FloodProtectorConfig("MultiSellFloodProtector");
        Config.FLOOD_PROTECTOR_TRANSACTION = new FloodProtectorConfig("TransactionFloodProtector");
        Config.FLOOD_PROTECTOR_MANUFACTURE = new FloodProtectorConfig("ManufactureFloodProtector");
        Config.FLOOD_PROTECTOR_MANOR = new FloodProtectorConfig("ManorFloodProtector");
        Config.FLOOD_PROTECTOR_SENDMAIL = new FloodProtectorConfig("SendMailFloodProtector");
        Config.FLOOD_PROTECTOR_CHARACTER_SELECT = new FloodProtectorConfig("CharacterSelectFloodProtector");
        Config.FLOOD_PROTECTOR_ITEM_AUCTION = new FloodProtectorConfig("ItemAuctionFloodProtector");
        final PropertiesParser serverSettings = new PropertiesParser("config/server.properties");
        try {
            Config.DATAPACK_ROOT = new File(serverSettings.getString("DatapackRoot", ".").replaceAll("\\\\", "/")).getCanonicalFile();
        }
        catch (IOException e) {
            Config.LOGGER.warn("Error setting datapack root!", (Throwable)e);
            Config.DATAPACK_ROOT = new File(".");
        }
        Config.PET_NAME_TEMPLATE = serverSettings.getString("PetNameTemplate", ".*");
        Config.CLAN_NAME_TEMPLATE = serverSettings.getString("ClanNameTemplate", ".*");
        Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT = serverSettings.getInt("CharMaxNumber", 7);
        Config.HARDWARE_INFO_ENABLED = serverSettings.getBoolean("EnableHardwareInfo", false);
        Config.MAX_PLAYERS_PER_HWID = serverSettings.getInt("MaxPlayersPerHWID", 0);
        Config.SERVER_LIST_AGE = serverSettings.getInt("ServerListAge", 0);
        Config.SERVER_LIST_BRACKET = serverSettings.getBoolean("ServerListBrackets", false);
        Config.SERVER_RESTART_SCHEDULE_MESSAGE = serverSettings.getBoolean("ServerRestartScheduleMessage", false);
        Config.SERVER_RESTART_SCHEDULE_COUNTDOWN = serverSettings.getInt("ServerRestartScheduleCountdown", 600);
        Config.SERVER_RESTART_SCHEDULE = serverSettings.getString("ServerRestartSchedule", "08:00").split(",");
        final IPConfigData ipcd = new IPConfigData();
        Config.GAME_SERVER_SUBNETS = ipcd.getSubnets();
        Config.GAME_SERVER_HOSTS = ipcd.getHosts();
        final PropertiesParser Feature = new PropertiesParser("config/feature.properties");
        Config.CASTLE_BUY_TAX_NEUTRAL = Feature.getInt("BuyTaxForNeutralSide", 15);
        Config.CASTLE_BUY_TAX_LIGHT = Feature.getInt("BuyTaxForLightSide", 0);
        Config.CASTLE_BUY_TAX_DARK = Feature.getInt("BuyTaxForDarkSide", 30);
        Config.CASTLE_SELL_TAX_NEUTRAL = Feature.getInt("SellTaxForNeutralSide", 0);
        Config.CASTLE_SELL_TAX_LIGHT = Feature.getInt("SellTaxForLightSide", 0);
        Config.CASTLE_SELL_TAX_DARK = Feature.getInt("SellTaxForDarkSide", 20);
        Config.CS_TELE_FEE_RATIO = Feature.getLong("CastleTeleportFunctionFeeRatio", 604800000L);
        Config.CS_TELE1_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl1", 1000);
        Config.CS_TELE2_FEE = Feature.getInt("CastleTeleportFunctionFeeLvl2", 10000);
        Config.CS_SUPPORT_FEE_RATIO = Feature.getLong("CastleSupportFunctionFeeRatio", 604800000L);
        Config.CS_SUPPORT1_FEE = Feature.getInt("CastleSupportFeeLvl1", 49000);
        Config.CS_SUPPORT2_FEE = Feature.getInt("CastleSupportFeeLvl2", 120000);
        Config.CS_MPREG_FEE_RATIO = Feature.getLong("CastleMpRegenerationFunctionFeeRatio", 604800000L);
        Config.CS_MPREG1_FEE = Feature.getInt("CastleMpRegenerationFeeLvl1", 45000);
        Config.CS_MPREG2_FEE = Feature.getInt("CastleMpRegenerationFeeLvl2", 65000);
        Config.CS_HPREG_FEE_RATIO = Feature.getLong("CastleHpRegenerationFunctionFeeRatio", 604800000L);
        Config.CS_HPREG1_FEE = Feature.getInt("CastleHpRegenerationFeeLvl1", 12000);
        Config.CS_HPREG2_FEE = Feature.getInt("CastleHpRegenerationFeeLvl2", 20000);
        Config.CS_EXPREG_FEE_RATIO = Feature.getLong("CastleExpRegenerationFunctionFeeRatio", 604800000L);
        Config.CS_EXPREG1_FEE = Feature.getInt("CastleExpRegenerationFeeLvl1", 63000);
        Config.CS_EXPREG2_FEE = Feature.getInt("CastleExpRegenerationFeeLvl2", 70000);
        Config.OUTER_DOOR_UPGRADE_PRICE2 = Feature.getInt("OuterDoorUpgradePriceLvl2", 3000000);
        Config.OUTER_DOOR_UPGRADE_PRICE3 = Feature.getInt("OuterDoorUpgradePriceLvl3", 4000000);
        Config.OUTER_DOOR_UPGRADE_PRICE5 = Feature.getInt("OuterDoorUpgradePriceLvl5", 5000000);
        Config.INNER_DOOR_UPGRADE_PRICE2 = Feature.getInt("InnerDoorUpgradePriceLvl2", 750000);
        Config.INNER_DOOR_UPGRADE_PRICE3 = Feature.getInt("InnerDoorUpgradePriceLvl3", 900000);
        Config.INNER_DOOR_UPGRADE_PRICE5 = Feature.getInt("InnerDoorUpgradePriceLvl5", 1000000);
        Config.WALL_UPGRADE_PRICE2 = Feature.getInt("WallUpgradePriceLvl2", 1600000);
        Config.WALL_UPGRADE_PRICE3 = Feature.getInt("WallUpgradePriceLvl3", 1800000);
        Config.WALL_UPGRADE_PRICE5 = Feature.getInt("WallUpgradePriceLvl5", 2000000);
        Config.TRAP_UPGRADE_PRICE1 = Feature.getInt("TrapUpgradePriceLvl1", 3000000);
        Config.TRAP_UPGRADE_PRICE2 = Feature.getInt("TrapUpgradePriceLvl2", 4000000);
        Config.TRAP_UPGRADE_PRICE3 = Feature.getInt("TrapUpgradePriceLvl3", 5000000);
        Config.TRAP_UPGRADE_PRICE4 = Feature.getInt("TrapUpgradePriceLvl4", 6000000);
        Config.TAKE_CASTLE_POINTS = Feature.getInt("TakeCastlePoints", 1500);
        Config.LOOSE_CASTLE_POINTS = Feature.getInt("LooseCastlePoints", 3000);
        Config.CASTLE_DEFENDED_POINTS = Feature.getInt("CastleDefendedPoints", 750);
        Config.HERO_POINTS = Feature.getInt("HeroPoints", 1000);
        Config.ROYAL_GUARD_COST = Feature.getInt("CreateRoyalGuardCost", 5000);
        Config.KNIGHT_UNIT_COST = Feature.getInt("CreateKnightUnitCost", 10000);
        Config.REPUTATION_SCORE_PER_KILL = Feature.getInt("ReputationScorePerKill", 1);
        Config.JOIN_ACADEMY_MIN_REP_SCORE = Feature.getInt("CompleteAcademyMinPoints", 190);
        Config.JOIN_ACADEMY_MAX_REP_SCORE = Feature.getInt("CompleteAcademyMaxPoints", 650);
        Config.ALLOW_WYVERN_ALWAYS = Feature.getBoolean("AllowRideWyvernAlways", false);
        Config.ALLOW_WYVERN_DURING_SIEGE = Feature.getBoolean("AllowRideWyvernDuringSiege", true);
        Config.ALLOW_MOUNTS_DURING_SIEGE = Feature.getBoolean("AllowRideMountsDuringSiege", false);
        final PropertiesParser Character = new PropertiesParser("config/character.properties");
        Config.RESPAWN_RESTORE_CP = Character.getDouble("RespawnRestoreCP", 0.0) / 100.0;
        Config.RESPAWN_RESTORE_HP = Character.getDouble("RespawnRestoreHP", 65.0) / 100.0;
        Config.RESPAWN_RESTORE_MP = Character.getDouble("RespawnRestoreMP", 0.0) / 100.0;
        Config.ENABLE_MODIFY_SKILL_DURATION = Character.getBoolean("EnableModifySkillDuration", false);
        if (Config.ENABLE_MODIFY_SKILL_DURATION) {
            final String[] propertySplit = Character.getString("SkillDurationList", "").split(";");
            Config.SKILL_DURATION_LIST = new HashMap<Integer, Integer>(propertySplit.length);
            for (final String skill : propertySplit) {
                final String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    Config.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, skill));
                }
                else {
                    try {
                        Config.SKILL_DURATION_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    }
                    catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            Config.LOGGER.warn(StringUtil.concat(new String[] { "[SkillDurationList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1] }));
                        }
                    }
                }
            }
        }
        Config.ENABLE_MODIFY_SKILL_REUSE = Character.getBoolean("EnableModifySkillReuse", false);
        if (Config.ENABLE_MODIFY_SKILL_REUSE) {
            final String[] propertySplit = Character.getString("SkillReuseList", "").split(";");
            Config.SKILL_REUSE_LIST = new HashMap<Integer, Integer>(propertySplit.length);
            for (final String skill : propertySplit) {
                final String[] skillSplit = skill.split(",");
                if (skillSplit.length != 2) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "[SkillReuseList]: invalid config property -> SkillReuseList \"", skill, "\"" }));
                }
                else {
                    try {
                        Config.SKILL_REUSE_LIST.put(Integer.parseInt(skillSplit[0]), Integer.parseInt(skillSplit[1]));
                    }
                    catch (NumberFormatException nfe) {
                        if (!skill.isEmpty()) {
                            Config.LOGGER.warn(StringUtil.concat(new String[] { "[SkillReuseList]: invalid config property -> SkillList \"", skillSplit[0], "\"", skillSplit[1] }));
                        }
                    }
                }
            }
        }
        Config.AUTO_LEARN_SKILLS = Character.getBoolean("AutoLearnSkills", false);
        Config.AUTO_LEARN_FS_SKILLS = Character.getBoolean("AutoLearnForgottenScrollSkills", false);
        Config.AUTO_LOOT_HERBS = Character.getBoolean("AutoLootHerbs", false);
        Config.BUFFS_MAX_AMOUNT = Character.getByte("MaxBuffAmount", (byte)20);
        Config.TRIGGERED_BUFFS_MAX_AMOUNT = Character.getByte("MaxTriggeredBuffAmount", (byte)12);
        Config.DANCES_MAX_AMOUNT = Character.getByte("MaxDanceAmount", (byte)12);
        Config.DANCE_CANCEL_BUFF = Character.getBoolean("DanceCancelBuff", false);
        Config.DANCE_CONSUME_ADDITIONAL_MP = Character.getBoolean("DanceConsumeAdditionalMP", true);
        Config.ALT_STORE_DANCES = Character.getBoolean("AltStoreDances", false);
        Config.ALT_GAME_CANCEL_BOW = (Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("bow") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all"));
        Config.ALT_GAME_CANCEL_CAST = (Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("cast") || Character.getString("AltGameCancelByHit", "Cast").equalsIgnoreCase("all"));
        Config.ALT_GAME_MAGICFAILURES = Character.getBoolean("MagicFailures", true);
        Config.ALT_GAME_STUN_BREAK = Character.getBoolean("BreakStun", false);
        Config.PLAYER_FAKEDEATH_UP_PROTECTION = Character.getInt("PlayerFakeDeathUpProtection", 0);
        Config.STORE_SKILL_COOLTIME = Character.getBoolean("StoreSkillCooltime", true);
        Config.SUBCLASS_STORE_SKILL_COOLTIME = Character.getBoolean("SubclassStoreSkillCooltime", false);
        Config.SUMMON_STORE_SKILL_COOLTIME = Character.getBoolean("SummonStoreSkillCooltime", true);
        Config.EFFECT_TICK_RATIO = Character.getLong("EffectTickRatio", 666L);
        Config.LIFE_CRYSTAL_NEEDED = Character.getBoolean("LifeCrystalNeeded", true);
        Config.DIVINE_SP_BOOK_NEEDED = Character.getBoolean("DivineInspirationSpBookNeeded", true);
        Config.ALT_GAME_SUBCLASS_WITHOUT_QUESTS = Character.getBoolean("AltSubClassWithoutQuests", false);
        Config.ALT_GAME_SUBCLASS_EVERYWHERE = Character.getBoolean("AltSubclassEverywhere", false);
        Config.ALLOW_TRANSFORM_WITHOUT_QUEST = Character.getBoolean("AltTransformationWithoutQuest", false);
        Config.ENABLE_VITALITY = Character.getBoolean("EnableVitality", true);
        Config.STARTING_VITALITY_POINTS = Character.getInt("StartingVitalityPoints", 140000);
        Config.RAIDBOSS_USE_VITALITY = Character.getBoolean("RaidbossUseVitality", true);
        Config.MAX_BONUS_EXP = Character.getDouble("MaxExpBonus", 0.0);
        Config.MAX_BONUS_SP = Character.getDouble("MaxSpBonus", 0.0);
        Config.MAX_RUN_SPEED = Character.getInt("MaxRunSpeed", 300);
        Config.MAX_PATK = Character.getInt("MaxPAtk", 999999);
        Config.MAX_MATK = Character.getInt("MaxMAtk", 999999);
        Config.MAX_PCRIT_RATE = Character.getInt("MaxPCritRate", 500);
        Config.MAX_MCRIT_RATE = Character.getInt("MaxMCritRate", 200);
        Config.MAX_PATK_SPEED = Character.getInt("MaxPAtkSpeed", 1500);
        Config.MAX_MATK_SPEED = Character.getInt("MaxMAtkSpeed", 1999);
        Config.MAX_EVASION = Character.getInt("MaxEvasion", 250);
        Config.MIN_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MinAbnormalStateSuccessRate", 10);
        Config.MAX_ABNORMAL_STATE_SUCCESS_RATE = Character.getInt("MaxAbnormalStateSuccessRate", 90);
        Config.MAX_SP = ((Character.getLong("MaxSp", 50000000000L) >= 0L) ? Character.getLong("MaxSp", 50000000000L) : Long.MAX_VALUE);
        Config.MAX_SUBCLASS = (byte)Math.min(3, Character.getByte("MaxSubclass", (byte)3));
        Config.BASE_SUBCLASS_LEVEL = Character.getByte("BaseSubclassLevel", (byte)40);
        Config.BASE_DUALCLASS_LEVEL = Character.getByte("BaseDualclassLevel", (byte)85);
        Config.MAX_SUBCLASS_LEVEL = Character.getByte("MaxSubclassLevel", (byte)80);
        Config.MAX_PVTSTORESELL_SLOTS_DWARF = Character.getInt("MaxPvtStoreSellSlotsDwarf", 4);
        Config.MAX_PVTSTORESELL_SLOTS_OTHER = Character.getInt("MaxPvtStoreSellSlotsOther", 3);
        Config.MAX_PVTSTOREBUY_SLOTS_DWARF = Character.getInt("MaxPvtStoreBuySlotsDwarf", 5);
        Config.MAX_PVTSTOREBUY_SLOTS_OTHER = Character.getInt("MaxPvtStoreBuySlotsOther", 4);
        Config.INVENTORY_MAXIMUM_NO_DWARF = Character.getInt("MaximumSlotsForNoDwarf", 80);
        Config.INVENTORY_MAXIMUM_DWARF = Character.getInt("MaximumSlotsForDwarf", 100);
        Config.INVENTORY_MAXIMUM_GM = Character.getInt("MaximumSlotsForGMPlayer", 250);
        Config.INVENTORY_MAXIMUM_QUEST_ITEMS = Character.getInt("MaximumSlotsForQuestItems", 100);
        Config.MAX_ITEM_IN_PACKET = Math.max(Config.INVENTORY_MAXIMUM_NO_DWARF, Math.max(Config.INVENTORY_MAXIMUM_DWARF, Config.INVENTORY_MAXIMUM_GM));
        Config.WAREHOUSE_SLOTS_DWARF = Character.getInt("MaximumWarehouseSlotsForDwarf", 120);
        Config.WAREHOUSE_SLOTS_NO_DWARF = Character.getInt("MaximumWarehouseSlotsForNoDwarf", 100);
        Config.WAREHOUSE_SLOTS_CLAN = Character.getInt("MaximumWarehouseSlotsForClan", 150);
        Config.ALT_FREIGHT_SLOTS = Character.getInt("MaximumFreightSlots", 200);
        Config.ALT_FREIGHT_PRICE = Character.getInt("FreightPrice", 1000);
        Config.ENCHANT_CHANCE_ELEMENT_STONE = Character.getDouble("EnchantChanceElementStone", 50.0);
        Config.ENCHANT_CHANCE_ELEMENT_CRYSTAL = Character.getDouble("EnchantChanceElementCrystal", 30.0);
        Config.ENCHANT_CHANCE_ELEMENT_JEWEL = Character.getDouble("EnchantChanceElementJewel", 20.0);
        Config.ENCHANT_CHANCE_ELEMENT_ENERGY = Character.getDouble("EnchantChanceElementEnergy", 10.0);
        final String[] notenchantable = Character.getString("EnchantBlackList", "7816,7817,7818,7819,7820,7821,7822,7823,7824,7825,7826,7827,7828,7829,7830,7831,13293,13294,13296").split(",");
        Config.ENCHANT_BLACKLIST = new int[notenchantable.length];
        for (int i = 0; i < notenchantable.length; ++i) {
            Config.ENCHANT_BLACKLIST[i] = Integer.parseInt(notenchantable[i]);
        }
        Arrays.sort(Config.ENCHANT_BLACKLIST);
        Config.DISABLE_OVER_ENCHANTING = Character.getBoolean("DisableOverEnchanting", true);
        final String[] array = Character.getString("AugmentationBlackList", "6656,6657,6658,6659,6660,6661,6662,8191,10170,10314,13740,13741,13742,13743,13744,13745,13746,13747,13748,14592,14593,14594,14595,14596,14597,14598,14599,14600,14664,14665,14666,14667,14668,14669,14670,14671,14672,14801,14802,14803,14804,14805,14806,14807,14808,14809,15282,15283,15284,15285,15286,15287,15288,15289,15290,15291,15292,15293,15294,15295,15296,15297,15298,15299,16025,16026,21712,22173,22174,22175").split(",");
        Config.AUGMENTATION_BLACKLIST = new int[array.length];
        for (int j = 0; j < array.length; ++j) {
            Config.AUGMENTATION_BLACKLIST[j] = Integer.parseInt(array[j]);
        }
        Arrays.sort(Config.AUGMENTATION_BLACKLIST);
        Config.ALT_ALLOW_AUGMENT_PVP_ITEMS = Character.getBoolean("AltAllowAugmentPvPItems", false);
        Config.ALT_GAME_KARMA_PLAYER_CAN_BE_KILLED_IN_PEACEZONE = Character.getBoolean("AltKarmaPlayerCanBeKilledInPeaceZone", false);
        Config.ALT_GAME_KARMA_PLAYER_CAN_SHOP = Character.getBoolean("AltKarmaPlayerCanShop", true);
        Config.ALT_GAME_KARMA_PLAYER_CAN_TELEPORT = Character.getBoolean("AltKarmaPlayerCanTeleport", true);
        Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK = Character.getBoolean("AltKarmaPlayerCanUseGK", false);
        Config.ALT_GAME_KARMA_PLAYER_CAN_TRADE = Character.getBoolean("AltKarmaPlayerCanTrade", true);
        Config.ALT_GAME_KARMA_PLAYER_CAN_USE_WAREHOUSE = Character.getBoolean("AltKarmaPlayerCanUseWareHouse", true);
        Config.MAX_PERSONAL_FAME_POINTS = Character.getInt("MaxPersonalFamePoints", 100000);
        Config.CASTLE_ZONE_FAME_TASK_FREQUENCY = Character.getInt("CastleZoneFameTaskFrequency", 300);
        Config.CASTLE_ZONE_FAME_AQUIRE_POINTS = Character.getInt("CastleZoneFameAquirePoints", 125);
        Config.FAME_FOR_DEAD_PLAYERS = Character.getBoolean("FameForDeadPlayers", true);
        Config.IS_CRAFTING_ENABLED = Character.getBoolean("CraftingEnabled", true);
        Config.CRAFT_MASTERWORK = Character.getBoolean("CraftMasterwork", true);
        Config.BASE_CRITICAL_CRAFT_RATE = Character.getInt("BaseCriticalCraftRate", 3);
        Config.DWARF_RECIPE_LIMIT = Character.getInt("DwarfRecipeLimit", 50);
        Config.COMMON_RECIPE_LIMIT = Character.getInt("CommonRecipeLimit", 50);
        Config.ALT_GAME_CREATION = Character.getBoolean("AltGameCreation", false);
        Config.ALT_GAME_CREATION_SPEED = Character.getDouble("AltGameCreationSpeed", 1.0);
        Config.ALT_GAME_CREATION_XP_RATE = Character.getDouble("AltGameCreationXpRate", 1.0);
        Config.ALT_GAME_CREATION_SP_RATE = Character.getDouble("AltGameCreationSpRate", 1.0);
        Config.ALT_GAME_CREATION_RARE_XPSP_RATE = Character.getDouble("AltGameCreationRareXpSpRate", 2.0);
        Config.ALT_CLAN_LEADER_INSTANT_ACTIVATION = Character.getBoolean("AltClanLeaderInstantActivation", false);
        Config.ALT_CLAN_JOIN_DAYS = Character.getInt("DaysBeforeJoinAClan", 1);
        Config.ALT_CLAN_CREATE_DAYS = Character.getInt("DaysBeforeCreateAClan", 10);
        Config.ALT_CLAN_DISSOLVE_DAYS = Character.getInt("DaysToPassToDissolveAClan", 7);
        Config.ALT_ALLY_JOIN_DAYS_WHEN_LEAVED = Character.getInt("DaysBeforeJoinAllyWhenLeaved", 1);
        Config.ALT_ALLY_JOIN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeJoinAllyWhenDismissed", 1);
        Config.ALT_ACCEPT_CLAN_DAYS_WHEN_DISMISSED = Character.getInt("DaysBeforeAcceptNewClanWhenDismissed", 1);
        Config.ALT_CREATE_ALLY_DAYS_WHEN_DISSOLVED = Character.getInt("DaysBeforeCreateNewAllyWhenDissolved", 1);
        Config.ALT_MAX_NUM_OF_CLANS_IN_ALLY = Character.getInt("AltMaxNumOfClansInAlly", 3);
        Config.ALT_CLAN_MEMBERS_FOR_WAR = Character.getInt("AltClanMembersForWar", 15);
        Config.ALT_MEMBERS_CAN_WITHDRAW_FROM_CLANWH = Character.getBoolean("AltMembersCanWithdrawFromClanWH", false);
        Config.ALT_CLAN_MEMBERS_TIME_FOR_BONUS = Character.getDuration("AltClanMembersTimeForBonus", "30mins").toMillis();
        Config.REMOVE_CASTLE_CIRCLETS = Character.getBoolean("RemoveCastleCirclets", true);
        Config.ALT_PARTY_MAX_MEMBERS = Character.getInt("AltPartyMaxMembers", 7);
        Config.ALT_PARTY_RANGE = Character.getInt("AltPartyRange", 1600);
        Config.ALT_LEAVE_PARTY_LEADER = Character.getBoolean("AltLeavePartyLeader", false);
        Config.STARTING_ADENA = Character.getLong("StartingAdena", 0L);
        Config.STARTING_LEVEL = Character.getByte("StartingLevel", (byte)1);
        Config.STARTING_SP = Character.getInt("StartingSP", 0);
        Config.MAX_ADENA = Character.getLong("MaxAdena", 99900000000L);
        if (Config.MAX_ADENA < 0L) {
            Config.MAX_ADENA = Long.MAX_VALUE;
        }
        Config.AUTO_LOOT_SLOT_LIMIT = Character.getBoolean("AutoLootSlotLimit", false);
        Config.LOOT_RAIDS_PRIVILEGE_CC_SIZE = Character.getInt("RaidLootRightsCCSize", 45);
        Config.ENABLE_KEYBOARD_MOVEMENT = Character.getBoolean("KeyboardMovement", true);
        Config.UNSTUCK_INTERVAL = Character.getInt("UnstuckInterval", 300);
        Config.TELEPORT_WATCHDOG_TIMEOUT = Character.getInt("TeleportWatchdogTimeout", 0);
        Config.PLAYER_SPAWN_PROTECTION = Character.getInt("PlayerSpawnProtection", 0);
        Config.PLAYER_TELEPORT_PROTECTION = Character.getInt("PlayerTeleportProtection", 0);
        Config.RANDOM_RESPAWN_IN_TOWN_ENABLED = Character.getBoolean("RandomRespawnInTownEnabled", true);
        Config.OFFSET_ON_TELEPORT_ENABLED = Character.getBoolean("OffsetOnTeleportEnabled", true);
        Config.MAX_OFFSET_ON_TELEPORT = Character.getInt("MaxOffsetOnTeleport", 50);
        Config.PETITIONING_ALLOWED = Character.getBoolean("PetitioningAllowed", true);
        Config.MAX_PETITIONS_PER_PLAYER = Character.getInt("MaxPetitionsPerPlayer", 5);
        Config.MAX_PETITIONS_PENDING = Character.getInt("MaxPetitionsPending", 25);
        Config.MAX_FREE_TELEPORT_LEVEL = Character.getInt("MaxFreeTeleportLevel", 0);
        Config.MAX_NEWBIE_BUFF_LEVEL = Character.getInt("MaxNewbieBuffLevel", 0);
        Config.DELETE_DAYS = Character.getInt("DeleteCharAfterDays", 1);
        Config.PARTY_XP_CUTOFF_METHOD = Character.getString("PartyXpCutoffMethod", "highfive").toLowerCase();
        Config.PARTY_XP_CUTOFF_PERCENT = Character.getDouble("PartyXpCutoffPercent", 3.0);
        Config.PARTY_XP_CUTOFF_LEVEL = Character.getInt("PartyXpCutoffLevel", 20);
        final String[] gaps = Character.getString("PartyXpCutoffGaps", "0,9;10,14;15,99").split(";");
        Config.PARTY_XP_CUTOFF_GAPS = new int[gaps.length][2];
        for (int k = 0; k < gaps.length; ++k) {
            Config.PARTY_XP_CUTOFF_GAPS[k] = new int[] { Integer.parseInt(gaps[k].split(",")[0]), Integer.parseInt(gaps[k].split(",")[1]) };
        }
        final String[] percents = Character.getString("PartyXpCutoffGapPercent", "100;30;0").split(";");
        Config.PARTY_XP_CUTOFF_GAP_PERCENTS = new int[percents.length];
        for (int l = 0; l < percents.length; ++l) {
            Config.PARTY_XP_CUTOFF_GAP_PERCENTS[l] = Integer.parseInt(percents[l]);
        }
        Config.DISABLE_TUTORIAL = Character.getBoolean("DisableTutorial", false);
        Config.STORE_RECIPE_SHOPLIST = Character.getBoolean("StoreRecipeShopList", false);
        Config.STORE_UI_SETTINGS = Character.getBoolean("StoreCharUiSettings", true);
        Config.PLAYER_MOVEMENT_BLOCK_TIME = Character.getInt("NpcTalkBlockingTime", 0) * 1000;
        final PropertiesParser General = new PropertiesParser("config/general.properties");
        Config.SERVER_GMONLY = General.getBoolean("ServerGMOnly", false);
        Config.GM_HERO_AURA = General.getBoolean("GMHeroAura", false);
        Config.GM_STARTUP_BUILDER_HIDE = General.getBoolean("GMStartupBuilderHide", false);
        Config.GM_STARTUP_INVULNERABLE = General.getBoolean("GMStartupInvulnerable", false);
        Config.GM_STARTUP_INVISIBLE = General.getBoolean("GMStartupInvisible", false);
        Config.GM_STARTUP_SILENCE = General.getBoolean("GMStartupSilence", false);
        Config.GM_STARTUP_AUTO_LIST = General.getBoolean("GMStartupAutoList", false);
        Config.GM_STARTUP_DIET_MODE = General.getBoolean("GMStartupDietMode", false);
        Config.GM_ITEM_RESTRICTION = General.getBoolean("GMItemRestriction", true);
        Config.GM_SKILL_RESTRICTION = General.getBoolean("GMSkillRestriction", true);
        Config.GM_TRADE_RESTRICTED_ITEMS = General.getBoolean("GMTradeRestrictedItems", false);
        Config.GM_RESTART_FIGHTING = General.getBoolean("GMRestartFighting", true);
        Config.GM_ANNOUNCER_NAME = General.getBoolean("GMShowAnnouncerName", false);
        Config.GM_GIVE_SPECIAL_SKILLS = General.getBoolean("GMGiveSpecialSkills", false);
        Config.GM_GIVE_SPECIAL_AURA_SKILLS = General.getBoolean("GMGiveSpecialAuraSkills", false);
        Config.GM_DEBUG_HTML_PATHS = General.getBoolean("GMDebugHtmlPaths", true);
        Config.USE_SUPER_HASTE_AS_GM_SPEED = General.getBoolean("UseSuperHasteAsGMSpeed", false);
        Config.LOG_ITEM_ENCHANTS = General.getBoolean("LogItemEnchants", false);
        Config.LOG_SKILL_ENCHANTS = General.getBoolean("LogSkillEnchants", false);
        Config.SKILL_CHECK_ENABLE = General.getBoolean("SkillCheckEnable", false);
        Config.SKILL_CHECK_REMOVE = General.getBoolean("SkillCheckRemove", false);
        Config.SKILL_CHECK_GM = General.getBoolean("SkillCheckGM", true);
        Config.HTML_ACTION_CACHE_DEBUG = General.getBoolean("HtmlActionCacheDebug", false);
        Config.DEVELOPER = General.getBoolean("Developer", false);
        Config.ALT_DEV_NO_QUESTS = (General.getBoolean("AltDevNoQuests", false) || Boolean.getBoolean("noquests"));
        Config.ALT_DEV_NO_SPAWNS = (General.getBoolean("AltDevNoSpawns", false) || Boolean.getBoolean("nospawns"));
        Config.ALT_DEV_SHOW_QUESTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowQuestsLoadInLogs", false);
        Config.ALT_DEV_SHOW_SCRIPTS_LOAD_IN_LOGS = General.getBoolean("AltDevShowScriptsLoadInLogs", false);
        Config.ALLOW_DISCARDITEM = General.getBoolean("AllowDiscardItem", true);
        Config.LAZY_ITEMS_UPDATE = General.getBoolean("LazyItemsUpdate", false);
        Config.UPDATE_ITEMS_ON_CHAR_STORE = General.getBoolean("UpdateItemsOnCharStore", false);
        Config.DESTROY_ALL_ITEMS = General.getBoolean("DestroyAllItems", false);
        Config.AUTODELETE_INVALID_QUEST_DATA = General.getBoolean("AutoDeleteInvalidQuestData", false);
        Config.ENABLE_STORY_QUEST_BUFF_REWARD = General.getBoolean("StoryQuestRewardBuff", true);
        Config.MULTIPLE_ITEM_DROP = General.getBoolean("MultipleItemDrop", true);
        Config.FORCE_INVENTORY_UPDATE = General.getBoolean("ForceInventoryUpdate", false);
        Config.MIN_NPC_ANIMATION = General.getInt("MinNpcAnimation", 5);
        Config.MAX_NPC_ANIMATION = General.getInt("MaxNpcAnimation", 60);
        Config.MIN_MONSTER_ANIMATION = General.getInt("MinMonsterAnimation", 5);
        Config.MAX_MONSTER_ANIMATION = General.getInt("MaxMonsterAnimation", 60);
        Config.GRIDS_ALWAYS_ON = General.getBoolean("GridsAlwaysOn", false);
        Config.GRID_NEIGHBOR_TURNON_TIME = General.getInt("GridNeighborTurnOnTime", 1);
        Config.GRID_NEIGHBOR_TURNOFF_TIME = General.getInt("GridNeighborTurnOffTime", 90);
        Config.PEACE_ZONE_MODE = General.getInt("PeaceZoneMode", 0);
        Config.WAREHOUSE_CACHE = General.getBoolean("WarehouseCache", false);
        Config.WAREHOUSE_CACHE_TIME = General.getInt("WarehouseCacheTime", 15);
        Config.ALLOW_REFUND = General.getBoolean("AllowRefund", true);
        Config.ALLOW_ATTACHMENTS = General.getBoolean("AllowAttachments", true);
        Config.ALLOW_WEAR = General.getBoolean("AllowWear", true);
        Config.WEAR_DELAY = General.getInt("WearDelay", 5);
        Config.WEAR_PRICE = General.getInt("WearPrice", 10);
        Config.INSTANCE_FINISH_TIME = General.getInt("DefaultFinishTime", 5);
        Config.RESTORE_PLAYER_INSTANCE = General.getBoolean("RestorePlayerInstance", false);
        Config.EJECT_DEAD_PLAYER_TIME = General.getInt("EjectDeadPlayerTime", 1);
        Config.ALLOW_WATER = General.getBoolean("AllowWater", true);
        Config.ALLOW_FISHING = General.getBoolean("AllowFishing", true);
        Config.ALLOW_MANOR = General.getBoolean("AllowManor", true);
        Config.ALLOW_BOAT = General.getBoolean("AllowBoat", true);
        Config.BOAT_BROADCAST_RADIUS = General.getInt("BoatBroadcastRadius", 20000);
        Config.SERVER_NEWS = General.getBoolean("ShowServerNews", false);
        Config.ENABLE_COMMUNITY_BOARD = General.getBoolean("EnableCommunityBoard", true);
        Config.BBS_DEFAULT = General.getString("BBSDefault", "_bbshome");
        Config.WORLD_CHAT_POINTS_PER_DAY = General.getInt("WorldChatPointsPerDay", 10);
        Config.ALT_MANOR_REFRESH_TIME = General.getInt("AltManorRefreshTime", 20);
        Config.ALT_MANOR_REFRESH_MIN = General.getInt("AltManorRefreshMin", 0);
        Config.ALT_MANOR_APPROVE_TIME = General.getInt("AltManorApproveTime", 4);
        Config.ALT_MANOR_APPROVE_MIN = General.getInt("AltManorApproveMin", 30);
        Config.ALT_MANOR_MAINTENANCE_MIN = General.getInt("AltManorMaintenanceMin", 6);
        Config.ALT_MANOR_SAVE_PERIOD_RATE = General.getInt("AltManorSavePeriodRate", 2);
        Config.ALT_ITEM_AUCTION_ENABLED = General.getBoolean("AltItemAuctionEnabled", true);
        Config.ALT_ITEM_AUCTION_EXPIRED_AFTER = General.getInt("AltItemAuctionExpiredAfter", 14);
        Config.ALT_ITEM_AUCTION_TIME_EXTENDS_ON_BID = General.getInt("AltItemAuctionTimeExtendsOnBid", 0) * 1000;
        Config.DEFAULT_PUNISH_PARAM = General.getInt("DefaultPunishParam", 0);
        Config.ONLY_GM_ITEMS_FREE = General.getBoolean("OnlyGMItemsFree", true);
        Config.JAIL_IS_PVP = General.getBoolean("JailIsPvp", false);
        Config.JAIL_DISABLE_TRANSACTION = General.getBoolean("JailDisableTransaction", false);
        Config.CUSTOM_NPC_DATA = General.getBoolean("CustomNpcData", false);
        Config.CUSTOM_ITEMS_LOAD = General.getBoolean("CustomItemsLoad", false);
        Config.ALT_BIRTHDAY_GIFT = General.getInt("AltBirthdayGift", 22187);
        Config.ALT_BIRTHDAY_MAIL_SUBJECT = General.getString("AltBirthdayMailSubject", "Happy Birthday!");
        Config.ALT_BIRTHDAY_MAIL_TEXT = General.getString("AltBirthdayMailText", invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, System.lineSeparator().repeat(2)));
        Config.ENABLE_BLOCK_CHECKER_EVENT = General.getBoolean("EnableBlockCheckerEvent", false);
        Config.HBCE_FAIR_PLAY = General.getBoolean("HBCEFairPlay", false);
        Config.BOTREPORT_ENABLE = General.getBoolean("EnableBotReportButton", false);
        Config.BOTREPORT_RESETPOINT_HOUR = General.getString("BotReportPointsResetHour", "00:00").split(":");
        Config.BOTREPORT_REPORT_DELAY = General.getInt("BotReportDelay", 30) * 60000;
        Config.BOTREPORT_ALLOW_REPORTS_FROM_SAME_CLAN_MEMBERS = General.getBoolean("AllowReportsFromSameClanMembers", false);
        Config.ENABLE_FALLING_DAMAGE = General.getBoolean("EnableFallingDamage", true);
        final PropertiesParser FloodProtectors = new PropertiesParser("./config/FloodProtector.ini");
        loadFloodProtectorConfigs(FloodProtectors);
        final PropertiesParser NPC = new PropertiesParser("./config/NPC.ini");
        Config.ALT_MOB_AGRO_IN_PEACEZONE = NPC.getBoolean("AltMobAgroInPeaceZone", true);
        Config.ALT_ATTACKABLE_NPCS = NPC.getBoolean("AltAttackableNpcs", true);
        Config.ALT_GAME_VIEWNPC = NPC.getBoolean("AltGameViewNpc", false);
        Config.SHOW_NPC_LVL = NPC.getBoolean("ShowNpcLevel", false);
        Config.SHOW_CREST_WITHOUT_QUEST = NPC.getBoolean("ShowCrestWithoutQuest", false);
        Config.ENABLE_RANDOM_ENCHANT_EFFECT = NPC.getBoolean("EnableRandomEnchantEffect", false);
        Config.MIN_NPC_LVL_DMG_PENALTY = NPC.getInt("MinNPCLevelForDmgPenalty", 78);
        Config.NPC_DMG_PENALTY = parseConfigLine(NPC.getString("DmgPenaltyForLvLDifferences", "0.7, 0.6, 0.6, 0.55"));
        Config.NPC_CRIT_DMG_PENALTY = parseConfigLine(NPC.getString("CritDmgPenaltyForLvLDifferences", "0.75, 0.65, 0.6, 0.58"));
        Config.NPC_SKILL_DMG_PENALTY = parseConfigLine(NPC.getString("SkillDmgPenaltyForLvLDifferences", "0.8, 0.7, 0.65, 0.62"));
        Config.MIN_NPC_LVL_MAGIC_PENALTY = NPC.getInt("MinNPCLevelForMagicPenalty", 78);
        Config.NPC_SKILL_CHANCE_PENALTY = parseConfigLine(NPC.getString("SkillChancePenaltyForLvLDifferences", "2.5, 3.0, 3.25, 3.5"));
        Config.DEFAULT_CORPSE_TIME = NPC.getInt("DefaultCorpseTime", 7);
        Config.SPOILED_CORPSE_EXTEND_TIME = NPC.getInt("SpoiledCorpseExtendTime", 10);
        Config.CORPSE_CONSUME_SKILL_ALLOWED_TIME_BEFORE_DECAY = NPC.getInt("CorpseConsumeSkillAllowedTimeBeforeDecay", 2000);
        Config.MAX_DRIFT_RANGE = NPC.getInt("MaxDriftRange", 300);
        Config.AGGRO_DISTANCE_CHECK_ENABLED = NPC.getBoolean("AggroDistanceCheckEnabled", true);
        Config.AGGRO_DISTANCE_CHECK_RANGE = NPC.getInt("AggroDistanceCheckRange", 1500);
        Config.AGGRO_DISTANCE_CHECK_RAIDS = NPC.getBoolean("AggroDistanceCheckRaids", false);
        Config.AGGRO_DISTANCE_CHECK_INSTANCES = NPC.getBoolean("AggroDistanceCheckInstances", false);
        Config.AGGRO_DISTANCE_CHECK_RESTORE_LIFE = NPC.getBoolean("AggroDistanceCheckRestoreLife", true);
        Config.GUARD_ATTACK_AGGRO_MOB = NPC.getBoolean("GuardAttackAggroMob", false);
        Config.RAID_HP_REGEN_MULTIPLIER = NPC.getDouble("RaidHpRegenMultiplier", 100.0) / 100.0;
        Config.RAID_MP_REGEN_MULTIPLIER = NPC.getDouble("RaidMpRegenMultiplier", 100.0) / 100.0;
        Config.RAID_PDEFENCE_MULTIPLIER = NPC.getDouble("RaidPDefenceMultiplier", 100.0) / 100.0;
        Config.RAID_MDEFENCE_MULTIPLIER = NPC.getDouble("RaidMDefenceMultiplier", 100.0) / 100.0;
        Config.RAID_PATTACK_MULTIPLIER = NPC.getDouble("RaidPAttackMultiplier", 100.0) / 100.0;
        Config.RAID_MATTACK_MULTIPLIER = NPC.getDouble("RaidMAttackMultiplier", 100.0) / 100.0;
        Config.RAID_MIN_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMinRespawnMultiplier", 1.0f);
        Config.RAID_MAX_RESPAWN_MULTIPLIER = NPC.getFloat("RaidMaxRespawnMultiplier", 1.0f);
        Config.RAID_MINION_RESPAWN_TIMER = NPC.getInt("RaidMinionRespawnTime", 300000);
        Config.RAIDBOSS_LIMIT_BARRIER = NPC.getInt("LimitBarrier", 500);
        final String[] propertySplit2 = NPC.getString("CustomMinionsRespawnTime", "").split(";");
        Config.MINIONS_RESPAWN_TIME = new HashMap<Integer, Integer>(propertySplit2.length);
        for (final String prop : propertySplit2) {
            final String[] propSplit = prop.split(",");
            if (propSplit.length != 2) {
                Config.LOGGER.warn(StringUtil.concat(new String[] { "[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", prop, "\"" }));
            }
            try {
                Config.MINIONS_RESPAWN_TIME.put(Integer.valueOf(propSplit[0]), Integer.valueOf(propSplit[1]));
            }
            catch (NumberFormatException nfe2) {
                if (!prop.isEmpty()) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "[CustomMinionsRespawnTime]: invalid config property -> CustomMinionsRespawnTime \"", propSplit[0], "\"", propSplit[1] }));
                }
            }
        }
        Config.FORCE_DELETE_MINIONS = NPC.getBoolean("ForceDeleteMinions", false);
        Config.DESPAWN_MINION_DELAY = NPC.getLong("DespawnDelayMinions", 20000L);
        Config.RAID_DISABLE_CURSE = NPC.getBoolean("DisableRaidCurse", false);
        Config.RAID_CHAOS_TIME = NPC.getInt("RaidChaosTime", 10);
        Config.GRAND_CHAOS_TIME = NPC.getInt("GrandChaosTime", 10);
        Config.MINION_CHAOS_TIME = NPC.getInt("MinionChaosTime", 10);
        Config.INVENTORY_MAXIMUM_PET = NPC.getInt("MaximumSlotsForPet", 12);
        Config.PET_HP_REGEN_MULTIPLIER = NPC.getDouble("PetHpRegenMultiplier", 100.0) / 100.0;
        Config.PET_MP_REGEN_MULTIPLIER = NPC.getDouble("PetMpRegenMultiplier", 100.0) / 100.0;
        Config.VITALITY_CONSUME_BY_MOB = NPC.getInt("VitalityConsumeByMob", 2250);
        Config.VITALITY_CONSUME_BY_BOSS = NPC.getInt("VitalityConsumeByBoss", 1125);
        final PropertiesParser RatesSettings = new PropertiesParser("config/rates.properties");
        Config.RATE_SP = RatesSettings.getFloat("RateSp", 1.0f);
        Config.RATE_PARTY_XP = RatesSettings.getFloat("RatePartyXp", 1.0f);
        Config.RATE_PARTY_SP = RatesSettings.getFloat("RatePartySp", 1.0f);
        Config.L2_COIN_DROP_RATE = RatesSettings.getFloat("L2CoinDropRate", 0.1f);
        Config.RATE_INSTANCE_XP = RatesSettings.getFloat("RateInstanceXp", -1.0f);
        if (Config.RATE_INSTANCE_XP < 0.0f) {
            Config.RATE_INSTANCE_XP = ((RateSettings)Configurator.getSettings((Class)RateSettings.class)).xp();
        }
        Config.RATE_INSTANCE_SP = RatesSettings.getFloat("RateInstanceSp", -1.0f);
        if (Config.RATE_INSTANCE_SP < 0.0f) {
            Config.RATE_INSTANCE_SP = Config.RATE_SP;
        }
        Config.RATE_INSTANCE_PARTY_XP = RatesSettings.getFloat("RateInstancePartyXp", -1.0f);
        if (Config.RATE_INSTANCE_PARTY_XP < 0.0f) {
            Config.RATE_INSTANCE_PARTY_XP = Config.RATE_PARTY_XP;
        }
        Config.RATE_INSTANCE_PARTY_SP = RatesSettings.getFloat("RateInstancePartyXp", -1.0f);
        if (Config.RATE_INSTANCE_PARTY_SP < 0.0f) {
            Config.RATE_INSTANCE_PARTY_SP = Config.RATE_PARTY_SP;
        }
        Config.RATE_EXTRACTABLE = RatesSettings.getFloat("RateExtractable", 1.0f);
        Config.RATE_DROP_MANOR = RatesSettings.getInt("RateDropManor", 1);
        Config.RATE_QUEST_DROP = RatesSettings.getFloat("RateQuestDrop", 1.0f);
        Config.RATE_QUEST_REWARD = RatesSettings.getFloat("RateQuestReward", 1.0f);
        Config.RATE_QUEST_REWARD_XP = RatesSettings.getFloat("RateQuestRewardXP", 1.0f);
        Config.RATE_QUEST_REWARD_SP = RatesSettings.getFloat("RateQuestRewardSP", 1.0f);
        Config.RATE_QUEST_REWARD_ADENA = RatesSettings.getFloat("RateQuestRewardAdena", 1.0f);
        Config.RATE_QUEST_REWARD_USE_MULTIPLIERS = RatesSettings.getBoolean("UseQuestRewardMultipliers", false);
        Config.RATE_QUEST_REWARD_POTION = RatesSettings.getFloat("RateQuestRewardPotion", 1.0f);
        Config.RATE_QUEST_REWARD_SCROLL = RatesSettings.getFloat("RateQuestRewardScroll", 1.0f);
        Config.RATE_QUEST_REWARD_RECIPE = RatesSettings.getFloat("RateQuestRewardRecipe", 1.0f);
        Config.RATE_QUEST_REWARD_MATERIAL = RatesSettings.getFloat("RateQuestRewardMaterial", 1.0f);
        Config.RATE_RAIDBOSS_POINTS = RatesSettings.getFloat("RateRaidbossPointsReward", 1.0f);
        Config.RATE_VITALITY_EXP_MULTIPLIER = RatesSettings.getFloat("RateVitalityExpMultiplier", 2.0f);
        Config.VITALITY_MAX_ITEMS_ALLOWED = RatesSettings.getInt("VitalityMaxItemsAllowed", 999);
        Config.RATE_VITALITY_LOST = RatesSettings.getFloat("RateVitalityLost", 1.0f);
        Config.RATE_VITALITY_GAIN = RatesSettings.getFloat("RateVitalityGain", 1.0f);
        Config.RATE_KARMA_LOST = RatesSettings.getFloat("RateKarmaLost", -1.0f);
        if (Config.RATE_KARMA_LOST == -1.0f) {
            Config.RATE_KARMA_LOST = ((RateSettings)Configurator.getSettings((Class)RateSettings.class)).xp();
        }
        Config.RATE_KARMA_EXP_LOST = RatesSettings.getFloat("RateKarmaExpLost", 1.0f);
        Config.RATE_SIEGE_GUARDS_PRICE = RatesSettings.getFloat("RateSiegeGuardsPrice", 1.0f);
        Config.PLAYER_DROP_LIMIT = RatesSettings.getInt("PlayerDropLimit", 3);
        Config.PLAYER_RATE_DROP = RatesSettings.getInt("PlayerRateDrop", 5);
        Config.PLAYER_RATE_DROP_ITEM = RatesSettings.getInt("PlayerRateDropItem", 70);
        Config.PLAYER_RATE_DROP_EQUIP = RatesSettings.getInt("PlayerRateDropEquip", 25);
        Config.PLAYER_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("PlayerRateDropEquipWeapon", 5);
        Config.PET_XP_RATE = RatesSettings.getFloat("PetXpRate", 1.0f);
        Config.PET_FOOD_RATE = RatesSettings.getInt("PetFoodRate", 1);
        Config.SINEATER_XP_RATE = RatesSettings.getFloat("SinEaterXpRate", 1.0f);
        Config.KARMA_DROP_LIMIT = RatesSettings.getInt("KarmaDropLimit", 10);
        Config.KARMA_RATE_DROP = RatesSettings.getInt("KarmaRateDrop", 70);
        Config.KARMA_RATE_DROP_ITEM = RatesSettings.getInt("KarmaRateDropItem", 50);
        Config.KARMA_RATE_DROP_EQUIP = RatesSettings.getInt("KarmaRateDropEquip", 40);
        Config.KARMA_RATE_DROP_EQUIP_WEAPON = RatesSettings.getInt("KarmaRateDropEquipWeapon", 10);
        Config.RATE_DEATH_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("DeathDropAmountMultiplier", 1.0f);
        Config.RATE_SPOIL_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("SpoilDropAmountMultiplier", 1.0f);
        Config.RATE_HERB_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("HerbDropAmountMultiplier", 1.0f);
        Config.RATE_RAID_DROP_AMOUNT_MULTIPLIER = RatesSettings.getFloat("RaidDropAmountMultiplier", 1.0f);
        Config.RATE_DEATH_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("DeathDropChanceMultiplier", 1.0f);
        Config.RATE_SPOIL_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("SpoilDropChanceMultiplier", 1.0f);
        Config.RATE_HERB_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("HerbDropChanceMultiplier", 1.0f);
        Config.RATE_RAID_DROP_CHANCE_MULTIPLIER = RatesSettings.getFloat("RaidDropChanceMultiplier", 1.0f);
        final String[] dropAmountMultiplier = RatesSettings.getString("DropAmountMultiplierByItemId", "").split(";");
        Config.RATE_DROP_AMOUNT_BY_ID = new HashMap<Integer, Float>(dropAmountMultiplier.length);
        if (!dropAmountMultiplier[0].isEmpty()) {
            for (final String item : dropAmountMultiplier) {
                final String[] itemSplit = item.split(",");
                if (itemSplit.length != 2) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "Config.load(): invalid config property -> RateDropItemsById \"", item, "\"" }));
                }
                else {
                    try {
                        Config.RATE_DROP_AMOUNT_BY_ID.put(Integer.valueOf(itemSplit[0]), Float.valueOf(itemSplit[1]));
                    }
                    catch (NumberFormatException nfe3) {
                        if (!item.isEmpty()) {
                            Config.LOGGER.warn(StringUtil.concat(new String[] { "Config.load(): invalid config property -> RateDropItemsById \"", item, "\"" }));
                        }
                    }
                }
            }
        }
        final String[] dropChanceMultiplier = RatesSettings.getString("DropChanceMultiplierByItemId", "").split(";");
        Config.RATE_DROP_CHANCE_BY_ID = new HashMap<Integer, Float>(dropChanceMultiplier.length);
        if (!dropChanceMultiplier[0].isEmpty()) {
            for (final String item2 : dropChanceMultiplier) {
                final String[] itemSplit2 = item2.split(",");
                if (itemSplit2.length != 2) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "Config.load(): invalid config property -> RateDropItemsById \"", item2, "\"" }));
                }
                else {
                    try {
                        Config.RATE_DROP_CHANCE_BY_ID.put(Integer.valueOf(itemSplit2[0]), Float.valueOf(itemSplit2[1]));
                    }
                    catch (NumberFormatException nfe4) {
                        if (!item2.isEmpty()) {
                            Config.LOGGER.warn(StringUtil.concat(new String[] { "Config.load(): invalid config property -> RateDropItemsById \"", item2, "\"" }));
                        }
                    }
                }
            }
        }
        Config.DROP_MAX_OCCURRENCES_NORMAL = RatesSettings.getInt("DropMaxOccurrencesNormal", 2);
        Config.DROP_MAX_OCCURRENCES_RAIDBOSS = RatesSettings.getInt("DropMaxOccurrencesRaidboss", 7);
        Config.DROP_ADENA_MIN_LEVEL_DIFFERENCE = RatesSettings.getInt("DropAdenaMinLevelDifference", 8);
        Config.DROP_ADENA_MAX_LEVEL_DIFFERENCE = RatesSettings.getInt("DropAdenaMaxLevelDifference", 15);
        Config.DROP_ADENA_MIN_LEVEL_GAP_CHANCE = RatesSettings.getDouble("DropAdenaMinLevelGapChance", 10.0);
        Config.DROP_ITEM_MIN_LEVEL_DIFFERENCE = RatesSettings.getInt("DropItemMinLevelDifference", 5);
        Config.DROP_ITEM_MAX_LEVEL_DIFFERENCE = RatesSettings.getInt("DropItemMaxLevelDifference", 10);
        Config.DROP_ITEM_MIN_LEVEL_GAP_CHANCE = RatesSettings.getDouble("DropItemMinLevelGapChance", 10.0);
        final PropertiesParser PVPSettings = new PropertiesParser("./config/PVP.ini");
        Config.KARMA_DROP_GM = PVPSettings.getBoolean("CanGMDropEquipment", false);
        Config.KARMA_PK_LIMIT = PVPSettings.getInt("MinimumPKRequiredToDrop", 4);
        Config.KARMA_NONDROPPABLE_PET_ITEMS = PVPSettings.getString("ListOfPetItems", "2375,3500,3501,3502,4422,4423,4424,4425,6648,6649,6650,9882");
        Config.KARMA_NONDROPPABLE_ITEMS = PVPSettings.getString("ListOfNonDroppableItems", "57,1147,425,1146,461,10,2368,7,6,2370,2369,6842,6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,7694,8181,5575,7694,9388,9389,9390");
        String[] karma = Config.KARMA_NONDROPPABLE_PET_ITEMS.split(",");
        Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS = new int[karma.length];
        for (int m = 0; m < karma.length; ++m) {
            Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS[m] = Integer.parseInt(karma[m]);
        }
        karma = Config.KARMA_NONDROPPABLE_ITEMS.split(",");
        Config.KARMA_LIST_NONDROPPABLE_ITEMS = new int[karma.length];
        for (int m = 0; m < karma.length; ++m) {
            Config.KARMA_LIST_NONDROPPABLE_ITEMS[m] = Integer.parseInt(karma[m]);
        }
        Config.ANTIFEED_ENABLE = PVPSettings.getBoolean("AntiFeedEnable", false);
        Config.ANTIFEED_DUALBOX = PVPSettings.getBoolean("AntiFeedDualbox", true);
        Config.ANTIFEED_DISCONNECTED_AS_DUALBOX = PVPSettings.getBoolean("AntiFeedDisconnectedAsDualbox", true);
        Config.ANTIFEED_INTERVAL = PVPSettings.getInt("AntiFeedInterval", 120) * 1000;
        Arrays.sort(Config.KARMA_LIST_NONDROPPABLE_PET_ITEMS);
        Arrays.sort(Config.KARMA_LIST_NONDROPPABLE_ITEMS);
        Config.PVP_NORMAL_TIME = PVPSettings.getInt("PvPVsNormalTime", 120000);
        Config.PVP_PVP_TIME = PVPSettings.getInt("PvPVsPvPTime", 60000);
        Config.MAX_REPUTATION = PVPSettings.getInt("MaxReputation", 500);
        Config.REPUTATION_INCREASE = PVPSettings.getInt("ReputationIncrease", 100);
        final PropertiesParser Olympiad = new PropertiesParser("./config/Olympiad.ini");
        Config.ALT_OLY_BATTLE = Olympiad.getLong("AltOlyBattle", 300000L);
        Config.ALT_OLY_CLASSED = Olympiad.getInt("AltOlyClassedParticipants", 20);
        Config.ALT_OLY_NONCLASSED = Olympiad.getInt("AltOlyNonClassedParticipants", 20);
        Config.ALT_OLY_WINNER_REWARD = parseItemsList(Olympiad.getString("AltOlyWinReward", ""));
        Config.ALT_OLY_LOSER_REWARD = parseItemsList(Olympiad.getString("AltOlyLoserReward", ""));
        Config.ALT_OLY_COMP_RITEM = Olympiad.getInt("AltOlyCompRewItem", 45584);
        Config.ALT_OLY_MIN_MATCHES = Olympiad.getInt("AltOlyMinMatchesForPoints", 15);
        Config.ALT_OLY_MARK_PER_POINT = Olympiad.getInt("AltOlyMarkPerPoint", 20);
        Config.ALT_OLY_MAX_POINTS = Olympiad.getInt("AltOlyMaxPoints", 10);
        Config.ALT_OLY_DIVIDER_CLASSED = Olympiad.getInt("AltOlyDividerClassed", 5);
        Config.ALT_OLY_DIVIDER_NON_CLASSED = Olympiad.getInt("AltOlyDividerNonClassed", 5);
        Config.ALT_OLY_LOG_FIGHTS = Olympiad.getBoolean("AltOlyLogFights", false);
        Config.ALT_OLY_SHOW_MONTHLY_WINNERS = Olympiad.getBoolean("AltOlyShowMonthlyWinners", true);
        final String[] olyRestrictedItems = Olympiad.getString("AltOlyRestrictedItems", "6611,6612,6613,6614,6615,6616,6617,6618,6619,6620,6621,9388,9389,9390,17049,17050,17051,17052,17053,17054,17055,17056,17057,17058,17059,17060,17061,20759,20775,20776,20777,20778,14774").split(",");
        Config.LIST_OLY_RESTRICTED_ITEMS = new ArrayList<Integer>(olyRestrictedItems.length);
        for (final String id : olyRestrictedItems) {
            Config.LIST_OLY_RESTRICTED_ITEMS.add(Integer.parseInt(id));
        }
        Config.ALT_OLY_ENCHANT_LIMIT = Olympiad.getInt("AltOlyEnchantLimit", -1);
        Config.ALT_OLY_WAIT_TIME = Olympiad.getInt("AltOlyWaitTime", 60);
        final PropertiesParser GrandBossSettings = new PropertiesParser("./config/GrandBoss.ini");
        Config.ANTHARAS_WAIT_TIME = GrandBossSettings.getInt("AntharasWaitTime", 30);
        Config.ANTHARAS_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfAntharasSpawn", 264);
        Config.ANTHARAS_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfAntharasSpawn", 72);
        Config.BAIUM_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfBaiumSpawn", 168);
        Config.CORE_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfCoreSpawn", 60);
        Config.CORE_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfCoreSpawn", 24);
        Config.ORFEN_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfOrfenSpawn", 48);
        Config.ORFEN_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfOrfenSpawn", 20);
        Config.QUEEN_ANT_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfQueenAntSpawn", 36);
        Config.QUEEN_ANT_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfQueenAntSpawn", 17);
        Config.ZAKEN_SPAWN_INTERVAL = GrandBossSettings.getInt("IntervalOfZakenSpawn", 168);
        Config.ZAKEN_SPAWN_RANDOM = GrandBossSettings.getInt("RandomOfZakenSpawn", 48);
        final PropertiesParser GraciaSeedsSettings = new PropertiesParser("./config/GraciaSeeds.ini");
        Config.SOD_TIAT_KILL_COUNT = GraciaSeedsSettings.getInt("TiatKillCountForNextState", 10);
        Config.SOD_STAGE_2_LENGTH = GraciaSeedsSettings.getLong("Stage2Length", 720L) * 60000L;
        try {
            final Stream<String> lines = Files.lines(Paths.get("./config/chatfilter.txt", new String[0]), StandardCharsets.UTF_8);
            try {
                Config.FILTER_LIST = lines.map((Function<? super String, ?>)String::trim).filter(line -> !line.isEmpty() && line.charAt(0) != '#').collect((Collector<? super Object, ?, List<String>>)Collectors.toList());
                Config.LOGGER.info(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, Config.FILTER_LIST.size()));
                if (lines != null) {
                    lines.close();
                }
            }
            catch (Throwable t) {
                if (lines != null) {
                    try {
                        lines.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                }
                throw t;
            }
        }
        catch (IOException e2) {
            Config.LOGGER.warn("Error while loading chat filter words!", (Throwable)e2);
        }
        final PropertiesParser Banking = new PropertiesParser("./config/Custom/Banking.ini");
        Config.BANKING_SYSTEM_GOLDBARS = Banking.getInt("BankingGoldbarCount", 1);
        Config.BANKING_SYSTEM_ADENA = Banking.getInt("BankingAdenaCount", 500000000);
        final PropertiesParser BoostNpcStats = new PropertiesParser("./config/Custom/NpcStatMultipliers.ini");
        Config.ENABLE_NPC_STAT_MULTIPIERS = BoostNpcStats.getBoolean("EnableNpcStatMultipliers", false);
        Config.MONSTER_HP_MULTIPLIER = BoostNpcStats.getDouble("MonsterHP", 1.0);
        Config.MONSTER_MP_MULTIPLIER = BoostNpcStats.getDouble("MonsterMP", 1.0);
        Config.MONSTER_PATK_MULTIPLIER = BoostNpcStats.getDouble("MonsterPAtk", 1.0);
        Config.MONSTER_MATK_MULTIPLIER = BoostNpcStats.getDouble("MonsterMAtk", 1.0);
        Config.MONSTER_PDEF_MULTIPLIER = BoostNpcStats.getDouble("MonsterPDef", 1.0);
        Config.MONSTER_MDEF_MULTIPLIER = BoostNpcStats.getDouble("MonsterMDef", 1.0);
        Config.MONSTER_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("MonsterAggroRange", 1.0);
        Config.MONSTER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("MonsterClanHelpRange", 1.0);
        Config.RAIDBOSS_HP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossHP", 1.0);
        Config.RAIDBOSS_MP_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMP", 1.0);
        Config.RAIDBOSS_PATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPAtk", 1.0);
        Config.RAIDBOSS_MATK_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMAtk", 1.0);
        Config.RAIDBOSS_PDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossPDef", 1.0);
        Config.RAIDBOSS_MDEF_MULTIPLIER = BoostNpcStats.getDouble("RaidbossMDef", 1.0);
        Config.RAIDBOSS_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("RaidbossAggroRange", 1.0);
        Config.RAIDBOSS_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("RaidbossClanHelpRange", 1.0);
        Config.GUARD_HP_MULTIPLIER = BoostNpcStats.getDouble("GuardHP", 1.0);
        Config.GUARD_MP_MULTIPLIER = BoostNpcStats.getDouble("GuardMP", 1.0);
        Config.GUARD_PATK_MULTIPLIER = BoostNpcStats.getDouble("GuardPAtk", 1.0);
        Config.GUARD_MATK_MULTIPLIER = BoostNpcStats.getDouble("GuardMAtk", 1.0);
        Config.GUARD_PDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardPDef", 1.0);
        Config.GUARD_MDEF_MULTIPLIER = BoostNpcStats.getDouble("GuardMDef", 1.0);
        Config.GUARD_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("GuardAggroRange", 1.0);
        Config.GUARD_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("GuardClanHelpRange", 1.0);
        Config.DEFENDER_HP_MULTIPLIER = BoostNpcStats.getDouble("DefenderHP", 1.0);
        Config.DEFENDER_MP_MULTIPLIER = BoostNpcStats.getDouble("DefenderMP", 1.0);
        Config.DEFENDER_PATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderPAtk", 1.0);
        Config.DEFENDER_MATK_MULTIPLIER = BoostNpcStats.getDouble("DefenderMAtk", 1.0);
        Config.DEFENDER_PDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderPDef", 1.0);
        Config.DEFENDER_MDEF_MULTIPLIER = BoostNpcStats.getDouble("DefenderMDef", 1.0);
        Config.DEFENDER_AGRRO_RANGE_MULTIPLIER = BoostNpcStats.getDouble("DefenderAggroRange", 1.0);
        Config.DEFENDER_CLAN_HELP_RANGE_MULTIPLIER = BoostNpcStats.getDouble("DefenderClanHelpRange", 1.0);
        final PropertiesParser ChampionMonster = new PropertiesParser("./config/Custom/ChampionMonsters.ini");
        Config.CHAMPION_ENABLE = ChampionMonster.getBoolean("ChampionEnable", false);
        Config.CHAMPION_PASSIVE = ChampionMonster.getBoolean("ChampionPassive", false);
        Config.CHAMPION_FREQUENCY = ChampionMonster.getInt("ChampionFrequency", 0);
        Config.CHAMP_TITLE = ChampionMonster.getString("ChampionTitle", "Champion");
        Config.SHOW_CHAMPION_AURA = ChampionMonster.getBoolean("ChampionAura", true);
        Config.CHAMP_MIN_LVL = ChampionMonster.getInt("ChampionMinLevel", 20);
        Config.CHAMP_MAX_LVL = ChampionMonster.getInt("ChampionMaxLevel", 60);
        Config.CHAMPION_HP = ChampionMonster.getInt("ChampionHp", 7);
        Config.CHAMPION_HP_REGEN = ChampionMonster.getFloat("ChampionHpRegen", 1.0f);
        Config.CHAMPION_REWARDS_EXP_SP = ChampionMonster.getFloat("ChampionRewardsExpSp", 8.0f);
        Config.CHAMPION_REWARDS_CHANCE = ChampionMonster.getFloat("ChampionRewardsChance", 8.0f);
        Config.CHAMPION_REWARDS_AMOUNT = ChampionMonster.getFloat("ChampionRewardsAmount", 1.0f);
        Config.CHAMPION_ADENAS_REWARDS_CHANCE = ChampionMonster.getFloat("ChampionAdenasRewardsChance", 1.0f);
        Config.CHAMPION_ADENAS_REWARDS_AMOUNT = ChampionMonster.getFloat("ChampionAdenasRewardsAmount", 1.0f);
        Config.CHAMPION_ATK = ChampionMonster.getFloat("ChampionAtk", 1.0f);
        Config.CHAMPION_SPD_ATK = ChampionMonster.getFloat("ChampionSpdAtk", 1.0f);
        Config.CHAMPION_REWARD_LOWER_LVL_ITEM_CHANCE = ChampionMonster.getInt("ChampionRewardLowerLvlItemChance", 0);
        Config.CHAMPION_REWARD_HIGHER_LVL_ITEM_CHANCE = ChampionMonster.getInt("ChampionRewardHigherLvlItemChance", 0);
        Config.CHAMPION_REWARD_ID = ChampionMonster.getInt("ChampionRewardItemID", 6393);
        Config.CHAMPION_REWARD_QTY = ChampionMonster.getInt("ChampionRewardItemQty", 1);
        Config.CHAMPION_ENABLE_VITALITY = ChampionMonster.getBoolean("ChampionEnableVitality", false);
        Config.CHAMPION_ENABLE_IN_INSTANCES = ChampionMonster.getBoolean("ChampionEnableInInstances", false);
        final PropertiesParser CommunityBoard = new PropertiesParser("./config/Custom/CommunityBoard.ini");
        Config.CUSTOM_CB_ENABLED = CommunityBoard.getBoolean("CustomCommunityBoard", false);
        Config.COMMUNITYBOARD_CURRENCY = CommunityBoard.getInt("CommunityCurrencyId", 57);
        Config.COMMUNITYBOARD_ENABLE_MULTISELLS = CommunityBoard.getBoolean("CommunityEnableMultisells", true);
        Config.COMMUNITYBOARD_ENABLE_TELEPORTS = CommunityBoard.getBoolean("CommunityEnableTeleports", false);
        Config.COMMUNITYBOARD_ENABLE_BUFFS = CommunityBoard.getBoolean("CommunityEnableBuffs", false);
        Config.COMMUNITYBOARD_ENABLE_HEAL = CommunityBoard.getBoolean("CommunityEnableHeal", false);
        Config.COMMUNITYBOARD_ENABLE_PREMIUM = CommunityBoard.getBoolean("CommunityEnablePremium", false);
        Config.COMMUNITYBOARD_ENABLE_AUTO_HP_MP_CP = CommunityBoard.getBoolean("CommunityEnableAutoHpMpCp", false);
        Config.COMMUNITYBOARD_TELEPORT_PRICE = CommunityBoard.getInt("CommunityTeleportPrice", 0);
        Config.COMMUNITYBOARD_BUFF_PRICE = CommunityBoard.getInt("CommunityBuffPrice", 0);
        Config.COMMUNITYBOARD_HEAL_PRICE = CommunityBoard.getInt("CommunityHealPrice", 0);
        Config.COMMUNITYBOARD_KARMA_DISABLED = CommunityBoard.getBoolean("CommunityKarmaDisabled", true);
        Config.COMMUNITYBOARD_CAST_ANIMATIONS = CommunityBoard.getBoolean("CommunityCastAnimations", false);
        final String[] allowedBuffs = CommunityBoard.getString("CommunityAvailableBuffs", "").split(",");
        Config.COMMUNITY_AVAILABLE_BUFFS = new ArrayList<Integer>(allowedBuffs.length);
        for (final String s : allowedBuffs) {
            Config.COMMUNITY_AVAILABLE_BUFFS.add(Integer.parseInt(s));
        }
        final String[] availableTeleports = CommunityBoard.getString("CommunityTeleportList", "").split(";");
        Config.COMMUNITY_AVAILABLE_TELEPORTS = new HashMap<String, Location>(availableTeleports.length);
        for (final String s2 : availableTeleports) {
            final String[] splitInfo = s2.split(",");
            Config.COMMUNITY_AVAILABLE_TELEPORTS.put(splitInfo[0], new Location(Integer.parseInt(splitInfo[1]), Integer.parseInt(splitInfo[2]), Integer.parseInt(splitInfo[3])));
        }
        final PropertiesParser DualboxCheck = new PropertiesParser("./config/Custom/DualboxCheck.ini");
        Config.DUALBOX_CHECK_MAX_PLAYERS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxPlayersPerIP", 0);
        Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxOlympiadParticipantsPerIP", 0);
        Config.DUALBOX_CHECK_MAX_L2EVENT_PARTICIPANTS_PER_IP = DualboxCheck.getInt("DualboxCheckMaxL2EventParticipantsPerIP", 0);
        Config.DUALBOX_COUNT_OFFLINE_TRADERS = DualboxCheck.getBoolean("DualboxCountOfflineTraders", false);
        final String[] dualboxCheckWhiteList = DualboxCheck.getString("DualboxCheckWhitelist", "127.0.0.1,0").split(";");
        Config.DUALBOX_CHECK_WHITELIST = new HashMap<Integer, Integer>(dualboxCheckWhiteList.length);
        for (final String entry : dualboxCheckWhiteList) {
            final String[] entrySplit = entry.split(",");
            if (entrySplit.length != 2) {
                Config.LOGGER.warn(StringUtil.concat(new String[] { "DualboxCheck[Config.load()]: invalid config property -> DualboxCheckWhitelist \"", entry, "\"" }));
            }
            else {
                try {
                    int num = Integer.parseInt(entrySplit[1]);
                    num = ((num == 0) ? -1 : num);
                    Config.DUALBOX_CHECK_WHITELIST.put(InetAddress.getByName(entrySplit[0]).hashCode(), num);
                }
                catch (UnknownHostException e3) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "DualboxCheck[Config.load()]: invalid address -> DualboxCheckWhitelist \"", entrySplit[0], "\"" }));
                }
                catch (NumberFormatException e4) {
                    Config.LOGGER.warn(StringUtil.concat(new String[] { "DualboxCheck[Config.load()]: invalid number -> DualboxCheckWhitelist \"", entrySplit[1], "\"" }));
                }
            }
        }
        final PropertiesParser MultilingualSupport = new PropertiesParser("./config/Custom/MultilingualSupport.ini");
        Config.MULTILANG_DEFAULT = MultilingualSupport.getString("MultiLangDefault", "en");
        Config.MULTILANG_ENABLE = MultilingualSupport.getBoolean("MultiLangEnable", false);
        final String[] allowed = MultilingualSupport.getString("MultiLangAllowed", Config.MULTILANG_DEFAULT).split(";");
        Config.MULTILANG_ALLOWED = new ArrayList<String>(allowed.length);
        for (final String lang : allowed) {
            Config.MULTILANG_ALLOWED.add(lang);
        }
        if (!Config.MULTILANG_ALLOWED.contains(Config.MULTILANG_DEFAULT)) {
            Config.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, Config.MULTILANG_DEFAULT));
        }
        Config.MULTILANG_VOICED_ALLOW = MultilingualSupport.getBoolean("MultiLangVoiceCommand", true);
        final PropertiesParser PcCafe = new PropertiesParser("./config/Custom/PcCafe.ini");
        Config.PC_CAFE_ENABLED = PcCafe.getBoolean("PcCafeEnabled", false);
        Config.PC_CAFE_ONLY_VIP = PcCafe.getBoolean("PcCafeOnlyVip", false);
        Config.PC_CAFE_MAX_POINTS = PcCafe.getInt("MaxPcCafePoints", 200000);
        if (Config.PC_CAFE_MAX_POINTS < 0) {
            Config.PC_CAFE_MAX_POINTS = 0;
        }
        Config.PC_CAFE_ENABLE_DOUBLE_POINTS = PcCafe.getBoolean("DoublingAcquisitionPoints", false);
        Config.PC_CAFE_DOUBLE_POINTS_CHANCE = PcCafe.getInt("DoublingAcquisitionPointsChance", 1);
        if (Config.PC_CAFE_DOUBLE_POINTS_CHANCE < 0 || Config.PC_CAFE_DOUBLE_POINTS_CHANCE > 100) {
            Config.PC_CAFE_DOUBLE_POINTS_CHANCE = 1;
        }
        Config.PC_CAFE_POINT_RATE = PcCafe.getDouble("AcquisitionPointsRate", 1.0);
        Config.PC_CAFE_RANDOM_POINT = PcCafe.getBoolean("AcquisitionPointsRandom", false);
        if (Config.PC_CAFE_POINT_RATE < 0.0) {
            Config.PC_CAFE_POINT_RATE = 1.0;
        }
        Config.PC_CAFE_REWARD_LOW_EXP_KILLS = PcCafe.getBoolean("RewardLowExpKills", true);
        Config.PC_CAFE_LOW_EXP_KILLS_CHANCE = PcCafe.getInt("RewardLowExpKillsChance", 50);
        if (Config.PC_CAFE_LOW_EXP_KILLS_CHANCE < 0) {
            Config.PC_CAFE_LOW_EXP_KILLS_CHANCE = 0;
        }
        if (Config.PC_CAFE_LOW_EXP_KILLS_CHANCE > 100) {
            Config.PC_CAFE_LOW_EXP_KILLS_CHANCE = 100;
        }
        final PropertiesParser PvpAnnounce = new PropertiesParser("./config/Custom/PvpAnnounce.ini");
        Config.ANNOUNCE_PK_PVP = PvpAnnounce.getBoolean("AnnouncePkPvP", false);
        Config.ANNOUNCE_PK_PVP_NORMAL_MESSAGE = PvpAnnounce.getBoolean("AnnouncePkPvPNormalMessage", true);
        Config.ANNOUNCE_PK_MSG = PvpAnnounce.getString("AnnouncePkMsg", "$killer has slaughtered $target");
        Config.ANNOUNCE_PVP_MSG = PvpAnnounce.getString("AnnouncePvpMsg", "$killer has defeated $target");
        final PropertiesParser PvpRewardItem = new PropertiesParser("./config/Custom/PvpRewardItem.ini");
        Config.REWARD_PVP_ITEM = PvpRewardItem.getBoolean("RewardPvpItem", false);
        Config.REWARD_PVP_ITEM_ID = PvpRewardItem.getInt("RewardPvpItemId", 57);
        Config.REWARD_PVP_ITEM_AMOUNT = PvpRewardItem.getInt("RewardPvpItemAmount", 1000);
        Config.REWARD_PVP_ITEM_MESSAGE = PvpRewardItem.getBoolean("RewardPvpItemMessage", true);
        Config.REWARD_PK_ITEM = PvpRewardItem.getBoolean("RewardPkItem", false);
        Config.REWARD_PK_ITEM_ID = PvpRewardItem.getInt("RewardPkItemId", 57);
        Config.REWARD_PK_ITEM_AMOUNT = PvpRewardItem.getInt("RewardPkItemAmount", 500);
        Config.REWARD_PK_ITEM_MESSAGE = PvpRewardItem.getBoolean("RewardPkItemMessage", true);
        Config.DISABLE_REWARDS_IN_INSTANCES = PvpRewardItem.getBoolean("DisableRewardsInInstances", true);
        Config.DISABLE_REWARDS_IN_PVP_ZONES = PvpRewardItem.getBoolean("DisableRewardsInPvpZones", true);
        final PropertiesParser PvpTitleColor = new PropertiesParser("./config/Custom/PvpTitleColor.ini");
        Config.PVP_COLOR_SYSTEM_ENABLED = PvpTitleColor.getBoolean("EnablePvPColorSystem", false);
        Config.PVP_AMOUNT1 = PvpTitleColor.getInt("PvpAmount1", 500);
        Config.PVP_AMOUNT2 = PvpTitleColor.getInt("PvpAmount2", 1000);
        Config.PVP_AMOUNT3 = PvpTitleColor.getInt("PvpAmount3", 1500);
        Config.PVP_AMOUNT4 = PvpTitleColor.getInt("PvpAmount4", 2500);
        Config.PVP_AMOUNT5 = PvpTitleColor.getInt("PvpAmount5", 5000);
        Config.NAME_COLOR_FOR_PVP_AMOUNT1 = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PvpTitleColor.getString("ColorForAmount1", "00FF00")));
        Config.NAME_COLOR_FOR_PVP_AMOUNT2 = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PvpTitleColor.getString("ColorForAmount2", "00FF00")));
        Config.NAME_COLOR_FOR_PVP_AMOUNT3 = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PvpTitleColor.getString("ColorForAmount3", "00FF00")));
        Config.NAME_COLOR_FOR_PVP_AMOUNT4 = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PvpTitleColor.getString("ColorForAmount4", "00FF00")));
        Config.NAME_COLOR_FOR_PVP_AMOUNT5 = Integer.decode(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, PvpTitleColor.getString("ColorForAmount5", "00FF00")));
        Config.TITLE_FOR_PVP_AMOUNT1 = PvpTitleColor.getString("PvPTitleForAmount1", "Title");
        Config.TITLE_FOR_PVP_AMOUNT2 = PvpTitleColor.getString("PvPTitleForAmount2", "Title");
        Config.TITLE_FOR_PVP_AMOUNT3 = PvpTitleColor.getString("PvPTitleForAmount3", "Title");
        Config.TITLE_FOR_PVP_AMOUNT4 = PvpTitleColor.getString("PvPTitleForAmount4", "Title");
        Config.TITLE_FOR_PVP_AMOUNT5 = PvpTitleColor.getString("PvPTitleForAmount5", "Title");
        final PropertiesParser RandomSpawns = new PropertiesParser("./config/Custom/RandomSpawns.ini");
        Config.ENABLE_RANDOM_MONSTER_SPAWNS = RandomSpawns.getBoolean("EnableRandomMonsterSpawns", false);
        Config.MOB_MAX_SPAWN_RANGE = RandomSpawns.getInt("MaxSpawnMobRange", 150);
        Config.MOB_MIN_SPAWN_RANGE = Config.MOB_MAX_SPAWN_RANGE * -1;
        if (Config.ENABLE_RANDOM_MONSTER_SPAWNS) {
            final String[] mobsIds = RandomSpawns.getString("MobsSpawnNotRandom", "18812,18813,18814,22138").split(",");
            Config.MOBS_LIST_NOT_RANDOM = new ArrayList<Integer>(mobsIds.length);
            for (final String id2 : mobsIds) {
                Config.MOBS_LIST_NOT_RANDOM.add(Integer.valueOf(id2));
            }
        }
        final PropertiesParser ScreenWelcomeMessage = new PropertiesParser("./config/Custom/ScreenWelcomeMessage.ini");
        Config.WELCOME_MESSAGE_ENABLED = ScreenWelcomeMessage.getBoolean("ScreenWelcomeMessageEnable", false);
        Config.WELCOME_MESSAGE_TEXT = ScreenWelcomeMessage.getString("ScreenWelcomeMessageText", "Welcome to our server!");
        Config.WELCOME_MESSAGE_TIME = ScreenWelcomeMessage.getInt("ScreenWelcomeMessageTime", 10) * 1000;
        final PropertiesParser SellBuffs = new PropertiesParser("./config/Custom/SellBuffs.ini");
        Config.SELLBUFF_ENABLED = SellBuffs.getBoolean("SellBuffEnable", false);
        Config.SELLBUFF_MP_MULTIPLER = SellBuffs.getInt("MpCostMultipler", 1);
        Config.SELLBUFF_PAYMENT_ID = SellBuffs.getInt("PaymentID", 57);
        Config.SELLBUFF_MIN_PRICE = SellBuffs.getLong("MinimalPrice", 100000L);
        Config.SELLBUFF_MAX_PRICE = SellBuffs.getLong("MaximalPrice", 100000000L);
        Config.SELLBUFF_MAX_BUFFS = SellBuffs.getInt("MaxBuffs", 15);
        final PropertiesParser ServerTime = new PropertiesParser("./config/Custom/ServerTime.ini");
        Config.DISPLAY_SERVER_TIME = ServerTime.getBoolean("DisplayServerTime", false);
        final PropertiesParser SchemeBuffer = new PropertiesParser("./config/Custom/ShemeBuffer.ini");
        Config.BUFFER_MAX_SCHEMES = SchemeBuffer.getInt("BufferMaxSchemesPerChar", 4);
        Config.BUFFER_STATIC_BUFF_COST = SchemeBuffer.getInt("BufferStaticCostPerBuff", -1);
        final PropertiesParser StartingLocation = new PropertiesParser("./config/Custom/StartingLocation.ini");
        Config.CUSTOM_STARTING_LOC = StartingLocation.getBoolean("CustomStartingLocation", false);
        Config.CUSTOM_STARTING_LOC_X = StartingLocation.getInt("CustomStartingLocX", 50821);
        Config.CUSTOM_STARTING_LOC_Y = StartingLocation.getInt("CustomStartingLocY", 186527);
        Config.CUSTOM_STARTING_LOC_Z = StartingLocation.getInt("CustomStartingLocZ", -3625);
        final PropertiesParser VoteReward = new PropertiesParser("./config/Custom/VoteReward.ini");
        Config.ALLOW_NETWORK_VOTE_REWARD = VoteReward.getBoolean("AllowNetworkVoteReward", false);
        Config.NETWORK_SERVER_LINK = VoteReward.getString("NetworkServerLink", "");
        Config.NETWORK_VOTES_DIFFERENCE = VoteReward.getInt("NetworkVotesDifference", 5);
        Config.NETWORK_REWARD_CHECK_TIME = VoteReward.getInt("NetworkRewardCheckTime", 5);
        final String NETWORK_SMALL_REWARD_VALUE = VoteReward.getString("NetworkReward", "57,100000000;");
        final String[] split;
        final String[] NETWORK_small_reward_splitted_1 = split = NETWORK_SMALL_REWARD_VALUE.split(";");
        for (final String i2 : split) {
            final String[] NETWORK_small_reward_splitted_2 = i2.split(",");
            Config.NETWORK_REWARD.put(Integer.parseInt(NETWORK_small_reward_splitted_2[0]), Integer.parseInt(NETWORK_small_reward_splitted_2[1]));
        }
        Config.NETWORK_DUALBOXES_ALLOWED = VoteReward.getInt("NetworkDualboxesAllowed", 1);
        Config.ALLOW_NETWORK_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowNetworkGameServerReport", false);
        Config.ALLOW_TOPZONE_VOTE_REWARD = VoteReward.getBoolean("AllowTopzoneVoteReward", false);
        Config.TOPZONE_SERVER_LINK = VoteReward.getString("TopzoneServerLink", "");
        Config.TOPZONE_VOTES_DIFFERENCE = VoteReward.getInt("TopzoneVotesDifference", 5);
        Config.TOPZONE_REWARD_CHECK_TIME = VoteReward.getInt("TopzoneRewardCheckTime", 5);
        final String TOPZONE_SMALL_REWARD_VALUE = VoteReward.getString("TopzoneReward", "57,100000000;");
        final String[] split2;
        final String[] topzone_small_reward_splitted_1 = split2 = TOPZONE_SMALL_REWARD_VALUE.split(";");
        for (final String i3 : split2) {
            final String[] topzone_small_reward_splitted_2 = i3.split(",");
            Config.TOPZONE_REWARD.put(Integer.parseInt(topzone_small_reward_splitted_2[0]), Integer.parseInt(topzone_small_reward_splitted_2[1]));
        }
        Config.TOPZONE_DUALBOXES_ALLOWED = VoteReward.getInt("TopzoneDualboxesAllowed", 1);
        Config.ALLOW_TOPZONE_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowTopzoneGameServerReport", false);
        Config.ALLOW_HOPZONE_VOTE_REWARD = VoteReward.getBoolean("AllowHopzoneVoteReward", false);
        Config.HOPZONE_SERVER_LINK = VoteReward.getString("HopzoneServerLink", "");
        Config.HOPZONE_VOTES_DIFFERENCE = VoteReward.getInt("HopzoneVotesDifference", 5);
        Config.HOPZONE_REWARD_CHECK_TIME = VoteReward.getInt("HopzoneRewardCheckTime", 5);
        final String HOPZONE_SMALL_REWARD_VALUE = VoteReward.getString("HopzoneReward", "57,100000000;");
        final String[] split3;
        final String[] hopzone_small_reward_splitted_1 = split3 = HOPZONE_SMALL_REWARD_VALUE.split(";");
        for (final String i4 : split3) {
            final String[] hopzone_small_reward_splitted_2 = i4.split(",");
            Config.HOPZONE_REWARD.put(Integer.parseInt(hopzone_small_reward_splitted_2[0]), Integer.parseInt(hopzone_small_reward_splitted_2[1]));
        }
        Config.HOPZONE_DUALBOXES_ALLOWED = VoteReward.getInt("HopzoneDualboxesAllowed", 1);
        Config.ALLOW_HOPZONE_GAME_SERVER_REPORT = VoteReward.getBoolean("AllowHopzoneGameServerReport", false);
        final PropertiesParser timeLimitedZoneSettings = new PropertiesParser("./config/time-limited-zones.properties");
        Config.TIME_LIMITED_ZONE_INITIAL_TIME = timeLimitedZoneSettings.getLong("InitialTime", 3600000L);
        Config.TIME_LIMITED_MAX_ADDED_TIME = timeLimitedZoneSettings.getLong("MaximumAddedTime", 18000000L);
        Config.TIME_LIMITED_ZONE_RESET_DELAY = timeLimitedZoneSettings.getLong("ResetDelay", 36000000L);
        Config.TIME_LIMITED_ZONE_TELEPORT_FEE = timeLimitedZoneSettings.getLong("TeleportFee", 10000L);
        final PropertiesParser althars = new PropertiesParser("config/althars.ini");
        Config.ALTHARS_ACTIVATE_CHANCE_RATE = althars.getInt("althars_activation_chance_rate", 70);
        Config.ALTHARS_MAX_ACTIVE = althars.getInt("althars_max_active", 3);
        Config.ALTHARS_MIN_DURATION_CYCLE = althars.getInt("althars_min_duration_cycle", 240000);
        Config.ALTHARS_MAX_DURATION_CYCLE = althars.getInt("althars_max_duration_cycle", 480000);
    }
    
    private static void loadFloodProtectorConfigs(final PropertiesParser properties) {
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_USE_ITEM, "UseItem", 4);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_ROLL_DICE, "RollDice", 42);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_FIREWORK, "Firework", 42);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_ITEM_PET_SUMMON, "ItemPetSummon", 16);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_HERO_VOICE, "HeroVoice", 100);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_GLOBAL_CHAT, "GlobalChat", 5);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_SUBCLASS, "Subclass", 20);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_DROP_ITEM, "DropItem", 10);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_SERVER_BYPASS, "ServerBypass", 5);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_MULTISELL, "MultiSell", 1);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_TRANSACTION, "Transaction", 10);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_MANUFACTURE, "Manufacture", 3);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_MANOR, "Manor", 30);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_SENDMAIL, "SendMail", 100);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_CHARACTER_SELECT, "CharacterSelect", 30);
        loadFloodProtectorConfig(properties, Config.FLOOD_PROTECTOR_ITEM_AUCTION, "ItemAuction", 9);
    }
    
    private static void loadFloodProtectorConfig(final PropertiesParser properties, final FloodProtectorConfig config, final String configString, final int defaultInterval) {
        config.FLOOD_PROTECTION_INTERVAL = properties.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configString), defaultInterval);
        config.LOG_FLOODING = properties.getBoolean(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configString), false);
        config.PUNISHMENT_LIMIT = properties.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configString), 0);
        config.PUNISHMENT_TYPE = properties.getString(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configString), "none");
        config.PUNISHMENT_TIME = properties.getInt(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, configString), 0) * 60000;
    }
    
    private static Map<Integer, Float> parseConfigLine(final String line) {
        final String[] propertySplit = line.split(",");
        final Map<Integer, Float> ret = new HashMap<Integer, Float>(propertySplit.length);
        int i = 0;
        for (final String value : propertySplit) {
            ret.put(i++, Float.parseFloat(value));
        }
        return ret;
    }
    
    private static List<ItemHolder> parseItemsList(final String line) {
        if (Util.isNullOrEmpty((CharSequence)line)) {
            return null;
        }
        final String[] propertySplit = line.split(";");
        if (propertySplit.length == 0) {
            return null;
        }
        final List<ItemHolder> result = new ArrayList<ItemHolder>(propertySplit.length);
        for (final String value : propertySplit) {
            final String[] valueSplit = value.split(",");
            Label_0185: {
                if (valueSplit.length != 2) {
                    Config.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, valueSplit[0]));
                }
                else {
                    int itemId;
                    try {
                        itemId = Integer.parseInt(valueSplit[0]);
                    }
                    catch (NumberFormatException e) {
                        Config.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, valueSplit[0]));
                        break Label_0185;
                    }
                    int count;
                    try {
                        count = Integer.parseInt(valueSplit[1]);
                    }
                    catch (NumberFormatException e2) {
                        Config.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, valueSplit[1]));
                        break Label_0185;
                    }
                    if (itemId > 0 && count > 0) {
                        result.add(new ItemHolder(itemId, count));
                    }
                }
            }
        }
        return result;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Config.class);
        Config.MULTILANG_ALLOWED = new ArrayList<String>();
        Config.NETWORK_REWARD = new HashMap<Integer, Integer>();
        Config.TOPZONE_REWARD = new HashMap<Integer, Integer>();
        Config.HOPZONE_REWARD = new HashMap<Integer, Integer>();
    }
    
    private static class IPConfigData extends GameXmlReader
    {
        private static final List<String> _subnets;
        private static final List<String> _hosts;
        
        public IPConfigData() {
            this.load();
        }
        
        protected Path getSchemaFilePath() {
            return Path.of("./config/xsd/ipconfig.xsd", new String[0]);
        }
        
        public void load() {
            final File f = new File("./config/ipconfig.xml");
            if (f.exists()) {
                Config.LOGGER.info("Network Config: ipconfig.xml exists using manual configuration...");
                this.parseFile(new File("./config/ipconfig.xml"));
            }
            else {
                Config.LOGGER.info("Network Config: ipconfig.xml doesn't exists using automatic configuration...");
                this.autoIpConfig();
            }
            this.releaseResources();
        }
        
        public void parseDocument(final Document doc, final File f) {
            for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
                if ("gameserver".equalsIgnoreCase(n.getNodeName())) {
                    for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                        if ("define".equalsIgnoreCase(d.getNodeName())) {
                            final NamedNodeMap attrs = d.getAttributes();
                            IPConfigData._subnets.add(attrs.getNamedItem("subnet").getNodeValue());
                            IPConfigData._hosts.add(attrs.getNamedItem("address").getNodeValue());
                            if (IPConfigData._hosts.size() != IPConfigData._subnets.size()) {
                                Config.LOGGER.warn("Failed to Load ./config/ipconfig.xml File - subnets does not match server addresses.");
                            }
                        }
                    }
                    final Node att = n.getAttributes().getNamedItem("address");
                    if (att == null) {
                        Config.LOGGER.warn("Failed to load ./config/ipconfig.xml file - default server address is missing.");
                        IPConfigData._hosts.add("127.0.0.1");
                    }
                    else {
                        IPConfigData._hosts.add(att.getNodeValue());
                    }
                    IPConfigData._subnets.add("0.0.0.0/0");
                }
            }
        }
        
        protected void autoIpConfig() {
            String externalIp;
            try {
                final URL autoIp = new URL("https://api.ipify.org/");
                final BufferedReader in = new BufferedReader(new InputStreamReader(autoIp.openStream()));
                try {
                    externalIp = in.readLine();
                    in.close();
                }
                catch (Throwable t) {
                    try {
                        in.close();
                    }
                    catch (Throwable exception) {
                        t.addSuppressed(exception);
                    }
                    throw t;
                }
            }
            catch (IOException e3) {
                Config.LOGGER.info("Failed to connect to api.ipify.org please check your internet connection! using 127.0.0.1!");
                externalIp = "127.0.0.1";
            }
            try {
                final Enumeration<NetworkInterface> niList = NetworkInterface.getNetworkInterfaces();
                while (niList.hasMoreElements()) {
                    final NetworkInterface ni = niList.nextElement();
                    if (ni.isUp()) {
                        if (ni.isVirtual()) {
                            continue;
                        }
                        if (!ni.isLoopback()) {
                            if (ni.getHardwareAddress() == null) {
                                continue;
                            }
                            if (ni.getHardwareAddress().length != 6) {
                                continue;
                            }
                        }
                        for (final InterfaceAddress ia : ni.getInterfaceAddresses()) {
                            if (ia.getAddress() instanceof Inet6Address) {
                                continue;
                            }
                            final String hostAddress = ia.getAddress().getHostAddress();
                            final int subnetPrefixLength = ia.getNetworkPrefixLength();
                            final int subnetMaskInt = IntStream.rangeClosed(1, subnetPrefixLength).reduce((r, e) -> (r << 1) + 1).orElse(0) << 32 - subnetPrefixLength;
                            final int hostAddressInt = Arrays.stream(hostAddress.split("\\.")).mapToInt(Integer::parseInt).reduce((r, e) -> (r << 8) + e).orElse(0);
                            final int subnetAddressInt = hostAddressInt & subnetMaskInt;
                            final String subnetAddress = invokedynamic(makeConcatWithConstants:(IIII)Ljava/lang/String;, subnetAddressInt >> 24 & 0xFF, subnetAddressInt >> 16 & 0xFF, subnetAddressInt >> 8 & 0xFF, subnetAddressInt & 0xFF);
                            final String subnet = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, subnetAddress, subnetPrefixLength);
                            if (IPConfigData._subnets.contains(subnet) || subnet.equals("0.0.0.0/0")) {
                                continue;
                            }
                            IPConfigData._subnets.add(subnet);
                            IPConfigData._hosts.add(hostAddress);
                            Config.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, subnet, hostAddress));
                        }
                    }
                }
                IPConfigData._hosts.add(externalIp);
                IPConfigData._subnets.add("0.0.0.0/0");
                Config.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, externalIp));
            }
            catch (SocketException e2) {
                Config.LOGGER.info("Network Config: Configuration failed please configure manually using ipconfig.xml", (Throwable)e2);
                System.exit(0);
            }
        }
        
        protected List<String> getSubnets() {
            if (IPConfigData._subnets.isEmpty()) {
                return Collections.singletonList("0.0.0.0/0");
            }
            return IPConfigData._subnets;
        }
        
        protected List<String> getHosts() {
            if (IPConfigData._hosts.isEmpty()) {
                return Collections.singletonList("127.0.0.1");
            }
            return IPConfigData._hosts;
        }
        
        static {
            _subnets = new ArrayList<String>(5);
            _hosts = new ArrayList<String>(5);
        }
    }
}
