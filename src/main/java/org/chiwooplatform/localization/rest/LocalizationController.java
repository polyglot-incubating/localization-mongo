package org.chiwooplatform.localization.rest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.chiwooplatform.localization.dam.mongo.LocalizationMongoTemplate;
import org.chiwooplatform.localization.model.LanMessage;
import org.chiwooplatform.localization.support.WebUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Data MongoDB - Reference Documentation
 * https://docs.spring.io/spring-data/data-mongo/docs/current/reference/html/
 * 
 * @author aider
 *
 */
@Slf4j
@CrossOrigin
@RestController
public class LocalizationController {
    protected static final String BASE_URI = "/i18n/messages";

    private final LocalizationMongoTemplate template;

    @Autowired
    public LocalizationController(LocalizationMongoTemplate localizationMongoTemplate) {
        this.template = localizationMongoTemplate;
    }

    private static Query queryId(LanMessage model) {
        return Query.query(Criteria.where("code").is(model.getCode()).and("lang")
                .is(model.getLang()));
    }

    @RequestMapping(value = BASE_URI + "/info", method = RequestMethod.GET, consumes = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<Object> info(@RequestParam Map<String, Object> param) {
        log.info("param: {}", param);
        param.forEach((k, v) -> {
            log.debug("k: {}, v: {}", k, v);
        });
        return ResponseEntity.ok(param);
    }

    @PostMapping(value = BASE_URI, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LanMessage> add(@RequestBody LanMessage model) {
        log.debug("model: {}", model);
        template.add(model);
        LanMessage result = template.findOne(queryId(model));
        URI location = WebUtils.uriLocation("/{code}/{lang}", model.getCode(),
                model.getLang());
        return ResponseEntity.created(location).body(result);
    }

    @PutMapping(value = BASE_URI
            + "/{code}/{lang}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LanMessage> modify(@RequestBody LanMessage model) {
        log.debug("model: {}", model);
        template.save(model);
        LanMessage result = template.findOne(queryId(model));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = BASE_URI
            + "/{code}/{lang}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LanMessage> get(@PathVariable("code") String code,
            @PathVariable("lang") String lang) {

        Query query = Query.query(Criteria.where("code").is(code).and("lang").is(lang));
        log.debug("query: {}", query);
        LanMessage result = template.findOne(query);
        return ResponseEntity.ok(result);

    }

    @GetMapping(value = BASE_URI + "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LanMessage>> query(
            @RequestParam Map<String, Object> param) {
        log.debug("param: {}", param);
        Criteria criteria = Criteria.where("_id").exists(true);
        if (param.containsKey("value")) {
            criteria.and("value").regex((String) param.get("value"), "i");
        }
        if (param.containsKey("code")) {
            criteria.and("code").regex((String) param.get("code"), "i");
        }
        if (param.containsKey("codes")) {
            String codes = (String) param.get("codes");
            if (codes != null) {
                List<String> codeList = Arrays
                        .asList(StringUtils.delimitedListToStringArray(codes, ","));
                criteria.and("code").in(codeList);
            }
        }
        if (param.containsKey("lang")) {
            criteria.and("lang").is((String) param.get("lang"));
        }
        Query query = Query.query(criteria).limit(200);
        log.debug("query: {}", query);
        List<LanMessage> result = template.find(query);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(value = BASE_URI
            + "/{code}/{lang}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> remove(@PathVariable("code") String code,
            @PathVariable("lang") String lang) {
        final Query query = Query
                .query(Criteria.where("code").is(code).and("lang").is(lang));
        LanMessage result = template.findOne(query);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        boolean deleted = template.remove(query);
        log.debug("query: {}", query);
        log.debug("deleted: {}", deleted);
        if (deleted) {
            return ResponseEntity.noContent().build();
        }
        else {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

    }

}
