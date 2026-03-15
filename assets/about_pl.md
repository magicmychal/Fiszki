# O Fiszkach

## Słowo od autora/opiekuna projektu

Fiszki to odrodzona wersja oryginalnej aplikacji Fiszki, którą współtworzyłem z moim najlepszym przyjacielem w czasach szkolnych jako część zespołu RST Newbies. Po ukończeniu szkoły życie potoczyło się w różnych kierunkach, i nie mieliśmy już czasu na dalszy rozwój aplikacji.

Teraz, mieszkając w nowym kraju i ucząc się lokalnego języka, stwierdziłem, że brak mi narzędzia, z którego kiedyś korzystałem. Chociaż na rynku jest wiele aplikacji do nauki języków — zarówno płatnych, jak i darmowych — potrzebowałem czegoś konkretnego: aplikacji, która zmusi mnie do aktywnego pisania słów i pozwoli tworzyć własne, spersonalizowane zestawy fiszek.

Oryginalna aplikacja nie była już kompatybilna z najnowszymi wersjami Androida i została usunięta ze sklepu Play. Dzięki postępowi w dziedzinie sztucznej inteligencji udało mi się szybko przywrócić ją do życia. W moim repozytorium na GitHubie znajdziesz oryginalną wersję, zaktualizowaną do działania na Androidzie 13 i nowszych. Wersja, z której teraz korzystasz, została przeprojektowana, aby lepiej odpowiadać obecnym wytycznym stylistycznym.

**Uwaga:** Nie jestem ani programistą, ani projektantem, więc mogą występować błędy lub ograniczenia. Aby pozostać wiernym pierwotnemu duchowi projektu, aplikacja pozostaje open source. Zachęcam do zgłaszania problemów, wkładu w rozwój projektu lub pomocy w tłumaczeniach poprzez oficjalne repozytorium na GitHubie.

---

## Jak to działa

Fiszki nie zawierają gotowych zestawów fiszek. Zamiast tego aplikacja została zaprojektowana dla użytkowników, którzy już posiadają własne materiały do nauki — czy to z kursów językowych, czy innych aplikacji. Aplikacja pomaga przekształcić te materiały w fiszki i skutecznie je zapamiętywać.

### Główne funkcje:
- **Tworzenie własnych zestawów fiszek:** Buduj własne zestawy słownictwa lub pojęć.
- **Tryb nauki:** Testuj się, korzystając z algorytmu FSRS (Free Spaced Repetition Scheduler), który optymalizuje powtarzanie kart, aby pomóc Ci szybciej zapamiętywać słowa. [Dowiedz się więcej o FSRS.]
- **Tryb egzaminu:** Sprawdź się w surowszym trybie, w którym nie można natychmiast powtarzać słów. Wyniki są wyświetlane dopiero po ukończeniu określonej liczby fiszek.

---

## Dodatkowe ustawienia

### Import i eksport zestawów
- **Import:** Łatwo importuj zestawy fiszek w formacie CSV bezpośrednio z ekranu "Edytuj zestaw". Przygotuj swoje zestawy w Excelu lub Arkuszach Google na komputerze, a następnie zaimportuj je do aplikacji, aby łatwo nimi zarządzać.
- **Eksport:** Udostępniaj swoje zestawy innym, eksportując je z aplikacji.

## Podziękowania
Fiszki opierają się na następujących projektach open-source oraz badaniach:

| Projekt | Zastosowanie | Licencja |
|---------|-------------|----------|
| [FSRS (Free Spaced Repetition Scheduler)](https://github.com/open-spaced-repetition/fsrs-rs) | Algorytm powtórek rozłożonych w czasie — harmonogram FSRS v6 został przeniesiony z referencyjnej implementacji w języku Rust | MIT |
| [ORMLite](https://ormlite.com/) | ORM dla SQLite na Androida | ISC |
| [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) | Szuflada nawigacyjna | Apache 2.0 |
| [Material Dialogs](https://github.com/afollestad/material-dialogs) | Framework okien dialogowych | MIT |
| [SlidingTutorial](https://github.com/nickseven/SlidingTutorial) | Samouczek przy pierwszym uruchomieniu | MIT |
| [Sentry Android SDK](https://github.com/getsentry/sentry-java) | Dobrowolne raportowanie błędów i diagnostyka | MIT |
| [Jetpack Compose](https://developer.android.com/jetpack/compose) | Narzędzie do tworzenia interfejsu użytkownika | Apache 2.0 |
| [Google Fonts for Compose](https://developer.android.com/develop/ui/compose/text/fonts#downloadable) | Roboto Flex, Roboto Mono, Roboto Serif, Porter Sans Block | Apache 2.0 / OFL |

Algorytm FSRS opiera się na badaniach Jarretta Ye oraz społeczności [open-spaced-repetition](https://github.com/open-spaced-repetition). Domyślne parametry (w[0..20]) pochodzą z modelu FSRS v6 wytrenowanego na anonimowych danych recenzji z aplikacji Anki.
