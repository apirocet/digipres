#!/bin/bash

#-----------------------------------------------------------------------#
# coptyToRemotes.sh                                                     #
#                                                                       #
# Script to copy source directory to one or more remote                 #
# storage locations.                                                    #
#                                                                       #
# This script is a wrapper for rclone.                                  #
#                                                                       #
# Usage: copyToRemotes.sh [source directory]                            # 
#            source directory:  directory to copy, relative to archive  #
#                               root directory. If not set, the entire  #
#                               archive will be copied.                 #
# Note:  This script only copies new files and updates exisiting files. #
#        No files are deleted.                                          #
#                                                                       #
# January 2020                                                          #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Rclone binary
RCLONE='/bin/rclone'

# Source archive root
ARCHDIR='/archive'

# Podcast inventory
INVENTORY="${ARCHDIR}/poetry_podcast_inventory.xlsx"

# Remotes
declare -a remotes=('tpf-b2:pf-audio-archive')

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage:  $0 [source directory]" 1>&2
    echo "        source directory:  path to directory to copy, relative" 1>&2
    echo "                           to Archive root directory. If not" 1>&2
    echo "                           set, the entire archive will be copied." 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

srcdir="${ARCHDIR}"
copydir="$1"
if [ "X${copydir}" != "X" ]
then
    srcdir="${ARCHDIR}/${copydir}"
    if [ ! -d "${srcdir}" ] 
    then
        echo "Source directory '${srcdir}' does not exist." 1>&2
        usage
    fi
fi

# Copy to each remote
status=0
for rmt in "${remotes[@]}"
do
    # Copy directories
    $RCLONE -v copy "${srcdir}" "${rmt}/${copydir}"
    tstatus=$?
    if [ $tstatus -gt 0 ]
    then
        status=1
    fi

    # Copy inventory file
    $RCLONE -v copy "${INVENTORY}" "${rmt}/"
    tstatus=$?
    if [ $tstatus -gt 0 ]
    then
        status=1
    fi
done

exit $status
