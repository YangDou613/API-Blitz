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
                    ' <input id="run-all-button" type="button" onclick="testAllAPI()" value="Test All">');
                button.insertAdjacentHTML("beforeend",
                    ' <input id="add-button" type="submit" onclick="addAPI()" value=" + Add">');
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
