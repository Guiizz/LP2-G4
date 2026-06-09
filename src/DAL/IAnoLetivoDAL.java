package DAL;

import Model.AnoLetivo;
import Model.DadosEstudanteFecho;

import java.util.ArrayList;
import java.util.List;

/**
 * Contrato de acesso a dados de AnoLetivo, comum às implementações
 * em ficheiro/memória (AnoLetivoDAL) e em base de dados (DAL.BD.AnoLetivoDAL_BD).
 */
public interface IAnoLetivoDAL {

    void adicionarAnoLetivo(AnoLetivo anoLetivo);

    boolean atualizarAnoLetivo(AnoLetivo anoLetivoAtualizado);

    ArrayList<AnoLetivo> listarAnosLetivos();

    void removerAnoLetivo(int ano);

    AnoLetivo procurarPorAno(int ano);

    AnoLetivo procurarAnoAberto();

    AnoLetivo procurarMaisRecente();

    // ── Fecho de Ano Letivo (leitura SQL estruturada) ─────────────────────────

    /**
     * Lê em simultâneo Estudante + Inscricao + Propina num único JOIN,
     * devolvendo apenas os campos necessários para o fecho de ano letivo.
     * Implementação CSV devolve lista vazia (BLL usa path de objetos).
     */
    ArrayList<DadosEstudanteFecho> carregarDadosParaFecho(int anoLetivo);

    /**
     * Persiste os resultados do fecho diretamente em BD via SQL:
     *   - Estudantes em {@code numMecsAvancar}: anoAtual++ + nova inscrição + propina
     *   - Estudantes em {@code numMecsConcluir}: estado = 'CONCLUIDO'
     * Implementação CSV é no-op (BLL persiste via objetos Estudante).
     */
    void persistirResultadoFecho(int anoLetivo,
                                  List<String> numMecsAvancar,
                                  List<String> numMecsConcluir);
}
