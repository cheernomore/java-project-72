@import hexlet.code.controller.NamedRoutes
@import hexlet.code.dto.urls.UrlPage

@param UrlPage page

@template.layout.page(
    content = @`
        <div>
            <p>URL: ${page.getUrl().getName()}</p>
            <form
                    action="${NamedRoutes.urlsIdCheck(String.valueOf(page.getUrl().getId()))}"
                    method="post"
            >
                <input type="submit" value="Check url">
            </form>
        </div>
        @if(page.getUrlCheckList() == null)
            <p>Проверки не выполнялись</p>
        @else
            <table class="table table-striped-columns">
                <thead>
                <tr>
                    <th scope="col">id</th>
                    <th scope="col">status code</th>
                    <th scope="col">title</th>
                    <th scope="col">h1</th>
                    <th scope="col">description</th>
                    <th scope="col">checked at</th>
                </tr>
                </thead>

                <tbody>
                @for(var urlCheck : page.getUrlCheckList())
                    <tr>
                        <th scope="row">${urlCheck.getId()}</th>
                        <td>${String.valueOf(urlCheck.getStatusCode())}</td>
                        <td>${String.valueOf(urlCheck.getTitle())}</td>
                        <td>${String.valueOf(urlCheck.getH1())}</td>
                        <td>${String.valueOf(urlCheck.getDescription())}</td>
                        <td>${String.valueOf(urlCheck.getCreatedAt())}</td>
                    </tr>
                @endfor
                </tbody>
            </table>
        @endif
    `
)