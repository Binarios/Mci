package com.aegean.icsd.ontology.beans;

import com.google.gson.JsonArray;

public class FusekiResults {
  private JsonArray bindings;

  public JsonArray getBindings() {
    return bindings;
  }

  public void setBindings(JsonArray bindings) {
    this.bindings = bindings;
  }
}
