import java.util.*;
import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.json.JSONArray;
import org.json.JSONObject;
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
    	searchString=searchString.replaceAll(" ", "");
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
        		break;
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
            if(lyricsBody.contains("niggas"))
            {
            	System.out.println();
            	System.out.println("The song "+SongName+" By "+ArtistName+" has the explicit word niggas");
            }
            else {
            	 System.out.println("The song is clean and safe to Listen");
            }
        	System.out.println();
        }
        inputScanner.close();
        } catch (Exception e) {
            System.out.println("Exception encountered : "+e.getMessage());
        }
        
        ///Return the body
    return "";
    }
    
    public static void main(String[] args) {
        makeGetRequest("Russian Cream");
    }
}
