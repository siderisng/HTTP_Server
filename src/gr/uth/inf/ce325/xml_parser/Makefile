JFLAGS = -g

JAVA_SRCS = Namespace.java \
	Node.java \
	Attribute.java \
	Document.java\
	DocumentBuilder.java


CLASSES = edit.class commands.class display.class files.class

$(CLASSES): $(JAVA_SRCS)
	javac $(JFLAGS) $(JAVA_SRCS)
	
clean:
	rm -f *.class