document.getElementById("modify-form").addEventListener("submit", function(event) {
    event.preventDefault();
    const formData = new FormData(this);
    fetch('/api/1.0/testCase/update', {
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                alert("Please confirm whether the entered information is correct.");
            } else {
                window.location.href = "/api/1.0/testCase/modifyTestCase";
                alert("Update Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
});

fetch('/api/1.0/testCase/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const form = document.getElementById('modify-form');
        const table = document.getElementById('test-case-tale');
        table.classList.add('test-case-tale')
        let isResetTestCaseTitleExist = false;
        data.forEach(testCase => {

            const tr = document.createElement("tr");

            let inputHtml = `<input type="submit" name="id" value="${testCase["id"]}">`;
            let methodHtml = `<td>${testCase["method"]}</td>`;
            let urlHtml = `<td>${testCase["apiurl"]}</td>`;
            tr.insertAdjacentHTML('beforeend', inputHtml);
            tr.insertAdjacentHTML('beforeend', methodHtml);
            tr.insertAdjacentHTML('beforeend', urlHtml);
            table.appendChild(tr);

            const input = tr.querySelector('input[type="submit"]');

            input.addEventListener('click', () => {
                table.style.display = "none";
                if (!isResetTestCaseTitleExist) {
                    form.insertAdjacentHTML("beforebegin", '<h2>Reset Test Case</h2>');
                    isResetTestCaseTitleExist = true;
                }
                showTestCase(testCase)
            });
        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function showTestCase(testCase) {
    const dom = document.getElementById('modify-form');
    dom.style.display = "block";
    insertData(testCase)
}

function insertData(testCase) {

    // Id
    document.getElementById('id').value = testCase["id"];

    // API url
    document.getElementById('url').value = testCase["apiurl"];

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
    if (testCase["queryParams"] != null) {
        const queryParamsObj = JSON.parse(testCase["queryParams"]);
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

    if (testCase["headers"] != null) {
        const headersObj = JSON.parse(testCase["headers"]);
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
                    let headersKeyHtml = `<input id="headersKey" type="text" name="headersKey" placeholder="Key" value=${key}>`;
                    let headersValueHtml = `<input id="headersValue" type="text" name="headersValue" placeholder="Value" value=${value}><br>`;
                    headers.insertAdjacentHTML("beforeend", headersKeyHtml)
                    headers.insertAdjacentHTML("beforeend", headersValueHtml)
                }
            }
        });
    }

    // Body
    document.getElementById('body').value = '';
    if (testCase["body"] != null) {
        document.getElementById('body').value = testCase["body"];
    }

    // Status code
    document.getElementById('statusCode').value = testCase["statusCode"];

    // Expected response body
    document.getElementById('expectedResponseBody').value = testCase["expectedResponseBody"];

    // Intervals time unit
    document.getElementById('intervalsTimeUnit').value = testCase["intervalsTimeUnit"];

    // Intervals time value
    document.getElementById('intervalsTimeValue').value = testCase["intervalsTimeValue"];

    // Notification
    if (testCase["notification"] === 0) {
        document.getElementById('notification').value = "No";

        recipientEmailDiv.style.display = "none";
        emailButton.style.display = "none";

        // Recipient email
        const recipientEmailInputs = document.querySelectorAll('#recipientEmail input[name="email"]');

        recipientEmailInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

    } else {
        document.getElementById('notification').value = "Yes";

        recipientEmailDiv.style.display = "block";
        emailButton.style.display = "block";

        // Recipient email
        const recipientEmailInputs = document.querySelectorAll('#recipientEmail input[name="email"]');

        recipientEmailInputs.forEach((input, index) => {
            if (index > 0) {
                input.parentNode.removeChild(input);
            } else {
                input.value = '';
            }
        });

        const recipientEmail = document.getElementById("recipientEmail");
        if (testCase["recipientEmail"].length > 0) {
            const emailObj = JSON.parse(testCase["recipientEmail"]);
            let isFirstIteration = true;
            emailObj.forEach((email) => {
                if (email !== "") {
                    if (isFirstIteration) {
                        document.getElementById('email').value = email;
                        isFirstIteration = false;
                    } else {
                        // document.getElementById("emailButton").style.display = "none";
                        let emailHtml = `<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email" value=${email}>`;
                        recipientEmail.insertAdjacentHTML("beforeend", emailHtml)
                    }
                }
            });
        }
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

function addRecipientEmail() {
    const recipientEmailDiv = document.getElementById("recipientEmail");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email">`;
    recipientEmailDiv.appendChild(inputDiv);
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
