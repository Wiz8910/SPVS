//base filemapping class that takes a map of senses and the words in the index that apply to them
//then it can produce a csv that has every document with columns replaced by senses
//In addition it can produce LSI representation using ojalgo to perform the decomposition and produce a csv
package mstProjects.SPTN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.util.BytesRef;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.decomposition.SingularValue;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;



public class SPTNFileMapping {
	public Map<String,ArrayList<String>> generalizeToSpecificWords;
	
	public SPTNFileMapping(Map<String, ArrayList<String>> wordsUnderTerms) {
		generalizeToSpecificWords =new HashMap<String,ArrayList<String>>(wordsUnderTerms);
	}
	
	public SPTNFileMapping(String fileName)throws Exception{
		generalizeToSpecificWords =new HashMap<String,ArrayList<String>>();
		BufferedReader source = new BufferedReader(new FileReader(fileName));
		String line = null;
		while((line=source.readLine())!=null){
			//we want to shallow copy the list
			String[] temp = line.split("\\$");
			String key = temp[0];
			ArrayList<String> comps = new ArrayList<String>();
			for(int i=1;i<temp.length;i++){
				comps.add(temp[i]);
			}
			generalizeToSpecificWords.put(key, comps);
		}
		source.close();
		return;
	}
	
	public void saveMapping(String fileName,boolean append)throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append));
		for(String key:generalizeToSpecificWords.keySet()){
			StringBuilder line = new StringBuilder();
			line.append(key);
			line.append("$");
			for(String temp:generalizeToSpecificWords.get(key)){line.append(temp);line.append("$");}
			writer.write(line+"\n");
		}
		writer.close();
		return;
	}
		
	//get occurrence of generalize term for all docs
	public double getInverseDocFreq(String key, IndexSearcher searcher)throws Exception{
		return App.termInverseDocFrequency(searcher, generalizeToSpecificWords.get(key));
	}
	
	//get occurrence of generalize term for a specific doc
	/*
	public float getInverseDocFreq(String key, IndexSearcher searcher, String doc)throws Exception{
		//if key not in set, will get zero anyhow
		int count=0;
		for(String term:generalizeToSpecificWords.get(key)){
			count+= App.termInverseDocFrequency(searcher, term);
		}
		return count;
	}*/
	
	//get occurrence of generalize term for all docs
	public int getKeyCount(String key, IndexSearcher searcher)throws Exception{
		//if key not in set, will get zero anyhow
		int count=0;
		for(String term:generalizeToSpecificWords.get(key)){
			count+= App.termCount(searcher, term);
		}
		return count;
	}
	//get occurrence of generalize term for a specific doc
	public int getKeyCount(String key, IndexSearcher searcher,String doc)throws Exception{
		//if key not in set, will get zero anyhow
		int count=0;
		for(String term:generalizeToSpecificWords.get(key)){
			count+= App.termCount(searcher, term,doc);
		}
		return count;
	}
	
	//generate csv from file and index
	public void generateCSV(String fileName,IndexSearcher searcher, Terms DocumentSet, int minFreq, int maxFreq,boolean useTFIDF)throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		StringBuilder line = new StringBuilder();
		Set<String> keysNotToCheck = new HashSet<String>();
		line.append("fileName,");
		for(String key:generalizeToSpecificWords.keySet()){
			/*int totalHits = getKeyCount(key,searcher);
			if(!(minFreq<totalHits && totalHits<maxFreq)){
				keysNotToCheck.add(key);
			}else{
		*/
			line.append(key);
			line.append(",");
			//}
		}
		writer.write(line.toString()+"\n");
		System.out.println("Printing documents "+DocumentSet.size());
		TermsEnum it = DocumentSet.iterator();
		BytesRef term = it.next();

		int maxLinesInMemory = 1000;
		int curLine =0;
		line = new StringBuilder();
		Map<String,Double> inverseDocCount = new HashMap<String,Double>(); 
		while(term!=null){
			String curDoc = term.utf8ToString();
			line.append(curDoc+",");
			for(String key:generalizeToSpecificWords.keySet()){
				if(!keysNotToCheck.contains(key)){
				//get every document count for current term
					if(!useTFIDF){
						line.append(getKeyCount(key,searcher,curDoc)+",");
					}else{
						//otherwise need tfidf
						//int docCount = getKeyCount(key,searcher,curDoc);
						if(!inverseDocCount.containsKey(key)){inverseDocCount.put(key, getInverseDocFreq(key,searcher));}
						double docFrequencyForTerm = inverseDocCount.get(key);
						line.append(docFrequencyForTerm*getKeyCount(key,searcher,curDoc)+",");
					}
				}
			}
			term=it.next();
			line.append("\n");
			if(curLine>=maxLinesInMemory)
			{
				writer.write(line.toString());
				line = new StringBuilder();
				curLine = -1;
			}
			curLine++;
		}
		writer.close();
		return;
	}
	
	//generate LSI and save to csv from file and index
	public long generateLSI_CSV(String fileName,IndexSearcher searcher, Terms DocumentSet, int minFreq, int maxFreq,int k,boolean useTFIDF)throws Exception{
		//get matrix first
		//get terms with insufficient counts
		System.out.println("Generating LSI");
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		StringBuilder line = new StringBuilder();
		Set<String> keysNotToCheck = new HashSet<String>();
		line.append("fileName,");
		int count = 0;
		//reducing lsi to k terms, so row of terms doesn't make sense
		//but do it to keep same format as other matrix
		for(String key:generalizeToSpecificWords.keySet()){
			int totalHits = getKeyCount(key,searcher);
			if(count<k){
				line.append(key);
				line.append(",");
				count++;
			}
		}
		writer.write(line.toString()+"\n");
		//also want list of documents for later
		PrimitiveDenseStore currentMatrix = PrimitiveDenseStore.FACTORY.makeZero(generalizeToSpecificWords.size(),DocumentSet.getDocCount());
		//now set all indices, rows are term counts columns are documents
		int column =0,row=0;
		for(String key:generalizeToSpecificWords.keySet()){
			column=0;
			TermsEnum it = DocumentSet.iterator();
			BytesRef term = it.next();
			while(term!=null){
				String curDoc = term.utf8ToString();
				double curScore = 0;
				if(!useTFIDF){
					curScore = getKeyCount(key,searcher,curDoc);
				}else{
					curScore = getInverseDocFreq(key,searcher)*getKeyCount(key,searcher,curDoc);
				}
				currentMatrix.set(row, column, curScore);
				column++;
				term=it.next();
			}
			row++;
		}
		long lsiStartTime = System.currentTimeMillis();
		//now compute LSI
		//u correlation between terms, v correlation between documents
		SingularValue<Double> sv = SingularValue.PRIMITIVE.make(currentMatrix);
		if(!sv.compute(currentMatrix)){
			throw(new Exception("Failed to decompose matrix"));
		}
		/* we dont use q1 for lsi
		MatrixStore<Double> oldUK  = sv.getQ1();
		PrimitiveDenseStore u_k = PrimitiveDenseStore.FACTORY.makeZero(sv.getQ1().countRows(),k);//sv.getQ1().countColumns()-k);
		for(long i=0;i<oldUK.countRows();i++)
		{
			for(long col=0;col<u_k.countColumns();col++){
				u_k.add(i,col,oldUK.get(i, col));
			}
		}
		*/
		MatrixStore<Double> oldVK = sv.getQ2();
		PrimitiveDenseStore v_k = PrimitiveDenseStore.FACTORY.makeZero(oldVK.countRows(),k);//sv.getQ2().countColumns()-k);
		for(long i=0;i<oldVK.countRows();i++)
		{	 
			for(long col=0;col<v_k.countColumns();col++){
				v_k.add(i,col,oldVK.get(i, col));
			}
		}
		MatrixStore<Double> oldSigma = sv.getD();
		PrimitiveDenseStore sigma_k = PrimitiveDenseStore.FACTORY.makeZero(k,k);//sv.getD().countRows()-k,sv.getD().countColumns()-k);
		//only need diagonals on this one
		for(long i=0;i<sigma_k.countRows();i++)
		{
			sigma_k.add(i,i,oldSigma.get(i, i));
		}
		//now we compute decomposition each row is a document and it has
		//k columns condensed word representation info
		long lsiEndTime = System.currentTimeMillis();
		TermsEnum it = DocumentSet.iterator();
		BytesRef term = it.next();
		MatrixStore<Double> decomposition = v_k.multiply(sigma_k);
		for(row=0;row<decomposition.countRows();row++){
			line = new StringBuilder();
			//add a junk term for this line because experiment expects it
			line.append(term.utf8ToString());
			line.append(",");
			//now add from decomposition
			for(column=0;column<decomposition.countColumns();column++){
				line.append(decomposition.get(row,column)+",");
			}
			writer.write(line.toString()+"\n");
			term = it.next();
		}
		writer.close();
		return lsiEndTime-lsiStartTime;
		
	}
	
}
