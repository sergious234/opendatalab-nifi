import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients

class MongoConnection {
		private static final String username = "root"
		private static final String password = "example"
		private static final String uri = String.format("mongodb://%s:%s@mongodb:27017", username, password);

	static Optional<MongoClient> getMongoClient() {
		try {
			MongoClient mongoClient = MongoClients.create(uri);
			return Optional.ofNullable(mongoClient);
		} catch (Exception ignored) {
			return Optional.empty()
		}
	}
}
