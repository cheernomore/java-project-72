@import hexlet.code.dto.urls.UrlPage

@param UrlPage page

@template.layout.page(
    content = @`
        <div>
            <p>URL: ${page.getUrl().getName()}</p>
        </div>
    `
)