<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section class="container">
        <ul class="tblNews">
            <#list articles as article>
            <li>
                <div class="newsSection">
                    <span>${article.title} >${article.sentiment}</span>
                    <span>${article.sourceName} ${article.url} ${article.publishedAt}</span>
                    <span>${article.content} </span>
                </div>
            </li>
            </#list>
        </ul>
    </section>
</@layout.noauthentication>