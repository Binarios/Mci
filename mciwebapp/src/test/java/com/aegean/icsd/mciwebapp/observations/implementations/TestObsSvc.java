package com.aegean.icsd.mciwebapp.observations.implementations;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.aegean.icsd.engine.generator.interfaces.IGenerator;


@ExtendWith(MockitoExtension.class)
@Execution(ExecutionMode.CONCURRENT)
public class TestObsSvc {

  @InjectMocks
  @Spy
  private ObservationSvc svc = new ObservationSvc();

  @Mock
  private IGenerator generator;

}
