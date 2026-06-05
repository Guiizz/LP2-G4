package BLL;

import DAL.AnoLetivoDAL;
import DAL.CursoDAL;
import DAL.EstudanteDAL;
import DAL.UnidadeCurricularDAL;
import Model.*;
import Utils.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Camada de Lógica de Negócio (BLL) para a entidade Curso.
 * Responsável por validar e aplicar todas as regras de negócio associadas aos cursos.
 */
public class CursoBLL {

    private CursoDAL              cursoDAL;
    private EstudanteDAL          estudanteDAL;
    private AnoLetivoDAL          anoLetivoDAL;
    private UnidadeCurricularDAL  unidadeCurricularDAL;

    private static final int QUORUM_MINIMO   = 5;
    private static final int MAX_UCS_POR_ANO = 5;
    private static final int DURACAO_CURSO   = 3;

    public CursoBLL(CursoDAL cursoDAL, EstudanteDAL estudanteDAL,
                    AnoLetivoDAL anoLetivoDAL, UnidadeCurricularDAL unidadeCurricularDAL) {
        this.cursoDAL             = cursoDAL;
        this.estudanteDAL         = estudanteDAL;
        this.anoLetivoDAL         = anoLetivoDAL;
        this.unidadeCurricularDAL = unidadeCurricularDAL;
    }

    /** Construtor de compatibilidade — cria os DAL extra com configuração padrão. */
    public CursoBLL(CursoDAL cursoDAL, EstudanteDAL estudanteDAL) {
        this(cursoDAL, estudanteDAL, new AnoLetivoDAL(), new UnidadeCurricularDAL());
    }

    // -------------------------------------------------------------------------
    // CRUD
    // -------------------------------------------------------------------------

    public Curso registarCurso(String nomeCurso, Departamento departamento, double valorPropina) {
        Utils.validarNome(nomeCurso);
        if (departamento == null)
            throw new IllegalArgumentException("O departamento não pode ser nulo.");
        if (valorPropina < 0)
            throw new IllegalArgumentException("O valor da propina não pode ser negativo.");
        if (procurarPorNome(nomeCurso) != null)
            throw new IllegalArgumentException("Já existe um curso com o nome: " + nomeCurso);

        Curso novoCurso = new Curso(nomeCurso, departamento);
        novoCurso.setValorPropina(valorPropina);
        cursoDAL.adicionarCurso(novoCurso);
        return novoCurso;
    }

    public Curso registarCurso(String nomeCurso, Departamento departamento) {
        return registarCurso(nomeCurso, departamento, 0.0);
    }

    public ArrayList<Curso> listarCursos() {
        return cursoDAL.listarCursos();
    }

    /** Com lista externa — mantido para compatibilidade. */
    public void atualizarNomeCurso(Curso curso, String novoNome, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        Utils.validarNome(novoNome);

        if (temEstudantesAlocados(curso, estudantes) || temDocentesAlocados(curso)) {
            throw new IllegalArgumentException(
                    "Não é possível alterar o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes ou docentes alocados.");
        }

        Curso existente = procurarPorNome(novoNome);
        if (existente != null && existente != curso) {
            throw new IllegalArgumentException("Já existe um curso com o nome: " + novoNome);
        }

        curso.setNomeCurso(novoNome);
        cursoDAL.atualizarCurso(curso);
    }

    /** Sem lista externa — obtém estudantes internamente. */
    public void atualizarNomeCurso(Curso curso, String novoNome) {
        atualizarNomeCurso(curso, novoNome, estudanteDAL.listarEstudantes());
    }

    /** Com lista externa — mantido para compatibilidade. */
    public void removerCurso(Curso curso, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }

        if (temEstudantesAlocados(curso, estudantes) || temDocentesAlocados(curso)) {
            throw new IllegalArgumentException(
                    "Não é possível remover o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes ou docentes alocados.");
        }

