package lab1;

import java.util.List;

public class Judger {
	public final String[] Key_Word = {"if","else","for","do","while","return",
			"int","float","char","double","boolean","void","true","false","include","string"};
	public final String[] Operator = {"+", "-", "*", "/", "<", "<=", ">", ">=", "==",
		    "!=","&&", "||", "%", "!","++","--"};
	public final String[] OperatorName = {"ADD","SUB","MUL","DIV","LESS","LOE","GREATER","GOE","EQUAL",
			"NE","AND","OR","MOD","NOT","INC","DEC"};
	public final String[] Boundary = {"=", ";", "(", ")", "[", "]", "{", "}"};
	public final String[] BoundaryName = {"AS", "SEMI", "SLP", "SRP", "MLP", "MRP","LP", "RP"};
	
	//ÅÐ¶ÏÊÇ·ñÎª¹Ø¼ü×Ö
	public boolean isKeyWord(String s){
		for(int i=0;i<Key_Word.length;i++){
			if(s.equals(Key_Word[i])){
				return true;
			}
		}
		return false;
	}
	
	//ÅÐ¶ÏÊÇ·ñÎª²Ù×÷·û
	public boolean isOperator(String s){
		for(int i=0;i<Operator.length;i++){
			if(s.equals(Operator[i])){
				return true;
			}
		}
		return false;
	}
	
	//ÅÐ¶ÏÊÇ·ñÎª½ç·û
	public boolean isBoundary(char s){
		for(int i=0;i<Boundary.length;i++){
			if(Boundary[i].charAt(0)==s) {
				return true;
			}
		}
		return false;
	}
	
	//ÅÐ¶ÏÊÇ·ñÎªÊý×Ö
	public boolean isDigit(char c){
		return 	c>='0' && c<='9';
	}
	
	//ÅÐ¶ÏÊÇ·ñÎª×Ö·û
	public boolean isChar(char c){
		return (c>='a' && c<='z') || (c>='A' && c<='Z') || c == '_';
	}
	
	public boolean judgeForComment(char c){
		return !(c=='*');
	}
	
	public String getBoundaryName(char c){
		for(int i=0;i<Boundary.length;i++){
			if(Boundary[i].charAt(0)==c) {
				return BoundaryName[i];
			}
		}
		return null;
	}
	
	public void operationJudge(StateSnap snap, List<Token> result){
		String operation2 = null;
		if((snap.current+1)<snap.input.length()){
			operation2 = snap.input.charAt(snap.current) + "" + snap.input.charAt(snap.current+1);
		} else{
			operation2 = snap.input.charAt(snap.current) + "";
		}
		String operation1 = snap.input.charAt(snap.current) + "";
		for(int i=0;i<Operator.length;i++){
			if(Operator[i].equals(operation2)){
				result.add(new Token(operation2,OperatorName[i],"_"));
				snap.current += 2;
				snap.forward += 2;
				return;
			}
		}
		for(int i=0;i<Operator.length;i++){
			if(Operator[i].equals(operation1)){
				result.add(new Token(operation1,OperatorName[i],"_"));
				snap.current ++;
				snap.forward ++;
				return;
			}
		}
	}
}
