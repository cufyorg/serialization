package org.cufy.ped

import org.cufy.bson.BsonDocument
import org.cufy.bson.ID
import org.cufy.bson.bson
import org.cufy.bson.java
import kotlin.test.Test
import kotlin.test.assertEquals

class ExampleTest {
    @Test
    fun `common example 1`() {
        val id = ID<Document1>()
        val name = "Sulaiman"
        val age = 69

        val doc = BsonDocument {
            Document1.Id by id
            Document1.Name by name
            Document1.Age by age
        }

        val actual = doc decode Document1f1Codec

        val expected = Document1f1(
            id = id,
            name = name,
            age = age
        )

        println(doc)
        println(doc.java)
        assertEquals(expected, actual)
    }

    @Test
    fun `common example 2`() {
        val id = ID<Document1>()
        val name = "Abdullah"
        val age = 6969
        val birthday = 696969696969

        val doc = BsonDocument {
            Document1.Id by id
            Document1.Name by name
            Document1.Age by age
            "birthday" by birthday
        }

        val fragment = Document1f2(doc)

        assertEquals(id, fragment.id)
        assertEquals(name, fragment.name)
        assertEquals(age, fragment.age)
        assertEquals(birthday, fragment.birthday)
    }

    @Test
    fun `catchIn actually uses the fallback value`() {
        val nameSource = "Hello World".bson
        val codec = Bson.Int32 defaultIn 0

        val name = nameSource decodeAny codec

        assertEquals(0, name)
    }
}

object Document1 {
    val Id = Bson.ID<Document1>() at "_id"
    val Name = Bson.String at "name"
    val Age = Bson.Int32 at "age"

    val Birthday = "birthday"
        .be(Bson.Int64.Nullable)

    val Birthday1 = "birthday1"
        .be(Bson.Int64.Nullable)
        .defaultIn(0)
        .catchIn { 0 }
}

data class Document1f1(
    val id: ID<Document1>,
    val name: String,
    val age: Int,
)

data class Document1f2(val document: BsonDocument) {
    val id by lazy { document[Document1.Id] }
    val name by lazy { document[Document1.Name] }
    val age by lazy { document[Document1.Age] }

    val birthday by lazy { document[Document1.Birthday] }
}

val Document1f2.birthday1 get() = document[Document1.Birthday1]

val Document1f1Codec = Codec {
    encodeCatching { it: Document1f1 ->
        BsonDocument {
            Document1.Id by it.id
            Document1.Name by it.name
            Document1.Age by it.age
        }
    }
    decodeCatching { it: BsonDocument ->
        Document1f1(
            id = it[Document1.Id],
            name = it[Document1.Name],
            age = it[Document1.Age]
        )
    }
}
