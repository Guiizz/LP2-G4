package BLL;

import DAL.IAnoLetivoDAL;
import DAL.IAvaliacaoDAL;
import Model.AnoLetivo;
import Model.Avaliacao;
import Model.DadosEstudanteFecho;
import Model.UnidadeCurricular;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AvaliacaoBLLTest {

    private AvaliacaoBLL bll;
    private StubAvaliacaoDAL avaliacaoDAL;
    private StubAnoLetivoDAL anoLetivoDAL;

    // Ano letivo aberto: 2026/2027 (abertura 2026-09-01, fim 2027-08-31)
    private static final Date DATA_VALIDA  = toDate(LocalDate.of(2027, 1, 15));
    private static final Date DATA_VALIDA2 = toDate(LocalDate.of(2027, 2,  1));
    private static final Date DATA_VALIDA3 = toDate(LocalDate.of(2027, 3,  1));
    private static final Date DATA_VALIDA4 = toDate(LocalDate.of(2027, 4,  1));
    private static final Date DATA_PASSADA = toDate(LocalDate.of(2024, 1,  1));
    private static final Date DATA_FORA    = toDate(LocalDate.of(2030, 1,  1));

    private static final UnidadeCurricular UC_MAT = new UnidadeCurricular("Matemática", 6);
    private static final String CURSO = "Informática";

    @BeforeEach
    void setUp() {
        avaliacaoDAL  = new StubAvaliacaoDAL();
        anoLetivoDAL  = new StubAnoLetivoDAL();
        bll = new AvaliacaoBLL(avaliacaoDAL, anoLetivoDAL);
    }

    // ── distribuição automática de pesos ──────────────────────────────────────

    @Test
    void registar_primeiroMomento_peso100() {
        Avaliacao a = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0);
        assertEquals(100.0, a.getPeso(), 0.001);
    }

    @Test
    void registar_segundoMomento_ambos50() {
        Avaliacao a1 = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA,  0);
        Avaliacao a2 = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA2, 0);
        assertEquals(50.0, a1.getPeso(), 0.001);
        assertEquals(50.0, a2.getPeso(), 0.001);
    }

    @Test
    void registar_terceiroMomento_3333_3333_3334() {
        Avaliacao a1 = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA,  0);
        Avaliacao a2 = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA2, 0);
        Avaliacao a3 = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA3, 0);
        assertEquals(33.33, a1.getPeso(), 0.01);
        assertEquals(33.33, a2.getPeso(), 0.01);
        assertEquals(33.34, a3.getPeso(), 0.01);
    }

    @Test
    void registar_maxMomentos3_lancaExcecao() {
        bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA,  0);
        bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA2, 0);
        bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA3, 0);
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA4, 0));
    }

    // ── validações de entrada ─────────────────────────────────────────────────

    @Test
    void registar_ucsNulas_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(null, CURSO, DATA_VALIDA, 0));
    }

    @Test
    void registar_ucsVazias_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(new ArrayList<>(), CURSO, DATA_VALIDA, 0));
    }

    @Test
    void registar_dataNula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, null, 0));
    }

    @Test
    void registar_dataPassada_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_PASSADA, 0));
    }

    @Test
    void registar_cursoBranco_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), "   ", DATA_VALIDA, 0));
    }

    @Test
    void registar_cursoNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), null, DATA_VALIDA, 0));
    }

    // ── validações de ano letivo ───────────────────────────────────────────────

    @Test
    void registar_semAnoLetivoAberto_lancaExcecao() {
        anoLetivoDAL.setAnoAberto(null);
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0));
    }

    @Test
    void registar_dataForaAnoLetivo_lancaExcecao() {
        // 2030-01-01 está fora do intervalo 2026-09-01 a 2027-08-31
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_FORA, 0));
    }

    // ── duplicados ────────────────────────────────────────────────────────────

    @Test
    void registar_avaliacaoDuplicada_lancaExcecao() {
        bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0);
        // Mesma UC, curso e data → duplicado
        assertThrows(IllegalArgumentException.class, () ->
            bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0));
    }

    // ── removerAvaliacao ──────────────────────────────────────────────────────

    @Test
    void removerAvaliacao_nula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerAvaliacao(null));
    }

    @Test
    void removerAvaliacao_sucesso() {
        Avaliacao a = bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0);
        assertDoesNotThrow(() -> bll.removerAvaliacao(a));
        assertTrue(bll.listarAvaliacoes().isEmpty());
    }

    // ── procurar ──────────────────────────────────────────────────────────────

    @Test
    void procurarPorUC_nula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.procurarPorUC(null));
    }

    @Test
    void procurarPorUCeCurso_deveRetornarSoMomentosDoCurso() {
        bll.registarAvaliacao(List.of(UC_MAT), CURSO, DATA_VALIDA, 0);
        ArrayList<Avaliacao> resultado = bll.procurarPorUCeCurso(UC_MAT, CURSO);
        assertEquals(1, resultado.size());
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    private static Date toDate(LocalDate d) {
        return Date.from(d.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    // ===================== Stubs =====================

    static class StubAvaliacaoDAL implements IAvaliacaoDAL {
        private final ArrayList<Avaliacao> lista = new ArrayList<>();

        @Override public void adicionarAvaliacao(Avaliacao a) { lista.add(a); }

        @Override public ArrayList<Avaliacao> listarAvaliacoes() { return new ArrayList<>(lista); }

        @Override public boolean atualizarAvaliacao(Avaliacao antiga, Avaliacao nova) {
            int idx = lista.indexOf(antiga);
            if (idx < 0) return false;
            lista.set(idx, nova);
            return true;
        }

        @Override public void removerAvaliacao(Avaliacao a) { lista.remove(a); }

        @Override public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
            ArrayList<Avaliacao> r = new ArrayList<>();
            for (Avaliacao a : lista)
                if (a.getUc() != null && a.getUc().contains(uc)) r.add(a);
            return r;
        }

        @Override public ArrayList<Avaliacao> procurarPorData(Date data) {
            ArrayList<Avaliacao> r = new ArrayList<>();
            for (Avaliacao a : lista)
                if (data.equals(a.getData())) r.add(a);
            return r;
        }
    }

    static class StubAnoLetivoDAL implements IAnoLetivoDAL {
        // Por defeito: ano letivo 2026/2027 aberto desde 2026-09-01
        private AnoLetivo anoAberto = new AnoLetivo(2026, AnoLetivo.ESTADO_ABERTO,
            LocalDate.of(2026, 9, 1), null);

        void setAnoAberto(AnoLetivo al) { this.anoAberto = al; }

        @Override public void adicionarAnoLetivo(AnoLetivo al) {}
        @Override public boolean atualizarAnoLetivo(AnoLetivo al) { return true; }
        @Override public ArrayList<AnoLetivo> listarAnosLetivos() {
            ArrayList<AnoLetivo> r = new ArrayList<>();
            if (anoAberto != null) r.add(anoAberto);
            return r;
        }
        @Override public void removerAnoLetivo(int ano) {}
        @Override public AnoLetivo procurarPorAno(int ano) {
            return (anoAberto != null && anoAberto.getAno() == ano) ? anoAberto : null;
        }
        @Override public AnoLetivo procurarAnoAberto()   { return anoAberto; }
        @Override public AnoLetivo procurarMaisRecente() { return anoAberto; }
        @Override public ArrayList<DadosEstudanteFecho> carregarDadosParaFecho(int ano) { return new ArrayList<>(); }
        @Override public void persistirResultadoFecho(int ano, List<String> a, List<String> c) {}
    }
}
