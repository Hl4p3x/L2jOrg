// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.templates.PlayerTemplate;
import org.l2j.gameserver.network.serverpackets.CharCreateOk;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.model.actor.instance.PlayerFactory;
import org.l2j.gameserver.data.database.data.PlayerData;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.base.ClassId;
import java.util.Objects;
import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.Config;
import org.l2j.commons.configuration.Configurator;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.CharCreateFail;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class CharacterCreate extends ClientPacket
{
    private static final Logger LOGGER_ACCOUNTING;
    private static final Logger LOGGER;
    private static final Object NAME_VERIFY_LOCKER;
    private String name;
    private boolean female;
    private int classId;
    private byte hairStyle;
    private byte hairColor;
    private byte face;
    
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
        if (!Util.isAlphaNumeric(this.name) || !((ServerSettings)Configurator.getSettings((Class)ServerSettings.class)).acceptPlayerName(this.name)) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_INCORRECT_NAME));
            return;
        }
        if (Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT != 0 && ((GameClient)this.client).getPlayerCount() >= Config.MAX_CHARACTERS_NUMBER_PER_ACCOUNT) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_TOO_MANY_CHARACTERS));
            return;
        }
        final PlayerTemplate template = PlayerTemplateData.getInstance().getTemplate(this.classId);
        if (Objects.isNull(template) || ClassId.getClassId(this.classId).level() > 0) {
            ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_CREATION_FAILED));
            return;
        }
        final PlayerData data;
        synchronized (CharacterCreate.NAME_VERIFY_LOCKER) {
            if (PlayerNameTable.getInstance().doesCharNameExist(this.name)) {
                ((GameClient)this.client).sendPacket(new CharCreateFail(CharCreateFail.CharacterCreateFailReason.REASON_NAME_ALREADY_EXISTS));
                return;
            }
            data = PlayerData.of(((GameClient)this.client).getAccountName(), this.name, this.classId, this.face, this.hairColor, this.hairStyle, this.female);
            PlayerFactory.savePlayerData(template, data);
            if (((GeneralSettings)Configurator.getSettings((Class)GeneralSettings.class)).cachePlayersName()) {
                PlayerNameTable.getInstance().addName(data.getCharId(), data.getName());
            }
        }
        PlayerFactory.init((GameClient)this.client, data);
        ((GameClient)this.client).sendPacket(CharCreateOk.STATIC_PACKET);
        CharacterCreate.LOGGER_ACCOUNTING.info("Created new character {}, {}", (Object)this.name, (Object)this.client);
    }
    
    static {
        LOGGER_ACCOUNTING = LoggerFactory.getLogger("accounting");
        LOGGER = LoggerFactory.getLogger((Class)CharacterCreate.class);
        NAME_VERIFY_LOCKER = new Object();
    }
}
