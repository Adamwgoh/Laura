package neuralnetwork;

//A input with its value and associated weight
class NInput{
	double weight = 0;
	Neuron neuron = null;
	int index = 0;
	
	NInput(double weight, Neuron neuron){
		this.weight = weight;
		this.neuron = neuron;
		this.index = neuron.nofInputs();
	}
	
	public double getWeight(){
		return weight;
	}
	
	public void setWeight(double newweight){
		this.weight = newweight;
	}
	
	public int getindex(){
		return index;
	}
	
	@Override
	public String toString(){
		return "NInput no. " + index + ", weight value of " + weight;
	}
}//end NInput class
