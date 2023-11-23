package io.collective.start.analyzer

import edu.stanford.nlp.ling.CoreAnnotations
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations
import edu.stanford.nlp.trees.Tree
import java.util.*

class SentimentAnalyzer {
    private var pipeline: StanfordCoreNLP
    init {
        val props: Properties = Properties()
        props.setProperty("annotators", "tokenize,ssplit,parse,sentiment")
        pipeline = StanfordCoreNLP(props)
    }
    fun getSentiment(text: String): Number {
        var sentiment:Number=0
        val annotation = pipeline.process(text)
        for (sentence in annotation.get(CoreAnnotations.SentencesAnnotation::class.java)) {
            val tree: Tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree::class.java)
            sentiment= RNNCoreAnnotations.getPredictedClass(tree)
        }
        return sentiment
    }

}