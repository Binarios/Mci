package com.aegean.icsd.mci.connection;

import java.sql.Connection;

import com.aegean.icsd.mci.common.beans.MciOntologyException;

public interface ITdbConnection {

  Connection connect(String directory) throws MciOntologyException;
}
