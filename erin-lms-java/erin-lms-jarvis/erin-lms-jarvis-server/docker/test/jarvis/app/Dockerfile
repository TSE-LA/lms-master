FROM mwader/static-ffmpeg:4.4.0
FROM tomcat:9.0-jdk11-corretto

RUN mkdir alfresco /usr/local/tomcat/webapps/alfresco

COPY server.xml /usr/local/tomcat/conf/server.xml
# You should be include the ROOT.war file.
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war
COPY client/ /usr/local/tomcat/webapps/client

COPY --from=mwader/static-ffmpeg:4.4.0 /ffmpeg /usr/local/bin/
COPY --from=mwader/static-ffmpeg:4.4.0 /ffprobe /usr/local/bin/
