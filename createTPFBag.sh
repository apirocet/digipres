#!/bin/bash

#-----------------------------------------------------------------------#
# createTPFBag.sh                                                       #
#                                                                       #
# Script to create a Poetry Foundation archive Bag structure from       #
# object contents in the staging directory.                             #
#                                                                       #
#  This script is a wrapper for the bagmanager utility:                 #
#  https://github.com/apirocet/digipres/tree/master/bagmanager          #
#                                                                       #
# Usage:                                                                #
# createTPFBag.sh [-r] <staging directory> <archive root directory>     #
#                           -r:  Replace bag directory if it exists     #
#            staging directory:  path to top-level staging directory    #
#                                tree to copy to a bag.                 #
#       archive root directory:  base of the archive directory tree,    #
#                                where bag will be written              #
#                                                                       #
# January 2020                                                          #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Metadata file
mdfile="metadata.txt" 

# Replace bag contents if they exist?
replace="false"

## Archive bag path:  bags will be stored by id in a pairtree
## "##/##/#####......"

# Archive directory depth
depth=2

# Number of pairetree chars in directory name
ptsize=2

# Bagmanager jar path
bagmgr="bagmanager.jar"

# Checksum algorithm
ag="SHA512"

# Bag metadata file
bagmd="../apirocet.github.io/poetryfoundation/poetryfoundation-bagit.properties"

# Rclone staging root
rclone_stage_root='tpfstage:'

# Rclone archive root
rclone_archive_root='tpfarchive:'

# Temporary stage data location
tmpstage_root='/var/tmp/tmpstage'

# Temporary bag data loacation
tmpbag_root='/var/tmp/tmpbag'

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

set_bagpath () {
    local str="$1"
    local pos=0
    local path=""
    for ((i=0; i<${depth}; i++))
    do
       subdir="${str:${pos}:${ptsize}}"
       path="$path/${subdir}"
       pos=$((pos+${ptsize}))
    done
    echo "$path";
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

# Check that we have a valid staging directory
stgdir="$1"
tmpstagedir="${tmpstage_root}/${stgdir#*:}"

if ! rclone lsf "${stgdir}" > /dev/null
then
    echo "ERROR: No such directory '${stgdir}'" 1>&2
    usage
fi
if ! mkdir -p "${tmpstagedir}"
then
    echo "ERROR: Unable to create temporary staging directory '${tmpstagedir}'" 1>&2
    exit 1
fi

# Check that we have a valid archive root directory
archrootdir="$2"
if ! rclone lsf "${archrootdir}" > /dev/null
then
    echo "ERROR: No such archive root directory '$archrootdir'" 1>&2
    usage
fi
if ! mkdir -p "${tmpbag_root}"
then
    echo "ERROR: Unable to create temporary bag directory '${tmpbag_root}'" 1>&2
    exit 1
fi
rm -rf "${tmpbag_root}"/*


# Check that we have metadata file
if ! rclone lsf "${stgdir}/${mdfile}" > /dev/null
then
    echo "ERROR: No metadata file found under '${stgdir}'" 1>&2
    exit 1
fi

# Copy the staging dir tree to the local temp tree
echo "Copying source staging data to temporary work folder..."
if ! rclone -v sync "${stgdir}" "${tmpstagedir}"
then
    echo "ERROR: Unable to copy data under '${stgdir}' to '${tmpstagedir}'" 1>&2
    exit 1
fi
echo "Copy complete."

# Get the UUID
rawuuid=`grep 'archive_id' "${tmpstagedir}/${mdfile}" | awk '{ print $2 }' | sed 's/"//g'`
uuid="${rawuuid//[$'\t\r\n']}"
if [ "X${uuid}" == "X" ]
then
    echo "ERROR: Cannot determine object UUID from metadata" 1>&2
    exit 1
fi

# Create the Archive path
subdirs=$(set_bagpath ${uuid})
bagpath="${archrootdir}${subdirs}/${uuid}"
tmpbagpath="${tmpbag_root}${subdirs}/${uuid}"
echo "Archive path is ${subdirs}/${uuid}"
if ! mkdir -p "${tmpbag_root}${subdirs}"
then
    echo "ERROR: Cannot create archive directory '${tmpbag_root}${subdirs}'" 1>&2
    exit 1
fi

# Check if bagpath directory exists
if rclone lsf "${bagpath}" > /dev/null 2>&1 && [[ "X${replace}" != "Xtrue" ]]
then
    echo "ERROR: Archive Directory '${bagpath}' exists, will not overwrite" 1>&2
    exit 1
fi

# Create the bag
if ! java -jar "${bagmgr}" write ${rflag} --algorithm=${ag} \
     --metadata-file="${bagmd}" \
     --metadata-fields "External-Identifier=${uuid}" \
     --outdir="${tmpbagpath}" \
     "${tmpstagedir}"
then
    echo "ERROR:  Unable to create and validate bag at '${tmpbagpath}'."
    exit 1
fi

copymode='copy'
if [ "X${replace}" != "Xtrue" ]
then
    copymode='sync'
fi

# Copy the bag
echo "Copying created bag to archive..."
if ! rclone -v ${copymode} "${tmpbagpath}" "${bagpath}"
then
    echo "ERROR:  Unable to copy bag at '${tmpbagpath}' to archive location '${bagpath}'."
    exit 1
fi
echo "Copy complete."

echo "OK: Bag created and archived."

exit 0
