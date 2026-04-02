# Jak dziala algorytm

Fiszki wykorzystuja dwa algorytmy nauki. Mozesz przelaczac miedzy nimi w Ustawieniach.

---

## FSRS (Free Spaced Repetition Scheduler)

FSRS to nowoczesny algorytm powtarzania rozlozonego w czasie, oparty na badaniach naukowych dotyczacych ludzkiej pamieci. Planuje powtorki w optymalnych odstepach czasu, aby zmaksymalizowac dlugoterminowe zapamietywanie przy minimalnym wysilku.

### Stany kart

Kazda fiszka znajduje sie w jednym z czterech stanow:

| Stan | Znaczenie |
|---|---|
| **Nowa** | Nigdy nie powtarzana. Karta nie ma jeszcze danych o pamieci. |
| **Nauka** | Uczona po raz pierwszy. Zobaczysz ja wkrotce ponownie. |
| **Powtorka** | Przeszla do pamieci dlugoterminowej. Odstepy rosna z kazdym sukcesem. |
| **Powtorna nauka** | Wczesniej znana, ale zapomniana. Powrot do krotkich odsteepow. |

### Jak dzialaja oceny

Po kazdej odpowiedzi algorytm przypisuje ocene na podstawie Twojego wyniku:

| Ocena | Kiedy wystepuje |
|---|---|
| **Latwe** | Poprawna odpowiedz za pierwszym razem, w ciagu 2 minut, dokladne dopasowanie |
| **Dobre** | Poprawna odpowiedz za pierwszym razem, ale dluzej lub z drobnymi literowkami |
| **Trudne** | Poprawna odpowiedz, ale dopiero po kilku probach |
| **Ponow** | Pominieto lub poddano sie |

### Kluczowe pojecia

**Stabilnosc** mierzy, jak dlugo utrzymuje sie pamiec. Wyzsza stabilnosc oznacza, ze mozesz czekac dluzej przed nastepna powtorka. Po udanej powtorce stabilnosc rosnie. Po zapomnieniu spada.

**Trudnosc** (1.0 - 10.0) odzwierciedla, jak trudna jest dla Ciebie dana karta. Karty, na ktore konsekwentnie odpowiadasz poprawnie, staja sie latwiejsze. Karty, z ktorymi masz problemy, staja sie trudniejsze. Trudnosc wplywa na tempo wzrostu stabilnosci.

**Odtwarzalnosc** to prawdopodobienstwo, ze mozesz sobie teraz przypomniecc karte. Zaczyna sie od wysokiej wartosci po powtorce i maleje w czasie zgodnie z krzywa zapominania. Gdy odtwarzalnosc spadnie do okolo 90%, czas na powtorke.

**Interwal** to liczba dni do nastepnej zaplanowanej powtorki. Jest obliczany na podstawie stabilnosci i pozadanego wskaznika utrzymania w pamieci (domyslnie 90%).

### Bieglosc

Gdy FSRS jest aktywny, procent bieglosci dla kazdego zestawu to **srednia odtwarzalnosc** wszystkich kart. Mowi Ci, jaki procent zestawu moglbys teraz przypomniecc sobie. Naturalnie spada z czasem, jesli nie powtarzasz, i rosnie po sesjach nauki.

---

## Tradycyjny algorytm

Tradycyjny algorytm uzywa prostego losowania opartego na priorytetach. Kazda karta ma priorytet (0-5). Poprawna odpowiedz zwieksza priorytet, co sprawia, ze karta pojawia sie rzadziej. Bledna odpowiedz zmniejsza priorytet, przez co karta pojawia sie czesciej.

Bieglosc w trybie tradycyjnym jest obliczana jako ogolny wskaznik poprawnych odpowiedzi (poprawne odpowiedzi / wszystkie proby).

---

## Parametry FSRS v6

Algorytm uzywa 21 parametrow (w[0]-w[20]) wytrenowanych na anonimowych danych z powtorzek z aplikacji Anki. Kontroluja one poczatkowe wartosci stabilnosci, obliczenia trudnosci, krzywa zapominania oraz sposob zmian stabilnosci po kazdej powtorce.

Oparty na badaniach Jarretta Ye oraz spolecznosci [open-spaced-repetition](https://github.com/open-spaced-repetition).
