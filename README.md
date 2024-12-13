## URL Shortener with AWS Lambda Function
### Encurtador de URL com integração de função AWS Lambda

Este projeto para fins acadêmicos funciona como um sistema de encurtamento de URL´s utilizando a AWS como infraestrutura servless.
<br>
Os usuários poderão criar URL encurtadas que redirecionem para a URL original, com tempo de expiração ajustável. 
Este sistema é composto por 2 funções AWS Lambda: 
- a primeira é responsável por **gerar e armazenar** links encurtados em um AWS Bucket S3, juntamente com informações da URL original e o tempo de expiração; 
- a segunda **gerencia o redirecionamento**, verificando o código da URL encurtada e validando o tempo de expiração
<br>
Projeto desenvolvido enquanto participante no Curso gratuíto de Java da **Rocketseat**, ministrado entre os dias 18 a 21 de Novembro de 2024.

#Observações:
Não encontrei uma forma para que a função AWS Lambda retornasse status 400 no caso de erro no envio do body de requisição, 
portanto todos os erros retornam com status 200.

* Link da Função Geradora URL : https://oqyi3z4vjzq7kxc4duiqcdkgaq0zjdux.lambda-url.us-east-1.on.aws/
* Link da Função Redirecionadora URL : https://oqyi3z4vjzq7kxc4duiqcdkgaq0zjdux.lambda-url.us-east-1.on.aws/

| Método  | Endpoint             			 | Responsabilidade                                 | Acesso via token		   |
| ------- | -------------------------------- | ------------------------------------------------ | ------------------------ |
| POST    | Link da Função Geradora URL      | Cria uma nova URL encurtada                      | Livre (sem token)        |
| GET     | Link da Função Geradora URL      | Redireciona à URL original pela URL encurtada    | Livre (sem token)        |

## Rota Post/Link da Função Lambda Geradora de URL
Esta rota cria uma URL encurtada, ao fornecer a URL original e o tempo de expiração *em segundos, no formato String*.
Não é enviado Token de **autenticação**. O corpo da requisição tem os seguites campos obrigatórios:

| Dados de Envio:    |
| ------------------ |
| Body: Formato Json |

```json
{
	"expirationTime": "1000",
	"originalUrl": "https://www.linkedin.com/in/jose-martins0808/"
}
```

| Resposta do servidor:                               |
| --------------------------------------------------- |
| Body: Formato Json                                  |
| Status code: <b style="color:green">200 OK</b>      |

```json
{
	"code": "123456"
}
```

Caso não seja enviado o corpo de requisição, retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:green">200 OK</b>           |

```json
{
	"message": "Body request must be sent."
}
```

Caso seja enviado o corpo de requisição **sem** a chave "expirationTime", retornará o seguinte erro:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:green">200 OK</b>           |

```json
{
	"message": "Expiration Time must be sent"
}
```

Caso seja enviado o corpo de requisição **sem** a chave "originalUrl", retornará o seguinte erro:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:green">200 OK</b>           |

```json
{
	"message": "Original Url must be sent"
}
```

## Rota Post/Link da Função Lambda Redirecionadora de URL
Esta rota recebe uma URL encurtada, juntamente com o tempo de expiração. Verifica-se se a URL encurtada não expirou em seu
tempo útil, fornecendo assim a URL original para acesso pelo cliente.
Não é enviado Token de **autenticação**. O corpo da requisição tem os seguites campos obrigatórios:

