
Week 1: Milestone 1

//Conversion of XML to JSON

Please make sure you add the json jar file to the parent directory of the source code.
--Steps to run smallFile1.xml

•	Task 1: Convert XML to JSON file
Open file “XMLtoJSON_M1.java”
Run
File: XMLtoJSON_M1
Argument: “../smallFile1.xml”
output will save in output1.json

•	Task 2: Get a sub object in JSON file with hard-coded key-path
Open file “GetSubObjectsJSON.java”
Change the JSONPointer according to the XML file that we need to process in line 30 of .java file
String jsonPointerString = “/catalog/book/0”;
Run 
file: GetSubObjectsJSON
Argument: “../smallFile1.xml”
output will save in output2.json

•	Task 3: Find a sub object in JSON file with command line key-path
Open file “FindSubObjectInJSON.java”
File needs two arguments to run, xml file and the desired key-path (change according to the xml file)
Run
File: FindSubObjectInJSON
Arguments: “../smallFile1.xml” “/catalog/book/0”
output will save in output3.json

•	Task 4: Add prefix “swe262_” to all keys in JSON file
Open file “AddPrefixJSONKeys.java”
Run:
File: AddPrefixJSONKeys
Arguments: “../smallFile1.xml”
output will save in output4.json

•	Task 5: Replace a sub object in JSON file with another object
Open file “ReplaceSubObjectJSON.java”
Change the json pointer string and the new object details in the file (line 63 and 77)
String keypath = "/catalog/book/0";

myObject.put("author", "Hemant").put("id", "bk110").put("title", "How to be a successfull women entrepreneur").put("price", 10);

Run
File: ReplaceSubObjectJSON
Arguments: “../smallFile1.xml”
output will save in output5.json


---How to run medium fie - "mediumFile1.xml"
change arguments to ---> /table/T/o
annd follow the above steps mention for "smallFile1.xml" above.

Files processed:
I have process a few of small file (size: kbs), medium files (size: 5MB to 90MB) and large files (size: 133MB) and very large file (size: 716 MB - 2.7GB). 


Files Processed:
Used this link to download 10 files: http://aiweb.cs.washington.edu/research/projects/xmltk/xmldata/www/repository.html#auctions
I have alread added 4 file in the project and other 6 files that I have tested in from above link. file names from link below-
SigmodRecord.xml
nasa.xml
treebank.xml
dblp.xml
psd7003.xml

https://ftp.acc.umu.se/mirror/wikimedia.org/dumps/enwiki/20201220/ very large from this link.
enwiki-20201220-stub-meta-history23

Observations:
Small and some medium size files are processing well, but some large and very large (above 500MB) file are giving java heap out memory error. My current heap size is 1GB and not increasing belond.




