package lab2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Parser {
	static int count = 0;
	List<Production> production_list = new ArrayList<Production>();
	Set<String> terminal_set = new HashSet<String>();
	Set<String> non_terminal_set = new HashSet<String>();
	List<Set<Item>> item_set_list = new ArrayList<Set<Item>>();
	
	
	//////////////
	public Set<Item> getClosure(Item item){
		if(item.after_point.size() == 0){
			return null;
		}
		Set<Item> result = new HashSet<Item>();
		if(this.isNonTerminal(item.after_point.get(0))){
			List<Production> plist = this.getProduction(item.after_point.get(0));
			List<String> search = this.getStringFirst(item.after_point, item.search_character);
			for(int i=0;i<plist.size();i++){
				Item it = new Item();
				it.left = item.after_point.get(0);
				it.before_point = new ArrayList<String>();
				it.after_point = plist.get(i).right;
				it.search_character = search;
				result.add(it);
			}
		}
		return null;
	}
	
	public List<String> getListFirst(List<String> word){
		List<String> result = new ArrayList<String>();
		return null;
	}
	
	public List<String> getFirst(String s){
		Set<String> result = new HashSet<String>();
		if(this.isTerminal(s)){
			result.add(s);
			return new ArrayList<String>();
		}
		for(int i=0;i<this.production_list.size();i++){
			if(this.production_list.get(i).left.equals(s)){
				Production p = this.production_list.get(i);
				if(this.terminal_set.contains(p.right.get(0))){
					result.add(p.right.get(0));
					continue;
				}
				if(this.non_terminal_set.contains(p.right.get(0)) && (!this.isNull(p.right.get(0)))){
					if(p.right.get(0).equals(s)){
						continue;
					}
					result.addAll(getFirst(p.right.get(0)));
					continue;
				}
				int j = 0;
				while(j<p.right.size() && this.isNull(p.right.get(j))){
					if(this.isNonTerminal(p.right.get(j))){
//						System.out.println(p.right.get(j));
						if(p.right.get(j+1).equals(s)){
							break;
						}
						List<String> result_right = getFirst(p.right.get(j));
						for(int k=0;k<result_right.size();k++){
							if(!result_right.get(k).equals("EPSILON")){
								result.add(result_right.get(k));
							}
						}
						j++;
					}
				}
				if(j==p.right.size()){
					result.add("EPSILON");
				} else{
					result.addAll(getFirst(p.right.get(j)));
					result.add("EPSILON");
				}
			}
		}
		return new ArrayList<String>(result);
	}
	
	public boolean isTerminal(String s){
		return this.terminal_set.contains(s);
	}
	
	public boolean isNonTerminal(String s){
		return this.non_terminal_set.contains(s);
	}
	
	public boolean isNull(String s){
		for(int i=0;i<this.production_list.size();i++){
			if(this.production_list.get(i).left.equals(s)){
				//TODO 如何表示空
				if(this.production_list.get(i).right.contains("EPSILON")){
					return true;
				}
			}
		}
		return false;
	}
	
	public void readFromFile(String path){
		FileReader file;
		List<String> lines = new ArrayList<String>();
		try {
			file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);
			String str;
			while((str=br.readLine())!=null){
				lines.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		int production_index = 0;
		for(int i=0;i<lines.size();i++){
//			System.out.println(lines.get(i));
			if(lines.get(i).equals("Terminals:")){
				continue;
			}
			if(lines.get(i).equals("Productions:")){
				production_index = i+1;
				break;
			}
			this.terminal_set.add(lines.get(i));
		}
		for(int i=production_index;i<lines.size();i++){
			this.non_terminal_set.add(lines.get(i).split("->")[0]);
		}
		for(int i=production_index;i<lines.size();i++){
			System.out.println(lines.get(i));
			Production p = new Production();
			String[] words = lines.get(i).split("->");
			p.left = words[0];
			p.right = new ArrayList<String>();
			for(int j=0;j<words[1].length();){
				for(int k=j+1;k<=words[1].length();k++){
					String word = words[1].substring(j, k);
					if(this.terminal_set.contains(word)){
						int flag = 0;
						if(k+1<words[1].length()){
							if(this.terminal_set.contains(words[1].substring(j, k+1))){
								word = words[1].substring(j, k+1);
								flag = 1;
							}
						}
						p.right.add(word);
						if(flag == 1){
							j = k+1;
							break;
						} else{
							j=k;
							break;
						}
					}
					if(this.non_terminal_set.contains(word)){
						p.right.add(word);
						j=k;
						break;
					}
				}
			}
			this.production_list.add(p);
		}
	}
	
	public List<Production> getProduction(String head){
		List<Production> plist = new ArrayList<Production>();
		for(int i=0;i<this.production_list.size();i++){
			if(this.production_list.get(i).left.equals(head)){
				plist.add(this.production_list.get(i));
			}
		}
		return plist;
	}
	
	public List<String> getStringFirst(List<String> input1, List<String> input2){
		int i = 1;
		List<String> result = new ArrayList<String>();
		int flag = 0;
		while(i<input1.size()){
			List<String> first = this.getFirst(input1.get(i));
			if(first.contains("EPSILON")){
				result.addAll(first);
				i++;
			} else{
				flag = 1;
				break;
			}
		}
		if(flag == 0){
			result.addAll(input2);
		}
		return result;
	}
	
	public static void main(String args[]){
		Parser parser = new Parser();
		parser.readFromFile("input.txt");
		System.out.println(parser.production_list);
		System.out.println(parser.getFirst("C"));
	
	}
}
