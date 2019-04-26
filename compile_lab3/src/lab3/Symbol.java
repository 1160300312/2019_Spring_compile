package lab3;

public class Symbol {
	String type;
	String name;
	int offset;
	
	@Override
	public String toString(){
		return "(" + name + "," + type + "," + offset + ")";
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		} else{
			if(o instanceof Symbol){
				Symbol sm = (Symbol)o;
				return sm.name == this.name;
			}
		}
		return false;
	}
	
	@Override 
	public int hashCode(){
		return this.name.hashCode();
	}
	
}
