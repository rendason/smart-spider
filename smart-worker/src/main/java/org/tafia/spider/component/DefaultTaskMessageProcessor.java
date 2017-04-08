package org.tafia.spider.component;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by Dason on 2017/4/7.
 */
@Component
public class DefaultTaskMessageProcessor implements TaskMessageProcessor, ApplicationContextAware {

    private RequestConfig defaultRequestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();

    private Map<String, HttpContentProcessor> processorMap;

    @Autowired
    private DataProducer dataProducer;

    @Override
    @Async
    public void process(TaskMessage taskMessage) {
        if (!processorMap.containsKey(taskMessage.getProcessorId())) return;
        BasicCookieStore cookieStore = new BasicCookieStore();
        addCookies(cookieStore, taskMessage.getCookies());
        try (CloseableHttpClient httpClient = getHttpClient(taskMessage, cookieStore)) {
            HttpUriRequest httpUriRequest = getHttpUriRequest(taskMessage);
            try (CloseableHttpResponse httpResponse = httpClient.execute(httpUriRequest)) {
                HttpContent httpContent = getHttpContent(taskMessage, httpResponse);
                HttpContentProcessor httpContentProcessor = processorMap.get(taskMessage.getProcessorId());
                DataMessage dataMessage = httpContentProcessor.process(httpContent);
                if (dataMessage == null) return;
                dataMessage.setTaskId(taskMessage.getTaskId());
                dataMessage.setCookies(cookieStore.getCookies());
                dataProducer.produce(dataMessage);
            }
        } catch (Exception e) {
            dataProducer.produce(getExceptionDataMessage(taskMessage, e));
        }
    }

    private void addCookies(CookieStore cookieStore, List<Cookie> cookies) {
        if (cookies == null) return;
        cookies.forEach(cookieStore::addCookie);
    }

    private void addHeader(RequestBuilder requestBuilder, Map<String, String> headers) {
        if (headers == null) return;
        headers.forEach(requestBuilder::addHeader);
    }

    private HttpEntity getFormEntity(Map<String, String> forms, Charset charset) {
        List<NameValuePair> nameValuePairs = forms.entrySet().stream()
                .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        return new UrlEncodedFormEntity(nameValuePairs, charset);
    }

    private CloseableHttpClient getHttpClient(TaskMessage taskMessage, CookieStore cookieStore) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                .setDefaultCookieStore(cookieStore);
        if (StringUtils.isNotEmpty(taskMessage.getProxy())) {
            httpClientBuilder.setProxy(HttpHost.create(taskMessage.getProxy()));
        }
        return httpClientBuilder.build();
    }

    private Charset getCharset(String charsetName) {
        return StringUtils.isEmpty(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    private HttpUriRequest getHttpUriRequest(TaskMessage taskMessage) {
        String httpMethod = StringUtils.defaultString(taskMessage.getMethod(), "GET").toUpperCase();
        Charset charset = getCharset(taskMessage.getCharset());
        RequestBuilder requestBuilder = RequestBuilder.create(httpMethod)
                .setUri(taskMessage.getUrl())
                .setConfig(defaultRequestConfig)
                .setCharset(charset);
        addHeader(requestBuilder, taskMessage.getHeaders());
        if ("POST".equalsIgnoreCase(httpMethod)) {
            requestBuilder.setEntity(getFormEntity(taskMessage.getForms(), charset));
        }
        return requestBuilder.build();
    }

    private HttpContent getHttpContent(TaskMessage taskMessage, HttpResponse httpResponse) throws IOException {
        byte[] bytes = IOUtils.toByteArray(httpResponse.getEntity().getContent());
        HttpContent httpContent = new HttpContent();
        httpContent.setTaskId(taskMessage.getTaskId());
        httpContent.setProcessorId(taskMessage.getProcessorId());
        httpContent.setUrl(taskMessage.getUrl());
        httpContent.setStatusCode(httpResponse.getStatusLine().getStatusCode());
        if (taskMessage.isBinary()) {
            httpContent.setBytes(bytes);
        } else {
            httpContent.setText(new String(bytes, getCharset(taskMessage.getCharset())));
        }
        return httpContent;
    }

    private DataMessage getExceptionDataMessage(TaskMessage taskMessage, Throwable throwable) {
        DataMessage dataMessage = new DataMessage();
        dataMessage.setTaskId(taskMessage.getTaskId());
        dataMessage.setThrowable(throwable);
        return dataMessage;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        processorMap = applicationContext.getBeansOfType(HttpContentProcessor.class);
    }
}
