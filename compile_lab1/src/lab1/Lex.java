package lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lex {
	public static void main(String args[]){
		String filepath = "input.txt";
		FileReader file;
		String input = "";
		try {
			file = new FileReader(filepath);
			BufferedReader br = new BufferedReader(file);
			String str;
			while((str = br.readLine())!=null){
				input += str;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		StateSnap buffer = new StateSnap();
		Judger judger = new Judger();
		DFA dfa = new DFA();
//		System.out.println(input);
		buffer.input = input;
		buffer.current = 0;
		buffer.forward = 0;
		List<Token> result = new ArrayList<Token>();
		while(buffer.current!=buffer.input.length()){
			char cur = buffer.input.charAt(buffer.current);
			if(cur==' '||cur=='\t'){
				buffer.current++;
				buffer.forward++;
				continue;
			}
			if(judger.isChar(cur)){
				dfa.identifierDFA(buffer, result);
			} 
			if(judger.isDigit(cur)){
				dfa.digitDFA(buffer, result);
			}
			if(judger.isBoundary(cur)){
				result.add(new Token(""+buffer.input.charAt(buffer.current),
					judger.getBoundaryName(cur), "_"));
				buffer.current++;
				buffer.forward++;
			}
			if(buffer.current<buffer.input.length()-1){
//				System.out.println(cur);
				if(cur == '/' && buffer.input.charAt(buffer.current+1) == '*'){
					dfa.commentDFA(buffer, result);
					continue;
				}
			}
			if(buffer.current!=buffer.input.length()){
				judger.operationJudge(buffer, result);
			}
			
		}
		for(int i=0;i<result.size();i++){
			System.out.println(result.get(i));
		}
	}

}
