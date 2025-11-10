import java.util.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
public class ListenSafe {
    static String apiMainUrl="https://api.genius.com/";
	//Get the list of words to filter
	static List<String> wordsToFilter=getExplicitWords();
	
	//Takes in a search string and returns a List of JSON Objects
    static List<JSONObject>search(String searchString)
    {
    	searchString= URLEncoder.encode(searchString, StandardCharsets.UTF_8);;
        List<JSONObject> searchResult=new ArrayList<JSONObject>();
        
        try { 
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiMainUrl+"search?q="+searchString))
                .header("Authorization", "Bearer " + MyTokens.AccessToken)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject jsonObj = new JSONObject(response.body());
        JSONObject jsonResponse = jsonObj.getJSONObject("response");
        JSONArray hits = jsonResponse.getJSONArray("hits");
        
        
        // Iterate over hits populate it to the List
        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            JSONObject result=hit.getJSONObject("result");
            searchResult.add(result);
        }
         

        } catch (Exception e) {
            System.out.println("Exception encountered : "+e.getMessage());
        }
        return searchResult;
    }

    //From The lyrics url obtained in the JSON you get a a string of Lyrics
    static String getLyrics(String lyricsUrl)
    {  
    	String lyricsBody="";
    	try {
    		  HttpClient lyricClient = HttpClient.newHttpClient();
              HttpRequest lyricRequest = HttpRequest.newBuilder()
                      .uri(URI.create(lyricsUrl))
                      .header("Authorization", "Bearer " + MyTokens.AccessToken)
                      .GET()
                      .build();
              HttpResponse<String> lyricResponse = lyricClient.send(lyricRequest, HttpResponse.BodyHandlers.ofString());
              lyricsBody=lyricResponse.body().toLowerCase();
		} catch (Exception e) {
			 System.err.println("Exception encountered : "+e.getMessage());
		}
    	return lyricsBody;
    }
    
    ///Retrieve explicit words from the file
    static List<String> getExplicitWords()
    {
    	List<String> res=new ArrayList<String>();
    
    	///Currently using a txt file of all offensive words maybe add how to add words later
    	try(BufferedReader reader = new BufferedReader(new FileReader(MyTokens.BadWordsSource))) {
			///Open file
    		   String line;
            while ((line = reader.readLine()) != null) {
                // Process each line here
      			res.add(line);
            }
			
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Encountered Error : "+e.getLocalizedMessage());
		}
    	return res;
    }
    
    ///Add a new bad word to the List
    static void AddNewBadWord(String badWord)
    {
    	if(!wordsToFilter.isEmpty())
    	{
    		wordsToFilter.add(badWord);
    	}
    	
    	///Add to file 
    	try(FileWriter writer = new FileWriter(MyTokens.BadWordsSource, true)) {
			writer.write(badWord);
		} catch (Exception e) {
			System.err.println("Error appending to file: " + e.getMessage());
		}
    }
    
    //Return a map of whether the Lyrics has a bad word and the List of the bad Words
    static Map<Boolean,List<String>> checkIfHasBadWord(String lyricsBody) {
 
    	Boolean returnBool=false;
    	List<String> badWordsFound=new ArrayList<String>();
        for (String badword : wordsToFilter) {
            if(lyricsBody.contains(badword))
            {
               returnBool=true;
               badWordsFound.add(badword);
            }
		}
        return Map.of(returnBool,badWordsFound);
    }
    
    ///Remove an Item from the bad words List
}
