package fr.inria.diversim.model;

/**
 * User: Simon
 * Date: 7/3/13
 * Time: 10:46 AM
 */
public enum ServiceState {
    OK, FAIL, ATTACK;

    public int stateToInt() {
        int c = 0;
        switch (this) {
            case OK:
                c = 1;
                break;
            case FAIL:
                c = 2;
                break;
            case ATTACK:
                c = 3;
                break;
        }
        return c;
    }
}
