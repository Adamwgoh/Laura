package neuralnetwork;

import java.util.ArrayList;
/**
 * The implentaion of neural network. Combines all classes together from the input to Neurons to NeuralLayer together
 */
import java.util.Random;
import java.util.Vector;
public class NeuralNetwork{
	private ArrayList<NeuralLayer> nlayers = new ArrayList<NeuralLayer>();
	//the danger of using such variable to store all weights instead of accessing the layers is unsync'd values, resulting in difference in values, hence errors.
	private ArrayList<ArrayList<ArrayList<Double>>> allWeights = new ArrayList<ArrayList<ArrayList<Double>>>();//has all weights of all neurons indexed
	private double total_weight  = 0;
	
	public NeuralNetwork(Vector<Integer> noflayers){
		//initialize neurallayers
		for(int i = 0; i<noflayers.size(); i++){
			NeuralLayer layer = new NeuralLayer(1.0 ,noflayers.elementAt(i), this);
			//Add a neural layer, with default bias as 1.s
			nlayers.add(layer);//parameters are: intercept term, nofneurons, and neural network associate
			allWeights.add(layer.getWeights());
			total_weight += layer.getLayerFanin();
			
		}
	}
	
	/**
	 * <p>Initialize all input weights to a value and setting up bias values</p>
	 */
	public void init(){
		Random rand = new Random();
		//initialize all weights
		for(int i=0; i<getNofLayers(); i++){
			for(int j =0; i<nlayers.get(i).NofNeurons(); j++){
				for(int k = 0; k<nlayers.get(i).getNeuron(j).nofInputs(); k++){
					//nextGaussian provides the next norm dist. value wit mean of 0 & std deviation of 1. multiplying it makes it to 0.01^2
					double newweight = rand.nextGaussian()*Math.pow(0.01, 2); 	
					nlayers.get(i).getNeuron(j).getNInput(k).setWeight(newweight);
					allWeights.get(i).get(j).set(k, newweight);	//changes weight in here to be sync'd
				}
			}
		}
		
		//initialize all bias
		for (int i = 0; i< getNofLayers(); i++){
			double newbias = rand.nextGaussian()*Math.pow(0.01, 2);
			nlayers.get(i).setBias(newbias);
		}
	}
	
	/**
	 * <p> resets all weights and bias values to 0 </p>
	 */
	public void reset(){
		//set all weights to 1
		for(int i=0; i<getNofLayers(); i++){
			for(int j =0; i<nlayers.get(i).NofNeurons(); j++){
				for(int k = 0; k<nlayers.get(i).getNeuron(j).nofInputs(); k++){
					nlayers.get(i).getNeuron(j).getNInput(k).setWeight(1.0);
				}
				
			}
		}
		
		//set all bias to 0
		for(int i = 0; i < getNofLayers(); i++){
			nlayers.get(i).setBias(0);
		}
	}
	
	@Override
	public String toString(){
		int nofneurons = 0;
		int nofinputs = 0;
		for(int i=0; i< nlayers.size(); i++){
			nofneurons += nlayers.get(i).NofNeurons();
			for(int j=0; j< nlayers.get(i).NofNeurons(); j++){
				nofinputs += nlayers.get(i).getNeuron(j).nofInputs();
			}
		}
		return "Neural network:  noflayers :" + getNofLayers() + ", nofneurons :" + nofneurons + ", nofinputs :" + nofinputs;
	}
	
	public int getNofLayers(){
		return nlayers.size();
	}
	
	/**
	 * <p> Returns the specific weight associated with the specified input. </p>
	 * @param nlayer	layer index where the requested weight is located
	 * @param nneuron	neuron index where the requested weight is located
	 * @param ninput	input index where the requested weight is located
	 * @return			the specified weight
	 */
	public double getWeight(int nlayer, int nneuron, int ninput){
		double weight;
		weight = allWeights.get(nlayer).get(nneuron).get(ninput);
		return weight;
	}
	
	public void setWeight(int nlayer, int nneuron, int ninput, double newweight){
		nlayers.get(nlayer).getNeuron(nneuron).getNInput(ninput).setWeight(newweight);
		allWeights.get(nlayer).get(nneuron).set(ninput, newweight);	//changes weight in here to be sync'd
	}
	
	public ArrayList<NeuralLayer> getAllLayers(){
		return nlayers;
	}
	
	public NeuralLayer getTopLayer(){
		return nlayers.get(0);
	}
	
	public NeuralLayer getBottomLayer(){
		return nlayers.get(nlayers.size()-1);
	}
	
