package BLL;

import DAL.IAnoLetivoDAL;
import Model.AnoLetivo;
import Model.DadosEstudanteFecho;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AnoLetivoBLLTest {

    private AnoLetivoBLL bll;
    private StubAnoLetivoDAL dal;

    private static final LocalDate DATA_2025 = LocalDate.of(2025, 9, 1);
    private static final LocalDate DATA_2026 = LocalDate.of(2026, 9, 1);

    @BeforeEach
    void setUp() {
        dal = new StubAnoLetivoDAL();
        bll = new AnoLetivoBLL(dal);
    }

    // ── abrirAnoLetivo ────────────────────────────────────────────────────────

    @Test
    void abrirAnoLetivo_sucesso() {
        AnoLetivo al = bll.abrirAnoLetivo(2025, DATA_2025);
        assertNotNull(al);
        assertEquals(2025, al.getAno());
        assertTrue(al.isAberto());
    }

    @Test
    void abrirAnoLetivo_anoMenorQue2000_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.abrirAnoLetivo(1999, DATA_2025));
    }

    @Test
    void abrirAnoLetivo_dataNula_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.abrirAnoLetivo(2025, null));
    }

    @Test
    void abrirAnoLetivo_jaExisteAberto_lancaExcecao() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        // Há um aberto → não pode abrir outro
        assertThrows(IllegalArgumentException.class, () ->
            bll.abrirAnoLetivo(2026, DATA_2026));
    }

    @Test
    void abrirAnoLetivo_mesmoAnoJaExiste_lancaExcecao() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        dal.fecharAtual(); // fecha para não bloquear pelo "já existe aberto"
        assertThrows(IllegalArgumentException.class, () ->
            bll.abrirAnoLetivo(2025, DATA_2025)); // 2025 já tem registo
    }

    // ── removerAnoLetivo ──────────────────────────────────────────────────────

    @Test
    void removerAnoLetivo_fechadoMaisRecente_sucesso() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        dal.fecharAtual();
        assertDoesNotThrow(() -> bll.removerAnoLetivo(2025));
        assertNull(bll.consultarAnoAtual());
    }

    @Test
    void removerAnoLetivo_inexistente_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerAnoLetivo(2099));
    }

    @Test
    void removerAnoLetivo_estaAberto_lancaExcecao() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerAnoLetivo(2025));
    }

    @Test
    void removerAnoLetivo_naoEMaisRecente_lancaExcecao() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        dal.fecharAtual();
        bll.abrirAnoLetivo(2026, DATA_2026);
        dal.fecharAtual();
        // Mais recente é 2026; tentar remover 2025 falha
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerAnoLetivo(2025));
    }

    // ── consultas ─────────────────────────────────────────────────────────────

    @Test
    void consultarAnoAtual_semAberto_retornaNull() {
        assertNull(bll.consultarAnoAtual());
    }

    @Test
    void consultarAnoAtual_comAberto_retornaAno() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        AnoLetivo atual = bll.consultarAnoAtual();
        assertNotNull(atual);
        assertEquals(2025, atual.getAno());
    }

    @Test
    void consultarMaisRecente_variosFechados_retornaOUltimo() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        dal.fecharAtual();
        bll.abrirAnoLetivo(2026, DATA_2026);
        dal.fecharAtual();
        AnoLetivo mr = bll.consultarMaisRecente();
        assertNotNull(mr);
        assertEquals(2026, mr.getAno());
    }

    @Test
    void listarTodos_retornaAnosOrdenados() {
        bll.abrirAnoLetivo(2025, DATA_2025);
        dal.fecharAtual();
        bll.abrirAnoLetivo(2026, DATA_2026);
        ArrayList<AnoLetivo> todos = bll.listarTodos();
        assertEquals(2, todos.size());
    }

    // ===================== Stubs =====================

    static class StubAnoLetivoDAL implements IAnoLetivoDAL {
        private final ArrayList<AnoLetivo> lista = new ArrayList<>();

        void fecharAtual() {
            for (AnoLetivo al : lista) {
                if (al.isAberto()) { al.fechar(LocalDate.now()); break; }
            }
        }

        @Override
        public void adicionarAnoLetivo(AnoLetivo al) { lista.add(al); }

        @Override
        public boolean atualizarAnoLetivo(AnoLetivo alAtualizado) {
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getAno() == alAtualizado.getAno()) {
                    lista.set(i, alAtualizado);
                    return true;
                }
            }
            return false;
        }

        @Override
        public ArrayList<AnoLetivo> listarAnosLetivos() { return new ArrayList<>(lista); }

        @Override
        public void removerAnoLetivo(int ano) {
            lista.removeIf(al -> al.getAno() == ano);
        }

        @Override
        public AnoLetivo procurarPorAno(int ano) {
            return lista.stream()
                .filter(al -> al.getAno() == ano)
                .findFirst().orElse(null);
        }

        @Override
        public AnoLetivo procurarAnoAberto() {
            return lista.stream()
                .filter(AnoLetivo::isAberto)
                .findFirst().orElse(null);
        }

        @Override
        public AnoLetivo procurarMaisRecente() {
            return lista.stream()
                .max((a, b) -> Integer.compare(a.getAno(), b.getAno()))
                .orElse(null);
        }

        @Override
        public ArrayList<DadosEstudanteFecho> carregarDadosParaFecho(int ano) {
            return new ArrayList<>();
        }

        @Override
        public void persistirResultadoFecho(int ano, List<String> avancar, List<String> concluir) {}
    }
}
