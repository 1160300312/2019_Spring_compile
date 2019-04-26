package lab3;

import java.util.Stack;

public class Test {
	public static void main(String args[]){
		SemParser s = new SemParser();
		System.out.println(s.getDimension("int[5][6][7]"));
	}
}
