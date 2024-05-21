import io.github.com6235.configurator.ConfigLoader
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// HOCON
@Serializable
data class Cocfig(val ktor: KtorCocfig, val aftor: AftorCocfig)
@Serializable
data class KtorCocfig(val biba: Int)
@Serializable
data class AftorCocfig(val befor: BeforAftorCocfig)
@Serializable
data class BeforAftorCocfig(val nwo: NwoBeforAftorCocfig)
@Serializable
data class NwoBeforAftorCocfig(val `uwu-mode`: String)

// JSON
@Serializable
data class Json(val users: List<User>, val users_count: Int)
@Serializable
data class User(val name: String, val age: Int)

// PROPERTIES
@Serializable
data class Properties(val potato: String, val `fun`: String)

// YAML
@Serializable
data class Yaml(val name: String, val jobs: MutableMap<String, Job>)
@Serializable
data class Job(val `runs-on`: String, val steps: MutableMap<String, Step>)
@Serializable
data class Step(val uses: String)

// TOML
@Serializable
data class Toml(val name: String, val account: Account)
@Serializable
data class Account(val email: String, val password: String, val dateOfBirth: MutableList<Int>)

class ConfigTest {
    @Test
    fun testLoadingHocon() {
        val stream = ConfigTest::class.java.getResourceAsStream("hocon.conf")!!
        val s = ConfigLoader(Cocfig.serializer()).loadConfig(stream, "hocon").data

        assertEquals(19, s.ktor.biba)
        assertEquals("enadled", s.aftor.befor.nwo.`uwu-mode`)
    }

    @Test
    fun testLoadingJson() {
        val stream = ConfigTest::class.java.getResourceAsStream("json.json")!!
        val s = ConfigLoader(Json.serializer()).loadConfig(stream, "json").data

        assertEquals(21, s.users.first { it.name == "Beberich" }.age)
        assertEquals(2, s.users_count)
    }

    @Test
    fun testLoadingProperties() {
        val stream = ConfigTest::class.java.getResourceAsStream("props.properties")!!
        val s = ConfigLoader(Properties.serializer()).loadConfig(stream, "properties").data

        assertEquals("kartofka", s.potato)
        assertEquals("cmex", s.`fun`)
    }

    @Test
    fun testLoadingYaml() {
        val stream = ConfigTest::class.java.getResourceAsStream("yaml.yaml")!!
        val s = ConfigLoader(Yaml.serializer()).loadConfig(stream, "yaml").data

        assertEquals("Test", s.name)
        assertEquals("my bebera", s.jobs["test"]?.`runs-on`)
    }

    @Test
    fun testLoadingToml() {
        val stream = ConfigTest::class.java.getResourceAsStream("toml.toml")!!
        val s = ConfigLoader(Toml.serializer()).loadConfig(stream, "toml").data

        assertEquals("niba", s.name)
        assertEquals("01022012", s.account.password)
        assertEquals(1, s.account.dateOfBirth[0])
    }

}