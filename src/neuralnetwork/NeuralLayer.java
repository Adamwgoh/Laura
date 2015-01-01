package neuralnetwork;

import java.util.ArrayList;
/**
 * @author dolphin
 * <p>A neural layer consist of a specified number of neurons.</p>
 * <p> If its the first layer, each neuron has only one input, else it takes the nof neurons of prev layer as nof input.</p>
 */
//A neurallayer of neurons.
public class NeuralLayer{
		private ArrayList<Neuron> neurons = new ArrayList<Neuron>();
		private ArrayList<ArrayList<Double>> Weights = new ArrayList<ArrayList<Double>>();
		private ArrayList<Double> outputs = new ArrayList<Double>();
		private double layer_fanin = 0; //layer fan-in returns the total input value for the layer.
		NeuralNetwork neuralnetwork;
		private double bias;	//this bias value belongs to the previous layer's bias. This is poor practice is done for convenience's sake.
		int index = 0;
		
		//constructor
		/**
		 * @author dolphin
		 * <p>A neural layer consist of a specified number of neurons.</p>
		 * <p> If its the first layer, each neuron has only one input, else it takes the nof neurons of prev layer as nof input.</p>
		 * @param  bias			 the intercept term related to this neural layer. The first layer of neurons do not have bias value as it is an input layer
		 * @param  nofneuron	 The number of neurons exist in this neuron layer
		 * @param  neuralnetwork the neural network this layer belongs to 
		 */
		public NeuralLayer(double bias, int nofneurons, NeuralNetwork neuralnetwork){
			//initialize the neurallayer with each layer initialize each neuron, each neuron initialize each Ninput
			for(int i=0; i<nofneurons; i++ ){
				index = neuralnetwork.getNofLayers()-1;
				if(index == 0){	//only one input if its the first layer
					Neuron n = new Neuron(1, this);
					neurons.add(n);
					Weights.add(n.getWeights());
					layer_fanin += n.getFanIn();
				}else{
					this.bias = bias;	//no bias value for the first layer.
					Neuron n = new Neuron(neuralnetwork.getLayer(this.index-1).NofNeurons(),this);
					neurons.add(n);//get previous layer's nofneurons as nofinputs to create this one.
					Weights.add(n.getWeights());
					layer_fanin += n.getFanIn();
				}
			}
		}
		
		
		
		public int getIndex(){
			return index;
		}
		
		public ArrayList<ArrayList<Double>> getWeights(){
			return Weights;
		}
		
		public double getLayerFanin(){
			return layer_fanin;
		}
		
		public int NofNeurons(){
			return this.neurons.size();
		}
		
		public Neuron getNeuron(int index){
			return neurons.get(index);
		}
		
		@Override
		public String toString(){
			return "NeuralLayer no. " + index + ", with nofneurons: " + NofNeurons() + ". Bias value is " + bias;
		}
		
		public double getBias(){ 
			return bias; 
		}
		
		public void setBias(double newbias){ 
			this.bias = newbias;
		}
		
		public ArrayList<Double> getDeltas(){
			ArrayList<Double> deltas = new ArrayList<Double>();
			for(Neuron n : neurons){
				deltas.add(n.getdelta());
			}
			return deltas;
		}
		
		public boolean isFirstLayer(){
			return this.index == 0; 
		}
		
		public boolean isLastLayer(){
			return this.index==(neuralnetwork.getNofLayers()-1); 
		}
		
		public NeuralLayer getPreviousLayer(){ 
			if(isLastLayer()){
				throw new Error("This is the first layer, no previous layer found");
			}
				return neuralnetwork.getLayer(index-1);	
		}
		
		public NeuralLayer getNextLayer(){ 
			if(isLastLayer()){
				throw new Error("This is the last layer, no next layer found");
			}
				return neuralnetwork.getLayer(index+1);	
		}
		
		//receives given inputs and produces an output for each neuron.
		public ArrayList<Double> getOutputs(ArrayList<Double> inputs){	
			double output;
				for(int i=0; i<NofNeurons(); i++){
					output = neurons.get(i).ActivationOutput(inputs);
					outputs.add(output);
				}
			return outputs;
		}
}//end of NeuralLayer class
