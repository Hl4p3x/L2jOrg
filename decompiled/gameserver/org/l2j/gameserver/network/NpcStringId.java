// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import io.github.joealisson.primitive.HashIntMap;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class NpcStringId
{
    private static final Logger LOGGER;
    @ClientString(id = 4, message = "none")
    public static NpcStringId NONE_2;
    @ClientString(id = 6, message = "(<font color='FFAABB'>$s1</font> Adena/$s2 Day(s))")
    public static NpcStringId FONT_COLOR_FFAABB_S1_FONT_ADENA_S2_DAY_S;
    @ClientString(id = 9, message = "Stage $s1")
    public static NpcStringId STAGE_S1;
    @ClientString(id = 10, message = "$s1%%")
    public static NpcStringId S1;
    @ClientString(id = 18568, message = "")
    public static NpcStringId EMPTY;
    @ClientString(id = 22937, message = "How dare you wake me! Now you shall die!")
    public static NpcStringId HOW_DARE_YOU_WAKE_ME_NOW_YOU_SHALL_DIE;
    @ClientString(id = 42231, message = "Hey, it seems like you need my help, doesn't it?")
    public static NpcStringId HEY_IT_SEEMS_LIKE_YOU_NEED_MY_HELP_DOESN_T_IT;
    @ClientString(id = 42232, message = "Almost got it... Ouch! Stop! Damn these bloody manacles!")
    public static NpcStringId ALMOST_GOT_IT_OUCH_STOP_DAMN_THESE_BLOODY_MANACLES;
    @ClientString(id = 42233, message = "Oh, that smarts!")
    public static NpcStringId OH_THAT_SMARTS;
    @ClientString(id = 42234, message = "Hey, master! Pay attention! I'm dying over here!")
    public static NpcStringId HEY_MASTER_PAY_ATTENTION_I_M_DYING_OVER_HERE;
    @ClientString(id = 42235, message = "What have I done to deserve this?")
    public static NpcStringId WHAT_HAVE_I_DONE_TO_DESERVE_THIS;
    @ClientString(id = 42236, message = "Oh, this is just great! What are you going to do now?")
    public static NpcStringId OH_THIS_IS_JUST_GREAT_WHAT_ARE_YOU_GOING_TO_DO_NOW;
    @ClientString(id = 42237, message = "You inconsiderate moron! Can't you even take care of little old me?!")
    public static NpcStringId YOU_INCONSIDERATE_MORON_CAN_T_YOU_EVEN_TAKE_CARE_OF_LITTLE_OLD_ME;
    @ClientString(id = 42238, message = "Oh no! The man who eats one's sins has died! Penitence is further away~!")
    public static NpcStringId OH_NO_THE_MAN_WHO_EATS_ONE_S_SINS_HAS_DIED_PENITENCE_IS_FURTHER_AWAY;
    @ClientString(id = 42239, message = "Using a special skill here could trigger a bloodbath!")
    public static NpcStringId USING_A_SPECIAL_SKILL_HERE_COULD_TRIGGER_A_BLOODBATH;
    @ClientString(id = 42240, message = "Hey, what do you expect of me?")
    public static NpcStringId HEY_WHAT_DO_YOU_EXPECT_OF_ME;
    @ClientString(id = 42241, message = "Ugggggh! Push! It's not coming out!")
    public static NpcStringId UGGGGGH_PUSH_IT_S_NOT_COMING_OUT;
    @ClientString(id = 42242, message = "Ah, I missed the mark!")
    public static NpcStringId AH_I_MISSED_THE_MARK;
    @ClientString(id = 42243, message = "Yawwwwn! It's so boring here. We should go and find some action!")
    public static NpcStringId YAWWWWN_IT_S_SO_BORING_HERE_WE_SHOULD_GO_AND_FIND_SOME_ACTION;
    @ClientString(id = 42244, message = "Hey, if you continue to waste time you will never finish your penance!")
    public static NpcStringId HEY_IF_YOU_CONTINUE_TO_WASTE_TIME_YOU_WILL_NEVER_FINISH_YOUR_PENANCE;
    @ClientString(id = 42245, message = "I know you don't like me. The feeling is mutual!")
    public static NpcStringId I_KNOW_YOU_DON_T_LIKE_ME_THE_FEELING_IS_MUTUAL;
    @ClientString(id = 42246, message = "I need a drink.")
    public static NpcStringId I_NEED_A_DRINK;
    @ClientString(id = 42247, message = "Oh, this is dragging on too long... At this rate I won't make it home before the seven seals are broken.")
    public static NpcStringId OH_THIS_IS_DRAGGING_ON_TOO_LONG_AT_THIS_RATE_I_WON_T_MAKE_IT_HOME_BEFORE_THE_SEVEN_SEALS_ARE_BROKEN;
    @ClientString(id = 99702, message = "What are you looking at?")
    public static NpcStringId WHAT_ARE_YOU_LOOKING_AT;
    @ClientString(id = 586601, message = "|Lv. 70+| Punitive Operation on the Devil's Isle")
    public static NpcStringId LV_70_PUNITIVE_OPERATION_ON_THE_DEVIL_S_ISLE;
    @ClientString(id = 1000001, message = "A non-permitted target has been discovered.")
    public static NpcStringId A_NON_PERMITTED_TARGET_HAS_BEEN_DISCOVERED;
    @ClientString(id = 1000002, message = "Intruder removal system initiated.")
    public static NpcStringId INTRUDER_REMOVAL_SYSTEM_INITIATED;
    @ClientString(id = 1000003, message = "Removing intruders.")
    public static NpcStringId REMOVING_INTRUDERS;
    @ClientString(id = 1000004, message = "A fatal error has occurred.")
    public static NpcStringId A_FATAL_ERROR_HAS_OCCURRED;
    @ClientString(id = 1000005, message = "System is being shut down...")
    public static NpcStringId SYSTEM_IS_BEING_SHUT_DOWN;
    @ClientString(id = 1000028, message = "$s1. Stop kidding yourself about your own powerlessness!")
    public static NpcStringId S1_STOP_KIDDING_YOURSELF_ABOUT_YOUR_OWN_POWERLESSNESS;
    @ClientString(id = 1000029, message = "$s1. I'll make you feel what true fear is!")
    public static NpcStringId S1_I_LL_MAKE_YOU_FEEL_WHAT_TRUE_FEAR_IS;
    @ClientString(id = 1000030, message = "You're really stupid to have challenged me. $s1! Get ready!")
    public static NpcStringId YOU_RE_REALLY_STUPID_TO_HAVE_CHALLENGED_ME_S1_GET_READY;
    @ClientString(id = 1000031, message = "$s1. Do you think that's going to work?!")
    public static NpcStringId S1_DO_YOU_THINK_THAT_S_GOING_TO_WORK;
    @ClientString(id = 1000288, message = "$s1! How dare you interrupt our fight! Hey guys, help!")
    public static NpcStringId S1_HOW_DARE_YOU_INTERRUPT_OUR_FIGHT_HEY_GUYS_HELP;
    @ClientString(id = 1000294, message = "Come out, you children of darkness!")
    public static NpcStringId COME_OUT_YOU_CHILDREN_OF_DARKNESS;
    @ClientString(id = 1000388, message = "$s1! Hey! We're having a duel here!")
    public static NpcStringId S1_HEY_WE_RE_HAVING_A_DUEL_HERE;
    @ClientString(id = 1000389, message = "The duel is over! Attack!")
    public static NpcStringId THE_DUEL_IS_OVER_ATTACK;
    @ClientString(id = 1000390, message = "Foul! Kill the coward!")
    public static NpcStringId FOUL_KILL_THE_COWARD;
    @ClientString(id = 1000391, message = "How dare you interrupt a sacred duel! You must be taught a lesson!")
    public static NpcStringId HOW_DARE_YOU_INTERRUPT_A_SACRED_DUEL_YOU_MUST_BE_TAUGHT_A_LESSON;
    @ClientString(id = 1000392, message = "Die, you coward!")
    public static NpcStringId DIE_YOU_COWARD;
    @ClientString(id = 1000394, message = "Kill the coward!")
    public static NpcStringId KILL_THE_COWARD;
    @ClientString(id = 1000403, message = "Show yourselves!")
    public static NpcStringId SHOW_YOURSELVES;
    @ClientString(id = 1000404, message = "Forces of darkness! Follow me!")
    public static NpcStringId FORCES_OF_DARKNESS_FOLLOW_ME;
    @ClientString(id = 1000405, message = "Destroy the enemy, my brothers!")
    public static NpcStringId DESTROY_THE_ENEMY_MY_BROTHERS;
    @ClientString(id = 1000406, message = "Now the fun starts!")
    public static NpcStringId NOW_THE_FUN_STARTS;
    @ClientString(id = 1000407, message = "Enough fooling around. Get ready to die!")
    public static NpcStringId ENOUGH_FOOLING_AROUND_GET_READY_TO_DIE;
    @ClientString(id = 1000408, message = "You idiot! I've just been toying with you!")
    public static NpcStringId YOU_IDIOT_I_VE_JUST_BEEN_TOYING_WITH_YOU;
    @ClientString(id = 1000409, message = "Witness my true power!")
    public static NpcStringId WITNESS_MY_TRUE_POWER;
    @ClientString(id = 1000410, message = "Now the battle begins!")
    public static NpcStringId NOW_THE_BATTLE_BEGINS;
    @ClientString(id = 1000411, message = "I must admit, no one makes my blood boil quite like you do!")
    public static NpcStringId I_MUST_ADMIT_NO_ONE_MAKES_MY_BLOOD_BOIL_QUITE_LIKE_YOU_DO;
    @ClientString(id = 1000412, message = "It's stronger than expected!")
    public static NpcStringId IT_S_STRONGER_THAN_EXPECTED_2;
    @ClientString(id = 1000413, message = "I'll double my strength!")
    public static NpcStringId I_LL_DOUBLE_MY_STRENGTH;
    @ClientString(id = 1000414, message = "Prepare to die!")
    public static NpcStringId PREPARE_TO_DIE;
    @ClientString(id = 1000443, message = "The defenders of $s1 castle will be teleported to the inner castle.")
    public static NpcStringId THE_DEFENDERS_OF_S1_CASTLE_WILL_BE_TELEPORTED_TO_THE_INNER_CASTLE;
    @ClientString(id = 1000520, message = "$s1!!!! You cannot hope to defeat me with your meager strength.")
    public static NpcStringId S1_YOU_CANNOT_HOPE_TO_DEFEAT_ME_WITH_YOUR_MEAGER_STRENGTH;
    @ClientString(id = 1000521, message = "Not even the gods themselves could touch me. But you, $s1, you dare challenge me?! Ignorant mortal!")
    public static NpcStringId NOT_EVEN_THE_GODS_THEMSELVES_COULD_TOUCH_ME_BUT_YOU_S1_YOU_DARE_CHALLENGE_ME_IGNORANT_MORTAL;
    @ClientString(id = 1010229, message = "You have destroyed the Limit Barrier!")
    public static NpcStringId YOU_HAVE_DESTROYED_THE_LIMIT_BARRIER;
    @ClientString(id = 1010230, message = "You have failed to destroy the Limit Barrier.\nThe raid boss fully recovers its health.")
    public static NpcStringId YOU_HAVE_FAILED_TO_DESTROY_THE_LIMIT_BARRIER_NTHE_RAID_BOSS_FULLY_RECOVERS_ITS_HEALTH;
    @ClientString(id = 1010231, message = "The Raid Boss uses the Limit Barrier.\nFocus your attacks to destroy the Limit Barrier in 15 sec.")
    public static NpcStringId THE_RAID_BOSS_USES_THE_LIMIT_BARRIER_NFOCUS_YOUR_ATTACKS_TO_DESTROY_THE_LIMIT_BARRIER_IN_15_SEC;
    @ClientString(id = 1300004, message = "You may have broken our arrows, but you will never break our will! Archers, retreat!")
    public static NpcStringId YOU_MAY_HAVE_BROKEN_OUR_ARROWS_BUT_YOU_WILL_NEVER_BREAK_OUR_WILL_ARCHERS_RETREAT;
    @ClientString(id = 1300005, message = "At last! The Magic Circle that protects the fortress has weakened! Volunteers, stand back!")
    public static NpcStringId AT_LAST_THE_MAGIC_CIRCLE_THAT_PROTECTS_THE_FORTRESS_HAS_WEAKENED_VOLUNTEERS_STAND_BACK;
    @ClientString(id = 1300006, message = "Aiieeee! Command Center! This is guard unit! We need backup right away!")
    public static NpcStringId AIIEEEE_COMMAND_CENTER_THIS_IS_GUARD_UNIT_WE_NEED_BACKUP_RIGHT_AWAY;
    @ClientString(id = 1300012, message = "Everyone, concentrate your attacks on $s1! Show the enemy your resolve!")
    public static NpcStringId EVERYONE_CONCENTRATE_YOUR_ATTACKS_ON_S1_SHOW_THE_ENEMY_YOUR_RESOLVE;
    @ClientString(id = 1300013, message = "Attacking the enemy's reinforcements is necessary. Time to die!")
    public static NpcStringId ATTACKING_THE_ENEMY_S_REINFORCEMENTS_IS_NECESSARY_TIME_TO_DIE;
    @ClientString(id = 1300014, message = "Fire Spirit, unleash your power! Burn the enemy!!")
    public static NpcStringId FIRE_SPIRIT_UNLEASH_YOUR_POWER_BURN_THE_ENEMY;
    @ClientString(id = 1300020, message = "I feel so much grief that I can't even take care of myself. There isn't any reason for me to stay here any longer.")
    public static NpcStringId I_FEEL_SO_MUCH_GRIEF_THAT_I_CAN_T_EVEN_TAKE_CARE_OF_MYSELF_THERE_ISN_T_ANY_REASON_FOR_ME_TO_STAY_HERE_ANY_LONGER;
    @ClientString(id = 1300166, message = "Olympiad class-free individual match is going to begin in Arena $s1 in a moment.")
    public static NpcStringId OLYMPIAD_CLASS_FREE_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
    @ClientString(id = 1300167, message = "Olympiad class individual match is going to begin in Arena $s1 in a moment.")
    public static NpcStringId OLYMPIAD_CLASS_INDIVIDUAL_MATCH_IS_GOING_TO_BEGIN_IN_ARENA_S1_IN_A_MOMENT;
    @ClientString(id = 1300172, message = "When the world plunges into chaos, we will need your help. We hope you join us when the time comes.")
    public static NpcStringId WHEN_THE_WORLD_PLUNGES_INTO_CHAOS_WE_WILL_NEED_YOUR_HELP_WE_HOPE_YOU_JOIN_US_WHEN_THE_TIME_COMES;
    @ClientString(id = 1600019, message = "If your means of arrival was a bit unconventional, then I'll be sending you back to the Town of Rune, which is the nearest town.")
    public static NpcStringId IF_YOUR_MEANS_OF_ARRIVAL_WAS_A_BIT_UNCONVENTIONAL_THEN_I_LL_BE_SENDING_YOU_BACK_TO_THE_TOWN_OF_RUNE_WHICH_IS_THE_NEAREST_TOWN;
    @ClientString(id = 1802764, message = "Speak with the Newbie Helper.")
    public static NpcStringId SPEAK_WITH_THE_NEWBIE_HELPER;
    @ClientString(id = 1803665, message = "Ha! Not bad.")
    public static NpcStringId HA_NOT_BAD;
    @ClientString(id = 1803684, message = "Balthus Knights are looking for mercenaries!")
    public static NpcStringId BALTHUS_KNIGHTS_ARE_LOOKING_FOR_MERCENARIES;
    @ClientString(id = 1803685, message = "Let's join our forces and face this together!")
    public static NpcStringId LET_S_JOIN_OUR_FORCES_AND_FACE_THIS_TOGETHER;
    @ClientString(id = 1803666, message = "Welcome to the Arena! Test your clan's strength!")
    public static NpcStringId WELCOME_TO_THE_ARENA_TEST_YOUR_CLAN_S_STRENGTH;
    @ClientString(id = 1811302, message = "Remaining Time")
    public static NpcStringId REMAINING_TIME;
    @ClientString(id = 1811308, message = "Speak with me about traveling around Aden.")
    public static NpcStringId SPEAK_WITH_ME_ABOUT_TRAVELING_AROUND_ADEN;
    @ClientString(id = 596110, message = "Defeat the monsters on the Hill of Hope")
    public static NpcStringId DEFEAT_THE_MONSTERS_ON_THE_HILL_OF_HOPE;
    @ClientString(id = 596101, message = "|Lv. 2~20| Effective Training")
    public static NpcStringId LV_2_20_EFFECTIVE_TRAINING;
    @ClientString(id = 596201, message = "|Lv. 15~20| New Horizons")
    public static NpcStringId LV_15_20_NEW_HORIZONS;
    @ClientString(id = 1803751, message = "You've finished the tutorial.\nTake your 1st class transfer and complete your training with Bathis to become stronger.")
    public static NpcStringId YOU_VE_FINISHED_THE_TUTORIAL_NTAKE_YOUR_1ST_CLASS_TRANSFER_AND_COMPLETE_YOUR_TRAINING_WITH_BATHIS_TO_BECOME_STRONGER;
    @ClientString(id = 596210, message = "Defeat the monsters in the Golden Hills")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_GOLDEN_HILLS;
    @ClientString(id = 596301, message = "|Lv. 34~37| Exploring the Ant Nest")
    public static NpcStringId LV_34_37_EXPLORING_THE_ANT_NEST;
    @ClientString(id = 596311, message = "Monsters of the Ant Nest are killed.\nUse the teleport to get to High Priest Raymond in Gludio.")
    public static NpcStringId MONSTERS_OF_THE_ANT_NEST_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO;
    @ClientString(id = 596305, message = "Defeat the monsters in the Ant Nest.")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_ANT_NEST;
    @ClientString(id = 596401, message = "|Lv. 30~34| Secret Garden")
    public static NpcStringId LV_30_34_SECRET_GARDEN;
    @ClientString(id = 1803752, message = "The mission 'Adventurer's Journey II' is now available.\nClick the yellow question mark in the right bottom corner of your screen to see the quest's info.")
    public static NpcStringId THE_MISSION_ADVENTURER_S_JOURNEY_II_IS_NOW_AVAILABLE_NCLICK_THE_YELLOW_QUESTION_MARK_IN_THE_RIGHT_BOTTOM_CORNER_OF_YOUR_SCREEN_TO_SEE_THE_QUEST_S_INFO;
    @ClientString(id = 596411, message = "Monsters of the Gorgon Flower Garden are killed.\nUse the teleport to get to High Priest Raymond in Gludio.")
    public static NpcStringId MONSTERS_OF_THE_GORGON_FLOWER_GARDEN_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_HIGH_PRIEST_RAYMOND_IN_GLUDIO;
    @ClientString(id = 596405, message = "Defeat the monsters in the Gorgon Flower Garden")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_GORGON_FLOWER_GARDEN;
    @ClientString(id = 596501, message = "|Lv. 37~40| Death Mysteries")
    public static NpcStringId LV_37_40_DEATH_MYSTERIES;
    @ClientString(id = 1803787, message = "You've got Adventurer's Agathion Bracelet and Adventurer's Agathion - Griffin.\nComplete the tutorial and try to use the agathion.")
    public static NpcStringId YOU_VE_GOT_ADVENTURER_S_AGATHION_BRACELET_AND_ADVENTURER_S_AGATHION_GRIFFIN_NCOMPLETE_THE_TUTORIAL_AND_TRY_TO_USE_THE_AGATHION;
    @ClientString(id = 596511, message = "Monsters of the Death Pass are killed.\nUse the teleport or the Scroll of Escape to get to High Priest Maximilian in Giran.")
    public static NpcStringId MONSTERS_OF_THE_DEATH_PASS_ARE_KILLED_NUSE_THE_TELEPORT_OR_THE_SCROLL_OF_ESCAPE_TO_GET_TO_HIGH_PRIEST_MAXIMILIAN_IN_GIRAN;
    @ClientString(id = 596506, message = "Defeat the monsters in the Death Pass")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_DEATH_PASS;
    @ClientString(id = 596601, message = "|Lv. 20~25| A Trip Begins")
    public static NpcStringId LV_20_25_A_TRIP_BEGINS;
    @ClientString(id = 596605, message = "Talk to Bella")
    public static NpcStringId TALK_TO_BELLA;
    @ClientString(id = 596606, message = "Defeat the monsters in the Ruins of Agony.")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_RUINS_OF_AGONY;
    @ClientString(id = 596701, message = "|Lv. 25~30| More Experience")
    public static NpcStringId LV_25_30_MORE_EXPERIENCE;
    @ClientString(id = 596711, message = "Monsters of the Abandoned Camp are killed.\nUse the teleport to get to Bathis in Gludio.")
    public static NpcStringId MONSTERS_OF_THE_ABANDONED_CAMP_ARE_KILLED_NUSE_THE_TELEPORT_TO_GET_TO_BATHIS_IN_GLUDIO;
    @ClientString(id = 596705, message = "Defeat the monsters in the Abandoned Camp.")
    public static NpcStringId DEFEAT_THE_MONSTERS_IN_THE_ABANDONED_CAMP;
    @ClientString(id = 103111, message = "Kill Wolves and Bearded Keltirs.")
    public static NpcStringId KILL_WOLVES_AND_BEARDED_KELTIRS;
    @ClientString(id = 103101, message = "|Lv. 2~20| Unbearable Wolves' Howling")
    public static NpcStringId LV_2_20_UNBEARABLE_WOLVES_HOWLING;
    @ClientString(id = 103211, message = "Kill Giant Spiders.")
    public static NpcStringId KILL_GIANT_SPIDERS;
    @ClientString(id = 103201, message = "|Lv. 15~20| Spider Hunt")
    public static NpcStringId LV_15_20_SPIDER_HUNT;
    @ClientString(id = 103311, message = "Kill Orcs and Goblins.")
    public static NpcStringId KILL_ORCS_AND_GOBLINS;
    @ClientString(id = 103301, message = "|Lv. 2~20| Troubled Forest")
    public static NpcStringId LV_2_20_TROUBLED_FOREST;
    @ClientString(id = 103402, message = "|Lv. 15~20| Spider Web (In progress)")
    public static NpcStringId LV_15_20_SPIDER_WEB_IN_PROGRESS;
    @ClientString(id = 103401, message = "|Lv. 15~20| Spider Web")
    public static NpcStringId LV_15_20_SPIDER_WEB;
    @ClientString(id = 103511, message = "Kill monsters near the village.")
    public static NpcStringId KILL_MONSTERS_NEAR_THE_VILLAGE;
    @ClientString(id = 103501, message = "|Lv. 2~20| Cleaning-up the Grounds")
    public static NpcStringId LV_2_20_CLEANING_UP_THE_GROUNDS;
    @ClientString(id = 103611, message = "Kill Zombies and Dark Horrors.")
    public static NpcStringId KILL_ZOMBIES_AND_DARK_HORRORS;
    @ClientString(id = 103601, message = "|Lv. 15~20| Terrible swamp monsters")
    public static NpcStringId LV_15_20_TERRIBLE_SWAMP_MONSTERS;
    @ClientString(id = 103711, message = "Track down grave robbers.")
    public static NpcStringId TRACK_DOWN_GRAVE_ROBBERS;
    @ClientString(id = 103701, message = "|Lv. 2~20| Plundered Graves")
    public static NpcStringId LV_2_20_PLUNDERED_GRAVES;
    @ClientString(id = 103801, message = "|Lv. 15~20| Conspiracy")
    public static NpcStringId LV_15_20_CONSPIRACY;
    @ClientString(id = 103811, message = "Expose a plot of Maraku Werewolves.")
    public static NpcStringId EXPOSE_A_PLOT_OF_MARAKU_WEREWOLVES;
    @ClientString(id = 103911, message = "Kill Wolves and Bearded Keltirs.")
    public static NpcStringId KILL_WOLVES_AND_BEARDED_KELTIRS_2;
    @ClientString(id = 103901, message = "|Lv. 2~20| Dangerous Predators")
    public static NpcStringId LV_2_20_DANGEROUS_PREDATORS;
    @ClientString(id = 104002, message = "|Lv. 15~20| Poison Extraction (In progress)")
    public static NpcStringId LV_15_20_POISON_EXTRACTION_IN_PROGRESS;
    @ClientString(id = 104001, message = "|Lv. 15~20| Poison Extraction")
    public static NpcStringId LV_15_20_POISON_EXTRACTION;
    @ClientString(id = 1000526, message = "Mournful Chorale Prelude")
    public static NpcStringId MOURNFUL_CHORALE_PRELUDE;
    @ClientString(id = 1000522, message = "Requiem of Hatred")
    public static NpcStringId REQUIEM_OF_HATRED;
    @ClientString(id = 1000523, message = "Fugue of Jubilation")
    public static NpcStringId FUGUE_OF_JUBILATION;
    @ClientString(id = 1000524, message = "Frenetic Toccata")
    public static NpcStringId FRENETIC_TOCCATA;
    @ClientString(id = 1000525, message = "Hypnotic Mazurka")
    public static NpcStringId HYPNOTIC_MAZURKA;
    @ClientString(id = 1000527, message = "Rondo of Solitude")
    public static NpcStringId RONDO_OF_SOLITUDE;
    @ClientString(id = 104505, message = "Orven's Request")
    public static NpcStringId ORVEN_S_REQUEST;
    @ClientString(id = 1010480, message = "The P. Atk. reduction device has now been destroyed.")
    public static NpcStringId THE_P_ATK_REDUCTION_DEVICE_HAS_NOW_BEEN_DESTROYED;
    @ClientString(id = 1010479, message = "The poison device has now been destroyed.")
    public static NpcStringId THE_POISON_DEVICE_HAS_NOW_BEEN_DESTROYED;
    @ClientString(id = 8058, message = "Help me!")
    public static NpcStringId HELP_ME;
    @ClientString(id = 8059, message = "Don't miss!")
    public static NpcStringId DON_T_MISS;
    @ClientString(id = 8060, message = "Keep pushing!")
    public static NpcStringId KEEP_PUSHING;
    @ClientString(id = 1000180, message = "Who dared to enter here?")
    public static NpcStringId WHO_DARED_TO_ENTER_HERE;
    @ClientString(id = 1000189, message = "How dare you invade our land! I won\u2019t leave it that easy!")
    public static NpcStringId HOW_DARE_YOU_INVADE_OUR_LAND_I_WONT_LEAVE_IT_THAT_EASY;
    @ClientString(id = 1000502, message = "The monsters have spawned!")
    public static NpcStringId THE_MONSTERS_HAVE_SPAWNED;
    @ClientString(id = 17178347, message = "Can't die in a place like this!!")
    public static NpcStringId CAN_T_DIE_IN_A_PLACE_LIKE_THIS;
    @ClientString(id = 1802034, message = "You have failed.")
    public static NpcStringId YOU_HAVE_FAILED;
    @ClientString(id = 1803286, message = "Divine Angels are nowhere to be seen! I want to talk to the party leader!")
    public static NpcStringId DIVINE_ANGELS_ARE_NOWHERE_TO_BE_SEEN_I_WANT_TO_TALK_TO_THE_PARTY_LEADER;
    @ClientString(id = 1803287, message = "Destroy weakened Divine Angels")
    public static NpcStringId DESTROY_WEAKENED_DIVINE_ANGELS;
    @ClientString(id = 1803288, message = "Set off bombs and get treasures")
    public static NpcStringId SET_OFF_BOMBS_AND_GET_TREASURES;
    @ClientString(id = 1803289, message = "Protect the Central Tower from Divine Angels")
    public static NpcStringId PROTECT_THE_CENTRAL_TOWER_FROM_DIVINE_ANGELS;
    @ClientString(id = 1803290, message = "My servants can keep me safe! I have nothing to fear!")
    public static NpcStringId MY_SERVANTS_CAN_KEEP_ME_SAFE_I_HAVE_NOTHING_TO_FEAR;
    @ClientString(id = 1803770, message = "Glory to the heroes who have defeated Lord Ishka!")
    public static NpcStringId GLORY_TO_THE_HEROES_WHO_HAVE_DEFEATED_LORD_ISHKA;
    private static IntMap<NpcStringId> VALUES;
    private final int _id;
    private String _name;
    private byte _params;
    private ExShowScreenMessage _staticScreenMessage;
    
    protected NpcStringId(final int id) {
        this._id = id;
    }
    
    private static void buildFastLookupTable() {
        final Field[] declaredFields;
        final Field[] fields = declaredFields = NpcStringId.class.getDeclaredFields();
        for (final Field field : declaredFields) {
            final int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.getType().equals(NpcStringId.class) && field.isAnnotationPresent(ClientString.class)) {
                try {
                    final ClientString annotation = field.getAnnotationsByType(ClientString.class)[0];
                    final NpcStringId nsId = new NpcStringId(annotation.id());
                    nsId.setName(annotation.message());
                    nsId.setParamCount(parseMessageParameters(field.getName()));
                    field.set(null, nsId);
                    NpcStringId.VALUES.put(nsId.getId(), (Object)nsId);
                }
                catch (Exception e) {
                    NpcStringId.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, field.getName()), (Throwable)e);
                }
            }
        }
    }
    
    private static int parseMessageParameters(final String name) {
        int paramCount = 0;
        for (int i = 0; i < name.length() - 1; ++i) {
            final char c1 = name.charAt(i);
            if (c1 == 'C' || c1 == 'S') {
                final char c2 = name.charAt(i + 1);
                if (Character.isDigit(c2)) {
                    paramCount = Math.max(paramCount, Character.getNumericValue(c2));
                    ++i;
                }
            }
        }
        return paramCount;
    }
    
    public static NpcStringId getNpcStringId(final int id) {
        return getNpcStringIdOrDefault(id, new NpcStringId(id));
    }
    
    public static NpcStringId getNpcStringIdOrDefault(final int id, final NpcStringId defaultValue) {
        final NpcStringId nsi = getNpcStringIdInternal(id);
        return (nsi == null) ? defaultValue : nsi;
    }
    
    private static NpcStringId getNpcStringIdInternal(final int id) {
        return (NpcStringId)NpcStringId.VALUES.get(id);
    }
    
    public static NpcStringId getNpcStringId(final String name) {
        try {
            return (NpcStringId)NpcStringId.class.getField(name).get(null);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public final int getId() {
        return this._id;
    }
    
    public final String getName() {
        return this._name;
    }
    
    private void setName(final String name) {
        this._name = name;
    }
    
    public final int getParamCount() {
        return this._params;
    }
    
    public final void setParamCount(final int params) {
        if (params < 0) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, params));
        }
        if (params > 10) {
            throw new IllegalArgumentException(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, params));
        }
        if (params != 0) {
            this._staticScreenMessage = null;
        }
        this._params = (byte)params;
    }
    
    public final ExShowScreenMessage getStaticScreenMessage() {
        return this._staticScreenMessage;
    }
    
    public final void setStaticSystemMessage(final ExShowScreenMessage ns) {
        this._staticScreenMessage = ns;
    }
    
    @Override
    public final String toString() {
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.getId(), this.getName());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)NpcStringId.class);
        NpcStringId.VALUES = (IntMap<NpcStringId>)new HashIntMap();
        buildFastLookupTable();
    }
}