	public NeuralLayer getLayer(int index){
		return nlayers.get(index);
	}
	
	/**
	 * <p> Calculates the given output for each neuron for with the given input</p>
	 * 
	 * @param inputs	The input values
	 * @return outputs	An arraylist of arraylist with outputs for each layer's neurons.
	 */
	public ArrayList<ArrayList<Double>> calculate(ArrayList<Double> inputs){
		ArrayList<ArrayList<Double>> outputs = new ArrayList<ArrayList<Double>>();
		outputs.add(nlayers.get(0).getOutputs(inputs));//first layer
		
		for(int i=1; i<getNofLayers();i++){
			outputs.add( nlayers.get(i).getOutputs(outputs.get(i-1)) );
		}
		if(outputs.size() > 1){
			System.out.println("WARNING: Final output value is more than 1");
		}
		return outputs; 
	}
	
	public void normalizeInputs(ArrayList<Double> inputs, int minrange, int maxrange){
		int maxindex = findLargest(inputs);
		int minindex = findSmallest(inputs);
		
		for(int i=0;i<inputs.size(); i++){
			double val = ((  (inputs.get(i) - inputs.get(minindex))*(maxrange - minrange)  )/		//y = ((x - xmin)*(d2 - d1))/(xmax - xmin) + d1
						 (	inputs.get(maxindex) - inputs.get(minindex)	 )) + minrange;
			inputs.set(i, val);
							
		}
	}
	
	public int findLargest(ArrayList<Double> arr){	//return index of largest value in arraylist
		int k = 0;
		for(int i=0; i<arr.size(); i++){
			if (arr.get(i) > arr.get(k)){
				k = i;
			}
		}
		
		return k;
		
	}
	
	public int findSmallest(ArrayList<Double> arr){	//return index of smallest value in arrayList
		int k = 0;
		for(int i=0; i<arr.size(); i++){
			if (arr.get(i) < arr.get(k)){
				k = i;
			}
		}
		
		return k;
	}

}//end of NeuralNetwork

class Trainer{
	private IOValues trainpairs;
	private IOValues testpairs;
	private NeuralNetwork neuralnetwork;
	//TODO: change value to a given value, either in a properties or a fixed value
	private final double LearningRate = 0;//learning rate alpha in modifying weights of each neuron
	private final double DecayRate = 0;//decay rate parameter to prevent overfitting
	public Trainer(NeuralNetwork neuralnetwork){
		this.neuralnetwork = neuralnetwork;
	}
	
	public Trainer(NeuralNetwork neuralnetwork, IOValues trainpairs){
		this.neuralnetwork = neuralnetwork;
		this.trainpairs = trainpairs;
	}
	
	public boolean addTrainPair(ArrayList<Double> input, ArrayList<Double> output){
		return trainpairs.add(input, output);
	}
	
	public IOPair getTrainPair(int index){
		return trainpairs.getIOPair(index);
	}
	
	public boolean addTestPair(ArrayList<Double> input, ArrayList<Double> output){
		return testpairs.add(input, output);
	}
	
	public IOPair getTestPair(int index){
		return testpairs.getIOPair(index);
	}
	
	/**
	 * <p>Updates weights and bias with the new caluclated ones. New weights and bias are calculated by the following formulas: </p>
	 * <p>
	 * @param neuralnetwork
	 */
	private void calculateDerivativesAndUpdate(NeuralNetwork neuralnetwork, IOValues trainpairs){
		//will just get 1 because all those input only gives one specific output, plate or no plate.
		//wrote in an arraylist to provide flexibility and increase reusability of codes.
		ArrayList<Double> inputs		   = trainpairs.getAllInputs();
		ArrayList<ArrayList<Double>> outputs = neuralnetwork.calculate(inputs);//outputs of all neurons, first being the input layers and last being the output layer
		
		//calculate the desired partial derivatives of weights and bias
		//dWlJ(W,b:x,y) = deltal+1 T al
		//dblJ(W,b:x,y) = deltal+1
		double derivative_weights = 0;
		double derivativeBias = 0;
		double newweight = 0;
		double newbias = 0;
		for(int layer = neuralnetwork.getNofLayers()-2; layer >0; layer--){	//for each layer
			for(int neuron = 0; neuron <neuralnetwork.getLayer(layer).NofNeurons(); neuron++ ){
				/*
				 *for as many neurons are there in the next layer,
				 *grab its delta value for each neuron and multiply by the current neuron
				 *stores value as the derivative_weight 
				 */
				double[] deltas = new double[neuralnetwork.getLayer(layer).getNextLayer().NofNeurons()];
				for(int i=0; i<deltas.length; i++){
					deltas[i] = neuralnetwork.getLayer(layer).getNextLayer().getNeuron(i).getdelta();
					derivative_weights += (outputs.get(layer).get(neuron))*deltas[i];
				}
				updateWeight(derivative_weights, layer, neuron);
				
			}
			updateBias(derivativeBias, layer);
		}
		
		
	}
	
