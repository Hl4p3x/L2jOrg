// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.item;

import org.l2j.gameserver.model.item.type.ItemType;
import org.l2j.gameserver.model.item.type.ActionType;
import java.util.ArrayList;
import org.l2j.gameserver.model.ExtractableProduct;
import java.util.List;
import org.l2j.gameserver.model.item.type.EtcItemType;

public final class EtcItem extends ItemTemplate
{
    private String handler;
    private EtcItemType type;
    private List<ExtractableProduct> _extractableItems;
    private int _extractableCountMin;
    private int _extractableCountMax;
    private boolean isInfinite;
    private boolean selfResurrection;
    private AutoUseType autoUseType;
    
    public EtcItem(final int id, final String name, final EtcItemType type) {
        super(id, name);
        this.type = type;
        this.type1 = 4;
    }
    
    public void fillType2() {
        if (this.isQuestItem()) {
            this.type2 = 3;
        }
        else {
            int type2 = 0;
            switch (this.getId()) {
                case 57:
                case 5575:
                case 29983:
                case 29984:
                case 91663: {
                    type2 = 4;
                    break;
                }
                default: {
                    type2 = 5;
                    break;
                }
            }
            this.type2 = type2;
        }
    }
    
    @Override
    public EtcItemType getItemType() {
        return this.type;
    }
    
    @Override
    public int getItemMask() {
        return this.type.mask();
    }
    
    public String getHandlerName() {
        return this.handler;
    }
    
    public List<ExtractableProduct> getExtractableItems() {
        return this._extractableItems;
    }
    
    public int getExtractableCountMin() {
        return this._extractableCountMin;
    }
    
    public int getExtractableCountMax() {
        return this._extractableCountMax;
    }
    
    public boolean isInfinite() {
        return this.isInfinite;
    }
    
    public void addCapsuledItem(final ExtractableProduct extractableProduct) {
        if (this._extractableItems == null) {
            this._extractableItems = new ArrayList<ExtractableProduct>();
        }
        this._extractableItems.add(extractableProduct);
    }
    
    public void setImmediateEffect(final boolean immediateEffect) {
        this.immediateEffect = immediateEffect;
    }
    
    public void setExImmediateEffect(final boolean exImmediateEffect) {
        this.exImmediateEffect = exImmediateEffect;
    }
    
    public void setQuestItem(final boolean questItem) {
        this.questItem = questItem;
    }
    
    public void setInfinite(final boolean infinite) {
        this.isInfinite = infinite;
    }
    
    public void setSelfResurrection(final boolean selfResurrection) {
        this.selfResurrection = selfResurrection;
    }
    
    public boolean isSelfResurrection() {
        return this.selfResurrection;
    }
    
    public void setHandler(final String handler) {
        this.handler = handler;
    }
    
    public void setAction(final ActionType action) {
        this._defaultAction = action;
    }
    
    public void setAutoUseType(final AutoUseType autoUseType) {
        this.autoUseType = autoUseType;
    }
    
    public boolean isAutoPotion() {
        return this.autoUseType == AutoUseType.HEALING;
    }
    
    public boolean isAutoSupply() {
        return this.autoUseType == AutoUseType.SUPPLY;
    }
}
