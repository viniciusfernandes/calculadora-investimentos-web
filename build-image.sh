mvn clean package
docker build -t calculadora-investimentos-web .
docker container run --name calculadora-investimentos-web -p 8081:8081 calculadora-investimentos-web

