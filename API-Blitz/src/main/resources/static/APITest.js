let storedResponse = null;

document.getElementById('api-form').addEventListener('submit', function(event) {
    event.preventDefault();

    let formData = new FormData(this);

    let xhr = new XMLHttpRequest();
    xhr.open('POST', '/APITest.html');
    xhr.onload = function() {
        let response = JSON.parse(xhr.responseText);

        storedResponse = response;
        displayResponse(response);

    };
    xhr.send(formData);
});

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
    let responseBodyText = formatJSON(response.body);
    let responseBodyHtml = `Response Body: <pre><code>${responseBodyText}</code></pre>`;
    responseBody.insertAdjacentHTML('beforeend', responseBodyHtml);
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
    optionButton.insertAdjacentHTML('beforeend','<button id="show-response-body" onclick="displayResponse()">Body</button>');

    let responseHeader = document.getElementById('response');
    responseHeader.innerHTML = '';
    let TitleHtml = `Response Header:`;
    let connectionHtml = `Connection: ${response.headers["Connection"]}`;
    let contentTypeHtml = `Content-Length: ${response.headers["Content-Length"]}`;
    let dateHtml = `Date: ${response.headers["Date"]}`;
    let serverHtml = `Server: ${response.headers["Server"]}`;
    let transferEncodingHtml = `Transformer-Encoding: ${response.headers["Transformer-Encoding"]}`;
    let varyHtml = `Vary: ${response.headers["Vary"]}`;
    responseHeader.insertAdjacentHTML('beforeend', TitleHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br><br>');
    responseHeader.insertAdjacentHTML('beforeend', connectionHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', contentTypeHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', dateHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', serverHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', transferEncodingHtml);
    responseHeader.insertAdjacentHTML('beforeend', '<br>');
    responseHeader.insertAdjacentHTML('beforeend', varyHtml);
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

function addQueryParamsInput(event) {

    let input = event.target;

    const cursorPosition = input.selectionStart;

    if (input.value.trim() !== "" &&
        !input.nextElementSibling?.classList.contains("dynamic-input")
    ) {
        const br = document.createElement("br");

        const newKeyInput = document.createElement("input");
        newKeyInput.setAttribute("type", "text");
        newKeyInput.classList.add("dynamic-input");
        newKeyInput.setAttribute("name", "paramsKey");
        newKeyInput.setAttribute("placeholder", "Key");
        newKeyInput.setAttribute("oninput", "addQueryParamsInput(event)");

        const newValueInput = document.createElement("input");
        newValueInput.setAttribute("type", "text");
        newValueInput.classList.add("dynamic-input");
        newValueInput.setAttribute("name", "paramsValue");
        newValueInput.setAttribute("placeholder", "Value");
        newValueInput.setAttribute("oninput", "addQueryParamsInput(event)");

        if (!input.nextElementSibling || (input.nextElementSibling && input.nextElementSibling.value.trim() === "")) {
            input.parentNode.appendChild(br);
            input.parentNode.appendChild(newKeyInput);
            input.parentNode.appendChild(newValueInput);
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
        newHeadersKeyInput.setAttribute("name", "headersKey");
        newHeadersKeyInput.setAttribute("placeholder", "Key");
        newHeadersKeyInput.setAttribute("oninput", "addHeadersInput(event)");

        const newHeadersValueInput = document.createElement("input");
        newHeadersValueInput.setAttribute("type", "text");
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
