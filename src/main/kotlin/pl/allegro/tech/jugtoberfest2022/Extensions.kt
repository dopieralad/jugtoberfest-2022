package pl.allegro.tech.jugtoberfest2022

import org.apache.beam.sdk.coders.Coder
import org.apache.beam.sdk.coders.KvCoder
import org.apache.beam.sdk.transforms.DoFn
import org.apache.beam.sdk.transforms.ParDo
import org.apache.beam.sdk.transforms.WithKeys
import org.apache.beam.sdk.transforms.join.CoGbkResult
import org.apache.beam.sdk.transforms.join.CoGroupByKey
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple
import org.apache.beam.sdk.values.KV
import org.apache.beam.sdk.values.PCollection

fun <I, O> PCollection<I>.flatMap(
    name: String,
    f: (I) -> Collection<O>
): PCollection<O> =
    this.apply(
        name,
        ParDo.of(
            object : DoFn<I, O>() {
                @ProcessElement
                fun processElement(context: ProcessContext) {
                    val results = f(context.element())
                    results.forEach(context::output)
                }
            }
        )
    )

inline fun <I, reified O> PCollection<I>.map(name: String, crossinline map: (I) -> O): PCollection<O> =
    this.flatMap(name) { listOf(map(it)) }.setCoder(coder<O>())

fun <I> PCollection<I>.filter(name: String, matches: (I) -> Boolean): PCollection<I> =
    this.flatMap(name) {
        if (matches(it)) listOf(it)
        else emptyList()
    }

inline fun <reified K, V> PCollection<V>.keyedBy(name: String, noinline f: (V) -> K): PCollection<KV<K, V>> =
    this.apply(name, WithKeys.of(f)).setCoder(KvCoder.of(coder<K>(), this.coder))

inline fun <reified T> PCollection<*>.coder(): Coder<T> =
    this.pipeline.coderRegistry.getCoder(T::class.java)

inline fun <reified K, reified L, reified R> PCollection<KV<K, L>>.innerJoin(
    name: String,
    right: PCollection<KV<K, R>>
): PCollection<KV<K, KV<L, List<R>>>> =
    leftJoin(name, right).filter("Filter elements without matches") { it.value.value.isNotEmpty() }

inline fun <reified K, reified L, reified R> PCollection<KV<K, L>>.leftJoin(
    name: String,
    right: PCollection<KV<K, R>>
): PCollection<KV<K, KV<L, List<R>>>> {
    val leftTag = "left"
    val rightTag = "right"
    return KeyedPCollectionTuple.of(leftTag, this).and(rightTag, right)
        .apply("Group by key", CoGroupByKey.create())
        .flatMap("Explode") { result: KV<K, CoGbkResult> ->
            val groupedLefts = result.value.getAll<L>(leftTag)
            val groupedRights = result.value.getAll<R>(rightTag)
            groupedLefts.map { groupedLeft ->
                KV.of(
                    result.key,
                    KV.of(groupedLeft, groupedRights.toList())
                )
            }
        }
}
