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

// function addQueryParam() {
//     const queryParamsDiv = document.getElementById("queryParams");
//     const inputDiv = document.createElement("div");
//     inputDiv.innerHTML = `
//                 <input type="text" name="paramsKey" placeholder="Key">
//                 <input type="text" name="paramsValue" placeholder="Value">
//                 <br>
//             `;
//     queryParamsDiv.appendChild(inputDiv);
// }
//
// function addHeader() {
//     const headersDiv = document.getElementById("headers");
//     const inputDiv = document.createElement("div");
//     inputDiv.innerHTML = `
//                 <input type="text" name="headerKey" placeholder="Key">
//                 <input type="text" name="headerValue" placeholder="Value">
//                 <br>
//             `;
//     headersDiv.appendChild(inputDiv);
// }

function addRecipientEmail() {
    const recipientEmailDiv = document.getElementById("recipientEmail");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `
                    <input id="email" type="text" name="email" placeholder="Email">
                    <br>
                `;
    recipientEmailDiv.appendChild(inputDiv);
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

let intervalsTimeUnitSelect = document.getElementById("intervalsTimeUnit");
let intervalsTimeValueInput = document.getElementById("intervalsTimeValue");

intervalsTimeUnitSelect.addEventListener("change", function() {
    let selectedUnit = intervalsTimeUnitSelect.value;
    switch(selectedUnit) {
    case "Hour":
    intervalsTimeValueInput.max = 23;
    break;
    case "Day":
    intervalsTimeValueInput.max = 365;
    break;
    case "Sec":
    intervalsTimeValueInput.max = 86399;
    break;
}
});

let notificationSelect = document.getElementById("notification");
let recipientEmailDiv = document.getElementById("recipientEmail");
let emailInput = document.getElementById("email");
let emailButton = document.getElementById("emailButton");

notificationSelect.addEventListener("change", function() {
    if (notificationSelect.value === "Yes") {
        recipientEmailDiv.style.display = "block";
        emailButton.style.display = "block";
    } else {
        recipientEmailDiv.style.display = "none";
        emailButton.style.display = "none";
    }
    emailInput.required = notificationSelect.value === "Yes";
});


// function addQueryParamsInputOfTestCase(event) {
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
//         newKeyInput.setAttribute("oninput", "addQueryParamsInputOfTestCase(event)");
//
//         const newValueInput = document.createElement("input");
//         newValueInput.setAttribute("type", "text");
//         newValueInput.classList.add("dynamic-input");
//         newValueInput.setAttribute("name", "paramsValue");
//         newValueInput.setAttribute("placeholder", "Value");
//         newValueInput.setAttribute("oninput", "addQueryParamsInputOfTestCase(event)");
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
// function addHeadersInputOfTestCase(event) {
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
//         newHeadersKeyInput.setAttribute("oninput", "addHeadersInputOfTestCase(event)");
//
//         const newHeadersValueInput = document.createElement("input");
//         newHeadersValueInput.setAttribute("type", "text");
//         newHeadersValueInput.classList.add("headers-input");
//         newHeadersValueInput.setAttribute("name", "headersValue");
//         newHeadersValueInput.setAttribute("placeholder", "Value");
//         newHeadersValueInput.setAttribute("oninput", "addHeadersInputOfTestCase(event)");
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
