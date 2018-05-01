FEATURES = \
	TestAllMapping.csv \
	TestAllMapping.txt \
	TestBaseMapping.csv \
	TestBaseMapping.txt \
	TestHothoMapping.csv \
	TestHothoMapping.txt \
	TestSARMapping.csv \
	TestSARMapping.txt

LSI_FEATURE = TestLSIMapping.csv

all: run

# Prepare python virtual environment
venv: venv/bin/_ready_
venv/bin/_ready_: requirements.txt
	@test -d venv || python -m venv venv
	@xargs -L1 -n1 venv/bin/pip install -U < requirements.txt
	@touch venv/bin/_ready_

# Generate LSI feature space dependencies
document_listing.txt:
	find `pwd`/testDataSet -type f > document_listing.txt
feature.sspace: SSpace/sspace.jar document_listing.txt
	@java -Xmx8g -cp SSpace/sspace.jar edu.ucla.sspace.mains.LSAMain \
	-f document_listing.txt -F include=/usr/share/dict/words -o SPARSE_TEXT \
	feature.sspace
$(FEATURES): target/SPTN.jar
	@java -Xmx8g -jar $<

# Generate LSI feature space
$(LSI_FEATURE): venv feature.sspace $(FEATURES)
	@venv/bin/python KMeansClusterer/lsi.py

# Compile and produce java classes and jar (w/ dependencies)
target/SPTN.jar: pom.xml
	@mvn compile assembly:single
	mv target/sptn-jar-with-dependencies.jar target/SPTN.jar

# Install evaluation script
install: venv $(LSI_FEATURE)
	@venv/bin/python setup.py install --record .installed.txt

# Run evaluation script
run: install
	venv/bin/evaluate > results.csv
	@cat results.csv

# Remove all installed files
clean:
	-@rm -rf venv *.egg-info build dist document_listing.txt
	-@cat .installed.txt | xargs rm -rf
	-@mvn clean
clean/all: clean
	-@rm -rf $(FEATURES) $(LSI_FEATURE) feature.sspace
