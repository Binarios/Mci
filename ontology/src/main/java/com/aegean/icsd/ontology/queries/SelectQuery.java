package com.aegean.icsd.ontology.queries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.aegean.icsd.ontology.queries.beans.Triplet;

public class SelectQuery {
  private String command;
  private Map<String, String> prefixes = new LinkedHashMap<>();
  private Map<String, List<Triplet>> conditions = new LinkedHashMap<>();
  private Map<String, String> iriParams = new LinkedHashMap<>();
  private Map<String, String> literalParams = new LinkedHashMap<>();
  private List<String> selectParams = new LinkedList<>();

  private SelectQuery() { }

  public String getCommand() {
    return command;
  }

  public Map<String, String> getPrefixes() {
    return prefixes;
  }

  public Map<String, List<Triplet>> getConditions() {
    return conditions;
  }

  public Map<String, String> getIriParams() {
    return iriParams;
  }

  public Map<String, String> getLiteralParams() {
    return literalParams;
  }

  public List<String> getSelectParams() {
    return selectParams;
  }


  public static class Builder {
    private List<String> params = new LinkedList<>();
    private Map<String, List<Triplet>> conditions = new LinkedHashMap<>();
    private Map<String, String> prefixes = new LinkedHashMap<>();
    private List<String> filters = new LinkedList<>();
    private Map<String, String> iriParams = new LinkedHashMap<>();
    private Map<String, String> literalParams = new LinkedHashMap<>();
    private boolean isAscOrdered = false;
    private String orderFiled = null;
    private int limit = -1;
    private boolean distinct = false;

    public enum Operator {
      GT, LT, EQ
    }

    public Builder addPrefix (String prefix, String Uri) {
      prefixes.put(prefix, Uri);
      return this;
    }

    public Builder addIriParam(String param, String value) {
      iriParams.put(param, value);
      return this;
    }

    public Builder addLiteralParam(String param, String value) {
      literalParams.put(param, value);
      return this;
    }

    public Builder select(String... paramNames) {
      params.addAll(Arrays.asList(paramNames));
      return this;
    }

    public Builder setDistinct(boolean distinct) {
      this.distinct = distinct;
      return this;
    }

    public Builder where(String subject, String predicate, String object) {
      if (conditions.containsKey(subject)) {
        conditions.get(subject).add(new Triplet(subject, predicate, object));
      } else {
        Triplet triplet = new Triplet(subject, predicate, object);
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

    public Builder orderByDesc(String field) {
      return this.orderBy(field, false);
    }

    public Builder orderByAsc(String field) {
      return this.orderBy(field, true);
    }

    public Builder limit(int numberOfRecords) {
      limit = numberOfRecords;
      return this;
    }

    public SelectQuery build() {
      SelectQuery query = new SelectQuery();
      StringBuilder builder = new StringBuilder();
      buildSelectParams(builder);
      buildWhereClauses(builder);
      buildOrderClause(builder);
      buildLimitClause(builder);

      query.command = builder.toString();
      query.prefixes = prefixes;
      query.conditions = conditions;
      query.iriParams = iriParams;
      query.literalParams = literalParams;
      query.selectParams = params;

      return query;
    }

    void buildSelectParams(StringBuilder builder) {
      builder.append("SELECT").append(" ");
      if (distinct) {
        builder.append("DISTINCT").append(" ");
      }
      for (String param : params) {
        if (StringUtils.isEmpty(param)) {
          continue;
        }
        builder.append(removeParamChars(param)).append(" ");
      }
      builder.append("\n");
    }

    void buildWhereClauses(StringBuilder builder) {
      builder.append("WHERE").append(" ").append("{").append("\n\t");

      for (Map.Entry<String, List<Triplet>> entry : conditions.entrySet()) {
        String whereClause = buildWhereClause(entry);
        builder.append(whereClause).append("\n\t");
      }

      for(String filter : filters) {
        builder.append(filter).append("\n");
      }

      builder.append("}").append("\n");
    }

    void buildOrderClause(StringBuilder builder) {
      if (!StringUtils.isEmpty(orderFiled)) {
        builder.append("ORDER BY ");
        if (isAscOrdered) {
          builder.append("ASC");
        } else {
          builder.append("DESC");
        }
        builder.append("(").append(removeParamChars(orderFiled)).append(")\n");
      }
    }

    void buildLimitClause(StringBuilder builder) {
      if (limit > 0) {
        builder.append("LIMIT ").append(limit).append("\n");
      }
    }

    String buildWhereClause(Map.Entry<String, List<Triplet>> entry) {
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

    Builder orderBy(String field, boolean ascended) {
      this.orderFiled = field;
      this.isAscOrdered = ascended;
      return this;
    }
  }
}
