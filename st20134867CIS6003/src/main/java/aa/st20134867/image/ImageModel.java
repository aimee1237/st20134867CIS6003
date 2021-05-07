package aa.st20134867.image;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ForkJoinPool;
import javax.imageio.ImageIO;

public class ImageModel {

	private File fp;
	private BufferedImage bufImg = null;
	private BufferedImage grayscaleImg = null;
	private BufferedImage edgeImg = null;
	
	private int weight;
	private int height;
	private String size;
	
	private double nweight;
	private double nheight;
	private double distance;
	
	
	public ImageModel(int weight, int height, String size) {
		this.weight = weight;
		this.height = height;
		this.size = size;
		
	}

	public ImageModel(BufferedImage bufferedImage){
		this.bufImg = bufferedImage;
		this.weight = bufferedImage.getWidth();;
		this.height = bufferedImage.getHeight();
		this.size = "M";
		
	}

	public ImageModel(File fp) throws IOException {
		this.bufImg = ImageIO.read(fp);
	}


	public void setFilePointer(File fp) {
		this.fp = fp;
	}
	
	public void LoadImage(File fp) throws IOException {
			bufImg = ImageIO.read(fp);
	}
	

	public BufferedImage getRGBImage() {
		return bufImg;
		
		}
	
	
	/**
	 * Introduce strategy pattern to this..
	 * @return
	 */
	public BufferedImage getGrayscaleImage() {
		if(this.grayscaleImg == null && this.bufImg != null)
			return convertRGBToGrayscale(this.bufImg);
			//return convertRGBToGrayscaleParallel(this.bufImg);
		else
			return this.grayscaleImg;
	}
	
	public BufferedImage getEdgeImage() {
		if(this.edgeImg == null && this.bufImg != null) {
			return computeImageEdges(this.bufImg);
		}
		else {
			return this.edgeImg;
		}
	}
	
	public BufferedImage convertRGBToGrayscale(BufferedImage img) {
		// TODO Auto-generated method stub

		this.grayscaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        long startTime = System.currentTimeMillis();

		int w = img.getWidth();
        int h = img.getHeight();
        
        int[] source = img.getRGB(0, 0, w, h, null, 0, w);
        int[] destination = new int[source.length];
        
		for(int y = 0; y < img.getHeight(); y++) {
			for(int x = 0; x < img.getWidth(); x++) {
				
				int rgbvalue   = source[(y)*w + (x)];

				//int rgbvalue = img.getRGB(x,y);

				int alpha = (rgbvalue >> 24) & 0xff;
				int red = (rgbvalue >> 16) & 0xff;
				int green = (rgbvalue >> 8) & 0xff;
				int blue = (rgbvalue) & 0xff;
				
				// grayscale = ( (0.3 * R) + (0.59 * G) + (0.11 * B) ).				
				int grayscale = (int) ((0.3 * red) + (0.59 * green) + (0.11 * blue));
				int new_pixel_value = 0xFF000000 | ( grayscale << 16 ) |
						(grayscale << 8 ) |
						(grayscale);
				
				destination[(y)*w + (x)] = new_pixel_value;
				
				//this.grayscaleImg.setRGB(x, y, new_pixel_value);
				
			}
		}
      
        long endTime = System.currentTimeMillis();
 
        System.out.println("filter took " + (endTime - startTime) + 
                " milliseconds.");
        
        this.grayscaleImg.setRGB(0, 0, w, h, destination, 0, w);
        
		return this.grayscaleImg;
	}
	
	
	public BufferedImage convertRGBToGrayscaleParallel(BufferedImage img) {
		
		this.grayscaleImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		int w = img.getWidth();
        int h = img.getHeight();
 
        int[] source = img.getRGB(0, 0, w, h, null, 0, w);
        int[] destination = new int[source.length];
        
        if(w % 2 == 1 || h % 2 == 1) {
        	System.out.println("Width or height is not divisable, need padding");
        	return null;
        }
        
        //identify path size;
        int patch_width = w/2;
        int patch_height = h/2;
        
 
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Available processors: " + processors);
 
        ImageFilter ifilter = new ImageFilter(source, 0, (w * h), destination);
        
        //ifilter.setThreshold_x(patch_width);
        //ifilter.setThreshold_y(patch_height);
        
        
        System.out.println("Array size is " + source.length);
        System.out.println("Threshold is " + ifilter.getThreshold());
        
        ForkJoinPool pool = new ForkJoinPool();
        
        long startTime = System.currentTimeMillis();
        pool.invoke(ifilter);        
        long endTime = System.currentTimeMillis();
 
        System.out.println("filter took " + (endTime - startTime) + 
                " milliseconds.");
 
        BufferedImage dstImage =
                new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        
        this.grayscaleImg.setRGB(0, 0, w, h, destination, 0, w);
		return this.grayscaleImg;
	}
	
	
	public BufferedImage computeImageEdges(BufferedImage img) {
		// TODO Auto-generated method stub
		
		this.edgeImg = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		for(int row = 0; row < img.getHeight(); row++) {
			for(int col = 0; col < img.getWidth(); col++) {
				
				int new_pixel_value  = 0;
				
				if (col-1 < 0 || row - 1 < 0 || col+1>=img.getWidth() || row+1 >= img.getHeight()){
					new_pixel_value = 0xFF000000;
					this.edgeImg.setRGB(col, row, new_pixel_value);
					continue;
				}
				
				int p1 = img.getRGB(col-1,row-1) & 0xff;
				int p2 = img.getRGB(col,row-1) & 0xff;
				int p3 = img.getRGB(col+1,row-1) & 0xff;
				
				int p4 = img.getRGB(col-1,row) & 0xff;
				int p5 = img.getRGB(col,row) & 0xff;
				int p6 = img.getRGB(col+1,row) & 0xff;
				
				int p7 = img.getRGB(col-1,row+1) & 0xff;
				int p8 = img.getRGB(col,row+1) & 0xff;
				int p9 = img.getRGB(col+1,row+1) & 0xff;
				
				int pixel_value = Math.abs((p1 + 2 * p2 + p3) - (p7 + 2*p8 + p9)) + Math.abs((p3 + 2 * p6 + p9) - (p1 + 2*p4 + p7));
				
				new_pixel_value = 0xFF000000 | ( pixel_value << 16 ) |
						(pixel_value << 8 ) |
						(pixel_value);
				
				this.edgeImg.setRGB(col, row, new_pixel_value);
				
			}
		}
		return this.edgeImg;
	}
	
	public double getNweight() {
		return nweight;
	}
	
	/**
	 * @param nweight the nweight to set
	 */
	public void setNweight(double nweight) {
		this.nweight = nweight;
	}

	/**
	 * @return the nheight
	 */
	public double getNheight() {
		return nheight;
	}

	/**
	 * @param nheight the nheight to set
	 */
	public void setNheight(double nheight) {
		this.nheight = nheight;
	}

	/**
	 * @return the distance
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * @param distance the distance to set
	 */
	public void setDistance(double distance) {
		this.distance = distance;
	}

	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	/**
	 * @return the height
	 */
	public int getHeight() {
		return height;
	}
	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		this.height = height;
	}
	/**
	 * @return the size
	 */
	public String getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(String size) {
		this.size = size;
	}
	
	@Override
	public String toString() {
		return "Image [width=" + weight + ", height=" + height + ", size=" + size + "]";
	}
	
}

