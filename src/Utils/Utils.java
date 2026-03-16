package Utils;

public class Utils {
    public static boolean validarNif(String nif){
        if (nif == null || nif.isEmpty()){
            return false;
        }
        if (nif.length() != 9){
            return false;
        }
        for (int i = 0; i < nif.length();i++){
            if(Character.isDigit(nif.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public static boolean validarNome(String nome){
        return nome != null && !nome.isEmpty();
    }
}
