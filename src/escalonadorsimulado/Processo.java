package escalonadorsimulado;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Arthur Esdras, Gustavo Almeida e Jefferson Renê
 */
public class Processo extends Thread {

    //Atributos
    String tipo;
    public int quantum, tempototal;
    //Variáveis características
    int contexto,numero,tempoIO,tempoEX,prioridade;
    String nome,status,dados;
    String regEX,regBL;
    

    //Construtor
    public Processo(String tipo, int tempototal, int numero, String nome){
        this.tipo = tipo;        
        this.tempototal = tempototal;
        this.numero = numero;
        this.nome = nome;
        status = "pronto";
        contexto = 1;
    }


    //Métodos
    public void registrar(String reg){
        try{
           FileWriter registro = new FileWriter(reg,true);
           registro.write(dados+"\n_________________________________\n");
           registro.close();
        }catch(IOException re){
           JOptionPane.showConfirmDialog(null, "Problema no arquivo de gravação: "+ re.getMessage());
        }
    }

    public void retorno(){
        System.out.println(nome+" "+numero+" "+status+"\n");
    }

    

    public void setStatus(String st){
        status = st;
    }

    public void setArquivo(String arq1,String arq2){
        regEX = arq1;
        regBL = arq2;
    }

    public synchronized void bloqueado(){
        dados = ("\nProcesso Bloqueado:"+nome+"  \nNúmero:"+numero+"    Tipo:"+tipo
                          +"\n   Quantum:"+quantum+"   Tempo total:"+tempototal+""
                          + "\n Contexto:"+(contexto - 1));
                  Random temp = new Random();
                  tempoIO = temp.nextInt(6 - 2 +1)+2;//O tempo de I/O é aleatório entre 2 e 6
                  try{
                    sleep(tempoIO*1000);
                  }catch(InterruptedException re){}
                  registrar(regBL);
                  retorno();
                  if(tempoEX < tempototal){
                   status = "pronto";
                  }
    }

    public synchronized void executando(){

        dados = ("\nProcesso Executado:"+nome+"\nNúmero:"+numero+"    Tipo:"+tipo
                          +"\n   Quantum:"+quantum+"   Tempo total:"+tempototal+""
                          + "\n Contexto:"+contexto);
                  tempoEX += quantum;
                  try{
                    sleep(quantum*1000);
                  }catch(InterruptedException re){}
                  registrar(regEX);
                  contexto++;
                  retorno();
                  if(tempoEX < tempototal && tipo.equalsIgnoreCase("IObound")){
                    status = "bloqueado";
                  }else if(tempoEX < tempototal && tipo.equalsIgnoreCase("CPUbound")){
                      status = "pronto";
                  } else if (tempoEX >= tempototal) {
                    status = "finalizado";//Ao checar essa string a thread para
                  }
    }


    //Metodo rodar
    public void run(){
          while(status.equalsIgnoreCase("finalizado")==false){
              if(status.equalsIgnoreCase("executando")==true){
                  executando();
              }else if(status.equalsIgnoreCase("bloqueado")==true){
                  bloqueado();
              }
          }

    }
}
