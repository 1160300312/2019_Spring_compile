package lab3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Identifier {
	String name;
	String statuteName;
	HashMap<String, String> attributes;
	List<Integer> codes;
	
	public Identifier(String name, String statuteName){
		this.attributes = new HashMap<String,String>();
		this.statuteName = statuteName;
		this.name = name;
		codes = new ArrayList<Integer>();
	}
	
	public Identifier(String statuteName){
		this.attributes = new HashMap<String,String>();
		this.statuteName = statuteName;
		codes = new ArrayList<Integer>();
	}
	
	@Override
	public String toString(){
		String result = "";
		result += statuteName;
		/*for (Entry<String, String> entry : attributes.entrySet()) { 
			result += entry.getKey() + " " + entry.getValue() + "\n";  
		}*/
		return result;
	}
}
