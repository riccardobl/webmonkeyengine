if (!window.jme) {
    window.jme = {};
}

if (!window.jme.getEndian) window.jme.getEndian = function () {
    var arrayBuffer = new ArrayBuffer(2);
    var uint8Array = new Uint8Array(arrayBuffer);
    var uint16array = new Uint16Array(arrayBuffer);
    uint8Array[0] = 0xAA;
    uint8Array[1] = 0xBB;
    if (uint16array[0] === 0xBBAA) return "little";
    if (uint16array[0] === 0xAABB) return "big";
    else return "unknown";
}

if (!window.jme.canvasFitParent) window.jme.canvasFitParent = function (canvas) {
    const parent = canvas.parentElement;
    canvas.width = parent.clientWidth;
    canvas.height = parent.clientHeight;
    canvas.setAttribute("width", parent.clientWidth);
    canvas.setAttribute("height", parent.clientHeight);

}

if (!window.jme.getPixelDeltaScroll) window.jme.getPixelDeltaScroll = function (deltaValue, deltaMode) {
    let PIXELS_PER_LINE = window.PIXELS_PER_LINE;
    if (typeof PIXELS_PER_LINE === "undefined") {
        const testElement = document.createElement("div");
        testElement.style.cssText = "position: absolute; visibility: hidden; font-size: 1em; line-height: 1em; padding: 0; margin: 0; border: 0;";
        testElement.textContent = "Test";

        document.body.appendChild(testElement);

        PIXELS_PER_LINE = testElement.offsetHeight;

        document.body.removeChild(testElement);
        window.PIXELS_PER_LINE = PIXELS_PER_LINE;
    }

    if (deltaMode === 0) {
        return deltaValue;
    } else if (deltaMode === 1) {
        return deltaValue * PIXELS_PER_LINE;
    } else {
        const viewportHeight = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);
        const estimatedLinesPerPage = Math.floor(viewportHeight / averageLineHeight);
        return deltaValue * PIXELS_PER_LINE * estimatedLinesPerPage;
    }

}

if (!window.jme.increaseProgress) window.jme.increaseProgress = function (progress, message = "") {
    let currentProgress = window.jme.progress || 0;
    currentProgress += progress;
    if (currentProgress >= 0.99) {
        currentProgress = 0;
    }
    window.jme.setProgress(currentProgress, message);
    window.jme.progress = currentProgress;
    if (window.jme.increaseProgressLoop) {
        clearInterval(window.jme.increaseProgressLoop);
    }
    window.jme.increaseProgressLoop = setInterval(() => {
        window.jme.increaseProgress(0.01, message);        
    },100);
}



if (!window.jme.setProgress) window.jme.setProgress = function (progress, message = "") {
    if (window.jme.increaseProgressLoop) {
        clearInterval(window.jme.increaseProgressLoop);
        window.jme.increaseProgressLoop=undefined;
    }
    window.jme.progress = progress
    let jmeProgressEl = document.querySelector("#jmeProgress");
    if (!jmeProgressEl) {
        jmeProgressEl = document.createElement("div");
        jmeProgressEl.id = "jmeProgress";
        document.body.appendChild(jmeProgressEl);

        const poweredByEl = document.createElement("div");
        poweredByEl.id = "poweredBy";
        jmeProgressEl.appendChild(poweredByEl);

        const poweredByLogoEl = document.createElement("div");
        poweredByLogoEl.classList.add("img");
        // poweredByLogoEl.src = "jmelogo.webp";
        poweredByEl.appendChild(poweredByLogoEl);

        // const poweredByTextEl = document.createElement("span");
        // poweredByTextEl.innerText = "Powered by jMonkeyEngine";
        // poweredByEl.appendChild(poweredByTextEl);


        const jmeProgressBarEl = document.createElement("progress");
        jmeProgressEl.appendChild(jmeProgressBarEl);

        const jmeProgressMessageEl = document.createElement("span");
        jmeProgressMessageEl.id = "jmeProgressMessage";
        jmeProgressEl.appendChild(jmeProgressMessageEl);
    }
    if (progress >= 1) {
        window.jme.canvas.style.opacity = 1;
        jmeProgressEl.style.display = "none";
    } else {
        window.jme.canvas.style.opacity = 0;
        jmeProgressEl.style.display = "flex";



        const jmeProgressBarEl = jmeProgressEl.querySelector("progress");
        if (!jmeProgressBarEl) {

        }
        jmeProgressBarEl.max = 100;
        jmeProgressBarEl.value = Math.floor(progress * 100);

        const jmeProgressMessageEl = jmeProgressEl.querySelector("#jmeProgressMessage");
        jmeProgressMessageEl.innerText = message;
    }
}


