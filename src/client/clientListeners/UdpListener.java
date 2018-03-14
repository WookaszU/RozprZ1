package client.clientListeners;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Random;

public abstract class UdpListener {


    // method to .jpg , image size <1500B  mtu ,  > need fragmentation
    public void decodeImage(byte[] photoBytes, String path){

        try {
            InputStream in = new ByteArrayInputStream(photoBytes);
            BufferedImage bImageFromConvert = ImageIO.read(in);

            int rand = (new Random()).nextInt();
            File imageFile = new File(path + rand + ".jpg");
            ImageIO.write(bImageFromConvert, "jpg", imageFile);


            System.out.println("  saved, path: " + imageFile.getPath());

        }
        catch(FileNotFoundException e){
            System.out.println("  System nie moze odnalezc okreslonej sciezki do zapisu pliku !");
        }
        catch (IOException e) {
            System.out.println("  Unable to decode image !");
        }

    }

    public int stringToInt(String stringNumber) {
        int res = 0;
        for (int i = 0; i < stringNumber.length(); i++) {
            char c = stringNumber.charAt(i);
            if (c < '0' || c > '9') continue;
            res = res * 10 + c - '0';
        }
        return res;
    }

}
