package lab3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lab3.SemParser;

public class MyController implements Initializable{
	public Button browse;
	public TextArea code;
	public TextArea token;
	public AnchorPane root;
	public TextArea error;
	public TextArea table;

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
		
		SemParser parser = new SemParser();
		parser.readFromFile("input_wbh.txt");
		parser.getItemSet();
		parser.fillTable();
		parser.parser(path);
		String code = "";
		FileReader file;
		try {
			file = new FileReader(path);
			BufferedReader br = new BufferedReader(file);
			String str; 
			while((str = br.readLine())!=null){
				code = code + str + "\n";
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		this.code.setText(code);
		String output = "";
		for(int i=0;i<parser.quaternary_list.size();i++){
			output += parser.quaternary_list.get(i) + "\n";
		}
		this.token.setText(output);
		String error = "";
		for(int i=0;i<parser.error.size();i++){
			error += parser.error.get(i) + "\n";
		}
		this.error.setText(error);
		String table = "";
		for (Map.Entry<SymbolTable, Integer> entry : parser.map_table.entrySet()) { 
			table += entry.getKey()+"\n";
		}
		this.table.setText(table);
	}

}
