#!/bin/bash

#-----------------------------------------------------------------------#
# createDerivatives.sh                                                  #
#                                                                       #
# Script to create derivatives for Microsoft Word and Excel files,      #
# create PDF-A versions of PDF files in a directory tree prior to       #
# archiving them.                                                       #
#                                                                       #
#  This script is a wrapper for the following conversion tools:         #
#      Microsoft Word and Excel to PDF-A, csv:  LibreOffice             #
#      PDF to PDF-A: pdf2pdfa.sh                                        #
#                                                                       #
# LibreOffice may first need to be configured to export to the correct  #
# PDF-A version.  In the GUI, got to "File" -> "Export as PDF", then    #
# under the "General" tab in the PDF Options window, select             #
# "Archive (PDF/A, ISO 19005)", and "PDF/A-1b".                         #
#                                                                       #
# Usage:  createDerivatives.sh directory                                #
#            directory:  path to top-level directory tree to examine    #
#                        convert                                        #
#                                                                       #
# February 2020                                                         #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Program directory
wkdir=`dirname $0`

# Formats to convert
declare -a formats=("doc" "docx" "xls" "xlsx" "pdf")

# Path to LibreOffice
lo='/opt/libreoffice6.3/program/soffice'

# Path to pdf2pdfa.sh
pdfa="${wkdir}/pdf2pdfa/pdf2pdfa.sh"

# Path to verapdf (VeraPDF PDF/A validator)
pdfchecker='/usr/local/verapdf/verapdf'
pdfokstr='isCompliant="true"'

# Default status is OK
final_status=0

# List of files with problems
declare -a errfiles=()

# Temp directory
tmpdir="/var/tmp/createDerivatives-$$"
mkdir -p "$tmpdir" || exit 1

# Counters
haveread=0
converted=0
skipped=0
failed=0

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage: $0 directory" 1>&2
    echo "          directory:  path to directory tree to examine and convert files" 1>&2
    exit 1
}

success_convert () {
    cfile="$1"
    sdir="$2"
    if [ ! -e "${tmpdir}/${cfile}" ]
    then
        echo "Conversion skipped."
        skipped=$((skipped+1))
        return
    fi

    if [ ! -e "${sdir}/${cfile}" ]
    then
        echo "Conversion successful."
        mv "${tmpdir}/${cfile}" "${sdir}"
        converted=$((converted+1))
    else
        echo "WARNING:  '${sdir}/${cfile}' already exists.  Skipping."
        rm "${tmpdir}/${cfile}"
        skipped=$((skipped+1))
    fi
}

fail_convert () {
    sfile="$1"
    dfile="$2"
    echo "WARNING:  cannot convert '${sfile}'"
    final_status=2
    errfiles=("${errfiles[@]}" "${sfile}")
    failed=$((failed+1))
    rm "${dfile}"
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Check that we have a valid directory
convertdir="$1"
if [ ! -d "${convertdir}" ]
then
    echo "No such directory '${convertdir}'" 1>&2
    usage
fi

# Run the conversions
for fmt in "${formats[@]}"
do
   echo "Converting .${fmt} files..."
   while read archfile
   do
       srcdir=`dirname "${archfile}"`
       base=`basename "${archfile}"`
       base="${base%.*}"
       echo "Converting ${archfile}..."
       case "$fmt" in
           doc*)
               if [ ! -e "${srcdir}/${base}.pdf" ]
               then
                   if "$lo" --headless --convert-to pdf --outdir "${tmpdir}" "${archfile}" > /dev/null
                   then
                       success_convert "${base}.pdf" "${srcdir}"
                   else
                       fail_convert "${archfile}" "${tmpdir}/${base}.pdf"
                   fi
               else
                   echo "File '${srcdir}/${base}.pdf' exists. Conversion skipped."
                   skipped=$((skipped+1))
               fi
               ;;
           xls*)
               if [ ! -e "${srcdir}/${base}.csv" ]
               then
                   if "$lo" --headless --convert-to csv --outdir "${tmpdir}" "${archfile}" > /dev/null
                   then
                       success_convert "${base}.csv" "${srcdir}"
                   else
                       fail_convert "${archfile}" "${tmpdir}/${base}.csv"
                   fi
               else
                   echo "File '${srcdir}/${base}.csv' exists. Conversion skipped."
                   skipped=$((skipped+1))
               fi
               ;;
            pdf)

               if [ ! -e "${srcdir}/${base}-PDFA.pdf" ]
               then
                   if "$pdfa" "${archfile}" "${tmpdir}/${base}-PDFA.pdf" > /dev/null
                   then
                       success_convert "${base}-PDFA.pdf" "${srcdir}"
                   else
                       fail_convert "${archfile}" "${tmpdir}/${base}-PDFA.pdf"
                   fi
               else
                   echo "File '${srcdir}/${base}-PDFA.pdf' exists. Conversion skipped."
                   skipped=$((skipped+1))
               fi
               ;;
              *)
               continue;
               ;;
       esac
       haveread=$((haveread+1))
   done < <(find "${convertdir}" -iname "*.${fmt}")
done

ls "${tmpdir}"

# Output the summary
printf "Files marked for conversion: %5s\n" $haveread
printf "Files converted:             %5s\n" $converted
printf "Files skipped:               %5s\n" $skipped
printf "Files failed:                %5s\n" $failed
if [ ${final_status} -eq 2 ]
then
    echo "Conversion failed for some files:"
    printf "    %s\n" "${errfiles[@]}"
else
    echo "All derivatives are in place."
fi

rm -rf "${tmpdir}"
exit ${final_status}
