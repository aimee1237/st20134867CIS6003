package aa.st20134867.image;
import java.util.concurrent.RecursiveAction;

public class ImageFilter extends RecursiveAction {

    private int[] source;
    private int start;
    
    private int[] destination;    
    private final int threshold = 1000;
	
    private int length;
    
	public ImageFilter(int[] psource, int pstart, int plength, int[] pdestination) {
		
		source = psource;
		start = pstart;
		length = plength;
		destination = pdestination;
	
	}
		
	@Override
	protected void compute() {

		if((length) < (threshold)) {
			// compute..
			// With a specified coordinate (x, y) in the image, the ARGB pixel can be accessed in this way:
			// pixel   = rgbArray[offset + (y-startY)*scansize + (x-startX)];

			for(int p = start; p < (start+length); p++) {
					int pixel = source[p];
					
					int r = (pixel >> 16) & 0x000000ff;
					int g = (pixel >> 8) & 0x000000ff;
					int b = pixel & 0x000000ff;
					
					//Y = 0.299R + 0.587G + 0.114B
					int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);				
		            int dpixel = 0xFF000000 | (gray << 16) | (gray << 8 )
		                    | (gray);
		            
		            destination[p] = dpixel;
		            
		            //System.out.println("p: " + p + " , " + pixel + " r: " + r + " g: " + g + " b: " + b);
					
			}
			
			
			
			return;
		}
		
		int split = length / 2;
		
        invokeAll(new ImageFilter(source, start, split, destination),
                new ImageFilter(source, start + split, length - split, destination));
        
        
		
	}
	/**
	 * @return the threshold
	 */
	public int getThreshold() {
		return threshold;
	}

}