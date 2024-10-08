package pl.futurecollars.invoicing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.model.Company;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.model.Tax;

@Slf4j
@Service
@Data
public class JsonService {

  public JsonService() {
  }

  public String convertToJson(Object objectToConvert) {

    String separatorFromInvoiceObject = ", ";
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
      log.info(exception.toString());
    }
    log.info("Invoice Service convertToJson");
    return convertedData + separatorFromInvoiceObject;
  }

  public List<Invoice> convertToInvoices(String jsonString) {

    ObjectMapper objectMapper = new ObjectMapper();
    CollectionType javaType = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, Invoice.class);
    List<Invoice> objectWithJason = null;
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectWithJason = objectMapper.readValue(jsonString, javaType);
    } catch (IOException exception) {
      log.info(exception.toString());
    }
    log.info("Invoice Service convertToInvoices");
    return objectWithJason;
  }

  public List<Company> convertToCompany(String company) {

    ObjectMapper objectMapper = new ObjectMapper();
    CollectionType javaType = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, Company.class);
    List<Company> objectWithJason = null;
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectWithJason = objectMapper.readValue(company, javaType);
    } catch (IOException exception) {
      log.info(exception.toString());
    }
    log.info("Invoice Service convertToCompany");
    return objectWithJason;
  }

  public List<Tax> convertToTax(String tax) {

    ObjectMapper objectMapper = new ObjectMapper();
    CollectionType javaType = objectMapper.getTypeFactory()
        .constructCollectionType(List.class, Tax.class);
    List<Tax> objectWithJason = null;
    try {
      objectMapper.registerModule(new JavaTimeModule());
      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      objectWithJason = objectMapper.readValue(tax, javaType);
    } catch (IOException exception) {
      log.info(exception.toString());
    }
    log.info("Invoice Service convertToCompany");
    return objectWithJason;
  }

  // Generic to prepare. integration Test simplify
  //  public  <T> T convertToObject (String jsonString, T type){
  //    ObjectMapper objectMapper = new ObjectMapper();
  //    TypeReference<T> ref = new TypeReference<>() { };
  //    JavaType javaType = objectMapper.getTypeFactory().constructType(ref.getType());
  //
  //    T objectWithJason = null;
  //    try {
  //      objectMapper.registerModule(new JavaTimeModule());
  //      objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  //      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
  //      objectWithJason = objectMapper.readValue(jsonString,ref);
  //    } catch (IOException exception) {
  //      System.out.println(exception.getMessage());
  //    }
  //    return objectWithJason;
  //  }
}
