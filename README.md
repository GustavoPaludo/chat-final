# mobile-final
Trabalho 1 da matéria de programação para dispositivos móveis

Equipe: Gustavo Francener Paludo, Ana Clara, Pedro Nunes e Eric Nascimento


Como executar:
Há três classes nesse chat entre clientes: o Server.java, o ClientHandler.java e o Client.java. Para iniciar o chat, deve-se rodar primeiramente a classe Server.java. O servidor irá ser iniciado na porta 8180 do localhost. Quando o servidor estiver de pé, pode-se iniciar múltiplas instâncias de clientes, o Client.java. Os clientes poderão enviar mensagens, listar usuários e enviar arquivos. O ClientHandler, que é uma Thread no lado do servidor, será responsável pelo tratamento das requisições do Client.java e é dinamicamente criada pelo Server.java. Os logs do servidor são exportados para a pasta logs na raiz do projeto.

