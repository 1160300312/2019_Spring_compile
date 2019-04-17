package lab2;

import java.util.List;

public class Item {
	String left;
	List<String> before_point;
	List<String> after_point;
	List<String> search_character;
	
	@Override
	public String toString(){
		String s = "";
		s = s + "[" + left + "->";
		for(int i=0;i<before_point.size();i++){
			s += before_point.get(i);
		}
		s += ".";
		for(int j=0;j<after_point.size();j++){
			s += after_point.get(j);
		}
		s += ",";
		for(int k=0;k<search_character.size()-1;k++){
			s += search_character.get(k) + "/";
		}
		s += search_character.get(search_character.size()-1);
		return s;
	}
	
}
