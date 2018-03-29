BIN = bin/

# Directory of source files
SRC = src

STRUCTURE := $(shell find $(SRCDIR) -type d) 

CODEFILES := $(addsuffix /*,$(STRUCTURE))
CODEFILES := $(wildcard $(CODEFILES))

SRCFILES := $(filter %.java,$(CODEFILES))

output :
	javac -g -d $(BIN) $(SRCFILES)
	java -cp bin/ lispinterpreter.LispInterpreter
clean :
	rm -rf $(BIN)*
	rm output.txt