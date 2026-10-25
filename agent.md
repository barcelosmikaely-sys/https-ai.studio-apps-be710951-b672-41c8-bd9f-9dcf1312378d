# AGENT.MD - DOCUMENTAÇÃO DO SISTEMA AURA ESTÉTICA

Sistema Interno de Gestão Clínica, Prontuário Eletrônico, Agenda Inteligente, Estoque e Gestão Financeira com Alta Performance, Responsividade e Foco Rigoroso em Cibersegurança e LGPD na Estética Avançada.

---

## 1. Visão Geral do Projeto

O **Aura Estética** é uma solução completa desenvolvida para clínicas de estética médica, biomédica e enfermagem estética. O sistema foi concebido para atender às rotinas mais exigentes de gestão de procedimentos injetáveis (toxina botulínica, preenchimentos, bioestimuladores de colágeno, enzimas, PEIM, microagulhamento, terapia capilar e tratamentos faciais).

### Diretrizes de Design & Paleta de Luxo Implementada
- **Fundo Geral (Canvas):** `#F8F9FA` (Off-white neutro para conforto visual e sofisticação).
- **Superfície (Cards e Módulos):** `#FFFFFF` (Branco puro com elevação sutil).
- **Bordas e Linhas de Divisão:** `#E4E7EB` (Cinza ultra suave para delimitação elegante).
- **Texto Principal:** `#1E293B` (Slate escuro de alto contraste e legibilidade).
- **Texto Secundário / Subtítulos:** `#64748B` (Cinza médio neutro).
- **Texto Inativo / Placeholder:** `#94A3B8` (Suave para inputs).
- **Ação Principal (Botões e Destaques):** `#2A363B` (Grafite ardósia profundo).
- **Destaque Secundário / Badge:** `#D4C5B9` (Nude / Taupe neutro premium).
- **Acento Alternativo:** `#8B9DA3` (Verde-cinza acalmado para ícones selecionados e tags).
- **Alertas e Status (Pastel Clean):**
  - **Sucesso (Confirmado / Concluído):** Fundo `#E8F5E9`, Texto `#2E7D32`
  - **Atenção (Aguardando / Validade):** Fundo `#FFF8E1`, Texto `#B78103`
  - **Perigo / Alergias:** Fundo `#FFEBEE`, Texto `#C62828`
  - **Info (Em Atendimento):** Fundo `#E0F2FE`, Texto `#0369A1`
- **Suporte a Tema Escuro (Dark Luxury):** Fundo `#12181B`, Superfície `#1B2326` e realces nobres em Taupe `#D4C5B9`.

---

## 2. Arquitetura e Engenharia de Software

### Persistência de Dados 100% Local (Autocontida)
Atendendo à exigência de que **"Tudo realizado neste aplicativo precisa ficar salvo nele mesmo"**, o sistema utiliza o **Android Room Database** com suporte reativo via **Kotlin Coroutines** e **Flow**.
- Não depende de servidores web instáveis ou cadastros externos.
- Todas as evoluções clínicas, novos agendamentos, cadastros de pacientes, fotos, baixas de pacotes, insumos do estoque e transações financeiras são gravados de forma transacional e atômica no banco de dados SQLite local (`aura_estetica_db`).
- **Pré-Povoamento Inteligente (Seed Data):** No primeiro acesso, o banco de dados inicializa dados realistas de procedimentos, pacientes, equipe médica, estoques de toxina e preenchedores com lotes ANVISA e histórico financeiro, permitindo uso imediato.

---

## 3. Matriz de Segurança e Controle de Acesso Baseado em Papéis (RBAC)

O sistema conta com duas **Roles** bem definidas:
1. **ADMIN (Dono / Diretor Técnico):**
   - Acesso total ao Dashboard Executivo (faturamento diário, faturamento mensal, ticket médio);
   - Acesso irrestrito ao fluxo de caixa, despesas operacionais e lucratividade da clínica;
   - Calculadora geral de comissões de todos os profissionais;
   - Criação e gerenciamento de usuários autorizados do sistema (sem cadastro público externo);
   - Edição de procedimentos, cadastro de insumos de estoque e configurações institucionais.
