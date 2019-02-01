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
        int threshold = 10;
        int min_container = 1;
        int sleeptime = 20;
        index = ContainerCreate(index);
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
                difhttp = http/threshold - prehttp/threshold;
                System.out.println(prehttp);
                prehttp = http;
                int ret = process.waitFor();
                if(difhttp > 0){
                    for(int a = 0;a < difhttp;a++){
                        index = ContainerCreate(index);
                    }
                }else if(difhttp < 0 && index-y > min_container){
                    for(int b = 0;b < -difhttp;b++){
                        y = ContainerDelete(y);
                    }
                }
            }catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

            TimeUnit.SECONDS.sleep(sleeptime);
        }
    }
}