let selectedAPI;
let selectedCollection;
let selectedCollectionName;
let selectedRequestId;
let apiArray;

function createCollection() {
    const dom = document.getElementById("collection-form")
    const list = document.getElementById("collection-list")
    if (dom.style.display === "none") {
        list.style.display = "none";
        dom.style.display = "block";
    } else {
        dom.style.display = "none";
        list.style.display = "block";
    }
    document.getElementById("collection-form").addEventListener("submit", function(event) {
        event.preventDefault();
        const formData = new FormData(this);
        fetch('/api/1.0/collections/create?userId=1', {
            method: 'POST',
            body: formData
        })
            .then(response => {
                if (!response.ok) {
                    alert("Please confirm whether the entered information is correct.");
                } else {
                    window.location.href = "/api/1.0/collections";
                    alert("Create Successfully!");
                }
            })
            .catch(error => {
                console.error('Error submitting form:', error);
            });
    });
}

fetch('/api/1.0/collections/get?userId=1')
    .then(response => {
        if (!response.ok) {
            console.log(response.status)
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        const dom = document.getElementById('collection-list');
        data.forEach(collection => {
            const button = document.createElement('button');
            button.innerText = `${collection["collectionName"]}`;
            dom.appendChild(button);
            const img = document.createElement('img');
            img.setAttribute("src", "/delete.png")
            img.setAttribute("width", "20");
            img.setAttribute("height", "20");
            dom.appendChild(img);
            const content = document.createElement('p');
            content.innerText = `Description: ${collection["description"]}`;
            dom.appendChild(content);
            const lineBreak = document.createElement('br');
            dom.appendChild(lineBreak);
            button.addEventListener('click', () => {
                selectedCollection = collection["id"];
                selectedCollectionName = collection["collectionName"]
                const apiList = document.getElementById('api-list');
                apiList.innerText = '';
                showAPIData(collection)
            });
            img.addEventListener('click', () => {
                selectedCollectionName = collection["collectionName"]
                deleteCollection(selectedCollectionName);
            });
        });
    })
    .catch(error => {
        console.error('There was an error!', error);
    });

function showAPIData(collection) {

    apiArray = JSON.parse(collection["collectionDetails"]);

    const apiList = document.getElementById('api-list');

    if (apiArray === null) {
        apiList.innerText = "You don't have any API data yet.";
    } else {

        const runAllButton = document.createElement("button");
        runAllButton.innerText = "Run All";
        apiList.appendChild(runAllButton);

        apiArray.forEach(api => {
            const method = document.createElement('p');
            method.innerHTML = api["method"];
            apiList.appendChild(method);
            const requestName = document.createElement('p');
            requestName.innerHTML = api["requestName"];
            apiList.appendChild(requestName);
            const button = document.createElement('button');
            button.innerText = api["apiurl"];
            apiList.appendChild(button);
            const img = document.createElement('img');
            img.setAttribute("src", "/delete.png")
            img.setAttribute("width", "20");
            img.setAttribute("height", "20");
            apiList.appendChild(img);
            const lineBreak = document.createElement('br');
            apiList.appendChild(lineBreak);
            button.addEventListener('click', () => {
                selectedAPI = api;
                const queryString = `?selectedAPI=${encodeURIComponent(JSON.stringify(selectedAPI))}`;
                window.location.href = "/APITest.html" + queryString;
            });
            img.addEventListener('click', () => {
                selectedRequestId = api["id"]
                deleteAPI(selectedCollectionName, selectedRequestId);
            });
        });
        runAllButton.addEventListener("click", () => {
            getAPIList(selectedCollection);
        })
    }
}

function getAPIList(selectedCollection) {
    fetch('/api/1.0/collections/getAllAPI?collectionId=' + selectedCollection, {
        method: 'GET'
    })
        .then(response => response.json())
        .then(apiList => {
            for (let i = 0; i < apiList.length; i++) {
                let api = apiList[i];
                api.collectionDetailsId = apiArray[i].id;
            }
            testAllAPI(apiList)
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}

function testAllAPI(apiList) {

    const requestHeader = {
        'Content-Type': 'application/json'
    };

    fetch('/api/1.0/collections/runAll?collectionId=' + selectedCollection, {
        method: 'POST',
        headers: requestHeader,
        body: JSON.stringify(apiList)
    })
        .then(response => {
            if (!response.ok) {
                console.log(response);
                alert("Test failed!");
            } else {
                alert("All API test completed!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}

async function addAPI() {

    if (selectedCollection == null) {
        alert("Please select the collection first!")
    } else {
        const dom = document.getElementById("api-form")
        const list = document.getElementById("api-list")
        if (dom.style.display === "none") {
            list.style.display = "none";
            dom.style.display = "block";
        } else {
            dom.style.display = "none";
            list.style.display = "block";
        }
        document.getElementById("api-form").addEventListener("submit", function(event) {
            event.preventDefault();
            const formData = new FormData(this);
            fetch('/api/1.0/collections/create/addAPI?collectionId=' + selectedCollection, {
                method: 'POST',
                body: formData
            })
                .then(response => {
                    if (!response.ok) {
                        alert("Please confirm whether the entered information is correct.");
                    } else {
                        window.location.href = "/api/1.0/collections";
                        alert("Added Successfully!");
                    }
                })
                .catch(error => {
                    console.error('Error submitting form:', error);
                });
        });
    }
}

function deleteCollection(selectedCollectionName) {
    fetch('/api/1.0/collections/delete?userId=1&collectionName=' + selectedCollectionName, {
        method: 'DELETE'
    })
        .then(response => {
            console.log(response)
            if (!response.ok) {
                alert("Failed to delete!");
            } else {
                window.location.href = "/api/1.0/collections";
                alert("Delete Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}

function deleteAPI(selectedCollectionName, selectedRequestId) {
    fetch('/api/1.0/collections/delete?userId=1&collectionName=' + selectedCollectionName +
        "&requestId=" + selectedRequestId, {
        method: 'DELETE'
    })
        .then(response => {
            if (!response.ok) {
                alert("Failed to delete!");
            } else {
                window.location.href = "/api/1.0/collections";
                alert("Delete Successfully!");
            }
        })
        .catch(error => {
            console.error('Error submitting form:', error);
        });
}
