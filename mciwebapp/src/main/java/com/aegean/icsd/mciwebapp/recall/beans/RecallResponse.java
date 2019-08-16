package com.aegean.icsd.mciwebapp.recall.beans;

import java.util.List;

import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public class RecallResponse extends ServiceResponse<Recall> {

  private Long recallNumber;
  private List<Long> numbers;

  public RecallResponse(Recall game) {
    super(game);
  }


  public Long getRecallNumber() {
    return recallNumber;
  }

  public void setRecallNumber(Long recallNumber) {
    this.recallNumber = recallNumber;
  }

  public List<Long> getNumbers() {
    return numbers;
  }

  public void setNumbers(List<Long> numbers) {
    this.numbers = numbers;
  }
}
