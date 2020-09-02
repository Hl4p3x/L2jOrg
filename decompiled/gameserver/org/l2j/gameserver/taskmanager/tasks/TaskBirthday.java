// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager.tasks;

import org.l2j.gameserver.taskmanager.TaskType;
import org.l2j.gameserver.data.database.data.PlayerData;
import java.util.List;
import java.util.GregorianCalendar;
import org.l2j.gameserver.engine.mail.MailEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.Attachment;
import org.l2j.gameserver.data.database.data.MailData;
import org.l2j.gameserver.enums.MailType;
import org.l2j.gameserver.Config;
import java.util.Objects;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.util.GameUtils;
import org.l2j.gameserver.taskmanager.TaskManager;
import java.util.Calendar;
import org.l2j.gameserver.taskmanager.Task;

public class TaskBirthday extends Task
{
    private static final String NAME = "birthday";
    private static final Calendar _today;
    private int _count;
    
    public TaskBirthday() {
        this._count = 0;
    }
    
    @Override
    public String getName() {
        return "birthday";
    }
    
    @Override
    public void onTimeElapsed(final TaskManager.ExecutableTask task) {
        final Calendar lastExecDate = Calendar.getInstance();
        final long lastActivation = task.getLastActivation();
        if (lastActivation > 0L) {
            lastExecDate.setTimeInMillis(lastActivation);
        }
        final String rangeDate = invokedynamic(makeConcatWithConstants:(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;, GameUtils.getDateString(lastExecDate.getTime()), GameUtils.getDateString(TaskBirthday._today.getTime()));
        while (!TaskBirthday._today.before(lastExecDate)) {
            this.checkBirthday(lastExecDate.get(1), lastExecDate.get(2), lastExecDate.get(5));
            lastExecDate.add(5, 1);
        }
        this.LOGGER.info("BirthdayManager: {} gifts sent. {}", (Object)this._count, (Object)rangeDate);
    }
    
    private void checkBirthday(final int year, final int month, final int day) {
        final List<PlayerData> charactersData = ((PlayerDAO)DatabaseAccess.getDAO((Class)PlayerDAO.class)).findBirthdayCharacters(year, month, day);
        final String name;
        int age;
        String text;
        MailData mail;
        Attachment attachments;
        charactersData.forEach(characterData -> {
            name = PlayerNameTable.getInstance().getNameById(characterData.getCharId());
            if (Objects.isNull(name)) {
                return;
            }
            else {
                age = year - characterData.getCreateDate().getYear();
                text = Config.ALT_BIRTHDAY_MAIL_TEXT.replace("$c1", name).replace("$s1", String.valueOf(age));
                mail = MailData.of(characterData.getCharId(), Config.ALT_BIRTHDAY_MAIL_SUBJECT, text, MailType.BIRTHDAY);
                attachments = new Attachment(mail.getSender(), mail.getId());
                attachments.addItem("Birthday", Config.ALT_BIRTHDAY_GIFT, 1L, null, null);
                mail.attach(attachments);
                MailEngine.getInstance().sendMail(mail);
                ++this._count;
                return;
            }
        });
        final GregorianCalendar calendar = new GregorianCalendar();
        if (month == 1 && day == 28 && !calendar.isLeapYear(TaskBirthday._today.get(1))) {
            this.checkBirthday(year, month, 29);
        }
    }
    
    @Override
    public void initializate() {
        TaskManager.addUniqueTask("birthday", TaskType.GLOBAL_TASK, "1", "06:30:00", "");
    }
    
    static {
        _today = Calendar.getInstance();
    }
}
