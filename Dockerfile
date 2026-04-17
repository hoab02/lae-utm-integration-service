############ Builder Stage Parent ###################
FROM registry.viettelpost.vn/library/maven:3.9.11-eclipse-temurin-21 AS builder

WORKDIR /server/fms-parent
ADD ./fms-parent /server/fms-parent
#RUN --mount=type=bind,source=./settings.xml,target=/root/.m2/settings.xml mvn install -Dmaven.test.skip=true
RUN mvn clean install -Dmaven.test.skip=true

WORKDIR /server/fms-common
ADD ./fms-common /server/fms-common
#RUN --mount=type=bind,source=./settings.xml,target=/root/.m2/settings.xml mvn install -Dmaven.test.skip=true
RUN mvn clean install -Dmaven.test.skip=true

WORKDIR /server/fms-utm-integration-service
ADD ./ /server/fms-utm-integration-service
#RUN --mount=type=bind,source=./settings.xml,target=/root/.m2/settings.xml mvn install -Dmaven.test.skip=true
RUN mvn -f pom.xml clean install -Dmaven.test.skip=true

########### Run stage #####################
FROM registry.viettelpost.vn/library/jdk-community:21.0.2-ol9
ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en' TZ='Asia/Ho_Chi_Minh'
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
RUN microdnf update \
    && microdnf install curl telnet ca-certificates freetype fontconfig \
    && microdnf update \
    && microdnf clean all \
    && mkdir /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments \
    && echo "securerandom.source=file:/dev/urandom" >> /usr/lib64/graalvm/graalvm-community-java21/lib/security/java.security

WORKDIR /deployments
COPY --from=builder /server/fms-utm-integration-service/target/ /deployments

EXPOSE 8100
USER 1001

#ENTRYPOINT exec java $JAVA_OPTIONS -Dquarkus.config.locations=/deployments/resources/application.properties -jar /deployments/fms-utm-integration-service-0.0.1.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -Dspring.config.additional-location=file:/deployments/resources/application.yml -jar /deployments/utm-integration-0.0.1.jar"]
