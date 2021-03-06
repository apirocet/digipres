#!/bin/bash

#-----------------------------------------------------------------------#
# archiver.sh                                                           #
#                                                                       #
# Wrapper script to archive Poetry Foundation audio resources.          #
# object contents in the staging directory.                             #
#                                                                       #
# See the Audio Archive Workflow document for more information on the   #
# archiving steps.                                                      #
#                                                                       #
# Usage:                                                                #
# archiver.sh [-r] <staging directory> <archive root directory>         #
#                           -r:  Replace bag in archive if it exists    #
#            staging directory:  path to top-level staging directory    #
#                                tree to copy to a bag.                 #
#       archive root directory:  base of the archive directory tree,    #
#                                where bag will be written              #
#                                                                       #
# August 20 20                                                          #
#-----------------------------------------------------------------------#

#-----------------------------------------------------------------------#
# Configuration                                                         #
#-----------------------------------------------------------------------#

# Work directory
wkdir=`dirname $0`


# Log file
logfile="archiver-`date '+%Y%m%d-%H%M'`.log"

# Stage root directory
stage_root='/archive-stage'

# Archive root directory
arch_root='/archive'

# Organization key
orgkey='poetryfoundation'

# Remote stage root
remote_stage_root='tpfstage:'

# Remote stage root
remote_archive_root='tpfarchive:'

# Bash colors
ok='\e[32m\e[1m'
warn='\e[33m\e[1m'
err='\e[31m\e[1m'
reset='\e[0m'

# Programs
declare -A programs=( ["poetrymagazine"]="Poetry Magazine" )
#-----------------------------------------------------------------------#
# Functions                                                             #
#-----------------------------------------------------------------------#

usage () {
    echo "Usage:  $0 [-r] -s <staging directory> -p <program key>" 1>&2
    echo "                       -r:  Replace bag in archive if it exists" 1>&2
    echo "        staging directory:  top-level staging directory to be archived, relative to staging drive root." 1>&2
    echo "              program key:  key for the program whose audio resources will be archived." 1>&2
    echo "                            Valid key values are: " 1>&2
    for key in "${!programs[@]}"
    do
        echo "                                       ${key}" 1>&2
    done
    exit 1
}

send_ok () {
    if [ -n "$1" ]
    then
        IN="$1"
    else
        read IN # This reads a string from stdin and stores it in a variable called IN
    fi

    echo -e "${ok}${IN}${reset}"
}
send_warn () {
    if [ -n "$1" ]
    then
        IN="$1"
    else
        read IN # This reads a string from stdin and stores it in a variable called IN
    fi

    echo -e "${warn}${IN}${reset}"
}
send_err () {
    if [ -n "$1" ]
    then
        IN="$1"
    else
        read IN # This reads a string from stdin and stores it in a variable called IN
    fi

    echo -e "${err}${IN}${reset}"
}

display_messages () {
    while read LINE # This reads a string from stdin and stores it in a variable called IN
    do
        if echo "$LINE" | grep 'OK:' > /dev/null
        then
            send_ok "$LINE"
        fi
        if echo "$LINE" | grep 'WARNING:' > /dev/null
        then
            send_warn "$LINE"
        fi
        if echo "$LINE" | grep 'ERROR:' > /dev/null
        then
            send_err "$LINE"
        fi
    done
}

#-----------------------------------------------------------------------#
# Main                                                                  #
#-----------------------------------------------------------------------#

# Get replace flag
while getopts "rhs:p:" options
do
    case "${options}" in
         s)
             stage="$OPTARG"
             ;;
         p)
             program="$OPTARG"
             ;;
         h)
             usage
             ;;
         r)
             rflag="-r"
             ;;
         ?)
             usage
             ;;
    esac
done
shift "$((OPTIND-1))"

if [ "X${stage}" == "X" ]
then
    send_err "No staging directory specified."
    usage
fi

if [ "X${program}" == "X" -o "X${programs[$program]}" == "X" ]
then
    send_err "Program key '${program}' is not a valid key."
    usage
