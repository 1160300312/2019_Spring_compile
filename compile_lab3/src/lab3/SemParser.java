package lab3;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import lab1.Lex;
import lab1.Token;
import lab2.AnalysisTable;
import lab2.Item;
import lab2.ItemSet;
import lab2.Parser;
import lab2.Production;

public class SemParser {
	static int count = 0;
	List<Production> production_list = new ArrayList<Production>();
	Set<String> terminal_set = new HashSet<String>();
	Set<String> non_terminal_set = new HashSet<String>();
	List<ItemSet> item_set_list = new ArrayList<ItemSet>();
	AnalysisTable table;
	int stateNum;
	List<Integer> line_index = new ArrayList<Integer>();
	List<String> error = new ArrayList<String>();
	List<String> output = new ArrayList<String>();
	Stack<SymbolTable> table_stack = new Stack<SymbolTable>();
	Stack<Integer> offset_stack = new Stack<Integer>();
	Map<SymbolTable, Integer> map_table = new HashMap<SymbolTable, Integer>();
	List<Quaternary> quaternary_list = new ArrayList<Quaternary>();
	List<String> proc_list = new ArrayList<String>();
	int temp = 0;
	int num = 0;	
	
	//////////////
	public Set<Item> getClosure(Set<Item> result){
		int size = result.size();
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
							if(!result_right.get(k).equals("ε")){
								result.add(result_right.get(k));
							}
						}
						j++;
					}
				}
				if(j==p.right.size()){
					result.add("ε");
				} else{
					result.addAll(getFirst(p.right.get(j)));
					result.add("ε");
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
				if(this.production_list.get(i).right.contains("ε")){
					return true;
				}
			}
		}
		return false;
	}
	
	public void readFromFile(String path){
		List<String> lines = new ArrayList<String>();
		try {
			FileInputStream fis = new FileInputStream(path);   
			InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
			BufferedReader br = new BufferedReader(isr);  
			String str;
			while((str=br.readLine())!=null){
				lines.add(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(int i=0;i<lines.size();i++){
			this.non_terminal_set.add(lines.get(i).split("->")[0]);
		}
		for(int i=0;i<lines.size();i++){
			
			
			/*String[] words = lines.get(i).split("->");
			p.left = words[0];
			p.right = new ArrayList<String>();
			String[] content = words[1].split(" ");
			for(int j=0;j<content.length;j++){
				p.right.add(content[j]);
			}
//			System.out.println(p);
			this.production_list.add(p);*/
			String left = lines.get(i).split("->")[0];
			String[] right = lines.get(i).split("->")[1].split("\\|");
			for(int j=0;j<right.length;j++){
				Production p = new Production();
				p.left = left;
				p.right = new ArrayList<String>();
				String[] content = right[j].split(" ");
				for(int k=0;k<content.length;k++){
					if(!this.isNonTerminal(content[k])){
						this.terminal_set.add(content[k]);
					}
					p.right.add(content[k]);
				}
				this.production_list.add(p);
			}
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
			if(first.contains("ε")){
				for(int j=0;j<first.size();j++){
					if(!first.get(j).equals("ε")){
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
	
	public ItemSet goTo(ItemSet i, String x){
		ItemSet j = new ItemSet();
		Set<Item> iset = new HashSet<Item>();
		List<Item> itlist = new ArrayList<Item>(i.itemSet);
		for(int k=0;k<itlist.size();k++){
			if(itlist.get(k).after_point.size() == 0){
				continue;
			} else{
				if(itlist.get(k).after_point.get(0).equals(x)){
					Item it = new Item();
					it.before_point = new ArrayList<String>();
					it.after_point = new ArrayList<String>();
					it.left = itlist.get(k).left;
					for(int m=0;m<itlist.get(k).before_point.size();m++){
						it.before_point.add(itlist.get(k).before_point.get(m));
					}
					it.before_point.add(itlist.get(k).after_point.get(0));
					for(int n=1;n<itlist.get(k).after_point.size();n++){
						it.after_point.add(itlist.get(k).after_point.get(n));
					}
					it.search_character = itlist.get(k).search_character;
					iset.add(it);
				}
			}
		}
		j.itemSet = this.getClosure(iset);
		return j;
	}
	
	public void getItemSet(){
		Item start = new Item();
		start.left = "S1";
		start.before_point = new ArrayList<String>();
		start.after_point = new ArrayList<String>();
		start.search_character = new ArrayList<String>();
		start.after_point.add("P");
		start.search_character.add("#");
		Set<Item> start1 = new HashSet<Item>();
		start1.add(start);
		Set<Item> start_set = this.getClosure(start1);
		ItemSet start_itemset = new ItemSet();
		start_itemset.num = 0;
		start_itemset.itemSet = start_set;
		this.item_set_list.add(start_itemset);
		int count = 1;
		while(true){
			int size = this.item_set_list.size();
			for(int i=0;i<this.item_set_list.size();i++){
				List<String> not = new ArrayList<String>(this.non_terminal_set);
				for(int j=0;j<not.size();j++){
					if(this.goTo(this.item_set_list.get(i), not.get(j)).itemSet.size() > 0 && !this.item_set_list.contains(this.goTo(this.item_set_list.get(i), not.get(j)))){
						ItemSet p = new ItemSet();
						p = this.goTo(this.item_set_list.get(i), not.get(j));
						p.num = count;
						this.item_set_list.get(i).go.put(not.get(j), count);
						this.item_set_list.add(p);
						count++;
					}
					if(this.item_set_list.contains(this.goTo(this.item_set_list.get(i), not.get(j)))){
						int index = 0;
						for(int a=0;a<this.item_set_list.size();a++){
							if(this.item_set_list.get(a).equals(this.goTo(this.item_set_list.get(i), not.get(j)))){
								index = a;
							}
						}
						this.item_set_list.get(i).go.put(not.get(j), index);
					}
				}
				List<String> t = new ArrayList<String>(this.terminal_set);
				for(int j=0;j<t.size();j++){
					if(!t.get(j).equals("ε")){
						if(this.goTo(this.item_set_list.get(i), t.get(j)).itemSet.size() > 0 && !this.item_set_list.contains(this.goTo(this.item_set_list.get(i), t.get(j)))){
							ItemSet p = new ItemSet();
							p = this.goTo(this.item_set_list.get(i), t.get(j));
							p.num = count;
							this.item_set_list.get(i).go.put(t.get(j), count);
							this.item_set_list.add(p);
							count++;
						}
						if(this.item_set_list.contains(this.goTo(this.item_set_list.get(i), t.get(j)))){
							int index = 0;
							for(int a=0;a<this.item_set_list.size();a++){
								if(this.item_set_list.get(a).equals(this.goTo(this.item_set_list.get(i), t.get(j)))){
									index = a;
								}
							}
							this.item_set_list.get(i).go.put(t.get(j), index);
						}
					}
				}
			}
			if(this.item_set_list.size() == size){
				break;
			}
		}
		this.stateNum = count - 1;
	}
	
	/*public void getItemSet(){
		Item start = new Item();
		start.left = "S1";
		start.before_point = new ArrayList<String>();
		start.after_point = new ArrayList<String>();
		start.search_character = new ArrayList<String>();
		start.after_point.add("P");
		start.search_character.add("#");
		Set<Item> start1 = new HashSet<Item>();
		start1.add(start);
		Set<Item> start_set = this.getClosure(start1);
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
							if(search_character.equals("ε")){
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
								Set<Item> is = new HashSet<Item>();
								is.add(new_item);
								if(current.go.containsKey(search_character)){
									this.item_set_list.get(current.go.get(search_character)).itemSet.addAll(this.getClosure(is));
									break;
								}else{
									set.itemSet = this.getClosure(is);
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
				}
				if(loop_flag == 1){
					break;
				}
			}
			if(loop_flag == 0){
				break;
			}
		}
		this.stateNum = count-1;
	}*/
	
	public void parser(String path){
		/*FileReader file;
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
		List<String> input = new ArrayList<String>();
		for(int i=0;i<lines.get(0).length();i++){
			input.add(lines.get(0).charAt(i) + "");
		}*/
		List<Identifier> input = new ArrayList<Identifier>();
		Lex lex = new Lex();
		List<Token> token_list = lex.lex(path);
		for(int i=0;i<token_list.size();i++){
			if(token_list.get(i).property.equals("IDN")){
				input.add(new Identifier(token_list.get(i).value,"id"));
				this.line_index.add(token_list.get(i).line);
			} else if(token_list.get(i).property.equals("CONST_INT")||token_list.get(i).property.equals("CONST_FLOAT")){
				input.add(new Identifier(token_list.get(i).value,"digit"));
				this.line_index.add(token_list.get(i).line);
			}else if(token_list.get(i).property.equals("COMMENT")){
				continue;
			}else{
				input.add(new Identifier(token_list.get(i).value,token_list.get(i).value));
				this.line_index.add(token_list.get(i).line);
			}
		}
		input.add(new Identifier("#"));
		this.line_index.add(this.line_index.get(this.line_index.size()-1));
		Stack<Integer> state_stack = new Stack<Integer>();
		Stack<Identifier> parser_stack = new Stack<Identifier>();
		state_stack.push(0);
		SymbolTable st = new SymbolTable();
		st.name = "main";
		table_stack.push(st);
		offset_stack.push(0);
		String tt = null;
		String ww = null;
		while(true){
			int break_flag = 0;
			String state = this.table.action_table[state_stack.peek()][this.table.terminals.indexOf(input.get(0).statuteName)];
			if(state == null){
				this.error.add("Error at line " + this.line_index.get(0));
				Identifier top = parser_stack.peek();
				state_stack.pop();
				while(true){
					while(!this.isNonTerminal(parser_stack.peek().statuteName)){
						state_stack.pop();
						parser_stack.pop();
						if(state_stack.peek() == 0){
							break_flag = 1;
							break;
						}
						
					}
					if(break_flag == 1){
						break;
					}
					if(this.table.goto_table[state_stack.peek()][this.table.nonterminals.indexOf(parser_stack.peek().statuteName)] == null){
						state_stack.pop();
						parser_stack.pop();
						if(state_stack.peek() == 0){
							break_flag = 1;
							break;
						}
						continue;
					} else{
						break;
					}
				}
				while(this.table.action_table[state_stack.peek()][this.table.terminals.indexOf(input.get(0).statuteName)] == null){
					input.remove(0);
					this.line_index.remove(0);
					if(input.size() == 0){
						break_flag = 1;
						break;
					}
				}if(break_flag == 1){
					break;
				}
				continue;
			}
			if(break_flag == 1){
				break;
			}
			if(state.charAt(0) == 'S'){
				int new_state = Integer.parseInt(state.substring(1, state.length()));
				state_stack.push(new_state);
				parser_stack.push(input.get(0));
				input.remove(0);
				this.line_index.remove(0);
			}
			if(state.charAt(0) == 'r' && !state.split("->")[1].equals("ε)")){
				
				Identifier id = null;
				if(state.equals("r(X->int)")){
					id = new Identifier("X");
					id.attributes.put("type", "int");
					id.attributes.put("width", "4");
				} else if(state.equals("r(X->float)")){
					id = new Identifier("X");
					id.attributes.put("type", "float");
					id.attributes.put("width", "4");
				} else if(state.equals("r(D->proc X id ( M ) A2 { P })")){
					id = new Identifier("D");
					String name = parser_stack.get(parser_stack.size()-8).name;
					int flag = 0;
					for(int i=0;i<this.proc_list.size();i++){
						if(this.proc_list.get(i).equals(name)){
							flag = 1;
							break;
						}
					}
					if(flag == 0){
						this.map_table.put(this.table_stack.peek(),this.offset_stack.peek());
						this.table_stack.peek().name = name;
						this.table_stack.pop();
						this.offset_stack.pop();
					} else{
						this.error.add("error at line " + (this.line_index.get(0)) +", 函数名重复声明");
					}
				} /*else if(state.equals("r(M->X id)")){
					id = new Identifier("M");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					Identifier i2 = parser_stack.get(parser_stack.size()-2);
					Symbol s = new Symbol();
					s.name = i1.name;
					s.type = i2.attributes.get("type");
					s.offset = this.offset_stack.peek();
					this.offset_stack.set(this.offset_stack.size()-1, this.offset_stack.peek() + Integer.parseInt(i2.attributes.get("width")));
					
				}*/ else if(state.equals("r(C->[ digit ] C)")){
					id = new Identifier("C");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					Identifier i2 = parser_stack.get(parser_stack.size()-3);
					id.attributes.put("type", "[" + i2.name + "]" + i1.attributes.get("type") );
					id.attributes.put("width", (Integer.parseInt(i1.attributes.get("width"))*Integer.parseInt(i2.name)) + "");
				} else if(state.equals("r(T->X A1 C)")){
					id = new Identifier("T");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("type", i1.attributes.get("type"));
					id.attributes.put("width", i1.attributes.get("width"));
				} else if(state.equals("r(D->T id ;)")){
					id = new Identifier("D");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					Identifier i2 = parser_stack.get(parser_stack.size()-3);
					Symbol s = new Symbol();
					s.name = i1.name;
					s.type = i2.attributes.get("type");
					s.offset = this.offset_stack.peek();
					int flag = 1;
					SymbolTable currentTable = this.table_stack.peek();
					while(currentTable!=null){
						for(int index=0;index<currentTable.table.size();index++){
							if(currentTable.table.get(index).name.equals(s.name)){
								flag = 0;
								break;
							}
						}
						if(flag == 0){
							break;
						}
						currentTable = currentTable.father;
					}
					if(flag == 0){
						this.error.add("error at line " + (this.line_index.get(0) - 1) +", 重复声明变量");
						//TODO 错误处理
					} else {
						this.offset_stack.set(this.offset_stack.size()-1, this.offset_stack.peek() + Integer.parseInt(i2.attributes.get("width")));
						this.table_stack.peek().table.add(s);
					}
					
				} else if(state.equals("r(F->id)")){
					id = new Identifier("F");
					Identifier i1 = parser_stack.peek();
					int flag = 1;
					SymbolTable currentTable = this.table_stack.peek();
					String type = null;
					Symbol ss = null;
					while(currentTable!=null){
						for(int index=0;index<currentTable.table.size();index++){
							if(currentTable.table.get(index).name.equals(i1.name)){
								flag = 0;
								type = currentTable.table.get(index).type;
								ss = currentTable.table.get(index);
								break;
							}
						}
						if(flag == 0){
							break;
						}
						currentTable = currentTable.father;
					}
					if(flag == 1){
//						System.out.println("error");
						System.out.println(i1.name);
						this.error.add("error at line " + this.line_index.get(0) +", 引用变量前未声明");
						//TODO 错误处理
					} else{ 	
						if(ss.type.startsWith("[")){
							this.error.add("error at line " + this.line_index.get(0) +", 使用数组变量做运算");
						} else{
							id.attributes.put("addr", i1.name);
							id.attributes.put("type", type);
						}
					}
				} else if(state.equals("r(F->digit)")){
					id = new Identifier("F");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("addr", i1.name);
					id.attributes.put("type", "digit");
				} else if(state.equals("r(F->( E ))")){
					id = new Identifier("F");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					id.attributes.put("addr", i1.attributes.get("addr"));
					id.attributes.put("type", i1.attributes.get("type"));
				} else if(state.equals("r(F->L)")){
					id = new Identifier("F");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					id.attributes.put("addr", i1.attributes.get("addr"));
				} else if(state.equals("r(G->F)")){
					id = new Identifier("G");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("addr", i1.attributes.get("addr"));
					id.attributes.put("type", i1.attributes.get("type"));
				} else if(state.equals("r(G->G * F)")){
					id = new Identifier("G");
					id.attributes.put("addr", this.newTemp());
					Identifier i1 = parser_stack.peek();
					Identifier i2 = parser_stack.get(parser_stack.size()-3);
					Quaternary q = new Quaternary("*",i2.attributes.get("addr"),i1.attributes.get("addr"),id.attributes.get("addr"));
					q.num = this.num;
					num ++;
					this.quaternary_list.add(q);
				} else if(state.equals("r(E->G)")){
					id = new Identifier("E");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("addr", i1.attributes.get("addr"));
					id.attributes.put("type", i1.attributes.get("type"));
				} else if(state.equals("r(E->E + G)")){
					id = new Identifier("E");
					Identifier i1 = parser_stack.peek();
					Identifier i2 = parser_stack.get(parser_stack.size()-3);
					if(!(i1.attributes.get("type")==null || i2.attributes.get("type") == null)){
						id.attributes.put("type", this.maxType(i1.attributes.get("type"), i2.attributes.get("type")));
						String a1 = this.widenAndCheck(i1.attributes.get("addr"), i1.attributes.get("type"), id.attributes.get("type"));
						String a2 = this.widenAndCheck(i2.attributes.get("addr"), i2.attributes.get("type"), id.attributes.get("type"));
						if(a1.equals("digit")){
							a1 = i1.attributes.get("addr");
						}
						if(a2.equals("digit")){
							a2 = i2.attributes.get("addr");
						}
						id.attributes.put("addr", this.newTemp());
						Quaternary q = new Quaternary("+",a1,a2,id.attributes.get("addr"));
						q.num = this.num;
						num ++;
						this.quaternary_list.add(q);
					}
				} else if(state.equals("r(S->id = E ;)")){
					id = new Identifier("S");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					Identifier i2 = parser_stack.get(parser_stack.size()-4);
					int flag = 1;
					SymbolTable currentTable = this.table_stack.peek();
					while(currentTable!=null){
						for(int index=0;index<currentTable.table.size();index++){
							if(currentTable.table.get(index).name.equals(i2.name)){
								flag = 0;
								break;
							}
						}
						if(flag == 0){
							break;
						}
						currentTable = currentTable.father;
					}
					if(flag == 1){
						System.out.println("error");
						this.error.add("error at line " + (this.line_index.get(0) - 1) +", 引用变量前未声明");
						//TODO 错误处理  未定义值
					} else{
						if(!(i1.attributes.get("addr")==null)){
							Quaternary q = new Quaternary("=",i1.attributes.get("addr"),i2.name);
							this.quaternary_list.add(q);
							q.num = this.num;
							this.num ++;
						}
					}
				} else if(state.equals("r(L->id [ E ])")){
					id = new Identifier("L");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					Identifier i2 = parser_stack.get(parser_stack.size()-4);
					Symbol s1 = null;
					int flag = 1;
					SymbolTable currentTable = this.table_stack.peek();
					while(currentTable!=null){
						for(int index=0;index<currentTable.table.size();index++){
							if(currentTable.table.get(index).name.equals(i2.name)){
								s1 = currentTable.table.get(index);
								flag = 0;
								break;
							}
						}
						if(flag == 0){
							break;
						}
						currentTable = currentTable.father;
					}
					if(flag == 1){
						this.error.add("error at line " + this.line_index.get(0) +", 引用变量前未声明");
						//TODO 错误处理  未定义值
					} else{
						if(!s1.type.startsWith("[")){
							this.error.add("error at line " + this.line_index.get(0) +", 对非数组变量进行数组访问操作");
						} else{
							id.attributes.put("addr", this.newTemp());
							id.attributes.put("id", s1.name);
							id.attributes.put("type", s1.type);
							id.attributes.put("dimension", 1+"");
							int dim = this.getDimension(id.attributes.get("type")).get(0);
							Quaternary q = new Quaternary("*",i1.attributes.get("addr"),4*dim + "",id.attributes.get("addr"));
							q.num = this.num;
							this.quaternary_list.add(q);
							this.num ++;
						}
					}
				} else if(state.equals("r(L->L [ E ])")){
					id = new Identifier("L");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					Identifier i2 = parser_stack.get(parser_stack.size()-4);
					String te = this.newTemp(); 
					List<Integer> ilist = this.getDimension(i2.attributes.get("type"));
					Quaternary q1 = null;
					if(ilist.size() == Integer.parseInt(i2.attributes.get("dimension"))+1){
						q1 = new Quaternary("*",i1.attributes.get("addr"),4 + "",te);
					} else{
						int dim = this.getDimension(i2.attributes.get("type")).get(Integer.parseInt(i2.attributes.get("dimension")));
						q1 = new Quaternary("*",i1.attributes.get("addr"),4*dim  + "",te);
					}
					id.attributes.put("dimension", Integer.parseInt(i2.attributes.get("dimension"))+1+"");
					q1.num = this.num;
					this.quaternary_list.add(q1);
					this.num++;
					id.attributes.put("addr", this.newTemp());
					Quaternary q2 = new Quaternary("+",i2.attributes.get("addr"),te,id.attributes.get("addr"));
					q2.num = this.num;
					this.quaternary_list.add(q2);
					this.num++;
					id.attributes.put("id", i2.attributes.get("id"));
					id.attributes.put("type", i2.attributes.get("type"));

				} else if(state.equals("r(S->L = E ;)")){
					id = new Identifier("S");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					Identifier i2 = parser_stack.get(parser_stack.size()-4);
					Quaternary q = new Quaternary("=",i1.attributes.get("addr"), i2.attributes.get("id") + "[" + i2.attributes.get("addr") + "]");
					q.num = this.num;
					this.quaternary_list.add(q);
					this.num ++;
				} else if(state.equals("r(I->true)")){
					id = new Identifier("I");
					id.attributes.put("truelist", this.num+"");
					Quaternary q = new Quaternary("j","_");
					q.num = this.num;
					q.truelist = 0;
					this.quaternary_list.add(q);
					this.num++;
				} else if(state.equals("r(I->false)")){
					id = new Identifier("I");
					id.attributes.put("falselist", this.num+"");
					Quaternary q = new Quaternary("j","_");
					q.num = this.num;
					q.falselist = 0;
					this.quaternary_list.add(q);
					this.num++;
				} else if(state.equals("r(I->( B ))")){
					id = new Identifier("I");
					Identifier i1 = parser_stack.get(parser_stack.size()-2);
					id.attributes.put("truelist", i1.attributes.get("truelist"));
					id.attributes.put("falselist", i1.attributes.get("falselist"));
				} else if(state.equals("r(I->E relop E)")){
					id = new Identifier("I");
					id.attributes.put("truelist", num+"");
					id.attributes.put("falselist", num+1+"");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					Identifier i2 = parser_stack.get(parser_stack.size()-2);
					Identifier i3 = parser_stack.get(parser_stack.size()-3);
					Quaternary q1 = new Quaternary("j"+i2.attributes.get("rel"),i3.attributes.get("addr"),i1.attributes.get("addr"),"_");
					q1.num = this.num;
					q1.truelist = 0;
					this.quaternary_list.add(q1);
					this.num ++;
					Quaternary q2 = new Quaternary("j","_");
					q2.num = this.num;
					q2.falselist = 0;
					this.quaternary_list.add(q2);
					this.num++;
				} else if(state.equals("r(I->not I)")){
					id = new Identifier("I");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					id.attributes.put("truelist", i1.attributes.get("falselist"));
					id.attributes.put("falselist", i1.attributes.get("truelist"));
				} else if(state.equals("r(B->B or M H)")){
					id = new Identifier("B");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					Identifier i2 = parser_stack.get(parser_stack.size()-2);
					Identifier i3 = parser_stack.get(parser_stack.size()-4);
					this.backpatch(i3, "falselist", Integer.parseInt(i2.attributes.get("instr")));
					int tl = this.merge(i3, i1, "truelist");
					id.attributes.put("truelist", tl+"");
					id.attributes.put("falselist", i1.attributes.get("falselist"));
				} else if(state.equals("r(H->H and M I)")){
					id = new Identifier("H");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);
					Identifier i2 = parser_stack.get(parser_stack.size()-2);
					Identifier i3 = parser_stack.get(parser_stack.size()-4);
					this.backpatch(i3, "truelist", Integer.parseInt(i2.attributes.get("instr")));
					int tl = this.merge(i3, i1, "falselist");
					id.attributes.put("falselist", tl+"");
					id.attributes.put("truelist", i1.attributes.get("truelist"));
				} else if(state.equals("r(H->I)")){
					id = new Identifier("H");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("truelist", i1.attributes.get("truelist"));
					id.attributes.put("falselist", i1.attributes.get("falselist"));
				} else if(state.equals("r(B->H)")){
					id = new Identifier("B");
					Identifier i1 = parser_stack.peek();
					id.attributes.put("truelist", i1.attributes.get("truelist"));
					id.attributes.put("falselist", i1.attributes.get("falselist"));
				} else if(state.equals("r(relop-><)")){
					id = new Identifier("relop");
					id.attributes.put("rel", "<");
				} else if(state.equals("r(relop-><=)")){
					id = new Identifier("relop");
					id.attributes.put("rel", "<=");
				} else if(state.equals("r(relop->>)")){
					id = new Identifier("relop");
					id.attributes.put("rel", ">");
				} else if(state.equals("r(relop->>=)")){
					id = new Identifier("relop");
					id.attributes.put("rel", ">=");
				} else if(state.equals("r(relop->==)")){
					id = new Identifier("relop");
					id.attributes.put("rel", "==");
				} else if(state.equals("r(relop->!=)")){
					id = new Identifier("relop");
					id.attributes.put("rel", "!=");
				} else if(state.equals("r(S->if ( B ) then A3 S A4 else A3 S A3)")){
					id = new Identifier("S");
					Identifier i1 = parser_stack.get(parser_stack.size()-2); //S
					Identifier i2 = parser_stack.get(parser_stack.size()-3); //A3
					Identifier i4 = parser_stack.get(parser_stack.size()-5); //A4
					Identifier i5 = parser_stack.get(parser_stack.size()-6); //S
					Identifier i6 = parser_stack.get(parser_stack.size()-7); //A3
					Identifier i7 = parser_stack.get(parser_stack.size()-10); //B
					Identifier i8 = parser_stack.get(parser_stack.size()-1);
					this.backpatch(i7, "truelist", Integer.parseInt(i6.attributes.get("instr")));
					this.backpatch(i7, "falselist", Integer.parseInt(i2.attributes.get("instr")));
					int temp = this.merge(i5, i4, "nextlist");
					int nl = this.merge(temp, i1, "nextlist");
					id.attributes.put("nextlist", nl+"");
					this.backpatch(id, "nextlist", Integer.parseInt(i8.attributes.get("instr")));
				} else if(state.equals("r(S->while A3 ( B ) do A3 S A3)")){
					id = new Identifier("S");
					Identifier i1 = parser_stack.get(parser_stack.size()-1);//A3
					Identifier i2 = parser_stack.get(parser_stack.size()-2);//S
					Identifier i3 = parser_stack.get(parser_stack.size()-3);//A4
					Identifier i4 = parser_stack.get(parser_stack.size()-6);//B
					Identifier i5 = parser_stack.get(parser_stack.size()-8);//A3
					this.backpatch(i2, "nextlist", Integer.parseInt(i5.attributes.get("instr")));
					this.backpatch(i4, "truelist", Integer.parseInt(i3.attributes.get("instr")));
					id.attributes.put("nextlist", i4.attributes.get("falselist"));
					Quaternary q = new Quaternary("j",i5.attributes.get("instr"));
					q.num = this.num;
					this.num++;
					this.quaternary_list.add(q);
					this.backpatch(id, "nextlist", Integer.parseInt(i1.attributes.get("instr"))+1);
				} else{
					id = new Identifier(state.split("->")[0].substring(2, state.split("->")[0].length()));
				}
				this.output.add(state);
				int length = state.split("->")[1].split(" ").length;
				for(int i=0;i<length;i++){
					state_stack.pop();
					parser_stack.pop();
				}
				parser_stack.push(id);
				int new_state = Integer.parseInt(this.table.goto_table[state_stack.peek()][this.table.nonterminals.indexOf(state.split("->")[0].substring(2, state.split("->")[0].length()))]);
				state_stack.push(new_state);
			}
			if(state.charAt(0) == 'r' && state.split("->")[1].equals("ε)")){
				Identifier id = null;
				
				
				if(state.equals("r(A1->ε)")){
					id = new Identifier("A1");
					Identifier i1 = parser_stack.peek();
					tt = i1.attributes.get("type");
					ww = i1.attributes.get("width");
				} else if(state.equals("r(C->ε)")){
					id = new Identifier("C");
					id.attributes.put("type", tt);
					id.attributes.put("width", ww);
				} else if(state.equals("r(A2->ε)")){
					id = new Identifier("A2");
					SymbolTable new_table = new SymbolTable();
					new_table.father = this.table_stack.peek();
					this.table_stack.peek().sons.add(new_table);
					this.table_stack.push(new_table);
					this.offset_stack.push(0);
				} else if(state.equals("r(M->ε)")){
					id = new Identifier("M");
					id.attributes.put("instr", this.num+"");
				} else if(state.equals("r(A3->ε)")){/////TODO
					id = new Identifier("A3");
					id.attributes.put("instr", this.num+"");
				} else if(state.equals("r(A4->ε)")){
					id = new Identifier("A4");
					id.attributes.put("nextlist", this.num+"");
					Quaternary q = new Quaternary("j","_");
					q.num = this.num;
					q.nextlist = 0;
					this.num++;
					this.quaternary_list.add(q);			
				} else{
					id = new Identifier(state.split("->")[0].substring(2, state.split("->")[0].length()));
				}
				
				
				this.output.add(state);
				parser_stack.push(id);
				int new_state = Integer.parseInt(this.table.goto_table[state_stack.peek()][this.table.nonterminals.indexOf(parser_stack.peek().statuteName)]);
				state_stack.push(new_state);
			}
			if(state.equals("acc")){
				this.output.add(state);;
				this.map_table.put(this.table_stack.peek(), this.offset_stack.peek());
				break;
			}
		}
	}
	
	public String widenAndCheck(String a, String t, String w){
		if((!t.equals("int")) && (!t.equals("float")) || (!w.equals("int")) && (!w.equals("float"))){
			return "digit";
		}
		if(t.equals(w)){
			return a;
		} else if(t.equals("int") && w.equals("float")){
			String temp = this.newTemp();
			Quaternary q = new Quaternary("=","(float)" + a,temp);
			q.num = this.num;
			this.num++;
			this.quaternary_list.add(q);
			return temp;
		} else{
			return "error";
		}
	}
	
	public String maxType(String a, String b){
		if(a.equals("float") || b.equals("float")){
			return "float";
		}
		if(a.equals("int") && b.equals("int")){
			return "int";
		}
		return "digit";
	}
	
	public void backpatch(Identifier B, String type, int instr){
		if(B.attributes.get(type) == null){
			return;
		}
		int line1 = Integer.parseInt(B.attributes.get(type));
		Quaternary q1 = this.quaternary_list.get(line1);
		if(type.equals("nextlist")){
			int num1 = q1.nextlist;
			while(num1 != 0){
				q1.des = instr + "";
				q1 = this.quaternary_list.get(num1);
				num1 = q1.nextlist;
			}
			q1.des = instr + "";
			q1 = this.quaternary_list.get(num1);
		}
		if(type.equals("truelist")){
			int num1 = q1.truelist;
			while(num1 != 0){
				q1.des = instr + "";
				q1 = this.quaternary_list.get(num1);
				num1 = q1.truelist;
			}
			q1.des = instr + "";
			q1 = this.quaternary_list.get(num1);
		}
		if(type.equals("falselist")){
			int num = q1.falselist;
			while(num != 0){
				q1.des = instr + "";
				q1 = this.quaternary_list.get(num);
				num = q1.falselist;
			}
			q1.des = instr + "";
			q1 = this.quaternary_list.get(num);
		}
	}
	
	//第一个对应的行号写到第二个上
	public int merge(Identifier i1, Identifier i2, String type){
		int line2 = Integer.parseInt(i2.attributes.get(type));
		if(i1.attributes.get(type) == null){
			return line2;
		}
		int line1 = Integer.parseInt(i1.attributes.get(type));
		Quaternary q2 = this.quaternary_list.get(line2);
		if(type.equals("truelist")){
			q2.truelist = line1;
		}
		if(type.equals("falselist")){
			q2.falselist = line1;
		}
		if(type.equals("nextlist")){
			q2.nextlist = line1;
		}
		return line2;
	}
	
	public int merge(int line1, Identifier i2, String type){
		if(i2.attributes.get(type) == null){
			return line1;
		}
		int line2 = Integer.parseInt(i2.attributes.get(type));
		Quaternary q2 = this.quaternary_list.get(line2);
		if(type.equals("truelist")){
			q2.truelist = line1;
		}
		if(type.equals("falselist")){
			q2.falselist = line1;
		}
		if(type.equals("nextlist")){
			q2.nextlist = line1;
		}
		return line2;
	}
	
	public void fillTable(){
		table = new AnalysisTable(new ArrayList<String>(this.terminal_set),new ArrayList<String>(this.non_terminal_set),stateNum);
		for(int i=0;i<this.item_set_list.size();i++){
			ItemSet current = this.item_set_list.get(i);
			Iterator<Item> itr1 = current.itemSet.iterator();
			while(itr1.hasNext()){
				
				Item item = itr1.next();
				if(item.after_point.size() == 0 && (!item.left.equals("S1"))){
					for(int j=0;j<item.search_character.size();j++){
						Production p = new Production();
						p.left = item.left;
						p.right = item.before_point;
						table.action_table[current.num][table.terminals.indexOf(item.search_character.get(j))] = "r(" + p.toString() + ")";
					}
				}
				if(item.after_point.size() == 0 && item.left.equals("S1")){
					for(int j=0;j<item.search_character.size();j++){
						table.action_table[current.num][table.terminals.indexOf(item.search_character.get(j))] = "acc";
					}
				}
				if(item.after_point.size()>0 && this.isTerminal(item.after_point.get(0)) && current.go.containsKey(item.after_point.get(0))){
					//System.out.println(item.after_point.get(0));
					table.action_table[current.num][table.terminals.indexOf(item.after_point.get(0))] = "S" + current.go.get(item.after_point.get(0));
				}
				if(item.after_point.size()>0 && item.after_point.get(0).equals("ε")){
					for(int j=0;j<item.search_character.size();j++){
						Production p = new Production();
						p.left = item.left;
						p.right = item.after_point;
						table.action_table[current.num][table.terminals.indexOf(item.search_character.get(j))] = "r(" + p.toString() + ")";
					}
				}
			}
			for (Map.Entry<String, Integer> entry : current.go.entrySet()) { 
				if(this.isNonTerminal(entry.getKey())){
					table.goto_table[current.num][table.nonterminals.indexOf(entry.getKey())] = entry.getValue() + "";
				}
			}
			
		}
	}
	
	public List<Integer> getDimension(String s){
		List<Integer> result = new ArrayList<Integer>();
		String[] word = s.split("\\]\\[");
		result.add(Integer.parseInt(word[0].split("\\[")[1]));
		for(int i=1;i<word.length-1;i++){
			result.add(Integer.parseInt(word[i]));
		}
		result.add(Integer.parseInt(word[word.length-1].split("]")[0]));
		for(int i=0;i<result.size();i++){
			int temp = 1;
			for(int j=i+1;j<result.size();j++){
				temp *= result.get(j);
			}
			result.set(i, temp);
		}
		return result;
	} 
	
	public String newTemp(){
		String result = "t" + this.temp;
		this.temp ++;
		return result;
	}
	
	
	
	public static void main(String args[]){
		SemParser parser = new SemParser();
		parser.readFromFile("input_wbh.txt");
		
		/*for(int i=0;i<parser.production_list.size();i++){
			for(int j=0;j<parser.production_list.get(i).right.size();j++){
				if(parser.isNonTerminal(parser.production_list.get(i).right.get(j))){
					
				} else if(parser.isTerminal(parser.production_list.get(i).right.get(j))){
					
				}else{
					System.out.println(parser.production_list.get(i).right.get(j));
				}
			}
		}*/
		
		parser.getItemSet();
		/*System.out.println(parser.item_set_list.size());
		for(int i=0;i<parser.item_set_list.size();i++){
			System.out.print(parser.item_set_list.get(i));
		}*/
		/*for(int i=0;i<parser.production_list.size();i++){
			System.out.println(parser.production_list.get(i));
		}*/
		parser.fillTable();
		//System.out.println(parser.table.action_table[0][parser.table.terminals.indexOf("id")]);
		
		parser.parser("test1.txt");
		
		for (Map.Entry<SymbolTable, Integer> entry : parser.map_table.entrySet()) { 
			System.out.println(entry.getKey());
		}
		//System.out.println(parser.table);
		/*for(int i=0;i<parser.output.size();i++){
			System.out.println(parser.output.get(i));
		}*/
		for(int i=0;i<parser.error.size();i++){
			System.out.println(parser.error.get(i));
		}
		for(int i=0;i<parser.quaternary_list.size();i++){
			System.out.println(parser.quaternary_list.get(i));
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
