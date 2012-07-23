Cloudgene
=========

Cloudgene is an open-source platform to improve the usability of MapReduce programs by providing a graphical user interface for the execution, the import and export of data and the reproducibility of workflows on in-house and rented clusters, i.e. in the cloud. The aim of Cloudgene is to build a standardized graphical execution environment for currently available and future MapReduce programs, which can all be integrated by using its plug-in interface.

**Cloudgene-Cluster**
***
Cloudgene-Cluster supports scientists by launching a cluster in the cloud (currently AWS-EC2) and set ups a ready-to-use environment for a specific use case. It installs the MapReduce framework and necessary variables (instance type, amount of instances, firewall rules) as defined in the configuration file and launches Cloudgene-MapRed on it (see below). All complicated set-ups through the command line are elimnated.

**Cloudgene-MapRed**
***
Cloudgene-MapRed improves the usability of currently available MapReduce programs by providing a web interface for their execution and monitoring. Furthermore, a standarized way to import/export data (from S3, HTTP, FTP, file upload) is provided. Cloudgene-MapRed supports the execution of Hadoop jar files (written in Java), the Hadoop Streaming mode (written in any other programming language) and allows a concatenation of programs by defining steps in the manifest file and a reproducibility of analysis.


How to start Cloudgene-Cluster and Cloudgene-MapRed
=========
1. Clone both repositories https://github.com/genepi/cloudgene.git **and** https://github.com/genepi/cloudgene-samples.git. The file <code> conf/settings.yaml </code>defines the relative bath to each other
2. 
  * Start **Cloudgene-Cluster**, use the username 'cloudgene' and pwd 'cloud' to login at *http://localhost:8085*
  * Start **Cloudgene-MapRed**, create a user by adding the program argument <code> --add-user user pwd --admin </code>and login at *http://localhost:8082*