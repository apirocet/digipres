Apirocet Lightweight Digital Preservation Toolkit
=================================================

The scripts and utilities in this toolkit form an end-to-end 
lightweight digital preservation workflow that can be implemented
with relatively little effort in small organizations with few
resources.  The tools are all free and open-source, and use common
open source utilities with proven history.

The core of the system are BagIT 1.0 Bags, which are created, 
stored in remote locations, then periodically verified.

BagIT Specification: https://tools.ietf.org/html/rfc8493

A basic workflow consists of the following tasks:

1. Put files your want to preserve into a folder with some
   structure and accompanying metadata.
2. Create preservation-ready derivative copies of selected files 
   within the folder (`createDerivatives.sh`).
3. Validate all the files within the folder, to make sure
   the formats are correct, and the files are not corrupted
   (`validateFiles.sh`).
4. Create a BagIT bag folder from your prepared
   folder of stuff (`createTPFBag.sh`) in your primary
   archive location.
5. Copy the Bag folder to other storage platforms for
   redundancy and offsite safekeeping (`copyToRemotes.sh`).
6. Periodically verify the Bag folder in your primary archive
   location, other storage locations (`verifyTPFBags.sh`).

## Dependencies

The tools are designed to run on a Unix host (inclusing Mac)
that has the **Bash shell** and Java installed. All the tools
are Bash shell scripts or Java applications.

### System Dependencies

**Java 11+** (OpenJDK or Oracle Java) is required to run JHove, the 
bag packaging and inventory management applications.

Java: https://openjdk.java.net/install/

### Managing Archives as Local Files

All the scripts and utilities listed below are designed to work 
on archive contents as local files.  For example, Google Drive 
folders can be mounted on the archiving processing host as local 
directories, using Google Sync or a similar tool.  These tools 
were developed and tested on files mounted by **Rclone**.

Rclone:  https://www.rclone.org/

### Derivative generation

**LibreOffice 6.3+** is used to create PDF-A versions of Microsoft Word doc 
and docx files, and to create CSV exports of Microsoft Excel .xls and .xlsx 
spreadsheets.  

LibreOffice may first need to be configured to export to the correct
PDF-A version.  In the GUI, got to *File* -> *Export as PDF*, then
under the *General* tab in the PDF Options window, select
*Archive (PDF/A, ISO 19005)*, and *PDF/A-1b*.

The script **pdf2pdfa.sh** is used to convert PDFs to PDF-A;  it 
uses **Ghostscript** under the hood.

The script `createDerivatives.sh` makes use of these applications.

LibreOffice:  https://www.libreoffice.org/download/download/

Ghostscript:  https://www.ghostscript.com/

pdf2pdfa:  https://github.com/apirocet/digipres/tree/master/pdf2pdfa

### File Validation

The script `validateFiles.sh` validates the files in a bag to make sure they are 
not corrupted and in the correct format.  Only MP3, WAV, and PDF files are 
checked.  The following third-party tools are used to validate the files:

**Checkmate MPCK** MP3 checker:  https://checkmate.gissen.nl/

**JHove (Version 1.x)** (for WAV and normal PDF file validation):  https://jhove.openpreservation.org/

**VeraPDF** (PDF-A validator):  https://verapdf.org/

### Bag Creation

The script `createTPFBag.sh` creates a BagIt Bag of the episode directory 
contents in the primary archive storage location.  It is a wrapper script 
for the underlying `bagmanager` Java application.  It can be customized to
fit an organization's needs.

Properties files can be created to serve as a template for the bag information 
file.  The file `poetryfoundation-bagit.properties` is one such file.

### File Sync

Once the bag is created in the primary archive, it can be replicated to 
secondary storage locations.  The script `copyToRemotes.sh` copies a bag, 
or the entire archive, from the primary location to all secondary storage 
locations.  It uses **Rclone** to push the copies.  

Each remote storage location is configured in Rclone;  the list of Rclone 
remote locations is set at the top of the script.

### Periodic Archive Verification

Objects stored in the primary archive (and selected secondary storage hosts) 
are periodically checked for corruption.  The script `verifyTPFBags.sh` is 
designed to be run periodically (such as in cron) to check:

1. That the inventory contents and archive contents match;  and 
2. That the bags  in the archive are valid, and have not changed.  

