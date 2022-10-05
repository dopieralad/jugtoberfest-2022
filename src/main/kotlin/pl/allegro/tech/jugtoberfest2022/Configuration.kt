package pl.allegro.tech.jugtoberfest2022

import org.apache.beam.runners.dataflow.DataflowRunner
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions
import org.apache.beam.runners.portability.PortableRunner
import org.apache.beam.sdk.options.PipelineOptions
import org.apache.beam.sdk.options.PortablePipelineOptions
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

object Configuration {
    fun configure(options: PipelineOptions): PipelineOptions =
        if (System.getenv("RUNNER") == "DataflowRunner") configureDataflow(options)
        else configureLocal(options)

    private fun configureDataflow(options: PipelineOptions): DataflowPipelineOptions = options.`as`(DataflowPipelineOptions::class.java).apply {
        runner = DataflowRunner::class.java
        region = "europe-west1"
        workerZone = "europe-west1-b"
        jobName = "jugtoberfest-2022-${DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").withZone(ZoneOffset.UTC).format(Instant.now())}"
        numWorkers = 1
        maxNumWorkers = 1
        diskSizeGb = 100
        workerMachineType = "n1-standard-1"
        usePublicIps = false
        project = TODO()
        tempLocation = TODO()
        stagingLocation = TODO()
        subnetwork = TODO()
    }

    private fun configureLocal(options: PipelineOptions): PipelineOptions = options.`as`(PortablePipelineOptions::class.java).apply {
        runner = PortableRunner::class.java
        jobEndpoint = "localhost:8081"
    }
}
