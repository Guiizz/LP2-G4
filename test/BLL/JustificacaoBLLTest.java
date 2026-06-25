package BLL;

import DAL.IJustificacaoDAL;
import DAL.ITipoJustificacaoDAL;
import Model.JustificacaoFalta;
import Model.TipoJustificacao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class JustificacaoBLLTest {

    private JustificacaoBLL bll;

    private static final LocalDate DATA = LocalDate.of(2024, 10, 15);

    @BeforeEach
    void setUp() {
        bll = new JustificacaoBLL(new StubJustificacaoDAL(), new StubTipoDAL());
    }

    // ── criarTipo ─────────────────────────────────────────────────────────────

    @Test
    void criarTipo_saude_sucesso() {
        TipoJustificacao t = bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        assertEquals("Doença", t.getNome());
        assertEquals(TipoJustificacao.CATEGORIA_SAUDE, t.getCategoria());
    }

    @Test
    void criarTipo_estatuto_sucesso() {
        TipoJustificacao t = bll.criarTipo("Atleta", TipoJustificacao.CATEGORIA_ESTATUTO);
        assertEquals(TipoJustificacao.CATEGORIA_ESTATUTO, t.getCategoria());
    }

    @Test
    void criarTipo_nomeVazio_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.criarTipo("", TipoJustificacao.CATEGORIA_SAUDE));
    }

    @Test
    void criarTipo_nomeNulo_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.criarTipo(null, TipoJustificacao.CATEGORIA_SAUDE));
    }

    @Test
    void criarTipo_categoriaInvalida_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.criarTipo("Viagem", "LAZER"));
    }

    @Test
    void criarTipo_duplicado_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        assertThrows(IllegalArgumentException.class, () ->
            bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE));
    }

    // ── removerTipo ───────────────────────────────────────────────────────────

    @Test
    void removerTipo_sucesso() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        assertDoesNotThrow(() -> bll.removerTipo("Doença"));
    }

    @Test
    void removerTipo_inexistente_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.removerTipo("NaoExiste"));
    }

    // ── pedirJustificacao ─────────────────────────────────────────────────────

    @Test
    void pedirJustificacao_sucesso() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        assertNotNull(j);
        assertEquals(JustificacaoFalta.PENDENTE, j.getEstado());
        assertEquals("123", j.getNumMecanografico());
    }

    @Test
    void pedirJustificacao_tipoInexistente_lancaExcecao() {
        assertThrows(IllegalArgumentException.class, () ->
            bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Inexistente"));
    }

    @Test
    void pedirJustificacao_duplicada_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        assertThrows(IllegalArgumentException.class, () ->
            bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença"));
    }

    @Test
    void pedirJustificacao_alunosDiferentes_semConflito() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        JustificacaoFalta j2 = bll.pedirJustificacao("456", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        assertEquals(JustificacaoFalta.PENDENTE, j2.getEstado());
    }

    // ── aprovarJustificacao ───────────────────────────────────────────────────

    @Test
    void aprovarJustificacao_sucesso() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.aprovarJustificacao(j);
        assertEquals(JustificacaoFalta.APROVADA, j.getEstado());
    }

    @Test
    void aprovarJustificacao_jaAprovada_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.aprovarJustificacao(j);
        assertThrows(IllegalArgumentException.class, () -> bll.aprovarJustificacao(j));
    }

    @Test
    void aprovarJustificacao_jaRejeitada_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.rejeitarJustificacao(j);
        assertThrows(IllegalArgumentException.class, () -> bll.aprovarJustificacao(j));
    }

    // ── rejeitarJustificacao ──────────────────────────────────────────────────

    @Test
    void rejeitarJustificacao_sucesso() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.rejeitarJustificacao(j);
        assertEquals(JustificacaoFalta.REJEITADA, j.getEstado());
    }

    @Test
    void rejeitarJustificacao_jaRejeitada_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.rejeitarJustificacao(j);
        assertThrows(IllegalArgumentException.class, () -> bll.rejeitarJustificacao(j));
    }

    @Test
    void rejeitarJustificacao_jaAprovada_lancaExcecao() {
        bll.criarTipo("Doença", TipoJustificacao.CATEGORIA_SAUDE);
        JustificacaoFalta j = bll.pedirJustificacao("123", "Mat", "CS", 2024, DATA, "18:00", "Doença");
        bll.aprovarJustificacao(j);
        assertThrows(IllegalArgumentException.class, () -> bll.rejeitarJustificacao(j));
    }

    // ===================== Stubs =====================

    static class StubTipoDAL implements ITipoJustificacaoDAL {
        private final List<TipoJustificacao> lista = new ArrayList<>();

        @Override
        public void adicionar(TipoJustificacao t) { lista.add(t); }

        @Override
        public void remover(String nome) {
            lista.removeIf(t -> t.getNome().equalsIgnoreCase(nome));
        }

        @Override
        public TipoJustificacao procurarPorNome(String nome) {
            return lista.stream()
                .filter(t -> t.getNome().equalsIgnoreCase(nome))
                .findFirst().orElse(null);
        }

        @Override
        public List<TipoJustificacao> listarTodos() { return new ArrayList<>(lista); }
    }

    static class StubJustificacaoDAL implements IJustificacaoDAL {
        private final List<JustificacaoFalta> lista = new ArrayList<>();

        @Override public void adicionar(JustificacaoFalta j) { lista.add(j); }

        @Override public void atualizar(JustificacaoFalta j) {
            // estado já foi mutado diretamente no objeto
        }

        @Override
        public List<JustificacaoFalta> listarPorEstudante(String num) {
            return lista.stream()
                .filter(j -> j.getNumMecanografico().equals(num))
                .collect(Collectors.toList());
        }

        @Override
        public List<JustificacaoFalta> listarPendentes() {
            return lista.stream()
                .filter(j -> JustificacaoFalta.PENDENTE.equals(j.getEstado()))
                .collect(Collectors.toList());
        }

        @Override
        public List<JustificacaoFalta> listarTodas() { return new ArrayList<>(lista); }
    }
}