if (!window.jme.resources) window.jme.resources = {
    id: "jmeApp",
    cacheDir: undefined,
    prefetchFilters: undefined,
    index: undefined,
    preloadPos: 0,

    _fullPath(path) {
        if (path.startsWith("/")) {
            path=path.substring(1);
        }
        let basePath = window.location.origin + window.location.pathname;
        if (basePath.endsWith("/")) {
            basePath = basePath.substring(0, basePath.length - 1);
        }
        return basePath + "/" + encodeURI(path);
    },


    /**
     * Initialize the resource loader
     */
    init: async function () {

        try {
            if (!this.index) {
                this.index = {};
                const lines = (await fetch(this._fullPath( "resourcesIndex.txt")).then(r => r.text())).split("\n");
                for (const line of lines) {

                    let [hash, ...path] = line.split(" ");
                    path = path.join(" ");

                    this.index[path.trim()] = hash.trim();
                }
                console.log("Loaded " + Object.keys(this.index).length + " hashes.");
            }
        } catch (e) {
            console.warn("Error loading resources index", e);
        }
        try {
            if (!this.prefetchFilters) {
                try {
                    this.prefetchFilters = [];
                    const prefetchFiltersLines = (await fetch(this._fullPath("resourcesPrefetchFilters.txt")).then(r => r.text())).split("\n");
                    for (let line of prefetchFiltersLines) {
                        line = line.trim();
                        if (line) this.prefetchFilters.push(new RegExp(line));
                    }

                } catch (e) {
                    console.error(e);
                }
                console.log("Loaded " + this.prefetchFilters.length + " preload filters.");
            }
        } catch (e) {
            console.error("Error loading resources preload filters", e);
        }
        try {
            if (!this.cacheDir) {
                const opfsRoot = await navigator.storage.getDirectory();
                if (!opfsRoot) {
                    console.warn("Origin private file system not available.");
                } else {
                    this.cacheDir = await opfsRoot.getDirectoryHandle(this.id, { create: true });
                    console.log("Loaded cache dir", this.cacheDir);
                }
            }
        } catch (e) {
            console.error("Error loading cache dir", e);
        }

    },


    prefetch: async function (res) {
        try {
            await this.init();
            const entries = Object.entries(this.index);
            setTimeout(async () => {
                this.preloadPos = entries.length;
                res(false);
            }, 60000);
            try {
                const nextToPreload = entries[this.preloadPos];
                if (nextToPreload) {
                    let path;
                    let hash;
                    try {
                        [path, hash] = nextToPreload;
                        for (const preloadFilter of this.prefetchFilters) {
                            if (preloadFilter.test(path)) {
                                if (!(await this.getCachedBuffer(path))) {
                                    console.log("Download " + path);
                                    window.jme.setProgress(this.preloadPos / entries.length, "Downloading resources (" + path + ")");
                                    await this.getEntry(path);
                                } else {
                                    window.jme.setProgress(this.preloadPos / entries.length, "Checking resources (" + path + ")");

                                    // console.log("Already downloaded " + path);
                                }
                                break;
                            }
                        }
                    } catch (e) {
                        console.error("Can't preload " + path, e);
                    }
                }

            } catch (e) {
                console.error("Error preloading resources", e);
            }
            this.preloadPos++;
            if (this.preloadPos < entries.length) {
                setTimeout(() => this.prefetch(res), 2);
            } else {
                console.log("Resources downloaded!");
                res(true);
                window.jme.setProgress(1, "Resources downloaded!");
            }

        } catch (e0) {
            console.error(e0);
            res(false);
        }
    },


    /**
     * Remove something from local cache
     * @param {string} name 
     * @returns 
     */
    unsetCached: async function (name) {
        await this.init();
        if (!this.cacheDir) {
            return;
        }
        const cacheName = this._cacheName(name);

        try {
            await this.cacheDir.removeEntry(cacheName);
        } catch (e) {
            console.error(e);
        }

        try {
            await this.cacheDir.removeEntry(cacheName + "_hash");
        } catch (e) {
            console.error(e);
        }

    },

    _cacheName: function (name) {
        const cacheName = name.replace(/[^a-zA-Z0-9]/g, "_");
        return cacheName;
    },
    /**
     * Stores a buffer in the local cache
     * @param {string} name 
     * @param {ArrayBuffer} buffer 
     */
    setCachedBuffer: async function (name, buffer) {
        await this.init();
        if (!this.cacheDir) {
            console.log("Cache dir not available. Disable caching.");
            return;
        }
        if (!this.index[name]) {
            return;
        }
        const cacheName = this._cacheName(name);

        // console.log("Write ", cacheName)
        const file = await this.cacheDir.getFileHandle(cacheName, { create: true });
        const fileContent = await file.createWritable();
        await fileContent.write(buffer);
        await fileContent.close();

        const hash = this.index[name];
        // console.log("Write ", cacheName+"_hash", hash)
        const hashBuffer = new TextEncoder("utf-8").encode(hash);
        const cachedHashFile = await this.cacheDir.getFileHandle(cacheName + "_hash", { create: true });
        const cachedHashFileContent = await cachedHashFile.createWritable();
        await cachedHashFileContent.write(hashBuffer);
        await cachedHashFileContent.close();

        console.log("Cached " + name);
    },

    existsInCache: async function (name) {
        try {
            await this.init();
            if (!this.cacheDir) {
                console.log("Cache dir not available. Disable caching.");
                return false;
            }
            const cacheName = this._cacheName(name);
            const cachedHashFile = await this.cacheDir.getFileHandle(cacheName, { create: false });
            return cachedHashFile !== undefined;
        } catch (e) {
            // console.log(e,name);
            return false;
        }

    },
    /**
     * Get a buffer from the local cache
     * @param {string} name 
     * @returns {ArrayBuffer}
     */
    getCachedBuffer: async function (name) {
        await this.init();
        if (!this.cacheDir) {
            console.log("Cache dir not available. Disable caching.");
            return undefined;
        }
        const cacheName = this._cacheName(name);

        if (!(await this.existsInCache(cacheName + "_hash"))) return undefined;
        const cachedHashFile = await this.cacheDir.getFileHandle(cacheName + "_hash", { create: false });
        const cachedHashFileContent = await cachedHashFile.getFile();

        const cachedHashBuffer = await cachedHashFileContent.arrayBuffer();
        const cachedHash = new TextDecoder("utf-8").decode(cachedHashBuffer);
        if (cachedHash !== this.index[name]) {
            console.warn("Cached hash doesn't match the hash. Redownload resource.");
            this.unsetCached(name);
            return undefined;
        }

        if (!(await this.existsInCache(cacheName))) return undefined;
        const file = await this.cacheDir.getFileHandle(cacheName, { create: false });

        const fileContent = await file.getFile();

        const buffer = await fileContent.arrayBuffer();
        return buffer;
    },

    /**
     * List resources
     * @returns {Array<string>} the index of resources
     */
    getIndex: async function (callback) {
        await this.init();
        let index = Object.keys(this.index);
        if (callback) {
            callback(index);
        }
        return index;
    },

    /**
     * Get a resource and store it in the local cache if it's not there.
     * @param {string} name
     * @returns {ArrayBuffer} the resource
     */
    getEntry: async function (name, callback) {
        await this.init();
        let entry = await this.getCachedBuffer(name);
        if (!entry) {
            entry = await fetch(this._fullPath(name)).then(r => r.arrayBuffer());
            await this.setCachedBuffer(name, entry);
        }
        const int8Array = new Int8Array(entry);
        if (callback) {
            callback(int8Array);
        }
        return int8Array;
    }


}

