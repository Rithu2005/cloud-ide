FROM ubuntu:22.04

RUN apt-get update && apt-get install -y \
    openjdk-17-jdk \
    python3 \
    python3-pip \
    python-is-python3 \
    gcc \
    g++ \
    nodejs \
    npm \
    && apt-get clean

WORKDIR /app

COPY target/code-exec-0.0.1-SNAPSHOT.jar app.jar

CMD ["java", "-jar", "app.jar"]
