package lab2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ItemSet {
	Set<Item> itemSet;
	Map<String, Integer> go;
	int num;
	
	public ItemSet(){
		this.go = new HashMap<String, Integer>();
	}
	
	@Override
	public String toString(){
		Iterator<Item> it = this.itemSet.iterator();
		String result = "";
		result += "I" + num + ":";
		result += "\n";
		while(it.hasNext()){
			result += it.next().toString();
			result += "\n";
		}
		for (Map.Entry<String, Integer> entry : go.entrySet()) { 
			result += entry.getKey() + " " + entry.getValue() + "\n";  
		}
		return result;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		} else{
			if(o instanceof ItemSet){
				ItemSet os = (ItemSet)o;
				return this.itemSet.equals(os.itemSet);
			}
		}
		return false;
	}
}