2. **FUNCIONÁRIO (Profissional / Esteta / Atendente):**
   - Acesso à Agenda Inteligente e gerenciamento de seus próprios agendamentos;
   - Acesso completo ao Prontuário Eletrônico e Anamnese dos pacientes (com fotos antes/depois, produtos utilizados e termos assinados);
   - Execução e baixa de sessões de pacotes de tratamento;
   - Acesso a Clientes e fila de retornos;
   - **Privacidade Financeira:** Visualiza **apenas o seu extrato individual de produção e comissão acumulada**, sendo bloqueado do faturamento global, despesas ou configurações sensíveis da clínica.

### Mecanismos de Cibersegurança & LGPD Médica
- **Sem Cadastro Aberto:** Bloqueio de auto-registro. O acesso é liberado exclusivamente pelo Administrador na tela de Configurações.
- **Hashing Criptográfico SHA-256:** Senhas e PINs são criptografados localmente.
- **Bloqueio Rápido de Terminal (Quick Lock):** Botão de cadeado no cabeçalho do sistema permite que o profissional bloqueie a tela rapidamente ao se levantar da mesa, exigindo o PIN de 4 dígitos para reabrir prontuários e proteger os dados sensíveis dos pacientes contra visualizações não autorizadas na recepção.
- **Assinatura Digital com Carimbo de Integridade:** O prontuário gera um hash hexadecimal único (SHA-256) atrelado à data e hora UTC da assinatura do paciente.

---

## 4. Módulos Implementados

### 4.1. Dashboard Executivo
- KPIs em tempo real (Faturamento do Dia, Faturamento do Mês, Procedimentos Hoje, Clientes Ativos, Ticket Médio).
- Gráfico interativo com Canvas nativo exibindo o faturamento por dia da semana com seleção por toque.
- Lista de agendamentos prioritários do dia com alteração de status em 1 toque ("Iniciar Atendimento", "Concluir", "Ver Prontuário").
- Chips de alerta no topo para estoque baixo e retornos atrasados.

### 4.2. Agenda Inteligente
- Grade completa de horários com indicação de salas e profissionais.
- Filtros dinâmicos por Status (Confirmado, Aguardando, Em Atendimento, Concluído, Cancelado), por Profissional e por Sala (Sala 01 - Injetáveis, Sala 02 - Harmonização Facial, Sala 03 - Laser e Tecnologias).
- Modal para novo agendamento com seleção rápida de paciente, procedimento, valor, horário e consultório.
- Recurso de **Bloqueio de Horário** para reuniões clínicas, manutenção preventiva de equipamentos ou intervalo da equipe.

### 4.3. Prontuário Eletrônico & Anamnese
- **Slider Interativo Antes e Depois:** Componente touch/drag em tempo real onde o usuário arrasta o botão divisor central e compara lado a lado a foto prévia e o resultado pós-procedimento com indicação visual de "ANTES" e "DEPOIS (15 DIAS)".
- **Banner de Alerta Crítico para Alergias:** Destaque em fundo pastel avermelhado quando o paciente apresenta alergias a anestésicos tópicos, dipirona, frutos do mar ou látex.
- **Rastreabilidade ANVISA:** Registro formal do produto aplicado, marca, dose (UI ou ml) e número de lote.
- **Mapeamento de Regiões Tratadas:** Tags anatômicas (Frontal, Glabela, Periorbital, Lábios, Malar, Papada).
- **Assinatura Digital de Consentimento (SignaturePad):** Canvas interativo onde o paciente assina com o dedo/caneta na tela do dispositivo, gerando carimbo com data, hora e hash de segurança.
- **Envio de Recomendações Pós via WhatsApp:** Botão com disparo automático de orientações formatadas.

### 4.4. Procedimentos e Protocolos
- Catálogo com abas por categoria: Injetáveis, Facial, Corporal, Capilar e Laser & Tecnologias.
- Informações completas: Preço, duração em minutos, intervalo técnico recomendado para retorno (em dias), descrição clínica e orientações de pós-cuidados.
- Modal para cadastro de novos procedimentos.

