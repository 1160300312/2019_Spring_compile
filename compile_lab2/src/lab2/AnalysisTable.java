package lab2;

import java.util.ArrayList;
import java.util.List;

public class AnalysisTable {
	List<String> terminals;
	List<String> nonterminals;
	
	String[][] action_table;
	String[][] goto_table;
	
	int setnum;
	
	public AnalysisTable(List<String> l1, List<String> l2, int setnum){
		List<String> t = new ArrayList<String>();
		for(int i=0;i<l1.size();i++){
			if(!l1.get(i).equals("¦Å")){
				t.add(l1.get(i));
			}
		}
		t.add("#");
		this.terminals = t;
		List<String> n = new ArrayList<String>();
		for(int i=0;i<l2.size();i++){
			if(!l2.get(i).equals("S1")){
				n.add(l2.get(i));
			}
		}
		this.nonterminals = n;
		action_table = new String[setnum+1][l1.size()];
		goto_table = new String[setnum+1][l2.size()];
		this.setnum = setnum;
	}
	
	@Override
	public String toString(){
		String result = "";
		result += "\t\t";
		for(int i=0;i<this.terminals.size();i++){
			result += this.terminals.get(i) + "\t\t";
		}
		for(int i=0;i<this.nonterminals.size();i++){
			result += this.nonterminals.get(i) + "\t\t";
		}
		result += "\n";
		for(int i=0;i<setnum;i++){
			result += i + "\t\t";
			for(int j=0;j<this.terminals.size();j++){
				result += this.action_table[i][j] + "\t\t";
			}
			for(int j=0;j<this.nonterminals.size();j++){
				result += this.goto_table[i][j] + "\t\t";
			}
			result += "\n";
		}
		return result;
	}
}
