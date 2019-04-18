package lab2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
	static int count = 0;
	List<Production> production_list = new ArrayList<Production>();
	Set<String> terminal_set = new HashSet<String>();
	Set<String> non_terminal_set = new HashSet<String>();
	List<ItemSet> item_set_list = new ArrayList<ItemSet>();
	AnalysisTable table;
	int stateNum;
	
	
	//////////////
	public Set<Item> getClosure(Item item){
		Set<Item> result = new HashSet<Item>();
		result.add(item);
		int size = 1;
		while(true){
			result = this.countClosure(result);
			if(size == result.size()){
				break;
			} else{
				size = result.size();
			}
		}
		return result;
	}
	
	public Set<Item> countClosure(Set<Item> item){
		List<Item> it_list = new ArrayList<Item>(item);
		
		for(int j=0;j<it_list.size();j++){
			if(it_list.get(j).after_point.size()==0){
				continue;
			}
			if(this.isNonTerminal(it_list.get(j).after_point.get(0))){
				List<Production> plist = this.getProduction(it_list.get(j).after_point.get(0));
				List<String> search = this.getStringFirst(it_list.get(j).after_point, it_list.get(j).search_character);
				for(int i=0;i<plist.size();i++){
					Item it = new Item();
					it.left = it_list.get(j).after_point.get(0);
					it.before_point = new ArrayList<String>();
					it.after_point = plist.get(i).right;
					it.search_character = search;
					item.add(it);
				}
			}
		}
		return item;
	}

	
	public List<String> getFirst(String s){
		Set<String> result = new HashSet<String>();
		if(this.isTerminal(s)){
			result.add(s);
			return new ArrayList<String>(result);
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
//			System.out.println(lines.get(i));
			Production p = new Production();
			p.num = i - production_index;
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
				for(int j=0;j<first.size();j++){
					if(!first.get(j).equals("EPSILON")){
						result.add(first.get(j));
					}
				}
				i++;
			} else{
				result.addAll(first);
				flag = 1;
				break;
			}
		}
		if(flag == 0){
			result.addAll(input2);
		}
		return result;
	}
	
	public void getItemSet(){
		Item start = new Item();
		start.left = "S1";
		start.before_point = new ArrayList<String>();
		start.after_point = new ArrayList<String>();
		start.search_character = new ArrayList<String>();
		start.after_point.add("S");
		start.search_character.add("#");
		Set<Item> start_set = this.getClosure(start);
		ItemSet start_itemset = new ItemSet();
		start_itemset.num = 0;
		start_itemset.itemSet = start_set;
		this.item_set_list.add(start_itemset);
//		System.out.println(start_itemset);
		
		int count = 1;
		while(true){
			int loop_flag = 0;
			ItemSet set = new ItemSet();
			set.num = count;
			for(int i=0;i<count;i++){
				ItemSet current = this.item_set_list.get(i);
				List<Item> itlist = new ArrayList<Item>(current.itemSet);
				for(int j=0;j<itlist.size();j++){
					if(itlist.get(j).flag == 0){
						
						int repeat_flag = 0;
						Item current_item = itlist.get(j);
						current_item.flag = 1;
						if(current_item.after_point.size() == 0){
							continue;
						} else{
							Item new_item = new Item();
							String search_character = current_item.after_point.get(0);
							if(search_character.equals("EPSILON")){
								continue;
							}
							new_item.left = current_item.left;
							List<String> before = new ArrayList<String>();
							List<String> after = new ArrayList<String>();
							for(int k=0;k<current_item.before_point.size();k++){
								before.add(current_item.before_point.get(k));
							}
							before.add(current_item.after_point.get(0));
							for(int k=1;k<current_item.after_point.size();k++){
								after.add(current_item.after_point.get(k));
							}
							new_item.before_point = before;
							new_item.after_point = after;
							new_item.search_character = current_item.search_character;
							int repeat_num = 0;
							for(int m=0;m<count;m++){
								List<Item> itlist1 = new ArrayList<Item>(this.item_set_list.get(m).itemSet);
								for(int n=0;n<itlist1.size();n++){
									if(new_item.equals(itlist1.get(n))){
										repeat_flag = 1;
										repeat_num = this.item_set_list.get(m).num;
									}
								}
							}
							if(repeat_flag == 1){
								current.go.put(search_character, repeat_num);
								continue;
							} else{
								set.itemSet = this.getClosure(new_item);
								set.num = count;
								this.item_set_list.add(set);
								current.go.put(search_character, count);
								count ++;
								loop_flag = 1;
								break;
							}
						}
					}
				}
				if(loop_flag == 1){
					break;
				}
			}
			if(loop_flag == 0){
				break;
			}
			this.stateNum = count;
		}
	}
	
	public void fillTable(){
		table = new AnalysisTable(new ArrayList<String>(this.terminal_set),new ArrayList<String>(this.non_terminal_set),stateNum);
		for(int i=0;i<this.item_set_list.size();i++){
			ItemSet current = this.item_set_list.get(i);
			System.out.println(current);
			Iterator<Item> itr1 = current.itemSet.iterator();
			while(itr1.hasNext()){
				
				Item item = itr1.next();
				System.out.println(item);
				if(item.after_point.size() == 0 && (!item.left.equals("S1"))){
					for(int j=0;j<item.search_character.size();i++){
						table.action_table[current.num][table.terminals.indexOf(item.search_character.get(j))] = item.left + "->" + item.before_point;
					}
				}
				if(item.after_point.size() == 0 && item.left.equals("S1")){
					for(int j=0;j<item.search_character.size();i++){
						table.action_table[current.num][table.terminals.indexOf(item.search_character.get(j))] = "acc";
					}
				}
				if(item.after_point.size()>0 && this.isTerminal(item.after_point.get(0)) && current.go.containsKey(item.after_point.get(0))){
					table.action_table[current.num][table.terminals.indexOf(item.after_point.get(0))] = "S" + current.go.get(item.after_point.get(0));
				}
			}
			for (Map.Entry<String, Integer> entry : current.go.entrySet()) { 
				
				if(this.isNonTerminal(entry.getKey())){
					table.goto_table[current.num][table.nonterminals.indexOf(entry.getKey())] = entry.getValue() + "";
				}
			}
			
		}
	}
	
	public static void main(String args[]){
		Parser parser = new Parser();
		parser.readFromFile("input.txt");
		
		parser.getItemSet();
		//parser.fillTable();
		//System.out.println(parser.table);
		for(int i=0;i<parser.item_set_list.size();i++){
			System.out.print(parser.item_set_list.get(i));
		}
		
		/*Item it = new Item();
		it.left = "A";
		it.before_point = new ArrayList<String>();
		it.after_point = new ArrayList<String>();
		it.before_point.add("B");
		it.after_point.add("A");
		it.search_character = new ArrayList<String>();
		it.search_character.add("#");
		Set<Item> it_set = new HashSet<Item>();
		it_set.add(it);
		System.out.println(parser.getClosure(it));
		
		/*List<String> l1 = new ArrayList<String>();
		l1.add("L");
		l1.add("=");
		l1.add("R");
		List<String> l2 = new ArrayList<String>();
		l2.add("#");
		System.out.println(parser.getStringFirst(l1, l2));
		System.out.println(parser.getFirst("="));*/
		
		/*Item it1 = new Item();
		it1.left = "S";
		it1.before_point = new ArrayList<String>();
		it1.before_point.add("");
		it1.after_point = new ArrayList<String>();
		it1.after_point.add("CC");
		it1.search_character = new ArrayList<String>();
		it1.search_character.add("#");
		Item it2 = new Item();
		it2.left = "S";
		it2.before_point = new ArrayList<String>();
		it2.before_point.add("");
		it2.after_point = new ArrayList<String>();
		it2.after_point.add("CC");
		it2.search_character = new ArrayList<String>();
		it2.search_character.add("#");
		System.out.println(it1.equals(it2));
		Set<Item> test = new HashSet<Item>();
		test.add(it1);
		test.add(it2);
		System.out.println(test);*/
		
	}
}
