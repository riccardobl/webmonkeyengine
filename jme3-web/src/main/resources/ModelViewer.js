window.ModelViewer = {
    show: function (baseUrl, params = {}, iframe = undefined) {
        if (!iframe) {
            iframe = document.createElement('iframe');
        }
        const url = new URL("embed.html", baseUrl);
        for (const [k, v] of Object.entries(params)) {
            url.searchParams.set(k, JSON.stringify(v));
        }

        iframe.src = url;

    }
}