### 4.5. Pacotes de Tratamento
- Criação e acompanhamento de pacotes de sessões (ex: Rejuvenescimento Full Face 3x, Lipo Enzimática de Papada 5x, Glow & Firm 4x).
- Barra de progresso visual de sessões concluídas vs. restantes.
- Ação de "Baixa de 1 Sessão" em 1 toque que atualiza automaticamente o status para CONCLUÍDO ao finalizar.

### 4.6. Cadastro de Clientes
- Cadastro completo com Nome, Telefone, E-mail, Data de Nascimento, Endereço, Alergias Conhecidas, Observações de Anamnese, LTV (gasto total acumulado) e Próximo Retorno Estimado.
- Busca em tempo real por nome, telefone ou e-mail.
- Atalhos diretos para abrir conversa no WhatsApp e para abrir a pasta de prontuário do paciente.

### 4.7. Retornos & CRM Inteligente
- Fila de clientes com retorno sugerido calculado a partir do procedimento realizado (ex: Botox = 120 dias, Bioestimulador = 45 dias, Limpeza = 30 dias).
- **Simulador de Mensagens & Marketing WhatsApp:**
  - Confirmação de agendamento (D-1)
  - Check-in pós-procedimento (D+1)
  - Avaliação de cicatrização (D+7)
  - Lembrete de manutenção preventiva de Botox (4 meses)
  - Lembrete de continuidade de Bioestimulador
  - Visualização em balão de conversa estilo WhatsApp com botão de disparo direto.

### 4.8. Documentação & Termos Digitais
- Modelos pré-carregados de Termos de Consentimento Livre e Esclarecido (TCLE) para Toxina Botulínica, Preenchimento com Ácido Hialurônico, Bioestimulador de Colágeno, Manual de Orientações Pós e Contrato de Prestação de Serviços.
- Vinculação com o paciente, assinatura digital touch no próprio termo e compartilhamento formatado via WhatsApp.

### 4.9. Controle de Estoque
- Controle por lotes, fabricantes (Allergan, Galderma, Merz, BD Medical, etc.) e data de validade.
- Etiquetas automáticas de **Estoque Baixo** e alerta de validade próxima.
- Ajuste rápido de entrada (+1) e saída (-1) para agilizar o atendimento na sala de aplicação.

### 4.10. Gestão Financeira & Comissões
- Fluxo de caixa diário e mensal segregando Receitas e Despesas.
- Calculadora automatizada de comissões calculada sobre os procedimentos concluídos da equipe (Dra. Helena 40%, Dra. Camila 35%, Enf. Lucas 30%).
- Extrato com privacidade rigorosa (funcionários visualizam unicamente sua produção pessoal).

### 4.11. Configurações Gerais & Usuários
- Edição dos dados cadastrais da clínica (Razão Social, CNPJ, telefone, endereço, horários).
- Cadastro de novos usuários autorizados com definição de papel (ADMIN ou FUNCIONÁRIO).
- Alternância entre o Tema Claro de Luxo e o Tema Escuro Dark Luxury.

---

## 5. Como Rodar o Sistema

O sistema foi compilado e estruturado nativamente para o ambiente Android (SDK 36, MinSdk 24, Kotlin 2.2, Jetpack Compose, Room Database).

### Opção 1: Execução no AI Studio (Streaming Emulator)
1. O emulador no navegador recarrega e instala automaticamente o APK gerado com o novo ícone adaptativo e todos os dados.
2. Na tela de login inicial, você pode usar as credenciais:
   - **Administrador (Dono):** `admin@clinica.com` / Senha: `1234`
   - **Dra. Camila (Esteta):** `camila@clinica.com` / Senha: `1234`
   - **Enf. Lucas (Laser & Micro):** `lucas@clinica.com` / Senha: `1234`
   *(Ou utilize os botões de seleção rápida de perfil na própria tela de login)*.

### Opção 2: Execução no Android Studio / Dispositivo Físico
1. Abra o projeto no **Android Studio** (versão Ladybug / Jellyfish ou superior com JDK 17 / 21).
2. O Gradle sincronizará automaticamente as dependências (`androidx.compose`, `androidx.room`, `coil-compose`, etc.).
3. Execute o comando no terminal do projeto caso deseje compilar via CLI:
   ```bash
   gradle assembleDebug
   ```
4. Conecte seu dispositivo Android ou inicie um emulador AVD e clique no botão **Run (Shift + F10)**.
