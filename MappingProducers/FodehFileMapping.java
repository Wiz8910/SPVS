//Similar to sptnfilemapping except it produces mapping per document
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



public class FodehFileMapping {
	public Map<String,Map<String,List<String>>> docSanitizations;
	public Set<String> generalizedWords;
	public FodehFileMapping(Map<String,Map<String,List<String>>> givenDocSanitizations) {
		docSanitizations =new HashMap<String,Map<String,List<String>>>(givenDocSanitizations);
		generalizedWords = new HashSet<String>();
		for(String doc:docSanitizations.keySet()){
			for(String generalizedWord:docSanitizations.get(doc).keySet()){
				if(!generalizedWords.contains(generalizedWord)){generalizedWords.add(generalizedWord);}
			}
		}
	}
	
	public FodehFileMapping(String fileName)throws Exception{
		docSanitizations =new HashMap<String,Map<String,List<String>>>();
		generalizedWords = new HashSet<String>();
		BufferedReader source = new BufferedReader(new FileReader(fileName));
		String line = null;
		while((line=source.readLine())!=null){
			//each line is DocName$GeneralizedWord|base|Words$GeneralizedWordTwo|Base|Words$...
			//we want to shallow copy the list
			String[] temp = line.split("\\$");
			String curDoc = temp[0];
			Map<String,List<String>> docTermMap = new HashMap<String,List<String>>();
			for(int i=1;i<temp.length;i++){
				String[] wordMapping = temp[i].split("\\|");
				List<String> baseWords = new ArrayList<String>();
				for(int k=1;k<wordMapping.length;k++){
					baseWords.add(wordMapping[k]);
				}
				if(!generalizedWords.contains(wordMapping[0])){generalizedWords.add(wordMapping[0]);}
				docTermMap.put(wordMapping[0], baseWords);
			}
			docSanitizations.put(curDoc, docTermMap);
		}
		source.close();
		return;
	}
	
