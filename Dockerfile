FROM openjdk:11
LABEL verion="1.0.0" description="Sistema de calculo de investimentos para aposentadoria"
COPY ./target/calculadora-investimentos.jar calculadora-investimentos.jar
CMD ["java","-jar","calculadora-investimentos.jar"]