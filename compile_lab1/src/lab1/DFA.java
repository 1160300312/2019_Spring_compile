package lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DFA {

	public StateSnap digitDFA(StateSnap snap, List<Token> output){
		String result = "";
		Judger judger = new Judger();
		int state = 0;
		char[][] dfa = new char[7][7];
		
		return null;
	}
	
	public void identifierDFA(StateSnap snap, List<Token> output){
		String result = "";
		Judger judger = new Judger();
		int state = 0;
		char[][] dfa = new char[2][2];
		readFromFile("DFA//identifier.txt", dfa);
		char ch = snap.input.charAt(snap.forward);
		while(judger.isChar(ch) || judger.isDigit(ch)){
//			System.out.println(state);
			List<Character> trans = this.getStateTrans(dfa, state);
			if(trans.contains('l')){
				result += ch;
				state = this.getStateTrans(dfa, state).indexOf('l');
			}
			if(trans.contains('a')){
				result += ch;
				state = this.getStateTrans(dfa, state).indexOf('a');
			}
			snap.forward++;
			if(snap.forward<snap.input.length()){
				ch = snap.input.charAt(snap.forward);
			} else{
				break;
			}
		}
		if(state!=2){
			System.out.println("error");
			//TODO ´íÎó´¦Àí
		}
		else{
			if(judger.isKeyWord(result)){
				output.add(new Token(result,result.toUpperCase(),"_"));
				snap.current = snap.forward;
			} else{
				output.add(new Token(result,"IDN",result));
			}
		}
	}

	
	public void readFromFile(String filepath,char[][] dfa){
		try {
			FileReader file = new FileReader(filepath);
			BufferedReader br = new BufferedReader(file);
			String str;
			int count = 0;
			while((str = br.readLine())!=null){
				String[] words = str.split(" ");
				for(int i=0;i<words.length;i++){
					dfa[count][i] = words[i].charAt(0);
				}
				count++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<Character> getStateTrans(char[][] dfa, int state){
		List<Character> result = new ArrayList<Character>();
		for(int i=0;i<dfa[state].length;i++){
			result.add(dfa[state][i]);
		}
		return result;
	}
	
	public static void main(String args[]){
		DFA dfa = new DFA();
		List<Token> result = new ArrayList<Token>();
		StateSnap a = new StateSnap();
		a.input = "2";
		a.current = 0;
		a.forward = 0;
		dfa.identifierDFA(a, result);
		System.out.println(result);
	}
	
}
