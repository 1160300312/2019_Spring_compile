package lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DFA {

	public void digitDFA(StateSnap snap, List<Token> output, int line, List<String> errors){
		String result = "";
		Judger judger = new Judger();
		int state = 0;
		char[][] dfa = new char[7][7];
		readFromFile("DFA//digit.txt", dfa);
		char ch = snap.input.charAt(snap.forward);
		while(true){
//			System.out.println(ch + " " + state);
			List<Character> trans = this.getStateTrans(dfa, state);
			if(judger.isDigit(ch) && trans.contains('d')){
				result += ch;
				state = trans.indexOf('d');
//				System.out.println(state);
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else if(ch == '.' && trans.contains('.')){
				result += ch;
				state = trans.indexOf('.');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else if(ch == 'e' && trans.contains('e')){
				result += ch;
				state = trans.indexOf('e');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else if((ch == '+' || ch == '-') && trans.contains('a')){
				result += ch;
				state = trans.indexOf('a');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else{
				break;
			}
		}
		if(state!=1 && state!=3 && state!=6){
			snap.current = snap.forward;
			errors.add("An error happened during identifying digit at"
					+ " line " + line);
//			System.out.println("error");
			//TODO 错误处理
		} else if (state == 1){
			output.add(new Token(result,"CONST_INT",result));
			snap.current = snap.forward;
		} else if (state == 3 || state == 6){
			output.add(new Token(result,"CONST_FLOAT",result));
			snap.current = snap.forward;
		}
	}
	
	public void commentDFA(StateSnap snap, List<Token> output, int line, List<String> errors){
		String result = "";
		Judger judger = new Judger();
		int state = 0;
		char[][] dfa = new char[5][5];
		readFromFile("DFA//comment.txt", dfa);
		char ch = snap.input.charAt(snap.forward);
		while(true){
			List<Character> trans = this.getStateTrans(dfa, state);
//			System.out.println(ch + " " + state);
			if(ch == '/' && trans.contains('/')){
				result += ch;
				state = trans.indexOf('/');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else if(ch == '*' && trans.contains('*')){
				result += ch;
				state = trans.indexOf('*');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else if(judger.judgeForComment(ch) && trans.contains('a')){
				result += ch;
				state = trans.indexOf('a');
				snap.forward++;
				if(snap.forward<snap.input.length()){
					ch = snap.input.charAt(snap.forward);
				} else{
					break;
				}
			} else{
				break;
			}
		}
		
		if(state != 4){
			snap.current = snap.forward;
			errors.add("An error happened during handling comments at line "+line);
//			System.out.println("error");
		} else{
			output.add(new Token(result,"COMMENT","_"));
			snap.current = snap.forward;
			
		}
	}
	
	
	public void identifierDFA(StateSnap snap, List<Token> output, int line, List<String> errors){
		String result = "";
		Judger judger = new Judger();
		int state = 0;
		char[][] dfa = new char[2][2];
		readFromFile("DFA//identifier.txt", dfa);
		char ch = snap.input.charAt(snap.forward);
		while(judger.isChar(ch) || judger.isDigit(ch)){
//			System.out.println(state);
			List<Character> trans = this.getStateTrans(dfa, state);
			int flag = 0;
			if(trans.contains('l')){
				result += ch;
				state = trans.indexOf('l');
				flag = 1;
			}
			if(trans.contains('a')){
				result += ch;
				state = trans.indexOf('a');
				flag = 1;
			}
			if(flag == 0){
				break;
			}
			snap.forward++;
			if(snap.forward<snap.input.length()){
				ch = snap.input.charAt(snap.forward);
			} else{
				break;
			}
		}
		if(state!=1){
			snap.current = snap.forward;
			errors.add("An error happened during identifying identification at"
					+ " line " + line);
//			System.out.println("error");
			//TODO 错误处理
		}
		else{
			if(judger.isKeyWord(result)){
				output.add(new Token(result,result.toUpperCase(),"_"));
				snap.current = snap.forward;
			} else if(result.equals("true") || result.equals("false")){
				output.add(new Token(result,result.toUpperCase(),"_"));
				
			}else{
				output.add(new Token(result,"IDN",result));
				snap.current = snap.forward;
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
	
	
	
}
