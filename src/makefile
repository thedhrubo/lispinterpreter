BIN = bin/

# Directory of source files
SRC = src
EMPTY = 

STRUCTURE := $(shell find $(SRCDIR) -type d) 

CODEFILES := $(addsuffix /*,$(STRUCTURE))
CODEFILES := $(wildcard $(CODEFILES))

SRCFILES := $(filter %.java,$(CODEFILES))

output :
	javac -g -d $(BIN) $(SRCFILES)
	
clean :
	rm -rf $(BIN)*