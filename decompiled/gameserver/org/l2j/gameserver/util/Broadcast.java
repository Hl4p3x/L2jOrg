// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.util;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.serverpackets.ExShowScreenMessage;
import org.l2j.gameserver.network.serverpackets.CreatureSay;
import org.l2j.gameserver.enums.ChatType;
import java.util.function.Predicate;
import java.util.Objects;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.network.serverpackets.RelationChanged;
import org.l2j.gameserver.network.serverpackets.ExCharInfo;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.model.actor.Creature;
import org.slf4j.Logger;

public final class Broadcast
{
    private static final Logger LOGGER;
    
    public static void toPlayersTargettingMyself(final Creature character, final ServerPacket mov) {
        World.getInstance().forEachVisibleObject(character, Player.class, player -> {
            if (player.getTarget() == character) {
                player.sendPacket(mov);
            }
        });
    }
    
    public static void toKnownPlayers(final Creature character, final ServerPacket mov) {
        int relation;
        Integer oldrelation;
        RelationChanged rc;
        Summon pet;
        World.getInstance().forEachVisibleObject(character, Player.class, player -> {
            try {
                player.sendPacket(mov);
                if (mov instanceof ExCharInfo && GameUtils.isPlayer(character)) {
                    relation = ((Player)character).getRelation(player);
                    oldrelation = character.getKnownRelations().get(player.getObjectId());
                    if (oldrelation != null && oldrelation != relation) {
                        rc = new RelationChanged();
                        rc.addRelation((Playable)character, relation, character.isAutoAttackable(player));
                        if (character.hasSummon()) {
                            pet = character.getPet();
                            if (pet != null) {
                                rc.addRelation(pet, relation, character.isAutoAttackable(player));
                            }
                            if (character.hasServitors()) {
                                character.getServitors().values().forEach(s -> rc.addRelation(s, relation, character.isAutoAttackable(player)));
                            }
                        }
                        player.sendPacket(rc);
                        character.getKnownRelations().put(player.getObjectId(), relation);
                    }
                }
            }
            catch (NullPointerException e) {
                Broadcast.LOGGER.warn(e.getMessage(), (Throwable)e);
            }
        });
    }
    
    public static void toKnownPlayersInRadius(final Creature character, final ServerPacket mov, int radius) {
        if (radius < 0) {
            radius = 1500;
        }
        final World instance = World.getInstance();
        final Class<Player> clazz = Player.class;
        final int range = radius;
        Objects.requireNonNull(mov);
        instance.forEachVisibleObjectInRange(character, clazz, range, mov::sendTo);
    }
    
    public static void toSelfAndKnownPlayers(final Creature character, final ServerPacket mov) {
        if (GameUtils.isPlayer(character)) {
            character.sendPacket(mov);
        }
        toKnownPlayers(character, mov);
    }
    
    public static void toSelfAndKnownPlayersInRadius(final Creature character, final ServerPacket mov, final int radius) {
        toSelfAndKnownPlayersInRadius(character, mov, radius, o -> true);
    }
    
    public static void toSelfAndKnownPlayersInRadius(final Creature character, final ServerPacket mov, int radius, final Predicate<Player> filter) {
        if (radius < 0) {
            radius = 600;
        }
        if (GameUtils.isPlayer(character)) {
            character.sendPacket(mov);
        }
        final World instance = World.getInstance();
        final Class<Player> clazz = Player.class;
        final int range = radius;
        Objects.requireNonNull(mov);
        instance.forEachVisibleObjectInRange(character, clazz, range, mov::sendTo, filter);
    }
    
    public static void toAllOnlinePlayers(final ServerPacket... packets) {
        World.getInstance().forEachPlayer(p -> p.sendPacket(packets));
    }
    
    public static void toAllOnlinePlayers(final String text) {
        toAllOnlinePlayers(text, false);
    }
    
    public static void toAllOnlinePlayers(final String text, final boolean isCritical) {
        toAllOnlinePlayers(new CreatureSay(0, isCritical ? ChatType.CRITICAL_ANNOUNCE : ChatType.ANNOUNCEMENT, "", text));
    }
    
    public static void toAllOnlinePlayersOnScreen(final String text) {
        toAllOnlinePlayers(new ExShowScreenMessage(text, 10000));
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)Broadcast.class);
    }
}
