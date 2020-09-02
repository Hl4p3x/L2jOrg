// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.item.instance.Item;
import java.util.Iterator;
import java.util.List;
import org.l2j.gameserver.network.serverpackets.CharSelectionInfo;
import org.l2j.gameserver.network.Disconnection;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.events.Listeners;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerCreate;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.data.xml.impl.InitialShortcutData;
import org.l2j.gameserver.model.item.PcItemTemplate;
import org.l2j.gameserver.data.xml.impl.InitialEquipmentData;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.network.serverpackets.CharCreateOk;
import java.util.Objects;
import org.l2j.gameserver.model.actor.instance.Player;
import java.time.LocalDate;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CharCreateFail;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.Config;
import org.slf4j.Logger;

public final class CharacterCreate extends ClientPacket
{
    private static final Logger LOGGER_ACCOUNTING;
    private static final Logger LOGGER;
    private String name;
    private boolean female;
    private int classId;
    private byte hairStyle;
    private byte hairColor;
    private byte face;
    
    private static boolean isValidName(final String text) {
        return Config.CHARNAME_TEMPLATE_PATTERN.matcher(text).matches();
    }
    
    public void readImpl() {
        this.name = this.readString();
        this.readInt();
        this.female = (this.readInt() != 0);
        this.classId = this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.readInt();
        this.hairStyle = (byte)this.readInt();
        this.hairColor = (byte)this.readInt();
        this.face = (byte)this.readInt();
    }
    
    public void runImpl() {
        if (this.name.length() < 1 || this.name.length() > 16) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_16_ENG_CHARS));
            return;
        }
        if (this.face > 2 || this.face < 0) {
            CharacterCreate.LOGGER.warn("Character Creation Failure: Character face {} is invalid. Possible client hack {}", (Object)this.face, (Object)this.client);
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
            return;
        }
        if (this.hairStyle < 0 || (!this.female && this.hairStyle > 4) || (this.female && this.hairStyle > 6)) {
            CharacterCreate.LOGGER.warn("Character Creation Failure: Character hair style {} is invalid. Possible client hack {}", (Object)this.hairStyle, (Object)this.client);
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
            return;
        }
        if (this.hairColor > 3 || this.hairColor < 0) {
            CharacterCreate.LOGGER.warn("Character Creation Failure: Character hair color {} is invalid. Possible client hack {}", (Object)this.hairColor, (Object)this.client);
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
            return;
        }
        if (!Util.isAlphaNumeric(this.name) || !isValidName(this.name)) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_INCORRECT_NAME));
            return;
        }
        final Player newChar;
        synchronized (PlayerNameTable.getInstance()) {
            if (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0 && PlayerNameTable.getInstance().getAccountCharacterCount(((GameClient)this.client).getAccountName()) >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) {
                ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_TOO_MANY_CHARACTERS));
                return;
            }
            if (PlayerNameTable.getInstance().doesCharNameExist(this.name)) {
                ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_NAME_ALREADY_EXISTS));
                return;
            }
            final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(this.classId);
            if (template == null || ClassId.getClassId(this.classId).level() > 0) {
                ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
                return;
            }
            final PlayerData character = new PlayerData();
            character.setId(IdFactory.getInstance().getNextId());
            character.setName(this.name);
            character.setBaseClass(this.classId);
            character.setClassId(this.classId);
            character.setFace(this.face);
            character.setHairColor(this.hairColor);
            character.setHairStyle(this.hairStyle);
            character.setFemale(this.female);
            character.setAccountName(((GameClient)this.client).getAccountName());
            character.setCreateDate(LocalDate.now());
            newChar = Player.create(character, template);
        }
        if (Objects.isNull(newChar)) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
            return;
        }
        newChar.setCurrentHp(newChar.getMaxHp());
        newChar.setCurrentMp(newChar.getMaxMp());
        this.initNewChar((GameClient)this.client, newChar);
        ((GameClient)this.client).sendPacket(CharCreateOk.STATIC_PACKET);
        CharacterCreate.LOGGER_ACCOUNTING.info("Created new character {}, {}", (Object)newChar, (Object)this.client);
    }
    
    private void initNewChar(final GameClient client, final Player newChar) {
        World.getInstance().addObject(newChar);
        if (Config.STARTING_ADENA > 0L) {
            newChar.addAdena("Init", Config.STARTING_ADENA, null, false);
        }
        final PlayerTemplate template = newChar.getTemplate();
        if (Config.CUSTOM_STARTING_LOC) {
            final Location createLoc = new Location(Config.CUSTOM_STARTING_LOC_X, Config.CUSTOM_STARTING_LOC_Y, Config.CUSTOM_STARTING_LOC_Z);
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        }
        else {
            final Location createLoc = template.getCreationPoint();
            newChar.setXYZInvisible(createLoc.getX(), createLoc.getY(), createLoc.getZ());
        }
        newChar.setTitle("");
        if (Config.ENABLE_VITALITY) {
            newChar.setVitalityPoints(Math.min(Config.STARTING_VITALITY_POINTS, 140000), true);
        }
        if (Config.STARTING_LEVEL > 1) {
            newChar.getStats().addLevel((byte)(Config.STARTING_LEVEL - 1));
        }
        if (Config.STARTING_SP > 0) {
            newChar.getStats().addSp(Config.STARTING_SP);
        }
        final List<PcItemTemplate> initialItems = InitialEquipmentData.getInstance().getEquipmentList(newChar.getClassId());
        if (initialItems != null) {
            for (final PcItemTemplate ie : initialItems) {
                final Item item = newChar.getInventory().addItem("Init", ie.getId(), ie.getCount(), newChar, null);
                if (item == null) {
                    CharacterCreate.LOGGER.warn(invokedynamic(makeConcatWithConstants:(IJ)Ljava/lang/String;, ie.getId(), ie.getCount()));
                }
                else {
                    if (!item.isEquipable() || !ie.isEquipped()) {
                        continue;
                    }
                    newChar.getInventory().equipItem(item);
                }
            }
        }
        newChar.giveAvailableAutoGetSkills();
        InitialShortcutData.getInstance().registerAllShortcuts(newChar);
        EventDispatcher.getInstance().notifyEvent(new OnPlayerCreate(newChar, newChar.getObjectId(), newChar.getName(), client), Listeners.players());
        newChar.setOnlineStatus(true, false);
        Disconnection.of(client, newChar).storeMe().deleteMe();
        final CharSelectionInfo cl = new CharSelectionInfo(client.getAccountName(), client.getSessionId().getGameServerSessionId());
        client.setCharSelection(cl.getCharInfo());
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
        LOGGER = LoggerFactory.getLogger((Class)CharacterCreate.class);
    }
}
