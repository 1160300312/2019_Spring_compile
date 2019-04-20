package lab2;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Test {
	public static void main(String args[]){
		/*List<Integer> list = new ArrayList<Integer>();
		list.add(1);
		list.add(2);
		list.remove(0);
		System.out.println(list);
		String a = "12345";
		System.out.println(a.substring(1, a.length()));*/
		//System.out.println("B A".split(" ")[0]);
		Set<Integer> a = new HashSet<Integer>();
		Set<Integer> b = new HashSet<Integer>();
		a.add(1);
		b.add(1);
		System.out.println(a.equals(b));
	}
}
