all: compile run

compile:
	./gradlew build -x test

run:
	./gradlew lwjgl3:run

test:
	./gradlew test jacocoTestReport