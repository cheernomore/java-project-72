@import hexlet.code.controller.NamedRoutes
@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.model.Url

@param UrlsPage page

@template.layout.page(
content = @`
    <div class="container-fluid">
        <div class="row">
            <div class="col-12">
                <table class="table table-striped-columns">
                    <thead>
                        <tr>
                            <th scope="col">#</th>
                            <th scope="col">Urls</th>
                        </tr>
                    </thead>

                    <tbody>
                    @for(Url url : page.getUrls())
                        <tr>
                            <th scope="row">${url.getId()}</th>
                            <td>
                                <a href="${NamedRoutes.handleUrlPath(url.getId())}">
                                    ${url.getName()}
                                </a>
                            </td>
                        </tr>
                    @endfor
                    </tbody>
                </table>
            </div>
        </div>
    </div>

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