#!bin/bash
###
## NOTE: Should `docker-compose up --no-start` before you run this script
## Volume resotre
docker run --rm -v jenkins-sample_jenkins_home:/var/jenkins_home -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-jenkins_home.tar
docker run --rm -v jenkins-sample_sonarqube_data:/opt/sonarqube/data -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-sonarqube_data.tar
docker run --rm -v jenkins-sample_sonarqube_postgresql_data:/var/lib/postgresql/data -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-sonarqube_postgresql_data.tar

## The other volumes
# docker run --rm -v jenkins-sample_sonarqube_postgresql:/var/lib/postgresql -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-sonarqube_postgresql.tar
# docker run --rm -v jenkins-sample_sonarqube_extensions:/opt/sonarqube/extensions -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-sonarqube_extensions.tar
# docker run --rm -v jenkins-sample_sonarqube_logs:/opt/sonarqube/logs -v `pwd`:/backup busybox tar xvf /backup/volume_backup/bk-sfdx-poc-sonarqube_logs.tar
