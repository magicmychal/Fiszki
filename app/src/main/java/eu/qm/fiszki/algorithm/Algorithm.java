package eu.qm.fiszki.algorithm;

import android.content.Context;

import java.util.ArrayList;
import java.util.Random;

import eu.qm.fiszki.model.flashcard.Flashcard;
import eu.qm.fiszki.model.flashcard.FlashcardRepository;

/**
 * Created by mBoiler on 11.02.2016.
 */
public class Algorithm {

    public Context mContext;
    public int draw;
    FlashcardRepository flashcardRepository;
    CatcherFlashcardToAlgorithm catcherFlashcardToAlgorithm;
    PriorityCount priorityCount;
    MultiplierPoints multiplierPoints;
    int[] calculatedPriority;
    Drawer drawer;

    public Algorithm(Context context) {
        flashcardRepository = new FlashcardRepository(context);
        catcherFlashcardToAlgorithm = new CatcherFlashcardToAlgorithm(context);
    }

    public Flashcard drawCardAlgorithm(ArrayList<Flashcard> flashcardPool) {
        // TODO: Implement priority-based algorithm using PriorityCount, MultiplierPoints, Drawer
        // For now, uses random selection from the pool
        return flashcardPool.get(new Random().nextInt(flashcardPool.size()));
    }


}
