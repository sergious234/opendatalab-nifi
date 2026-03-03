import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients

class MongoConnection {
    private static final String password = "\$ergio13~"
    private static final String username = "sergio"
    private static final String uri = String.format("mongodb://%s:%s@localhost:27017", username, password);

    static Optional<MongoClient> getMongoClient() {
        try {
            MongoClient mongoClient = MongoClients.create(uri);
            return Optional.ofNullable(mongoClient);
        } catch (Exception ignored) {
            return Optional.empty()
        }
    }
}
