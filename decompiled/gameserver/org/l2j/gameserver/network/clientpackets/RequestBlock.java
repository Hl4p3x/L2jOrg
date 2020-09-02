// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.clientpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.BlockList;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.network.GameClient;
import org.slf4j.Logger;

public final class RequestBlock extends ClientPacket
{
    private static final Logger LOGGER;
    private static final int BLOCK = 0;
    private static final int UNBLOCK = 1;
    private static final int BLOCKLIST = 2;
    private static final int ALLBLOCK = 3;
    private static final int ALLUNBLOCK = 4;
    private String _name;
    private Integer _type;
    
    public void readImpl() {
        this._type = this.readInt();
        if (this._type == 0 || this._type == 1) {
            this._name = this.readString();
        }
    }
    
    public void runImpl() {
        final Player activeChar = ((GameClient)this.client).getPlayer();
        final int targetId = PlayerNameTable.getInstance().getIdByName(this._name);
        final int targetAL = PlayerNameTable.getInstance().getAccessLevelById(targetId);
        if (activeChar == null) {
            return;
        }
        switch (this._type) {
            case 0:
            case 1: {
                if (targetId <= 0) {
                    activeChar.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
                    return;
                }
                if (targetAL > 0) {
                    activeChar.sendPacket(SystemMessageId.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
                    return;
                }
                if (activeChar.getObjectId() == targetId) {
                    return;
                }
                if (this._type == 0) {
                    BlockList.addToBlockList(activeChar, targetId);
                    break;
                }
                BlockList.removeFromBlockList(activeChar, targetId);
                break;
            }
            case 2: {
                BlockList.sendListToOwner(activeChar);
                break;
            }
            case 3: {
                activeChar.sendPacket(SystemMessageId.MESSAGE_REFUSAL_MODE);
                BlockList.setBlockAll(activeChar, true);
                break;
            }
            case 4: {
                activeChar.sendPacket(SystemMessageId.MESSAGE_ACCEPTANCE_MODE);
                BlockList.setBlockAll(activeChar, false);
                break;
            }
            default: {
                RequestBlock.LOGGER.info(invokedynamic(makeConcatWithConstants:(Ljava/lang/Integer;)Ljava/lang/String;, this._type));
                break;
            }
        }
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RequestBlock.class);
    }
}
