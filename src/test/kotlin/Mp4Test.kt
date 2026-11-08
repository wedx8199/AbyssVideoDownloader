import com.abmo.model.video.Mp4
import com.abmo.model.video.Source
import com.abmo.model.video.toSimpleVideo
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class Mp4Test {
    @Test
    fun `skip a source without a server when the resolution has another source`() {
        val metadata = Mp4(
            domains = listOf("keph9dhn0.sssrr.org", "m0iidt1rp0.sssrr.org"),
            sources = listOf(
                Source(label = "1080p", size = 296681301L, sub = null, res_id = 1),
                Source(label = "1080p", size = 198825564L, sub = "m0iidt1rp0", res_id = 2)
            )
        )

        val video = metadata.toSimpleVideo("1080p")

        assertEquals("https://m0iidt1rp0.sssrr.org", video.url)
        assertEquals(198825564L, video.size)
        assertEquals(2, video.resId)
    }

    @Test
    fun `skip blank server names and null source entries`() {
        val metadata = Mp4(
            domains = listOf("working.sssrr.org"),
            sources = listOf(
                null,
                Source(label = "720p", sub = ""),
                Source(label = "720p", sub = " "),
                Source(label = "720p", sub = "working")
            )
        )

        assertEquals("https://working.sssrr.org", metadata.toSimpleVideo("720p").url)
    }

    @Test
    fun `keep selecting the requested resolution for normal sources`() {
        val metadata = Mp4(
            domains = listOf("low.sssrr.org"),
            sources = listOf(
                Source(label = "360p", sub = "low"),
                Source(label = "720p", sub = "high")
            )
        )

        assertEquals("https://high.sssrr.org", metadata.toSimpleVideo("720p").url)
    }

    @Test
    fun `report an unavailable server instead of constructing a null hostname`() {
        val metadata = Mp4(
            domains = listOf("other.sssrr.org"),
            sources = listOf(Source(label = "1080p", sub = null))
        )

        val error = assertFailsWith<IllegalArgumentException> { metadata.toSimpleVideo("1080p") }
        assertEquals("No source with a server is available for resolution 1080p", error.message)
    }
}
