FROM gradle:6.8-jdk11

RUN mkdir -p /home/gradle/.vscode-server/extensions /home/gradle/.m2 && \
    chown -R gradle /home/gradle/.vscode-server /home/gradle/.m2

USER gradle
