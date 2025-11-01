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

    static List<String>search()
    {
        List<String> searchResult=new ArrayList<String>();
        return searchResult;
    }

    static String makeGetRequest(String searchString)
    {
        ///Make the Get request to genius
    	searchString= URLEncoder.encode(searchString, StandardCharsets.UTF_8);;
    	
    	//Get the list of words to filter
    	List<String> wordsToFilter=getExplicitWords();
    	if(wordsToFilter.size()==0)
    	{
    		return "Words to filter cannot be empty";
    	}
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
        
        
        // Iterate over hits
        for (int i = 0; i < hits.length(); i++) {
            JSONObject hit = hits.getJSONObject(i);
            JSONObject result=hit.getJSONObject("result");
            System.out.println("Option to Select "+i);
            System.out.println("Artist Name: " + result.getString("artist_names"));
            System.out.println("Song title: " + result.getString("full_title"));
            System.out.println();
        }
        
        Scanner inputScanner=new Scanner(System.in);
        int input=0;
        while(true)
        {
        	System.out.println("Enter 0.01 to exit");
        	System.out.println("Enter an Option");
        	input=inputScanner.nextInt();
        	if(input==0.01)
        	{
                inputScanner.close();
        		return "User requested To exit";	
        	}
        	else if(input>= hits.length())
        	{
        		continue;
        	}
        	String lyricUrl=hits.getJSONObject(input).getJSONObject("result").getString("url");
        	String ArtistName=hits.getJSONObject(input).getJSONObject("result").getString("artist_names");
        	String SongName=hits.getJSONObject(input).getJSONObject("result").getString("full_title");
            HttpClient lyricClient = HttpClient.newHttpClient();
            HttpRequest lyricRequest = HttpRequest.newBuilder()
                    .uri(URI.create(lyricUrl))
                    .header("Authorization", "Bearer " + MyTokens.AccessToken)
                    .GET()
                    .build();
            HttpResponse<String> lyricResponse = lyricClient.send(lyricRequest, HttpResponse.BodyHandlers.ofString());
            String lyricsBody=lyricResponse.body().toLowerCase();
            
            for (String badword : wordsToFilter) {
                if(lyricsBody.contains(badword))
                {
                	System.out.println();
                	System.out.println("The song "+SongName+" By "+ArtistName+" has the explicit word "+badword);
                }
                else {
                	/// System.out.println("The song is clean and safe to Listen");
                }
			}

        	System.out.println();
        }
        } catch (Exception e) {
            System.out.println("Exception encountered : "+e.getMessage());
        }
        
        ///Return the body
    return "";
    }
    
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
			System.out.println("Encountered Error : "+e.getLocalizedMessage());
		}
    	return res;
    }
    public static void main(String[] args) {
       System.out.println( makeGetRequest("Glock In my lap"));
    }
}
