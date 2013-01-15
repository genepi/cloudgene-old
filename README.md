Cloudgene
=========

Cloudgene is an open-source platform to improve the usability of MapReduce programs by providing a graphical user interface for the execution, the import and export of data and the reproducibility of workflows on in-house and rented clusters, i.e. in the cloud. The aim of Cloudgene is to build a standardized graphical execution environment for currently available and future MapReduce programs, which can all be integrated by using its plug-in interface.


Two Cloudgene modes are available:

**Public Mode**
***
Cloudgene's public mode supports scientists by launching a cluster in the cloud (currently Amazon EC2) and set ups a ready-to-use environment for a specific use case. It installs the MapReduce framework and necessary variables (instance type, amount of instances, firewall rules) as defined in the configuration file and launches the same web interface as in point 2 (see below). All complicated set-ups through the command line are elimnated. 

**Private Mode**
***
Cloudgene's private mode improves the usability of currently available MapReduce programs by providing a web interface for their execution and monitoring. Furthermore, a standarized way to import/export data (from S3, HTTP, FTP, file upload) is provided. Cloudgene-MapRed supports the execution of Hadoop jar files (written in Java), the Hadoop Streaming mode (written in any other programming language) and allows a concatenation of programs by defining steps in the manifest file and a reproducibility of analysis.


Cloudgene can be started and run as follows:
http://cloudgene.uibk.ac.at/docs/install_cloudgene
