// 
// Decompiled by Procyon v0.5.36
// 

package org.l2j.gameserver.taskmanager;

import org.slf4j.LoggerFactory;
import java.util.concurrent.ScheduledFuture;
import java.util.Date;
import java.util.Calendar;
import java.text.DateFormat;
import org.l2j.commons.threading.ThreadPool;
import java.util.Objects;
import org.l2j.gameserver.taskmanager.tasks.TaskShutdown;
import org.l2j.gameserver.taskmanager.tasks.TaskRestart;
import org.l2j.gameserver.taskmanager.tasks.TaskBirthday;
import org.l2j.gameserver.data.database.data.TaskData;
import org.l2j.commons.database.DatabaseAccess;
import org.l2j.gameserver.data.database.dao.TaskDAO;
import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.slf4j.Logger;

public final class TaskManager
{
    private static final Logger LOGGER;
    private final IntMap<Task> tasks;
    
    private TaskManager() {
        this.tasks = (IntMap<Task>)new CHashIntMap();
        this.initializate();
        this.startAllTasks();
        TaskManager.LOGGER.info("Loaded: {} Tasks", (Object)this.tasks.size());
    }
    
    public static boolean addUniqueTask(final String task, final TaskType type, final String param1, final String param2, final String param3) {
        return addUniqueTask(task, type, param1, param2, param3, 0L);
    }
    
    private static boolean addUniqueTask(final String task, final TaskType type, final String param1, final String param2, final String param3, final long lastActivation) {
        final TaskDAO taskDao = (TaskDAO)DatabaseAccess.getDAO((Class)TaskDAO.class);
        if (!taskDao.existsWithName(task)) {
            final TaskData data = new TaskData();
            data.setLastActivation(lastActivation);
            data.setName(task);
            data.setType(type.name());
            data.setParam1(param1);
            data.setParam2(param2);
            data.setParam3(param3);
            return taskDao.save((Object)data);
        }
        return false;
    }
    
    private void initializate() {
        this.registerTask(new TaskBirthday());
        this.registerTask(new TaskRestart());
        this.registerTask(new TaskShutdown());
    }
    
    private void registerTask(final Task task) {
        this.tasks.computeIfAbsent(task.getName().hashCode(), k -> {
            task.initializate();
            return task;
        });
    }
    
    private void startAllTasks() {
        final Task task;
        TaskType type;
        ExecutableTask current;
        ((TaskDAO)DatabaseAccess.getDAO((Class)TaskDAO.class)).findAll().forEach(data -> {
            task = (Task)this.tasks.get(data.getName().trim().toLowerCase().hashCode());
            if (!Objects.isNull(task)) {
                type = TaskType.valueOf(data.geType());
                if (TaskType.NONE != type) {
                    current = new ExecutableTask(task, type, data);
                    this.launchTask(current);
                }
            }
        });
    }
    
    private boolean launchTask(final ExecutableTask task) {
        boolean b = false;
        switch (task.getType()) {
            case STARTUP: {
                task.run();
                b = false;
                break;
            }
            case SHEDULED: {
                final long delay = Long.parseLong(task.getParam1());
                task.scheduled = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)task, delay);
                b = true;
                break;
            }
            case FIXED_SHEDULED: {
                final long delay = Long.parseLong(task.getParam1());
                final long interval = Long.parseLong(task.getParam2());
                task.scheduled = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)task, delay, interval);
                b = true;
                break;
            }
            case TIME: {
                try {
                    final Date desired = DateFormat.getInstance().parse(task.getParam1());
                    final long diff = desired.getTime() - System.currentTimeMillis();
                    if (diff >= 0L) {
                        task.scheduled = (ScheduledFuture<?>)ThreadPool.schedule((Runnable)task, diff);
                        b = true;
                        break;
                    }
                    TaskManager.LOGGER.info("Task {} is obsoleted", (Object)task.getId());
                }
                catch (Exception e) {
                    TaskManager.LOGGER.warn(e.getMessage(), (Throwable)e);
                }
                b = false;
                break;
            }
            case SPECIAL: {
                final ScheduledFuture<?> result = task.getTask().launchSpecial(task);
                if (result != null) {
                    task.scheduled = result;
                    b = true;
                    break;
                }
                b = false;
                break;
            }
            case GLOBAL_TASK: {
                final long interval = Long.parseLong(task.getParam1()) * 86400000L;
                final String[] hour = task.getParam2().split(":");
                if (hour.length != 3) {
                    TaskManager.LOGGER.warn("Task {} has incorrect parameters {}", (Object)task.getId(), (Object)task.getParam2());
                    b = false;
                    break;
                }
                final Calendar check = Calendar.getInstance();
                check.setTimeInMillis(task.getLastActivation() + interval);
                final Calendar min = Calendar.getInstance();
                try {
                    min.set(11, Integer.parseInt(hour[0]));
                    min.set(12, Integer.parseInt(hour[1]));
                    min.set(13, Integer.parseInt(hour[2]));
                }
                catch (Exception e2) {
                    TaskManager.LOGGER.warn(invokedynamic(makeConcatWithConstants:(Ljava/lang/String;ILjava/lang/String;)Ljava/lang/String;, task.getParam2(), task.getId(), e2.getMessage()), (Throwable)e2);
                    b = false;
                    break;
                }
                long delay = min.getTimeInMillis() - System.currentTimeMillis();
                if (check.after(min) || delay < 0L) {
                    delay += interval;
                }
                task.scheduled = (ScheduledFuture<?>)ThreadPool.scheduleAtFixedRate((Runnable)task, delay, interval);
                b = true;
                break;
            }
            default: {
                b = false;
                break;
            }
        }
        return b;
    }
    
    public static TaskManager getInstance() {
        return Singleton.INSTANCE;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger(TaskManager.class.getName());
    }
    
    private static class Singleton
    {
        private static final TaskManager INSTANCE;
        
        static {
            INSTANCE = new TaskManager();
        }
    }
    
    public static class ExecutableTask implements Runnable
    {
        private final Task task;
        private final TaskType type;
        private final TaskData data;
        ScheduledFuture<?> scheduled;
        
        private ExecutableTask(final Task task, final TaskType type, final TaskData data) {
            this.task = task;
            this.type = type;
            this.data = data;
        }
        
        @Override
        public void run() {
            this.task.onTimeElapsed(this);
            final long lastActivation = System.currentTimeMillis();
            this.data.setLastActivation(lastActivation);
            ((TaskDAO)DatabaseAccess.getDAO((Class)TaskDAO.class)).updateLastActivation(this.data.getId(), lastActivation);
            if (this.type == TaskType.SHEDULED || this.type == TaskType.TIME) {
                this.stopTask();
            }
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final ExecutableTask that = (ExecutableTask)o;
            return Objects.equals(this.data, that.data);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(this.data);
        }
        
        public Task getTask() {
            return this.task;
        }
        
        public TaskType getType() {
            return this.type;
        }
        
        public int getId() {
            return this.data.getId();
        }
        
        public long getLastActivation() {
            return this.data.getLastActivation();
        }
        
        private void stopTask() {
            this.task.onDestroy();
            if (this.scheduled != null) {
                this.scheduled.cancel(true);
            }
        }
        
        String getParam1() {
            return this.data.getParam1();
        }
        
        String getParam2() {
            return this.data.getParam2();
        }
        
        public String getParam3() {
            return this.data.getparam3();
        }
    }
}
