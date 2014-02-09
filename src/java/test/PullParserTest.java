/**
 * 
 */
package test;
import java.util.*;
import java.io.*;

import net.vvakame.util.jsonpullparser.JsonPullParser;
import net.vvakame.util.jsonpullparser.JsonSlice;
import net.vvakame.util.jsonpullparser.JsonPullParser.State;

/**
 * @author park
 *
 */
public class PullParserTest {
	private final String fileName = "KBDump1384826978070.json";

	/**
	 * 
	 */
	public PullParserTest() {
		try {
			File f = new File(fileName);
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader fr = new InputStreamReader(fis);
			JsonPullParser p = JsonPullParser.newParser(fr);
			//List<JsonSlice> ls = p.getSlices();
			int counter = 0;
			int maxcount = 100;
			while (counter++ < maxcount) {
				JsonPullParser.State it = null;
				try { it = p.getEventType(); } catch (Exception e) {
					System.out.println(e.getMessage());
				}
				if (it != null) {
					System.out.println(it);
					switch(it) {
					case KEY: 
						System.out.println("Key "+p.getValueString()); break;
					case VALUE_STRING:
						System.out.println("Value "+p.getValueString()); break;
					case ORIGIN:
						System.out.println("origin"); break; // don't see these
					case VALUE_LONG:
						System.out.println("long"); break;
					case VALUE_DOUBLE:
						System.out.println("double"); break;
					case VALUE_BOOLEAN:
						System.out.println("boolean"); break;
					case VALUE_NULL:
						System.out.println("null"); break;
					case START_HASH: // starting a { JSONObject block
						System.out.println("starthash"); break;
					case END_HASH: // ending a } JSONObject block
						System.out.println("endhash"); break;
					case START_ARRAY: // starting a List [
						System.out.println("startarray"); break;
					case END_ARRAY: // ending a List ]
						System.out.println("endarray"); break;
					default:
						System.out.println("foo ");
					}
				}
				it = null;
				
			}
			
		} catch (Exception e) { 
			//unexpected token. token=?
			e.printStackTrace();
		}
	}

}
