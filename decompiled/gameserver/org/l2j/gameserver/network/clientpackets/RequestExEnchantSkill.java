// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.enums.SkillEnchantType;
import org.slf4j.Logger;

public final class RequestExEnchantSkill extends ClientPacket
{
    private static final Logger LOGGER;
    private static final Logger LOGGER_ENCHANT;
    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;
    
    public void readImpl() throws InvalidDataPacketException {
        final int type = this.readInt();
        if (type < 0 || type >= SkillEnchantType.values().length) {
            RequestExEnchantSkill.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Lio/github/joealisson/mmocore/Client;ILjava/lang/String;)Ljava/lang/String;, this.client, type, this.getClass().getSimpleName()));
            throw new InvalidDataPacketException();
        }
        this._type = SkillEnchantType.values()[type];
        this._skillId = this.readInt();
        this._skillLvl = this.readShort();
        this._skillSubLvl = this.readShort();
    }
    
    public void runImpl() {
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestExEnchantSkill.class);
        LOGGER_ENCHANT = LoggerFactory.getLogger("enchant.skills");
    }
}
