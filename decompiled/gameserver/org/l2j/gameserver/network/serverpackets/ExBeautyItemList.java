// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.GameClient;
import java.util.Iterator;
import java.util.ArrayList;
import org.l2j.gameserver.data.xml.impl.BeautyShopData;
import java.util.HashMap;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.beautyshop.BeautyItem;
import java.util.List;
import java.util.Map;
import org.l2j.gameserver.model.beautyshop.BeautyData;

public class ExBeautyItemList extends ServerPacket
{
    private static final int HAIR_TYPE = 0;
    private static final int FACE_TYPE = 1;
    private static final int COLOR_TYPE = 2;
    private final BeautyData _beautyData;
    private final Map<Integer, List<BeautyItem>> _colorData;
    private int _colorCount;
    
    public ExBeautyItemList(final Player activeChar) {
        this._colorData = new HashMap<Integer, List<BeautyItem>>();
        this._beautyData = BeautyShopData.getInstance().getBeautyData(activeChar.getRace(), activeChar.getAppearance().getSexType());
        for (final BeautyItem hair : this._beautyData.getHairList().values()) {
            final List<BeautyItem> colors = new ArrayList<BeautyItem>();
            for (final BeautyItem color : hair.getColors().values()) {
                colors.add(color);
                ++this._colorCount;
            }
            this._colorData.put(hair.getId(), colors);
        }
    }
    
    public void writeImpl(final GameClient client) {
        this.writeId(ServerExPacketId.EX_BEAUTY_ITEM_LIST);
        this.writeInt(0);
        this.writeInt(this._beautyData.getHairList().size());
        for (final BeautyItem hair : this._beautyData.getHairList().values()) {
            this.writeInt(0);
            this.writeInt(hair.getId());
            this.writeInt(hair.getAdena());
            this.writeInt(hair.getResetAdena());
            this.writeInt(hair.getBeautyShopTicket());
            this.writeInt(1);
        }
        this.writeInt(1);
        this.writeInt(this._beautyData.getFaceList().size());
        for (final BeautyItem face : this._beautyData.getFaceList().values()) {
            this.writeInt(0);
            this.writeInt(face.getId());
            this.writeInt(face.getAdena());
            this.writeInt(face.getResetAdena());
            this.writeInt(face.getBeautyShopTicket());
            this.writeInt(1);
        }
        this.writeInt(2);
        this.writeInt(this._colorCount);
        for (final int hairId : this._colorData.keySet()) {
            for (final BeautyItem color : this._colorData.get(hairId)) {
                this.writeInt(hairId);
                this.writeInt(color.getId());
                this.writeInt(color.getAdena());
                this.writeInt(color.getResetAdena());
                this.writeInt(color.getBeautyShopTicket());
                this.writeInt(1);
            }
        }
    }
}
