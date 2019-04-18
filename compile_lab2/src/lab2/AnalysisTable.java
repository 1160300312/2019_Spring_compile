package lab2;

import java.util.List;

public class AnalysisTable {
	List<String> terminals;
	List<String> nonterminals;
	
	String[][] action_table;
	String[][] goto_table;
	
	int setnum;
	
	public AnalysisTable(List<String> l1, List<String> l2, int setnum){
		this.terminals = l1;
		this.nonterminals = l2;
		action_table = new String[setnum][l1.size()];
		goto_table = new String[setnum][l2.size()];
	}
	
	@Override
	public String toString(){
		String result = "";
		result += "\t";
		for(int i=0;i<this.terminals.size();i++){
			result += this.terminals.get(i) + "\t";
		}
		for(int i=0;i<this.nonterminals.size();i++){
			result += this.nonterminals.get(i) + "\t";
		}
		result += "\n";
		for(int i=0;i<setnum;i++){
			result += i + "\t";
			for(int j=0;j<this.terminals.size();j++){
				result += this.action_table[i][j] + "\t";
			}
			for(int j=0;j<this.nonterminals.size();j++){
				result += this.goto_table[i][j] + "\t";
			}
			result += "\n";
		}
		return result;
	}
}
