//Name: 
//Section: 
//ID: 

import java.util.*;

public class JaccardSearcher extends Searcher{

	public JaccardSearcher(String docFilename) {
		super(docFilename);
		/************* YOUR CODE HERE ******************/
		//Tokenize a string (No duplicate)
		List<String> word = new ArrayList<String>();
		for (Document d : super.documents){
			for (String i: d.getTokens())
			{
				if (word.contains(i) == false){
					word.add(i);
				}
			}
		}
		/***********************************************/
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<String> query = Searcher.tokenize(queryString);
		List<String> term;
		List<SearchResult>results = new LinkedList<>();
		double score = 0;
		for (Document i : documents)
		{
			List<String> Union = new ArrayList<>(query);
			List<String> Intersect = new ArrayList<>(query);

			term = i.getTokens();
			Union.addAll(term);
			Intersect.retainAll(term);
			double u_size = Union.size();
			double in_size = Intersect.size();
			if (u_size == 0 || in_size == 0)
				score = 0;
			else {
				score = in_size / u_size;
			}
			results.add(new SearchResult(i,score));
		}
		Collections.sort(results);
		return results.subList(0,k);
		/***********************************************/
	}
