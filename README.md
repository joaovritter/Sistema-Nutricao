# 🥗 Sistema de Nutrição - Guia de Deploy (Docker no Servidor da Faculdade)

Este guia prático ensina como subir e gerenciar a aplicação no servidor Linux da faculdade utilizando **Docker** e **Docker Compose**.

---

## 🛠️ Estrutura do Ambiente do Servidor
* **IP do Servidor na Rede Interna:** `10.21.19.20`
* **Porta de Acesso Web:** `80` (HTTP Padrão)
* **Banco de Dados:** MySQL 8 rodando internamente na rede do Docker.
* **Ferramenta de Orquestração:** `docker-compose` (versão 1.29.2 com hífen).

---

## 🚀 Passo a Passo: Subir a Aplicação do Zero

### Passo 1: Transferir os arquivos
Conecte-se via **WinSCP** e envie toda a pasta do projeto para o diretório `~/nutricaoMJW/Sistema-Nutricao` no servidor da faculdade.
> *Nota: Não é necessário copiar a pasta `target/`, o Docker gerará uma nova compilação automaticamente.*

---

### Passo 2: Acessar a pasta e criar as senhas
Acesse o servidor via **PuTTY**, entre na pasta do projeto e crie o arquivo com as credenciais do banco MySQL:

```bash
cd ~/nutricaoMJW/Sistema-Nutricao

# Cria o arquivo de ambiente
cat > .env << 'EOF'
DB_ROOT_PASSWORD=www.com.brj
DB_PASSWORD=www.com.brj
EOF
```

---

### Passo 3: Limpeza completa do Docker
Para garantir que nenhuma porta ou container antigo cause conflito, limpe o ambiente:

```bash
# Para o compose atual e apaga volumes anteriores
docker-compose down -v

# Remove outros containers antigos que ocupam as portas 80 ou 3306
docker rm -f nutricao-nginx nutricao-mysql nutricao-app
```

---

### Passo 4: Subir os Containers

1. **Suba o Banco de Dados MySQL** primeiro para que ele configure o schema e insira a tabela TACO automaticamente:
   ```bash
   docker-compose up -d db
   ```

2. **Suba a aplicação Spring Boot** diretamente ligada à rede do banco na porta `80`:
   ```bash
   docker run -d --name nutricao-app \
     --network sistema-nutricao_default \
     -p 80:8080 \
     -e SPRING_PROFILES_ACTIVE=docker \
     -e DB_HOST=db \
     -e DB_PORT=3306 \
     -e DB_NAME=nutricao \
     -e DB_USERNAME=root \
     -e DB_PASSWORD=www.com.brj \
     -v app_logs:/app/logs \
     sistema-nutricao_app:latest
   ```

---

### Passo 5: Acompanhar se o sistema iniciou com sucesso
Monitore a subida da aplicação pelo log em tempo real:

```bash
docker logs -f nutricao-app
```
*Assim que aparecer a mensagem `Tomcat started on port 8080` e `Nutricionista Criado.`, aperte **`Ctrl + C`** para liberar o terminal.*

---

## 🖥️ Como Acessar o Sistema
Abra qualquer navegador conectado à rede da faculdade e acesse:

👉 **`http://10.21.19.20`** (Ou `http://10.21.19.20/login`)

* **Usuário Padrão:** `nutricionista`
* **Senha Padrão:** `nutricionista`

---

## 🔄 Como Atualizar o Sistema (Após alterar o código)
Caso você faça alterações nas telas HTML ou no código Java e queira atualizar o servidor:

1. Transfira os arquivos modificados usando o **WinSCP**.
2. Acesse o **PuTTY** e execute:
   ```bash
   cd ~/nutricaoMJW/Sistema-Nutricao
   
   # Recompila o projeto e atualiza a imagem Docker
   docker-compose build app
   
   # Reinicia o container da aplicação com a nova versão
   docker rm -f nutricao-app
   
   docker run -d --name nutricao-app \
     --network sistema-nutricao_default \
     -p 80:8080 \
     -e SPRING_PROFILES_ACTIVE=docker \
     -e DB_HOST=db \
     -e DB_PORT=3306 \
     -e DB_NAME=nutricao \
     -e DB_USERNAME=root \
     -e DB_PASSWORD=www.com.brj \
     -v app_logs:/app/logs \
     sistema-nutricao_app:latest
   ```

---

## 📊 Comandos Úteis de Monitoramento
* **Ver logs do Banco:** `docker-compose logs -f db`
* **Ver logs do App:** `docker logs -f nutricao-app`
* **Ver containers ativos:** `docker ps`
* **Ver uso de memória/CPU:** `docker stats`
