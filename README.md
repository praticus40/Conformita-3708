# Conformità 37/08 (MVP)

Applicazione desktop per la redazione guidata del certificato di conformità impianti elettrici (DM 37/08).

- **Java** 22 · **JavaFX** 23 · **AtlantaFX** 2.1
- **Persistenza:** H2 su file in `./data` (relativo alla directory di avvio; con `gradlew :app:run` di solito `app/data/`)
- **Export:** pacchetto PDF (`PdfPacchettoExporter`) con overlay su template pag. 1–2 e sezioni generate

## Avvio

```bat
cd conformita-3708
gradlew.bat :app:run
```

Oppure `scripts\run-dev.bat`.

## Uso rapido

1. **Impresa** — anagrafica impresa (sidebar) e salva.
2. **Pratiche** — toolbar **Nuova** crea una pratica e apre il wizard a 9 step; **Dettaglio** o doppio clic riapre una pratica esistente.
3. Compila gli step del wizard (autosalvataggio ~2 s sui form).
4. **← Torna a elenco pratiche** chiude il wizard e torna alla tabella.
5. **Valida** (barra strumenti o step 9) — errori nel pannello destro; clic sulla riga salta allo step indicato.
6. **Esporta PDF** / **Anteprima PDF** — solo se la validazione è ok (anteprima scrive in `%TEMP%` e apre il PDF di sistema).

Toolbar **Pratiche:** Nuova, Elimina (con conferma), Esporta, Dettaglio, Aggiorna.

Menu **File → Nuova pratica** equivale a **Nuova** nella schermata Pratiche.

## Wizard (9 step)

1. Anagrafica impresa (riepilogo da dati globali)
2. Committente e ubicazione
3. Intervento e impianto
4. Dichiarazione Di.Co
5. Relazione tecnica
6. Materiali (tabella)
7. Verifiche e prove
8. Libretto e schemi
9. Riepilogo ed export

Template PDF di riferimento: `core/src/main/resources/templates/dico-pacchetto-template.pdf` (coordinate overlay in `dico-field-map.yml`).

## Database e schema

- File H2: `data/conformita.mv.db` (+ eventuale `.trace.db`).
- File `data/schema-version`: versione logica dello schema. Se l’app contiene una versione **più recente** (costante in codice), al primo avvio **cancella i file H2** e ricrea il database (nessuna migrazione dati). Compare un messaggio informativo in UI.
- Reset manuale: chiudere l’app, eliminare `data/conformita*.db` (e opzionalmente `schema-version`), riavviare. Oppure `scripts\reset-db.bat` (da eseguire con l’app chiusa).

## Backup

Copia l’intera cartella `data/` (database H2 + file `schema-version`). Non aprire due istanze dell’app sullo stesso database.

## Build

```bat
gradlew.bat build
gradlew.bat :app:installDist
```

## Moduli

- `core` — JPA/H2, validazione certificato, PDF pacchetto
- `app` — interfaccia JavaFX
