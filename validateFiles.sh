#!/bin/bash

#-----------------------------------------------------------------------#
# validateFiles.sh                                                      #
#                                                                       #
# Script to recursively validate mp3, wav, and pdf files in a directory #
# tree prior to archiving them.                                         #
#                                                                       #
#  This script is a wrapper for the following validation tools:         #
#      MP3:  Checkmate MP3 checker                                      #
#      WAV:  jhove                                                      #
#      PDF:  VeraPDF                                                    #
#                                                                       #
# Usage:  validateFiles.sh directory                                    #
#            directory:  path to top-level directory tree to check      #
#                                                                       #
# December 2019                                                         #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Formats to validate
declare -a formats=("mp3" "wav" "pdf")

# Path to mpck (Checkmate mp3 checker)
mp3checker='/usr/local/bin/mpck -q'
mp3okstr=': Ok'

# Path to jhove (JHove WAV validator)
wavchecker='/usr/local/jhove/jhove -m WAVE-hul '
wavokstr='Status: Well-Formed and valid'

# Path to verapdf (VeraPDF PDF/A validator)
pdfchecker='/usr/local/verapdf/verapdf'
pdfokstr='isCompliant="true"'

# Default status is OK
final_status=0

# List of files with problems
declare -a errfiles=()

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage: $0 directory" 1>&2
    echo "          directory:  path to directory tree to check" 1>&2
    exit 1
}

check_file () {
    local checker="$1"
    local okstr="$2"
    local file="$3"

    $checker "$file" | grep "$okstr" >/dev/null
    local status=$?

    if [ $status -eq 0 ]
    then
        echo "$file: OK"
    else
        echo "$file: FAIL"
        errfiles=("${errfiles[@]}" "$file")
        final_status=1
    fi
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have a valid directory
ckdir="$1"
if [ ! -d "${ckdir}" ]
then
    echo "No such directory '$ckdir'" 1>&2
    usage
fi

# Run the checks
for fmt in "${formats[@]}"
do
   echo "Checking ${fmt}s..."
   fmtchecker="${fmt}checker"
   fmtokstr="${fmt}okstr"
   while read archfile
   do
       check_file "${!fmtchecker}" "${!fmtokstr}" "${archfile}"
   done < <(find "${ckdir}" -iname "*.${fmt}")
done

# Output the summary
if [ ${final_status} -gt 0 ]
then
    echo "Some files failed to validate:"
    printf "    %s\n" "${errfiles[@]}"
else
    echo "All files are valid."
fi

exit ${final_status}
