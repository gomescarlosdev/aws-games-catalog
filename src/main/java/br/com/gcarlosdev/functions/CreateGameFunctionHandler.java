package br.com.gcarlosdev.functions;

import br.com.gcarlosdev.model.Game;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
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
import java.util.UUID;

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

        try {
            JSONObject request = (JSONObject) parser.parse(reader);
            if (request.get("body") != null) {
                var body = (JSONObject) request.get("body");
                context.getLogger().log("received body: " + body.toString());

                var game = new Gson().fromJson(body.toJSONString(), Game.class);
                var gameId = UUID.randomUUID().toString();

                dynamoDB.getTable(DYNAMODB_TABLE).putItem(
                        new PutItemSpec().withItem(
                                new Item().withString("id", gameId)
                                        .withString("title", game.getTitle())
                                        .withString("genre", game.getGenre())
                                        .withString("publisher", game.getPublisher())
                                        .withNumber("release_year", game.getReleaseYear())
                                        .withNumber("rating", game.getRating())
                                        .withBoolean("multiplayer", game.isMultiplayer())
                                        .withStringSet("platforms", game.getPlatforms())
                        ));

                response.put("statusCode", 201);
                responseBody.put("body", String.format("{\"id\" : \"%s\"}", gameId));
            } else {
                response.put("statusCode", 400);
                responseBody.put("error", "Body can't be null");
            }
        } catch (Exception e) {
            context.getLogger().log("Error adding item to DynamoDB: "+ e.getMessage());
            responseBody.put("error", "Failed to add item to DynamoDB.");
            response.put("statusCode", 400);
        }
        response.put("body", responseBody.toString());

        writer.write(response.toString());
        reader.close();
        writer.close();
    }
}
