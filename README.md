# 🛒 productAlert

<p align="center">
  <img src="https://img.shields.io/badge/Status-Concluído-brightgreen?style=for-the-badge&logo=github" alt="Concluído">
</p>

---

## 📝 Sobre o Projeto

O **productAlert** é uma aplicação de **web scraping** que monitora ofertas em lojas online, extrai os produtos e centraliza tudo em um formato único, independente de qual site foi consultado.

O fluxo é simples: um agendador dispara a coleta, cada loja registrada é consultada em sequência, o HTML retornado é interpretado e convertido em produtos de domínio. Produtos repetidos entre páginas são deduplicados e, quando o mesmo item aparece mais de uma vez, o menor preço prevalece.

Atualmente o **Mercado Livre** está implementado, com suporte a paginação e filtragem de anúncios patrocinados.

O projeto foi construído com **arquitetura hexagonal** justamente por causa do ponto central abaixo.

---

## 🔌 Extensível

Adicionar uma nova loja **não exige alterar nenhum código existente**.

O núcleo da aplicação não conhece Jsoup, HTML ou qualquer site específico — ele conhece apenas o contrato `StoreScraper`. Cada loja é um adaptador independente:

```
infrastructure/scraper/
├── mercadolivre/
│   ├── MercadoLivreParser.java
│   └── MercadoLivreScraper.java
└── novaloja/                  ← basta criar esta pasta
    ├── NovaLojaParser.java
    └── NovaLojaScraper.java
```

Ao anotar o novo scraper com `@Component`, o Spring o injeta automaticamente na lista de lojas do serviço de orquestração. O agendador, o serviço e o domínio permanecem intactos.

Se a nova loja renderizar conteúdo via JavaScript e o Jsoup não der conta, o mesmo vale para o carregamento da página: basta uma nova implementação de `HtmlFetcher` usando outra biblioteca, sem impacto nas lojas já existentes.

---

## 🛣️ Roadmap

| Status | Funcionalidade |
| :---: | :--- |
| ✅ | Scraping do Mercado Livre com paginação |
| ✅ | Deduplicação de produtos por menor preço |
| ✅ | Tolerância a falhas — uma loja fora do ar não interrompe as demais |
| ✅ | Execução agendada via cron |
| ✅ | **Notificação via bot do Telegram** |

---

## 🛠️ Tecnologias Utilizadas

<p align="left">
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg" alt="Java" width="40" height="40" style="max-width: 100%;">
  &nbsp;&nbsp;
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg" alt="Spring Boot" width="40" height="40" style="max-width: 100%;">
  &nbsp;&nbsp;
  <img src="https://raw.githubusercontent.com/devicons/devicon/master/icons/maven/maven-original.svg" alt="Maven" width="40" height="40" style="max-width: 100%;">
</p>

* **Java 17 & Spring Boot 4.0.7:** Base da aplicação.
* **Jsoup:** Requisição HTTP e parsing do HTML das lojas.
* **Spring Scheduling:** Execução automática e periódica da coleta.
* **Lombok:** Redução de código repetitivo.
* **Arquitetura Hexagonal (Ports & Adapters):** Isolamento do núcleo de negócio.

---

## 🚀 Estrutura do Projeto

O projeto segue **arquitetura hexagonal**, onde todas as dependências apontam para o centro:

```
com.example.product_alert
│
├── domain/                    ← Núcleo. Zero frameworks.
│   ├── model/
│   │   ├── Product.java           Produto de negócio (não é entidade de banco)
│   │   ├── Price.java             Value object com validação
│   │   ├── Store.java
│   │   └── SearchQuery.java
│   └── exception/
│       └── ScrapingException.java
│
├── application/                   ← Orquestração e contratos
│   ├── port/out/
│   │   ├── StoreScraper.java      Contrato de qualquer loja
│   │   └── HtmlFetcher.java       Contrato de carregamento de página
│   └── service/
│       └── ScrapeStoreService.java
│
├── infrastructure/                ← Implementações e libs externas
│   ├── scraper/mercadolivre/
│   │   ├── MercadoLivreParser.java
│   │   └── MercadoLivreScraper.java
│   └── http/
│       └── JsoupHtmlFetcher.java
│
└── interfaces/                    ← Quem inicia a execução
    └── scheduler/
        └── ScrapingScheduler.java
```

