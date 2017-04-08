package org.tafia.spider.processor;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.tafia.spider.component.DataMessage;
import org.tafia.spider.component.HttpContent;
import org.tafia.spider.component.HttpContentProcessor;
import org.tafia.spider.component.TaskMessage;
import org.tafia.spider.util.IdGen;
import org.tafia.spider.util.UrlUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Dason on 2017/4/8.
 */
@Component("imdb-year-processor")
public class ImdbYearProcessor implements HttpContentProcessor {

    @Override
    public DataMessage process(HttpContent httpContent) {
        Document document = Jsoup.parse(httpContent.getText());
        Element table = document.body().getElementsByClass("splash").get(0);
        Elements as = table.getElementsByTag("a");
        List<TaskMessage> taskMessages = as.stream()
                .map(element -> {
                    String href = element.attr("href");
                    String url = UrlUtils.canonicalizeUrl(href, httpContent.getUrl());
                    TaskMessage taskMessage = new TaskMessage();
                    taskMessage.setUrl(url);
                    taskMessage.setTaskId(IdGen.uuid());
                    taskMessage.setProcessorId("imdb-list-processor");
                    return taskMessage;
                })
                .collect(Collectors.toList());
        DataMessage dataMessage = new DataMessage();
        dataMessage.setTaskId(httpContent.getTaskId());
        dataMessage.setTasks(taskMessages);
        return dataMessage;
    }
}
