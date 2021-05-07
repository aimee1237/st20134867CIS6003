package aa.st20134867.algorithms;

import aa.st20134867.image.ImageModel;
import java.util.concurrent.RecursiveAction;

public class PNormaliser extends RecursiveAction implements INormaliser<Double, Integer> {

	private static final long serialVersionUID = 1L;

	
	final int threshold = 3;
	static int min_height;
	static int min_weight;
	static int max_height;
	static int max_weight;
	
	ImageModel[] data;
	int start, end;
	
	public PNormaliser(ImageModel[] data, int start, int end) {
		this.data = data;
		this.start = start;
		this.end = end;
	}
	
	@Override
	public Double normalize(Integer val, Integer min, Integer max) {
		double n = (double)(val - min) / (double)(max - min);
		return n;
	}

	@Override
	protected void compute() {		
		if((end - start) < threshold) {
			
			for(int i = start; i < end; i++) {				
				double nheight = normalize(data[i].getHeight(), min_height, max_height);
				double nweight = normalize(data[i].getWeight(), min_weight, max_weight);
				data[i].setNheight(nheight);
				data[i].setNweight(nweight);				
			}			
		}
		else {			
			int middle = (start + end)/2;			
			invokeAll(new PNormaliser(data, start, middle), new PNormaliser(data, middle, end));			
		}
		
	}
}

