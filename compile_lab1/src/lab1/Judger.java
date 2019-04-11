package lab1;

public class Judger {
	public final String[] Key_Word = {"if","else","for","do","while","return",
			"int","float","char","double","boolean","void","true","false","include","string"};
	public final String[] Operator = {"+", "-", "*", "/", "<", "<=", ">", ">=", "=", "==",
		    "!=",  "^", ",", "\"", "\'", "#", "&","&&", "|", "||", "%", "~", "<<", ">>",  
		    "\\", ".", "\\?", ":", "!"};
	public final String[] Boundary = {";", "(", ")", "[", "]", "{", "}"};
	
	//�ж��Ƿ�Ϊ�ؼ���
	public boolean isKeyWord(String s){
		for(int i=0;i<Key_Word.length;i++){
			if(s.equals(Key_Word[i])){
				return true;
			}
		}
		return false;
	}
	
	//�ж��Ƿ�Ϊ������
	public boolean isOperator(String s){
		for(int i=0;i<Operator.length;i++){
			if(s.equals(Operator[i])){
				return true;
			}
		}
		return false;
	}
	
	//�ж��Ƿ�Ϊ���
	public boolean isBoundary(String s){
		for(int i=0;i<Boundary.length;i++){
			if(s.equals(Boundary[i])){
				return true;
			}
		}
		return false;
	}
	
	//�ж��Ƿ�Ϊ����
	public boolean isDigit(char c){
		return 	c>='0' && c<='9';
	}
	
	//�ж��Ƿ�Ϊ�ַ�
	public boolean isChar(char c){
		return (c>='a' && c<='z') || (c>='A' && c<='Z');
	}
}
