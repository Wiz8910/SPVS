package mstProjects.SPTN;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.sf.extjwnl.data.relationship.RelationshipFinder;
import net.sf.extjwnl.data.relationship.RelationshipList;
//import net.didion.jwnl.JWNL;
//import  net.sf.extjwnl.JWNL;
import  net.sf.extjwnl.JWNLException;
import  net.sf.extjwnl.data.IndexWord;
//net.sf.extjwnl.JWNL;
import  net.sf.extjwnl.data.POS;
import  net.sf.extjwnl.data.Synset;
import  net.sf.extjwnl.data.Pointer;
import net.sf.extjwnl.data.PointerTarget;
import  net.sf.extjwnl.data.PointerType;
import  net.sf.extjwnl.data.Word;
import net.sf.extjwnl.data.list.PointerTargetNode;
import net.sf.extjwnl.data.list.PointerTargetNodeList;
import net.sf.extjwnl.data.list.PointerTargetTree;
import net.sf.extjwnl.data.list.PointerTargetTreeNode;
import  net.sf.extjwnl.dictionary.Dictionary;
import net.sf.extjwnl.dictionary.MorphologicalProcessor;
import net.sf.extjwnl.data.PointerUtils;

//lucene stuff
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanQuery.Builder;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.store.Directory;

/*
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hit;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;*/
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.IOContext;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.QueryBuilder;
import org.apache.lucene.util.Version;

/**
 * Hello world!
 *
 *///lucene
public class App 
{

	public static String DATADir =  "PreprocessedtestDataSet";//"PreprocessedtestDataSet"
	public static final String SynonomizedDATADir = "SynonomizedPreprocessedtestDataSet";//"20news-18828";
	public static final String luceneIndex ="testIndex";
	public static final String MappingDir = "mappings";

	private static int LSI_K = 150;
	private static final boolean USE_TF_IDF = true;
	private static boolean COMPUTE_LSI = false;
	public static final String RecuperoIndex = "TestRecuperoMapping.txt";
	public static final String Recuperocsv = "TestRecuperoMapping.csv";
	public static final String HothoIndex = "TestHothoMapping.txt";
	public static final String Hothocsv = "TestHothoMapping.csv";
	public static final String FodehIndex = "TestFodehMapping.txt";
	public static final String Fodehcsv = "TestFodehMapping.csv";
	public static final String SARIndex = "TestSARMapping.txt";
	public static final String SARcsv = "TestSARMapping.csv";
	public static final String SARGreedyIndex = "TestSARGreedyMapping.txt";
	public static final String SARGreedycsv = "TestSARGreedyMapping.csv";
	public static final String BaseIndex = "TestBaseMapping.txt";
	public static final String Basecsv = "TestBaseMapping.csv";
	public static final String AllIndex = "TestAllMapping.txt";
	public static final String Allcsv = "TestAllMapping.csv";
	

	public static final String NounIndex = "Nouns.txt";
	
	public static Map<String,Integer> senseCounts;
	public static String timeFile = "times.txt";
	
	public static String getCurIndexString(int minFreq, int maxFreq, String curIndex){
		String index="";
		if(SynonomizedDATADir.equals(DATADir)){index = "Synnomized";}
		index = index+"Min"+minFreq+"Max"+maxFreq+curIndex;
		return index;
	}
	public static void processDirectory(File source,Map<String,Map<String,Integer>> wordMaps,
										Map<String,Integer> allWords,String prevName){
		String line;
		//for(File file:source.listFiles()){System.out.println(file.getName());}
		for(File file: source.listFiles()){
			if(file.isDirectory()){processDirectory(file,wordMaps,allWords,prevName+"."+file.getName());}
			else{
				Map<String,Integer> wordMap = new HashMap<String,Integer>();
				try{
					BufferedReader bf = new BufferedReader(new FileReader(file.getAbsolutePath()));
					while((line=bf.readLine())!=null){
						String[] words = line.split("\\s+");
						for(String word:words){
							//replace special characters only want letters
							word = word.replaceAll("[^\\p{L}\\p{Z}]","");
							//update current wordlist
							if(wordMap.containsKey(word)){
								wordMap.put(word, 1+wordMap.get(word));
							}else{
								wordMap.put(word,1);
							}
							//and global one
							if(allWords.containsKey(word)){
								allWords.put(word, 1+wordMap.get(word));
							}else{
								allWords.put(word,1);
							}
						}
					}
					bf.close();
					wordMaps.put(prevName+"."+file.getName(), wordMap);
				}catch(Exception e){
					System.out.println("Failed for "+file.getAbsolutePath());
				}
				
			}
		}
		return;
	}
	
