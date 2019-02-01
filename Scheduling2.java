import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class ContainerScheduling{

    static void ContainerCreate(int create_container_number){
        try{
            Process process = new ProcessBuilder("docker", "run", "--name", "nanami"+create_container_number, "ubuntu").start();
            System.out.println("Create");
            System.out.println("nanami"+create_container_number);
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
    }

    static void ContainerDelete(int delete_container_number){

        try{
            Process process = new ProcessBuilder("docker", "rm", "nanami"+delete_container_number).start();
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
    }
    public static void main(String[] args) throws InterruptedException{
      int create_container_number = 1;
      int delete_container_number = 1;
      int http;
      int current_container_count = 1;
      int necessary_container_count;
      int change_container_count;
      int threshold = 10;
      int min_container_count = 1;
      int sleeptime = 20;
      //create_container_number = ContainerCreate(create_container_number);
      ContainerCreate(create_container_number);
      create_container_number++;
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
              http = Integer.parseInt(text.replace("\n",""));
              System.out.println("同時接続数"+http);
              necessary_container_count = http/threshold;
              if(necessary_container_count < min_container_count){
                  necessary_container_count = min_container_count;
              }
              change_container_count = necessary_container_count - current_container_count;
              int ret = process.waitFor();
              if(change_container_count > 0){
                  for(int i = 0;i < change_container_count;i++){
                      ContainerCreate(create_container_number);
                      create_container_number++;
                      current_container_count++;

                  }
              }else if(change_container_count < 0){
                  for(int i = 0;i < -change_container_count;i++){
                      ContainerDelete(delete_container_number);
                      delete_container_number++;
                      current_container_count--;
                  }
              }
          }catch (IOException | InterruptedException e) {
              e.printStackTrace();
          }

          TimeUnit.SECONDS.sleep(sleeptime);
    
      }
    }
}