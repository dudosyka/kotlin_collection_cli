# Build modules
gradle server:build
gradle resolver:build
gradle client:build
gradle servers_observer:build
gradle tests:build


# Create output dir
mkdir -p ~/Documents/lab6Output
mkdir -p ~/Documents/lab6Output/server ~/Documents/lab6Output/resolver ~/Documents/lab6Output/client ~/Documents/lab6Output/tests ~/Documents/lab6Output/servers_observer


# Copy jar files
cp ./server/build/libs/server-standalone.jar ~/Documents/lab6Output/server/server.jar
cp ./resolver/build/libs/resolver-standalone.jar ~/Documents/lab6Output/resolver/resolver.jar
cp ./client/build/libs/client-standalone.jar ~/Documents/lab6Output/client/client.jar
cp ./tests/build/libs/tests-standalone.jar ~/Documents/lab6Output/tests/tests.jar
cp ./servers_observer/build/libs/servers_observer-standalone.jar ~/Documents/lab6Output/servers_observer/servers_observer.jar



# Create subdir for prometheus & grafana docker files
mkdir -p ~/Documents/lab6Output/prometheus_grafana



# Copy docker files
cp ./docker/docker-compose.yml ~/Documents/lab6Output/prometheus_grafana
cp -r ./docker/config ~/Documents/lab6Output/prometheus_grafana


echo "rm */logger.file.log" >> ~/Documents/lab6Output/removeLogs.sh