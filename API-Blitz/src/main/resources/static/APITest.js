let response = null;
let selectedAPI = null;
let method = null;

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
            allParamsKeysInput[index+1].value = '';
            allParamsValueInput[index+1].value = '';
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

document.getElementById("url").addEventListener("input", function(event) {
    updateParamsFromUrl();
});

document.getElementById("queryParams").addEventListener("input", function(event) {
    updateUrlFromParams();
});

document.getElementById('api-form').addEventListener('submit', function(event) {
    event.preventDefault();

    let formData = new FormData(this);
    method = formData.get("method");

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/APITest.html');
    xhr.onload = function() {
        response = JSON.parse(xhr.responseText);
        displayResponse();
    };
    xhr.send(formData);
});

function displayResponse() {

    let statusCode = document.getElementById('status-code');
    statusCode.innerHTML = '';
    let responseCodeHtml = `Status Code: ${response.statusCodeValue}`;
    statusCode.insertAdjacentHTML('beforeend', responseCodeHtml);

    let responseTime = document.getElementById('response-time');
    responseTime.innerHTML = '';
    let responseTimeHtml = `Response Time: ${response.headers['Execution-Duration']} ms`;
    responseTime.insertAdjacentHTML('beforeend', responseTimeHtml);

    let responseSize = document.getElementById('response-size');
    responseSize.innerHTML = '';
    let responseSizeHtml = `Response Size: ${response.headers["Content-Length"]} B`;
    responseSize.insertAdjacentHTML('beforeend', responseSizeHtml);

    let headerTab = document.getElementById("show-response-header");
    headerTab.style.display = "block";
    let bodyTab = document.getElementById("show-response-body");
    bodyTab.style.display = "block";

    let responseBody = document.getElementById('response');
    responseBody.innerHTML = '';

    let contentType = getContentType();

    if (response.body == null) {
        responseBody.innerHTML = 'There is no response body.';
    } else if (contentType === "image") {
        const imageURL = btoa(response.body);
        let img = document.createElement("img");
        img.src = `data:image/bmp;base64, ${imageURL}`;
        responseBody.insertAdjacentHTML("beforeend", "<br>");
        responseBody.appendChild(img);
    } else {
        let responseBodyText = formatJSON(JSON.parse(response.body));
        let responseBodyHtml = `<pre><code>${responseBodyText}</code></pre>`;
        responseBody.insertAdjacentHTML('beforeend', responseBodyHtml);
    }
}

function getContentType() {
    let contentType = response.headers["Content-Type"];
    return contentType[0].split("/")[0];
}

function displayHeaders() {

    let statusCode = document.getElementById('status-code');
    statusCode.innerHTML = '';
    let responseCodeHtml = `Status Code: ${response.statusCodeValue}`;
    statusCode.insertAdjacentHTML('beforeend', responseCodeHtml);

    let responseTime = document.getElementById('response-time');
    responseTime.innerHTML = '';
    let responseTimeHtml = `Response Time: ${response.headers['Execution-Duration']} ms`;
    responseTime.insertAdjacentHTML('beforeend', responseTimeHtml);

    let responseSize = document.getElementById('response-size');
    responseSize.innerHTML = '';
    let responseSizeHtml = `Response Size: ${response.headers["Content-Length"]} B`;
    responseSize.insertAdjacentHTML('beforeend', responseSizeHtml);

    let responseHeaders = document.getElementById('response');
    responseHeaders.innerHTML = '';

    const headersTable = document.createElement('table');
    headersTable.classList.add('headers-table');

    const headersMap = objectToMap(response.headers);
    headersMap.forEach((value, key) => {
        const tr = document.createElement("tr");
        let keyHtml = `<td>${key}</td>`;
        let valueHtml = `<td>${value}</td>`;
        tr.insertAdjacentHTML('beforeend', keyHtml);
        tr.insertAdjacentHTML('beforeend', valueHtml);
        headersTable.appendChild(tr);
    });
    responseHeaders.appendChild(headersTable);
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('show-response-body').addEventListener('click', displayResponse);
    document.getElementById('show-response-header').addEventListener('click', displayHeaders);
});