if (!window.jme.postMessage) window.jme.postMessage = function (message) {
    if (!window.jme.postedMessages) {
        window.jme.postedMessages = [];
    }
    if (typeof message != "string") message = JSON.stringify(message);
    window.jme.postedMessages.push(message);   
};


if (!window.jme.getPostedMessages) window.jme.getPostedMessages = function () {
    if (!window.jme.postedMessages) {
        window.jme.postedMessages = [];
    }    
    const out = window.jme.postedMessages;   
    if(out.length>0) window.jme.postedMessages = [];
    return out;
}

if (!window.expo) window.expo = {};

window.expo.checkUrl = async function (url, type="model") {
    if (!url) return false;
    // protect lfi
    if (url.indexOf("..") >= 0) {
        return false;
    }

    let whiteListedOrigins = window.expo.whiteListedOrigins;
    if (!whiteListedOrigins) {
        try {
            whiteListedOrigins = await fetch("/whitelistedOrigins.json").then(r => r.json());
            window.expo.whiteListedOrigins = whiteListedOrigins;
        } catch (e) {
            console.error(e);
            whiteListedOrigins = {};            
        }
    }


    let urlObj;
    try {
        urlObj = new URL(url);  
    } catch (e) {
        urlObj = new URL(url, window.location.href);    
    }

    
    // allow only http and https
    if (urlObj.protocol !== "http:" && urlObj.protocol !== "https:") {
        console.warn("Invalid protocol: ", urlObj.protocol, " in ", url);
        return false;
    }
    
    const whiteList = whiteListedOrigins[type];

    // protect xss, allow only relative and whitelisted urls
    if (urlObj.origin !== window.location.origin) {
        let valid = false;
        for (let origin of whiteList) {
            const operator = origin.charAt(0);
            origin = origin.substr(1);
            if (operator == "*") {
                valid = true;
                break;
            }else if (operator == "=") {
                if (urlObj.toString() == origin) {
                    valid = true;
                    break;
                }
            } else {
                if (urlObj.toString().startsWith(origin)) {
                    valid = true;
                    break;
                }
            }          
        }
        
        if (!valid) {
            console.warn("Invalid origin in ", url);
            return false;
        }
    }

    return true;
    
    
}

