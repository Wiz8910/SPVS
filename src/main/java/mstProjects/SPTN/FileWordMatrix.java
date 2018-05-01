package mstProjects.SPTN;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileWordMatrix {
	public static String Label;
	public Map<String,Map<String,Integer>> words;
	public List<String> fileNames;
	public List<String> labels;
	
	public FileWordMatrix(Map prevWords,File fileToConstruct){
		words = new HashMap<String,Map<String,Integer>>();
		labels = new ArrayList<String>();
		fileNames = new ArrayList<String>();
	}
	/*
	//expect everyfile to 
	public void addWords(int numWords){
		for(int i=0;i<words;i++){
			words.add(0);
		}
	}*/
	
}
