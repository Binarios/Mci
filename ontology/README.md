# Ontology
Το project αυτό περιέχει τις κλάσεις που εκτελούν την ανάγνωση της οντολογίας καθώς και την εκτέλεσει των SPARQL ερωτήσεων στην βάση γνώσης

---

## Δομή project
Το project έχει ως κυρίως package το `com.aegean.icsd.ontology`.

Το αρχείο `OntologyConfiguration` περιέχει το Spring configuration για το project αυτό καθώς και το configuration της οντολογίας όπως θα περιγραφεί παρακάτω.

Η δομή του project είναι παρόμοια με αυτή που έχει περιγραφεί για το project [mciwebapp](../mciwebapp/README.md).

### Διεπαφές
Στο project υπάρχουν δύο κεντρικές διεπαφές:
1. `IMciModelReader`: Η διεπαφή αυτή χρησιμοποιείται για την ανάγνωση οντοτήτων και των συσχετίσεων της.
2. `IOntologyConnector`: Η διεπαφή αυτή χρησιμοποιείται για την εκτέλεση SPARQL ερωτημάτων στη βάση γνώσης
---

## Configuration
Στην τοποθεσία [src/main/resources/com/aegean/icsd/ontology/ontology.properties](src/main/resources/com/aegean/icsd/ontology/ontology.properties) βρίσκονται τα properties που χρησιμοποιούνται για το configuration του project κατά το Spring initialization.

* ontologyLoc: Το URL της οντολογίας, π.χ `http://localhost:3030/mci/ontology/games.owl` .
* datasetLoc: Το URL της βάσης γνώσης, π.χ `http://localhost:3030/mci` .
* ontologyType: Το συντακτικό της οντολογίας, π.χ. `TURTLE` .
* ontologyName: Το όνομα της οντολογίας, π.χ. `games`
* namespace: Το namespace της οντολογίας, π.χ. `http://www.semanticweb.org/iigou/diplomatiki/ontologies/Games#`.
* prefix: Το prefix που χρησιμοποιείται στην οντολογία, π.χ. `mci`