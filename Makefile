CP := src
JAVAC := javac

# Trova tutti i file .java nella cartella src
SOURCES := $(wildcard $(CP)/**/*.java)

.PHONY: all clean

all: build

# Compila i file .java in .class
build:
	$(JAVAC) $(SOURCES) -sourcepath $(CP)

COMPILED := $(wildcard $(CP)/**/*.class)
clean:
	rm -f $(COMPILED)

