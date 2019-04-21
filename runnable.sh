#!/bin/bash
export CLASSPATH="$CLASSPATH:/home/ec2-user/cnv-project"
source /home/ec2-user/.bashrc

cd /home/ec2-user/cnv-project
java pt.ulisboa.tecnico.cnv.server.WebServer
