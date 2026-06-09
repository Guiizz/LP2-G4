package DAL;

import Model.Propina;

/**
 * Contrato de acesso a dados de Propina, comum às implementações
 * em ficheiro/memória (PropinaDAL — no-op) e em base de dados (DAL.BD.PropinaDAL_BD).
 */
public interface IPropinaDAL {

    /** Insere ou actualiza a propina de uma inscrição. */
    void guardarPropina(String numMecanografico, int anoLetivo, Propina propina);

    /** Devolve a propina de uma inscrição (null se não existir). */
    Propina carregarPropina(String numMecanografico, int anoLetivo);

    /** Remove a propina (e os seus pagamentos) de uma inscrição. */
    void removerPropina(String numMecanografico, int anoLetivo);
}
