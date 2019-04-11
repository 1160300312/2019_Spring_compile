package lab1;

public class Token {
	String value;
	String property;
	String comment;
	
	public Token(String value, String property, String comment){
		this.value = value;
		this.property = property;
		this.comment = comment;
	}
	
	@Override
	public String toString(){
		return value + "<" + property + "," + comment + ">";
	} 
}
