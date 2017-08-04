package org.chiwooplatform.localization.dam.mongo;

import java.util.List;

import org.springframework.data.mongodb.core.query.Query;

public interface LocalizationTemplate<T> {

    void add(T model);

    void save(T model);

    void saveOrUpdate(T model);

    T findOne(Object id);

    T findOne(Query query);

    public List<T> find(Query query);

    public <R> List<R> find(Query query, Class<R> clazz);

    boolean exists(Query query);

    boolean remove(Query query);

    public void batchUpdate(List<T> models);

}
