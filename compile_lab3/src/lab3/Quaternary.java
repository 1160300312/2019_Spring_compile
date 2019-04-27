package lab3;

public class Quaternary {
	int num;
	String action;
	String value1;
	String value2;
	String des;
	int truelist;
	int falselist;
	
	public Quaternary(String action, String value1, String value2, String des){
		this.action = action;
		this.value1 = value1;
		this.value2 = value2;
		this.des = des;
	}
	
	public Quaternary(String action, String des){
		this.action = action;
		this.des = des;
	}
	
	public Quaternary(String action, String value1, String des){
		this.action = action;
		this.value1 = value1;
		this.des = des;
	}
	
	@Override
	public String toString(){
		String result = "";
		result += num + " (" + action + ",";
		if(this.value1!=null){
			result += value1 + ",";
		} else{
			result += "_,";
		}
		if(this.value2!=null){
			result += value2 + ",";
		} else{
			result += "_,";
		}
		result += des + ")";
		return result;
	}
}
