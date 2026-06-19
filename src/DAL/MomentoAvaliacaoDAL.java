package DAL;

import Model.MomentoAvaliacao;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação CSV de IMomentoAvaliacaoDAL.
 * Em modo ficheiro os momentos de avaliação são guardados embutidos na coluna
 * "momentos" do CSV de UnidadeCurricular (formato nome:peso:anoLetivo|...).
 * Esta classe é no-op — UnidadeCurricularDAL já trata da serialização/deserialização.
 */
public class MomentoAvaliacaoDAL implements IMomentoAvaliacaoDAL {

    @Override
    public void guardarMomentos(String nomeUC, List<MomentoAvaliacao> momentos) {
        // No-op: persistência feita pela UnidadeCurricularDAL no CSV da UC.
    }

    @Override
    public List<MomentoAvaliacao> listarPorUC(String nomeUC) {
        // No-op: momentos carregados directamente pela UnidadeCurricularDAL.
        return new ArrayList<>();
    }

    @Override
    public void removerPorUC(String nomeUC) {
        // No-op.
    }

    @Override
    public java.util.Map<String, List<MomentoAvaliacao>> listarTodosPorUC() {
        // No-op: momentos carregados directamente pela UnidadeCurricularDAL.
        return new java.util.LinkedHashMap<>();
    }
}
