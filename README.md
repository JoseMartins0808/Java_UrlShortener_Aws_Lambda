## URL Shortener with AWS Lambda Function
### Encurtador de URL com integração de função AWS Lambda

Este projeto para fins acadêmicos funciona como um sistema de encurtamento de URL´s utilizando a AWS como infraestrutura servless.
<br>
Os usuários poderão criar URL encurtadas que redirecionem para a URL original, com tempo de expiração ajustável. 
Este sistema é composto por 2 funções AWS Lambda: 
- a primeira é responsável por **gerar e armazenar** links encurtados em um AWS Bucket S3, juntamente com informações da URL original e o tempo de expiração; 
- a segunda **gerencia o redirecionamento**, verificando o código da URL encurtada e validando o tempo de expiração
