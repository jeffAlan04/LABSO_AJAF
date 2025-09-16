COMMON_DIR := src/common
CLIENT_DIR := src/client
MASTER_DIR := src/master
BIN_DIR   := bin
LIB_DIR   := lib

#librerie jar
CP := $(LIB_DIR)/*

PORT      := 7000
MASTER_IP := localhost

#cartelle con i sorgenti
SOURCES_COMMON := $(shell find $(COMMON_DIR) -name "*.java")
SOURCES_CLIENT := $(shell find $(CLIENT_DIR) -name "*.java")
SOURCES_MASTER := $(shell find $(MASTER_DIR) -name "*.java")

.PHONY: all clean client master compile
all: compile

compile:
	@mkdir -p $(BIN_DIR) #crea cartella bin
	javac -d $(BIN_DIR) $(SOURCES_COMMON) #compila i sorgenti dentro common
	javac -cp "$(BIN_DIR):$(CP)" -d $(BIN_DIR) $(SOURCES_MASTER) #compila sorgenti di master
	javac -cp "$(BIN_DIR)" -d $(BIN_DIR) $(SOURCES_CLIENT) #compila sorgenti di client

client:
	java -classpath "$(BIN_DIR)" Client $(MASTER_IP) $(PORT)

master:
	java -classpath "$(BIN_DIR):$(CP)" Master $(PORT)

clean:
	rm -rf $(BIN_DIR)
