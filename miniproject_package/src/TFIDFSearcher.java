//Name:		
//Section: 	
//ID: 		

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TFIDFSearcher extends Searcher
{	

	double norm;
	Map<Integer, Map<String, Double>> tfidf = new HashMap<>();
	Map<String, Integer> doc_word_freq = new HashMap<>();
	Map<String, Double> idf = new HashMap<>();
	Map<Integer, Double> tfidf_normalized = new HashMap<>();
	
	public TFIDFSearcher(String docFilename) {
		super(docFilename); 
		/************* YOUR CODE HERE ******************/
		for(Document d: this.documents) {
			Set<String> terms = new HashSet<String>(d.getTokens());
			Map<String, Double> tf = new HashMap<>();
			for(String term: terms) {
				if (doc_word_freq.get(term) != null)
					doc_word_freq.put(term, doc_word_freq.get(term) + 1);
				else
					doc_word_freq.put(term, 1);
				tf.put(term, tf(d.getTokens(), term));
			}
			tfidf.put(d.getId(), tf);
		}

		for(String term: doc_word_freq.keySet()) {
			idf.put(term, Math.log10(1 + ((double) this.documents.size() / doc_word_freq.get(term))));
		}

		for(Integer i: tfidf.keySet()) {
			norm = 0;
			for(String term: tfidf.get(i).keySet()) {
				tfidf.get(i).put(term, tfidf.get(i).get(term) * idf.get(term));
				norm += Math.pow(tfidf.get(i).get(term), 2);
			}
			tfidf_normalized.put(i, Math.sqrt(norm));
		}
		/***********************************************/
	}

	public double tf(List<String> d, String t) {
		int f = 0;
		for(String s: d)
			if(t.equals(s)) f++;
		if(f == 0)	return 0;
		else		return 1 + Math.log10(f);
	}
	
	public double cosine_similarity(Map<String, Double> query, int docId) {
		double dot = 0, normq = 0, normd;
		HashSet<String> intersection = new HashSet<>(query.keySet());
		
		for(String term: query.keySet())
			normq += Math.pow(query.get(term), 2);
		normq = Math.sqrt(normq);
		normd = tfidf_normalized.get(docId);
		
		intersection.retainAll(tfidf.get(docId).keySet());
		for(String term: intersection)
			dot += query.get(term) * tfidf.get(docId).get(term);
		
		return dot/(normq*normd);
	}

	@Override
	public List<SearchResult> search(String queryString, int k) {
		/************* YOUR CODE HERE ******************/
		List<SearchResult> cosine_score = new ArrayList<>();
		List<SearchResult> results = new ArrayList<>();
		Map<String, Double> q_vector = new HashMap<>();
		Set<String> q_term = new HashSet<>(tokenize(queryString));
		
		for(String term: q_term) {
			if (idf.containsKey(term))
			q_vector.put(term, tf(tokenize(queryString), term) * idf.get(term));
		}
		
		for(Document d: this.documents) {
			cosine_score.add(new SearchResult(d, cosine_similarity(q_vector, d.getId())));
		}
		
		Collections.sort(cosine_score);
		
		for(int i = 0; i < k; i++) {
			results.add(cosine_score.get(i));
		}
		
		return results;
		/***********************************************/
	}
}
