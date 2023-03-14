package lab5kotlin.collection.sort

import lab5kotlin.collection.item.Entity

class IdComparator: Comparator<Entity> {
    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.
     *
     *
     *
     * The implementor must ensure that [ signum][Integer.signum]`(compare(x, y)) == -signum(compare(y, x))` for
     * all `x` and `y`.  (This implies that `compare(x, y)` must throw an exception if and only if `compare(y, x)` throws an exception.)
     *
     *
     *
     * The implementor must also ensure that the relation is transitive:
     * `((compare(x, y)>0) && (compare(y, z)>0))` implies
     * `compare(x, z)>0`.
     *
     *
     *
     * Finally, the implementor must ensure that `compare(x,
     * y)==0` implies that `signum(compare(x,
     * z))==signum(compare(y, z))` for all `z`.
     *
     * @apiNote
     * It is generally the case, but *not* strictly required that
     * `(compare(x, y)==0) == (x.equals(y))`.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is less than, equal to, or greater than the
     * second.
     * @throws NullPointerException if an argument is null and this
     * comparator does not permit null arguments
     * @throws ClassCastException if the arguments' types prevent them from
     * being compared by this comparator.
     */
    override fun compare(o1: Entity?, o2: Entity?): Int {
        return o1?.id!!.minus(o2!!.id)
    }
}