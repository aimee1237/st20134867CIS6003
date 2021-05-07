package aa.st20134867.file;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Color;

import aa.st20134867.image.ImageModel;


public abstract class FileReader implements IFileReader {

    String filename;

    BufferedInputStream bufferedInputStream = null;
    FileInputStream fileInputStream = null;
    FileInputStream bufferedInputStream_images = null;
    //Using byte streams
    int number_of_labels;
    int number_of_images;
    int image_width;
    int image_height;
    int image_size = 1024;
    int size;

    List<ImageModel> image_list = new ArrayList();


    public void Filereader() throws IOException {
        FileInputStream in = null;
        int size;
        try {
            in = new FileInputStream("FileInputStreamJava.java");

            System.out.println("Total Available Bytes: " + (size = in.available()));
            for (int i = 0; i < size; i++) {
                System.out.print((char) in.read());
            }
        } catch (IOException e) {
            System.out.println("I/O Error: " + e);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    public void read() throws IOException {

        if (this.filename != null) {
            try {
                fileInputStream = new FileInputStream(new File(this.filename));
                bufferedInputStream = new BufferedInputStream(fileInputStream);
                System.out.println("Filename: " + this.filename);
                System.out.println("Total Available Bytes: " +
                        (size = fileInputStream.available()));
                this.image_list = new ArrayList<ImageModel>();

                int label = bufferedInputStream.read();
                int count = 1;
                while (label != -1) {
                    System.out.println("Label:" +  (label & 0xFF) + "\t\tImage-Number:" + count);

                    byte[] red_data = new byte[1024];
                    bufferedInputStream.read(red_data);
                    byte[] green_data = new byte[1024];
                    bufferedInputStream.read(green_data);
                    byte[] blue_data = new byte[1024];
                    bufferedInputStream.read(blue_data);

                    BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
                    for (int i = 0; i < 32; i++) {
                        for (int j = 0; j < 32; j++) {
                            Color color = new Color(
                                    red_data[i * 32 + j] & 0xFF,
                                    green_data[i * 32 + j] & 0xFF,
                                    blue_data[i * 32 + j] & 0xFF);
                            img.setRGB(i, j, color.getRGB());
                        }
                    }

                    ImageModel imageModel = new  ImageModel(img);

                    image_list.add(new ImageModel(img));
                    count++;
                    label = bufferedInputStream.read();
                }
            } catch (Exception e) {
                // if any I/O error occurs
                e.printStackTrace();
            } finally {
                // releases system resources associated with this stream
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            }
        }
    }


    public void write(int b) throws IOException {
        String source = "Learning Java IO";

        byte buf[] = source.getBytes();
        FileOutputStream out = null;

        try {
            out = new FileOutputStream("outfile.txt");
            for (int i = 0; i < buf.length; i += 2)
                out.write(buf[i]);
        } catch (IOException e) {
            System.out.println("I/O Error: " + e);
        } finally {
            if (out != null) {
                out.close();
            }
        }

    }

    public List<ImageModel> getData() {
        return this.image_list;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    private int read4bytes(FileInputStream filename) throws IOException {
        int bufimg = filename.read() << 24 | filename.read() << 16 | filename.read() << 8 | filename.read();
        return bufimg;
    }
}
 