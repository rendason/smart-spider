package org.tafia.spider.component;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by Dason on 2017/4/2.
 */
public class DataMessage {

    private String taskId;

    List<TaskMessage> tasks;

    private Map<String, Object> data;

    private List<Cookie> cookies;

    private Throwable throwable;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public List<TaskMessage> getTasks() {
        return tasks;
    }

    public void setTasks(List<TaskMessage> tasks) {
        this.tasks = tasks;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
}
