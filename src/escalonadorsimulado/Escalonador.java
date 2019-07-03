package escalonadorsimulado;


import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


/**
 *
 * @author Arthur Esdras, Gustavo Almeida e Jefferson Renê
 */
public class Escalonador extends Thread {

    //Variáveis

     ArrayList<Processo> fila_pronto = new ArrayList<>();
     ArrayList<Processo> fila_bloqueado = new ArrayList<>();

    boolean novoadd;

    String regEX;
    String regBL;

    public Escalonador(){}

    public Escalonador(String caminho_regex, String caminho_regbl){
        regEX = caminho_regex;
        regBL = caminho_regbl;
    }

    public void run(){
      while(true){
         if(novoadd == true){
             novoadd = false;
          organizar();
          }
         if(fila_pronto.isEmpty()==false){//Para garantir que só faça esta parte do código se existir um Processo
           if(fila_pronto.get(0).status.equalsIgnoreCase("pronto")){             
             Processo trocador = fila_pronto.get(0);
             fila_pronto.remove(0);
             prontorecebeprocesso(trocador);
           }else if(fila_pronto.get(0).status.equalsIgnoreCase("bloqueado")){
             Processo trocador = fila_pronto.get(0);
             fila_pronto.remove(0);
             fila_bloqueado.add(trocador);
           }else if(fila_pronto.get(0).status.equalsIgnoreCase("finalizado")){
             fila_pronto.remove(0);

           }
             if(fila_bloqueado.isEmpty()==false){
              if(fila_bloqueado.get(0).status.equalsIgnoreCase("pronto")){
                 Processo trocador = fila_bloqueado.get(0);
                 fila_bloqueado.remove(0);
                 prontorecebeprocesso(trocador);
              }
             }
         }
      }


    }

    public void organizar(){
        for(int ctd = 0;ctd<fila_pronto.size();ctd++){
              if(fila_pronto.get(ctd).status.equalsIgnoreCase("finalizado")){
               fila_pronto.remove(ctd);
              }else{
                fila_pronto.get(ctd).quantum = calcularQuantum(fila_pronto.get(ctd).tipo);
                fila_pronto.get(ctd).prioridade = calcularprioridade(fila_pronto.get(ctd).tipo);
                fila_pronto.get(ctd).setArquivo(regEX,regBL);
                if(fila_pronto.get(ctd).isAlive()==false){
                     fila_pronto.get(ctd).start();
                }
              }
         }
         Processo trocador;
         int ultimo = fila_pronto.size() - 1;
            for(int ctd2 = 0;ctd2<ultimo;ctd2++){   
             if(fila_pronto.get(ultimo).prioridade > fila_pronto.get(ctd2).prioridade){
                 trocador = fila_pronto.get(ultimo);
                 fila_pronto.remove(ultimo);
                 fila_pronto.add(ctd2, trocador);
             }
            }
         if(fila_pronto.get(0).status.equalsIgnoreCase("pronto")){
         fila_pronto.get(0).status = "executando";
         }

    }

    public void prontorecebeprocesso(Processo proc){
        fila_pronto.add(proc);
        novoadd = true;
    }    

    public int calcularQuantum(String tipo){

        int qt = 1;

        if (tipo.equalsIgnoreCase("IObound")){
            qt = 2;
        }
        if(tipo.equalsIgnoreCase("CPUbound")){
        qt = 4;
        }
        if(tipo.equalsIgnoreCase("Mista")){
        qt = 3;
        }
        if(tipo.equalsIgnoreCase("Administrador")){
        qt = 3;
        }
        
        return qt;
    }

    public int calcularprioridade(String tipo){

        int prd = 1;

        if (tipo.equalsIgnoreCase("IObound")){
            prd = 4;
        }
        if(tipo.equalsIgnoreCase("CPUbound")){
        prd = 3;
        }
        if(tipo.equalsIgnoreCase("Mista")){
        prd = 2;
        }
        if(tipo.equalsIgnoreCase("Administrador")){
        prd = 1;
        }

        return prd;
    }
}
