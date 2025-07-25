CP := src
BUILD_DIR := bin
JAVAC := javac

# Trova tutti i file .java nella cartella src
SOURCES := $(wildcard $(CP)/**/*.java)
# Crea una lista di file .class corrispondenti nella cartella build
CLASSES := $(patsubst $(CP)/%.java,$(BUILD_DIR)/%.class,$(SOURCES))

.PHONY: all clean

all: build

# Compila i file .java in .class
build: $(CLASSES)

$(BUILD_DIR)/%.class: $(CP)/%.java
	$(JAVAC) -d $(BUILD_DIR) $<


clean:
	rm -rf $(BUILD_DIR)/*

