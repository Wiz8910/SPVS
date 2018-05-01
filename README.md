# Semantics Preserving Text Sanitization

In order to run the gave program(which produces the csv files) run java -jar SemanticsPreservingTextSanitization.jar. This program assumes there is a testDataSet directory to perform process on. This directory should contain one directory for each class of files, with the class being the directory name. In addition, for the program to run correctly wordnet needs to be installed on the local system, can find here: [WordNet](https://wordnet.princeton.edu/wordnet/download/current-version/).  

If you want to run the included java files yourself, the dependencies(jwnl) are given in the pom file. To run the evaluation program, move the 4 csv files generated by the java program to the KmeansClusterer directory. Then run the python program(sci-kit is assumed to be installed). The python program assumes all 4 csv files are locally included and names are unchanged.  

## Using SPTS (Recommended for Windows Users)

Requirements*:

- `Python >=3.6`
- `Java >=8`
- `Maven >=4.0`

### Explicit Commands

```bash
$ git clone https://github.com/Wiz8910/SPTN.git
$ cd SPTN/SPTN
$ # Build dependencies for java and python
$ mvn compile assembly:single
$ pip install numpy
$ pip install gensim
$ pip install spherecluster
$ pip install -r requirements.txt
$ # Stem documents
$ python .\docStemmer.python
$ #generate doce2vec csv
$ python .\doc2VecGenerator.python
$ # Generate rest ofcsv matrices
$ java -jar SemanticsPreservingTextSanitization.jar
$ # perform spherical kmeans
$ python .\kmeans.py
$ # or you can simply run the Trial ps1 script to reproduce the experiment
```

### GNU Make Based Build (Recommended for *nix/macOS Users)

Requirements*:

- `GNU Make >=3.8`

```bash
$ cd SPTN
$ # Option 1:
$ #   Installs dependencies and executes evaluation
$ make
$ # Option 2:
$ #   Installs dependencies, then executes evaluation
$ make install
$ make run
$ # Option 3:
$ #   Installs dependencies and executes evaluation using multiple threads
$ make -j
$ # Option 4:
$ make install
$ venv/bin/evaluate
$ #
$ # Once finished with evalutations run:
$ make clean
$ # which removes all executables which were built
$ # -- OR --
$ make clean/all
$ # which removes all executables which were built AND all csv matrices
```

(*) -- Not thoroughly tested with other versions of these tools.

## Authors

Adam Bowers and Quincy Conduff