	//////////////////lucene methods///////////////////////////////
	private final static String CONTENT = "contents";
	private final static String TITLE = "path";
	//recursive function to take directory and add all files to index
	//TODO if we have one file per line instead of one doc per file could speed things up
	static void indexDocs(final IndexWriter writer, Path path)throws IOException{
		if(Files.isDirectory(path)){
			Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)throws IOException{
					try{
						indexDoc(writer, file,attrs.lastModifiedTime().toMillis());
					}catch(IOException ignore){
						ignore.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	//and actual function to index a file
	static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException{
		Document doc = new Document();
		//add path to doc as path field
		final FieldType ft = new FieldType(TextField.TYPE_STORED);
		Field pathField = new StringField(TITLE,file.toString(),Field.Store.YES);
		doc.add(pathField);
		//doc.add(new Field(CONTENT,new FileReader(file.toFile()),ft));
		//then add document coments
		//gonna slow down but have to make string
		//\Z is end of string anchor so input will be one token
		String content = new String(Files.readAllBytes(file));//,new FileReader(file.toFile())
		doc.add(new Field(CONTENT,content,TYPE_STORED_VECTORIZED));
		//either create new doc or update doc based on open mode
		if(writer.getConfig().getOpenMode()==OpenMode.CREATE){
			writer.addDocument(doc);
		}else{
			writer.updateDocument(new Term("path",file.toString()),doc);
		}
			
	}
	
	static void synonomizeDoc(Path path,final Path outPath)throws Exception{
		final Random randGen = new Random();
		if(Files.isDirectory(path)){
			Files.walkFileTree(path, new SimpleFileVisitor<Path>(){
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)throws IOException{
					System.out.println(file.toString());
					try{
						synonomizeDoc(file,outPath,randGen);
					}catch(Exception ignore){
						ignore.printStackTrace();
					}
					return FileVisitResult.CONTINUE;
				}
			});
		}
	}
	
	//and actual function to index a file
	static void synonomizeDoc(Path file,Path outDirectory, Random randGen) throws Exception{
		//a bit screwy but only one level of directory so its okay
		//out to outDirectory/currentCategoryDirectory/Synonmized+currentFileName
		Path outputFile = Paths.get(outDirectory.toString()+File.separator+file.getParent().getFileName()+File.separator+"Synonomized"+file.getFileName());
		FileReader fileReader = new FileReader(file.toFile());
		BufferedReader br = new BufferedReader(fileReader);
		FileWriter fileWriter = new FileWriter(outputFile.toFile());
		BufferedWriter bw = new BufferedWriter(fileWriter);
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		String line =null;
		while((line=br.readLine())!=null){
			String[] args = line.split("\\s+");
			String[] argsOut = new String[args.length];
			//now synonomize
			for(int i=0;i<args.length;i++){//String arg:args){
				//see if we have a noun, verb or other part of speech we can lookup
				IndexWord word = morph.lookupBaseForm(POS.NOUN,args[i]);
				if(word==null){
					word = morph.lookupBaseForm(POS.VERB, args[i]);
				}
				if(word==null){
					word = morph.lookupBaseForm(POS.ADJECTIVE, args[i]);
				}
				if(word==null){
					word = morph.lookupBaseForm(POS.ADVERB, args[i]);
				}
				//now if we found an index word replace arg with random dataset
				if(word!=null&&word.getSenses()!=null){
					List<Synset> possibleSynset = word.getSenses();
					List<Word> possibleWords = possibleSynset.get(randGen.nextInt(possibleSynset.size())).getWords();
					argsOut[i] = possibleWords.get(randGen.nextInt(possibleWords.size())).getLemma();
				}else{
				//otherwise simply keep the term the same
					argsOut[i]=args[i];
				}
			}
			//now write the new line with space seperating
			StringBuilder lineOut = new StringBuilder();
			for(String argOut:argsOut){
				lineOut.append(argOut+" ");
			}
			lineOut.append("\n");
			bw.write(lineOut.toString());
		}
		bw.close();
		br.close();
	}
	
	//apparently need to set my own field types to enable storing of term vector
	//got this from http://stackoverflow.com/questions/1844194/get-cosine-similarity-between-two-documents-in-lucene?noredirect=1&lq=1
	/* Indexed, tokenized, stored. */
    public static final FieldType TYPE_STORED_VECTORIZED = new FieldType();

    static {
    	//TYPE_STORED_VECTORIZED.setIndexOptions(true);
    	//TYPE_STORED_VECTORIZED.setTokenized(true);
    	//TYPE_STORED_VECTORIZED.setIndexed(true);
    	TYPE_STORED_VECTORIZED.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    	TYPE_STORED_VECTORIZED.setStored(true);
    	TYPE_STORED_VECTORIZED.setStoreTermVectors(true);
    	//TYPE_STORED_VECTORIZED.
    	//TYPE_STORED_VECTORIZED.freeze();
    }

	//my generic analyzer for lucene use
	private final static Analyzer analyzer = new Analyzer(){
		@Override
		protected TokenStreamComponents createComponents(String fieldName) {
			Tokenizer source = new StandardTokenizer();
		    TokenStream filter = new LowerCaseFilter(source);
		    return new TokenStreamComponents(source, filter);
		}
	};
	
	
	//takes directory containing documents as first parameter and 
	//directory to save index as second parameter
	private static void generateIndex(Path docDir,Path indexDir)throws IOException{
		//Indexer indexer = new Indexer("index");
		Date start = new Date();
		Directory dir = FSDirectory.open(indexDir);
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		//can increase buffer iwc.setRAMBufferSizeMB(256);
		IndexWriter writer = new IndexWriter(dir,iwc);
		indexDocs(writer,docDir);
		
		//this is costly operation so only do if index is static
		//speeds up searches
		writer.forceMerge(1);
		
		writer.close();
		Date end = new Date();
		System.out.println(end.getTime()-start.getTime() + " total milliseconds");
		
	}
	
	//get number of occurneces of term across all documents
	public static int termCount(IndexSearcher searcher, String term)throws Exception{
	    // Parse a simple query that searches for "text":
		QueryBuilder querParse = new QueryBuilder(analyzer);
		Query query = querParse.createBooleanQuery(CONTENT, term);
		TopDocs docs = searcher.search(query, 10);
		//return number of documents with the following term
		return docs.totalHits;
	}	
	
	//get number of docs with term
	public static int docCount(IndexSearcher searcher, String term)throws Exception{

		Builder builder = new BooleanQuery.Builder();
		Query textField = new TermQuery(new Term(CONTENT, term));
		builder.add(textField,BooleanClause.Occur.SHOULD);
		BooleanQuery query =builder.build();
		return searcher.count(query);
	}	
	
	public static double termInverseDocFrequency(IndexSearcher searcher, String term)throws Exception{
		return Math.log(searcher.getIndexReader().numDocs()/(1.0+termFrequency(searcher,term)));
	}
	public static double termInverseDocFrequency(IndexSearcher searcher, List<String> term)throws Exception{
		return Math.log(searcher.getIndexReader().numDocs()/(1.0+termFrequency(searcher,term)));
	}
	
	//get number of documents with any of a list of terms
	//get number of documents with a term
	public static int termFrequency(IndexSearcher searcher, List<String> terms)throws Exception{
	    // Parse a simple query that searches for "text":
		BooleanQuery.setMaxClauseCount(20000);
		Builder builder = new BooleanQuery.Builder();
		for(String term:terms){

			Query textField = new TermQuery(new Term(CONTENT, term));
			builder.add(textField,BooleanClause.Occur.SHOULD);
		}
		BooleanQuery query =builder.build();
		return searcher.count(query);
	}
	
	//get number of documents with a term
	public static int termFrequency(IndexSearcher searcher, String term)throws Exception{

	    // Parse a simple query that searches for "text":
		QueryBuilder querParse = new QueryBuilder(analyzer);
		Query query = querParse.createBooleanQuery(CONTENT, term);
			
		TotalHitCountCollector collector = new TotalHitCountCollector();//.create(5);
		return searcher.count(query);
	}
	//get number of occurences of term in doc
	public static int termCount(IndexSearcher searcher, String term,String doc)throws Exception{
	    // Parse a simple query that searches for "text":

		Query titleField = new TermQuery(new Term(TITLE, doc));
		Query textField = new TermQuery(new Term(CONTENT, term));
		BooleanQuery booleanQuery = new BooleanQuery.Builder()
									   .add(titleField,BooleanClause.Occur.MUST)
									   .add(textField,BooleanClause.Occur.MUST)
									   .build();
		QueryBuilder querParse = new QueryBuilder(analyzer);
		TopScoreDocCollector collector = TopScoreDocCollector.create(5);
		TopDocs docs = searcher.search(booleanQuery, 10);
		//return number of documents with the following term
		return docs.totalHits;
	}	

	 /*irectoryReader ireader = DirectoryReader.open(FSDirectory.open(indexPath));
   IndexSearcher isearcher = new IndexSearcher(ireader);
   // Parse a simple query that searches for "text":
   QueryParser parser = new QueryParser("fieldname", analyzer);
   Query query = parser.parse("text");
   ScoreDoc[] hits = isearcher.search(query, null, 1000).scoreDocs;
   assertEquals(1, hits.length);
   // Iterate through the results:
   for (int i = 0; i < hits.length; i++) {
     Document hitDoc = isearcher.doc(hits[i].doc);
     assertEquals("This is the text to be indexed.", hitDoc.get("fieldname"));
   }
   ireader.close();
   directory.close();
	*/

	//get all terms
	
	//now generalize
	/*
	long count=0;
	long max=50000,min=0;
	BytesRef term = it.next();
	while(term!=null){
		if(count>min&&count<max)
			System.out.println(term.utf8ToString());
		count++;
		term = it.next();
	}
	System.out.println(count+" unique terms");*/

	/*
	QueryBuilder querParse = new QueryBuilder(analyzer);
	Query query = querParse.createBooleanQuery(CONTENT, "zyckp");
	TopScoreDocCollector collector = TopScoreDocCollector.create(5);
	searcher.search(query, collector);
	System.out.println(collector.getTotalHits()+" docs match");
	ScoreDoc[] hits = collector.topDocs().scoreDocs;
	if(hits.length==0){System.out.println("No matches found");}
	// `i` is just a number of document in Lucene. Note, that this number may change after document deletion 
	for (int i = 0; i < hits.length; i++) {
	    Document hitDoc = searcher.doc(hits[i].doc);  // getting actual document
	    System.out.println("Title: " + hitDoc.get(TITLE));
	    //System.out.println("Content: " + hitDoc.get(CONTENT));
	}*/
	//test
	//TopDocsCollector totalCollector = new TopDocsCollector(); 
	//searcher.search(query, totalCollector);
	//System.out.println(totalCollector.getTotalHits());
	
	//method that saves every word not just nouns
	public static void AllFromIndex(Path indexPath,Path newIndex,Path dataSet, int minFreq, int maxFreq)throws Exception{
		long startTime = System.currentTimeMillis();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);

		//new directory for paired down index
		/*
		Directory dir = FSDirectory.open(newIndex);
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		//can increase buffer iwc.setRAMBufferSizeMB(256);
		IndexWriter writer = new IndexWriter(dir,iwc);
		*/
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		File source = new File(dataSet.toString());
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		int count=0;
		while(term!=null){
			String curTerm = term.utf8ToString();
			int freq = termCount(searcher,curTerm);
			if(freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
			}
			term=it.next();
		}
		System.out.println("Have "+termsToConsider.size()+" Terms");
		Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		for(String word:termsToConsider){
			ArrayList<String> temp = new ArrayList();
			temp.add(word);
			wordsUnderTerms.put(word, temp);
		}
		long endTime = System.currentTimeMillis();
		writeTime(AllIndex,endTime-startTime);
		SPTNFileMapping baseMapping = new SPTNFileMapping(wordsUnderTerms);
		baseMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,AllIndex),false);
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv");
		baseMapping.generateCSV(MappingDir+File.separator+Allcsv, searcher, titles,minFreq,maxFreq,USE_TF_IDF);
		if(false&&COMPUTE_LSI){
			long lsiTime = baseMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+Allcsv, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+AllIndex,endTime-startTime+lsiTime);
		}
		System.out.println("End with "+termsToConsider.size());
		return;		
	}
	
	//base method to compare for index, simply checks if nouns
	public static void BaseFromIndex(Path indexPath,Path newIndex,Path dataSet, int minFreq, int maxFreq)throws Exception{
		long startTime = System.currentTimeMillis();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);

		//new directory for paired down index
		/*
		Directory dir = FSDirectory.open(newIndex);
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		//can increase buffer iwc.setRAMBufferSizeMB(256);
		IndexWriter writer = new IndexWriter(dir,iwc);
		*/
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		File source = new File(dataSet.toString());
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		int count=0;
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
			}
			term=it.next();
			count++;
		}
		System.out.println("Have "+termsToConsider.size()+" terms");
		//now format the data set rather pointlessly to play nice with functions for other data
		Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		for(String word:termsToConsider){
			ArrayList<String> temp = new ArrayList();
			temp.add(word);
			wordsUnderTerms.put(word, temp);
		}
		long endTime = System.currentTimeMillis();
		writeTime(BaseIndex,endTime-startTime);
		
		SPTNFileMapping baseMapping = new SPTNFileMapping(wordsUnderTerms);
		baseMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,BaseIndex),false);
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv");
		baseMapping.generateCSV(MappingDir+File.separator+Basecsv, searcher, titles,minFreq,maxFreq,USE_TF_IDF);
		if(COMPUTE_LSI){
			long lsiTime = baseMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+Basecsv, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+BaseIndex,endTime-startTime+lsiTime);
		}
		return;		
	}
	
	//sadly have to use a class variable for recursive function,
	//stupid not passing by references
	private static float bestScore;
	
	
	public static void indexNouns(Path indexPath,Path newIndex,Path dataSet,
											   int minFreq, int maxFreq, String FilePath)throws Exception{
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);

		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		System.out.println(dictionary.getVersion());
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		//File source = new File(dataSet.toString());
		Terms allTerms = MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		int count=0;
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word =  morph.lookupBaseForm(POS.NOUN, curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
			}
			term=it.next();
			count++;
		}
		System.out.println(termsToConsider.size());
		writeTerms(termsToConsider,FilePath);
		return;
	}
	
	public static void writeTerms(ArrayList<String> terms, String filePath)throws Exception{
		BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
		for(String key:terms){
			StringBuilder line = new StringBuilder();
			line.append(key);
			line.append("$");
			writer.write(line+"\n");
		}
		writer.close();
	}
	
	public static ArrayList<String> readTerms(String filePath)throws Exception{
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		ArrayList<String> result = new ArrayList<String>();
		String line;
		while((line=reader.readLine())!=null){
			String[] temp = line.split("\\$");
			for(String term:temp){
				result.add(term);
			}
		}
		return result;
	}	
	
	public static List<String> getPresentTerms(String curTerm,List<String> baseTerms, Map<String,IndexWord> wordMap){
		List<String> set = new ArrayList<String>();
		set.add(curTerm);

		IndexWord curWord = wordMap.get(curTerm);
		List<Synset> givenSynset = curWord.getSenses();
		for(String term:baseTerms){
			if(term.equals(curTerm)){continue;}
			IndexWord candidateWord = wordMap.get(term);
			boolean inASynset =false;
			for(int i=0;i<givenSynset.size()&&!inASynset;i++){
				if(givenSynset.get(i).containsWord(candidateWord.getLemma())){inASynset=true;}
			}
			if(inASynset){
				set.add(term);
			}
		}
		return set;
	}
	public static int getUniqueSenses(List<String> terms, Map<String,IndexWord> wordMap){
		Set<Synset> senses = new HashSet<Synset>();
		//List<Synset> senses = new ArrayList<Synset>();
		for(String term:terms){
			IndexWord candidateWord = wordMap.get(term);
			for(Synset temp:candidateWord.getSenses()){
				//hashSet simply ignores attempting to insert duplicates 
				//so that's why not checking
				senses.add(temp);
			}
		}
		return senses.size();
	}
	//lucene methods to perform text sanitization from index
	//indexPath is directory containing generated index
	public static int PartialSarFromIndex(Path indexPath,Path newIndex,Path dataSet,
										   int minFreq, int maxFreq,int startingIndex,int maxTerms, String termsPath,int SantizationGeneration, boolean greedy)throws Exception{

		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		Map<String,IndexWord> wordSenses = new HashMap<String,IndexWord>();
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();

		ArrayList<String> termsToConsider = readTerms(termsPath);//getNouns=new ArrayList<String>();
		for(String termToConsider:termsToConsider){
			IndexWord word = morph.lookupBaseForm(POS.NOUN, termToConsider);
			wordSenses.put(termToConsider, word);
		}
		//also precompute termCounts for each term
		Map<String,Integer> termCounts = new HashMap<String,Integer>();
		for(int j=termsToConsider.size()-1;j>=0;j--){
			termCounts.put(termsToConsider.get(j), docCount(searcher,termsToConsider.get(j)));
		}
		Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		//todo consider different data structure for faster removal
		System.out.println("Start with "+termsToConsider.size());
		int iterations;
		numScoresChecked = 0;
		for(iterations=startingIndex;iterations<termsToConsider.size()&&iterations<maxTerms;iterations++){
			String key = termsToConsider.get(iterations);
			//now 0 is lowest possible score
			IndexWord word = wordSenses.get(key);
			float initScore = 0;
			
			if(SantizationGeneration==-1){
				initScore = docCount(searcher,key);
				int numSenses = word.getSenses().size();//getUniqueSenses(basePresentTerms,wordSenses);
				int numDocsOccuring = docCount(searcher,key);// termFrequency(searcher,basePresentTerms);
				initScore = numDocsOccuring/((float)numSenses);
			}
			bestScore = initScore;
			ArrayList<Integer> consolidatedWords=null;
			Synset replacingSense=null;
			//now iterate through every possibly sense
			Map<Synset,ArrayList<Integer>> termsUsed = new HashMap<Synset,ArrayList<Integer>>();
			int count=0;
			do
			{
				for(Synset originalSense:word.getSenses()){
					int prevWords =originalSense.getWords().size();
					//go through every node and get necessary variables
					PointerTargetTreeNode hypernymNode = PointerUtils.getHypernymTree(originalSense).getRootNode();
					Map<Synset,ArrayList<Integer>> temp = null;
					if(greedy){
						boolean[] indicesInSet = new boolean[termsToConsider.size()];
						Arrays.fill(indicesInSet, false);
						List<Synset> terms = new ArrayList<Synset>();
						temp =checkScore(hypernymNode,iterations,prevWords,initScore,termsToConsider,termCounts,wordSenses,indicesInSet,searcher,terms,0,SantizationGeneration-count);
					}else{
						temp =checkScore(hypernymNode,iterations,prevWords,initScore,termsToConsider,termCounts,wordSenses,searcher,0,SantizationGeneration-count);
					}
					//if we have an item we know we beat the previous best for the current sense
					if(temp!=null&&temp.keySet().size()>0){
						termsUsed = temp;
					}
				}
				count++;
			}while(bestScore==initScore&&SantizationGeneration-count>0);
			//if we beat initial score, we replace term
			if(bestScore>initScore){
				replacingSense = termsUsed.keySet().iterator().next();
				if(termsUsed.keySet().size()!=1){
					throw new Exception("Invalid number of keys in replacing sense");
				}
				String keySense = replacingSense.getWords().get(0).getLemma();
				if(wordsUnderTerms.containsKey(keySense)){
					wordsUnderTerms.get(keySense).add(termsToConsider.get(iterations));
				}else{
					ArrayList<String> baseWords = new ArrayList<String>();
					baseWords.add(termsToConsider.get(iterations));
					wordsUnderTerms.put(keySense,baseWords);
				}
			}else{
				ArrayList<String> shortList = new ArrayList<String>();
				shortList.add(termsToConsider.get(iterations));
				wordsUnderTerms.put(shortList.get(0), shortList);
			}
		}
		System.out.println("Checked "+numScoresChecked+" scores");
		//writeTerms(termsToConsider,termsPath);
		SPTNFileMapping sarMapping = new SPTNFileMapping(wordsUnderTerms);
		sarMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,
				                (SantizationGeneration==-1?"":SantizationGeneration)+(greedy?SARGreedyIndex:SARIndex)),true);
		return termsToConsider.size()==iterations?0:iterations;		
	}
	
	//lucene methods to perform text sanitization from index
		//indexPath is directory containing generated index
		public static int OldPartialSarFromIndex(Path indexPath,Path newIndex,Path dataSet,
											   int minFreq, int maxFreq,int startingIndex,int maxTerms, String termsPath,int SantizationGeneration, boolean greedy)throws Exception{

			Dictionary dictionary = Dictionary.getDefaultResourceInstance();
			MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
			RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
			IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
			IndexSearcher searcher = new IndexSearcher(reader);
			//ArrayList<String> termsToConsider = readTerms(termsPath);
			/*
			*/
			Map<String,IndexWord> wordSenses = new HashMap<String,IndexWord>();
			Terms allTerms =MultiFields.getTerms(reader, CONTENT);
			TermsEnum it = allTerms.iterator();
			BytesRef term = it.next();

			ArrayList<String> termsToConsider = readTerms(termsPath);//getNouns=new ArrayList<String>();
			for(String termToConsider:termsToConsider){
				IndexWord word = morph.lookupBaseForm(POS.NOUN, termToConsider);
				wordSenses.put(termToConsider, word);
			}
			/*
			while(term!=null){
				String curTerm = term.utf8ToString();
				IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
				int freq = termCount(searcher,curTerm);
				if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
					termsToConsider.add(curTerm);
					wordSenses.put(curTerm, word);
				}
				term=it.next();
			}
			*/
			//also precompute termCounts for each term
			Map<String,Integer> termCounts = new HashMap<String,Integer>();
			for(int j=termsToConsider.size()-1;j>=0;j--){
				termCounts.put(termsToConsider.get(j), docCount(searcher,termsToConsider.get(j)));
			}
			Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
			//todo consider different data structure for faster removal
			System.out.println("Start with "+termsToConsider.size());
			int iterations;
			for(iterations=startingIndex;iterations<termsToConsider.size()&&iterations<maxTerms;iterations++){
				String key = termsToConsider.get(iterations);
				//now 0 is lowest possible score
				IndexWord word = wordSenses.get(key);
				float initScore = SantizationGeneration==-1?docCount(searcher,key)/((float) getCount(word.getSenses())):0;
				bestScore = initScore;
				ArrayList<Integer> consolidatedWords=null;
				Synset replacingSense=null;
				//now iterate through every possibly sense
				Map<Synset,ArrayList<Integer>> termsUsed = new HashMap<Synset,ArrayList<Integer>>();
				int count=0;
				do
				{
					for(Synset originalSense:word.getSenses()){
						int prevWords =originalSense.getWords().size();
						//go through every node and get necessary variables
						PointerTargetTreeNode hypernymNode = PointerUtils.getHypernymTree(originalSense).getRootNode();
						Map<Synset,ArrayList<Integer>> temp = null;
						if(greedy){
							boolean[] indicesInSet = new boolean[termsToConsider.size()];
							Arrays.fill(indicesInSet, false);
							List<Synset> terms = new ArrayList<Synset>();
							temp =checkScore(hypernymNode,iterations,prevWords,initScore,termsToConsider,termCounts,wordSenses,indicesInSet,searcher,terms,0,SantizationGeneration-count);
						}else{
							temp =checkScore(hypernymNode,iterations,prevWords,initScore,termsToConsider,termCounts,wordSenses,searcher,0,SantizationGeneration-count);
						}
						//if we have an item we know we beat the previous best for the current sense
						if(temp!=null&&temp.keySet().size()>0){
							termsUsed = temp;
						}
					}
					count++;
				}while(bestScore==initScore&&SantizationGeneration-count>0);
				//if we beat initial score, we replace term
				if(bestScore>initScore){
					replacingSense = termsUsed.keySet().iterator().next();
					if(termsUsed.keySet().size()!=1){
						throw new Exception("Invalid number of keys in replacing sense");
					}
					String keySense = replacingSense.getWords().get(0).getLemma();
					if(wordsUnderTerms.containsKey(keySense)){
						wordsUnderTerms.get(keySense).add(termsToConsider.get(iterations));
					}else{
						ArrayList<String> baseWords = new ArrayList<String>();
						baseWords.add(termsToConsider.get(iterations));
						wordsUnderTerms.put(keySense,baseWords);
					}
					/*
					consolidatedWords = termsUsed.get(replacingSense);
					//save which base terms have been generalize to current term
					//String prevWord = termsToConsider.get(i);
					//TODO consider changing datatype since adding like this
					ArrayList<String> baseWords = new ArrayList<String>(consolidatedWords.size()+1);
					//for(int j=consolidatedWords.size()-1;j>=0;j--){//<consolidatedWords.size();j++){
						int index = consolidatedWords.get(j);
					    //baseWords.add(termsToConsider.get(index));
						baseWords.add(termsToConsider.get(index));
					//}
					baseWords.add(termsToConsider.get(0));
					/*
					for(int j=baseWords.size()-1;j>=0;j--){
						termsToConsider.remove(baseWords.get(j));
					}
					wordsUnderTerms.put(replacingSense.getWords().get(0).getLemma(), baseWords);
					//get the replacing words
					//removeIndices(termsToConsider,consolidatedWords);
					//termsInTree.clear();*/
				}else{
					ArrayList<String> shortList = new ArrayList<String>();
					shortList.add(termsToConsider.get(iterations));
					wordsUnderTerms.put(shortList.get(0), shortList);
				}
			}
			//writeTerms(termsToConsider,termsPath);
			SPTNFileMapping sarMapping = new SPTNFileMapping(wordsUnderTerms);
			sarMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,
					                (SantizationGeneration==-1?"":SantizationGeneration)+(greedy?SARGreedyIndex:SARIndex)),true);
			/*
			Terms titles =MultiFields.getTerms(reader, TITLE);
			System.out.println("Generating csv");
			sarMapping.generateCSV(SARcsv, searcher, titles,minFreq,maxFreq);
			System.out.println("End with "+termsToConsider.size());
			*/
			return termsToConsider.size()==iterations?0:iterations;		
		}
	
	//static Map<String,Integer> termsInTree = new HashMap<String,Integer>();
	//get count of all words applicable to this node and its hyponyms
	/*
	private static int getCount(PointerTargetTreeNode node){
		int count = 0;
		System.out.println(node.toString());

		if(senseCounts.containsKey(node.toString())){
			return senseCounts.get(node.getSynset().toString());
		}
		if(node.getSynset()!=null){

			count+= node.getSynset().getPointers().size();//.getWords().size();
		}
		for(PointerTargetTreeNode child: node.getChildTreeList())
		{
			count += getCount(child);
		}
		senseCounts.put(node.toString(), count);
		return count;
	}*/
	
	private static int getCount(Synset sense)throws Exception{
		if(sense==null){return 0;}
		int count = 0;
		if(senseCounts.containsKey(sense.toString())){
			return senseCounts.get(sense.toString());
		}
		count+= sense.getWords().size();//.getWords().size();

		for(PointerTargetNode child: PointerUtils.getDirectHyponyms(sense))
		{
			count = count + getCount(child.getSynset());
		}
		senseCounts.put(sense.toString(), count);
		return count;
	}
	
	private static Map<String,Boolean> termsInSense;
	private static boolean checkSenseContainsWord(Synset sense, IndexWord curWord)throws Exception{
		if(sense==null){return false;}
		String curConcat = sense.getGloss()+curWord;
		if(termsInSense.containsKey(curConcat)){return termsInSense.get(curConcat);}
		if(sense.containsWord(curWord.getLemma())){termsInSense.put(curConcat, true);return true;}
		//otherwise check hypnonyms for the word
		//otherwise use find relationsShip
		PointerTargetTree hypnonymTree =PointerUtils.getHyponymTree(sense,8);
		//now see if any sense of index word in tree
		for(int i=0;i<curWord.getSenses().size()&&i<FODEH_MAX_SENSE_CONSIDERED;i++){//Synset wordSense:curWord.getSenses()){
			Synset wordSense = curWord.getSenses().get(i);
			/*
			RelationshipList temp = RelationshipFinder.findRelationships(sense, wordSense, PointerType.HYPONYM,4);
			if(temp.size()!=0){
				termsInSense.put(curConcat, true);
				return true;
			}
			*/
			for(PointerTarget target:wordSense.getTargets(PointerType.HYPONYM)){//INSTANCES_HYPONYM)){//.getTargets()){
				PointerTargetTreeNode temp =hypnonymTree.findFirst(target);
				if(temp!=null){
					termsInSense.put(curConcat, true);
					return true;
				}
			}
		}
		termsInSense.put(curConcat, false);
		return false;
		
	}
	
	private static boolean checkSenseContainsWord(List<Synset> senses, IndexWord curWord)throws Exception{
		if(senses==null){return false;}
		for(Synset sense:senses){
			if(sense.containsWord(curWord.getLemma())){return true;}
		}
		return false;		
	}
	
	private static int getCount(List<Synset> senses){
		int count =0;
		for(Synset sense:senses){
			count+=sense.getWords().size();
		}
		return count;
	}
	
	private static int getCount(List<String> wordsInSet, Map<String,IndexWord> wordMap){
		int count=0;
		for(String key:wordsInSet){
			count+=wordMap.get(key).getSenses().size();
		}
		return count;
	}
	/*
	private static Map<Synset,ArrayList<Integer>> checkScore(PointerTargetTreeNode hypernymNode,int currentTerm, int totalTerms,
												float initScore,ArrayList<String> termsToConsider, Map<String,Integer> termCount,Map<String,IndexWord> wordMap, IndexSearcher searcher,int depth)throws Exception{
		//first computer score of current meaning
		Synset sense = hypernymNode.getSynset();
		//sadly need hyponymTree to determine count of applicable terms
		Map<Synset,ArrayList<Integer>> replacement=null;// = new HashMap<Synset,List<Integer>>();
		if(sense!=null){
			ArrayList<Integer> tempWords=new ArrayList<Integer>();	
			List<String> tempList = new ArrayList<String>();	
			tempList.add(termsToConsider.get(currentTerm));
			float curScore=initScore;
			for(int curWord=0;curWord<termsToConsider.size();curWord++){
				if(curWord==currentTerm){continue;}
				String curTerm = termsToConsider.get(curWord);
				boolean inSense = checkSenseContainsWord(sense,wordMap.get(curTerm));
				if(inSense){
					//curScore+=termCount.get(curTerm);
					tempWords.add(curWord);
					tempList.add(curTerm);
				}
			}
			curScore = termFrequency(searcher,tempList);
			//now divide score by number of senses
			//totalTerms+=sense.getWords().size();
			int meanings = getCount(sense);
			curScore = curScore/meanings; //totalTerms;
			if(curScore>bestScore){
				bestScore = curScore;
				replacement = new HashMap<Synset,ArrayList<Integer>>();
				replacement.put(sense,tempWords);
			}
			//do breadth first
			if(depth<2){
				Map<Synset,ArrayList<Integer>> temp = new HashMap<Synset,ArrayList<Integer>>();
				for(PointerTargetTreeNode child : hypernymNode.getChildTreeList()){
					temp = checkScore(child,currentTerm,totalTerms,initScore,
												 termsToConsider,termCount,wordMap,searcher,depth+1);
					//if noon empty map returned we know score has been beat
					if(temp!=null&&temp.keySet().size()>0){
						if(replacement!=null){replacement.clear();}
						replacement = temp;
					}
				}
			}
		}
		return replacement;
	}*/
	static int numScoresChecked =0;
	private static Map<Synset,ArrayList<Integer>> checkScore(PointerTargetTreeNode hypernymNode,int currentTerm, int totalTerms,float initScore,ArrayList<String> termsToConsider, 
															 Map<String,Integer> termCount,Map<String,IndexWord> wordMap,boolean[] indicesInSet, IndexSearcher searcher,List<Synset> wordsInSet,int depth,int SanitizationGeneration)throws Exception{
		if(wordsInSet==null){wordsInSet=new ArrayList<Synset>();}
		numScoresChecked++;
		//first computer score of current meaning
		Synset sense = hypernymNode.getSynset();

		if(!wordsInSet.contains(sense)){
			wordsInSet.add(sense);
		}
		for(PointerTargetNode child:PointerUtils.getDirectHyponyms(sense)){
			if(!wordsInSet.contains(child.getSynset())){
				wordsInSet.add(child.getSynset());
			}
		}
		
		//sadly need hyponymTree to determine count of applicable terms
		Map<Synset,ArrayList<Integer>> replacement=null;// = new HashMap<Synset,List<Integer>>();
		boolean bestScoreChanged=false;
		if(sense!=null){
			if(SanitizationGeneration==-1||SanitizationGeneration==depth+1){
				List<String> tempList = new ArrayList<String>();
				ArrayList<Integer> tempWords=new ArrayList<Integer>();		
				float curScore=initScore;

				tempList.add(termsToConsider.get(currentTerm));
				for(int curWord=0;curWord<termsToConsider.size();curWord++){
					if(curWord==currentTerm){continue;}
					String curTerm = termsToConsider.get(curWord);
					boolean inSense = indicesInSet[curWord];
					if(!inSense&&checkSenseContainsWord(wordsInSet,wordMap.get(curTerm))){
						indicesInSet[curWord] = true;
						inSense=true;
					}
					if(curWord!=currentTerm&&inSense){
						//curScore+=termCount.get(curTerm);
						tempWords.add(curWord);
						tempList.add(curTerm);
					}
				}
				curScore = termFrequency(searcher,tempList);
				//now divide score by number of senses
				//totalTerms+=sense.getWords().size();
				int meanings = getCount(tempList,wordMap);//getCount(wordsInSet);//getUniqueSenses(tempList,wordMap);//wordsInSet.size();//
				curScore = curScore/(meanings); //totalTerms;
				if(curScore>bestScore){
					bestScore = curScore;
					replacement = new HashMap<Synset,ArrayList<Integer>>();
					replacement.put(sense,tempWords);
					bestScoreChanged=true;		
				}
			}
			//if we aren't to a specified fixed depth yet, or if doing SAR and depth less than max and current path beat previous best
			if((depth+1!=SanitizationGeneration&&SanitizationGeneration!=-1)||(bestScoreChanged&&SanitizationGeneration==-1&&depth<2&&replacement!=null)){
				Map<Synset,ArrayList<Integer>> temp = new HashMap<Synset,ArrayList<Integer>>();
				for(PointerTargetTreeNode child : hypernymNode.getChildTreeList()){
					temp = checkScore(child,currentTerm,totalTerms,initScore,
												 termsToConsider,termCount,wordMap,indicesInSet,searcher,wordsInSet,depth+1,SanitizationGeneration);
					//if non empty map returned we know score has been beat
					if(temp!=null&&temp.keySet().size()>0){
						if(replacement!=null){replacement.clear();}
						replacement = temp;
					}
				}
			}
		}
		return replacement;
	}

	private static Map<Synset,ArrayList<Integer>> checkScore(PointerTargetTreeNode hypernymNode,int currentTerm, int totalTerms,float initScore,ArrayList<String> termsToConsider, 
			 Map<String,Integer> termCount,Map<String,IndexWord> wordMap, IndexSearcher searcher,int depth,int SanitizationGeneration)throws Exception{
		//first computer score of current meaning
		Synset sense = hypernymNode.getSynset();
		//sadly need hyponymTree to determine count of applicable terms
		Map<Synset,ArrayList<Integer>> replacement=null;// = new HashMap<Synset,List<Integer>>();
		if(sense!=null){
			if(depth+1!=SanitizationGeneration&&SanitizationGeneration!=-1){//||(SanitizationGeneration==-1&&depth<2)){
				Map<Synset,ArrayList<Integer>> temp = new HashMap<Synset,ArrayList<Integer>>();
				for(PointerTargetTreeNode child : hypernymNode.getChildTreeList()){
					temp = checkScore(child,currentTerm,totalTerms,initScore,
									  termsToConsider,termCount,wordMap,searcher,depth+1,SanitizationGeneration);
					//if noon empty map returned we know score has been beat
					if(temp!=null&&temp.keySet().size()>0){
						if(replacement!=null){replacement.clear();}
						replacement = temp;
					}
				}
			}
			if((SanitizationGeneration==-1&&depth<2)||SanitizationGeneration==depth+1){
				List<String> tempList = new ArrayList<String>();
				ArrayList<Integer> tempWords=new ArrayList<Integer>();		
				float curScore=initScore;

				tempList.add(termsToConsider.get(currentTerm));
				for(int curWord=0;curWord<termsToConsider.size();curWord++){
					if(curWord==currentTerm){continue;}
					String curTerm = termsToConsider.get(curWord);
					boolean inSense = checkSenseContainsWord(sense,wordMap.get(curTerm));
					if(curWord!=currentTerm&&inSense){
						//curScore+=termCount.get(curTerm);
						tempWords.add(curWord);
						tempList.add(curTerm);
					}
				}
				curScore = termFrequency(searcher,tempList);
				//now divide score by number of senses
				//totalTerms+=sense.getWords().size();
				int meanings =getCount(sense);// getUniqueSenses(tempList,wordMap);//
				curScore = curScore/meanings; //totalTerms;
				if(curScore>bestScore){
					bestScore = curScore;
					replacement = new HashMap<Synset,ArrayList<Integer>>();
					replacement.put(sense,tempWords);
					//now for our approach search here for new best
					if(SanitizationGeneration==-1&&depth<2){
						Map<Synset,ArrayList<Integer>> temp = new HashMap<Synset,ArrayList<Integer>>();
						for(PointerTargetTreeNode child : hypernymNode.getChildTreeList()){
							temp = checkScore(child,currentTerm,totalTerms,initScore,
														 termsToConsider,termCount,wordMap,searcher,depth+1,SanitizationGeneration);
							//if noon empty map returned we know score has been beat
							if(temp!=null&&temp.keySet().size()>0){
								if(replacement!=null){replacement.clear();}
								replacement = temp;
							}
						}
					}
				}
			}
			//do breadth first
		}
		return replacement;
	}
	//lucene methods to perform text sanitization from index
	//indexPath is directory containing generated index
	//this method is nearly identical to SAR except we we add a concept for each term
	//that applies to the most terms in the document, only check given levels of concepts.
	public static void HOTHOFromIndex(Path indexPath,Path newIndex,Path dataSet,int minFreq,int maxFreq)throws Exception{
		long startTime = System.currentTimeMillis();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		File source = new File(dataSet.toString());
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		Map<String,IndexWord> wordMap = new HashMap<String,IndexWord>();
		int count=0;
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
				wordMap.put(curTerm, word);
			}
			term=it.next();
			count++;
		}
		System.out.println("Originally had "+count);
		//todo consider different data structure for faster removal
		System.out.println("Start with "+termsToConsider.size());
		Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		//todo consider different data structure for faster removal
		for(int i=0;i<termsToConsider.size();i++){
			//now 0 is lowest possible score
			float bestScore =0;
			float initScore = termCount(searcher,termsToConsider.get(i));
			Synset replacingSense=null;
			IndexWord word = wordMap.get(termsToConsider.get(i));
			//in this method we simply use the first closest sense
			Synset sense = word.getSenses().get(0);
			if(wordsUnderTerms.containsKey(sense.getGloss())){
				wordsUnderTerms.get(sense.getGloss()).add(termsToConsider.get(i));
			}else{
				ArrayList<String> consolidatedWords=new ArrayList<String>();
				//List<String> tempWords = new ArrayList<String>();
				consolidatedWords.add(termsToConsider.get(i));
				wordsUnderTerms.put(sense.getGloss(), consolidatedWords);
			}
		}
		long endTime = System.currentTimeMillis();
		writeTime(HothoIndex,endTime-startTime);
		SPTNFileMapping hothoMapping = new SPTNFileMapping(wordsUnderTerms);
		hothoMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,HothoIndex),false);
		Terms titles =MultiFields.getTerms(reader, TITLE);
		hothoMapping.generateCSV(MappingDir+File.separator+Hothocsv, searcher, titles,minFreq,maxFreq,USE_TF_IDF);
		if(COMPUTE_LSI){
			long lsiTime =hothoMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+Hothocsv, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+HothoIndex,endTime-startTime+lsiTime);
		}
		return;		
	}
	
	public static boolean checkForRelationShip(String termOne, String termTwo,Map<String,IndexWord> wordMap)throws Exception{
		IndexWord wordTwo = wordMap.get(termTwo);
		for(Synset sense: wordMap.get(termOne).getSenses()){
			//otherwise check hypnonyms for the word
			List<PointerTarget> pointers = sense.getTargets();
			PointerTargetTree hypnonymTree = PointerUtils.getHyponymTree(sense);
			PointerTargetTree hypernymTree = PointerUtils.getHypernymTree(sense);
			//now see if any sense of index word in tree
			for(Synset wordSense:wordTwo.getSenses()){
				for(PointerTarget target:wordSense.getTargets()){
					PointerTargetTreeNode temp =hypnonymTree.findFirst(target);
					if(temp!=null){
						return true;
					}
					temp =hypernymTree.findFirst(target);
					if(temp!=null){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	
	public static void RecuperoFromIndex(Path indexPath,Path newIndex,Path dataSet,int minFreq,int maxFreq)throws Exception{

		long startTime = System.currentTimeMillis();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		senseDistances = new HashMap<String,Integer>();
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		File source = new File(dataSet.toString());
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		Map<String, IndexWord> wordMap = new HashMap<String,IndexWord>();
		int count=0;
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			if(word==null||word.getSenses()==null){word = morph.lookupBaseForm(POS.VERB, curTerm);}
			int freq = termCount(searcher,curTerm);
			if( word!=null&&word.getSenses()!=null
				&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
				wordMap.put(curTerm, word);
			}
			term=it.next();
			count++;
		}
		Map<String,ArrayList<Integer>> possibleLexCategories = new HashMap<String,ArrayList<Integer>>();
		//Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		Map<Integer,ArrayList<String>> wordsInGroup = new HashMap<Integer,ArrayList<String>>();
		List<Integer> groupKeySet = new ArrayList<Integer>();
		//todo consider different data structure for faster removal
		System.out.println("Recupero "+termsToConsider.size()+" terms");
		for(int i=0;i<termsToConsider.size();i++){
			String curTerm = termsToConsider.get(i);
			//first we just get all possible lex categories for a dataset
			ArrayList<Integer> sets = new ArrayList<Integer>();
			IndexWord word = wordMap.get(termsToConsider.get(i));
			boolean addedToSet = false;
			for(Integer keys:wordsInGroup.keySet()){
				//if current term is a hypernym or hyponym of every other term in set it gets added to set
				ArrayList<String> termsToTestForSimilarity = wordsInGroup.get(keys);
				boolean potentiallyRelated = true;
				for(int k=0;k<termsToTestForSimilarity.size()&& potentiallyRelated;k++){
					potentiallyRelated = checkForRelationShip(curTerm,termsToTestForSimilarity.get(k),wordMap);
				}
				if(potentiallyRelated){
					wordsInGroup.get(keys).add(curTerm);
					sets.add(keys);
					addedToSet = true;
				}
			}
			if(!addedToSet){
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(curTerm);
				wordsInGroup.put(wordsInGroup.keySet().size(), temp);
				groupKeySet.add(wordsInGroup.keySet().size()-1);
				sets.add(wordsInGroup.keySet().size()-1);
			}
			possibleLexCategories.put(termsToConsider.get(i), sets);
		}
		double minMergePercent = .5;
		//now we have to make sure each word under term goes to only one lex file based
		//first we merge generalizations with 50% or more in common
		for(int i=0;i<groupKeySet.size();i++){//String key:wordsUnderTerms.keySet()){
			List<String> currentBaseTerms = wordsInGroup.get(groupKeySet.get(i));
			for(int k=0;k<groupKeySet.size();k++){//String keyToCompare:wordsUnderTerms.keySet()){
				if(i==k){continue;}
				//get terms in common from both sets
				List<String> baseTermsToCompare = wordsInGroup.get(groupKeySet.get(k));
				//now get count in common
				List<String> elementsNotInFirst = new ArrayList<String>();
				for(String toCompare:baseTermsToCompare){
					if(!currentBaseTerms.contains(toCompare)){elementsNotInFirst.add(toCompare);}
				}
				float numinCommon = baseTermsToCompare.size()-elementsNotInFirst.size();
				if(numinCommon/baseTermsToCompare.size()>minMergePercent||numinCommon/currentBaseTerms.size()>minMergePercent){
					//add unique terms from second set before moving
					for(String toAdd:elementsNotInFirst){
						currentBaseTerms.add(toAdd);
					}
					//now update possibleLexCategories to remove previous keyword
					for(String word:termsToConsider){
						ArrayList<Integer> temp = possibleLexCategories.get(word);
						if(temp.contains(Integer.valueOf(groupKeySet.get(k)))){
							temp.remove(Integer.valueOf(groupKeySet.get(k)));
						}
						if(!temp.contains(Integer.valueOf(groupKeySet.get(i)))){
							temp.add(Integer.valueOf(groupKeySet.get(i)));
						}
					}
					wordsInGroup.remove(Integer.valueOf(groupKeySet.get(k)));
					groupKeySet.remove(k);
					k--;
				}
			}
		}
		//then for words with multiple lex files we choose the best one based on hop distance to other words in the set
		for(String origWord:termsToConsider){//possibleLexCategories.keySet()){
			ArrayList<Integer> possibleLexes = possibleLexCategories.get(origWord);
			if(possibleLexes.size()<=1){continue;}
			double bestScore = Double.MAX_VALUE;
			int bestLex = -1;
			Synset currentSynset = wordMap.get(origWord).getSenses().get(0);
			//otherwise find best average and choose that one
			for(int possibleLex:possibleLexes){
				//need to get list of fellow synset to compare to
				List<Synset> fellowSynses = new ArrayList<Synset>();
				for(String fellowSense:wordsInGroup.get(possibleLex)){
					if(!fellowSense.equals(origWord)){
						fellowSynses.add(wordMap.get(fellowSense).getSenses().get(0));
					}
				}
				double avgDistance = avgDistance(currentSynset,fellowSynses);
				if(avgDistance<bestScore){
					bestScore = avgDistance;
					bestLex = possibleLex;
				}
			}
			//now remove extra occurences of word from generalized term
			List<Integer> lexesToRemove = new ArrayList<Integer>();
			for(int possibleLex:possibleLexes){
				if(possibleLex ==bestLex){continue;}
				wordsInGroup.get(possibleLex).remove(origWord);
				lexesToRemove.add(possibleLex);
			}
			for(int possibleLex:lexesToRemove){
				possibleLexes.remove(Integer.valueOf(possibleLex));
			}
		}
		//now convert for sptnfilemapping 
		Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		for(int val:wordsInGroup.keySet()){
			wordsUnderTerms.put(Integer.toString(val), wordsInGroup.get(val));
		}

		long endTime = System.currentTimeMillis();
		writeTime(RecuperoIndex,endTime-startTime);
		SPTNFileMapping recuperoMapping = new SPTNFileMapping(wordsUnderTerms);
		recuperoMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,RecuperoIndex),false);
		Terms titles =MultiFields.getTerms(reader, TITLE);
		recuperoMapping.generateCSV(MappingDir+File.separator+Recuperocsv, searcher, titles,minFreq,maxFreq,USE_TF_IDF);
		if(COMPUTE_LSI){
			long lsiTime = recuperoMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+Recuperocsv, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+RecuperoIndex,endTime-startTime+lsiTime);
		}
		senseDistances.clear();
		return;		
	}

	private static Map<String,Integer> senseDistances;
	private final static int FODEH_MAX_SENSE_CONSIDERED=3;
	//compare a synset to a list of synset and return minimum distnace using jwnl relationship finder
	public static int minDistance(Synset from, List<Synset> optionsTo)throws Exception{
		int bestDistance = Integer.MAX_VALUE;
		for(int i=0;i<optionsTo.size()&&i<FODEH_MAX_SENSE_CONSIDERED;i++){
			int curDistance = Integer.MAX_VALUE;
			String curConcat = from.toString()+optionsTo.get(i).toString();
			//if(senseDistances.containsKey(curConcat)){
				//curDistance = senseDistances.get(curConcat);
			//}else{
			RelationshipList list = RelationshipFinder.findRelationships(from, optionsTo.get(i), PointerType.HYPERNYM);
			curDistance = list.getShallowest().getDepth();
				//senseDistances.put(curConcat, curDistance);
			//}
			if(curDistance<bestDistance){
				bestDistance=curDistance;
			}
		}
		return bestDistance;
	}
	
	//compute average hops for from comparedTo every item in to
	private final static int MAX_DISTANCE = 10;
	public static float avgDistance(Synset from, List<Synset> optionsTo)throws Exception{
		float avgDistance = 0;
		for(int i=0;i<optionsTo.size()&&i<FODEH_MAX_SENSE_CONSIDERED;i++){
			int curDistance = Integer.MAX_VALUE;
			String curConcat = from.toString()+optionsTo.get(i).toString();
			RelationshipList list = RelationshipFinder.findRelationships(from, optionsTo.get(i), PointerType.HYPERNYM);
			if(list ==null||list.size()==0){list = RelationshipFinder.findRelationships(from, optionsTo.get(i), PointerType.HYPONYM);}
			if(list==null||list.size()==0){list = RelationshipFinder.findRelationships(from, optionsTo.get(i), PointerType.ANTONYM);}
			if(list!=null&&list.size()!=0){
				if(list.getShallowest()==null){
					curDistance = list.get(0).getDepth();
				}else{
					curDistance = list.getShallowest().getDepth();
				}
			}else{curDistance = MAX_DISTANCE;}
			avgDistance+=curDistance;
		}
		avgDistance = avgDistance/optionsTo.size();
		return avgDistance;
	}

	//lucene methods to perform text sanitization from index
	//indexPath is directory containing generated index
	//this method is nearly identical to SAR except we we add a concept for each term
	//that applies to the most terms in the document, only check given levels of concepts.
	public static String PartialFodehFromIndex(Path indexPath,Path newIndex,Path dataSet,int minFreq,int maxFreq,String curDoc,int termsBeforeSaving)throws Exception{
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		
		//Set<String> termsUsed
		Map<String,List<Synset>> wordSenses = new HashMap<String,List<Synset>>();
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
				wordSenses.put(curTerm, word.getSenses());
			}
			term=it.next();
		}
		//Set<String> termsUsed = new HashSet<String>();
		//ArrayList<Synset> addedSenses = new ArrayList<Synset>();
		//Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		//todo consider different data structure for faster removal
		//now perform fodehs algorithm for each document
		Terms titles =MultiFields.getTerms(reader, TITLE);
		it = titles.iterator();
		term = it.next();
		int count =0;
		Map<String,Map<String,List<String>>> docSanitizations = new HashMap<String,Map<String,List<String>>>();
		String tempString = term.utf8ToString();
		if(curDoc!=null){
			while(term!=null&&!curDoc.equals(tempString)){term = it.next(); tempString = term.utf8ToString();}
		}
		while(term!=null&&count<termsBeforeSaving){
			Map<String,List<String>> wordMap = new HashMap<String,List<String>>();
			curDoc = term.utf8ToString();
			boolean[] termsPresent = new boolean[termsToConsider.size()];
			//first bfigure out which terms current document has
			for(int i=0;i<termsToConsider.size();i++){
				int docCount  = termCount(searcher,termsToConsider.get(i),curDoc);
				if(docCount>0){termsPresent[i]=true;}
				else{termsPresent[i]=false;}
			}
			//now build the word map for each term using formula described in paper
			for(int i=0;i<termsToConsider.size();i++){
				if(!termsPresent[i]){continue;}
				//have to iterate through all other terms and get a score
				List<Synset> curSenses = wordSenses.get(termsToConsider.get(i));
				int bestSenseScore = Integer.MAX_VALUE;
				int bestSenseIndex = -1;
				for(int k=0;k<curSenses.size()&&k<FODEH_MAX_SENSE_CONSIDERED;k++){
					//get distance from every other sense and compare
					//want to replace with the minimum sense
					int curSenseScore = 0;
					for(int j=0;j<termsToConsider.size();j++){
						if(j==i||!termsPresent[j]){continue;}
						//want to minimize distances to other terms
						//so add min distance to list of sense for other word
						int curDistance = minDistance(curSenses.get(k),wordSenses.get(termsToConsider.get(j)));
						//if there was no path max value returned so throw that out
						if(curDistance!=Integer.MAX_VALUE){curSenseScore += curDistance;}
						else{System.out.println("No path");}
					}
					if(curSenseScore<=bestSenseScore){
						bestSenseScore = curSenseScore;
						bestSenseIndex = k;
					}
				}
				//first make sure current list of terms has the newly selected sense
				String newTerm = curSenses.get(bestSenseIndex).getGloss();
				//if(!termsUsed.contains(newTerm)){termsUsed.add(newTerm);}
				//now either add it to this documents file mappings or add to the list if sense already used
				if(wordMap.containsKey(newTerm)){wordMap.get(newTerm).add(termsToConsider.get(i));}
				else{
					List<String> temp = new ArrayList<String>();
					temp.add(termsToConsider.get(i));
					wordMap.put(newTerm, temp);
				}
			}
			//now add word mappings to doc mapping set
			docSanitizations.put(curDoc, wordMap);
			term = it.next();
			//write docs one term at a time
			count++;
		}
		//now add new mapping info
		FodehFileMapping fodehMapping = new FodehFileMapping(docSanitizations);
		fodehMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,FodehIndex),true);
		docSanitizations.clear();
		//senseDistances.clear();
		wordSenses.clear();
		if(count==termsBeforeSaving&&term!=null){
			curDoc = term.utf8ToString();
			System.out.println("End with "+curDoc);
			return curDoc;
		}
		return null;
	}
	
	
	public static boolean FodehFromIndexGreedy(Path indexPath,Path newIndex,Path dataSet,int minFreq,int maxFreq,String termsPath,int termsToDo)throws Exception{
		
		long startTime = System.currentTimeMillis();
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		
		//Set<String> termsUsed
		Map<String,List<Synset>> wordSenses = new HashMap<String,List<Synset>>();
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider = readTerms(termsPath);//getNouns=new ArrayList<String>();
		for(String curTerm:termsToConsider){

			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				wordSenses.put(curTerm, word.getSenses());
			}
		}
		//Set<String> termsUsed = new HashSet<String>();
		//todo consider different data structure for faster removal
		//ArrayList<Synset> addedSenses = new ArrayList<Synset>();
		//Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		//todo consider different data structure for faster removal
		//now perform fodehs algorithm for each document
		Map<String,ArrayList<String>> generalizedTerms = new HashMap<String,ArrayList<String>>();
		for(int i=0;termsToConsider.size()!=0&&i<termsToDo;i++){
			//have to iterate through all other terms and get a score
			List<Synset> curSenses = wordSenses.get(termsToConsider.get(0));
			int bestSenseScore = Integer.MAX_VALUE;
			int bestSenseIndex = -1;
			for(int k=0;k<curSenses.size()&&k<FODEH_MAX_SENSE_CONSIDERED;k++){
				//get distance from every other sense and compare
				//want to replace with the minimum sense
				int curSenseScore = 0;
				for(int j=1;j<termsToConsider.size();j++){
					//want to minimize distances to other terms
					//so add min distance to list of sense for other word
					int curDistance = minDistance(curSenses.get(k),wordSenses.get(termsToConsider.get(j)));
					//if there was no path max value returned so throw that out
					if(curDistance!=Integer.MAX_VALUE){curSenseScore += curDistance;}
					else{System.out.println("No path");}
				}
				if(curSenseScore<=bestSenseScore){
					bestSenseScore = curSenseScore;
					bestSenseIndex = k;
				}
			}
			//first make sure current list of terms has the newly selected sense
			String newTerm = curSenses.get(bestSenseIndex).toString();
			//if(!termsUsed.contains(newTerm)){termsUsed.add(newTerm);}
			//now either add it to this documents file mappings or add to the list if sense already used
			if(generalizedTerms.containsKey(newTerm)){generalizedTerms.get(newTerm).add(termsToConsider.get(0));}
			else{
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(termsToConsider.get(0));
				generalizedTerms.put(newTerm, temp);
			}
			termsToConsider.remove(0);
		}
		senseDistances.clear();
		//now add new mapping info
		SPTNFileMapping fodehMapping = new SPTNFileMapping(generalizedTerms);
		fodehMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,FodehIndex),true);
		writeTerms(termsToConsider,termsPath);
		return termsToConsider.size()==0;
	}
	
	//lucene methods to perform text sanitization from index
	//indexPath is directory containing generated index
	//this method is nearly identical to SAR except we we add a concept for each term
	//that applies to the most terms in the document, only check given levels of concepts.
	public static void FodehFromIndex(Path indexPath,Path newIndex,Path dataSet,int minFreq,int maxFreq)throws Exception{
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(indexPath),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		long startTime = System.currentTimeMillis();
		//now run the generalization algorithm on all terms
		Dictionary dictionary = Dictionary.getDefaultResourceInstance();
		MorphologicalProcessor morph = dictionary.getMorphologicalProcessor();
		//Dictionary dictionary = configureJWordNet();// Dictionary.getInstance(new FileInputStream("Properties.xml"));
		
		Map<String,Map<String,List<String>>> docSanitizations = new HashMap<String,Map<String,List<String>>>();

		Map<String,List<Synset>> wordSenses = new HashMap<String,List<Synset>>();
		Terms allTerms =MultiFields.getTerms(reader, CONTENT);
		TermsEnum it = allTerms.iterator();
		BytesRef term = it.next();
		ArrayList<String> termsToConsider=new ArrayList<String>();
		while(term!=null){
			String curTerm = term.utf8ToString();
			IndexWord word = morph.lookupBaseForm(POS.NOUN,curTerm);
			int freq = termCount(searcher,curTerm);
			if(word!=null&&word.getSenses()!=null&&freq>minFreq&&freq<maxFreq){
				termsToConsider.add(curTerm);
				wordSenses.put(curTerm, word.getSenses());
			}
			term=it.next();
		}
		Set<String> termsUsed = new HashSet<String>();
		//todo consider different data structure for faster removal
		System.out.println("Start with "+termsToConsider.size());
		//ArrayList<Synset> addedSenses = new ArrayList<Synset>();
		//Map<String,ArrayList<String>> wordsUnderTerms = new HashMap<String,ArrayList<String>>();
		//todo consider different data structure for faster removal
		//now perform fodehs algorithm for each document
		Map<String,List<String>> wordMap = new HashMap<String,List<String>>();
		Terms titles =MultiFields.getTerms(reader, TITLE);
		it = titles.iterator();
		term = it.next();
		while(term!=null){
			
			String curDoc = term.utf8ToString();
			boolean[] termsPresent = new boolean[termsToConsider.size()];
			//first bfigure out which terms current document has
			for(int i=0;i<termsToConsider.size();i++){
				int docCount  = termCount(searcher,termsToConsider.get(i),curDoc);
				if(docCount>0){termsPresent[i]=true;}
				else{termsPresent[i]=false;}
			}
			//now build the word map for each term using formula described in paper
			for(int i=0;i<termsToConsider.size();i++){
				if(!termsPresent[i]){continue;}
				//have to iterate through all other terms and get a score
				List<Synset> curSenses = wordSenses.get(termsToConsider.get(i));
				int bestSenseScore = Integer.MAX_VALUE;
				int bestSenseIndex = -1;
				for(int k=0;k<curSenses.size()&&k<FODEH_MAX_SENSE_CONSIDERED;k++){
					//get distance from every other sense and compare
					//want to replace with the minimum sense
					int curSenseScore = 0;
					for(int j=0;j<termsToConsider.size();j++){
						if(j==i){continue;}
						//want to minimize distances to other terms
						//so add min distance to list of sense for other word
						int curDistance = minDistance(curSenses.get(k),wordSenses.get(termsToConsider.get(j)));
						//if there was no path max value returned so throw that out
						if(curDistance!=Integer.MAX_VALUE){curSenseScore += curDistance;}
						else{System.out.println("No path");}
					}
					if(curSenseScore<=bestSenseScore){
						bestSenseScore = curSenseScore;
						bestSenseIndex = k;
					}
				}
				//first make sure current list of terms has the newly selected sense
				String newTerm = curSenses.get(bestSenseIndex).toString();
				if(!termsUsed.contains(newTerm)){termsUsed.add(newTerm);}
				//now either add it to this documents file mappings or add to the list if sense already used
				if(wordMap.containsKey(newTerm)){wordMap.get(newTerm).add(termsToConsider.get(i));}
				else{
					List<String> temp = new ArrayList<String>();
					temp.add(termsToConsider.get(i));
					wordMap.put(newTerm, temp);
				}
			}
			//now add word mappings to doc mapping set
			docSanitizations.put(curDoc, wordMap);
			term = it.next();
		}
		senseDistances.clear();

		long endTime = System.currentTimeMillis();
		FodehFileMapping fodehMapping = new FodehFileMapping(docSanitizations);
		fodehMapping.saveMapping(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,FodehIndex),false);
		titles =MultiFields.getTerms(reader, TITLE);
		fodehMapping.generateCSV(Fodehcsv, searcher, titles,minFreq,maxFreq,USE_TF_IDF);
		if(COMPUTE_LSI){
			long lsiTime =fodehMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+Fodehcsv, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+FodehIndex,endTime-startTime+lsiTime);
		}
		System.out.println("End with "+termsToConsider.size());
		return;		
	}
	/////////////////lucene method end////////////////////////////
	
	public static void partialSarExperiment(int minFreq, int maxFreq, int generationCount, boolean greedy)throws Exception{
		indexNouns(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),
				 minFreq,maxFreq,Paths.get(NounIndex).toString());
		String curPrecedingCharacter = "";
		String curIndex = SARIndex;
		String curCSV = SARcsv;
		if(greedy){
			curIndex = SARGreedyIndex;
			curCSV = SARGreedycsv;
		}
		if(generationCount!=-1){curPrecedingCharacter = generationCount+"";}
		FileWriter junk = new FileWriter(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curPrecedingCharacter+curIndex)).toString(),false);
		junk.close();
		long startTime = System.currentTimeMillis();
		//then do our sar method
		int startingIndex = 0;
		do{
			startingIndex = PartialSarFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),
						 	minFreq,maxFreq,startingIndex,30000,Paths.get(NounIndex).toString(),generationCount,greedy);
		}while(startingIndex!=0);
		
		long endTime = System.currentTimeMillis();
		writeTime(getCurIndexString(minFreq,maxFreq,curPrecedingCharacter+curIndex),endTime-startTime);
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(Paths.get(luceneIndex)),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		SPTNFileMapping sarMapping = new SPTNFileMapping(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curPrecedingCharacter+curIndex)).toString());
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv SAR"+sarMapping.generalizeToSpecificWords.keySet().size());
		sarMapping.generateCSV(MappingDir+File.separator+curPrecedingCharacter+curCSV, searcher, titles,minFreq,maxFreq,USE_TF_IDF);

		if(COMPUTE_LSI){
			long lsiTime = sarMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+curPrecedingCharacter+curCSV, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+curPrecedingCharacter+curIndex,endTime-startTime+lsiTime);
		}
	}
	
	public static void partialFodehExperiment(int minFreq, int maxFreq)throws Exception{
		indexNouns(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),
				 minFreq,maxFreq,Paths.get(NounIndex).toString());
		String curIndex = FodehIndex;
		String curCSV = Fodehcsv;
		FileWriter junk = new FileWriter(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curIndex)).toString(),false);
		junk.close();
		long startTime = System.currentTimeMillis();
		//then do our sar method
		String curDoc = null;
		do{
			curDoc = PartialFodehFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),
						 minFreq,maxFreq,curDoc,100);
		}while(curDoc!=null);
		
		long endTime = System.currentTimeMillis();
		writeTime(curIndex,endTime-startTime);
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(Paths.get(luceneIndex)),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		FodehFileMapping fodehMapping = new FodehFileMapping(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curIndex)).toString());
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv fodeh"+fodehMapping.generalizedWords.size());
		fodehMapping.generateCSV(MappingDir+File.separator+curCSV, searcher, titles,minFreq,maxFreq,USE_TF_IDF);

		if(COMPUTE_LSI){
			long lsiTime =fodehMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+curCSV, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+curIndex,endTime-startTime+lsiTime);
		}
	}
	
	public static void generateMappings(int minFreq,int maxFreq)throws Exception{
		generateIndex(Paths.get(DATADir),Paths.get(luceneIndex));
		for(int i=-1;i<=1;i++){
			if(i==0){continue;}
			//if(i!=-1){partialSarExperiment(minFreq,maxFreq,i,true);}
			partialSarExperiment(minFreq,maxFreq,i,true);
		}
		System.out.println("Recupero");
		RecuperoFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),minFreq,maxFreq);
		//System.out.println("Fodeh");
		//partialFodehExperiment(minFreq,maxFreq);
		//FodehFromIndexGreedy(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),minFreq,maxFreq);
		/*
		System.out.println("Hotho");
		HOTHOFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),minFreq,maxFreq);
		*/
		
		System.out.println("Baseline");
		BaseFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),minFreq,maxFreq);
		System.out.println("All");
		AllFromIndex(Paths.get(luceneIndex),Paths.get("generalizedIndex"),Paths.get(DATADir),minFreq,maxFreq);
	}
	
	public static void generateFodehCSV(String curIndex,String curCSV, int minFreq, int maxFreq,boolean lsi)throws Exception{
		RAMDirectory dic = new RAMDirectory(FSDirectory.open(Paths.get(luceneIndex)),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		System.out.println(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curIndex));
		FodehFileMapping fodehMapping = new FodehFileMapping(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curIndex)).toString());
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv "+fodehMapping.generalizedWords.size());
		fodehMapping.generateCSV(MappingDir+File.separator+curCSV, searcher, titles,minFreq,maxFreq,USE_TF_IDF);

		if(COMPUTE_LSI&&lsi){
			long lsiTime =fodehMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+curCSV, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+curIndex,lsiTime);
		}
	}

	public static void generateSPTNCSV(String curIndex,String curCSV, int minFreq, int maxFreq,boolean lsi)throws Exception{

		RAMDirectory dic = new RAMDirectory(FSDirectory.open(Paths.get(luceneIndex)),IOContext.DEFAULT);
		IndexReader reader = DirectoryReader.open(dic);//IndexReader.open(FSDirectory.open(indexPath));
		IndexSearcher searcher = new IndexSearcher(reader);
		SPTNFileMapping fodehMapping = new SPTNFileMapping(Paths.get(MappingDir+File.separator+getCurIndexString(minFreq,maxFreq,curIndex)).toString());
		Terms titles =MultiFields.getTerms(reader, TITLE);
		System.out.println("Generating csv "+getCurIndexString(minFreq,maxFreq,curIndex)+" "+fodehMapping.generalizeToSpecificWords.keySet().size());
		fodehMapping.generateCSV(MappingDir+File.separator+curCSV, searcher, titles,minFreq,maxFreq,USE_TF_IDF);

		if(COMPUTE_LSI&&lsi){
			long lsiTime = fodehMapping.generateLSI_CSV(MappingDir+File.separator+"LSI_"+curCSV, searcher, titles,minFreq,maxFreq,LSI_K,USE_TF_IDF);
			writeTime("LSI_"+curIndex,lsiTime);
		}
	}
	
	public static void generateCSVS(int minFreq,int maxFreq)throws Exception{
		generateIndex(Paths.get(DATADir),Paths.get(luceneIndex));
		for(int i=-1;i<=1;i++){

			if(i==0){continue;}
			String curPrecedingCharacter = "";
			String curIndex = SARGreedyIndex;
			String curCSV = SARGreedycsv;
			boolean lsi=false;
			if(i<=1){lsi=true;}
			if(i!=-1){curPrecedingCharacter = i+"";}
			//if(i!=-1){partialSarExperiment(minFreq,maxFreq,i,true);}
			generateSPTNCSV(curPrecedingCharacter+curIndex,curPrecedingCharacter+curCSV,minFreq,maxFreq,lsi);
		}
		System.out.println("Baseline");
		generateSPTNCSV(BaseIndex,Basecsv,minFreq,maxFreq,true);
		System.out.println("All");
		generateSPTNCSV(AllIndex,Allcsv,minFreq,maxFreq,false);
		System.out.println("Recupero");
		generateSPTNCSV(RecuperoIndex,Recuperocsv,minFreq,maxFreq,false);
		//System.out.println("Fodeh");
		//generateFodehCSV(FodehIndex,Fodehcsv,minFreq,maxFreq,false);
		System.out.println("Hotho");
		generateSPTNCSV(HothoIndex,Hothocsv,minFreq,maxFreq,false);
		
	}
	
	public static void writeTime(String method, long time)throws Exception{
		long secondTime = time/1000;
		BufferedWriter writer = new BufferedWriter(new FileWriter(timeFile,true));
		writer.write(method+"\t"+secondTime+"\n");
		writer.close();
	}
	
    public static void main( String[] args )
    {

		Properties prop = new Properties();
		InputStream input = null;
    	try{

			input = new FileInputStream("dataconfig.properties");
			prop.load(input);

    		int maxFreq = Integer.parseInt(prop.getProperty("maxFrequency"));
    		int minFreq = Integer.parseInt(prop.getProperty("minFrequency"));
    		COMPUTE_LSI = Integer.parseInt(prop.getProperty("useLSI"))==1;
    		LSI_K = Integer.parseInt(prop.getProperty("lsi_k"));
    		timeFile ="Min"+minFreq+"Max"+maxFreq+timeFile;
    		boolean useSynonomized = Integer.parseInt(prop.getProperty("synonomized"))==1;
    		if(useSynonomized){
    			//synonomizeDoc(Paths.get(DATADir),Paths.get(SynonomizedDATADir));
	    		DATADir = SynonomizedDATADir;
	    		timeFile = "Synonomized"+timeFile;
	    	}
    		senseCounts = new HashMap<String,Integer>();
    		senseDistances = new HashMap<String,Integer>();
    		termsInSense = new HashMap<String,Boolean>();
    		
    		generateMappings(minFreq,maxFreq);
    		generateCSVS(minFreq,maxFreq);
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}
	}
	
	public static Dictionary configureJWordNet() {
		// WARNING: This still does not work in Java 5!!!
		try {
			// initialize JWNL (this must be done before JWNL can be used)
			// See the JWordnet documentation for details on the properties file
			return Dictionary.getInstance(new FileInputStream("Properties.xml"));//C:/projects/jwnl/file_properties.xml"));
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
		return null;
	}
}
