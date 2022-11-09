FROM openjdk:15-jdk-alpine
RUN apk add --update --no-cache tzdata && \ 
    cp /usr/share/zoneinfo/Asia/Tokyo /etc/localtime && \
    echo "Asia/Tokyo" > /etc/timezone && \
    addgroup -S appusers && \
    adduser -S --disabled-password appuser -G appusers --uid 1001

USER appuser:appusers
COPY build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