function formatJSON(json) {
    return JSON.stringify(json, null, 4)
}

function showHistory() {

    const form = document.getElementById("api-form");
    const dom = document.getElementById('api-history-list');
    if (dom.style.display === "block") {
        dom.style.display = "none";
        form.style.display = "block";
    } else {
        form.style.display = "none";
        dom.style.display = "block";
        dom.innerHTML = '';

        fetch('/APITest/history?userId=1')
            .then(response => {
                if (!response.ok) {
                    console.log(response.status)
                    throw new Error('Network response was not ok');
                }
                return response.json();
            })
            .then(data => {
                data.forEach(api => {
                    const method = document.createElement('p');
                    method.innerHTML = api["method"];
                    dom.appendChild(method);
                    const button = document.createElement('button');
                    button.innerText = api["apiurl"];
                    dom.appendChild(button);
                    const lineBreak = document.createElement('br');
                    dom.appendChild(lineBreak);
                    button.addEventListener('click', () => {
                        selectedAPI = api;
                        dom.style.display = "none";
                        insertData(selectedAPI)
                    });
                });
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }
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
                let paramKeyHtml = `<input id="paramsKey" type="text" name="paramsKey" placeholder="Key" value=${key}>`;
                let paramValueHtml = `<input id="paramsValue" type="text" name="paramsValue" placeholder="Value" value=${value}><br>`;
                queryParams.insertAdjacentHTML("beforeend", paramKeyHtml)
                queryParams.insertAdjacentHTML("beforeend", paramValueHtml)
            }
        });
        document.getElementById("queryParamsButton").style.display = "block";
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
                    let headersKeyHtml = `<input id="headersKey" type="text" name="headersKey" placeholder="Key" value=${key}>`;
                    let headersValueHtml = `<input id="headersValue" type="text" name="headersValue" placeholder="Value" value=${value}><br>`;
                    headers.insertAdjacentHTML("beforeend", headersKeyHtml)
                    headers.insertAdjacentHTML("beforeend", headersValueHtml)
                }
            }
        });
        document.getElementById("headersButton").style.display = "block";
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
    headersMap.forEach((value, key) => {
        if (key === "Authorization") {
            let getKeyValue = value[0].split(" ");
            document.getElementById('authorizationKey').value = getKeyValue[0];
            document.getElementById('authorizationValue').value = getKeyValue[1];
        }
    });
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
        newHeadersKeyInput.id = "headersKey";
        newHeadersKeyInput.setAttribute("type", "text");
        newHeadersKeyInput.classList.add("headers-input");
        newHeadersKeyInput.setAttribute("name", "headersKey");
        newHeadersKeyInput.setAttribute("placeholder", "Key");
        newHeadersKeyInput.setAttribute("oninput", "addHeadersInput(event)");

        const newHeadersValueInput = document.createElement("input");
        newHeadersValueInput.setAttribute("type", "text");
        newHeadersValueInput.id = "headersValue";
        newHeadersValueInput.classList.add("headers-input");
        newHeadersValueInput.setAttribute("name", "headersValue");
        newHeadersValueInput.setAttribute("placeholder", "Value");
        newHeadersValueInput.setAttribute("oninput", "addHeadersInput(event)");

        if (!inputHeaders.nextElementSibling || inputHeaders.nextElementSibling && inputHeaders.nextElementSibling.value.trim() === "") {
            inputHeaders.parentNode.appendChild(br);
            inputHeaders.parentNode.appendChild(newHeadersKeyInput);
            inputHeaders.parentNode.appendChild(newHeadersValueInput);
        }

        newHeadersKeyInput.selectionStart = cursorPositionHeaders;
        newHeadersKeyInput.selectionEnd = cursorPositionHeaders;
    }
}
