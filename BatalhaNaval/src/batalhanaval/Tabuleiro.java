package batalhanaval;

public class Tabuleiro {
    char casa; // 0,X,S,D,C,P
    int indiceEmbarcacao; //Indice do vetor embarcações

    public char getCasa() {
        return casa;
    }

    public void setCasa(char casa) {
        this.casa = casa;
    }

    public int getindiceEmbarcacao() {
        return indiceEmbarcacao;
    }

    public void setindiceEmbarcacao(int indiceEmbarcacao) {
        this.indiceEmbarcacao = indiceEmbarcacao;
    }
    
    
}