	private void updateBias(double derivativeBias, int layer){
		double newbias = 0;
		
		derivativeBias = neuralnetwork.getBottomLayer().getNeuron(0).getdelta(); //get output layer's delta as bias derivative
		//B = B - learningrate*(1/m*biasderivative);
		newbias = neuralnetwork.getLayer(layer).getBias() - LearningRate*((1/neuralnetwork.getNofLayers())*derivativeBias);
		neuralnetwork.getLayer(layer).setBias(newbias);
	}
	
	private void updateWeight(double derivative_weights, int layer, int neuron){
		ArrayList<Double> weights = neuralnetwork.getLayer(layer).getNeuron(neuron).getWeights();
		double newweight = 0;
		for(double w : weights){
			int index = 0;
			//W = W - learningrate*( (1/m*Wderivatives) + decayrate*W  );
			newweight = w - LearningRate*( ((1/neuralnetwork.getNofLayers())*derivative_weights) + (DecayRate*w) );
			neuralnetwork.getLayer(layer).getNeuron(neuron).getNInput(index).setWeight(newweight);
			index++;
		}
	}
	
	public void updateDelta(NeuralNetwork neuralnetwork, IOValues trainpairs){//check what to feed it with
		double delta;
		double previous_delta;//calculate for output layer's delta
		//will just get 1 because all those input only gives one specific output, plate or no plate.
		//wrote in an arraylist to provide flexibility and increase reusability of codes.
		double expected_output = trainpairs.getAllExpectedOutput().get(0);
		ArrayList<Double> inputs		   = trainpairs.getAllInputs();
		ArrayList<ArrayList<Double>> outputs = neuralnetwork.calculate(inputs);//outputs of all neurons, first being the input layers and last being the output layer
		
			//calculate for last layer's delta
			double activation_val = outputs.get(outputs.size()-1).get(0);
			//DONE:	Might want to check out why the negative on the error gradient.
			//Ans: if expected = 3, activate = 2, then 3-2 = 1, hence activate value is error off by -1.
			//		same thing goes if expected = 2, activate = 3, 2-3 = -1, hence there's a activate value is error off by +1
			previous_delta = (-1*(expected_output - activation_val) ) * (activation_val*(1-activation_val));
			neuralnetwork.getBottomLayer().getNeuron(0).setdelta(previous_delta);
			//change delta value of neuron
			//delta - (y - a).*(a(1-a));
			
			for(int layer = neuralnetwork.getNofLayers()-2; layer > 0; layer--){//for each layer's nuerons, calculate its delta value
				for(int neuron = 0; neuron < neuralnetwork.getLayer(layer).NofNeurons(); neuron++){
					/*
					 * for as many neurons are there in the next layer, 
					 * grab it's nth Ninput of each neuron. 
					 * N being the index of current layer's neuron
					 */
					double[] weights = new double[neuralnetwork.getLayer(layer).getNextLayer().NofNeurons()];
					double   weightedDeltas = 0;	//contains the sum of elem-wise product of weights and next layer's delta value
					for(int i=0; i<weights.length; i++){
						//these are each nth weights of each neuron of the next layer
						weights[i] = neuralnetwork.getLayer(layer).getNextLayer().getNeuron(i).getNInput(neuron).getWeight();
						weightedDeltas += weights[i]*previous_delta;
					}
					delta = (   weightedDeltas * 
							((outputs.get(layer).get(neuron) * (1-outputs.get(layer).get(neuron)) ))   );
					//deltal = (Wl T deltal+1).*al(1-al)
					neuralnetwork.getLayer(layer).getNeuron(neuron).setdelta(delta);
					
					previous_delta = delta;
				}
			}	
	}
	
	public void backpropagation(NeuralNetwork neuralnetwork, IOValues trainpairs){
		updateDelta(neuralnetwork, trainpairs);
		calculateDerivativesAndUpdate(neuralnetwork, trainpairs);
		
	}
	
}//end class of Trainer

