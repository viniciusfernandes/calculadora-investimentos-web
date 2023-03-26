FROM openjdk:11
LABEL verion="1.0.0" description="Sistema de calculo de investimentos para aposentadoria"
COPY ./target/calculadora-investimentos-web.jar calculadora-investimentos-web.jar
CMD ["java","-jar","calculadora-investimentos-web.jar"]