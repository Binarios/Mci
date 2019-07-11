package com.aegean.icsd.ontology.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class SelectQuery {
  private String command;
  private Map<String, String> prefixes = new HashMap<>();
  private Map<String, List<Triplet>> conditions = new HashMap<>();

  private SelectQuery() {
  }

  public String getCommand() {
    return command;
  }

  public Map<String, String> getPrefixes() {
    return prefixes;
  }

  public Map<String, List<Triplet>> getConditions() {
    return conditions;
  }


  public static class Builder {
    private List<String> params = new ArrayList<>();
    private Map<String, List<Triplet>> conditions = new HashMap<>();
    private Map<String, String> prefixes = new HashMap<>();
    private List<String> filters = new ArrayList<>();

    public enum Operator {
      GT, LT, EQ
    }

    public Builder addPrefix (String prefix, String Uri) {
      prefixes.put(prefix, Uri);
      return this;
    }

    public Builder select(String... paramNames) {
      params.addAll(Arrays.asList(paramNames));
      return this;
    }

    public Builder where(Triplet triplet) {
      String subject = triplet.getSubject();
      String predicate = triplet.getPredicate();
      String object = triplet.getObject();
      boolean literalObject = triplet.isLiteralObject();

      this.where(subject, predicate, object, literalObject);
      return this;
    }

    public Builder where(String subject, String predicate, String object, boolean isLiteralObject) {
      if (conditions.containsKey(subject)) {
        conditions.get(subject).add(new Triplet(subject, predicate, object, isLiteralObject));
      } else {
        Triplet triplet = new Triplet(subject, predicate, object, isLiteralObject);
        List<Triplet> entries = new ArrayList<>();
        entries.add(triplet);
        conditions.put(subject, entries);
      }
      return this;
    }

    public Builder regexFilter(String value, String pattern) {
      this.regexFilter(value, pattern, null);
      return this;
    }

    public Builder regexFilter(String value, String pattern, String flags) {
      String filter = "FILTER regex(" + value + ", " + pattern ;
      if (!StringUtils.isEmpty(flags)) {
        filter += ", " + flags;
      }
      filter += ")";
      filters.add(filter);
      return this;
    }

    public Builder filter(String var, Operator operator, String value) {

      String filter = "FILTER (" + var;
      switch (operator) {
        case EQ:
          filter += " = " + value + ")";
          break;
        case GT:
          filter += " > " + value + ")";
          break;
        case LT:
          filter += " < " + value + ")";
          break;
        default:
          break;
      }

      filters.add(filter);
      return this;
    }

    public SelectQuery build() {
      SelectQuery query = new SelectQuery();
      StringBuilder builder = new StringBuilder();
      buildSelectParams(builder);
      buildWhereClauses(builder);

      query.command = builder.toString();
      query.prefixes = prefixes;
      query.conditions = conditions;

      return query;
    }

    void buildSelectParams(StringBuilder builder) {
      builder.append("SELECT").append(" ");
      for (String param : params) {
        if (StringUtils.isEmpty(param)) {
          continue;
        }
        String uniParam= param.replace("$", "?");
        if (uniParam.indexOf("?") == 0) {
          builder.append(uniParam).append(" ");
        } else if (uniParam.indexOf("?") > 0) {
          uniParam = uniParam.replace("?", "");
          builder.append("?").append(uniParam).append(" ");
        } else {
          builder.append("?").append(uniParam).append(" ");
        }
      }
      builder.append("\n");
    }

    void buildWhereClauses(StringBuilder builder) {
      builder.append("WHERE").append(" ").append("{").append("\n");

      for (Map.Entry<String, List<Triplet>> entry : conditions.entrySet()) {
        String whereClause = buildWhereClause(entry);
        builder.append(whereClause).append("\n");
      }

      for(String filter : filters) {
        builder.append(filter).append("\n");
      }

      builder.append("}").append("\n");
    }

    String buildWhereClause(Map.Entry<String, List<Triplet>> entry) {
      StringBuilder builder = new StringBuilder();
      builder.append(entry.getKey()).append(" ");
      Iterator<Triplet> it = entry.getValue().iterator();
      while (it.hasNext()) {
        Triplet triplet = it.next();
        builder.append(triplet.getPredicate()).append(" ").append(triplet.getObject());
        if(it.hasNext()) {
          builder.append(";").append("\n").append("\t");
        }
      }
      builder.append(".").append("\n");
      return builder.toString();
    }
  }
}
