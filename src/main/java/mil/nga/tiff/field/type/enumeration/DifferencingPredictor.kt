package mil.nga.tiff.field.type.enumeration

import mil.nga.tiff.internal.predictor.FloatingPointPredictor
import mil.nga.tiff.internal.predictor.HorizontalPredictor
import mil.nga.tiff.internal.predictor.NullPredictor
import mil.nga.tiff.internal.predictor.Predictor

enum class DifferencingPredictor(val id: Int, val implementation: Predictor) {
    NO(1, NullPredictor()),
    HORIZONTAL(2, HorizontalPredictor()),
    FLOATINGPOINT(3, FloatingPointPredictor());

    companion object {
        private val DEFAULT = NO

        @JvmStatic
        fun findById(id: Int?): DifferencingPredictor {
            if (id == null) {
                return DEFAULT
            }

            return entries.first { it.id == id}
        }
    }
}
