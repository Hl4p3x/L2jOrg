// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.gameserver.data.database.data.SiegeClanData;
import org.l2j.gameserver.enums.CastleSide;
import java.sql.ResultSet;
import java.util.function.Consumer;
import org.l2j.gameserver.data.database.data.CastleFunctionData;
import java.util.List;
import org.l2j.commons.database.annotation.Query;
import org.l2j.gameserver.data.database.data.CastleData;
import org.l2j.commons.database.DAO;

public interface CastleDAO extends DAO<CastleData>
{
    @Query("UPDATE castle SET side='NEUTRAL' WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);")
    void updateToNeutralWithoutOwner();
    
    @Query("SELECT * FROM castle WHERE id = :id:")
    CastleData findById(final int id);
    
    @Query("UPDATE castle SET treasury = :treasury: WHERE id = :id:")
    void updateTreasury(final int id, final long treasury);
    
    @Query("UPDATE castle SET show_npc_crest = :showNpcCrest: WHERE id = :id:")
    void updateShowNpcCrest(final int id, final boolean showNpcCrest);
    
    @Query("UPDATE castle SET ticket_buy_count = :ticketBuyCount: WHERE id = :id:")
    void updateTicketBuyCount(final int id, final int ticketBuyCount);
    
    @Query("SELECT type, castle_id, level, lease, endTime FROM castle_functions WHERE castle_id = :id:")
    List<CastleFunctionData> findFunctionsByCastle(final int id);
    
    @Query("DELETE FROM castle_functions WHERE castle_id=:id: AND type=:type:")
    void deleteFunction(final int id, final int type);
    
    @Query("SELECT doorId, ratio FROM castle_doorupgrade WHERE castleId= :castleId:")
    void withDoorUpgradeDo(final int castleId, final Consumer<ResultSet> action);
    
    @Query("REPLACE INTO castle_doorupgrade (doorId, ratio, castleId) values (:doorId:, :ratio:, :castleId:)")
    void saveDoorUpgrade(final int castleId, final int doorId, final int ratio);
    
    @Query("DELETE FROM castle_doorupgrade WHERE castleId=:castleId:")
    void deleteDoorUpgradeByCastle(final int castleId);
    
    @Query("REPLACE INTO castle_trap_upgrade (castle_id, tower_index, level) values (:id:,:towerIndex:,:level:)")
    void saveTrapUpgrade(final int id, final int towerIndex, final int level);
    
    @Query("DELETE FROM castle_trap_upgrade WHERE castle_id= :id:")
    void deleteTrapUpgradeByCastle(final int id);
    
    @Query("UPDATE castle SET side = :side: WHERE id = :id:")
    void updateSide(final int id, final CastleSide side);
    
    void save(final CastleFunctionData functionData);
    
    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId:")
    void deleteSiegeByCastle(final int castleId);
    
    @Query("DELETE FROM siege_clans WHERE clan_id=:clanId:")
    void deleteSiegeByClan(final int clanId);
    
    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId: and type = 2")
    void deleteWaintingClansByCastle(final int castleId);
    
    @Query("SELECT clan_id,type FROM siege_clans where castle_id=:castleId:")
    List<SiegeClanData> findSiegeClansByCastle(final int castleId);
    
    void save(final SiegeClanData siegeClanData);
    
    @Query("DELETE FROM siege_clans WHERE castle_id=:castleId: and clan_id=:clanId:")
    void deleteSiegeClanByCastle(final int clanId, final int castleId);
    
    @Query("SELECT * FROM castle")
    List<CastleData> findAll();
}
