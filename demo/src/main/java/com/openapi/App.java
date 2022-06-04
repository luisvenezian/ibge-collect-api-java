package com.openapi;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import org.apache.avro.Schema;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;

import tech.allegro.schema.json2avro.converter.JsonAvroConverter;
import org.json.JSONArray;  


/**
 * Collect data from public API and writes in AVRO files 
 * 
 */
public class App 
{
    
    private static final File FILE = new File("municipios.avro.snappy");
    private static final String BASE_URL = "https://servicodados.ibge.gov.br/api/v1/localidades";

    public static void main(String[] args) throws Exception, IOException{
        String data = collectFromHttp();
        data = data.replace("regiao-intermediaria", "regiao_intermediaria");
        data = data.replace("regiao-imediata", "regiao_imediata");
        JSONArray array = new JSONArray(data); 

        /**
         *  Schema from file
         */
        File schemaFile = new File("municipios.avsc");
        Schema schema;
        schema = new Schema.Parser().parse(schemaFile);

        /**
         * Writer object
         */
        final DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
        DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(writer);
        
        /**
         * Snappy Codec
         */
        CodecFactory codecFactory = CodecFactory.snappyCodec();
        fileWriter.setCodec(codecFactory);

        /**
         * Open writer
         */
        fileWriter.create(schema, FILE);

        /**
         * Iterate each item appending to avro file 
         */
        for(Object item: array){

            /**
             * Convert item to record
             */
            JsonAvroConverter converter = new JsonAvroConverter();
            GenericData.Record record = converter.convertToGenericDataRecord(item.toString().getBytes(), schema);
            
            /**
             * Append to avro
             */
            fileWriter.append(record);
        }

        /*
         * Close writer, file now should be in a place :)
         */
        fileWriter.close();
        System.out.println("Written to avro data file");

    }

    public static String collectFromHttp() throws Exception{
        /**
         * Collect response from a HTTP endpoint  
         */
        String request_url = BASE_URL + "/" + "municipios";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(request_url)).build();
        HttpResponse<String> response;
        
        /**
         * Http request 
         */
        response = client.send(request, BodyHandlers.ofString());

        /**
         * If the response isn't 200 (Success) then return empty string 
         */
        return (response.statusCode()==200) ? response.body() : "";
    }

}
