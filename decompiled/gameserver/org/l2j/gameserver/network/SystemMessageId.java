// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network;

import io.github.joealisson.primitive.HashIntMap;
import org.slf4j.LoggerFactory;
import java.lang.reflect.Field;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class SystemMessageId
{
    private static final Logger LOGGER;
    @ClientString(id = 1, message = "The server will be coming down in $s1 second(s).  Please find a safe place to log out.")
    public static SystemMessageId THE_SERVER_WILL_BE_COMING_DOWN_IN_S1_SECOND_S_PLEASE_FIND_A_SAFE_PLACE_TO_LOG_OUT;
    @ClientString(id = 2, message = "$s1 does not exist.")
    public static SystemMessageId S1_DOES_NOT_EXIST;
    @ClientString(id = 4, message = "You cannot ask yourself to apply to a clan.")
    public static SystemMessageId YOU_CANNOT_ASK_YOURSELF_TO_APPLY_TO_A_CLAN;
    @ClientString(id = 5, message = "$s1 already exists.")
    public static SystemMessageId S1_ALREADY_EXISTS;
    @ClientString(id = 9, message = "$s1 is not a clan leader.")
    public static SystemMessageId S1_IS_NOT_A_CLAN_LEADER;
    @ClientString(id = 10, message = "$s1 is already a member of another clan.")
    public static SystemMessageId S1_IS_ALREADY_A_MEMBER_OF_ANOTHER_CLAN;
    @ClientString(id = 20, message = "The gate is being opened.")
    public static SystemMessageId THE_GATE_IS_BEING_OPENED;
    @ClientString(id = 22, message = "Your target is out of range.")
    public static SystemMessageId YOUR_TARGET_IS_OUT_OF_RANGE;
    @ClientString(id = 23, message = "Not enough HP.")
    public static SystemMessageId NOT_ENOUGH_HP;
    @ClientString(id = 24, message = "Not enough MP.")
    public static SystemMessageId NOT_ENOUGH_MP;
    @ClientString(id = 25, message = "Rejuvenating HP.")
    public static SystemMessageId REJUVENATING_HP;
    @ClientString(id = 27, message = "Your casting has been interrupted.")
    public static SystemMessageId YOUR_CASTING_HAS_BEEN_INTERRUPTED;
    @ClientString(id = 29, message = "You have obtained $s2 $s1.")
    public static SystemMessageId YOU_HAVE_OBTAINED_S2_S1;
    @ClientString(id = 30, message = "You have obtained $s1.")
    public static SystemMessageId YOU_HAVE_OBTAINED_S1;
    @ClientString(id = 31, message = "You cannot use actions and skills while the character is sitting.")
    public static SystemMessageId YOU_CANNOT_USE_ACTIONS_AND_SKILLS_WHILE_THE_CHARACTER_IS_SITTING;
    @ClientString(id = 33, message = "You cannot move while casting.")
    public static SystemMessageId YOU_CANNOT_MOVE_WHILE_CASTING;
    @ClientString(id = 34, message = "Welcome to the World.")
    public static SystemMessageId WELCOME_TO_THE_WORLD;
    @ClientString(id = 35, message = "You hit for $s1 damage.")
    public static SystemMessageId YOU_HIT_FOR_S1_DAMAGE;
    @ClientString(id = 45, message = "You have earned $s1 XP.")
    public static SystemMessageId YOU_HAVE_EARNED_S1_XP;
    @ClientString(id = 46, message = "You use $s1.")
    public static SystemMessageId YOU_USE_S1;
    @ClientString(id = 48, message = "$s1 is not available at this time: being prepared for reuse.")
    public static SystemMessageId S1_IS_NOT_AVAILABLE_AT_THIS_TIME_BEING_PREPARED_FOR_REUSE;
    @ClientString(id = 49, message = "You have equipped your $s1.")
    public static SystemMessageId YOU_HAVE_EQUIPPED_YOUR_S1;
    @ClientString(id = 50, message = "Your target cannot be found.")
    public static SystemMessageId YOUR_TARGET_CANNOT_BE_FOUND;
    @ClientString(id = 51, message = "You cannot use this on yourself.")
    public static SystemMessageId YOU_CANNOT_USE_THIS_ON_YOURSELF;
    @ClientString(id = 52, message = "You have earned $s1 Adena.")
    public static SystemMessageId YOU_HAVE_EARNED_S1_ADENA;
    @ClientString(id = 53, message = "You have earned $s2 $s1(s).")
    public static SystemMessageId YOU_HAVE_EARNED_S2_S1_S;
    @ClientString(id = 54, message = "You have earned $s1.")
    public static SystemMessageId YOU_HAVE_EARNED_S1;
    @ClientString(id = 55, message = "You have failed to pick up $s1 Adena.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA;
    @ClientString(id = 56, message = "You have failed to pick up $s1.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_PICK_UP_S1;
    @ClientString(id = 57, message = "You have failed to pick up $s2 $s1(s).")
    public static SystemMessageId YOU_HAVE_FAILED_TO_PICK_UP_S2_S1_S;
    @ClientString(id = 61, message = "Nothing happened.")
    public static SystemMessageId NOTHING_HAPPENED;
    @ClientString(id = 80, message = "Your title cannot exceed 16 characters in length. Please try again.")
    public static SystemMessageId YOUR_TITLE_CANNOT_EXCEED_16_CHARACTERS_IN_LENGTH_PLEASE_TRY_AGAIN;
    @ClientString(id = 84, message = "You may not attack in a peaceful zone.")
    public static SystemMessageId YOU_MAY_NOT_ATTACK_IN_A_PEACEFUL_ZONE;
    @ClientString(id = 85, message = "You may not attack this target in a peaceful zone.")
    public static SystemMessageId YOU_MAY_NOT_ATTACK_THIS_TARGET_IN_A_PEACEFUL_ZONE;
    @ClientString(id = 92, message = "$s1 has worn off.")
    public static SystemMessageId S1_HAS_WORN_OFF;
    @ClientString(id = 96, message = "Your level has increased!")
    public static SystemMessageId YOUR_LEVEL_HAS_INCREASED;
    @ClientString(id = 98, message = "This item cannot be destroyed.")
    public static SystemMessageId THIS_ITEM_CANNOT_BE_DESTROYED;
    @ClientString(id = 104, message = "You cannot change weapons during an attack.")
    public static SystemMessageId YOU_CANNOT_CHANGE_WEAPONS_DURING_AN_ATTACK;
    @ClientString(id = 105, message = "$c1 has been invited to the party.")
    public static SystemMessageId C1_HAS_BEEN_INVITED_TO_THE_PARTY;
    @ClientString(id = 106, message = "You have joined $s1's party.")
    public static SystemMessageId YOU_HAVE_JOINED_S1_S_PARTY;
    @ClientString(id = 107, message = "$c1 has joined the party.")
    public static SystemMessageId C1_HAS_JOINED_THE_PARTY;
    @ClientString(id = 108, message = "$c1 has left the party.")
    public static SystemMessageId C1_HAS_LEFT_THE_PARTY;
    @ClientString(id = 109, message = "Invalid target.")
    public static SystemMessageId INVALID_TARGET;
    @ClientString(id = 110, message = "$s1's effect can be felt.")
    public static SystemMessageId S1_S_EFFECT_CAN_BE_FELT;
    @ClientString(id = 111, message = "Your shield defense has succeeded.")
    public static SystemMessageId YOUR_SHIELD_DEFENSE_HAS_SUCCEEDED;
    @ClientString(id = 112, message = "You have run out of arrows.")
    public static SystemMessageId YOU_HAVE_RUN_OUT_OF_ARROWS;
    @ClientString(id = 113, message = "$s1 cannot be used due to unsuitable terms.")
    public static SystemMessageId S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS;
    @ClientString(id = 118, message = "You have requested a trade with $c1.")
    public static SystemMessageId YOU_HAVE_REQUESTED_A_TRADE_WITH_C1;
    @ClientString(id = 119, message = "$c1 has denied your request to trade.")
    public static SystemMessageId C1_HAS_DENIED_YOUR_REQUEST_TO_TRADE;
    @ClientString(id = 120, message = "You begin trading with $c1.")
    public static SystemMessageId YOU_BEGIN_TRADING_WITH_C1;
    @ClientString(id = 121, message = "$c1 has confirmed the trade.")
    public static SystemMessageId C1_HAS_CONFIRMED_THE_TRADE;
    @ClientString(id = 122, message = "You may no longer adjust items in the trade because the trade has been confirmed.")
    public static SystemMessageId YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED;
    @ClientString(id = 123, message = "Your trade was successful.")
    public static SystemMessageId YOUR_TRADE_WAS_SUCCESSFUL;
    @ClientString(id = 124, message = "$c1 has cancelled the trade.")
    public static SystemMessageId C1_HAS_CANCELLED_THE_TRADE;
    @ClientString(id = 129, message = "Your inventory is full.")
    public static SystemMessageId YOUR_INVENTORY_IS_FULL;
    @ClientString(id = 132, message = "$s1 has been added to your friends list.")
    public static SystemMessageId S1_HAS_BEEN_ADDED_TO_YOUR_FRIENDS_LIST;
    @ClientString(id = 133, message = "$s1 has been removed from your friends list.")
    public static SystemMessageId S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIENDS_LIST;
    @ClientString(id = 139, message = "$c1 has resisted your $s2.")
    public static SystemMessageId C1_HAS_RESISTED_YOUR_S2;
    @ClientString(id = 140, message = "Your skill was deactivated due to lack of MP.")
    public static SystemMessageId YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP;
    @ClientString(id = 141, message = "You may no longer adjust items in the trade because the trade has been confirmed.")
    public static SystemMessageId YOU_MAY_NO_LONGER_ADJUST_ITEMS_IN_THE_TRADE_BECAUSE_THE_TRADE_HAS_BEEN_CONFIRMED_141;
    @ClientString(id = 142, message = "You are already trading with someone.")
    public static SystemMessageId YOU_ARE_ALREADY_TRADING_WITH_SOMEONE;
    @ClientString(id = 144, message = "That is an incorrect target.")
    public static SystemMessageId THAT_IS_AN_INCORRECT_TARGET;
    @ClientString(id = 145, message = "That player is not online.")
    public static SystemMessageId THAT_PLAYER_IS_NOT_ONLINE;
    @ClientString(id = 147, message = "Chatting is currently prohibited.")
    public static SystemMessageId CHATTING_IS_CURRENTLY_PROHIBITED;
    @ClientString(id = 148, message = "You cannot use quest items.")
    public static SystemMessageId YOU_CANNOT_USE_QUEST_ITEMS;
    @ClientString(id = 151, message = "You cannot discard something that far away from you.")
    public static SystemMessageId YOU_CANNOT_DISCARD_SOMETHING_THAT_FAR_AWAY_FROM_YOU;
    @ClientString(id = 152, message = "You have invited the wrong target.")
    public static SystemMessageId YOU_HAVE_INVITED_THE_WRONG_TARGET;
    @ClientString(id = 153, message = "$c1 is on another task. Please try again later.")
    public static SystemMessageId C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER;
    @ClientString(id = 154, message = "Only the leader can give out invitations.")
    public static SystemMessageId ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS;
    @ClientString(id = 155, message = "The party is full.")
    public static SystemMessageId THE_PARTY_IS_FULL;
    @ClientString(id = 156, message = "Drain was only 50%% successful.")
    public static SystemMessageId DRAIN_WAS_ONLY_50_SUCCESSFUL;
    @ClientString(id = 157, message = "You resisted $c1's drain.")
    public static SystemMessageId YOU_RESISTED_C1_S_DRAIN;
    @ClientString(id = 158, message = "Your attack has failed.")
    public static SystemMessageId YOUR_ATTACK_HAS_FAILED;
    @ClientString(id = 159, message = "You resisted $c1's magic.")
    public static SystemMessageId YOU_RESISTED_C1_S_MAGIC;
    @ClientString(id = 160, message = "$c1 is a member of another party and cannot be invited.")
    public static SystemMessageId C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED;
    @ClientString(id = 161, message = "That player is not currently online.")
    public static SystemMessageId THAT_PLAYER_IS_NOT_CURRENTLY_ONLINE;
    @ClientString(id = 164, message = "Waiting for another reply.")
    public static SystemMessageId WAITING_FOR_ANOTHER_REPLY;
    @ClientString(id = 165, message = "You cannot add yourself to your own friend list.")
    public static SystemMessageId YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST;
    @ClientString(id = 167, message = "$c1 is already on your friend list.")
    public static SystemMessageId C1_IS_ALREADY_ON_YOUR_FRIEND_LIST;
    @ClientString(id = 170, message = "The user who requested to become friends is not found in the game.")
    public static SystemMessageId THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME;
    @ClientString(id = 171, message = "$c1 is not on your friend list.")
    public static SystemMessageId C1_IS_NOT_ON_YOUR_FRIEND_LIST;
    @ClientString(id = 175, message = "That skill has been de-activated as HP was fully recovered.")
    public static SystemMessageId THAT_SKILL_HAS_BEEN_DE_ACTIVATED_AS_HP_WAS_FULLY_RECOVERED;
    @ClientString(id = 176, message = "That person is in message refusal mode.")
    public static SystemMessageId THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE;
    @ClientString(id = 177, message = "Message refusal mode.")
    public static SystemMessageId MESSAGE_REFUSAL_MODE;
    @ClientString(id = 178, message = "Message acceptance mode.")
    public static SystemMessageId MESSAGE_ACCEPTANCE_MODE;
    @ClientString(id = 181, message = "Cannot see target.")
    public static SystemMessageId CANNOT_SEE_TARGET;
    @ClientString(id = 185, message = "You must first select a user to invite to your party.")
    public static SystemMessageId YOU_MUST_FIRST_SELECT_A_USER_TO_INVITE_TO_YOUR_PARTY;
    @ClientString(id = 189, message = "Your clan has been created.")
    public static SystemMessageId YOUR_CLAN_HAS_BEEN_CREATED;
    @ClientString(id = 190, message = "You have failed to create a clan.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_CREATE_A_CLAN;
    @ClientString(id = 191, message = "Clan member $s1 has been expelled.")
    public static SystemMessageId CLAN_MEMBER_S1_HAS_BEEN_EXPELLED;
    @ClientString(id = 193, message = "Clan has dispersed.")
    public static SystemMessageId CLAN_HAS_DISPERSED;
    @ClientString(id = 195, message = "Entered the clan.")
    public static SystemMessageId ENTERED_THE_CLAN;
    @ClientString(id = 197, message = "You have withdrawn from the clan.")
    public static SystemMessageId YOU_HAVE_WITHDRAWN_FROM_THE_CLAN;
    @ClientString(id = 199, message = "You have recently been dismissed from a clan.  You are not allowed to join another clan for 24 hours.")
    public static SystemMessageId YOU_HAVE_RECENTLY_BEEN_DISMISSED_FROM_A_CLAN_YOU_ARE_NOT_ALLOWED_TO_JOIN_ANOTHER_CLAN_FOR_24_HOURS;
    @ClientString(id = 200, message = "You have withdrawn from the party.")
    public static SystemMessageId YOU_HAVE_WITHDRAWN_FROM_THE_PARTY;
    @ClientString(id = 201, message = "$c1 was expelled from the party.")
    public static SystemMessageId C1_WAS_EXPELLED_FROM_THE_PARTY;
    @ClientString(id = 202, message = "You have been expelled from the party.")
    public static SystemMessageId YOU_HAVE_BEEN_EXPELLED_FROM_THE_PARTY;
    @ClientString(id = 203, message = "The party has dispersed.")
    public static SystemMessageId THE_PARTY_HAS_DISPERSED;
    @ClientString(id = 204, message = "Name is not allowed. Please choose another name.")
    public static SystemMessageId NAME_IS_NOT_ALLOWED_PLEASE_CHOOSE_ANOTHER_NAME;
    @ClientString(id = 209, message = "The size of the image file is inappropriate.  Please adjust to 16x12 pixels.")
    public static SystemMessageId THE_SIZE_OF_THE_IMAGE_FILE_IS_INAPPROPRIATE_PLEASE_ADJUST_TO_16X12_PIXELS;
    @ClientString(id = 212, message = "You are not a clan member and cannot perform this action.")
    public static SystemMessageId YOU_ARE_NOT_A_CLAN_MEMBER_AND_CANNOT_PERFORM_THIS_ACTION;
    @ClientString(id = 214, message = "Your title has been changed.")
    public static SystemMessageId YOUR_TITLE_HAS_BEEN_CHANGED;
    @ClientString(id = 215, message = "A clan war with Clan $s1 has started. The clan that cancels the war first will lose 500 Clan Reputation. Any clan that cancels the war will be unable to declare a war for 1 week. If your clan member gets killed by the other clan, XP decreases by 1/4 of the amount that decreases in the hunting ground.")
    public static SystemMessageId A_CLAN_WAR_WITH_CLAN_S1_HAS_STARTED_THE_CLAN_THAT_CANCELS_THE_WAR_FIRST_WILL_LOSE_500_CLAN_REPUTATION_ANY_CLAN_THAT_CANCELS_THE_WAR_WILL_BE_UNABLE_TO_DECLARE_A_WAR_FOR_1_WEEK_IF_YOUR_CLAN_MEMBER_GETS_KILLED_BY_THE_OTHER_CLAN_XP_DECREASES_BY_1_4_OF_THE_AMOUNT_THAT_DECREASES_IN_THE_HUNTING_GROUND;
    @ClientString(id = 222, message = "$s1 has joined the clan.")
    public static SystemMessageId S1_HAS_JOINED_THE_CLAN;
    @ClientString(id = 223, message = "$s1 has withdrawn from the clan.")
    public static SystemMessageId S1_HAS_WITHDRAWN_FROM_THE_CLAN;
    @ClientString(id = 224, message = "$s1 did not respond: Invitation to the clan has been cancelled.")
    public static SystemMessageId S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED;
    @ClientString(id = 225, message = "You didn't respond to $s1's invitation: joining has been cancelled.")
    public static SystemMessageId YOU_DIDN_T_RESPOND_TO_S1_S_INVITATION_JOINING_HAS_BEEN_CANCELLED;
    @ClientString(id = 226, message = "The $s1 clan did not respond: war proclamation has been refused.")
    public static SystemMessageId THE_S1_CLAN_DID_NOT_RESPOND_WAR_PROCLAMATION_HAS_BEEN_REFUSED;
    @ClientString(id = 228, message = "Request to end war has been denied.")
    public static SystemMessageId REQUEST_TO_END_WAR_HAS_BEEN_DENIED;
    @ClientString(id = 229, message = "You do not meet the criteria in order to create a clan.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_CRITERIA_IN_ORDER_TO_CREATE_A_CLAN;
    @ClientString(id = 230, message = "You must wait 10 days before creating a new clan.")
    public static SystemMessageId YOU_MUST_WAIT_10_DAYS_BEFORE_CREATING_A_NEW_CLAN;
    @ClientString(id = 231, message = "After a clan member is dismissed from a clan, the clan must wait at least a day before accepting a new member.")
    public static SystemMessageId AFTER_A_CLAN_MEMBER_IS_DISMISSED_FROM_A_CLAN_THE_CLAN_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_ACCEPTING_A_NEW_MEMBER;
    @ClientString(id = 232, message = "After leaving or having been dismissed from a clan, you must wait at least a day before joining another clan.")
    public static SystemMessageId AFTER_LEAVING_OR_HAVING_BEEN_DISMISSED_FROM_A_CLAN_YOU_MUST_WAIT_AT_LEAST_A_DAY_BEFORE_JOINING_ANOTHER_CLAN;
    @ClientString(id = 233, message = "The Clan is full.")
    public static SystemMessageId THE_CLAN_IS_FULL;
    @ClientString(id = 234, message = "The target must be a clan member.")
    public static SystemMessageId THE_TARGET_MUST_BE_A_CLAN_MEMBER;
    @ClientString(id = 236, message = "Only the clan leader is enabled.")
    public static SystemMessageId ONLY_THE_CLAN_LEADER_IS_ENABLED;
    @ClientString(id = 238, message = "Not joined in any clan.")
    public static SystemMessageId NOT_JOINED_IN_ANY_CLAN;
    @ClientString(id = 239, message = "A clan leader cannot withdraw from their own clan.")
    public static SystemMessageId A_CLAN_LEADER_CANNOT_WITHDRAW_FROM_THEIR_OWN_CLAN;
    @ClientString(id = 242, message = "Select target.")
    public static SystemMessageId SELECT_TARGET;
    @ClientString(id = 261, message = "Clan name is invalid.")
    public static SystemMessageId CLAN_NAME_IS_INVALID;
    @ClientString(id = 262, message = "Clan name's length is incorrect.")
    public static SystemMessageId CLAN_NAME_S_LENGTH_IS_INCORRECT;
    @ClientString(id = 263, message = "You have already requested the dissolution of your clan.")
    public static SystemMessageId YOU_HAVE_ALREADY_REQUESTED_THE_DISSOLUTION_OF_YOUR_CLAN;
    @ClientString(id = 264, message = "You cannot dissolve a clan while engaged in a war.")
    public static SystemMessageId YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_ENGAGED_IN_A_WAR;
    @ClientString(id = 265, message = "You cannot dissolve a clan during a siege or while protecting a castle.")
    public static SystemMessageId YOU_CANNOT_DISSOLVE_A_CLAN_DURING_A_SIEGE_OR_WHILE_PROTECTING_A_CASTLE;
    @ClientString(id = 266, message = "You cannot dissolve a clan while owning a clan hall or castle.")
    public static SystemMessageId YOU_CANNOT_DISSOLVE_A_CLAN_WHILE_OWNING_A_CLAN_HALL_OR_CASTLE;
    @ClientString(id = 269, message = "You cannot dismiss yourself.")
    public static SystemMessageId YOU_CANNOT_DISMISS_YOURSELF;
    @ClientString(id = 271, message = "A player can only be granted a title if the clan is level 3 or above.")
    public static SystemMessageId A_PLAYER_CAN_ONLY_BE_GRANTED_A_TITLE_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE;
    @ClientString(id = 272, message = "A clan crest can only be registered when the clan's skill level is 3 or above.")
    public static SystemMessageId A_CLAN_CREST_CAN_ONLY_BE_REGISTERED_WHEN_THE_CLAN_S_SKILL_LEVEL_IS_3_OR_ABOVE;
    @ClientString(id = 274, message = "Your clan's level has increased.")
    public static SystemMessageId YOUR_CLAN_S_LEVEL_HAS_INCREASED;
    @ClientString(id = 276, message = "You do not have enough items to learn this skill.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_LEARN_THIS_SKILL;
    @ClientString(id = 278, message = "You do not have enough SP to learn this skill.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SP_TO_LEARN_THIS_SKILL;
    @ClientString(id = 279, message = "You do not have enough Adena.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP;
    @ClientString(id = 282, message = "You have not deposited any items in your warehouse.")
    public static SystemMessageId YOU_HAVE_NOT_DEPOSITED_ANY_ITEMS_IN_YOUR_WAREHOUSE;
    @ClientString(id = 283, message = "You have entered a combat zone.")
    public static SystemMessageId YOU_HAVE_ENTERED_A_COMBAT_ZONE;
    @ClientString(id = 284, message = "You have left a combat zone.")
    public static SystemMessageId YOU_HAVE_LEFT_A_COMBAT_ZONE;
    @ClientString(id = 285, message = "Clan $s1 has succeeded in $s2!")
    public static SystemMessageId CLAN_S1_HAS_SUCCEEDED_IN_S2;
    @ClientString(id = 286, message = "Siege Camp is under attack.")
    public static SystemMessageId SIEGE_CAMP_IS_UNDER_ATTACK;
    @ClientString(id = 287, message = "The opposing clan has started $s1.")
    public static SystemMessageId THE_OPPOSING_CLAN_HAS_STARTED_S1;
    @ClientString(id = 288, message = "The castle gate has been destroyed.")
    public static SystemMessageId THE_CASTLE_GATE_HAS_BEEN_DESTROYED;
    @ClientString(id = 290, message = "You can't build headquarters here.")
    public static SystemMessageId YOU_CAN_T_BUILD_HEADQUARTERS_HERE;
    @ClientString(id = 291, message = "Clan $s1 is victorious over $s2's castle siege!")
    public static SystemMessageId CLAN_S1_IS_VICTORIOUS_OVER_S2_S_CASTLE_SIEGE;
    @ClientString(id = 292, message = "$s1 has announced the next castle siege time.")
    public static SystemMessageId S1_HAS_ANNOUNCED_THE_NEXT_CASTLE_SIEGE_TIME;
    @ClientString(id = 293, message = "The registration term for $s1 has ended.")
    public static SystemMessageId THE_REGISTRATION_TERM_FOR_S1_HAS_ENDED;
    @ClientString(id = 295, message = "$s1's siege was canceled because there were no clans that participated.")
    public static SystemMessageId S1_S_SIEGE_WAS_CANCELED_BECAUSE_THERE_WERE_NO_CLANS_THAT_PARTICIPATED;
    @ClientString(id = 296, message = "You received $s1 falling damage.")
    public static SystemMessageId YOU_RECEIVED_S1_FALLING_DAMAGE;
    @ClientString(id = 297, message = "You have taken $s1 damage because you were unable to breathe.")
    public static SystemMessageId YOU_HAVE_TAKEN_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE;
    @ClientString(id = 298, message = "You have dropped $s1.")
    public static SystemMessageId YOU_HAVE_DROPPED_S1;
    @ClientString(id = 299, message = "$c1 has obtained $s3 $s2.")
    public static SystemMessageId C1_HAS_OBTAINED_S3_S2;
    @ClientString(id = 300, message = "$c1 has obtained $s2.")
    public static SystemMessageId C1_HAS_OBTAINED_S2;
    @ClientString(id = 301, message = "$s2 $s1(s) disappeared.")
    public static SystemMessageId S2_S1_S_DISAPPEARED;
    @ClientString(id = 302, message = "$s1 disappeared.")
    public static SystemMessageId S1_DISAPPEARED;
    @ClientString(id = 304, message = "Clan member $s1 has logged into game.")
    public static SystemMessageId CLAN_MEMBER_S1_HAS_LOGGED_INTO_GAME;
    @ClientString(id = 305, message = "The player declined to join your party.")
    public static SystemMessageId THE_PLAYER_DECLINED_TO_JOIN_YOUR_PARTY;
    @ClientString(id = 309, message = "You have succeeded in expelling the clan member.")
    public static SystemMessageId YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN_MEMBER;
    @ClientString(id = 319, message = "This door cannot be unlocked.")
    public static SystemMessageId THIS_DOOR_CANNOT_BE_UNLOCKED;
    @ClientString(id = 320, message = "You have failed to unlock the door.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR;
    @ClientString(id = 323, message = "Your force has increased to level $s1.")
    public static SystemMessageId YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1;
    @ClientString(id = 324, message = "Your force has reached maximum capacity.")
    public static SystemMessageId YOUR_FORCE_HAS_REACHED_MAXIMUM_CAPACITY;
    @ClientString(id = 331, message = "You have acquired $s1 SP.")
    public static SystemMessageId YOU_HAVE_ACQUIRED_S1_SP;
    @ClientString(id = 335, message = "$s1 has been aborted.")
    public static SystemMessageId S1_HAS_BEEN_ABORTED;
    @ClientString(id = 338, message = "You do not have enough soulshots for that.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SOULSHOTS_FOR_THAT;
    @ClientString(id = 339, message = "Cannot use soulshots.")
    public static SystemMessageId CANNOT_USE_SOULSHOTS;
    @ClientString(id = 342, message = "Your soulshots are enabled.")
    public static SystemMessageId YOUR_SOULSHOTS_ARE_ENABLED;
    @ClientString(id = 343, message = "Sweeper failed, target not spoiled.")
    public static SystemMessageId SWEEPER_FAILED_TARGET_NOT_SPOILED;
    @ClientString(id = 347, message = "Incorrect item count.")
    public static SystemMessageId INCORRECT_ITEM_COUNT;
    @ClientString(id = 355, message = "Inappropriate enchant conditions.")
    public static SystemMessageId INAPPROPRIATE_ENCHANT_CONDITIONS;
    @ClientString(id = 356, message = "Reject resurrection.")
    public static SystemMessageId REJECT_RESURRECTION;
    @ClientString(id = 357, message = "It has already been spoiled.")
    public static SystemMessageId IT_HAS_ALREADY_BEEN_SPOILED;
    @ClientString(id = 358, message = "$s1 hour(s) until castle siege conclusion.")
    public static SystemMessageId S1_HOUR_S_UNTIL_CASTLE_SIEGE_CONCLUSION;
    @ClientString(id = 359, message = "$s1 minute(s) until castle siege conclusion.")
    public static SystemMessageId S1_MINUTE_S_UNTIL_CASTLE_SIEGE_CONCLUSION;
    @ClientString(id = 360, message = "This castle siege will end in $s1 second(s)!")
    public static SystemMessageId THIS_CASTLE_SIEGE_WILL_END_IN_S1_SECOND_S;
    @ClientString(id = 361, message = "Over-hit!")
    public static SystemMessageId OVER_HIT;
    @ClientString(id = 368, message = "Equipped +$s1 $s2.")
    public static SystemMessageId EQUIPPED_S1_S2;
    @ClientString(id = 369, message = "You have obtained a +$s1 $s2.")
    public static SystemMessageId YOU_HAVE_OBTAINED_A_S1_S2;
    @ClientString(id = 371, message = "Acquired +$s1 $s2.")
    public static SystemMessageId ACQUIRED_S1_S2;
    @ClientString(id = 378, message = "$c1 purchased $s2.")
    public static SystemMessageId C1_PURCHASED_S2;
    @ClientString(id = 380, message = "$c1 purchased $s3 $s2(s).")
    public static SystemMessageId C1_PURCHASED_S3_S2_S;
    @ClientString(id = 381, message = "The game client encountered an error and was unable to connect to the petition server.")
    public static SystemMessageId THE_GAME_CLIENT_ENCOUNTERED_AN_ERROR_AND_WAS_UNABLE_TO_CONNECT_TO_THE_PETITION_SERVER;
    @ClientString(id = 387, message = "GM has replied to your question.\\nPlease leave a review on our Customer Query Service.")
    public static SystemMessageId GM_HAS_REPLIED_TO_YOUR_QUESTION_PLEASE_LEAVE_A_REVIEW_ON_OUR_CUSTOMER_QUERY_SERVICE;
    @ClientString(id = 388, message = "Not under petition consultation.")
    public static SystemMessageId NOT_UNDER_PETITION_CONSULTATION;
    @ClientString(id = 389, message = "Your petition application has been accepted. \\nReceipt No. is $s1.")
    public static SystemMessageId YOUR_PETITION_APPLICATION_HAS_BEEN_ACCEPTED_NRECEIPT_NO_IS_S1;
    @ClientString(id = 390, message = "You may only submit one petition (active) at a time.")
    public static SystemMessageId YOU_MAY_ONLY_SUBMIT_ONE_PETITION_ACTIVE_AT_A_TIME;
    @ClientString(id = 391, message = "Canceled the Query No. $s1.")
    public static SystemMessageId CANCELED_THE_QUERY_NO_S1;
    @ClientString(id = 393, message = "Failed to cancel petition. Please try again later.")
    public static SystemMessageId FAILED_TO_CANCEL_PETITION_PLEASE_TRY_AGAIN_LATER;
    @ClientString(id = 394, message = "Starting petition consultation with $c1.")
    public static SystemMessageId STARTING_PETITION_CONSULTATION_WITH_C1;
    @ClientString(id = 395, message = "Ended the customer consultation with $c1.")
    public static SystemMessageId ENDED_THE_CUSTOMER_CONSULTATION_WITH_C1;
    @ClientString(id = 402, message = "You do not possess the correct ticket to board the boat.")
    public static SystemMessageId YOU_DO_NOT_POSSESS_THE_CORRECT_TICKET_TO_BOARD_THE_BOAT;
    @ClientString(id = 404, message = "Your Create Item level is too low to register this recipe.")
    public static SystemMessageId YOUR_CREATE_ITEM_LEVEL_IS_TOO_LOW_TO_REGISTER_THIS_RECIPE;
    @ClientString(id = 406, message = "Petition application accepted.")
    public static SystemMessageId PETITION_APPLICATION_ACCEPTED;
    @ClientString(id = 407, message = "Your petition is being processed.")
    public static SystemMessageId YOUR_PETITION_IS_BEING_PROCESSED;
    @ClientString(id = 417, message = "$s1 has been unequipped.")
    public static SystemMessageId S1_HAS_BEEN_UNEQUIPPED;
    @ClientString(id = 421, message = "You are logged in to two places. If you suspect account theft, we recommend changing your password, scanning your computer for viruses and using an anti virus software.")
    public static SystemMessageId YOU_ARE_LOGGED_IN_TO_TWO_PLACES_IF_YOU_SUSPECT_ACCOUNT_THEFT_WE_RECOMMEND_CHANGING_YOUR_PASSWORD_SCANNING_YOUR_COMPUTER_FOR_VIRUSES_AND_USING_AN_ANTI_VIRUS_SOFTWARE;
    @ClientString(id = 422, message = "You have exceeded the weight limit.")
    public static SystemMessageId YOU_HAVE_EXCEEDED_THE_WEIGHT_LIMIT;
    @ClientString(id = 424, message = "Does not fit strengthening conditions of the scroll.")
    public static SystemMessageId DOES_NOT_FIT_STRENGTHENING_CONDITIONS_OF_THE_SCROLL;
    @ClientString(id = 464, message = "This feature is only available to alliance leaders.")
    public static SystemMessageId THIS_FEATURE_IS_ONLY_AVAILABLE_TO_ALLIANCE_LEADERS;
    @ClientString(id = 465, message = "You are not currently allied with any clans.")
    public static SystemMessageId YOU_ARE_NOT_CURRENTLY_ALLIED_WITH_ANY_CLANS;
    @ClientString(id = 466, message = "You have exceeded the limit.")
    public static SystemMessageId YOU_HAVE_EXCEEDED_THE_LIMIT;
    @ClientString(id = 467, message = "You may not accept any clan within a day after expelling another clan.")
    public static SystemMessageId YOU_MAY_NOT_ACCEPT_ANY_CLAN_WITHIN_A_DAY_AFTER_EXPELLING_ANOTHER_CLAN;
    @ClientString(id = 468, message = "A clan that has withdrawn or been expelled cannot enter into an alliance within one day of withdrawal or expulsion.")
    public static SystemMessageId A_CLAN_THAT_HAS_WITHDRAWN_OR_BEEN_EXPELLED_CANNOT_ENTER_INTO_AN_ALLIANCE_WITHIN_ONE_DAY_OF_WITHDRAWAL_OR_EXPULSION;
    @ClientString(id = 469, message = "You may not ally with a clan you are currently at war with.  That would be diabolical and treacherous.")
    public static SystemMessageId YOU_MAY_NOT_ALLY_WITH_A_CLAN_YOU_ARE_CURRENTLY_AT_WAR_WITH_THAT_WOULD_BE_DIABOLICAL_AND_TREACHEROUS;
    @ClientString(id = 470, message = "Only the clan leader may apply for withdrawal from the alliance.")
    public static SystemMessageId ONLY_THE_CLAN_LEADER_MAY_APPLY_FOR_WITHDRAWAL_FROM_THE_ALLIANCE;
    @ClientString(id = 471, message = "Alliance leaders cannot withdraw.")
    public static SystemMessageId ALLIANCE_LEADERS_CANNOT_WITHDRAW;
    @ClientString(id = 473, message = "Different alliance.")
    public static SystemMessageId DIFFERENT_ALLIANCE;
    @ClientString(id = 474, message = "That clan does not exist.")
    public static SystemMessageId THAT_CLAN_DOES_NOT_EXIST;
    @ClientString(id = 476, message = "Please adjust the image size to 8x12.")
    public static SystemMessageId PLEASE_ADJUST_THE_IMAGE_SIZE_TO_8X12;
    @ClientString(id = 477, message = "No response. Invitation to join an alliance has been cancelled.")
    public static SystemMessageId NO_RESPONSE_INVITATION_TO_JOIN_AN_ALLIANCE_HAS_BEEN_CANCELLED;
    @ClientString(id = 478, message = "No response. Your entrance to the alliance has been cancelled.")
    public static SystemMessageId NO_RESPONSE_YOUR_ENTRANCE_TO_THE_ALLIANCE_HAS_BEEN_CANCELLED;
    @ClientString(id = 484, message = "This player is already registered on your friends list.")
    public static SystemMessageId THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST;
    @ClientString(id = 487, message = "======<Friends List>======")
    public static SystemMessageId FRIENDS_LIST;
    @ClientString(id = 488, message = "$s1 (Currently: Online)")
    public static SystemMessageId S1_CURRENTLY_ONLINE;
    @ClientString(id = 489, message = "$s1 (Currently: Offline)")
    public static SystemMessageId S1_CURRENTLY_OFFLINE;
    @ClientString(id = 490, message = "========================")
    public static SystemMessageId SEPARATOR_EQUALS;
    @ClientString(id = 491, message = "=======<Alliance Information>=======")
    public static SystemMessageId ALLIANCE_INFORMATION;
    @ClientString(id = 492, message = "Alliance Name: $s1")
    public static SystemMessageId ALLIANCE_NAME_S1;
    @ClientString(id = 493, message = "Connection: $s1 / Total $s2")
    public static SystemMessageId CONNECTION_S1_TOTAL_S2;
    @ClientString(id = 494, message = "Alliance Leader: $s2 of $s1")
    public static SystemMessageId ALLIANCE_LEADER_S2_OF_S1;
    @ClientString(id = 495, message = "Affiliated clans: Total $s1 clan(s)")
    public static SystemMessageId AFFILIATED_CLANS_TOTAL_S1_CLAN_S;
    @ClientString(id = 496, message = "=====<Clan Information>=====")
    public static SystemMessageId CLAN_INFORMATION;
    @ClientString(id = 497, message = "Clan Name: $s1")
    public static SystemMessageId CLAN_NAME_S1;
    @ClientString(id = 498, message = "Clan Leader:  $s1")
    public static SystemMessageId CLAN_LEADER_S1;
    @ClientString(id = 499, message = "Clan Level: $s1")
    public static SystemMessageId CLAN_LEVEL_S1;
    @ClientString(id = 500, message = "------------------------")
    public static SystemMessageId SEPARATOR_DASHES;
    @ClientString(id = 502, message = "You already belong to another alliance.")
    public static SystemMessageId YOU_ALREADY_BELONG_TO_ANOTHER_ALLIANCE;
    @ClientString(id = 503, message = "Your friend $s1 just logged in.")
    public static SystemMessageId YOUR_FRIEND_S1_JUST_LOGGED_IN;
    @ClientString(id = 504, message = "Only clan leaders may create alliances.")
    public static SystemMessageId ONLY_CLAN_LEADERS_MAY_CREATE_ALLIANCES;
    @ClientString(id = 505, message = "You cannot create a new alliance within 1 day of dissolution.")
    public static SystemMessageId YOU_CANNOT_CREATE_A_NEW_ALLIANCE_WITHIN_1_DAY_OF_DISSOLUTION;
    @ClientString(id = 506, message = "Incorrect alliance name.  Please try again.")
    public static SystemMessageId INCORRECT_ALLIANCE_NAME_PLEASE_TRY_AGAIN;
    @ClientString(id = 507, message = "Incorrect length for an alliance name.")
    public static SystemMessageId INCORRECT_LENGTH_FOR_AN_ALLIANCE_NAME;
    @ClientString(id = 508, message = "That alliance name already exists.")
    public static SystemMessageId THAT_ALLIANCE_NAME_ALREADY_EXISTS;
    @ClientString(id = 517, message = "You have accepted the alliance.")
    public static SystemMessageId YOU_HAVE_ACCEPTED_THE_ALLIANCE;
    @ClientString(id = 519, message = "You have withdrawn from the alliance.")
    public static SystemMessageId YOU_HAVE_WITHDRAWN_FROM_THE_ALLIANCE;
    @ClientString(id = 521, message = "You have succeeded in expelling the clan.")
    public static SystemMessageId YOU_HAVE_SUCCEEDED_IN_EXPELLING_THE_CLAN;
    @ClientString(id = 523, message = "The alliance has been dissolved.")
    public static SystemMessageId THE_ALLIANCE_HAS_BEEN_DISSOLVED;
    @ClientString(id = 525, message = "That person has been successfully added to your Friend List")
    public static SystemMessageId THAT_PERSON_HAS_BEEN_SUCCESSFULLY_ADDED_TO_YOUR_FRIEND_LIST;
    @ClientString(id = 526, message = "You have failed to add a friend to your friends list.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_ADD_A_FRIEND_TO_YOUR_FRIENDS_LIST;
    @ClientString(id = 527, message = "$s1 leader, $s2, has requested an alliance.")
    public static SystemMessageId S1_LEADER_S2_HAS_REQUESTED_AN_ALLIANCE;
    @ClientString(id = 531, message = "You do not have enough Spiritshot for that.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SPIRITSHOT_FOR_THAT;
    @ClientString(id = 532, message = "You may not use Spiritshots.")
    public static SystemMessageId YOU_MAY_NOT_USE_SPIRITSHOTS;
    @ClientString(id = 533, message = "Your spiritshot has been enabled.")
    public static SystemMessageId YOUR_SPIRITSHOT_HAS_BEEN_ENABLED;
    @ClientString(id = 538, message = "Your SP has decreased by $s1.")
    public static SystemMessageId YOUR_SP_HAS_DECREASED_BY_S1;
    @ClientString(id = 539, message = "Your XP has decreased by $s1.")
    public static SystemMessageId YOUR_XP_HAS_DECREASED_BY_S1;
    @ClientString(id = 543, message = "You already have a pet.")
    public static SystemMessageId YOU_ALREADY_HAVE_A_PET;
    @ClientString(id = 544, message = "Your pet cannot carry this item.")
    public static SystemMessageId YOUR_PET_CANNOT_CARRY_THIS_ITEM;
    @ClientString(id = 545, message = "Your pet cannot carry any more items.")
    public static SystemMessageId YOUR_PET_CANNOT_CARRY_ANY_MORE_ITEMS;
    @ClientString(id = 547, message = "Summoning your pet\u2026")
    public static SystemMessageId SUMMONING_YOUR_PET;
    @ClientString(id = 549, message = "To create an alliance, your clan must be Level 5 or higher.")
    public static SystemMessageId TO_CREATE_AN_ALLIANCE_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER;
    @ClientString(id = 550, message = "As you are currently schedule for clan dissolution, no alliance can be created.")
    public static SystemMessageId AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_NO_ALLIANCE_CAN_BE_CREATED;
    @ClientString(id = 551, message = "As you are currently schedule for clan dissolution, your clan level cannot be increased.")
    public static SystemMessageId AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOUR_CLAN_LEVEL_CANNOT_BE_INCREASED;
    @ClientString(id = 552, message = "As you are currently schedule for clan dissolution, you cannot register or delete a Clan Crest.")
    public static SystemMessageId AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST;
    @ClientString(id = 554, message = "You cannot disperse the clans in your alliance.")
    public static SystemMessageId YOU_CANNOT_DISPERSE_THE_CLANS_IN_YOUR_ALLIANCE;
    @ClientString(id = 559, message = "You have purchased $s2 from $c1.")
    public static SystemMessageId YOU_HAVE_PURCHASED_S2_FROM_C1;
    @ClientString(id = 561, message = "You have purchased $s3 $s2(s) from $c1.")
    public static SystemMessageId YOU_HAVE_PURCHASED_S3_S2_S_FROM_C1;
    @ClientString(id = 562, message = "You may not crystallize this item. Your crystallization skill level is too low.")
    public static SystemMessageId YOU_MAY_NOT_CRYSTALLIZE_THIS_ITEM_YOUR_CRYSTALLIZATION_SKILL_LEVEL_IS_TOO_LOW;
    @ClientString(id = 563, message = "Failed to remove enmity.")
    public static SystemMessageId FAILED_TO_REMOVE_ENMITY;
    @ClientString(id = 564, message = "Failed to change enmity.")
    public static SystemMessageId FAILED_TO_CHANGE_ENMITY;
    @ClientString(id = 574, message = "Servitors are not available at this time.")
    public static SystemMessageId SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME;
    @ClientString(id = 577, message = "You cannot summon during a trade or while using a private store.")
    public static SystemMessageId YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE;
    @ClientString(id = 578, message = "You cannot summon during combat.")
    public static SystemMessageId YOU_CANNOT_SUMMON_DURING_COMBAT;
    @ClientString(id = 579, message = "A pet cannot be unsummoned during battle.")
    public static SystemMessageId A_PET_CANNOT_BE_UNSUMMONED_DURING_BATTLE;
    @ClientString(id = 580, message = "You may not summon multiple pets at the same time.")
    public static SystemMessageId YOU_MAY_NOT_SUMMON_MULTIPLE_PETS_AT_THE_SAME_TIME;
    @ClientString(id = 584, message = "This is already in use by another pet.")
    public static SystemMessageId THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET;
    @ClientString(id = 589, message = "Dead pets cannot be returned to their summoning item.")
    public static SystemMessageId DEAD_PETS_CANNOT_BE_RETURNED_TO_THEIR_SUMMONING_ITEM;
    @ClientString(id = 590, message = "Your pet is dead and any attempt you make to give it something goes unrecognized.")
    public static SystemMessageId YOUR_PET_IS_DEAD_AND_ANY_ATTEMPT_YOU_MAKE_TO_GIVE_IT_SOMETHING_GOES_UNRECOGNIZED;
    @ClientString(id = 591, message = "An invalid character is included in the pet's name.")
    public static SystemMessageId AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PET_S_NAME;
    @ClientString(id = 594, message = "You may not restore a hungry pet.")
    public static SystemMessageId YOU_MAY_NOT_RESTORE_A_HUNGRY_PET;
    @ClientString(id = 596, message = "Your pet ate a little, but is still hungry.")
    public static SystemMessageId YOUR_PET_ATE_A_LITTLE_BUT_IS_STILL_HUNGRY;
    @ClientString(id = 601, message = "There are $s1 petitions currently on the waiting list.")
    public static SystemMessageId THERE_ARE_S1_PETITIONS_CURRENTLY_ON_THE_WAITING_LIST;
    @ClientString(id = 603, message = "That item cannot be discarded or exchanged.")
    public static SystemMessageId THAT_ITEM_CANNOT_BE_DISCARDED_OR_EXCHANGED;
    @ClientString(id = 607, message = "You do not have any further skills to learn. Come back when you have reached Level $s1.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1;
    @ClientString(id = 608, message = "$c1 has obtained $s3 $s2(s) by using sweeper.")
    public static SystemMessageId C1_HAS_OBTAINED_S3_S2_S_BY_USING_SWEEPER;
    @ClientString(id = 609, message = "$c1 has obtained $s2 by using sweeper.")
    public static SystemMessageId C1_HAS_OBTAINED_S2_BY_USING_SWEEPER;
    @ClientString(id = 610, message = "Your skill has been canceled due to lack of HP.")
    public static SystemMessageId YOUR_SKILL_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_HP;
    @ClientString(id = 612, message = "The Spoil condition has been activated.")
    public static SystemMessageId THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED;
    @ClientString(id = 615, message = "You have failed to register the user to your Ignore List.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST;
    @ClientString(id = 617, message = "$s1 has been added to your Ignore List.")
    public static SystemMessageId S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST;
    @ClientString(id = 618, message = "$s1 has been removed from your Ignore List.")
    public static SystemMessageId S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST;
    @ClientString(id = 619, message = "$c1 has placed you on his/her Ignore List.")
    public static SystemMessageId C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST;
    @ClientString(id = 628, message = "You have already been at war with the $s1 clan: 5 days must pass before you can declare war again.")
    public static SystemMessageId YOU_HAVE_ALREADY_BEEN_AT_WAR_WITH_THE_S1_CLAN_5_DAYS_MUST_PASS_BEFORE_YOU_CAN_DECLARE_WAR_AGAIN;
    @ClientString(id = 638, message = "You have already requested a Castle Siege.")
    public static SystemMessageId YOU_HAVE_ALREADY_REQUESTED_A_CASTLE_SIEGE;
    @ClientString(id = 639, message = "Your application has been denied because you have already submitted a request for another Castle Siege.")
    public static SystemMessageId YOUR_APPLICATION_HAS_BEEN_DENIED_BECAUSE_YOU_HAVE_ALREADY_SUBMITTED_A_REQUEST_FOR_ANOTHER_CASTLE_SIEGE;
    @ClientString(id = 645, message = "Only clans of level 3 or above may register for a castle siege.")
    public static SystemMessageId ONLY_CLANS_OF_LEVEL_3_OR_ABOVE_MAY_REGISTER_FOR_A_CASTLE_SIEGE;
    @ClientString(id = 648, message = "No more registrations may be accepted for the attacker side.")
    public static SystemMessageId NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_ATTACKER_SIDE;
    @ClientString(id = 649, message = "No more registrations may be accepted for the defender side.")
    public static SystemMessageId NO_MORE_REGISTRATIONS_MAY_BE_ACCEPTED_FOR_THE_DEFENDER_SIDE;
    @ClientString(id = 650, message = "You may not summon from your current location.")
    public static SystemMessageId YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;
    @ClientString(id = 651, message = "Place $s1 in the current location and direction. Do you wish to continue?")
    public static SystemMessageId PLACE_S1_IN_THE_CURRENT_LOCATION_AND_DIRECTION_DO_YOU_WISH_TO_CONTINUE;
    @ClientString(id = 653, message = "You do not have the authority to position mercenaries.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_POSITION_MERCENARIES;
    @ClientString(id = 654, message = "You do not have the authority to cancel mercenary positioning.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_CANCEL_MERCENARY_POSITIONING;
    @ClientString(id = 655, message = "Mercenaries cannot be positioned here.")
    public static SystemMessageId MERCENARIES_CANNOT_BE_POSITIONED_HERE;
    @ClientString(id = 656, message = "This mercenary cannot be positioned anymore.")
    public static SystemMessageId THIS_MERCENARY_CANNOT_BE_POSITIONED_ANYMORE;
    @ClientString(id = 657, message = "Positioning cannot be done here because the distance between mercenaries is too short.")
    public static SystemMessageId POSITIONING_CANNOT_BE_DONE_HERE_BECAUSE_THE_DISTANCE_BETWEEN_MERCENARIES_IS_TOO_SHORT;
    @ClientString(id = 660, message = "This is not the time for siege registration and so registration and cancellation cannot be done.")
    public static SystemMessageId THIS_IS_NOT_THE_TIME_FOR_SIEGE_REGISTRATION_AND_SO_REGISTRATION_AND_CANCELLATION_CANNOT_BE_DONE;
    @ClientString(id = 672, message = "$s1 Adena disappeared.")
    public static SystemMessageId S1_ADENA_DISAPPEARED;
    @ClientString(id = 673, message = "To participate in the 32 clan hall auction, the clan level must be 2 or higher, and the character must be a clan leader or have the right to bid and sell.")
    public static SystemMessageId TO_PARTICIPATE_IN_THE_32_CLAN_HALL_AUCTION_THE_CLAN_LEVEL_MUST_BE_2_OR_HIGHER_AND_THE_CHARACTER_MUST_BE_A_CLAN_LEADER_OR_HAVE_THE_RIGHT_TO_BID_AND_SELL;
    @ClientString(id = 675, message = "There are no clan halls up for auction.")
    public static SystemMessageId THERE_ARE_NO_CLAN_HALLS_UP_FOR_AUCTION;
    @ClientString(id = 676, message = "Since you have already submitted a bid, you are not allowed to participate in another auction at this time.")
    public static SystemMessageId SINCE_YOU_HAVE_ALREADY_SUBMITTED_A_BID_YOU_ARE_NOT_ALLOWED_TO_PARTICIPATE_IN_ANOTHER_AUCTION_AT_THIS_TIME;
    @ClientString(id = 677, message = "Your bid price must be higher than the minimum price currently being bid.")
    public static SystemMessageId YOUR_BID_PRICE_MUST_BE_HIGHER_THAN_THE_MINIMUM_PRICE_CURRENTLY_BEING_BID;
    @ClientString(id = 678, message = "You have submitted a bid for the auction of $s1.")
    public static SystemMessageId YOU_HAVE_SUBMITTED_A_BID_FOR_THE_AUCTION_OF_S1;
    @ClientString(id = 679, message = "You have canceled your bid.")
    public static SystemMessageId YOU_HAVE_CANCELED_YOUR_BID;
    @ClientString(id = 680, message = "You do not meet the requirements to participate in an auction.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_PARTICIPATE_IN_AN_AUCTION;
    @ClientString(id = 683, message = "There are no priority rights on a sweeper.")
    public static SystemMessageId THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER;
    @ClientString(id = 688, message = "Castle-owning clans are automatically registered on the defending side.")
    public static SystemMessageId CASTLE_OWNING_CLANS_ARE_AUTOMATICALLY_REGISTERED_ON_THE_DEFENDING_SIDE;
    @ClientString(id = 689, message = "A clan that owns a castle cannot participate in another siege.")
    public static SystemMessageId A_CLAN_THAT_OWNS_A_CASTLE_CANNOT_PARTICIPATE_IN_ANOTHER_SIEGE;
    @ClientString(id = 690, message = "You cannot register as an attacker because you are in an alliance with the castle-owning clan.")
    public static SystemMessageId YOU_CANNOT_REGISTER_AS_AN_ATTACKER_BECAUSE_YOU_ARE_IN_AN_ALLIANCE_WITH_THE_CASTLE_OWNING_CLAN;
    @ClientString(id = 691, message = "$s1 clan is already a member of $s2 alliance.")
    public static SystemMessageId S1_CLAN_IS_ALREADY_A_MEMBER_OF_S2_ALLIANCE;
    @ClientString(id = 695, message = "You cannot set the name of the pet.")
    public static SystemMessageId YOU_CANNOT_SET_THE_NAME_OF_THE_PET;
    @ClientString(id = 702, message = "There are no GMs currently visible in the public list as they may be performing other functions at the moment.")
    public static SystemMessageId THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT;
    @ClientString(id = 703, message = "======<GM List>======")
    public static SystemMessageId GM_LIST;
    @ClientString(id = 704, message = "GM : $c1")
    public static SystemMessageId GM_C1;
    @ClientString(id = 707, message = "You cannot teleport to a village that is in a siege.")
    public static SystemMessageId YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE;
    @ClientString(id = 709, message = "You do not have the right to use the clan warehouse.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_RIGHT_TO_USE_THE_CLAN_WAREHOUSE;
    @ClientString(id = 710, message = "Only clans of clan level 1 or above can use a clan warehouse.")
    public static SystemMessageId ONLY_CLANS_OF_CLAN_LEVEL_1_OR_ABOVE_CAN_USE_A_CLAN_WAREHOUSE;
    @ClientString(id = 711, message = "The $s1 siege has started.")
    public static SystemMessageId THE_S1_SIEGE_HAS_STARTED;
    @ClientString(id = 712, message = "The $s1 siege has finished.")
    public static SystemMessageId THE_S1_SIEGE_HAS_FINISHED;
    @ClientString(id = 715, message = "The trap device has been stopped.")
    public static SystemMessageId THE_TRAP_DEVICE_HAS_BEEN_STOPPED;
    @ClientString(id = 716, message = "If a base camp does not exist, resurrection is not possible.")
    public static SystemMessageId IF_A_BASE_CAMP_DOES_NOT_EXIST_RESURRECTION_IS_NOT_POSSIBLE;
    @ClientString(id = 717, message = "The guardian tower has been destroyed and resurrection is not possible.")
    public static SystemMessageId THE_GUARDIAN_TOWER_HAS_BEEN_DESTROYED_AND_RESURRECTION_IS_NOT_POSSIBLE;
    @ClientString(id = 718, message = "The castle gates cannot be opened and closed during a siege.")
    public static SystemMessageId THE_CASTLE_GATES_CANNOT_BE_OPENED_AND_CLOSED_DURING_A_SIEGE;
    @ClientString(id = 719, message = "You failed at mixing the item.")
    public static SystemMessageId YOU_FAILED_AT_MIXING_THE_ITEM;
    @ClientString(id = 720, message = "The purchase price is higher than the amount of money that you have and so you cannot open a personal store.")
    public static SystemMessageId THE_PURCHASE_PRICE_IS_HIGHER_THAN_THE_AMOUNT_OF_MONEY_THAT_YOU_HAVE_AND_SO_YOU_CANNOT_OPEN_A_PERSONAL_STORE;
    @ClientString(id = 722, message = "You cannot dissolve an alliance while an affiliated clan is participating in a siege battle.")
    public static SystemMessageId YOU_CANNOT_DISSOLVE_AN_ALLIANCE_WHILE_AN_AFFILIATED_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE;
    @ClientString(id = 723, message = "The opposing clan is participating in a siege battle.")
    public static SystemMessageId THE_OPPOSING_CLAN_IS_PARTICIPATING_IN_A_SIEGE_BATTLE;
    @ClientString(id = 730, message = "You have submitted $s1 petition(s). \\nYou may submit $s2 more petition(s) today.")
    public static SystemMessageId YOU_HAVE_SUBMITTED_S1_PETITION_S_NYOU_MAY_SUBMIT_S2_MORE_PETITION_S_TODAY;
    @ClientString(id = 733, message = "We have received $s1 petitions from you today and that is the maximum that you can submit in one day. You cannot submit any more petitions.")
    public static SystemMessageId WE_HAVE_RECEIVED_S1_PETITIONS_FROM_YOU_TODAY_AND_THAT_IS_THE_MAXIMUM_THAT_YOU_CAN_SUBMIT_IN_ONE_DAY_YOU_CANNOT_SUBMIT_ANY_MORE_PETITIONS;
    @ClientString(id = 736, message = "The petition was canceled. You may submit $s1 more petition(s) today.")
    public static SystemMessageId THE_PETITION_WAS_CANCELED_YOU_MAY_SUBMIT_S1_MORE_PETITION_S_TODAY;
    @ClientString(id = 738, message = "You have not submitted a petition.")
    public static SystemMessageId YOU_HAVE_NOT_SUBMITTED_A_PETITION;
    @ClientString(id = 745, message = "You are currently not in a petition chat.")
    public static SystemMessageId YOU_ARE_CURRENTLY_NOT_IN_A_PETITION_CHAT;
    @ClientString(id = 748, message = "The distance is too far and so the casting has been cancelled.")
    public static SystemMessageId THE_DISTANCE_IS_TOO_FAR_AND_SO_THE_CASTING_HAS_BEEN_CANCELLED;
    @ClientString(id = 749, message = "The effect of $s1 has been removed.")
    public static SystemMessageId THE_EFFECT_OF_S1_HAS_BEEN_REMOVED;
    @ClientString(id = 750, message = "There are no other skills to learn.")
    public static SystemMessageId THERE_ARE_NO_OTHER_SKILLS_TO_LEARN;
    @ClientString(id = 760, message = "$c1 cannot join the clan because one day has not yet passed since they left another clan.")
    public static SystemMessageId C1_CANNOT_JOIN_THE_CLAN_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_CLAN;
    @ClientString(id = 761, message = "$s1 clan cannot join the alliance because one day has not yet passed since they left another alliance.")
    public static SystemMessageId S1_CLAN_CANNOT_JOIN_THE_ALLIANCE_BECAUSE_ONE_DAY_HAS_NOT_YET_PASSED_SINCE_THEY_LEFT_ANOTHER_ALLIANCE;
    @ClientString(id = 764, message = "You have been playing for $s1 hour(s). How about taking a break?")
    public static SystemMessageId YOU_HAVE_BEEN_PLAYING_FOR_S1_HOUR_S_HOW_ABOUT_TAKING_A_BREAK;
    @ClientString(id = 780, message = "Observation is only possible during a siege.")
    public static SystemMessageId OBSERVATION_IS_ONLY_POSSIBLE_DURING_A_SIEGE;
    @ClientString(id = 781, message = "Observers cannot participate.")
    public static SystemMessageId OBSERVERS_CANNOT_PARTICIPATE;
    @ClientString(id = 782, message = "You may not observe a siege with a servitor summoned.")
    public static SystemMessageId YOU_MAY_NOT_OBSERVE_A_SIEGE_WITH_A_SERVITOR_SUMMONED;
    @ClientString(id = 794, message = "You are not authorized to do that.")
    public static SystemMessageId YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT;
    @ClientString(id = 797, message = "You may create up to 48 macros.")
    public static SystemMessageId YOU_MAY_CREATE_UP_TO_48_MACROS;
    @ClientString(id = 810, message = "Invalid macro. Refer to the Help file for instructions.")
    public static SystemMessageId INVALID_MACRO_REFER_TO_THE_HELP_FILE_FOR_INSTRUCTIONS;
    @ClientString(id = 827, message = "You may not impose a block on a GM.")
    public static SystemMessageId YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM;
    @ClientString(id = 829, message = "You cannot recommend yourself.")
    public static SystemMessageId YOU_CANNOT_RECOMMEND_YOURSELF;
    @ClientString(id = 830, message = "You have recommended $c1. You have $s2 recommendations left.")
    public static SystemMessageId YOU_HAVE_RECOMMENDED_C1_YOU_HAVE_S2_RECOMMENDATIONS_LEFT;
    @ClientString(id = 831, message = "You have been recommended by $c1.")
    public static SystemMessageId YOU_HAVE_BEEN_RECOMMENDED_BY_C1;
    @ClientString(id = 834, message = "$c1 has rolled a $s2.")
    public static SystemMessageId C1_HAS_ROLLED_A_S2;
    @ClientString(id = 835, message = "You may not throw the dice at this time. Try again later.")
    public static SystemMessageId YOU_MAY_NOT_THROW_THE_DICE_AT_THIS_TIME_TRY_AGAIN_LATER;
    @ClientString(id = 837, message = "Macro descriptions may contain up to 32 characters.")
    public static SystemMessageId MACRO_DESCRIPTIONS_MAY_CONTAIN_UP_TO_32_CHARACTERS;
    @ClientString(id = 838, message = "Enter the name of the macro.")
    public static SystemMessageId ENTER_THE_NAME_OF_THE_MACRO;
    @ClientString(id = 840, message = "That recipe is already registered.")
    public static SystemMessageId THAT_RECIPE_IS_ALREADY_REGISTERED;
    @ClientString(id = 845, message = "The deadline to register for the siege of $s1 has passed.")
    public static SystemMessageId THE_DEADLINE_TO_REGISTER_FOR_THE_SIEGE_OF_S1_HAS_PASSED;
    @ClientString(id = 846, message = "The siege of $s1 has been canceled due to lack of interest.")
    public static SystemMessageId THE_SIEGE_OF_S1_HAS_BEEN_CANCELED_DUE_TO_LACK_OF_INTEREST;
    @ClientString(id = 851, message = "$s1 has been added.")
    public static SystemMessageId S1_HAS_BEEN_ADDED;
    @ClientString(id = 853, message = "You may not alter your recipe book while engaged in manufacturing.")
    public static SystemMessageId YOU_MAY_NOT_ALTER_YOUR_RECIPE_BOOK_WHILE_ENGAGED_IN_MANUFACTURING;
    @ClientString(id = 854, message = "You need $s2 more $s1(s).")
    public static SystemMessageId YOU_NEED_S2_MORE_S1_S;
    @ClientString(id = 856, message = "The siege of $s1 has ended in a draw.")
    public static SystemMessageId THE_SIEGE_OF_S1_HAS_ENDED_IN_A_DRAW;
    @ClientString(id = 872, message = "This seed may not be sown here.")
    public static SystemMessageId THIS_SEED_MAY_NOT_BE_SOWN_HERE;
    @ClientString(id = 873, message = "That character does not exist.")
    public static SystemMessageId THAT_CHARACTER_DOES_NOT_EXIST;
    @ClientString(id = 877, message = "The symbol has been added.")
    public static SystemMessageId THE_SYMBOL_HAS_BEEN_ADDED;
    @ClientString(id = 878, message = "The symbol has been deleted.")
    public static SystemMessageId THE_SYMBOL_HAS_BEEN_DELETED;
    @ClientString(id = 879, message = "The manor system is currently under maintenance.")
    public static SystemMessageId THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE;
    @ClientString(id = 884, message = "The manor information has been updated.")
    public static SystemMessageId THE_MANOR_INFORMATION_HAS_BEEN_UPDATED;
    @ClientString(id = 889, message = "The seed was successfully sown.")
    public static SystemMessageId THE_SEED_WAS_SUCCESSFULLY_SOWN;
    @ClientString(id = 890, message = "The seed was not sown.")
    public static SystemMessageId THE_SEED_WAS_NOT_SOWN;
    @ClientString(id = 891, message = "You are not authorized to harvest.")
    public static SystemMessageId YOU_ARE_NOT_AUTHORIZED_TO_HARVEST;
    @ClientString(id = 892, message = "The harvest has failed.")
    public static SystemMessageId THE_HARVEST_HAS_FAILED;
    @ClientString(id = 893, message = "The harvest failed because the seed was not sown.")
    public static SystemMessageId THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN;
    @ClientString(id = 894, message = "Up to $s1 recipes can be registered.")
    public static SystemMessageId UP_TO_S1_RECIPES_CAN_BE_REGISTERED;
    @ClientString(id = 899, message = "The symbol cannot be drawn.")
    public static SystemMessageId THE_SYMBOL_CANNOT_BE_DRAWN;
    @ClientString(id = 900, message = "No slot exists to draw the symbol.")
    public static SystemMessageId NO_SLOT_EXISTS_TO_DRAW_THE_SYMBOL;
    @ClientString(id = 910, message = "Current location: $s1 / $s2 / $s3 (Near Talking Island Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_TALKING_ISLAND_VILLAGE;
    @ClientString(id = 911, message = "Current location: $s1 / $s2 / $s3 (Near Gludin Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_GLUDIN_VILLAGE;
    @ClientString(id = 912, message = "Current location: $s1 / $s2 / $s3 (Near the Town of Gludio)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GLUDIO;
    @ClientString(id = 913, message = "Current location: $s1 / $s2 / $s3 (Near the Neutral Zone)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_NEUTRAL_ZONE;
    @ClientString(id = 914, message = "Current location: $s1 / $s2 / $s3 (Near the Elven Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ELVEN_VILLAGE;
    @ClientString(id = 915, message = "Current location: $s1 / $s2 / $s3 (Near the Dark Elf Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DARK_ELF_VILLAGE;
    @ClientString(id = 916, message = "Current location: $s1 / $s2 / $s3 (Near the Town of Dion)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_DION;
    @ClientString(id = 917, message = "Current location: $s1 / $s2 / $s3 (Near the Floran Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_FLORAN_VILLAGE;
    @ClientString(id = 918, message = "Current location: $s1 / $s2 / $s3 (Near the Town of Giran)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GIRAN;
    @ClientString(id = 919, message = "Current location: $s1 / $s2 / $s3 (Near Giran Harbor)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_GIRAN_HARBOR;
    @ClientString(id = 920, message = "Current location: $s1 / $s2 / $s3 (Near the Orc Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_ORC_VILLAGE;
    @ClientString(id = 921, message = "Current location: $s1 / $s2 / $s3 (Near the Dwarven Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_DWARVEN_VILLAGE;
    @ClientString(id = 922, message = "Current location: $s1 / $s2 / $s3 (Near the Town of Oren)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_OREN;
    @ClientString(id = 923, message = "Current location: $s1 / $s2 / $s3 (Near Hunter's Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_HUNTER_S_VILLAGE;
    @ClientString(id = 924, message = "Current location: $s1 / $s2 / $s3 (Near Town of Aden)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_TOWN_OF_ADEN;
    @ClientString(id = 925, message = "Current location: $s1 / $s2 / $s3 (Near the Coliseum)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_COLISEUM;
    @ClientString(id = 926, message = "Current location: $s1 / $s2 / $s3 (Near Heine)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_HEINE;
    @ClientString(id = 927, message = "The current time is $s1:$s2.")
    public static SystemMessageId THE_CURRENT_TIME_IS_S1_S2;
    @ClientString(id = 935, message = "You do not have enough funds in the clan warehouse for the Manor to operate.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_FUNDS_IN_THE_CLAN_WAREHOUSE_FOR_THE_MANOR_TO_OPERATE;
    @ClientString(id = 938, message = "The community server is currently offline.")
    public static SystemMessageId THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE;
    @ClientString(id = 966, message = "Chatting is currently prohibited. If you try to chat before the prohibition is removed, the prohibition time will increase even further.")
    public static SystemMessageId CHATTING_IS_CURRENTLY_PROHIBITED_IF_YOU_TRY_TO_CHAT_BEFORE_THE_PROHIBITION_IS_REMOVED_THE_PROHIBITION_TIME_WILL_INCREASE_EVEN_FURTHER;
    @ClientString(id = 970, message = "$s2's MP has been drained by $c1.")
    public static SystemMessageId S2_S_MP_HAS_BEEN_DRAINED_BY_C1;
    @ClientString(id = 971, message = "The petition can contain up to 800 characters.")
    public static SystemMessageId THE_PETITION_CAN_CONTAIN_UP_TO_800_CHARACTERS;
    @ClientString(id = 972, message = "This pet cannot use this item.")
    public static SystemMessageId THIS_PET_CANNOT_USE_THIS_ITEM;
    @ClientString(id = 1004, message = "You have registered for a clan hall auction.")
    public static SystemMessageId YOU_HAVE_REGISTERED_FOR_A_CLAN_HALL_AUCTION;
    @ClientString(id = 1005, message = "There is not enough Adena in the clan hall warehouse.")
    public static SystemMessageId THERE_IS_NOT_ENOUGH_ADENA_IN_THE_CLAN_HALL_WAREHOUSE;
    @ClientString(id = 1006, message = "Your bid has been successfully placed.")
    public static SystemMessageId YOUR_BID_HAS_BEEN_SUCCESSFULLY_PLACED;
    @ClientString(id = 1008, message = "A hungry mount cannot be mounted or dismounted.")
    public static SystemMessageId A_HUNGRY_MOUNT_CANNOT_BE_MOUNTED_OR_DISMOUNTED;
    @ClientString(id = 1009, message = "A mount cannot be ridden when dead.")
    public static SystemMessageId A_MOUNT_CANNOT_BE_RIDDEN_WHEN_DEAD;
    @ClientString(id = 1010, message = "A dead mount cannot be ridden.")
    public static SystemMessageId A_DEAD_MOUNT_CANNOT_BE_RIDDEN;
    @ClientString(id = 1011, message = "A mount in battle cannot be ridden.")
    public static SystemMessageId A_MOUNT_IN_BATTLE_CANNOT_BE_RIDDEN;
    @ClientString(id = 1012, message = "A mount cannot be ridden while in battle.")
    public static SystemMessageId A_MOUNT_CANNOT_BE_RIDDEN_WHILE_IN_BATTLE;
    @ClientString(id = 1013, message = "A mount can be ridden only when standing.")
    public static SystemMessageId A_MOUNT_CAN_BE_RIDDEN_ONLY_WHEN_STANDING;
    @ClientString(id = 1014, message = "Your pet gained $s1 XP.")
    public static SystemMessageId YOUR_PET_GAINED_S1_XP;
    @ClientString(id = 1016, message = "Your pet received $s2 damage by $c1.")
    public static SystemMessageId YOUR_PET_RECEIVED_S2_DAMAGE_BY_C1;
    @ClientString(id = 1017, message = "Pet's critical hit!")
    public static SystemMessageId PET_S_CRITICAL_HIT;
    @ClientString(id = 1018, message = "Your pet uses $s1.")
    public static SystemMessageId YOUR_PET_USES_S1;
    @ClientString(id = 1020, message = "Your pet picked up $s1.")
    public static SystemMessageId YOUR_PET_PICKED_UP_S1;
    @ClientString(id = 1021, message = "Your pet picked up $s2 $s1(s).")
    public static SystemMessageId YOUR_PET_PICKED_UP_S2_S1_S;
    @ClientString(id = 1022, message = "Your pet picked up +$s1 $s2.")
    public static SystemMessageId YOUR_PET_PICKED_UP_S1_S2;
    @ClientString(id = 1023, message = "Your pet picked up $s1 Adena.")
    public static SystemMessageId YOUR_PET_PICKED_UP_S1_ADENA;
    @ClientString(id = 1028, message = "Summoned monster's critical hit!")
    public static SystemMessageId SUMMONED_MONSTER_S_CRITICAL_HIT;
    @ClientString(id = 1029, message = "A summoned monster uses $s1.")
    public static SystemMessageId A_SUMMONED_MONSTER_USES_S1;
    @ClientString(id = 1030, message = "<Party Information>")
    public static SystemMessageId PARTY_INFORMATION;
    @ClientString(id = 1031, message = "Looting method: Finders Keepers")
    public static SystemMessageId LOOTING_METHOD_FINDERS_KEEPERS;
    @ClientString(id = 1032, message = "Looting method: Random.")
    public static SystemMessageId LOOTING_METHOD_RANDOM;
    @ClientString(id = 1033, message = "Looting method: Random including spoil.")
    public static SystemMessageId LOOTING_METHOD_RANDOM_INCLUDING_SPOIL;
    @ClientString(id = 1034, message = "Looting method: By turn.")
    public static SystemMessageId LOOTING_METHOD_BY_TURN;
    @ClientString(id = 1035, message = "Looting method: By turn including spoil.")
    public static SystemMessageId LOOTING_METHOD_BY_TURN_INCLUDING_SPOIL;
    @ClientString(id = 1036, message = "You have exceeded the quantity that can be inputted.")
    public static SystemMessageId YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED;
    @ClientString(id = 1039, message = "Items left at the clan hall warehouse can only be retrieved by the clan leader. Do you want to continue?")
    public static SystemMessageId ITEMS_LEFT_AT_THE_CLAN_HALL_WAREHOUSE_CAN_ONLY_BE_RETRIEVED_BY_THE_CLAN_LEADER_DO_YOU_WANT_TO_CONTINUE;
    @ClientString(id = 1050, message = "There are no communities in my clan. Clan communities are allowed for clans with skill levels of 2 and higher.")
    public static SystemMessageId THERE_ARE_NO_COMMUNITIES_IN_MY_CLAN_CLAN_COMMUNITIES_ARE_ALLOWED_FOR_CLANS_WITH_SKILL_LEVELS_OF_2_AND_HIGHER;
    @ClientString(id = 1051, message = "Payment for your clan hall has not been made. Please make payment to your clan warehouse by $s1 tomorrow.")
    public static SystemMessageId PAYMENT_FOR_YOUR_CLAN_HALL_HAS_NOT_BEEN_MADE_PLEASE_MAKE_PAYMENT_TO_YOUR_CLAN_WAREHOUSE_BY_S1_TOMORROW;
    @ClientString(id = 1052, message = "The clan hall fee is one week overdue; therefore the clan hall ownership has been revoked.")
    public static SystemMessageId THE_CLAN_HALL_FEE_IS_ONE_WEEK_OVERDUE_THEREFORE_THE_CLAN_HALL_OWNERSHIP_HAS_BEEN_REVOKED;
    @ClientString(id = 1053, message = "It is not possible to resurrect in battlegrounds where a siege war is taking place.")
    public static SystemMessageId IT_IS_NOT_POSSIBLE_TO_RESURRECT_IN_BATTLEGROUNDS_WHERE_A_SIEGE_WAR_IS_TAKING_PLACE;
    @ClientString(id = 1054, message = "You have entered a mystical land.")
    public static SystemMessageId YOU_HAVE_ENTERED_A_MYSTICAL_LAND;
    @ClientString(id = 1055, message = "You have left a mystical land.")
    public static SystemMessageId YOU_HAVE_LEFT_A_MYSTICAL_LAND;
    @ClientString(id = 1061, message = "The recipe cannot be registered.  You do not have the ability to create items.")
    public static SystemMessageId THE_RECIPE_CANNOT_BE_REGISTERED_YOU_DO_NOT_HAVE_THE_ABILITY_TO_CREATE_ITEMS;
    @ClientString(id = 1063, message = "The Petition Service is currently unavailable, please send a support ticket on support. If you become trapped or unable to move, please use the '/unstuck' command.")
    public static SystemMessageId THE_PETITION_SERVICE_IS_CURRENTLY_UNAVAILABLE_PLEASE_SEND_A_SUPPORT_TICKET_ON_SUPPORT_IF_YOU_BECOME_TRAPPED_OR_UNABLE_TO_MOVE_PLEASE_USE_THE_UNSTUCK_COMMAND;
    @ClientString(id = 1064, message = "The equipment, +$s1 $s2, has been removed.")
    public static SystemMessageId THE_EQUIPMENT_S1_S2_HAS_BEEN_REMOVED;
    @ClientString(id = 1065, message = "While operating a private store or workshop, you cannot discard, destroy, or trade an item.")
    public static SystemMessageId WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM;
    @ClientString(id = 1066, message = "$s1 HP has been restored.")
    public static SystemMessageId S1_HP_HAS_BEEN_RESTORED;
    @ClientString(id = 1067, message = "$s2 HP has been restored by $c1.")
    public static SystemMessageId S2_HP_HAS_BEEN_RESTORED_BY_C1;
    @ClientString(id = 1068, message = "$s1 MP has been restored.")
    public static SystemMessageId S1_MP_HAS_BEEN_RESTORED;
    @ClientString(id = 1069, message = "$s2 MP has been restored by $c1.")
    public static SystemMessageId S2_MP_HAS_BEEN_RESTORED_BY_C1;
    @ClientString(id = 1078, message = "When a user's keyboard input exceeds a certain cumulative score a chat ban will be applied. This is done to discourage spamming. Please avoid posting the same message multiple times during a short period.")
    public static SystemMessageId WHEN_A_USER_S_KEYBOARD_INPUT_EXCEEDS_A_CERTAIN_CUMULATIVE_SCORE_A_CHAT_BAN_WILL_BE_APPLIED_THIS_IS_DONE_TO_DISCOURAGE_SPAMMING_PLEASE_AVOID_POSTING_THE_SAME_MESSAGE_MULTIPLE_TIMES_DURING_A_SHORT_PERIOD;
    @ClientString(id = 1114, message = "Your clan may not register to participate in a siege while under a grace period of the clan's dissolution.")
    public static SystemMessageId YOUR_CLAN_MAY_NOT_REGISTER_TO_PARTICIPATE_IN_A_SIEGE_WHILE_UNDER_A_GRACE_PERIOD_OF_THE_CLAN_S_DISSOLUTION;
    @ClientString(id = 1116, message = "You cannot leave a clan while engaged in combat.")
    public static SystemMessageId YOU_CANNOT_LEAVE_A_CLAN_WHILE_ENGAGED_IN_COMBAT;
    @ClientString(id = 1117, message = "A clan member may not be dismissed during combat.")
    public static SystemMessageId A_CLAN_MEMBER_MAY_NOT_BE_DISMISSED_DURING_COMBAT;
    @ClientString(id = 1118, message = "Unable to process this request until your inventory's weight and slot count are less than 80 percent of capacity.")
    public static SystemMessageId UNABLE_TO_PROCESS_THIS_REQUEST_UNTIL_YOUR_INVENTORY_S_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY;
    @ClientString(id = 1123, message = "Weight and volume limit have been exceeded. That skill is currently unavailable.")
    public static SystemMessageId WEIGHT_AND_VOLUME_LIMIT_HAVE_BEEN_EXCEEDED_THAT_SKILL_IS_CURRENTLY_UNAVAILABLE;
    @ClientString(id = 1124, message = "Your recipe book may not be accessed while using a skill.")
    public static SystemMessageId YOUR_RECIPE_BOOK_MAY_NOT_BE_ACCESSED_WHILE_USING_A_SKILL;
    @ClientString(id = 1125, message = "Item creation is not possible while engaged in a trade.")
    public static SystemMessageId ITEM_CREATION_IS_NOT_POSSIBLE_WHILE_ENGAGED_IN_A_TRADE;
    @ClientString(id = 1129, message = "This is not allowed while riding a ferry or boat.")
    public static SystemMessageId THIS_IS_NOT_ALLOWED_WHILE_RIDING_A_FERRY_OR_BOAT;
    @ClientString(id = 1130, message = "You have dealt $s1 damage to your target and $s2 damage to the servitor.")
    public static SystemMessageId YOU_HAVE_DEALT_S1_DAMAGE_TO_YOUR_TARGET_AND_S2_DAMAGE_TO_THE_SERVITOR;
    @ClientString(id = 1131, message = "It is now midnight and the effect of $s1 can be felt.")
    public static SystemMessageId IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT;
    @ClientString(id = 1132, message = "It is dawn and the effect of $s1 will now disappear.")
    public static SystemMessageId IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR;
    @ClientString(id = 1135, message = "While you are engaged in combat, you cannot operate a private store or private workshop.")
    public static SystemMessageId WHILE_YOU_ARE_ENGAGED_IN_COMBAT_YOU_CANNOT_OPERATE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
    @ClientString(id = 1137, message = "$c1 harvested $s3 $s2(s).")
    public static SystemMessageId C1_HARVESTED_S3_S2_S;
    @ClientString(id = 1138, message = "$c1 has obtained $s2.")
    public static SystemMessageId C1_HAS_OBTAINED_S2_BROWN;
    @ClientString(id = 1140, message = "Would you like to open the gate?")
    public static SystemMessageId WOULD_YOU_LIKE_TO_OPEN_THE_GATE;
    @ClientString(id = 1141, message = "Would you like to close the gate?")
    public static SystemMessageId WOULD_YOU_LIKE_TO_CLOSE_THE_GATE;
    @ClientString(id = 1143, message = "Since you do not have enough items to maintain the servitor's stay, the servitor has disappeared.")
    public static SystemMessageId SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITOR_S_STAY_THE_SERVITOR_HAS_DISAPPEARED;
    @ClientString(id = 1145, message = "$s2 has been created for $c1 after the payment of $s3 Adena was received.")
    public static SystemMessageId S2_HAS_BEEN_CREATED_FOR_C1_AFTER_THE_PAYMENT_OF_S3_ADENA_WAS_RECEIVED;
    @ClientString(id = 1146, message = "$c1 created $s2 after receiving $s3 Adena.")
    public static SystemMessageId C1_CREATED_S2_AFTER_RECEIVING_S3_ADENA;
    @ClientString(id = 1147, message = "$s3 $s2(s) have been created for $c1 at the price of $s4 Adena.")
    public static SystemMessageId S3_S2_S_HAVE_BEEN_CREATED_FOR_C1_AT_THE_PRICE_OF_S4_ADENA;
    @ClientString(id = 1148, message = "$c1 created $s3 $s2(s) at the price of $s4 Adena.")
    public static SystemMessageId C1_CREATED_S3_S2_S_AT_THE_PRICE_OF_S4_ADENA;
    @ClientString(id = 1149, message = "You failed to create $s2 for $c1 at the price of $s3 Adena.")
    public static SystemMessageId YOU_FAILED_TO_CREATE_S2_FOR_C1_AT_THE_PRICE_OF_S3_ADENA;
    @ClientString(id = 1150, message = "$c1 has failed to create $s2 at the price of $s3 Adena.")
    public static SystemMessageId C1_HAS_FAILED_TO_CREATE_S2_AT_THE_PRICE_OF_S3_ADENA;
    @ClientString(id = 1158, message = "You cannot dismount from this elevation.")
    public static SystemMessageId YOU_CANNOT_DISMOUNT_FROM_THIS_ELEVATION;
    @ClientString(id = 1188, message = "Your selected target can no longer receive a recommendation.")
    public static SystemMessageId YOUR_SELECTED_TARGET_CAN_NO_LONGER_RECEIVE_A_RECOMMENDATION;
    @ClientString(id = 1197, message = "Summoning a servitor costs $s2 $s1.")
    public static SystemMessageId SUMMONING_A_SERVITOR_COSTS_S2_S1;
    @ClientString(id = 1200, message = "= $s1 ($s2 Alliance)")
    public static SystemMessageId S1_S2_ALLIANCE;
    @ClientString(id = 1202, message = "$s1 (No Alliance exists)")
    public static SystemMessageId S1_NO_ALLIANCE_EXISTS;
    @ClientString(id = 1208, message = "$c1 died and dropped $s3 $s2(s).")
    public static SystemMessageId C1_DIED_AND_DROPPED_S3_S2_S;
    @ClientString(id = 1209, message = "Congratulations. Your raid was successful.")
    public static SystemMessageId CONGRATULATIONS_YOUR_RAID_WAS_SUCCESSFUL;
    @ClientString(id = 1228, message = "$c1 has blocked you. You cannot send mail to $c1.")
    public static SystemMessageId C1_HAS_BLOCKED_YOU_YOU_CANNOT_SEND_MAIL_TO_C1;
    @ClientString(id = 1247, message = "The corpse is too old. The skill cannot be used.")
    public static SystemMessageId THE_CORPSE_IS_TOO_OLD_THE_SKILL_CANNOT_BE_USED;
    @ClientString(id = 1248, message = "You are out of feed. Mount status canceled.")
    public static SystemMessageId YOU_ARE_OUT_OF_FEED_MOUNT_STATUS_CANCELED;
    @ClientString(id = 1258, message = "$s1 has been crystallized.")
    public static SystemMessageId S1_HAS_BEEN_CRYSTALLIZED;
    @ClientString(id = 1269, message = "The new subclass has been added.")
    public static SystemMessageId THE_NEW_SUBCLASS_HAS_BEEN_ADDED;
    @ClientString(id = 1270, message = "You have successfully switched $s1 to $s2.")
    public static SystemMessageId YOU_HAVE_SUCCESSFULLY_SWITCHED_S1_TO_S2;
    @ClientString(id = 1278, message = "The NPC server is not operating at this time.")
    public static SystemMessageId THE_NPC_SERVER_IS_NOT_OPERATING_AT_THIS_TIME;
    @ClientString(id = 1280, message = "M. Critical!")
    public static SystemMessageId M_CRITICAL;
    @ClientString(id = 1281, message = "Your excellent shield defense was a success!")
    public static SystemMessageId YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS;
    @ClientString(id = 1282, message = "Your Reputation has been changed to $s1.")
    public static SystemMessageId YOUR_REPUTATION_HAS_BEEN_CHANGED_TO_S1;
    @ClientString(id = 1295, message = "Subclasses may not be created or changed while a skill is in use.")
    public static SystemMessageId SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE;
    @ClientString(id = 1296, message = "You cannot open a Private Store here.")
    public static SystemMessageId YOU_CANNOT_OPEN_A_PRIVATE_STORE_HERE;
    @ClientString(id = 1297, message = "You cannot open a Private Workshop here.")
    public static SystemMessageId YOU_CANNOT_OPEN_A_PRIVATE_WORKSHOP_HERE;
    @ClientString(id = 1300, message = "You are no longer trying on equipment.")
    public static SystemMessageId YOU_ARE_NO_LONGER_TRYING_ON_EQUIPMENT;
    @ClientString(id = 1308, message = "Congratulations - You've completed a class transfer!")
    public static SystemMessageId CONGRATULATIONS_YOU_VE_COMPLETED_A_CLASS_TRANSFER;
    @ClientString(id = 1368, message = "You can not try those items on at the same time.")
    public static SystemMessageId YOU_CAN_NOT_TRY_THOSE_ITEMS_ON_AT_THE_SAME_TIME;
    @ClientString(id = 1370, message = "Your message to $c1 did not reach its recipient. You cannot send mail to the GM staff.")
    public static SystemMessageId YOUR_MESSAGE_TO_C1_DID_NOT_REACH_ITS_RECIPIENT_YOU_CANNOT_SEND_MAIL_TO_THE_GM_STAFF;
    @ClientString(id = 1384, message = "$c1 has become the party leader.")
    public static SystemMessageId C1_HAS_BECOME_THE_PARTY_LEADER;
    @ClientString(id = 1385, message = "You are not allowed to dismount in this location.")
    public static SystemMessageId YOU_ARE_NOT_ALLOWED_TO_DISMOUNT_IN_THIS_LOCATION;
    @ClientString(id = 1388, message = "You have created a party room.")
    public static SystemMessageId YOU_HAVE_CREATED_A_PARTY_ROOM;
    @ClientString(id = 1391, message = "You have exited the party room.")
    public static SystemMessageId YOU_HAVE_EXITED_THE_PARTY_ROOM;
    @ClientString(id = 1392, message = "$c1 has left the party room.")
    public static SystemMessageId C1_HAS_LEFT_THE_PARTY_ROOM;
    @ClientString(id = 1393, message = "You have been ousted from the party room.")
    public static SystemMessageId YOU_HAVE_BEEN_OUSTED_FROM_THE_PARTY_ROOM;
    @ClientString(id = 1394, message = "$c1 has been kicked from the party room.")
    public static SystemMessageId C1_HAS_BEEN_KICKED_FROM_THE_PARTY_ROOM;
    @ClientString(id = 1395, message = "The party room has been disbanded.")
    public static SystemMessageId THE_PARTY_ROOM_HAS_BEEN_DISBANDED;
    @ClientString(id = 1396, message = "The list of party rooms can only be viewed by a person who is not part of a party.")
    public static SystemMessageId THE_LIST_OF_PARTY_ROOMS_CAN_ONLY_BE_VIEWED_BY_A_PERSON_WHO_IS_NOT_PART_OF_A_PARTY;
    @ClientString(id = 1397, message = "The leader of the party room has changed.")
    public static SystemMessageId THE_LEADER_OF_THE_PARTY_ROOM_HAS_CHANGED;
    @ClientString(id = 1401, message = "Slow down, you are already the party leader.")
    public static SystemMessageId SLOW_DOWN_YOU_ARE_ALREADY_THE_PARTY_LEADER;
    @ClientString(id = 1402, message = "You may only transfer party leadership to another member of the party.")
    public static SystemMessageId YOU_MAY_ONLY_TRANSFER_PARTY_LEADERSHIP_TO_ANOTHER_MEMBER_OF_THE_PARTY;
    @ClientString(id = 1405, message = "$s1 CP has been restored.")
    public static SystemMessageId S1_CP_HAS_BEEN_RESTORED;
    @ClientString(id = 1406, message = "$s2 CP has been restored by $c1.")
    public static SystemMessageId S2_CP_HAS_BEEN_RESTORED_BY_C1;
    @ClientString(id = 1413, message = "You do not meet the requirements to enter that party room.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_REQUIREMENTS_TO_ENTER_THAT_PARTY_ROOM;
    @ClientString(id = 1433, message = "The automatic use of $s1 has been activated.")
    public static SystemMessageId THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED;
    @ClientString(id = 1434, message = "The automatic use of $s1 has been deactivated.")
    public static SystemMessageId THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED;
    @ClientString(id = 1439, message = "You do not have all of the items needed to enchant that skill.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ALL_OF_THE_ITEMS_NEEDED_TO_ENCHANT_THAT_SKILL;
    @ClientString(id = 1440, message = "Skill enchant was successful! $s1 has been enchanted.")
    public static SystemMessageId SKILL_ENCHANT_WAS_SUCCESSFUL_S1_HAS_BEEN_ENCHANTED;
    @ClientString(id = 1441, message = "Skill enchant failed. The skill will be initialized.")
    public static SystemMessageId SKILL_ENCHANT_FAILED_THE_SKILL_WILL_BE_INITIALIZED;
    @ClientString(id = 1443, message = "You do not have enough SP to enchant that skill.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SP_TO_ENCHANT_THAT_SKILL;
    @ClientString(id = 1448, message = "Only fishing skills may be used at this time.")
    public static SystemMessageId ONLY_FISHING_SKILLS_MAY_BE_USED_AT_THIS_TIME;
    @ClientString(id = 1452, message = "The bait has been lost because the fish got away.")
    public static SystemMessageId THE_BAIT_HAS_BEEN_LOST_BECAUSE_THE_FISH_GOT_AWAY;
    @ClientString(id = 1453, message = "You do not have a fishing pole equipped.")
    public static SystemMessageId YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED;
    @ClientString(id = 1454, message = "You must put bait on your hook before you can fish.")
    public static SystemMessageId YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH;
    @ClientString(id = 1455, message = "You cannot fish while under water.")
    public static SystemMessageId YOU_CANNOT_FISH_WHILE_UNDER_WATER;
    @ClientString(id = 1456, message = "You cannot fish while riding as a passenger of a boat or transformed.")
    public static SystemMessageId YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT_OR_TRANSFORMED;
    @ClientString(id = 1457, message = "You can't fish here.")
    public static SystemMessageId YOU_CAN_T_FISH_HERE;
    @ClientString(id = 1458, message = "Your attempt at fishing has been cancelled.")
    public static SystemMessageId YOUR_ATTEMPT_AT_FISHING_HAS_BEEN_CANCELLED;
    @ClientString(id = 1460, message = "You reel your line in and stop fishing.")
    public static SystemMessageId YOU_REEL_YOUR_LINE_IN_AND_STOP_FISHING;
    @ClientString(id = 1461, message = "You cast your line and start to fish.")
    public static SystemMessageId YOU_CAST_YOUR_LINE_AND_START_TO_FISH;
    @ClientString(id = 1470, message = "You cannot do that while fishing.")
    public static SystemMessageId YOU_CANNOT_DO_THAT_WHILE_FISHING_SCREEN;
    @ClientString(id = 1472, message = "You look oddly at the fishing pole in disbelief and realize that you can't attack anything with this.")
    public static SystemMessageId YOU_LOOK_ODDLY_AT_THE_FISHING_POLE_IN_DISBELIEF_AND_REALIZE_THAT_YOU_CAN_T_ATTACK_ANYTHING_WITH_THIS;
    @ClientString(id = 1479, message = "That is the wrong grade of soulshot for that fishing pole.")
    public static SystemMessageId THAT_IS_THE_WRONG_GRADE_OF_SOULSHOT_FOR_THAT_FISHING_POLE;
    @ClientString(id = 1491, message = "Failed in trading $s2 of $s1 crops.")
    public static SystemMessageId FAILED_IN_TRADING_S2_OF_S1_CROPS;
    @ClientString(id = 1492, message = "You will be moved to the Olympiad Stadium in $s1 second(s).")
    public static SystemMessageId YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S;
    @ClientString(id = 1493, message = "Your opponent made haste with their tail between their legs; the match has been cancelled.")
    public static SystemMessageId YOUR_OPPONENT_MADE_HASTE_WITH_THEIR_TAIL_BETWEEN_THEIR_LEGS_THE_MATCH_HAS_BEEN_CANCELLED;
    @ClientString(id = 1494, message = "Your opponent does not meet the requirements to do battle; the match has been cancelled.")
    public static SystemMessageId YOUR_OPPONENT_DOES_NOT_MEET_THE_REQUIREMENTS_TO_DO_BATTLE_THE_MATCH_HAS_BEEN_CANCELLED;
    @ClientString(id = 1495, message = "The match will start in $s1 second(s).")
    public static SystemMessageId THE_MATCH_WILL_START_IN_S1_SECOND_S;
    @ClientString(id = 1496, message = "The match has started. Fight!")
    public static SystemMessageId THE_MATCH_HAS_STARTED_FIGHT;
    @ClientString(id = 1497, message = "Congratulations, $c1! You win the match!")
    public static SystemMessageId CONGRATULATIONS_C1_YOU_WIN_THE_MATCH;
    @ClientString(id = 1498, message = "There is no victor; the match ends in a tie.")
    public static SystemMessageId THERE_IS_NO_VICTOR_THE_MATCH_ENDS_IN_A_TIE;
    @ClientString(id = 1499, message = "You will be moved back to town in $s1 second(s).")
    public static SystemMessageId YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECOND_S;
    @ClientString(id = 1501, message = "$c1 does not meet the participation requirements. Only characters that completed the 2nd class transfer can participate in the Olympiad.")
    public static SystemMessageId C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_CHARACTERS_THAT_COMPLETED_THE_2ND_CLASS_TRANSFER_CAN_PARTICIPATE_IN_THE_OLYMPIAD;
    @ClientString(id = 1502, message = "$c1, you have already registered for the match.")
    public static SystemMessageId C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH;
    @ClientString(id = 1503, message = "You have been registered for the Olympiad waiting list for a class battle.")
    public static SystemMessageId YOU_HAVE_BEEN_REGISTERED_FOR_THE_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_BATTLE;
    @ClientString(id = 1504, message = "You've been registered in the waiting list of All-Class Battle.")
    public static SystemMessageId YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE;
    @ClientString(id = 1505, message = "You have been removed from the Olympiad waiting list.")
    public static SystemMessageId YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST;
    @ClientString(id = 1506, message = "You are not currently registered for the Olympiad.")
    public static SystemMessageId YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_OLYMPIAD;
    @ClientString(id = 1507, message = "You cannot equip that item in a Olympiad match.")
    public static SystemMessageId YOU_CANNOT_EQUIP_THAT_ITEM_IN_A_OLYMPIAD_MATCH;
    @ClientString(id = 1508, message = "You cannot use that item in a Olympiad match.")
    public static SystemMessageId YOU_CANNOT_USE_THAT_ITEM_IN_A_OLYMPIAD_MATCH;
    @ClientString(id = 1509, message = "You cannot use that skill in a Olympiad match.")
    public static SystemMessageId YOU_CANNOT_USE_THAT_SKILL_IN_A_OLYMPIAD_MATCH;
    @ClientString(id = 1510, message = "$c1 is attempting to do a resurrection that restores $s2($s3%%) XP. Accept?")
    public static SystemMessageId C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT;
    @ClientString(id = 1511, message = "While a pet is being resurrected, it cannot help in resurrecting its master.")
    public static SystemMessageId WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER;
    @ClientString(id = 1513, message = "Resurrection has already been proposed.")
    public static SystemMessageId RESURRECTION_HAS_ALREADY_BEEN_PROPOSED;
    @ClientString(id = 1515, message = "A pet cannot be resurrected while it's owner is in the process of resurrecting.")
    public static SystemMessageId A_PET_CANNOT_BE_RESURRECTED_WHILE_IT_S_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING;
    @ClientString(id = 1516, message = "The target is unavailable for seeding.")
    public static SystemMessageId THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING;
    @ClientString(id = 1517, message = "The Blessed Enchant failed. The enchant value of the item became 0.")
    public static SystemMessageId THE_BLESSED_ENCHANT_FAILED_THE_ENCHANT_VALUE_OF_THE_ITEM_BECAME_0;
    @ClientString(id = 1518, message = "You do not meet the required condition to equip that item.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_REQUIRED_CONDITION_TO_EQUIP_THAT_ITEM;
    @ClientString(id = 1519, message = "The pet has been killed. If you don't resurrect it within 24 hours, the pet's body will disappear along with all the pet's items.")
    public static SystemMessageId THE_PET_HAS_BEEN_KILLED_IF_YOU_DON_T_RESURRECT_IT_WITHIN_24_HOURS_THE_PET_S_BODY_WILL_DISAPPEAR_ALONG_WITH_ALL_THE_PET_S_ITEMS;
    @ClientString(id = 1520, message = "Your servitor passed away.")
    public static SystemMessageId YOUR_SERVITOR_PASSED_AWAY;
    @ClientString(id = 1521, message = "Your servitor has vanished! You'll need to summon a new one.")
    public static SystemMessageId YOUR_SERVITOR_HAS_VANISHED_YOU_LL_NEED_TO_SUMMON_A_NEW_ONE;
    @ClientString(id = 1523, message = "You should release your servitor so that it does not fall off of the boat and drown!")
    public static SystemMessageId YOU_SHOULD_RELEASE_YOUR_SERVITOR_SO_THAT_IT_DOES_NOT_FALL_OFF_OF_THE_BOAT_AND_DROWN;
    @ClientString(id = 1527, message = "Your pet was hungry so it ate $s1.")
    public static SystemMessageId YOUR_PET_WAS_HUNGRY_SO_IT_ATE_S1;
    @ClientString(id = 1529, message = "$c1 is inviting you to a Command Channel. Do you accept?")
    public static SystemMessageId C1_IS_INVITING_YOU_TO_A_COMMAND_CHANNEL_DO_YOU_ACCEPT;
    @ClientString(id = 1533, message = "Attention: $c1 has picked up $s2.")
    public static SystemMessageId ATTENTION_C1_HAS_PICKED_UP_S2;
    @ClientString(id = 1534, message = "Attention: $c1 has picked up +$s2 $s3.")
    public static SystemMessageId ATTENTION_C1_HAS_PICKED_UP_S2_S3;
    @ClientString(id = 1537, message = "Current Location:  $s1 / $s2 / $s3 (near Rune Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_RUNE_VILLAGE;
    @ClientString(id = 1538, message = "Current Location: $s1 / $s2 / $s3 (near the Town of Goddard)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_GODDARD;
    @ClientString(id = 1561, message = "$s1 has declared a Clan War. The war will automatically start if you kill $s1 clan members 5 times within a week.")
    public static SystemMessageId S1_HAS_DECLARED_A_CLAN_WAR_THE_WAR_WILL_AUTOMATICALLY_START_IF_YOU_KILL_S1_CLAN_MEMBERS_5_TIMES_WITHIN_A_WEEK;
    @ClientString(id = 1562, message = "You have declared a Clan War with $s1.")
    public static SystemMessageId YOU_HAVE_DECLARED_A_CLAN_WAR_WITH_S1;
    @ClientString(id = 1564, message = "A clan war can only be declared if the clan is level 3 or above, and the number of clan members is 15 or greater.")
    public static SystemMessageId A_CLAN_WAR_CAN_ONLY_BE_DECLARED_IF_THE_CLAN_IS_LEVEL_3_OR_ABOVE_AND_THE_NUMBER_OF_CLAN_MEMBERS_IS_15_OR_GREATER;
    @ClientString(id = 1565, message = "A clan war cannot be declared against a clan that does not exist!")
    public static SystemMessageId A_CLAN_WAR_CANNOT_BE_DECLARED_AGAINST_A_CLAN_THAT_DOES_NOT_EXIST;
    @ClientString(id = 1569, message = "A declaration of Clan War against an allied clan can't be made.")
    public static SystemMessageId A_DECLARATION_OF_CLAN_WAR_AGAINST_AN_ALLIED_CLAN_CAN_T_BE_MADE;
    @ClientString(id = 1570, message = "A declaration of war against more than 30 Clans can't be made at the same time.")
    public static SystemMessageId A_DECLARATION_OF_WAR_AGAINST_MORE_THAN_30_CLANS_CAN_T_BE_MADE_AT_THE_SAME_TIME;
    @ClientString(id = 1571, message = "======<Clans You've Declared War On>======")
    public static SystemMessageId CLANS_YOU_VE_DECLARED_WAR_ON;
    @ClientString(id = 1572, message = "======<Clans That Have Declared War On You>======")
    public static SystemMessageId CLANS_THAT_HAVE_DECLARED_WAR_ON_YOU;
    @ClientString(id = 1574, message = "Command Channels can only be formed by a party leader who is also the leader of a level 5 clan.")
    public static SystemMessageId COMMAND_CHANNELS_CAN_ONLY_BE_FORMED_BY_A_PARTY_LEADER_WHO_IS_ALSO_THE_LEADER_OF_A_LEVEL_5_CLAN;
    @ClientString(id = 1575, message = "Your pet uses spiritshot.")
    public static SystemMessageId YOUR_PET_USES_SPIRITSHOT;
    @ClientString(id = 1580, message = "The Command Channel has been formed.")
    public static SystemMessageId THE_COMMAND_CHANNEL_HAS_BEEN_FORMED;
    @ClientString(id = 1581, message = "The Command Channel has been disbanded.")
    public static SystemMessageId THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED;
    @ClientString(id = 1582, message = "You have joined the Command Channel.")
    public static SystemMessageId YOU_HAVE_JOINED_THE_COMMAND_CHANNEL;
    @ClientString(id = 1583, message = "You were dismissed from the Command Channel.")
    public static SystemMessageId YOU_WERE_DISMISSED_FROM_THE_COMMAND_CHANNEL;
    @ClientString(id = 1584, message = "$c1's party has been dismissed from the Command Channel.")
    public static SystemMessageId C1_S_PARTY_HAS_BEEN_DISMISSED_FROM_THE_COMMAND_CHANNEL;
    @ClientString(id = 1586, message = "You have quit the Command Channel.")
    public static SystemMessageId YOU_HAVE_QUIT_THE_COMMAND_CHANNEL;
    @ClientString(id = 1587, message = "$c1's party has left the Command Channel.")
    public static SystemMessageId C1_S_PARTY_HAS_LEFT_THE_COMMAND_CHANNEL;
    @ClientString(id = 1589, message = "Command Channel authority has been transferred to $c1.")
    public static SystemMessageId COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1;
    @ClientString(id = 1593, message = "You do not have authority to invite someone to the Command Channel.")
    public static SystemMessageId YOU_DO_NOT_HAVE_AUTHORITY_TO_INVITE_SOMEONE_TO_THE_COMMAND_CHANNEL;
    @ClientString(id = 1594, message = "$c1's party is already a member of the Command Channel.")
    public static SystemMessageId C1_S_PARTY_IS_ALREADY_A_MEMBER_OF_THE_COMMAND_CHANNEL;
    @ClientString(id = 1598, message = "Soulshots and spiritshots are not available for a dead servitor. Sad, isn't it?")
    public static SystemMessageId SOULSHOTS_AND_SPIRITSHOTS_ARE_NOT_AVAILABLE_FOR_A_DEAD_SERVITOR_SAD_ISN_T_IT;
    @ClientString(id = 1606, message = "Congratulations - You've completed your third-class transfer quest!")
    public static SystemMessageId CONGRATULATIONS_YOU_VE_COMPLETED_YOUR_THIRD_CLASS_TRANSFER_QUEST;
    @ClientString(id = 1610, message = "Fool! You cannot declare war against your own clan!")
    public static SystemMessageId FOOL_YOU_CANNOT_DECLARE_WAR_AGAINST_YOUR_OWN_CLAN;
    @ClientString(id = 1612, message = "=====<Clan War List>=====")
    public static SystemMessageId CLAN_WAR_LIST;
    @ClientString(id = 1638, message = "You cannot fish while using a recipe book, private workshop or private store.")
    public static SystemMessageId YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_WORKSHOP_OR_PRIVATE_STORE;
    @ClientString(id = 1639, message = "Round $s1 of the Olympiad Games has started!")
    public static SystemMessageId ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_STARTED;
    @ClientString(id = 1640, message = "Round $s1 of the Olympiad Games has now ended.")
    public static SystemMessageId ROUND_S1_OF_THE_OLYMPIAD_GAMES_HAS_NOW_ENDED;
    @ClientString(id = 1641, message = "Sharpen your swords, tighten the stitching in your armor, and make haste to a Olympiad Manager!  Battles in the Olympiad Games are now taking place!")
    public static SystemMessageId SHARPEN_YOUR_SWORDS_TIGHTEN_THE_STITCHING_IN_YOUR_ARMOR_AND_MAKE_HASTE_TO_A_OLYMPIAD_MANAGER_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_TAKING_PLACE;
    @ClientString(id = 1642, message = "Much carnage has been left for the cleanup crew of the Olympiad Stadium.  Battles in the Olympiad Games are now over!")
    public static SystemMessageId MUCH_CARNAGE_HAS_BEEN_LEFT_FOR_THE_CLEANUP_CREW_OF_THE_OLYMPIAD_STADIUM_BATTLES_IN_THE_OLYMPIAD_GAMES_ARE_NOW_OVER;
    @ClientString(id = 1643, message = "Current Location: $s1 / $s2 / $s3 (Dimensional Gap)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_DIMENSIONAL_GAP;
    @ClientString(id = 1651, message = "The Olympiad Games are not currently in progress.")
    public static SystemMessageId THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS;
    @ClientString(id = 1657, message = "$c1 has earned $s2 points in the Olympiad Games.")
    public static SystemMessageId C1_HAS_EARNED_S2_POINTS_IN_THE_OLYMPIAD_GAMES;
    @ClientString(id = 1658, message = "$c1 has lost $s2 points in the Olympiad Games.")
    public static SystemMessageId C1_HAS_LOST_S2_POINTS_IN_THE_OLYMPIAD_GAMES;
    @ClientString(id = 1659, message = "Current Location: $s1 / $s2 / $s3 (Cemetery of the Empire).")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_CEMETERY_OF_THE_EMPIRE;
    @ClientString(id = 1663, message = "The Clan Mark was successfully registered.  The symbol will appear on the clan flag, and the insignia is only displayed on items pertaining to a clan that owns a clan hall or castle.")
    public static SystemMessageId THE_CLAN_MARK_WAS_SUCCESSFULLY_REGISTERED_THE_SYMBOL_WILL_APPEAR_ON_THE_CLAN_FLAG_AND_THE_INSIGNIA_IS_ONLY_DISPLAYED_ON_ITEMS_PERTAINING_TO_A_CLAN_THAT_OWNS_A_CLAN_HALL_OR_CASTLE;
    @ClientString(id = 1667, message = "Lethal Strike!")
    public static SystemMessageId LETHAL_STRIKE;
    @ClientString(id = 1668, message = "Hit with Lethal Strike!")
    public static SystemMessageId HIT_WITH_LETHAL_STRIKE;
    @ClientString(id = 1669, message = "There was nothing found inside.")
    public static SystemMessageId THERE_WAS_NOTHING_FOUND_INSIDE;
    @ClientString(id = 1673, message = "For the current Olympiad you have participated in $s1 match(es) and had $s2 win(s) and $s3 defeat(s). You currently have $s4 Olympiad Point(s).")
    public static SystemMessageId FOR_THE_CURRENT_OLYMPIAD_YOU_HAVE_PARTICIPATED_IN_S1_MATCH_ES_AND_HAD_S2_WIN_S_AND_S3_DEFEAT_S_YOU_CURRENTLY_HAVE_S4_OLYMPIAD_POINT_S;
    @ClientString(id = 1674, message = "This command is available only when the target has completed the 2nd class transfer.")
    public static SystemMessageId THIS_COMMAND_IS_AVAILABLE_ONLY_WHEN_THE_TARGET_HAS_COMPLETED_THE_2ND_CLASS_TRANSFER;
    @ClientString(id = 1675, message = "A manor cannot be set up between 4:30 am and 8 pm.")
    public static SystemMessageId A_MANOR_CANNOT_BE_SET_UP_BETWEEN_4_30_AM_AND_8_PM;
    @ClientString(id = 1676, message = "You do not have a servitor and therefore cannot use the automatic-use function.")
    public static SystemMessageId YOU_DO_NOT_HAVE_A_SERVITOR_AND_THEREFORE_CANNOT_USE_THE_AUTOMATIC_USE_FUNCTION;
    @ClientString(id = 1677, message = "A cease-fire during a Clan War can not be called while members of your clan are engaged in battle.")
    public static SystemMessageId A_CEASE_FIRE_DURING_A_CLAN_WAR_CAN_NOT_BE_CALLED_WHILE_MEMBERS_OF_YOUR_CLAN_ARE_ENGAGED_IN_BATTLE;
    @ClientString(id = 1678, message = "You have not declared a Clan War against the clan $s1.")
    public static SystemMessageId YOU_HAVE_NOT_DECLARED_A_CLAN_WAR_AGAINST_THE_CLAN_S1;
    @ClientString(id = 1683, message = "Only a party leader can leave a command channel.")
    public static SystemMessageId ONLY_A_PARTY_LEADER_CAN_LEAVE_A_COMMAND_CHANNEL;
    @ClientString(id = 1684, message = "A Clan War can not be declared against a clan that is being dissolved.")
    public static SystemMessageId A_CLAN_WAR_CAN_NOT_BE_DECLARED_AGAINST_A_CLAN_THAT_IS_BEING_DISSOLVED;
    @ClientString(id = 1687, message = "This area cannot be entered while mounted atop of a Wyvern.  You will be dismounted from your Wyvern if you do not leave!")
    public static SystemMessageId THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN_YOU_WILL_BE_DISMOUNTED_FROM_YOUR_WYVERN_IF_YOU_DO_NOT_LEAVE;
    @ClientString(id = 1688, message = "You cannot enchant while operating a Private Store or Private Workshop.")
    public static SystemMessageId YOU_CANNOT_ENCHANT_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
    @ClientString(id = 1689, message = "$c1 is already registered on the class match waiting list.")
    public static SystemMessageId C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST;
    @ClientString(id = 1690, message = "$c1 is already registered on the waiting list for the All-Class Battle.")
    public static SystemMessageId C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE;
    @ClientString(id = 1691, message = "$c1 does not meet the participation requirements for Olympiad as the inventory weight/slot exceeds 80%%.")
    public static SystemMessageId C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_FOR_OLYMPIAD_AS_THE_INVENTORY_WEIGHT_SLOT_EXCEEDS_80;
    @ClientString(id = 1692, message = "$c1 does not meet the participation requirements. You cannot participate in the Olympiad because you have changed your class to subclass.")
    public static SystemMessageId C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_YOU_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD_BECAUSE_YOU_HAVE_CHANGED_YOUR_CLASS_TO_SUBCLASS;
    @ClientString(id = 1693, message = "You may not observe a Olympiad Games match while you are on the waiting list.")
    public static SystemMessageId YOU_MAY_NOT_OBSERVE_A_OLYMPIAD_GAMES_MATCH_WHILE_YOU_ARE_ON_THE_WAITING_LIST;
    @ClientString(id = 1694, message = "Only a clan leader that is a Noblesse or Exalted can view the Siege Status window during a siege war.")
    public static SystemMessageId ONLY_A_CLAN_LEADER_THAT_IS_A_NOBLESSE_OR_EXALTED_CAN_VIEW_THE_SIEGE_STATUS_WINDOW_DURING_A_SIEGE_WAR;
    @ClientString(id = 1699, message = "You cannot dismiss a party member by force.")
    public static SystemMessageId YOU_CANNOT_DISMISS_A_PARTY_MEMBER_BY_FORCE;
    @ClientString(id = 1700, message = "You don't have enough spiritshots needed for a servitor.")
    public static SystemMessageId YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
    @ClientString(id = 1701, message = "You don't have enough soulshots needed for a servitor.")
    public static SystemMessageId YOU_DON_T_HAVE_ENOUGH_SOULSHOTS_NEEDED_FOR_A_SERVITOR;
    @ClientString(id = 1704, message = "Please close the  setup window for your private workshop or private store, and try again.")
    public static SystemMessageId PLEASE_CLOSE_THE_SETUP_WINDOW_FOR_YOUR_PRIVATE_WORKSHOP_OR_PRIVATE_STORE_AND_TRY_AGAIN;
    @ClientString(id = 1708, message = "Double points! You earned $s1 PA Point(s).")
    public static SystemMessageId DOUBLE_POINTS_YOU_EARNED_S1_PA_POINT_S;
    @ClientString(id = 1710, message = "You are short of PA Points.")
    public static SystemMessageId YOU_ARE_SHORT_OF_PA_POINTS;
    @ClientString(id = 1713, message = "The games may be delayed due to an insufficient number of players waiting.")
    public static SystemMessageId THE_GAMES_MAY_BE_DELAYED_DUE_TO_AN_INSUFFICIENT_NUMBER_OF_PLAYERS_WAITING;
    @ClientString(id = 1714, message = "Current Location: $s1 / $s2 / $s3 (Near the Town of Schuttgart)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_TOWN_OF_SCHUTTGART;
    @ClientString(id = 1728, message = "The recipient of your invitation did not accept the party matching invitation.")
    public static SystemMessageId THE_RECIPIENT_OF_YOUR_INVITATION_DID_NOT_ACCEPT_THE_PARTY_MATCHING_INVITATION;
    @ClientString(id = 1730, message = "To establish a Clan Academy, your clan must be Level 5 or higher.")
    public static SystemMessageId TO_ESTABLISH_A_CLAN_ACADEMY_YOUR_CLAN_MUST_BE_LEVEL_5_OR_HIGHER;
    @ClientString(id = 1734, message = "In order to join the clan academy, you must be unaffiliated with a clan and be an unawakened character Lv. 84 or below fpr both main and subclass.")
    public static SystemMessageId IN_ORDER_TO_JOIN_THE_CLAN_ACADEMY_YOU_MUST_BE_UNAFFILIATED_WITH_A_CLAN_AND_BE_AN_UNAWAKENED_CHARACTER_LV_84_OR_BELOW_FPR_BOTH_MAIN_AND_SUBCLASS;
    @ClientString(id = 1735, message = "$s1 does not meet the requirements to join a Clan Academy.")
    public static SystemMessageId S1_DOES_NOT_MEET_THE_REQUIREMENTS_TO_JOIN_A_CLAN_ACADEMY;
    @ClientString(id = 1724, message = "A servitor whom is engaged in battle cannot be de-activated.")
    public static SystemMessageId A_SERVITOR_WHOM_IS_ENGAGED_IN_BATTLE_CANNOT_BE_DE_ACTIVATED;
    @ClientString(id = 1725, message = "You have earned $s1 raid point(s).")
    public static SystemMessageId YOU_HAVE_EARNED_S1_RAID_POINT_S;
    @ClientString(id = 1738, message = "Your clan has already established a Clan Academy.")
    public static SystemMessageId YOUR_CLAN_HAS_ALREADY_ESTABLISHED_A_CLAN_ACADEMY;
    @ClientString(id = 1741, message = "Congratulations! The $s1's Clan Academy has been created.")
    public static SystemMessageId CONGRATULATIONS_THE_S1_S_CLAN_ACADEMY_HAS_BEEN_CREATED;
    @ClientString(id = 1749, message = "Congratulations! You will now graduate from the Clan Academy and leave your current clan. You can now join a clan without being subject to any penalties.")
    public static SystemMessageId CONGRATULATIONS_YOU_WILL_NOW_GRADUATE_FROM_THE_CLAN_ACADEMY_AND_LEAVE_YOUR_CURRENT_CLAN_YOU_CAN_NOW_JOIN_A_CLAN_WITHOUT_BEING_SUBJECT_TO_ANY_PENALTIES;
    @ClientString(id = 1750, message = "$c1 does not meet the participation requirements. The owner of $s2 cannot participate in the Olympiad.")
    public static SystemMessageId C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_THE_OWNER_OF_S2_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD;
    @ClientString(id = 1754, message = "That privilege cannot be granted to a Clan Academy member.")
    public static SystemMessageId THAT_PRIVILEGE_CANNOT_BE_GRANTED_TO_A_CLAN_ACADEMY_MEMBER;
    @ClientString(id = 1755, message = "$s2 has been designated as the apprentice of clan member $s1.")
    public static SystemMessageId S2_HAS_BEEN_DESIGNATED_AS_THE_APPRENTICE_OF_CLAN_MEMBER_S1;
    @ClientString(id = 1756, message = "Your apprentice, $s1, has logged in.")
    public static SystemMessageId YOUR_APPRENTICE_S1_HAS_LOGGED_IN;
    @ClientString(id = 1758, message = "Your sponsor, $c1 , has logged in.")
    public static SystemMessageId YOUR_SPONSOR_C1_HAS_LOGGED_IN;
    @ClientString(id = 1761, message = "Clan member $c1's privilege level has been changed to $s2.")
    public static SystemMessageId CLAN_MEMBER_C1_S_PRIVILEGE_LEVEL_HAS_BEEN_CHANGED_TO_S2;
    @ClientString(id = 1762, message = "You do not have the right to dismiss an apprentice.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_RIGHT_TO_DISMISS_AN_APPRENTICE;
    @ClientString(id = 1763, message = "$s2, clan member $c1's apprentice, has been removed.")
    public static SystemMessageId S2_CLAN_MEMBER_C1_S_APPRENTICE_HAS_BEEN_REMOVED;
    @ClientString(id = 1771, message = "Now that your clan level is above Level 5, it can accumulate Clan Reputation.")
    public static SystemMessageId NOW_THAT_YOUR_CLAN_LEVEL_IS_ABOVE_LEVEL_5_IT_CAN_ACCUMULATE_CLAN_REPUTATION;
    @ClientString(id = 1776, message = "Clan member $c1 was named a hero. $s2 points have been added to your Clan Reputation.")
    public static SystemMessageId CLAN_MEMBER_C1_WAS_NAMED_A_HERO_S2_POINTS_HAVE_BEEN_ADDED_TO_YOUR_CLAN_REPUTATION;
    @ClientString(id = 1781, message = "Your clan has added $s1 point(s) to its Clan Reputation.")
    public static SystemMessageId YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION;
    @ClientString(id = 1787, message = "$s1 point(s) have been deducted from the clan's Reputation.")
    public static SystemMessageId S1_POINT_S_HAVE_BEEN_DEDUCTED_FROM_THE_CLAN_S_REPUTATION;
    @ClientString(id = 1788, message = "The clan skill $s1 has been added.")
    public static SystemMessageId THE_CLAN_SKILL_S1_HAS_BEEN_ADDED;
    @ClientString(id = 1789, message = "Since the Clan Reputation has dropped below 0, your clan skill(s) will be de-activated.")
    public static SystemMessageId SINCE_THE_CLAN_REPUTATION_HAS_DROPPED_BELOW_0_YOUR_CLAN_SKILL_S_WILL_BE_DE_ACTIVATED;
    @ClientString(id = 1790, message = "The conditions necessary to increase the clan's level have not been met.")
    public static SystemMessageId THE_CONDITIONS_NECESSARY_TO_INCREASE_THE_CLAN_S_LEVEL_HAVE_NOT_BEEN_MET;
    @ClientString(id = 1791, message = "The conditions necessary to create a military unit have not been met.")
    public static SystemMessageId THE_CONDITIONS_NECESSARY_TO_CREATE_A_MILITARY_UNIT_HAVE_NOT_BEEN_MET;
    @ClientString(id = 1793, message = "$c1 has been selected as the captain of $s2.")
    public static SystemMessageId C1_HAS_BEEN_SELECTED_AS_THE_CAPTAIN_OF_S2;
    @ClientString(id = 1794, message = "The Knights of $s1 have been created.")
    public static SystemMessageId THE_KNIGHTS_OF_S1_HAVE_BEEN_CREATED;
    @ClientString(id = 1795, message = "The Royal Guard of $s1 have been created.")
    public static SystemMessageId THE_ROYAL_GUARD_OF_S1_HAVE_BEEN_CREATED;
    @ClientString(id = 1798, message = "Clan Leader privileges have been transferred to $c1.")
    public static SystemMessageId CLAN_LEADER_PRIVILEGES_HAVE_BEEN_TRANSFERRED_TO_C1;
    @ClientString(id = 1803, message = "Participation requests are no longer being accepted.")
    public static SystemMessageId PARTICIPATION_REQUESTS_ARE_NO_LONGER_BEING_ACCEPTED;
    @ClientString(id = 1814, message = "$s1 has $s2 minute(s) of usage time remaining.")
    public static SystemMessageId S1_HAS_S2_MINUTE_S_OF_USAGE_TIME_REMAINING;
    @ClientString(id = 1815, message = "$s2 has dropped in $s1. The quantity of Adena stacked in the $s2 treasure chest is$s3 in fixed quantity and $s4 in variable quantity. The owner at 23:59 will finally own this Adena.")
    public static SystemMessageId S2_HAS_DROPPED_IN_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA;
    @ClientString(id = 1816, message = "$s2's owner has appeared in $s1. The quantity of Adena stacked in the $s2 treasure chest is$s3 in fixed quantity and $s4 in variable quantity. The owner at 23:59 will finally own this Adena.")
    public static SystemMessageId S2_S_OWNER_HAS_APPEARED_IN_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA;
    @ClientString(id = 1817, message = "$s2's owner has logged into $s1. The quantity of Adena stacked in the $s2 treasure chest is$s3 in fixed quantity and $s4 in variable quantity. The owner at 23:59 will finally own this Adena.")
    public static SystemMessageId S2_S_OWNER_HAS_LOGGED_INTO_S1_THE_QUANTITY_OF_ADENA_STACKED_IN_THE_S2_TREASURE_CHEST_IS_S3_IN_FIXED_QUANTITY_AND_S4_IN_VARIABLE_QUANTITY_THE_OWNER_AT_23_59_WILL_FINALLY_OWN_THIS_ADENA;
    @ClientString(id = 1818, message = "$s1 has disappeared.")
    public static SystemMessageId S1_HAS_DISAPPEARED;
    @ClientString(id = 1835, message = "$s1 is full and cannot accept additional clan members at this time.")
    public static SystemMessageId S1_IS_FULL_AND_CANNOT_ACCEPT_ADDITIONAL_CLAN_MEMBERS_AT_THIS_TIME;
    @ClientString(id = 1842, message = "$c1 wishes to summon you from $s2. Do you accept?")
    public static SystemMessageId C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT;
    @ClientString(id = 1843, message = "$c1 is engaged in combat and cannot be summoned or teleported.")
    public static SystemMessageId C1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED;
    @ClientString(id = 1844, message = "$c1 is dead at the moment and cannot be summoned or teleported.")
    public static SystemMessageId C1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED;
    @ClientString(id = 1845, message = "Hero weapons cannot be destroyed.")
    public static SystemMessageId HERO_WEAPONS_CANNOT_BE_DESTROYED;
    @ClientString(id = 1846, message = "You are too far away from your mount to ride.")
    public static SystemMessageId YOU_ARE_TOO_FAR_AWAY_FROM_YOUR_MOUNT_TO_RIDE;
    @ClientString(id = 1850, message = "The Captain of the Order of Knights cannot be appointed.")
    public static SystemMessageId THE_CAPTAIN_OF_THE_ORDER_OF_KNIGHTS_CANNOT_BE_APPOINTED;
    @ClientString(id = 1851, message = "The Royal Guard Captain cannot be appointed.")
    public static SystemMessageId THE_ROYAL_GUARD_CAPTAIN_CANNOT_BE_APPOINTED;
    @ClientString(id = 1852, message = "The attempt to acquire the skill has failed because of an insufficient Clan Reputation.")
    public static SystemMessageId THE_ATTEMPT_TO_ACQUIRE_THE_SKILL_HAS_FAILED_BECAUSE_OF_AN_INSUFFICIENT_CLAN_REPUTATION;
    @ClientString(id = 1855, message = "Another military unit is already using that name. Please enter a different name.")
    public static SystemMessageId ANOTHER_MILITARY_UNIT_IS_ALREADY_USING_THAT_NAME_PLEASE_ENTER_A_DIFFERENT_NAME;
    @ClientString(id = 1858, message = "$c1 is currently dead and cannot participate in the Olympiad.")
    public static SystemMessageId C1_IS_CURRENTLY_DEAD_AND_CANNOT_PARTICIPATE_IN_THE_OLYMPIAD;
    @ClientString(id = 1860, message = "The Clan Reputation is too low.")
    public static SystemMessageId THE_CLAN_REPUTATION_IS_TOO_LOW;
    @ClientString(id = 1861, message = "The Clan Mark has been deleted.")
    public static SystemMessageId THE_CLAN_MARK_HAS_BEEN_DELETED;
    @ClientString(id = 1862, message = "Clan skills will now be activated since the Clan Reputation is 1 or higher.")
    public static SystemMessageId CLAN_SKILLS_WILL_NOW_BE_ACTIVATED_SINCE_THE_CLAN_REPUTATION_IS_1_OR_HIGHER;
    @ClientString(id = 1864, message = "Your servitor is unresponsive and will not obey any orders.")
    public static SystemMessageId YOUR_SERVITOR_IS_UNRESPONSIVE_AND_WILL_NOT_OBEY_ANY_ORDERS;
    @ClientString(id = 1867, message = "Your opponent's MP was reduced by $s1.")
    public static SystemMessageId YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1;
    @ClientString(id = 1878, message = "The game will end in $s1 second(s).")
    public static SystemMessageId THE_GAME_WILL_END_IN_S1_SECOND_S;
    @ClientString(id = 1883, message = "There are no offerings I own or I made a bid for.")
    public static SystemMessageId THERE_ARE_NO_OFFERINGS_I_OWN_OR_I_MADE_A_BID_FOR;
    @ClientString(id = 1894, message = "A subclass cannot be created or changed while you are over your weight limit.")
    public static SystemMessageId A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_WHILE_YOU_ARE_OVER_YOUR_WEIGHT_LIMIT;
    @ClientString(id = 1895, message = "$c1 is in an area which blocks summoning or teleporting.")
    public static SystemMessageId C1_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING_OR_TELEPORTING;
    @ClientString(id = 1897, message = "$s1 is required for summoning.")
    public static SystemMessageId S1_IS_REQUIRED_FOR_SUMMONING;
    @ClientString(id = 1898, message = "$c1 is currently trading or operating a private store and cannot be summoned or teleported.")
    public static SystemMessageId C1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED_OR_TELEPORTED;
    @ClientString(id = 1899, message = "You cannot use summoning or teleporting in this area.")
    public static SystemMessageId YOU_CANNOT_USE_SUMMONING_OR_TELEPORTING_IN_THIS_AREA;
    @ClientString(id = 1900, message = "$c1 has entered the party room.")
    public static SystemMessageId C1_HAS_ENTERED_THE_PARTY_ROOM;
    @ClientString(id = 1911, message = "A user participating in the Olympiad cannot use summoning or teleporting.")
    public static SystemMessageId A_USER_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_USE_SUMMONING_OR_TELEPORTING;
    @ClientString(id = 1918, message = "Your pet is too high level to control.")
    public static SystemMessageId YOUR_PET_IS_TOO_HIGH_LEVEL_TO_CONTROL;
    @ClientString(id = 1919, message = "The Olympiad registration period has ended.")
    public static SystemMessageId THE_OLYMPIAD_REGISTRATION_PERIOD_HAS_ENDED;
    @ClientString(id = 1926, message = "There is no opponent to receive your challenge for a duel.")
    public static SystemMessageId THERE_IS_NO_OPPONENT_TO_RECEIVE_YOUR_CHALLENGE_FOR_A_DUEL;
    @ClientString(id = 1927, message = "$c1 has been challenged to a duel.")
    public static SystemMessageId C1_HAS_BEEN_CHALLENGED_TO_A_DUEL;
    @ClientString(id = 1928, message = "$c1's party has been challenged to a duel.")
    public static SystemMessageId C1_S_PARTY_HAS_BEEN_CHALLENGED_TO_A_DUEL;
    @ClientString(id = 1929, message = "$c1 has accepted your challenge to a duel. The duel will begin in a few moments.")
    public static SystemMessageId C1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS;
    @ClientString(id = 1930, message = "You have accepted $c1's challenge a duel. The duel will begin in a few moments.")
    public static SystemMessageId YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_A_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS;
    @ClientString(id = 1931, message = "$c1 has declined your challenge to a duel.")
    public static SystemMessageId C1_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL;
    @ClientString(id = 1933, message = "You have accepted $c1's challenge to a party duel. The duel will begin in a few moments.")
    public static SystemMessageId YOU_HAVE_ACCEPTED_C1_S_CHALLENGE_TO_A_PARTY_DUEL_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS;
    @ClientString(id = 1934, message = "$s1 has accepted your challenge to duel against their party. The duel will begin in a few moments.")
    public static SystemMessageId S1_HAS_ACCEPTED_YOUR_CHALLENGE_TO_DUEL_AGAINST_THEIR_PARTY_THE_DUEL_WILL_BEGIN_IN_A_FEW_MOMENTS;
    @ClientString(id = 1936, message = "The opposing party has declined your challenge to a duel.")
    public static SystemMessageId THE_OPPOSING_PARTY_HAS_DECLINED_YOUR_CHALLENGE_TO_A_DUEL;
    @ClientString(id = 1937, message = "Since the person you challenged is not currently in a party, they cannot duel against your party.")
    public static SystemMessageId SINCE_THE_PERSON_YOU_CHALLENGED_IS_NOT_CURRENTLY_IN_A_PARTY_THEY_CANNOT_DUEL_AGAINST_YOUR_PARTY;
    @ClientString(id = 1938, message = "$c1 has challenged you to a duel.")
    public static SystemMessageId C1_HAS_CHALLENGED_YOU_TO_A_DUEL;
    @ClientString(id = 1939, message = "$c1's party has challenged your party to a duel.")
    public static SystemMessageId C1_S_PARTY_HAS_CHALLENGED_YOUR_PARTY_TO_A_DUEL;
    @ClientString(id = 1940, message = "You are unable to request a duel at this time.")
    public static SystemMessageId YOU_ARE_UNABLE_TO_REQUEST_A_DUEL_AT_THIS_TIME;
    @ClientString(id = 1942, message = "The opposing party is currently unable to accept a challenge to a duel.")
    public static SystemMessageId THE_OPPOSING_PARTY_IS_CURRENTLY_UNABLE_TO_ACCEPT_A_CHALLENGE_TO_A_DUEL;
    @ClientString(id = 1944, message = "In a moment, you will be transported to the site where the duel will take place.")
    public static SystemMessageId IN_A_MOMENT_YOU_WILL_BE_TRANSPORTED_TO_THE_SITE_WHERE_THE_DUEL_WILL_TAKE_PLACE;
    @ClientString(id = 1945, message = "The duel will begin in $s1 second(s).")
    public static SystemMessageId THE_DUEL_WILL_BEGIN_IN_S1_SECOND_S;
    @ClientString(id = 1949, message = "Let the duel begin!")
    public static SystemMessageId LET_THE_DUEL_BEGIN;
    @ClientString(id = 1950, message = "$c1 has won the duel.")
    public static SystemMessageId C1_HAS_WON_THE_DUEL;
    @ClientString(id = 1951, message = "$c1's party has won the duel.")
    public static SystemMessageId C1_S_PARTY_HAS_WON_THE_DUEL;
    @ClientString(id = 1952, message = "The duel has ended in a tie.")
    public static SystemMessageId THE_DUEL_HAS_ENDED_IN_A_TIE;
    @ClientString(id = 1960, message = "This is not a suitable item.")
    public static SystemMessageId THIS_IS_NOT_A_SUITABLE_ITEM;
    @ClientString(id = 1961, message = "Gemstone quantity is incorrect.")
    public static SystemMessageId GEMSTONE_QUANTITY_IS_INCORRECT;
    @ClientString(id = 1964, message = "Augmentation removal can only be done on an augmented item.")
    public static SystemMessageId AUGMENTATION_REMOVAL_CAN_ONLY_BE_DONE_ON_AN_AUGMENTED_ITEM;
    @ClientString(id = 1966, message = "Only the clan leader may issue commands.")
    public static SystemMessageId ONLY_THE_CLAN_LEADER_MAY_ISSUE_COMMANDS;
    @ClientString(id = 1970, message = "Once an item is augmented, it cannot be augmented again.")
    public static SystemMessageId ONCE_AN_ITEM_IS_AUGMENTED_IT_CANNOT_BE_AUGMENTED_AGAIN;
    @ClientString(id = 1972, message = "You cannot augment items while a private store or private workshop is in operation.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP_IS_IN_OPERATION;
    @ClientString(id = 1974, message = "You cannot augment items while dead.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_DEAD;
    @ClientString(id = 1975, message = "You cannot augment items while engaged in trade activities.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_ENGAGED_IN_TRADE_ACTIVITIES;
    @ClientString(id = 1976, message = "You cannot augment items while paralyzed.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_PARALYZED;
    @ClientString(id = 1977, message = "You cannot augment items while fishing.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_FISHING;
    @ClientString(id = 1978, message = "You cannot augment items while sitting down.")
    public static SystemMessageId YOU_CANNOT_AUGMENT_ITEMS_WHILE_SITTING_DOWN;
    @ClientString(id = 1979, message = "$s1's remaining Mana is now 10.")
    public static SystemMessageId S1_S_REMAINING_MANA_IS_NOW_10;
    @ClientString(id = 1980, message = "$s1's remaining Mana is now 5.")
    public static SystemMessageId S1_S_REMAINING_MANA_IS_NOW_5;
    @ClientString(id = 1981, message = "$s1's remaining Mana is now 1. It will disappear soon.")
    public static SystemMessageId S1_S_REMAINING_MANA_IS_NOW_1_IT_WILL_DISAPPEAR_SOON;
    @ClientString(id = 1982, message = "$s1's remaining Mana is now 0, and the item has disappeared.")
    public static SystemMessageId S1_S_REMAINING_MANA_IS_NOW_0_AND_THE_ITEM_HAS_DISAPPEARED;
    @ClientString(id = 1983, message = "$s1")
    public static SystemMessageId S1;
    @ClientString(id = 1996, message = "The attack has been blocked.")
    public static SystemMessageId THE_ATTACK_HAS_BEEN_BLOCKED;
    @ClientString(id = 1997, message = "$c1 is performing a counterattack.")
    public static SystemMessageId C1_IS_PERFORMING_A_COUNTERATTACK;
    @ClientString(id = 1998, message = "You countered $c1's attack.")
    public static SystemMessageId YOU_COUNTERED_C1_S_ATTACK;
    @ClientString(id = 1999, message = "$c1 dodged the attack.")
    public static SystemMessageId C1_DODGED_THE_ATTACK;
    @ClientString(id = 2000, message = "You have dodged $c1's attack.")
    public static SystemMessageId YOU_HAVE_DODGED_C1_S_ATTACK;
    @ClientString(id = 2001, message = "Augmentation failed due to inappropriate conditions.")
    public static SystemMessageId AUGMENTATION_FAILED_DUE_TO_INAPPROPRIATE_CONDITIONS;
    @ClientString(id = 2010, message = "$s2 $s1")
    public static SystemMessageId S2_S1;
    @ClientString(id = 2012, message = "$s1 has been activated.")
    public static SystemMessageId S1_HAS_BEEN_ACTIVATED;
    @ClientString(id = 2015, message = "A skill is ready to be used again.")
    public static SystemMessageId A_SKILL_IS_READY_TO_BE_USED_AGAIN;
    @ClientString(id = 2017, message = "$c1 cannot duel because $c1 is currently engaged in a private store or manufacture.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_A_PRIVATE_STORE_OR_MANUFACTURE;
    @ClientString(id = 2018, message = "$c1 cannot duel because $c1 is currently fishing.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_FISHING;
    @ClientString(id = 2019, message = "$c1's HP or MP is below 50%% and cannot duel.")
    public static SystemMessageId C1_S_HP_OR_MP_IS_BELOW_50_AND_CANNOT_DUEL;
    @ClientString(id = 2020, message = "$c1 is in an area where duel is not allowed and you cannot apply for a duel.")
    public static SystemMessageId C1_IS_IN_AN_AREA_WHERE_DUEL_IS_NOT_ALLOWED_AND_YOU_CANNOT_APPLY_FOR_A_DUEL;
    @ClientString(id = 2021, message = "$c1 cannot duel because $c1 is currently engaged in battle.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_ENGAGED_IN_BATTLE;
    @ClientString(id = 2022, message = "$c1 cannot duel because $c1 is already engaged in a duel.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_ALREADY_ENGAGED_IN_A_DUEL;
    @ClientString(id = 2023, message = "$c1 cannot duel because $c1 is in a chaotic or purple state.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_IN_A_CHAOTIC_OR_PURPLE_STATE;
    @ClientString(id = 2024, message = "$c1 cannot duel because $c1 is participating in the Olympiad or the Ceremony of Chaos.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_OR_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 2027, message = "$c1 cannot duel because $c1 is currently riding a boat, fenrir, or strider.")
    public static SystemMessageId C1_CANNOT_DUEL_BECAUSE_C1_IS_CURRENTLY_RIDING_A_BOAT_FENRIR_OR_STRIDER;
    @ClientString(id = 2028, message = "$c1 is too far away to receive a duel challenge.")
    public static SystemMessageId C1_IS_TOO_FAR_AWAY_TO_RECEIVE_A_DUEL_CHALLENGE;
    @ClientString(id = 2033, message = "A subclass cannot be created or changed because you have exceeded your inventory limit.")
    public static SystemMessageId A_SUBCLASS_CANNOT_BE_CREATED_OR_CHANGED_BECAUSE_YOU_HAVE_EXCEEDED_YOUR_INVENTORY_LIMIT;
    @ClientString(id = 2040, message = "Cannot trade items with the targeted user.")
    public static SystemMessageId CANNOT_TRADE_ITEMS_WITH_THE_TARGETED_USER;
    @ClientString(id = 2050, message = "$s1 clan is trying to display a flag.")
    public static SystemMessageId S1_CLAN_IS_TRYING_TO_DISPLAY_A_FLAG;
    @ClientString(id = 2057, message = "You have blocked $c1.")
    public static SystemMessageId YOU_HAVE_BLOCKED_C1;
    @ClientString(id = 2058, message = "You already polymorphed and cannot polymorph again.")
    public static SystemMessageId YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN;
    @ClientString(id = 2060, message = "You cannot polymorph into the desired form in water.")
    public static SystemMessageId YOU_CANNOT_POLYMORPH_INTO_THE_DESIRED_FORM_IN_WATER;
    @ClientString(id = 2063, message = "You cannot transform while riding a pet.")
    public static SystemMessageId YOU_CANNOT_TRANSFORM_WHILE_RIDING_A_PET;
    @ClientString(id = 2065, message = "That item cannot be taken off.")
    public static SystemMessageId THAT_ITEM_CANNOT_BE_TAKEN_OFF;
    @ClientString(id = 2066, message = "That weapon cannot perform any attacks.")
    public static SystemMessageId THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS;
    @ClientString(id = 2067, message = "That weapon cannot use any other skill except the weapon's skill.")
    public static SystemMessageId THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPON_S_SKILL;
    @ClientString(id = 2073, message = "Enchant skill route change was successful. Lv of enchant skill $s1 will remain.")
    public static SystemMessageId ENCHANT_SKILL_ROUTE_CHANGE_WAS_SUCCESSFUL_LV_OF_ENCHANT_SKILL_S1_WILL_REMAIN;
    @ClientString(id = 2074, message = "Skill enchant failed. Current level of enchant skill $s1 will remain unchanged.")
    public static SystemMessageId SKILL_ENCHANT_FAILED_CURRENT_LEVEL_OF_ENCHANT_SKILL_S1_WILL_REMAIN_UNCHANGED;
    @ClientString(id = 2075, message = "It is not an auction period.")
    public static SystemMessageId IT_IS_NOT_AN_AUCTION_PERIOD;
    @ClientString(id = 2076, message = "The highest bid is over 999.9 billion, therefore you cannot place a bid.")
    public static SystemMessageId THE_HIGHEST_BID_IS_OVER_999_9_BILLION_THEREFORE_YOU_CANNOT_PLACE_A_BID;
    @ClientString(id = 2077, message = "Your bid must be higher than the current highest bid.")
    public static SystemMessageId YOUR_BID_MUST_BE_HIGHER_THAN_THE_CURRENT_HIGHEST_BID;
    @ClientString(id = 2078, message = "You do not have enough Adena for this bid.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ADENA_FOR_THIS_BID;
    @ClientString(id = 2079, message = "You currently have the highest bid, but the reserve has not been met.")
    public static SystemMessageId YOU_CURRENTLY_HAVE_THE_HIGHEST_BID_BUT_THE_RESERVE_HAS_NOT_BEEN_MET;
    @ClientString(id = 2080, message = "You were outbid. The new highest bid is $s1 Adena.")
    public static SystemMessageId YOU_WERE_OUTBID_THE_NEW_HIGHEST_BID_IS_S1_ADENA;
    @ClientString(id = 2085, message = "Shout and trade chatting cannot be used while possessing a cursed weapon.")
    public static SystemMessageId SHOUT_AND_TRADE_CHATTING_CANNOT_BE_USED_WHILE_POSSESSING_A_CURSED_WEAPON;
    @ClientString(id = 2286, message = "You cannot wear $s1 because you are not wearing a bracelet.")
    public static SystemMessageId YOU_CANNOT_WEAR_S1_BECAUSE_YOU_ARE_NOT_WEARING_A_BRACELET;
    @ClientString(id = 2089, message = "$s1 second(s) until the fortress battle starts.")
    public static SystemMessageId S1_SECOND_S_UNTIL_THE_FORTRESS_BATTLE_STARTS;
    @ClientString(id = 2096, message = "$c1 is in a location which cannot be entered, therefore it cannot be processed.")
    public static SystemMessageId C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED;
    @ClientString(id = 2097, message = "$c1's level does not correspond to the requirements for entry.")
    public static SystemMessageId C1_S_LEVEL_DOES_NOT_CORRESPOND_TO_THE_REQUIREMENTS_FOR_ENTRY;
    @ClientString(id = 2098, message = "$c1's quest requirement is not sufficient and cannot be entered.")
    public static SystemMessageId C1_S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
    @ClientString(id = 2099, message = "$c1's item requirement is not sufficient and cannot be entered.")
    public static SystemMessageId C1_S_ITEM_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED;
    @ClientString(id = 2100, message = "$c1 may not re-enter yet.")
    public static SystemMessageId C1_MAY_NOT_RE_ENTER_YET;
    @ClientString(id = 2101, message = "You are not currently in a party, so you cannot enter.")
    public static SystemMessageId YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER;
    @ClientString(id = 2102, message = "You cannot enter due to the party having exceeded the limit.")
    public static SystemMessageId YOU_CANNOT_ENTER_DUE_TO_THE_PARTY_HAVING_EXCEEDED_THE_LIMIT;
    @ClientString(id = 2103, message = "You cannot enter because you are not associated with the current command channel.")
    public static SystemMessageId YOU_CANNOT_ENTER_BECAUSE_YOU_ARE_NOT_ASSOCIATED_WITH_THE_CURRENT_COMMAND_CHANNEL;
    @ClientString(id = 2104, message = "The maximum number of instant zones has been exceeded. You cannot enter.")
    public static SystemMessageId THE_MAXIMUM_NUMBER_OF_INSTANT_ZONES_HAS_BEEN_EXCEEDED_YOU_CANNOT_ENTER;
    @ClientString(id = 2107, message = "This instant zone will be terminated in $s1 minute(s). You will be forced out of the dungeon when the time expires.")
    public static SystemMessageId THIS_INSTANT_ZONE_WILL_BE_TERMINATED_IN_S1_MINUTE_S_YOU_WILL_BE_FORCED_OUT_OF_THE_DUNGEON_WHEN_THE_TIME_EXPIRES;
    @ClientString(id = 2131, message = "You have bid the highest price and have won the item. The item can be found in your personal warehouse.")
    public static SystemMessageId YOU_HAVE_BID_THE_HIGHEST_PRICE_AND_HAVE_WON_THE_ITEM_THE_ITEM_CAN_BE_FOUND_IN_YOUR_PERSONAL_WAREHOUSE;
    @ClientString(id = 2143, message = "You cannot add elemental power while operating a Private Store or Private Workshop.")
    public static SystemMessageId YOU_CANNOT_ADD_ELEMENTAL_POWER_WHILE_OPERATING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
    @ClientString(id = 2145, message = "Attribute item usage has been cancelled.")
    public static SystemMessageId ATTRIBUTE_ITEM_USAGE_HAS_BEEN_CANCELLED;
    @ClientString(id = 2146, message = "Elemental power enhancer usage requirement is not sufficient.")
    public static SystemMessageId ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT;
    @ClientString(id = 2147, message = "$s2 elemental power has been added successfully to $s1.")
    public static SystemMessageId S2_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1;
    @ClientString(id = 2148, message = "$s3 elemental power has been added successfully to +$s1 $s2.")
    public static SystemMessageId S3_ELEMENTAL_POWER_HAS_BEEN_ADDED_SUCCESSFULLY_TO_S1_S2;
    @ClientString(id = 2149, message = "You have failed to add elemental power.")
    public static SystemMessageId YOU_HAVE_FAILED_TO_ADD_ELEMENTAL_POWER;
    @ClientString(id = 2150, message = "Another elemental power has already been added. This elemental power cannot be added.")
    public static SystemMessageId ANOTHER_ELEMENTAL_POWER_HAS_ALREADY_BEEN_ADDED_THIS_ELEMENTAL_POWER_CANNOT_BE_ADDED;
    @ClientString(id = 2156, message = "There are not enough necessary items to use the skill.")
    public static SystemMessageId THERE_ARE_NOT_ENOUGH_NECESSARY_ITEMS_TO_USE_THE_SKILL;
    @ClientString(id = 2158, message = "Force attack is impossible against a temporary allied member during a siege.")
    public static SystemMessageId FORCE_ATTACK_IS_IMPOSSIBLE_AGAINST_A_TEMPORARY_ALLIED_MEMBER_DURING_A_SIEGE;
    @ClientString(id = 2159, message = "Bidder exists, the auction time has been extended by 5 minutes.")
    public static SystemMessageId BIDDER_EXISTS_THE_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_5_MINUTES;
    @ClientString(id = 2160, message = "Bidder exists, auction time has been extended by 3 minutes.")
    public static SystemMessageId BIDDER_EXISTS_AUCTION_TIME_HAS_BEEN_EXTENDED_BY_3_MINUTES;
    @ClientString(id = 2162, message = "Your soul count has increased by $s1. It is now at $s2.")
    public static SystemMessageId YOUR_SOUL_COUNT_HAS_INCREASED_BY_S1_IT_IS_NOW_AT_S2;
    @ClientString(id = 2163, message = "Soul cannot be increased anymore.")
    public static SystemMessageId SOUL_CANNOT_BE_INCREASED_ANYMORE;
    @ClientString(id = 2167, message = "You cannot use skills that may harm other players in here.")
    public static SystemMessageId YOU_CANNOT_USE_SKILLS_THAT_MAY_HARM_OTHER_PLAYERS_IN_HERE;
    @ClientString(id = 2168, message = "$c1 has acquired the flag.")
    public static SystemMessageId C1_HAS_ACQUIRED_THE_FLAG;
    @ClientString(id = 2171, message = "This item cannot be crystallized.")
    public static SystemMessageId THIS_ITEM_CANNOT_BE_CRYSTALLIZED;
    @ClientString(id = 2173, message = "$s1's auction has ended.")
    public static SystemMessageId S1_S_AUCTION_HAS_ENDED;
    @ClientString(id = 2176, message = "$s1's $s2 attribute has been removed.")
    public static SystemMessageId S1_S_S2_ATTRIBUTE_HAS_BEEN_REMOVED;
    @ClientString(id = 2177, message = "+$s1$s2's $s3 attribute has been removed.")
    public static SystemMessageId S1_S2_S_S3_ATTRIBUTE_HAS_BEEN_REMOVED;
    @ClientString(id = 2182, message = "You cannot polymorph while riding a boat.")
    public static SystemMessageId YOU_CANNOT_POLYMORPH_WHILE_RIDING_A_BOAT;
    @ClientString(id = 2184, message = "$s1 is victorious in the fortress battle of $s2.")
    public static SystemMessageId S1_IS_VICTORIOUS_IN_THE_FORTRESS_BATTLE_OF_S2;
    @ClientString(id = 2185, message = "Only a party leader can make the request to enter.")
    public static SystemMessageId ONLY_A_PARTY_LEADER_CAN_MAKE_THE_REQUEST_TO_ENTER;
    @ClientString(id = 2186, message = "Soul cannot be absorbed anymore.")
    public static SystemMessageId SOUL_CANNOT_BE_ABSORBED_ANYMORE;
    @ClientString(id = 2187, message = "The target is located where you cannot charge.")
    public static SystemMessageId THE_TARGET_IS_LOCATED_WHERE_YOU_CANNOT_CHARGE;
    @ClientString(id = 2188, message = "Another enchantment is in progress. Please complete the previous task, then try again")
    public static SystemMessageId ANOTHER_ENCHANTMENT_IS_IN_PROGRESS_PLEASE_COMPLETE_THE_PREVIOUS_TASK_THEN_TRY_AGAIN;
    @ClientString(id = 2189, message = "Current location: $s1 / $s2 / $s3 (Near Jin Kamael Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_JIN_KAMAEL_VILLAGE;
    @ClientString(id = 2190, message = "Current location: $s1 / $s2 / $s3 (near Refugee Camp)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_REFUGEE_CAMP;
    @ClientString(id = 2208, message = "You do not meet the skill level requirements.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_SKILL_LEVEL_REQUIREMENTS;
    @ClientString(id = 2211, message = "You must learn the Onyx Beast skill before you can learn further skills.")
    public static SystemMessageId YOU_MUST_LEARN_THE_ONYX_BEAST_SKILL_BEFORE_YOU_CAN_LEARN_FURTHER_SKILLS;
    @ClientString(id = 2212, message = "You have not completed the necessary quest for skill acquisition.")
    public static SystemMessageId YOU_HAVE_NOT_COMPLETED_THE_NECESSARY_QUEST_FOR_SKILL_ACQUISITION;
    @ClientString(id = 2219, message = "This squad skill has already been learned.")
    public static SystemMessageId THIS_SQUAD_SKILL_HAS_ALREADY_BEEN_LEARNED;
    @ClientString(id = 2220, message = "The previous level skill has not been learned.")
    public static SystemMessageId THE_PREVIOUS_LEVEL_SKILL_HAS_NOT_BEEN_LEARNED;
    @ClientString(id = 2224, message = "Your crossbow is preparing to fire.")
    public static SystemMessageId YOUR_CROSSBOW_IS_PREPARING_TO_FIRE;
    @ClientString(id = 2225, message = "There are no other skills to learn. Please come back after $s1nd class change.")
    public static SystemMessageId THERE_ARE_NO_OTHER_SKILLS_TO_LEARN_PLEASE_COME_BACK_AFTER_S1ND_CLASS_CHANGE;
    @ClientString(id = 2261, message = "$c1 has inflicted $s3 damage on $c2.")
    public static SystemMessageId C1_HAS_INFLICTED_S3_DAMAGE_ON_C2;
    @ClientString(id = 2262, message = "$c1 has received $s3 damage from $c2.")
    public static SystemMessageId C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2;
    @ClientString(id = 2264, message = "$c1 has evaded $c2's attack.")
    public static SystemMessageId C1_HAS_EVADED_C2_S_ATTACK;
    @ClientString(id = 2265, message = "$c1's attack went astray.")
    public static SystemMessageId C1_S_ATTACK_WENT_ASTRAY;
    @ClientString(id = 2266, message = "$c1 landed a critical hit!")
    public static SystemMessageId C1_LANDED_A_CRITICAL_HIT;
    @ClientString(id = 2267, message = "$c1 resisted $c2's drain.")
    public static SystemMessageId C1_RESISTED_C2_S_DRAIN;
    @ClientString(id = 2271, message = "$c1 weakly resisted $c2's magic.")
    public static SystemMessageId C1_WEAKLY_RESISTED_C2_S_MAGIC;
    @ClientString(id = 2280, message = "Damage is decreased because $c1 resisted $c2's magic.")
    public static SystemMessageId DAMAGE_IS_DECREASED_BECAUSE_C1_RESISTED_C2_S_MAGIC;
    @ClientString(id = 2283, message = "You cannot transform while sitting.")
    public static SystemMessageId YOU_CANNOT_TRANSFORM_WHILE_SITTING;
    @ClientString(id = 2303, message = "There are $s2 second(s) remaining in $s1's re-use time.")
    public static SystemMessageId THERE_ARE_S2_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME;
    @ClientString(id = 2304, message = "There are $s2 minute(s), $s3 second(s) remaining in $s1's re-use time.")
    public static SystemMessageId THERE_ARE_S2_MINUTE_S_S3_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME;
    @ClientString(id = 2305, message = "There are $s2 hour(s), $s3 minute(s), and $s4 second(s) remaining in $s1's re-use time.")
    public static SystemMessageId THERE_ARE_S2_HOUR_S_S3_MINUTE_S_AND_S4_SECOND_S_REMAINING_IN_S1_S_RE_USE_TIME;
    @ClientString(id = 2306, message = "Your Charm of Courage is trying to resurrect you. Would you like to resurrect now?")
    public static SystemMessageId YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW;
    @ClientString(id = 2311, message = "You do not have a servitor.")
    public static SystemMessageId YOU_DO_NOT_HAVE_A_SERVITOR;
    @ClientString(id = 2312, message = "You do not have a pet.")
    public static SystemMessageId YOU_DO_NOT_HAVE_A_PET;
    @ClientString(id = 2314, message = "Your Vitality is at maximum.")
    public static SystemMessageId YOUR_VITALITY_IS_AT_MAXIMUM;
    @ClientString(id = 2315, message = "Your Vitality has increased.")
    public static SystemMessageId YOUR_VITALITY_HAS_INCREASED;
    @ClientString(id = 2316, message = "Your Vitality has decreased.")
    public static SystemMessageId YOUR_VITALITY_HAS_DECREASED;
    @ClientString(id = 2317, message = "Your Vitality is fully exhausted.")
    public static SystemMessageId YOUR_VITALITY_IS_FULLY_EXHAUSTED;
    @ClientString(id = 2319, message = "You have acquired $s1 fame.")
    public static SystemMessageId YOU_HAVE_ACQUIRED_S1_FAME;
    @ClientString(id = 2321, message = "Current location: Inside Kamaloka")
    public static SystemMessageId CURRENT_LOCATION_INSIDE_KAMALOKA;
    @ClientString(id = 2322, message = "Current location: Inside Nia Kamaloka")
    public static SystemMessageId CURRENT_LOCATION_INSIDE_NIA_KAMALOKA;
    @ClientString(id = 2323, message = "Current location: Inside Rim Kamaloka")
    public static SystemMessageId CURRENT_LOCATION_INSIDE_RIM_KAMALOKA;
    @ClientString(id = 2327, message = "You don't have enough Fame to do that.")
    public static SystemMessageId YOU_DON_T_HAVE_ENOUGH_FAME_TO_DO_THAT;
    @ClientString(id = 2333, message = "You cannot receive the dimensional item because you have exceed your inventory weight/quantity limit.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_THE_DIMENSIONAL_ITEM_BECAUSE_YOU_HAVE_EXCEED_YOUR_INVENTORY_WEIGHT_QUANTITY_LIMIT;
    @ClientString(id = 2357, message = "You cannot use My Teleports in an instant zone.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_IN_AN_INSTANT_ZONE;
    @ClientString(id = 2335, message = "There are no more dimensional items to be found.")
    public static SystemMessageId THERE_ARE_NO_MORE_DIMENSIONAL_ITEMS_TO_BE_FOUND;
    @ClientString(id = 2336, message = "Half-Kill!")
    public static SystemMessageId HALF_KILL;
    @ClientString(id = 2337, message = "Your CP was drained because you were hit with a Half-Kill skill.")
    public static SystemMessageId YOUR_CP_WAS_DRAINED_BECAUSE_YOU_WERE_HIT_WITH_A_HALF_KILL_SKILL;
    @ClientString(id = 2348, message = "You cannot use My Teleports during a battle.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_BATTLE;
    @ClientString(id = 2349, message = "You cannot use My Teleports while participating a large-scale battle such as a castle siege, fortress siege, or clan hall siege.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_A_LARGE_SCALE_BATTLE_SUCH_AS_A_CASTLE_SIEGE_FORTRESS_SIEGE_OR_CLAN_HALL_SIEGE;
    @ClientString(id = 2350, message = "You cannot use My Teleports during a duel.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_DURING_A_DUEL;
    @ClientString(id = 2351, message = "You cannot use My Teleports while flying.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_WHILE_FLYING;
    @ClientString(id = 2352, message = "You cannot use My Teleports while participating in an Olympiad match.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_WHILE_PARTICIPATING_IN_AN_OLYMPIAD_MATCH;
    @ClientString(id = 2353, message = "You cannot use My Teleports while you are in a petrified or paralyzed state.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_IN_A_PETRIFIED_OR_PARALYZED_STATE;
    @ClientString(id = 2354, message = "You cannot use My Teleports while you are dead.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_WHILE_YOU_ARE_DEAD;
    @ClientString(id = 2355, message = "You cannot use My Teleports in this area.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_IN_THIS_AREA;
    @ClientString(id = 2356, message = "You cannot use My Teleports underwater.")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_UNDERWATER;
    @ClientString(id = 2358, message = "You have no space to save the teleport location.")
    public static SystemMessageId YOU_HAVE_NO_SPACE_TO_SAVE_THE_TELEPORT_LOCATION;
    @ClientString(id = 2359, message = "You cannot teleport because you do not have a teleport item.")
    public static SystemMessageId YOU_CANNOT_TELEPORT_BECAUSE_YOU_DO_NOT_HAVE_A_TELEPORT_ITEM;
    @ClientString(id = 2361, message = "Current Location: $s1")
    public static SystemMessageId CURRENT_LOCATION_S1;
    @ClientString(id = 2364, message = "$s1 has expired.")
    public static SystemMessageId S1_HAS_EXPIRED;
    @ClientString(id = 2371, message = "$c1 was reported as a BOT.")
    public static SystemMessageId C1_WAS_REPORTED_AS_A_BOT;
    @ClientString(id = 2372, message = "There is not much time remaining until the pet leaves.")
    public static SystemMessageId THERE_IS_NOT_MUCH_TIME_REMAINING_UNTIL_THE_PET_LEAVES;
    @ClientString(id = 2373, message = "The pet is now leaving.")
    public static SystemMessageId THE_PET_IS_NOW_LEAVING;
    @ClientString(id = 2376, message = "You cannot receive a dimensional item during an exchange.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_A_DIMENSIONAL_ITEM_DURING_AN_EXCHANGE;
    @ClientString(id = 2377, message = "You cannot report a character who is in a peace zone or a battleground.")
    public static SystemMessageId YOU_CANNOT_REPORT_A_CHARACTER_WHO_IS_IN_A_PEACE_ZONE_OR_A_BATTLEGROUND;
    @ClientString(id = 2378, message = "You cannot report when a clan war has been declared.")
    public static SystemMessageId YOU_CANNOT_REPORT_WHEN_A_CLAN_WAR_HAS_BEEN_DECLARED;
    @ClientString(id = 2379, message = "You cannot report a character who has not acquired any XP after connecting.")
    public static SystemMessageId YOU_CANNOT_REPORT_A_CHARACTER_WHO_HAS_NOT_ACQUIRED_ANY_XP_AFTER_CONNECTING;
    @ClientString(id = 2380, message = "You cannot report this person again at this time.")
    public static SystemMessageId YOU_CANNOT_REPORT_THIS_PERSON_AGAIN_AT_THIS_TIME;
    @ClientString(id = 2389, message = "You have earned the maximum number of PA Points.")
    public static SystemMessageId YOU_HAVE_EARNED_THE_MAXIMUM_NUMBER_OF_PA_POINTS;
    @ClientString(id = 2390, message = "Your number of My Teleports slots has reached its maximum limit.")
    public static SystemMessageId YOUR_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_REACHED_ITS_MAXIMUM_LIMIT;
    @ClientString(id = 2396, message = "That servitor skill cannot be used because it is recharging.")
    public static SystemMessageId THAT_SERVITOR_SKILL_CANNOT_BE_USED_BECAUSE_IT_IS_RECHARGING;
    @ClientString(id = 2409, message = "The number of My Teleports slots has been increased.")
    public static SystemMessageId THE_NUMBER_OF_MY_TELEPORTS_SLOTS_HAS_BEEN_INCREASED;
    @ClientString(id = 2410, message = "You cannot use My Teleports to reach this area!")
    public static SystemMessageId YOU_CANNOT_USE_MY_TELEPORTS_TO_REACH_THIS_AREA;
    @ClientString(id = 2432, message = "The central stronghold's compressor has been destroyed.")
    public static SystemMessageId THE_CENTRAL_STRONGHOLD_S_COMPRESSOR_HAS_BEEN_DESTROYED;
    @ClientString(id = 2436, message = "The central stronghold's compressor is working.")
    public static SystemMessageId THE_CENTRAL_STRONGHOLD_S_COMPRESSOR_IS_WORKING;
    @ClientString(id = 2448, message = "Happy birthday! Alegria has sent you a birthday gift.")
    public static SystemMessageId HAPPY_BIRTHDAY_ALEGRIA_HAS_SENT_YOU_A_BIRTHDAY_GIFT;
    @ClientString(id = 2449, message = "There are $s1 days remaining until your birthday. On your birthday, you will receive a gift that Alegria has carefully prepared.")
    public static SystemMessageId THERE_ARE_S1_DAYS_REMAINING_UNTIL_YOUR_BIRTHDAY_ON_YOUR_BIRTHDAY_YOU_WILL_RECEIVE_A_GIFT_THAT_ALEGRIA_HAS_CAREFULLY_PREPARED;
    @ClientString(id = 2450, message = "$c1's birthday is $s3/$s4/$s2.")
    public static SystemMessageId C1_S_BIRTHDAY_IS_S3_S4_S2;
    @ClientString(id = 2451, message = "Your cloak has been unequipped because your armor set is no longer complete.")
    public static SystemMessageId YOUR_CLOAK_HAS_BEEN_UNEQUIPPED_BECAUSE_YOUR_ARMOR_SET_IS_NO_LONGER_COMPLETE;
    @ClientString(id = 2463, message = "The airship's fuel (EP) will soon run out.")
    public static SystemMessageId THE_AIRSHIP_S_FUEL_EP_WILL_SOON_RUN_OUT;
    @ClientString(id = 2464, message = "The airship's fuel (EP) has run out. The airship's speed will be greatly decreased in this condition.")
    public static SystemMessageId THE_AIRSHIP_S_FUEL_EP_HAS_RUN_OUT_THE_AIRSHIP_S_SPEED_WILL_BE_GREATLY_DECREASED_IN_THIS_CONDITION;
    @ClientString(id = 2468, message = "You have used a report point on $c1. You have $s2 points remaining on this account.")
    public static SystemMessageId YOU_HAVE_USED_A_REPORT_POINT_ON_C1_YOU_HAVE_S2_POINTS_REMAINING_ON_THIS_ACCOUNT;
    @ClientString(id = 2469, message = "You've spent all points. The points will be reset at 06:30 so that you can use them again.")
    public static SystemMessageId YOU_VE_SPENT_ALL_POINTS_THE_POINTS_WILL_BE_RESET_AT_06_30_SO_THAT_YOU_CAN_USE_THEM_AGAIN;
    @ClientString(id = 2470, message = "This character cannot make a report. You cannot make a report while located inside a peace zone or a battleground, while you are an opposing clan member during a clan war, or while participating in the Olympiad.")
    public static SystemMessageId THIS_CHARACTER_CANNOT_MAKE_A_REPORT_YOU_CANNOT_MAKE_A_REPORT_WHILE_LOCATED_INSIDE_A_PEACE_ZONE_OR_A_BATTLEGROUND_WHILE_YOU_ARE_AN_OPPOSING_CLAN_MEMBER_DURING_A_CLAN_WAR_OR_WHILE_PARTICIPATING_IN_THE_OLYMPIAD;
    @ClientString(id = 2471, message = "This character cannot make a report. The target has already been reported by either your clan, or has already been reported from your current IP.")
    public static SystemMessageId THIS_CHARACTER_CANNOT_MAKE_A_REPORT_THE_TARGET_HAS_ALREADY_BEEN_REPORTED_BY_EITHER_YOUR_CLAN_OR_HAS_ALREADY_BEEN_REPORTED_FROM_YOUR_CURRENT_IP;
    @ClientString(id = 2481, message = "$c1 has been reported as an illegal program user and is currently being investigated.")
    public static SystemMessageId C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_IS_CURRENTLY_BEING_INVESTIGATED;
    @ClientString(id = 2482, message = "$c1 has been reported as an illegal program user and cannot join a party.")
    public static SystemMessageId C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY;
    @ClientString(id = 2483, message = "You have been reported as an illegal program user, so chatting is not allowed.")
    public static SystemMessageId YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_CHATTING_IS_NOT_ALLOWED;
    @ClientString(id = 2484, message = "You have been reported as an illegal program user, so participating in a party is not allowed.")
    public static SystemMessageId YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_PARTICIPATING_IN_A_PARTY_IS_NOT_ALLOWED;
    @ClientString(id = 2485, message = "You have been reported as an illegal program user so your actions have been restricted.")
    public static SystemMessageId YOU_HAVE_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_SO_YOUR_ACTIONS_HAVE_BEEN_RESTRICTED;
    @ClientString(id = 2491, message = "Your airship cannot teleport because due to low fuel.")
    public static SystemMessageId YOUR_AIRSHIP_CANNOT_TELEPORT_BECAUSE_DUE_TO_LOW_FUEL;
    @ClientString(id = 2703, message = "Team members were modified because the teams were unbalanced.")
    public static SystemMessageId TEAM_MEMBERS_WERE_MODIFIED_BECAUSE_THE_TEAMS_WERE_UNBALANCED;
    @ClientString(id = 2707, message = "You must wait 10 seconds before attempting to register again.")
    public static SystemMessageId YOU_MUST_WAIT_10_SECONDS_BEFORE_ATTEMPTING_TO_REGISTER_AGAIN;
    @ClientString(id = 2708, message = "You cannot register while in possession of a cursed weapon.")
    public static SystemMessageId YOU_CANNOT_REGISTER_WHILE_IN_POSSESSION_OF_A_CURSED_WEAPON;
    @ClientString(id = 2709, message = "Applicants for the Olympiad, Underground Coliseum, or Kratei's Cube matches cannot register.")
    public static SystemMessageId APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER;
    @ClientString(id = 2710, message = "Current location: $s1 / $s2 / $s3 (near the Keucereus Alliance Base)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_KEUCEREUS_ALLIANCE_BASE;
    @ClientString(id = 2711, message = "Current location: $s1 / $s2 / $s3 (inside the Seed of Infinity)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_INSIDE_THE_SEED_OF_INFINITY;
    @ClientString(id = 2712, message = "Current location: $s1 / $s2 / $s3 (outside the Seed of Infinity)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_OUTSIDE_THE_SEED_OF_INFINITY;
    @ClientString(id = 2720, message = "Instant zone: $s1's entry has been restricted. You can check the next possible entry time by using the command /instancezone.")
    public static SystemMessageId INSTANT_ZONE_S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_POSSIBLE_ENTRY_TIME_BY_USING_THE_COMMAND_INSTANCEZONE;
    @ClientString(id = 2721, message = "You are too high to perform this action. Please lower your altitude and try again.")
    public static SystemMessageId YOU_ARE_TOO_HIGH_TO_PERFORM_THIS_ACTION_PLEASE_LOWER_YOUR_ALTITUDE_AND_TRY_AGAIN;
    @ClientString(id = 2728, message = "This action is prohibited while mounted or on an airship.")
    public static SystemMessageId THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP;
    @ClientString(id = 2729, message = "You cannot control the helm while transformed.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_TRANSFORMED;
    @ClientString(id = 2730, message = "You cannot control the helm while you are petrified.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_YOU_ARE_PETRIFIED;
    @ClientString(id = 2731, message = "You cannot control the helm when you are dead.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHEN_YOU_ARE_DEAD;
    @ClientString(id = 2732, message = "You cannot control the helm while fishing.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_FISHING;
    @ClientString(id = 2733, message = "You cannot control the helm while in a battle.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_BATTLE;
    @ClientString(id = 2734, message = "You cannot control the helm while in a duel.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_DUEL;
    @ClientString(id = 2735, message = "You cannot control the helm while in a sitting position.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_IN_A_SITTING_POSITION;
    @ClientString(id = 2736, message = "You cannot control the helm while using a skill.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_USING_A_SKILL;
    @ClientString(id = 2737, message = "You cannot control the helm while a cursed weapon is equipped.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_A_CURSED_WEAPON_IS_EQUIPPED;
    @ClientString(id = 2738, message = "You cannot control the helm while holding a flag.")
    public static SystemMessageId YOU_CANNOT_CONTROL_THE_HELM_WHILE_HOLDING_A_FLAG;
    @ClientString(id = 2762, message = "You cannot control because you are too far.")
    public static SystemMessageId YOU_CANNOT_CONTROL_BECAUSE_YOU_ARE_TOO_FAR;
    @ClientString(id = 2774, message = "You can make another report in $s1 minute(s). You have $s2 point(s) remaining on this account.")
    public static SystemMessageId YOU_CAN_MAKE_ANOTHER_REPORT_IN_S1_MINUTE_S_YOU_HAVE_S2_POINT_S_REMAINING_ON_THIS_ACCOUNT;
    @ClientString(id = 2778, message = "You cannot teleport while in possession of a ward.")
    public static SystemMessageId YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD;
    @ClientString(id = 2793, message = "You must have a minimum of $s1 people to enter this instanced zone.")
    public static SystemMessageId YOU_MUST_HAVE_A_MINIMUM_OF_S1_PEOPLE_TO_ENTER_THIS_INSTANCED_ZONE;
    @ClientString(id = 2909, message = "A servitor cannot be summoned while on an airship.")
    public static SystemMessageId A_SERVITOR_CANNOT_BE_SUMMONED_WHILE_ON_AN_AIRSHIP;
    @ClientString(id = 2911, message = "You've requested $c1 to be on your Friends List.")
    public static SystemMessageId YOU_VE_REQUESTED_C1_TO_BE_ON_YOUR_FRIENDS_LIST;
    @ClientString(id = 2928, message = "The $c1 team has won.")
    public static SystemMessageId THE_C1_TEAM_HAS_WON;
    @ClientString(id = 2960, message = "You need a(n) $s1.")
    public static SystemMessageId YOU_NEED_A_N_S1;
    @ClientString(id = 2961, message = "You need $s2 $s1(s).")
    public static SystemMessageId YOU_NEED_S2_S1_S;
    @ClientString(id = 2966, message = "It's a Payment Request transaction. Please attach the item.")
    public static SystemMessageId IT_S_A_PAYMENT_REQUEST_TRANSACTION_PLEASE_ATTACH_THE_ITEM;
    @ClientString(id = 2968, message = "The mail limit (240) has been exceeded and this cannot be forwarded.")
    public static SystemMessageId THE_MAIL_LIMIT_240_HAS_BEEN_EXCEEDED_AND_THIS_CANNOT_BE_FORWARDED;
    @ClientString(id = 2969, message = "The previous mail was forwarded less than 1 minute ago and this cannot be forwarded.")
    public static SystemMessageId THE_PREVIOUS_MAIL_WAS_FORWARDED_LESS_THAN_1_MINUTE_AGO_AND_THIS_CANNOT_BE_FORWARDED;
    @ClientString(id = 2970, message = "You cannot forward in a non-peace zone location.")
    public static SystemMessageId YOU_CANNOT_FORWARD_IN_A_NON_PEACE_ZONE_LOCATION;
    @ClientString(id = 2971, message = "You cannot forward during an exchange.")
    public static SystemMessageId YOU_CANNOT_FORWARD_DURING_AN_EXCHANGE;
    @ClientString(id = 2972, message = "You cannot forward because the private store or workshop is in progress.")
    public static SystemMessageId YOU_CANNOT_FORWARD_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS;
    @ClientString(id = 2973, message = "You can't send while enchanting an item or attribute, combining jewels, or sealing, unsealing or combining.")
    public static SystemMessageId YOU_CAN_T_SEND_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING;
    @ClientString(id = 2974, message = "The item that you're trying to send cannot be forwarded because it isn't proper.")
    public static SystemMessageId THE_ITEM_THAT_YOU_RE_TRYING_TO_SEND_CANNOT_BE_FORWARDED_BECAUSE_IT_ISN_T_PROPER;
    @ClientString(id = 2975, message = "You cannot forward because you don't have enough Adena.")
    public static SystemMessageId YOU_CANNOT_FORWARD_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA;
    @ClientString(id = 2976, message = "You cannot receive in a non-peace zone location.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_IN_A_NON_PEACE_ZONE_LOCATION;
    @ClientString(id = 2977, message = "You cannot receive during an exchange.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_DURING_AN_EXCHANGE;
    @ClientString(id = 2978, message = "You cannot receive because the private store or workshop is in progress.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS;
    @ClientString(id = 2979, message = "You can't receive while enchanting an item or attribute, combining jewels, or sealing, unsealing or combining.")
    public static SystemMessageId YOU_CAN_T_RECEIVE_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE_COMBINING_JEWELS_OR_SEALING_UNSEALING_OR_COMBINING;
    @ClientString(id = 2980, message = "You cannot receive because you don't have enough Adena.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_BECAUSE_YOU_DON_T_HAVE_ENOUGH_ADENA;
    @ClientString(id = 2981, message = "You could not receive because your inventory is full.")
    public static SystemMessageId YOU_COULD_NOT_RECEIVE_BECAUSE_YOUR_INVENTORY_IS_FULL;
    @ClientString(id = 2982, message = "You cannot cancel in a non-peace zone location.")
    public static SystemMessageId YOU_CANNOT_CANCEL_IN_A_NON_PEACE_ZONE_LOCATION;
    @ClientString(id = 2983, message = "You cannot cancel during an exchange.")
    public static SystemMessageId YOU_CANNOT_CANCEL_DURING_AN_EXCHANGE;
    @ClientString(id = 2984, message = "You cannot cancel because the private store or workshop is in progress.")
    public static SystemMessageId YOU_CANNOT_CANCEL_BECAUSE_THE_PRIVATE_STORE_OR_WORKSHOP_IS_IN_PROGRESS;
    @ClientString(id = 2985, message = "You can't cancel while enchanting an item or attribute.")
    public static SystemMessageId YOU_CAN_T_CANCEL_WHILE_ENCHANTING_AN_ITEM_OR_ATTRIBUTE;
    @ClientString(id = 2988, message = "You could not cancel receipt because your inventory is full.")
    public static SystemMessageId YOU_COULD_NOT_CANCEL_RECEIPT_BECAUSE_YOUR_INVENTORY_IS_FULL;
    @ClientString(id = 2994, message = "The Command Channel matching room was cancelled.")
    public static SystemMessageId THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CANCELLED;
    @ClientString(id = 2996, message = "You cannot enter the Command Channel matching room because you do not meet the requirements.")
    public static SystemMessageId YOU_CANNOT_ENTER_THE_COMMAND_CHANNEL_MATCHING_ROOM_BECAUSE_YOU_DO_NOT_MEET_THE_REQUIREMENTS;
    @ClientString(id = 2997, message = "You exited from the Command Channel matching room.")
    public static SystemMessageId YOU_EXITED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM;
    @ClientString(id = 2998, message = "You were expelled from the Command Channel matching room.")
    public static SystemMessageId YOU_WERE_EXPELLED_FROM_THE_COMMAND_CHANNEL_MATCHING_ROOM;
    @ClientString(id = 2999, message = "The Command Channel affiliated party's party member cannot use the matching screen.")
    public static SystemMessageId THE_COMMAND_CHANNEL_AFFILIATED_PARTY_S_PARTY_MEMBER_CANNOT_USE_THE_MATCHING_SCREEN;
    @ClientString(id = 3000, message = "The Command Channel matching room was created.")
    public static SystemMessageId THE_COMMAND_CHANNEL_MATCHING_ROOM_WAS_CREATED;
    @ClientString(id = 3001, message = "The Command Channel matching room information was edited.")
    public static SystemMessageId THE_COMMAND_CHANNEL_MATCHING_ROOM_INFORMATION_WAS_EDITED;
    @ClientString(id = 3002, message = "When the recipient doesn't exist or the character has been deleted, sending mail is not possible.")
    public static SystemMessageId WHEN_THE_RECIPIENT_DOESN_T_EXIST_OR_THE_CHARACTER_HAS_BEEN_DELETED_SENDING_MAIL_IS_NOT_POSSIBLE;
    @ClientString(id = 3003, message = "$c1 entered the Command Channel matching room.")
    public static SystemMessageId C1_ENTERED_THE_COMMAND_CHANNEL_MATCHING_ROOM;
    @ClientString(id = 3009, message = "Mail successfully sent.")
    public static SystemMessageId MAIL_SUCCESSFULLY_SENT;
    @ClientString(id = 3010, message = "Mail successfully returned.")
    public static SystemMessageId MAIL_SUCCESSFULLY_RETURNED;
    @ClientString(id = 3011, message = "Mail successfully cancelled.")
    public static SystemMessageId MAIL_SUCCESSFULLY_CANCELLED;
    @ClientString(id = 3012, message = "Mail successfully received.")
    public static SystemMessageId MAIL_SUCCESSFULLY_RECEIVED;
    @ClientString(id = 3013, message = "$c1 has successfully enchanted a +$s2 $s3.")
    public static SystemMessageId C1_HAS_SUCCESSFULLY_ENCHANTED_A_S2_S3;
    @ClientString(id = 3016, message = "Item selection is possible up to 8.")
    public static SystemMessageId ITEM_SELECTION_IS_POSSIBLE_UP_TO_8;
    @ClientString(id = 3019, message = "You cannot send a mail to yourself.")
    public static SystemMessageId YOU_CANNOT_SEND_A_MAIL_TO_YOURSELF;
    @ClientString(id = 3020, message = "When not entering the amount for the payment request, you cannot send any mail.")
    public static SystemMessageId WHEN_NOT_ENTERING_THE_AMOUNT_FOR_THE_PAYMENT_REQUEST_YOU_CANNOT_SEND_ANY_MAIL;
    @ClientString(id = 3025, message = "$s2 has made a payment of $s1 Adena per your payment request mail.")
    public static SystemMessageId S2_HAS_MADE_A_PAYMENT_OF_S1_ADENA_PER_YOUR_PAYMENT_REQUEST_MAIL;
    @ClientString(id = 3029, message = "$s1 returned the mail.")
    public static SystemMessageId S1_RETURNED_THE_MAIL;
    @ClientString(id = 3030, message = "You cannot cancel sent mail since the recipient received it.")
    public static SystemMessageId YOU_CANNOT_CANCEL_SENT_MAIL_SINCE_THE_RECIPIENT_RECEIVED_IT;
    @ClientString(id = 3065, message = "Current location: Inside the Chamber of Delusion")
    public static SystemMessageId CURRENT_LOCATION_INSIDE_THE_CHAMBER_OF_DELUSION;
    @ClientString(id = 3066, message = "You cannot receive or send mail with attached items in non-peace zone regions.")
    public static SystemMessageId YOU_CANNOT_RECEIVE_OR_SEND_MAIL_WITH_ATTACHED_ITEMS_IN_NON_PEACE_ZONE_REGIONS;
    @ClientString(id = 3067, message = "$s1 canceled the sent mail.")
    public static SystemMessageId S1_CANCELED_THE_SENT_MAIL;
    @ClientString(id = 3068, message = "The mail was returned due to the exceeded waiting time.")
    public static SystemMessageId THE_MAIL_WAS_RETURNED_DUE_TO_THE_EXCEEDED_WAITING_TIME;
    @ClientString(id = 3072, message = "$s1 acquired the attached item to your mail.")
    public static SystemMessageId S1_ACQUIRED_THE_ATTACHED_ITEM_TO_YOUR_MAIL;
    @ClientString(id = 3073, message = "You have acquired $s2 $s1.")
    public static SystemMessageId YOU_HAVE_ACQUIRED_S2_S1;
    @ClientString(id = 3074, message = "The allowed length for recipient exceeded.")
    public static SystemMessageId THE_ALLOWED_LENGTH_FOR_RECIPIENT_EXCEEDED;
    @ClientString(id = 3075, message = "The allowed length for a title exceeded.")
    public static SystemMessageId THE_ALLOWED_LENGTH_FOR_A_TITLE_EXCEEDED;
    @ClientString(id = 3079, message = "There are items in your Pet Inventory rendering you unable to sell/trade/drop pet summoning items. Please empty your Pet Inventory.")
    public static SystemMessageId THERE_ARE_ITEMS_IN_YOUR_PET_INVENTORY_RENDERING_YOU_UNABLE_TO_SELL_TRADE_DROP_PET_SUMMONING_ITEMS_PLEASE_EMPTY_YOUR_PET_INVENTORY;
    @ClientString(id = 3094, message = "A user currently participating in the Olympiad cannot send party and friend invitations.")
    public static SystemMessageId A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS;
    @ClientString(id = 3108, message = "You are no longer protected from aggressive monsters.")
    public static SystemMessageId YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS;
    @ClientString(id = 3119, message = "The couple action was denied.")
    public static SystemMessageId THE_COUPLE_ACTION_WAS_DENIED;
    @ClientString(id = 3120, message = "The request cannot be completed because the target does not meet location requirements.")
    public static SystemMessageId THE_REQUEST_CANNOT_BE_COMPLETED_BECAUSE_THE_TARGET_DOES_NOT_MEET_LOCATION_REQUIREMENTS;
    @ClientString(id = 3121, message = "The couple action was cancelled.")
    public static SystemMessageId THE_COUPLE_ACTION_WAS_CANCELLED;
    @ClientString(id = 3122, message = "The size of the uploaded symbol does not meet the standard requirements.")
    public static SystemMessageId THE_SIZE_OF_THE_UPLOADED_SYMBOL_DOES_NOT_MEET_THE_STANDARD_REQUIREMENTS;
    @ClientString(id = 3123, message = "$c1 is in private store mode or in a battle and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_IN_PRIVATE_STORE_MODE_OR_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3124, message = "$c1 is fishing and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_FISHING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3125, message = "$c1 is in a battle and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_IN_A_BATTLE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3126, message = "$c1 is already participating in a couple action and cannot be requested for another couple action.")
    public static SystemMessageId C1_IS_ALREADY_PARTICIPATING_IN_A_COUPLE_ACTION_AND_CANNOT_BE_REQUESTED_FOR_ANOTHER_COUPLE_ACTION;
    @ClientString(id = 3127, message = "$c1 is in a chaotic state and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_IN_A_CHAOTIC_STATE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3128, message = "$c1 is participating in the Olympiad and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_PARTICIPATING_IN_THE_OLYMPIAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3129, message = "$c1 is participating in a clan hall siege and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_PARTICIPATING_IN_A_CLAN_HALL_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3130, message = "$c1 is in a castle siege and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_IN_A_CASTLE_SIEGE_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3131, message = "$c1 is riding a ship, steed, or strider and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_RIDING_A_SHIP_STEED_OR_STRIDER_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3132, message = "$c1 is currently teleporting and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_CURRENTLY_TELEPORTING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3133, message = "$c1 is currently transforming and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_CURRENTLY_TRANSFORMING_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3135, message = "Requesting approval for changing party loot to $s1.")
    public static SystemMessageId REQUESTING_APPROVAL_FOR_CHANGING_PARTY_LOOT_TO_S1;
    @ClientString(id = 3137, message = "Party loot change was cancelled.")
    public static SystemMessageId PARTY_LOOT_CHANGE_WAS_CANCELLED;
    @ClientString(id = 3138, message = "Party loot was changed to $s1.")
    public static SystemMessageId PARTY_LOOT_WAS_CHANGED_TO_S1;
    @ClientString(id = 3139, message = "$c1 is currently dead and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_CURRENTLY_DEAD_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3140, message = "The crest was successfully registered.")
    public static SystemMessageId THE_CREST_WAS_SUCCESSFULLY_REGISTERED;
    @ClientString(id = 3144, message = "The $s2's attribute was successfully bestowed on $s1, and resistance to $s3 was increased.")
    public static SystemMessageId THE_S2_S_ATTRIBUTE_WAS_SUCCESSFULLY_BESTOWED_ON_S1_AND_RESISTANCE_TO_S3_WAS_INCREASED;
    @ClientString(id = 3147, message = "If you are not resurrected within $s1 minute(s), you will be expelled from the instant zone.")
    public static SystemMessageId IF_YOU_ARE_NOT_RESURRECTED_WITHIN_S1_MINUTE_S_YOU_WILL_BE_EXPELLED_FROM_THE_INSTANT_ZONE;
    @ClientString(id = 3148, message = "The number of instant zones that can be created has been exceeded. Please try again later.")
    public static SystemMessageId THE_NUMBER_OF_INSTANT_ZONES_THAT_CAN_BE_CREATED_HAS_BEEN_EXCEEDED_PLEASE_TRY_AGAIN_LATER;
    @ClientString(id = 3150, message = "You have requested a couple action with $c1.")
    public static SystemMessageId YOU_HAVE_REQUESTED_A_COUPLE_ACTION_WITH_C1;
    @ClientString(id = 3152, message = "$s1's $s2 attribute was removed, and resistance to $s3 was decreased.")
    public static SystemMessageId S1_S_S2_ATTRIBUTE_WAS_REMOVED_AND_RESISTANCE_TO_S3_WAS_DECREASED;
    @ClientString(id = 3156, message = "You do not have enough funds to cancel this attribute.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_FUNDS_TO_CANCEL_THIS_ATTRIBUTE;
    @ClientString(id = 3160, message = "+$s1$s2's $s3 attribute was removed, so resistance to $s4 was decreased.")
    public static SystemMessageId S1_S2_S_S3_ATTRIBUTE_WAS_REMOVED_SO_RESISTANCE_TO_S4_WAS_DECREASED;
    @ClientString(id = 3168, message = "$c1 is set to refuse party requests and cannot receive a party request.")
    public static SystemMessageId C1_IS_SET_TO_REFUSE_PARTY_REQUESTS_AND_CANNOT_RECEIVE_A_PARTY_REQUEST;
    @ClientString(id = 3157, message = "Are you sure you want to delete the Clan Mark?")
    public static SystemMessageId ARE_YOU_SURE_YOU_WANT_TO_DELETE_THE_CLAN_MARK;
    @ClientString(id = 3158, message = "This is not the Lilith server. This command can only be used on the Lilith server.")
    public static SystemMessageId THIS_IS_NOT_THE_LILITH_SERVER_THIS_COMMAND_CAN_ONLY_BE_USED_ON_THE_LILITH_SERVER;
    @ClientString(id = 3159, message = "First, please select the shortcut key category to be changed.")
    public static SystemMessageId FIRST_PLEASE_SELECT_THE_SHORTCUT_KEY_CATEGORY_TO_BE_CHANGED;
    @ClientString(id = 3163, message = "The $s3's attribute was successfully bestowed on +$s1$s2, and resistance to $s4 was increased.")
    public static SystemMessageId THE_S3_S_ATTRIBUTE_WAS_SUCCESSFULLY_BESTOWED_ON_S1_S2_AND_RESISTANCE_TO_S4_WAS_INCREASED;
    @ClientString(id = 3164, message = "$c1 is set to refuse couple actions and cannot be requested for a couple action.")
    public static SystemMessageId C1_IS_SET_TO_REFUSE_COUPLE_ACTIONS_AND_CANNOT_BE_REQUESTED_FOR_A_COUPLE_ACTION;
    @ClientString(id = 3169, message = "$c1 is set to refuse duel requests and cannot receive a duel request.")
    public static SystemMessageId C1_IS_SET_TO_REFUSE_DUEL_REQUESTS_AND_CANNOT_RECEIVE_A_DUEL_REQUEST;
    @ClientString(id = 3206, message = "You are out of Recommendations. Try again later.")
    public static SystemMessageId YOU_ARE_OUT_OF_RECOMMENDATIONS_TRY_AGAIN_LATER;
    @ClientString(id = 3207, message = "You obtained $s1 Recommendation(s).")
    public static SystemMessageId YOU_OBTAINED_S1_RECOMMENDATION_S;
    @ClientString(id = 3212, message = "You cannot use your pet when its hunger gauge is at 0%%.")
    public static SystemMessageId YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0;
    @ClientString(id = 3213, message = "Your pet is starving and will not obey until it gets it's food. Feed your pet!")
    public static SystemMessageId YOUR_PET_IS_STARVING_AND_WILL_NOT_OBEY_UNTIL_IT_GETS_IT_S_FOOD_FEED_YOUR_PET;
    @ClientString(id = 3214, message = "$s1 was successfully added to your Contact List.")
    public static SystemMessageId S1_WAS_SUCCESSFULLY_ADDED_TO_YOUR_CONTACT_LIST;
    @ClientString(id = 3215, message = "The name $s1 doesn't exist. Please try another name.")
    public static SystemMessageId THE_NAME_S1_DOESN_T_EXIST_PLEASE_TRY_ANOTHER_NAME;
    @ClientString(id = 3216, message = "The name already exists on the added list.")
    public static SystemMessageId THE_NAME_ALREADY_EXISTS_ON_THE_ADDED_LIST;
    @ClientString(id = 3217, message = "The name is not currently registered.")
    public static SystemMessageId THE_NAME_IS_NOT_CURRENTLY_REGISTERED;
    @ClientString(id = 3219, message = "$s1 was successfully deleted from your Contact List.")
    public static SystemMessageId S1_WAS_SUCCESSFULLY_DELETED_FROM_YOUR_CONTACT_LIST;
    @ClientString(id = 3221, message = "You cannot add your own name.")
    public static SystemMessageId YOU_CANNOT_ADD_YOUR_OWN_NAME;
    @ClientString(id = 3222, message = "The maximum number of names (100) has been reached. You cannot register any more.")
    public static SystemMessageId THE_MAXIMUM_NUMBER_OF_NAMES_100_HAS_BEEN_REACHED_YOU_CANNOT_REGISTER_ANY_MORE;
    @ClientString(id = 3224, message = "You can participate in up to 30 matches per week.")
    public static SystemMessageId YOU_CAN_PARTICIPATE_IN_UP_TO_30_MATCHES_PER_WEEK;
    @ClientString(id = 3226, message = "You cannot move while speaking to an NPC. One moment please.")
    public static SystemMessageId YOU_CANNOT_MOVE_WHILE_SPEAKING_TO_AN_NPC_ONE_MOMENT_PLEASE;
    @ClientString(id = 3255, message = "Arcane Shield decreased your MP by $s1 instead of HP.")
    public static SystemMessageId ARCANE_SHIELD_DECREASED_YOUR_MP_BY_S1_INSTEAD_OF_HP;
    @ClientString(id = 3256, message = "MP became 0 and the Arcane Shield is disappearing.")
    public static SystemMessageId MP_BECAME_0_AND_THE_ARCANE_SHIELD_IS_DISAPPEARING;
    @ClientString(id = 3259, message = "You have acquired $s1 XP (Bonus: $s2) and $s3 SP (Bonus: $s4).")
    public static SystemMessageId YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4;
    @ClientString(id = 3261, message = "You can participate in $s1 All-Class Battle matches this week.")
    public static SystemMessageId YOU_CAN_PARTICIPATE_IN_S1_ALL_CLASS_BATTLE_MATCHES_THIS_WEEK;
    @ClientString(id = 3276, message = "Crystallization cannot be proceeded because there are no items registered.")
    public static SystemMessageId CRYSTALLIZATION_CANNOT_BE_PROCEEDED_BECAUSE_THERE_ARE_NO_ITEMS_REGISTERED;
    @ClientString(id = 3283, message = "You cannot declare defeat as it has not been 7 days since starting a clan war with Clan $s1.")
    public static SystemMessageId YOU_CANNOT_DECLARE_DEFEAT_AS_IT_HAS_NOT_BEEN_7_DAYS_SINCE_STARTING_A_CLAN_WAR_WITH_CLAN_S1;
    @ClientString(id = 3284, message = "The war ended by your Defeat Declaration with the $s1 clan.")
    public static SystemMessageId THE_WAR_ENDED_BY_YOUR_DEFEAT_DECLARATION_WITH_THE_S1_CLAN;
    @ClientString(id = 3285, message = "The war ended by the $s1 clan's Defeat Declaration. You have won the Clan War over the $s1 clan.")
    public static SystemMessageId THE_WAR_ENDED_BY_THE_S1_CLAN_S_DEFEAT_DECLARATION_YOU_HAVE_WON_THE_CLAN_WAR_OVER_THE_S1_CLAN;
    @ClientString(id = 3286, message = "You can't declare a war because the 21-day-period hasn't passed after a Defeat Declaration with the $s1 clan.")
    public static SystemMessageId YOU_CAN_T_DECLARE_A_WAR_BECAUSE_THE_21_DAY_PERIOD_HASN_T_PASSED_AFTER_A_DEFEAT_DECLARATION_WITH_THE_S1_CLAN;
    @ClientString(id = 3363, message = "The item cannot be registered because requirements are not met.")
    public static SystemMessageId THE_ITEM_CANNOT_BE_REGISTERED_BECAUSE_REQUIREMENTS_ARE_NOT_MET;
    @ClientString(id = 3364, message = "You do not have enough Adena to register the item.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ADENA_TO_REGISTER_THE_ITEM;
    @ClientString(id = 3365, message = "The item has failed to be registered.")
    public static SystemMessageId THE_ITEM_HAS_FAILED_TO_BE_REGISTERED;
    @ClientString(id = 3366, message = "Cancellation of Sale has failed because requirements are not met.")
    public static SystemMessageId CANCELLATION_OF_SALE_HAS_FAILED_BECAUSE_REQUIREMENTS_ARE_NOT_MET;
    @ClientString(id = 3370, message = "Item Purchase is not available because the corresponding item does not exist.")
    public static SystemMessageId ITEM_PURCHASE_IS_NOT_AVAILABLE_BECAUSE_THE_CORRESPONDING_ITEM_DOES_NOT_EXIST;
    @ClientString(id = 3371, message = "Item Purchase has failed.")
    public static SystemMessageId ITEM_PURCHASE_HAS_FAILED;
    @ClientString(id = 3463, message = "$c1 used $s3 on $c2.")
    public static SystemMessageId C1_USED_S3_ON_C2;
    @ClientString(id = 3472, message = "You must have rights to a Clan Hall auction in order to make a bid for Provisional Clan Hall.")
    public static SystemMessageId YOU_MUST_HAVE_RIGHTS_TO_A_CLAN_HALL_AUCTION_IN_ORDER_TO_MAKE_A_BID_FOR_PROVISIONAL_CLAN_HALL;
    @ClientString(id = 3481, message = "If the weight is 80%% or more and the inventory number is 90%% or more, purchase/cancellation is not possible.")
    public static SystemMessageId IF_THE_WEIGHT_IS_80_OR_MORE_AND_THE_INVENTORY_NUMBER_IS_90_OR_MORE_PURCHASE_CANCELLATION_IS_NOT_POSSIBLE;
    @ClientString(id = 3484, message = "The item has been successfully registered.")
    public static SystemMessageId THE_ITEM_HAS_BEEN_SUCCESSFULLY_REGISTERED;
    @ClientString(id = 3485, message = "Cancellation of Sale for the item is successful.")
    public static SystemMessageId CANCELLATION_OF_SALE_FOR_THE_ITEM_IS_SUCCESSFUL;
    @ClientString(id = 3490, message = "The item you registered has been sold.")
    public static SystemMessageId THE_ITEM_YOU_REGISTERED_HAS_BEEN_SOLD;
    @ClientString(id = 3491, message = "$s1 has been sold.")
    public static SystemMessageId S1_HAS_BEEN_SOLD;
    @ClientString(id = 3492, message = "The registration period for the item you registered has expired.")
    public static SystemMessageId THE_REGISTRATION_PERIOD_FOR_THE_ITEM_YOU_REGISTERED_HAS_EXPIRED;
    @ClientString(id = 3493, message = "The auction house registration period has expired and the corresponding item is being forwarded.")
    public static SystemMessageId THE_AUCTION_HOUSE_REGISTRATION_PERIOD_HAS_EXPIRED_AND_THE_CORRESPONDING_ITEM_IS_BEING_FORWARDED;
    @ClientString(id = 3517, message = "You cannot register in the waiting list while being inside of a battleground (castle siege/fortress siege).")
    public static SystemMessageId YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGE_FORTRESS_SIEGE;
    @ClientString(id = 3518, message = "Waiting list registration is not allowed while the cursed sword is being used or the status is in a chaotic state.")
    public static SystemMessageId WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE;
    @ClientString(id = 3519, message = "You cannot register in the waiting list during a duel.")
    public static SystemMessageId YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL;
    @ClientString(id = 3520, message = "You cannot register in the waiting list while participating in Olympiad.")
    public static SystemMessageId YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD;
    @ClientString(id = 3521, message = "You cannot register for the waiting list while participating in the Block Checker/Coliseum/Olympiad/Kratei's Cube/Ceremony of Chaos.")
    public static SystemMessageId YOU_CANNOT_REGISTER_FOR_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_THE_BLOCK_CHECKER_COLISEUM_OLYMPIAD_KRATEI_S_CUBE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3522, message = "You cannot register for the waiting list on the battlefield (castle siege/fortress siege).")
    public static SystemMessageId YOU_CANNOT_REGISTER_FOR_THE_WAITING_LIST_ON_THE_BATTLEFIELD_CASTLE_SIEGE_FORTRESS_SIEGE;
    @ClientString(id = 3534, message = "You may not register while using the instant zone.")
    public static SystemMessageId YOU_MAY_NOT_REGISTER_WHILE_USING_THE_INSTANT_ZONE;
    @ClientString(id = 3566, message = "Current location: $s1 / $s2 / $s3 (Magmeld, near Ancient City Arcan)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_MAGMELD_NEAR_ANCIENT_CITY_ARCAN;
    @ClientString(id = 3567, message = "Current location: $s1 / $s2 / $s3 (Magmeld, near Ancient City Arcan)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_MAGMELD_NEAR_ANCIENT_CITY_ARCAN_3567;
    @ClientString(id = 3574, message = "You cannot change the class because of identity crisis.")
    public static SystemMessageId YOU_CANNOT_CHANGE_THE_CLASS_BECAUSE_OF_IDENTITY_CRISIS;
    @ClientString(id = 3617, message = "Item has been stored successfully.")
    public static SystemMessageId ITEM_HAS_BEEN_STORED_SUCCESSFULLY;
    @ClientString(id = 3639, message = "$s1 Clan Reputation has been consumed.")
    public static SystemMessageId S1_CLAN_REPUTATION_HAS_BEEN_CONSUMED;
    @ClientString(id = 3640, message = "$s1 Fame has been consumed.")
    public static SystemMessageId S1_FAME_HAS_BEEN_CONSUMED;
    @ClientString(id = 3659, message = "You cannot change an attribute while using a private store or workshop.")
    public static SystemMessageId YOU_CANNOT_CHANGE_AN_ATTRIBUTE_WHILE_USING_A_PRIVATE_STORE_OR_WORKSHOP;
    @ClientString(id = 3661, message = "Changing attributes has been failed.")
    public static SystemMessageId CHANGING_ATTRIBUTES_HAS_BEEN_FAILED;
    @ClientString(id = 3664, message = "Current Location: $s1 / $s2 / $s3(Near the Magmeld Orbis Temple)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_THE_MAGMELD_ORBIS_TEMPLE;
    @ClientString(id = 3668, message = "<$s1>'s <$s2> attribute has successfully changed to <$s3> attribute.")
    public static SystemMessageId S1_S_S2_ATTRIBUTE_HAS_SUCCESSFULLY_CHANGED_TO_S3_ATTRIBUTE;
    @ClientString(id = 3669, message = "The item for changing an attribute does not exist.")
    public static SystemMessageId THE_ITEM_FOR_CHANGING_AN_ATTRIBUTE_DOES_NOT_EXIST;
    @ClientString(id = 3712, message = "Plunder skill has been already used on this target.")
    public static SystemMessageId PLUNDER_SKILL_HAS_BEEN_ALREADY_USED_ON_THIS_TARGET;
    @ClientString(id = 3713, message = "You can bond with a new mentee in $s1 day(s) $s2 hour(s) $s3 minute(s).")
    public static SystemMessageId YOU_CAN_BOND_WITH_A_NEW_MENTEE_IN_S1_DAY_S_S2_HOUR_S_S3_MINUTE_S;
    @ClientString(id = 3732, message = "You are now on the waiting list. You will automatically be teleported when the tournament starts, and will be removed from the waiting list if you log out. If you cancel registration (within the last minute of entering the arena after signing up) 30 times or more or forfeit after entering the arena 30 times or more during a cycle, you become ineligible for participation in the Ceremony of Chaos until the next cycle. All the buffs except the Vitality buff will be removed once you enter the arenas.")
    public static SystemMessageId YOU_ARE_NOW_ON_THE_WAITING_LIST_YOU_WILL_AUTOMATICALLY_BE_TELEPORTED_WHEN_THE_TOURNAMENT_STARTS_AND_WILL_BE_REMOVED_FROM_THE_WAITING_LIST_IF_YOU_LOG_OUT_IF_YOU_CANCEL_REGISTRATION_WITHIN_THE_LAST_MINUTE_OF_ENTERING_THE_ARENA_AFTER_SIGNING_UP_30_TIMES_OR_MORE_OR_FORFEIT_AFTER_ENTERING_THE_ARENA_30_TIMES_OR_MORE_DURING_A_CYCLE_YOU_BECOME_INELIGIBLE_FOR_PARTICIPATION_IN_THE_CEREMONY_OF_CHAOS_UNTIL_THE_NEXT_CYCLE_ALL_THE_BUFFS_EXCEPT_THE_VITALITY_BUFF_WILL_BE_REMOVED_ONCE_YOU_ENTER_THE_ARENAS;
    @ClientString(id = 3733, message = "Only characters level 85 or above may participate in the tournament.")
    public static SystemMessageId ONLY_CHARACTERS_LEVEL_85_OR_ABOVE_MAY_PARTICIPATE_IN_THE_TOURNAMENT;
    @ClientString(id = 3734, message = "There are too many challengers. You cannot participate now.")
    public static SystemMessageId THERE_ARE_TOO_MANY_CHALLENGERS_YOU_CANNOT_PARTICIPATE_NOW;
    @ClientString(id = 3736, message = "You have been taken off the wait list. You may only enter the wait list on Mon-Thurs every quarter of an hour for 5 minutes between 20:00 and 23:40. If you cancel registration or choose to forfeit after entering a match 30 times or more during a cycle, you must wait until the next cycle to participate in the Ceremony of Chaos. Upon entering the arena, all buffs excluding Vitality buffs are removed.")
    public static SystemMessageId YOU_HAVE_BEEN_TAKEN_OFF_THE_WAIT_LIST_YOU_MAY_ONLY_ENTER_THE_WAIT_LIST_ON_MON_THURS_EVERY_QUARTER_OF_AN_HOUR_FOR_5_MINUTES_BETWEEN_20_00_AND_23_40_IF_YOU_CANCEL_REGISTRATION_OR_CHOOSE_TO_FORFEIT_AFTER_ENTERING_A_MATCH_30_TIMES_OR_MORE_DURING_A_CYCLE_YOU_MUST_WAIT_UNTIL_THE_NEXT_CYCLE_TO_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_UPON_ENTERING_THE_ARENA_ALL_BUFFS_EXCLUDING_VITALITY_BUFFS_ARE_REMOVED;
    @ClientString(id = 3737, message = "You will be moved to the arena in $s1 second(s).")
    public static SystemMessageId YOU_WILL_BE_MOVED_TO_THE_ARENA_IN_S1_SECOND_S;
    @ClientString(id = 3741, message = "You cannot chat in the Ceremony of Chaos.")
    public static SystemMessageId YOU_CANNOT_CHAT_IN_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3742, message = "You cannot open a private store or workshop in the Ceremony of Chaos.")
    public static SystemMessageId YOU_CANNOT_OPEN_A_PRIVATE_STORE_OR_WORKSHOP_IN_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3744, message = "Prove your abilities.")
    public static SystemMessageId PROVE_YOUR_ABILITIES;
    @ClientString(id = 3745, message = "There are no allies here; everyone is an enemy.")
    public static SystemMessageId THERE_ARE_NO_ALLIES_HERE_EVERYONE_IS_AN_ENEMY;
    @ClientString(id = 3746, message = "It will be a lonely battle, but I wish you victory.")
    public static SystemMessageId IT_WILL_BE_A_LONELY_BATTLE_BUT_I_WISH_YOU_VICTORY;
    @ClientString(id = 3749, message = "In $s1 second(s), you will be moved to where you were before participating in the Ceremony of Chaos.")
    public static SystemMessageId IN_S1_SECOND_S_YOU_WILL_BE_MOVED_TO_WHERE_YOU_WERE_BEFORE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3765, message = "Current Location: $s1 / $s2 / $s3 (inside the Seed of Hellfire)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_INSIDE_THE_SEED_OF_HELLFIRE;
    @ClientString(id = 3774, message = "Only characters who are a part of a clan of level 3 or above may participate.")
    public static SystemMessageId ONLY_CHARACTERS_WHO_ARE_A_PART_OF_A_CLAN_OF_LEVEL_3_OR_ABOVE_MAY_PARTICIPATE;
    @ClientString(id = 3775, message = "Only characters who have completed the 3rd Class Transfer may participate.")
    public static SystemMessageId ONLY_CHARACTERS_WHO_HAVE_COMPLETED_THE_3RD_CLASS_TRANSFER_MAY_PARTICIPATE;
    @ClientString(id = 3777, message = "You are on the waiting list for the Ceremony of Chaos.")
    public static SystemMessageId YOU_ARE_ON_THE_WAITING_LIST_FOR_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3781, message = "Registration for the Ceremony of Chaos has begun.")
    public static SystemMessageId REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_BEGUN;
    @ClientString(id = 3782, message = "Registration for the Ceremony of Chaos has ended.")
    public static SystemMessageId REGISTRATION_FOR_THE_CEREMONY_OF_CHAOS_HAS_ENDED;
    @ClientString(id = 3786, message = "You cannot use this item in the tournament.")
    public static SystemMessageId YOU_CANNOT_USE_THIS_ITEM_IN_THE_TOURNAMENT;
    @ClientString(id = 3789, message = "You cannot invite a friend or party while participating in the Ceremony of Chaos.")
    public static SystemMessageId YOU_CANNOT_INVITE_A_FRIEND_OR_PARTY_WHILE_PARTICIPATING_IN_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 3811, message = "Because $c1 was killed by a clan member of $s2, Clan Reputation decreased by 1.")
    public static SystemMessageId BECAUSE_C1_WAS_KILLED_BY_A_CLAN_MEMBER_OF_S2_CLAN_REPUTATION_DECREASED_BY_1;
    @ClientString(id = 3812, message = "Because a clan member of $s1 was killed by $c2, Clan Reputation increased by 1.")
    public static SystemMessageId BECAUSE_A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_C2_CLAN_REPUTATION_INCREASED_BY_1;
    @ClientString(id = 3813, message = "Because Clan $s1 did not fight back for 1 week, the clan war was cancelled.")
    public static SystemMessageId BECAUSE_CLAN_S1_DID_NOT_FIGHT_BACK_FOR_1_WEEK_THE_CLAN_WAR_WAS_CANCELLED;
    @ClientString(id = 3814, message = "A clan war declared by Clan $s1 was cancelled.")
    public static SystemMessageId A_CLAN_WAR_DECLARED_BY_CLAN_S1_WAS_CANCELLED;
    @ClientString(id = 3815, message = "A clan member of $s1 was killed by your clan member. If your clan kills $s2 members of Clan $s1, a clan war with Clan $s1 will start.")
    public static SystemMessageId A_CLAN_MEMBER_OF_S1_WAS_KILLED_BY_YOUR_CLAN_MEMBER_IF_YOUR_CLAN_KILLS_S2_MEMBERS_OF_CLAN_S1_A_CLAN_WAR_WITH_CLAN_S1_WILL_START;
    @ClientString(id = 3849, message = "You obtained $s1 Oriana's Coins.")
    public static SystemMessageId YOU_OBTAINED_S1_ORIANA_S_COINS;
    @ClientString(id = 3853, message = "You cannot participate in the Ceremony of Chaos as a flying transformed object.")
    public static SystemMessageId YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_AS_A_FLYING_TRANSFORMED_OBJECT;
    @ClientString(id = 3890, message = "All buffs like Rosy Seductions and Art of Seduction will be removed. Sayha's Grace will remain.")
    public static SystemMessageId ALL_BUFFS_LIKE_ROSY_SEDUCTIONS_AND_ART_OF_SEDUCTION_WILL_BE_REMOVED_SAYHA_S_GRACE_WILL_REMAIN;
    @ClientString(id = 3901, message = "Current location: Last Imperial Tomb")
    public static SystemMessageId CURRENT_LOCATION_LAST_IMPERIAL_TOMB;
    @ClientString(id = 4031, message = "Only the clan leader or someone with rank management authority may register the clan.")
    public static SystemMessageId ONLY_THE_CLAN_LEADER_OR_SOMEONE_WITH_RANK_MANAGEMENT_AUTHORITY_MAY_REGISTER_THE_CLAN;
    @ClientString(id = 4038, message = "You may apply for entry after $s1 minute(s) due to cancelling your application.")
    public static SystemMessageId YOU_MAY_APPLY_FOR_ENTRY_AFTER_S1_MINUTE_S_DUE_TO_CANCELLING_YOUR_APPLICATION;
    @ClientString(id = 4039, message = "Entry application complete. Use Entry Application Info to check or cancel your application. Application is automatically cancelled after 30 days; if you cancel application, you cannot apply again for 5 minutes.")
    public static SystemMessageId ENTRY_APPLICATION_COMPLETE_USE_ENTRY_APPLICATION_INFO_TO_CHECK_OR_CANCEL_YOUR_APPLICATION_APPLICATION_IS_AUTOMATICALLY_CANCELLED_AFTER_30_DAYS_IF_YOU_CANCEL_APPLICATION_YOU_CANNOT_APPLY_AGAIN_FOR_5_MINUTES;
    @ClientString(id = 4040, message = "Entry application cancelled. You may apply to a new clan after 5 minutes.")
    public static SystemMessageId ENTRY_APPLICATION_CANCELLED_YOU_MAY_APPLY_TO_A_NEW_CLAN_AFTER_5_MINUTES;
    @ClientString(id = 4043, message = "Entered into waiting list. Name is automatically deleted after 30 days. If Delete from waiting list is used, you cannot enter names into the waiting list for 5 minutes.")
    public static SystemMessageId ENTERED_INTO_WAITING_LIST_NAME_IS_AUTOMATICALLY_DELETED_AFTER_30_DAYS_IF_DELETE_FROM_WAITING_LIST_IS_USED_YOU_CANNOT_ENTER_NAMES_INTO_THE_WAITING_LIST_FOR_5_MINUTES;
    @ClientString(id = 4085, message = "You cannot use the $s1 skill due to insufficient summon points.")
    public static SystemMessageId YOU_CANNOT_USE_THE_S1_SKILL_DUE_TO_INSUFFICIENT_SUMMON_POINTS;
    @ClientString(id = 4104, message = "Shout chat cannot be used by users Lv. $s1 or lower.")
    public static SystemMessageId SHOUT_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER;
    @ClientString(id = 4105, message = "Trade chat cannot be used by users Lv. $s1 or lower.")
    public static SystemMessageId TRADE_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER;
    @ClientString(id = 4106, message = "General chat cannot be used by users Lv. $s1 or lower.")
    public static SystemMessageId GENERAL_CHAT_CANNOT_BE_USED_BY_USERS_LV_S1_OR_LOWER;
    @ClientString(id = 4107, message = "Users Lv. $s1 or lower can respond to a whisper, but cannot initiate it.")
    public static SystemMessageId USERS_LV_S1_OR_LOWER_CAN_RESPOND_TO_A_WHISPER_BUT_CANNOT_INITIATE_IT;
    @ClientString(id = 4148, message = "You cannot destroy or crystallize items while enchanting attributes.")
    public static SystemMessageId YOU_CANNOT_DESTROY_OR_CRYSTALLIZE_ITEMS_WHILE_ENCHANTING_ATTRIBUTES;
    @ClientString(id = 4150, message = "Adena distribution has started.")
    public static SystemMessageId ADENA_DISTRIBUTION_HAS_STARTED;
    @ClientString(id = 4151, message = "Adena distribution has been cancelled.")
    public static SystemMessageId ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED;
    @ClientString(id = 4152, message = "The adena in possession has been decreased. Adena distribution has been cancelled.")
    public static SystemMessageId THE_ADENA_IN_POSSESSION_HAS_BEEN_DECREASED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED;
    @ClientString(id = 4153, message = "The distribution participants have changed. Adena distribution has been cancelled.")
    public static SystemMessageId THE_DISTRIBUTION_PARTICIPANTS_HAVE_CHANGED_ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED;
    @ClientString(id = 4154, message = "You cannot proceed as you are not in an alliance or party.")
    public static SystemMessageId YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_IN_AN_ALLIANCE_OR_PARTY;
    @ClientString(id = 4155, message = "You cannot proceed as you are not an alliance leader or party leader.")
    public static SystemMessageId YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_AN_ALLIANCE_LEADER_OR_PARTY_LEADER;
    @ClientString(id = 4156, message = "You cannot proceed as you are not a party leader.")
    public static SystemMessageId YOU_CANNOT_PROCEED_AS_YOU_ARE_NOT_A_PARTY_LEADER;
    @ClientString(id = 4157, message = "You do not have enough Adena.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_ADENA;
    @ClientString(id = 4161, message = "Distribution cannot proceed as there is insufficient Adena for distribution.")
    public static SystemMessageId DISTRIBUTION_CANNOT_PROCEED_AS_THERE_IS_INSUFFICIENT_ADENA_FOR_DISTRIBUTION;
    @ClientString(id = 4167, message = "Hair accessories will no longer be displayed.")
    public static SystemMessageId HAIR_ACCESSORIES_WILL_NO_LONGER_BE_DISPLAYED;
    @ClientString(id = 4168, message = "Hair accessories will be displayed from now on.")
    public static SystemMessageId HAIR_ACCESSORIES_WILL_BE_DISPLAYED_FROM_NOW_ON;
    @ClientString(id = 4172, message = "You cannot chat while participating in the Olympiad.")
    public static SystemMessageId YOU_CANNOT_CHAT_WHILE_PARTICIPATING_IN_THE_OLYMPIAD;
    @ClientString(id = 4174, message = "After about 1 minute, you will move to the Olympiad arena.")
    public static SystemMessageId AFTER_ABOUT_1_MINUTE_YOU_WILL_MOVE_TO_THE_OLYMPIAD_ARENA;
    @ClientString(id = 4175, message = "You will shortly move to the Olympiad arena.")
    public static SystemMessageId YOU_WILL_SHORTLY_MOVE_TO_THE_OLYMPIAD_ARENA;
    @ClientString(id = 4201, message = "You are not in a party.")
    public static SystemMessageId YOU_ARE_NOT_IN_A_PARTY;
    @ClientString(id = 4202, message = "You are not in a clan.")
    public static SystemMessageId YOU_ARE_NOT_IN_A_CLAN;
    @ClientString(id = 4203, message = "You are not in an alliance.")
    public static SystemMessageId YOU_ARE_NOT_IN_AN_ALLIANCE;
    @ClientString(id = 4204, message = "Only Heroes can enter the Hero channel.")
    public static SystemMessageId ONLY_HEROES_CAN_ENTER_THE_HERO_CHANNEL;
    @ClientString(id = 4209, message = "You consumed $s1 Raid Points.")
    public static SystemMessageId YOU_CONSUMED_S1_RAID_POINTS;
    @ClientString(id = 4211, message = "Not enough Raid Points.")
    public static SystemMessageId NOT_ENOUGH_RAID_POINTS;
    @ClientString(id = 4215, message = "You cannot participate in the Ceremony of Chaos while fishing.")
    public static SystemMessageId YOU_CANNOT_PARTICIPATE_IN_THE_CEREMONY_OF_CHAOS_WHILE_FISHING;
    @ClientString(id = 4217, message = "You cannot do that while in a private store or private workshop.")
    public static SystemMessageId YOU_CANNOT_DO_THAT_WHILE_IN_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
    @ClientString(id = 4223, message = "You cannot do that while trading.")
    public static SystemMessageId YOU_CANNOT_DO_THAT_WHILE_TRADING;
    @ClientString(id = 4226, message = "Frintezza is playing my victory song!")
    public static SystemMessageId FRINTEZZA_IS_PLAYING_MY_VICTORY_SONG;
    @ClientString(id = 4227, message = "Well, it's been nice knowing you. Shall we have the last dance?")
    public static SystemMessageId WELL_IT_S_BEEN_NICE_KNOWING_YOU_SHALL_WE_HAVE_THE_LAST_DANCE;
    @ClientString(id = 4228, message = "Back away! I will use Tauti's Cyclone.")
    public static SystemMessageId BACK_AWAY_I_WILL_USE_TAUTI_S_CYCLONE;
    @ClientString(id = 4229, message = "Magic and arrows, hm? Well, take a dose of Tauti's Typhoon!")
    public static SystemMessageId MAGIC_AND_ARROWS_HM_WELL_TAKE_A_DOSE_OF_TAUTI_S_TYPHOON;
    @ClientString(id = 4237, message = "You cannot equip $s1 without equipping a brooch.")
    public static SystemMessageId YOU_CANNOT_EQUIP_S1_WITHOUT_EQUIPPING_A_BROOCH;
    @ClientString(id = 4239, message = "You used World Chat up to today's limit. The usage count of World Chat is reset every day at 6:30.")
    public static SystemMessageId YOU_USED_WORLD_CHAT_UP_TO_TODAY_S_LIMIT_THE_USAGE_COUNT_OF_WORLD_CHAT_IS_RESET_EVERY_DAY_AT_6_30;
    @ClientString(id = 4240, message = "You must be Lv. $s1 or higher to use World Chat. You can also use it with VIP benefits.")
    public static SystemMessageId YOU_MUST_BE_LV_S1_OR_HIGHER_TO_USE_WORLD_CHAT_YOU_CAN_ALSO_USE_IT_WITH_VIP_BENEFITS;
    @ClientString(id = 4241, message = "You have $s1 sec. until you are able to use World Chat.")
    public static SystemMessageId YOU_HAVE_S1_SEC_UNTIL_YOU_ARE_ABLE_TO_USE_WORLD_CHAT;
    @ClientString(id = 4255, message = "Current location: $s1 / $s2 / $s3 (near Faeron Village)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_FAERON_VILLAGE;
    @ClientString(id = 4271, message = "Current location: $s1 / $s2 / $s3")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3;
    @ClientString(id = 4299, message = "You cannot change your subclass while registering for the Ceremony of Chaos.")
    public static SystemMessageId YOU_CANNOT_CHANGE_YOUR_SUBCLASS_WHILE_REGISTERING_FOR_THE_CEREMONY_OF_CHAOS;
    @ClientString(id = 4300, message = "Current location:  $s1 / $s2 / $s3 (Infinite Depths)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_INFINITE_DEPTHS;
    @ClientString(id = 4303, message = "Fishing will end because the conditions have not been met.")
    public static SystemMessageId FISHING_WILL_END_BECAUSE_THE_CONDITIONS_HAVE_NOT_BEEN_MET;
    @ClientString(id = 4313, message = "You do not meet the fishing level requirements.")
    public static SystemMessageId YOU_DO_NOT_MEET_THE_FISHING_LEVEL_REQUIREMENTS;
    @ClientString(id = 4320, message = "Location: $s1 / $s2 / $s3 (Ancient Talking Island Village)")
    public static SystemMessageId LOCATION_S1_S2_S3_ANCIENT_TALKING_ISLAND_VILLAGE;
    @ClientString(id = 4321, message = "You can redeem your reward $s1 minutes after logging in. $s2 minutes left.")
    public static SystemMessageId YOU_CAN_REDEEM_YOUR_REWARD_S1_MINUTES_AFTER_LOGGING_IN_S2_MINUTES_LEFT;
    @ClientString(id = 4337, message = "Rune insertion is impossible when private store and workshop are opened.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_WHEN_PRIVATE_STORE_AND_WORKSHOP_ARE_OPENED;
    @ClientString(id = 4338, message = "Rune insertion is impossible while in frozen state.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_FROZEN_STATE;
    @ClientString(id = 4339, message = "Rune insertion is impossible if the character is dead.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_IF_THE_CHARACTER_IS_DEAD;
    @ClientString(id = 4340, message = "Rune insertion is impossible during exchange.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_DURING_EXCHANGE;
    @ClientString(id = 4341, message = "Rune insertion is impossible while petrified.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_PETRIFIED;
    @ClientString(id = 4342, message = "Rune insertion is impossible during fishing.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_DURING_FISHING;
    @ClientString(id = 4343, message = "Rune insertion is impossible while sitting.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_SITTING;
    @ClientString(id = 4344, message = "Rune insertion is impossible while in combat.")
    public static SystemMessageId RUNE_INSERTION_IS_IMPOSSIBLE_WHILE_IN_COMBAT;
    @ClientString(id = 4349, message = "The rune does not fit.")
    public static SystemMessageId THE_RUNE_DOES_NOT_FIT;
    @ClientString(id = 4351, message = "Location: $s1 / $s2 / $s3 (Underground Gainak)")
    public static SystemMessageId LOCATION_S1_S2_S3_UNDERGROUND_GAINAK;
    @ClientString(id = 4352, message = "Location: $s1 / $s2 / $s3 (Forge of the Old Gods)")
    public static SystemMessageId LOCATION_S1_S2_S3_FORGE_OF_THE_OLD_GODS;
    @ClientString(id = 4353, message = "Location: $s1 / $s2 / $s3 (Old Schuttgart Castle)")
    public static SystemMessageId LOCATION_S1_S2_S3_OLD_SCHUTTGART_CASTLE;
    @ClientString(id = 4354, message = "Location: $s1 / $s2 / $s3 (Old Summer Labyrinth)")
    public static SystemMessageId LOCATION_S1_S2_S3_OLD_SUMMER_LABYRINTH;
    @ClientString(id = 4358, message = "Exchange is successful.")
    public static SystemMessageId EXCHANGE_IS_SUCCESSFUL;
    @ClientString(id = 4403, message = "Current location: $s1 / $s2 / $s3 (near Prison of Abyss (west))")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_PRISON_OF_ABYSS_WEST;
    @ClientString(id = 4404, message = "Current location: $s1 / $s2 / $s3 (near Prison of Abyss (east))")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_PRISON_OF_ABYSS_EAST;
    @ClientString(id = 4405, message = "Current location: $s1 / $s2 / $s3 (near Monster Arena)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_MONSTER_ARENA;
    @ClientString(id = 4407, message = "Current location: $s1 / $s2 / $s3 (near Prison of Abyss)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_PRISON_OF_ABYSS;
    @ClientString(id = 4436, message = "$s1 Territory")
    public static SystemMessageId S1_TERRITORY;
    @ClientString(id = 4471, message = "You obtained $s1 through Equipment Upgrade.")
    public static SystemMessageId YOU_OBTAINED_S1_THROUGH_EQUIPMENT_UPGRADE;
    @ClientString(id = 4472, message = "$c1, you obtained $s2 through Equipment Upgrade.")
    public static SystemMessageId C1_YOU_OBTAINED_S2_THROUGH_EQUIPMENT_UPGRADE;
    @ClientString(id = 4473, message = "Failed the operation.")
    public static SystemMessageId FAILED_THE_OPERATION;
    @ClientString(id = 4474, message = "Failed because the target item does not exist.")
    public static SystemMessageId FAILED_BECAUSE_THE_TARGET_ITEM_DOES_NOT_EXIST;
    @ClientString(id = 4475, message = "Failed because there are not enough ingredients.")
    public static SystemMessageId FAILED_BECAUSE_THERE_ARE_NOT_ENOUGH_INGREDIENTS;
    @ClientString(id = 4476, message = "Failed because there's not enough Adena.")
    public static SystemMessageId FAILED_BECAUSE_THERE_S_NOT_ENOUGH_ADENA;
    @ClientString(id = 4527, message = "You cannot use the Agathion's power because you are not wearing the left bracelet.")
    public static SystemMessageId YOU_CANNOT_USE_THE_AGATHION_S_POWER_BECAUSE_YOU_ARE_NOT_WEARING_THE_LEFT_BRACELET;
    @ClientString(id = 4535, message = "Current location: $s1/$s2/$s3 (Balthus Knight Barracks)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_BALTHUS_KNIGHT_BARRACKS;
    @ClientString(id = 4536, message = "Current location: $s1/$s2/$s3 (Hatchling Habitat)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_HATCHLING_HABITAT;
    @ClientString(id = 4537, message = "Current location: $s1/$s2/$s3 (Near Hatchling Habitat)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_NEAR_HATCHLING_HABITAT;
    @ClientString(id = 4538, message = "Current location: $s1/$s2/$s3 (Antharas' Nest)")
    public static SystemMessageId CURRENT_LOCATION_S1_S2_S3_ANTHARAS_NEST;
    @ClientString(id = 5106, message = "No Spirits are available.")
    public static SystemMessageId NO_SPIRITS_ARE_AVAILABLE;
    @ClientString(id = 5107, message = "<$s1> will be your attribute attack from now on.")
    public static SystemMessageId S1_WILL_BE_YOUR_ATTRIBUTE_ATTACK_FROM_NOW_ON;
    @ClientString(id = 5109, message = "<$s1> evolved to <$s2-Star>!")
    public static SystemMessageId S1_EVOLVED_TO_S2_STAR;
    @ClientString(id = 5111, message = "Extracted <$s1, x$s2> successfully!")
    public static SystemMessageId EXTRACTED_S1_S2_SUCCESSFULLY;
    @ClientString(id = 5113, message = "Cannot evolve/absorb/extract while using the private store/workshop.")
    public static SystemMessageId CANNOT_EVOLVE_ABSORB_EXTRACT_WHILE_USING_THE_PRIVATE_STORE_WORKSHOP;
    @ClientString(id = 5114, message = "Drain successful!")
    public static SystemMessageId DRAIN_SUCCESSFUL;
    @ClientString(id = 5116, message = "Characteristics were applied successfully.")
    public static SystemMessageId CHARACTERISTICS_WERE_APPLIED_SUCCESSFULLY;
    @ClientString(id = 5118, message = "Reset the selected Spirit's Characteristics successfully.")
    public static SystemMessageId RESET_THE_SELECTED_SPIRIT_S_CHARACTERISTICS_SUCCESSFULLY;
    @ClientString(id = 5144, message = "You do not have the materials required to Evolve.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EVOLVE;
    @ClientString(id = 5147, message = "You do not have the materials required to extract.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_EXTRACT;
    @ClientString(id = 5149, message = "You do not have the materials required to Absorb.")
    public static SystemMessageId YOU_DO_NOT_HAVE_THE_MATERIALS_REQUIRED_TO_ABSORB;
    @ClientString(id = 5150, message = "You have reached the highest level and cannot Absorb any further.")
    public static SystemMessageId YOU_HAVE_REACHED_THE_HIGHEST_LEVEL_AND_CANNOT_ABSORB_ANY_FURTHER;
    @ClientString(id = 6182, message = "You've already received the Attendance Check rewards.")
    public static SystemMessageId YOU_VE_ALREADY_RECEIVED_THE_ATTENDANCE_CHECK_REWARDS;
    @ClientString(id = 5172, message = "You have not learned Skill to use $s1.")
    public static SystemMessageId YOU_HAVE_NOT_LEARNED_SKILL_TO_USE_S1;
    @ClientString(id = 5163, message = "Inventory is full. Cannot Extract.")
    public static SystemMessageId INVENTORY_IS_FULL_CANNOT_EXTRACT;
    @ClientString(id = 5164, message = "Cannot Evolve during battle.")
    public static SystemMessageId CANNOT_EVOLVE_DURING_BATTLE;
    @ClientString(id = 5165, message = "Spirit cannot be Evolved.")
    public static SystemMessageId SPIRIT_CANNOT_BE_EVOLVED;
    @ClientString(id = 5167, message = "Cannot Drain during battle.")
    public static SystemMessageId CANNOT_DRAIN_DURING_BATTLE;
    @ClientString(id = 5168, message = "Cannot reset Spirit Characteristics during battle.")
    public static SystemMessageId CANNOT_RESET_SPIRIT_CHARACTERISTICS_DURING_BATTLE;
    @ClientString(id = 5170, message = "You have acquired $s1\u2019s $s2 Skill XP.")
    public static SystemMessageId YOU_HAVE_ACQUIRED_S1S_S2_SKILL_XP;
    @ClientString(id = 5171, message = "$s1 Attack Spirits have reached Level $s2.")
    public static SystemMessageId S1_ATTACK_SPIRITS_HAVE_REACHED_LEVEL_S2;
    @ClientString(id = 5173, message = "You do not have enough Skill XP to extract.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_SKILL_XP_TO_EXTRACT;
    @ClientString(id = 5176, message = "$s1 has inflicted $s3 ($s4 attribute damge) damage to $s2.")
    public static SystemMessageId S1_HAS_INFLICTED_S3_S4_ATTRIBUTE_DAMGE_DAMAGE_TO_S2;
    @ClientString(id = 5211, message = "No Artifact Book equipped. You cannot equip $s1.")
    public static SystemMessageId NO_ARTIFACT_BOOK_EQUIPPED_YOU_CANNOT_EQUIP_S1;
    @ClientString(id = 6004, message = "Enchant failed. The enchant skill for the corresponding item will be exactly retained.")
    public static SystemMessageId ENCHANT_FAILED_THE_ENCHANT_SKILL_FOR_THE_CORRESPONDING_ITEM_WILL_BE_EXACTLY_RETAINED;
    @ClientString(id = 6005, message = "You do not have enough NCoin.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_NCOIN;
    @ClientString(id = 6083, message = "You cannot use this system during trading, private store, and workshop setup.")
    public static SystemMessageId YOU_CANNOT_USE_THIS_SYSTEM_DURING_TRADING_PRIVATE_STORE_AND_WORKSHOP_SETUP;
    @ClientString(id = 6139, message = "You do not have enough tickets. You cannot continue the game.")
    public static SystemMessageId YOU_DO_NOT_HAVE_ENOUGH_TICKETS_YOU_CANNOT_CONTINUE_THE_GAME;
    @ClientString(id = 6140, message = "Your inventory is either full or overweight.")
    public static SystemMessageId YOUR_INVENTORY_IS_EITHER_FULL_OR_OVERWEIGHT;
    @ClientString(id = 6141, message = "Congratulations! $c1 has obtained $s2 of $s3 through Fortune Reading.")
    public static SystemMessageId CONGRATULATIONS_C1_HAS_OBTAINED_S2_OF_S3_THROUGH_FORTUNE_READING;
    @ClientString(id = 6178, message = "The Attendance Reward cannot be received because the inventory weight/quantity limit has been exceeded.")
    public static SystemMessageId THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHT_QUANTITY_LIMIT_HAS_BEEN_EXCEEDED;
    @ClientString(id = 6179, message = "Due to a system error, the Attendance Reward cannot be received. Please try again later by going to Menu > Attendance Check.")
    public static SystemMessageId DUE_TO_A_SYSTEM_ERROR_THE_ATTENDANCE_REWARD_CANNOT_BE_RECEIVED_PLEASE_TRY_AGAIN_LATER_BY_GOING_TO_MENU_ATTENDANCE_CHECK;
    @ClientString(id = 6181, message = "You've received your VIP Attendance Reward for Day $s1.")
    public static SystemMessageId YOU_VE_RECEIVED_YOUR_VIP_ATTENDANCE_REWARD_FOR_DAY_S1;
    @ClientString(id = 6183, message = "Your VIP level is too low to receive the reward.")
    public static SystemMessageId YOUR_VIP_LEVEL_IS_TOO_LOW_TO_RECEIVE_THE_REWARD;
    @ClientString(id = 6501, message = "You cannot bookmark this location because you do not have a My Teleport Flag.")
    public static SystemMessageId YOU_CANNOT_BOOKMARK_THIS_LOCATION_BECAUSE_YOU_DO_NOT_HAVE_A_MY_TELEPORT_FLAG;
    @ClientString(id = 6803, message = "Please, enter the authentication code in time to continue playing.")
    public static SystemMessageId PLEASE_ENTER_THE_AUTHENTICATION_CODE_IN_TIME_TO_CONTINUE_PLAYING;
    @ClientString(id = 6805, message = "Wrong authentication code. If you enter the wrong code $s1 time(s), the system will qualify you as a prohibited software user and charge a penalty. (Attempts left: $s2.)")
    public static SystemMessageId WRONG_AUTHENTICATION_CODE_IF_YOU_ENTER_THE_WRONG_CODE_S1_TIME_S_THE_SYSTEM_WILL_QUALIFY_YOU_AS_A_PROHIBITED_SOFTWARE_USER_AND_CHARGE_A_PENALTY_ATTEMPTS_LEFT_S2;
    @ClientString(id = 6807, message = "Identification completed. Have a good time with! Thank you!")
    public static SystemMessageId IDENTIFICATION_COMPLETED_HAVE_A_GOOD_TIME_WITH_THANK_YOU;
    @ClientString(id = 6808, message = "If a user enters a wrong authentication code 3 times in a row or does not enter the code in time, the system will qualify him as a rule breaker and charge his account with a penalty ($s1).")
    public static SystemMessageId IF_A_USER_ENTERS_A_WRONG_AUTHENTICATION_CODE_3_TIMES_IN_A_ROW_OR_DOES_NOT_ENTER_THE_CODE_IN_TIME_THE_SYSTEM_WILL_QUALIFY_HIM_AS_A_RULE_BREAKER_AND_CHARGE_HIS_ACCOUNT_WITH_A_PENALTY_S1;
    @ClientString(id = 6826, message = "Your clan has achieved login bonus Lv. $s1.")
    public static SystemMessageId YOUR_CLAN_HAS_ACHIEVED_LOGIN_BONUS_LV_S1;
    @ClientString(id = 6827, message = "Your clan has achieved hunting bonus Lv. $s1.")
    public static SystemMessageId YOUR_CLAN_HAS_ACHIEVED_HUNTING_BONUS_LV_S1;
    @ClientString(id = 6840, message = "You can collect a collection effect again after $s1 minutes.")
    public static SystemMessageId YOU_CAN_COLLECT_A_COLLECTION_EFFECT_AGAIN_AFTER_S1_MINUTES;
    @ClientString(id = 6841, message = "This Collection effect is already active.")
    public static SystemMessageId THIS_COLLECTION_EFFECT_IS_ALREADY_ACTIVE;
    @ClientString(id = 6842, message = "Cannot activate the effect. The Collection is incomplete.")
    public static SystemMessageId CANNOT_ACTIVATE_THE_EFFECT_THE_COLLECTION_IS_INCOMPLETE;
    @ClientString(id = 6846, message = "Cannot use Sealbooks and evolve or extract Transformations while using a private store or private workshop.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_USING_A_PRIVATE_STORE_OR_PRIVATE_WORKSHOP;
    @ClientString(id = 6848, message = "You don\u2019t have necessary items.")
    public static SystemMessageId YOU_DONT_HAVE_NECESSARY_ITEMS;
    @ClientString(id = 6849, message = "Cannot use Sealbooks and evolve or extract Transformations while frozen.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_FROZEN;
    @ClientString(id = 6850, message = "Cannot use Sealbooks and evolve or extract Transformations when dead.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHEN_DEAD;
    @ClientString(id = 6851, message = "Cannot use Sealbooks and evolve or extract Transformations during exchange.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_DURING_EXCHANGE;
    @ClientString(id = 6852, message = "Cannot use Sealbooks and evolve or extract Transformations while petrified.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_PETRIFIED;
    @ClientString(id = 6853, message = "Cannot use Sealbooks and evolve or extract Transformations while fishing.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_FISHING;
    @ClientString(id = 6854, message = "Cannot use Sealbooks and evolve or extract Transformations while sitting.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_WHILE_SITTING;
    @ClientString(id = 6856, message = "This Transformation cannot evolve.")
    public static SystemMessageId THIS_TRANSFORMATION_CANNOT_EVOLVE;
    @ClientString(id = 6858, message = "Cannot use Sealbooks and evolve or extract Transformations during a battle.")
    public static SystemMessageId CANNOT_USE_SEALBOOKS_AND_EVOLVE_OR_EXTRACT_TRANSFORMATIONS_DURING_A_BATTLE;
    @ClientString(id = 6859, message = "This Transformation cannot be extracted.")
    public static SystemMessageId THIS_TRANSFORMATION_CANNOT_BE_EXTRACTED;
    @ClientString(id = 6860, message = "Not enough material to extract.")
    public static SystemMessageId NOT_ENOUGH_MATERIAL_TO_EXTRACT;
    @ClientString(id = 6861, message = "Not enough space in the inventory. Please make more room and try again.")
    public static SystemMessageId NOT_ENOUGH_SPACE_IN_THE_INVENTORY_PLEASE_MAKE_MORE_ROOM_AND_TRY_AGAIN;
    @ClientString(id = 6862, message = "Cannot edit the Lock Transformation setting during a battle.")
    public static SystemMessageId CANNOT_EDIT_THE_LOCK_TRANSFORMATION_SETTING_DURING_A_BATTLE;
    @ClientString(id = 6864, message = "You can select another Collection effect $s1 seconds later.")
    public static SystemMessageId YOU_CAN_SELECT_ANOTHER_COLLECTION_EFFECT_S1_SECONDS_LATER;
    @ClientString(id = 7090, message = "Cannot locate the selected foe. The foe is not online.")
    public static SystemMessageId CANNOT_LOCATE_THE_SELECTED_FOE_THE_FOE_IS_NOT_ONLINE;
    @ClientString(id = 7113, message = "Not enough L2 Coins to buy it.")
    public static SystemMessageId NOT_ENOUGH_L2_COINS_TO_BUY_IT;
    @ClientString(id = 7238, message = "You are too far way to trade.")
    public static SystemMessageId YOU_ARE_TOO_FAR_WAY_TO_TRADE;
    @ClientString(id = 7323, message = "Learned $s1 Lv. $s2.")
    public static SystemMessageId LEARNED_S1_LV_S2;
    @ClientString(id = 13002, message = "Only characters of level 70 or higher who have completed the 2nd class transfer can use this command.")
    public static SystemMessageId ONLY_CHARACTERS_OF_LEVEL_70_OR_HIGHER_WHO_HAVE_COMPLETED_THE_2ND_CLASS_TRANSFER_CAN_USE_THIS_COMMAND;
    @ClientString(id = 5288, message = "You can't teleport in this area.")
    public static SystemMessageId YOU_CAN_T_TELEPORT_IN_THIS_AREA;
    private static final IntMap<SystemMessageId> VALUES;
    private final int _id;
    private String _name;
    private byte _params;
    private SystemMessage _staticSystemMessage;
    
    private SystemMessageId(final int id) {
        this._id = id;
    }
    
    private static void buildFastLookupTable() {
        final Field[] declaredFields;
        final Field[] fields = declaredFields = SystemMessageId.class.getDeclaredFields();
        for (final Field field : declaredFields) {
            final int mod = field.getModifiers();
            if (Modifier.isStatic(mod) && Modifier.isPublic(mod) && field.getType().equals(SystemMessageId.class) && field.isAnnotationPresent(ClientString.class)) {
                try {
                    final ClientString annotation = field.getAnnotationsByType(ClientString.class)[0];
                    final SystemMessageId smId = new SystemMessageId(annotation.id());
                    smId.setName(annotation.message());
                    smId.setParamCount(parseMessageParameters(field.getName()));
                    field.set(null, smId);
                    SystemMessageId.VALUES.put(smId.getId(), (Object)smId);
                }
                catch (Exception e) {
                    SystemMessageId.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, field.getName()), (Throwable)e);
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
    
    public static SystemMessageId getSystemMessageId(final int id) {
        final SystemMessageId smi = getSystemMessageIdInternal(id);
        return (smi == null) ? new SystemMessageId(id) : smi;
    }
    
    private static SystemMessageId getSystemMessageIdInternal(final int id) {
        return (SystemMessageId)SystemMessageId.VALUES.get(id);
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
            this._staticSystemMessage = null;
        }
        this._params = (byte)params;
    }
    
    public final SystemMessage getStaticSystemMessage() {
        return this._staticSystemMessage;
    }
    
    public final void setStaticSystemMessage(final SystemMessage sm) {
        this._staticSystemMessage = sm;
    }
    
    @Override
    public final String toString() {
        return invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, this.getId(), this.getName());
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)SystemMessageId.class);
        VALUES = (IntMap)new HashIntMap();
        buildFastLookupTable();
    }
}
