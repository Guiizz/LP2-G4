package DAL;

import Model.Estudante;
import Model.Inscricao;

import java.util.ArrayList;

/**
 * Contrato de acesso a dados de Inscricao, comum às implementações
 * em ficheiro/memória (InscricaoDAL) e em base de dados (DAL.BD.InscricaoDAL_BD).
 */
public interface IInscricaoDAL {

    /**
     * Carrega todas as inscrições e associa-as aos respetivos estudantes.
     * Usa cursoDAL para resolver o Curso a partir do nome.
     */
    void carregarInscricoes(ArrayList<Estudante> estudantes, CursoDAL cursoDAL);

    /**
     * Persiste todas as inscrições de todos os estudantes.
     * (CSV: reescreve o ficheiro. BD: faz upsert de todas as linhas.)
     */
    void guardarInscricoes(ArrayList<Estudante> estudantes);

    /**
     * Adiciona/actualiza imediatamente a inscrição mais recente de um estudante.
     * Útil para BD onde cada operação se reflecte de imediato.
     */
    void adicionarInscricao(String numMecanografico, Inscricao inscricao);

    /**
     * Atualiza imediatamente uma inscrição existente (ex.: pagamento de propina, notas).
     */
    void atualizarInscricao(String numMecanografico, Inscricao inscricao);
}
