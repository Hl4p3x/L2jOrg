// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.actor.appearance;

import java.util.Objects;
import org.l2j.gameserver.enums.Sex;
import org.l2j.gameserver.model.actor.instance.Player;

public class PlayerAppearance
{
    public static final int DEFAULT_TITLE_COLOR = 15530402;
    private Player owner;
    private byte face;
    private byte hairColor;
    private byte hairStyle;
    private boolean female;
    private String _visibleName;
    private String _visibleTitle;
    private int _nameColor;
    private int _titleColor;
    private int _visibleClanId;
    private int _visibleClanCrestId;
    private int _visibleClanLargeCrestId;
    private int _visibleAllyId;
    private int _visibleAllyCrestId;
    
    public PlayerAppearance(final Player owner, final byte face, final byte hairColor, final byte hairStyle, final boolean female) {
        this._nameColor = 16777215;
        this._titleColor = 15530402;
        this._visibleClanId = -1;
        this._visibleClanCrestId = -1;
        this._visibleClanLargeCrestId = -1;
        this._visibleAllyId = -1;
        this._visibleAllyCrestId = -1;
        this.owner = owner;
        this.face = face;
        this.hairColor = hairColor;
        this.hairStyle = hairStyle;
        this.female = female;
    }
    
    public final String getVisibleName() {
        if (this._visibleName == null) {
            return this.owner.getName();
        }
        return this._visibleName;
    }
    
    public final void setVisibleName(final String visibleName) {
        this._visibleName = visibleName;
    }
    
    public final String getVisibleTitle() {
        if (this._visibleTitle == null) {
            return this.owner.getTitle();
        }
        return this._visibleTitle;
    }
    
    public final void setVisibleTitle(final String visibleTitle) {
        this._visibleTitle = visibleTitle;
    }
    
    public final byte getFace() {
        return this.face;
    }
    
    public final void setFace(final int value) {
        this.face = (byte)value;
    }
    
    public final byte getHairColor() {
        return this.hairColor;
    }
    
    public final void setHairColor(final int value) {
        this.hairColor = (byte)value;
    }
    
    public final byte getHairStyle() {
        return this.hairStyle;
    }
    
    public final void setHairStyle(final int value) {
        this.hairStyle = (byte)value;
    }
    
    public final boolean isFemale() {
        return this.female;
    }
    
    public final void setFemale(final boolean isfemale) {
        this.female = isfemale;
    }
    
    public Sex getSexType() {
        return this.female ? Sex.FEMALE : Sex.MALE;
    }
    
    public int getNameColor() {
        return this._nameColor;
    }
    
    public void setNameColor(final int nameColor) {
        if (nameColor < 0) {
            return;
        }
        this._nameColor = nameColor;
    }
    
    public void setNameColor(final int red, final int green, final int blue) {
        this._nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
    }
    
    public int getTitleColor() {
        return this._titleColor;
    }
    
    public void setTitleColor(final int titleColor) {
        if (titleColor < 0) {
            return;
        }
        this._titleColor = titleColor;
    }
    
    public void setTitleColor(final int red, final int green, final int blue) {
        this._titleColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
    }
    
    public Player getOwner() {
        return this.owner;
    }
    
    public void setOwner(final Player owner) {
        this.owner = owner;
    }
    
    public int getVisibleClanId() {
        return (this._visibleClanId != -1) ? this._visibleClanId : this.owner.getClanId();
    }
    
    public int getVisibleClanCrestId() {
        return (this._visibleClanCrestId != -1) ? this._visibleClanCrestId : this.owner.getClanCrestId();
    }
    
    public int getVisibleClanLargeCrestId() {
        return (this._visibleClanLargeCrestId != -1) ? this._visibleClanLargeCrestId : this.owner.getClanCrestLargeId();
    }
    
    public int getVisibleAllyId() {
        return (this._visibleAllyId != -1) ? this._visibleAllyId : this.owner.getAllyId();
    }
    
    public int getVisibleAllyCrestId() {
        return (this._visibleAllyCrestId != -1) ? this._visibleAllyCrestId : (Objects.isNull(this.owner) ? 0 : this.owner.getAllyCrestId());
    }
    
    public void setVisibleClanData(final int clanId, final int clanCrestId, final int clanLargeCrestId, final int allyId, final int allyCrestId) {
        this._visibleClanId = clanId;
        this._visibleClanCrestId = clanCrestId;
        this._visibleClanLargeCrestId = clanLargeCrestId;
        this._visibleAllyId = allyId;
        this._visibleAllyCrestId = allyCrestId;
    }
}
