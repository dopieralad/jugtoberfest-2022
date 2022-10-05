package pl.allegro.tech.jugtoberfest2022

import org.apache.beam.sdk.PipelineResult.State
import org.apache.beam.sdk.options.PipelineOptionsFactory
import org.apache.beam.sdk.Pipeline as BeamPipeline

fun main(args: Array<String>) {
    val options = PipelineOptionsFactory.fromArgs(*args).withValidation().create().also { Configuration.configure(it) }
    println("Options configured")

    val pipeline = BeamPipeline.create(options)
    println("Pipeline created")

    Pipeline.build(pipeline)
    println("Pipeline built")

    val result = pipeline.run()
    println("Pipeline started")

    val state = result.waitUntilFinish()
    println("Pipeline finished")

    if (state != State.DONE) throw RuntimeException("Pipeline finished with state different than `DONE`!")
}
