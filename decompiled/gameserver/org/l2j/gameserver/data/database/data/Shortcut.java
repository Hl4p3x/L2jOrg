// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

@Table("character_shortcuts")
public class Shortcut
{
    public static final int MAX_SLOTS_PER_PAGE = 12;
    public static final int MAX_ROOM = 240;
    public static final int AUTO_POTION_ROOM;
    @Column("player_id")
    private int playerId;
    @Column("client_id")
    private int clientId;
    @Column("class_index")
    private int classIndex;
    private ShortcutType type;
    @Column("shortcut_id")
    private int shortcutId;
    private int level;
    @Column("sub_level")
    private int subLevel;
    @NonUpdatable
    private int slot;
    @NonUpdatable
    private int page;
    @NonUpdatable
    private int sharedReuseGroup;
    @NonUpdatable
    private int characterType;
    private boolean active;
    
    public Shortcut() {
        this.sharedReuseGroup = -1;
    }
    
    public Shortcut(final int clientId, final ShortcutType type, final int id, final int level, final int subLevel, final int characterType) {
        this.sharedReuseGroup = -1;
        this.clientId = clientId;
        this.type = type;
        this.shortcutId = id;
        this.level = level;
        this.subLevel = subLevel;
        this.characterType = characterType;
    }
    
    public int getClientId() {
        return this.clientId;
    }
    
    public int getShortcutId() {
        return this.shortcutId;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public int getSubLevel() {
        return this.subLevel;
    }
    
    public int getPage() {
        return this.page;
    }
    
    public int getSlot() {
        return this.slot;
    }
    
    public ShortcutType getType() {
        return this.type;
    }
    
    public int getCharacterType() {
        return this.characterType;
    }
    
    public int getSharedReuseGroup() {
        return this.sharedReuseGroup;
    }
    
    public void setSharedReuseGroup(final int sharedReuseGroup) {
        this.sharedReuseGroup = sharedReuseGroup;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public void setClassIndex(final int classIndex) {
        this.classIndex = classIndex;
    }
    
    public void setActive(final boolean active) {
        this.active = active;
    }
    
    public static int pageAndSlotToClientId(final int page, final int slot) {
        return slot + page * 12;
    }
    
    public boolean isActive() {
        return this.active;
    }
    
    static {
        AUTO_POTION_ROOM = pageAndSlotToClientId(23, 1);
    }
}
