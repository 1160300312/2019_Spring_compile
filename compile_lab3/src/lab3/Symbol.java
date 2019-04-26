package lab3;


public class Symbol {
	String type;
	String name;
	int offset;
	
	@Override
	public String toString(){
		return "(" + name + "," + type + "," + offset + ")";
	}
}
