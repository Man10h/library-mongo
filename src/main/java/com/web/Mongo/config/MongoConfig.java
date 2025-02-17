package com.web.Mongo.config;

import com.mongodb.AutoEncryptionSettings;
import com.mongodb.ClientEncryptionSettings;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.vault.ClientEncryption;
import com.mongodb.client.vault.ClientEncryptions;
import com.web.Mongo.convert.VerifyReadingConverter;
import org.bson.BsonBinary;
import org.bson.BsonValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.PropertyValueConverterFactory;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.convert.encryption.MongoEncryptionConverter;
import org.springframework.data.mongodb.core.encryption.Encryption;
import org.springframework.data.mongodb.core.encryption.EncryptionKey;
import org.springframework.data.mongodb.core.encryption.EncryptionKeyResolver;
import org.springframework.data.mongodb.core.encryption.MongoClientEncryption;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableMongoAuditing
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${kmsKey}")
    private String kmsKey;

    @Autowired
    private ApplicationContext applicationContext;


    @Bean
    public Map<String, Map<String, Object>> getKmsProviders() {
        Map<String, Object> localKms = new HashMap();
        localKms.put("key", Base64.getDecoder().decode(this.kmsKey));
        Map<String, Map<String, Object>> kmsProviders = new HashMap();
        kmsProviders.put("local", localKms);
        return kmsProviders;
    }

    @Bean
    public ClientEncryption clientEncryption() {
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .build();
        ClientEncryptionSettings clientEncryptionSettings = ClientEncryptionSettings.builder()
                .keyVaultMongoClientSettings(mongoClientSettings)
                .kmsProviders(getKmsProviders())
                .keyVaultNamespace("thuvien.kms")
                .build();
        return ClientEncryptions.create(clientEncryptionSettings);
    }


    @Bean
    public MongoClient mongoClient() {
        AutoEncryptionSettings autoEncryptionSettings = AutoEncryptionSettings.builder()
                .keyVaultNamespace("thuvien.kms")
                .kmsProviders(getKmsProviders())
                .bypassAutoEncryption(true)
                .build();

        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .autoEncryptionSettings(autoEncryptionSettings)
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    MongoEncryptionConverter encryptingConverter(ClientEncryption clientEncryption) {

        Encryption<BsonValue, BsonBinary> encryption = MongoClientEncryption.just(clientEncryption);
        EncryptionKeyResolver keyResolver = EncryptionKeyResolver.annotated((ctx) -> {
            return EncryptionKey.keyAltName(ctx.getProperty().getFieldName());
        });

        return new MongoEncryptionConverter(encryption, keyResolver);
    }

    @Override
    protected void configureConverters(MongoCustomConversions.MongoConverterConfigurationAdapter adapter) {

        adapter
                .registerPropertyValueConverterFactory(PropertyValueConverterFactory.beanFactoryAware(applicationContext))
                .registerConverter(new VerifyReadingConverter())
                ;
    }

    @Override
    protected String getDatabaseName() {
        return "thuvien";
    }


}