	public void saveMapping(String fileName,boolean append)throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName,append));
		for(String curDoc:docSanitizations.keySet()){
			StringBuilder line = new StringBuilder();
			line.append(curDoc);
			line.append("$");
			Map<String,List<String>> docSet = docSanitizations.get(curDoc);
			for(String generalizedTerm:docSet.keySet()){
				line.append(generalizedTerm);
				line.append("|");
				for(String baseTerm:docSet.get(generalizedTerm)){
					line.append(baseTerm);
					line.append("|");
				}
				line.append("$");
			}
			writer.write(line+"\n");
		}
		writer.close();
		return;
	}
	
		
	//get occurrence of generalize term for all docs
	public double getInverseDocFreq(List<String> terms, IndexSearcher searcher)throws Exception{
		return App.termInverseDocFrequency(searcher, terms);
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
	public int getKeyCount( List<String> baseTerms, IndexSearcher searcher)throws Exception{
		//if key not in set, will get zero anyhow
		int count=0;
		for(String term:baseTerms){
			count+= App.termCount(searcher, term);
		}
		return count;
	}
	//get occurrence of generalize term for a specific doc
	public int getKeyCount(List<String> baseTerms, IndexSearcher searcher,String doc)throws Exception{
		//if key not in set, will get zero anyhow
		int count=0;
		for(String term:baseTerms){
			count+= App.termCount(searcher, term,doc);
		}
		return count;
	}
	
	//generate csv from file and index
	public void generateCSV(String fileName,IndexSearcher searcher, Terms DocumentSet, int minFreq, int maxFreq,boolean useTFIDF)throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		StringBuilder line = new StringBuilder();
		line.append("fileName,");
		String[] columns = generalizedWords.toArray(new String[generalizedWords.size()]);
		for(String key:columns){
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
		
		TermsEnum it = DocumentSet.iterator();
		BytesRef term = it.next();
		while(term!=null){
			String curDoc = term.utf8ToString();
			line = new StringBuilder();
			line.append(curDoc+",");
			Map<String,List<String>> curDocMap = docSanitizations.get(curDoc);
			if(!(curDocMap==null)){
				for(String column:columns){
					if(!curDocMap.containsKey(column)){line.append("0,");}
					else{
						if(!useTFIDF){
							line.append(getKeyCount(curDocMap.get(column),searcher,curDoc)+",");
						}else{
							//otherwise need tfidf
							//int docCount = getKeyCount(key,searcher,curDoc);
							List<String> curBaseTerms = curDocMap.get(column);
							double docFrequencyForTerm = getInverseDocFreq(curBaseTerms,searcher);
							line.append(docFrequencyForTerm*getKeyCount(curBaseTerms,searcher,curDoc)+",");
						}
					}
				}

				writer.write(line.toString()+"\n");
			}else{
				System.out.println("No DocMap");
			}
			term=it.next();
		}
		writer.close();
		return;
	}
	
	//generate LSI and save to csv from file and index
	public void oldgenerateLSI_CSV(String fileName,IndexSearcher searcher, Terms DocumentSet, int minFreq, int maxFreq,int k,boolean useTFIDF)throws Exception{
		//get matrix first
		//get terms with insufficient counts
		BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		StringBuilder line = new StringBuilder();
		line.append("fileName,");
		String[] columns = generalizedWords.toArray(new String[generalizedWords.size()]);
		for(String key:columns){
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
		/*
		TermsEnum it = DocumentSet.iterator();
		BytesRef term = it.next();
		while(term!=null){
			String curDoc = term.utf8ToString();
			line = new StringBuilder();
			line.append(curDoc+",");
			Map<String,List<String>> curDocMap = docSanitizations.get(curDoc);
			for(String column:columns){
				if(!curDocMap.containsKey(column)){line.append("0,");}
				else{
					if(!useTFIDF){
						line.append(getKeyCount(curDocMap.get(column),searcher,curDoc)+",");
					}else{
						//otherwise need tfidf
						//int docCount = getKeyCount(key,searcher,curDoc);
						List<String> curBaseTerms = curDocMap.get(column);
						double docFrequencyForTerm = getInverseDocFreq(curBaseTerms,searcher);
						line.append(docFrequencyForTerm*getKeyCount(curBaseTerms,searcher,curDoc)+",");
					}
				}
			}
			term=it.next();
			writer.write(line.toString()+"\n");
		}
		*/
		//also want list of documents for later
		PrimitiveDenseStore currentMatrix = PrimitiveDenseStore.FACTORY.makeZero(columns.length,DocumentSet.getDocCount());
		//now set all indices, rows are term counts columns are documents
		int column =0,row=0;
		for(String columnVal:columns){
			column=0;
			TermsEnum it = DocumentSet.iterator();
			BytesRef term = it.next();
			while(term!=null){
				String curDoc = term.utf8ToString();
				double curScore = 0;
				if(docSanitizations.get(curDoc).containsKey(columnVal)){
					List<String> baseTerms = docSanitizations.get(curDoc).get(columnVal);
					if(!useTFIDF){
						curScore = getKeyCount(baseTerms,searcher,curDoc);
					}else{
						curScore = getInverseDocFreq(baseTerms,searcher)*getKeyCount(baseTerms,searcher,curDoc);
					}
				}
				currentMatrix.set(row, column, curScore);
				column++;
				term=it.next();
			}
			row++;
		}

		//now compute LSI
		//u correlation between terms, v correlation between documents
		SingularValue<Double> sv = SingularValue.PRIMITIVE.make();
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
		PrimitiveDenseStore v_k = PrimitiveDenseStore.FACTORY.makeZero(sv.getQ2().countRows(),k);//sv.getQ2().countColumns()-k);
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
		String[] columns = generalizedWords.toArray(new String[generalizedWords.size()]);
		for(String key:columns){
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
		//also want list of documents for later
		PrimitiveDenseStore currentMatrix = PrimitiveDenseStore.FACTORY.makeZero(columns.length,DocumentSet.getDocCount());
		//now set all indices, rows are term counts columns are documents
		int column =0,row=0;
		for(String columnVal:columns){
			column=0;
			TermsEnum it = DocumentSet.iterator();
			BytesRef term = it.next();
			while(term!=null){
				String curDoc = term.utf8ToString();
				double curScore = 0;
				if(docSanitizations.get(curDoc).containsKey(columnVal)){
					List<String> baseTerms = docSanitizations.get(curDoc).get(columnVal);
					if(!useTFIDF){
						curScore = getKeyCount(baseTerms,searcher,curDoc);
					}else{
						curScore = getInverseDocFreq(baseTerms,searcher)*getKeyCount(baseTerms,searcher,curDoc);
					}
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
