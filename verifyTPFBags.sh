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

# Archive directory depth
depth=3

# Inventory Manager jar path
invmgr="inventory-manager.jar"

# Bagmanager jar path
bagmgr="bagmanager.jar"

# PID
mypid="$$"

# Mismatch flag
mismatched=0

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
fstmp="${tmpdir}/bags-fs.txt.${mypid}"
invtmp="${tmpdir}/bags-inv.txt.${mypid}"

echo "Checking that the inventory matches the archive contents..."
# Get the directories in the archive
(cd "${archrootdir}" && find . -maxdepth ${depth} -mindepth ${depth} -type d | sed 's/\.\///') | sort > "${fstmp}"

# Copy the inventory sheet to the tmpdir
if ! cp "${archrootdir}/${invfile}" "${tmpdir}/${invfile}"
then
    echo "Cannot copy inventory file '${archrootdir}/${invfile}' to '${tmpdir}'.  Exiting." 1>&2
    exit 1
fi

# Get the list of paths in the inventory
if ! java -jar "${invmgr}" read --columns=Path "${tmpdir}/${invfile}" > "${invtmp}"
then
    echo "Cannot read inventory file '${invtmp}'.  Exiting." 1>&2
    exit 1
fi

sed -i 's/^Path: \///' "${invtmp}"
sort -o "${invtmp}"  "${invtmp}"

# Get bags only in filesystems
fsonly=`comm -23 "${fstmp}" "${invtmp}"`
if [ "X${fsonly}" != "X" ]
then
    echo "WARNING:  Bags found in the archive that are not in the inventory:"
    echo "${fsonly}"
    mismatched=1
fi

invonly=`comm -13 "${fstmp}" "${invtmp}"`
if [ "X${invonly}" != "X" ]
then
    echo "WARNING:  Bags found in the inventory that are not in the archive:"
    echo "${invonly}"
    mismatched=1
fi

if [ ${mismatched} -eq 0 ]
then
    echo "Inventory and archive are in sync."
fi

# Verify the bags
java -jar "${bagmgr}" verify --with-profile "${bagpath}"

rm -f "${fstmp}" "${invtmp}"
exit $?
