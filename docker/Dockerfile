FROM centos:latest
RUN yum install -y "https://www.igniterealtime.org/downloadServlet?filename=openfire/openfire-4.3.0-1.x86_64.rpm"
EXPOSE 3478/tcp 3479/tcp 5222/tcp 5223/tcp 5229/tcp 7070/tcp 7443/tcp 7777/tcp 9090/tcp 9091/tcp
COPY openfire.xml /opt/openfire/conf/openfire.xml
COPY openfire.script /opt/openfire/embedded-db/
ENTRYPOINT ["/opt/openfire/bin/openfire.sh", "start"]
