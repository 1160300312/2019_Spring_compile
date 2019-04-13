package lab1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MyController implements Initializable{

	public Button browse;
	public TextArea code;
	public TextArea token;
	public AnchorPane root;
	public TextArea error;
	
	

	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

	public void browseFile(ActionEvent e){
		Stage stage = (Stage) root.getScene().getWindow();
		stage.setTitle("compile");
		FileChooser fileChooser=new FileChooser();
		File file1 = fileChooser.showOpenDialog(stage);
		String path = file1.getPath();
		
		FileReader file;
		String input = "";
		String code = "";
		try {
			file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);
			String str;
			while((str = br.readLine())!=null){
				input = input + str + "\n";
				code = code + str + "\n";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		int line = 1; 
		StateSnap buffer = new StateSnap();
		Judger judger = new Judger();
		DFA dfa = new DFA();
//		System.out.println(input);
		buffer.input = input;
		buffer.current = 0;
		buffer.forward = 0;
		List<Token> result = new ArrayList<Token>();
		List<String> error_list = new ArrayList<String>();
		while(buffer.current!=buffer.input.length()){
			char cur = buffer.input.charAt(buffer.current);
			int flag = 0;
			if(cur==' '||cur=='\t'||cur=='\n'){
				if(cur == '\n'){
					line ++;
				}
				buffer.current++;
				buffer.forward++;
				flag = 1;
				continue;
			}
			if(judger.isChar(cur)){
				dfa.identifierDFA(buffer, result, line, error_list);
				flag = 1;
				continue;
			} 
			if(judger.isDigit(cur)){
				dfa.digitDFA(buffer, result, line, error_list);
				flag = 1;
				continue;
			}
			if(judger.isBoundary(cur)){
				result.add(new Token(""+buffer.input.charAt(buffer.current),
					judger.getBoundaryName(cur), "_"));
				buffer.current++;
				buffer.forward++;
				flag = 1;
				continue;
			}
			if(buffer.current<buffer.input.length()-1){
//				System.out.println(cur);
				if(cur == '/' && buffer.input.charAt(buffer.current+1) == '*'){
					dfa.commentDFA(buffer, result, line, error_list);
					flag = 1;
					continue;
				}
			}
			if(buffer.current!=buffer.input.length()){
				flag = judger.operationJudge(buffer, result);
				if(flag == 1){
					continue;
				}
			}
			if(flag == 0){
				error_list.add("Unrecognized symbol at line " + line);
				buffer.current++;
				buffer.forward++;
				continue;
			}
		}
		this.code.setText(code);
		String output = "";
		for(int i=0;i<result.size();i++){
			output += result.get(i) + "\n";
		}
		this.token.setText(output);
		String error_output = "";
		for(int i=0;i<error_list.size();i++){
			error_output += error_list.get(i) + "\n";
		}
		this.error.setText(error_output);
		
	}
	
}
