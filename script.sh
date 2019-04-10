#!/usr/bin/env bash
# Hill@Climb config files for Linux based Environments

export CNV_ROOT=C:/Users/mbele/OneDrive/Documentos/GitHub/cnv1819

export CNV_GEN=$CNV_ROOT/pt/ulisboa/tecnico/cnv/generator

export CNV_SERVER=$CNV_ROOT/pt/ulisboa/tecnico/cnv/server

export CNV_SOLVER=$CNV_ROOT/pt/ulisboa/tecnico/cnv/solver

export CNV_UTIL=$CNV_ROOT/pt/ulisboa/tecnico/cnv/util


export CNV_EXAMPLES=$CNV_ROOT/examples

export CNV_SAMPLES=$CNV_ROOT/samples

export CNV_TMP=$HOME/compiled_cnv


# Color codes
export BLINK_ENABLE="\e[5m"
export BLINK_DISABLE="\e[25m"
export BASH_RED="\e[91m"
export BASH_DEFAULT="\e[39m"
export BASH_GREEN="\e[32m"

echo -e " _    _ _ _ _          _____ _ _           _     "
echo -e "| |  | (_) | |  ____  / ____| (_)         | |    "
echo -e "| |__| |_| | | / __ \| |    | |_ _ __ ___ | |__  "
echo -e "|  __  | | | |/ / _\` | |    | | | '_ \` _ \| '_ \ "
echo -e "| |  | | | | | | (_| | |____| | | | | | | | |_) |"
echo -e "|_|  |_|_|_|_|\ \__,_|\_____|_|_|_| |_| |_|_.__/ "
echo -e "               \____/                            "
echo -e ""
echo -e "Hello $USER! Have a lot of fun!"
echo -e ""
echo -e "Project loaded"

mkdir -p $CNV_TMP


#################################################################
#                          FUNCTIONS                            #
#################################################################

# Clean all *.class from all directories
check() {
	
	if [ $? -eq 0 ]; then
		echo -e "$1 ${BASH_GREEN} OK! ${BASH_DEFAULT}"
	else
		echo -e "$1 ${BASH_RED} ${BLINK_ENABLE} FAILED! ${BLINK_DISABLE} ${BASH_DEFAULT}"
	fi

}

clean() {
	rm -f $CNV_GEN/*.class
	rm -f $CNV_SERVER/*.class
	rm -f $CNV_SOLVER/*.class
	rm -f $CNV_UTIL/*.class
	rm -f $CNV_EXAMPLES/*.class
	rm -f $CNV_SAMPLES/*.class
	check Cleaning	
}

compile() {
	javac $CNV_GEN/*.java
	javac $CNV_SERVER/*.java
	javac $CNV_SOLVER/*.java
	javac $CNV_UTIL/*.java
	javac $CNV_EXAMPLES/*.java
	javac $CNV_SAMPLES/*.java
	check Compilation
	cp -r pt/ $CNV_TMP
	check Backup
}

inst() {
	echo "Deleting old solver class files ..."
	rm -r pt/ulisboa/tecnico/cnv/solver/
	check Delete

	echo "Restoring previous compiled version ..."
	cp -r $CNV_TMP/pt/ulisboa/tecnico/cnv/solver $CNV_SOLVER 
	check Copy

	echo "Compiling CNVMetric ..."
	javac $CNV_SAMPLES/CNVMetric.java
	check Compilation

	echo "Instrumenting solver folder with CNVMetric ..."
	java CNVMetric $CNV_SOLVER $CNV_SOLVER
	check Instrumentation

}

check_inst() {
	javap -c pt/ulisboa/tecnico/cnv/solver/*.class | egrep "printStats"
}

wsvc() {
	java pt.ulisboa.tecnico.cnv.server.WebServer	
}

readLog() {
	javac $CNV_UTIL/LogReader.java
	java pt.ulisboa.tecnico.cnv.util.LogReader $1
}


