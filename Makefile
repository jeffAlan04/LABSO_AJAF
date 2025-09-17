SRC_DIR := src
BIN_DIR   := bin
LIB_DIR   := lib

#librerie jar
CP := $(LIB_DIR)/*

PORT      := 7000
MASTER_IP := localhost

#cartelle con i sorgenti
SOURCES := $(shell find $(SRC_DIR) -name "*.java")

.PHONY: all clean client master compile
all: compile

compile:
	@mkdir -p $(BIN_DIR) #crea cartella bin
	javac -cp "$(BIN_DIR):$(CP)" -d $(BIN_DIR) $(SOURCES) #compila sorgenti

client:
	java -classpath "$(BIN_DIR)" Client $(MASTER_IP) $(PORT)

master:
	java -classpath "$(BIN_DIR):$(CP)" Master $(PORT)

clean:
	rm -rf $(BIN_DIR)
