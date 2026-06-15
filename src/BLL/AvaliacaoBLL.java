package BLL;

import DAL.AnoLetivoDAL;
import DAL.AvaliacaoDAL;
import DAL.IAnoLetivoDAL;
import DAL.IAvaliacaoDAL;
import Model.AnoLetivo;
import Model.Avaliacao;
import Model.UnidadeCurricular;
import Utils.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Camada de Lógica de Negócio (BLL) para a entidade Avaliacao.
 * Responsável por validar e aplicar regras de negócio às avaliações.
 */
public class AvaliacaoBLL {

    private IAvaliacaoDAL avaliacaoDAL;
    private IAnoLetivoDAL anoLetivoDAL;

    /**
     * Construtor da classe AvaliacaoBLL.
     */
    public AvaliacaoBLL() {
        this.avaliacaoDAL = new AvaliacaoDAL();  // modo ficheiro
        this.anoLetivoDAL = new AnoLetivoDAL();
    }

    /**
     * Construtor com injeção da DAL.
     *
     * @param avaliacaoDAL A camada DAL a utilizar.
     * @param anoLetivoDAL A DAL do ano letivo, para validar as datas das avaliações.
     */
    public AvaliacaoBLL(IAvaliacaoDAL avaliacaoDAL, IAnoLetivoDAL anoLetivoDAL) {
        this.avaliacaoDAL = avaliacaoDAL;
        this.anoLetivoDAL = anoLetivoDAL;
    }

    /**
     * Regista uma nova avaliação no sistema.
     *
     * Regras:
     * - a lista de UCs não pode ser nula nem vazia
     * - o peso deve ser superior a 0 e inferior ou igual a 100
     * - a data não pode ser nula nem futura
     * - a nota tem de estar entre 0 e 20
     *
     * @param ucs Lista de UCs associadas à avaliação.
     * @param peso Peso da avaliação.
     * @param data Data da avaliação.
     * @param nota Nota da avaliação.
     * @return A avaliação criada.
     */
    public Avaliacao registarAvaliacao(List<UnidadeCurricular> ucs, String nomeCurso, Date data, double nota) {
        validarUCs(ucs);
        validarData(data);
        Utils.validarNota(nota);

        if (nomeCurso == null || nomeCurso.isBlank()) {
            throw new IllegalArgumentException("O momento de avaliação tem de estar associado a um curso.");
        }

        for (Avaliacao existente : avaliacaoDAL.listarAvaliacoes()) {
            if (existente.getData() != null && existente.getData().equals(data)
                    && nomeCurso.equalsIgnoreCase(existente.getNomeCurso())
                    && existente.getUc() != null && existente.getUc().containsAll(ucs) && ucs.containsAll(existente.getUc())) {
                throw new IllegalArgumentException("Já existe uma avaliação para as mesmas UCs nesta data neste curso.");
            }
        }

        // O peso é distribuído automaticamente pelos momentos da UC neste curso:
        // 1 momento -> 100%, 2 -> 50/50, 3 -> 33.33/33.33/33.34
        ArrayList<Avaliacao> existentes = procurarPorUCeCurso(ucs.get(0), nomeCurso);
        if (existentes.size() >= 3) {
            throw new IllegalArgumentException(
                    "A UC '" + ucs.get(0).getNome() + "' já tem 3 momentos de avaliação no curso '" + nomeCurso + "'.");
        }

        double pesoCalculado = distribuirPesos(existentes);

        // Momento criado sem nota lançada — fica "Pendente" até o docente lançar
        Avaliacao novaAvaliacao = new Avaliacao(ucs, pesoCalculado, data);
        novaAvaliacao.setNomeCurso(nomeCurso);
        avaliacaoDAL.adicionarAvaliacao(novaAvaliacao);

        for (UnidadeCurricular uc : ucs) {
            if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(novaAvaliacao)) {
                uc.getAvaliacoes().add(novaAvaliacao);
            }
        }

