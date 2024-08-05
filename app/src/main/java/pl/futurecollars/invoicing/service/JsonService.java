package pl.futurecollars.invoicing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import lombok.Data;
import pl.futurecollars.invoicing.model.Invoice;

@Data
public class JsonService {
  private Path file;

  public JsonService() {

  }

  public String convertToJson(Object objectToConvert) {

    ObjectMapper objectMapper = new ObjectMapper();
    String convertedData = "";
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      convertedData = objectMapper
          .writerWithDefaultPrettyPrinter()
          .writeValueAsString(objectToConvert);
    } catch (IOException exception) {
      System.out.println(exception.getClass());
      System.out.println(exception.getMessage());
    }
    return convertedData;
  }

  public List<Invoice> convertToInvoices(String jsonString) {

    List<Invoice> objectWithJason = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectWithJason = Arrays.asList(objectMapper.readValue(jsonString, Invoice[].class));

    } catch (IOException exception) {
      System.out.println(exception.getMessage());
    }

    return objectWithJason;
  }

}
