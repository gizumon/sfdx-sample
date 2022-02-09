#!bin/bash
## volume store
docker run --rm -v jenkins-sample_jenkins_home:/var/jenkins_home -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-jenkins_home.tar /var/jenkins_home
# docker run --rm -v jenkins-sample_sonarqube_extensions:/opt/sonarqube/extensions -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-sonarqube_extensions.tar /opt/sonarqube/extensions
# docker run --rm -v jenkins-sample_sonarqube_logs:/opt/sonarqube/logs -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-sonarqube_logs.tar /opt/sonarqube/logs
docker run --rm -v jenkins-sample_sonarqube_data:/opt/sonarqube/data -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-sonarqube_data.tar /opt/sonarqube/data
# docker run --rm -v jenkins-sample_sonarqube_postgresql:/var/lib/postgresql -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-sonarqube_postgresql.tar /var/lib/postgresql
docker run --rm -v jenkins-sample_sonarqube_postgresql_data:/var/lib/postgresql/data -v `pwd`:/backup busybox tar cvf /backup/bk-sfdx-poc-sonarqube_postgresql_data.tar /var/lib/postgresql/data
