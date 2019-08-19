package com.aegean.icsd.mciwebapp.common;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.aegean.icsd.engine.common.beans.Difficulty;
import com.aegean.icsd.engine.generator.beans.BaseGame;
import com.aegean.icsd.mciwebapp.common.beans.ServiceResponse;

public final class FilterResponse {

  private FilterResponse() { }

  public static  <T extends ServiceResponse<? extends BaseGame>> List<T> by(List<T> responses,
                                                                            String difficulty, Boolean completed ) {

    return responses.stream().filter(x -> {
      boolean choose = true;
      if (!StringUtils.isEmpty(difficulty)) {
        Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
        choose = x.getGame().getDifficulty().equals(diff);
      }

      if (completed != null) {
        if (completed) {
          choose &= x.getGame().getCompletedDate() != null;
        } else {
          choose &= x.getGame().getCompletedDate() == null;
        }
      }
      return choose;
    }).collect(Collectors.toList());
  }

}
