package org.tafia.spider.component;

import org.apache.http.cookie.Cookie;

import java.util.List;
import java.util.Map;

/**
 * Created by Dason on 2017/4/2.
 */
public class TaskMessage {

    private String taskId;

    private String processorId;

    private String url;

    private String method;

    private String charset;

    private String proxy;

    private Map<String, String> headers;

    private List<Cookie> cookies;

    private Map<String, String> forms;

    private boolean isBinary;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProcessorId() {
        return processorId;
    }

    public void setProcessorId(String processorId) {
        this.processorId = processorId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public List<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(List<Cookie> cookies) {
        this.cookies = cookies;
    }

    public Map<String, String> getForms() {
        return forms;
    }

    public void setForms(Map<String, String> forms) {
        this.forms = forms;
    }

    public boolean isBinary() {
        return isBinary;
    }

    public void setBinary(boolean binary) {
        isBinary = binary;
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }
}
