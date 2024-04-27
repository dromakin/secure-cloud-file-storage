FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8088
# copy target folder
ADD target ./target
ENTRYPOINT ["java", "-jar", "target/netology-cloud-service-0.0.1-SNAPSHOT.jar"]