if (!window.jme.start) window.jme.start = async function (canvas, message) {
    if (canvas) {
        window.jme.canvas = canvas;
        window.jme.canvasFitParent(canvas);
    }
    const scriptsToLoad = [
        "stb/stb_image_load.js",
        "physics/ammo.js",
        "jmeapp.js"
    ];

    const loadScriptAndWait = async function (script, progress) {
        console.info("Loading script", script);
        window.jme.setProgress(progress, "Loading " + script);   
        const scriptElement = document.createElement("script");
        scriptElement.src = script;
        document.head.appendChild(scriptElement);
        return new Promise((res, rej) => {
            scriptElement.onload = () => {
                res();
            }
            scriptElement.onerror = (e) => {
                rej(e);
            }
        });
    };

    const loadScripts = async function () {
        let progress = 0;
        const step = 1.0 / scriptsToLoad.length;
        for (let i = 0; i < scriptsToLoad.length; i++) {
            await loadScriptAndWait(scriptsToLoad[i], progress);
            progress += step;
        }
    }

    await loadScripts();
    window.jme.setProgress(1.0, "Starting");
    if (message) {
        window.jme.postMessage(message);
    }
    main();


}



if (!window.expo.setLogo) window.expo.setLogo = async function (logoUrl) {
    if (!await window.expo.checkUrl(logoUrl,"logo")) {
        console.error("Invalid logo url: ", msg.logo);
        return;
    }

    document.documentElement.style.setProperty('--logo', `url(${logoUrl})`);


}

