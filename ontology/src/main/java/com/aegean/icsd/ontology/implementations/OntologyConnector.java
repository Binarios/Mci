package com.aegean.icsd.ontology.implementations;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.jena.query.ParameterizedSparqlString;
import org.apache.jena.query.QueryException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aegean.icsd.ontology.beans.DatasetProperties;
import com.aegean.icsd.ontology.beans.FusekiResponse;
import com.aegean.icsd.ontology.beans.OntologyException;
import com.aegean.icsd.ontology.interfaces.IOntologyConnector;
import com.aegean.icsd.ontology.queries.AskQuery;
import com.aegean.icsd.ontology.queries.InsertQuery;
import com.aegean.icsd.ontology.queries.SelectQuery;
import com.aegean.icsd.ontology.queries.beans.InsertParam;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class OntologyConnector implements IOntologyConnector {
  private static Logger LOGGER = Logger.getLogger(OntologyConnector.class);

  @Autowired
  private DatasetProperties ontologyProps;

  @Override
  public boolean ask(AskQuery ask) throws OntologyException {
    ParameterizedSparqlString sparql = getPrefixedSparql(new HashMap<>());
    sparql.setCommandText(ask.getCommand());

    for (Map.Entry<String, String> entry : ask.getIriParams().entrySet()) {
      sparql.setIri(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, String> entry : ask.getStrLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Integer> entry : ask.getIntLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Long> entry : ask.getLongLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }

    for (Map.Entry<String, Boolean> entry : ask.getBoolLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }


    String query;
    try {
      query = sparql.asQuery().toString();
    } catch (QueryException ex) {
      throw new OntologyException("ASK.1", "Error when constructing the query", ex);
    }

    CloseableHttpClient client = HttpClients.createDefault();
    try {
      HttpPost request = buildPostRequest("query", query);
      CloseableHttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() >= 400) {
        throw new OntologyException("ASK.400", "Error when executing the query");
      }
      String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      FusekiResponse res = new Gson().fromJson(body, FusekiResponse.class);
      return res.getAskResponse();
    } catch (IOException e) {
      throw new OntologyException("ASK.999", "Error when executing the query", e);
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        LOGGER.error("Error when closing the client", e);
      }
    }
  }

  @Override
  public JsonArray select(SelectQuery selectQuery) throws OntologyException {
    JsonArray array = new JsonArray();

    ParameterizedSparqlString sparql = getPrefixedSparql(selectQuery.getPrefixes());
    sparql.setCommandText(selectQuery.getCommand());

    for(Map.Entry<String, String> entry : selectQuery.getIriParams().entrySet()) {
      sparql.setIri(entry.getKey(), entry.getValue());
    }
    for(Map.Entry<String, String> entry : selectQuery.getLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }
    for(Map.Entry<String, Long> entry : selectQuery.getLongLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }
    for(Map.Entry<String, Boolean> entry : selectQuery.getBoolLiteralParams().entrySet()) {
      sparql.setLiteral(entry.getKey(), entry.getValue());
    }

    String query;
    try {
      query = sparql.asQuery().toString();
    } catch (QueryException ex ) {
      throw new OntologyException("SEL.1", "Error when constructing the query", ex);
    }

    CloseableHttpClient client = HttpClients.createDefault();
    try {
      HttpPost request = buildPostRequest("query", query);

      CloseableHttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() >= 400) {
        throw new OntologyException("SEL.400", "Error when executing the query");
      }

      String body = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      FusekiResponse res = new Gson().fromJson(body, FusekiResponse.class);
      List<String> varNames = res.getHead().getVars();
      for (JsonElement elem : res.getResults().getBindings()) {
        JsonObject resultObj = new JsonObject();
        for (String varName : varNames) {
          JsonObject currentResult = elem.getAsJsonObject();
          if (currentResult.has(varName)) {
            String value = currentResult.get(varName).getAsJsonObject().get("value").getAsString();
            resultObj.addProperty(varName, value);
          }
        }
        if (resultObj.entrySet().size() > 0) {
          array.add(resultObj);
        }
      }
    } catch (IOException e) {
      throw new OntologyException("SEL.1", "Error when executing the query", e);
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        LOGGER.error("Error when closing the client", e);
      }
    }

    return array;
  }

  @Override
  public boolean insert(InsertQuery insertQuery) throws OntologyException {
    ParameterizedSparqlString sparql = getPrefixedSparql(insertQuery.getPrefixes());
    sparql.setCommandText(insertQuery.getCommand());

    for (InsertParam param : insertQuery.getParams()) {
      if (param.isIriParam()) {
        sparql.setIri(param.getName(), param.getValue().toString());
      } else {
        if (String.class.equals(param.getValueClass())) {
          sparql.setLiteral(param.getName(), param.getValue().toString());
        } else if (Long.class.equals(param.getValueClass())) {
          sparql.setLiteral(param.getName(), Long.parseLong(param.getValue().toString()));
        } else if (Boolean.class.equals(param.getValueClass())) {
          sparql.setLiteral(param.getName(), (Boolean) param.getValue());
        }
      }
    }

    String query;
    try {
      query = sparql.asUpdate().toString();
    } catch (QueryException ex ) {
      throw new OntologyException("INS.1", "Error when constructing the query", ex);
    }

    CloseableHttpClient client = HttpClients.createDefault();
    try {
      HttpPost request = buildPostRequest("update", query);
      CloseableHttpResponse response = client.execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        throw new OntologyException("INS.2", "Error when inserting the data");
      }
      return true;
    } catch (IOException e) {
      throw new OntologyException("INS.99", "Error when executing the query", e);
    } finally {
      try {
        client.close();
      } catch (IOException e) {
        LOGGER.error("Error when closing the client", e);
      }
    }

  }

  ParameterizedSparqlString getPrefixedSparql(Map<String, String> prefixes) {
    Map<String, String> defaultPrefixes = new HashMap<>();
    defaultPrefixes.put(ontologyProps.getPrefix(), ontologyProps.getNamespace());
    defaultPrefixes.put("owl", "http://www.w3.org/2002/07/owl#");
    defaultPrefixes.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    defaultPrefixes.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    defaultPrefixes.put("xsd", "http://www.w3.org/2001/XMLSchema#");

    defaultPrefixes.putAll(prefixes);

    ParameterizedSparqlString sparql = new ParameterizedSparqlString();
    sparql.setNsPrefixes(defaultPrefixes);
    return sparql;
  }


  HttpPost buildPostRequest (String action, String query) throws UnsupportedEncodingException {
    HttpPost httpPost = new HttpPost(ontologyProps.getDatasetLocation() + "/" + action);
    String body = action + "=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
    StringEntity entity = new StringEntity(body);
    httpPost.setEntity(entity);
    httpPost.setHeader("Accept", "application/sparql-results+json,*/*;q=0.9");
    httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
    return httpPost;
  }

}
