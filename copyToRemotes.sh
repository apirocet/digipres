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
#            source directory:  directory to copy                       #
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

# Podcast inventory
INVENTORY="poetry_podcast_inventory.xlsx"

# Local archive root
ARCHROOT='/archive'

# Remotes
declare -a remotes=('tpf-b2:pf-audio-archive')

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage:  $0 [source directory]" 1>&2
    echo "        source directory:  path to directory to copy" 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

copydir="$1"
if [ "X${copydir}" != "X" ]
then
    if ! rclone lsf "${copydir}" > /dev/null
    then
        echo "ERROR: No such archive directory '${copydir}'" 1>&2
        usage
    fi
fi

# Copy to each remote
status=0
for rmt in "${remotes[@]}"
do
    # Copy directories
    relpath="${copydir#*:}"
    relpath="${relpath#${ARCHROOT}}"
    $RCLONE -v copy "${copydir}" "${rmt}/${relpath#/}"
    tstatus=$?
    if [ $tstatus -gt 0 ]
    then
        echo "ERROR: Cannot copy '${copydir}' to '${rmt}/${relpath#/}'" 1>&2
        status=1
    fi

    # Copy inventory file
    $RCLONE -v copy "${copydir%:*}:${INVENTORY}" "${rmt}"
    tstatus=$?
    if [ $tstatus -gt 0 ]
    then
        echo "ERROR: Cannot copy '${INVENTORY}' to '${rmt}'" 1>&2
        status=1
    fi
done

exit $status
