package DAL;

import Model.Propina;

/**
 * Implementação CSV de IPropinaDAL.
 * Em modo ficheiro o valor pago da propina é guardado na coluna "valorPago"
 * da InscricaoDAL — esta classe é no-op.
 */
public class PropinaDAL implements IPropinaDAL {

    @Override
    public void guardarPropina(String numMecanografico, int anoLetivo, Propina propina) {
        // No-op: persistência feita pela InscricaoDAL no CSV de inscrições.
    }

    @Override
    public Propina carregarPropina(String numMecanografico, int anoLetivo) {
        // No-op: propina reconstituída pela InscricaoDAL ao carregar a inscrição.
        return null;
    }

    @Override
    public void removerPropina(String numMecanografico, int anoLetivo) {
        // No-op.
    }
}
