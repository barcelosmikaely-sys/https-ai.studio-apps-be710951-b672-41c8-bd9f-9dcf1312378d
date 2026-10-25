package com.example.model

enum class UserRole(val label: String, val badge: String) {
    ADMIN("Administrador / Dono", "Acesso Total"),
    EMPLOYEE("Profissional / Atendente", "Operacional")
}

enum class AppointmentStatus(val label: String) {
    CONFIRMADO("Confirmado"),
    AGUARDANDO("Aguardando"),
    EM_ATENDIMENTO("Em Atendimento"),
    CONCLUIDO("Concluído"),
    CANCELADO("Cancelado")
}

enum class ProcedureCategory(val label: String) {
    INJETAVEIS("Injetáveis"),
    FACIAL("Facial"),
    CORPORAL("Corporal"),
    CAPILAR("Capilar"),
    LASER_TECNOLOGIA("Laser & Tecnologia")
}

enum class TransactionType(val label: String) {
    RECEITA("Receita"),
    DESPESA("Despesa")
}

enum class ItemCategory(val label: String) {
    TOXINAS("Toxinas Botulínicas"),
    PREENCHEDORES("Ácido Hialurônico"),
    BIOESTIMULADORES("Bioestimuladores"),
    DESCARTAVEIS("Descartáveis & Agulhas"),
    ANESTESICOS("Anestésicos & Dermocosméticos")
}

enum class DocumentType(val title: String, val description: String) {
    TCLE_BOTOX(
        "TCLE - Toxina Botulínica",
        "Termo de Consentimento Livre e Esclarecido para aplicação de toxina botulínica tipo A."
    ),
    TCLE_PREENCHIMENTO(
        "TCLE - Preenchimento com Ácido Hialurônico",
        "Consentimento informado para volumização e harmonização com preenchedor dérmico."
    ),
    TCLE_BIOESTIMULADOR(
        "TCLE - Bioestimulador de Colágeno",
        "Termo para indução tecidual com hidroxiapatita de cálcio ou ácido poli-L-lático."
    ),
    ORIENTACOES_POS(
        "Orientações Pós-Procedimento",
        "Cuidados essenciais nas primeiras 48h a 14 dias após a intervenção estética."
    ),
    CONTRATO_TRATAMENTO(
        "Contrato de Prestação de Serviços Estéticos",
        "Instrumento particular de prestação de serviços estéticos e plano de tratamento."
    )
}
