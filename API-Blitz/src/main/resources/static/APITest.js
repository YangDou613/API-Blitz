let response = null;
let selectedAPI = null;
let method = null;
let testDateTime = null;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/signUpIn";

} else {

    document.addEventListener("DOMContentLoaded", function () {

        const currentPagePath = window.location.pathname;

        const sidebarLinks = document.querySelectorAll('.sidebar-link');

        sidebarLinks.forEach(link => {

            const linkPath = link.getAttribute('href');

            if (linkPath === currentPagePath) {
                link.classList.add('active');
            }
        });

        const socket = new SockJS('https://apiblitz.site/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {

            stompClient.subscribe('/topic/APITest', function (message) {
                getResult();
            });
        });
    });

    const body = document.getElementById("body");
    body.addEventListener("input", () => {
        body.value = JSON.stringify(JSON.parse(body.value), null, 4);
    })

    function updateParamsFromUrl() {
        let url = document.getElementById("url");
        let allParamsKeysInput = document.querySelectorAll(".paramsKey");
        let allParamsValueInput = document.querySelectorAll(".paramsValue");

        let urlParams = url.value.split("?")[1] || "";
        let paramsArray = urlParams.split("&");

        paramsArray.forEach((param, index) => {
            let keyValue = param.split("=");
            let key = keyValue[0];
            let value = keyValue[1] || '';

            if (index >= allParamsKeysInput.length) {
                addQueryParamsInput();
                allParamsKeysInput = document.querySelectorAll(".paramsKey");
                allParamsValueInput = document.querySelectorAll(".paramsValue");
            }

            allParamsKeysInput[index].value = key;

            if (paramsArray.length < allParamsKeysInput.length) {
                allParamsKeysInput[index + 1].value = '';
                allParamsValueInput[index + 1].value = '';
            }

            if (!param.includes("=")) {
                allParamsValueInput[index].value = '';
            } else {
                allParamsValueInput[index].value = value;
            }
        });

        if (paramsArray.length >= allParamsKeysInput.length) {
            addQueryParamsInput();
            allParamsKeysInput = document.querySelectorAll(".paramsKey");
            allParamsValueInput = document.querySelectorAll(".paramsValue");
        }
        let equalsArray = urlParams.split("=");
        if (equalsArray.length - 1 > paramsArray.length) {
            updateUrlFromParams();
        }
    }

    function updateUrlFromParams() {
        let url = document.getElementById("url");
        let allParamsKeysInput = document.querySelectorAll(".paramsKey");
        let allParamsValueInput = document.querySelectorAll(".paramsValue");

        let queryString = "";
        let count = 0;
        for (let i = 0; i < allParamsKeysInput.length; i++) {
            let paramsKeysInputValue = allParamsKeysInput[i].value;
            let paramsValueInputValue = allParamsValueInput[i].value;
            if (paramsKeysInputValue) {
                queryString += paramsKeysInputValue;
                if (paramsValueInputValue) {
                    queryString += "=" + paramsValueInputValue;
                }
                queryString += "&";
            } else {
                if (paramsValueInputValue) {
                    queryString += "=" + paramsValueInputValue;
                }
            }
            count += 1;
        }
        if (queryString.charAt(queryString.length - 1) === "&") {
            queryString = queryString.slice(0, -1);
        }
        url.value = url.value.split("?")[0] + (queryString ? "?" + queryString : "");
    }

    document.getElementById("url").addEventListener("input", function (event) {
        updateParamsFromUrl();
    });

    document.getElementById("queryParams").addEventListener("input", function (event) {
        updateUrlFromParams();
    });

    document.getElementById('api-form').addEventListener('submit', function (event) {
        event.preventDefault();

        clear();

        document.getElementById('loading').style.display = 'block';

        let formData = new FormData(this);
        method = formData.get("method");

        let xhr = new XMLHttpRequest();
        xhr.open('POST', '/api/1.0/APITest');

        xhr.onload = function () {

            if (xhr.status === 200) {
                testDateTime = JSON.parse(xhr.responseText);
            }
        };
        xhr.setRequestHeader("Authorization", `Bearer ${token}`);
        xhr.send(formData);
    });


    function getResult() {

        fetch('/api/1.0/APITest/testResult?testDateTime=' + testDateTime, {
            method: 'GET',
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                response = data;
                document.getElementById('loading').style.display = 'none';
                displayResponse();
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function clear() {
        let statusCode = document.getElementById('status-code');
        statusCode.innerHTML = '';

        let responseTime = document.getElementById('response-time');
        responseTime.innerHTML = '';

        let responseSize = document.getElementById('response-size');
        responseSize.innerHTML = '';

        let headerTab = document.getElementById("show-response-header");
        headerTab.style.display = "none";

        let bodyTab = document.getElementById("show-response-body");
        bodyTab.style.display = "none";

        let responseBody = document.getElementById('response');
        responseBody.innerHTML = '';
    }

    function displayResponse() {

        let responseHeaders = JSON.parse(response["responseHeaders"]);

        let statusCode = document.getElementById('status-code');
        statusCode.innerHTML = '';
        let responseCodeHtml = `Status Code: ${response["statusCode"]}`;
        statusCode.insertAdjacentHTML('beforeend', responseCodeHtml);

        let responseTime = document.getElementById('response-time');
        responseTime.innerHTML = '';
        let responseTimeHtml = `Response Time: ${responseHeaders['Execution-Duration']} ms`;
        responseTime.insertAdjacentHTML('beforeend', responseTimeHtml);

        let responseSize = document.getElementById('response-size');
        responseSize.innerHTML = '';
        let responseSizeHtml = `Response Size: ${responseHeaders["Content-Length"]} B`;
        responseSize.insertAdjacentHTML('beforeend', responseSizeHtml);

        let headerTab = document.getElementById("show-response-header");
        headerTab.style.display = "block";
        let bodyTab = document.getElementById("show-response-body");
        bodyTab.style.display = "block";

        let responseBody = document.getElementById('response');
        responseBody.innerHTML = '';

        let contentType = getContentType();

        if (response["responseBody"] == null) {
            responseBody.innerHTML = 'There is no response body.';
        } else if (contentType === "image") {
            let responseBodyText = JSON.parse(response["responseBody"]);
            const imageURL = btoa(responseBodyText);
            let img = document.createElement("img");
            img.src = `data:image/bmp;base64, ${imageURL}`;
            responseBody.insertAdjacentHTML("beforeend", "<br>");
            responseBody.appendChild(img);
        } else {
            let responseBodyText = formatJSON(JSON.parse(response["responseBody"]));
            let responseBodyHtml = `<pre><code>${responseBodyText}</code></pre>`;
            responseBody.insertAdjacentHTML('beforeend', responseBodyHtml);
        }
    }

    function getContentType() {
        let responseHeaders = JSON.parse(response["responseHeaders"]);
        let contentType = responseHeaders["Content-Type"];
        return contentType[0].split("/")[0];
    }

    function displayHeaders() {

        let responseHeaders = JSON.parse(response["responseHeaders"]);

        let statusCode = document.getElementById('status-code');
        statusCode.innerHTML = '';
        let responseCodeHtml = `Status Code: ${response["statusCode"]}`;
        statusCode.insertAdjacentHTML('beforeend', responseCodeHtml);

        let responseTime = document.getElementById('response-time');
        responseTime.innerHTML = '';
        let responseTimeHtml = `Response Time: ${responseHeaders['Execution-Duration']} ms`;
        responseTime.insertAdjacentHTML('beforeend', responseTimeHtml);

        let responseSize = document.getElementById('response-size');
        responseSize.innerHTML = '';
        let responseSizeHtml = `Response Size: ${responseHeaders["Content-Length"]} B`;
        responseSize.insertAdjacentHTML('beforeend', responseSizeHtml);

        let responseHeader = document.getElementById('response');
        responseHeader.innerHTML = '';

        const headersTable = document.createElement('table');
        headersTable.classList.add('headers-table');

        const headersMap = objectToMap(responseHeaders);

        let keyHtml;
        let valueHtml;
        headersMap.forEach((value, key) => {
            const tr = document.createElement("tr");
            if (key === "Content-Length") {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value} B</td>`;
            } else if (key === "Execution-Duration") {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value} ms</td>`;
            } else {
                keyHtml = `<td id="headers-table-title">${key}</td>`;
                valueHtml = `<td>${value}</td>`;
            }
            tr.insertAdjacentHTML('beforeend', keyHtml);
            tr.insertAdjacentHTML('beforeend', valueHtml);
            headersTable.appendChild(tr);
        });
        responseHeader.appendChild(headersTable);
    }

    document.addEventListener('DOMContentLoaded', function () {
        document.getElementById('show-response-body').addEventListener('click', displayResponse);
        document.getElementById('show-response-header').addEventListener('click', displayHeaders);
    });

    function formatJSON(json) {
        return JSON.stringify(json, null, 4)
    }

    let urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("selectedAPI")) {
        let api = urlParams.get("selectedAPI");
        selectedAPI = JSON.parse(api);
        insertData(selectedAPI);
    }

    function insertData(selectedAPI) {

        const form = document.getElementById("api-form");
        form.style.display = "block";

        // Method
        document.getElementById('method').value = selectedAPI["method"];

        // API url
        document.getElementById('url').value = selectedAPI["apiurl"];

        // Query Params
        const paramsKeyInputs = document.querySelectorAll('#queryParams input[name="paramsKey"]');
        const paramsValueInputs = document.querySelectorAll('#queryParams input[name="paramsValue"]');

        paramsKeyInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

        paramsValueInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

        const queryParams = document.getElementById("queryParams");
        if (selectedAPI["queryParams"] != null) {
            const queryParamsObj = JSON.parse(selectedAPI["queryParams"]);
            const queryParamsMap = objectToMap(queryParamsObj);
            let isFirstIteration = true;
            queryParamsMap.forEach((value, key) => {
                if (isFirstIteration) {
                    document.getElementById('paramsKey').value = key;
                    document.getElementById('paramsValue').value = value;
                    isFirstIteration = false;
                } else {
                    document.getElementById("queryParamsButton").style.display = "none";
                    let paramKeyHtml = `<input type="text" class="dynamic-input paramsKey" name="paramsKey" placeholder="Key" value=${key}>`;
                    let paramValueHtml = `<input type="text" class="dynamic-input paramsValue" name="paramsValue" placeholder="Value" value=${value}><br>`;
                    queryParams.insertAdjacentHTML("beforeend", paramKeyHtml)
                    queryParams.insertAdjacentHTML("beforeend", paramValueHtml)
                }
            });
        }

        document.getElementById('authorizationKey').value = '';
        document.getElementById('authorizationValue').value = '';

        const headersKeyInputs = document.querySelectorAll('#headers input[name="headersKey"]');
        const headersValueInputs = document.querySelectorAll('#headers input[name="headersValue"]');

        headersKeyInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

        headersValueInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

        if (selectedAPI["headers"] != null) {
            const headersObj = JSON.parse(selectedAPI["headers"]);
            const headersMap = objectToMap(headersObj);

            // Authorization
            if (headersMap.has("Authorization")) {
                getAuthorization(headersMap);
            } else {
                document.getElementById('authorizationKey').value = "No Auth";
            }

            // Headers
            const headers = document.getElementById("headers");
            let isFirstIteration = true;
            headersMap.forEach((value, key) => {
                if (key !== "Content-Type" && key !== "Authorization") {
                    if (isFirstIteration) {
                        document.getElementById('headersKey').value = key;
                        document.getElementById('headersValue').value = value;
                        isFirstIteration = false;
                    } else {
                        document.getElementById("headersButton").style.display = "none";
                        let headersKeyHtml = `<input type="text" class="headers-input headersKey" name="headersKey" placeholder="Key" value=${key}>`;
                        let headersValueHtml = `<input type="text" class="headers-input headersValue" name="headersValue" placeholder="Value" value=${value}><br>`;
                        headers.insertAdjacentHTML("beforeend", headersKeyHtml)
                        headers.insertAdjacentHTML("beforeend", headersValueHtml)
                    }
                }
            });
        }

        // Body
        document.getElementById('body').value = '';
        if (selectedAPI["body"] != null) {
            document.getElementById('body').value = selectedAPI["body"];
        }
    }

    function objectToMap(obj) {
        const map = new Map();
        for (const key in obj) {
            if (Object.hasOwnProperty.call(obj, key)) {
                map.set(key, obj[key]);
            }
        }
        return map;
    }

    function getAuthorization(headersMap) {

        let authorizationKey = null;
        let authorizationValue = null;

        headersMap.forEach((value, key) => {
            if (key === "Authorization") {

                let lastSpaceIndex = value[0].lastIndexOf(" ");

                if (lastSpaceIndex !== -1) {
                    authorizationKey = value[0].substring(0, lastSpaceIndex);
                    authorizationValue = value[0].substring(lastSpaceIndex + 1);
                }

                document.getElementById('authorizationKey').value = authorizationKey;
                document.getElementById('authorizationValue').value = authorizationValue;
            }
        })
    }

    function addQueryParamsInput(event) {

        let allParamsValueInput = document.querySelectorAll(".paramsValue");
        let paramsValueInput = allParamsValueInput[allParamsValueInput.length - 1];

        const cursorPosition = paramsValueInput.selectionStart;

        if (paramsValueInput.value.trim() !== "" &&
            !paramsValueInput.nextElementSibling?.classList.contains("dynamic-input")
        ) {
            const br = document.createElement("br");

            const newKeyInput = document.createElement("input");
            newKeyInput.setAttribute("type", "text");
            newKeyInput.classList.add("dynamic-input");
            newKeyInput.classList.add("paramsKey");
            newKeyInput.setAttribute("name", "paramsKey");
            newKeyInput.setAttribute("placeholder", "Key");
            newKeyInput.setAttribute("oninput", "addQueryParamsInput(event)");

            const newValueInput = document.createElement("input");
            newValueInput.setAttribute("type", "text");
            newValueInput.classList.add("dynamic-input");
            newValueInput.classList.add("paramsValue");
            newValueInput.setAttribute("name", "paramsValue");
            newValueInput.setAttribute("placeholder", "Value");
            newValueInput.setAttribute("oninput", "addQueryParamsInput(event)");

            if (!paramsValueInput.nextElementSibling ||
                (paramsValueInput.nextElementSibling &&
                    paramsValueInput.nextElementSibling.value
                    && paramsValueInput.nextElementSibling.value.trim() === "")) {
                paramsValueInput.parentNode.appendChild(br);
                paramsValueInput.parentNode.appendChild(newKeyInput);
                paramsValueInput.parentNode.appendChild(document.createTextNode(" "));
                paramsValueInput.parentNode.appendChild(newValueInput);
            }

            newKeyInput.selectionStart = cursorPosition;
            newKeyInput.selectionEnd = cursorPosition;
        }
    }

    function addHeadersInput(event) {

        let inputHeaders = event.target;

        const cursorPositionHeaders = inputHeaders.selectionStart;

        if (inputHeaders.value.trim() !== "" &&
            !inputHeaders.nextElementSibling?.classList.contains("headers-input")
        ) {
            const br = document.createElement("br");

            const newHeadersKeyInput = document.createElement("input");
            newHeadersKeyInput.setAttribute("type", "text");
            newHeadersKeyInput.classList.add("headers-input");
            newHeadersKeyInput.classList.add("headersKey");
            newHeadersKeyInput.setAttribute("name", "headersKey");
            newHeadersKeyInput.setAttribute("placeholder", "Key");
            newHeadersKeyInput.setAttribute("oninput", "addHeadersInput(event)");

            const newHeadersValueInput = document.createElement("input");
            newHeadersValueInput.setAttribute("type", "text");
            newHeadersValueInput.classList.add("headers-input");
            newHeadersValueInput.classList.add("headersValue");
            newHeadersValueInput.setAttribute("name", "headersValue");
            newHeadersValueInput.setAttribute("placeholder", "Value");
            newHeadersValueInput.setAttribute("oninput", "addHeadersInput(event)");

            if (!inputHeaders.nextElementSibling || inputHeaders.nextElementSibling && inputHeaders.nextElementSibling.value.trim() === "") {
                inputHeaders.parentNode.appendChild(br);
                inputHeaders.parentNode.appendChild(newHeadersKeyInput);
                inputHeaders.parentNode.appendChild(document.createTextNode(" "));
                inputHeaders.parentNode.appendChild(newHeadersValueInput);
            }

            newHeadersKeyInput.selectionStart = cursorPositionHeaders;
            newHeadersKeyInput.selectionEnd = cursorPositionHeaders;
        }
    }
}
