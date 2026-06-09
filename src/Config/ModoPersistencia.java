package Config;

/**
 * Modo de persistência escolhido pelo utilizador no arranque da aplicação.
 * Permite migrar a aplicação para base de dados entidade a entidade,
 * mantendo a opção de ficheiros/memória sempre disponível.
 */
public enum ModoPersistencia {
    FICHEIRO,
    BASE_DADOS;

    private static ModoPersistencia atual = FICHEIRO;

    public static ModoPersistencia getAtual() {
        return atual;
    }

    public static void setAtual(ModoPersistencia modo) {
        atual = modo;
    }

    public static boolean isBaseDados() {
        return atual == BASE_DADOS;
    }
}
