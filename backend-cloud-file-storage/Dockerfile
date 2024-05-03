FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8088
# copy target folder
ADD backend-cloud-file-storage/target ./target
ENTRYPOINT ["java", "-jar", "target/backend-cloud-file-storage-0.0.1-SNAPSHOT.jar"]