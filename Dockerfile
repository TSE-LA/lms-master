FROM gradle:5.5.1-jdk11 as build

COPY . .

# TARGET argument should be either jarvis or unitel
ARG TARGET

# Build server
RUN gradle :erin-lms-${TARGET}-server:clean :erin-lms-${TARGET}-server:build

# Build web
RUN gradle :erin-lms-web-new:clean :erin-lms-web-new:${TARGET}ProdWar

FROM mwader/static-ffmpeg:4.4.0
FROM tomcat:9.0-jdk11-corretto

RUN sed -i 's/port="8080"/port="8040"/' /usr/local/tomcat/conf/server.xml

ARG TARGET
ARG serverPath=/home/gradle/erin-lms-java/erin-lms-${TARGET}/erin-lms-${TARGET}-server/build/libs/erin-lms-${TARGET}-server-*.war
ARG clientPath=/home/gradle/erin-lms-web-new/build/libs/${TARGET}/client-new.war

COPY --from=build $serverPath /usr/local/tomcat/webapps/ROOT.war
COPY --from=build $clientPath /usr/local/tomcat/webapps/client.war
COPY --from=mwader/static-ffmpeg:4.4.0 /ffmpeg /usr/local/bin/
COPY --from=mwader/static-ffmpeg:4.4.0 /ffprobe /usr/local/bin/
