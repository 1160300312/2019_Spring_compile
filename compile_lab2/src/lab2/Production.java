package lab2;

import java.util.List;

public class Production {
	String left;
	List<String> right;
	int num;
	
	@Override
	public String toString(){
		String s = "";
		s += left + "->";
		for(int i=0;i<right.size()-1;i++){
			s += right.get(i) + " ";
		}
		s += right.get(right.size()-1);
		return s;
	}
}
