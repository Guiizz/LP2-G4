package BLL;

import DAL.EstudanteDAL;
import Model.Curso;
import Model.Estudante;
import java.time.LocalDate;
import static Utils.Utils.*;

public class EstudanteBLL {
    private EstudanteDAL estudanteDAL;

    public EstudanteBLL(){
        this.estudanteDAL = new EstudanteDAL();
    }
}
