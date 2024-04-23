let selectedAPI = null;
let method = null;

document.getElementById('api-form').addEventListener('submit', function(event) {
    event.preventDefault();

    let formData = new FormData(this);
    method = formData.get("method");

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/APITest.html');
    xhr.onload = function() {
        let response = JSON.parse(xhr.responseText);
        storedResponse = response;
        displayResponse(response);
    };
    xhr.send(formData);
});

function addQueryParam() {
    const queryParamsDiv = document.getElementById("queryParams");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                <input type="text" name="paramsKey" placeholder="Key">
                <input type="text" name="paramsValue" placeholder="Value">
                <br>
            `;
    queryParamsDiv.appendChild(inputDiv);
}

function addHeader() {
    const headersDiv = document.getElementById("headers");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                <input type="text" name="headerKey" placeholder="Key">
                <input type="text" name="headerValue" placeholder="Value">
                <br>
            `;
    headersDiv.appendChild(inputDiv);
}

function displayResponse() {

    let response = storedResponse;

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

    let optionButton = document.getElementById('option-button');
    optionButton.innerHTML = '';
    optionButton.insertAdjacentHTML('beforeend','<button id="show-response-header" onclick="displayHeader()">Header</button>');

    let responseBody = document.getElementById('response');
    responseBody.innerHTML = '';
    if (response.body == null) {
        responseBody.innerHTML = 'There is no response body.';
    } else {
        let responseBodyText = formatJSON(response.body);
        let responseBodyHtml = `Response Body: <pre><code>${responseBodyText}</code></pre>`;
        responseBody.insertAdjacentHTML('beforeend', responseBodyHtml);
    }
}

function displayHeader() {

    let response = storedResponse;

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

    let optionButton = document.getElementById('option-button');
    optionButton.innerHTML = '';
    optionButton.insertAdjacentHTML('beforeend', '<button id="show-response-body" onclick="displayResponse()">Body</button>');

    let responseHeader = document.getElementById('response');
    responseHeader.innerHTML = '';
    let TitleHtml = `Response Header:`;
    let serverHtml = `Server: ${response.headers["Server"]}`;
    let dateHtml = `Date: ${response.headers["Date"]}`;
    let contentTypeHtml = `Content-Type: ${response.headers["Content-Type"]}`;

    responseHeader.insertAdjacentHTML('beforeend', TitleHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br><br>');
    responseHeader.insertAdjacentHTML('beforeend', serverHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', dateHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', contentTypeHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');

    if (method !== "OPTIONS") {
        let transferEncodingHtml = `Transfer-Encoding: ${response.headers["Transfer-Encoding"]}`;
        let connectionHtml = `Connection: ${response.headers["Connection"]}`;
        let varyHtml = `Vary: ${response.headers["Vary"]}`;

        responseHeader.insertAdjacentHTML('beforeend', transferEncodingHtml);
        responseHeader.insertAdjacentHTML('beforeend', '<br>');
        responseHeader.insertAdjacentHTML('beforeend', connectionHtml);
        responseHeader.insertAdjacentHTML('beforeend', '<br>');
        responseHeader.insertAdjacentHTML('beforeend', varyHtml);
    } else {
        let allowHtml = `Allow: ${response.headers["Allow"]}`;
        responseHeader.insertAdjacentHTML('beforeend', allowHtml);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('show-response-body').addEventListener('click', displayResponse);
    document.getElementById('show-response-header').addEventListener('click', displayHeader);
});

function formatJSON(json) {
    return JSON.stringify(json, null, 2)
        .replace(/^"|"$/g, '')
        .replace(/\\/g, '')
        .replace(/,/g, ',\n')
        .replace(/(["{\[\]}])\n(?=.)/g, '$1\n')
        .replace(/(".*?": )/g, '\n$1')
        .replace(/\n/g, '\n    ');
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

// function addQueryParamsInput(event) {
//
//     let input = event.target;
//
//     const cursorPosition = input.selectionStart;
//
//     if (input.value.trim() !== "" &&
//         !input.nextElementSibling?.classList.contains("dynamic-input")
//     ) {
//         const br = document.createElement("br");
//
//         const newKeyInput = document.createElement("input");
//         newKeyInput.setAttribute("type", "text");
//         newKeyInput.classList.add("dynamic-input");
//         newKeyInput.setAttribute("name", "paramsKey");
//         newKeyInput.setAttribute("placeholder", "Key");
//         newKeyInput.setAttribute("oninput", "addQueryParamsInput(event)");
//
//         const newValueInput = document.createElement("input");
//         newValueInput.setAttribute("type", "text");
//         newValueInput.classList.add("dynamic-input");
//         newValueInput.setAttribute("name", "paramsValue");
//         newValueInput.setAttribute("placeholder", "Value");
//         newValueInput.setAttribute("oninput", "addQueryParamsInput(event)");
//
//         if (!input.nextElementSibling || (input.nextElementSibling && input.nextElementSibling.value.trim() === "")) {
//             input.parentNode.appendChild(br);
//             input.parentNode.appendChild(newKeyInput);
//             input.parentNode.appendChild(newValueInput);
//         }
//
//         newKeyInput.selectionStart = cursorPosition;
//         newKeyInput.selectionEnd = cursorPosition;
//     }
// }
//
// function addHeadersInput(event) {
//
//     let inputHeaders = event.target;
//
//     const cursorPositionHeaders = inputHeaders.selectionStart;
//
//     if (inputHeaders.value.trim() !== "" &&
//         !inputHeaders.nextElementSibling?.classList.contains("headers-input")
//     ) {
//         const br = document.createElement("br");
//
//         const newHeadersKeyInput = document.createElement("input");
//         newHeadersKeyInput.setAttribute("type", "text");
//         newHeadersKeyInput.classList.add("headers-input");
//         newHeadersKeyInput.setAttribute("name", "headersKey");
//         newHeadersKeyInput.setAttribute("placeholder", "Key");
//         newHeadersKeyInput.setAttribute("oninput", "addHeadersInput(event)");
//
//         const newHeadersValueInput = document.createElement("input");
//         newHeadersValueInput.setAttribute("type", "text");
//         newHeadersValueInput.classList.add("headers-input");
//         newHeadersValueInput.setAttribute("name", "headersValue");
//         newHeadersValueInput.setAttribute("placeholder", "Value");
//         newHeadersValueInput.setAttribute("oninput", "addHeadersInput(event)");
//
//         if (!inputHeaders.nextElementSibling || inputHeaders.nextElementSibling && inputHeaders.nextElementSibling.value.trim() === "") {
//             inputHeaders.parentNode.appendChild(br);
//             inputHeaders.parentNode.appendChild(newHeadersKeyInput);
//             inputHeaders.parentNode.appendChild(newHeadersValueInput);
//         }
//
//         newHeadersKeyInput.selectionStart = cursorPositionHeaders;
//         newHeadersKeyInput.selectionEnd = cursorPositionHeaders;
//     }
// }
