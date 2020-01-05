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
if [ ! -d "${stgdir}" ]
then
    echo "No such staging directory '$stgdir'" 1>&2
    usage
fi

# Check that we have a valid archive root directory
archrootdir="$2"
if [ ! -d "${archrootdir}" ]
then
    echo "No such archive root directory '$archrootdir'" 1>&2
    usage
fi

# Check that we have metadata file
if [ ! -s "${stgdir}/${mdfile}" ]
then
    echo "No metadata file found under '${stgdir}'" 1>&2
    usage
fi

# Get the UUID
rawuuid=`grep 'archive_id' "${stgdir}/${mdfile}" | awk '{ print $2 }'`
uuid="${rawuuid//[$'\t\r\n']}"
if [ "X${uuid}" == "X" ]
then
    echo "Cannot determine object UUId from metadata" 1>&2
    exit 1
fi

# Create the Archive path
subdirs=$(set_bagpath ${uuid})
bagpath="${archrootdir}${subdirs}/${uuid}"
if ! mkdir -p "${archrootdir}${subdirs}"
then
    echo "Cannot create archive directory '${archrootdir}${subdirs}'" 1>&2
    exit 1
fi

# Check if bagpath directory exists
if [ -d "${bagpath}" -a "X${replace}" != "Xtrue" ]
then
    echo "Directory '${bagpath}' exists, will not overwrite" 1>&2
    exit 1
fi

# Create the bag
java -jar "${bagmgr}" write ${rflag} --algorithm=${ag} \
     --metadata-file="${bagmd}" \
     --metadata-fields "External-Identifier=${uuid}" \
     --outdir="${bagpath}" \
     "${stgdir}"

exit $?
