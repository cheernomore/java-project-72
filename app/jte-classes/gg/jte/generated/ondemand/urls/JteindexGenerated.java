package gg.jte.generated.ondemand.urls;
import hexlet.code.controller.NamedRoutes;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
@SuppressWarnings("unchecked")
public final class JteindexGenerated {
	public static final String JTE_NAME = "urls/index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,4,4,4,4,6,6,8,8,21,21,23,23,23,25,25,25,25,25,25,25,25,25,26,26,26,30,30,37,37,38,48,48,48,67,70,77,77,80,111,111,111,111,111,4,4,4,4};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, UrlsPage page) {
		jteOutput.writeContent("\n");
		gg.jte.generated.ondemand.layout.JtepageGenerated.render(jteOutput, jteHtmlInterceptor, new gg.jte.html.HtmlContent() {
			public void writeTo(gg.jte.html.HtmlTemplateOutput jteOutput) {
				jteOutput.writeContent("\n    <div class=\"container-fluid\">\n        <div class=\"row\">\n            <div class=\"col-12\">\n                <table class=\"table table-striped-columns\">\n                    <thead>\n                        <tr>\n                            <th scope=\"col\">#</th>\n                            <th scope=\"col\">Urls</th>\n                        </tr>\n                    </thead>\n\n                    <tbody>\n                    ");
				for (Url url : page.getUrls()) {
					jteOutput.writeContent("\n                        <tr>\n                            <th scope=\"row\">");
					jteOutput.setContext("th", null);
					jteOutput.writeUserContent(url.getId());
					jteOutput.writeContent("</th>\n                            <td>\n                                <a");
					var __jte_html_attribute_0 = NamedRoutes.handleUrlPath(url.getId());
					if (gg.jte.runtime.TemplateUtils.isAttributeRendered(__jte_html_attribute_0)) {
						jteOutput.writeContent(" href=\"");
						jteOutput.setContext("a", "href");
						jteOutput.writeUserContent(__jte_html_attribute_0);
						jteOutput.setContext("a", null);
						jteOutput.writeContent("\"");
					}
					jteOutput.writeContent(">\n                                    ");
					jteOutput.setContext("a", null);
					jteOutput.writeUserContent(url.getName());
					jteOutput.writeContent("\n                                </a>\n                            </td>\n                        </tr>\n                    ");
				}
				jteOutput.writeContent("\n                    </tbody>\n                </table>\n            </div>\n        </div>\n    </div>\n\n    ");
				if (page.getFlash() != null) {
					jteOutput.writeContent("\n        ");
					jteOutput.writeContent("\n        <div class=\"position-fixed top-0 end-0 p-3\" style=\"z-index: 1050;\">\n            <div class=\"toast align-items-center text-white bg-success border-0 show\"\n                 role=\"alert\"\n                 aria-live=\"assertive\"\n                 aria-atomic=\"true\"\n                 id=\"flash-toast\">\n                <div class=\"d-flex\">\n                    <div class=\"toast-body\">\n                        <i class=\"bi bi-check-circle-fill me-2\"></i>\n                        ");
					jteOutput.setContext("div", null);
					jteOutput.writeUserContent(page.getFlash());
					jteOutput.writeContent("\n                    </div>\n                    <button type=\"button\"\n                            class=\"btn-close btn-close-white me-2 m-auto\"\n                            data-bs-dismiss=\"toast\"\n                            aria-label=\"Close\"></button>\n                </div>\n            </div>\n        </div>\n\n        <script>\n            document.addEventListener('DOMContentLoaded', function() {\n                const toastElement = document.getElementById('flash-toast');\n                if (toastElement) {\n                    const toast = new bootstrap.Toast(toastElement, {\n                        autohide: true,\n                        delay: 4000\n                    });\n\n                    ");
					jteOutput.writeContent("\n                    toast.show();\n\n                    ");
					jteOutput.writeContent("\n                    setTimeout(function() {\n                        toast.hide();\n                    }, 4000);\n                }\n            });\n        </script>\n    ");
				}
				jteOutput.writeContent("\n\n    <style>\n        ");
				jteOutput.writeContent("\n        .toast {\n            animation: slideInRight 0.3s ease-out;\n        }\n\n        .toast.hiding {\n            animation: slideOutRight 0.3s ease-out;\n        }\n\n        @keyframes slideInRight {\n            from {\n                opacity: 0;\n                transform: translateX(100%);\n            }\n            to {\n                opacity: 1;\n                transform: translateX(0);\n            }\n        }\n\n        @keyframes slideOutRight {\n            from {\n                opacity: 1;\n                transform: translateX(0);\n            }\n            to {\n                opacity: 0;\n                transform: translateX(100%);\n            }\n        }\n    </style>\n");
			}
		});
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		UrlsPage page = (UrlsPage)params.get("page");
		render(jteOutput, jteHtmlInterceptor, page);
	}
}
