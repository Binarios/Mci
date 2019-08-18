package com.aegean.icsd.ontology.queries;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.aegean.icsd.ontology.queries.beans.Triplet;

public class AskQuery {
  private String command;
  private Map<String, List<Triplet>> conditions = new LinkedHashMap<>();
  private Map<String, String> iriParams = new LinkedHashMap<>();
  private Map<String, String> strLiteralParams = new LinkedHashMap<>();
  private Map<String, Integer> intLiteralParams = new LinkedHashMap<>();
  private Map<String, Long> longLiteralParams = new LinkedHashMap<>();
  private Map<String, Boolean> boolLiteralParams = new LinkedHashMap<>();

  private AskQuery() { }

  public String getCommand() {
    return command;
  }

  public Map<String, List<Triplet>> getConditions() {
    return conditions;
  }

  public Map<String, String> getIriParams() {
    return iriParams;
  }

  public Map<String, String> getStrLiteralParams() {
    return strLiteralParams;
  }

  public Map<String, Integer> getIntLiteralParams() {
    return intLiteralParams;
  }

  public Map<String, Long> getLongLiteralParams() {
    return longLiteralParams;
  }

  public Map<String, Boolean> getBoolLiteralParams() {
    return boolLiteralParams;
  }

  public static class Builder {
    private Map<String, List<Triplet>> conditions = new LinkedHashMap<>();
    private Map<String, String> iriParams = new LinkedHashMap<>();
    private Map<String, String> strLiteralParams = new LinkedHashMap<>();
    private Map<String, Integer> intLiteralParams = new LinkedHashMap<>();
    private Map<String, Long> longLiteralParams = new LinkedHashMap<>();
    private Map<String, Boolean> boolLiteralParams = new LinkedHashMap<>();

    public Builder addIriParam(String param, String value) {
      iriParams.put(param, value);
      return this;
    }

    public Builder addLiteralParam(String param, String value) {
      strLiteralParams.put(param, value);
      return this;
    }

    public Builder addLiteralParam(String param, Integer value) {
      intLiteralParams.put(param, value);
      return this;
    }

    public Builder addLiteralParam(String param, Long value) {
      longLiteralParams.put(param, value);
      return this;
    }

    public Builder addLiteralParam(String param, Boolean value) {
      boolLiteralParams.put(param, value);
      return this;
    }


    public Builder is(String subject, String predicate, String object) {
      if (conditions.containsKey(subject)) {
        conditions.get(subject).add(new Triplet(subject, predicate, object));
      } else {
        Triplet triplet = new Triplet(subject, predicate, object);
        List<Triplet> entries = new LinkedList<>();
        entries.add(triplet);
        conditions.put(subject, entries);
      }
      return this;
    }

    public AskQuery build() {
      AskQuery query = new AskQuery();
      StringBuilder builder = new StringBuilder();
      builder.append("ASK {\n");

      for (Map.Entry<String, List<Triplet>> entry : conditions.entrySet()) {
        String isClause = buildIsClause(entry);
        builder.append("\t").append(isClause).append("\n");
      }
      builder.append("}\n");

      query.command = builder.toString();
      query.conditions = conditions;
      query.iriParams = iriParams;
      query.strLiteralParams = strLiteralParams;
      query.intLiteralParams = intLiteralParams;
      query.longLiteralParams = longLiteralParams;
      query.boolLiteralParams = boolLiteralParams;
      return query;
    }

    String buildIsClause(Map.Entry<String, List<Triplet>> entry) {
      StringBuilder builder = new StringBuilder();
      builder.append(removeParamChars(entry.getKey())).append(" ");
      Iterator<Triplet> it = entry.getValue().iterator();
      while (it.hasNext()) {
        Triplet triplet = it.next();
        builder.append(removeParamChars(triplet.getPredicate())).append(" ")
          .append(removeParamChars(triplet.getObject()));
        if(it.hasNext()) {
          builder.append(";").append("\n\t\t");
        }
      }
      builder.append(" ").append(".");
      return builder.toString();
    }

    String removeParamChars(String entry) {
      return "?" + entry.replace("?", "").replace("$", "");
    }

  }
}
