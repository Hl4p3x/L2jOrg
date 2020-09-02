// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.events;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.holders.MovieHolder;
import org.l2j.gameserver.enums.Movie;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.data.xml.DoorDataManager;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.util.MinionList;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Trap;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.spawns.SpawnGroup;
import org.l2j.gameserver.model.spawns.SpawnTemplate;
import io.github.joealisson.primitive.Containers;
import org.l2j.gameserver.model.instancezone.InstanceTemplate;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.world.zone.ZoneManager;
import java.util.Collection;
import java.util.ArrayList;
import org.l2j.gameserver.model.events.listeners.DummyEventListener;
import org.l2j.gameserver.model.events.listeners.AnnotationEventListener;
import org.l2j.gameserver.model.events.listeners.RunnableEventListener;
import org.l2j.gameserver.model.events.listeners.FunctionEventListener;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceStatusChange;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceLeave;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceEnter;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceDestroy;
import org.l2j.gameserver.model.events.impl.instance.OnInstanceCreated;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionCancel;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerProfessionChange;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeFinish;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeOwnerChange;
import org.l2j.gameserver.model.events.impl.sieges.OnCastleSiegeStart;
import org.l2j.gameserver.model.events.impl.olympiad.OnOlympiadMatchResult;
import org.l2j.gameserver.model.events.impl.item.OnItemTalk;
import org.l2j.gameserver.model.events.impl.item.OnItemBypassEvent;
import org.l2j.gameserver.model.events.impl.character.player.OnTrapAction;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneExit;
import org.l2j.gameserver.model.events.impl.character.OnCreatureZoneEnter;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogin;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonTalk;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonSpawn;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSkillLearn;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAggroRangeEnter;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableAttack;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableFactionCall;
import org.l2j.gameserver.model.events.impl.character.OnCreatureSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCreatureSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcCanBeSeen;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableHate;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveRouteFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcMoveFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcEventReceived;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcDespawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSpawn;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillFinished;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcSkillSee;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleport;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcFirstTalk;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttacked;
import org.l2j.gameserver.model.events.returns.AbstractEventReturn;
import org.l2j.gameserver.model.events.impl.character.OnCreatureDeath;
import java.util.function.Function;
import java.util.Objects;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.impl.character.npc.OnAttackableKill;
import java.util.function.Consumer;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import java.lang.reflect.Method;
import io.github.joealisson.primitive.IntCollection;
import io.github.joealisson.primitive.CHashIntMap;
import org.l2j.gameserver.model.events.annotations.Priority;
import org.l2j.gameserver.model.events.annotations.NpcLevelRanges;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.model.events.annotations.NpcLevelRange;
import org.l2j.gameserver.model.events.annotations.Ranges;
import org.l2j.gameserver.model.events.annotations.Range;
import org.l2j.gameserver.model.events.annotations.Ids;
import org.l2j.gameserver.model.events.annotations.Id;
import java.util.Arrays;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import java.lang.annotation.Annotation;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import io.github.joealisson.primitive.HashIntSet;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.events.timers.TimerHolder;
import org.l2j.gameserver.model.interfaces.ILocational;
import org.l2j.gameserver.network.serverpackets.SpecialCamera;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.WorldTimeController;
import org.l2j.gameserver.enums.InventorySlot;
import java.util.List;
import org.l2j.gameserver.instancemanager.PcCafePointsManager;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.PlaySound;
import org.l2j.gameserver.model.instancezone.Instance;
import java.util.Iterator;
import java.util.Set;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.network.serverpackets.InventoryUpdate;
import org.l2j.gameserver.model.item.enchant.attribute.AttributeHolder;
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.enums.QuestSound;
import org.l2j.gameserver.network.serverpackets.ExBloodyCoinCount;
import org.l2j.gameserver.network.serverpackets.ExAdenaInvenCount;
import org.l2j.gameserver.network.serverpackets.ExUserInfoInvenWeight;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.instance.Player;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.EnumMap;
import org.l2j.gameserver.model.events.listeners.AbstractEventListener;
import java.util.Queue;
import io.github.joealisson.primitive.IntSet;
import java.util.Map;
import org.slf4j.Logger;
import org.l2j.gameserver.model.events.timers.IEventTimerCancel;
import org.l2j.gameserver.model.events.timers.IEventTimerEvent;
import org.l2j.gameserver.engine.scripting.ManagedScript;

public abstract class AbstractScript extends ManagedScript implements IEventTimerEvent<String>, IEventTimerCancel<String>
{
    protected static final Logger LOGGER;
    private final Map<ListenerRegisterType, IntSet> _registeredIds;
    private final Queue<AbstractEventListener> _listeners;
    private volatile TimerExecutor<String> _timerExecutor;
    
    protected AbstractScript() {
        this._registeredIds = new EnumMap<ListenerRegisterType, IntSet>(ListenerRegisterType.class);
        this._listeners = new PriorityBlockingQueue<AbstractEventListener>();
        this.initializeAnnotationListeners();
    }
    
