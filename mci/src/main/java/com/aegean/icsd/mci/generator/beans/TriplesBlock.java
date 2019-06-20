package com.aegean.icsd.mci.generator.beans;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TriplesBlock {

  private String subject;
  private Map<String, String> relations;

  public TriplesBlock (String subject, String property, String object) {
    this.subject = subject;
    this.relations = new HashMap<>();
    this.relations.put(property, object);
  }

  public TriplesBlock addRelation(String property, String object) {
    if (this.relations == null) {
      this.relations = new HashMap<>();
    }
    this.relations.put(property, object);
    return this;
  }

  public String asString() {
    StringBuilder sb = new StringBuilder();
    sb.append(this.subject).append(" ");
    Iterator<Map.Entry<String, String>> it = relations.entrySet().iterator();
    while (it.hasNext()) {
      Map.Entry<String, String> entry = it.next();
      sb.append(entry.getKey()).append(" ");
      sb.append(entry.getValue()).append(" ");
      if (it.hasNext()) {
        sb.append(";");
      } else {
        sb.append(".");
      }
    }
    return sb.toString();
  }

}
