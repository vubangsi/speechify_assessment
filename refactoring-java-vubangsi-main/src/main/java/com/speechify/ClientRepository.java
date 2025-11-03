package com.speechify;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

public class ClientRepository {

    private static final String DB_FILE = "db.json";
    private final ObjectMapper objectMapper;
    private final LRUCache<Client> clientCache;
    private final LRUCache<List<Client>> clientListCache;

    public ClientRepository(LRUCache<Client> clientCache,LRUCache<List<Client>> clientListCache) {
        this.objectMapper = new ObjectMapper();
        this.clientCache = clientCache;
        this.clientListCache = clientListCache;
    }

    public CompletableFuture<Client> getById(String id) {
        return CompletableFuture.supplyAsync(() -> {
         //   try {
                //File dbFile = new File(DB_FILE);
                Client cachedClient = clientCache.get(id);
                
                if(cachedClient != null){
                    return cachedClient;
                }
                Client client = loadClientFromDBById(id);
                
                if(client != null){
                    clientCache.set(id, client);
                    
                }
                return client;
         //   } catch (IOException e) {
          //      return null;
         //   }
        });
    }

    private Client loadClientFromDBById(String id) {
        try{
            File dbFile = new File(DB_FILE);
            if (!dbFile.exists()) {
                return null;
            }

            ObjectNode root = (ObjectNode) objectMapper.readTree(dbFile);
            ArrayNode clients = (ArrayNode) root.get("clients");

            for (int i = 0; i < clients.size(); i++) {
                ObjectNode clientNode = (ObjectNode) clients.get(i);
                if (clientNode.get("id").asText().equals(id)) {
                    Client client = new Client();
                    client.setId(clientNode.get("id").asText());
                    client.setName(clientNode.get("name").asText());
                    return client;
                }
            }
            
            return null;
        } catch (IOException e) {
            return null;
        }
    }

    public CompletableFuture<List<Client>> getAll() {
        return CompletableFuture.supplyAsync(() -> {
           List<Client> cachedClients = clientListCache.get("allClient");
           if(cachedClients != null){
            return cachedClients;
           }
           List<Client> clients = loadClientsFromDB();
           clientListCache.set("allClient", clients);
           return clients;
        });
    }

    private List<Client> loadClientsFromDB() {
        try{
            File dbFile = new File(DB_FILE);
            if (!dbFile.exists()) {
                return new ArrayList<>();
            }

            ObjectNode root = (ObjectNode) objectMapper.readTree(dbFile);
            ArrayNode clients = (ArrayNode) root.get("clients");

            List<Client> clientList = new ArrayList<>();
            for (int i = 0; i < clients.size(); i++) {
                ObjectNode clientNode = (ObjectNode) clients.get(i);
                Client client = new Client();
                client.setId(clientNode.get("id").asText());
                client.setName(clientNode.get("name").asText());
                clientList.add(client);
            }
            return clientList;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }
} 