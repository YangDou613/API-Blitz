function addQueryParamsInputOfTestCase(event) {

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
        newKeyInput.setAttribute("oninput", "addQueryParamsInputOfTestCase(event)");

        const newValueInput = document.createElement("input");
        newValueInput.setAttribute("type", "text");
        newValueInput.classList.add("dynamic-input");
        newValueInput.setAttribute("name", "paramsValue");
        newValueInput.setAttribute("placeholder", "Value");
        newValueInput.setAttribute("oninput", "addQueryParamsInputOfTestCase(event)");

        if (!input.nextElementSibling || (input.nextElementSibling && input.nextElementSibling.value.trim() === "")) {
            input.parentNode.appendChild(br);
            input.parentNode.appendChild(newKeyInput);
            input.parentNode.appendChild(newValueInput);
        }

        newKeyInput.selectionStart = cursorPosition;
        newKeyInput.selectionEnd = cursorPosition;
    }
}

function addHeadersInputOfTestCase(event) {

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
        newHeadersKeyInput.setAttribute("oninput", "addHeadersInputOfTestCase(event)");

        const newHeadersValueInput = document.createElement("input");
        newHeadersValueInput.setAttribute("type", "text");
        newHeadersValueInput.classList.add("headers-input");
        newHeadersValueInput.setAttribute("name", "headersValue");
        newHeadersValueInput.setAttribute("placeholder", "Value");
        newHeadersValueInput.setAttribute("oninput", "addHeadersInputOfTestCase(event)");

        if (!inputHeaders.nextElementSibling || inputHeaders.nextElementSibling && inputHeaders.nextElementSibling.value.trim() === "") {
            inputHeaders.parentNode.appendChild(br);
            inputHeaders.parentNode.appendChild(newHeadersKeyInput);
            inputHeaders.parentNode.appendChild(newHeadersValueInput);
        }

        newHeadersKeyInput.selectionStart = cursorPositionHeaders;
        newHeadersKeyInput.selectionEnd = cursorPositionHeaders;
    }
}

function validateForm() {
    const bodyInput = document.getElementById("body");
    const expectedResponseBodyInput = document.getElementById("expectedResponseBody");

    if (bodyInput.value.trim() !== "" && !isValidJSON(bodyInput.value)) {
        alert("Body is not a valid JSON.");
        return false;
    }

    if (!isValidJSON(expectedResponseBodyInput.value)) {
        alert("Expected Response Body is not a valid JSON.");
        return false;
    }

    return true;
}

function validateFormAndSubmit() {
    if (validateForm()) {
        alert("Added Successfully!");
        window.location.href = "/api/1.0/testCase";
        return true;
    } else {
        return false;
    }
}

function isValidJSON(jsonString) {
    try {
        JSON.parse(jsonString);
        return true;
    } catch (error) {
        return false;
    }
}
