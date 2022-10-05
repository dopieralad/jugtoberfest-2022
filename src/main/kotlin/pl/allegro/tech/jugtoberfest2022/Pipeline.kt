package pl.allegro.tech.jugtoberfest2022

import org.apache.beam.sdk.coders.StringUtf8Coder
import org.apache.beam.sdk.extensions.python.PythonExternalTransform
import org.apache.beam.sdk.io.TextIO
import org.apache.beam.sdk.transforms.Create
import org.apache.beam.sdk.values.PCollection
import org.apache.beam.sdk.Pipeline as BeamPipeline

object Pipeline {
    fun build(pipeline: BeamPipeline) {
        pipeline
            .apply("Generate years", Create.of(2022.rangeTo(3022)))
            .map("Map to phrases") { year -> "JUGtoberFest $year!" }
            .apply(
                "Hash phrases",
                PythonExternalTransform
                    .from<PCollection<String>, PCollection<String>>("jugtoberfest-2022.hash_strings.HashStrings", "localhost:8080")
                    .withOutputCoder(StringUtf8Coder.of())
            )
            .apply(
                "Write hashes",
                TextIO.write().to("gs://${TODO()}/hashes").withSuffix(".csv")
            )
    }
}
