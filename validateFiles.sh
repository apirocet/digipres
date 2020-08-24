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
declare -a mp3checker=('/usr/local/bin/mpck -q')
declare -a mp3okstr=(': Ok')
declare -a mp3label=('MP3')

# Path to jhove (JHove WAV validator)
declare -a wavchecker=('/usr/local/jhove/jhove -m WAVE-hul')
declare -a wavokstr=('Status: Well-Formed and valid')
declare -a wavlabel=('WAV')

# Path to verapdf, JHove (VeraPDF PDF/A validator, Jhove PDF validator)
declare -a pdfchecker=('/usr/local/verapdf/verapdf' '/usr/local/jhove/jhove -m PDF-hul')
declare -a pdfokstr=('isCompliant="true"' 'Status: Well-Formed and valid')
declare -a pdflabel=('PDF/A' 'PDF')

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
    return $?
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
   while read archfile
   do
       
       fmtchecker="${fmt}checker"
       fmtokstr="${fmt}okstr"
       fmtlabel="${fmt}label"
       checkcount=$(($(eval "echo \${#${fmt}checker[@]}")-1))
       for i in $(eval "echo \${!${fmt}checker[@]}")
       do
           checker=${fmtchecker}[$i]
           okstr=${fmtokstr}[$i]
           label=${fmtlabel}[$i]
           check_file "${!checker}" "${!okstr}" "${archfile}"
           if [ $? -ne 0 ]
           then
               if [ $i -lt $checkcount ]
               then
                   echo "  WARNING: ${archfile} failed ${!label} validation.  Trying next validation." 
               else 
                   echo " ${archfile} ${!label}: FAIL"
                   errfiles=("${errfiles[@]}" "${archfile}")
                   final_status=2
               fi
           else
               echo "${archfile} ${!label}: OK"
               break
           fi
       done
   done < <(find "${ckdir}" -iname "*.${fmt}")
done

# Output the summary
if [ ${final_status} -gt 0 ]
then
    echo "WARNING: Some files failed to validate:"
    printf "WARNING:    %s\n" "${errfiles[@]}"
else
    echo "OK: All files are valid."
fi

exit ${final_status}
