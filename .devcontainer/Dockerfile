FROM gradle:7.1-jdk11-hotspot

RUN mkdir -p /home/gradle/.vscode-server/extensions /home/gradle/.m2 && \
    chown -R gradle /home/gradle/.vscode-server /home/gradle/.m2
USER gradle

CMD [ "sleep", "infinity" ]
