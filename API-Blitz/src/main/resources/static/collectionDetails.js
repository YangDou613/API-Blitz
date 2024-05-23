let collectionId;
let selectedCollectionName;
let selectedRequestId;
let apiList;
let selectedAPI;

const token = localStorage.getItem("access_token");

if (token === null) {

    alert("Please sign in fist!")
    window.location.href = "/signUpIn";

} else {

    document.addEventListener("DOMContentLoaded", function () {

        const sidebarLinks = document.querySelectorAll('.sidebar-link');

        sidebarLinks.forEach(link => {

            const linkPath = link.getAttribute('href');

            if (linkPath === "/collections") {
                link.classList.add('active');
            }
        });
    });

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

                const container = document.getElementById("api-container");

                if (data === null) {
                    container.innerText = "You don't have any API data yet.";
                } else {

                    const table = document.createElement("table");
                    table.classList.add("table");
                    table.classList.add("table-bordered");

                    const thead = document.createElement("thead");
                    const theadTr = document.createElement("tr");
                    theadTr.insertAdjacentHTML("beforeend", "<th>Request Name</th>");
                    theadTr.insertAdjacentHTML("beforeend", "<th>Method</th>");
                    theadTr.insertAdjacentHTML("beforeend", "<th>URL</th>");
                    theadTr.insertAdjacentHTML("beforeend", "<th>Actions</th>");
                    thead.appendChild(theadTr);

                    const tbody = document.createElement("tbody");

                    data.forEach(api => {

                        const tbodyTr = document.createElement("tr");
                        tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-1">${api["requestName"]}</td>`);
                        tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-2">${api["method"]}</td>`);
                        tbodyTr.insertAdjacentHTML("beforeend", `<td class="col col-3">${api["apiurl"]}</td>`);
                        tbodyTr.insertAdjacentHTML("beforeend",
                            `<td class="col col-4"><button type="button" class="btn btn-block btn-test">Test</button>
                        <button type="button" class="btn btn-block btn-delete">Delete</button></td>`)

                        tbody.appendChild(tbodyTr);

                        selectedCollectionName = api["requestName"];
                        selectedRequestId = api["id"];

                        const testButton = tbodyTr.querySelector(".btn-test");
                        const deleteButton = tbodyTr.querySelector(".btn-delete");

                        testButton.addEventListener("click", () => {
                            selectedAPI = api;
                            const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
                            window.location.href = "/APITest" + queryString;
                        })
                        deleteButton.addEventListener("click", () => {
                            deleteAPI();
                        })
                    });
                    table.appendChild(thead);
                    table.appendChild(tbody);
                    container.insertAdjacentHTML("beforeend",
                        '<div class="buttonDiv"><button type="button" class="btn btn-block btn-add" onclick="addAPI()">+ Add</button>' +
                        '<button type="button" class="btn btn-block btn-run-all" onclick="testAllAPI()">Test All</button></div>');
                    container.appendChild(table);
                }
            })
            .catch(error => {
                console.error('There was an error!', error);
            });
    }

    function addAPI() {

        // Reset query params
        const queryParams = document.getElementById("queryParams");
        queryParams.innerHTML = '';
        queryParams.insertAdjacentHTML("beforeend", '<label for="queryParams">Query Params</label><br>');
        queryParams.insertAdjacentHTML("beforeend", '<input id="paramsKey" type="text" class="dynamic-input paramsKey" name="paramsKey" placeholder="Key" style="margin-top: 10px; margin-top: 10px" oninput="addQueryParamsInput(event)">');
        queryParams.insertAdjacentHTML("beforeend", " ");
        queryParams.insertAdjacentHTML("beforeend", '<input id="paramsValue" type="text" class="dynamic-input paramsValue" name="paramsValue" placeholder="Value" style="margin-top: 10px; margin-top: 10px" oninput="addQueryParamsInput(event)">');

        // Reset headers
        const headers = document.getElementById("headers");
        headers.innerHTML = '';
        headers.insertAdjacentHTML("beforeend", '<label for="headers">Headers</label><br>');
        headers.insertAdjacentHTML("beforeend", '<input id="headersKey" type="text" class="headers-input" name="headersKey" placeholder="Key" style="margin-top: 10px" oninput="addHeadersInput(event)">');
        headers.insertAdjacentHTML("beforeend", " ");
        headers.insertAdjacentHTML("beforeend", '<input id="headersValue" type="text" class="headers-input" name="headersValue" placeholder="Value" style="margin-top: 10px" oninput="addHeadersInput(event)">');

        // Reset other
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

        const body = document.getElementById("body");
        body.addEventListener("input", () => {
            body.value = JSON.stringify(JSON.parse(body.value), null, 4);
        })

        const cancelButton = document.getElementById("api-cancel-button");
        cancelButton.addEventListener("click", (event) => {
            event.preventDefault();
            dom.style.display = "none";
            document.getElementById('overlay').style.display = 'none';
        })

        document.getElementById("api-form").addEventListener("submit", function (event) {
            event.preventDefault();

            const formData = new FormData(this);
            fetch('/api/1.0/collections/create/addAPI?collectionId=' + collectionId, {
                method: 'POST',
                headers: {
                    "Authorization": `Bearer ${token}`
                },
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Please confirm whether the entered information is correct.");
                    } else {
                        window.location.href =
                            "/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + collectionId;
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

    function deleteAPI() {

        fetch('/api/1.0/collections/delete?collectionName=' + selectedCollectionName +
            "&requestId=" + selectedRequestId, {
            method: 'DELETE',
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    alert("Failed to delete!");
                } else {
                    window.location.href =
                        "/collections/details?collectionName=" + selectedCollectionName + "&collectionId=" + collectionId;
                    alert("Delete Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    }

    function testAllAPI() {

        document.getElementById("overlay").style.display = 'block';
        document.getElementById('loading').style.display = 'block';

        const requestHeader = {
            "Authorization": `Bearer ${token}`,
            'Content-Type': 'application/json'
        };

        const socket = new SockJS('https://apiblitz.site/ws');
        const stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {

            fetch('/api/1.0/collections/testAll?collectionId=' + collectionId, {
                method: 'POST',
                headers: requestHeader,
                body: JSON.stringify(apiList)
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Test failed!");
                    } else {
                        return response.json();
                    }
                })
                .then(data => {

                    const testDate = data.testDate;
                    const testTime = (data.testTime).split(".")[0];

                    stompClient.subscribe('/topic/Collections', function (message) {

                        document.getElementById("overlay").style.display = 'none';
                        document.getElementById('loading').style.display = 'none';

                        window.location.href =
                            "/report?collectionId=" + collectionId + "&testDate=" + testDate + "&testTime=" + testTime;
                        alert("Successfully set up test all!");
                    });

                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
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
}
