# Sistema Nutrição

Sistema web para gestão de fichas técnicas de receitas e montagem de refeições, voltado para nutricionistas e profissionais da área de alimentação.

## Objetivo
Permitir o cadastro, visualização, edição e agrupamento de fichas técnicas de receitas, facilitando o controle nutricional, de custos e a montagem de cardápios/planejamentos alimentares.

## Funcionalidades Principais
- **Cadastro de Fichas Técnicas:**
  - Ingredientes, modo de preparo, perfil nutricional, custos, rendimento, etc.
- **Visualização e Edição de Fichas Técnicas:**
  - Listagem, busca, edição e exclusão de receitas.
- **Cadastro de Ingredientes:**
  - Ingredientes próprios ou do sistema, com valores nutricionais.
- **Montagem de Refeições:**
  - Crie refeições agrupando várias fichas técnicas.
  - Dê um nome à refeição e selecione as fichas técnicas desejadas.
  - Edite ou exclua refeições já criadas.
- **Dashboard:**
  - Acesso rápido às principais funções do sistema.
- **Autenticação:**
  - Controle de acesso por usuário.

## Tecnologias Utilizadas
- Java 17+
- Spring Boot
- Spring Data JPA
- Thymeleaf
- HTML5, CSS3, JavaScript
- H2 Database (padrão, pode ser adaptado para outros bancos)

## Requisitos
- Java 17 ou superior
- Maven

## Como Executar
1. Clone o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```
2. Entre na pasta do projeto:
   ```bash
   cd Sistema Nutricao
   ```
3. Execute o projeto com Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
   Ou, se preferir, gere o JAR e execute:
   ```bash
   ./mvnw clean package
   java -jar target/*.jar
   ```
4. Acesse no navegador:
   - [http://localhost:8080](http://localhost:8080)

## Estrutura de Pastas
- `src/main/java/com/mjwsolucoes/sistemanutricao/` - Código fonte Java
- `src/main/resources/templates/` - Templates HTML (Thymeleaf)
- `src/main/resources/static/css/` - Arquivos CSS
- `src/main/resources/static/images/` - Imagens

## Exemplos de Uso
- **Criar Ficha Técnica:**
  - Acesse "Criar Ficha Técnica" no dashboard, preencha os campos e salve.
- **Montar Refeição:**
  - Acesse "Refeições" no dashboard, clique em "Criar Nova Refeição", selecione as fichas técnicas desejadas e salve.
- **Editar/Excluir:**
  - Use os botões de ação nas listas para editar ou excluir fichas técnicas e refeições.

## Observações
- O sistema pode ser facilmente adaptado para outros bancos de dados.
- O layout é responsivo e pode ser customizado via CSS.

## Licença
Projeto acadêmico - livre para uso e adaptação. 
