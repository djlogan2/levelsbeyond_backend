FROM ubuntu
WORKDIR levelsbeyond
COPY lbtest.tgz /lbtest/lbtest.tgz
ENV DEBIAN_FRONTEND="noninteractive"
RUN echo "mysql-server mysql-server/root_password password ca014dedjl" | debconf-set-selections && \
    echo "mysql-server mysql-server/root_password_again password ca014dedjl" | debconf-set-selections && \
    apt-get -y update && \
    apt-get -y upgrade && \
    apt-get -y install curl && \
    curl -sL https://deb.nodesource.com/setup_7.x | bash && \
    apt-get -y install default-jre nodejs mysql-server
RUN tar xzf /lbtest/lbtest.tgz && \
    mv run.sh installed_run.sh && \
    cat META-INF/persistence.xml | sed 's/"user"/"root"/g' | sed 's/"password"/"ca014dedjl"/g' > z && \
    mv z META-INF/persistence.xml && \
    cat mysql.sql | sed "s/'user'/'root'/g" > z && \
    mv z mysql.sql && \
    service mysql start && \
    mysql -uroot -pca014dedjl < mysql.sql && \
    echo "#!/bin/bash" >> run.sh && \
    echo "nohup mysqld &" >> run.sh && \
    echo "java -cp WEB-INF/classes/:WEB-INF/lib/*:. david.logan.levels.beyond.Main" >> run.sh && \
    chmod +x run.sh
CMD ["./run.sh"]
