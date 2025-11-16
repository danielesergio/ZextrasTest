# Struttura del progetto
Il progetto è suddiviso in 4 package:
- domain: contiene le classi della logica di business
- datasource: contiene le implementazioni per ottenere i post da:
  - File
  - Un servizio remoto
  - Una view di due datasource (uno considerato immutabile, e l'altro che contiene tutte le modifiche che devono essere applicate sopra il primo)
- client: contiene l'implementazione del client REST per ottenere i post da jsonplaceholder.typicode.com
- android: 
  - state: contiene lo stato dei fragments
  - ui: contiene tutte le classi che estendono classi Android relative alla ui (Activity, Fragment, adapter etc)
  - viewmodel: contiene i ViewModel