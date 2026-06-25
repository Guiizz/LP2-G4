package BLL;

import DAL.IHorarioDAL;
import DAL.IUnidadeCurricularDAL;
import Model.BlocoHorario;
import Model.Horario;
import Model.UnidadeCurricular;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class HorarioBLLTest {

    private HorarioBLL bll;

    @BeforeEach
    void setUp() {
        bll = new HorarioBLL(new StubHorarioDAL(), new StubUCDAL());
    }

    // ── adicionarBloco ────────────────────────────────────────────────────────

    @Test
    void adicionarBloco_valido1h_sucesso() {
        BlocoHorario b = bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 60, "Mat");
        assertEquals("Segunda", b.getDiaSemana());
        assertEquals("18:00", b.getHoraInicio());
        assertEquals(60, b.getDuracao());
    }

    @Test
    void adicionarBloco_valido2h_sucesso() {
        BlocoHorario b = bll.adicionarBloco("CS", 1, 2024, "Terça", "18:00", 120, "Mat");
        assertEquals(120, b.getDuracao());
    }

    @Test
    void adicionarBloco_diaInvalido_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Domingo", "18:00", 60, "Mat"));
    }

    @Test
    void adicionarBloco_duracaoInvalida_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 90, "Mat"));
    }

    @Test
    void adicionarBloco_horaAntesLimite_lancaExcecao() {
        // 17:00 < 18:00
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "17:00", 60, "Mat"));
    }

    @Test
    void adicionarBloco_blocoUltrapassaLimite2330_lancaExcecao() {
        // 23:00 + 120min = 25:00 → ultrapassa 23:30
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "23:00", 120, "Mat"));
    }

    @Test
    void adicionarBloco_sobreposicaoPausaJantar_lancaExcecao() {
        // 19:30 + 120min = 21:30 cobre 20:00-20:30
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "19:30", 120, "Mat"));
    }

    @Test
    void adicionarBloco_inicioPausaJantar_lancaExcecao() {
        // início às 20:00 cai dentro da pausa
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "20:00", 60, "Mat"));
    }

    @Test
    void adicionarBloco_sobreposicaoComBlocoExistente_lancaExcecao() {
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 60, "Mat");
        // 18:30 está dentro de 18:00-19:00
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:30", 60, "Est"));
    }

    @Test
    void adicionarBloco_limiteUCSemanal6h_lancaExcecao() {
        // 3 × 2h = 6h. 4.ª bloco lança exceção.
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 120, "Mat");
        bll.adicionarBloco("CS", 1, 2024, "Terça",   "18:00", 120, "Mat");
        bll.adicionarBloco("CS", 1, 2024, "Quarta",  "18:00", 120, "Mat");
        assertThrows(IllegalArgumentException.class, () ->
            bll.adicionarBloco("CS", 1, 2024, "Quinta", "18:00", 60, "Mat"));
    }

    // ── removerBloco ──────────────────────────────────────────────────────────

    @Test
    void removerBloco_indiceValido_sucesso() {
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 60, "Mat");
        assertDoesNotThrow(() -> bll.removerBloco("CS", 1, 2024, 0));
    }

    @Test
    void removerBloco_semBlocos_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerBloco("CS", 1, 2024, 0));
    }

    @Test
    void removerBloco_indiceForaLimite_lancaExcecao() {
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 60, "Mat");
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerBloco("CS", 1, 2024, 5));
    }

    // ── proximaHoraLivre ──────────────────────────────────────────────────────

    @Test
    void proximaHoraLivre_semBlocos_retorna1800() {
        assertEquals("18:00", bll.proximaHoraLivre("CS", 1, 2024, "Segunda", 60));
    }

    @Test
    void proximaHoraLivre_saltaBlocoExistente() {
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 60, "Mat");
        assertEquals("19:00", bll.proximaHoraLivre("CS", 1, 2024, "Segunda", 60));
    }

    @Test
    void proximaHoraLivre_saltaPausaJantar() {
        // Bloco 18:00-20:00 → próxima livre deve ser 20:30
        bll.adicionarBloco("CS", 1, 2024, "Segunda", "18:00", 120, "Mat");
        assertEquals("20:30", bll.proximaHoraLivre("CS", 1, 2024, "Segunda", 60));
    }

    @Test
    void proximaHoraLivre_diaCompleto_lancaExcecao() {
        // 18-20 (2h) + 20:30-22:30 (2h) + 22:30-23:30 (1h) = 5h = dia cheio
        bll.adicionarBloco("CS", 1, 2024, "Terça", "18:00", 120, "UC1");
        bll.adicionarBloco("CS", 1, 2024, "Terça", "20:30", 120, "UC2");
        bll.adicionarBloco("CS", 1, 2024, "Terça", "22:30",  60, "UC3");
        assertThrows(IllegalArgumentException.class, () ->
            bll.proximaHoraLivre("CS", 1, 2024, "Terça", 60));
    }

    // ── parseMinutos (utilitário público) ─────────────────────────────────────

    @Test
    void parseMinutos_horaValida() {
        assertEquals(18 * 60, HorarioBLL.parseMinutos("18:00"));
        assertEquals(20 * 60 + 30, HorarioBLL.parseMinutos("20:30"));
    }

    @Test
    void parseMinutos_horaInvalida_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> HorarioBLL.parseMinutos("abc"));
        assertThrows(IllegalArgumentException.class, () -> HorarioBLL.parseMinutos(null));
    }

    // ===================== Stubs =====================

    static class StubHorarioDAL implements IHorarioDAL {
        private final List<Horario> lista = new ArrayList<>();

        @Override
        public Horario procurarOuCriar(String nomeCurso, int anoCurricular, int anoLetivo) {
            for (Horario h : lista) {
                if (h.getNomeCurso().equals(nomeCurso)
                        && h.getAnoCurricular() == anoCurricular
                        && h.getAnoLetivo() == anoLetivo) return h;
            }
            Horario novo = new Horario(nomeCurso, anoCurricular, anoLetivo);
            lista.add(novo);
            return novo;
        }

        @Override
        public List<Horario> listarTodos() { return new ArrayList<>(lista); }

        @Override
        public List<Horario> listarPorCurso(String nomeCurso) {
            return lista.stream()
                .filter(h -> h.getNomeCurso().equalsIgnoreCase(nomeCurso))
                .collect(Collectors.toList());
        }

        @Override
        public void guardar(Horario horario) {
            lista.removeIf(h -> h.getNomeCurso().equals(horario.getNomeCurso())
                    && h.getAnoCurricular() == horario.getAnoCurricular()
                    && h.getAnoLetivo() == horario.getAnoLetivo());
            lista.add(horario);
        }
    }

    static class StubUCDAL implements IUnidadeCurricularDAL {
        @Override public void adicionarUnidade(UnidadeCurricular u) {}
        @Override public boolean atualizarUnidade(UnidadeCurricular u) { return true; }
        @Override public ArrayList<UnidadeCurricular> listarUnidades() { return new ArrayList<>(); }
        @Override public void removerUnidade(UnidadeCurricular u) {}
        @Override public UnidadeCurricular procurarPorNome(String nome) { return null; }
        @Override public boolean atribuirDocenteResponsavel(String uc, String d) { return true; }
    }
}
