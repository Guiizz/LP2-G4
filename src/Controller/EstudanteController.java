package Controller;

import BLL.EstudanteBLL;
import Model.Estudante;
import Model.Inscricao;

import java.time.LocalDate;
import java.util.ArrayList;
/**
 * Controller responsável pela ligação entre a View e a BLL do Estudante.
 */
public class EstudanteController {
    private EstudanteBLL estudanteBLL;

    /**
     * Construtor do EstudanteController.
     * @param estudanteBLL A camada BLL a utilizar.
     */
    public EstudanteController(EstudanteBLL estudanteBLL) {
        this.estudanteBLL = estudanteBLL;
    }

    /**
     *
     * @param nome Nome do Estudante.
     * @param dataNascimento Data de nascimento do estudante.
     * @param nif Número de Identificação fical do estudante.
     * @param morada Morada do estudante.
     * @return
     */
    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        return estudanteBLL.registarEstudante(nome, dataNascimento, nif, morada);
    }

    /**
     * Lista todos os estudantes registados no sistema.
     * @return Lista de estudantes.
     */
    public ArrayList<Estudante> listarEstudante(){
        return estudanteBLL.listarEstudante();
    }

    /**
     * Atualiza nome e morada de um estudante (sem inscrições ativas).
     * @param numMecanografico Número mecanográfico do estudante.
     * @param novoNome Novo nome.
     * @param novaMorada Nova morada.
     */
    public void atualizarEstudante(String numMecanografico, String novoNome, String novaMorada){
        estudanteBLL.atualizarEstudante(numMecanografico,novoNome,novaMorada);
    }

    /**
     * Remove um estudante sem inscrições ativas.
     * @param numMecanografico Número mecanográfico do estudante a remover.
     */
    public void removerEstudante(String numMecanografico){
        estudanteBLL.removerEstudante(numMecanografico);
    }

    /**
     * Procura um estudante pelo número mecanográfico.
     * @param numMecanografico Número mecanográfico.
     * @return O estudante encontrado.
     */
    public Estudante procurarPorNumMecanografico(String numMecanografico){
        return estudanteBLL.procurarPorNumMecanografico(numMecanografico);
    }

    /**
     * Procura um estudante pelo NIF.
     * @param nif NIF do estudante.
     * @return O estudante encontrado.
     */
    public Estudante procurarPorNif(String nif){
        return estudanteBLL.procurarPorNif(nif);
    }

    /**
     * Autentica um estudante com e-mail e palavra-passe.
     * @param email E-mail do estudante.
     * @param password Palavra-passe do estudante.
     * @return O estudante autenticado.
     */
    public Estudante autenticarEstudante(String email, String password){
        return estudanteBLL.autenticarEmail(email,password);
    }
    /**
     * Altera a password do estudante e marca o primeiro login como concluído.
     * @param estudante O estudante a alterar.
     * @param novaPassword A nova password.
     */
    public void alterarPassword(Estudante estudante, String novaPassword) {
        estudanteBLL.alterarPassword(estudante, novaPassword);
    }

    /**
     * Verifica se o estudante pode progredir para o ano seguinte (regra dos 60%).
     * @param estudante O estudante a verificar.
     */
    public void verificarProgressaoAno(Estudante estudante){
        estudanteBLL.podeProgredirAno(estudante);
    }
    /**
     * Avança o estudante para o próximo ano letivo, criando a nova inscrição.
     * @param estudante O estudante a passar de ano.
     * @param novaInscricao A inscrição para o novo ano.
     */
    public void passarAno(Estudante estudante, Inscricao novaInscricao){
        estudanteBLL.passarDeAno(estudante,novaInscricao);
    }
}
