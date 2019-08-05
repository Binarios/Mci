package com.aegean.icsd.mciwebapp.providers.objects;

import com.aegean.icsd.engine.annotations.Entity;
import com.aegean.icsd.engine.annotations.Id;
import com.aegean.icsd.engine.annotations.Key;
import com.aegean.icsd.engine.annotations.DataProperty;
import com.aegean.icsd.engine.annotations.Relations;

@Entity(Word.TYPE)
public class Word {
  static final String TYPE = "Word";

  @Id
  @DataProperty(Relations.HAS_ID)
  private String id;

  @Key
  private String value;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
