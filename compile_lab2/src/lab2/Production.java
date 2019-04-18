package lab2;

import java.util.List;

public class Production {
	String left;
	List<String> right;
	
	@Override
	public String toString(){
		String s = "";
		s += left + "->";
		for(int i=0;i<right.size();i++){
			s += right.get(i);
		}
		return s;
	}
}
