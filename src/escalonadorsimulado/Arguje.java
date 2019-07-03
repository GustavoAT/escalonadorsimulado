package escalonadorsimulado;

import java.util.ArrayList;

/**
 *
 * @author Gustavo,Athur,Jefferson
 */
public class Arguje extends Escalonador {

      //Atributos
    ArrayList<Integer> starvationList = new ArrayList<>();

    public Arguje(String caminho_regex, String caminho_regbl){
        regEX = caminho_regex;
        regBL = caminho_regbl;
    }

    @Override
    public void organizar(){
        for(int ctd = 0;ctd<fila_pronto.size();ctd++){
              if(fila_pronto.get(ctd).status.equalsIgnoreCase("finalizado")){
               fila_pronto.remove(ctd);
              }else{
                fila_pronto.get(ctd).quantum = calcularQuantum(fila_pronto.get(ctd).tipo);
                calcularpiroridade(ctd);//Aqui sua prioridade é configurada checando a lista de starvation
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
                 starvationList.add(fila_pronto.get(ctd2).numero);//Aqui o Processo passado para traz é lembrado na lista de starvation
                 fila_pronto.add(ctd2, trocador);
             }
            }
         if(fila_pronto.get(0).status.equalsIgnoreCase("pronto")){
         fila_pronto.get(0).status = "executando";
         }

    }

    public void calcularpiroridade(int indc){
        if(procurastarvation(fila_pronto.get(indc).numero)>=2 && fila_pronto.get(indc).prioridade < 4){
                    fila_pronto.get(indc).prioridade++;
                }else if(procurastarvation(fila_pronto.get(indc).numero) < 2){
                    fila_pronto.get(indc).prioridade = calcularprioridade(fila_pronto.get(indc).tipo);
                }
    }

    public int procurastarvation(int num){
        int res = 0;
        for(int ctd = 0;ctd < starvationList.size();ctd++){
            if(starvationList.get(ctd)==num)res++;
        }
        return res;
    }
    
    public void retiraStarvation(int num){
        for(int ctd = 0;ctd < starvationList.size();ctd++){
            if(starvationList.get(ctd)==num){
                starvationList.remove(ctd);
            }
        }
    }

    //Método rodar
    @Override
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
             retiraStarvation(trocador.numero);
             prontorecebeprocesso(trocador);
           }else if(fila_pronto.get(0).status.equalsIgnoreCase("bloqueado")){
             Processo trocador = fila_pronto.get(0);
             fila_pronto.remove(0);
             fila_bloqueado.add(trocador);
           }else if(fila_pronto.get(0).status.equalsIgnoreCase("finalizado")){
             retiraStarvation(fila_pronto.get(0).numero);
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
    
}
