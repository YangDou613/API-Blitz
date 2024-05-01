let collectionId;
let selectedCollectionName;
let selectedRequestId;
let apiList;
let selectedAPI;

if (window.location.search !== "") {
    let urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has("collectionId")) {
        collectionId = urlParams.get("collectionId");
    }
    if (urlParams.has("collectionName")) {
        selectedCollectionName = urlParams.get("collectionName");
    }
    showAPIData();
}

function showAPIData() {
    fetch('/api/1.0/collections/getAllAPI?collectionId=' + collectionId)
        .then(response => {
            if (!response.ok) {
                console.log(response.status)
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {

            apiList = data;

            const container = document.getElementById("container");

            if (data === null) {
                container.innerText = "You don't have any API data yet.";
            } else {

                const button = document.createElement("div");
                button.id = "button-div";
                button.insertAdjacentHTML("beforeend",
                    ' <input id="add-button" type="submit" onclick="addAPI()" value=" + Add">');
                button.insertAdjacentHTML("beforeend",
                    ' <input id="run-all-button" type="button" onclick="testAllAPI()" value="Test All">');
                container.appendChild(button);

                const ul = document.createElement("ul");
                ul.classList.add("api-table");

                const tableHeaderLi = document.createElement("li");
                tableHeaderLi.classList.add("api-table-header");
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-1 tableHeader">Request Name</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-2 tableHeader">Method</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-3 tableHeader">URL</div>`);
                tableHeaderLi.insertAdjacentHTML("beforeend", `<div class="col col-4 tableHeader"></div>`);

                ul.appendChild(tableHeaderLi);

                data.forEach(api => {

                    const li = document.createElement("li");
                    li.classList.add("api-table-row");
                    li.insertAdjacentHTML("beforeend", `<div class="col col-1" data-label="Request Name">${api["requestName"]}</div>`);
                    li.insertAdjacentHTML("beforeend", `<div class="col col-2" data-label="Method">${api["method"]}</div>`);
                    li.insertAdjacentHTML("beforeend", `<div class="col col-3" data-label="URL">${api["apiurl"]}</div>`);

                    const buttonDiv = document.createElement("div");
                    buttonDiv.classList.add("col", "col-4", "buttonDiv");

                    const testButton = document.createElement("button");
                    testButton.type = "button";
                    testButton.classList.add("btn", "btn-primary", "btn-xs", "dt-edit");
                    testButton.style.marginRight = "16px";
                    testButton.style.backgroundImage = "url('/test-all.png')";
                    testButton.style.backgroundSize = "contain";
                    testButton.style.backgroundRepeat = "no-repeat";
                    testButton.style.backgroundPosition = "center";

                    const editIcon = document.createElement("span");
                    editIcon.classList.add("glyphicon", "glyphicon-pencil");
                    editIcon.setAttribute("aria-hidden", "true");

                    testButton.appendChild(editIcon);

                    const deleteButton = document.createElement("button");
                    deleteButton.type = "button";
                    deleteButton.classList.add("btn", "btn-primary", "btn-xs", "dt-delete");
                    deleteButton.style.marginRight = "16px";
                    deleteButton.style.backgroundImage = "url('/delete.png')";
                    deleteButton.style.backgroundSize = "contain";
                    deleteButton.style.backgroundRepeat = "no-repeat";
                    deleteButton.style.backgroundPosition = "center";

                    const deleteIcon = document.createElement("span");
                    deleteIcon.classList.add("glyphicon", "glyphicon-pencil");
                    deleteIcon.setAttribute("aria-hidden", "true");

                    deleteButton.appendChild(deleteIcon);

                    buttonDiv.appendChild(testButton);
                    buttonDiv.appendChild(deleteButton);

                    li.appendChild(buttonDiv);

                    ul.appendChild(li);

                    selectedCollectionName = api["requestName"];
                    selectedRequestId = api["id"];

                    testButton.addEventListener("click", () => {
                        selectedAPI = api;
                        const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
                        window.location.href = "/APITest.html" + queryString;
                    })
                    deleteButton.addEventListener('click', () => {
                        deleteAPI();
                    });
                });
                container.appendChild(ul);
            }
        })
        .catch(error => {
            console.error('There was an error!', error);
        });
}

async function addAPI() {

    document.getElementById("api-form").reset();

    const dom = document.getElementById("api-form-container")
    dom.style.display = "block";
    const overlay = document.getElementById('overlay');
    overlay.style.display = 'block'
    overlay.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        overlay.style.display = "none";
    })

    const cancelButton = document.getElementById("api-cancel-button");
    cancelButton.addEventListener("click", (event) => {
        event.preventDefault();
        dom.style.display = "none";
        document.getElementById('overlay').style.display = 'none';
    })

    document.getElementById("api-form").addEventListener("submit", function(event) {
        event.preventDefault();

        const formData = new FormData(this);
        fetch('/api/1.0/collections/create/addAPI?collectionId=' + collectionId, {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    alert("Please confirm whether the entered information is correct.");
                } else {
                    window.location.href =
                        "/api/1.0/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + collectionId;
                    alert("Added Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    });
}

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

function deleteAPI() {
    fetch('/api/1.0/collections/delete?userId=1&collectionName=' + selectedCollectionName +
        "&requestId=" + selectedRequestId, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                alert("Failed to delete!");
            } else {
                window.location.href =
                    "/api/1.0/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + collectionId;
                alert("Delete Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}

function testAllAPI() {

    const requestHeader = {
        'Content-Type': 'application/json'
    };

    fetch('/api/1.0/collections/testAll?collectionId=' + collectionId, {
        method: 'POST',
        headers: requestHeader,
        body: JSON.stringify(apiList)
    })
        .then(response => {
            if (!response.ok) {
                alert("Test failed!");
            } else {
                alert("All API test completed!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
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
