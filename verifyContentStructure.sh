#!/bin/bash

#-----------------------------------------------------------------------#
# verifyContentStructure.sh                                             #
#                                                                       #
# Script to verify that files and directories are structured correctly. #
# The script reads a structure definition file to determine the         #
# directory structure, and a metadata definition file to verify         #
# metadata file contents.                                               #
#                                                                       #
# Usage:  validateFiles.sh project directory                            #
#            project:    the project with definition files              #
#            directory:  path to top-level directory tree to check      #
#                                                                       #
# May 2020                                                              #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Directory with definition files
mydir=`dirname $0`

# Structure definition file suffix
dirstructfile='_structure_def.txt'

# Metadata definition file suffix
mdstructfile='_metadata_def.txt'

# Default status is OK
final_status=0

# Warnings
declare -a warnings=()
warnstatus=0

# Errors
declare -a errors=()
errstatus=0

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage: $0 project directory" 1>&2
    echo "          project:    project ID with definition files" 1>&2
    echo "          directory:  path to directory tree to check" 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have a valid project with definition files
prj="$1"
if [ "X${prj}" == "X" ]
then
    usage
fi
if [ ! -s "${mydir}/${prj}${dirstructfile}" ]
then
    echo "No directory structure definition file '${mydir}/${prj}${dirstructfile}' found." 1>&2
    usage
fi
if [ ! -s "${mydir}/${prj}${mdstructfile}" ]
then
    echo "No metadata definition file '${mydir}/${prj}${mdstructfile}' found." 1>&2
    usage
fi

dirdef="${mydir}/${prj}${dirstructfile}"
mddef="${mydir}/${prj}${mdstructile}"

# Check that we have a valid directory
ckdir="$2"
if [ "X${ckdir}" == "X" ]
then
    usage
fi
if [ ! -d "${ckdir}" ]
then
    echo "No such directory '$ckdir'" 1>&2
    usage
fi

# Run the directory structure check
while IFS= read line
do
    dir=`echo ${line} | cut -f1 -d'|'`
    contents_req=`echo ${line} | cut -f2 -d'|'`
    if [ ! -d "${ckdir}/${dir}" ]
    then
        errors=("${errors[@]}" "ERROR: directory check - directory '${ckdir}/${dir}' is missing")
        errstatus=1
        continue
    fi

    numfiles=$(find "${ckdir}/${dir}" -maxdepth 1 -type f| wc -l)
    if [ ${numfiles} -eq 0 ]
    then
        if [ "X$contents_req" == "Xyes" ]
        then
            errors=("${errors[@]}" "ERROR: directory check - directory '${ckdir}/${dir}' does not contain any files")
            errstatus=1
        else
            warnings=("${warnings[@]}" "WARNING: directory check - directory '${ckdir}/${dir}' does not contain any files")
            warnstatus=1
        fi
    fi
done < <(grep -v '^#' "${dirdef}")

# Run the metadata file check
if [ ! -s "${ckdir}/metadata.txt" ]
then
    errors=("${errors[@]}" "ERROR: metadata check - metadata file '${ckdir}/metadata.txt' is missing")
    errstatus=1
fi

# Output the summary
if [ ${errstatus} -gt 0 ]
then
    echo "Some errors were encountered:"
    printf "    %s\n" "${errors[@]}"
fi

if [ ${warnstatus} -gt 0 ]
then
    echo "Some warnings were encountered:"
    printf "    %s\n" "${warnings[@]}"
fi

if [ ${errstatus} -gt 0 -o ${warnstatus} -gt 0 ]
then
    exit 1
else
    echo "Directory structure and metadata file OK."
    exit 0
fi
