package BLL;

import DAL.EstudanteDAL;
import Model.Avaliacao;
import Model.Curso;
import Model.Estudante;
import Model.Inscricao;
import Utils.Utils;
import Utils.ServicoEmail;
import java.time.LocalDate;
import java.util.ArrayList;


public class EstudanteBLL {
    private EstudanteDAL estudanteDAL;

    /**
     * Construtor da classe EstudanteBLL.
     * @param estudanteDAL A camada DAL a ser utilizada.
     */
    public EstudanteBLL(EstudanteDAL estudanteDAL) {
        this.estudanteDAL = estudanteDAL;
    }

    /**
     * Regista um novo estudante ao sistema.
     * Valida todos os dados e se o NIF não esta duplicado.
     * @param nome O nome do estudante.
     * @param dataNascimento A data de nascimento do estudante.
     * @param nif O nif do estudante.
     * @param morada A morada do estudante.
     * @param emailReal O e-mail real do estudante para envio das credenciais.
     * @return O estudante criado.
     * @throws IllegalArgumentException Se alguma validação falhar.
     */
    public Estudante registarEstudante(String nome, LocalDate dataNascimento, String nif, String morada) {
        Utils.validarNome(nome);
        Utils.validarDataNascimento(dataNascimento);
        Utils.validarNif(nif);
        Utils.validarMorada(morada);

        if (estudanteDAL.procurarPorNif(nif) != null) {
            throw new IllegalArgumentException("Já existe um estudante com este NIF!");
        }

        Estudante novoEstudante = new Estudante(nome, dataNascimento, nif, morada);
        estudanteDAL.adicionarEstudante(novoEstudante);

        ServicoEmail.enviarCredenciais(
                novoEstudante.getNumMecanografico() + "@issmf.pt",
                "Issmf" + novoEstudante.getNumMecanografico(),
                "Estudante"
        );
        return novoEstudante;
    }

    /**
     * Lista todos os estudantes do sistema.
     * @return A lista de estudantes.
     */
    public ArrayList <Estudante> listarEstudante(){
        return estudanteDAL.listarEstudantes();
    }