        return novaAvaliacao;
    }

    /**
     * Lista todas as avaliações registadas.
     *
     * @return Lista de avaliações.
     */
    public ArrayList<Avaliacao> listarAvaliacoes() {
        return avaliacaoDAL.listarAvaliacoes();
    }

    /**
     * Atualiza uma avaliação existente.
     *
     * @param avaliacaoAntiga A avaliação original.
     * @param novasUCs As novas UCs.
     * @param novoPeso O novo peso.
     * @param novaData A nova data.
     * @param novaNota A nova nota.
     */
    public void atualizarAvaliacao(Avaliacao avaliacaoAntiga,
                                   List<UnidadeCurricular> novasUCs,
                                   double novoPeso,
                                   Date novaData,
                                   double novaNota) {

        if (avaliacaoAntiga == null) {
            throw new IllegalArgumentException("A avaliação antiga não pode ser nula.");
        }

        validarUCs(novasUCs);
        validarPeso(novoPeso);
        validarData(novaData);
        Utils.validarNota(novaNota);

        Avaliacao avaliacaoNova = new Avaliacao(novasUCs, novoPeso, novaData, novaNota, novaNota >= 10.0);

        boolean atualizado = avaliacaoDAL.atualizarAvaliacao(avaliacaoAntiga, avaliacaoNova);

        if (!atualizado) {
            throw new IllegalArgumentException("Não foi possível encontrar a avaliação para atualizar.");
        }

        // remover a avaliação antiga das UCs antigas
        if (avaliacaoAntiga.getUc() != null) {
            for (UnidadeCurricular uc : avaliacaoAntiga.getUc()) {
                if (uc.getAvaliacoes() != null) {
                    uc.getAvaliacoes().remove(avaliacaoAntiga);
                }
            }
        }

        // adicionar a nova avaliação às novas UCs
        for (UnidadeCurricular uc : novasUCs) {
            if (uc.getAvaliacoes() != null && !uc.getAvaliacoes().contains(avaliacaoNova)) {
                uc.getAvaliacoes().add(avaliacaoNova);
            }
        }
    }

    /**
     * Remove uma avaliação do sistema.
     *
     * @param avaliacao A avaliação a remover.
     */
    public void removerAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("A avaliação não pode ser nula.");
        }

        if (avaliacao.getUc() != null) {
            for (UnidadeCurricular uc : avaliacao.getUc()) {
                if (uc.getAvaliacoes() != null) {
                    uc.getAvaliacoes().remove(avaliacao);
                }
            }
        }

        avaliacaoDAL.removerAvaliacao(avaliacao);
    }

    /**
     * Procura avaliações associadas a uma determinada UC.
     *
     * @param uc A unidade curricular.
     * @return Lista de avaliações da UC.
     */
    public ArrayList<Avaliacao> procurarPorUC(UnidadeCurricular uc) {
        if (uc == null) {
            throw new IllegalArgumentException("A unidade curricular não pode ser nula.");
        }

        return avaliacaoDAL.procurarPorUC(uc);
    }

    /**
     * Procura avaliações por data.
     *
     * @param data A data da avaliação.
     * @return Lista de avaliações encontradas.
     */
    public ArrayList<Avaliacao> procurarPorData(Date data) {
        if (data == null) {
            throw new IllegalArgumentException("A data da avaliação não pode ser nula.");
        }
        return avaliacaoDAL.procurarPorData(data);
    }

    /**
     * Procura os momentos de avaliação de uma UC num curso específico.
     *
     * @param uc A unidade curricular.
     * @param nomeCurso O nome do curso.
     * @return Lista de momentos dessa UC nesse curso.
     */
    public ArrayList<Avaliacao> procurarPorUCeCurso(UnidadeCurricular uc, String nomeCurso) {
        ArrayList<Avaliacao> resultado = new ArrayList<>();
        for (Avaliacao a : avaliacaoDAL.procurarPorUC(uc)) {
            if (nomeCurso.equalsIgnoreCase(a.getNomeCurso())) {
                resultado.add(a);
            }
        }
        return resultado;
    }

    /**
     * Redistribui os pesos dos momentos existentes de uma UC e devolve
     * o peso do novo momento: 1 -> 100%, 2 -> 50/50, 3 -> 33.33/33.33/33.34.
     *
     * @param existentes Momentos de avaliação já registados na UC.
     * @return O peso a atribuir ao novo momento.
     */
    private double distribuirPesos(List<Avaliacao> existentes) {
        int total = existentes.size() + 1;

        if (total == 1) {
            return 100.0;
        }
        if (total == 2) {
            existentes.get(0).setPeso(50.0);
            return 50.0;
        }
        existentes.get(0).setPeso(33.33);
        existentes.get(1).setPeso(33.33);
        return 33.34;
    }

    /**
     * Valida se a lista de UCs é válida.
     *
     * @param ucs Lista de UCs.
     */
    private void validarUCs(List<UnidadeCurricular> ucs) {
        if (ucs == null || ucs.isEmpty()) {
            throw new IllegalArgumentException("A avaliação tem de estar associada a pelo menos uma UC.");
        }

        for (UnidadeCurricular uc : ucs) {
            if (uc == null) {
                throw new IllegalArgumentException("A lista de UCs não pode conter valores nulos.");
            }
        }
    }

    /**
     * Valida o peso da avaliação.
     *
     * @param peso O peso.
     */
    private void validarPeso(double peso) {
        if (peso <= 0 || peso > 100) {
            throw new IllegalArgumentException("O peso da avaliação deve estar entre 0 e 100.");
        }
    }

    /**
     * Valida a data da avaliação.
     *
     * @param data A data.
     */
    private void validarData(Date data) {
        if (data == null) {
            throw new IllegalArgumentException("A data da avaliação não pode ser nula.");
        }

        java.time.LocalDate dataAvaliacao = data.toInstant()
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();

        if (dataAvaliacao.isBefore(java.time.LocalDate.now())) {
            throw new IllegalArgumentException("A data da avaliação não pode ser no passado.");
        }

        AnoLetivo anoAberto = anoLetivoDAL.procurarAnoAberto();
        if (anoAberto == null) {
            throw new IllegalArgumentException("Não existe ano letivo aberto para agendar a avaliação.");
        }

        // O ano letivo X/X+1 termina a 31 de agosto de X+1
        java.time.LocalDate fimAnoLetivo = java.time.LocalDate.of(anoAberto.getAno() + 1, 8, 31);
        if (dataAvaliacao.isBefore(anoAberto.getDataAbertura()) || dataAvaliacao.isAfter(fimAnoLetivo)) {
            throw new IllegalArgumentException(
                    "A data da avaliação tem de estar dentro do ano letivo "
                    + anoAberto.getDesignacao() + " (entre " + anoAberto.getDataAbertura()
                    + " e " + fimAnoLetivo + ").");
        }
    }
}