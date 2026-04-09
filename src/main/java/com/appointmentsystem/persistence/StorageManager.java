package com.appointmentsystem.persistence;

import com.appointmentsystem.model.Admin;
import com.appointmentsystem.model.Customer;
import com.appointmentsystem.model.Supplier;
import com.appointmentsystem.model.User;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for saving and loading application data to/from JSON files.
 * 
 * @author Team
 * @version 1.0
 */
public class StorageManager {

    /** The directory path where JSON data files are stored. */
    private static final String DATA_DIR = "app_data";

    /** The formatter used for parsing and formatting LocalDateTime objects. */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /** The Gson instance configured with custom type adapters. */
    private final Gson gson;

    /**
     * Constructs a new StorageManager.
     * Initializes Gson with custom adapters and ensures the data directory exists.
     */
    public StorageManager() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserTypeAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .setPrettyPrinting()
                .create();
                
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Saves a list of data to a specified JSON file.
     *
     * @param <T>      the type of data elements
     * @param fileName the name of the file to save data in
     * @param data     the list of data to save
     */
    public <T> void saveData(String fileName, List<T> data) {
        try {
            String json = gson.toJson(data);
            try (Writer writer = new FileWriter(DATA_DIR + File.separator + fileName)) {
                writer.write(json);
            }
        } catch (Exception e) {
            System.err.println("Error saving data to " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Loads a list of data from a specified JSON file.
     *
     * @param <T>       the type of data elements
     * @param fileName  the name of the file to load data from
     * @param typeToken the type token representing the list type
     * @return the list of loaded data, or an empty list if the file does not exist or fails to load
     */
    public <T> List<T> loadData(String fileName, TypeToken<List<T>> typeToken) {
        File file = new File(DATA_DIR + File.separator + fileName);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Type type = typeToken.getType();
            List<T> data = gson.fromJson(reader, type);
            
            if (data == null) {
                file.delete();
                return new ArrayList<>();
            }
            
            data.removeIf(item -> item == null);
            return data;
            
        } catch (Exception e) { 
            System.err.println("Error loading old data from " + fileName + ". Resetting file.");
            file.delete(); 
            return new ArrayList<>();
        }
    }

    /**
     * Adapter to handle Abstract User class serialization and deserialization.
     * 
     * @author Team
     * @version 1.0
     */
    private static class UserTypeAdapter implements JsonDeserializer<User>, JsonSerializer<User> {

        /**
         * Serializes a User object to JSON, including its specific role.
         *
         * @param user    the user object to serialize
         * @param type    the type of the object to serialize
         * @param context the serialization context
         * @return the JSON representation of the user
         */
        @Override
        public JsonElement serialize(User user, Type type, JsonSerializationContext context) {
            JsonObject jsonObject = context.serialize(user, user.getClass()).getAsJsonObject();
            jsonObject.addProperty("role", user.getClass().getSimpleName());
            return jsonObject;
        }

        /**
         * Deserializes a JSON element back into a specific User subclass.
         *
         * @param json    the JSON element to deserialize
         * @param type    the type of the object to deserialize
         * @param context the deserialization context
         * @return the deserialized User object, or null if role is missing
         * @throws JsonParseException if JSON is not in the expected format
         */
        @Override
        public User deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            JsonElement roleElement = jsonObject.get("role");
            
            if (roleElement == null) return null;

            String role = roleElement.getAsString();
            
            switch (role) {
                case "Admin": return context.deserialize(jsonObject, Admin.class);
                case "Supplier": return context.deserialize(jsonObject, Supplier.class);
                case "Customer": return context.deserialize(jsonObject, Customer.class);
                default: return null;
            }
        }
    }

    /**
     * Adapter to handle Java 8 LocalDateTime serialization and deserialization.
     * 
     * @author Team
     * @version 1.0
     */
    private static class LocalDateTimeAdapter implements JsonDeserializer<LocalDateTime>, JsonSerializer<LocalDateTime> {

        /**
         * Serializes a LocalDateTime object to a JSON string.
         *
         * @param src       the local date time object to serialize
         * @param typeOfSrc the type of the source object
         * @param context   the serialization context
         * @return the JSON primitive containing the formatted date time string
         */
        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.format(DATE_FORMATTER));
        }

        /**
         * Deserializes a JSON string back into a LocalDateTime object.
         *
         * @param json    the JSON element containing the date time string
         * @param typeOfT the type of the object to deserialize
         * @param context the deserialization context
         * @return the parsed LocalDateTime object
         * @throws JsonParseException if the string is not in the expected format
         */
        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DATE_FORMATTER);
        }
    }
}