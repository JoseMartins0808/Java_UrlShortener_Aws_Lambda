## URL Shortener with AWS Lambda Function
### Encurtador de URL com integração de função AWS Lambda

Este projeto para fins acadêmicos funciona como um sistema de encurtamento de URL´s utilizando a AWS como infraestrutura servless.
<br>
Os usuários poderão criar URL encurtadas que redirecionem para a URL original, com tempo de expiração ajustável. 
Este sistema é composto por 2 funções AWS Lambda: 
- a primeira é responsável por **gerar e armazenar** links encurtados em um AWS Bucket S3, juntamente com informações da URL original e o tempo de expiração; 
- a segunda **gerencia o redirecionamento**, verificando o código da URL encurtada e validando o tempo de expiração.

Projeto desenvolvido enquanto participante no Curso gratuíto de Java da Rocketseat, ministrado entre os dias 18 a 21 de Novembro de 2024.

* Link da Função Geradora URL : https://qkthphbzyf.execute-api.us-east-1.amazonaws.com/prod/create
* Link da Função Redirecionadora URL : https://oqyi3z4vjzq7kxc4duiqcdkgaq0zjdux.lambda-url.us-east-1.on.aws/

<br>

| Método  | Endpoint             			 | Responsabilidade                                 | Acesso via token		   |
| ------- | -------------------------------- | ------------------------------------------------ | ------------------------ |
| POST    | Link da Função Geradora URL      | Cria uma nova URL encurtada                      | Livre (sem token)        |
| GET     | Link da Função Redirecionadora/Código da URL Encurtada      | Redireciona à URL original pela URL encurtada    | Livre (sem token)        |

<br>

## Rota Post/Link da Função Lambda Geradora de URL
Esta rota cria uma URL encurtada, ao fornecer a URL original e o tempo de expiração *em segundos, no formato String*.
Não é enviado Token de **autenticação**. O corpo da requisição tem os seguites campos obrigatórios:

| Dados de Envio:    |
| ------------------ |
| Body: Formato Json |

```json
{
	"originalUrl": "https://www.linkedin.com/in/jose-martins0808/",
	"expirationTime": "1735350935"
}
```

| Resposta do servidor:                               |
| --------------------------------------------------- |
| Body: Formato Json                                  |
| Status code: <b style="color:green">200 OK</b>      |

```json
{
	"code": "e3b29fa7"
}
```
<br>
Caso não seja enviado o corpo de requisição, retornará o seguinte <b>erro</b>:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Body request must be sent"
}
```
<br>
Caso seja enviado o corpo de requisição **sem** a chave "expirationTime" **nem** a chave "originalUrl", retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Expiration Time and Original Url must be sent"
}
```
<br>
Caso seja enviado o corpo de requisição **sem** a chave "expirationTime", retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Expiration Time must be sent"
}
```
<br>
Caso seja enviado o corpo de requisição **sem** a chave "originalUrl", retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Original Url must be sent"
}
```
<br>
Caso seja enviado uma **url inválida** no campo "originalUrl", retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Oiginal Url must be a valid URL"
}
```
<br>
Caso seja enviado um timestamp **inferior** ao timestamp do momento da requisição, retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Expiration Time must be after Current Time: 1735351533"
}
```

<br>

## Rota Post/Link da Função Lambda Redirecionadora de URL/Código da URL Encurtada
Esta rota recebe uma URL encurtada por meio de **querry param**, no qual se encontra o código
para a URL original. Verifica-se por este código se a URL encurtada não expirou em seu tempo útil, 
fornecendo assim a URL original para acesso pelo cliente.
Não é enviado Token de **autenticação**. **Não** há corpo de requisição.

| Dados de Envio:    						   |
| -------------------------------------------- |
| Body: null		 						   |
| Endpoint: link da função lambda/URL encurtada|

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Redirecionamento: para a URL original					   |
| Body: null					                           |
| Status code: <b style="color:green">200 OK</b> 		   |
<br>
Caso **não seja enviado o código** da URL encurtada, como query param, retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Short URL code is required"
}
```
<br>
Caso seja enviada um código **inexistente** de URL, retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">400 BAD REQUEST</b> |

```json
{
	"message": "Short Url not found"
}
```
<br>
Caso seja enviado um código da URL encurtada, que **já esteja expirada**, retornará o seguinte **erro**:

| Resposta do servidor:                                    |
| -------------------------------------------------------- |
| Body: Formato Json                                       |
| Status code: <b style="color:orange">410 GONE</b> 	   |

```json
{
	"message": "This URL has expired"
}
```

