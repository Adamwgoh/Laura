package neuralnetwork;

import java.util.ArrayList;
import java.util.Iterator;


public class Neuron {
	private ArrayList<Double> InputWeights  = new ArrayList<Double>();
	//private ArrayList<Double> OutputWeights = new ArrayList<Double>();
	public ArrayList<NInput> ninputs		= new ArrayList<NInput>();
	private NeuralLayer neurallayer;
	private double delta = 0;//error term delta for each neuron. 
	private int index = 0;
	private final double sparsityVal = 0.05; //sparsity parameter used with KL-divergence algorithm. used an example 0.05
	
	public Neuron(int nofInputLayers, NeuralLayer neurallayer){
		this.neurallayer = neurallayer;
		index = neurallayer.NofNeurons();
		//initialize input with default weight
		for(int i=0; i<nofInputLayers; i++){
			ninputs.add(new NInput(1.0, this));//initializing weights should be random'd and close to 0. Use an init func.
			InputWeights.add(1.0);
		}	
	}
	
	public double getdelta(){
		return delta;
	}
	
	public void setdelta(double delta){
		this.delta = delta;
	}	

	public int nofInputs(){
		return this.ninputs.size();
	}
	
	public NInput getNInput(int index){
		return ninputs.get(index);
	}
	
	@Override
	public String toString(){
		return "Neuron no. " + index + "of neurallayer no. " + neurallayer.getIndex() + ", with nofinputs: " + nofInputs();
	}
	
	public ArrayList<Double> getWeights(){
		ArrayList<Double> inputweights = new ArrayList<Double>()
				;
		for(NInput n : ninputs){
			inputweights.add(n.getWeight());
		}
		return inputweights;
	}
	
	//Done:	gather all inputweights
	public double getFanIn(){
		Iterator it = getWeights().iterator();
		double fan_in = 0;
		while(it.hasNext()){
			fan_in += (double) it.next();
		}
		return fan_in;
	}
	
	//activation function here, using sigmoid
	public double activationFunc(double netinput){
		netinput = 1/(1 + Math.exp(-netinput));	//or use tanh? Tanh: (e^x-e^-x)/(e^x + e^-x)
		return netinput;
	}
	
	public double netInputSignal(ArrayList<Double> inputs){
		//iterate through both arraylist and associate them accordingly, return the sum.
		double sum = 0;
		Iterator val_it    = inputs.iterator();
		Iterator weight_it = this.ninputs.iterator();
		if(inputs.size() != ninputs.size()){
			System.err.println("ERROR: Input amount not matched with given nof weights");
			return 0;
		}
		while(val_it.hasNext()){
			if(!weight_it.hasNext()){
				System.out.println("WARNING: non-weighted values present");
				sum += (double) val_it.next();
			}
			sum += (double) val_it.next()*(double) weight_it.next();
		}
		return sum + neurallayer.getBias();
	}
	
	//activation output val
	public double ActivationOutput(ArrayList<Double> inputs){
		return activationFunc(netInputSignal(inputs));
	}
	
	
	//rho value for KL-divergence formula. returns a average activation value of the neuron 
	//applicable only for hidden layer units
	public double getAvgActivationVal(ArrayList<Double> inputs){
		//TODO: fix this to proper throw statements
		if (index == 0){
			System.out.println("only applicable for hidden units!");
			return 0;
		}else if(inputs.size() != ninputs.size()){
			System.out.println("only applicable for hidden units!");
			return 0;
		}
		double avgRho = 0;
		double totalActVal = 0; //total activation*input val
		// 1/m * Epsilon( activation val*inputVal);
		for(int i = 0; i < ninputs.size(); i++){
			totalActVal += ((double) ActivationOutput(inputs)* inputs.get(i));
		}
		avgRho =  totalActVal / (double) inputs.size();
		
		return  avgRho;
	}
	
	public double  KLdivergence(ArrayList<Double> inputs){
		double penaltyVal;
		double p2 = getAvgActivationVal(inputs); 
		double p = sparsityVal;
		penaltyVal = ( p * Math.log((p/p2)) ) + ( (1 - p)*Math.log((1 - p)/(1-p2)) );
		
		return penaltyVal;
	}

	

}//end Neuron class
