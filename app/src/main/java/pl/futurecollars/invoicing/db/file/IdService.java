package pl.futurecollars.invoicing.db.file;

import lombok.Data;

@Data
public class IdService {

  private int nextId;
  private int startId;

  public IdService(int startId) {

    this.startId = startId;
    this.nextId = startId;
  }
}
