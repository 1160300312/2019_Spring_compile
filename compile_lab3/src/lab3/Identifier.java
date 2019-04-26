package lab3;

import java.util.HashMap;
import java.util.Map.Entry;

public class Identifier {
	String name;
	String statuteName;
	HashMap<String, String> attributes;
	
	public Identifier(String name, String statuteName){
		this.attributes = new HashMap<String,String>();
		this.statuteName = statuteName;
		this.name = name;
	}
	
	public Identifier(String statuteName){
		this.attributes = new HashMap<String,String>();
		this.statuteName = statuteName;
	}
	
	@Override
	public String toString(){
		String result = "";
		result += statuteName;
		for (Entry<String, String> entry : attributes.entrySet()) { 
			result += entry.getKey() + " " + entry.getValue() + "\n";  
		}
		return result;
	}
}
