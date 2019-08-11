package com.aegean.icsd.ontology.beans;

import com.google.gson.annotations.SerializedName;

public class FusekiResponse {
  private FusekiHead head;
  private FusekiResults results;
  @SerializedName("boolean")
  private Boolean askResponse;

  public FusekiHead getHead() {
    return head;
  }

  public void setHead(FusekiHead head) {
    this.head = head;
  }

  public FusekiResults getResults() {
    return results;
  }

  public void setResults(FusekiResults results) {
    this.results = results;
  }

  public Boolean getAskResponse() {
    return askResponse;
  }

  public void setAskResponse(Boolean askResponse) {
    this.askResponse = askResponse;
  }
}
