FROM crizin/java8-tomcat8:0.1

MAINTAINER Crizin <crizin@gmail.com>

COPY . /application

WORKDIR /application

RUN ./gradlew war && \
    cd /application/build/libs && \
    jar -xf learning-spring-mvc-1.0-SNAPSHOT.war && \
    rm -rf /usr/local/tomcat/webapps/* && \
    ln -s /application/build/libs /usr/local/tomcat/webapps/ROOT