if (!window.expo.setPalette) window.expo.setPalette = async function (palette) {

    if (!Array.isArray(palette)) {
        palette = [palette];
    }

    for (let i = 0; i < palette.length; i++) {
        const key = "--color" + i;
        const value = palette[i];
        if (!value.match(/^#[0-9A-F]{6}$/i)) {
            console.error("Invalid color: ", value);
            return;
        }
        document.documentElement.style.setProperty(key, value);
    }



}

if (!window.expo.applyCorsProxy) window.expo.applyCorsProxy = async function (url, cors) {
    if(!cors) return url;
    if (!await window.expo.checkUrl(cors,"corsProxy")) {
        console.error("Invalid cors url: ", cors);
        return url;
    }
    const isAbsolute = url.startsWith("http://") || url.startsWith("https://");
    if (isAbsolute) {
        return cors.replace("%URL%", url);
    } else {
        return url;
    }
}


if (!window.expo.setEnvironment) window.expo.setEnvironment = async function (msg) {

    if (!await window.expo.checkUrl(msg.environment,"environment")) {
        console.error("Invalid environment url: ", msg.environment);
        return;
    }

    msg.environment = await window.expo.applyCorsProxy(msg.environment, msg.corsProxy);



    if (msg.logo) window.expo.setLogo(msg.logo);
    if (msg.palette) window.expo.setPalette(msg.palette);


    msg.type = "setEnvironment";


    window.jme.postMessage(msg);

}

if (!window.jme.loadWebGLdebug) window.jme.loadWebGLdebug = async function (callback) {
    const loadScript=(scriptPath,onLoad)=>{
        return new Promise((res, rej) => {
            let scriptElement = document.head.querySelector(`script[src="${scriptPath}"]`);
            if (scriptElement) {
                res();
                return;
            }
        
            scriptElement = document.createElement("script");
            scriptElement.setAttribute("src", scriptPath);
            scriptElement.addEventListener("load", async () => {
                if (onLoad) await onLoad();
                res();
                
            });
            document.head.appendChild(scriptElement);
        });
    }
    await loadScript("./debug/webgl-lint.js"); 
    if (!window.XRWebGLBinding) {
        class FakeXRWebGLBinding{ }
        window.XRWebGLBinding = FakeXRWebGLBinding;
    }
    if (!window.XRWebGLLayer) {
        class FakeXRWebGLLayer { }
        window.XRWebGLLayer = FakeXRWebGLLayer;
    }
    await loadScript("./debug/spector.js", async () => {
        const spector = new SPECTOR.Spector();
        spector.displayUI();
        spector.spyCanvases();
        // spector.captureCanvas(window.jme.canvas);
    });
    callback();
}

if (!window.expo.setModel) window.expo.setModel = async function (
    msg
) {

    if (!await window.expo.checkUrl(msg.model,"model")) {
        console.error("Invalid environment url: ", msg.model);
        return;
    }

    if (msg.material&&!await window.expo.checkUrl(msg.material, "material")) {
        console.error("Invalid material url: ", msg.material);
        return;
    }

    if (msg.assetRoots) {
        for (let i = 0; i < msg.assetRoots.length; i++) {
            if (!await window.expo.checkUrl(msg.assetRoots[i], "assetRoot")) {
                console.error("Invalid assetRoot url: ", msg.assetRoots[i]);
                return;
            }
        }
    }

    msg.model = await window.expo.applyCorsProxy(msg.model, msg.corsProxy);
    if (msg.material) msg.material = await window.expo.applyCorsProxy(msg.material, msg.corsProxy);

    if (msg.assetRoots) {
        for (let i = 0; i < msg.assetRoots.length; i++) {
            msg.assetRoots[i] = await window.expo.applyCorsProxy(msg.assetRoots[i], msg.corsProxy);
        }
    }

    if (msg.logo) window.expo.setLogo(msg.logo);
    if (msg.palette) window.expo.setPalette(msg.palette);

    msg.type = "setModel";


    window.jme.postMessage(msg);

}
