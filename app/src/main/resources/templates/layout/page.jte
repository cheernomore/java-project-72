@import gg.jte.Content

@param Content content

<!doctype html>
<html lang="ru">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1">
        <title>SEO Анализатор - Проверка сайтов</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <style>
            :root {
                --primary-color: #0d6efd;
                --secondary-color: #6c757d;
                --success-color: #198754;
                --danger-color: #dc3545;
                --warning-color: #ffc107;
                --info-color: #0dcaf0;
            }

            body {
                min-height: 100vh;
                display: flex;
                flex-direction: column;
            }

            main {
                flex: 1;
            }

            .navbar-brand {
                font-weight: bold;
                display: flex;
                align-items: center;
                gap: 0.5rem;
            }

            .navbar {
                box-shadow: 0 2px 4px rgba(0,0,0,.08);
            }

            footer {
                background-color: #f8f9fa;
                padding: 1.5rem 0;
                margin-top: 3rem;
                border-top: 1px solid #dee2e6;
            }

            .btn-primary {
                transition: transform 0.2s;
            }

            .btn-primary:hover {
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(13, 110, 253, 0.3);
            }

            .table-responsive {
                border-radius: 0.375rem;
                overflow: hidden;
                box-shadow: 0 0 20px rgba(0,0,0,.05);
            }

            .hero-section {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                color: white;
                padding: 3rem 1rem;
                border-radius: 0.5rem;
                margin-bottom: 2rem;
                text-align: center;
            }
        </style>
    </head>
    <body>
        <header>
            <nav class="navbar navbar-expand-lg navbar-light bg-white">
                <div class="container">
                    <a class="navbar-brand" href="/">
                        <i class="bi bi-graph-up-arrow text-primary"></i>
                        SEO Анализатор
                    </a>
                    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" aria-controls="navbarNav" aria-expanded="false" aria-label="Переключить навигацию">
                        <span class="navbar-toggler-icon"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarNav">
                        <ul class="navbar-nav ms-auto">
                            <li class="nav-item">
                                <a class="nav-link" href="/">
                                    <i class="bi bi-house-door me-1"></i>
                                    Главная
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/urls">
                                    <i class="bi bi-list-ul me-1"></i>
                                    Сайты
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/urls/build">
                                    <i class="bi bi-plus-circle me-1"></i>
                                    Добавить URL
                                </a>
                            </li>
                        </ul>
                    </div>
                </div>
            </nav>
        </header>

        <main class="container my-5">
            ${content}
        </main>

        <footer>
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <p class="mb-0 text-muted">© 2024 SEO Анализатор. Все права защищены.</p>
                    </div>
                    <div class="col-md-6 text-md-end">
                        <a href="https://github.com" target="_blank" class="text-muted me-3">
                            <i class="bi bi-github"></i> GitHub
                        </a>
                        <a href="#" class="text-muted">
                            <i class="bi bi-question-circle"></i> Помощь
                        </a>
                    </div>
                </div>
            </div>
        </footer>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>