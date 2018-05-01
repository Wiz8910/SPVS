$varToChange = 'minFrequency'
$firstVarToChange = 'synonomized'
$vals =  '5','10','20'
iex('python .\docStemmer.py')
iex('python .\doc2VecGenerator.py')
iex ('java -jar SemanticsPreservingTextSanitization.jar -Xms8g')
iex('python .\kmeans.py')
for($i=0; $i -lt $vals.Count -1; $i=$i +1){
	$prev=$vals[$i]
	$next=$vals[$i+1]
	iex ('cscript replace.vbs ".\dataconfig.properties" "'+$varToChange+'=' + $prev + '" "'+$varToChange+'=' + $next + '"')
	iex ('cscript replace.vbs ".\python.ini" "'+$varToChange+':' + $prev + '" "'+$varToChange+':' + $next + '"')
	iex('python .\doc2VecGenerator.py')
	iex('python .\kmeans.py')
}

