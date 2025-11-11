@import hexlet.code.dto.urls.UrlBuildPage

@param UrlBuildPage page

@template.layout.page(
content = @`
    <div class="hero-section mb-5">
        <h1 class="display-4 fw-bold mb-3">SEO Анализатор сайтов</h1>
        <p class="lead">Проверяйте доступность сайтов и анализируйте их SEO-параметры</p>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h2 class="card-title mb-4">
                        <i class="bi bi-plus-circle text-primary me-2"></i>
                        Добавить новый сайт
                    </h2>

                    <form action="/urls" method="post" id="addUrlForm">
                        <div class="mb-4">
                            <label for="urlId" class="form-label fw-semibold">
                                URL адрес сайта <span class="text-danger">*</span>
                            </label>
                            <div class="input-group">
                                <span class="input-group-text">
                                    <i class="bi bi-globe"></i>
                                </span>
                                <input
                                    name="url"
                                    type="url"
                                    class="form-control form-control-lg"
                                    value="${page.getName() != null ? page.getName() : ""}"
                                    id="urlId"
                                    placeholder="https://example.com"
                                    aria-describedby="urlHelp"
                                    required
                                    pattern="https?://.*"
                                    title="Введите корректный URL, начинающийся с http:// или https://">
                            </div>
                            <div id="urlHelp" class="form-text mt-2">
                                <i class="bi bi-info-circle me-1"></i>
                                Введите полный URL адрес включая протокол (http:// или https://)
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-primary btn-lg">
                                <i class="bi bi-search me-2"></i>
                                Проверить сайт
                            </button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="mt-4">
                <div class="card bg-light border-0">
                    <div class="card-body">
                        <h5 class="card-title">
                            <i class="bi bi-lightbulb text-warning me-2"></i>
                            Как это работает?
                        </h5>
                        <ol class="mb-0">
                            <li>Введите URL сайта, который хотите проверить</li>
                            <li>Система выполнит проверку доступности сайта</li>
                            <li>Получите информацию о SEO-параметрах: title, h1, description</li>
                            <li>Отслеживайте историю всех проверок</li>
                        </ol>
                    </div>
                </div>
            </div>
        </div>
    </div>

    @if(page.getErrors() != null && !page.getErrors().isEmpty())
        <!-- Флеш-уведомление об ошибке справа сверху -->
        <div class="position-fixed top-0 end-0 p-3" style="z-index: 1050;">
            <div class="toast align-items-center text-white bg-danger border-0 show"
                 role="alert"
                 aria-live="assertive"
                 aria-atomic="true"
                 id="error-toast">
                <div class="d-flex">
                    <div class="toast-body">
                        <i class="bi bi-exclamation-triangle-fill me-2"></i>
                        <strong>Ошибка!</strong>
                        @for(var validator : page.getErrors().values())
                            @for(var error : validator)
                                <div class="mt-1">${error.getMessage()}</div>
                            @endfor
                        @endfor
                    </div>
                    <button type="button"
                            class="btn-close btn-close-white me-2 m-auto"
                            data-bs-dismiss="toast"
                            aria-label="Close"></button>
                </div>
            </div>
        </div>

        <!-- SVG иконки для ошибок -->
        <svg xmlns="http://www.w3.org/2000/svg" style="display: none;">
            <symbol id="exclamation-triangle-fill" fill="currentColor" viewBox="0 0 16 16">
                <path d="M8.982 1.566a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566zM8 5c.535 0 .954.462.9.995l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995A.905.905 0 0 1 8 5zm.002 6a1 1 0 1 1 0 2 1 1 0 0 1 0-2z"/>
            </symbol>
        </svg>

        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const toastElement = document.getElementById('error-toast');
                if (toastElement) {
                    const toast = new bootstrap.Toast(toastElement, {
                        autohide: true,
                        delay: 6000  // Увеличиваем время для ошибок
                    });

                    // Показываем toast
                    toast.show();

                    // Автоматически скрываем через 6 секунд
                    setTimeout(function() {
                        toast.hide();
                    }, 6000);
                }
            });
        </script>

        <style>
            /* Дополнительные стили для плавной анимации */
            .toast {
                animation: slideInRight 0.3s ease-out;
                max-width: 400px; /* Увеличиваем ширину для ошибок */
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
    @endif
`
)