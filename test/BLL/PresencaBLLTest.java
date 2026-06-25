package BLL;

import DAL.IPresencaDAL;
import DAL.IRegistoAulaDAL;
import Model.Presenca;
import Model.RegistoAula;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class PresencaBLLTest {

    private PresencaBLL bll;
    private StubRegistoAulaDAL registoDAL;
    private StubPresencaDAL presencaDAL;

    private static final String UC      = "Matemática";
    private static final String CURSO   = "CS";
    private static final int    ANO_LET = 2024;
    private static final LocalDate DATA = LocalDate.of(2024, 10, 15);
    private static final String HORA    = "18:00";
    private static final String DOCENTE = "ABC";
    private static final String ALUNO   = "1234567";

    @BeforeEach
    void setUp() {
        registoDAL = new StubRegistoAulaDAL();
        presencaDAL = new StubPresencaDAL();
        bll = new PresencaBLL(registoDAL, presencaDAL);
    }

    // ── marcarAulaDocente ─────────────────────────────────────────────────────

    @Test
    void marcarAulaDocente_sucesso() {
        RegistoAula r = bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        assertNotNull(r);
        assertEquals(DOCENTE, r.getSiglaDocente());
        assertFalse(r.isTerminada());
    }

    @Test
    void marcarAulaDocente_siglaVazia_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarAulaDocente("", UC, CURSO, ANO_LET, DATA, HORA));
    }

    @Test
    void marcarAulaDocente_siglaNula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarAulaDocente(null, UC, CURSO, ANO_LET, DATA, HORA));
    }

    @Test
    void marcarAulaDocente_jaMarcada_lancaExcecao() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA));
    }

    // ── marcarPresencaEstudante ───────────────────────────────────────────────

    @Test
    void marcarPresenca_sucesso() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        Presenca p = bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA);
        assertNotNull(p);
        assertTrue(p.isPresente());
    }

    @Test
    void marcarPresenca_semAulaDocente_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA));
    }

    @Test
    void marcarPresenca_aulaJaTerminada_lancaExcecao() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of());
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA));
    }

    @Test
    void marcarPresenca_duplicada_lancaExcecao() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA);
        assertThrows(IllegalArgumentException.class, () ->
            bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA));
    }

    // ── terminarAula ──────────────────────────────────────────────────────────

    @Test
    void terminarAula_registaFaltaParaAusente() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        String ausente = "9999999";
        bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA);
        // ALUNO presente, ausente tem falta
        int faltas = bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO, ausente));
        assertEquals(1, faltas);
    }

    @Test
    void terminarAula_todosPresentes_semFaltas() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA);
        int faltas = bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO));
        assertEquals(0, faltas);
    }

    @Test
    void terminarAula_todosAusentes_todosFaltam() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        int faltas = bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO, "2222222"));
        assertEquals(2, faltas);
    }

    @Test
    void terminarAula_semAula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of()));
    }

    @Test
    void terminarAula_jaTerminada_lancaExcecao() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of());
        assertThrows(IllegalArgumentException.class, () ->
            bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of()));
    }

    // ── listarFaltasEstudante ─────────────────────────────────────────────────

    @Test
    void listarFaltas_soFaltas() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO));
        List<Presenca> faltas = bll.listarFaltasEstudante(ALUNO);
        assertEquals(1, faltas.size());
        assertFalse(faltas.get(0).isPresente());
    }

    @Test
    void listarFaltas_presenteNaoContaFalta() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.marcarPresencaEstudante(ALUNO, UC, CURSO, ANO_LET, DATA, HORA);
        bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO));
        List<Presenca> faltas = bll.listarFaltasEstudante(ALUNO);
        assertTrue(faltas.isEmpty());
    }

    // ── listarAulasSemPresencaEstudante ───────────────────────────────────────

    @Test
    void listarAulasSemPresenca_aulaNaoTerminada_ignorada() {
        // Aula marcada mas não terminada → não conta como falta
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        List<RegistoAula> semPresenca = bll.listarAulasSemPresencaEstudante(ALUNO, UC, CURSO, ANO_LET);
        assertTrue(semPresenca.isEmpty());
    }

    @Test
    void listarAulasSemPresenca_ausente_apareceNaLista() {
        bll.marcarAulaDocente(DOCENTE, UC, CURSO, ANO_LET, DATA, HORA);
        bll.terminarAula(UC, CURSO, ANO_LET, DATA, HORA, List.of(ALUNO)); // ALUNO ficou com falta
        List<RegistoAula> semPresenca = bll.listarAulasSemPresencaEstudante(ALUNO, UC, CURSO, ANO_LET);
        assertEquals(1, semPresenca.size());
    }

    // ===================== Stubs =====================

    static class StubRegistoAulaDAL implements IRegistoAulaDAL {
        private final List<RegistoAula> lista = new ArrayList<>();

        @Override
        public void adicionar(RegistoAula r) { lista.add(r); }

        @Override
        public boolean existe(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String hora) {
            return procurar(nomeUC, nomeCurso, anoLetivo, data, hora) != null;
        }

        @Override
        public RegistoAula procurar(String nomeUC, String nomeCurso, int anoLetivo, LocalDate data, String hora) {
            for (RegistoAula r : lista)
                if (r.corresponde(nomeUC, nomeCurso, anoLetivo, data, hora)) return r;
            return null;
        }

        @Override public void guardar() {}

        @Override public void guardarRegisto(RegistoAula r) {
            // estado terminada já foi mutado no objeto em memória
        }

        @Override
        public List<RegistoAula> listarPorDocente(String sigla) {
            return lista.stream()
                .filter(r -> r.getSiglaDocente().equalsIgnoreCase(sigla))
                .collect(Collectors.toList());
        }

        @Override
        public List<RegistoAula> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
            return lista.stream()
                .filter(r -> r.getNomeUC().equalsIgnoreCase(nomeUC)
                    && r.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && r.getAnoLetivo() == anoLetivo)
                .collect(Collectors.toList());
        }

        @Override
        public List<RegistoAula> listarTodas() { return new ArrayList<>(lista); }
    }

    static class StubPresencaDAL implements IPresencaDAL {
        private final List<Presenca> lista = new ArrayList<>();

        @Override
        public void adicionar(Presenca p) { lista.add(p); }

        @Override
        public boolean existe(String num, String nomeUC, String nomeCurso, int anoLetivo,
                              LocalDate data, String hora) {
            return lista.stream()
                .anyMatch(p -> p.corresponde(num, nomeUC, nomeCurso, anoLetivo, data, hora));
        }

        @Override
        public List<Presenca> listarPorEstudante(String num) {
            return lista.stream()
                .filter(p -> p.getNumMecanografico().equals(num))
                .collect(Collectors.toList());
        }

        @Override
        public List<Presenca> listarPorUCeCurso(String nomeUC, String nomeCurso, int anoLetivo) {
            return lista.stream()
                .filter(p -> p.getNomeUC().equalsIgnoreCase(nomeUC)
                    && p.getNomeCurso().equalsIgnoreCase(nomeCurso)
                    && p.getAnoLetivo() == anoLetivo)
                .collect(Collectors.toList());
        }

        @Override
        public List<Presenca> listarTodas() { return new ArrayList<>(lista); }
    }
}
