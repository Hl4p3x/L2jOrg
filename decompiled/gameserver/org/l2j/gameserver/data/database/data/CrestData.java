// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.model.interfaces.IIdentifiable;

@Table("crests")
public final class CrestData implements IIdentifiable
{
    private int id;
    private byte[] data;
    private CrestType type;
    
    public CrestData() {
    }
    
    public CrestData(final int id, final byte[] data, final CrestType type) {
        this.id = id;
        this.data = data;
        this.type = type;
    }
    
    @Override
    public int getId() {
        return this.id;
    }
    
    public byte[] getData() {
        return this.data;
    }
    
    public CrestType getType() {
        return this.type;
    }
    
    public enum CrestType
    {
        PLEDGE(1), 
        PLEDGE_LARGE(2), 
        ALLY(3);
        
        private final int _id;
        
        private CrestType(final int id) {
            this._id = id;
        }
        
        public static CrestType getById(final int id) {
            for (final CrestType crestType : values()) {
                if (crestType.getId() == id) {
                    return crestType;
                }
            }
            return null;
        }
        
        public int getId() {
            return this._id;
        }
    }
}
