[[overview]]

MongoDB 를 통해 다국어 정보를 관리하는  서비스를 구성 한다.


### Environment Properties

"application-gen.yml" is the environment file.
~~~~
server:
  port: ${port:8080}

spring:
  profiles:
    active:  default
  session:
    store-type: hash-map
  data:
    mongodb:
      host: localhost
      port: 27017
      database: Localization

  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  pattern:
    console: "%d %-5level %logger : %msg%n"
  level:
    root: info
    org.springframework: info
    org.springframework.security: debug
    org.chiwooplatform: debug

---
spring:
  profiles: home
  data:
    mongodb:
      host: 192.168.30.210
      port: 27017
      database: Localization
      
---
spring:
  profiles: dev

logging:
  config: classpath:logback-dev.xml

server:
  port: ${port:8082}}
---
spring:
  profiles: dev

logging:
  config: classpath:logback-dev.xml

server:
  port: ${port:8082}}
~~~~



