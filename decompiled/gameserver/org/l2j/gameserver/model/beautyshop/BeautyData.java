// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.model.beautyshop;

import java.util.HashMap;
import java.util.Map;

public class BeautyData
{
    private final Map<Integer, BeautyItem> _hairList;
    private final Map<Integer, BeautyItem> _faceList;
    
    public BeautyData() {
        this._hairList = new HashMap<Integer, BeautyItem>();
        this._faceList = new HashMap<Integer, BeautyItem>();
    }
    
    public final void addHair(final BeautyItem hair) {
        this._hairList.put(hair.getId(), hair);
    }
    
    public final void addFace(final BeautyItem face) {
        this._faceList.put(face.getId(), face);
    }
    
    public final Map<Integer, BeautyItem> getHairList() {
        return this._hairList;
    }
    
    public final Map<Integer, BeautyItem> getFaceList() {
        return this._faceList;
    }
}
