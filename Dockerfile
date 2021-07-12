FROM openjdk:11-jre

ADD ./build/libs/aliyun-client.jar /application.jar

CMD java -jar application.jar
