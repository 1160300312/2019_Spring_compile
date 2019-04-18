package lab2;

import java.util.List;

public class Item {
	String left;
	List<String> before_point;
	List<String> after_point;
	List<String> search_character;
	int flag;
	
	public Item(){
		this.flag = 0;
	}
	
	@Override
	public String toString(){
		String s = "";
		s = s + "[" + left + "->";
		for(int i=0;i<before_point.size();i++){
			s += before_point.get(i);
		}
		s += ".";
		for(int j=0;j<after_point.size();j++){
			s += after_point.get(j);
		}
		s += ",";
		for(int k=0;k<search_character.size()-1;k++){
			s += search_character.get(k) + "/";
		}
		s += search_character.get(search_character.size()-1);
		s += "]";
		return s + flag;
	}
	
	@Override
	public boolean equals(Object o){
		if(o == null){
			return false;
		} else{
			if(o instanceof Item){
				Item it = (Item)o;
				boolean re = true;
				re = re && (this.left.equals(it.left));
				if(this.before_point.size()!=it.before_point.size()){
					return false;
				}
				if(this.after_point.size()!=it.after_point.size()){
					return false;
				}
				if(this.search_character.size()!=it.search_character.size()){
					return false;
				}
				for(int i=0;i<Math.min(this.before_point.size(),it.before_point.size());i++){
					re = re && this.before_point.get(i).equals(it.before_point.get(i));
				}
				for(int i=0;i<Math.min(this.after_point.size(),it.after_point.size());i++){
					re = re && this.after_point.get(i).equals(it.after_point.get(i));
				}
				for(int i=0;i<Math.min(this.search_character.size(),it.search_character.size());i++){
					re = re && this.search_character.get(i).equals(it.search_character.get(i));
				}
				return re;
			}
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		int sum = 0;
		sum += this.left.hashCode();
		for(int i=0;i<this.before_point.size();i++){
			sum += this.before_point.get(i).hashCode();
		}
		for(int i=0;i<this.after_point.size();i++){
			sum += this.after_point.get(i).hashCode()*2;
		}
		for(int i=0;i<this.search_character.size();i++){
			sum += this.search_character.get(i).hashCode()*3;
		}
		return sum;
	}
	
}
