JSONTopicMap
============

Full TMRM (ISO-13250-5) TopicMap for ElasticSearch

Status: *alpha*<br/>
Latest edit: 20140226<br/>
## Background ##
For many SolrSherlock and other projects, a simple, scalable topic map platform is needed. In particular, SolrSherlock's text harvesting platform needs one to function in isolation of other topic maps.

At this time, the topic map now handles *AIRs* (IAddressableInformationResource objects), which are topic nodes with the specific purpose of carrying text information such as blog posts (subject and body) or structured conversation nodes.

Each topic node now contains information on its heritage, its *transitive closure* making it easy to answer any *isA* question about that node without a recursive database query.

##Notes##
The /lib directory includes only a dependencies.txt file. The jars required for this project are all found [[https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib](https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib)]([https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib](https://github.com/SolrSherlock/SolrSherlock/tree/master/masterLib) "In the masterlib directory")


This is a really early release; lots to do, but tests show it is possible to build a topic map and use the INodeQuery object to navigate. Many more tests will be added.

## Update History ##
20140226 Upgraded from TopicQuestsCoreAPI to OpenSherlockCoreAPI, which moved a number of definitions around in the classpath, but added new definitions for greater topic map representational convenience.

20140219 Fixed missing StatsTab initialization: added a run.bat file and an installation manual.

20140218 No code changes: added a UML diagram of the classes.

20140209 This project has been through rapid evolution while being used in a text-reading project.

20131129 First GitHub commit

## ToDo ##
Lots<br/>
Mavenize the project<br/>
Create a full unit test suite

## License ##
Apache 2


