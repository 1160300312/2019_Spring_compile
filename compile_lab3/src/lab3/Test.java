package lab3;

import java.util.Stack;

public class Test {
	public static void main(String args[]){
		Stack<Integer> s = new Stack<Integer>();
		s.push(0);
		s.push(1);
		s.set(s.size()-1, s.peek() + 1);
		System.out.println(s.get(s.size()-1));
	}
}
