# How the Algorithm Works

Fiszki uses two learning algorithms. You can switch between them in Settings.

---

## FSRS (Free Spaced Repetition Scheduler)

FSRS is a modern spaced repetition algorithm based on scientific research into human memory. It schedules reviews at optimal intervals to maximize long-term retention with minimal effort.

### Card States

Every flashcard is in one of four states:

| State | Meaning |
|---|---|
| **New** | Never reviewed. The card has no memory data yet. |
| **Learning** | Being learned for the first time. You'll see it again soon. |
| **Review** | Graduated to long-term memory. Intervals grow with each success. |
| **Relearning** | Previously known but forgotten. Back to short intervals. |

### How Ratings Work

After each answer, the algorithm assigns a rating based on your performance:

| Rating | When it happens |
|---|---|
| **Easy** | Correct on 1st attempt, answered within 2 minutes, exact match |
| **Good** | Correct on 1st attempt, but took longer or had minor typos |
| **Hard** | Correct, but only after multiple attempts |
| **Again** | Skipped or gave up |

### Key Concepts

**Stability** measures how long a memory lasts. Higher stability means you can wait longer before the next review. After a successful review, stability increases. After forgetting, it drops.

**Difficulty** (1.0 - 10.0) represents how hard a card is for you personally. Cards you consistently get right become easier. Cards you struggle with become harder. Difficulty affects how fast stability grows.

**Retrievability** is the probability that you can recall a card right now. It starts high after a review and decays over time following a forgetting curve. When retrievability drops to about 90%, it's time to review.

**Interval** is the number of days until the next scheduled review. It's calculated from stability and your desired retention rate (90% by default).

### Mastery

When FSRS is active, the mastery percentage for each set is the **average retrievability** across all cards. This tells you what percentage of the set you could recall right now. It naturally decays over time if you don't review, and increases after practice sessions.

---

## Legacy Algorithm

The legacy algorithm uses a simple priority-based random selection. Each card has a priority (0-5). Getting a card right increases its priority, making it less likely to appear. Getting it wrong decreases the priority, making it more likely to appear.

Mastery in legacy mode is calculated as your overall pass rate (correct answers / total attempts).

---

## FSRS v6 Parameters

The algorithm uses 21 parameters (w[0]-w[20]) trained on anonymised review data from Anki. These control initial stability values, difficulty calculations, the forgetting curve, and how stability changes after each review.

Based on research by Jarrett Ye and the [open-spaced-repetition](https://github.com/open-spaced-repetition) community.
