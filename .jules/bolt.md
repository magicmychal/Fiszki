## 2025-05-15 - [Inefficient Database Pattern: Fetch-then-Count/Delete]
**Learning:** The codebase contained multiple instances where the entire contents of a Room database table (or category filter) were fetched into memory (as `ArrayList<Entity>`) just to get a `size` count or to perform a loop-based deletion. This is an anti-pattern that leads to high memory pressure and slow UI performance, especially in list adapters like `CategoryShowAdapter.onBindViewHolder`.
**Action:** Always use specific SQL queries like `COUNT(*)` for size checks and `@Query("DELETE FROM ...")` or batch `@Delete` for multiple items. Use these in DAOs and Repositories rather than fetching and processing lists in memory.

## 2025-05-22 - [Redundant O(N) Filtering in Selection Loops]
**Learning:** The flashcard selection algorithm was repeatedly filtering the entire card pool ($O(N)$) inside a weighted selection loop (up to 10 attempts). This results in $O(Attempts \times N)$ complexity, which is inefficient for large flashcard sets.
**Action:** Use a single-pass pre-grouping step (e.g., `groupBy`) to organize the data into an efficient lookup structure (like a Map) before entering the loop. This reduces the selection complexity to $O(N + Attempts)$.
