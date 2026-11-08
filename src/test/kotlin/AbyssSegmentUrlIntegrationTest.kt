import com.abmo.di.koinModule
import com.abmo.model.video.toSimpleVideo
import com.abmo.services.VideoDownloader
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import kotlin.test.assertFalse
import kotlin.test.assertNotNull

class AbyssSegmentUrlIntegrationTest : KoinComponent {
    private val videoDownloader: VideoDownloader by inject()

    @ParameterizedTest
    @ValueSource(strings = ["M-Dr7OaY0", "Pr4AZVvi8"])
    fun `default resolution must not produce a null segment hostname`(id: String) {
        val metadata = assertNotNull(videoDownloader.getVideoMetaData(
            "https://abysscdn.com/?v=$id",
            mapOf("Referer" to "https://abyss.to/")
        ))
        println("id=$id domains=${metadata.domains}")
        metadata.sources?.forEach { source ->
            println("id=$id label=${source?.label} size=${source?.size} sub=${source?.sub} status=${source?.status} codec=${source?.codec} hasUrl=${!source?.url.isNullOrBlank()}")
        }
        val source = assertNotNull(metadata.sources?.maxBy { it?.size!! })
        val video = metadata.toSimpleVideo(assertNotNull(source.label))
        println("id=$id selected=${video.label} segmentBase=${video.url}")
        assertFalse(video.url.orEmpty().contains("null"), "id=$id invalid segment URL: ${video.url}")
    }

    companion object {
        @JvmStatic
        @BeforeAll
        fun setUp() {
            startKoin { modules(koinModule) }
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            stopKoin()
        }
    }
}
