// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.data.database.dao;

import org.l2j.commons.database.annotation.Query;
import io.github.joealisson.primitive.ConcurrentIntMap;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.commons.database.DAO;

public interface MailDAO extends DAO<MailData>
{
    @Query("SELECT * FROM mail")
    ConcurrentIntMap<MailData> findAll();
    
    @Query("UPDATE mail SET unread=FALSE WHERE id=:mailId:")
    void markAsRead(final int mailId);
    
    @Query("UPDATE mail SET sender_deleted=TRUE WHERE id=:mailId:")
    void markAsDeletedBySender(final int mailId);
    
    @Query("UPDATE mail SET receiver_deleted=TRUE WHERE id=:mailId:")
    void markAsDeletedByReceiver(final int mailId);
    
    @Query("UPDATE mail SET has_attachment=FALSE WHERE id=:mailId:")
    void deleteAttachment(final int mailId);
    
    @Query("DELETE FROM mail WHERE id=:mailId:")
    void deleteById(final int mailId);
}
