package org.chiwooplatform.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;

@SpringBootApplication
public class LocalizationMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocalizationMongoApplication.class, args);
    }

    @Configuration
    class MongoConfiguration {

        @Autowired
        private MongoDbFactory mongoFactory;

        @Autowired
        private MongoMappingContext mongoMappingContext;

        @Bean
        public MappingMongoConverter mongoConverter() throws Exception {
            final DefaultMongoTypeMapper typeMapper = new DefaultMongoTypeMapper(null);
            DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoFactory);
            MappingMongoConverter mongoConverter = new MappingMongoConverter(
                    dbRefResolver, mongoMappingContext);
            // this is my customization
            mongoConverter.setTypeMapper(typeMapper);
            // mongoConverter.setMapKeyDotReplacement("_");
            mongoConverter.afterPropertiesSet();
            return mongoConverter;
        }
    }

    @Bean
    public Jackson2ObjectMapperBuilder objectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.serializationInclusion(JsonInclude.Include.NON_NULL);
        builder.indentOutput(true);
        builder.failOnUnknownProperties(false);
        return builder;
    }
}
