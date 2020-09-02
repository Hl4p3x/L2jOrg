// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.slf4j.LoggerFactory;
import org.l2j.gameserver.model.RecipeList;
import org.l2j.gameserver.network.InvalidDataPacketException;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.data.xml.impl.RecipeData;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;

public class RecipeItemMakeInfo extends ServerPacket
{
    private static final Logger LOGGER;
    private final int _id;
    private final Player _activeChar;
    private final boolean _success;
    
    public RecipeItemMakeInfo(final int id, final Player player, final boolean success) {
        this._id = id;
        this._activeChar = player;
        this._success = success;
    }
    
    public RecipeItemMakeInfo(final int id, final Player player) {
        this._id = id;
        this._activeChar = player;
        this._success = true;
    }
    
    public void writeImpl(final GameClient client) throws InvalidDataPacketException {
        final RecipeList recipe = RecipeData.getInstance().getRecipeList(this._id);
        if (recipe != null) {
            this.writeId(ServerPacketId.RECIPE_ITEM_MAKE_INFO);
            this.writeInt(this._id);
            this.writeInt((int)(recipe.isDwarvenRecipe() ? 0 : 1));
            this.writeInt((int)this._activeChar.getCurrentMp());
            this.writeInt(this._activeChar.getMaxMp());
            this.writeInt((int)(this._success ? 1 : 0));
            this.writeByte(0);
            this.writeLong(0L);
            this.writeDouble(this._activeChar.getStats().getValue(Stat.CRAFT_RATE_MASTER));
            this.writeByte(1);
            this.writeDouble(this._activeChar.getStats().getValue(Stat.CRAFT_RATE_CRITICAL));
            return;
        }
        RecipeItemMakeInfo.LOGGER.info(invokedynamic(makeConcatWithConstants:(Lorg/l2j/gameserver/model/actor/instance/Player;I)Ljava/lang/String;, this._activeChar, this._id));
        throw new InvalidDataPacketException();
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)RecipeItemMakeInfo.class);
    }
}
