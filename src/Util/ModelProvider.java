package Util;

import java.util.ArrayList;
import java.util.List;

public enum ModelProvider {
	INSTANCE;
	private List<ExtractMethodResults> results;
	private ModelProvider(){
		results = new ArrayList<ExtractMethodResults>();
	}
	
	public List<ExtractMethodResults> getResults(){		
		return results;
	}

	public void setResults(List<ExtractMethodResults> results) {
		this.results = results;
	}		
}