### Responsabilidade de cada camada

| Camada | Responsabilidade |
| :--- | :--- |
| **`domain`** | Define o que é um produto e um preço, com suas regras e invariantes. Não conhece framework algum. |
| **`application`** | Orquestra o fluxo e declara as portas. Decide **o quê** acontece, nunca **como**. |
| **`infrastructure`** | Implementa as portas. É onde vivem Jsoup, HTTP e os seletores CSS de cada site. |
| **`interfaces`** | Adaptadores de entrada — quem dispara a aplicação. Hoje o agendador; futuramente controllers ou o bot. |

O ganho prático: o `ScrapeStoreService` pode ser testado sem rede, sem banco e sem subir o Spring.

---

## ⚙️ Variáveis de Configuração

Configuráveis via `application.yml` ou variáveis de ambiente:

| Propriedade | Descrição | Exemplo |
| :--- | :--- | :--- |
| `scraper.category` | Código da categoria monitorada | `MLB1648` |
| `scraper.max-pages` | Número máximo de páginas por loja | `2` |
| `scraper.cron` | Expressão cron da execução (6 campos) | `0 0 3 * * *` |
| `telegram.bot-token` | Token do seu bot no Telegram | `seu-token-aqui` |
| `telegram.chat-id` | ID do chat para envio das notificações | `seu-chat-id` |

---

## 🚀 Como Executar

Antes de iniciar, certifique-se de ter o [Java 17](https://adoptium.net/) e o [Git](https://git-scm.com/) instalados.
Clone o repositório:

```bash
git clone https://github.com/breenoox/product-alert.git
cd product-alert
```

Para configurar as notificações via Telegram, defina as variáveis de ambiente necessárias:

```bash
export TELEGRAM_BOT_TOKEN="seu-token-aqui"
export TELEGRAM_CHAT_ID="seu-chat-id"
```

Execute a aplicação:

```bash
./mvnw spring-boot:run
```

Para testar sem esperar o horário agendado, use um cron de execução por minuto:

```yaml
scraper:
  cron: "0 * * * * *"
```

Saída esperada no log:

```
Started ProductAlertApplication in 1.8 seconds
Iniciando scraping da categoria MLB1648
Coletados 48 produtos
```

> Certifique-se de que a classe principal está anotada com `@EnableScheduling` — sem ela o agendamento é ignorado silenciosamente.

---

## ➕ Como Adicionar uma Nova Loja

1. Crie a pasta `infrastructure/scraper/<nomedaloja>/`.
2. Implemente o parser da loja, responsável por extrair os produtos do HTML.
3. Crie a classe do scraper implementando `StoreScraper` e anote com `@Component`.
4. Adicione o novo valor ao enum `Store`.

Pronto. O serviço de orquestração passa a consultar a loja nova automaticamente, sem qualquer alteração no agendador, no serviço ou no domínio.

---

## ✅ Observações

O scraping respeita um intervalo entre requisições para não sobrecarregar os servidores das lojas.

Anúncios patrocinados são descartados durante o parsing, garantindo que apenas ofertas orgânicas sejam coletadas.

Cada produto carrega o instante da captura (`capturedAt`), o que viabiliza o histórico de preços previsto no roadmap.

---

## Desenvolvedor

<table align="center">
  <tr>
    <td align="center">
      <div>
        <img src="https://avatars.githubusercontent.com/breenoox" width="120px;" alt="Foto no GitHub" class="profile"/><br>
          <b> Breno Barbosa </b><br>
            <a href="https://www.linkedin.com/in/brenobarbosa22/" alt="Linkedin"><img src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" height="20"></a>
            <a href="https://github.com/breenoox" alt="Github"><img src="https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white" height="20"></a>
      </div>
    </td>
  </tr>
</table>
