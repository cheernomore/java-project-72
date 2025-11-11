@import hexlet.code.controller.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.model.Url

@param UrlsPage page

@template.layout.page(
    content = @`
        <div class="row mb-4">
            <div class="col">
                <h1 class="display-5">
                    <i class="bi bi-list-check text-primary me-2"></i>
                    Список проверенных сайтов
                </h1>
                <p class="text-muted">Всего сайтов: ${page.getUrls().size()}</p>
            </div>
            <div class="col-auto">
                <a href="/urls/build" class="btn btn-success">
                    <i class="bi bi-plus-circle me-2"></i>
                    Добавить сайт
                </a>
            </div>
        </div>

        @if(page.getUrls().isEmpty())
            <div class="alert alert-info" role="alert">
                <i class="bi bi-info-circle me-2"></i>
                Список сайтов пуст. <a href="/urls/build" class="alert-link">Добавьте первый сайт</a> для начала работы.
            </div>
        @else
            <div class="card shadow-sm">
                <div class="card-body p-0">
                    <div class="table-responsive">
                        <table class="table table-hover mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th scope="col" width="60">ID</th>
                                    <th scope="col">Сайт</th>
                                    <th scope="col" width="150">Статус</th>
                                    <th scope="col" width="200">Последняя проверка</th>
                                    <th scope="col" width="100">Действия</th>
                                </tr>
                            </thead>

                            <tbody>
                            @for(Url url : page.getUrls())
                                <tr class="align-middle">
                                    <th scope="row">
                                        <span class="badge bg-secondary">${url.getId()}</span>
                                    </th>
                                    <td>
                                        <a href="${NamedRoutes.handleUrlPath(url.getId())}" class="text-decoration-none fw-semibold">
                                            <i class="bi bi-link-45deg me-1"></i>
                                            ${url.getName()}
                                        </a>
                                    </td>
                                    @if(page.getUrlChecks().isEmpty() || page.getUrlChecks().get(url.getId()) == null || page.getUrlChecks().get(url.getId()).getStatusCode() == 0)
                                        <td>
                                            <span class="badge bg-warning text-dark">
                                                <i class="bi bi-clock me-1"></i>
                                                Не проверялся
                                            </span>
                                        </td>
                                        <td class="text-muted">—</td>
                                    @else
                                        !{var statusCode = page.getUrlChecks().get(url.getId()).getStatusCode();}
                                        <td>
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
                                            <small class="text-muted">
                                                <i class="bi bi-calendar-check me-1"></i>
                                                ${String.valueOf(page.getUrlChecks().get(url.getId()).getCreatedAt())}
                                            </small>
                                        </td>
                                    @endif
                                    <td>
                                        <a href="${NamedRoutes.handleUrlPath(url.getId())}" class="btn btn-sm btn-outline-primary">
                                            <i class="bi bi-eye"></i>
                                        </a>
                                    </td>
                                </tr>
                            @endfor
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        @endif

        @if(page.getFlash() != null)
        <!-- Флеш-уведомление справа сверху -->
        <div class="position-fixed top-0 end-0 p-3" style="z-index: 1050;">
            <div class="toast align-items-center text-white bg-success border-0 show"
                 role="alert"
                 aria-live="assertive"
                 aria-atomic="true"
                 id="flash-toast">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi bi-check-circle-fill me-2"></i>
                        ${page.getFlash()}
                    </div>
                    <button type="button"
                            class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"
                            aria-label="Close"></button>
                </div>
            </div>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const toastElement = document.getElementById('flash-toast');
                if (toastElement) {
                    const toast = new bootstrap.Toast(toastElement, {
                        autohide: true,
                        delay: 4000
                    });

                    // Показываем toast
                    toast.show();

                    // Автоматически скрываем через 4 секунды
                    setTimeout(function() {
                        toast.hide();
                    }, 4000);
                }
            });
        </script>
    @endif

        <style>
        /* Дополнительные стили для плавной анимации */
        .toast {
            animation: slideInRight 0.3s ease-out;
        }

        .toast.hiding {
            animation: slideOutRight 0.3s ease-out;
        }

        @keyframes slideInRight {
            from {
                opacity: 0;
                transform: translateX(100%);
            }
            to {
                opacity: 1;
                transform: translateX(0);
            }
        }

        @keyframes slideOutRight {
            from {
                opacity: 1;
                transform: translateX(0);
            }
            to {
                opacity: 0;
                transform: translateX(100%);
            }
        }
    </style>
`
)