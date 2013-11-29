JSONTopicMap
============

Full TMRM (ISO-13250-5) TopicMap for ElasticSearch

Status: *very-pre-alpha*<br/>
Latest edit: 20131129<br/>
## Background ##
For many SolrSherlock and other projects, a simple, scalable topic map platform is needed. In particular, SolrSherlock's text harvesting platform needs one to function in isolation of other topic maps.

##Notes##
The /lib directory includes only a dependencies.txt file. The jars required for this project are all found [[https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib](https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib)]([https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib](https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib) "In the masterlib directory")

The file ExportTest1385517929160.xml is the result of the ExportTest. This shows the full *upper typology* created when the topic map is first booted.

This is a really early release; lots to do, but tests show it is possible to build a topic map and use the INodeQuery object to navigate. Many more tests will be added.

## Update History ##
20131129 First GitHub commit

## ToDo ##
Lots<br/>
Mavenize the project<br/>
Create a full unit test suite

## License ##
Apache 2


