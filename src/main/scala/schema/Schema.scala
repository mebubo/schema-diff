package schema

case class StructField(name: String, value: Schema)
case class SchemaDiffField(name: String, value: SchemaDiff)

sealed trait Schema
case object IntSchema extends Schema
case object StringSchema extends Schema
case class StructSchema(fields: List[StructField]) extends Schema

sealed trait SchemaDiff
case object NoDiff extends SchemaDiff
case class PrimitiveDiff(left: Schema, right: Schema) extends SchemaDiff
case class StructDiff(schemaDiff: List[(String, SchemaDiff)], onlyInLeft: List[StructField], onlyInRight: List[StructField]) extends SchemaDiff

object SchemaCompare {
    def compare(left: Schema, right: Schema): SchemaDiff = (left, right) match {
        case (StructSchema(fieldsLeft), StructSchema(fieldsRight)) =>
            val fieldNamesLeft = fieldsLeft.map(_.name)
            val fieldNamesRight = fieldsRight.map(_.name)
            val namesOnlyInLeft = (fieldNamesLeft.toSet -- fieldNamesRight).toList
            val namesOnlyInRight = (fieldNamesRight.toSet -- fieldNamesLeft).toList
            val namesInBoth = fieldNamesLeft.intersect(fieldNamesRight)
            val schemaDiff = namesInBoth.map { name =>
                val l = lookupField(name, fieldsLeft)
                val r = lookupField(name, fieldsRight)
                name -> compare(l.value, r.value)
            }
            val onlyInLeft = namesOnlyInLeft.map(n => lookupField(n, fieldsLeft))
            val onlyInRight = namesOnlyInRight.map(n => lookupField(n, fieldsRight))
            StructDiff(schemaDiff, onlyInLeft, onlyInRight)
        case (l, r) if l == r => NoDiff
        case (l, r) if l != r => PrimitiveDiff(l, r)
    }

    def lookupField(name: String, fields: List[StructField]): StructField = fields.find(_.name == name).get
}