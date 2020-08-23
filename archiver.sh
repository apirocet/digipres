#!/bin/bash

#-----------------------------------------------------------------------#
# archiver.sh                                                           #
#                                                                       #
# Wrapper script to archive Poetry Foundation audio resources.          #
# object contents in the staging directory.                             #
#                                                                       #
# See the Audio Archive Workflow document for more information on the   #
# archiving steps.                                                      #
#                                                                       #
# Usage:                                                                #
# createTPFBag.sh [-r] <staging directory> <archive root directory>     #
#                           -r:  Replace bag directory if it exists     #
#            staging directory:  path to top-level staging directory    #
#                                tree to copy to a bag.                 #
#       archive root directory:  base of the archive directory tree,    #
#                                where bag will be written              #
#                                                                       #
# August 20 20                                                          #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Work directory
wkdir=`dirname $0`

# Bash colors
ok='\e[32'
warn='\e[33\e[21'
err='\e[31\e[21'
reset='\e[0'
#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage:  $0 [-r] <staging directory> <archive root directory>" 1>&2
    echo "                       -r:  Replace bag directory if it exists" 1>&2
    echo "        staging directory:  path to top-level staging directory" 1>&2
    echo "                            tree to copy to a bag." 1>&2
    echo "   archive root directory:  base of the archive directory tree," 1>&2
    echo "                            where bag will be written" 1>&2
    exit 1
}

send_ok () {
    echo -e "${ok}${1}${reset}"
}
send_warn () {
    echo -e "${warn}${1}${reset}"
}
send_err () {
    echo -e "${err}${1}${reset}"
    echo -e "${err}Exiting.${reset}"
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Get replace flag
while getopts "r" options
do
    case "${options}" in
         r)
             replace="true"
             rflag="-r"
             ;;
    esac
done
shift "$((OPTIND-1))"

# Step 1:  Create derivatives


exit 0
