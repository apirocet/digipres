#!/bin/bash

#-----------------------------------------------------------------------#
# generateMetadata.sh                                                   #
#                                                                       #
# Script to generate a metadata file for an archive object.             #
# The script is a wrapper for the mdgenerate.jar Java application,      #
# which reads an Excel spreadsheet updated by Poetry Foundation staff,  #
# reads the PCMS database, and writes out a metadata file.              #
#                                                                       #
# Usage:  generateMetadata.sh file sheet                                #
#            file:  Excel file containing skeletal object information   #
#            sheet: Sheet in the spreadsheet containing rows to process #
#                                                                       #
# July 2020                                                             #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

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
    echo "Usage: $0 file sheet" 1>&2
    echo "            file:  Excel file containing skeletal object information" 1>&2
    echo "            sheet: Sheet in the spreadsheet containing rows to process" 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have the right number of arguments
if [ "$#" -ne 2 ]
then
    usage
fi

excelfile="$1"
sheet="$2"

# Generate the metadata
if ! java -Dlogfile=mdgenerate-$$.log -jar mdgenerate.jar -l mdgenerate-$$.log "${excelfile}"  "${sheet}"
then
    errors=("${errors[@]}" "ERROR: metadata could not be generated.  See mdgenerate-$$.log for details.")
    errstatus=1
fi

if [ -s mdgenerate-$$.log ]
then
    if grep WARN mdgenerate-$$.log > /dev/null
    then
        warnings=("${warnings[@]}" "WARNING: metadata generator - metadata file(s) may have problems.  See mdgenerate-$$.log for details.")
        warnstatus=1
    fi
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
    echo "Metadata file(s) generated."
    exit 0
fi