    public static void showOnScreenMsg(final Player player, final String text, final int time) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new ExShowScreenMessage(text, time));
    }
    
    public static void showOnScreenMsg(final Player player, final NpcStringId npcString, final int position, final int time, final String... params) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new ExShowScreenMessage(npcString, position, time, params));
    }
    
    public static void showOnScreenMsg(final Player player, final NpcStringId npcString, final int position, final int time, final boolean showEffect, final String... params) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new ExShowScreenMessage(npcString, position, time, showEffect, params));
    }
    
    public static void showOnScreenMsg(final Player player, final SystemMessageId systemMsg, final int position, final int time, final String... params) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new ExShowScreenMessage(systemMsg, position, time, params));
    }
    
    public static Npc addSpawn(final int npcId, final IPositionable pos) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), false, 0L, false, 0);
    }
    
    public static Npc addSpawn(final Npc summoner, final int npcId, final IPositionable pos, final boolean randomOffset, final long despawnDelay) {
        return addSpawn(summoner, npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, false, 0);
    }
    
    public static Npc addSpawn(final int npcId, final IPositionable pos, final boolean isSummonSpawn) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), false, 0L, isSummonSpawn, 0);
    }
    
    public static Npc addSpawn(final int npcId, final IPositionable pos, final boolean randomOffset, final long despawnDelay) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, false, 0);
    }
    
    public static Npc addSpawn(final int npcId, final IPositionable pos, final boolean randomOffset, final long despawnDelay, final boolean isSummonSpawn) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, isSummonSpawn, 0);
    }
    
    public static Npc addSpawn(final Npc summoner, final int npcId, final IPositionable pos, final boolean randomOffset, final int instanceId) {
        return addSpawn(summoner, npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, 0L, false, instanceId);
    }
    
    public static Npc addSpawn(final int npcId, final IPositionable pos, final boolean randomOffset, final long despawnDelay, final boolean isSummonSpawn, final int instanceId) {
        return addSpawn(npcId, pos.getX(), pos.getY(), pos.getZ(), pos.getHeading(), randomOffset, despawnDelay, isSummonSpawn, instanceId);
    }
    
    public static Npc addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffset, final long despawnDelay) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, false, 0);
    }
    
    public static Npc addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffset, final long despawnDelay, final boolean isSummonSpawn) {
        return addSpawn(npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, 0);
    }
    
    public static Npc addSpawn(final int npcId, final int x, final int y, final int z, final int heading, final boolean randomOffset, final long despawnDelay, final boolean isSummonSpawn, final int instanceId) {
        return addSpawn(null, npcId, x, y, z, heading, randomOffset, despawnDelay, isSummonSpawn, instanceId);
    }
    
    public static Npc addSpawn(final Npc summoner, final int npcId, int x, int y, final int z, final int heading, final boolean randomOffset, final long despawnDelay, final boolean isSummonSpawn, final int instance) {
        try {
            final Spawn spawn = new Spawn(npcId);
            if (x == 0 && y == 0) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(I)Ljava/lang/String;, npcId));
                return null;
            }
            if (randomOffset) {
                int offset = Rnd.get(50, 100);
                if (Rnd.nextBoolean()) {
                    offset *= -1;
                }
                x += offset;
                offset = Rnd.get(50, 100);
                if (Rnd.nextBoolean()) {
                    offset *= -1;
                }
                y += offset;
            }
            spawn.setInstanceId(instance);
            spawn.setHeading(heading);
            spawn.setXYZ(x, y, z);
            spawn.stopRespawn();
            final Npc npc = spawn.doSpawn(isSummonSpawn);
            if (despawnDelay > 0L) {
                npc.scheduleDespawn(despawnDelay);
            }
            if (summoner != null) {
                summoner.addSummonedNpc(npc);
            }
            return npc;
        }
        catch (Exception e) {
            AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(ILjava/lang/String;)Ljava/lang/String;, npcId, e.getMessage()));
            return null;
        }
    }
    
    public static long getQuestItemsCount(final Player player, final int itemId) {
        return player.getInventory().getInventoryItemCount(itemId, -1);
    }
    
    protected static boolean hasItem(final Player player, final ItemHolder item) {
        return hasItem(player, item, true);
    }
    
    protected static boolean hasItem(final Player player, final ItemHolder item, final boolean checkCount) {
        if (item == null) {
            return false;
        }
        if (checkCount) {
            return getQuestItemsCount(player, item.getId()) >= item.getCount();
        }
        return hasQuestItems(player, item.getId());
    }
    
    protected static boolean hasAllItems(final Player player, final boolean checkCount, final ItemHolder... itemList) {
        if (itemList == null || itemList.length == 0) {
            return false;
        }
        for (final ItemHolder item : itemList) {
            if (!hasItem(player, item, checkCount)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean hasQuestItems(final Player player, final int itemId) {
        return player.getInventory().getItemByItemId(itemId) != null;
    }
    
    public static boolean hasQuestItems(final Player player, final int... itemIds) {
        if (itemIds == null || itemIds.length == 0) {
            return false;
        }
        final PlayerInventory inv = player.getInventory();
        for (final int itemId : itemIds) {
            if (inv.getItemByItemId(itemId) == null) {
                return false;
            }
        }
        return true;
    }
    
    public static int getEnchantLevel(final Player player, final int itemId) {
        final Item enchantedItem = player.getInventory().getItemByItemId(itemId);
        if (enchantedItem == null) {
            return 0;
        }
        return enchantedItem.getEnchantLevel();
    }
    
    public static void rewardItems(final Player player, final ItemHolder holder) {
        rewardItems(player, holder.getId(), holder.getCount());
    }
    
    public static void rewardItems(final Player player, final int itemId, long count) {
        if (player.isSimulatingTalking()) {
            return;
        }
        if (count <= 0L) {
            return;
        }
        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item == null) {
            return;
        }
        try {
            if (itemId == 57) {
                count *= (long)Config.RATE_QUEST_REWARD_ADENA;
            }
            else if (Config.RATE_QUEST_REWARD_USE_MULTIPLIERS) {
                if (item instanceof EtcItem) {
                    switch (((EtcItem)item).getItemType()) {
                        case POTION: {
                            count *= (long)Config.RATE_QUEST_REWARD_POTION;
                            break;
                        }
                        case ENCHANT_WEAPON:
                        case ENCHANT_ARMOR:
                        case SCROLL: {
                            count *= (long)Config.RATE_QUEST_REWARD_SCROLL;
                            break;
                        }
                        case RECIPE: {
                            count *= (long)Config.RATE_QUEST_REWARD_RECIPE;
                            break;
                        }
                        case MATERIAL: {
                            count *= (long)Config.RATE_QUEST_REWARD_MATERIAL;
                            break;
                        }
                        default: {
                            count *= (long)Config.RATE_QUEST_REWARD;
                            break;
                        }
                    }
                }
            }
            else {
                count *= (long)Config.RATE_QUEST_REWARD;
            }
        }
        catch (Exception e) {
            count = Long.MAX_VALUE;
        }
        final Item itemInstance = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
        if (itemInstance == null) {
            return;
        }
        sendItemGetMessage(player, itemInstance, count);
    }
    
    private static void sendItemGetMessage(final Player player, final Item item, final long count) {
        if (item.getId() == 57) {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1_ADENA);
            smsg.addLong(count);
            player.sendPacket(smsg);
        }
        else if (count > 1L) {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S2_S1_S);
            smsg.addItemName(item);
            smsg.addLong(count);
            player.sendPacket(smsg);
        }
        else {
            final SystemMessage smsg = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_EARNED_S1);
            smsg.addItemName(item);
            player.sendPacket(smsg);
        }
        player.sendPacket(new ExUserInfoInvenWeight(player));
        player.sendPacket(new ExAdenaInvenCount(player));
        player.sendPacket(new ExBloodyCoinCount());
    }
    
    public static void giveItems(final Player player, final int itemId, final long count) {
        giveItems(player, itemId, count, 0, false);
    }
    
    public static void giveItems(final Player player, final int itemId, final long count, final boolean playSound) {
        giveItems(player, itemId, count, 0, playSound);
    }
    
    protected static void giveItems(final Player player, final ItemHolder holder) {
        giveItems(player, holder.getId(), holder.getCount());
    }
    
    public static void giveItems(final Player player, final int itemId, final long count, final int enchantlevel, final boolean playSound) {
        if (player.isSimulatingTalking()) {
            return;
        }
        if (count <= 0L) {
            return;
        }
        final Item item = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
        if (item == null) {
            return;
        }
        if (enchantlevel > 0 && itemId != 57) {
            item.setEnchantLevel(enchantlevel);
        }
        if (playSound) {
            playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        }
        sendItemGetMessage(player, item, count);
    }
    
    public static void giveItems(final Player player, final int itemId, final long count, final AttributeType attributeType, final int attributeValue) {
        if (player.isSimulatingTalking()) {
            return;
        }
        if (count <= 0L) {
            return;
        }
        final Item item = player.getInventory().addItem("Quest", itemId, count, player, player.getTarget());
        if (item == null) {
            return;
        }
        if (attributeType != null && attributeValue > 0) {
            item.setAttribute(new AttributeHolder(attributeType, attributeValue), true);
            if (item.isEquipped()) {
                player.getStats().recalculateStats(true);
            }
            final InventoryUpdate iu = new InventoryUpdate();
            iu.addModifiedItem(item);
            player.sendInventoryUpdate(iu);
        }
        sendItemGetMessage(player, item, count);
    }
    
    public static boolean giveItemRandomly(final Player player, final int itemId, final long amountToGive, final long limit, final double dropChance, final boolean playSound) {
        return giveItemRandomly(player, null, itemId, amountToGive, amountToGive, limit, dropChance, playSound);
    }
    
    public static boolean giveItemRandomly(final Player player, final Npc npc, final int itemId, final long amountToGive, final long limit, final double dropChance, final boolean playSound) {
        return giveItemRandomly(player, npc, itemId, amountToGive, amountToGive, limit, dropChance, playSound);
    }
    
    public static boolean giveItemRandomly(final Player player, final Npc npc, final int itemId, long minAmount, long maxAmount, final long limit, double dropChance, final boolean playSound) {
        if (player.isSimulatingTalking()) {
            return false;
        }
        final long currentCount = getQuestItemsCount(player, itemId);
        if (limit > 0L && currentCount >= limit) {
            return true;
        }
        minAmount *= (long)Config.RATE_QUEST_DROP;
        maxAmount *= (long)Config.RATE_QUEST_DROP;
        dropChance *= Config.RATE_QUEST_DROP;
        if (npc != null && Config.CHAMPION_ENABLE && npc.isChampion()) {
            if (itemId == 57 || itemId == 5575) {
                dropChance *= Config.CHAMPION_ADENAS_REWARDS_CHANCE;
                minAmount *= (long)Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
                maxAmount *= (long)Config.CHAMPION_ADENAS_REWARDS_AMOUNT;
            }
            else {
                dropChance *= Config.CHAMPION_REWARDS_CHANCE;
                minAmount *= (long)Config.CHAMPION_REWARDS_AMOUNT;
                maxAmount *= (long)Config.CHAMPION_REWARDS_AMOUNT;
            }
        }
        long amountToGive = (minAmount == maxAmount) ? minAmount : Rnd.get(minAmount, maxAmount);
        final double random = Rnd.nextDouble();
        if (dropChance >= random && amountToGive > 0L && player.getInventory().validateCapacityByItemId(itemId)) {
            if (limit > 0L && currentCount + amountToGive > limit) {
                amountToGive = limit - currentCount;
            }
            if (player.addItem("Quest", itemId, amountToGive, npc, true) != null) {
                if (currentCount + amountToGive == limit) {
                    if (playSound) {
                        playSound(player, QuestSound.ITEMSOUND_QUEST_MIDDLE);
                    }
                    return true;
                }
                if (playSound) {
                    playSound(player, QuestSound.ITEMSOUND_QUEST_ITEMGET);
                }
                return limit <= 0L;
            }
        }
        return false;
    }
    
    public static boolean takeItems(final Player player, final int itemId, long amount) {
        if (player.isSimulatingTalking()) {
            return false;
        }
        final Item item = player.getInventory().getItemByItemId(itemId);
        if (item == null) {
            return false;
        }
        if (amount < 0L || amount > item.getCount()) {
            amount = item.getCount();
        }
        if (item.isEquipped()) {
            final Set<Item> unequiped = player.getInventory().unEquipItemInBodySlotAndRecord(item.getBodyPart());
            final InventoryUpdate iu = new InventoryUpdate();
            for (final Item itm : unequiped) {
                iu.addModifiedItem(itm);
            }
            player.sendInventoryUpdate(iu);
            player.broadcastUserInfo();
        }
        return player.destroyItemByItemId("Quest", itemId, amount, player, true);
    }
    
    protected static boolean takeItem(final Player player, final ItemHolder holder) {
        return holder != null && takeItems(player, holder.getId(), holder.getCount());
    }
    
    protected static boolean takeAllItems(final Player player, final ItemHolder... itemList) {
        if (player.isSimulatingTalking()) {
            return false;
        }
        if (itemList == null || itemList.length == 0) {
            return false;
        }
        if (!hasAllItems(player, true, itemList)) {
            return false;
        }
        for (final ItemHolder item : itemList) {
            if (!takeItem(player, item)) {
                return false;
            }
        }
        return true;
    }
    
    public static boolean takeItems(final Player player, final int amount, final int... itemIds) {
        if (player.isSimulatingTalking()) {
            return false;
        }
        boolean check = true;
        if (itemIds != null) {
            for (final int item : itemIds) {
                check &= takeItems(player, item, amount);
            }
        }
        return check;
    }
    
    public static void playSound(final Instance world, final String sound) {
        world.broadcastPacket(new PlaySound(sound));
    }
    
    public static void playSound(final Player player, final String sound) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(QuestSound.getSound(sound));
    }
    
    public static void playSound(final Player player, final QuestSound sound) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(sound.getPacket());
    }
    
    public static void addExpAndSp(final Player player, final long exp, final int sp) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.addExpAndSp((double)(long)player.getStats().getValue(Stat.EXPSP_RATE, exp * Config.RATE_QUEST_REWARD_XP), (int)player.getStats().getValue(Stat.EXPSP_RATE, sp * Config.RATE_QUEST_REWARD_SP));
        PcCafePointsManager.getInstance().givePcCafePoint(player, (double)(long)(exp * Config.RATE_QUEST_REWARD_XP));
    }
    
    public static int getRandom(final int max) {
        return Rnd.get(max);
    }
    
    public static int getRandom(final int min, final int max) {
        return Rnd.get(min, max);
    }
    
    public static boolean getRandomBoolean() {
        return Rnd.nextBoolean();
    }
    
    public static <T> T getRandomEntry(final T... array) {
        if (array.length == 0) {
            return null;
        }
        return array[getRandom(array.length)];
    }
    
    public static <T> T getRandomEntry(final List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        return list.get(getRandom(list.size()));
    }
    
    public static int getRandomEntry(final int... array) {
        return array[getRandom(array.length)];
    }
    
    public static int getItemEquipped(final Player player, final InventorySlot slot) {
        return player.getInventory().getPaperdollItemId(slot);
    }
    
    public static int getGameTicks() {
        return WorldTimeController.getInstance().getGameTicks();
    }
    
    public static void specialCamera(final Player player, final Creature creature, final int force, final int angle1, final int angle2, final int time, final int range, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle));
    }
    
    public static void specialCameraEx(final Player player, final Creature creature, final int force, final int angle1, final int angle2, final int time, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new SpecialCamera(creature, player, force, angle1, angle2, time, duration, relYaw, relPitch, isWide, relAngle));
    }
    
    public static void specialCamera3(final Player player, final Creature creature, final int force, final int angle1, final int angle2, final int time, final int range, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle, final int unk) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.sendPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle, unk));
    }
    
    public static void specialCamera(final Instance world, final Creature creature, final int force, final int angle1, final int angle2, final int time, final int range, final int duration, final int relYaw, final int relPitch, final int isWide, final int relAngle, final int unk) {
        world.broadcastPacket(new SpecialCamera(creature, force, angle1, angle2, time, range, duration, relYaw, relPitch, isWide, relAngle, unk));
    }
    
    public static void addRadar(final Player player, final ILocational loc) {
        addRadar(player, loc.getX(), loc.getY(), loc.getZ());
    }
    
    public static void addRadar(final Player player, final int x, final int y, final int z) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.getRadar().addMarker(x, y, z);
    }
    
    @Override
    public final void onTimerEvent(final TimerHolder<String> holder) {
        this.onTimerEvent(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }
    
    @Override
    public final void onTimerCancel(final TimerHolder<String> holder) {
        this.onTimerCancel(holder.getEvent(), holder.getParams(), holder.getNpc(), holder.getPlayer());
    }
    
    public void onTimerEvent(final String event, final StatsSet params, final Npc npc, final Player player) {
        AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;Lorg/l2j/gameserver/model/actor/Npc;Lorg/l2j/gameserver/model/actor/instance/Player;)Ljava/lang/String;, this.getClass().getSimpleName(), event, npc, player));
    }
    
    public void onTimerCancel(final String event, final StatsSet params, final Npc npc, final Player player) {
    }
    
    public TimerExecutor<String> getTimers() {
        if (this._timerExecutor == null) {
            synchronized (this) {
                if (this._timerExecutor == null) {
                    this._timerExecutor = new TimerExecutor<String>(this, this);
                }
            }
        }
        return this._timerExecutor;
    }
    
    public boolean hasTimers() {
        return this._timerExecutor != null;
    }
    
    private void initializeAnnotationListeners() {
        final IntSet ids = (IntSet)new HashIntSet();
        for (final Method method : this.getClass().getMethods()) {
            if (method.isAnnotationPresent(RegisterEvent.class) && method.isAnnotationPresent(RegisterType.class)) {
                final RegisterEvent listener = method.getAnnotation(RegisterEvent.class);
                final RegisterType regType = method.getAnnotation(RegisterType.class);
                final ListenerRegisterType type = regType.value();
                final EventType eventType = listener.value();
                if (method.getParameterCount() != 1) {
                    AbstractScript.LOGGER.warn("Non properly defined annotation listener on method: {} expected parameter count is 1 but found: {}", (Object)method.getName(), (Object)method.getParameterCount());
                }
                else if (!eventType.isEventClass(method.getParameterTypes()[0])) {
                    AbstractScript.LOGGER.warn("Non properly defined annotation listener on method: {} expected parameter to be type of: {}  but found: {}", new Object[] { method.getName(), eventType.getEventClass().getSimpleName(), method.getParameterTypes()[0].getSimpleName() });
                }
                else if (!eventType.isReturnClass(method.getReturnType())) {
                    AbstractScript.LOGGER.warn("Non properly defined annotation listener on method: {} expected return type to be one of: {} but found: {}", new Object[] { method.getName(), Arrays.toString(eventType.getReturnClasses()), method.getReturnType().getSimpleName() });
                }
                else {
                    int priority = 0;
                    ids.clear();
                    for (final Annotation annotation2 : method.getAnnotations()) {
                        final Annotation annotation = annotation2;
                        final Id npc;
                        if (annotation2 instanceof Id && (npc = (Id)annotation2) == annotation2) {
                            for (final int id : npc.value()) {
                                ids.add(id);
                            }
                        }
                        else {
                            final Annotation annotation3 = annotation;
                            final Ids npcs;
                            if (annotation3 instanceof Ids && (npcs = (Ids)annotation3) == annotation3) {
                                for (final Id npc2 : npcs.value()) {
                                    for (final int id2 : npc2.value()) {
                                        ids.add(id2);
                                    }
                                }
                            }
                            else {
                                final Annotation annotation4 = annotation;
                                final Range range;
                                if (annotation4 instanceof Range && (range = (Range)annotation4) == annotation4) {
                                    if (range.from() > range.to()) {
                                        AbstractScript.LOGGER.warn("Wrong Range: from is higher then to!");
                                    }
                                    else {
                                        for (int id3 = range.from(); id3 <= range.to(); ++id3) {
                                            ids.add(id3);
                                        }
                                    }
                                }
                                else {
                                    final Annotation annotation5 = annotation;
                                    final Ranges ranges;
                                    if (annotation5 instanceof Ranges && (ranges = (Ranges)annotation5) == annotation5) {
                                        for (final Range range2 : ranges.value()) {
                                            if (range2.from() > range2.to()) {
                                                AbstractScript.LOGGER.warn("Wrong Ranges: from is higher then to!");
                                            }
                                            else {
                                                for (int id4 = range2.from(); id4 <= range2.to(); ++id4) {
                                                    ids.add(id4);
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        final Annotation annotation6 = annotation;
                                        final NpcLevelRange range3;
                                        if (annotation6 instanceof NpcLevelRange && (range3 = (NpcLevelRange)annotation6) == annotation6) {
                                            if (range3.from() > range3.to()) {
                                                AbstractScript.LOGGER.warn("Wrong NpcLevelRange: from is higher then to!");
                                            }
                                            else if (type != ListenerRegisterType.NPC) {
                                                AbstractScript.LOGGER.warn("ListenerRegisterType {} for NpcLevelRange, NPC is expected!", (Object)type);
                                            }
                                            else {
                                                for (int level = range3.from(); level <= range3.to(); ++level) {
                                                    final List<NpcTemplate> templates = NpcData.getInstance().getAllOfLevel(level);
                                                    templates.forEach(template -> ids.add(template.getId()));
                                                }
                                            }
                                        }
                                        else {
                                            final Annotation annotation7 = annotation;
                                            final NpcLevelRanges ranges2;
                                            if (annotation7 instanceof NpcLevelRanges && (ranges2 = (NpcLevelRanges)annotation7) == annotation7) {
                                                for (final NpcLevelRange range4 : ranges2.value()) {
                                                    if (range4.from() > range4.to()) {
                                                        AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;)Ljava/lang/String;, annotation.getClass().getSimpleName()));
                                                    }
                                                    else if (type != ListenerRegisterType.NPC) {
                                                        AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/events/ListenerRegisterType;Ljava/lang/String;)Ljava/lang/String;, type, annotation.getClass().getSimpleName()));
                                                    }
                                                    else {
                                                        for (int level2 = range4.from(); level2 <= range4.to(); ++level2) {
                                                            final List<NpcTemplate> templates2 = NpcData.getInstance().getAllOfLevel(level2);
                                                            templates2.forEach(template -> ids.add(template.getId()));
                                                        }
                                                    }
                                                }
                                            }
                                            else {
                                                final Annotation annotation8 = annotation;
                                                final Priority p;
                                                if (annotation8 instanceof Priority && (p = (Priority)annotation8) == annotation8) {
                                                    priority = p.value();
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!ids.isEmpty()) {
                        this._registeredIds.computeIfAbsent(type, k -> CHashIntMap.newKeySet()).addAll((IntCollection)ids);
                    }
                    this.registerAnnotation(method, eventType, type, priority, (IntCollection)ids);
                }
            }
        }
    }
    
    @Override
    public boolean unload() {
        this._listeners.forEach(AbstractEventListener::unregisterMe);
        this._listeners.clear();
        if (this._timerExecutor != null) {
            this._timerExecutor.cancelAllTimers();
        }
        return true;
    }
    
    protected final List<AbstractEventListener> setAttackableKillId(final Consumer<OnAttackableKill> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableKillId(final Consumer<OnAttackableKill> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_ATTACKABLE_KILL);
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_KILL, ListenerRegisterType.NPC, npcIds);
    }
    
    private void checkNpcIds(final IntCollection npcIds, final EventType type) {
        npcIds.forEach(id -> {
            if (Objects.isNull(NpcData.getInstance().getTemplate(id))) {
                AbstractScript.LOGGER.error("Found registering event type {} for non existing NPC: {}!", (Object)type, (Object)id);
            }
        });
    }
    
    protected final List<AbstractEventListener> addCreatureKillId(final Function<OnCreatureDeath, ? extends AbstractEventReturn> callback, final int... npcIds) {
        return this.registerFunction(callback, EventType.ON_CREATURE_DEATH, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureKillId(final Consumer<OnCreatureDeath> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_DEATH, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureKillId(final Consumer<OnCreatureDeath> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_DEATH, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> addCreatureAttackedId(final Function<OnCreatureAttacked, ? extends AbstractEventReturn> callback, final int... npcIds) {
        return this.registerFunction(callback, EventType.ON_CREATURE_ATTACKED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureAttackedId(final Consumer<OnCreatureAttacked> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ATTACKED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureAttackedid(final Consumer<OnCreatureAttacked> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ATTACKED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcFirstTalkId(final Consumer<OnNpcFirstTalk> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerConsumer(callback, EventType.ON_NPC_FIRST_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcFirstTalkId(final Consumer<OnNpcFirstTalk> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_FIRST_TALK);
        return this.registerConsumer(callback, EventType.ON_NPC_FIRST_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcTalkId(final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_TALK);
        return this.registerDummy(EventType.ON_NPC_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcTalkId(final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error("Found addTalkId for non existing NPC: {}!", (Object)id);
            }
        }
        return this.registerDummy(EventType.ON_NPC_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcTeleportId(final Consumer<OnNpcTeleport> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_TELEPORT, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcTeleportId(final Consumer<OnNpcTeleport> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_TELEPORT, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcQuestStartId(final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerDummy(EventType.ON_NPC_QUEST_START, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcQuestStartId(final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_QUEST_START);
        return this.registerDummy(EventType.ON_NPC_QUEST_START, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSkillSeeId(final Consumer<OnNpcSkillSee> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerConsumer(callback, EventType.ON_NPC_SKILL_SEE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSkillSeeId(final Consumer<OnNpcSkillSee> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_SKILL_SEE);
        return this.registerConsumer(callback, EventType.ON_NPC_SKILL_SEE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSkillFinishedId(final Consumer<OnNpcSkillFinished> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerConsumer(callback, EventType.ON_NPC_SKILL_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSkillFinishedId(final Consumer<OnNpcSkillFinished> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_SKILL_FINISHED);
        return this.registerConsumer(callback, EventType.ON_NPC_SKILL_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSpawnId(final Consumer<OnNpcSpawn> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error("Found addSpawnId for non existing NPC: {}!", (Object)id);
            }
        }
        return this.registerConsumer(callback, EventType.ON_NPC_SPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcSpawnId(final Consumer<OnNpcSpawn> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_NPC_SPAWN);
        return this.registerConsumer(callback, EventType.ON_NPC_SPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcDespawnId(final Consumer<OnNpcDespawn> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_DESPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcDespawnId(final Consumer<OnNpcDespawn> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_DESPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcEventReceivedId(final Consumer<OnNpcEventReceived> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_EVENT_RECEIVED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcEventReceivedId(final Consumer<OnNpcEventReceived> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_EVENT_RECEIVED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcMoveFinishedId(final Consumer<OnNpcMoveFinished> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_MOVE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcMoveFinishedId(final Consumer<OnNpcMoveFinished> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_MOVE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcMoveRouteFinishedId(final Consumer<OnNpcMoveRouteFinished> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_MOVE_ROUTE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcMoveRouteFinishedId(final Consumer<OnNpcMoveRouteFinished> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_MOVE_ROUTE_FINISHED, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcHateId(final Consumer<OnAttackableHate> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcHateId(final Consumer<OnAttackableHate> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> addNpcHateId(final Function<OnAttackableHate, TerminateReturn> callback, final int... npcIds) {
        return this.registerFunction(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> addNpcHateId(final Function<OnAttackableHate, TerminateReturn> callback, final IntCollection npcIds) {
        return this.registerFunction(callback, EventType.ON_NPC_HATE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCanBeSeenId(final Consumer<OnNpcCanBeSeen> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_CAN_BE_SEEN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCanBeSeenId(final Consumer<OnNpcCanBeSeen> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_CAN_BE_SEEN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCanBeSeenId(final Function<OnNpcCanBeSeen, TerminateReturn> callback, final int... npcIds) {
        return this.registerFunction(callback, EventType.ON_NPC_CAN_BE_SEEN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCanBeSeenId(final Function<OnNpcCanBeSeen, TerminateReturn> callback, final IntCollection npcIds) {
        return this.registerFunction(callback, EventType.ON_NPC_CAN_BE_SEEN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCreatureSeeId(final Consumer<OnNpcCreatureSee> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_CREATURE_SEE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureSeeId(final Consumer<OnCreatureSee> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_SEE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setNpcCreatureSeeId(final Consumer<OnNpcCreatureSee> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_NPC_CREATURE_SEE, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableFactionIdId(final Consumer<OnAttackableFactionCall> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_FACTION_CALL, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableFactionIdId(final Consumer<OnAttackableFactionCall> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_FACTION_CALL, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableAttackId(final Consumer<OnAttackableAttack> callback, final int... npcIds) {
        for (final int id : npcIds) {
            if (NpcData.getInstance().getTemplate(id) == null) {
                AbstractScript.LOGGER.error(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;I)Ljava/lang/String;, this.getClass().getSimpleName(), id));
            }
        }
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_ATTACK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableAttackId(final Consumer<OnAttackableAttack> callback, final IntCollection npcIds) {
        this.checkNpcIds(npcIds, EventType.ON_ATTACKABLE_ATTACK);
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_ATTACK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableAggroRangeEnterId(final Consumer<OnAttackableAggroRangeEnter> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setAttackableAggroRangeEnterId(final Consumer<OnAttackableAggroRangeEnter> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_ATTACKABLE_AGGRO_RANGE_ENTER, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSkillLearnId(final Consumer<OnPlayerSkillLearn> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SKILL_LEARN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSkillLearnId(final Consumer<OnPlayerSkillLearn> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SKILL_LEARN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSummonSpawnId(final Consumer<OnPlayerSummonSpawn> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SUMMON_SPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSummonSpawnId(final Consumer<OnPlayerSummonSpawn> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SUMMON_SPAWN, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSummonTalkId(final Consumer<OnPlayerSummonTalk> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SUMMON_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerSummonTalkId(final Consumer<OnPlayerSummonSpawn> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_SUMMON_TALK, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setPlayerLoginId(final Consumer<OnPlayerLogin> callback) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_LOGIN, ListenerRegisterType.GLOBAL, new int[0]);
    }
    
    protected final List<AbstractEventListener> setPlayerLogoutId(final Consumer<OnPlayerLogout> callback) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_LOGOUT, ListenerRegisterType.GLOBAL, new int[0]);
    }
    
    protected final List<AbstractEventListener> setCreatureZoneEnterId(final Consumer<OnCreatureZoneEnter> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ZONE_ENTER, ListenerRegisterType.ZONE, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureZoneEnterId(final Consumer<OnCreatureZoneEnter> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ZONE_ENTER, ListenerRegisterType.ZONE, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureZoneExitId(final Consumer<OnCreatureZoneExit> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ZONE_EXIT, ListenerRegisterType.ZONE, npcIds);
    }
    
    protected final List<AbstractEventListener> setCreatureZoneExitId(final Consumer<OnCreatureZoneExit> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_CREATURE_ZONE_EXIT, ListenerRegisterType.ZONE, npcIds);
    }
    
    protected final List<AbstractEventListener> setTrapActionId(final Consumer<OnTrapAction> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_TRAP_ACTION, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setTrapActionId(final Consumer<OnTrapAction> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_TRAP_ACTION, ListenerRegisterType.NPC, npcIds);
    }
    
    protected final List<AbstractEventListener> setItemBypassEvenId(final Consumer<OnItemBypassEvent> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_ITEM_BYPASS_EVENT, ListenerRegisterType.ITEM, npcIds);
    }
    
    protected final List<AbstractEventListener> setItemBypassEvenId(final Consumer<OnItemBypassEvent> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_ITEM_BYPASS_EVENT, ListenerRegisterType.ITEM, npcIds);
    }
    
    protected final List<AbstractEventListener> setItemTalkId(final Consumer<OnItemTalk> callback, final int... npcIds) {
        return this.registerConsumer(callback, EventType.ON_ITEM_TALK, ListenerRegisterType.ITEM, npcIds);
    }
    
    protected final List<AbstractEventListener> setItemTalkId(final Consumer<OnItemTalk> callback, final IntCollection npcIds) {
        return this.registerConsumer(callback, EventType.ON_ITEM_TALK, ListenerRegisterType.ITEM, npcIds);
    }
    
    protected final List<AbstractEventListener> setOlympiadMatchResult(final Consumer<OnOlympiadMatchResult> callback) {
        return this.registerConsumer(callback, EventType.ON_OLYMPIAD_MATCH_RESULT, ListenerRegisterType.OLYMPIAD, new int[0]);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeStartId(final Consumer<OnCastleSiegeStart> callback, final int... castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_START, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeStartId(final Consumer<OnCastleSiegeStart> callback, final IntCollection castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_START, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeOwnerChangeId(final Consumer<OnCastleSiegeOwnerChange> callback, final int... castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_OWNER_CHANGE, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeOwnerChangeId(final Consumer<OnCastleSiegeOwnerChange> callback, final IntCollection castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_OWNER_CHANGE, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeFinishId(final Consumer<OnCastleSiegeFinish> callback, final int... castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_FINISH, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setCastleSiegeFinishId(final Consumer<OnCastleSiegeFinish> callback, final IntCollection castleIds) {
        return this.registerConsumer(callback, EventType.ON_CASTLE_SIEGE_FINISH, ListenerRegisterType.CASTLE, castleIds);
    }
    
    protected final List<AbstractEventListener> setPlayerProfessionChangeId(final Consumer<OnPlayerProfessionChange> callback) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_PROFESSION_CHANGE, ListenerRegisterType.GLOBAL, new int[0]);
    }
    
    protected final List<AbstractEventListener> setPlayerProfessionCancelId(final Consumer<OnPlayerProfessionCancel> callback) {
        return this.registerConsumer(callback, EventType.ON_PLAYER_PROFESSION_CANCEL, ListenerRegisterType.GLOBAL, new int[0]);
    }
    
    protected final List<AbstractEventListener> setInstanceCreatedId(final Consumer<OnInstanceCreated> callback, final int... templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_CREATED, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceCreatedId(final Consumer<OnInstanceCreated> callback, final IntCollection templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_CREATED, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceDestroyId(final Consumer<OnInstanceDestroy> callback, final int... templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_DESTROY, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceDestroyId(final Consumer<OnInstanceDestroy> callback, final IntCollection templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_DESTROY, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceEnterId(final Consumer<OnInstanceEnter> callback, final int... templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_ENTER, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceEnterId(final Consumer<OnInstanceEnter> callback, final IntCollection templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_ENTER, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceLeaveId(final Consumer<OnInstanceLeave> callback, final int... templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_LEAVE, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceLeaveId(final Consumer<OnInstanceLeave> callback, final IntCollection templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_LEAVE, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceStatusChangeId(final Consumer<OnInstanceStatusChange> callback, final int... templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_STATUS_CHANGE, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> setInstanceStatusChangeId(final Consumer<OnInstanceStatusChange> callback, final IntCollection templateIds) {
        return this.registerConsumer(callback, EventType.ON_INSTANCE_STATUS_CHANGE, ListenerRegisterType.INSTANCE, templateIds);
    }
    
    protected final List<AbstractEventListener> registerConsumer(final Consumer<? extends IBaseEvent> callback, final EventType type, final ListenerRegisterType registerType, final int... npcIds) {
        return this.registerListener(container -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerConsumer(final Consumer<? extends IBaseEvent> callback, final EventType type, final ListenerRegisterType registerType, final IntCollection npcIds) {
        return this.registerListener(container -> new ConsumerEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerFunction(final Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, final EventType type, final ListenerRegisterType registerType, final int... npcIds) {
        return this.registerListener(container -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerFunction(final Function<? extends IBaseEvent, ? extends AbstractEventReturn> callback, final EventType type, final ListenerRegisterType registerType, final IntCollection npcIds) {
        return this.registerListener(container -> new FunctionEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerRunnable(final Runnable callback, final EventType type, final ListenerRegisterType registerType, final int... npcIds) {
        return this.registerListener(container -> new RunnableEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerRunnable(final Runnable callback, final EventType type, final ListenerRegisterType registerType, final IntCollection npcIds) {
        return this.registerListener(container -> new RunnableEventListener(container, type, callback, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerAnnotation(final Method callback, final EventType type, final ListenerRegisterType registerType, final int priority, final int... npcIds) {
        return this.registerListener(container -> new AnnotationEventListener(container, type, callback, this, priority), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerAnnotation(final Method callback, final EventType type, final ListenerRegisterType registerType, final int priority, final IntCollection npcIds) {
        return this.registerListener(container -> new AnnotationEventListener(container, type, callback, this, priority), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerDummy(final EventType type, final ListenerRegisterType registerType, final int... npcIds) {
        return this.registerListener(container -> new DummyEventListener(container, type, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerDummy(final EventType type, final ListenerRegisterType registerType, final IntCollection npcIds) {
        return this.registerListener(container -> new DummyEventListener(container, type, this), registerType, npcIds);
    }
    
    protected final List<AbstractEventListener> registerListener(final Function<ListenersContainer, AbstractEventListener> action, final ListenerRegisterType registerType, final int... ids) {
        final List<AbstractEventListener> listeners = new ArrayList<AbstractEventListener>((ids.length > 0) ? ids.length : 1);
        if (ids.length > 0) {
            for (final int id : ids) {
                this.registerListenrWithId(action, registerType, listeners, id);
                this._registeredIds.computeIfAbsent(registerType, k -> CHashIntMap.newKeySet()).add(id);
            }
        }
        else {
            this.registerListenerWithoutId(action, registerType, listeners);
        }
        this._listeners.addAll((Collection<?>)listeners);
        return listeners;
    }
    
    private void registerListenerWithoutId(final Function<ListenersContainer, AbstractEventListener> action, final ListenerRegisterType registerType, final List<AbstractEventListener> listeners) {
        switch (registerType) {
            case GLOBAL: {
                final ListenersContainer template = Listeners.Global();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_NPCS: {
                final ListenersContainer template = Listeners.Npcs();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_MONSTERS: {
                final ListenersContainer template = Listeners.Monsters();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
            case GLOBAL_PLAYERS: {
                final ListenersContainer template = Listeners.players();
                listeners.add(template.addListener(action.apply(template)));
                break;
            }
        }
    }
    
    protected final List<AbstractEventListener> registerListener(final Function<ListenersContainer, AbstractEventListener> action, final ListenerRegisterType registerType, final IntCollection ids) {
        final List<AbstractEventListener> listeners = new ArrayList<AbstractEventListener>(ids.isEmpty() ? 1 : ids.size());
        if (!ids.isEmpty()) {
            ids.forEach(id -> this.registerListenrWithId(action, registerType, listeners, id));
            this._registeredIds.computeIfAbsent(registerType, k -> CHashIntMap.newKeySet()).addAll(ids);
        }
        else {
            this.registerListenerWithoutId(action, registerType, listeners);
        }
        this._listeners.addAll((Collection<?>)listeners);
        return listeners;
    }
    
    private void registerListenrWithId(final Function<ListenersContainer, AbstractEventListener> action, final ListenerRegisterType registerType, final List<AbstractEventListener> listeners, final int id) {
        switch (registerType) {
            case NPC: {
                final NpcTemplate template = NpcData.getInstance().getTemplate(id);
                if (template != null) {
                    listeners.add(template.addListener(action.apply(template)));
                    break;
                }
                break;
            }
            case ZONE: {
                final Zone template2 = ZoneManager.getInstance().getZoneById(id);
                if (template2 != null) {
                    listeners.add(template2.addListener(action.apply(template2)));
                    break;
                }
                break;
            }
            case ITEM: {
                final ItemTemplate template3 = ItemEngine.getInstance().getTemplate(id);
                if (template3 != null) {
                    listeners.add(template3.addListener(action.apply(template3)));
                    break;
                }
                break;
            }
            case CASTLE: {
                final Castle template4 = CastleManager.getInstance().getCastleById(id);
                if (template4 != null) {
                    listeners.add(template4.addListener(action.apply(template4)));
                    break;
                }
                break;
            }
            case INSTANCE: {
                final InstanceTemplate template5 = InstanceManager.getInstance().getInstanceTemplate(id);
                if (template5 != null) {
                    listeners.add(template5.addListener(action.apply(template5)));
                    break;
                }
                break;
            }
            default: {
                AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/events/ListenerRegisterType;)Ljava/lang/String;, registerType));
                break;
            }
        }
    }
    
    public IntSet getRegisteredIds(final ListenerRegisterType type) {
        return this._registeredIds.getOrDefault(type, Containers.emptyIntSet());
    }
    
    public Queue<AbstractEventListener> getListeners() {
        return this._listeners;
    }
    
    public void onSpawnActivate(final SpawnTemplate template) {
    }
    
    public void onSpawnDeactivate(final SpawnTemplate template) {
    }
    
    public void onSpawnNpc(final SpawnTemplate template, final SpawnGroup group, final Npc npc) {
    }
    
    public void onSpawnDespawnNpc(final SpawnTemplate template, final SpawnGroup group, final Npc npc) {
    }
    
    public void onSpawnNpcDeath(final SpawnTemplate template, final SpawnGroup group, final Npc npc, final Creature killer) {
    }
    
    public Trap addTrap(final int trapId, final int x, final int y, final int z, final int heading, final Skill skill, final int instanceId) {
        final NpcTemplate npcTemplate = NpcData.getInstance().getTemplate(trapId);
        final Trap trap = new Trap(npcTemplate, instanceId, -1);
        trap.setCurrentHp(trap.getMaxHp());
        trap.setCurrentMp(trap.getMaxMp());
        trap.setIsInvul(true);
        trap.setHeading(heading);
        trap.spawnMe(x, y, z);
        return trap;
    }
    
    public Npc addMinion(final Monster master, final int minionId) {
        return MinionList.spawnMinion(master, minionId);
    }
    
    public long getQuestItemsCount(final Player player, final int... itemIds) {
        long count = 0L;
        for (final Item item : player.getInventory().getItems()) {
            if (item == null) {
                continue;
            }
            for (final int itemId : itemIds) {
                if (item.getId() == itemId) {
                    if (MathUtil.checkOverFlow(count, item.getCount())) {
                        return Long.MAX_VALUE;
                    }
                    count += item.getCount();
                }
            }
        }
        return count;
    }
    
    public boolean hasAtLeastOneQuestItem(final Player player, final int... itemIds) {
        final PlayerInventory inv = player.getInventory();
        for (final int itemId : itemIds) {
            if (inv.getItemByItemId(itemId) != null) {
                return true;
            }
        }
        return false;
    }
    
    public void giveAdena(final Player player, final long count, final boolean applyRates) {
        if (applyRates) {
            rewardItems(player, 57, count);
        }
        else {
            giveItems(player, 57, count);
        }
    }
    
    public final void executeForEachPlayer(final Player player, final Npc npc, final boolean isSummon, final boolean includeParty, final boolean includeCommandChannel) {
        if (player.isSimulatingTalking()) {
            return;
        }
        if ((includeParty || includeCommandChannel) && player.isInParty()) {
            if (includeCommandChannel && player.getParty().isInCommandChannel()) {
                player.getParty().getCommandChannel().checkEachMember(member -> {
                    this.actionForEachPlayer(member, npc, isSummon);
                    return Boolean.valueOf(true);
                });
            }
            else if (includeParty) {
                player.getParty().checkEachMember(member -> {
                    this.actionForEachPlayer(member, npc, isSummon);
                    return Boolean.valueOf(true);
                });
            }
        }
        else {
            this.actionForEachPlayer(player, npc, isSummon);
        }
    }
    
    public void actionForEachPlayer(final Player player, final Npc npc, final boolean isSummon) {
    }
    
    public void openDoor(final int doorId, final int instanceId) {
        final Door door = this.getDoor(doorId, instanceId);
        if (door == null) {
            AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), doorId, instanceId), (Throwable)new NullPointerException());
        }
        else if (!door.isOpen()) {
            door.openMe();
        }
    }
    
    public void closeDoor(final int doorId, final int instanceId) {
        final Door door = this.getDoor(doorId, instanceId);
        if (door == null) {
            AbstractScript.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;II)Ljava/lang/String;, this.getClass().getSimpleName(), doorId, instanceId), (Throwable)new NullPointerException());
        }
        else if (door.isOpen()) {
            door.closeMe();
        }
    }
    
    public Door getDoor(final int doorId, final int instanceId) {
        Door door = null;
        if (instanceId <= 0) {
            door = DoorDataManager.getInstance().getDoor(doorId);
        }
        else {
            final Instance inst = InstanceManager.getInstance().getInstance(instanceId);
            if (inst != null) {
                door = inst.getDoor(doorId);
            }
        }
        return door;
    }
    
    protected void addAttackPlayerDesire(final Npc npc, final Playable playable) {
        this.addAttackPlayerDesire(npc, playable, 999);
    }
    
    protected void addAttackPlayerDesire(final Npc npc, final Playable target, final int desire) {
        if (GameUtils.isAttackable(npc)) {
            ((Attackable)npc).addDamageHate(target, 0, desire);
        }
        npc.setRunning();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }
    
    protected void addAttackDesire(final Npc npc, final Creature target) {
        npc.setRunning();
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, target);
    }
    
    protected void addMoveToDesire(final Npc npc, final Location loc, final int desire) {
        npc.getAI().setIntention(CtrlIntention.AI_INTENTION_MOVE_TO, loc);
    }
    
    protected void castSkill(final Npc npc, final Playable target, final SkillHolder skill) {
        npc.setTarget(target);
        npc.doCast(skill.getSkill());
    }
    
    protected void castSkill(final Npc npc, final Playable target, final Skill skill) {
        npc.setTarget(target);
        npc.doCast(skill);
    }
    
    protected void addSkillCastDesire(final Npc npc, final WorldObject target, final SkillHolder skill, final int desire) {
        this.addSkillCastDesire(npc, target, skill.getSkill(), desire);
    }
    
    protected void addSkillCastDesire(final Npc npc, final WorldObject target, final Skill skill, final int desire) {
        if (GameUtils.isAttackable(npc) && GameUtils.isCreature(target)) {
            ((Attackable)npc).addDamageHate((Creature)target, 0, desire);
        }
        npc.setTarget((target != null) ? target : npc);
        npc.doCast(skill);
    }
    
    public void removeRadar(final Player player, final int x, final int y, final int z) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.getRadar().removeMarker(x, y, z);
    }
    
    public void clearRadar(final Player player) {
        if (player.isSimulatingTalking()) {
            return;
        }
        player.getRadar().removeAllMarkers();
    }
    
    public void playMovie(final Player player, final Movie movie) {
        if (player.isSimulatingTalking()) {
            return;
        }
        new MovieHolder(List.of(player), movie);
    }
    
    public void playMovie(final List<Player> players, final Movie movie) {
        new MovieHolder(players, movie);
    }
    
    public void playMovie(final Set<Player> players, final Movie movie) {
        new MovieHolder(new ArrayList<Player>(players), movie);
    }
    
    public void playMovie(final Instance instance, final Movie movie) {
        if (instance != null) {
            for (final Player player : instance.getPlayers()) {
                if (player != null && player.getInstanceWorld() == instance) {
                    this.playMovie(player, movie);
                }
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)AbstractScript.class);
    }
}
