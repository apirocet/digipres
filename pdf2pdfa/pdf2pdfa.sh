#!/bin/bash

#-----------------------------------------------------------------------#
# pdf2pdfa.sh                                                           #
#                                                                       #
# Script to convert a PDF file to a valid PDF/A.                        #
#                                                                       #
#  This script is a wrapper for ghostscript, and optionally VeraPDF:    #
#  If verapdf is defined, the resulting PDF/A file will be validated.   #
#                                                                       #
# Usage:  pdf2pdfa input_file output_file [1|2]                         #
#            input_file:  source PDF file to convert to PDF/A           #
#            output_file:  PDF/A file to write out                      #
#            1 or 2:  PDF/A conformance level.  Default is 2.           #
#                                                                       #
# December 2019                                                         #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Path to gs (Ghostscript)
gs_exe='/bin/gs'

# Path to verapdf (VeraPDF PDF/A validator)
# Leave blank if not installed
pdfchecker='/usr/local/verapdf/verapdf'
pdfokstr='isCompliant="true"'

# Default PDF/A conformance level
pdfacl=2

#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage: $0 input_file output_file [1|2]" 1>&2
    echo "          input_file: source PDF file to convert to PDF/A" 1>&2
    echo "          output_file: PDF/A file to write out" 1>&2
    echo "          1 or 2:  PDF/A conformance level.  Default is 2." 1>&2
    exit 1
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Set our work directory
wkdir=`dirname $0`

# Check that we have a valid input file, output file parameter
infile="$1"
outfile="$2"
if [ ! -s "${infile}" ]
then
    echo "No such file '${infile}'" 1>&2
    usage
fi

if [ "X${outfile}" == "X" ]
then
    echo "Please provide the output filename for the PDF/A document" 1>&2
    usage
fi

# Set PDF/A conformance level
cl="$3"
if [ "X${cl}" == "X1" ]
then
    pdfacl=1
fi

# Get our absolute file paths
absinfile=`readlink -f "${infile}"`
absoutfile=`readlink -f "${outfile}"`
cd "${wkdir}"
abspath=`pwd`

# Convert
${gs_exe} -dPDFA=${pdfacl} \
          -dNOOUTERSAVE \
          -sProcessColorModel=DeviceRGB \
          -sColorConversionStrategy=RGB \
          -dPDFACompatibilityPolicy=1 \
          -sDEVICE=pdfwrite \
          -o "${absoutfile}" "${abspath}/PDFA_def.ps" \
          "${absinfile}"
final_status=$?

if [ ${final_status} -eq 0 -a "X${pdfchecker}" != "X" ]
then
    # Validate
    ${pdfchecker} "${absoutfile}" | grep "${pdfokstr}" > /dev/null
    final_status=$?

    if [ ${final_status} -eq 0 ]
    then
        echo "PDF/A file '${outfile}' successfully generated and validated."
    else
        echo "PDF/A file '${outfile}' was generated, but did not validate."
    fi
elif [ ${final_status} -eq 0 ]
then
    echo "PDF/A file '${outfile}' successfully generated."
else
    echo "Unable to generate PDF/A file '${outfile}'."
fi

exit ${final_status}
