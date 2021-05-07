package aa.st20134867.algorithms;

import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

import aa.st20134867.image.ImageModel;

public class Knn {

	private int 				k;
	private ImageModel 			unknown;
	private String				result;
	private double				confidence;
	private List<ImageModel> 	data;
	
	
	public Knn() {
	}
	
	public Knn(int k, List<ImageModel> data, ImageModel unknown) {
		this.k = k;
		this.data = data;
		this.unknown = unknown;
	}
	

	public void preprocessdata() {
		
		// find min, and max height values. 
		int min_height = data.stream()
				.min(Comparator.comparing(ImageModel::getHeight))
				.map(ImageModel::getHeight)
				.get();
		
		int max_height = data.stream()
				.max(Comparator.comparing(ImageModel::getHeight))
				.map(ImageModel::getHeight)
				.get();
		
		// find min and max weights
		int min_weight = data.stream()
				.min(Comparator.comparing(ImageModel::getWeight))
				.map(ImageModel::getWeight)
				.get();
		
		int max_weight = data.stream()
				.max(Comparator.comparing(ImageModel::getWeight))
				.map(ImageModel::getWeight)
				.get();		

		// serial implementation
		this.data.forEach(item -> {			
			double nheight = (double)(item.getHeight() - min_height) / (double)(max_height - min_height);
			double nweight = (double)(item.getWeight() - min_weight) / (double)(max_weight - min_weight);
			
			//save the normalized height and weight inside the object itself
			item.setNheight(nheight);
			item.setNweight(nweight);						
		});
		
		
		ForkJoinPool fjpool = new ForkJoinPool();
		ImageModel[] obj_array = this.data.toArray(new ImageModel[this.data.size()]);
		
		PNormaliser norm_task = new PNormaliser(obj_array, 0, this.data.size());
		PNormaliser.max_height = max_height;
		PNormaliser.max_weight = max_weight;
		PNormaliser.min_height = min_height;
		PNormaliser.min_weight = min_weight;
		
		fjpool.invoke(norm_task);
		
		this.data.forEach(item -> System.out.println(item + " norm. height: " + item.getNheight() + " , norm. weight: " + item.getNweight()));
	
		//normalise your unknown object
		double nheight_unknown = (double)(this.unknown.getHeight() - min_height) /(double)(max_height - min_height);
		double nweight_unknown = (double)(this.unknown.getWeight() - min_weight) /(double)(max_weight - min_weight);
		
		this.unknown.setNheight(nheight_unknown);
		this.unknown.setNweight(nweight_unknown);
	}
	
	public void computedistance() {
				    
		this.data.forEach(item -> {			
			double square_sum = Math.pow((item.getNheight() - unknown.getNheight()),2) 
					+ Math.pow((item.getNweight() - unknown.getNweight()),2);
			
			//for loop to go through all pixels
			
			double distance = Math.sqrt(square_sum);
			item.setDistance(distance);			
		});
		
	}
	
	public void classify() { 
		
		// option 1
		Collections.sort(this.data, new Comparator<ImageModel>() {
			@Override
			public int compare(ImageModel o1, ImageModel o2) {
				return Double.compare(o1.getDistance(), o2.getDistance());
			}
		});
		
		//option 2;		
		this.data.sort((ImageModel t1, ImageModel t2) -> Double.compare(t1.getDistance(), t2.getDistance()));
		
		this.data.forEach(item -> System.out.println(item + " distances; " + item.getDistance()));
		
		// get the closest K neighbours to the unknown object
		List<ImageModel> klist = this.data.subList(0, this.k);
		
		// find majority class
		// 1) find the L count

		int large_image_count = 0;
		int medium_image_count=0;
		for (ImageModel kItem:klist){
			if (kItem.getSize().equals("L")){
				large_image_count++;
			} else if (kItem.getSize().equals("m")){
				medium_image_count++;
			}
		}

		if(large_image_count > medium_image_count) {
			this.result = "L";
			this.confidence = 100 * ((double)large_image_count / klist.size());
		}
		else {
			this.result = "M";
			this.confidence = 100 * ((double)medium_image_count / klist.size());
		}
		
		System.out.println("Result: " + this.result + " , confidence: " + this.confidence);
		
	}
		
	/**
	 * @return the k
	 */
	public int getK() {
		return k;
	}

	/**
	 * @param k the k to set
	 */
	public void setK(int k) {
		this.k = k;
	}

	/**
	 * @return the data
	 */
	public List<ImageModel> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(List<ImageModel> data) {
		this.data = data;
	}

	/**
	 * @return the unknown
	 */
	public ImageModel getUnknown() {
		return unknown;
	}

	/**
	 * @param unknown the unknown to set
	 */
	public void setUnknown(ImageModel unknown) {
		this.unknown = unknown;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the confidence
	 */
	public double getConfidence() {
		return confidence;
	}

	/**
	 * @param confidence the confidence to set
	 */
	public void setConfidence(double confidence) {
		this.confidence = confidence;
	}

	
}