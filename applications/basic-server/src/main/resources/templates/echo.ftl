<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section class="container">
        <div>
            <p>You entered ${text}</p>
        </div>
    </section>

</@layout.noauthentication>