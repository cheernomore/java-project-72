@import hexlet.code.controller.NamedRoutes
@import hexlet.code.dto.urls.UrlPage

@param UrlPage page

@template.layout.page(
    content = @`
        <div class="row mb-4">
            <div class="col">
                <h1 class="display-6">Информация о сайте</h1>
            </div>
        </div>

        <div class="card shadow-sm mb-4">
            <div class="card-body">
                <div class="row align-items-center">
                    <div class="col-md-8">
                        <h5 class="card-title mb-3">
                            <i class="bi bi-globe text-primary me-2"></i>
                            URL адрес
                        </h5>
                        <p class="card-text fs-5 mb-0">
                            <a href="${page.getUrl().getName()}" target="_blank" class="text-decoration-none">
                                ${page.getUrl().getName()}
                                <i class="bi bi-box-arrow-up-right ms-1 small"></i>
                            </a>
                        </p>
                        <small class="text-muted">ID: ${page.getUrl().getId()}</small>
                    </div>
                    <div class="col-md-4 text-md-end mt-3 mt-md-0">
                        <form action="${NamedRoutes.urlsIdCheck(String.valueOf(page.getUrl().getId()))}" method="post">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-arrow-clockwise me-2"></i>
                                Запустить проверку
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <div class="card shadow-sm">
            <div class="card-header bg-light">
                <h5 class="mb-0">
                    <i class="bi bi-clipboard-check text-success me-2"></i>
                    История проверок
                </h5>
            </div>
            <div class="card-body p-0">
                @if(page.getUrlCheckList() == null || page.getUrlCheckList().isEmpty())
                    <div class="p-4 text-center">
                        <i class="bi bi-inbox display-1 text-muted"></i>
                        <p class="mt-3 mb-0 text-muted">Проверки не выполнялись</p>
                        <p class="text-muted">Нажмите кнопку "Запустить проверку" для анализа сайта</p>
                    </div>
                @else
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col" width="60">ID</th>
                                    <th scope="col" width="100">Статус</th>
                                    <th scope="col">Title</th>
                                    <th scope="col">H1</th>
                                    <th scope="col">Description</th>
                                    <th scope="col" width="180">Дата проверки</th>
                                </tr>
                            </thead>

                            <tbody>
                            @for(var urlCheck : page.getUrlCheckList())
                                <tr class="align-middle">
                                    <th scope="row">
                                        <span class="badge bg-secondary">${urlCheck.getId()}</span>
                                    </th>
                                    <td>
                                        !{var statusCode = urlCheck.getStatusCode();}
                                        @if(statusCode >= 200 && statusCode < 300)
                                            <span class="badge bg-success">
                                                <i class="bi bi-check-circle me-1"></i>
                                                ${statusCode}
                                            </span>
                                        @elseif(statusCode >= 400 && statusCode < 500)
                                            <span class="badge bg-warning text-dark">
                                                <i class="bi bi-exclamation-triangle me-1"></i>
                                                ${statusCode}
                                            </span>
                                        @elseif(statusCode >= 500)
                                            <span class="badge bg-danger">
                                                <i class="bi bi-x-circle me-1"></i>
                                                ${statusCode}
                                            </span>
                                        @else
                                            <span class="badge bg-secondary">${statusCode}</span>
                                        @endif
                                    </td>
                                    <td>
                                        @if(urlCheck.getTitle() != null && !urlCheck.getTitle().equals("null"))
                                            <span class="text-truncate d-inline-block" style="max-width: 200px;" title="${urlCheck.getTitle()}">
                                                ${urlCheck.getTitle()}
                                            </span>
                                        @else
                                            <span class="text-muted">—</span>
                                        @endif
                                    </td>
                                    <td>
                                        @if(urlCheck.getH1() != null && !urlCheck.getH1().equals("null"))
                                            <span class="text-truncate d-inline-block" style="max-width: 200px;" title="${urlCheck.getH1()}">
                                                ${urlCheck.getH1()}
                                            </span>
                                        @else
                                            <span class="text-muted">—</span>
                                        @endif
                                    </td>
                                    <td>
                                        @if(urlCheck.getDescription() != null && !urlCheck.getDescription().equals("null"))
                                            <span class="text-truncate d-inline-block" style="max-width: 250px;" title="${urlCheck.getDescription()}">
                                                ${urlCheck.getDescription()}
                                            </span>
                                        @else
                                            <span class="text-muted">—</span>
                                        @endif
                                    </td>
                                    <td>
                                        <small class="text-muted">
                                            <i class="bi bi-calendar-check me-1"></i>
                                            ${String.valueOf(urlCheck.getCreatedAt())}
                                        </small>
                                    </td>
                                </tr>
                            @endfor
                            </tbody>
                        </table>
                    </div>
                @endif
            </div>
        </div>

        <div class="mt-4">
            <a href="/urls" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-2"></i>
                Вернуться к списку
            </a>
        </div>
    `
)