/**
 * 
 */
package test;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.topicquests.common.api.ITopicQuestsOntology;

/**
 * @author park
 *
 */
public class QueryBuilderTest {
	/**
	 * 
	 */
	public QueryBuilderTest() {
/*		BoolQueryBuilder qba = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, "Foo");
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY, "Bar");
		qba.must(qb1);
		qba.must(qb2);		
		System.out.println(qba.toString());

{
  "bool" : {
    "must" : [ {
      "term" : {
        "instanceOf" : "Foo"
      }
    }, {
      "term" : {
        "TupleObjectPropertyType" : "Bar"
      }
    } ]
  }
}		
		
*/
/**
		BoolQueryBuilder qba = QueryBuilders.boolQuery();
		QueryBuilder qb1 = QueryBuilders.termQuery(ITopicQuestsOntology.INSTANCE_OF_PROPERTY_TYPE, "Foo");
		QueryBuilder qb2 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_OBJECT_PROPERTY, "bar");
		QueryBuilder qb3 = QueryBuilders.termQuery(ITopicQuestsOntology.TUPLE_SUBJECT_PROPERTY, "bah");
		qba.must(qb1);
		qba.should(qb2);
		qba.should(qb3);
		System.out.println(qba.toString());
{
  "bool" : {
    "must" : {
      "term" : {
        "instanceOf" : "Foo"
      }
    },
    "should" : [ {
      "term" : {
        "TupleObjectPropertyType" : "bar"
      }
    }, {
      "term" : {
        "TupleSubjectPropertyType" : "bah"
      }
    } ]
  }
}		
*/
	}

}
