<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section class="container">
        <table class="tblNews">
            <tr>
                <th>Source</th>
                <th>Title</th>
                <th>Description</th>
                <th>Content</th>
                <th>Url</th>
                <th>Sentiment</th>
                <th>Date</th>
            </tr>
            <#list articles as article>
            <tr>
                <td>${article.sourceName}</td>
                <td>${article.title}</td>
                <td>${article.description}</td>
                <td>${article.content}</td>
                <td>${article.url}</td>
                <td>${article.sentiment}</td>
                <td>${article.publishedAt}</td>
            </tr>
            </#list>
        </table>
    </section>
</@layout.noauthentication>