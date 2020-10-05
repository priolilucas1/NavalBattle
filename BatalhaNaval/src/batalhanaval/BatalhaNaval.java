package batalhanaval;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class BatalhaNaval {

    public static Tabuleiro[][] tabuleiro_agua = new Tabuleiro[10][10]; //TABULEIRO QUE TA TUDO ESCONDIDO
    public static Tabuleiro[][] tabuleiro_resposta = new Tabuleiro[10][10]; //TABULEIRO COM AS RESPOSTAS
    public static File arquivoTabuleiro = new File("tabuleiro.txt");
    public static File arquivoLog = new File("log.txt");
    public static int[] embarcacao = new int[11]; //vetor indice
    public static DecimalFormat df = new DecimalFormat("0.0");
    public static int linha, coluna;
    public static String[] logs= new String[100];
    public static ArrayList<String> logList = new ArrayList<String>(); //LOG

    public static int tiros = 0, afundou = 0, agua = 0, repetidos = 0, invalidos = 0, posicoesCorretas = 0; //calculos
    public static float porcentagemAcertos = 0, porcentagemAgua = 0, porcentagemRepetidos = 0, porcentagemInvalidos = 0;
//-------------------------------------------------------------------------------------------------------------------------    

    //Main para o jogo
    public static void main(String[] args) throws IOException{
        InicioTabuleiro();
        lerArquivoTabuleiro();

        do {
            loadTabuleiro();
            tiro();
            tiros++;
            logList.add(gerarLog());
            saveLog(logList, arquivoLog);

        } while (afundou != 11);
          loadTabuleiro();
          
        porcentagemAcertos = (float) (posicoesCorretas * 100) / tiros; //Calculos de estatísticas
        porcentagemAgua = (float) (agua * 100) / tiros;
        porcentagemRepetidos = (float) (repetidos * 100) / tiros;
        porcentagemInvalidos = (float) (invalidos * 100) / tiros;

        System.out.println(tiros + " tiros: " + df.format(porcentagemAgua) + "% água," + df.format(porcentagemAcertos) + "% certos, "
        + df.format(porcentagemRepetidos) + "% repetidos e " + df.format(porcentagemInvalidos) + "% inválidos."); //COLOCAR ESTATÍSTICAS
    }

    //USUÁRIO ESCOLHE O TIRO
    public static void tiro() throws IOException {
        Scanner teclado = new Scanner(System.in);
        boolean i = true;

        try {            
            do {
                System.out.print("\nLinha: ");
                linha = teclado.nextInt();
                System.out.print("Coluna: ");
                coluna = teclado.nextInt();

                if (posicaoRepetida() == false) {
                    logs[coluna]="Você digitou uma posição repetida.";
                    logList.add("["+linha+","+coluna+"]-"+logs[coluna]);
                    continue;
                } else {
                    
                    if (tabuleiro_agua[linha][coluna].getCasa() == tabuleiro_resposta[linha][coluna].getCasa()) { //Se essa posição for igual a água

                        System.out.println("\nVocê acertou a água.");
                        tabuleiro_agua[linha][coluna].setCasa('X');
                        agua++;//Acertos na agua
                        logs[coluna]="Você acertou a Água";
               //------------------------------------------------------------------------------
                    } else if (tabuleiro_resposta[linha][coluna].getCasa() == 'S') { //Se essa posição for igual a 'S'

                        System.out.println("\nVocê afundou um Submarino!!");
                        tabuleiro_agua[linha][coluna].setCasa('S');
                        afundou++;//Conta os afundou pra acabar o loop da main
                        posicoesCorretas++;//acertos em embarcações
                        embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()]--;
                        logs[coluna]="Você afundou um Submarino!";
               //------------------------------------------------------------------------------
                    } else if (tabuleiro_resposta[linha][coluna].getCasa() == 'D') { //Se essa posição for igual a 'D'
                        System.out.println("\nVocê acertou um Destroyer!!");
                        tabuleiro_agua[linha][coluna].setCasa('D');
                        embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()]--;
                        posicoesCorretas++;
                        logs[coluna]="Você acertou um Destroyer!";

                        if (embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()] == 0) {
                            System.out.println("Você afundou o Destroyer!!");
                            afundou++;//Conta os afundou pra acabar o loop da main
                            logs[coluna]="Você acertou um Destroyer!\nVocê afundou o Destroyer!";
                        }
               //------------------------------------------------------------------------------
                    } else if (tabuleiro_resposta[linha][coluna].getCasa() == 'C') { //Se essa posição for igual a 'C'
                        System.out.println("\nVocê acertou um Cruzador!!");
                        tabuleiro_agua[linha][coluna].setCasa('C');
                        embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()]--;
                        posicoesCorretas++;
                        logs[coluna]="Você acertou um Cruzador!";

                        if (embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()] == 0) {
                            System.out.println("Você afundou o Cruzador!!");
                            afundou++;//Conta os afundou pra acabar o loop da main
                            logs[coluna]="Você acertou um Cruzador!\nVocê afundou o Cruzador!";
                        }
              //--------------------------------------------------------------------------------
                    } else if (tabuleiro_resposta[linha][coluna].getCasa() == 'P') { //Se essa posição for igual a 'P'
                        System.out.println("\nVocê acertou um Porta-Avião!!");
                        tabuleiro_agua[linha][coluna].setCasa('P');
                        embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()]--;
                        posicoesCorretas++;
                        logs[coluna]="Você acertou um Porta-Avião!";

                        if (embarcacao[tabuleiro_resposta[linha][coluna].getindiceEmbarcacao()] == 0) {
                            System.out.println("Você afundou o Porta-Avião!!");
                            afundou++; //Conta os afundou pra acabar o loop da main
                            logs[coluna]="Você acertou um Porta-Avião!\nVocê afundou o Porta-Avião!";
                        }                        
                    }
                }
                i = false;
            } while (i == true);
            
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("\nErro: Digite uma linha ou coluna válida.");
            invalidos++;
        } catch (InputMismatchException e) {
            System.out.println("\nErro: Só é permitido números inteiros.");
            invalidos++;
        }

    }

    //CARREGA O TABULEIRO DE ACORDO COM O QUE ACONTECE   
    public static void loadTabuleiro() {
        System.out.println("\n==================================TABULEIRO======================================");
        System.out.println("\n\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9");
        System.out.println();

        for (int linha1 = 0; linha1 < 10; linha1++) {
            System.out.print((linha1) + "");
            for (int coluna1 = 0; coluna1 < 10; coluna1++) {
                if (tabuleiro_agua[linha1][coluna1].getCasa() == '0') { //Se essa posição for igual a 0 bota #
                    System.out.print("\t" + "#");
                } else if (tabuleiro_agua[linha1][coluna1].getCasa() == 'S') { //Se essa posição for igual a S bota S
                    System.out.print("\t" + "S");
                } else if (tabuleiro_agua[linha1][coluna1].getCasa() == 'D') { //Se essa posição for igual a D bota D
                    System.out.print("\t" + "D");
                } else if (tabuleiro_agua[linha1][coluna1].getCasa() == 'C') { //Se essa posição for igual a C bota C
                    System.out.print("\t" + "C");
                } else if (tabuleiro_agua[linha1][coluna1].getCasa() == 'P') { //Se essa posição for igual a P bota P
                    System.out.print("\t" + "P");
                } else if (tabuleiro_agua[linha1][coluna1].getCasa() == 'X') { //Se essa posição for igual a X bota X
                    System.out.print("\t" + "X");
                }
            }
            System.out.println();
        }
    }

    //INICIA O TABULEIRO DO ZERO
    public static void InicioTabuleiro() {
        for (int linha1 = 0; linha1 < 10; linha1++) { //Definir as posições como 0(agua)
            for (int coluna1 = 0; coluna1 < 10; coluna1++) {
                tabuleiro_agua[linha1][coluna1] = new Tabuleiro();
                tabuleiro_agua[linha1][coluna1].setCasa('0'); //SETA TUDO DE TABULEIRO AGUA COMO ZERO
                tabuleiro_agua[linha1][coluna1].setindiceEmbarcacao(69); 
                tabuleiro_resposta[linha1][coluna1] = new Tabuleiro();
                tabuleiro_resposta[linha1][coluna1].setCasa('0'); //SETA TUDO DE TABULEIRO RESPOSTA COMO ZERO
                tabuleiro_resposta[linha1][coluna1].setindiceEmbarcacao(69); 

            }
        }
    }
    
    //LE O ARQUIVO DO TABULEIRO
    public static void lerArquivoTabuleiro(){
        Scanner leitor = null;
        try { //LER ARQUIVO
            leitor = new Scanner(arquivoTabuleiro);

            int index = 0;
            while (leitor.hasNextLine()) {
                String[] ler = new String[4]; //Indices
                String linha = leitor.nextLine();
                ler = linha.split(" "); //Separar espaços no arquivo

                int x = Integer.parseInt(ler[0]); //Le a linha
                int y = Integer.parseInt(ler[1]); //Le a coluna
                tabuleiro_resposta[x][y].setCasa(ler[2].charAt(0)); //Lê a embarcação
                tabuleiro_resposta[x][y].setindiceEmbarcacao(Integer.parseInt(ler[3])); //Le o índice
                embarcacao[Integer.parseInt(ler[3])]++; //Adiciona "vidas"
                if(verificarTabuleiro(x,y,Integer.parseInt(ler[3]))==0){ //Ver se tem embarcações encostando
                   System.out.println("ERRO: EMBARCAÇÕES ENCOSTANDO");
                   System.exit(0);                                             
                }else{
                    
                }
            }

        } catch (Exception e) {
            System.out.println("Erro: " + e);
        }
    } 
    
    //VERIFICAR SE POSIÇÃO JÁ FOI DIGITADA.
    public static boolean posicaoRepetida() throws IOException {
        if (tabuleiro_agua[linha][coluna].getCasa() != '0') {
            System.out.println("Posição já foi digitada, tente outra posição.");
            repetidos++;
            return false;
        } else {
            return true;
        }
        
        
    } 
    
    //VERIFICAR SE TEM EMBARCAÇÕES ENCONSTADAS
    public static int verificarTabuleiro(int x,int y,int indice){
        //   c. encosta em uma outra embarcação.
        int[] arredores = new int[8];
      //Em cima
        if (x != 0) {
            if (y != 0) {
                arredores[0] = tabuleiro_resposta[x - 1][y - 1].indiceEmbarcacao;
            }else{
                arredores[0] = 99;
            }
            arredores[1] = tabuleiro_resposta[x - 1][y].indiceEmbarcacao;
            if (y != 9) {
                arredores[2] = tabuleiro_resposta[x - 1][y + 1].indiceEmbarcacao;
            }else{
                arredores[2] = 99;
            }
        }else{
            arredores[0] = 99;
            arredores[1] = 99;
            arredores[2] = 99;
        }
       //Dos lados
        if (y != 0) {
            arredores[3] = tabuleiro_resposta[x][y - 1].indiceEmbarcacao;
        }else{
            arredores[3] = 99;
        }
        if (y != 9) {
            arredores[4] = tabuleiro_resposta[x][y + 1].indiceEmbarcacao;
        }else{
            arredores[4] = 99;
        }
      //Parte de baixo
        if (x != 9) {
            if (y != 0) {
                arredores[5] = tabuleiro_resposta[x + 1][y - 1].indiceEmbarcacao;
            }else{
                arredores[5] = 99;
            }
            arredores[6] = tabuleiro_resposta[x + 1][y].indiceEmbarcacao;
            if (y != 9) {
                arredores[7] = tabuleiro_resposta[x + 1][y + 1].indiceEmbarcacao;
            }else{
                arredores[7] = 99;
            }
        }else{
            arredores[5] = 99;
            arredores[6] = 99;
            arredores[7] = 99;
        }
        for (int i = 0; i < 8; i++) {
            if (arredores[i] != 69 && arredores[i] != 99) {
                if (arredores[i] != indice) {
                    return 0;
                }

            }
        }
        return 1;
    }

    //ESCREVER NO ARQUIVO LOG.TXT
    static String gerarLog() throws IOException {        
       if(afundou<11){ 
        String string= "["+linha+","+coluna+"]-"+logs[coluna]; //escreve 
	return string;
       
       }else {
        porcentagemAcertos = (float) (posicoesCorretas * 100) / tiros; //Calculos estatisticas pra botar no log
        porcentagemAgua = (float) (agua * 100) / tiros;
        porcentagemRepetidos = (float) (repetidos * 100) / tiros;
        porcentagemInvalidos = (float) (invalidos * 100) / tiros;
        
        String string1="["+linha+","+coluna+"]-"+logs[coluna]+"\n===ESTATÍSTICAS===\n"+tiros + " tiros: " + df.format(porcentagemAgua) + "% água," + df.format(porcentagemAcertos) + "% certos, "
        + df.format(porcentagemRepetidos) + "% repetidos e " + df.format(porcentagemInvalidos) + "% inválidos.";  
         
         return string1;  
       }
     
    }
    
    //SALVA O ARQUIVO LOG.TXT
    public static void saveLog(ArrayList<String> log, File arquivoLog) {

        try {
            Scanner leitor = new Scanner(arquivoLog);
            FileWriter escritor = new FileWriter(arquivoLog);
            for (String Logs : log) {
                escritor.append(Logs + "\n");
            }
            escritor.close();
            leitor.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

}
