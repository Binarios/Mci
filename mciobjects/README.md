# Mci Objects
Το project mciobjects περιέχει την λογική για την δημιουργία νέου στιγμιότυπου ενός αντικειμένου, 
για την ανάγνωση του από το project mciwebapp.
Επίσης υπάρχει μία main method η οποία χρησιμοποιείται για την αρχικοποίηση των αντικειμένων στη βάση γνώσης.
Τα αντικείμενα περιγράφονται σε ένα csv αρχείο και με τη βοήθεια κάποιων properties αρχείων γίνεται η σωστή αρχικοποίηση.
 
---
## Δομή project
Το project έχει ως κυρίως package το `com.aegean.icsd.mciobjects`.
Συγκεκριμένα στο package αυτό υπάρχουν τα εξής τρία αρχεία:
1. `MciObjectsConfiguration`: Η κλάση αυτή περιέχει το Spring configuration για το project αυτό.
Η κλάση αυτή cach-αρει τους κανόνες δημιουργίας κάθε αντικειμένου από την Οντολογία κατά την εκκίνηση της εφαρμογής.
Επίσης η κλασει αυτή διαβάζει τα properties αρχεία που περιγράφουν τη δομή των csv αρχείων των αντικειμένων.
2. `ObjectsInitiator` : Η κλάση αυτή αρχικοποιεί τα αντικείμενα και δημιουργεί τις συσχετίσεις μεταξύ τους στη βάση γνώση.
Αυτό γίνεται με την ανάγωνση των csv αρχείων, σύμφωνα με τον [οδηγό εγκατάστασης της οντολογίας](../docs/ontologyInstall.md),
και με την ανάγνωση των properties αρχείων.
3. `Main`: Η κλάση αυτή κάνει initialize ένα Spring context και εκτελεί την αρχικοποίηση των αντικειμένων.
**Η *Main* πρέπει να εκτελείται μετά την εκτέλεση του Apache Fuseki και πριν την εκτέλεση του Tomcat**

Στη συνέχεια κάθε αντικείμενο έχει το δικό του package, του οποίου η δομή είναι παρόμοια με αυτή των παιγνίων, όπως έχει [περιγραφεί](../mciwebapp/README.md).

---

## Περιγραφή Configuration
Στην τοποθεσία [`./src/main/resources/com/aegean/icsd/mciobjects/providers`](./src/main/resources/com/aegean/icsd/mciobjects/providers) υπάρχουν τα properties αρχεία που περιγράφουν την δομή των csv αρχείων

### Images
Για τις εικόνες έχουμε τα εξής:
* image.loc: Η online τοποθεσία των αρχείων, π.χ. `http://localhost:3030/mci/objects`.
* image.filename: Το όνομα του αρχείου, π.χ. `images.csv`.
* image.delimiter: Ο delimeter που χρησιμοποιείται στο αρχείο, π.χ. `;`.
* image.index.url: Ο index της κολλώνας που περιέχει το URL της εικόνας, π.χ. `0`.
* image.index.title: Ο index της κολλώνας που περιέχει τον τίτλο της εικόνας, π.χ. `1` . Ο τίτλος είναι το θέμα που εμφανίζεται στην εικόνα
* image.index.subject: Ο index της κολλώνας που περιέχει το υποκείμενο της εικόνας, π.χ. `2` .
* image.index.parentImage: Ο index της κολλώνας που περιέχει μία ιστορικά προηγούμενη εικόνα της εικόνας, π.χ. `3`.

### Questions
Για τις ερωτήσεις έχουμε:

* question.loc: Η online τοποθεσία των αρχείων, π.χ. `http://localhost:3030/mci/objects`.
* question.filename: Το όνομα του αρχείου.
* question.delimiter: Ο delimeter που χρησιμοποιείται στο αρχείο, π.χ. `;`.
* question.choicesDelimiter: Ο delimeter που χρησιμοποιείται για την διαχώριση των επιλογών, π.χ. `,`.
* question.questionIndex: Ο index της κολλώνας που περιέχει την ερώτηση, π.χ. `0`.
* question.correctAnswerIndex: Ο index της κολλώνας που περιέχει την απάντηση, π.χ. `1`.
* question.choicesIndex: Ο index της κολλώνας που περιέχει τις επιλογές (με βάση τον question.delimiter ), π.χ. `2`.
* question.categoryIndex: Ο index της κολλώνας που περιέχει την κατηγορία, π.χ. `3`.
* question.difficultyIndex: Ο index της κολλώνας που περιέχει την δυσκολία της ερώτησης, π.χ. `4`.

### Sounds
Για τους ήχους έχουμε:
* sound.loc: Η online τοποθεσία των αρχείων, π.χ. `http://localhost:3030/mci/objects`.
* sound.filename: Το όνομα του αρχείου, π.χ. `sounds.csv`.
* sound.delimiter: Ο delimeter που χρησιμοποιείται στο αρχείο, π.χ. `;`.
* sound.index.url: Ο index της κολλώνας που περιέχει το URL του ήχου, π.χ. `0`.
* sound.index.subject: Ο index της κολλώνας που περιέχει το υποκείμενο του ήχου, π.χ. `1` .

### Words
Για τις λέξεις έχουμε:
* word.loc: Η online τοποθεσία των αρχείων, π.χ. `http://localhost:3030/mci/objects`
* word.filename: Το όνομα του αρχείου, π.χ. `words.csv`.
* word.delimiter: Ο delimeter που χρησιμοποιείται στο αρχείο, π.χ. `;`.
* word.valueIndex: Ο index της κολλώνας που περιέχει την λέξη, π.χ. `0`.
* word.antonymIndex: Ο index της κολλώνας που περιέχει τις αντώνυμες λέξεις, π.χ. `1`.
* word.synonymIndex: Ο index της κολλώνας που περιέχει τις συνώνυμες λέξεις,  π.χ. `2`.
* word.antonym.delimiter: Ο delimeter που χρησιμοποιείται για την διαχώριση των αντώνυμων λέξεων, π.χ. `,`.
* word.synonym.delimiter: Ο delimeter που χρησιμοποιείται για την διαχώριση των συνώνυμων λέξεων, π.χ. `,`.