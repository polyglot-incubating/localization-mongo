package org.chiwooplatform.localization.dam.mongo;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import org.chiwooplatform.localization.model.LanMessage;

import com.mongodb.WriteResult;

@Repository
public class LocalizationMongoTemplate
        implements LocalizationTemplate<LanMessage>, InitializingBean {

    private final MongoTemplate template;

    private final String COLLECTION_NEME = LanMessage.COLLECTION_NEME;

    @Autowired
    public LocalizationMongoTemplate(MongoTemplate mongoTemplate) {
        this.template = mongoTemplate;
    }

    @Override
    public void add(LanMessage model) {
        template.insert(model, COLLECTION_NEME);
    }

    @Override
    public void save(LanMessage model) {
        template.save(model, COLLECTION_NEME);
    }

    @Override
    public void saveOrUpdate(LanMessage model) {
        template.save(model, COLLECTION_NEME);
    }

    @Override
    public LanMessage findOne(Object id) {
        return template.findOne(Query.query(Criteria.where("_id").is(id)),
                LanMessage.class, COLLECTION_NEME);
    }

    @Override
    public LanMessage findOne(Query query) {
        return template.findOne(query, LanMessage.class, COLLECTION_NEME);
    }

    @Override
    public List<LanMessage> find(Query query) {
        return template.find(query, LanMessage.class, COLLECTION_NEME);
    }

    @Override
    public <R> List<R> find(Query query, Class<R> clazz) {
        return template.find(query, clazz, COLLECTION_NEME);
    }

    @Override
    public boolean exists(Query query) {
        return template.exists(query, COLLECTION_NEME);
    }

    @Override
    public boolean remove(Query query) {
        WriteResult result = template.remove(query, COLLECTION_NEME);
        return result.getN() == 1 ? true : false;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Objects.requireNonNull(template, "mongoTemplate is required.");

        Index idx = new Index().named("idx_code_lang").on("code", Direction.ASC)
                .on("lang", Direction.ASC).unique();
        template.indexOps(LanMessage.class).ensureIndex(idx);
    }

    public void batchUpdate(List<LanMessage> models) {
        for (LanMessage model : models) {
            try {
                Query query = new Query().addCriteria(Criteria.where("code")
                        .is(model.getCode()).and("lang").is(model.getLang()));
                LanMessage result = findOne(query);
                if (result != null) {
                    template.save(model, COLLECTION_NEME);
                }
                else {
                    template.insert(model, COLLECTION_NEME);
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
