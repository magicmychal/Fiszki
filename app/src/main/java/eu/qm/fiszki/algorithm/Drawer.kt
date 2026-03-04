package eu.qm.fiszki.algorithm

import kotlin.random.Random

class Drawer {
    fun drawInteger(max: Int): Int = Random.nextInt(max)
}