        cursoDAL.removerCurso(curso);
    }

    /** Sem lista externa — obtém estudantes internamente. */
    public void removerCurso(Curso curso) {
        removerCurso(curso, estudanteDAL.listarEstudantes());
    }

    // -------------------------------------------------------------------------
    // Pesquisa
    // -------------------------------------------------------------------------

    public Curso procurarPorNome(String nome) {
        Utils.validarNome(nome);
        for (Curso c : cursoDAL.listarCursos()) {
            if (c.getNomeCurso().equalsIgnoreCase(nome.trim())) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Curso> listarCursosPorDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new IllegalArgumentException("O departamento não pode ser nulo.");
        }
        ArrayList<Curso> resultado = new ArrayList<>();
        for (Curso c : cursoDAL.listarCursos()) {
            if (c.getDepartamento().equals(departamento)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Unidades Curriculares
    // -------------------------------------------------------------------------

    public void adicionarUnidadeCurricular(Curso curso, UnidadeCurricular uc) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (uc == null) {
            throw new IllegalArgumentException("A unidade curricular não pode ser nula.");
        }
        validarCursoNaoAlocado(curso, estudanteDAL.listarEstudantes(), "alterar");

        if (uc.getAnoCurricular() < 1 || uc.getAnoCurricular() > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular da UC deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        if (curso.getUnidades().contains(uc)) {
            throw new IllegalArgumentException(
                    "A unidade curricular '" + uc.getNome() + "' já está registada neste curso.");
        }

        int ucsNesteAno = 0;
        for (UnidadeCurricular u : curso.getUnidades()) {
            if (u.getAnoCurricular() == uc.getAnoCurricular()) ucsNesteAno++;
        }

        if (ucsNesteAno >= MAX_UCS_POR_ANO) {
            throw new IllegalArgumentException(
                    "O curso já atingiu o limite de " + MAX_UCS_POR_ANO +
                            " unidades curriculares para o ano " + uc.getAnoCurricular() + ".");
        }

        curso.adicionarUnidadeCurricular(uc);
        cursoDAL.atualizarCurso(curso);
    }

    public List<UnidadeCurricular> listarUCsPorAno(Curso curso, int anoCurricular) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (anoCurricular < 1 || anoCurricular > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        List<UnidadeCurricular> resultado = new ArrayList<>();
        for (UnidadeCurricular uc : curso.getUnidades()) {
            if (uc.getAnoCurricular() == anoCurricular) {
                resultado.add(uc);
            }
        }
        return resultado;
    }

    // -------------------------------------------------------------------------
    // Regras de negócio auxiliares
    // -------------------------------------------------------------------------

    private void validarCursoNaoAlocado(Curso curso, List<Estudante> estudantes, String operacao) {
        if (temEstudantesAlocados(curso, estudantes) || temDocentesAlocados(curso)) {
            throw new IllegalArgumentException(
                    "Não é possível " + operacao + " o curso '" + curso.getNomeCurso() +
                            "' porque tem estudantes ou docentes alocados.");
        }
    }

    public boolean temEstudantesAlocados(Curso curso, List<Estudante> estudantes) {
        if (estudantes == null || estudantes.isEmpty()) {
            return false;
        }
        for (Estudante e : estudantes) {
            for (Inscricao inscricao : e.getInscricoes()) {
                if (inscricao.getCurso() != null && inscricao.getCurso().equals(curso)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean temDocentesAlocados(Curso curso) {
        if (curso == null || curso.getUnidades() == null) {
            return false;
        }
        for (UnidadeCurricular uc : curso.getUnidades()) {
            if (uc.temDocenteResponsavel()) {
                return true;
            }
        }
        return false;
    }

    public int vagasUCsDisponiveis(Curso curso, int anoCurricular) {
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (anoCurricular < 1 || anoCurricular > DURACAO_CURSO) {
            throw new IllegalArgumentException(
                    "O ano curricular deve estar entre 1 e " + DURACAO_CURSO + ".");
        }

        int ucsNesteAno = 0;
        for (UnidadeCurricular u : curso.getUnidades()) {
            if (u.getAnoCurricular() == anoCurricular) ucsNesteAno++;
        }
        return MAX_UCS_POR_ANO - ucsNesteAno;
    }

    /**
     * Inicia um curso: valida todas as pré-condições, muda o estado para ATIVO,
     * activa as UCs elegíveis e actualiza as propinas de todos os alunos inscritos.
     *
     * Pré-condições verificadas:
     *  1. Curso existe e está em estado PENDENTE.
     *  2. Existe um ano letivo aberto.
     *  3. O curso tem pelo menos uma UC associada.
     *  4. O quórum mínimo de alunos inscritos está cumprido.
     */
    public void iniciarCurso(Curso curso, List<Estudante> estudantes) {
        if (curso == null) {
            throw new IllegalArgumentException("Curso não encontrado.");
        }
        if (estudantes == null) {
            throw new IllegalArgumentException("Lista de estudantes inválida.");
        }

        // 1 — Estado
        if (curso.getEstado() == null) {
            curso.setEstado("PENDENTE");
        }
        if (!curso.getEstado().equalsIgnoreCase("PENDENTE")) {
            throw new IllegalArgumentException("Só é possível iniciar cursos no estado PENDENTE.");
        }

        // 2 — Ano letivo aberto
        AnoLetivo anoAtual = anoLetivoDAL.procurarAnoAberto();
        if (anoAtual == null) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar o curso: não existe ano letivo aberto.\n"
                    + "  Abra um ano letivo antes de iniciar o curso.");
        }

        // 3 — Curso tem pelo menos 1 UC por cada ano curricular (1, 2 e 3) com momentos válidos
        validarUCsPorAno(curso, anoAtual.getAno());

        // 4 — Quórum
        int numeroInscritos  = contarEstudantesInscritosNoCurso(curso, estudantes);
        boolean primeiroAno  = isPrimeiroAnoLetivoDoCurso(curso, estudantes);
        int quorumNecessario = primeiroAno ? QUORUM_MINIMO : 1;

        if (numeroInscritos < quorumNecessario) {
            throw new IllegalArgumentException(
                    "Número mínimo de " + quorumNecessario + " estudante(s) não atingido "
                    + (primeiroAno ? "(1.º ano letivo do curso)" : "(anos seguintes)")
                    + ". Inscritos: " + numeroInscritos + ".");
        }

        // — Mudar estado para ATIVO e persistir
        curso.setEstado("ATIVO");
        cursoDAL.atualizarCurso(curso);

        // — Activar UCs do curso que já têm momentos configurados corretamente para este ano
        ativarUCsElegiveis(curso, anoAtual.getAno());

        // — Actualizar propinas e confirmar estado dos alunos inscritos
        actualizarPropinasDosInscritos(curso, estudantes);
        confirmarEstadoDosInscritos(curso, estudantes);
    }

    /**
     * Valida que o curso tem pelo menos 1 UC por cada ano curricular (1, 2 e 3).
     * O prof definiu este requisito explicitamente: "no mínimo uma UC por ano".
     */
    private void validarUCsPorAno(Curso curso, int anoLetivo) {
        if (curso.getUnidades() == null || curso.getUnidades().isEmpty()) {
            throw new IllegalArgumentException(
                    "Não é possível iniciar o curso '" + curso.getNomeCurso()
                    + "': não tem unidades curriculares associadas.");
        }
        for (int ano = 1; ano <= DURACAO_CURSO; ano++) {
            boolean temUC = false;
            boolean temUCComMomentos = false;
            for (UnidadeCurricular uc : curso.getUnidades()) {
                if (uc.getAnoCurricular() == ano) {
                    temUC = true;
                    if (uc.momentosValidosParaAno(anoLetivo)) {
                        temUCComMomentos = true;
                    }
                }
            }
            if (!temUC) {
                throw new IllegalArgumentException(
                        "Não é possível iniciar o curso '" + curso.getNomeCurso()
                        + "': falta pelo menos uma UC no " + ano + ".º ano curricular.");
            }
            if (!temUCComMomentos) {
                throw new IllegalArgumentException(
                        "Não é possível iniciar o curso '" + curso.getNomeCurso()
                        + "': nenhuma UC do " + ano + ".º ano tem momentos de avaliação válidos"
                        + " (soma de pesos = 100%) para o ano letivo "
                        + anoLetivo + "/" + (anoLetivo + 1) + ".");
            }
        }
    }

    /**
     * Confirma o estado de todos os alunos inscritos no curso:
     * garante que estão ATIVO e com o ano actualizado a partir da sua inscrição.
     * Persiste o estudante se houve alguma alteração.
     */
    private void confirmarEstadoDosInscritos(Curso curso, List<Estudante> estudantes) {
        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;

            boolean inscritoNesteCurso = false;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao.getCurso() != null
                        && inscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())) {
                    inscritoNesteCurso = true;
                    break;
                }
            }
            if (!inscritoNesteCurso) continue;

            boolean alterado = false;

            if (!"ATIVO".equalsIgnoreCase(estudante.getEstado())
                    && !"CONCLUIDO".equalsIgnoreCase(estudante.getEstado())) {
                estudante.setEstado("ATIVO");
                alterado = true;
            }

            // Garantir que o anoAtual corresponde ao ano da última inscrição neste curso
            Inscricao ultimaInscricao = estudante.getInscricoes()
                    .get(estudante.getInscricoes().size() - 1);
            if (ultimaInscricao.getCurso() != null
                    && ultimaInscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())
                    && estudante.getAnoAtual() != ultimaInscricao.getAnoDeCurso()) {
                estudante.setAnoAtual(ultimaInscricao.getAnoDeCurso());
                alterado = true;
            }

            if (alterado) {
                estudanteDAL.atualizarEstudante(estudante);
            }
        }
    }

    /**
     * Activa todas as UCs do curso que ainda estão inactivas
     * e já têm momentos válidos para o ano letivo actual.
     */
    private void ativarUCsElegiveis(Curso curso, int anoLetivo) {
        for (UnidadeCurricular uc : curso.getUnidades()) {
            if (!uc.isAtiva() && uc.momentosValidosParaAno(anoLetivo)) {
                uc.setAtiva(true);
                unidadeCurricularDAL.atualizarUnidade(uc);
            }
        }
    }

    /**
     * Para cada aluno inscrito neste curso, actualiza o valor total da propina
     * com o valor corrente configurado no curso (caso ainda não tenha sido paga).
     * Persiste o estudante depois da actualização.
     */
    private void actualizarPropinasDosInscritos(Curso curso, List<Estudante> estudantes) {
        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;

            boolean alterado = false;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao.getCurso() == null) continue;
                if (!inscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())) continue;
                if (inscricao.isPropinaPaga()) continue;

                inscricao.getPropina().setValorTotal(curso.getValorPropina());
                alterado = true;
            }

            if (alterado) {
                estudanteDAL.atualizarEstudante(estudante);
            }
        }
    }

    /**
     * Devolve true se todos os alunos inscritos neste curso estão no 1.º ano —
     * ou seja, é o primeiro ano letivo do curso.
     */
    private boolean isPrimeiroAnoLetivoDoCurso(Curso curso, List<Estudante> estudantes) {
        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao.getCurso() == null) continue;
                if (!inscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())) continue;
                if (inscricao.getAnoDeCurso() > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Devolve a lista de estudantes inscritos num curso.
     * Um estudante conta como inscrito se tiver pelo menos uma inscrição com aquele curso.
     */
    public List<Estudante> listarEstudantesInscritos(Curso curso, List<Estudante> estudantes) {
        List<Estudante> inscritos = new ArrayList<>();
        if (curso == null || estudantes == null) return inscritos;

        for (Estudante estudante : estudantes) {
            if (estudante.getInscricoes() == null) continue;
            for (Inscricao inscricao : estudante.getInscricoes()) {
                if (inscricao.getCurso() != null
                        && inscricao.getCurso().getNomeCurso().equalsIgnoreCase(curso.getNomeCurso())) {
                    inscritos.add(estudante);
                    break; // um estudante só conta uma vez
                }
            }
        }
        return inscritos;
    }

    public int contarEstudantesInscritosNoCurso(Curso curso, List<Estudante> estudantes) {
        return listarEstudantesInscritos(curso, estudantes).size();
    }

    public void atualizarValorPropina(Curso curso, double novoValor) {
        if (curso == null)
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        if (novoValor < 0)
            throw new IllegalArgumentException("O valor da propina não pode ser negativo.");
        curso.setValorPropina(novoValor);
        cursoDAL.atualizarCurso(curso);
    }
}