package schema

object Main {
    val schema1 = StructSchema(List(StructField("a", StringSchema), StructField("b", StructSchema(List(StructField("c", IntSchema), StructField("x", IntSchema))))))
    val schema2 = StructSchema(List(StructField("a", StringSchema), StructField("b", StructSchema(List(StructField("c", StringSchema), StructField("d", StringSchema))))))

    def main(args: Array[String]): Unit = {
        pprint.pprintln(schema1)
        pprint.pprintln(schema2)
        pprint.pprintln(SchemaCompare.compare(schema1, schema2))
    }

}
