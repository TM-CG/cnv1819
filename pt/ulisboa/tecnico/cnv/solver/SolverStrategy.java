package pt.ulisboa.tecnico.cnv.solver;
import pt.ulisboa.tecnico.cnv.util.*;

public interface SolverStrategy {

    void solve(final Solver sol);

    @Override
    String toString();
}
