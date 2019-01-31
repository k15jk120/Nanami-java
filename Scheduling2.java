import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Scheduling2{
    static int ContainerCreate(int i){
        try{
          Process process = new ProcessBuilder("docker", "run", "--name", "nanami"+i, "ubuntu").start();
          System.out.println("Create");
          System.out.println("nanami"+i);
          i+=1;
          String text;
          InputStream is = process.getInputStream();
          InputStreamReader isr = new InputStreamReader(is, "UTF-8");
          BufferedReader reader = new BufferedReader(isr);
          StringBuilder builder = new StringBuilder();
          int c;
          while ((c = reader.read()) != -1) {
            builder.append((char)c);
          }
          text = builder.toString();
          int ret = process.waitFor();
        }catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
        return i;
    }

    static int ContainerDelete(int x){

        try{
          Process process = new ProcessBuilder("docker", "rm", "nanami"+x).start();
          x += 1;
          System.out.println("Delete");
          String text;
          InputStream is = process.getInputStream();
          InputStreamReader isr = new InputStreamReader(is, "UTF-8");
          BufferedReader reader = new BufferedReader(isr);
          StringBuilder builder = new StringBuilder();
          int c; 
        while ((c = reader.read()) != -1) {
          builder.append((char)c); 
        }
        // 実行結果を格納
        text = builder.toString();
        int ret = process.waitFor();
        System.out.println(text);
        }catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }
        return x;
    }

    public static void main(String[] args) throws InterruptedException{
    int index = 1;
    int y = 1;
    int prehttp = 1;
    int difhttp;
    while(true){
        try{
          Process process = new ProcessBuilder("sh", "httprequest.sh").start();
          String text;
          InputStream is = process.getInputStream();
          InputStreamReader isr = new InputStreamReader(is, "UTF-8");
          BufferedReader reader = new BufferedReader(isr);
          StringBuilder builder = new StringBuilder();
          int c;
        while ((c = reader.read()) != -1) {
          builder.append((char)c);
        }
        // 実行結果を格納
        text = builder.toString();
        int http = Integer.parseInt(text.replace("\n",""));
        System.out.println("同時接続数"+http);
        difhttp = http - prehttp;
        System.out.println(prehttp);
        prehttp = http;
        int ret = process.waitFor();
/*        if(difhttp >= 10){*/
            while(difhttp >= 10){
               index = ContainerCreate(index);
               difhttp -= 10;
            }
            while(difhttp <= -10 && index-y > 1){
               y = ContainerDelete(y);
               difhttp += 10;
            }
/*        }else if (index-y > 1 && difhttp <= -10) {
            y = ContainerDelete(y);
        }else{

        }
*/
        }catch (IOException | InterruptedException e) {
          e.printStackTrace();
        }

        TimeUnit.SECONDS.sleep(20);
    }
    }
}