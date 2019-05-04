#!/bin/bash
export CLASSPATH="$CLASSPATH:/home/ec2-user/cnv-project/src/java/"
source /home/ec2-user/.bashrc

export MAVEN_OPTS="-XX:-UseSplitVerifier"
cd /home/ec2-user/cnv-project/cnv1819/WebServer
mvn exec:java
