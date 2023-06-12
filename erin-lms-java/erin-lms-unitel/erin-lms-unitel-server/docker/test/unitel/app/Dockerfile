FROM tomcat:9.0-jdk11-corretto

COPY server.xml /usr/local/tomcat/conf/server.xml
# You should be include the ROOT.war file.
COPY ROOT.war /usr/local/tomcat/webapps/ROOT.war
COPY client /usr/local/tomcat/webapps/client
