#!/bin/bash

#-----------------------------------------------------------------------#
# verifyTPFBags.sh                                                      #
#                                                                       #
# Script to verify Poetry Foundation BagIt bags and inventory.          #
#                                                                       #
# Usage:                                                                #
# verifyTPFBags.sh <archive root directory>                             #
#       archive root directory:  base of the archive directory tree     #
#                                                                       #
# January 2020                                                          #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Temp directory for inventory and contents
tmpdir="/var/tmp/bagverify" 

# Inventory file
invfile="poetry_podcast_inventory.xlsx"

# LibreOffice command
lo="/opt/libreoffice6.3/program/soffice --headless --convert-to csv --outdir ${tmpdir} ${tmpdir}/${invfile}"

# Archive directory depth
depth=3

# Bagmanager jar path
bagmgr="bagmanager.jar"

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage:  $0 <archive root directory>" 1>&2
    echo "   archive root directory:  base of the archive directory tree," 1>&2
    echo "                            where bag will be written" 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have a valid archive root directory
archrootdir="$1"
if [ ! -d "${archrootdir}" ]
then
    echo "No such archive root directory '$archrootdir'" 1>&2
    usage
fi

# Check that we have an inventory file
if [ ! -e "${archrootdir}/${invfile}" ]
then
    echo "No inventory file found under '${archrootdir}'" 1>&2
    usage
fi

mkdir -p "${tmpdir}"

# Get the directories in the archive
(cd "${archrootdir}" && find . -maxdepth ${depth} -mindepth ${depth} -type d | sed 's/\.\///') | sort > "${tmpdir}/bags-fs.txt"

# Copy the inventory sheet to the tmpdir and convert it to csv
if ! cp "${archrootdir}/${invfile}" "${tmpdir}/${infile}"
then
    echo "Cannot copy inventory file '${archrootdir}/${invfile}' to '${tmpdir}'.  Exiting." 1>&2
    exit 1
fi

if ! $lo
then
    echo "Cannot convert inventory file '${tmpdir}/${invfile}' to CSV format.  Exiting." 1>&2
    exit 1
fi

# Verify that inventory and directories match

# Verify the bags
#java -jar "${bagmgr}" verify --with-profile "${bagpath}"
exit $?
