connect to docker
- docker exec -ti mydb2 bash -c "su - db2inst1"

docker CL inputs:
- db2 -tvf p1_create.sql


powershell / windows cmd inputs
UI- javac *.java; java -cp ";./db2jcc4.jar" p1 ./db.properties;
BATCH- javac *.java; java -cp ";./db2jcc4.jar" ProgramLauncher ./db.properties;
