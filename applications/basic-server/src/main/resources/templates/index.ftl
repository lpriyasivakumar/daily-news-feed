<#import "template.ftl" as layout />

<@layout.noauthentication>
    <section class="container">
        <form action="/echo" method="post">
            <p> Simple input echoer </p>
            <input name="user_input"/>
            <input type="submit" value="Submit"/>
        </form>
    </section>

</@layout.noauthentication>