The script uses the `bagmanager` Java application to verify the bags and the 
`inventory-manager` Java application to help verify the inventory.

The bagmanager application reads a profile publicly available on the web
to verify the bag information files.  The file `bagit-profile-poetryfoundation-v1.0.json`
is one such file.

## Contents

The toolkit contains the following scripts, applications, and utilities:

### `createDerivatives.sh`
  
Script to create derivatives for Microsoft Word and Excel files, create 
PDF-A versions of PDF files in a directory tree prior to archiving them.
  
This script is a wrapper for the following conversion tools: 

* Microsoft Word and Excel to PDF-A, csv:  LibreOffice
* PDF to PDF-A: pdf2pdfa.sh

```
  Usage:  createDerivatives.sh directory
            directory:  path to top-level directory tree to examine
                        and find files to convert
```

### `pdf2pdfa/pdf2pdfa.sh`

Script to convert a PDF file to a valid PDF/A.

The files alongside the script `pdf2pdfa/pdf2pdfa.sh` must be in
the same directory as the script itself.

This script is a wrapper for Ghostscript, and optionally VeraPDF:
If verapdf is defined, the resulting PDF/A file will be validated.

```
Usage:  pdf2pdfa input-file output-file [1|2]
           input-file:  source PDF file to convert to PDF/A
           output-file:  PDF/A file to write out
           1 or 2:  PDF/A conformance level.  Default is 2.
```

### `validateFiles.sh`

Script to recursively validate mp3, wav, and pdf files in a directory
tree prior to archiving them.

This script is a wrapper for the following validation tools:

* MP3:  Checkmate MP3 checker
* WAV:  JHove
* PDF:  VeraPDF

```
 Usage:  validateFiles.sh directory
            directory:  path to top-level directory tree to check
```

###  `createTPFBag.sh`

Script to create an archivable Bag structure from
object contents in the staging directory.

This script is a wrapper for the `bagmanager` Java application.

```
Usage: createTPFBag.sh [-r] <staging directory> <archive root directory>
                          -r:  Replace bag directory if it exists
           staging directory:  path to top-level staging directory
                               tree to copy to a bag.
      archive root directory:  base of the archive directory tree,
                               where bag will be written
```

### `copyToRemotes.sh`

Script to copy source directory to one or more remote storage locations.

This script is a wrapper for Rclone.

*Note*:  This script only copies new files and updates existing files.
No files are deleted.

Rclone remote locations are defined in the `Configuration` section at the 
top of the script.

```
Usage: copyToRemotes.sh [source directory]
           source directory:  directory to copy, relative to archive
                              root directory. If not set, the entire
                              archive will be copied.
```

### `verifyTPFBags.sh`

Script to verify BagIt bags and inventory.

The verification scripts checks that inventory contents and files in 
the archive match up;  and that all the directories and files in a bag 
match the contents of the bag manifest;  and that none of the files have
changes since the bag was created/updated.

This script is a wrapper for the `bagmanager` and `inventory-manager` 
Java applications.

```
Usage: verifyTPFBags.sh directory
      directory:  base of the archive directory tree
```

### `bagmanager`

Java application to create and verify bags.  See the help documentation
for the application to run the application on its own as a Java jar.

```
$ java -jar bagmanager.jar -h
Usage: bagmanager [-hV] [-l=<logfile>] ( write | verify )
Command line BagIt bag manager
  -l, --logfile=<logfile>   path to log file (default is bagmanager.log in
                              current directory)
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
Commands:
  write   write/update the bag
  verify  verify the bag
```

### `inventory-manager`

Java application to read the inventory spreadsheet.  See the help documentation
for the application to run the application on its own as a Java jar.

```
$ java -jar inventory-manager.jar -h
Usage: bagmanager [-hV] [-l=<logfile>] ( read )
Command line inventory spreadsheet manager
  -l, --logfile=<logfile>   path to log file (default is inventorymanager.log
                              in current directory)
  -h, --help                Show this help message and exit.
  -V, --version             Print version information and exit.
Commands:
  read  read columns from the inventory spreadsheet
```

### Miscellaneous Files

* `poetryfoundation-bagit.properties`:  sample BagiT template properties file,
  used by `createTPFBag.sh`.  The location of this file can be set in the script.
* This README

## Who do I talk to?

GitHub: @sprater