    /**
     * Atualiza os dados de um estudante que existente.
     * Apenas nome e morada podem ser alterados.
     * Não é permitido alterar se o estudante tiver inscrições ativas.
     * @param numMecanografico O número mecanográfico do estudante.
     * @param novoNome O novo nome.
     * @param novaMorada A nova morada.
     */
    public void atualizarEstudante(String numMecanografico, String novoNome, String novaMorada){
        if (numMecanografico == null || numMecanografico.trim().isEmpty()){
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);
        if (estudante == null){
            throw new IllegalArgumentException("O estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }

        if (!estudante.getInscricoes().isEmpty()){
            throw new IllegalArgumentException("Não é possivel alterar os dados de um estudante com inscrições ativas");
        }

        Utils.validarNome(novoNome);
        Utils.validarMorada(novaMorada);

        estudante.setNome(novoNome);
        estudante.setMorada(novaMorada);
        estudanteDAL.atualizarEstudante(estudante);
    }
    /**
     * Remove um estudante do sistema pelo número mecanográfico.
     * Não é permitido remover um estudante com inscrições ativas.
     * @param numMecanografico O número mecanográfico do estudante a remover.
     * @throws IllegalArgumentException Se o estudante não for encontrado ou tiver inscrições ativas.
     */
    public void removerEstudante(String numMecanografico){
        if (numMecanografico == null || numMecanografico.trim().isEmpty()){
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);
        if (estudante == null){
            throw new IllegalArgumentException("O estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }

        if (!estudante.getInscricoes().isEmpty()){
            throw new IllegalArgumentException("Não é possivel remover um estudante com inscrições ativas");
        }

        estudanteDAL.removerEstudante(numMecanografico);

    }
    /**
     * Procura um estudante pelo número mecanográfico.
     * @param numMecanografico O número mecanográfico a procurar.
     * @return O estudante encontrado.
     * @throws IllegalArgumentException Se o número mecanográfico for inválido ou o estudante não for encontrado.
     */
    public Estudante procurarPorNumMecanografico(String numMecanografico){
        if (numMecanografico == null || numMecanografico.trim().isEmpty()){
            throw new IllegalArgumentException("O número mecanográfico não pode estar vazio!");
        }

        Estudante estudante = estudanteDAL.procurarPorNumMecanografico(numMecanografico);
        if (estudante == null){
            throw new IllegalArgumentException("O estudante não encontrado com o número mecanográfico: " + numMecanografico);
        }
        return estudante;
    }

    /**
     * Procura um estudante pelo NIF.
     * @param nif O NIF do estudante a procurar.
     * @return O estudante encontrado.
     * @throws IllegalArgumentException Se o NIF for inválido ou o estudante não for encontrado.
     */
    public Estudante procurarPorNif(String nif){
        Utils.validarNif(nif);
        Estudante estudante = estudanteDAL.procurarPorNif(nif);
        if (estudante == null){
            throw new IllegalArgumentException("Estudante não encontrado com o nif: " + nif);
        }
        return estudante;
    }

    /**
     * Autentica um estudante pelo email e palavra-passe.
     * @param email O email do estudante.
     * @param password A palavra-passe do estudante.
     * @return O estudante autenticado.
     * @throws IllegalArgumentException Se o email ou palavra-passe forem inválidos.
     */
    public Estudante autenticarEmail(String email, String password){
        Utils.validarEmail(email);
        Utils.validarPassword(password);

        ArrayList<Estudante> estudantes = estudanteDAL.listarEstudantes();
        for (Estudante e : estudantes){
            if (e.getEmail().equalsIgnoreCase(email) && e.getPassword().equals(password)){
                return e;
            }
        }
        throw new IllegalArgumentException("E-mail ou Palavra-passe inválido!");
    }

    /**
     * Altera a password do estudante e marca o primeiro login como concluído.
     * @param estudante O estudante a alterar.
     * @param novaPassword A nova password.
     */
    public void alterarPassword(Estudante estudante, String novaPassword) {
        Utils.validarPassword(novaPassword);
        estudante.setPassword(novaPassword);
        estudante.setPrimeiroLogin(false);
        estudanteDAL.atualizarEstudante(estudante);
    }

    /**
     * Verifica se o estudante pode progredir para o ano seguinte.
     * O estudante precisa de mais de 60% de aprovação.
     * @param estudante O estudante a verificar.
     * @throws IllegalArgumentException Se o estudante for null, já estiver no último ano,
     * não tiver inscrições, não tiver avaliações ou não cumprir a regra dos 60%.
     */
    public void podeProgredirAno(Estudante estudante){
        if (estudante == null){
            throw new IllegalArgumentException("O estudante não pode ser null.");
        }

        if (estudante.getAnoAtual() >= 3){
            throw new IllegalArgumentException("O estudante já se encontra no ultimo ano do curso");
        }

        if (estudante.getInscricoes().isEmpty()){
            throw new IllegalArgumentException("O estudante não tem inscrições registadas");
        }

        int totalUCs = 0;
        int ucsAprovados = 0;

        for (Inscricao inscricao : estudante.getInscricoes()){
            if (inscricao.getAnoDeCurso() <= estudante.getAnoAtual()){
                for (Avaliacao avaliacao : inscricao.getAvaliacoes()){
                    totalUCs++;
                    if(avaliacao.getNota() >= 10){
                        ucsAprovados++;
                    }
                }
            }
        }
        if(totalUCs == 0){
            throw new IllegalArgumentException("Estudante não tem as avaliações registadas.");
        }
        double percentagemAprovacao = (double) ucsAprovados / totalUCs;
        if (percentagemAprovacao < 0.60){
            throw new IllegalArgumentException("O estudante não cumpre os requisitos para progredir de ano.\n" +
                    "Aprovação atual: " + String.format("%.1f", percentagemAprovacao * 100) + "% (mínimo necessário: 60%).");
        }

    }

    /**
     * Avança o estudante para o ano seguinte, se cumprir a regra dos 60%.
     * Cria automaticamente a inscrição no novo ano letivo.
     * @param estudante O estudante a passar de ano.
     * @param novaInscricao A inscrição para o novo ano letivo.
     * @throws IllegalArgumentException Se o estudante ou a inscrição forem null, ou se não cumprir os requisitos.
     */
    public void passarDeAno (Estudante estudante, Inscricao novaInscricao){
        if (estudante == null){
            throw new IllegalArgumentException("O estudante não pode ser null!");
        }
        if (novaInscricao == null){
            throw new IllegalArgumentException("A inscrição para o proximo ano não pode ser null!");
        }
        podeProgredirAno(estudante);

        estudante.setAnoAtual(estudante.getAnoAtual() + 1);
        estudante.adicionarInscricao(novaInscricao);
        estudanteDAL.atualizarEstudante(estudante);
    }
}
