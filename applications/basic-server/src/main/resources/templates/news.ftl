<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section class="container">
        <div>
            <h2>News Topics - AI, Technology</h2>
            <div class="filter-btn">
                <h2>Filter By Sentiment:</h2>
                <a href="/news-feed?filterby=all">All</a>
                <a href="/news-feed?filterby=positive">Positive</a>
                <a href="/news-feed?filterby=negative">Negative</a>
                <a href="/news-feed?filterby=neutral">Neutral</a>
            </div>
        </div>
        <#list articles as article>
            <article class="card">
                <div class="content">
                    <div>
                        <div>
                            <h3>${article.title} </h3>
                            <#if article.sentimentValue == 4>
                                <h4>Sentiment: Very positive</h4>
                            <#elseIf article.sentimentValue == 3>
                                <h4>Sentiment: Positive</h4>
                            <#elseIf article.sentimentValue == 1>
                                <h4>Sentiment: Negative</h4>
                            <#elseIf article.sentimentValue == 0>
                                <h4>Sentiment: Very Negative</h4>️
                            <#elseIf article.sentimentValue == 2>
                                <h4>Sentiment: Neutral</h4>
                            <#else>
                                <h4>Sentiment: Unknown</h4>
                            </#if>
                            <div class="news-details">
                                <div>Source: ${article.sourceName?capitalize}</div>
                                <div>Publish Date: ${article.publishedAt}</div>
                                <div><a href=${article.url}>Read More..</a></div>
                            </div>
                        </div>
                        <#if article.imageUrl?hasContent>
                            <div class="news-image">
                                <img src=${article.imageUrl} alt=${article.title}>
                            </div>
                        </#if>
                    </div>
                </div>
            </article>
        </#list>
    </section>
</@layout.noauthentication>