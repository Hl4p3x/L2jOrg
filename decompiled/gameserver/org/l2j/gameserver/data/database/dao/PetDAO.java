// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import java.util.List;
import org.l2j.gameserver.data.database.data.PetData;
import org.l2j.commons.database.DAO;

public interface PetDAO extends DAO<PetData>
{
    @Query("SELECT ownerId, item_obj_id FROM pets WHERE restore = 'true'")
    List<PetData> findAllPetOwnersByRestore();
    
    @Query("SELECT EXISTS (SELECT 1 FROM pets p, items i WHERE p.item_obj_id = i.object_id AND name= :name: AND i.item_id = :petItem:)")
    boolean existsPetName(final String name, final int petItem);
    
    @Query("DELETE FROM pets WHERE item_obj_id=:itemId:")
    void deleteByItem(final int itemId);
    
    @Query("DELETE FROM pets WHERE item_obj_id IN (SELECT object_id FROM items WHERE items.owner_id=:playerId:)")
    void deleteByOwner(final int playerId);
}
