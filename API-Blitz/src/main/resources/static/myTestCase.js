let testCaseId;

fetch('/api/1.0/testCase/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {

        const table = document.createElement("table");
        table.classList.add("table");
        table.classList.add("table-bordered");

        const thead = document.createElement("thead");
        const theadTr = document.createElement("tr");
        theadTr.insertAdjacentHTML("beforeend", "<th>ID</th>");
        theadTr.insertAdjacentHTML("beforeend", "<th>Test Item</th>");
        theadTr.insertAdjacentHTML("beforeend", "<th>Method</th>");
        theadTr.insertAdjacentHTML("beforeend", "<th>URL</th>");
        theadTr.insertAdjacentHTML("beforeend", "<th>Details</th>");
        theadTr.insertAdjacentHTML("beforeend", "<th>Actions</th>");
        thead.appendChild(theadTr);

        const tbody = document.createElement("tbody");

        // const ul = document.createElement("ul");
        // ul.classList.add("test-case-table");
        //
        // const tableHeaderLi = document.createElement("li");
        // tableHeaderLi.classList.add("test-case-table-header");
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">ID</div>`);
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Test Item</div>`);
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">Method</div>`);
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader">URL</div>`);
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-5 tableHeader">Details</div>`);
        // tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-6 tableHeader"></div>`);
        //
        // ul.appendChild(tableHeaderLi);

        data.forEach(testCase => {

            const tbodyTr = document.createElement("tr");
            tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-1">${testCase["id"]}</td>`);
            tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-2">${testCase["testItem"]}</td>`);
            tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-3">${testCase["method"]}</td>`);

            tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-4">${testCase["apiurl"]}</td>`);
            tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-5"><a href="/api/1.0/autoTest/monitor?testCaseId=${testCase["id"]}">View Monitor ⮕</a></td>`);
            tbodyTr.insertAdjacentHTML("beforeend",
                `<td class="col col-6"><button type="button" class="btn btn-block btn-edit">Edit</button>
                        <button type="button" class="btn btn-block btn-delete">Delete</button></td>`)

            tbody.appendChild(tbodyTr);

            const editButton = tbodyTr.querySelector(".btn-edit");
            const deleteButton = tbodyTr.querySelector(".btn-delete");

            editButton.addEventListener("click", () => {
                modifyTestCase(testCase);
            })
            deleteButton.addEventListener("click", () => {
                testCaseId = testCase["id"];
                deleteTestCase();
            })



            // const li = document.createElement("li");
            // li.classList.add("test-case-table-row");
            // li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="ID">${testCase["id"]}</div>`);
            // li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Test Item">${testCase["testItem"]}</div>`);
            // li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="Method">${testCase["method"]}</div>`);
            // li.insertAdjacentHTML("beforeend", `<div class="col col-4" data-label="URL">${testCase["apiurl"]}</div>`);
            // li.insertAdjacentHTML("beforeend",
            //     `<div class="col col-5" data-label="Details"><a href="/api/1.0/autoTest/monitor?testCaseId=${testCase["id"]}">View Monitor ⮕</a></div>`);
            //
            // const buttonDiv = document.createElement("div");
            // buttonDiv.classList.add("col", "col-6", "buttonDiv");
            //
            // const editButton = document.createElement("button");
            // editButton.type = "button";
            // editButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
            // editButton.style.backgroundImage = "url('/edit.png')";
            // editButton.style.backgroundSize = "contain";
            // editButton.style.backgroundRepeat = "no-repeat";
            // editButton.style.backgroundPosition = "center";
            //
            // const editIcon = document.createElement("span");
            // editIcon.classList.add("glyphicon", "glyphicon-pencil");
            // editIcon.setAttribute("aria-hidden", "true");
            //
            // editButton.appendChild(editIcon);
            //
            // const deleteButton = document.createElement("button");
            // deleteButton.type = "button";
            // deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
            // deleteButton.style.backgroundImage = "url('/delete.png')";
            // deleteButton.style.backgroundSize = "contain";
            // deleteButton.style.backgroundRepeat = "no-repeat";
            // deleteButton.style.backgroundPosition = "center";
            //
            // const deleteIcon = document.createElement("span");
            // deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
            // deleteIcon.setAttribute("aria-hidden", "true");
            //
            // deleteButton.appendChild(deleteIcon);
            //
            // buttonDiv.appendChild(editButton);
            // buttonDiv.appendChild(deleteButton);
            //
            // li.appendChild(buttonDiv);
            //
            // ul.appendChild(li);
            //
            // editButton.addEventListener('click', () => {
            //     modifyTestCase(testCase);
            // });
            // deleteButton.addEventListener('click', () => {
            //     testCaseId = testCase["id"];
            //     deleteTestCase();
            // });
        });
        table.appendChild(thead);
        table.appendChild(tbody);
        const container = document.getElementById('test-case-container');
        container.insertAdjacentHTML("beforeend",
        '<button type="button" class="btn btn-block btn-add" onclick="addTestCase()">+ Add</button>');
        container.appendChild(table);

    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function addTestCase() {

    // Reset query params
    const queryParams = document.getElementById("queryParams");
    queryParams.innerHTML = '';
    queryParams.insertAdjacentHTML("beforeend", '<label for="queryParams">Query Params</label><br>');
    queryParams.insertAdjacentHTML("beforeend", '<input id="paramsKey" type="text" class="dynamic-input paramsKey" name="paramsKey" placeholder="Key" oninput="addQueryParamsInput(event)">');
    queryParams.insertAdjacentHTML("beforeend"," ");
    queryParams.insertAdjacentHTML("beforeend", '<input id="paramsValue" type="text" class="dynamic-input paramsValue" name="paramsValue" placeholder="Value" oninput="addQueryParamsInput(event)">');

    // Reset headers
    const headers = document.getElementById("headers");
    headers.innerHTML = '';
    headers.insertAdjacentHTML("beforeend", '<label for="headers">Headers</label><br>');
    headers.insertAdjacentHTML("beforeend", '<input id="headersKey" type="text" class="headers-input" name="headersKey" placeholder="Key" oninput="addHeadersInput(event)">');
    headers.insertAdjacentHTML("beforeend"," ");
    headers.insertAdjacentHTML("beforeend", '<input id="headersValue" type="text" class="headers-input" name="headersValue" placeholder="Value" oninput="addHeadersInput(event)">');

    const recipientEmail = document.getElementById("recipientEmail");
    recipientEmail.innerHTML = '';
    recipientEmail.insertAdjacentHTML("beforeend", '<label for="recipientEmail">Recipient Email</label><br>');
    recipientEmail.insertAdjacentHTML("beforeend", '<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email">');
    recipientEmail.insertAdjacentHTML("beforeend", '<br>')

    document.getElementById("modify-form").reset();
    document.querySelector('label[for="id"]').style.display = "none";
    document.getElementById("id").style.display = "none";

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

    const dom = document.getElementById("add-container");
    dom.style.display = "block";
    const overlay = document.getElementById('overlay');
    overlay.style.display = 'block'
    overlay.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        overlay.style.display = "none";
    })

    const body = document.getElementById("body");
    body.addEventListener("input", () => {
        body.value = JSON.stringify(JSON.parse(body.value), null, 4);
    })

    const expectedResponseBody = document.getElementById("expectedResponseBody");
    expectedResponseBody.addEventListener("input", () => {
        expectedResponseBody.value = JSON.stringify(JSON.parse(body.value), null, 4);
    })

    const cancelButton = document.getElementById("cancel-button");
    cancelButton.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        document.getElementById('overlay').style.display = 'none';
    })

    document.getElementById("modify-form").addEventListener("submit", function (event) {
        event.preventDefault();
        const formData = new FormData(this);
        fetch('/api/1.0/testCase/create', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    alert("Please confirm whether the entered information is correct.");
                } else {
                    window.location.href = "/api/1.0/testCase/myTestCase";
                    alert("Added Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    });
}

function modifyTestCase(testCase) {

    const recipientEmail = document.getElementById("recipientEmail");
    recipientEmail.innerHTML = '';
    recipientEmail.insertAdjacentHTML("beforeend", '<label for="recipientEmail">Recipient Email</label><br>');
    recipientEmail.insertAdjacentHTML("beforeend", '<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email">');
    recipientEmail.insertAdjacentHTML("beforeend", '<br>')

    document.querySelector('label[for="id"]').style.display = "block";
    document.getElementById("id").style.display = "block";

    showTestCase(testCase);

    const dom = document.getElementById("add-container");
    dom.style.display = "block";
    const overlay = document.getElementById('overlay');
    overlay.style.display = 'block'
    overlay.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        overlay.style.display = "none";
    })

    const cancelButton = document.getElementById("cancel-button");
    cancelButton.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        document.getElementById('overlay').style.display = 'none';
    })

    document.getElementById("modify-form").addEventListener("submit", function (event) {
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
                    window.location.href = "/api/1.0/testCase/myTestCase";
                    alert("Update Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    });
}

function deleteTestCase() {

    fetch('/api/1.0/testCase/delete?testCaseId=' + testCaseId, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                alert("Failed to delete, please check!");
            } else {
                window.location.href = "/api/1.0/testCase/myTestCase";
                alert("Delete Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}

function showTestCase(testCase) {
    const dom = document.getElementById('modify-form');
    dom.style.display = "block";
    insertData(testCase)
}

function insertData(testCase) {

    // Id
    document.getElementById('id').value = testCase["id"];

    // Test item
    document.getElementById('testItem').value = testCase["testItem"];

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
        let bodyText = JSON.parse(testCase["body"]);
        document.getElementById('body').value = JSON.stringify(bodyText, null, 4);
    }

    // Status code
    document.getElementById('statusCode').value = testCase["statusCode"];

    // Expected response body
    let expectedResponseBodyText = JSON.parse(testCase["expectedResponseBody"]);
    document.getElementById('expectedResponseBody').value = JSON.stringify(expectedResponseBodyText, null, 4);

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
                        let emailHtml = `<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email" value=${email}><br>`;
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
            inputHeaders.parentNode.appendChild(document.createTextNode(" "));
            inputHeaders.parentNode.appendChild(newHeadersValueInput);
        }

        newHeadersKeyInput.selectionStart = cursorPositionHeaders;
        newHeadersKeyInput.selectionEnd = cursorPositionHeaders;
    }
}

function addRecipientEmail() {
    const recipientEmailDiv = document.getElementById("recipientEmail");
    const inputDiv = document.createElement("div");
    inputDiv.innerHTML = `<input id="email" type="text" name="email" style="width: 300px;" placeholder="Email"><br>`;
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
