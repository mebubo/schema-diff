package schema

class SchemaTest extends munit.FunSuite {
    test("NoDiff") {
        val schema1 = IntSchema
        val schema2 = IntSchema
        val diff = SchemaCompare.compare(schema1, schema2)
        assertEquals(diff, NoDiff)
    }

    test("Different primitives") {
        val schema1 = IntSchema
        val schema2 = StringSchema
        val diff = SchemaCompare.compare(schema1, schema2)
        assertEquals(diff, PrimitiveDiff(schema1, schema2))
    }

    test("Primitive vs Struct") {
        val schema1 = IntSchema
        val schema2 = StructSchema(List())
        val diff = SchemaCompare.compare(schema1, schema2)
        assertEquals(diff, PrimitiveDiff(schema1, schema2))
    }

    test("Struct vs Primitive") {
        val schema1 = StructSchema(List())
        val schema2 = IntSchema
        val diff = SchemaCompare.compare(schema1, schema2)
        assertEquals(diff, PrimitiveDiff(schema1, schema2))
    }

    test("Struct field schema change, then primitive diff and no diff") {
        val left = StructSchema(List(StructField("a", IntSchema), StructField("b", StringSchema)))
        val right = StructSchema(List(StructField("a", StringSchema), StructField("b", StringSchema)))
        val diff = SchemaCompare.compare(left, right)
        assertEquals(diff, StructDiff(List("a" -> PrimitiveDiff(IntSchema, StringSchema), "b" -> NoDiff), List(), List()))
    }

    test("One struct field only in left, one only in right, one in common") {
        val left = StructSchema(List(StructField("a", IntSchema), StructField("b", StringSchema)))
        val right = StructSchema(List(StructField("c", StringSchema), StructField("b", StringSchema)))
        val diff = SchemaCompare.compare(left, right)
        assertEquals(diff, StructDiff(List("b" -> NoDiff), List(StructField("a", IntSchema)), List(StructField("c", StringSchema))))
    }
}
