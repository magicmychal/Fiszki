## 2025-05-15 - [Inefficient Database Pattern: Fetch-then-Count/Delete]
**Learning:** The codebase contained multiple instances where the entire contents of a Room database table (or category filter) were fetched into memory (as `ArrayList<Entity>`) just to get a `size` count or to perform a loop-based deletion. This is an anti-pattern that leads to high memory pressure and slow UI performance, especially in list adapters like `CategoryShowAdapter.onBindViewHolder`.
**Action:** Always use specific SQL queries like `COUNT(*)` for size checks and `@Query("DELETE FROM ...")` or batch `@Delete` for multiple items. Use these in DAOs and Repositories rather than fetching and processing lists in memory.

## 2025-05-22 - [Redundant O(N) Filtering in Selection Loop]
**Learning:** The `Algorithm.drawCardAlgorithm` previously performed O(N) filtering inside a weighted selection loop that could execute multiple times (up to 10 attempts). This resulted in O(K*N) complexity. Furthermore, priority 0 cards were ignored during weight calculation but included in selection, causing potential mismatches.
**Action:** Pre-group flashcards by priority in a single O(N) pass during the initial `PriorityCount` calculation. This allows the selection loop to access candidates in O(1), reducing complexity to O(N + K). Ensure consistent mapping of priority 0 to 1 across both counting and selection logic.
