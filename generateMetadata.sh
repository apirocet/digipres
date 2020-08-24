#!/bin/bash

#-----------------------------------------------------------------------#
# generateMetadata.sh                                                   #
#                                                                       #
# Script to generate a metadata file for an archive object.             #
# The script is a wrapper for the mdgenerate.jar Java application,      #
# which reads an Excel spreadsheet updated by Poetry Foundation staff,  #
# reads the PCMS database, and writes out a metadata file.              #
#                                                                       #
# Usage:  generateMetadata.sh file sheet [episode date]                 #
#            file:  Excel file containing skeletal object information   #
#            sheet: Sheet in the spreadsheet containing rows to process #
#            episode_date: Episode date to generate metadata for        #
#                          (YYYY-MM format) (optional, default will     #
#                          generate metadata for all episodes in sheet) #
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
    echo "Usage: $0 file sheet [episode date]" 1>&2
    echo "                    file: Excel file containing skeletal object information" 1>&2
    echo "                   sheet: Sheet in the spreadsheet containing rows to process" 1>&2
    echo "            episode_date: Episode date to generate metadata for (YYYY-MM format)" 1>&2
    echo "                          (optional, default will generate metadata for all episodes in sheet)" 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have the right number of arguments
if [ "$#" -lt 2 -o "$#" -gt 3 ]
then
    usage
fi

excelfile="$1"
sheet="$2"
if [ "X$3" != "X" ]
then
    episode_date="-d $3"
fi


# Generate the metadata
java -Dlogfile=mdgenerate-$$.log -jar mdgenerate.jar -l mdgenerate-$$.log ${episode_date} "${excelfile}"  "${sheet}"
if [ "$?" -eq 1 ]
then
    if [ -s mdgenerate-$$.log ]
    then
        errors=("${errors[@]}" "ERROR: metadata could not be generated.  See mdgenerate-$$.log for details.")
    else
        errors=("${errors[@]}" "ERROR: metadata could not be generated.")
    fi

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

if [ ${errstatus} -gt 0 ]
then
    exit 1
fi

if [ ${warnstatus} -gt 0 ]
then
    exit 2
fi

echo "OK: Metadata file(s) generated."
exit 0