fi

stagedir="${stage_root}/${programs[$program]}/${stage}"
if [ ! -d "${stagedir}" ]
then
    send_err "Staging directory '${stagedir}' does not exist."
    usage
fi

echo "Archiving contents of $stagedir" | tee -a ${logfile}
echo "Writing log to ${logfile}"


# Step 1:  Create derivatives
echo "Step 1: Create derivatives" | tee -a ${logfile}
$wkdir/createDerivatives.sh "${stagedir}" 2>&1 | sed 's/^/createDerivatives: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 ]
then
    send_err "ERROR: Unable to create derivatives."
    exit 1
fi
send_ok "Step 1: Derivatives created."

# Step 2:  Validate files
echo "Step 2: Validate files" | tee -a ${logfile}
$wkdir/validateFiles.sh "${stagedir}" 2>&1 | sed 's/^/validateFiles: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 ]
then
    send_err "ERROR: Unable to validate files."
    exit 1
fi
send_ok "Step 2: Files validated."

# Step 3:  Generate metadata
sheet="${stage%-*}"
echo "Step 3: Generate metadata" | tee -a ${logfile}
$wkdir/generateMetadata.sh "${stage_root}/${programs[$program]}/${programs[$program]} Metadata Collection.xlsx" "${sheet}" "${stage}" 2>&1 | sed 's/^/generateMetadata: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 -o ${PIPESTATUS[0]} -gt 2 ]
then
    send_err "ERROR: Unable to generate metadata."
    exit 1
fi
if [ -s "${stagedir}"/metadata.txt ]
then
    echo "Overwriting '${stagedir}/metadata.txt'"
    orig_archive_id=`grep '^archive_id:' "${stagedir}"/metadata.txt | awk '{ print $2 }'`
fi

sleep 3
if ! cp metadata-${stage}.yml "${stagedir}"/metadata.txt
then
    send_err "ERROR:  Unable to copy 'metadata-${stage}.yml' to '${stagedir}/metadata.txt'"
    exit 1
fi
if [ "X${orig_archive_id}" != "X" ]
then
    sleep 2
    sed -i "s/^archive_id: .*/archive_id: ${orig_archive_id}/" "${stagedir}"/metadata.txt
fi
rm -f metadata-${stage}.yml
send_ok "Step 3: Metadata generated."

# Step 4:  Verify archive structure and metadata content
echo "Step 4: Verify metadata content and archive structure" | tee -a ${logfile}
$wkdir/verifyContentStructure.sh "${orgkey}" "${stagedir}" 2>&1 | sed 's/^/verifyContentStructure: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 ]
then
    send_err "ERROR: Unable to verify metadata content and archive structure."
    exit 1
fi
send_ok "Step 4: Metadata content and archive structure verified."

# Step 5:  Create and archive bag
echo "Step 5: Create and archive bag" | tee -a ${logfile}
$wkdir/createTPFBag.sh ${rflag} "${remote_stage_root}${programs[$program]}/${stage}" "${remote_archive_root}" 2>&1 | sed 's/^/createTPFBag: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 ]
then
    send_err "ERROR: Unable to create and archive bag."
    exit 1
fi
send_ok "Step 5: Bag created and copied to archive."

# Step 6:  Copy to remotes
archpath=`grep 'Archive path is' "${logfile}" | sed 's/.* //'`
echo "Step 6: Copy archive to remotes" | tee -a ${logfile}
$wkdir/copyToRemotes.sh "${remote_archive_root}${archpath}" 2>&1 | sed 's/^/copyToRemotes: /' | tee -a ${logfile} | display_messages
if [ ${PIPESTATUS[0]} -eq 1 ]
then
    send_err "ERROR: Unable to copy archive '${remote_archive_root}${archpath}' to remote storage locations."
    exit 1
fi
send_ok "Step 6: Archive copied to remote storage."

send_ok "Archive process completed."

rm -f md*.log
exit 0
