package Controller;

import BLL.EstudanteBLL;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;

import java.util.ArrayList;

/**
 * Controlador responsável por gerir as operações relacionadas com a entidade Inscricao.
 * Serve de intermediário entre a camada de apresentação e a camada de negócio.
 * <p>
 * Regras de negócio aplicadas:
 * <ul>
 *   <li>Um estudante só pode estar inscrito num único curso.</li>
 *   <li>Só existe uma turma por ano letivo de um curso.</li>
 *   <li>Para progredir de ano, o estudante necessita de mais de 60% de aproveitamento.</li>
 * </ul>
 */
public class InscricaoController {

    private EstudanteBLL estudanteBLL;

    /**
     * Construtor do InscricaoController.
     * Inicializa a camada de negócio do Estudante, responsável pela lógica de inscrições.
     *
     * @param estudanteBLL Instância da camada BLL do Estudante.
     */
    public InscricaoController(EstudanteBLL estudanteBLL) {
        this.estudanteBLL = estudanteBLL;
    }

    /**
     * Inscreve um estudante no primeiro ano de um curso.
     * Cria a inscrição inicial e associa-a ao estudante.
     * Um estudante só pode estar inscrito num único curso.
     *
     * @param estudante  O estudante a inscrever.
     * @param curso      O curso no qual o estudante se vai inscrever.
     * @param anoLetivo  O ano letivo de início (ex: 2026 para 2026/2027).
     * @return A inscrição criada.
     * @throws IllegalArgumentException Se o estudante ou curso forem nulos,
     *                                  ou se o estudante já tiver inscrições registadas.
     */
    public Inscricao inscreverEstudante(Estudante estudante, Curso curso, int anoLetivo) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        if (!estudante.getInscricoes().isEmpty()) {
            throw new IllegalArgumentException(
                    "O estudante '" + estudante.getNome() + "' já se encontra inscrito num curso.");
        }

        Inscricao inscricao = new Inscricao(anoLetivo, 1, curso);
        estudante.adicionarInscricao(inscricao);
        estudanteBLL.atualizarEstudante(
                estudante.getNumMecanografico(),
                estudante.getNome(),
                estudante.getMorada()
        );
        return inscricao;
    }

    /**
     * Verifica se um estudante cumpre os requisitos para progredir para o ano seguinte.
     * O estudante necessita de mais de 60% de aproveitamento nas UCs do ano atual.
     *
     * @param estudante O estudante a verificar.
     * @throws IllegalArgumentException Se o estudante for nulo, já estiver no último ano,
     *                                  não tiver inscrições ou não cumprir os 60%.
     */
    public void verificarProgressaoAno(Estudante estudante) {
        estudanteBLL.podeProgredirAno(estudante);
    }

    /**
     * Avança o estudante para o próximo ano letivo, criando uma nova inscrição.
     * Valida previamente se o estudante cumpre a regra dos 60% de aproveitamento.
     *
     * @param estudante    O estudante a passar de ano.
     * @param novaInscricao A inscrição para o novo ano letivo (já construída com o
     *                      ano letivo, ano de curso e curso corretos).
     * @throws IllegalArgumentException Se o estudante ou a inscrição forem nulos,
     *                                  ou se não cumprir os requisitos de progressão.
     */
    public void passarDeAno(Estudante estudante, Inscricao novaInscricao) {
        estudanteBLL.passarDeAno(estudante, novaInscricao);
    }

    /**
     * Lista todas as inscrições de um estudante.
     *
     * @param estudante O estudante cujas inscrições se pretendem listar.
     * @return Lista de inscrições do estudante.
     * @throws IllegalArgumentException Se o estudante for nulo.
     */
    public ArrayList<Inscricao> listarInscricoes(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }
        return estudante.getInscricoes();
    }

    /**
     * Obtém a inscrição atual (mais recente) de um estudante.
     *
     * @param estudante O estudante a consultar.
     * @return A inscrição do ano atual, ou null se não existirem inscrições.
     * @throws IllegalArgumentException Se o estudante for nulo.
     */
    public Inscricao obterInscricaoAtual(Estudante estudante) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }
        ArrayList<Inscricao> inscricoes = estudante.getInscricoes();
        if (inscricoes.isEmpty()) {
            return null;
        }
        return inscricoes.get(inscricoes.size() - 1);
    }

    /**
     * Verifica se um estudante já se encontra inscrito num determinado curso.
     *
     * @param estudante O estudante a verificar.
     * @param curso     O curso a verificar.
     * @return true se o estudante tiver pelo menos uma inscrição nesse curso, false caso contrário.
     * @throws IllegalArgumentException Se o estudante ou o curso forem nulos.
     */
    public boolean estaInscritoNoCurso(Estudante estudante, Curso curso) {
        if (estudante == null) {
            throw new IllegalArgumentException("O estudante não pode ser nulo.");
        }
        if (curso == null) {
            throw new IllegalArgumentException("O curso não pode ser nulo.");
        }
        for (Inscricao inscricao : estudante.getInscricoes()) {
            if (inscricao.getCurso() != null && inscricao.getCurso().equals(curso)) {
                return true;
            }
        }
        return false;
    }
}