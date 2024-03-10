package br.com.gcarlosdev.functions;

import br.com.gcarlosdev.model.Game;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.google.gson.Gson;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class CreateGameFunctionHandler implements RequestStreamHandler {

    private static final String DYNAMODB_TABLE = "Games";

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        var writer = new OutputStreamWriter(outputStream);
        var reader = new BufferedReader(new InputStreamReader(inputStream));
        var parser = new JSONParser();
        var response = new JSONObject();
        var responseBody = new JSONObject();

        var dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();
        var dynamoDB = new DynamoDB(dynamoDBClient);

        String id;
        Item item = null;
        try{
            var request = (JSONObject) parser.parse(reader);

            if(request.get("pathParameters") != null){
                var pathParameters = (JSONObject) request.get("pathParameters");
                if (pathParameters.get("id") != null) {
                    id = pathParameters.get("id").toString();
                    item = dynamoDB.getTable(DYNAMODB_TABLE).getItem("id", id);
                }
            } else if(request.get("queryStringParameters") != null){
                var queryStringParameters = (JSONObject) request.get("queryStringParameters");
                if (queryStringParameters.get("id") != null) {
                    id = queryStringParameters.get("id").toString();
                    item = dynamoDB.getTable(DYNAMODB_TABLE).getItem("id", id);
                }
            }

            if (item != null) {
                var game = new Gson().fromJson(item.toJSON(), Game.class);
                responseBody.put("game", game);
                response.put("statusCode", 200);
            }else {
                responseBody.put("message", "Game Not Found");
                response.put("statusCode", 404);
            }

            response.put("body", responseBody.toString());

        }catch (Exception e){
            context.getLogger().log("Error: "+ e.getMessage());
        }

        writer.write(response.toString());
        reader.close();
        writer.close();
    }
}
