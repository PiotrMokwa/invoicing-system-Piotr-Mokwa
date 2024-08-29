package pl.futurecollars.invoicing.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.futurecollars.invoicing.db.file.IdService;
import pl.futurecollars.invoicing.model.Invoice;
import pl.futurecollars.invoicing.setup.InvoiceSetup;

@Slf4j
@Service
@Data
public class JsonService {

  FileService fileService;
  IdService idService;
  private Path file;

  public JsonService(InvoiceSetup invoiceSetup) {
    this.fileService = new FileService();
    this.idService = new IdService(invoiceSetup);
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
