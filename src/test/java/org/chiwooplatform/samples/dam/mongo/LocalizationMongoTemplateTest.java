package org.chiwooplatform.samples.dam.mongo;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import org.chiwooplatform.localization.dam.mongo.LocalizationMongoTemplate;
import org.chiwooplatform.localization.model.LanMessage;
import org.chiwooplatform.localization.support.WebUtils;
import org.chiwooplatform.samples.AbstractMongoTests;
import org.junit.Test;
import org.junit.runner.RunWith;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles(profiles = {
        // "home",
        "default" })
@RunWith(SpringRunner.class)
@SpringBootTest(classes = { AbstractMongoTests.class,
        LocalizationMongoTemplateTest.MongoConfiguration.class })
public class LocalizationMongoTemplateTest {

    @Autowired
    private LocalizationMongoTemplate template;

    @EnableMongoRepositories
    @Configuration
    static class MongoConfiguration {
        @Bean
        public LocalizationMongoTemplate localizationMongoTemplate(
                MongoTemplate mongoTemplate) {
            log.info("mongoTemplate: {}", mongoTemplate);
            return new LocalizationMongoTemplate(mongoTemplate);
        }
    }

    LanMessage model() {
        LanMessage model = new LanMessage(WebUtils.uuid(), "hello", "ko", "안녕하세요.",
                "aider", System.currentTimeMillis());
        return model;
    }

    private List<String> toList(final String filename) throws IOException {
        InputStream in = LocalizationMongoTemplateTest.class
                .getResourceAsStream(filename);
        String en = IOUtils.toString(in, "UTF-8");
        return Arrays.asList(en.split("\n"));
    }

    private List<LanMessage> toLocaleList(final List<String> list, final String locale) {
        final List<LanMessage> enMsgs = list.stream()
                .map(v -> v = v.replaceAll("(\r\n|\r|\n|\n\r)", "")).map(m -> {
                    String[] msg = m.split("=");
                    final String id = WebUtils.uuid();
                    final String code = msg[0];
                    final String lang = locale;
                    final String value = StringEscapeUtils.unescapeJava(msg[1]);
                    final String creator = "aider";
                    final Long createTime = System.currentTimeMillis();

                    final LanMessage model = new LanMessage(id, code, lang, value,
                            creator, createTime);

                    return model;
                }).collect(Collectors.toList());
        return enMsgs;
    }

    @Test
    public void loadData() throws Exception {
        final List<LanMessage> english = toLocaleList(toList("/en.txt"), "en");
        final List<LanMessage> germany = toLocaleList(toList("/de.txt"), "de");
        final List<LanMessage> france = toLocaleList(toList("/fr.txt"), "fr");
        final List<LanMessage> korean = toLocaleList(toList("/ko.txt"), "ko");
        template.batchUpdate(english);
        template.batchUpdate(germany);
        template.batchUpdate(france);
        template.batchUpdate(korean);
    }

    @Test
    public void ut1001_save() throws Exception {
        LanMessage model = model();
        template.save(model);
        log.info("result: {}", template.findOne(model.getId()));
    }

    @Test
    public void ut1002_findOne() throws Exception {
        LanMessage model = model();
        Query query = Query.query(Criteria.where("_id").is(model.getId()));
        LanMessage result = template.findOne(query);
        log.info("result: {}", result);
    }

    @Test
    public void ut1003_findQuery() throws Exception {
        final String word = "access";
        // word = "hello";
        Query query = Query.query(Criteria.where("messages.value").regex(word, "i"));
        List<LanMessage> results = template.find(query);
        log.info("query: {}", query.toString());
        log.info("results: {}", results);
    }
}
